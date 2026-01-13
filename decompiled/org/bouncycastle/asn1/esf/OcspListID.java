package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class OcspListID extends ASN1Object {
   private ASN1Sequence ocspResponses;

   public static OcspListID getInstance(Object var0) {
      if (var0 instanceof OcspListID) {
         return (OcspListID)var0;
      } else {
         return var0 != null ? new OcspListID(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private OcspListID(ASN1Sequence var1) {
      if (var1.size() != 1) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         this.ocspResponses = (ASN1Sequence)var1.getObjectAt(0);
         Enumeration var2 = this.ocspResponses.getObjects();

         while (var2.hasMoreElements()) {
            OcspResponsesID.getInstance(var2.nextElement());
         }
      }
   }

   public OcspListID(OcspResponsesID[] var1) {
      this.ocspResponses = new DERSequence(var1);
   }

   public OcspResponsesID[] getOcspResponses() {
      OcspResponsesID[] var1 = new OcspResponsesID[this.ocspResponses.size()];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = OcspResponsesID.getInstance(this.ocspResponses.getObjectAt(var2));
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.ocspResponses);
   }
}
