package org.bouncycastle.util.test;

public interface TestResult {
   boolean isSuccessful();

   Throwable getException();

   @Override
   String toString();
}
