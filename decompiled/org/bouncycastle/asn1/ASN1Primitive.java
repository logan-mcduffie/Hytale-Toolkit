package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public abstract class ASN1Primitive extends ASN1Object {
   ASN1Primitive() {
   }

   @Override
   public void encodeTo(OutputStream var1) throws IOException {
      ASN1OutputStream var2 = ASN1OutputStream.create(var1);
      var2.writePrimitive(this, true);
      var2.flushInternal();
   }

   @Override
   public void encodeTo(OutputStream var1, String var2) throws IOException {
      ASN1OutputStream var3 = ASN1OutputStream.create(var1, var2);
      var3.writePrimitive(this, true);
      var3.flushInternal();
   }

   public static ASN1Primitive fromByteArray(byte[] var0) throws IOException {
      ASN1InputStream var1 = new ASN1InputStream(var0);

      try {
         ASN1Primitive var2 = var1.readObject();
         if (var1.available() != 0) {
            throw new IOException("Extra data detected in stream");
         } else {
            return var2;
         }
      } catch (ClassCastException var3) {
         throw new IOException("cannot recognise object in stream");
      }
   }

   @Override
   public final boolean equals(Object var1) {
      return this == var1 ? true : var1 instanceof ASN1Encodable && this.asn1Equals(((ASN1Encodable)var1).toASN1Primitive());
   }

   public final boolean equals(ASN1Encodable var1) {
      return this == var1 || null != var1 && this.asn1Equals(var1.toASN1Primitive());
   }

   public final boolean equals(ASN1Primitive var1) {
      return this == var1 || this.asn1Equals(var1);
   }

   @Override
   public final ASN1Primitive toASN1Primitive() {
      return this;
   }

   ASN1Primitive toDERObject() {
      return this;
   }

   ASN1Primitive toDLObject() {
      return this;
   }

   @Override
   public abstract int hashCode();

   abstract boolean encodeConstructed();

   abstract int encodedLength(boolean var1) throws IOException;

   abstract void encode(ASN1OutputStream var1, boolean var2) throws IOException;

   abstract boolean asn1Equals(ASN1Primitive var1);
}
