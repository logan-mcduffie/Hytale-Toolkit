package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bouncycastle.util.Iterable;
import org.bouncycastle.util.Strings;

public class Headers implements Iterable<String> {
   private final Map<String, List> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
   private final List<String> headersAsPresented;
   private final String contentTransferEncoding;
   private String boundary;
   private boolean multipart;
   private String contentType;
   private Map<String, String> contentTypeParameters;

   private static List<String> parseHeaders(InputStream var0) throws IOException {
      ArrayList var2 = new ArrayList();
      LineReader var3 = new LineReader(var0);

      String var1;
      while ((var1 = var3.readLine()) != null && var1.length() != 0) {
         var2.add(var1);
      }

      return var2;
   }

   public Headers(String var1, String var2) {
      String var3 = "Content-Type: " + var1;
      this.headersAsPresented = new ArrayList<>();
      this.headersAsPresented.add(var3);
      this.put("Content-Type", var1);
      String var4 = this.getValues("Content-Type") == null ? "text/plain" : this.getValues("Content-Type")[0];
      int var5 = var4.indexOf(59);
      if (var5 < 0) {
         var1 = var4;
         this.contentTypeParameters = Collections.EMPTY_MAP;
      } else {
         var1 = var4.substring(0, var5);
         this.contentTypeParameters = this.createContentTypeParameters(var4.substring(var5 + 1).trim());
      }

      this.contentTransferEncoding = this.getValues("Content-Transfer-Encoding") == null ? var2 : this.getValues("Content-Transfer-Encoding")[0];
      if (var1.indexOf("multipart") >= 0) {
         this.multipart = true;
         String var6 = this.contentTypeParameters.get("boundary");
         if (var6.startsWith("\"") && var6.endsWith("\"")) {
            this.boundary = var6.substring(1, var6.length() - 1);
         } else {
            this.boundary = var6;
         }
      } else {
         this.boundary = null;
         this.multipart = false;
      }
   }

   public Headers(InputStream var1, String var2) throws IOException {
      this(parseHeaders(var1), var2);
   }

   public Headers(List<String> var1, String var2) {
      this.headersAsPresented = var1;
      String var3 = "";

      for (String var5 : var1) {
         if (!var5.startsWith(" ") && !var5.startsWith("\t")) {
            if (var3.length() != 0) {
               this.put(var3.substring(0, var3.indexOf(58)).trim(), var3.substring(var3.indexOf(58) + 1).trim());
            }

            var3 = var5;
         } else {
            var3 = var3 + var5.trim();
         }
      }

      if (var3.trim().length() != 0) {
         this.put(var3.substring(0, var3.indexOf(58)).trim(), var3.substring(var3.indexOf(58) + 1).trim());
      }

      String var7 = this.getValues("Content-Type") == null ? "text/plain" : this.getValues("Content-Type")[0];
      int var8 = var7.indexOf(59);
      if (var8 < 0) {
         this.contentType = var7;
         this.contentTypeParameters = Collections.EMPTY_MAP;
      } else {
         this.contentType = var7.substring(0, var8);
         this.contentTypeParameters = this.createContentTypeParameters(var7.substring(var8 + 1).trim());
      }

      this.contentTransferEncoding = this.getValues("Content-Transfer-Encoding") == null ? var2 : this.getValues("Content-Transfer-Encoding")[0];
      if (this.contentType.indexOf("multipart") >= 0) {
         this.multipart = true;
         String var6 = this.contentTypeParameters.get("boundary");
         this.boundary = var6.substring(1, var6.length() - 1);
      } else {
         this.boundary = null;
         this.multipart = false;
      }
   }

   public Map<String, String> getContentTypeAttributes() {
      return this.contentTypeParameters;
   }

   private Map<String, String> createContentTypeParameters(String var1) {
      String[] var2 = var1.split(";");
      LinkedHashMap var3 = new LinkedHashMap();

      for (int var4 = 0; var4 != var2.length; var4++) {
         String var5 = var2[var4];
         int var6 = var5.indexOf(61);
         if (var6 < 0) {
            throw new IllegalArgumentException("malformed Content-Type header");
         }

         var3.put(var5.substring(0, var6).trim(), var5.substring(var6 + 1).trim());
      }

      return Collections.unmodifiableMap(var3);
   }

   public boolean isMultipart() {
      return this.multipart;
   }

   public String getBoundary() {
      return this.boundary;
   }

   public String getContentType() {
      return this.contentType;
   }

   public String getContentTransferEncoding() {
      return this.contentTransferEncoding;
   }

   private void put(String var1, String var2) {
      synchronized (this) {
         Headers.KV var4 = new Headers.KV(var1, var2);
         Object var5 = this.headers.get(var1);
         if (var5 == null) {
            var5 = new ArrayList();
            this.headers.put(var1, (List)var5);
         }

         var5.add(var4);
      }
   }

   public Iterator<String> getNames() {
      return this.headers.keySet().iterator();
   }

   public String[] getValues(String var1) {
      synchronized (this) {
         List var3 = this.headers.get(var1);
         if (var3 == null) {
            return null;
         } else {
            String[] var4 = new String[var3.size()];

            for (int var5 = 0; var5 < var3.size(); var5++) {
               var4[var5] = ((Headers.KV)var3.get(var5)).value;
            }

            return var4;
         }
      }
   }

   public boolean isEmpty() {
      synchronized (this) {
         return this.headers.isEmpty();
      }
   }

   public boolean containsKey(String var1) {
      return this.headers.containsKey(var1);
   }

   @Override
   public Iterator<String> iterator() {
      return this.headers.keySet().iterator();
   }

   public void dumpHeaders(OutputStream var1) throws IOException {
      Iterator var2 = this.headersAsPresented.iterator();

      while (var2.hasNext()) {
         var1.write(Strings.toUTF8ByteArray(var2.next().toString()));
         var1.write(13);
         var1.write(10);
      }
   }

   private static class KV {
      public final String key;
      public final String value;

      public KV(String var1, String var2) {
         this.key = var1;
         this.value = var2;
      }

      public KV(Headers.KV var1) {
         this.key = var1.key;
         this.value = var1.value;
      }
   }
}
