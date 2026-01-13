package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import java.util.Date;
import org.bouncycastle.asn1.ASN1Integer;

public class Time32 extends UINT32 {
   public static long etsiEpochMillis = 1072915200000L;

   public Time32(long var1) {
      super(var1);
   }

   public Time32(BigInteger var1) {
      super(var1);
   }

   public Time32(UINT32 var1) {
      this(var1.getValue());
   }

   public static Time32 now() {
      return ofUnixMillis(System.currentTimeMillis());
   }

   public static Time32 ofUnixMillis(long var0) {
      return new Time32((var0 - etsiEpochMillis) / 1000L);
   }

   public static Time32 getInstance(Object var0) {
      if (var0 instanceof UINT32) {
         return new Time32((UINT32)var0);
      } else {
         return var0 != null ? new Time32(ASN1Integer.getInstance(var0).getValue()) : null;
      }
   }

   public long toUnixMillis() {
      return this.getValue().longValue() * 1000L + etsiEpochMillis;
   }

   @Override
   public String toString() {
      return new Date(this.toUnixMillis()).toString();
   }
}
