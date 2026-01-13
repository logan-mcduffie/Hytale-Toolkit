package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;

public abstract class ASN1BMPString extends ASN1Primitive implements ASN1String {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1BMPString.class, 30) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return ASN1BMPString.createPrimitive(var1.getOctets());
      }
   };
   final char[] string;

   public static ASN1BMPString getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1BMPString)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1BMPString) {
               return (ASN1BMPString)var1;
            }
         }

         if (var0 instanceof byte[]) {
            try {
               return (ASN1BMPString)TYPE.fromByteArray((byte[])var0);
            } catch (Exception var2) {
               throw new IllegalArgumentException("encoding error in getInstance: " + var2.toString());
            }
         } else {
            throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
         }
      } else {
         return (ASN1BMPString)var0;
      }
   }

   public static ASN1BMPString getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1BMPString)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1BMPString getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1BMPString)TYPE.getTagged(var0, var1);
   }

   ASN1BMPString(String var1) {
      if (var1 == null) {
         throw new NullPointerException("'string' cannot be null");
      } else {
         this.string = var1.toCharArray();
      }
   }

   ASN1BMPString(byte[] var1) {
      if (var1 == null) {
         throw new NullPointerException("'string' cannot be null");
      } else {
         int var2 = var1.length;
         if (0 != (var2 & 1)) {
            throw new IllegalArgumentException("malformed BMPString encoding encountered");
         } else {
            int var3 = var2 / 2;
            char[] var4 = new char[var3];

            for (int var5 = 0; var5 != var3; var5++) {
               var4[var5] = (char)(var1[2 * var5] << 8 | var1[2 * var5 + 1] & 255);
            }

            this.string = var4;
         }
      }
   }

   ASN1BMPString(char[] var1) {
      if (var1 == null) {
         throw new NullPointerException("'string' cannot be null");
      } else {
         this.string = var1;
      }
   }

   @Override
   public final String getString() {
      return new String(this.string);
   }

   @Override
   public String toString() {
      return this.getString();
   }

   @Override
   final boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1BMPString)) {
         return false;
      } else {
         ASN1BMPString var2 = (ASN1BMPString)var1;
         return Arrays.areEqual(this.string, var2.string);
      }
   }

   @Override
   public final int hashCode() {
      return Arrays.hashCode(this.string);
   }

   @Override
   final boolean encodeConstructed() {
      return false;
   }

   @Override
   final int encodedLength(boolean var1) {
      return ASN1OutputStream.getLengthOfEncodingDL(var1, this.string.length * 2);
   }

   @Override
   final void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      int var3 = this.string.length;
      var1.writeIdentifier(var2, 30);
      var1.writeDL(var3 * 2);
      byte[] var4 = new byte[8];
      int var5 = 0;
      int var6 = var3 & -4;

      while (var5 < var6) {
         char var7 = this.string[var5];
         char var8 = this.string[var5 + 1];
         char var9 = this.string[var5 + 2];
         char var10 = this.string[var5 + 3];
         var5 += 4;
         var4[0] = (byte)(var7 >> '\b');
         var4[1] = (byte)var7;
         var4[2] = (byte)(var8 >> '\b');
         var4[3] = (byte)var8;
         var4[4] = (byte)(var9 >> '\b');
         var4[5] = (byte)var9;
         var4[6] = (byte)(var10 >> '\b');
         var4[7] = (byte)var10;
         var1.write(var4, 0, 8);
      }

      if (var5 < var3) {
         int var11 = 0;

         do {
            char var13 = this.string[var5];
            var5++;
            var4[var11++] = (byte)(var13 >> '\b');
            var4[var11++] = (byte)var13;
         } while (var5 < var3);

         var1.write(var4, 0, var11);
      }
   }

   static ASN1BMPString createPrimitive(byte[] var0) {
      return new DERBMPString(var0);
   }

   static ASN1BMPString createPrimitive(char[] var0) {
      return new DERBMPString(var0);
   }
}
