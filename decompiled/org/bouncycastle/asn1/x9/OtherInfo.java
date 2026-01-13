package org.bouncycastle.asn1.x9;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class OtherInfo extends ASN1Object {
   private KeySpecificInfo keyInfo;
   private ASN1OctetString partyAInfo;
   private ASN1OctetString suppPubInfo;

   public OtherInfo(KeySpecificInfo var1, ASN1OctetString var2, ASN1OctetString var3) {
      this.keyInfo = var1;
      this.partyAInfo = var2;
      this.suppPubInfo = var3;
   }

   public static OtherInfo getInstance(Object var0) {
      if (var0 instanceof OtherInfo) {
         return (OtherInfo)var0;
      } else {
         return var0 != null ? new OtherInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private OtherInfo(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.keyInfo = KeySpecificInfo.getInstance(var2.nextElement());

      while (var2.hasMoreElements()) {
         ASN1TaggedObject var3 = (ASN1TaggedObject)var2.nextElement();
         if (var3.hasContextTag(0)) {
            this.partyAInfo = (ASN1OctetString)var3.getExplicitBaseObject();
         } else if (var3.hasContextTag(2)) {
            this.suppPubInfo = (ASN1OctetString)var3.getExplicitBaseObject();
         }
      }
   }

   public KeySpecificInfo getKeyInfo() {
      return this.keyInfo;
   }

   public ASN1OctetString getPartyAInfo() {
      return this.partyAInfo;
   }

   public ASN1OctetString getSuppPubInfo() {
      return this.suppPubInfo;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.keyInfo);
      if (this.partyAInfo != null) {
         var1.add(new DERTaggedObject(0, this.partyAInfo));
      }

      var1.add(new DERTaggedObject(2, this.suppPubInfo));
      return new DERSequence(var1);
   }
}
