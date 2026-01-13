package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;

public class EndEntityType extends ASN1Object {
   public static final int app = 128;
   public static final int enrol = 64;
   private final ASN1BitString type;

   public EndEntityType(int var1) {
      this(new DERBitString(var1));
   }

   private EndEntityType(ASN1BitString var1) {
      this.type = var1;
   }

   public static EndEntityType getInstance(Object var0) {
      if (var0 instanceof EndEntityType) {
         return (EndEntityType)var0;
      } else {
         return var0 != null ? new EndEntityType(ASN1BitString.getInstance(var0)) : null;
      }
   }

   public ASN1BitString getType() {
      return this.type;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.type;
   }
}
