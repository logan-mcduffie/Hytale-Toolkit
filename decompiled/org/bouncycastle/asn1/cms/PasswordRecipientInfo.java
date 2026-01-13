package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class PasswordRecipientInfo extends ASN1Object {
   private ASN1Integer version;
   private AlgorithmIdentifier keyDerivationAlgorithm;
   private AlgorithmIdentifier keyEncryptionAlgorithm;
   private ASN1OctetString encryptedKey;

   public PasswordRecipientInfo(AlgorithmIdentifier var1, ASN1OctetString var2) {
      this.version = new ASN1Integer(0L);
      this.keyEncryptionAlgorithm = var1;
      this.encryptedKey = var2;
   }

   public PasswordRecipientInfo(AlgorithmIdentifier var1, AlgorithmIdentifier var2, ASN1OctetString var3) {
      this.version = new ASN1Integer(0L);
      this.keyDerivationAlgorithm = var1;
      this.keyEncryptionAlgorithm = var2;
      this.encryptedKey = var3;
   }

   private PasswordRecipientInfo(ASN1Sequence var1) {
      this.version = (ASN1Integer)var1.getObjectAt(0);
      if (var1.getObjectAt(1) instanceof ASN1TaggedObject) {
         this.keyDerivationAlgorithm = AlgorithmIdentifier.getInstance((ASN1TaggedObject)var1.getObjectAt(1), false);
         this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(2));
         this.encryptedKey = (ASN1OctetString)var1.getObjectAt(3);
      } else {
         this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
         this.encryptedKey = (ASN1OctetString)var1.getObjectAt(2);
      }
   }

   public static PasswordRecipientInfo getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static PasswordRecipientInfo getInstance(Object var0) {
      if (var0 instanceof PasswordRecipientInfo) {
         return (PasswordRecipientInfo)var0;
      } else {
         return var0 != null ? new PasswordRecipientInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public AlgorithmIdentifier getKeyDerivationAlgorithm() {
      return this.keyDerivationAlgorithm;
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
      if (this.keyDerivationAlgorithm != null) {
         var1.add(new DERTaggedObject(false, 0, this.keyDerivationAlgorithm));
      }

      var1.add(this.keyEncryptionAlgorithm);
      var1.add(this.encryptedKey);
      return new DERSequence(var1);
   }
}
