package org.bouncycastle.mime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Strings;

class LineReader {
   private final InputStream src;
   private int lastC = -1;

   LineReader(InputStream var1) {
      this.src = var1;
   }

   String readLine() throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      int var2;
      if (this.lastC != -1) {
         if (this.lastC == 13) {
            return "";
         }

         var2 = this.lastC;
         this.lastC = -1;
      } else {
         var2 = this.src.read();
      }

      while (var2 >= 0 && var2 != 13 && var2 != 10) {
         var1.write(var2);
         var2 = this.src.read();
      }

      if (var2 == 13) {
         int var3 = this.src.read();
         if (var3 != 10 && var3 >= 0) {
            this.lastC = var3;
         }
      }

      return var2 < 0 ? null : Strings.fromUTF8ByteArray(var1.toByteArray());
   }
}
