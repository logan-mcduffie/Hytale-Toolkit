package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class Psid extends ASN1Object {
   private final BigInteger psid;

   public Psid(long var1) {
      this(BigInteger.valueOf(var1));
   }

   public Psid(BigInteger var1) {
      if (var1.signum() < 0) {
         throw new IllegalStateException("psid must be greater than zero");
      } else {
         this.psid = var1;
      }
   }

   public BigInteger getPsid() {
      return this.psid;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new ASN1Integer(this.psid);
   }

   public static Psid getInstance(Object var0) {
      if (var0 instanceof Psid) {
         return (Psid)var0;
      } else {
         return var0 != null ? new Psid(ASN1Integer.getInstance(var0).getValue()) : null;
      }
   }
}
