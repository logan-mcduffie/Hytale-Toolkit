package org.bouncycastle.cms;

public class CMSAttributeTableGenerationException extends CMSRuntimeException {
   Exception e;

   public CMSAttributeTableGenerationException(String var1) {
      super(var1);
   }

   public CMSAttributeTableGenerationException(String var1, Exception var2) {
      super(var1);
      this.e = var2;
   }

   @Override
   public Exception getUnderlyingException() {
      return this.e;
   }

   @Override
   public Throwable getCause() {
      return this.e;
   }
}
