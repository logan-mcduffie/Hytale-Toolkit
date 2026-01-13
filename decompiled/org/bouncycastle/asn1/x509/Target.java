package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class Target extends ASN1Object implements ASN1Choice {
   public static final int targetName = 0;
   public static final int targetGroup = 1;
   private GeneralName targName;
   private GeneralName targGroup;

   public static Target getInstance(Object var0) {
      if (var0 == null || var0 instanceof Target) {
         return (Target)var0;
      } else if (var0 instanceof ASN1TaggedObject) {
         return new Target((ASN1TaggedObject)var0);
      } else {
         throw new IllegalArgumentException("unknown object in factory: " + var0.getClass());
      }
   }

   private Target(ASN1TaggedObject var1) {
      switch (var1.getTagNo()) {
         case 0:
            this.targName = GeneralName.getInstance(var1, true);
            break;
         case 1:
            this.targGroup = GeneralName.getInstance(var1, true);
            break;
         default:
            throw new IllegalArgumentException("unknown tag: " + var1.getTagNo());
      }
   }

   public Target(int var1, GeneralName var2) {
      this(new DERTaggedObject(var1, var2));
   }

   public GeneralName getTargetGroup() {
      return this.targGroup;
   }

   public GeneralName getTargetName() {
      return this.targName;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.targName != null ? new DERTaggedObject(true, 0, this.targName) : new DERTaggedObject(true, 1, this.targGroup);
   }
}
