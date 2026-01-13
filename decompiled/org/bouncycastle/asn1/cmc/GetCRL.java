package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.ReasonFlags;

public class GetCRL extends ASN1Object {
   private final X500Name issuerName;
   private GeneralName cRLName;
   private ASN1GeneralizedTime time;
   private ReasonFlags reasons;

   public GetCRL(X500Name var1, GeneralName var2, ASN1GeneralizedTime var3, ReasonFlags var4) {
      this.issuerName = var1;
      this.cRLName = var2;
      this.time = var3;
      this.reasons = var4;
   }

   private GetCRL(ASN1Sequence var1) {
      if (var1.size() >= 1 && var1.size() <= 4) {
         this.issuerName = X500Name.getInstance(var1.getObjectAt(0));
         int var2 = 1;
         if (var1.size() > var2 && var1.getObjectAt(var2).toASN1Primitive() instanceof ASN1TaggedObject) {
            this.cRLName = GeneralName.getInstance(var1.getObjectAt(var2++));
         }

         if (var1.size() > var2 && var1.getObjectAt(var2).toASN1Primitive() instanceof ASN1GeneralizedTime) {
            this.time = ASN1GeneralizedTime.getInstance(var1.getObjectAt(var2++));
         }

         if (var1.size() > var2 && var1.getObjectAt(var2).toASN1Primitive() instanceof ASN1BitString) {
            this.reasons = new ReasonFlags(ASN1BitString.getInstance(var1.getObjectAt(var2)));
         }
      } else {
         throw new IllegalArgumentException("incorrect sequence size");
      }
   }

   public static GetCRL getInstance(Object var0) {
      if (var0 instanceof GetCRL) {
         return (GetCRL)var0;
      } else {
         return var0 != null ? new GetCRL(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public X500Name getIssuerName() {
      return this.issuerName;
   }

   public GeneralName getcRLName() {
      return this.cRLName;
   }

   public ASN1GeneralizedTime getTime() {
      return this.time;
   }

   public ReasonFlags getReasons() {
      return this.reasons;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.issuerName);
      if (this.cRLName != null) {
         var1.add(this.cRLName);
      }

      if (this.time != null) {
         var1.add(this.time);
      }

      if (this.reasons != null) {
         var1.add(this.reasons);
      }

      return new DERSequence(var1);
   }
}
