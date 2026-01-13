package org.bouncycastle.est;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CTEChunkedInputStream extends InputStream {
   private InputStream src;
   int chunkLen = 0;

   public CTEChunkedInputStream(InputStream var1) {
      this.src = var1;
   }

   private String readEOL() throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      int var2 = 0;

      do {
         var2 = this.src.read();
         if (var2 == -1) {
            return var1.size() == 0 ? null : var1.toString().trim();
         }

         var1.write(var2 & 0xFF);
      } while (var2 != 10);

      return var1.toString().trim();
   }

   @Override
   public int read() throws IOException {
      if (this.chunkLen == Integer.MIN_VALUE) {
         return -1;
      } else {
         if (this.chunkLen == 0) {
            Object var1 = null;

            do {
               var1 = this.readEOL();
            } while (var1 != null && var1.length() == 0);

            if (var1 == null) {
               return -1;
            }

            this.chunkLen = Integer.parseInt(var1.trim(), 16);
            if (this.chunkLen == 0) {
               this.readEOL();
               this.chunkLen = Integer.MIN_VALUE;
               return -1;
            }
         }

         int var3 = this.src.read();
         this.chunkLen--;
         return var3;
      }
   }
}
