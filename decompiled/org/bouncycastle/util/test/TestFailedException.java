package org.bouncycastle.util.test;

public class TestFailedException extends RuntimeException {
   private TestResult _result;

   public TestFailedException(TestResult var1) {
      this._result = var1;
   }

   public TestResult getResult() {
      return this._result;
   }
}
