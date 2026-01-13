package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CMCUnsignedData extends ASN1Object {
   private final BodyPartPath bodyPartPath;
   private final ASN1ObjectIdentifier identifier;
   private final ASN1Encodable content;

   public CMCUnsignedData(BodyPartPath var1, ASN1ObjectIdentifier var2, ASN1Encodable var3) {
      this.bodyPartPath = var1;
      this.identifier = var2;
      this.content = var3;
   }

   private CMCUnsignedData(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("incorrect sequence size");
      } else {
         this.bodyPartPath = BodyPartPath.getInstance(var1.getObjectAt(0));
         this.identifier = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(1));
         this.content = var1.getObjectAt(2);
      }
   }

   public static CMCUnsignedData getInstance(Object var0) {
      if (var0 instanceof CMCUnsignedData) {
         return (CMCUnsignedData)var0;
      } else {
         return var0 != null ? new CMCUnsignedData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.bodyPartPath);
      var1.add(this.identifier);
      var1.add(this.content);
      return new DERSequence(var1);
   }

   public BodyPartPath getBodyPartPath() {
      return this.bodyPartPath;
   }

   public ASN1ObjectIdentifier getIdentifier() {
      return this.identifier;
   }

   public ASN1Encodable getContent() {
      return this.content;
   }
}
