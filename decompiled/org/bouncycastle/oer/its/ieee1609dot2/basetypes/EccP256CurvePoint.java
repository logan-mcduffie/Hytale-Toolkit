package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

public class EccP256CurvePoint extends EccCurvePoint implements ASN1Choice {
   public static final int xonly = 0;
   public static final int fill = 1;
   public static final int compressedY0 = 2;
   public static final int compressedY1 = 3;
   public static final int uncompressedP256 = 4;
   private final int choice;
   private final ASN1Encodable eccp256CurvePoint;

   public EccP256CurvePoint(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.eccp256CurvePoint = var2;
   }

   private EccP256CurvePoint(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (var1.getTagNo()) {
         case 0:
         case 2:
         case 3:
            this.eccp256CurvePoint = ASN1OctetString.getInstance(var1.getExplicitBaseObject());
            break;
         case 1:
            this.eccp256CurvePoint = ASN1Null.getInstance(var1.getExplicitBaseObject());
            break;
         case 4:
            this.eccp256CurvePoint = Point256.getInstance(var1.getExplicitBaseObject());
            break;
         default:
            throw new IllegalArgumentException("invalid choice value " + var1.getTagNo());
      }
   }

   public static EccP256CurvePoint xOnly(ASN1OctetString var0) {
      return new EccP256CurvePoint(0, var0);
   }

   public static EccP256CurvePoint xOnly(byte[] var0) {
      return new EccP256CurvePoint(0, new DEROctetString(Arrays.clone(var0)));
   }

   public static EccP256CurvePoint fill() {
      return new EccP256CurvePoint(1, DERNull.INSTANCE);
   }

   public static EccP256CurvePoint compressedY0(ASN1OctetString var0) {
      return new EccP256CurvePoint(2, var0);
   }

   public static EccP256CurvePoint compressedY1(ASN1OctetString var0) {
      return new EccP256CurvePoint(3, var0);
   }

   public static EccP256CurvePoint compressedY0(byte[] var0) {
      return new EccP256CurvePoint(2, new DEROctetString(Arrays.clone(var0)));
   }

   public static EccP256CurvePoint compressedY1(byte[] var0) {
      return new EccP256CurvePoint(3, new DEROctetString(Arrays.clone(var0)));
   }

   public static EccP256CurvePoint uncompressedP256(Point256 var0) {
      return new EccP256CurvePoint(4, var0);
   }

   public static EccP256CurvePoint uncompressedP256(BigInteger var0, BigInteger var1) {
      return new EccP256CurvePoint(4, Point256.builder().setX(var0).setY(var1).createPoint256());
   }

   public static EccP256CurvePoint createEncodedPoint(byte[] var0) {
      if (var0[0] == 2) {
         byte[] var2 = new byte[var0.length - 1];
         System.arraycopy(var0, 1, var2, 0, var2.length);
         return new EccP256CurvePoint(2, new DEROctetString(var2));
      } else if (var0[0] == 3) {
         byte[] var1 = new byte[var0.length - 1];
         System.arraycopy(var0, 1, var1, 0, var1.length);
         return new EccP256CurvePoint(3, new DEROctetString(var1));
      } else if (var0[0] == 4) {
         return new EccP256CurvePoint(
            4, new Point256(new DEROctetString(Arrays.copyOfRange(var0, 1, 34)), new DEROctetString(Arrays.copyOfRange(var0, 34, 66)))
         );
      } else {
         throw new IllegalArgumentException("unrecognised encoding " + var0[0]);
      }
   }

   public EccP256CurvePoint createCompressed(ECPoint var1) {
      byte var2 = 0;
      byte[] var3 = var1.getEncoded(true);
      if (var3[0] == 2) {
         var2 = 2;
      } else if (var3[0] == 3) {
         var2 = 3;
      }

      byte[] var4 = new byte[var3.length - 1];
      System.arraycopy(var3, 1, var4, 0, var4.length);
      return new EccP256CurvePoint(var2, new DEROctetString(var4));
   }

   public static EccP256CurvePoint getInstance(Object var0) {
      if (var0 instanceof EccP256CurvePoint) {
         return (EccP256CurvePoint)var0;
      } else {
         return var0 != null ? new EccP256CurvePoint(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public ASN1Encodable getEccp256CurvePoint() {
      return this.eccp256CurvePoint;
   }

   public int getChoice() {
      return this.choice;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.eccp256CurvePoint);
   }

   @Override
   public byte[] getEncodedPoint() {
      byte[] var1;
      switch (this.choice) {
         case 0:
            throw new IllegalStateException("x Only not implemented");
         case 1:
         default:
            throw new IllegalStateException("unknown point choice");
         case 2:
            byte[] var6 = DEROctetString.getInstance(this.eccp256CurvePoint).getOctets();
            var1 = new byte[var6.length + 1];
            var1[0] = 2;
            System.arraycopy(var6, 0, var1, 1, var6.length);
            break;
         case 3:
            byte[] var5 = DEROctetString.getInstance(this.eccp256CurvePoint).getOctets();
            var1 = new byte[var5.length + 1];
            var1[0] = 3;
            System.arraycopy(var5, 0, var1, 1, var5.length);
            break;
         case 4:
            Point256 var2 = Point256.getInstance(this.eccp256CurvePoint);
            byte[] var3 = var2.getX().getOctets();
            byte[] var4 = var2.getY().getOctets();
            var1 = Arrays.concatenate(new byte[]{4}, var3, var4);
      }

      return var1;
   }
}
