package org.bouncycastle.oer.its.etsi102941.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class Version extends ASN1Object {
   private final BigInteger version;

   public Version(BigInteger var1) {
      this.version = var1;
   }

   public Version(int var1) {
      this(BigInteger.valueOf(var1));
   }

   public Version(long var1) {
      this(BigInteger.valueOf(var1));
   }

   protected Version(ASN1Integer var1) {
      this.version = var1.getValue();
   }

   public BigInteger getVersion() {
      return this.version;
   }

   public static Version getInstance(Object var0) {
      if (var0 instanceof UINT8) {
         return (Version)var0;
      } else {
         return var0 != null ? new Version(ASN1Integer.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new ASN1Integer(this.version);
   }
}
