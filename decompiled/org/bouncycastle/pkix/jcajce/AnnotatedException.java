package org.bouncycastle.pkix.jcajce;

class AnnotatedException extends Exception {
   private Throwable _underlyingException;

   public AnnotatedException(String var1, Throwable var2) {
      super(var1);
      this._underlyingException = var2;
   }

   public AnnotatedException(String var1) {
      this(var1, null);
   }

   @Override
   public Throwable getCause() {
      return this._underlyingException;
   }
}
