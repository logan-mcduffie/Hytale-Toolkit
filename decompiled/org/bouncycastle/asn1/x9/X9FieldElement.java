package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.math.ec.ECFieldElement;

public class X9FieldElement extends ASN1Object {
   protected ECFieldElement f;
   private static X9IntegerConverter converter = new X9IntegerConverter();

   public X9FieldElement(ECFieldElement var1) {
      this.f = var1;
   }

   public ECFieldElement getValue() {
      return this.f;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      int var1 = converter.getByteLength(this.f);
      byte[] var2 = converter.integerToBytes(this.f.toBigInteger(), var1);
      return new DEROctetString(var2);
   }
}
