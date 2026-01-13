package org.bouncycastle.pqc.crypto.lms;

public class LMSParameters {
   private final LMSigParameters lmSigParam;
   private final LMOtsParameters lmOTSParam;

   public LMSParameters(LMSigParameters var1, LMOtsParameters var2) {
      this.lmSigParam = var1;
      this.lmOTSParam = var2;
   }

   public LMSigParameters getLMSigParam() {
      return this.lmSigParam;
   }

   public LMOtsParameters getLMOTSParam() {
      return this.lmOTSParam;
   }
}
