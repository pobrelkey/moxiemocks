/*
 * Copyright (c) 2011 Moxie contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package moxie;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

class WeakIdentityMap<K,V> extends AbstractMap<K,V> {

    static private final int DEFAULT_CAPACITY = 16;
    static private final float DEFAULT_LOAD_FACTOR = 0.75f;

    private ReferenceQueue<K> referenceQueue = new ReferenceQueue<K>();
    private Entry<K,V>[] buckets;
    private int capacity, size;
    private float loadFactor;
    private volatile int serial = 0;

    public WeakIdentityMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }
    public WeakIdentityMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
    @SuppressWarnings("unchecked")
    public WeakIdentityMap(int initialCapacity, float loadFactor) {
        this.size = 0;
        this.capacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.buckets = new Entry[(int) (initialCapacity / loadFactor)];
    }
    public WeakIdentityMap(Map<? extends K,? extends V> m) {
        this(m.size(), DEFAULT_LOAD_FACTOR);
        putAll(m);
    }

    @Override
    public V get(Object k) {
        for (Entry<K, V> entry = buckets[System.identityHashCode(k) % capacity]; entry != null; entry = entry.next) {
            K entryKey = entry.keyReference.get();
            if (entryKey == k) {
                return entry.value;
            } else if (entryKey == null) {
                purgeEntry(entry);
            }
        }
        return null;
    }

    @Override
    public V remove(Object k) {
        for (Entry<K, V> entry = buckets[(System.identityHashCode(k) % capacity)]; entry != null; entry = entry.next) {
            K entryKey = entry.keyReference.get();
            if (entryKey == k) {
                V result = entry.value;
                purgeEntry(entry);
                return result;
            } else if (entryKey == null) {
                purgeEntry(entry);
            }
        }
        return null;
    }

    @Override
    public V put(K k, V v) {
        if (capacity <= size) {
            purge();
            if (capacity <= size) {
                rehash();
            }
        }

        int bucketIndex = System.identityHashCode(k) % capacity;
        for (Entry<K, V> entry = buckets[bucketIndex]; entry != null; entry = entry.next) {
            K entryKey = entry.getKey();
            if (entryKey == k) {
                V oldValue = entry.value;
                serial++;
                entry.value = v;
                return oldValue;
            } else if (entryKey == null) {
                purgeEntry(entry);
            }
        }
        serial++;
        Entry<K,V> newEntry = new Entry<K,V>(new KeyWeakReference<K>(k, referenceQueue), v);
        if (buckets[bucketIndex] != null) {
            newEntry.next = buckets[bucketIndex];
            buckets[bucketIndex].prev = newEntry;
        }
        buckets[bucketIndex] = newEntry;
        size++;
        return null;
    }

    @SuppressWarnings("unchecked")
    private void rehash() {
        capacity = capacity * 3 / 2;
        size = 0;
        Entry<K, V>[] oldBuckets = buckets;
        buckets = (Entry<K, V>[]) new Entry[(int) (capacity / loadFactor)];
        for (int i = 0; i < oldBuckets.length; i++) {
            for (Entry<K, V> entry = oldBuckets[i]; entry != null; entry = entry.next) {
                int newBucketIndex = entry.keyReference.identityHashCode % capacity;
                serial++;
                if (buckets[newBucketIndex] == null) {
                    buckets[newBucketIndex] = new Entry(entry);
                } else {
                    Entry<K,V> prev = buckets[newBucketIndex];
                    while (prev.next != null) {
                        prev = prev.next;
                    }
                    prev.next = new Entry(entry);
                }
                size++;
            }
        }
    }

    private void purge() {
        KeyWeakReference<K> ref = null;
        while ((ref = (KeyWeakReference<K>) referenceQueue.poll()) != null) {
            for (Entry<K, V> entry = buckets[(ref.identityHashCode % buckets.length)]; entry != null; entry = entry.next) {
                if (entry.keyReference == ref) {
                    purgeEntry(entry);
                    break;
                }
            }
        }
    }

    private void purgeEntry(Entry<K, V> entry) {
        serial++;
        if (entry.prev != null) {
            entry.prev.next = entry.next;
        } else {
            buckets[entry.keyReference.identityHashCode % capacity] = entry.next;
        }
        if (entry.next != null) {
            entry.next.prev = entry.prev;
        }
        entry.keyReference.clear();
        size--;
    }

    private static class IteratorState<K,V> {
        private int index;
        private Entry<K,V> entry;
        private IteratorState() {
            this(0, null);
        }
        private IteratorState(int index, Entry<K, V> entry) {
            this.index = index;
            this.entry = entry;
        }
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new AbstractSet<Map.Entry<K, V>>() {
            @Override
            public Iterator<Map.Entry<K, V>> iterator() {
                final int initialSerial = serial;
                return new Iterator<Map.Entry<K, V>>() {
                    private IteratorState<K,V> state = new IteratorState<K, V>();
                    private boolean removeState = false;
                    public boolean hasNext() {
                        checkSerial(initialSerial);
                        return pollNext() != null;
                    }

                    private IteratorState<K,V> pollNext() {
                        int index = state.index;
                        if (state.entry != null) {
                            if (state.entry.next != null) {
                                return new IteratorState<K, V>(index, state.entry.next);
                            }
                        }
                        while (++index < buckets.length) {
                            if (buckets[index] != null) {
                                return new IteratorState<K, V>(index, buckets[index]);
                            }
                        }
                        return null;
                    }

                    public Map.Entry<K, V> next() {
                        checkSerial(initialSerial);
                        removeState = false;
                        state = pollNext();
                        if (state == null) {
                            throw new NoSuchElementException();
                        }
                        return state.entry;
                    }

                    public void remove() {
                        if (removeState) {
                            throw new IllegalStateException("can't call remove() twice in succession");
                        }
                        checkSerial(initialSerial);
                        purgeEntry(state.entry);
                        state.entry = state.entry.prev;
                        removeState = false;
                    }
                };
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    private void checkSerial(int initialSerial) {
        if (serial != initialSerial) {
            throw new ConcurrentModificationException();
        }
    }

    private static class KeyWeakReference<K> extends WeakReference<K> {
        private final int identityHashCode;

        public KeyWeakReference(K k, ReferenceQueue<? super K> referenceQueue) {
            super(k, referenceQueue);
            this.identityHashCode = System.identityHashCode(k);
        }
    }

    private static class Entry<K,V> implements Map.Entry<K,V> {

        private Entry<K,V> next, prev;
        private final KeyWeakReference<K> keyReference;
        private V value;

        private Entry(Entry<K,V> oldEntry) {
            this(oldEntry.keyReference, oldEntry.value);
        }

        public Entry(KeyWeakReference<K> k, V v) {
            this.keyReference = k;
            this.value = v;
        }

        public K getKey() {
            return keyReference.get();
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V result = this.value;
            this.value = value;
            return result;
        }
    }

}
