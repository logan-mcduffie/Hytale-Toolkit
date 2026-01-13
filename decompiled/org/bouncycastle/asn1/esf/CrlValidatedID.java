package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CrlValidatedID extends ASN1Object {
   private OtherHash crlHash;
   private CrlIdentifier crlIdentifier;

   public static CrlValidatedID getInstance(Object var0) {
      if (var0 instanceof CrlValidatedID) {
         return (CrlValidatedID)var0;
      } else {
         return var0 != null ? new CrlValidatedID(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private CrlValidatedID(ASN1Sequence var1) {
      if (var1.size() >= 1 && var1.size() <= 2) {
         this.crlHash = OtherHash.getInstance(var1.getObjectAt(0));
         if (var1.size() > 1) {
            this.crlIdentifier = CrlIdentifier.getInstance(var1.getObjectAt(1));
         }
      } else {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      }
   }

   public CrlValidatedID(OtherHash var1) {
      this(var1, null);
   }

   public CrlValidatedID(OtherHash var1, CrlIdentifier var2) {
      this.crlHash = var1;
      this.crlIdentifier = var2;
   }

   public OtherHash getCrlHash() {
      return this.crlHash;
   }

   public CrlIdentifier getCrlIdentifier() {
      return this.crlIdentifier;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.crlHash.toASN1Primitive());
      if (null != this.crlIdentifier) {
         var1.add(this.crlIdentifier.toASN1Primitive());
      }

      return new DERSequence(var1);
   }
}
