package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public abstract class ASN1UTF8String extends ASN1Primitive implements ASN1String {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1UTF8String.class, 12) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return ASN1UTF8String.createPrimitive(var1.getOctets());
      }
   };
   final byte[] contents;

   public static ASN1UTF8String getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1UTF8String)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1UTF8String) {
               return (ASN1UTF8String)var1;
            }
         }

         if (var0 instanceof byte[]) {
            try {
               return (ASN1UTF8String)TYPE.fromByteArray((byte[])var0);
            } catch (Exception var2) {
               throw new IllegalArgumentException("encoding error in getInstance: " + var2.toString());
            }
         } else {
            throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
         }
      } else {
         return (ASN1UTF8String)var0;
      }
   }

   public static ASN1UTF8String getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1UTF8String)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1UTF8String getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1UTF8String)TYPE.getTagged(var0, var1);
   }

   ASN1UTF8String(String var1) {
      this(Strings.toUTF8ByteArray(var1), false);
   }

   ASN1UTF8String(byte[] var1, boolean var2) {
      this.contents = var2 ? Arrays.clone(var1) : var1;
   }

   @Override
   public final String getString() {
      return Strings.fromUTF8ByteArray(this.contents);
   }

   @Override
   public String toString() {
      return this.getString();
   }

   @Override
   public final int hashCode() {
      return Arrays.hashCode(this.contents);
   }

   @Override
   final boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1UTF8String)) {
         return false;
      } else {
         ASN1UTF8String var2 = (ASN1UTF8String)var1;
         return Arrays.areEqual(this.contents, var2.contents);
      }
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
      var1.writeEncodingDL(var2, 12, this.contents);
   }

   static ASN1UTF8String createPrimitive(byte[] var0) {
      return new DERUTF8String(var0, false);
   }
}
