package org.bouncycastle.util.io.pem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Base64;

public class PemWriter extends BufferedWriter {
   private static final int LINE_LENGTH = 64;
   private final int nlLength;
   private char[] buf = new char[64];

   public PemWriter(Writer var1) {
      super(var1);
      String var2 = Strings.lineSeparator();
      if (var2 != null) {
         this.nlLength = var2.length();
      } else {
         this.nlLength = 2;
      }
   }

   public int getOutputSize(PemObject var1) {
      int var2 = 2 * (var1.getType().length() + 10 + this.nlLength) + 6 + 4;
      if (!var1.getHeaders().isEmpty()) {
         for (PemHeader var4 : var1.getHeaders()) {
            var2 += var4.getName().length() + ": ".length() + var4.getValue().length() + this.nlLength;
         }

         var2 += this.nlLength;
      }

      int var5 = (var1.getContent().length + 2) / 3 * 4;
      return var2 + var5 + (var5 + 64 - 1) / 64 * this.nlLength;
   }

   public void writeObject(PemObjectGenerator var1) throws IOException {
      PemObject var2 = var1.generate();
      this.writePreEncapsulationBoundary(var2.getType());
      if (!var2.getHeaders().isEmpty()) {
         for (PemHeader var4 : var2.getHeaders()) {
            this.write(var4.getName());
            this.write(": ");
            this.write(var4.getValue());
            this.newLine();
         }

         this.newLine();
      }

      this.writeEncoded(var2.getContent());
      this.writePostEncapsulationBoundary(var2.getType());
   }

   private void writeEncoded(byte[] var1) throws IOException {
      var1 = Base64.encode(var1);
      int var2 = 0;

      while (var2 < var1.length) {
         int var3;
         for (var3 = 0; var3 != this.buf.length && var2 + var3 < var1.length; var3++) {
            this.buf[var3] = (char)var1[var2 + var3];
         }

         this.write(this.buf, 0, var3);
         this.newLine();
         var2 += this.buf.length;
      }
   }

   private void writePreEncapsulationBoundary(String var1) throws IOException {
      this.write("-----BEGIN " + var1 + "-----");
      this.newLine();
   }

   private void writePostEncapsulationBoundary(String var1) throws IOException {
      this.write("-----END " + var1 + "-----");
      this.newLine();
   }
}
