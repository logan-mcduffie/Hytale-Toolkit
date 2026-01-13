package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Strings;

public class IssuingDistributionPoint extends ASN1Object {
   private DistributionPointName distributionPoint;
   private boolean onlyContainsUserCerts;
   private boolean onlyContainsCACerts;
   private ReasonFlags onlySomeReasons;
   private boolean indirectCRL;
   private boolean onlyContainsAttributeCerts;
   private ASN1Sequence seq;

   public static IssuingDistributionPoint getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static IssuingDistributionPoint getInstance(Object var0) {
      if (var0 instanceof IssuingDistributionPoint) {
         return (IssuingDistributionPoint)var0;
      } else {
         return var0 != null ? new IssuingDistributionPoint(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public IssuingDistributionPoint(DistributionPointName var1, boolean var2, boolean var3, ReasonFlags var4, boolean var5, boolean var6) {
      if ((!var3 || !var2 && !var6) && (!var2 || !var6)) {
         this.distributionPoint = var1;
         this.indirectCRL = var5;
         this.onlyContainsAttributeCerts = var6;
         this.onlyContainsCACerts = var3;
         this.onlyContainsUserCerts = var2;
         this.onlySomeReasons = var4;
         ASN1EncodableVector var7 = new ASN1EncodableVector(6);
         if (var1 != null) {
            var7.add(new DERTaggedObject(true, 0, var1));
         }

         if (var2) {
            var7.add(new DERTaggedObject(false, 1, ASN1Boolean.getInstance(true)));
         }

         if (var3) {
            var7.add(new DERTaggedObject(false, 2, ASN1Boolean.getInstance(true)));
         }

         if (var4 != null) {
            var7.add(new DERTaggedObject(false, 3, var4));
         }

         if (var5) {
            var7.add(new DERTaggedObject(false, 4, ASN1Boolean.getInstance(true)));
         }

         if (var6) {
            var7.add(new DERTaggedObject(false, 5, ASN1Boolean.getInstance(true)));
         }

         this.seq = new DERSequence(var7);
      } else {
         throw new IllegalArgumentException("only one of onlyContainsCACerts, onlyContainsUserCerts, or onlyContainsAttributeCerts can be true");
      }
   }

   public IssuingDistributionPoint(DistributionPointName var1, boolean var2, boolean var3) {
      this(var1, false, false, null, var2, var3);
   }

   private IssuingDistributionPoint(ASN1Sequence var1) {
      this.seq = var1;

      for (int var2 = 0; var2 != var1.size(); var2++) {
         ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var1.getObjectAt(var2));
         switch (var3.getTagNo()) {
            case 0:
               this.distributionPoint = DistributionPointName.getInstance(var3, true);
               break;
            case 1:
               this.onlyContainsUserCerts = ASN1Boolean.getInstance(var3, false).isTrue();
               break;
            case 2:
               this.onlyContainsCACerts = ASN1Boolean.getInstance(var3, false).isTrue();
               break;
            case 3:
               this.onlySomeReasons = new ReasonFlags(ASN1BitString.getInstance(var3, false));
               break;
            case 4:
               this.indirectCRL = ASN1Boolean.getInstance(var3, false).isTrue();
               break;
            case 5:
               this.onlyContainsAttributeCerts = ASN1Boolean.getInstance(var3, false).isTrue();
               break;
            default:
               throw new IllegalArgumentException("unknown tag in IssuingDistributionPoint");
         }
      }
   }

   public boolean onlyContainsUserCerts() {
      return this.onlyContainsUserCerts;
   }

   public boolean onlyContainsCACerts() {
      return this.onlyContainsCACerts;
   }

   public boolean isIndirectCRL() {
      return this.indirectCRL;
   }

   public boolean onlyContainsAttributeCerts() {
      return this.onlyContainsAttributeCerts;
   }

   public DistributionPointName getDistributionPoint() {
      return this.distributionPoint;
   }

   public ReasonFlags getOnlySomeReasons() {
      return this.onlySomeReasons;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.seq;
   }

   @Override
   public String toString() {
      String var1 = Strings.lineSeparator();
      StringBuilder var2 = new StringBuilder();
      var2.append("IssuingDistributionPoint: [");
      var2.append(var1);
      if (this.distributionPoint != null) {
         this.appendObject(var2, var1, "distributionPoint", this.distributionPoint.toString());
      }

      if (this.onlyContainsUserCerts) {
         this.appendObject(var2, var1, "onlyContainsUserCerts", this.booleanToString(this.onlyContainsUserCerts));
      }

      if (this.onlyContainsCACerts) {
         this.appendObject(var2, var1, "onlyContainsCACerts", this.booleanToString(this.onlyContainsCACerts));
      }

      if (this.onlySomeReasons != null) {
         this.appendObject(var2, var1, "onlySomeReasons", this.onlySomeReasons.toString());
      }

      if (this.onlyContainsAttributeCerts) {
         this.appendObject(var2, var1, "onlyContainsAttributeCerts", this.booleanToString(this.onlyContainsAttributeCerts));
      }

      if (this.indirectCRL) {
         this.appendObject(var2, var1, "indirectCRL", this.booleanToString(this.indirectCRL));
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

   private String booleanToString(boolean var1) {
      return var1 ? "true" : "false";
   }
}
