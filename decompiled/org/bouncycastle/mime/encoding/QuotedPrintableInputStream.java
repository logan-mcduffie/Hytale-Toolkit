package org.bouncycastle.mime.encoding;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class QuotedPrintableInputStream extends FilterInputStream {
   public QuotedPrintableInputStream(InputStream var1) {
      super(var1);
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4;
      for (var4 = 0; var4 != var3; var4++) {
         int var5 = this.read();
         if (var5 < 0) {
            break;
         }

         var1[var4 + var2] = (byte)var5;
      }

      return var4 == 0 ? -1 : var4;
   }

   @Override
   public int read() throws IOException {
      int var1 = this.in.read();
      if (var1 == -1) {
         return -1;
      } else {
         while (var1 == 61) {
            int var2 = this.in.read();
            if (var2 == -1) {
               throw new IllegalStateException("Quoted '=' at end of stream");
            }

            if (var2 == 13) {
               var2 = this.in.read();
               if (var2 == 10) {
                  var2 = this.in.read();
               }

               var1 = var2;
            } else {
               if (var2 != 10) {
                  int var3 = 0;
                  if (var2 >= 48 && var2 <= 57) {
                     var3 = var2 - 48;
                  } else {
                     if (var2 < 65 || var2 > 70) {
                        throw new IllegalStateException("Expecting '0123456789ABCDEF after quote that was not immediately followed by LF or CRLF");
                     }

                     var3 = 10 + (var2 - 65);
                  }

                  var3 <<= 4;
                  var2 = this.in.read();
                  if (var2 >= 48 && var2 <= 57) {
                     var3 |= var2 - 48;
                  } else {
                     if (var2 < 65 || var2 > 70) {
                        throw new IllegalStateException("Expecting second '0123456789ABCDEF after quote that was not immediately followed by LF or CRLF");
                     }

                     var3 |= 10 + (var2 - 65);
                  }

                  return var3;
               }

               var1 = this.in.read();
            }
         }

         return var1;
      }
   }
}
