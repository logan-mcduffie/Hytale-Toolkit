package org.bouncycastle.util.test;

import org.bouncycastle.util.Strings;

public class SimpleTestResult implements TestResult {
   private static final String SEPARATOR = Strings.lineSeparator();
   private boolean success;
   private String message;
   private Throwable exception;

   public SimpleTestResult(boolean var1, String var2) {
      this.success = var1;
      this.message = var2;
   }

   public SimpleTestResult(boolean var1, String var2, Throwable var3) {
      this.success = var1;
      this.message = var2;
      this.exception = var3;
   }

   public static TestResult successful(Test var0, String var1) {
      return new SimpleTestResult(true, var0.getName() + ": " + var1);
   }

   public static TestResult failed(Test var0, String var1) {
      return new SimpleTestResult(false, var0.getName() + ": " + var1);
   }

   public static TestResult failed(Test var0, String var1, Throwable var2) {
      return new SimpleTestResult(false, var0.getName() + ": " + var1, var2);
   }

   public static TestResult failed(Test var0, String var1, Object var2, Object var3) {
      return failed(var0, var1 + SEPARATOR + "Expected: " + var2 + SEPARATOR + "Found   : " + var3);
   }

   public static String failedMessage(String var0, String var1, String var2, String var3) {
      StringBuilder var4 = new StringBuilder(var0);
      var4.append(" failing ").append(var1);
      var4.append(SEPARATOR).append("    expected: ").append(var2);
      var4.append(SEPARATOR).append("    got     : ").append(var3);
      return var4.toString();
   }

   @Override
   public boolean isSuccessful() {
      return this.success;
   }

   @Override
   public String toString() {
      return this.message;
   }

   @Override
   public Throwable getException() {
      return this.exception;
   }
}
