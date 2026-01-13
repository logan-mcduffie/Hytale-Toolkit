package org.bouncycastle.oer.its.etsi103097.extension;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class ExtId extends ASN1Object {
   private final BigInteger extId;
   private static final BigInteger MAX = BigInteger.valueOf(255L);

   public ExtId(long var1) {
      this(BigInteger.valueOf(var1));
   }

   public ExtId(BigInteger var1) {
      if (var1.signum() >= 0 && var1.compareTo(MAX) <= 0) {
         this.extId = var1;
      } else {
         throw new IllegalArgumentException("value " + var1 + " outside of range 0...255");
      }
   }

   public ExtId(byte[] var1) {
      this(new BigInteger(var1));
   }

   private ExtId(ASN1Integer var1) {
      this(var1.getValue());
   }

   public BigInteger getExtId() {
      return this.extId;
   }

   public static ExtId getInstance(Object var0) {
      if (var0 instanceof ExtId) {
         return (ExtId)var0;
      } else {
         return var0 != null ? new ExtId(ASN1Integer.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new ASN1Integer(this.extId);
   }
}
