package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class EncryptedSecretKeyData extends ASN1Object {
   private final AlgorithmIdentifier keyEncryptionAlgorithm;
   private final ASN1OctetString encryptedKeyData;

   public EncryptedSecretKeyData(AlgorithmIdentifier var1, byte[] var2) {
      this.keyEncryptionAlgorithm = var1;
      this.encryptedKeyData = new DEROctetString(Arrays.clone(var2));
   }

   private EncryptedSecretKeyData(ASN1Sequence var1) {
      this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
      this.encryptedKeyData = ASN1OctetString.getInstance(var1.getObjectAt(1));
   }

   public static EncryptedSecretKeyData getInstance(Object var0) {
      if (var0 instanceof EncryptedSecretKeyData) {
         return (EncryptedSecretKeyData)var0;
      } else {
         return var0 != null ? new EncryptedSecretKeyData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
      return this.keyEncryptionAlgorithm;
   }

   public byte[] getEncryptedKeyData() {
      return Arrays.clone(this.encryptedKeyData.getOctets());
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.keyEncryptionAlgorithm, this.encryptedKeyData);
   }
}
