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

public class Point384 extends ASN1Object {
   private final ASN1OctetString x;
   private final ASN1OctetString y;

   public Point384(ASN1OctetString var1, ASN1OctetString var2) {
      if (var1.getOctets().length != 48) {
         throw new IllegalArgumentException("x must be 48 bytes long");
      } else if (var2.getOctets().length != 48) {
         throw new IllegalArgumentException("y must be 48 bytes long");
      } else {
         this.x = var1;
         this.y = var2;
      }
   }

   private Point384(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.x = ASN1OctetString.getInstance(var1.getObjectAt(0));
         this.y = ASN1OctetString.getInstance(var1.getObjectAt(1));
         if (this.x.getOctets().length != 48) {
            throw new IllegalArgumentException("x must be 48 bytes long");
         } else if (this.y.getOctets().length != 48) {
            throw new IllegalArgumentException("y must be 48 bytes long");
         }
      }
   }

   public static Point384 getInstance(Object var0) {
      if (var0 instanceof Point384) {
         return (Point384)var0;
      } else {
         return var0 != null ? new Point384(ASN1Sequence.getInstance(var0)) : null;
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

   public static Point384.Builder builder() {
      return new Point384.Builder();
   }

   public static class Builder {
      private ASN1OctetString x;
      private ASN1OctetString y;

      public Point384.Builder setX(ASN1OctetString var1) {
         this.x = var1;
         return this;
      }

      public Point384.Builder setX(byte[] var1) {
         this.x = new DEROctetString(var1);
         return this;
      }

      public Point384.Builder setX(BigInteger var1) {
         this.x = new DEROctetString(BigIntegers.asUnsignedByteArray(48, var1));
         return this;
      }

      public Point384.Builder setY(ASN1OctetString var1) {
         this.y = var1;
         return this;
      }

      public Point384.Builder setY(byte[] var1) {
         this.y = new DEROctetString(var1);
         return this;
      }

      public Point384.Builder setY(BigInteger var1) {
         this.y = new DEROctetString(BigIntegers.asUnsignedByteArray(48, var1));
         return this;
      }

      public Point384 createPoint384() {
         return new Point384(this.x, this.y);
      }
   }
}
