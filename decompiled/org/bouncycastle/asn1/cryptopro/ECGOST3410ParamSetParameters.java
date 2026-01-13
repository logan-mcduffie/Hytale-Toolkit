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

public class ECGOST3410ParamSetParameters extends ASN1Object {
   ASN1Integer p;
   ASN1Integer q;
   ASN1Integer a;
   ASN1Integer b;
   ASN1Integer x;
   ASN1Integer y;

   public static ECGOST3410ParamSetParameters getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static ECGOST3410ParamSetParameters getInstance(Object var0) {
      if (var0 == null || var0 instanceof ECGOST3410ParamSetParameters) {
         return (ECGOST3410ParamSetParameters)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new ECGOST3410ParamSetParameters((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("Invalid GOST3410Parameter: " + var0.getClass().getName());
      }
   }

   public ECGOST3410ParamSetParameters(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, int var5, BigInteger var6) {
      this.a = new ASN1Integer(var1);
      this.b = new ASN1Integer(var2);
      this.p = new ASN1Integer(var3);
      this.q = new ASN1Integer(var4);
      this.x = new ASN1Integer(var5);
      this.y = new ASN1Integer(var6);
   }

   public ECGOST3410ParamSetParameters(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.a = (ASN1Integer)var2.nextElement();
      this.b = (ASN1Integer)var2.nextElement();
      this.p = (ASN1Integer)var2.nextElement();
      this.q = (ASN1Integer)var2.nextElement();
      this.x = (ASN1Integer)var2.nextElement();
      this.y = (ASN1Integer)var2.nextElement();
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
      ASN1EncodableVector var1 = new ASN1EncodableVector(6);
      var1.add(this.a);
      var1.add(this.b);
      var1.add(this.p);
      var1.add(this.q);
      var1.add(this.x);
      var1.add(this.y);
      return new DERSequence(var1);
   }
}
