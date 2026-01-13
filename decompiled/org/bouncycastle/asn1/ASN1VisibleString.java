package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public abstract class ASN1VisibleString extends ASN1Primitive implements ASN1String {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1VisibleString.class, 26) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return ASN1VisibleString.createPrimitive(var1.getOctets());
      }
   };
   final byte[] contents;

   public static ASN1VisibleString getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1VisibleString)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1VisibleString) {
               return (ASN1VisibleString)var1;
            }
         }

         if (var0 instanceof byte[]) {
            try {
               return (ASN1VisibleString)TYPE.fromByteArray((byte[])var0);
            } catch (Exception var2) {
               throw new IllegalArgumentException("encoding error in getInstance: " + var2.toString());
            }
         } else {
            throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
         }
      } else {
         return (ASN1VisibleString)var0;
      }
   }

   public static ASN1VisibleString getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1VisibleString)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1VisibleString getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1VisibleString)TYPE.getTagged(var0, var1);
   }

   ASN1VisibleString(String var1) {
      this.contents = Strings.toByteArray(var1);
   }

   ASN1VisibleString(byte[] var1, boolean var2) {
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
      var1.writeEncodingDL(var2, 26, this.contents);
   }

   @Override
   final boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1VisibleString)) {
         return false;
      } else {
         ASN1VisibleString var2 = (ASN1VisibleString)var1;
         return Arrays.areEqual(this.contents, var2.contents);
      }
   }

   @Override
   public final int hashCode() {
      return Arrays.hashCode(this.contents);
   }

   static ASN1VisibleString createPrimitive(byte[] var0) {
      return new DERVisibleString(var0, false);
   }
}
