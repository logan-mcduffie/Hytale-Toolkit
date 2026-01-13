package org.bouncycastle.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class MimeWriter {
   protected final Headers headers;

   protected MimeWriter(Headers var1) {
      this.headers = var1;
   }

   public Headers getHeaders() {
      return this.headers;
   }

   public abstract OutputStream getContentStream() throws IOException;

   protected static List<String> mapToLines(Map<String, String> var0) {
      ArrayList var1 = new ArrayList(var0.size());

      for (String var3 : var0.keySet()) {
         var1.add(var3 + ": " + (String)var0.get(var3));
      }

      return var1;
   }
}
