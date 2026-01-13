package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.BigIntegers;

public class Point256 extends ASN1Object {
   private final ASN1OctetString x;
   private final ASN1OctetString y;

   public Point256(ASN1OctetString var1, ASN1OctetString var2) {
      if (var1 == null || var1.getOctets().length != 32) {
         throw new IllegalArgumentException("x must be 32 bytes long");
      } else if (var2 != null && var2.getOctets().length == 32) {
         this.x = var1;
         this.y = var2;
      } else {
         throw new IllegalArgumentException("y must be 32 bytes long");
      }
   }

   private Point256(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.x = ASN1OctetString.getInstance(var1.getObjectAt(0));
         this.y = ASN1OctetString.getInstance(var1.getObjectAt(1));
         if (this.x.getOctets().length != 32) {
            throw new IllegalArgumentException("x must be 32 bytes long");
         } else if (this.y.getOctets().length != 32) {
            throw new IllegalArgumentException("y must be 32 bytes long");
         }
      }
   }

   public static Point256 getInstance(Object var0) {
      if (var0 instanceof Point256) {
         return (Point256)var0;
      } else {
         return var0 != null ? new Point256(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1OctetString getX() {
      return this.x;
   }

   public ASN1OctetString getY() {
      return this.y;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.x, this.y});
   }

   public static Point256.Builder builder() {
      return new Point256.Builder();
   }

   public static class Builder {
      private ASN1OctetString x;
      private ASN1OctetString y;

      public Point256.Builder setX(ASN1OctetString var1) {
         this.x = var1;
         return this;
      }

      public Point256.Builder setY(ASN1OctetString var1) {
         this.y = var1;
         return this;
      }

      public Point256.Builder setX(byte[] var1) {
         this.x = new DEROctetString(var1);
         return this;
      }

      public Point256.Builder setY(byte[] var1) {
         this.y = new DEROctetString(var1);
         return this;
      }

      public Point256.Builder setX(BigInteger var1) {
         return this.setX(BigIntegers.asUnsignedByteArray(32, var1));
      }

      public Point256.Builder setY(BigInteger var1) {
         return this.setY(BigIntegers.asUnsignedByteArray(32, var1));
      }

      public Point256 createPoint256() {
         return new Point256(this.x, this.y);
      }
   }
}
