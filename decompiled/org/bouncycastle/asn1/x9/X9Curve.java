package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Arrays;

public class X9Curve extends ASN1Object implements X9ObjectIdentifiers {
   private ECCurve curve;
   private byte[] seed;
   private ASN1ObjectIdentifier fieldIdentifier = null;

   public X9Curve(ECCurve var1) {
      this(var1, null);
   }

   public X9Curve(ECCurve var1, byte[] var2) {
      this.curve = var1;
      this.seed = Arrays.clone(var2);
      this.setFieldIdentifier();
   }

   public X9Curve(X9FieldID var1, BigInteger var2, BigInteger var3, ASN1Sequence var4) {
      this.fieldIdentifier = var1.getIdentifier();
      if (this.fieldIdentifier.equals(prime_field)) {
         BigInteger var5 = ((ASN1Integer)var1.getParameters()).getValue();
         BigInteger var6 = new BigInteger(1, ASN1OctetString.getInstance(var4.getObjectAt(0)).getOctets());
         BigInteger var7 = new BigInteger(1, ASN1OctetString.getInstance(var4.getObjectAt(1)).getOctets());
         this.curve = new ECCurve.Fp(var5, var6, var7, var2, var3);
      } else {
         if (!this.fieldIdentifier.equals(characteristic_two_field)) {
            throw new IllegalArgumentException("This type of ECCurve is not implemented");
         }

         ASN1Sequence var13 = ASN1Sequence.getInstance(var1.getParameters());
         int var14 = ((ASN1Integer)var13.getObjectAt(0)).intValueExact();
         ASN1ObjectIdentifier var15 = (ASN1ObjectIdentifier)var13.getObjectAt(1);
         int var8 = 0;
         int var9 = 0;
         int var10 = 0;
         if (var15.equals(tpBasis)) {
            var8 = ASN1Integer.getInstance(var13.getObjectAt(2)).intValueExact();
         } else {
            if (!var15.equals(ppBasis)) {
               throw new IllegalArgumentException("This type of EC basis is not implemented");
            }

            ASN1Sequence var11 = ASN1Sequence.getInstance(var13.getObjectAt(2));
            var8 = ASN1Integer.getInstance(var11.getObjectAt(0)).intValueExact();
            var9 = ASN1Integer.getInstance(var11.getObjectAt(1)).intValueExact();
            var10 = ASN1Integer.getInstance(var11.getObjectAt(2)).intValueExact();
         }

         BigInteger var17 = new BigInteger(1, ASN1OctetString.getInstance(var4.getObjectAt(0)).getOctets());
         BigInteger var12 = new BigInteger(1, ASN1OctetString.getInstance(var4.getObjectAt(1)).getOctets());
         this.curve = new ECCurve.F2m(var14, var8, var9, var10, var17, var12, var2, var3);
      }

      if (var4.size() == 3) {
         this.seed = ((DERBitString)var4.getObjectAt(2)).getBytes();
      }
   }

   private void setFieldIdentifier() {
      if (ECAlgorithms.isFpCurve(this.curve)) {
         this.fieldIdentifier = prime_field;
      } else {
         if (!ECAlgorithms.isF2mCurve(this.curve)) {
            throw new IllegalArgumentException("This type of ECCurve is not implemented");
         }

         this.fieldIdentifier = characteristic_two_field;
      }
   }

   public ECCurve getCurve() {
      return this.curve;
   }

   public byte[] getSeed() {
      return Arrays.clone(this.seed);
   }

   public boolean hasSeed() {
      return this.seed != null;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      if (this.fieldIdentifier.equals(prime_field)) {
         var1.add(new X9FieldElement(this.curve.getA()).toASN1Primitive());
         var1.add(new X9FieldElement(this.curve.getB()).toASN1Primitive());
      } else if (this.fieldIdentifier.equals(characteristic_two_field)) {
         var1.add(new X9FieldElement(this.curve.getA()).toASN1Primitive());
         var1.add(new X9FieldElement(this.curve.getB()).toASN1Primitive());
      }

      if (this.seed != null) {
         var1.add(new DERBitString(this.seed));
      }

      return new DERSequence(var1);
   }
}
