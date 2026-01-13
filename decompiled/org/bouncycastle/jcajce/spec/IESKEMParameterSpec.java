package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class IESKEMParameterSpec implements AlgorithmParameterSpec {
   private final byte[] recipientInfo;
   private final boolean usePointCompression;

   public IESKEMParameterSpec(byte[] var1) {
      this(var1, false);
   }

   public IESKEMParameterSpec(byte[] var1, boolean var2) {
      this.recipientInfo = Arrays.clone(var1);
      this.usePointCompression = var2;
   }

   public byte[] getRecipientInfo() {
      return Arrays.clone(this.recipientInfo);
   }

   public boolean hasUsePointCompression() {
      return this.usePointCompression;
   }
}
