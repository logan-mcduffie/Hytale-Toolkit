package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class OcspResponsesID extends ASN1Object {
   private OcspIdentifier ocspIdentifier;
   private OtherHash ocspRepHash;

   public static OcspResponsesID getInstance(Object var0) {
      if (var0 instanceof OcspResponsesID) {
         return (OcspResponsesID)var0;
      } else {
         return var0 != null ? new OcspResponsesID(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private OcspResponsesID(ASN1Sequence var1) {
      if (var1.size() >= 1 && var1.size() <= 2) {
         this.ocspIdentifier = OcspIdentifier.getInstance(var1.getObjectAt(0));
         if (var1.size() > 1) {
            this.ocspRepHash = OtherHash.getInstance(var1.getObjectAt(1));
         }
      } else {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      }
   }

   public OcspResponsesID(OcspIdentifier var1) {
      this(var1, null);
   }

   public OcspResponsesID(OcspIdentifier var1, OtherHash var2) {
      this.ocspIdentifier = var1;
      this.ocspRepHash = var2;
   }

   public OcspIdentifier getOcspIdentifier() {
      return this.ocspIdentifier;
   }

   public OtherHash getOcspRepHash() {
      return this.ocspRepHash;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.ocspIdentifier);
      if (null != this.ocspRepHash) {
         var1.add(this.ocspRepHash);
      }

      return new DERSequence(var1);
   }
}
