package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import org.bouncycastle.crypto.params.HKDFParameters;

public class HKDFParameterSpec implements KeySpec, AlgorithmParameterSpec {
   private final HKDFParameters hkdfParameters;
   private final int outputLength;

   public HKDFParameterSpec(byte[] var1, byte[] var2, byte[] var3, int var4) {
      this.hkdfParameters = new HKDFParameters(var1, var2, var3);
      this.outputLength = var4;
   }

   public byte[] getIKM() {
      return this.hkdfParameters.getIKM();
   }

   public boolean skipExtract() {
      return this.hkdfParameters.skipExtract();
   }

   public byte[] getSalt() {
      return this.hkdfParameters.getSalt();
   }

   public byte[] getInfo() {
      return this.hkdfParameters.getInfo();
   }

   public int getOutputLength() {
      return this.outputLength;
   }
}
