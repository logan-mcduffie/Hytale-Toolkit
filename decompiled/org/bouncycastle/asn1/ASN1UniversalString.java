package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;

public abstract class ASN1UniversalString extends ASN1Primitive implements ASN1String {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1UniversalString.class, 28) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return ASN1UniversalString.createPrimitive(var1.getOctets());
      }
   };
   private static final char[] table = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
   final byte[] contents;

   public static ASN1UniversalString getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1UniversalString)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1UniversalString) {
               return (ASN1UniversalString)var1;
            }
         }

         if (var0 instanceof byte[]) {
            try {
               return (ASN1UniversalString)TYPE.fromByteArray((byte[])var0);
            } catch (Exception var2) {
               throw new IllegalArgumentException("encoding error getInstance: " + var2.toString());
            }
         } else {
            throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
         }
      } else {
         return (ASN1UniversalString)var0;
      }
   }

   public static ASN1UniversalString getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1UniversalString)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1UniversalString getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1UniversalString)TYPE.getTagged(var0, var1);
   }

   ASN1UniversalString(byte[] var1, boolean var2) {
      this.contents = var2 ? Arrays.clone(var1) : var1;
   }

   @Override
   public final String getString() {
      int var1 = this.contents.length;
      StringBuilder var2 = new StringBuilder(3 + 2 * (ASN1OutputStream.getLengthOfDL(var1) + var1));
      var2.append("#1C");
      encodeHexDL(var2, var1);

      for (int var3 = 0; var3 < var1; var3++) {
         encodeHexByte(var2, this.contents[var3]);
      }

      return var2.toString();
   }

   @Override
   public String toString() {
      return this.getString();
   }

   public final byte[] getOctets() {
      return Arrays.clone(this.contents);
   }

   @Override
   final boolean encodeConstructed() {
      return false;
   }

   @Override
   final int encodedLength(boolean var1) {
      return ASN1OutputStream.getLengthOfEncodingDL(var1, this.contents.length);
   }

   @Override
   final void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      var1.writeEncodingDL(var2, 28, this.contents);
   }

   @Override
   final boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1UniversalString)) {
         return false;
      } else {
         ASN1UniversalString var2 = (ASN1UniversalString)var1;
         return Arrays.areEqual(this.contents, var2.contents);
      }
   }

   @Override
   public final int hashCode() {
      return Arrays.hashCode(this.contents);
   }

   static ASN1UniversalString createPrimitive(byte[] var0) {
      return new DERUniversalString(var0, false);
   }

   private static void encodeHexByte(StringBuilder var0, int var1) {
      var0.append(table[var1 >>> 4 & 15]);
      var0.append(table[var1 & 15]);
   }

   private static void encodeHexDL(StringBuilder var0, int var1) {
      if (var1 < 128) {
         encodeHexByte(var0, var1);
      } else {
         byte[] var2 = new byte[5];
         int var3 = 5;

         do {
            var2[--var3] = (byte)var1;
            var1 >>>= 8;
         } while (var1 != 0);

         int var4 = var2.length - var3;
         var2[--var3] = (byte)(128 | var4);

         do {
            encodeHexByte(var0, var2[var3++]);
         } while (var3 < var2.length);
      }
   }
}
