package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public class ASN1OutputStream {
   private OutputStream os;

   public static ASN1OutputStream create(OutputStream var0) {
      return new ASN1OutputStream(var0);
   }

   public static ASN1OutputStream create(OutputStream var0, String var1) {
      if (var1.equals("DER")) {
         return new DEROutputStream(var0);
      } else {
         return (ASN1OutputStream)(var1.equals("DL") ? new DLOutputStream(var0) : new ASN1OutputStream(var0));
      }
   }

   ASN1OutputStream(OutputStream var1) {
      this.os = var1;
   }

   public void close() throws IOException {
      this.os.close();
   }

   public void flush() throws IOException {
      this.os.flush();
   }

   public final void writeObject(ASN1Encodable var1) throws IOException {
      if (null == var1) {
         throw new IOException("null object detected");
      } else {
         this.writePrimitive(var1.toASN1Primitive(), true);
         this.flushInternal();
      }
   }

   public final void writeObject(ASN1Primitive var1) throws IOException {
      if (null == var1) {
         throw new IOException("null object detected");
      } else {
         this.writePrimitive(var1, true);
         this.flushInternal();
      }
   }

   void flushInternal() throws IOException {
   }

   DEROutputStream getDERSubStream() {
      return new DEROutputStream(this.os);
   }

   DLOutputStream getDLSubStream() {
      return new DLOutputStream(this.os);
   }

   final void writeDL(int var1) throws IOException {
      if (var1 < 128) {
         this.write(var1);
      } else {
         byte[] var2 = new byte[5];
         int var3 = var2.length;

         do {
            var2[--var3] = (byte)var1;
            var1 >>>= 8;
         } while (var1 != 0);

         int var4 = var2.length - var3;
         var2[--var3] = (byte)(128 | var4);
         this.write(var2, var3, var4 + 1);
      }
   }

   final void write(int var1) throws IOException {
      this.os.write(var1);
   }

   final void write(byte[] var1, int var2, int var3) throws IOException {
      this.os.write(var1, var2, var3);
   }

   void writeElements(ASN1Encodable[] var1) throws IOException {
      int var2 = 0;

      for (int var3 = var1.length; var2 < var3; var2++) {
         var1[var2].toASN1Primitive().encode(this, true);
      }
   }

   final void writeEncodingDL(boolean var1, int var2, byte var3) throws IOException {
      this.writeIdentifier(var1, var2);
      this.writeDL(1);
      this.write(var3);
   }

   final void writeEncodingDL(boolean var1, int var2, byte[] var3) throws IOException {
      this.writeIdentifier(var1, var2);
      this.writeDL(var3.length);
      this.write(var3, 0, var3.length);
   }

   final void writeEncodingDL(boolean var1, int var2, byte[] var3, int var4, int var5) throws IOException {
      this.writeIdentifier(var1, var2);
      this.writeDL(var5);
      this.write(var3, var4, var5);
   }

   final void writeEncodingDL(boolean var1, int var2, byte var3, byte[] var4, int var5, int var6) throws IOException {
      this.writeIdentifier(var1, var2);
      this.writeDL(1 + var6);
      this.write(var3);
      this.write(var4, var5, var6);
   }

   final void writeEncodingDL(boolean var1, int var2, byte[] var3, int var4, int var5, byte var6) throws IOException {
      this.writeIdentifier(var1, var2);
      this.writeDL(var5 + 1);
      this.write(var3, var4, var5);
      this.write(var6);
   }

   final void writeEncodingDL(boolean var1, int var2, int var3, byte[] var4) throws IOException {
      this.writeIdentifier(var1, var2, var3);
      this.writeDL(var4.length);
      this.write(var4, 0, var4.length);
   }

   final void writeEncodingIL(boolean var1, int var2, ASN1Encodable[] var3) throws IOException {
      this.writeIdentifier(var1, var2);
      this.write(128);
      this.writeElements(var3);
      this.write(0);
      this.write(0);
   }

   final void writeIdentifier(boolean var1, int var2) throws IOException {
      if (var1) {
         this.write(var2);
      }
   }

   final void writeIdentifier(boolean var1, int var2, int var3) throws IOException {
      if (var1) {
         if (var3 < 31) {
            this.write(var2 | var3);
         } else {
            byte[] var4 = new byte[6];
            int var5 = var4.length;

            for (var4[--var5] = (byte)(var3 & 127); var3 > 127; var4[--var5] = (byte)(var3 & 127 | 128)) {
               var3 >>>= 7;
            }

            var4[--var5] = (byte)(var2 | 31);
            this.write(var4, var5, var4.length - var5);
         }
      }
   }

   void writePrimitive(ASN1Primitive var1, boolean var2) throws IOException {
      var1.encode(this, var2);
   }

   void writePrimitives(ASN1Primitive[] var1) throws IOException {
      int var2 = 0;

      for (int var3 = var1.length; var2 < var3; var2++) {
         var1[var2].encode(this, true);
      }
   }

   static int getLengthOfDL(int var0) {
      if (var0 < 128) {
         return 1;
      } else {
         int var1 = 2;

         while ((var0 >>>= 8) != 0) {
            var1++;
         }

         return var1;
      }
   }

   static int getLengthOfEncodingDL(boolean var0, int var1) {
      return (var0 ? 1 : 0) + getLengthOfDL(var1) + var1;
   }

   static int getLengthOfIdentifier(int var0) {
      if (var0 < 31) {
         return 1;
      } else {
         int var1 = 2;

         while ((var0 >>>= 7) != 0) {
            var1++;
         }

         return var1;
      }
   }
}
