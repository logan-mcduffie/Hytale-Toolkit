package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.PolynomialExtensionField;

public class X9ECParameters extends ASN1Object implements X9ObjectIdentifiers {
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private X9FieldID fieldID;
   private X9Curve curve;
   private X9ECPoint g;
   private BigInteger n;
   private BigInteger h;

   private X9ECParameters(ASN1Sequence var1) {
      if (var1.getObjectAt(0) instanceof ASN1Integer && ((ASN1Integer)var1.getObjectAt(0)).hasValue(1)) {
         this.n = ((ASN1Integer)var1.getObjectAt(4)).getValue();
         if (var1.size() == 6) {
            this.h = ((ASN1Integer)var1.getObjectAt(5)).getValue();
         }

         this.fieldID = X9FieldID.getInstance(var1.getObjectAt(1));
         this.curve = new X9Curve(this.fieldID, this.n, this.h, ASN1Sequence.getInstance(var1.getObjectAt(2)));
         ASN1Encodable var2 = var1.getObjectAt(3);
         if (var2 instanceof X9ECPoint) {
            this.g = (X9ECPoint)var2;
         } else {
            this.g = new X9ECPoint(this.curve.getCurve(), (ASN1OctetString)var2);
         }
      } else {
         throw new IllegalArgumentException("bad version in X9ECParameters");
      }
   }

   public static X9ECParameters getInstance(Object var0) {
      if (var0 instanceof X9ECParameters) {
         return (X9ECParameters)var0;
      } else {
         return var0 != null ? new X9ECParameters(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public X9ECParameters(ECCurve var1, X9ECPoint var2, BigInteger var3) {
      this(var1, var2, var3, null, null);
   }

   public X9ECParameters(ECCurve var1, X9ECPoint var2, BigInteger var3, BigInteger var4) {
      this(var1, var2, var3, var4, null);
   }

   public X9ECParameters(ECCurve var1, X9ECPoint var2, BigInteger var3, BigInteger var4, byte[] var5) {
      this.curve = new X9Curve(var1, var5);
      this.g = var2;
      this.n = var3;
      this.h = var4;
      FiniteField var6 = var1.getField();
      if (ECAlgorithms.isFpField(var6)) {
         this.fieldID = new X9FieldID(var6.getCharacteristic());
      } else {
         if (!ECAlgorithms.isF2mField(var6)) {
            throw new IllegalArgumentException("'curve' is of an unsupported type");
         }

         PolynomialExtensionField var7 = (PolynomialExtensionField)var6;
         int[] var8 = var7.getMinimalPolynomial().getExponentsPresent();
         if (var8.length == 3) {
            this.fieldID = new X9FieldID(var8[2], var8[1]);
         } else {
            if (var8.length != 5) {
               throw new IllegalArgumentException("Only trinomial and pentomial curves are supported");
            }

            this.fieldID = new X9FieldID(var8[4], var8[1], var8[2], var8[3]);
         }
      }
   }

   public ECCurve getCurve() {
      return this.curve.getCurve();
   }

   public ECPoint getG() {
      return this.g.getPoint();
   }

   public BigInteger getN() {
      return this.n;
   }

   public BigInteger getH() {
      return this.h;
   }

   public byte[] getSeed() {
      return this.curve.getSeed();
   }

   public boolean hasSeed() {
      return this.curve.hasSeed();
   }

   public X9Curve getCurveEntry() {
      return this.curve;
   }

   public X9FieldID getFieldIDEntry() {
      return this.fieldID;
   }

   public X9ECPoint getBaseEntry() {
      return this.g;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(6);
      var1.add(new ASN1Integer(ONE));
      var1.add(this.fieldID);
      var1.add(this.curve);
      var1.add(this.g);
      var1.add(new ASN1Integer(this.n));
      if (this.h != null) {
         var1.add(new ASN1Integer(this.h));
      }

      return new DERSequence(var1);
   }
}
