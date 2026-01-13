package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public abstract class UintBase extends ASN1Object {
   protected final BigInteger value;

   public UintBase(BigInteger var1) {
      this.value = var1;
      this.assertLimit();
   }

   public UintBase(int var1) {
      this(BigInteger.valueOf(var1));
   }

   public UintBase(long var1) {
      this(BigInteger.valueOf(var1));
   }

   protected UintBase(ASN1Integer var1) {
      this(var1.getValue());
   }

   public BigInteger getValue() {
      return this.value;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new ASN1Integer(this.value);
   }

   protected abstract void assertLimit();
}
