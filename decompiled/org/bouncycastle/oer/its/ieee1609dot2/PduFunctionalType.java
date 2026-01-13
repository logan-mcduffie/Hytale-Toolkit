package org.bouncycastle.oer.its.ieee1609dot2;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class PduFunctionalType extends ASN1Object {
   private static final BigInteger MAX = BigInteger.valueOf(255L);
   public static final PduFunctionalType tlsHandshake = new PduFunctionalType(1L);
   public static final PduFunctionalType iso21177ExtendedAuth = new PduFunctionalType(2L);
   private final BigInteger functionalType;

   public PduFunctionalType(long var1) {
      this(BigInteger.valueOf(var1));
   }

   public PduFunctionalType(BigInteger var1) {
      this.functionalType = assertValue(var1);
   }

   public PduFunctionalType(byte[] var1) {
      this(new BigInteger(var1));
   }

   private PduFunctionalType(ASN1Integer var1) {
      this(var1.getValue());
   }

   public static PduFunctionalType getInstance(Object var0) {
      if (var0 instanceof PduFunctionalType) {
         return (PduFunctionalType)var0;
      } else {
         return var0 != null ? new PduFunctionalType(ASN1Integer.getInstance(var0)) : null;
      }
   }

   public BigInteger getFunctionalType() {
      return this.functionalType;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new ASN1Integer(this.functionalType);
   }

   private static BigInteger assertValue(BigInteger var0) {
      if (var0.signum() < 0) {
         throw new IllegalArgumentException("value less than 0");
      } else if (var0.compareTo(MAX) > 0) {
         throw new IllegalArgumentException("value exceeds " + MAX);
      } else {
         return var0;
      }
   }
}
