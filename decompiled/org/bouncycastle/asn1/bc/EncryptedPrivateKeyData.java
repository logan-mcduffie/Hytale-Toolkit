package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.x509.Certificate;

public class EncryptedPrivateKeyData extends ASN1Object {
   private final EncryptedPrivateKeyInfo encryptedPrivateKeyInfo;
   private final Certificate[] certificateChain;

   public EncryptedPrivateKeyData(EncryptedPrivateKeyInfo var1, Certificate[] var2) {
      this.encryptedPrivateKeyInfo = var1;
      this.certificateChain = new Certificate[var2.length];
      System.arraycopy(var2, 0, this.certificateChain, 0, var2.length);
   }

   private EncryptedPrivateKeyData(ASN1Sequence var1) {
      this.encryptedPrivateKeyInfo = EncryptedPrivateKeyInfo.getInstance(var1.getObjectAt(0));
      ASN1Sequence var2 = ASN1Sequence.getInstance(var1.getObjectAt(1));
      this.certificateChain = new Certificate[var2.size()];

      for (int var3 = 0; var3 != this.certificateChain.length; var3++) {
         this.certificateChain[var3] = Certificate.getInstance(var2.getObjectAt(var3));
      }
   }

   public static EncryptedPrivateKeyData getInstance(Object var0) {
      if (var0 instanceof EncryptedPrivateKeyData) {
         return (EncryptedPrivateKeyData)var0;
      } else {
         return var0 != null ? new EncryptedPrivateKeyData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public Certificate[] getCertificateChain() {
      Certificate[] var1 = new Certificate[this.certificateChain.length];
      System.arraycopy(this.certificateChain, 0, var1, 0, this.certificateChain.length);
      return var1;
   }

   public EncryptedPrivateKeyInfo getEncryptedPrivateKeyInfo() {
      return this.encryptedPrivateKeyInfo;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.encryptedPrivateKeyInfo, new DERSequence(this.certificateChain));
   }
}
