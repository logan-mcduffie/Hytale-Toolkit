package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

class StreamUtil {
   static int findLimit(InputStream var0) {
      if (var0 instanceof LimitedInputStream) {
         return ((LimitedInputStream)var0).getLimit();
      } else if (var0 instanceof ASN1InputStream) {
         return ((ASN1InputStream)var0).getLimit();
      } else if (var0 instanceof ByteArrayInputStream) {
         return ((ByteArrayInputStream)var0).available();
      } else {
         if (var0 instanceof FileInputStream) {
            try {
               FileChannel var1 = ((FileInputStream)var0).getChannel();
               long var2 = var1 != null ? var1.size() : 2147483647L;
               if (var2 < 2147483647L) {
                  return (int)var2;
               }
            } catch (IOException var4) {
            }
         }

         long var5 = Runtime.getRuntime().maxMemory();
         return var5 > 2147483647L ? Integer.MAX_VALUE : (int)var5;
      }
   }
}
