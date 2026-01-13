package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class EncryptedContentInfo extends ASN1Object {
   private ASN1ObjectIdentifier contentType;
   private AlgorithmIdentifier contentEncryptionAlgorithm;
   private ASN1OctetString encryptedContent;

   public EncryptedContentInfo(ASN1ObjectIdentifier var1, AlgorithmIdentifier var2, ASN1OctetString var3) {
      this.contentType = var1;
      this.contentEncryptionAlgorithm = var2;
      this.encryptedContent = var3;
   }

   private EncryptedContentInfo(ASN1Sequence var1) {
      if (var1.size() < 2) {
         throw new IllegalArgumentException("Truncated Sequence Found");
      } else {
         this.contentType = (ASN1ObjectIdentifier)var1.getObjectAt(0);
         this.contentEncryptionAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
         if (var1.size() > 2) {
            this.encryptedContent = ASN1OctetString.getInstance((ASN1TaggedObject)var1.getObjectAt(2), false);
         }
      }
   }

   public static EncryptedContentInfo getInstance(Object var0) {
      if (var0 instanceof EncryptedContentInfo) {
         return (EncryptedContentInfo)var0;
      } else {
         return var0 != null ? new EncryptedContentInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1ObjectIdentifier getContentType() {
      return this.contentType;
   }

   public AlgorithmIdentifier getContentEncryptionAlgorithm() {
      return this.contentEncryptionAlgorithm;
   }

   public ASN1OctetString getEncryptedContent() {
      return this.encryptedContent;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.contentType);
      var1.add(this.contentEncryptionAlgorithm);
      if (this.encryptedContent != null) {
         var1.add(new BERTaggedObject(false, 0, this.encryptedContent));
      }

      return new BERSequence(var1);
   }
}
