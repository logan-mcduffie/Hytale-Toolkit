package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public abstract class ASN1VideotexString extends ASN1Primitive implements ASN1String {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1VideotexString.class, 21) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return ASN1VideotexString.createPrimitive(var1.getOctets());
      }
   };
   final byte[] contents;

   public static ASN1VideotexString getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1VideotexString)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1VideotexString) {
               return (ASN1VideotexString)var1;
            }
         }

         if (var0 instanceof byte[]) {
            try {
               return (ASN1VideotexString)TYPE.fromByteArray((byte[])var0);
            } catch (Exception var2) {
               throw new IllegalArgumentException("encoding error in getInstance: " + var2.toString());
            }
         } else {
            throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
         }
      } else {
         return (ASN1VideotexString)var0;
      }
   }

   public static ASN1VideotexString getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1VideotexString)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1VideotexString getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1VideotexString)TYPE.getTagged(var0, var1);
   }

   ASN1VideotexString(byte[] var1, boolean var2) {
      this.contents = var2 ? Arrays.clone(var1) : var1;
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
      var1.writeEncodingDL(var2, 21, this.contents);
   }

   @Override
   final boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1VideotexString)) {
         return false;
      } else {
         ASN1VideotexString var2 = (ASN1VideotexString)var1;
         return Arrays.areEqual(this.contents, var2.contents);
      }
   }

   @Override
   public final int hashCode() {
      return Arrays.hashCode(this.contents);
   }

   @Override
   public final String getString() {
      return Strings.fromByteArray(this.contents);
   }

   static ASN1VideotexString createPrimitive(byte[] var0) {
      return new DERVideotexString(var0, false);
   }
}
