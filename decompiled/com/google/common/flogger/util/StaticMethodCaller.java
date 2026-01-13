package com.google.common.flogger.util;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public final class StaticMethodCaller {
   @NullableDecl
   public static <T> T getInstanceFromSystemProperty(String propertyName, @NullableDecl String defaultClassName, Class<T> type) {
      String className = readProperty(propertyName, defaultClassName);
      return className == null ? null : callStaticMethod(className, "getInstance", type);
   }

   @NullableDecl
   public static <T> T callGetterFromSystemProperty(String propertyName, @NullableDecl String defaultValue, Class<T> type) {
      String getter = readProperty(propertyName, defaultValue);
      if (getter == null) {
         return null;
      } else {
         int idx = getter.indexOf(35);
         if (idx > 0 && idx != getter.length() - 1) {
            return callStaticMethod(getter.substring(0, idx), getter.substring(idx + 1), type);
         } else {
            error("invalid getter (expected <class>#<method>): %s\n", getter);
            return null;
         }
      }
   }

   @NullableDecl
   public static <T> T callGetterFromSystemProperty(String propertyName, Class<T> type) {
      return callGetterFromSystemProperty(propertyName, null, type);
   }

   private static String readProperty(String propertyName, @NullableDecl String defaultValue) {
      Checks.checkNotNull(propertyName, "property name");

      try {
         return System.getProperty(propertyName, defaultValue);
      } catch (SecurityException var3) {
         error("cannot read property name %s: %s", propertyName, var3);
         return null;
      }
   }

   private static <T> T callStaticMethod(String className, String methodName, Class<T> type) {
      try {
         return type.cast(Class.forName(className).getMethod(methodName).invoke(null));
      } catch (ClassNotFoundException var4) {
      } catch (ClassCastException var5) {
         error("cannot cast result of calling '%s#%s' to '%s': %s\n", className, methodName, type.getName(), var5);
      } catch (Exception var6) {
         error("cannot call expected no-argument static method '%s#%s': %s\n", className, methodName, var6);
      }

      return null;
   }

   private static void error(String msg, Object... args) {
      System.err.println(StaticMethodCaller.class + ": " + String.format(msg, args));
   }

   private StaticMethodCaller() {
   }
}
