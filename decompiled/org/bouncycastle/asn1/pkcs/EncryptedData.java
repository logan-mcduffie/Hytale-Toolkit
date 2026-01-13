package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class EncryptedData extends ASN1Object {
   ASN1Sequence data;

   public static EncryptedData getInstance(Object var0) {
      if (var0 instanceof EncryptedData) {
         return (EncryptedData)var0;
      } else {
         return var0 != null ? new EncryptedData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private EncryptedData(ASN1Sequence var1) {
      ASN1Integer var2 = (ASN1Integer)var1.getObjectAt(0);
      if (!var2.hasValue(0)) {
         throw new IllegalArgumentException("sequence not version 0");
      } else {
         this.data = ASN1Sequence.getInstance(var1.getObjectAt(1));
      }
   }

   public EncryptedData(ASN1ObjectIdentifier var1, AlgorithmIdentifier var2, ASN1Encodable var3) {
      ASN1EncodableVector var4 = new ASN1EncodableVector(3);
      var4.add(var1);
      var4.add(var2.toASN1Primitive());
      var4.add(new BERTaggedObject(false, 0, var3));
      this.data = new BERSequence(var4);
   }

   public ASN1ObjectIdentifier getContentType() {
      return ASN1ObjectIdentifier.getInstance(this.data.getObjectAt(0));
   }

   public AlgorithmIdentifier getEncryptionAlgorithm() {
      return AlgorithmIdentifier.getInstance(this.data.getObjectAt(1));
   }

   public ASN1OctetString getContent() {
      if (this.data.size() == 3) {
         ASN1TaggedObject var1 = ASN1TaggedObject.getInstance(this.data.getObjectAt(2));
         return ASN1OctetString.getInstance(var1, false);
      } else {
         return null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new BERSequence(new ASN1Integer(0L), this.data);
   }
}
