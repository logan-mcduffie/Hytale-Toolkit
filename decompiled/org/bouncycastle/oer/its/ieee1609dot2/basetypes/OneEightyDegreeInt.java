package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class OneEightyDegreeInt extends ASN1Object {
   private static final BigInteger loweBound = new BigInteger("-1799999999");
   private static final BigInteger upperBound = new BigInteger("1800000000");
   private static final BigInteger unknown = new BigInteger("1800000001");
   private final BigInteger value;

   public OneEightyDegreeInt(long var1) {
      this(BigInteger.valueOf(var1));
   }

   public OneEightyDegreeInt(BigInteger var1) {
      if (!var1.equals(unknown)) {
         if (var1.compareTo(loweBound) < 0) {
            throw new IllegalStateException("one eighty degree int cannot be less than -1799999999");
         }

         if (var1.compareTo(upperBound) > 0) {
            throw new IllegalStateException("one eighty degree int cannot be greater than 1800000000");
         }
      }

      this.value = var1;
   }

   private OneEightyDegreeInt(ASN1Integer var1) {
      this(var1.getValue());
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new ASN1Integer(this.value);
   }

   public BigInteger getValue() {
      return this.value;
   }

   public static OneEightyDegreeInt getInstance(Object var0) {
      if (var0 instanceof OneEightyDegreeInt) {
         return (OneEightyDegreeInt)var0;
      } else {
         return var0 != null ? new OneEightyDegreeInt(ASN1Integer.getInstance(var0)) : null;
      }
   }

   private static BigInteger assertValue(BigInteger var0) {
      return var0;
   }
}
