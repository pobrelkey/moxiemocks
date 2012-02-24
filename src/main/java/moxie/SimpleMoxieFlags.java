/*
 * Copyright (c) 2010-2012 Moxie contributors
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

class SimpleMoxieFlags implements MoxieFlags {
    private final Boolean strictlyOrdered;
    private final Boolean autoStubbing;
    private final Boolean partial;
    private final Boolean tracing;

    public SimpleMoxieFlags(Boolean strictlyOrdered, Boolean autoStubbing, Boolean partial, Boolean tracing) {
        this.strictlyOrdered = strictlyOrdered;
        this.autoStubbing = autoStubbing;
        this.partial = partial;
        this.tracing = tracing;
    }

    public Boolean isStrictlyOrdered() {
        return strictlyOrdered;
    }

    public Boolean isAutoStubbing() {
        return autoStubbing;
    }

    public Boolean isPartial() {
        return partial;
    }

    public Boolean isTracing() {
        return tracing;
    }
}
