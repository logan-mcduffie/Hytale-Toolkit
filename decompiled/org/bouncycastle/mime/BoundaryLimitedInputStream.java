package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Strings;

public class BoundaryLimitedInputStream extends InputStream {
   private final InputStream src;
   private final byte[] boundary;
   private final byte[] buf;
   private int bufOff = 0;
   private int index = 0;
   private boolean ended = false;
   private int lastI;

   public BoundaryLimitedInputStream(InputStream var1, String var2) {
      this.src = var1;
      this.boundary = Strings.toByteArray(var2);
      this.buf = new byte[var2.length() + 3];
      this.bufOff = 0;
   }

   @Override
   public int read() throws IOException {
      if (this.ended) {
         return -1;
      } else {
         int var1;
         if (this.index < this.bufOff) {
            var1 = this.buf[this.index++] & 255;
            if (this.index < this.bufOff) {
               return var1;
            }

            this.index = this.bufOff = 0;
         } else {
            var1 = this.src.read();
         }

         this.lastI = var1;
         if (var1 < 0) {
            return -1;
         } else {
            if (var1 == 13 || var1 == 10) {
               this.index = 0;
               int var2;
               if (var1 == 13) {
                  var2 = this.src.read();
                  if (var2 == 10) {
                     this.buf[this.bufOff++] = 10;
                     var2 = this.src.read();
                  }
               } else {
                  var2 = this.src.read();
               }

               if (var2 == 45) {
                  this.buf[this.bufOff++] = 45;
                  var2 = this.src.read();
               }

               if (var2 == 45) {
                  this.buf[this.bufOff++] = 45;

                  int var3;
                  int var4;
                  for (var3 = this.bufOff; this.bufOff - var3 != this.boundary.length && (var4 = this.src.read()) >= 0; this.bufOff++) {
                     this.buf[this.bufOff] = (byte)var4;
                     if (this.buf[this.bufOff] != this.boundary[this.bufOff - var3]) {
                        this.bufOff++;
                        break;
                     }
                  }

                  if (this.bufOff - var3 == this.boundary.length) {
                     this.ended = true;
                     return -1;
                  }
               } else if (var2 >= 0) {
                  this.buf[this.bufOff++] = (byte)var2;
               }
            }

            return var1;
         }
      }
   }
}
