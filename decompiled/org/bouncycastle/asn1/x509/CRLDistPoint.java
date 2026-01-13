package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Strings;

public class CRLDistPoint extends ASN1Object {
   ASN1Sequence seq = null;

   public static CRLDistPoint getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static CRLDistPoint getInstance(Object var0) {
      if (var0 instanceof CRLDistPoint) {
         return (CRLDistPoint)var0;
      } else {
         return var0 != null ? new CRLDistPoint(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static CRLDistPoint fromExtensions(Extensions var0) {
      return getInstance(Extensions.getExtensionParsedValue(var0, Extension.cRLDistributionPoints));
   }

   private CRLDistPoint(ASN1Sequence var1) {
      this.seq = var1;
   }

   public CRLDistPoint(DistributionPoint[] var1) {
      this.seq = new DERSequence(var1);
   }

   public DistributionPoint[] getDistributionPoints() {
      DistributionPoint[] var1 = new DistributionPoint[this.seq.size()];

      for (int var2 = 0; var2 != this.seq.size(); var2++) {
         var1[var2] = DistributionPoint.getInstance(this.seq.getObjectAt(var2));
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.seq;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      var1.append("CRLDistPoint:");
      var1.append(var2);
      DistributionPoint[] var3 = this.getDistributionPoints();

      for (int var4 = 0; var4 != var3.length; var4++) {
         var1.append("    ");
         var1.append(var3[var4]);
         var1.append(var2);
      }

      return var1.toString();
   }
}
