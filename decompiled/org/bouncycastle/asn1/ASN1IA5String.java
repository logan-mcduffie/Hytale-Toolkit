package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public abstract class ASN1IA5String extends ASN1Primitive implements ASN1String {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1IA5String.class, 22) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return ASN1IA5String.createPrimitive(var1.getOctets());
      }
   };
   final byte[] contents;

   public static ASN1IA5String getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1IA5String)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1IA5String) {
               return (ASN1IA5String)var1;
            }
         }

         if (var0 instanceof byte[]) {
            try {
               return (ASN1IA5String)TYPE.fromByteArray((byte[])var0);
            } catch (Exception var2) {
               throw new IllegalArgumentException("encoding error in getInstance: " + var2.toString());
            }
         } else {
            throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
         }
      } else {
         return (ASN1IA5String)var0;
      }
   }

   public static ASN1IA5String getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1IA5String)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1IA5String getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1IA5String)TYPE.getTagged(var0, var1);
   }

   ASN1IA5String(String var1, boolean var2) {
      if (var1 == null) {
         throw new NullPointerException("'string' cannot be null");
      } else if (var2 && !isIA5String(var1)) {
         throw new IllegalArgumentException("'string' contains illegal characters");
      } else {
         this.contents = Strings.toByteArray(var1);
      }
   }

   ASN1IA5String(byte[] var1, boolean var2) {
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
      var1.writeEncodingDL(var2, 22, this.contents);
   }

   @Override
   final boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1IA5String)) {
         return false;
      } else {
         ASN1IA5String var2 = (ASN1IA5String)var1;
         return Arrays.areEqual(this.contents, var2.contents);
      }
   }

   @Override
   public final int hashCode() {
      return Arrays.hashCode(this.contents);
   }

   public static boolean isIA5String(String var0) {
      for (int var1 = var0.length() - 1; var1 >= 0; var1--) {
         char var2 = var0.charAt(var1);
         if (var2 > 127) {
            return false;
         }
      }

      return true;
   }

   static ASN1IA5String createPrimitive(byte[] var0) {
      return new DERIA5String(var0, false);
   }
}
