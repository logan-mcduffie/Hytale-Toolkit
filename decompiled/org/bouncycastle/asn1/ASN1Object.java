package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.Encodable;

public abstract class ASN1Object implements ASN1Encodable, Encodable {
   public void encodeTo(OutputStream var1) throws IOException {
      this.toASN1Primitive().encodeTo(var1);
   }

   public void encodeTo(OutputStream var1, String var2) throws IOException {
      this.toASN1Primitive().encodeTo(var1, var2);
   }

   @Override
   public byte[] getEncoded() throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      this.toASN1Primitive().encodeTo(var1);
      return var1.toByteArray();
   }

   public byte[] getEncoded(String var1) throws IOException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      this.toASN1Primitive().encodeTo(var2, var1);
      return var2.toByteArray();
   }

   @Override
   public int hashCode() {
      return this.toASN1Primitive().hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ASN1Encodable)) {
         return false;
      } else {
         ASN1Encodable var2 = (ASN1Encodable)var1;
         return this.toASN1Primitive().equals(var2.toASN1Primitive());
      }
   }

   protected static boolean hasEncodedTagValue(Object var0, int var1) {
      return var0 instanceof byte[] && ((byte[])var0)[0] == var1;
   }

   @Override
   public abstract ASN1Primitive toASN1Primitive();
}
