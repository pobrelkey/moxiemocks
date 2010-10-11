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

package moxietests;

import moxie.Moxie;
import moxie.MoxieUnexpectedInvocationError;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsSame;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;

public class MoxieMatchersTest {

    private interface TestInterface {
        void booleanCall(boolean p);
        void byteCall(byte p);
        void charCall(char p);
        void shortCall(short p);
        void intCall(int p);
        void longCall(long p);
        void floatCall(float p);
        void doubleCall(double p);
        void booleanObjectCall(Boolean p);
        void byteObjectCall(Byte p);
        void charObjectCall(Character p);
        void shortObjectCall(Short p);
        void intObjectCall(Integer p);
        void longObjectCall(Long p);
        void floatObjectCall(Float p);
        void doubleObjectCall(Double p);
        void stringCall(String p);
        void objectCall(Object p);

        void arrayBooleanCall(boolean[] p);
        void arrayByteCall(byte[] p);
        void arrayCharCall(char[] p);
        void arrayShortCall(short[] p);
        void arrayIntCall(int[] p);
        void arrayLongCall(long[] p);
        void arrayFloatCall(float[] p);
        void arrayDoubleCall(double[] p);
        void arrayBooleanObjectCall(Boolean[] p);
        void arrayByteObjectCall(Byte[] p);
        void arrayCharObjectCall(Character[] p);
        void arrayShortObjectCall(Short[] p);
        void arrayIntObjectCall(Integer[] p);
        void arrayLongObjectCall(Long[] p);
        void arrayFloatObjectCall(Float[] p);
        void arrayDoubleObjectCall(Double[] p);
        void arrayStringCall(String[] p);
        void arrayObjectCall(Object[] p);

        void varargsBooleanCall(boolean... p);
        void varargsByteCall(byte... p);
        void varargsCharCall(char... p);
        void varargsShortCall(short... p);
        void varargsIntCall(int... p);
        void varargsLongCall(long... p);
        void varargsFloatCall(float... p);
        void varargsDoubleCall(double... p);
        void varargsBooleanObjectCall(Boolean... p);
        void varargsByteObjectCall(Byte... p);
        void varargsCharObjectCall(Character... p);
        void varargsShortObjectCall(Short... p);
        void varargsIntObjectCall(Integer... p);
        void varargsLongObjectCall(Long... p);
        void varargsFloatObjectCall(Float... p);
        void varargsDoubleObjectCall(Double... p);
        void varargsStringCall(String... p);
        void varargsObjectCall(Object... p);
    }

    @Test
    public void testSame_happyPath(){
        TestInterface mock = Moxie.mock(TestInterface.class);
        Object someObject = new Object();
        Moxie.expect(mock).will().objectCall(Moxie.same(someObject));
        mock.objectCall(someObject);
        Moxie.verify(mock);
    }

