package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.pqc.crypto.lms.LMOtsParameters;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;

public class LMSKeyGenParameterSpec implements AlgorithmParameterSpec {
   private static final Map<String, LMSigParameters> sigParameters = new HashMap<>();
   private static final Map<String, LMOtsParameters> otsParameters = new HashMap<>();
   private final LMSigParameters lmSigParams;
   private final LMOtsParameters lmOtsParameters;

   public LMSKeyGenParameterSpec(LMSigParameters var1, LMOtsParameters var2) {
      this.lmSigParams = var1;
      this.lmOtsParameters = var2;
   }

   public LMSigParameters getSigParams() {
      return this.lmSigParams;
   }

   public LMOtsParameters getOtsParams() {
      return this.lmOtsParameters;
   }

   public static LMSKeyGenParameterSpec fromNames(String var0, String var1) {
      if (!sigParameters.containsKey(var0)) {
         throw new IllegalArgumentException("LM signature parameter name " + var0 + " not recognized");
      } else if (!otsParameters.containsKey(var1)) {
         throw new IllegalArgumentException("LM OTS parameter name " + var1 + " not recognized");
      } else {
         return new LMSKeyGenParameterSpec(sigParameters.get(var0), otsParameters.get(var1));
      }
   }

   static {
      sigParameters.put("lms-sha256-n32-h5", LMSigParameters.lms_sha256_n32_h5);
      sigParameters.put("lms-sha256-n32-h10", LMSigParameters.lms_sha256_n32_h10);
      sigParameters.put("lms-sha256-n32-h15", LMSigParameters.lms_sha256_n32_h15);
      sigParameters.put("lms-sha256-n32-h20", LMSigParameters.lms_sha256_n32_h20);
      sigParameters.put("lms-sha256-n32-h25", LMSigParameters.lms_sha256_n32_h25);
      sigParameters.put("lms-sha256-n24-h5", LMSigParameters.lms_sha256_n24_h5);
      sigParameters.put("lms-sha256-n24-h10", LMSigParameters.lms_sha256_n24_h10);
      sigParameters.put("lms-sha256-n24-h15", LMSigParameters.lms_sha256_n24_h15);
      sigParameters.put("lms-sha256-n24-h20", LMSigParameters.lms_sha256_n24_h20);
      sigParameters.put("lms-sha256-n24-h25", LMSigParameters.lms_sha256_n24_h25);
      sigParameters.put("lms-shake256-n32-h5", LMSigParameters.lms_shake256_n32_h5);
      sigParameters.put("lms-shake256-n32-h10", LMSigParameters.lms_shake256_n32_h10);
      sigParameters.put("lms-shake256-n32-h15", LMSigParameters.lms_shake256_n32_h15);
      sigParameters.put("lms-shake256-n32-h20", LMSigParameters.lms_shake256_n32_h20);
      sigParameters.put("lms-shake256-n32-h25", LMSigParameters.lms_shake256_n32_h25);
      sigParameters.put("lms-shake256-n24-h5", LMSigParameters.lms_shake256_n24_h5);
      sigParameters.put("lms-shake256-n24-h10", LMSigParameters.lms_shake256_n24_h10);
      sigParameters.put("lms-shake256-n24-h15", LMSigParameters.lms_shake256_n24_h15);
      sigParameters.put("lms-shake256-n24-h20", LMSigParameters.lms_shake256_n24_h20);
      sigParameters.put("lms-shake256-n24-h25", LMSigParameters.lms_shake256_n24_h25);
      otsParameters.put("sha256-n32-w1", LMOtsParameters.sha256_n32_w1);
      otsParameters.put("sha256-n32-w2", LMOtsParameters.sha256_n32_w2);
      otsParameters.put("sha256-n32-w4", LMOtsParameters.sha256_n32_w4);
      otsParameters.put("sha256-n32-w8", LMOtsParameters.sha256_n32_w8);
   }
}
