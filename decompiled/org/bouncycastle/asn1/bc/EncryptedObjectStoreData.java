package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class EncryptedObjectStoreData extends ASN1Object {
   private final AlgorithmIdentifier encryptionAlgorithm;
   private final ASN1OctetString encryptedContent;

   public EncryptedObjectStoreData(AlgorithmIdentifier var1, byte[] var2) {
      this.encryptionAlgorithm = var1;
      this.encryptedContent = new DEROctetString(Arrays.clone(var2));
   }

   private EncryptedObjectStoreData(ASN1Sequence var1) {
      this.encryptionAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
      this.encryptedContent = ASN1OctetString.getInstance(var1.getObjectAt(1));
   }

   public static EncryptedObjectStoreData getInstance(Object var0) {
      if (var0 instanceof EncryptedObjectStoreData) {
         return (EncryptedObjectStoreData)var0;
      } else {
         return var0 != null ? new EncryptedObjectStoreData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1OctetString getEncryptedContent() {
      return this.encryptedContent;
   }

   public AlgorithmIdentifier getEncryptionAlgorithm() {
      return this.encryptionAlgorithm;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.encryptionAlgorithm, this.encryptedContent);
   }
}
