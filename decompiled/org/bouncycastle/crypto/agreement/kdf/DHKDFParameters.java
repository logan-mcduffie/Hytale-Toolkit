package org.bouncycastle.crypto.agreement.kdf;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.DerivationParameters;

public class DHKDFParameters implements DerivationParameters {
   private ASN1ObjectIdentifier algorithm;
   private int keySize;
   private byte[] z;
   private byte[] extraInfo;

   public DHKDFParameters(ASN1ObjectIdentifier var1, int var2, byte[] var3) {
      this(var1, var2, var3, null);
   }

   public DHKDFParameters(ASN1ObjectIdentifier var1, int var2, byte[] var3, byte[] var4) {
      this.algorithm = var1;
      this.keySize = var2;
      this.z = var3;
      this.extraInfo = var4;
   }

   public ASN1ObjectIdentifier getAlgorithm() {
      return this.algorithm;
   }

   public int getKeySize() {
      return this.keySize;
   }

   public byte[] getZ() {
      return this.z;
   }

   public byte[] getExtraInfo() {
      return this.extraInfo;
   }
}
