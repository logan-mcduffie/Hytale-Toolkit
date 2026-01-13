package org.bouncycastle.util.test;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.util.Arrays;

public abstract class SimpleTest implements Test {
   @Override
   public abstract String getName();

   private TestResult success() {
      return SimpleTestResult.successful(this, "Okay");
   }

   public void fail(String var1) {
      throw new TestFailedException(SimpleTestResult.failed(this, var1));
   }

   protected void isTrue(boolean var1) {
      if (!var1) {
         throw new TestFailedException(SimpleTestResult.failed(this, "no message"));
      }
   }

   public void isTrue(String var1, boolean var2) {
      if (!var2) {
         throw new TestFailedException(SimpleTestResult.failed(this, var1));
      }
   }

   protected void isEquals(Object var1, Object var2) {
      if (!var1.equals(var2)) {
         throw new TestFailedException(SimpleTestResult.failed(this, "no message"));
      }
   }

   protected void isEquals(int var1, int var2) {
      if (var1 != var2) {
         throw new TestFailedException(SimpleTestResult.failed(this, "no message"));
      }
   }

   protected void isEquals(long var1, long var3) {
      if (var1 != var3) {
         throw new TestFailedException(SimpleTestResult.failed(this, "no message"));
      }
   }

   protected void isEquals(boolean var1, boolean var2) {
      if (var1 != var2) {
         throw new TestFailedException(SimpleTestResult.failed(this, "no message"));
      }
   }

   protected void isEquals(String var1, boolean var2, boolean var3) {
      if (var2 != var3) {
         throw new TestFailedException(SimpleTestResult.failed(this, var1));
      }
   }

   protected void isEquals(String var1, long var2, long var4) {
      if (var2 != var4) {
         throw new TestFailedException(SimpleTestResult.failed(this, var1));
      }
   }

   protected void isEquals(String var1, Object var2, Object var3) {
      if (var2 != null || var3 != null) {
         if (var2 == null) {
            throw new TestFailedException(SimpleTestResult.failed(this, var1));
         } else if (var3 == null) {
            throw new TestFailedException(SimpleTestResult.failed(this, var1));
         } else if (!var2.equals(var3)) {
            throw new TestFailedException(SimpleTestResult.failed(this, var1));
         }
      }
   }

   protected boolean areEqual(byte[][] var1, byte[][] var2) {
      if (var1 == null && var2 == null) {
         return true;
      } else if (var1 != null && var2 != null) {
         if (var1.length != var2.length) {
            return false;
         } else {
            for (int var3 = 0; var3 < var1.length; var3++) {
               if (!this.areEqual(var1[var3], var2[var3])) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   protected void fail(String var1, Throwable var2) {
      throw new TestFailedException(SimpleTestResult.failed(this, var1, var2));
   }

   public void fail(String var1, Object var2, Object var3) {
      throw new TestFailedException(SimpleTestResult.failed(this, var1, var2, var3));
   }

   protected boolean areEqual(byte[] var1, byte[] var2) {
      return Arrays.areEqual(var1, var2);
   }

   public boolean areEqual(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6) {
      return Arrays.areEqual(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public TestResult perform() {
      try {
         this.performTest();
         return this.success();
      } catch (TestFailedException var2) {
         return var2.getResult();
      } catch (Exception var3) {
         return SimpleTestResult.failed(this, "Exception: " + var3, var3);
      }
   }

   public abstract void performTest() throws Exception;

   public static void runTest(Test var0) {
      runTest(var0, System.out);
   }

   public static void runTest(Test var0, PrintStream var1) {
      TestResult var2 = var0.perform();
      if (var2.getException() != null) {
         var2.getException().printStackTrace(var1);
      }

      var1.println(var2);
   }

   public static void runTests(Test[] var0) {
      runTests(var0, System.out);
   }

   public static void runTests(Test[] var0, PrintStream var1) {
      Vector var2 = new Vector();

      for (int var3 = 0; var3 != var0.length; var3++) {
         TestResult var4 = var0[var3].perform();
         if (!var4.isSuccessful()) {
            var2.addElement(var4);
         }

         if (var4.getException() != null) {
            var4.getException().printStackTrace(var1);
         }

         var1.println(var4);
      }

      var1.println("-----");
      if (var2.isEmpty()) {
         var1.println("All tests successful.");
      } else {
         var1.println("Completed with " + var2.size() + " FAILURES:");
         Enumeration var5 = var2.elements();

         while (var5.hasMoreElements()) {
            System.out.println("=>  " + (TestResult)var5.nextElement());
         }
      }
   }

   public Exception testException(String var1, String var2, SimpleTest.TestExceptionOperation var3) {
      try {
         var3.operation();
         this.fail(var1);
         return null;
      } catch (Exception var5) {
         if (var1 != null) {
            this.isTrue(var5.getMessage(), var5.getMessage().indexOf(var1) >= 0);
         }

         this.isTrue(var5.getMessage(), var5.getClass().getName().indexOf(var2) >= 0);
         return var5;
      }
   }

   protected interface TestExceptionOperation {
      void operation() throws Exception;
   }
}
