package org.bouncycastle.mime;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.mime.smime.SMimeParserContext;

public class CanonicalOutputStream extends FilterOutputStream {
   protected int lastb = -1;
   protected static byte[] newline = new byte[2];
   private final boolean is7Bit;

   public CanonicalOutputStream(SMimeParserContext var1, Headers var2, OutputStream var3) {
      super(var3);
      if (var2.getContentType() != null) {
         this.is7Bit = var2.getContentType() != null && !var2.getContentType().equals("binary");
      } else {
         this.is7Bit = var1.getDefaultContentTransferEncoding().equals("7bit");
      }
   }

   @Override
   public void write(int var1) throws IOException {
      if (this.is7Bit) {
         if (var1 == 13) {
            this.out.write(newline);
         } else if (var1 == 10) {
            if (this.lastb != 13) {
               this.out.write(newline);
            }
         } else {
            this.out.write(var1);
         }
      } else {
         this.out.write(var1);
      }

      this.lastb = var1;
   }

   @Override
   public void write(byte[] var1) throws IOException {
      this.write(var1, 0, var1.length);
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      for (int var4 = var2; var4 != var2 + var3; var4++) {
         this.write(var1[var4]);
      }
   }

   public void writeln() throws IOException {
      super.out.write(newline);
   }

   static {
      newline[0] = 13;
      newline[1] = 10;
   }
}
