package org.bouncycastle.jcajce.spec;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class KEMKDFSpec {
   private final String keyAlgorithmName;
   private final int keySizeInBits;
   private final AlgorithmIdentifier kdfAlgorithm;
   private final byte[] otherInfo;

   protected KEMKDFSpec(AlgorithmIdentifier var1, byte[] var2, String var3, int var4) {
      this.keyAlgorithmName = var3;
      this.keySizeInBits = var4;
      this.kdfAlgorithm = var1;
      this.otherInfo = var2;
   }

   public String getKeyAlgorithmName() {
      return this.keyAlgorithmName;
   }

   public int getKeySize() {
      return this.keySizeInBits;
   }

   public AlgorithmIdentifier getKdfAlgorithm() {
      return this.kdfAlgorithm;
   }

   public byte[] getOtherInfo() {
      return Arrays.clone(this.otherInfo);
   }
}
