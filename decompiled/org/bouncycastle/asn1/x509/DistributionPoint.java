package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Strings;

public class DistributionPoint extends ASN1Object {
   DistributionPointName distributionPoint;
   ReasonFlags reasons;
   GeneralNames cRLIssuer;

   public static DistributionPoint getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static DistributionPoint getInstance(Object var0) {
      if (var0 == null || var0 instanceof DistributionPoint) {
         return (DistributionPoint)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new DistributionPoint((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("Invalid DistributionPoint: " + var0.getClass().getName());
      }
   }

   public DistributionPoint(ASN1Sequence var1) {
      for (int var2 = 0; var2 != var1.size(); var2++) {
         ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var1.getObjectAt(var2));
         switch (var3.getTagNo()) {
            case 0:
               this.distributionPoint = DistributionPointName.getInstance(var3, true);
               break;
            case 1:
               this.reasons = new ReasonFlags(ASN1BitString.getInstance(var3, false));
               break;
            case 2:
               this.cRLIssuer = GeneralNames.getInstance(var3, false);
               break;
            default:
               throw new IllegalArgumentException("Unknown tag encountered in structure: " + var3.getTagNo());
         }
      }
   }

   public DistributionPoint(DistributionPointName var1, ReasonFlags var2, GeneralNames var3) {
      this.distributionPoint = var1;
      this.reasons = var2;
      this.cRLIssuer = var3;
   }

   public DistributionPointName getDistributionPoint() {
      return this.distributionPoint;
   }

   public ReasonFlags getReasons() {
      return this.reasons;
   }

   public GeneralNames getCRLIssuer() {
      return this.cRLIssuer;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      if (this.distributionPoint != null) {
         var1.add(new DERTaggedObject(0, this.distributionPoint));
      }

      if (this.reasons != null) {
         var1.add(new DERTaggedObject(false, 1, this.reasons));
      }

      if (this.cRLIssuer != null) {
         var1.add(new DERTaggedObject(false, 2, this.cRLIssuer));
      }

      return new DERSequence(var1);
   }

   @Override
   public String toString() {
      String var1 = Strings.lineSeparator();
      StringBuilder var2 = new StringBuilder();
      var2.append("DistributionPoint: [");
      var2.append(var1);
      if (this.distributionPoint != null) {
         this.appendObject(var2, var1, "distributionPoint", this.distributionPoint.toString());
      }

      if (this.reasons != null) {
         this.appendObject(var2, var1, "reasons", this.reasons.toString());
      }

      if (this.cRLIssuer != null) {
         this.appendObject(var2, var1, "cRLIssuer", this.cRLIssuer.toString());
      }

      var2.append("]");
      var2.append(var1);
      return var2.toString();
   }

   private void appendObject(StringBuilder var1, String var2, String var3, String var4) {
      String var5 = "    ";
      var1.append(var5);
      var1.append(var3);
      var1.append(":");
      var1.append(var2);
      var1.append(var5);
      var1.append(var5);
      var1.append(var4);
      var1.append(var2);
   }
}
