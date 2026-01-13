package com.nimbusds.jose;

public class KeyLengthException extends KeyException {
   private final int expectedLength;
   private final Algorithm alg;

   public KeyLengthException(String message) {
      super(message);
      this.expectedLength = 0;
      this.alg = null;
   }

   public KeyLengthException(Algorithm alg) {
      this(0, alg);
   }

   public KeyLengthException(int expectedLength, Algorithm alg) {
      super(
         (expectedLength > 0 ? "The expected key length is " + expectedLength + " bits" : "Unexpected key length")
            + (alg != null ? " (for " + alg + " algorithm)" : "")
      );
      this.expectedLength = expectedLength;
      this.alg = alg;
   }

   public int getExpectedKeyLength() {
      return this.expectedLength;
   }

   public Algorithm getAlgorithm() {
      return this.alg;
   }
}
