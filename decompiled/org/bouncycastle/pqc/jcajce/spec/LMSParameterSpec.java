package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.pqc.crypto.lms.LMOtsParameters;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;

/** @deprecated */
public class LMSParameterSpec implements AlgorithmParameterSpec {
   private final LMSigParameters lmSigParams;
   private final LMOtsParameters lmOtsParameters;

   public LMSParameterSpec(LMSigParameters var1, LMOtsParameters var2) {
      this.lmSigParams = var1;
      this.lmOtsParameters = var2;
   }

   public LMSigParameters getSigParams() {
      return this.lmSigParams;
   }

   public LMOtsParameters getOtsParams() {
      return this.lmOtsParameters;
   }
}
