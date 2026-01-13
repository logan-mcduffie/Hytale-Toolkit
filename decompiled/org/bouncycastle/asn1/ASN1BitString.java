package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Arrays;

public abstract class ASN1BitString extends ASN1Primitive implements ASN1String, ASN1BitStringParser {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1BitString.class, 3) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return ASN1BitString.createPrimitive(var1.getOctets());
      }

      @Override
      ASN1Primitive fromImplicitConstructed(ASN1Sequence var1) {
         return var1.toASN1BitString();
      }
   };
   private static final char[] table = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
   final byte[] contents;

   public static ASN1BitString getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1BitString)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1BitString) {
               return (ASN1BitString)var1;
            }
         } else if (var0 instanceof byte[]) {
            try {
               return (ASN1BitString)TYPE.fromByteArray((byte[])var0);
            } catch (IOException var2) {
               throw new IllegalArgumentException("failed to construct BIT STRING from byte[]: " + var2.getMessage());
            }
         }

         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      } else {
         return (ASN1BitString)var0;
      }
   }

   public static ASN1BitString getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1BitString)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1BitString getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1BitString)TYPE.getTagged(var0, var1);
   }

   protected static int getPadBits(int var0) {
      int var1 = 0;

      for (int var2 = 3; var2 >= 0; var2--) {
         if (var2 != 0) {
            if (var0 >> var2 * 8 != 0) {
               var1 = var0 >> var2 * 8 & 0xFF;
               break;
            }
         } else if (var0 != 0) {
            var1 = var0 & 0xFF;
            break;
         }
      }

      if (var1 == 0) {
         return 0;
      } else {
         int var3 = 1;

         while (((var1 <<= 1) & 0xFF) != 0) {
            var3++;
         }

         return 8 - var3;
      }
   }

   protected static byte[] getBytes(int var0) {
      if (var0 == 0) {
         return new byte[0];
      } else {
         int var1 = 4;

         for (int var2 = 3; var2 >= 1 && (var0 & 255 << var2 * 8) == 0; var2--) {
            var1--;
         }

         byte[] var4 = new byte[var1];

         for (int var3 = 0; var3 < var1; var3++) {
            var4[var3] = (byte)(var0 >> var3 * 8 & 0xFF);
         }

         return var4;
      }
   }

   ASN1BitString(byte var1, int var2) {
      if (var2 <= 7 && var2 >= 0) {
         this.contents = new byte[]{(byte)var2, var1};
      } else {
         throw new IllegalArgumentException("pad bits cannot be greater than 7 or less than 0");
      }
   }

   ASN1BitString(byte[] var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException("'data' cannot be null");
      } else if (var1.length == 0 && var2 != 0) {
         throw new IllegalArgumentException("zero length data with non-zero pad bits");
      } else if (var2 <= 7 && var2 >= 0) {
         this.contents = Arrays.prepend(var1, (byte)var2);
      } else {
         throw new IllegalArgumentException("pad bits cannot be greater than 7 or less than 0");
      }
   }

   ASN1BitString(byte[] var1, boolean var2) {
      if (var2) {
         if (null == var1) {
            throw new NullPointerException("'contents' cannot be null");
         }

         if (var1.length < 1) {
            throw new IllegalArgumentException("'contents' cannot be empty");
         }

         int var3 = var1[0] & 255;
         if (var3 > 0) {
            if (var1.length < 2) {
               throw new IllegalArgumentException("zero length data with non-zero pad bits");
            }

            if (var3 > 7) {
               throw new IllegalArgumentException("pad bits cannot be greater than 7 or less than 0");
            }
         }
      }

      this.contents = var1;
   }

   @Override
   public InputStream getBitStream() throws IOException {
      return new ByteArrayInputStream(this.contents, 1, this.contents.length - 1);
   }

   @Override
   public InputStream getOctetStream() throws IOException {
      int var1 = this.contents[0] & 255;
      if (0 != var1) {
         throw new IOException("expected octet-aligned bitstring, but found padBits: " + var1);
      } else {
         return this.getBitStream();
      }
   }

   public ASN1BitStringParser parser() {
      return this;
   }

   @Override
   public String getString() {
      byte[] var1;
      try {
         var1 = this.getEncoded();
      } catch (IOException var5) {
         throw new ASN1ParsingException("Internal error encoding BitString: " + var5.getMessage(), var5);
      }

      StringBuilder var2 = new StringBuilder(1 + var1.length * 2);
      var2.append('#');

      for (int var3 = 0; var3 != var1.length; var3++) {
         byte var4 = var1[var3];
         var2.append(table[var4 >>> 4 & 15]);
         var2.append(table[var4 & 15]);
      }

      return var2.toString();
   }

   public int intValue() {
      int var1 = 0;
      int var2 = Math.min(5, this.contents.length - 1);

      for (int var3 = 1; var3 < var2; var3++) {
         var1 |= (this.contents[var3] & 255) << 8 * (var3 - 1);
      }

      if (1 <= var2 && var2 < 5) {
         int var5 = this.contents[0] & 255;
         byte var4 = (byte)(this.contents[var2] & 255 << var5);
         var1 |= (var4 & 255) << 8 * (var2 - 1);
      }

      return var1;
   }

   public byte[] getOctets() {
      if (this.contents[0] != 0) {
         throw new IllegalStateException("attempt to get non-octet aligned data from BIT STRING");
      } else {
         return Arrays.copyOfRange(this.contents, 1, this.contents.length);
      }
   }

   public byte[] getBytes() {
      if (this.contents.length == 1) {
         return ASN1OctetString.EMPTY_OCTETS;
      } else {
         int var1 = this.contents[0] & 255;
         byte[] var2 = Arrays.copyOfRange(this.contents, 1, this.contents.length);
         var2[var2.length - 1] = (byte)(var2[var2.length - 1] & (byte)(255 << var1));
         return var2;
      }
   }

   public int getBytesLength() {
      return this.contents.length - 1;
   }

   public boolean isOctetAligned() {
      return this.getPadBits() == 0;
   }

   @Override
   public int getPadBits() {
      return this.contents[0] & 0xFF;
   }

   @Override
   public String toString() {
      return this.getString();
   }

   @Override
   public int hashCode() {
      if (this.contents.length < 2) {
         return 1;
      } else {
         int var1 = this.contents[0] & 255;
         int var2 = this.contents.length - 1;
         byte var3 = (byte)(this.contents[var2] & 255 << var1);
         int var4 = Arrays.hashCode(this.contents, 0, var2);
         var4 *= 257;
         return var4 ^ var3;
      }
   }

   @Override
   boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1BitString)) {
         return false;
      } else {
         ASN1BitString var2 = (ASN1BitString)var1;
         byte[] var3 = this.contents;
         byte[] var4 = var2.contents;
         int var5 = var3.length;
         if (var4.length != var5) {
            return false;
         } else if (var5 == 1) {
            return true;
         } else {
            int var6 = var5 - 1;

            for (int var7 = 0; var7 < var6; var7++) {
               if (var3[var7] != var4[var7]) {
                  return false;
               }
            }

            int var10 = var3[0] & 255;
            byte var8 = (byte)(var3[var6] & 255 << var10);
            byte var9 = (byte)(var4[var6] & 255 << var10);
            return var8 == var9;
         }
      }
   }

   @Override
   public ASN1Primitive getLoadedObject() {
      return this.toASN1Primitive();
   }

   @Override
   ASN1Primitive toDERObject() {
      return new DERBitString(this.contents, false);
   }

   @Override
   ASN1Primitive toDLObject() {
      return new DLBitString(this.contents, false);
   }

   static ASN1BitString createPrimitive(byte[] var0) {
      int var1 = var0.length;
      if (var1 < 1) {
         throw new IllegalArgumentException("truncated BIT STRING detected");
      } else {
         int var2 = var0[0] & 255;
         if (var2 > 0) {
            if (var2 > 7 || var1 < 2) {
               throw new IllegalArgumentException("invalid pad bits detected");
            }

            byte var3 = var0[var1 - 1];
            if (var3 != (byte)(var3 & 255 << var2)) {
               return new DLBitString(var0, false);
            }
         }

         return new DERBitString(var0, false);
      }
   }
}
