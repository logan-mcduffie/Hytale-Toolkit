package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.BigIntegers;

public class IValue extends ASN1Object {
   private final BigInteger value;

   private IValue(ASN1Integer var1) {
      int var2 = BigIntegers.intValueExact(var1.getValue());
      if (var2 >= 0 && var2 <= 65535) {
         this.value = var1.getValue();
      } else {
         throw new IllegalArgumentException("value out of range");
      }
   }

   public static IValue getInstance(Object var0) {
      if (var0 instanceof IValue) {
         return (IValue)var0;
      } else {
         return var0 != null ? new IValue(ASN1Integer.getInstance(var0)) : null;
      }
   }

   public BigInteger getValue() {
      return this.value;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new ASN1Integer(this.value);
   }
}
