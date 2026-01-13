package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KEKRecipientInfo extends ASN1Object {
   private ASN1Integer version;
   private KEKIdentifier kekid;
   private AlgorithmIdentifier keyEncryptionAlgorithm;
   private ASN1OctetString encryptedKey;

   public KEKRecipientInfo(KEKIdentifier var1, AlgorithmIdentifier var2, ASN1OctetString var3) {
      this.version = new ASN1Integer(4L);
      this.kekid = var1;
      this.keyEncryptionAlgorithm = var2;
      this.encryptedKey = var3;
   }

   public KEKRecipientInfo(ASN1Sequence var1) {
      this.version = (ASN1Integer)var1.getObjectAt(0);
      this.kekid = KEKIdentifier.getInstance(var1.getObjectAt(1));
      this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(2));
      this.encryptedKey = (ASN1OctetString)var1.getObjectAt(3);
   }

   public static KEKRecipientInfo getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static KEKRecipientInfo getInstance(Object var0) {
      if (var0 instanceof KEKRecipientInfo) {
         return (KEKRecipientInfo)var0;
      } else {
         return var0 != null ? new KEKRecipientInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public KEKIdentifier getKekid() {
      return this.kekid;
   }

   public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
      return this.keyEncryptionAlgorithm;
   }

   public ASN1OctetString getEncryptedKey() {
      return this.encryptedKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.version);
      var1.add(this.kekid);
      var1.add(this.keyEncryptionAlgorithm);
      var1.add(this.encryptedKey);
      return new DERSequence(var1);
   }
}
