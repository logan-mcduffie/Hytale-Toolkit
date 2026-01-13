package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.util.BigIntegers;

public class Time64 extends UINT64 {
   public static long etsiEpochMicros = Time32.etsiEpochMillis * 1000L;

   public Time64(long var1) {
      this(BigInteger.valueOf(var1));
   }

   public Time64(BigInteger var1) {
      super(var1);
   }

   public Time64(UINT64 var1) {
      this(var1.getValue());
   }

   public static Time64 now() {
      return new Time64(1000L * System.currentTimeMillis() - etsiEpochMicros);
   }

   public static Time64 ofUnixMillis(long var0) {
      return new Time64(var0 * 1000L - etsiEpochMicros);
   }

   public static Time64 getInstance(Object var0) {
      if (var0 instanceof UINT64) {
         return new Time64((UINT64)var0);
      } else {
         return var0 != null ? new Time64(ASN1Integer.getInstance(var0).getValue()) : null;
      }
   }

   public long toUnixMillis() {
      return (BigIntegers.longValueExact(this.getValue()) + etsiEpochMicros) / 1000L;
   }
}
