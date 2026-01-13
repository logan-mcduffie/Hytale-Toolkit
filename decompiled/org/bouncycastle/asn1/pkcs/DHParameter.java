package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class DHParameter extends ASN1Object {
   ASN1Integer p;
   ASN1Integer g;
   ASN1Integer l;

   public DHParameter(BigInteger var1, BigInteger var2, int var3) {
      this.p = new ASN1Integer(var1);
      this.g = new ASN1Integer(var2);
      if (var3 != 0) {
         this.l = new ASN1Integer(var3);
      } else {
         this.l = null;
      }
   }

   public static DHParameter getInstance(Object var0) {
      if (var0 instanceof DHParameter) {
         return (DHParameter)var0;
      } else {
         return var0 != null ? new DHParameter(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private DHParameter(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.p = ASN1Integer.getInstance(var2.nextElement());
      this.g = ASN1Integer.getInstance(var2.nextElement());
      if (var2.hasMoreElements()) {
         this.l = (ASN1Integer)var2.nextElement();
      } else {
         this.l = null;
      }
   }

   public BigInteger getP() {
      return this.p.getPositiveValue();
   }

   public BigInteger getG() {
      return this.g.getPositiveValue();
   }

   public BigInteger getL() {
      return this.l == null ? null : this.l.getPositiveValue();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.p);
      var1.add(this.g);
      if (this.getL() != null) {
         var1.add(this.l);
      }

      return new DERSequence(var1);
   }
}
