package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Strings;

public class DistributionPointName extends ASN1Object implements ASN1Choice {
   public static final int FULL_NAME = 0;
   public static final int NAME_RELATIVE_TO_CRL_ISSUER = 1;
   private final ASN1Encodable name;
   private final int type;

   public static DistributionPointName getInstance(Object var0) {
      if (var0 == null || var0 instanceof DistributionPointName) {
         return (DistributionPointName)var0;
      } else if (var0 instanceof ASN1TaggedObject) {
         return new DistributionPointName((ASN1TaggedObject)var0);
      } else {
         throw new IllegalArgumentException("unknown object in factory: " + var0.getClass().getName());
      }
   }

   public static DistributionPointName getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Util.getInstanceChoiceBaseObject(var0, var1, "DistributionPointName"));
   }

   public static DistributionPointName getTagged(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Util.getTaggedChoiceBaseObject(var0, var1, "DistributionPointName"));
   }

   public DistributionPointName(int var1, ASN1Encodable var2) {
      this.type = var1;
      this.name = var2;
   }

   public DistributionPointName(GeneralNames var1) {
      this(0, var1);
   }

   public int getType() {
      return this.type;
   }

   public ASN1Encodable getName() {
      return this.name;
   }

   public DistributionPointName(ASN1TaggedObject var1) {
      this.type = var1.getTagNo();
      if (var1.hasContextTag(0)) {
         this.name = GeneralNames.getInstance(var1, false);
      } else {
         if (!var1.hasContextTag(1)) {
            throw new IllegalArgumentException("unknown tag: " + ASN1Util.getTagText(var1));
         }

         this.name = ASN1Set.getInstance(var1, false);
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(false, this.type, this.name);
   }

   @Override
   public String toString() {
      String var1 = Strings.lineSeparator();
      StringBuilder var2 = new StringBuilder();
      var2.append("DistributionPointName: [");
      var2.append(var1);
      if (this.type == 0) {
         this.appendObject(var2, var1, "fullName", this.name.toString());
      } else {
         this.appendObject(var2, var1, "nameRelativeToCRLIssuer", this.name.toString());
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
