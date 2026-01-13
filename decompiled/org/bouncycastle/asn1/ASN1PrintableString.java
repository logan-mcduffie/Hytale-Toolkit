package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public abstract class ASN1PrintableString extends ASN1Primitive implements ASN1String {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1PrintableString.class, 19) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return ASN1PrintableString.createPrimitive(var1.getOctets());
      }
   };
   final byte[] contents;

   public static ASN1PrintableString getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1PrintableString)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1PrintableString) {
               return (ASN1PrintableString)var1;
            }
         }

         if (var0 instanceof byte[]) {
            try {
               return (ASN1PrintableString)TYPE.fromByteArray((byte[])var0);
            } catch (Exception var2) {
               throw new IllegalArgumentException("encoding error in getInstance: " + var2.toString());
            }
         } else {
            throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
         }
      } else {
         return (ASN1PrintableString)var0;
      }
   }

   public static ASN1PrintableString getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1PrintableString)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1PrintableString getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1PrintableString)TYPE.getTagged(var0, var1);
   }

   ASN1PrintableString(String var1, boolean var2) {
      if (var2 && !isPrintableString(var1)) {
         throw new IllegalArgumentException("string contains illegal characters");
      } else {
         this.contents = Strings.toByteArray(var1);
      }
   }

   ASN1PrintableString(byte[] var1, boolean var2) {
      this.contents = var2 ? Arrays.clone(var1) : var1;
   }

   @Override
   public final String getString() {
      return Strings.fromByteArray(this.contents);
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
      var1.writeEncodingDL(var2, 19, this.contents);
   }

   @Override
   final boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1PrintableString)) {
         return false;
      } else {
         ASN1PrintableString var2 = (ASN1PrintableString)var1;
         return Arrays.areEqual(this.contents, var2.contents);
      }
   }

   @Override
   public final int hashCode() {
      return Arrays.hashCode(this.contents);
   }

   @Override
   public String toString() {
      return this.getString();
   }

   public static boolean isPrintableString(String var0) {
      for (int var1 = var0.length() - 1; var1 >= 0; var1--) {
         char var2 = var0.charAt(var1);
         if (var2 > 127) {
            return false;
         }

         if (('a' > var2 || var2 > 'z') && ('A' > var2 || var2 > 'Z') && ('0' > var2 || var2 > '9')) {
            switch (var2) {
               case ' ':
               case '\'':
               case '(':
               case ')':
               case '+':
               case ',':
               case '-':
               case '.':
               case '/':
               case ':':
               case '=':
               case '?':
                  break;
               case '!':
               case '"':
               case '#':
               case '$':
               case '%':
               case '&':
               case '*':
               case '0':
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
               case ';':
               case '<':
               case '>':
               default:
                  return false;
            }
         }
      }

      return true;
   }

   static ASN1PrintableString createPrimitive(byte[] var0) {
      return new DERPrintableString(var0, false);
   }
}
