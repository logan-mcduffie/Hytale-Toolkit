package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class CrlOcspRef extends ASN1Object {
   private CrlListID crlids;
   private OcspListID ocspids;
   private OtherRevRefs otherRev;

   public static CrlOcspRef getInstance(Object var0) {
      if (var0 instanceof CrlOcspRef) {
         return (CrlOcspRef)var0;
      } else {
         return var0 != null ? new CrlOcspRef(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private CrlOcspRef(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();

      while (var2.hasMoreElements()) {
         ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var2.nextElement(), 128);
         switch (var3.getTagNo()) {
            case 0:
               this.crlids = CrlListID.getInstance(var3.getExplicitBaseObject());
               break;
            case 1:
               this.ocspids = OcspListID.getInstance(var3.getExplicitBaseObject());
               break;
            case 2:
               this.otherRev = OtherRevRefs.getInstance(var3.getExplicitBaseObject());
               break;
            default:
               throw new IllegalArgumentException("illegal tag");
         }
      }
   }

   public CrlOcspRef(CrlListID var1, OcspListID var2, OtherRevRefs var3) {
      this.crlids = var1;
      this.ocspids = var2;
      this.otherRev = var3;
   }

   public CrlListID getCrlids() {
      return this.crlids;
   }

   public OcspListID getOcspids() {
      return this.ocspids;
   }

   public OtherRevRefs getOtherRev() {
      return this.otherRev;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      if (null != this.crlids) {
         var1.add(new DERTaggedObject(true, 0, this.crlids.toASN1Primitive()));
      }

      if (null != this.ocspids) {
         var1.add(new DERTaggedObject(true, 1, this.ocspids.toASN1Primitive()));
      }

      if (null != this.otherRev) {
         var1.add(new DERTaggedObject(true, 2, this.otherRev.toASN1Primitive()));
      }

      return new DERSequence(var1);
   }
}
