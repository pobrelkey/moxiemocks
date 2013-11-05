/*
 * Copyright (c) 2013 Moxie contributors
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

package moxie.hamcrest;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import moxie.Predicate;
import net.sf.cglib.proxy.Enhancer;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.io.IOException;

public class LambdaMatcher<T> extends BaseMatcher<T> {
    private final Predicate<T> lambda;

    public LambdaMatcher(Predicate<T> lambda) {
        this.lambda = lambda;
    }

    @SuppressWarnings("unchecked")
    public boolean matches(Object item) {
        return lambda.test((T) item);
    }

    public void describeTo(Description description) {
        description.appendText("a value matching the Predicate ");
        String whereDefined = LineNumberKludge.getWhereLambdaIsDefined(lambda);
        if (whereDefined != null) {
            description.appendText("defined at ");
            description.appendText(whereDefined);
        } else {
            description.appendText(lambda.getClass().getName());
        }
    }

    private static class LineNumberKludge {
        static private boolean haveCglib = false;
        static {
            try {
                new Enhancer();
                haveCglib = true;
            } catch (NoClassDefFoundError e) {
                // oh well, no cglib then.
            }
        }

        static private boolean haveJavassist = false;
        static {
            try {
                new javassist.util.proxy.ProxyFactory();
                haveJavassist = true;
            } catch (NoClassDefFoundError e) {
                // oh well, no javassist then.
            }
        }

        static private String getWhereLambdaIsDefined(Predicate<?> lambda) {
            String result = null;
            if (haveJavassist) {
                result = getWhereLambdaIsDefinedUsingJavassist(lambda);
            } else if (haveCglib) {
                result = getWhereLambdaIsDefinedUsingCGLIB(lambda);
            }
            return result;
        }

        static private String getWhereLambdaIsDefinedUsingJavassist(Predicate<?> lambda) {
            Class<?> lambdaClass = lambda.getClass();
            try {
                ClassPool classPool = ClassPool.getDefault();
                CtClass ctClass = classPool.get(lambdaClass.getName());
                String sourceFile = ctClass.getClassFile2().getSourceFile();
                if (sourceFile == null) {
                    return null;
                }
                CtMethod lambdaCtMethod = ctClass.getMethod("test", Descriptor.ofMethod(CtClass.booleanType, new CtClass[]{classPool.get("java.lang.Object")}));
                MethodInfo methodInfo = lambdaCtMethod.getMethodInfo();
                return sourceFile + ':' + methodInfo.getLineNumber(0);
            } catch (NotFoundException e) {
                return null;
            }
        }


        static private String getWhereLambdaIsDefinedUsingCGLIB(Predicate<?> lambda) {
            try {
                ClassReader classReader = new ClassReader(lambda.getClass().getResourceAsStream(lambda.getClass().getSimpleName() + ".class"));
                LambdaLineNumberVisitor visitor = new LambdaLineNumberVisitor();
                classReader.accept(visitor, ClassReader.SKIP_FRAMES);
                return (visitor.source != null && visitor.lineNumber != null) ? (visitor.source + ':' + visitor.lineNumber) : null;
            } catch (IOException e) {
                return null;
            }
        }

        static private class LambdaLineNumberVisitor implements ClassVisitor {

            private static final String PREDICATE_TEST_DESCRIPTOR = Type.getMethodDescriptor(Type.BOOLEAN_TYPE, new Type[]{Type.getType(Object.class)});

            private String source = null;
            private Integer lineNumber = null;

            public void visit(int i, int i2, String s, String s2, String s3, String[] strings) { }
            public void visitOuterClass(String s, String s2, String s3) { }
            public void visitInnerClass(String s, String s2, String s3, int i) { }
            public void visitEnd() { }
            public void visitAttribute(Attribute attribute) { }

            public AnnotationVisitor visitAnnotation(String s, boolean b) { return null; }
            public FieldVisitor visitField(int i, String s, String s2, String s3, Object o) { return null; }

            public void visitSource(String sourceFileName, String debugInfo) {
                this.source = sourceFileName;
            }

            public MethodVisitor visitMethod(int access, String name, String desc, String signature,  String[] exceptions) {
                if ("test".equals(name) && PREDICATE_TEST_DESCRIPTOR.equals(desc)) {
                    return new MethodVisitor() {
                        public void visitLineNumber(int i, Label label) {
                            if (lineNumber == null) {
                                lineNumber = i;
                            }
                        }
                        public AnnotationVisitor visitAnnotationDefault() { return null; }
                        public AnnotationVisitor visitAnnotation(String s, boolean b) { return null; }
                        public AnnotationVisitor visitParameterAnnotation(int i, String s, boolean b) { return null; }
                        public void visitAttribute(Attribute attribute) { }
                        public void visitCode() { }
                        public void visitFrame(int i, int i2, Object[] objects, int i3, Object[] objects2) { }
                        public void visitInsn(int i) { }
                        public void visitIntInsn(int i, int i2) { }
                        public void visitVarInsn(int i, int i2) { }
                        public void visitTypeInsn(int i, String s) { }
                        public void visitFieldInsn(int i, String s, String s2, String s3) { }
                        public void visitMethodInsn(int i, String s, String s2, String s3) { }
                        public void visitJumpInsn(int i, Label label) { }
                        public void visitLabel(Label label) { }
                        public void visitLdcInsn(Object o) { }
                        public void visitIincInsn(int i, int i2) { }
                        public void visitTableSwitchInsn(int i, int i2, Label label, Label[] labels) { }
                        public void visitLookupSwitchInsn(Label label, int[] ints, Label[] labels) { }
                        public void visitMultiANewArrayInsn(String s, int i) { }
                        public void visitTryCatchBlock(Label label, Label label2, Label label3, String s) { }
                        public void visitLocalVariable(String s, String s2, String s3, Label label, Label label2, int i) { }
                        public void visitMaxs(int i, int i2) { }
                        public void visitEnd() { }
                    };
                } else {
                    return null;
                }
            }

        }

    }

}