    @Test(expected= MoxieUnexpectedInvocationError.class)
    public void testSame_sadPath(){
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.same(new Object()));
        mock.objectCall(new Object());
    }

    @Test
    public void testArrayThat_happyPath() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Object[] anArray = new Object[]{"1","2","3"};
        Moxie.expect(mock).will().arrayObjectCall(Moxie.<Object[]>arrayThat(new IsSame(anArray)));
        mock.arrayObjectCall(anArray);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void testArrayThat_sadPath() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayObjectCall(Moxie.<Object[]>arrayThat(new IsSame(new Object[]{"1","2","3"})));
        mock.arrayObjectCall(new Object[]{"4","5"});
    }

    @Test
    public void testMatchesRegexp_happyPath() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.<String>matchesRegexp("^a.*z$"));
        mock.stringCall("alcatraz");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void testMatchesRegexp_sadPath() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.<String>matchesRegexp("^a.*z$"));
        mock.stringCall("san quentin");
    }

    @Test
    public void testEqIgnoreCase_happyPath() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.eqIgnoreCase("mash"));
        mock.stringCall("MASH");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void testEqIgnoreCase_sadPath() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.eqIgnoreCase("mash"));
        mock.stringCall("M*A*S*H");
    }

    @Test
    public void testEqWithDelta_happyPath() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.eq(3.0,1.5));
        mock.objectCall(new BigDecimal(4));
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void testEqWithDelta_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.eq(3.0,1.5));
        mock.objectCall(new BigDecimal(1));
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void testEqWithDelta_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.eq(3.0,1.5));
        mock.objectCall("2");
    }

    //
    // Below this point tests are automatically generated by the MoxieMatchers.rb script in this directory.
    //

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_byteCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.eq((byte) 2));
        mock.byteCall((byte) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_byteObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.eq((byte) 2));
        mock.byteObjectCall((byte) 1);
    }

    @Test
    public void eq_byteCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.eq((byte) 2));
        mock.byteCall((byte) 2);
        Moxie.verify(mock);
    }

    @Test
    public void eq_byteObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.eq((byte) 2));
        mock.byteObjectCall((byte) 2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_byteCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.eq((byte) 2));
        mock.byteCall((byte) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_byteObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.eq((byte) 2));
        mock.byteObjectCall((byte) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_byteObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.eq((byte) 2));
        mock.byteObjectCall(null);
    }

    @Test
    public void not_byteCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.not((byte) 2));
        mock.byteCall((byte) 1);
        Moxie.verify(mock);
    }

    @Test
    public void not_byteObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.not((byte) 2));
        mock.byteObjectCall((byte) 1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_byteCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.not((byte) 2));
        mock.byteCall((byte) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_byteObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.not((byte) 2));
        mock.byteObjectCall((byte) 2);
    }

    @Test
    public void not_byteCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.not((byte) 2));
        mock.byteCall((byte) 3);
        Moxie.verify(mock);
    }

    @Test
    public void not_byteObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.not((byte) 2));
        mock.byteObjectCall((byte) 3);
        Moxie.verify(mock);
    }

    @Test
    public void not_byteObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.not((byte) 2));
        mock.byteObjectCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void anyByte_byteCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.anyByte());
        mock.byteCall((byte) 1);
        Moxie.verify(mock);
    }

    @Test
    public void anyByte_byteObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.anyByte());
        mock.byteObjectCall((byte) 1);
        Moxie.verify(mock);
    }

    @Test
    public void anyByte_byteCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.anyByte());
        mock.byteCall((byte) 2);
        Moxie.verify(mock);
    }

    @Test
    public void anyByte_byteObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.anyByte());
        mock.byteObjectCall((byte) 2);
        Moxie.verify(mock);
    }

    @Test
    public void anyByte_byteCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.anyByte());
        mock.byteCall((byte) 3);
        Moxie.verify(mock);
    }

    @Test
    public void anyByte_byteObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.anyByte());
        mock.byteObjectCall((byte) 3);
        Moxie.verify(mock);
    }

    @Test
    public void anyByte_byteObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.anyByte());
        mock.byteObjectCall(null);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_charCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.eq('2'));
        mock.charCall('1');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_charObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.eq('2'));
        mock.charObjectCall('1');
    }

    @Test
    public void eq_charCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.eq('2'));
        mock.charCall('2');
        Moxie.verify(mock);
    }

    @Test
    public void eq_charObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.eq('2'));
        mock.charObjectCall('2');
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_charCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.eq('2'));
        mock.charCall('3');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_charObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.eq('2'));
        mock.charObjectCall('3');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_charObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.eq('2'));
        mock.charObjectCall(null);
    }

    @Test
    public void not_charCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.not('2'));
        mock.charCall('1');
        Moxie.verify(mock);
    }

    @Test
    public void not_charObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.not('2'));
        mock.charObjectCall('1');
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_charCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.not('2'));
        mock.charCall('2');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_charObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.not('2'));
        mock.charObjectCall('2');
    }

    @Test
    public void not_charCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.not('2'));
        mock.charCall('3');
        Moxie.verify(mock);
    }

    @Test
    public void not_charObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.not('2'));
        mock.charObjectCall('3');
        Moxie.verify(mock);
    }

    @Test
    public void not_charObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.not('2'));
        mock.charObjectCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void anyChar_charCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.anyChar());
        mock.charCall('1');
        Moxie.verify(mock);
    }

    @Test
    public void anyChar_charObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.anyChar());
        mock.charObjectCall('1');
        Moxie.verify(mock);
    }

    @Test
    public void anyChar_charCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.anyChar());
        mock.charCall('2');
        Moxie.verify(mock);
    }

    @Test
    public void anyChar_charObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.anyChar());
        mock.charObjectCall('2');
        Moxie.verify(mock);
    }

    @Test
    public void anyChar_charCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.anyChar());
        mock.charCall('3');
        Moxie.verify(mock);
    }

    @Test
    public void anyChar_charObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.anyChar());
        mock.charObjectCall('3');
        Moxie.verify(mock);
    }

    @Test
    public void anyChar_charObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.anyChar());
        mock.charObjectCall(null);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_shortCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.eq((short) 2));
        mock.shortCall((short) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_shortObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.eq((short) 2));
        mock.shortObjectCall((short) 1);
    }

    @Test
    public void eq_shortCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.eq((short) 2));
        mock.shortCall((short) 2);
        Moxie.verify(mock);
    }

    @Test
    public void eq_shortObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.eq((short) 2));
        mock.shortObjectCall((short) 2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_shortCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.eq((short) 2));
        mock.shortCall((short) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_shortObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.eq((short) 2));
        mock.shortObjectCall((short) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_shortObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.eq((short) 2));
        mock.shortObjectCall(null);
    }

    @Test
    public void not_shortCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.not((short) 2));
        mock.shortCall((short) 1);
        Moxie.verify(mock);
    }

    @Test
    public void not_shortObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.not((short) 2));
        mock.shortObjectCall((short) 1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_shortCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.not((short) 2));
        mock.shortCall((short) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_shortObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.not((short) 2));
        mock.shortObjectCall((short) 2);
    }

    @Test
    public void not_shortCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.not((short) 2));
        mock.shortCall((short) 3);
        Moxie.verify(mock);
    }

    @Test
    public void not_shortObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.not((short) 2));
        mock.shortObjectCall((short) 3);
        Moxie.verify(mock);
    }

    @Test
    public void not_shortObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.not((short) 2));
        mock.shortObjectCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void anyShort_shortCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.anyShort());
        mock.shortCall((short) 1);
        Moxie.verify(mock);
    }

    @Test
    public void anyShort_shortObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.anyShort());
        mock.shortObjectCall((short) 1);
        Moxie.verify(mock);
    }

    @Test
    public void anyShort_shortCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.anyShort());
        mock.shortCall((short) 2);
        Moxie.verify(mock);
    }

    @Test
    public void anyShort_shortObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.anyShort());
        mock.shortObjectCall((short) 2);
        Moxie.verify(mock);
    }

    @Test
    public void anyShort_shortCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.anyShort());
        mock.shortCall((short) 3);
        Moxie.verify(mock);
    }

    @Test
    public void anyShort_shortObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.anyShort());
        mock.shortObjectCall((short) 3);
        Moxie.verify(mock);
    }

    @Test
    public void anyShort_shortObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.anyShort());
        mock.shortObjectCall(null);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_intCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.eq(2));
        mock.intCall(1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_intObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.eq(2));
        mock.intObjectCall(1);
    }

    @Test
    public void eq_intCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.eq(2));
        mock.intCall(2);
        Moxie.verify(mock);
    }

    @Test
    public void eq_intObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.eq(2));
        mock.intObjectCall(2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_intCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.eq(2));
        mock.intCall(3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_intObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.eq(2));
        mock.intObjectCall(3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_intObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.eq(2));
        mock.intObjectCall(null);
    }

    @Test
    public void not_intCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.not(2));
        mock.intCall(1);
        Moxie.verify(mock);
    }

    @Test
    public void not_intObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.not(2));
        mock.intObjectCall(1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_intCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.not(2));
        mock.intCall(2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_intObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.not(2));
        mock.intObjectCall(2);
    }

    @Test
    public void not_intCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.not(2));
        mock.intCall(3);
        Moxie.verify(mock);
    }

    @Test
    public void not_intObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.not(2));
        mock.intObjectCall(3);
        Moxie.verify(mock);
    }

    @Test
    public void not_intObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.not(2));
        mock.intObjectCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void anyInt_intCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.anyInt());
        mock.intCall(1);
        Moxie.verify(mock);
    }

    @Test
    public void anyInt_intObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.anyInt());
        mock.intObjectCall(1);
        Moxie.verify(mock);
    }

    @Test
    public void anyInt_intCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.anyInt());
        mock.intCall(2);
        Moxie.verify(mock);
    }

    @Test
    public void anyInt_intObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.anyInt());
        mock.intObjectCall(2);
        Moxie.verify(mock);
    }

    @Test
    public void anyInt_intCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.anyInt());
        mock.intCall(3);
        Moxie.verify(mock);
    }

    @Test
    public void anyInt_intObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.anyInt());
        mock.intObjectCall(3);
        Moxie.verify(mock);
    }

    @Test
    public void anyInt_intObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.anyInt());
        mock.intObjectCall(null);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_longCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.eq((long) 2));
        mock.longCall((long) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_longObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.eq((long) 2));
        mock.longObjectCall((long) 1);
    }

    @Test
    public void eq_longCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.eq((long) 2));
        mock.longCall((long) 2);
        Moxie.verify(mock);
    }

    @Test
    public void eq_longObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.eq((long) 2));
        mock.longObjectCall((long) 2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_longCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.eq((long) 2));
        mock.longCall((long) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_longObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.eq((long) 2));
        mock.longObjectCall((long) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_longObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.eq((long) 2));
        mock.longObjectCall(null);
    }

    @Test
    public void not_longCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.not((long) 2));
        mock.longCall((long) 1);
        Moxie.verify(mock);
    }

    @Test
    public void not_longObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.not((long) 2));
        mock.longObjectCall((long) 1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_longCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.not((long) 2));
        mock.longCall((long) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_longObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.not((long) 2));
        mock.longObjectCall((long) 2);
    }

    @Test
    public void not_longCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.not((long) 2));
        mock.longCall((long) 3);
        Moxie.verify(mock);
    }

    @Test
    public void not_longObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.not((long) 2));
        mock.longObjectCall((long) 3);
        Moxie.verify(mock);
    }

    @Test
    public void not_longObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.not((long) 2));
        mock.longObjectCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void anyLong_longCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.anyLong());
        mock.longCall((long) 1);
        Moxie.verify(mock);
    }

    @Test
    public void anyLong_longObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.anyLong());
        mock.longObjectCall((long) 1);
        Moxie.verify(mock);
    }

    @Test
    public void anyLong_longCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.anyLong());
        mock.longCall((long) 2);
        Moxie.verify(mock);
    }

    @Test
    public void anyLong_longObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.anyLong());
        mock.longObjectCall((long) 2);
        Moxie.verify(mock);
    }

    @Test
    public void anyLong_longCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.anyLong());
        mock.longCall((long) 3);
        Moxie.verify(mock);
    }

    @Test
    public void anyLong_longObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.anyLong());
        mock.longObjectCall((long) 3);
        Moxie.verify(mock);
    }

    @Test
    public void anyLong_longObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.anyLong());
        mock.longObjectCall(null);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_floatCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.eq((float) 2));
        mock.floatCall((float) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_floatObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.eq((float) 2));
        mock.floatObjectCall((float) 1);
    }

    @Test
    public void eq_floatCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.eq((float) 2));
        mock.floatCall((float) 2);
        Moxie.verify(mock);
    }

    @Test
    public void eq_floatObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.eq((float) 2));
        mock.floatObjectCall((float) 2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_floatCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.eq((float) 2));
        mock.floatCall((float) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_floatObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.eq((float) 2));
        mock.floatObjectCall((float) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_floatObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.eq((float) 2));
        mock.floatObjectCall(null);
    }

    @Test
    public void not_floatCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.not((float) 2));
        mock.floatCall((float) 1);
        Moxie.verify(mock);
    }

    @Test
    public void not_floatObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.not((float) 2));
        mock.floatObjectCall((float) 1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_floatCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.not((float) 2));
        mock.floatCall((float) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_floatObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.not((float) 2));
        mock.floatObjectCall((float) 2);
    }

    @Test
    public void not_floatCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.not((float) 2));
        mock.floatCall((float) 3);
        Moxie.verify(mock);
    }

    @Test
    public void not_floatObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.not((float) 2));
        mock.floatObjectCall((float) 3);
        Moxie.verify(mock);
    }

    @Test
    public void not_floatObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.not((float) 2));
        mock.floatObjectCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void anyFloat_floatCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.anyFloat());
        mock.floatCall((float) 1);
        Moxie.verify(mock);
    }

    @Test
    public void anyFloat_floatObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.anyFloat());
        mock.floatObjectCall((float) 1);
        Moxie.verify(mock);
    }

    @Test
    public void anyFloat_floatCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.anyFloat());
        mock.floatCall((float) 2);
        Moxie.verify(mock);
    }

    @Test
    public void anyFloat_floatObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.anyFloat());
        mock.floatObjectCall((float) 2);
        Moxie.verify(mock);
    }

    @Test
    public void anyFloat_floatCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.anyFloat());
        mock.floatCall((float) 3);
        Moxie.verify(mock);
    }

    @Test
    public void anyFloat_floatObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.anyFloat());
        mock.floatObjectCall((float) 3);
        Moxie.verify(mock);
    }

    @Test
    public void anyFloat_floatObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.anyFloat());
        mock.floatObjectCall(null);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_doubleCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.eq((double) 2));
        mock.doubleCall((double) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_doubleObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.eq((double) 2));
        mock.doubleObjectCall((double) 1);
    }

    @Test
    public void eq_doubleCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.eq((double) 2));
        mock.doubleCall((double) 2);
        Moxie.verify(mock);
    }

    @Test
    public void eq_doubleObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.eq((double) 2));
        mock.doubleObjectCall((double) 2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_doubleCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.eq((double) 2));
        mock.doubleCall((double) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_doubleObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.eq((double) 2));
        mock.doubleObjectCall((double) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_doubleObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.eq((double) 2));
        mock.doubleObjectCall(null);
    }

    @Test
    public void not_doubleCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.not((double) 2));
        mock.doubleCall((double) 1);
        Moxie.verify(mock);
    }

    @Test
    public void not_doubleObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.not((double) 2));
        mock.doubleObjectCall((double) 1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_doubleCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.not((double) 2));
        mock.doubleCall((double) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_doubleObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.not((double) 2));
        mock.doubleObjectCall((double) 2);
    }

    @Test
    public void not_doubleCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.not((double) 2));
        mock.doubleCall((double) 3);
        Moxie.verify(mock);
    }

    @Test
    public void not_doubleObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.not((double) 2));
        mock.doubleObjectCall((double) 3);
        Moxie.verify(mock);
    }

    @Test
    public void not_doubleObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.not((double) 2));
        mock.doubleObjectCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void anyDouble_doubleCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.anyDouble());
        mock.doubleCall((double) 1);
        Moxie.verify(mock);
    }

    @Test
    public void anyDouble_doubleObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.anyDouble());
        mock.doubleObjectCall((double) 1);
        Moxie.verify(mock);
    }

    @Test
    public void anyDouble_doubleCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.anyDouble());
        mock.doubleCall((double) 2);
        Moxie.verify(mock);
    }

    @Test
    public void anyDouble_doubleObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.anyDouble());
        mock.doubleObjectCall((double) 2);
        Moxie.verify(mock);
    }

    @Test
    public void anyDouble_doubleCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.anyDouble());
        mock.doubleCall((double) 3);
        Moxie.verify(mock);
    }

    @Test
    public void anyDouble_doubleObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.anyDouble());
        mock.doubleObjectCall((double) 3);
        Moxie.verify(mock);
    }

    @Test
    public void anyDouble_doubleObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.anyDouble());
        mock.doubleObjectCall(null);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_stringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.eq("2"));
        mock.stringCall("1");
    }

    @Test
    public void eq_stringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.eq("2"));
        mock.stringCall("2");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_stringCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.eq("2"));
        mock.stringCall("3");
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_stringCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.eq("2"));
        mock.stringCall(null);
    }

    @Test
    public void not_stringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.not("2"));
        mock.stringCall("1");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_stringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.not("2"));
        mock.stringCall("2");
    }

    @Test
    public void not_stringCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.not("2"));
        mock.stringCall("3");
        Moxie.verify(mock);
    }

    @Test
    public void not_stringCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.not("2"));
        mock.stringCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void anyString_stringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.anyString());
        mock.stringCall("1");
        Moxie.verify(mock);
    }

    @Test
    public void anyString_stringCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.anyString());
        mock.stringCall("2");
        Moxie.verify(mock);
    }

    @Test
    public void anyString_stringCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.anyString());
        mock.stringCall("3");
        Moxie.verify(mock);
    }

    @Test
    public void anyString_stringCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.anyString());
        mock.stringCall(null);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_objectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.eq(Collections.singletonList("2")));
        mock.objectCall(Collections.singletonList("1"));
    }

    @Test
    public void eq_objectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.eq(Collections.singletonList("2")));
        mock.objectCall(Collections.singletonList("2"));
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_objectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.eq(Collections.singletonList("2")));
        mock.objectCall(Collections.singletonList("3"));
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_objectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.eq(Collections.singletonList("2")));
        mock.objectCall(null);
    }

    @Test
    public void not_objectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.not(Collections.singletonList("2")));
        mock.objectCall(Collections.singletonList("1"));
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_objectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.not(Collections.singletonList("2")));
        mock.objectCall(Collections.singletonList("2"));
    }

    @Test
    public void not_objectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.not(Collections.singletonList("2")));
        mock.objectCall(Collections.singletonList("3"));
        Moxie.verify(mock);
    }

    @Test
    public void not_objectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.not(Collections.singletonList("2")));
        mock.objectCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void anyObject_objectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.anyObject());
        mock.objectCall(Collections.singletonList("1"));
        Moxie.verify(mock);
    }

    @Test
    public void anyObject_objectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.anyObject());
        mock.objectCall(Collections.singletonList("2"));
        Moxie.verify(mock);
    }

    @Test
    public void anyObject_objectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.anyObject());
        mock.objectCall(Collections.singletonList("3"));
        Moxie.verify(mock);
    }

    @Test
    public void anyObject_objectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.anyObject());
        mock.objectCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void anything_objectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.anything());
        mock.objectCall(Collections.singletonList("1"));
        Moxie.verify(mock);
    }

    @Test
    public void anything_objectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.anything());
        mock.objectCall(Collections.singletonList("2"));
        Moxie.verify(mock);
    }

    @Test
    public void anything_objectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.anything());
        mock.objectCall(Collections.singletonList("3"));
        Moxie.verify(mock);
    }

    @Test
    public void anything_objectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.anything());
        mock.objectCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void eq_booleanCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanCall(Moxie.eq(true));
        mock.booleanCall(true);
        Moxie.verify(mock);
    }

    @Test
    public void eq_booleanObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.eq(true));
        mock.booleanObjectCall(true);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_booleanCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanCall(Moxie.eq(true));
        mock.booleanCall(false);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_booleanObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.eq(true));
        mock.booleanObjectCall(false);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_booleanObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.eq(true));
        mock.booleanObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_booleanCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanCall(Moxie.eq(false));
        mock.booleanCall(true);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_booleanObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.eq(false));
        mock.booleanObjectCall(true);
    }

    @Test
    public void eq_booleanCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanCall(Moxie.eq(false));
        mock.booleanCall(false);
        Moxie.verify(mock);
    }

    @Test
    public void eq_booleanObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.eq(false));
        mock.booleanObjectCall(false);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_booleanObjectCall_sadPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.eq(false));
        mock.booleanObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_booleanCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanCall(Moxie.not(true));
        mock.booleanCall(true);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_booleanObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.not(true));
        mock.booleanObjectCall(true);
    }

    @Test
    public void not_booleanCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanCall(Moxie.not(true));
        mock.booleanCall(false);
        Moxie.verify(mock);
    }

    @Test
    public void not_booleanObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.not(true));
        mock.booleanObjectCall(false);
        Moxie.verify(mock);
    }

    @Test
    public void not_booleanObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.not(true));
        mock.booleanObjectCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void not_booleanCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanCall(Moxie.not(false));
        mock.booleanCall(true);
        Moxie.verify(mock);
    }

    @Test
    public void not_booleanObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.not(false));
        mock.booleanObjectCall(true);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_booleanCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanCall(Moxie.not(false));
        mock.booleanCall(false);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_booleanObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.not(false));
        mock.booleanObjectCall(false);
    }

    @Test
    public void not_booleanObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.not(false));
        mock.booleanObjectCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void anyBoolean_booleanCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanCall(Moxie.anyBoolean());
        mock.booleanCall(true);
        Moxie.verify(mock);
    }

    @Test
    public void anyBoolean_booleanObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.anyBoolean());
        mock.booleanObjectCall(true);
        Moxie.verify(mock);
    }

    @Test
    public void anyBoolean_booleanCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanCall(Moxie.anyBoolean());
        mock.booleanCall(false);
        Moxie.verify(mock);
    }

    @Test
    public void anyBoolean_booleanObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.anyBoolean());
        mock.booleanObjectCall(false);
        Moxie.verify(mock);
    }

    @Test
    public void anyBoolean_booleanObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.anyBoolean());
        mock.booleanObjectCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void booleanThat_booleanCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanCall(Moxie.booleanThat(new IsEqual(Boolean.TRUE)));
        mock.booleanCall(true);
        Moxie.verify(mock);
    }

    @Test
    public void booleanThat_booleanObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.booleanThat(new IsEqual(Boolean.TRUE)));
        mock.booleanObjectCall(true);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void booleanThat_booleanCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanCall(Moxie.booleanThat(new IsEqual(Boolean.TRUE)));
        mock.booleanCall(false);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void booleanThat_booleanObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().booleanObjectCall(Moxie.booleanThat(new IsEqual(Boolean.TRUE)));
        mock.booleanObjectCall(false);
    }

    @Test
    public void aryEq_arrayBooleanCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanCall(Moxie.aryEq(new boolean[]{true, false, true}));
        mock.arrayBooleanCall(new boolean[]{true, false, true});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsBooleanCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanCall(Moxie.aryEq(new boolean[]{true, false, true}));
        mock.varargsBooleanCall(true, false, true);
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_arrayBooleanObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanObjectCall(Moxie.aryEq(new Boolean[]{true, false, true}));
        mock.arrayBooleanObjectCall(new Boolean[]{true, false, true});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsBooleanObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanObjectCall(Moxie.aryEq(new Boolean[]{true, false, true}));
        mock.varargsBooleanObjectCall(true, false, true);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayBooleanCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanCall(Moxie.aryEq(new boolean[]{true, false, true}));
        mock.arrayBooleanCall(new boolean[]{true, false});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsBooleanCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanCall(Moxie.aryEq(new boolean[]{true, false, true}));
        mock.varargsBooleanCall(true, false);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayBooleanObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanObjectCall(Moxie.aryEq(new Boolean[]{true, false, true}));
        mock.arrayBooleanObjectCall(new Boolean[]{true, false});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsBooleanObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanObjectCall(Moxie.aryEq(new Boolean[]{true, false, true}));
        mock.varargsBooleanObjectCall(true, false);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayBooleanCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanCall(Moxie.aryEq(new boolean[]{true, false, true}));
        mock.arrayBooleanCall(new boolean[]{true, false, true, false});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsBooleanCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanCall(Moxie.aryEq(new boolean[]{true, false, true}));
        mock.varargsBooleanCall(true, false, true, false);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayBooleanObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanObjectCall(Moxie.aryEq(new Boolean[]{true, false, true}));
        mock.arrayBooleanObjectCall(new Boolean[]{true, false, true, false});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsBooleanObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanObjectCall(Moxie.aryEq(new Boolean[]{true, false, true}));
        mock.varargsBooleanObjectCall(true, false, true, false);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayBooleanCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanCall(Moxie.aryEq(new boolean[]{true, false, true}));
        mock.arrayBooleanCall(new boolean[]{true, false, false});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsBooleanCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanCall(Moxie.aryEq(new boolean[]{true, false, true}));
        mock.varargsBooleanCall(true, false, false);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayBooleanObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanObjectCall(Moxie.aryEq(new Boolean[]{true, false, true}));
        mock.arrayBooleanObjectCall(new Boolean[]{true, false, false});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsBooleanObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanObjectCall(Moxie.aryEq(new Boolean[]{true, false, true}));
        mock.varargsBooleanObjectCall(true, false, false);
    }

    @Test
    public void array_arrayBooleanCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanCall(Moxie.array(new boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.arrayBooleanCall(new boolean[]{true, false, true});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsBooleanCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanCall(Moxie.array(new boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.varargsBooleanCall(true, false, true);
        Moxie.verify(mock);
    }

    @Test
    public void array_arrayBooleanObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanObjectCall(Moxie.array(new Boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.arrayBooleanObjectCall(new Boolean[]{true, false, true});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsBooleanObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanObjectCall(Moxie.array(new Boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.varargsBooleanObjectCall(true, false, true);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayBooleanCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanCall(Moxie.array(new boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.arrayBooleanCall(new boolean[]{true, false});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsBooleanCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanCall(Moxie.array(new boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.varargsBooleanCall(true, false);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayBooleanObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanObjectCall(Moxie.array(new Boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.arrayBooleanObjectCall(new Boolean[]{true, false});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsBooleanObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanObjectCall(Moxie.array(new Boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.varargsBooleanObjectCall(true, false);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayBooleanCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanCall(Moxie.array(new boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.arrayBooleanCall(new boolean[]{true, false, true, false});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsBooleanCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanCall(Moxie.array(new boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.varargsBooleanCall(true, false, true, false);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayBooleanObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanObjectCall(Moxie.array(new Boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.arrayBooleanObjectCall(new Boolean[]{true, false, true, false});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsBooleanObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanObjectCall(Moxie.array(new Boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.varargsBooleanObjectCall(true, false, true, false);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayBooleanCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanCall(Moxie.array(new boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.arrayBooleanCall(new boolean[]{true, false, false});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsBooleanCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanCall(Moxie.array(new boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.varargsBooleanCall(true, false, false);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayBooleanObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayBooleanObjectCall(Moxie.array(new Boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.arrayBooleanObjectCall(new Boolean[]{true, false, false});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsBooleanObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsBooleanObjectCall(Moxie.array(new Boolean[]{Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)}));
        mock.varargsBooleanObjectCall(true, false, false);
    }

    @Test
    public void leq_byteCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.leq((byte) 2));
        mock.byteCall((byte) 1);
        Moxie.verify(mock);
    }

    @Test
    public void leq_byteObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.leq((byte) 2));
        mock.byteObjectCall((byte) 1);
        Moxie.verify(mock);
    }

    @Test
    public void leq_byteCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.leq((byte) 2));
        mock.byteCall((byte) 2);
        Moxie.verify(mock);
    }

    @Test
    public void leq_byteObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.leq((byte) 2));
        mock.byteObjectCall((byte) 2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_byteCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.leq((byte) 2));
        mock.byteCall((byte) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_byteObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.leq((byte) 2));
        mock.byteObjectCall((byte) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_byteObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.leq((byte) 2));
        mock.byteObjectCall(null);
    }

    @Test
    public void lt_byteCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.lt((byte) 2));
        mock.byteCall((byte) 1);
        Moxie.verify(mock);
    }

    @Test
    public void lt_byteObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.lt((byte) 2));
        mock.byteObjectCall((byte) 1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_byteCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.lt((byte) 2));
        mock.byteCall((byte) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_byteObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.lt((byte) 2));
        mock.byteObjectCall((byte) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_byteCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.lt((byte) 2));
        mock.byteCall((byte) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_byteObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.lt((byte) 2));
        mock.byteObjectCall((byte) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_byteObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.lt((byte) 2));
        mock.byteObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_byteCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.geq((byte) 2));
        mock.byteCall((byte) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_byteObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.geq((byte) 2));
        mock.byteObjectCall((byte) 1);
    }

    @Test
    public void geq_byteCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.geq((byte) 2));
        mock.byteCall((byte) 2);
        Moxie.verify(mock);
    }

    @Test
    public void geq_byteObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.geq((byte) 2));
        mock.byteObjectCall((byte) 2);
        Moxie.verify(mock);
    }

    @Test
    public void geq_byteCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.geq((byte) 2));
        mock.byteCall((byte) 3);
        Moxie.verify(mock);
    }

    @Test
    public void geq_byteObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.geq((byte) 2));
        mock.byteObjectCall((byte) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_byteObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.geq((byte) 2));
        mock.byteObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_byteCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.gt((byte) 2));
        mock.byteCall((byte) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_byteObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.gt((byte) 2));
        mock.byteObjectCall((byte) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_byteCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.gt((byte) 2));
        mock.byteCall((byte) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_byteObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.gt((byte) 2));
        mock.byteObjectCall((byte) 2);
    }

    @Test
    public void gt_byteCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.gt((byte) 2));
        mock.byteCall((byte) 3);
        Moxie.verify(mock);
    }

    @Test
    public void gt_byteObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.gt((byte) 2));
        mock.byteObjectCall((byte) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_byteObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.gt((byte) 2));
        mock.byteObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_byteCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.and(new byte[]{Moxie.lt((byte) 4), Moxie.gt((byte) 2)}));
        mock.byteCall((byte) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_byteObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.and(new byte[]{Moxie.lt((byte) 4), Moxie.gt((byte) 2)}));
        mock.byteObjectCall((byte) 1);
    }

    @Test
    public void and_byteCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.and(new byte[]{Moxie.lt((byte) 4), Moxie.gt((byte) 2)}));
        mock.byteCall((byte) 3);
        Moxie.verify(mock);
    }

    @Test
    public void and_byteObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.and(new byte[]{Moxie.lt((byte) 4), Moxie.gt((byte) 2)}));
        mock.byteObjectCall((byte) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_byteCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.and(new byte[]{Moxie.lt((byte) 4), Moxie.gt((byte) 2)}));
        mock.byteCall((byte) 5);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_byteObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.and(new byte[]{Moxie.lt((byte) 4), Moxie.gt((byte) 2)}));
        mock.byteObjectCall((byte) 5);
    }

    @Test
    public void or_byteCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.or(new byte[]{Moxie.gt((byte) 4), Moxie.lt((byte) 2)}));
        mock.byteCall((byte) 1);
        Moxie.verify(mock);
    }

    @Test
    public void or_byteObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.or(new byte[]{Moxie.gt((byte) 4), Moxie.lt((byte) 2)}));
        mock.byteObjectCall((byte) 1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void or_byteCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.or(new byte[]{Moxie.gt((byte) 4), Moxie.lt((byte) 2)}));
        mock.byteCall((byte) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void or_byteObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.or(new byte[]{Moxie.gt((byte) 4), Moxie.lt((byte) 2)}));
        mock.byteObjectCall((byte) 3);
    }

    @Test
    public void or_byteCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.or(new byte[]{Moxie.gt((byte) 4), Moxie.lt((byte) 2)}));
        mock.byteCall((byte) 5);
        Moxie.verify(mock);
    }

    @Test
    public void or_byteObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.or(new byte[]{Moxie.gt((byte) 4), Moxie.lt((byte) 2)}));
        mock.byteObjectCall((byte) 5);
        Moxie.verify(mock);
    }

    @Test
    public void not_byteCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.not(Moxie.lt((byte) 3)));
        mock.byteCall((byte) 5);
        Moxie.verify(mock);
    }

    @Test
    public void not_byteObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.not(Moxie.lt((byte) 3)));
        mock.byteObjectCall((byte) 5);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_byteCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.not(Moxie.lt((byte) 3)));
        mock.byteCall((byte) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_byteObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.not(Moxie.lt((byte) 3)));
        mock.byteObjectCall((byte) 1);
    }

    @Test
    public void byteThat_byteCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.byteThat(new IsEqual((byte) 2)));
        mock.byteCall((byte) 2);
        Moxie.verify(mock);
    }

    @Test
    public void byteThat_byteObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.byteThat(new IsEqual((byte) 2)));
        mock.byteObjectCall((byte) 2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void byteThat_byteCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteCall(Moxie.byteThat(new IsEqual((byte) 2)));
        mock.byteCall((byte) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void byteThat_byteObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().byteObjectCall(Moxie.byteThat(new IsEqual((byte) 2)));
        mock.byteObjectCall((byte) 1);
    }

    @Test
    public void aryEq_arrayByteCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteCall(Moxie.aryEq(new byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.arrayByteCall(new byte[]{(byte) 1, (byte) 2, (byte) 3});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsByteCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteCall(Moxie.aryEq(new byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.varargsByteCall((byte) 1, (byte) 2, (byte) 3);
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_arrayByteObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteObjectCall(Moxie.aryEq(new Byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.arrayByteObjectCall(new Byte[]{(byte) 1, (byte) 2, (byte) 3});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsByteObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteObjectCall(Moxie.aryEq(new Byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.varargsByteObjectCall((byte) 1, (byte) 2, (byte) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayByteCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteCall(Moxie.aryEq(new byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.arrayByteCall(new byte[]{(byte) 1, (byte) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsByteCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteCall(Moxie.aryEq(new byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.varargsByteCall((byte) 1, (byte) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayByteObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteObjectCall(Moxie.aryEq(new Byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.arrayByteObjectCall(new Byte[]{(byte) 1, (byte) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsByteObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteObjectCall(Moxie.aryEq(new Byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.varargsByteObjectCall((byte) 1, (byte) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayByteCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteCall(Moxie.aryEq(new byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.arrayByteCall(new byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsByteCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteCall(Moxie.aryEq(new byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.varargsByteCall((byte) 1, (byte) 2, (byte) 3, (byte) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayByteObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteObjectCall(Moxie.aryEq(new Byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.arrayByteObjectCall(new Byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsByteObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteObjectCall(Moxie.aryEq(new Byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.varargsByteObjectCall((byte) 1, (byte) 2, (byte) 3, (byte) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayByteCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteCall(Moxie.aryEq(new byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.arrayByteCall(new byte[]{(byte) 1, (byte) 2, (byte) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsByteCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteCall(Moxie.aryEq(new byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.varargsByteCall((byte) 1, (byte) 2, (byte) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayByteObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteObjectCall(Moxie.aryEq(new Byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.arrayByteObjectCall(new Byte[]{(byte) 1, (byte) 2, (byte) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsByteObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteObjectCall(Moxie.aryEq(new Byte[]{(byte) 1, (byte) 2, (byte) 3}));
        mock.varargsByteObjectCall((byte) 1, (byte) 2, (byte) 4);
    }

    @Test
    public void array_arrayByteCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteCall(Moxie.array(new byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.arrayByteCall(new byte[]{(byte) 1, (byte) 2, (byte) 3});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsByteCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteCall(Moxie.array(new byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.varargsByteCall((byte) 1, (byte) 2, (byte) 3);
        Moxie.verify(mock);
    }

    @Test
    public void array_arrayByteObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteObjectCall(Moxie.array(new Byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.arrayByteObjectCall(new Byte[]{(byte) 1, (byte) 2, (byte) 3});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsByteObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteObjectCall(Moxie.array(new Byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.varargsByteObjectCall((byte) 1, (byte) 2, (byte) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayByteCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteCall(Moxie.array(new byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.arrayByteCall(new byte[]{(byte) 1, (byte) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsByteCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteCall(Moxie.array(new byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.varargsByteCall((byte) 1, (byte) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayByteObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteObjectCall(Moxie.array(new Byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.arrayByteObjectCall(new Byte[]{(byte) 1, (byte) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsByteObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteObjectCall(Moxie.array(new Byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.varargsByteObjectCall((byte) 1, (byte) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayByteCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteCall(Moxie.array(new byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.arrayByteCall(new byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsByteCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteCall(Moxie.array(new byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.varargsByteCall((byte) 1, (byte) 2, (byte) 3, (byte) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayByteObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteObjectCall(Moxie.array(new Byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.arrayByteObjectCall(new Byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsByteObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteObjectCall(Moxie.array(new Byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.varargsByteObjectCall((byte) 1, (byte) 2, (byte) 3, (byte) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayByteCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteCall(Moxie.array(new byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.arrayByteCall(new byte[]{(byte) 1, (byte) 2, (byte) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsByteCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteCall(Moxie.array(new byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.varargsByteCall((byte) 1, (byte) 2, (byte) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayByteObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteObjectCall(Moxie.array(new Byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.arrayByteObjectCall(new Byte[]{(byte) 1, (byte) 2, (byte) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsByteObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteObjectCall(Moxie.array(new Byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.varargsByteObjectCall((byte) 1, (byte) 2, (byte) 4);
    }

    @Test
    public void array_arrayByteCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteCall(Moxie.array(new byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.arrayByteCall(new byte[]{(byte) 0, (byte) 1, (byte) 2});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsByteCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteCall(Moxie.array(new byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.varargsByteCall((byte) 0, (byte) 1, (byte) 2);
        Moxie.verify(mock);
    }

    @Test
    public void array_arrayByteObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayByteObjectCall(Moxie.array(new Byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.arrayByteObjectCall(new Byte[]{(byte) 0, (byte) 1, (byte) 2});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsByteObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsByteObjectCall(Moxie.array(new Byte[]{Moxie.lt((byte) 2), Moxie.lt((byte) 3), Moxie.lt((byte) 4)}));
        mock.varargsByteObjectCall((byte) 0, (byte) 1, (byte) 2);
        Moxie.verify(mock);
    }

    @Test
    public void leq_charCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.leq('2'));
        mock.charCall('1');
        Moxie.verify(mock);
    }

    @Test
    public void leq_charObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.leq('2'));
        mock.charObjectCall('1');
        Moxie.verify(mock);
    }

    @Test
    public void leq_charCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.leq('2'));
        mock.charCall('2');
        Moxie.verify(mock);
    }

    @Test
    public void leq_charObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.leq('2'));
        mock.charObjectCall('2');
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_charCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.leq('2'));
        mock.charCall('3');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_charObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.leq('2'));
        mock.charObjectCall('3');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_charObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.leq('2'));
        mock.charObjectCall(null);
    }

    @Test
    public void lt_charCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.lt('2'));
        mock.charCall('1');
        Moxie.verify(mock);
    }

    @Test
    public void lt_charObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.lt('2'));
        mock.charObjectCall('1');
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_charCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.lt('2'));
        mock.charCall('2');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_charObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.lt('2'));
        mock.charObjectCall('2');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_charCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.lt('2'));
        mock.charCall('3');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_charObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.lt('2'));
        mock.charObjectCall('3');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_charObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.lt('2'));
        mock.charObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_charCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.geq('2'));
        mock.charCall('1');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_charObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.geq('2'));
        mock.charObjectCall('1');
    }

    @Test
    public void geq_charCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.geq('2'));
        mock.charCall('2');
        Moxie.verify(mock);
    }

    @Test
    public void geq_charObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.geq('2'));
        mock.charObjectCall('2');
        Moxie.verify(mock);
    }

    @Test
    public void geq_charCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.geq('2'));
        mock.charCall('3');
        Moxie.verify(mock);
    }

    @Test
    public void geq_charObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.geq('2'));
        mock.charObjectCall('3');
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_charObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.geq('2'));
        mock.charObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_charCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.gt('2'));
        mock.charCall('1');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_charObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.gt('2'));
        mock.charObjectCall('1');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_charCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.gt('2'));
        mock.charCall('2');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_charObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.gt('2'));
        mock.charObjectCall('2');
    }

    @Test
    public void gt_charCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.gt('2'));
        mock.charCall('3');
        Moxie.verify(mock);
    }

    @Test
    public void gt_charObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.gt('2'));
        mock.charObjectCall('3');
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_charObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.gt('2'));
        mock.charObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_charCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.and(new char[]{Moxie.lt('4'), Moxie.gt('2')}));
        mock.charCall('1');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_charObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.and(new char[]{Moxie.lt('4'), Moxie.gt('2')}));
        mock.charObjectCall('1');
    }

    @Test
    public void and_charCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.and(new char[]{Moxie.lt('4'), Moxie.gt('2')}));
        mock.charCall('3');
        Moxie.verify(mock);
    }

    @Test
    public void and_charObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.and(new char[]{Moxie.lt('4'), Moxie.gt('2')}));
        mock.charObjectCall('3');
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_charCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.and(new char[]{Moxie.lt('4'), Moxie.gt('2')}));
        mock.charCall('5');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_charObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.and(new char[]{Moxie.lt('4'), Moxie.gt('2')}));
        mock.charObjectCall('5');
    }

    @Test
    public void or_charCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.or(new char[]{Moxie.gt('4'), Moxie.lt('2')}));
        mock.charCall('1');
        Moxie.verify(mock);
    }

    @Test
    public void or_charObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.or(new char[]{Moxie.gt('4'), Moxie.lt('2')}));
        mock.charObjectCall('1');
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void or_charCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.or(new char[]{Moxie.gt('4'), Moxie.lt('2')}));
        mock.charCall('3');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void or_charObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.or(new char[]{Moxie.gt('4'), Moxie.lt('2')}));
        mock.charObjectCall('3');
    }

    @Test
    public void or_charCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.or(new char[]{Moxie.gt('4'), Moxie.lt('2')}));
        mock.charCall('5');
        Moxie.verify(mock);
    }

    @Test
    public void or_charObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.or(new char[]{Moxie.gt('4'), Moxie.lt('2')}));
        mock.charObjectCall('5');
        Moxie.verify(mock);
    }

    @Test
    public void not_charCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.not(Moxie.lt('3')));
        mock.charCall('5');
        Moxie.verify(mock);
    }

    @Test
    public void not_charObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.not(Moxie.lt('3')));
        mock.charObjectCall('5');
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_charCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.not(Moxie.lt('3')));
        mock.charCall('1');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_charObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.not(Moxie.lt('3')));
        mock.charObjectCall('1');
    }

    @Test
    public void charThat_charCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.charThat(new IsEqual('2')));
        mock.charCall('2');
        Moxie.verify(mock);
    }

    @Test
    public void charThat_charObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.charThat(new IsEqual('2')));
        mock.charObjectCall('2');
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void charThat_charCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charCall(Moxie.charThat(new IsEqual('2')));
        mock.charCall('1');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void charThat_charObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().charObjectCall(Moxie.charThat(new IsEqual('2')));
        mock.charObjectCall('1');
    }

    @Test
    public void aryEq_arrayCharCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharCall(Moxie.aryEq(new char[]{'1', '2', '3'}));
        mock.arrayCharCall(new char[]{'1', '2', '3'});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsCharCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharCall(Moxie.aryEq(new char[]{'1', '2', '3'}));
        mock.varargsCharCall('1', '2', '3');
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_arrayCharObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharObjectCall(Moxie.aryEq(new Character[]{'1', '2', '3'}));
        mock.arrayCharObjectCall(new Character[]{'1', '2', '3'});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsCharObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharObjectCall(Moxie.aryEq(new Character[]{'1', '2', '3'}));
        mock.varargsCharObjectCall('1', '2', '3');
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayCharCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharCall(Moxie.aryEq(new char[]{'1', '2', '3'}));
        mock.arrayCharCall(new char[]{'1', '2'});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsCharCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharCall(Moxie.aryEq(new char[]{'1', '2', '3'}));
        mock.varargsCharCall('1', '2');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayCharObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharObjectCall(Moxie.aryEq(new Character[]{'1', '2', '3'}));
        mock.arrayCharObjectCall(new Character[]{'1', '2'});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsCharObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharObjectCall(Moxie.aryEq(new Character[]{'1', '2', '3'}));
        mock.varargsCharObjectCall('1', '2');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayCharCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharCall(Moxie.aryEq(new char[]{'1', '2', '3'}));
        mock.arrayCharCall(new char[]{'1', '2', '3', '4'});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsCharCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharCall(Moxie.aryEq(new char[]{'1', '2', '3'}));
        mock.varargsCharCall('1', '2', '3', '4');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayCharObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharObjectCall(Moxie.aryEq(new Character[]{'1', '2', '3'}));
        mock.arrayCharObjectCall(new Character[]{'1', '2', '3', '4'});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsCharObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharObjectCall(Moxie.aryEq(new Character[]{'1', '2', '3'}));
        mock.varargsCharObjectCall('1', '2', '3', '4');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayCharCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharCall(Moxie.aryEq(new char[]{'1', '2', '3'}));
        mock.arrayCharCall(new char[]{'1', '2', '4'});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsCharCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharCall(Moxie.aryEq(new char[]{'1', '2', '3'}));
        mock.varargsCharCall('1', '2', '4');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayCharObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharObjectCall(Moxie.aryEq(new Character[]{'1', '2', '3'}));
        mock.arrayCharObjectCall(new Character[]{'1', '2', '4'});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsCharObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharObjectCall(Moxie.aryEq(new Character[]{'1', '2', '3'}));
        mock.varargsCharObjectCall('1', '2', '4');
    }

    @Test
    public void array_arrayCharCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharCall(Moxie.array(new char[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.arrayCharCall(new char[]{'1', '2', '3'});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsCharCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharCall(Moxie.array(new char[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.varargsCharCall('1', '2', '3');
        Moxie.verify(mock);
    }

    @Test
    public void array_arrayCharObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharObjectCall(Moxie.array(new Character[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.arrayCharObjectCall(new Character[]{'1', '2', '3'});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsCharObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharObjectCall(Moxie.array(new Character[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.varargsCharObjectCall('1', '2', '3');
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayCharCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharCall(Moxie.array(new char[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.arrayCharCall(new char[]{'1', '2'});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsCharCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharCall(Moxie.array(new char[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.varargsCharCall('1', '2');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayCharObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharObjectCall(Moxie.array(new Character[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.arrayCharObjectCall(new Character[]{'1', '2'});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsCharObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharObjectCall(Moxie.array(new Character[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.varargsCharObjectCall('1', '2');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayCharCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharCall(Moxie.array(new char[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.arrayCharCall(new char[]{'1', '2', '3', '4'});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsCharCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharCall(Moxie.array(new char[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.varargsCharCall('1', '2', '3', '4');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayCharObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharObjectCall(Moxie.array(new Character[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.arrayCharObjectCall(new Character[]{'1', '2', '3', '4'});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsCharObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharObjectCall(Moxie.array(new Character[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.varargsCharObjectCall('1', '2', '3', '4');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayCharCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharCall(Moxie.array(new char[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.arrayCharCall(new char[]{'1', '2', '4'});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsCharCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharCall(Moxie.array(new char[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.varargsCharCall('1', '2', '4');
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayCharObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharObjectCall(Moxie.array(new Character[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.arrayCharObjectCall(new Character[]{'1', '2', '4'});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsCharObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharObjectCall(Moxie.array(new Character[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.varargsCharObjectCall('1', '2', '4');
    }

    @Test
    public void array_arrayCharCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharCall(Moxie.array(new char[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.arrayCharCall(new char[]{'0', '1', '2'});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsCharCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharCall(Moxie.array(new char[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.varargsCharCall('0', '1', '2');
        Moxie.verify(mock);
    }

    @Test
    public void array_arrayCharObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayCharObjectCall(Moxie.array(new Character[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.arrayCharObjectCall(new Character[]{'0', '1', '2'});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsCharObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsCharObjectCall(Moxie.array(new Character[]{Moxie.lt('2'), Moxie.lt('3'), Moxie.lt('4')}));
        mock.varargsCharObjectCall('0', '1', '2');
        Moxie.verify(mock);
    }

    @Test
    public void leq_shortCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.leq((short) 2));
        mock.shortCall((short) 1);
        Moxie.verify(mock);
    }

    @Test
    public void leq_shortObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.leq((short) 2));
        mock.shortObjectCall((short) 1);
        Moxie.verify(mock);
    }

    @Test
    public void leq_shortCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.leq((short) 2));
        mock.shortCall((short) 2);
        Moxie.verify(mock);
    }

    @Test
    public void leq_shortObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.leq((short) 2));
        mock.shortObjectCall((short) 2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_shortCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.leq((short) 2));
        mock.shortCall((short) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_shortObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.leq((short) 2));
        mock.shortObjectCall((short) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_shortObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.leq((short) 2));
        mock.shortObjectCall(null);
    }

    @Test
    public void lt_shortCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.lt((short) 2));
        mock.shortCall((short) 1);
        Moxie.verify(mock);
    }

    @Test
    public void lt_shortObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.lt((short) 2));
        mock.shortObjectCall((short) 1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_shortCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.lt((short) 2));
        mock.shortCall((short) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_shortObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.lt((short) 2));
        mock.shortObjectCall((short) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_shortCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.lt((short) 2));
        mock.shortCall((short) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_shortObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.lt((short) 2));
        mock.shortObjectCall((short) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_shortObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.lt((short) 2));
        mock.shortObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_shortCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.geq((short) 2));
        mock.shortCall((short) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_shortObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.geq((short) 2));
        mock.shortObjectCall((short) 1);
    }

    @Test
    public void geq_shortCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.geq((short) 2));
        mock.shortCall((short) 2);
        Moxie.verify(mock);
    }

    @Test
    public void geq_shortObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.geq((short) 2));
        mock.shortObjectCall((short) 2);
        Moxie.verify(mock);
    }

    @Test
    public void geq_shortCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.geq((short) 2));
        mock.shortCall((short) 3);
        Moxie.verify(mock);
    }

    @Test
    public void geq_shortObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.geq((short) 2));
        mock.shortObjectCall((short) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_shortObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.geq((short) 2));
        mock.shortObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_shortCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.gt((short) 2));
        mock.shortCall((short) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_shortObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.gt((short) 2));
        mock.shortObjectCall((short) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_shortCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.gt((short) 2));
        mock.shortCall((short) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_shortObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.gt((short) 2));
        mock.shortObjectCall((short) 2);
    }

    @Test
    public void gt_shortCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.gt((short) 2));
        mock.shortCall((short) 3);
        Moxie.verify(mock);
    }

    @Test
    public void gt_shortObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.gt((short) 2));
        mock.shortObjectCall((short) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_shortObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.gt((short) 2));
        mock.shortObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_shortCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.and(new short[]{Moxie.lt((short) 4), Moxie.gt((short) 2)}));
        mock.shortCall((short) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_shortObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.and(new short[]{Moxie.lt((short) 4), Moxie.gt((short) 2)}));
        mock.shortObjectCall((short) 1);
    }

    @Test
    public void and_shortCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.and(new short[]{Moxie.lt((short) 4), Moxie.gt((short) 2)}));
        mock.shortCall((short) 3);
        Moxie.verify(mock);
    }

    @Test
    public void and_shortObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.and(new short[]{Moxie.lt((short) 4), Moxie.gt((short) 2)}));
        mock.shortObjectCall((short) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_shortCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.and(new short[]{Moxie.lt((short) 4), Moxie.gt((short) 2)}));
        mock.shortCall((short) 5);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_shortObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.and(new short[]{Moxie.lt((short) 4), Moxie.gt((short) 2)}));
        mock.shortObjectCall((short) 5);
    }

    @Test
    public void or_shortCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.or(new short[]{Moxie.gt((short) 4), Moxie.lt((short) 2)}));
        mock.shortCall((short) 1);
        Moxie.verify(mock);
    }

    @Test
    public void or_shortObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.or(new short[]{Moxie.gt((short) 4), Moxie.lt((short) 2)}));
        mock.shortObjectCall((short) 1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void or_shortCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.or(new short[]{Moxie.gt((short) 4), Moxie.lt((short) 2)}));
        mock.shortCall((short) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void or_shortObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.or(new short[]{Moxie.gt((short) 4), Moxie.lt((short) 2)}));
        mock.shortObjectCall((short) 3);
    }

    @Test
    public void or_shortCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.or(new short[]{Moxie.gt((short) 4), Moxie.lt((short) 2)}));
        mock.shortCall((short) 5);
        Moxie.verify(mock);
    }

    @Test
    public void or_shortObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.or(new short[]{Moxie.gt((short) 4), Moxie.lt((short) 2)}));
        mock.shortObjectCall((short) 5);
        Moxie.verify(mock);
    }

    @Test
    public void not_shortCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.not(Moxie.lt((short) 3)));
        mock.shortCall((short) 5);
        Moxie.verify(mock);
    }

    @Test
    public void not_shortObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.not(Moxie.lt((short) 3)));
        mock.shortObjectCall((short) 5);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_shortCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.not(Moxie.lt((short) 3)));
        mock.shortCall((short) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_shortObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.not(Moxie.lt((short) 3)));
        mock.shortObjectCall((short) 1);
    }

    @Test
    public void shortThat_shortCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.shortThat(new IsEqual((short) 2)));
        mock.shortCall((short) 2);
        Moxie.verify(mock);
    }

    @Test
    public void shortThat_shortObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.shortThat(new IsEqual((short) 2)));
        mock.shortObjectCall((short) 2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void shortThat_shortCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortCall(Moxie.shortThat(new IsEqual((short) 2)));
        mock.shortCall((short) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void shortThat_shortObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().shortObjectCall(Moxie.shortThat(new IsEqual((short) 2)));
        mock.shortObjectCall((short) 1);
    }

    @Test
    public void aryEq_arrayShortCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortCall(Moxie.aryEq(new short[]{(short) 1, (short) 2, (short) 3}));
        mock.arrayShortCall(new short[]{(short) 1, (short) 2, (short) 3});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsShortCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortCall(Moxie.aryEq(new short[]{(short) 1, (short) 2, (short) 3}));
        mock.varargsShortCall((short) 1, (short) 2, (short) 3);
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_arrayShortObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortObjectCall(Moxie.aryEq(new Short[]{(short) 1, (short) 2, (short) 3}));
        mock.arrayShortObjectCall(new Short[]{(short) 1, (short) 2, (short) 3});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsShortObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortObjectCall(Moxie.aryEq(new Short[]{(short) 1, (short) 2, (short) 3}));
        mock.varargsShortObjectCall((short) 1, (short) 2, (short) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayShortCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortCall(Moxie.aryEq(new short[]{(short) 1, (short) 2, (short) 3}));
        mock.arrayShortCall(new short[]{(short) 1, (short) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsShortCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortCall(Moxie.aryEq(new short[]{(short) 1, (short) 2, (short) 3}));
        mock.varargsShortCall((short) 1, (short) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayShortObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortObjectCall(Moxie.aryEq(new Short[]{(short) 1, (short) 2, (short) 3}));
        mock.arrayShortObjectCall(new Short[]{(short) 1, (short) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsShortObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortObjectCall(Moxie.aryEq(new Short[]{(short) 1, (short) 2, (short) 3}));
        mock.varargsShortObjectCall((short) 1, (short) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayShortCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortCall(Moxie.aryEq(new short[]{(short) 1, (short) 2, (short) 3}));
        mock.arrayShortCall(new short[]{(short) 1, (short) 2, (short) 3, (short) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsShortCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortCall(Moxie.aryEq(new short[]{(short) 1, (short) 2, (short) 3}));
        mock.varargsShortCall((short) 1, (short) 2, (short) 3, (short) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayShortObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortObjectCall(Moxie.aryEq(new Short[]{(short) 1, (short) 2, (short) 3}));
        mock.arrayShortObjectCall(new Short[]{(short) 1, (short) 2, (short) 3, (short) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsShortObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortObjectCall(Moxie.aryEq(new Short[]{(short) 1, (short) 2, (short) 3}));
        mock.varargsShortObjectCall((short) 1, (short) 2, (short) 3, (short) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayShortCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortCall(Moxie.aryEq(new short[]{(short) 1, (short) 2, (short) 3}));
        mock.arrayShortCall(new short[]{(short) 1, (short) 2, (short) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsShortCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortCall(Moxie.aryEq(new short[]{(short) 1, (short) 2, (short) 3}));
        mock.varargsShortCall((short) 1, (short) 2, (short) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayShortObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortObjectCall(Moxie.aryEq(new Short[]{(short) 1, (short) 2, (short) 3}));
        mock.arrayShortObjectCall(new Short[]{(short) 1, (short) 2, (short) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsShortObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortObjectCall(Moxie.aryEq(new Short[]{(short) 1, (short) 2, (short) 3}));
        mock.varargsShortObjectCall((short) 1, (short) 2, (short) 4);
    }

    @Test
    public void array_arrayShortCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortCall(Moxie.array(new short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.arrayShortCall(new short[]{(short) 1, (short) 2, (short) 3});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsShortCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortCall(Moxie.array(new short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.varargsShortCall((short) 1, (short) 2, (short) 3);
        Moxie.verify(mock);
    }

    @Test
    public void array_arrayShortObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortObjectCall(Moxie.array(new Short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.arrayShortObjectCall(new Short[]{(short) 1, (short) 2, (short) 3});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsShortObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortObjectCall(Moxie.array(new Short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.varargsShortObjectCall((short) 1, (short) 2, (short) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayShortCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortCall(Moxie.array(new short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.arrayShortCall(new short[]{(short) 1, (short) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsShortCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortCall(Moxie.array(new short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.varargsShortCall((short) 1, (short) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayShortObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortObjectCall(Moxie.array(new Short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.arrayShortObjectCall(new Short[]{(short) 1, (short) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsShortObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortObjectCall(Moxie.array(new Short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.varargsShortObjectCall((short) 1, (short) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayShortCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortCall(Moxie.array(new short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.arrayShortCall(new short[]{(short) 1, (short) 2, (short) 3, (short) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsShortCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortCall(Moxie.array(new short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.varargsShortCall((short) 1, (short) 2, (short) 3, (short) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayShortObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortObjectCall(Moxie.array(new Short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.arrayShortObjectCall(new Short[]{(short) 1, (short) 2, (short) 3, (short) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsShortObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortObjectCall(Moxie.array(new Short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.varargsShortObjectCall((short) 1, (short) 2, (short) 3, (short) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayShortCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortCall(Moxie.array(new short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.arrayShortCall(new short[]{(short) 1, (short) 2, (short) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsShortCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortCall(Moxie.array(new short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.varargsShortCall((short) 1, (short) 2, (short) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayShortObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortObjectCall(Moxie.array(new Short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.arrayShortObjectCall(new Short[]{(short) 1, (short) 2, (short) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsShortObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortObjectCall(Moxie.array(new Short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.varargsShortObjectCall((short) 1, (short) 2, (short) 4);
    }

    @Test
    public void array_arrayShortCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortCall(Moxie.array(new short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.arrayShortCall(new short[]{(short) 0, (short) 1, (short) 2});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsShortCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortCall(Moxie.array(new short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.varargsShortCall((short) 0, (short) 1, (short) 2);
        Moxie.verify(mock);
    }

    @Test
    public void array_arrayShortObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayShortObjectCall(Moxie.array(new Short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.arrayShortObjectCall(new Short[]{(short) 0, (short) 1, (short) 2});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsShortObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsShortObjectCall(Moxie.array(new Short[]{Moxie.lt((short) 2), Moxie.lt((short) 3), Moxie.lt((short) 4)}));
        mock.varargsShortObjectCall((short) 0, (short) 1, (short) 2);
        Moxie.verify(mock);
    }

    @Test
    public void leq_intCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.leq(2));
        mock.intCall(1);
        Moxie.verify(mock);
    }

    @Test
    public void leq_intObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.leq(2));
        mock.intObjectCall(1);
        Moxie.verify(mock);
    }

    @Test
    public void leq_intCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.leq(2));
        mock.intCall(2);
        Moxie.verify(mock);
    }

    @Test
    public void leq_intObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.leq(2));
        mock.intObjectCall(2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_intCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.leq(2));
        mock.intCall(3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_intObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.leq(2));
        mock.intObjectCall(3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_intObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.leq(2));
        mock.intObjectCall(null);
    }

    @Test
    public void lt_intCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.lt(2));
        mock.intCall(1);
        Moxie.verify(mock);
    }

    @Test
    public void lt_intObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.lt(2));
        mock.intObjectCall(1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_intCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.lt(2));
        mock.intCall(2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_intObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.lt(2));
        mock.intObjectCall(2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_intCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.lt(2));
        mock.intCall(3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_intObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.lt(2));
        mock.intObjectCall(3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_intObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.lt(2));
        mock.intObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_intCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.geq(2));
        mock.intCall(1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_intObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.geq(2));
        mock.intObjectCall(1);
    }

    @Test
    public void geq_intCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.geq(2));
        mock.intCall(2);
        Moxie.verify(mock);
    }

    @Test
    public void geq_intObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.geq(2));
        mock.intObjectCall(2);
        Moxie.verify(mock);
    }

    @Test
    public void geq_intCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.geq(2));
        mock.intCall(3);
        Moxie.verify(mock);
    }

    @Test
    public void geq_intObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.geq(2));
        mock.intObjectCall(3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_intObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.geq(2));
        mock.intObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_intCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.gt(2));
        mock.intCall(1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_intObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.gt(2));
        mock.intObjectCall(1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_intCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.gt(2));
        mock.intCall(2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_intObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.gt(2));
        mock.intObjectCall(2);
    }

    @Test
    public void gt_intCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.gt(2));
        mock.intCall(3);
        Moxie.verify(mock);
    }

    @Test
    public void gt_intObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.gt(2));
        mock.intObjectCall(3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_intObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.gt(2));
        mock.intObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_intCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.and(new int[]{Moxie.lt(4), Moxie.gt(2)}));
        mock.intCall(1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_intObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.and(new int[]{Moxie.lt(4), Moxie.gt(2)}));
        mock.intObjectCall(1);
    }

    @Test
    public void and_intCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.and(new int[]{Moxie.lt(4), Moxie.gt(2)}));
        mock.intCall(3);
        Moxie.verify(mock);
    }

    @Test
    public void and_intObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.and(new int[]{Moxie.lt(4), Moxie.gt(2)}));
        mock.intObjectCall(3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_intCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.and(new int[]{Moxie.lt(4), Moxie.gt(2)}));
        mock.intCall(5);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_intObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.and(new int[]{Moxie.lt(4), Moxie.gt(2)}));
        mock.intObjectCall(5);
    }

    @Test
    public void or_intCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.or(new int[]{Moxie.gt(4), Moxie.lt(2)}));
        mock.intCall(1);
        Moxie.verify(mock);
    }

    @Test
    public void or_intObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.or(new int[]{Moxie.gt(4), Moxie.lt(2)}));
        mock.intObjectCall(1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void or_intCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.or(new int[]{Moxie.gt(4), Moxie.lt(2)}));
        mock.intCall(3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void or_intObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.or(new int[]{Moxie.gt(4), Moxie.lt(2)}));
        mock.intObjectCall(3);
    }

    @Test
    public void or_intCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.or(new int[]{Moxie.gt(4), Moxie.lt(2)}));
        mock.intCall(5);
        Moxie.verify(mock);
    }

    @Test
    public void or_intObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.or(new int[]{Moxie.gt(4), Moxie.lt(2)}));
        mock.intObjectCall(5);
        Moxie.verify(mock);
    }

    @Test
    public void not_intCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.not(Moxie.lt(3)));
        mock.intCall(5);
        Moxie.verify(mock);
    }

    @Test
    public void not_intObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.not(Moxie.lt(3)));
        mock.intObjectCall(5);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_intCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.not(Moxie.lt(3)));
        mock.intCall(1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_intObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.not(Moxie.lt(3)));
        mock.intObjectCall(1);
    }

    @Test
    public void intThat_intCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.intThat(new IsEqual(2)));
        mock.intCall(2);
        Moxie.verify(mock);
    }

    @Test
    public void intThat_intObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.intThat(new IsEqual(2)));
        mock.intObjectCall(2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void intThat_intCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intCall(Moxie.intThat(new IsEqual(2)));
        mock.intCall(1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void intThat_intObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().intObjectCall(Moxie.intThat(new IsEqual(2)));
        mock.intObjectCall(1);
    }

    @Test
    public void aryEq_arrayIntCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntCall(Moxie.aryEq(new int[]{1, 2, 3}));
        mock.arrayIntCall(new int[]{1, 2, 3});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsIntCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntCall(Moxie.aryEq(new int[]{1, 2, 3}));
        mock.varargsIntCall(1, 2, 3);
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_arrayIntObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntObjectCall(Moxie.aryEq(new Integer[]{1, 2, 3}));
        mock.arrayIntObjectCall(new Integer[]{1, 2, 3});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsIntObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntObjectCall(Moxie.aryEq(new Integer[]{1, 2, 3}));
        mock.varargsIntObjectCall(1, 2, 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayIntCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntCall(Moxie.aryEq(new int[]{1, 2, 3}));
        mock.arrayIntCall(new int[]{1, 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsIntCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntCall(Moxie.aryEq(new int[]{1, 2, 3}));
        mock.varargsIntCall(1, 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayIntObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntObjectCall(Moxie.aryEq(new Integer[]{1, 2, 3}));
        mock.arrayIntObjectCall(new Integer[]{1, 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsIntObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntObjectCall(Moxie.aryEq(new Integer[]{1, 2, 3}));
        mock.varargsIntObjectCall(1, 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayIntCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntCall(Moxie.aryEq(new int[]{1, 2, 3}));
        mock.arrayIntCall(new int[]{1, 2, 3, 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsIntCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntCall(Moxie.aryEq(new int[]{1, 2, 3}));
        mock.varargsIntCall(1, 2, 3, 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayIntObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntObjectCall(Moxie.aryEq(new Integer[]{1, 2, 3}));
        mock.arrayIntObjectCall(new Integer[]{1, 2, 3, 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsIntObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntObjectCall(Moxie.aryEq(new Integer[]{1, 2, 3}));
        mock.varargsIntObjectCall(1, 2, 3, 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayIntCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntCall(Moxie.aryEq(new int[]{1, 2, 3}));
        mock.arrayIntCall(new int[]{1, 2, 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsIntCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntCall(Moxie.aryEq(new int[]{1, 2, 3}));
        mock.varargsIntCall(1, 2, 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayIntObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntObjectCall(Moxie.aryEq(new Integer[]{1, 2, 3}));
        mock.arrayIntObjectCall(new Integer[]{1, 2, 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsIntObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntObjectCall(Moxie.aryEq(new Integer[]{1, 2, 3}));
        mock.varargsIntObjectCall(1, 2, 4);
    }

    @Test
    public void array_arrayIntCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntCall(Moxie.array(new int[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.arrayIntCall(new int[]{1, 2, 3});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsIntCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntCall(Moxie.array(new int[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.varargsIntCall(1, 2, 3);
        Moxie.verify(mock);
    }

    @Test
    public void array_arrayIntObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntObjectCall(Moxie.array(new Integer[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.arrayIntObjectCall(new Integer[]{1, 2, 3});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsIntObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntObjectCall(Moxie.array(new Integer[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.varargsIntObjectCall(1, 2, 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayIntCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntCall(Moxie.array(new int[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.arrayIntCall(new int[]{1, 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsIntCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntCall(Moxie.array(new int[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.varargsIntCall(1, 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayIntObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntObjectCall(Moxie.array(new Integer[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.arrayIntObjectCall(new Integer[]{1, 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsIntObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntObjectCall(Moxie.array(new Integer[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.varargsIntObjectCall(1, 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayIntCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntCall(Moxie.array(new int[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.arrayIntCall(new int[]{1, 2, 3, 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsIntCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntCall(Moxie.array(new int[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.varargsIntCall(1, 2, 3, 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayIntObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntObjectCall(Moxie.array(new Integer[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.arrayIntObjectCall(new Integer[]{1, 2, 3, 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsIntObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntObjectCall(Moxie.array(new Integer[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.varargsIntObjectCall(1, 2, 3, 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayIntCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntCall(Moxie.array(new int[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.arrayIntCall(new int[]{1, 2, 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsIntCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntCall(Moxie.array(new int[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.varargsIntCall(1, 2, 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayIntObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntObjectCall(Moxie.array(new Integer[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.arrayIntObjectCall(new Integer[]{1, 2, 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsIntObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntObjectCall(Moxie.array(new Integer[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.varargsIntObjectCall(1, 2, 4);
    }

    @Test
    public void array_arrayIntCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntCall(Moxie.array(new int[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.arrayIntCall(new int[]{0, 1, 2});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsIntCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntCall(Moxie.array(new int[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.varargsIntCall(0, 1, 2);
        Moxie.verify(mock);
    }

    @Test
    public void array_arrayIntObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayIntObjectCall(Moxie.array(new Integer[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.arrayIntObjectCall(new Integer[]{0, 1, 2});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsIntObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsIntObjectCall(Moxie.array(new Integer[]{Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)}));
        mock.varargsIntObjectCall(0, 1, 2);
        Moxie.verify(mock);
    }

    @Test
    public void leq_longCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.leq((long) 2));
        mock.longCall((long) 1);
        Moxie.verify(mock);
    }

    @Test
    public void leq_longObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.leq((long) 2));
        mock.longObjectCall((long) 1);
        Moxie.verify(mock);
    }

    @Test
    public void leq_longCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.leq((long) 2));
        mock.longCall((long) 2);
        Moxie.verify(mock);
    }

    @Test
    public void leq_longObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.leq((long) 2));
        mock.longObjectCall((long) 2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_longCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.leq((long) 2));
        mock.longCall((long) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_longObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.leq((long) 2));
        mock.longObjectCall((long) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_longObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.leq((long) 2));
        mock.longObjectCall(null);
    }

    @Test
    public void lt_longCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.lt((long) 2));
        mock.longCall((long) 1);
        Moxie.verify(mock);
    }

    @Test
    public void lt_longObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.lt((long) 2));
        mock.longObjectCall((long) 1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_longCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.lt((long) 2));
        mock.longCall((long) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_longObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.lt((long) 2));
        mock.longObjectCall((long) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_longCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.lt((long) 2));
        mock.longCall((long) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_longObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.lt((long) 2));
        mock.longObjectCall((long) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_longObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.lt((long) 2));
        mock.longObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_longCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.geq((long) 2));
        mock.longCall((long) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_longObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.geq((long) 2));
        mock.longObjectCall((long) 1);
    }

    @Test
    public void geq_longCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.geq((long) 2));
        mock.longCall((long) 2);
        Moxie.verify(mock);
    }

    @Test
    public void geq_longObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.geq((long) 2));
        mock.longObjectCall((long) 2);
        Moxie.verify(mock);
    }

    @Test
    public void geq_longCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.geq((long) 2));
        mock.longCall((long) 3);
        Moxie.verify(mock);
    }

    @Test
    public void geq_longObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.geq((long) 2));
        mock.longObjectCall((long) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_longObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.geq((long) 2));
        mock.longObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_longCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.gt((long) 2));
        mock.longCall((long) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_longObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.gt((long) 2));
        mock.longObjectCall((long) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_longCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.gt((long) 2));
        mock.longCall((long) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_longObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.gt((long) 2));
        mock.longObjectCall((long) 2);
    }

    @Test
    public void gt_longCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.gt((long) 2));
        mock.longCall((long) 3);
        Moxie.verify(mock);
    }

    @Test
    public void gt_longObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.gt((long) 2));
        mock.longObjectCall((long) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_longObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.gt((long) 2));
        mock.longObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_longCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.and(new long[]{Moxie.lt((long) 4), Moxie.gt((long) 2)}));
        mock.longCall((long) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_longObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.and(new long[]{Moxie.lt((long) 4), Moxie.gt((long) 2)}));
        mock.longObjectCall((long) 1);
    }

    @Test
    public void and_longCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.and(new long[]{Moxie.lt((long) 4), Moxie.gt((long) 2)}));
        mock.longCall((long) 3);
        Moxie.verify(mock);
    }

    @Test
    public void and_longObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.and(new long[]{Moxie.lt((long) 4), Moxie.gt((long) 2)}));
        mock.longObjectCall((long) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_longCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.and(new long[]{Moxie.lt((long) 4), Moxie.gt((long) 2)}));
        mock.longCall((long) 5);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_longObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.and(new long[]{Moxie.lt((long) 4), Moxie.gt((long) 2)}));
        mock.longObjectCall((long) 5);
    }

    @Test
    public void or_longCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.or(new long[]{Moxie.gt((long) 4), Moxie.lt((long) 2)}));
        mock.longCall((long) 1);
        Moxie.verify(mock);
    }

    @Test
    public void or_longObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.or(new long[]{Moxie.gt((long) 4), Moxie.lt((long) 2)}));
        mock.longObjectCall((long) 1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void or_longCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.or(new long[]{Moxie.gt((long) 4), Moxie.lt((long) 2)}));
        mock.longCall((long) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void or_longObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.or(new long[]{Moxie.gt((long) 4), Moxie.lt((long) 2)}));
        mock.longObjectCall((long) 3);
    }

    @Test
    public void or_longCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.or(new long[]{Moxie.gt((long) 4), Moxie.lt((long) 2)}));
        mock.longCall((long) 5);
        Moxie.verify(mock);
    }

    @Test
    public void or_longObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.or(new long[]{Moxie.gt((long) 4), Moxie.lt((long) 2)}));
        mock.longObjectCall((long) 5);
        Moxie.verify(mock);
    }

    @Test
    public void not_longCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.not(Moxie.lt((long) 3)));
        mock.longCall((long) 5);
        Moxie.verify(mock);
    }

    @Test
    public void not_longObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.not(Moxie.lt((long) 3)));
        mock.longObjectCall((long) 5);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_longCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.not(Moxie.lt((long) 3)));
        mock.longCall((long) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_longObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.not(Moxie.lt((long) 3)));
        mock.longObjectCall((long) 1);
    }

    @Test
    public void longThat_longCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.longThat(new IsEqual((long) 2)));
        mock.longCall((long) 2);
        Moxie.verify(mock);
    }

    @Test
    public void longThat_longObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.longThat(new IsEqual((long) 2)));
        mock.longObjectCall((long) 2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void longThat_longCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longCall(Moxie.longThat(new IsEqual((long) 2)));
        mock.longCall((long) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void longThat_longObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().longObjectCall(Moxie.longThat(new IsEqual((long) 2)));
        mock.longObjectCall((long) 1);
    }

    @Test
    public void aryEq_arrayLongCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongCall(Moxie.aryEq(new long[]{(long) 1, (long) 2, (long) 3}));
        mock.arrayLongCall(new long[]{(long) 1, (long) 2, (long) 3});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsLongCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongCall(Moxie.aryEq(new long[]{(long) 1, (long) 2, (long) 3}));
        mock.varargsLongCall((long) 1, (long) 2, (long) 3);
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_arrayLongObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongObjectCall(Moxie.aryEq(new Long[]{(long) 1, (long) 2, (long) 3}));
        mock.arrayLongObjectCall(new Long[]{(long) 1, (long) 2, (long) 3});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsLongObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongObjectCall(Moxie.aryEq(new Long[]{(long) 1, (long) 2, (long) 3}));
        mock.varargsLongObjectCall((long) 1, (long) 2, (long) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayLongCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongCall(Moxie.aryEq(new long[]{(long) 1, (long) 2, (long) 3}));
        mock.arrayLongCall(new long[]{(long) 1, (long) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsLongCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongCall(Moxie.aryEq(new long[]{(long) 1, (long) 2, (long) 3}));
        mock.varargsLongCall((long) 1, (long) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayLongObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongObjectCall(Moxie.aryEq(new Long[]{(long) 1, (long) 2, (long) 3}));
        mock.arrayLongObjectCall(new Long[]{(long) 1, (long) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsLongObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongObjectCall(Moxie.aryEq(new Long[]{(long) 1, (long) 2, (long) 3}));
        mock.varargsLongObjectCall((long) 1, (long) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayLongCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongCall(Moxie.aryEq(new long[]{(long) 1, (long) 2, (long) 3}));
        mock.arrayLongCall(new long[]{(long) 1, (long) 2, (long) 3, (long) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsLongCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongCall(Moxie.aryEq(new long[]{(long) 1, (long) 2, (long) 3}));
        mock.varargsLongCall((long) 1, (long) 2, (long) 3, (long) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayLongObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongObjectCall(Moxie.aryEq(new Long[]{(long) 1, (long) 2, (long) 3}));
        mock.arrayLongObjectCall(new Long[]{(long) 1, (long) 2, (long) 3, (long) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsLongObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongObjectCall(Moxie.aryEq(new Long[]{(long) 1, (long) 2, (long) 3}));
        mock.varargsLongObjectCall((long) 1, (long) 2, (long) 3, (long) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayLongCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongCall(Moxie.aryEq(new long[]{(long) 1, (long) 2, (long) 3}));
        mock.arrayLongCall(new long[]{(long) 1, (long) 2, (long) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsLongCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongCall(Moxie.aryEq(new long[]{(long) 1, (long) 2, (long) 3}));
        mock.varargsLongCall((long) 1, (long) 2, (long) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayLongObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongObjectCall(Moxie.aryEq(new Long[]{(long) 1, (long) 2, (long) 3}));
        mock.arrayLongObjectCall(new Long[]{(long) 1, (long) 2, (long) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsLongObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongObjectCall(Moxie.aryEq(new Long[]{(long) 1, (long) 2, (long) 3}));
        mock.varargsLongObjectCall((long) 1, (long) 2, (long) 4);
    }

    @Test
    public void array_arrayLongCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongCall(Moxie.array(new long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.arrayLongCall(new long[]{(long) 1, (long) 2, (long) 3});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsLongCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongCall(Moxie.array(new long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.varargsLongCall((long) 1, (long) 2, (long) 3);
        Moxie.verify(mock);
    }

    @Test
    public void array_arrayLongObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongObjectCall(Moxie.array(new Long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.arrayLongObjectCall(new Long[]{(long) 1, (long) 2, (long) 3});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsLongObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongObjectCall(Moxie.array(new Long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.varargsLongObjectCall((long) 1, (long) 2, (long) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayLongCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongCall(Moxie.array(new long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.arrayLongCall(new long[]{(long) 1, (long) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsLongCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongCall(Moxie.array(new long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.varargsLongCall((long) 1, (long) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayLongObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongObjectCall(Moxie.array(new Long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.arrayLongObjectCall(new Long[]{(long) 1, (long) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsLongObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongObjectCall(Moxie.array(new Long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.varargsLongObjectCall((long) 1, (long) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayLongCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongCall(Moxie.array(new long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.arrayLongCall(new long[]{(long) 1, (long) 2, (long) 3, (long) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsLongCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongCall(Moxie.array(new long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.varargsLongCall((long) 1, (long) 2, (long) 3, (long) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayLongObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongObjectCall(Moxie.array(new Long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.arrayLongObjectCall(new Long[]{(long) 1, (long) 2, (long) 3, (long) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsLongObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongObjectCall(Moxie.array(new Long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.varargsLongObjectCall((long) 1, (long) 2, (long) 3, (long) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayLongCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongCall(Moxie.array(new long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.arrayLongCall(new long[]{(long) 1, (long) 2, (long) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsLongCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongCall(Moxie.array(new long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.varargsLongCall((long) 1, (long) 2, (long) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayLongObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongObjectCall(Moxie.array(new Long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.arrayLongObjectCall(new Long[]{(long) 1, (long) 2, (long) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsLongObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongObjectCall(Moxie.array(new Long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.varargsLongObjectCall((long) 1, (long) 2, (long) 4);
    }

    @Test
    public void array_arrayLongCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongCall(Moxie.array(new long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.arrayLongCall(new long[]{(long) 0, (long) 1, (long) 2});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsLongCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongCall(Moxie.array(new long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.varargsLongCall((long) 0, (long) 1, (long) 2);
        Moxie.verify(mock);
    }

    @Test
    public void array_arrayLongObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayLongObjectCall(Moxie.array(new Long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.arrayLongObjectCall(new Long[]{(long) 0, (long) 1, (long) 2});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsLongObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsLongObjectCall(Moxie.array(new Long[]{Moxie.lt((long) 2), Moxie.lt((long) 3), Moxie.lt((long) 4)}));
        mock.varargsLongObjectCall((long) 0, (long) 1, (long) 2);
        Moxie.verify(mock);
    }

    @Test
    public void leq_floatCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.leq((float) 2));
        mock.floatCall((float) 1);
        Moxie.verify(mock);
    }

    @Test
    public void leq_floatObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.leq((float) 2));
        mock.floatObjectCall((float) 1);
        Moxie.verify(mock);
    }

    @Test
    public void leq_floatCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.leq((float) 2));
        mock.floatCall((float) 2);
        Moxie.verify(mock);
    }

    @Test
    public void leq_floatObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.leq((float) 2));
        mock.floatObjectCall((float) 2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_floatCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.leq((float) 2));
        mock.floatCall((float) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_floatObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.leq((float) 2));
        mock.floatObjectCall((float) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_floatObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.leq((float) 2));
        mock.floatObjectCall(null);
    }

    @Test
    public void lt_floatCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.lt((float) 2));
        mock.floatCall((float) 1);
        Moxie.verify(mock);
    }

    @Test
    public void lt_floatObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.lt((float) 2));
        mock.floatObjectCall((float) 1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_floatCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.lt((float) 2));
        mock.floatCall((float) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_floatObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.lt((float) 2));
        mock.floatObjectCall((float) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_floatCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.lt((float) 2));
        mock.floatCall((float) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_floatObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.lt((float) 2));
        mock.floatObjectCall((float) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_floatObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.lt((float) 2));
        mock.floatObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_floatCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.geq((float) 2));
        mock.floatCall((float) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_floatObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.geq((float) 2));
        mock.floatObjectCall((float) 1);
    }

    @Test
    public void geq_floatCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.geq((float) 2));
        mock.floatCall((float) 2);
        Moxie.verify(mock);
    }

    @Test
    public void geq_floatObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.geq((float) 2));
        mock.floatObjectCall((float) 2);
        Moxie.verify(mock);
    }

    @Test
    public void geq_floatCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.geq((float) 2));
        mock.floatCall((float) 3);
        Moxie.verify(mock);
    }

    @Test
    public void geq_floatObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.geq((float) 2));
        mock.floatObjectCall((float) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_floatObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.geq((float) 2));
        mock.floatObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_floatCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.gt((float) 2));
        mock.floatCall((float) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_floatObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.gt((float) 2));
        mock.floatObjectCall((float) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_floatCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.gt((float) 2));
        mock.floatCall((float) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_floatObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.gt((float) 2));
        mock.floatObjectCall((float) 2);
    }

    @Test
    public void gt_floatCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.gt((float) 2));
        mock.floatCall((float) 3);
        Moxie.verify(mock);
    }

    @Test
    public void gt_floatObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.gt((float) 2));
        mock.floatObjectCall((float) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_floatObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.gt((float) 2));
        mock.floatObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_floatCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.and(new float[]{Moxie.lt((float) 4), Moxie.gt((float) 2)}));
        mock.floatCall((float) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_floatObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.and(new float[]{Moxie.lt((float) 4), Moxie.gt((float) 2)}));
        mock.floatObjectCall((float) 1);
    }

    @Test
    public void and_floatCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.and(new float[]{Moxie.lt((float) 4), Moxie.gt((float) 2)}));
        mock.floatCall((float) 3);
        Moxie.verify(mock);
    }

    @Test
    public void and_floatObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.and(new float[]{Moxie.lt((float) 4), Moxie.gt((float) 2)}));
        mock.floatObjectCall((float) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_floatCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.and(new float[]{Moxie.lt((float) 4), Moxie.gt((float) 2)}));
        mock.floatCall((float) 5);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_floatObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.and(new float[]{Moxie.lt((float) 4), Moxie.gt((float) 2)}));
        mock.floatObjectCall((float) 5);
    }

    @Test
    public void or_floatCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.or(new float[]{Moxie.gt((float) 4), Moxie.lt((float) 2)}));
        mock.floatCall((float) 1);
        Moxie.verify(mock);
    }

    @Test
    public void or_floatObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.or(new float[]{Moxie.gt((float) 4), Moxie.lt((float) 2)}));
        mock.floatObjectCall((float) 1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void or_floatCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.or(new float[]{Moxie.gt((float) 4), Moxie.lt((float) 2)}));
        mock.floatCall((float) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void or_floatObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.or(new float[]{Moxie.gt((float) 4), Moxie.lt((float) 2)}));
        mock.floatObjectCall((float) 3);
    }

    @Test
    public void or_floatCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.or(new float[]{Moxie.gt((float) 4), Moxie.lt((float) 2)}));
        mock.floatCall((float) 5);
        Moxie.verify(mock);
    }

    @Test
    public void or_floatObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.or(new float[]{Moxie.gt((float) 4), Moxie.lt((float) 2)}));
        mock.floatObjectCall((float) 5);
        Moxie.verify(mock);
    }

    @Test
    public void not_floatCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.not(Moxie.lt((float) 3)));
        mock.floatCall((float) 5);
        Moxie.verify(mock);
    }

    @Test
    public void not_floatObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.not(Moxie.lt((float) 3)));
        mock.floatObjectCall((float) 5);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_floatCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.not(Moxie.lt((float) 3)));
        mock.floatCall((float) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_floatObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.not(Moxie.lt((float) 3)));
        mock.floatObjectCall((float) 1);
    }

    @Test
    public void floatThat_floatCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.floatThat(new IsEqual((float) 2)));
        mock.floatCall((float) 2);
        Moxie.verify(mock);
    }

    @Test
    public void floatThat_floatObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.floatThat(new IsEqual((float) 2)));
        mock.floatObjectCall((float) 2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void floatThat_floatCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.floatThat(new IsEqual((float) 2)));
        mock.floatCall((float) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void floatThat_floatObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.floatThat(new IsEqual((float) 2)));
        mock.floatObjectCall((float) 1);
    }

    @Test
    public void aryEq_arrayFloatCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatCall(Moxie.aryEq(new float[]{(float) 1, (float) 2, (float) 3}));
        mock.arrayFloatCall(new float[]{(float) 1, (float) 2, (float) 3});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsFloatCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatCall(Moxie.aryEq(new float[]{(float) 1, (float) 2, (float) 3}));
        mock.varargsFloatCall((float) 1, (float) 2, (float) 3);
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_arrayFloatObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatObjectCall(Moxie.aryEq(new Float[]{(float) 1, (float) 2, (float) 3}));
        mock.arrayFloatObjectCall(new Float[]{(float) 1, (float) 2, (float) 3});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsFloatObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatObjectCall(Moxie.aryEq(new Float[]{(float) 1, (float) 2, (float) 3}));
        mock.varargsFloatObjectCall((float) 1, (float) 2, (float) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayFloatCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatCall(Moxie.aryEq(new float[]{(float) 1, (float) 2, (float) 3}));
        mock.arrayFloatCall(new float[]{(float) 1, (float) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsFloatCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatCall(Moxie.aryEq(new float[]{(float) 1, (float) 2, (float) 3}));
        mock.varargsFloatCall((float) 1, (float) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayFloatObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatObjectCall(Moxie.aryEq(new Float[]{(float) 1, (float) 2, (float) 3}));
        mock.arrayFloatObjectCall(new Float[]{(float) 1, (float) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsFloatObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatObjectCall(Moxie.aryEq(new Float[]{(float) 1, (float) 2, (float) 3}));
        mock.varargsFloatObjectCall((float) 1, (float) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayFloatCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatCall(Moxie.aryEq(new float[]{(float) 1, (float) 2, (float) 3}));
        mock.arrayFloatCall(new float[]{(float) 1, (float) 2, (float) 3, (float) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsFloatCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatCall(Moxie.aryEq(new float[]{(float) 1, (float) 2, (float) 3}));
        mock.varargsFloatCall((float) 1, (float) 2, (float) 3, (float) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayFloatObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatObjectCall(Moxie.aryEq(new Float[]{(float) 1, (float) 2, (float) 3}));
        mock.arrayFloatObjectCall(new Float[]{(float) 1, (float) 2, (float) 3, (float) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsFloatObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatObjectCall(Moxie.aryEq(new Float[]{(float) 1, (float) 2, (float) 3}));
        mock.varargsFloatObjectCall((float) 1, (float) 2, (float) 3, (float) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayFloatCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatCall(Moxie.aryEq(new float[]{(float) 1, (float) 2, (float) 3}));
        mock.arrayFloatCall(new float[]{(float) 1, (float) 2, (float) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsFloatCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatCall(Moxie.aryEq(new float[]{(float) 1, (float) 2, (float) 3}));
        mock.varargsFloatCall((float) 1, (float) 2, (float) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayFloatObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatObjectCall(Moxie.aryEq(new Float[]{(float) 1, (float) 2, (float) 3}));
        mock.arrayFloatObjectCall(new Float[]{(float) 1, (float) 2, (float) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsFloatObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatObjectCall(Moxie.aryEq(new Float[]{(float) 1, (float) 2, (float) 3}));
        mock.varargsFloatObjectCall((float) 1, (float) 2, (float) 4);
    }

    @Test
    public void array_arrayFloatCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatCall(Moxie.array(new float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.arrayFloatCall(new float[]{(float) 1, (float) 2, (float) 3});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsFloatCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatCall(Moxie.array(new float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.varargsFloatCall((float) 1, (float) 2, (float) 3);
        Moxie.verify(mock);
    }

    @Test
    public void array_arrayFloatObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatObjectCall(Moxie.array(new Float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.arrayFloatObjectCall(new Float[]{(float) 1, (float) 2, (float) 3});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsFloatObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatObjectCall(Moxie.array(new Float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.varargsFloatObjectCall((float) 1, (float) 2, (float) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayFloatCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatCall(Moxie.array(new float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.arrayFloatCall(new float[]{(float) 1, (float) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsFloatCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatCall(Moxie.array(new float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.varargsFloatCall((float) 1, (float) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayFloatObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatObjectCall(Moxie.array(new Float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.arrayFloatObjectCall(new Float[]{(float) 1, (float) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsFloatObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatObjectCall(Moxie.array(new Float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.varargsFloatObjectCall((float) 1, (float) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayFloatCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatCall(Moxie.array(new float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.arrayFloatCall(new float[]{(float) 1, (float) 2, (float) 3, (float) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsFloatCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatCall(Moxie.array(new float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.varargsFloatCall((float) 1, (float) 2, (float) 3, (float) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayFloatObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatObjectCall(Moxie.array(new Float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.arrayFloatObjectCall(new Float[]{(float) 1, (float) 2, (float) 3, (float) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsFloatObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatObjectCall(Moxie.array(new Float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.varargsFloatObjectCall((float) 1, (float) 2, (float) 3, (float) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayFloatCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatCall(Moxie.array(new float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.arrayFloatCall(new float[]{(float) 1, (float) 2, (float) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsFloatCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatCall(Moxie.array(new float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.varargsFloatCall((float) 1, (float) 2, (float) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayFloatObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatObjectCall(Moxie.array(new Float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.arrayFloatObjectCall(new Float[]{(float) 1, (float) 2, (float) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsFloatObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatObjectCall(Moxie.array(new Float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.varargsFloatObjectCall((float) 1, (float) 2, (float) 4);
    }

    @Test
    public void array_arrayFloatCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatCall(Moxie.array(new float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.arrayFloatCall(new float[]{(float) 0, (float) 1, (float) 2});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsFloatCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatCall(Moxie.array(new float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.varargsFloatCall((float) 0, (float) 1, (float) 2);
        Moxie.verify(mock);
    }

    @Test
    public void array_arrayFloatObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayFloatObjectCall(Moxie.array(new Float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.arrayFloatObjectCall(new Float[]{(float) 0, (float) 1, (float) 2});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsFloatObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsFloatObjectCall(Moxie.array(new Float[]{Moxie.lt((float) 2), Moxie.lt((float) 3), Moxie.lt((float) 4)}));
        mock.varargsFloatObjectCall((float) 0, (float) 1, (float) 2);
        Moxie.verify(mock);
    }

    @Test
    public void leq_doubleCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.leq((double) 2));
        mock.doubleCall((double) 1);
        Moxie.verify(mock);
    }

    @Test
    public void leq_doubleObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.leq((double) 2));
        mock.doubleObjectCall((double) 1);
        Moxie.verify(mock);
    }

    @Test
    public void leq_doubleCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.leq((double) 2));
        mock.doubleCall((double) 2);
        Moxie.verify(mock);
    }

    @Test
    public void leq_doubleObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.leq((double) 2));
        mock.doubleObjectCall((double) 2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_doubleCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.leq((double) 2));
        mock.doubleCall((double) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_doubleObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.leq((double) 2));
        mock.doubleObjectCall((double) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_doubleObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.leq((double) 2));
        mock.doubleObjectCall(null);
    }

    @Test
    public void lt_doubleCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.lt((double) 2));
        mock.doubleCall((double) 1);
        Moxie.verify(mock);
    }

    @Test
    public void lt_doubleObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.lt((double) 2));
        mock.doubleObjectCall((double) 1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_doubleCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.lt((double) 2));
        mock.doubleCall((double) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_doubleObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.lt((double) 2));
        mock.doubleObjectCall((double) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_doubleCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.lt((double) 2));
        mock.doubleCall((double) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_doubleObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.lt((double) 2));
        mock.doubleObjectCall((double) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_doubleObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.lt((double) 2));
        mock.doubleObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_doubleCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.geq((double) 2));
        mock.doubleCall((double) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_doubleObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.geq((double) 2));
        mock.doubleObjectCall((double) 1);
    }

    @Test
    public void geq_doubleCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.geq((double) 2));
        mock.doubleCall((double) 2);
        Moxie.verify(mock);
    }

    @Test
    public void geq_doubleObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.geq((double) 2));
        mock.doubleObjectCall((double) 2);
        Moxie.verify(mock);
    }

    @Test
    public void geq_doubleCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.geq((double) 2));
        mock.doubleCall((double) 3);
        Moxie.verify(mock);
    }

    @Test
    public void geq_doubleObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.geq((double) 2));
        mock.doubleObjectCall((double) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_doubleObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.geq((double) 2));
        mock.doubleObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_doubleCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.gt((double) 2));
        mock.doubleCall((double) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_doubleObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.gt((double) 2));
        mock.doubleObjectCall((double) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_doubleCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.gt((double) 2));
        mock.doubleCall((double) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_doubleObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.gt((double) 2));
        mock.doubleObjectCall((double) 2);
    }

    @Test
    public void gt_doubleCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.gt((double) 2));
        mock.doubleCall((double) 3);
        Moxie.verify(mock);
    }

    @Test
    public void gt_doubleObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.gt((double) 2));
        mock.doubleObjectCall((double) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_doubleObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.gt((double) 2));
        mock.doubleObjectCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_doubleCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.and(new double[]{Moxie.lt((double) 4), Moxie.gt((double) 2)}));
        mock.doubleCall((double) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_doubleObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.and(new double[]{Moxie.lt((double) 4), Moxie.gt((double) 2)}));
        mock.doubleObjectCall((double) 1);
    }

    @Test
    public void and_doubleCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.and(new double[]{Moxie.lt((double) 4), Moxie.gt((double) 2)}));
        mock.doubleCall((double) 3);
        Moxie.verify(mock);
    }

    @Test
    public void and_doubleObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.and(new double[]{Moxie.lt((double) 4), Moxie.gt((double) 2)}));
        mock.doubleObjectCall((double) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_doubleCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.and(new double[]{Moxie.lt((double) 4), Moxie.gt((double) 2)}));
        mock.doubleCall((double) 5);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_doubleObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.and(new double[]{Moxie.lt((double) 4), Moxie.gt((double) 2)}));
        mock.doubleObjectCall((double) 5);
    }

    @Test
    public void or_doubleCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.or(new double[]{Moxie.gt((double) 4), Moxie.lt((double) 2)}));
        mock.doubleCall((double) 1);
        Moxie.verify(mock);
    }

    @Test
    public void or_doubleObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.or(new double[]{Moxie.gt((double) 4), Moxie.lt((double) 2)}));
        mock.doubleObjectCall((double) 1);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void or_doubleCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.or(new double[]{Moxie.gt((double) 4), Moxie.lt((double) 2)}));
        mock.doubleCall((double) 3);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void or_doubleObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.or(new double[]{Moxie.gt((double) 4), Moxie.lt((double) 2)}));
        mock.doubleObjectCall((double) 3);
    }

    @Test
    public void or_doubleCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.or(new double[]{Moxie.gt((double) 4), Moxie.lt((double) 2)}));
        mock.doubleCall((double) 5);
        Moxie.verify(mock);
    }

    @Test
    public void or_doubleObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.or(new double[]{Moxie.gt((double) 4), Moxie.lt((double) 2)}));
        mock.doubleObjectCall((double) 5);
        Moxie.verify(mock);
    }

    @Test
    public void not_doubleCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.not(Moxie.lt((double) 3)));
        mock.doubleCall((double) 5);
        Moxie.verify(mock);
    }

    @Test
    public void not_doubleObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.not(Moxie.lt((double) 3)));
        mock.doubleObjectCall((double) 5);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_doubleCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.not(Moxie.lt((double) 3)));
        mock.doubleCall((double) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_doubleObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.not(Moxie.lt((double) 3)));
        mock.doubleObjectCall((double) 1);
    }

    @Test
    public void doubleThat_doubleCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.doubleThat(new IsEqual((double) 2)));
        mock.doubleCall((double) 2);
        Moxie.verify(mock);
    }

    @Test
    public void doubleThat_doubleObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.doubleThat(new IsEqual((double) 2)));
        mock.doubleObjectCall((double) 2);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void doubleThat_doubleCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.doubleThat(new IsEqual((double) 2)));
        mock.doubleCall((double) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void doubleThat_doubleObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.doubleThat(new IsEqual((double) 2)));
        mock.doubleObjectCall((double) 1);
    }

    @Test
    public void aryEq_arrayDoubleCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleCall(Moxie.aryEq(new double[]{(double) 1, (double) 2, (double) 3}));
        mock.arrayDoubleCall(new double[]{(double) 1, (double) 2, (double) 3});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsDoubleCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleCall(Moxie.aryEq(new double[]{(double) 1, (double) 2, (double) 3}));
        mock.varargsDoubleCall((double) 1, (double) 2, (double) 3);
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_arrayDoubleObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleObjectCall(Moxie.aryEq(new Double[]{(double) 1, (double) 2, (double) 3}));
        mock.arrayDoubleObjectCall(new Double[]{(double) 1, (double) 2, (double) 3});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsDoubleObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleObjectCall(Moxie.aryEq(new Double[]{(double) 1, (double) 2, (double) 3}));
        mock.varargsDoubleObjectCall((double) 1, (double) 2, (double) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayDoubleCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleCall(Moxie.aryEq(new double[]{(double) 1, (double) 2, (double) 3}));
        mock.arrayDoubleCall(new double[]{(double) 1, (double) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsDoubleCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleCall(Moxie.aryEq(new double[]{(double) 1, (double) 2, (double) 3}));
        mock.varargsDoubleCall((double) 1, (double) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayDoubleObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleObjectCall(Moxie.aryEq(new Double[]{(double) 1, (double) 2, (double) 3}));
        mock.arrayDoubleObjectCall(new Double[]{(double) 1, (double) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsDoubleObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleObjectCall(Moxie.aryEq(new Double[]{(double) 1, (double) 2, (double) 3}));
        mock.varargsDoubleObjectCall((double) 1, (double) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayDoubleCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleCall(Moxie.aryEq(new double[]{(double) 1, (double) 2, (double) 3}));
        mock.arrayDoubleCall(new double[]{(double) 1, (double) 2, (double) 3, (double) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsDoubleCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleCall(Moxie.aryEq(new double[]{(double) 1, (double) 2, (double) 3}));
        mock.varargsDoubleCall((double) 1, (double) 2, (double) 3, (double) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayDoubleObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleObjectCall(Moxie.aryEq(new Double[]{(double) 1, (double) 2, (double) 3}));
        mock.arrayDoubleObjectCall(new Double[]{(double) 1, (double) 2, (double) 3, (double) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsDoubleObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleObjectCall(Moxie.aryEq(new Double[]{(double) 1, (double) 2, (double) 3}));
        mock.varargsDoubleObjectCall((double) 1, (double) 2, (double) 3, (double) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayDoubleCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleCall(Moxie.aryEq(new double[]{(double) 1, (double) 2, (double) 3}));
        mock.arrayDoubleCall(new double[]{(double) 1, (double) 2, (double) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsDoubleCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleCall(Moxie.aryEq(new double[]{(double) 1, (double) 2, (double) 3}));
        mock.varargsDoubleCall((double) 1, (double) 2, (double) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayDoubleObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleObjectCall(Moxie.aryEq(new Double[]{(double) 1, (double) 2, (double) 3}));
        mock.arrayDoubleObjectCall(new Double[]{(double) 1, (double) 2, (double) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsDoubleObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleObjectCall(Moxie.aryEq(new Double[]{(double) 1, (double) 2, (double) 3}));
        mock.varargsDoubleObjectCall((double) 1, (double) 2, (double) 4);
    }

    @Test
    public void array_arrayDoubleCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleCall(Moxie.array(new double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.arrayDoubleCall(new double[]{(double) 1, (double) 2, (double) 3});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsDoubleCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleCall(Moxie.array(new double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.varargsDoubleCall((double) 1, (double) 2, (double) 3);
        Moxie.verify(mock);
    }

    @Test
    public void array_arrayDoubleObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleObjectCall(Moxie.array(new Double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.arrayDoubleObjectCall(new Double[]{(double) 1, (double) 2, (double) 3});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsDoubleObjectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleObjectCall(Moxie.array(new Double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.varargsDoubleObjectCall((double) 1, (double) 2, (double) 3);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayDoubleCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleCall(Moxie.array(new double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.arrayDoubleCall(new double[]{(double) 1, (double) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsDoubleCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleCall(Moxie.array(new double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.varargsDoubleCall((double) 1, (double) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayDoubleObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleObjectCall(Moxie.array(new Double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.arrayDoubleObjectCall(new Double[]{(double) 1, (double) 2});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsDoubleObjectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleObjectCall(Moxie.array(new Double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.varargsDoubleObjectCall((double) 1, (double) 2);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayDoubleCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleCall(Moxie.array(new double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.arrayDoubleCall(new double[]{(double) 1, (double) 2, (double) 3, (double) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsDoubleCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleCall(Moxie.array(new double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.varargsDoubleCall((double) 1, (double) 2, (double) 3, (double) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayDoubleObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleObjectCall(Moxie.array(new Double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.arrayDoubleObjectCall(new Double[]{(double) 1, (double) 2, (double) 3, (double) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsDoubleObjectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleObjectCall(Moxie.array(new Double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.varargsDoubleObjectCall((double) 1, (double) 2, (double) 3, (double) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayDoubleCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleCall(Moxie.array(new double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.arrayDoubleCall(new double[]{(double) 1, (double) 2, (double) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsDoubleCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleCall(Moxie.array(new double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.varargsDoubleCall((double) 1, (double) 2, (double) 4);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayDoubleObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleObjectCall(Moxie.array(new Double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.arrayDoubleObjectCall(new Double[]{(double) 1, (double) 2, (double) 4});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsDoubleObjectCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleObjectCall(Moxie.array(new Double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.varargsDoubleObjectCall((double) 1, (double) 2, (double) 4);
    }

    @Test
    public void array_arrayDoubleCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleCall(Moxie.array(new double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.arrayDoubleCall(new double[]{(double) 0, (double) 1, (double) 2});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsDoubleCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleCall(Moxie.array(new double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.varargsDoubleCall((double) 0, (double) 1, (double) 2);
        Moxie.verify(mock);
    }

    @Test
    public void array_arrayDoubleObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayDoubleObjectCall(Moxie.array(new Double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.arrayDoubleObjectCall(new Double[]{(double) 0, (double) 1, (double) 2});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsDoubleObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsDoubleObjectCall(Moxie.array(new Double[]{Moxie.lt((double) 2), Moxie.lt((double) 3), Moxie.lt((double) 4)}));
        mock.varargsDoubleObjectCall((double) 0, (double) 1, (double) 2);
        Moxie.verify(mock);
    }

    @Test
    public void leq_stringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.leq("2"));
        mock.stringCall("1");
        Moxie.verify(mock);
    }

    @Test
    public void leq_stringCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.leq("2"));
        mock.stringCall("2");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_stringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.leq("2"));
        mock.stringCall("3");
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void leq_stringCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.leq("2"));
        mock.stringCall(null);
    }

    @Test
    public void lt_stringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.lt("2"));
        mock.stringCall("1");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_stringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.lt("2"));
        mock.stringCall("2");
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_stringCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.lt("2"));
        mock.stringCall("3");
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void lt_stringCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.lt("2"));
        mock.stringCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_stringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.geq("2"));
        mock.stringCall("1");
    }

    @Test
    public void geq_stringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.geq("2"));
        mock.stringCall("2");
        Moxie.verify(mock);
    }

    @Test
    public void geq_stringCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.geq("2"));
        mock.stringCall("3");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void geq_stringCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.geq("2"));
        mock.stringCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_stringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.gt("2"));
        mock.stringCall("1");
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_stringCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.gt("2"));
        mock.stringCall("2");
    }

    @Test
    public void gt_stringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.gt("2"));
        mock.stringCall("3");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void gt_stringCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.gt("2"));
        mock.stringCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_stringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.and(new String[]{Moxie.lt("4"), Moxie.gt("2")}));
        mock.stringCall("1");
    }

    @Test
    public void and_stringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.and(new String[]{Moxie.lt("4"), Moxie.gt("2")}));
        mock.stringCall("3");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void and_stringCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.and(new String[]{Moxie.lt("4"), Moxie.gt("2")}));
        mock.stringCall("5");
    }

    @Test
    public void or_stringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.or(new String[]{Moxie.gt("4"), Moxie.lt("2")}));
        mock.stringCall("1");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void or_stringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.or(new String[]{Moxie.gt("4"), Moxie.lt("2")}));
        mock.stringCall("3");
    }

    @Test
    public void or_stringCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.or(new String[]{Moxie.gt("4"), Moxie.lt("2")}));
        mock.stringCall("5");
        Moxie.verify(mock);
    }

    @Test
    public void not_stringCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.not(Moxie.lt("3")));
        mock.stringCall("5");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void not_stringCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.not(Moxie.lt("3")));
        mock.stringCall("1");
    }

    @Test
    public void argThat_stringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.<String>argThat(new IsEqual("2")));
        mock.stringCall("2");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void argThat_stringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.<String>argThat(new IsEqual("2")));
        mock.stringCall("1");
    }

    @Test
    public void aryEq_arrayStringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayStringCall(Moxie.aryEq(new String[]{"1", "2", "3"}));
        mock.arrayStringCall(new String[]{"1", "2", "3"});
        Moxie.verify(mock);
    }

    @Test
    public void aryEq_varargsStringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsStringCall(Moxie.aryEq(new String[]{"1", "2", "3"}));
        mock.varargsStringCall("1", "2", "3");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayStringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayStringCall(Moxie.aryEq(new String[]{"1", "2", "3"}));
        mock.arrayStringCall(new String[]{"1", "2"});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsStringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsStringCall(Moxie.aryEq(new String[]{"1", "2", "3"}));
        mock.varargsStringCall("1", "2");
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayStringCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayStringCall(Moxie.aryEq(new String[]{"1", "2", "3"}));
        mock.arrayStringCall(new String[]{"1", "2", "3", "4"});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsStringCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsStringCall(Moxie.aryEq(new String[]{"1", "2", "3"}));
        mock.varargsStringCall("1", "2", "3", "4");
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayStringCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayStringCall(Moxie.aryEq(new String[]{"1", "2", "3"}));
        mock.arrayStringCall(new String[]{"1", "2", "4"});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsStringCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsStringCall(Moxie.aryEq(new String[]{"1", "2", "3"}));
        mock.varargsStringCall("1", "2", "4");
    }

    @Test
    public void array_arrayStringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayStringCall(Moxie.array(new String[]{Moxie.lt("2"), Moxie.lt("3"), Moxie.lt("4")}));
        mock.arrayStringCall(new String[]{"1", "2", "3"});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsStringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsStringCall(Moxie.array(new String[]{Moxie.lt("2"), Moxie.lt("3"), Moxie.lt("4")}));
        mock.varargsStringCall("1", "2", "3");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayStringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayStringCall(Moxie.array(new String[]{Moxie.lt("2"), Moxie.lt("3"), Moxie.lt("4")}));
        mock.arrayStringCall(new String[]{"1", "2"});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsStringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsStringCall(Moxie.array(new String[]{Moxie.lt("2"), Moxie.lt("3"), Moxie.lt("4")}));
        mock.varargsStringCall("1", "2");
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayStringCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayStringCall(Moxie.array(new String[]{Moxie.lt("2"), Moxie.lt("3"), Moxie.lt("4")}));
        mock.arrayStringCall(new String[]{"1", "2", "3", "4"});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsStringCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsStringCall(Moxie.array(new String[]{Moxie.lt("2"), Moxie.lt("3"), Moxie.lt("4")}));
        mock.varargsStringCall("1", "2", "3", "4");
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayStringCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayStringCall(Moxie.array(new String[]{Moxie.lt("2"), Moxie.lt("3"), Moxie.lt("4")}));
        mock.arrayStringCall(new String[]{"1", "2", "4"});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsStringCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsStringCall(Moxie.array(new String[]{Moxie.lt("2"), Moxie.lt("3"), Moxie.lt("4")}));
        mock.varargsStringCall("1", "2", "4");
    }

    @Test
    public void array_arrayStringCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayStringCall(Moxie.array(new String[]{Moxie.lt("2"), Moxie.lt("3"), Moxie.lt("4")}));
        mock.arrayStringCall(new String[]{"0", "1", "2"});
        Moxie.verify(mock);
    }

    @Test
    public void array_varargsStringCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsStringCall(Moxie.array(new String[]{Moxie.lt("2"), Moxie.lt("3"), Moxie.lt("4")}));
        mock.varargsStringCall("0", "1", "2");
        Moxie.verify(mock);
    }

    @Test
    public void isNull_objectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.isNull());
        mock.objectCall(null);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void isNull_objectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.isNull());
        mock.objectCall(true);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void notNull_objectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.notNull());
        mock.objectCall(null);
    }

    @Test
    public void notNull_objectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.notNull());
        mock.objectCall(true);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void isNotNull_objectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.isNotNull());
        mock.objectCall(null);
    }

    @Test
    public void isNotNull_objectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.isNotNull());
        mock.objectCall(true);
        Moxie.verify(mock);
    }

    @Test
    public void hasSubstring_stringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.hasSubstring("thor"));
        mock.stringCall("Scunthorpe");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void hasSubstring_stringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.hasSubstring("thor"));
        mock.stringCall("Wodin");
    }

    @Test
    public void endsWith_stringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.endsWith("ton"));
        mock.stringCall("weighs a ton");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void endsWith_stringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.endsWith("ton"));
        mock.stringCall("weighs a thousand kilos");
    }

    @Test
    public void startsWith_stringCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.startsWith("Fred"));
        mock.stringCall("Fred Flintstone");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void startsWith_stringCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().stringCall(Moxie.startsWith("Fred"));
        mock.stringCall("Andrew Flintoff");
    }

    @Test
    public void anyArray_objectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.anyArray());
        mock.objectCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void anyArray_objectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.anyArray());
        mock.objectCall(new Object[]{"foo","bar"});
        Moxie.verify(mock);
    }

    @Test
    public void anyArray_objectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.anyArray());
        mock.objectCall(new int[]{1,2,3});
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void anyArray_objectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.anyArray());
        mock.objectCall("blah");
    }

    @Test
    public void any_objectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.any(String.class));
        mock.objectCall(null);
        Moxie.verify(mock);
    }

    @Test
    public void any_objectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.any(String.class));
        mock.objectCall("blah");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void any_objectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.any(String.class));
        mock.objectCall(Collections.singletonList("1"));
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void isA_objectCall_sadPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.isA(String.class));
        mock.objectCall(null);
    }

    @Test
    public void isA_objectCall_happyPath1() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.isA(String.class));
        mock.objectCall("blah");
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void isA_objectCall_sadPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().objectCall(Moxie.isA(String.class));
        mock.objectCall(Collections.singletonList("1"));
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_floatCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.eq((float) 4.0, (float) 2.0));
        mock.floatCall((float) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_floatObjectCall_sadPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.eq((float) 4.0, (float) 2.0));
        mock.floatObjectCall((float) 1);
    }

    @Test
    public void eq_floatCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.eq((float) 4.0, (float) 2.0));
        mock.floatCall((float) 3);
        Moxie.verify(mock);
    }

    @Test
    public void eq_floatObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.eq((float) 4.0, (float) 2.0));
        mock.floatObjectCall((float) 3);
        Moxie.verify(mock);
    }

    @Test
    public void eq_floatCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.eq((float) 4.0, (float) 2.0));
        mock.floatCall((float) 4);
        Moxie.verify(mock);
    }

    @Test
    public void eq_floatObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.eq((float) 4.0, (float) 2.0));
        mock.floatObjectCall((float) 4);
        Moxie.verify(mock);
    }

    @Test
    public void eq_floatCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.eq((float) 4.0, (float) 2.0));
        mock.floatCall((float) 5);
        Moxie.verify(mock);
    }

    @Test
    public void eq_floatObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.eq((float) 4.0, (float) 2.0));
        mock.floatObjectCall((float) 5);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_floatCall_sadPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatCall(Moxie.eq((float) 4.0, (float) 2.0));
        mock.floatCall((float) 7);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_floatObjectCall_sadPath5() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().floatObjectCall(Moxie.eq((float) 4.0, (float) 2.0));
        mock.floatObjectCall((float) 7);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_doubleCall_sadPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.eq((double) 4.0, (double) 2.0));
        mock.doubleCall((double) 1);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_doubleObjectCall_sadPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.eq((double) 4.0, (double) 2.0));
        mock.doubleObjectCall((double) 1);
    }

    @Test
    public void eq_doubleCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.eq((double) 4.0, (double) 2.0));
        mock.doubleCall((double) 3);
        Moxie.verify(mock);
    }

    @Test
    public void eq_doubleObjectCall_happyPath2() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.eq((double) 4.0, (double) 2.0));
        mock.doubleObjectCall((double) 3);
        Moxie.verify(mock);
    }

    @Test
    public void eq_doubleCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.eq((double) 4.0, (double) 2.0));
        mock.doubleCall((double) 4);
        Moxie.verify(mock);
    }

    @Test
    public void eq_doubleObjectCall_happyPath3() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.eq((double) 4.0, (double) 2.0));
        mock.doubleObjectCall((double) 4);
        Moxie.verify(mock);
    }

    @Test
    public void eq_doubleCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.eq((double) 4.0, (double) 2.0));
        mock.doubleCall((double) 5);
        Moxie.verify(mock);
    }

    @Test
    public void eq_doubleObjectCall_happyPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.eq((double) 4.0, (double) 2.0));
        mock.doubleObjectCall((double) 5);
        Moxie.verify(mock);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_doubleCall_sadPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleCall(Moxie.eq((double) 4.0, (double) 2.0));
        mock.doubleCall((double) 7);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void eq_doubleObjectCall_sadPath5() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().doubleObjectCall(Moxie.eq((double) 4.0, (double) 2.0));
        mock.doubleObjectCall((double) 7);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_arrayStringCall_sadPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayStringCall(Moxie.aryEq(new String[]{"1"}));
        mock.arrayStringCall(new String[]{null});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void aryEq_varargsStringCall_sadPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsStringCall(Moxie.aryEq(new String[]{"1"}));
        mock.varargsStringCall(null);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_arrayStringCall_sadPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().arrayStringCall(Moxie.array(new String[]{Moxie.eq("1")}));
        mock.arrayStringCall(new String[]{null});
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    public void array_varargsStringCall_sadPath4() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).will().varargsStringCall(Moxie.array(new String[]{Moxie.eq("1")}));
        mock.varargsStringCall(null);
    }


}
