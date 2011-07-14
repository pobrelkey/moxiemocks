/*
 * Copyright (c) 2010 Moxie contributors
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

/**
 * Options that can be specified in the creation of {@link Moxie#mock(Class) mocks}, {@link MoxieControl#spy(Object,MoxieOptions...) spies} or {@link MoxieControl#group(MoxieOptions...) groups}.
 */
public enum MoxieOptions implements MoxieFlags {

    /**
     * The order in which expectations are fulfilled on this mock/group will be strictly checked; out-of-order calls will fail.
     */
    UNORDERED(Boolean.FALSE, null, null),

    /**
     * The order in which expectations are fulfilled on this mock/group will not be checked.
     */
    ORDERED(Boolean.TRUE, null, null),

    /**
     * <p>
     * Calls to this mock that do not fulfill any previously specified expectation will
     * perform the default behavior without raising an error.
     * </p>
     * <p>
     * On normal mocks, the default behavior for most methods is to return the method return type's default value
     * (<code>null</code> for methods returning objects, zero or <code>false</code> for methods
     * returning primitives).  The exceptions are three methods from {@link Object}:
     * <ul>
     * <li>{@link Object#hashCode() hashCode()} returns the value of {@link System#identityHashCode(Object) System.identityHashCode()} for the mock.</li>
     * <li>{@link Object#equals(Object) equals()} implements referential equality.</li>
     * <li>{@link Object#toString() toString()} returns a default string giving the name of the mock object.</li>
     * </ul>
     * </p>
     * <p>
     * On {@link #PARTIAL} mocks, the default behavior is to pass control to the method on the class being mocked.
     * </p>
     * <p>
     * On spies, the default behavior is always to delegate to the underlying object.
     * </p>
     */
    PERMISSIVE(null, Boolean.TRUE, null),

    /**
     * <p>
     * Calls to this mock that do not fulfill any previously specified expectation will raise an error.
     * </p>
     * <p>
     * Note that {@link Object#hashCode() hashCode()}, {@link Object#equals(Object) equals()}, {@link Object#toString() toString()}
     * and {@link Object#finalize() finalize()} will not raise errors on <code>PRESCRIPTIVE</code> mocks/spies
     * unless a {@link Expectation#never() never()} expectation is explicitly set for those methods.  
     * </p>
     */
    PRESCRIPTIVE(null, Boolean.FALSE, null),

    /**
     * <p>
     * The default behavior for methods on this mock will be to pass control to the original implementation
     * of the method on the concrete class being mocked.
     * </p>
     * <p>
     * This option only makes sense for mocks of concrete classes; specifying it for an interface mock
     * or a spy will raise an {@link IllegalArgumentException}.
     * </p>
     * <p>
     * Most users of this option will probably want to specify {@link #PERMISSIVE} at the same time,
     * lest unexpected method calls raise an error.
     * </p>
     */
    PARTIAL(null ,null, Boolean.TRUE),

    /**
     * Represents Moxie's default settings for mocks/spies ({@link #UNORDERED} and {@link #PRESCRIPTIVE}).
     */
    MOCK_DEFAULTS(Boolean.FALSE, Boolean.FALSE, null),

    /**
     * Represents Moxie's default settings for {@link Group}s ({@link #ORDERED}).
     */
    GROUP_DEFAULTS(Boolean.TRUE, null, null);

    final private Boolean strictlyOrdered, autoStubbing, partial;

    MoxieOptions(Boolean strictlyOrdered, Boolean autoStubbing, Boolean partial) {
        this.strictlyOrdered = strictlyOrdered;
        this.autoStubbing = autoStubbing;
        this.partial = partial;
    }

    /**
     * @deprecated Moxie internal method.
     */
    public Boolean isStrictlyOrdered() {
        return strictlyOrdered;
    }

    /**
     * @deprecated Moxie internal method.
     */
    public Boolean isAutoStubbing() {
        return autoStubbing;
    }

    /**
     * @deprecated Moxie internal method.
     */
    public Boolean isPartial() {
        return partial;
    }

    static MoxieFlags merge(MoxieFlags... options) {
        Boolean strictlyOrdered = null, autoStubbing = null, partial = null;
        if (options != null) {
            for (MoxieFlags flags : options) {
                if (flags.isStrictlyOrdered() != null) {
                    if (strictlyOrdered == null) {
                        strictlyOrdered = flags.isStrictlyOrdered();
                    } else if (!strictlyOrdered.equals(flags.isStrictlyOrdered())) {
                        throw new IllegalArgumentException("Specified options are contradictory regarding strict ordering of method calls");
                    }
                }
                if (flags.isAutoStubbing() != null) {
                    if (autoStubbing == null) {
                        autoStubbing = flags.isAutoStubbing();
                    } else if (!autoStubbing.equals(flags.isAutoStubbing())) {
                        throw new IllegalArgumentException("Specified options are contradictory regarding auto-stubbing of method calls");
                    }
                }
                if (flags.isPartial() != null) {
                    if (partial == null) {
                        partial = flags.isPartial();
                    } else if (!partial.equals(flags.isPartial())) {
                        throw new IllegalArgumentException("Specified options are contradictory regarding partial mocking");
                    }
                }
            }
        }
        return new SimpleMoxieFlags(strictlyOrdered, autoStubbing, partial);
    }

    static MoxieFlags mergeWithDefaults(MoxieFlags defaults, MoxieFlags... options) {
        MoxieFlags merged = merge(options);
        return new SimpleMoxieFlags(
                merged.isStrictlyOrdered() != null ? merged.isStrictlyOrdered() : defaults.isStrictlyOrdered(),
                merged.isAutoStubbing() != null ? merged.isAutoStubbing() : defaults.isAutoStubbing(),
                merged.isPartial() != null ? merged.isPartial() : defaults.isPartial());
    }
}
