package org.bouncycastle.asn1.cryptopro;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class GOST3410ParamSetParameters extends ASN1Object {
   int keySize;
   ASN1Integer p;
   ASN1Integer q;
   ASN1Integer a;

   public static GOST3410ParamSetParameters getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static GOST3410ParamSetParameters getInstance(Object var0) {
      if (var0 == null || var0 instanceof GOST3410ParamSetParameters) {
         return (GOST3410ParamSetParameters)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new GOST3410ParamSetParameters((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("Invalid GOST3410Parameter: " + var0.getClass().getName());
      }
   }

   public GOST3410ParamSetParameters(int var1, BigInteger var2, BigInteger var3, BigInteger var4) {
      this.keySize = var1;
      this.p = new ASN1Integer(var2);
      this.q = new ASN1Integer(var3);
      this.a = new ASN1Integer(var4);
   }

   public GOST3410ParamSetParameters(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.keySize = ((ASN1Integer)var2.nextElement()).intValueExact();
      this.p = (ASN1Integer)var2.nextElement();
      this.q = (ASN1Integer)var2.nextElement();
      this.a = (ASN1Integer)var2.nextElement();
   }

   /** @deprecated */
   public int getLKeySize() {
      return this.keySize;
   }

   public int getKeySize() {
      return this.keySize;
   }

   public BigInteger getP() {
      return this.p.getPositiveValue();
   }

   public BigInteger getQ() {
      return this.q.getPositiveValue();
   }

   public BigInteger getA() {
      return this.a.getPositiveValue();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(new ASN1Integer(this.keySize));
      var1.add(this.p);
      var1.add(this.q);
      var1.add(this.a);
      return new DERSequence(var1);
   }
}
