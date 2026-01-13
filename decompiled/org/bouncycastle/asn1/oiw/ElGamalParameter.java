package org.bouncycastle.asn1.oiw;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ElGamalParameter extends ASN1Object {
   ASN1Integer p;
   ASN1Integer g;

   public ElGamalParameter(BigInteger var1, BigInteger var2) {
      this.p = new ASN1Integer(var1);
      this.g = new ASN1Integer(var2);
   }

   private ElGamalParameter(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.p = (ASN1Integer)var2.nextElement();
      this.g = (ASN1Integer)var2.nextElement();
   }

   public static ElGamalParameter getInstance(Object var0) {
      if (var0 instanceof ElGamalParameter) {
         return (ElGamalParameter)var0;
      } else {
         return var0 != null ? new ElGamalParameter(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public BigInteger getP() {
      return this.p.getPositiveValue();
   }

   public BigInteger getG() {
      return this.g.getPositiveValue();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.p, this.g);
   }
}
