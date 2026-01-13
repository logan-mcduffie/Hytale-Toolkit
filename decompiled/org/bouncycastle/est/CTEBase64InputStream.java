package org.bouncycastle.est;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.util.encoders.Base64;

class CTEBase64InputStream extends InputStream {
   protected final InputStream src;
   protected final byte[] rawBuf = new byte[1024];
   protected final byte[] data = new byte[768];
   protected final OutputStream dataOutputStream;
   protected final Long max;
   protected int rp;
   protected int wp;
   protected boolean end;
   protected long read;

   public CTEBase64InputStream(InputStream var1) {
      this(var1, null);
   }

   public CTEBase64InputStream(InputStream var1, Long var2) {
      this.src = var1;
      this.dataOutputStream = new OutputStream() {
         @Override
         public void write(int var1) throws IOException {
            CTEBase64InputStream.this.data[CTEBase64InputStream.this.wp++] = (byte)var1;
         }
      };
      this.max = var2;
   }

   protected int pullFromSrc() throws IOException {
      int var1 = 0;
      int var2 = 0;

      while (this.max == null || this.read <= this.max) {
         var1 = this.src.read();
         if (var1 < 33 && var1 != 13 && var1 != 10) {
            if (var1 >= 0) {
               this.read++;
            }
         } else {
            if (var2 >= this.rawBuf.length) {
               throw new IOException("Content Transfer Encoding, base64 line length > 1024");
            }

            this.rawBuf[var2++] = (byte)var1;
            this.read++;
         }

         if (var1 <= -1 || var2 >= this.rawBuf.length || var1 == 10) {
            if (var2 > 0) {
               try {
                  Base64.decode(this.rawBuf, 0, var2, this.dataOutputStream);
               } catch (Exception var4) {
                  throw new IOException("Decode Base64 Content-Transfer-Encoding: " + var4);
               }
            } else if (var1 == -1) {
               return -1;
            }

            return this.wp;
         }
      }

      return -1;
   }

   @Override
   public int read() throws IOException {
      if (this.rp == this.wp) {
         this.rp = 0;
         this.wp = 0;
         int var1 = this.pullFromSrc();
         if (var1 == -1) {
            return var1;
         }
      }

      return this.data[this.rp++] & 0xFF;
   }

   @Override
   public void close() throws IOException {
      this.src.close();
   }
}
