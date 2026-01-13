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

public class KeyTransRecipientInfo extends ASN1Object {
   private ASN1Integer version;
   private RecipientIdentifier rid;
   private AlgorithmIdentifier keyEncryptionAlgorithm;
   private ASN1OctetString encryptedKey;

   public KeyTransRecipientInfo(RecipientIdentifier var1, AlgorithmIdentifier var2, ASN1OctetString var3) {
      if (var1.toASN1Primitive() instanceof ASN1TaggedObject) {
         this.version = new ASN1Integer(2L);
      } else {
         this.version = new ASN1Integer(0L);
      }

      this.rid = var1;
      this.keyEncryptionAlgorithm = var2;
      this.encryptedKey = var3;
   }

   private KeyTransRecipientInfo(ASN1Sequence var1) {
      this.version = (ASN1Integer)var1.getObjectAt(0);
      this.rid = RecipientIdentifier.getInstance(var1.getObjectAt(1));
      this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(2));
      this.encryptedKey = (ASN1OctetString)var1.getObjectAt(3);
   }

   public static KeyTransRecipientInfo getInstance(Object var0) {
      if (var0 instanceof KeyTransRecipientInfo) {
         return (KeyTransRecipientInfo)var0;
      } else {
         return var0 != null ? new KeyTransRecipientInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public RecipientIdentifier getRecipientIdentifier() {
      return this.rid;
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
      var1.add(this.rid);
      var1.add(this.keyEncryptionAlgorithm);
      var1.add(this.encryptedKey);
      return new DERSequence(var1);
   }
}
