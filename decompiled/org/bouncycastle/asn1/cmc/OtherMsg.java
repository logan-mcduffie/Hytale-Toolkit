package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class OtherMsg extends ASN1Object {
   private final BodyPartID bodyPartID;
   private final ASN1ObjectIdentifier otherMsgType;
   private final ASN1Encodable otherMsgValue;

   public OtherMsg(BodyPartID var1, ASN1ObjectIdentifier var2, ASN1Encodable var3) {
      this.bodyPartID = var1;
      this.otherMsgType = var2;
      this.otherMsgValue = var3;
   }

   private OtherMsg(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("incorrect sequence size");
      } else {
         this.bodyPartID = BodyPartID.getInstance(var1.getObjectAt(0));
         this.otherMsgType = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(1));
         this.otherMsgValue = var1.getObjectAt(2);
      }
   }

   public static OtherMsg getInstance(Object var0) {
      if (var0 instanceof OtherMsg) {
         return (OtherMsg)var0;
      } else {
         return var0 != null ? new OtherMsg(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static OtherMsg getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.bodyPartID);
      var1.add(this.otherMsgType);
      var1.add(this.otherMsgValue);
      return new DERSequence(var1);
   }

   public BodyPartID getBodyPartID() {
      return this.bodyPartID;
   }

   public ASN1ObjectIdentifier getOtherMsgType() {
      return this.otherMsgType;
   }

   public ASN1Encodable getOtherMsgValue() {
      return this.otherMsgValue;
   }
}
