package com.google.common.flogger.util;

import com.google.errorprone.annotations.CheckReturnValue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@CheckReturnValue
final class FastStackGetter {
   private final Object javaLangAccess;
   private final Method getElementMethod;
   private final Method getDepthMethod;

   @NullableDecl
   public static FastStackGetter createIfSupported() {
      try {
         Object javaLangAccess = Class.forName("sun.misc.SharedSecrets").getMethod("getJavaLangAccess").invoke(null);
         Method getElementMethod = Class.forName("sun.misc.JavaLangAccess").getMethod("getStackTraceElement", Throwable.class, int.class);
         Method getDepthMethod = Class.forName("sun.misc.JavaLangAccess").getMethod("getStackTraceDepth", Throwable.class);
         StackTraceElement unusedElement = (StackTraceElement)getElementMethod.invoke(javaLangAccess, new Throwable(), 0);
         int unusedDepth = (Integer)getDepthMethod.invoke(javaLangAccess, new Throwable());
         return new FastStackGetter(javaLangAccess, getElementMethod, getDepthMethod);
      } catch (ThreadDeath var5) {
         throw var5;
      } catch (Throwable var6) {
         return null;
      }
   }

   private FastStackGetter(Object javaLangAccess, Method getElementMethod, Method getDepthMethod) {
      this.javaLangAccess = javaLangAccess;
      this.getElementMethod = getElementMethod;
      this.getDepthMethod = getDepthMethod;
   }

   public StackTraceElement getStackTraceElement(Throwable throwable, int n) {
      try {
         return (StackTraceElement)this.getElementMethod.invoke(this.javaLangAccess, throwable, n);
      } catch (InvocationTargetException var4) {
         if (var4.getCause() instanceof RuntimeException) {
            throw (RuntimeException)var4.getCause();
         } else if (var4.getCause() instanceof Error) {
            throw (Error)var4.getCause();
         } else {
            throw new RuntimeException(var4.getCause());
         }
      } catch (IllegalAccessException var5) {
         throw new AssertionError(var5);
      }
   }

   public int getStackTraceDepth(Throwable throwable) {
      try {
         return (Integer)this.getDepthMethod.invoke(this.javaLangAccess, throwable);
      } catch (InvocationTargetException var3) {
         if (var3.getCause() instanceof RuntimeException) {
            throw (RuntimeException)var3.getCause();
         } else if (var3.getCause() instanceof Error) {
            throw (Error)var3.getCause();
         } else {
            throw new RuntimeException(var3.getCause());
         }
      } catch (IllegalAccessException var4) {
         throw new AssertionError(var4);
      }
   }
}
