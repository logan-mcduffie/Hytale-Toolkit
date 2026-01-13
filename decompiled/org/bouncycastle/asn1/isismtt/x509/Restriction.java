package org.bouncycastle.asn1.isismtt.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.DirectoryString;

public class Restriction extends ASN1Object {
   private DirectoryString restriction;

   public static Restriction getInstance(Object var0) {
      if (var0 instanceof Restriction) {
         return (Restriction)var0;
      } else {
         return var0 != null ? new Restriction(DirectoryString.getInstance(var0)) : null;
      }
   }

   private Restriction(DirectoryString var1) {
      this.restriction = var1;
   }

   public Restriction(String var1) {
      this.restriction = new DirectoryString(var1);
   }

   public DirectoryString getRestriction() {
      return this.restriction;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.restriction.toASN1Primitive();
   }
}
