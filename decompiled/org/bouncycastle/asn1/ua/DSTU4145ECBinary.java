package org.bouncycastle.asn1.ua;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

public class DSTU4145ECBinary extends ASN1Object {
   BigInteger version = BigInteger.valueOf(0L);
   DSTU4145BinaryField f;
   ASN1Integer a;
   ASN1OctetString b;
   ASN1Integer n;
   ASN1OctetString bp;

   public DSTU4145ECBinary(ECDomainParameters var1) {
      ECCurve var2 = var1.getCurve();
      if (!ECAlgorithms.isF2mCurve(var2)) {
         throw new IllegalArgumentException("only binary domain is possible");
      } else {
         PolynomialExtensionField var3 = (PolynomialExtensionField)var2.getField();
         int[] var4 = var3.getMinimalPolynomial().getExponentsPresent();
         if (var4.length == 3) {
            this.f = new DSTU4145BinaryField(var4[2], var4[1]);
         } else {
            if (var4.length != 5) {
               throw new IllegalArgumentException("curve must have a trinomial or pentanomial basis");
            }

            this.f = new DSTU4145BinaryField(var4[4], var4[1], var4[2], var4[3]);
         }

         this.a = new ASN1Integer(var2.getA().toBigInteger());
         this.b = new DEROctetString(var2.getB().getEncoded());
         this.n = new ASN1Integer(var1.getN());
         this.bp = new DEROctetString(DSTU4145PointEncoder.encodePoint(var1.getG()));
      }
   }

   private DSTU4145ECBinary(ASN1Sequence var1) {
      int var2 = 0;
      if (var1.getObjectAt(var2) instanceof ASN1TaggedObject) {
         ASN1TaggedObject var3 = (ASN1TaggedObject)var1.getObjectAt(var2);
         if (!var3.isExplicit() || 0 != var3.getTagNo()) {
            throw new IllegalArgumentException("object parse error");
         }

         this.version = ASN1Integer.getInstance(var3.getLoadedObject()).getValue();
         var2++;
      }

      this.f = DSTU4145BinaryField.getInstance(var1.getObjectAt(var2));
      this.a = ASN1Integer.getInstance(var1.getObjectAt(++var2));
      this.b = ASN1OctetString.getInstance(var1.getObjectAt(++var2));
      this.n = ASN1Integer.getInstance(var1.getObjectAt(++var2));
      this.bp = ASN1OctetString.getInstance(var1.getObjectAt(++var2));
   }

   public static DSTU4145ECBinary getInstance(Object var0) {
      if (var0 instanceof DSTU4145ECBinary) {
         return (DSTU4145ECBinary)var0;
      } else {
         return var0 != null ? new DSTU4145ECBinary(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public DSTU4145BinaryField getField() {
      return this.f;
   }

   public BigInteger getA() {
      return this.a.getValue();
   }

   public byte[] getB() {
      return Arrays.clone(this.b.getOctets());
   }

   public BigInteger getN() {
      return this.n.getValue();
   }

   public byte[] getG() {
      return Arrays.clone(this.bp.getOctets());
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(6);
      if (0 != this.version.compareTo(BigInteger.valueOf(0L))) {
         var1.add(new DERTaggedObject(true, 0, new ASN1Integer(this.version)));
      }

      var1.add(this.f);
      var1.add(this.a);
      var1.add(this.b);
      var1.add(this.n);
      var1.add(this.bp);
      return new DERSequence(var1);
   }
}
