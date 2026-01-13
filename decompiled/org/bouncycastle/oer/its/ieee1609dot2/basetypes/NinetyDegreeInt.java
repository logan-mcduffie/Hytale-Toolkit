package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class NinetyDegreeInt extends ASN1Object {
   private static final BigInteger loweBound = new BigInteger("-900000000");
   private static final BigInteger upperBound = new BigInteger("900000000");
   private static final BigInteger unknown = new BigInteger("900000001");
   private final BigInteger value;

   public NinetyDegreeInt(long var1) {
      this(BigInteger.valueOf(var1));
   }

   public NinetyDegreeInt(BigInteger var1) {
      if (!var1.equals(unknown)) {
         if (var1.compareTo(loweBound) < 0) {
            throw new IllegalStateException("ninety degree int cannot be less than -900000000");
         }

         if (var1.compareTo(upperBound) > 0) {
            throw new IllegalStateException("ninety degree int cannot be greater than 900000000");
         }
      }

      this.value = var1;
   }

   private NinetyDegreeInt(ASN1Integer var1) {
      this(var1.getValue());
   }

   public BigInteger getValue() {
      return this.value;
   }

   public static NinetyDegreeInt getInstance(Object var0) {
      if (var0 instanceof NinetyDegreeInt) {
         return (NinetyDegreeInt)var0;
      } else {
         return var0 != null ? new NinetyDegreeInt(ASN1Integer.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new ASN1Integer(this.value);
   }
}
