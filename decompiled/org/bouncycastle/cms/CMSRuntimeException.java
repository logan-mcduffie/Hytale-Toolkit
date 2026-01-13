package org.bouncycastle.cms;

public class CMSRuntimeException extends RuntimeException {
   Exception e;

   public CMSRuntimeException(String var1) {
      super(var1);
   }

   public CMSRuntimeException(String var1, Exception var2) {
      super(var1);
      this.e = var2;
   }

   public Exception getUnderlyingException() {
      return this.e;
   }

   @Override
   public Throwable getCause() {
      return this.e;
   }
}
