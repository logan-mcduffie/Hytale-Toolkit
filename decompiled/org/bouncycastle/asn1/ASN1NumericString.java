package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public abstract class ASN1NumericString extends ASN1Primitive implements ASN1String {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1NumericString.class, 18) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return ASN1NumericString.createPrimitive(var1.getOctets());
      }
   };
   final byte[] contents;

   public static ASN1NumericString getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1NumericString)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1NumericString) {
               return (ASN1NumericString)var1;
            }
         }

         if (var0 instanceof byte[]) {
            try {
               return (ASN1NumericString)TYPE.fromByteArray((byte[])var0);
            } catch (Exception var2) {
               throw new IllegalArgumentException("encoding error in getInstance: " + var2.toString());
            }
         } else {
            throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
         }
      } else {
         return (ASN1NumericString)var0;
      }
   }

   public static ASN1NumericString getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1NumericString)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1NumericString getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1NumericString)TYPE.getTagged(var0, var1);
   }

   ASN1NumericString(String var1, boolean var2) {
      if (var2 && !isNumericString(var1)) {
         throw new IllegalArgumentException("string contains illegal characters");
      } else {
         this.contents = Strings.toByteArray(var1);
      }
   }

   ASN1NumericString(byte[] var1, boolean var2) {
      this.contents = var2 ? Arrays.clone(var1) : var1;
   }

   @Override
   public final String getString() {
      return Strings.fromByteArray(this.contents);
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
      var1.writeEncodingDL(var2, 18, this.contents);
   }

   @Override
   public final int hashCode() {
      return Arrays.hashCode(this.contents);
   }

   @Override
   final boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1NumericString)) {
         return false;
      } else {
         ASN1NumericString var2 = (ASN1NumericString)var1;
         return Arrays.areEqual(this.contents, var2.contents);
      }
   }

   public static boolean isNumericString(String var0) {
      for (int var1 = var0.length() - 1; var1 >= 0; var1--) {
         char var2 = var0.charAt(var1);
         if (var2 > 127) {
            return false;
         }

         if (('0' > var2 || var2 > '9') && var2 != ' ') {
            return false;
         }
      }

      return true;
   }

   static boolean isNumericString(byte[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         switch (var0[var1]) {
            case 32:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            default:
               return false;
         }
      }

      return true;
   }

   static ASN1NumericString createPrimitive(byte[] var0) {
      return new DERNumericString(var0, false);
   }
}
