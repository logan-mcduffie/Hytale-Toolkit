package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

/** @deprecated */
public class RSAPublicKeyStructure extends ASN1Object {
   private BigInteger modulus;
   private BigInteger publicExponent;

   public static RSAPublicKeyStructure getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static RSAPublicKeyStructure getInstance(Object var0) {
      if (var0 == null || var0 instanceof RSAPublicKeyStructure) {
         return (RSAPublicKeyStructure)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new RSAPublicKeyStructure((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("Invalid RSAPublicKeyStructure: " + var0.getClass().getName());
      }
   }

   public RSAPublicKeyStructure(BigInteger var1, BigInteger var2) {
      this.modulus = var1;
      this.publicExponent = var2;
   }

   public RSAPublicKeyStructure(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         Enumeration var2 = var1.getObjects();
         this.modulus = ASN1Integer.getInstance(var2.nextElement()).getPositiveValue();
         this.publicExponent = ASN1Integer.getInstance(var2.nextElement()).getPositiveValue();
      }
   }

   public BigInteger getModulus() {
      return this.modulus;
   }

   public BigInteger getPublicExponent() {
      return this.publicExponent;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Integer(this.getModulus()), new ASN1Integer(this.getPublicExponent()));
   }
}
