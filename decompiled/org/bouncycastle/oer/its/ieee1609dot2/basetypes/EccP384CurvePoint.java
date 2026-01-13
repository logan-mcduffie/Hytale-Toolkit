package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;

public class EccP384CurvePoint extends EccCurvePoint implements ASN1Choice {
   public static final int xonly = 0;
   public static final int fill = 1;
   public static final int compressedY0 = 2;
   public static final int compressedY1 = 3;
   public static final int uncompressedP384 = 4;
   private final int choice;
   private final ASN1Encodable eccP384CurvePoint;

   public EccP384CurvePoint(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.eccP384CurvePoint = var2;
   }

   private EccP384CurvePoint(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (var1.getTagNo()) {
         case 0:
         case 2:
         case 3:
            this.eccP384CurvePoint = ASN1OctetString.getInstance(var1.getExplicitBaseObject());
            break;
         case 1:
            this.eccP384CurvePoint = ASN1Null.getInstance(var1.getExplicitBaseObject());
            break;
         case 4:
            this.eccP384CurvePoint = ASN1Sequence.getInstance(var1.getExplicitBaseObject());
            break;
         default:
            throw new IllegalArgumentException("invalid choice value " + var1.getTagNo());
      }
   }

   public static EccP384CurvePoint getInstance(Object var0) {
      if (var0 instanceof EccP384CurvePoint) {
         return (EccP384CurvePoint)var0;
      } else {
         return var0 != null ? new EccP384CurvePoint(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public static EccP384CurvePoint xOnly(ASN1OctetString var0) {
      return new EccP384CurvePoint(0, var0);
   }

   public static EccP384CurvePoint xOnly(byte[] var0) {
      return new EccP384CurvePoint(0, new DEROctetString(Arrays.clone(var0)));
   }

   public static EccP384CurvePoint fill() {
      return new EccP384CurvePoint(1, DERNull.INSTANCE);
   }

   public static EccP384CurvePoint compressedY0(ASN1OctetString var0) {
      return new EccP384CurvePoint(2, var0);
   }

   public static EccP384CurvePoint compressedY1(ASN1OctetString var0) {
      return new EccP384CurvePoint(3, var0);
   }

   public static EccP384CurvePoint compressedY0(byte[] var0) {
      return new EccP384CurvePoint(2, new DEROctetString(Arrays.clone(var0)));
   }

   public static EccP384CurvePoint compressedY1(byte[] var0) {
      return new EccP384CurvePoint(3, new DEROctetString(Arrays.clone(var0)));
   }

   public static EccP384CurvePoint uncompressedP384(Point384 var0) {
      return new EccP384CurvePoint(4, var0);
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getEccP384CurvePoint() {
      return this.eccP384CurvePoint;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.eccP384CurvePoint);
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
            byte[] var6 = DEROctetString.getInstance(this.eccP384CurvePoint).getOctets();
            var1 = new byte[var6.length + 1];
            var1[0] = 2;
            System.arraycopy(var6, 0, var1, 1, var6.length);
            break;
         case 3:
            byte[] var5 = DEROctetString.getInstance(this.eccP384CurvePoint).getOctets();
            var1 = new byte[var5.length + 1];
            var1[0] = 3;
            System.arraycopy(var5, 0, var1, 1, var5.length);
            break;
         case 4:
            ASN1Sequence var2 = ASN1Sequence.getInstance(this.eccP384CurvePoint);
            byte[] var3 = DEROctetString.getInstance(var2.getObjectAt(0)).getOctets();
            byte[] var4 = DEROctetString.getInstance(var2.getObjectAt(1)).getOctets();
            var1 = Arrays.concatenate(new byte[]{4}, var3, var4);
      }

      return var1;
   }
}
