package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.mime.encoding.Base64InputStream;
import org.bouncycastle.mime.encoding.QuotedPrintableInputStream;

public class BasicMimeParser implements MimeParser {
   private final InputStream src;
   private final MimeParserContext parserContext;
   private final String defaultContentTransferEncoding;
   private Headers headers;
   private boolean isMultipart = false;
   private final String boundary;

   public BasicMimeParser(InputStream var1) throws IOException {
      this(null, new Headers(var1, "7bit"), var1);
   }

   public BasicMimeParser(MimeParserContext var1, InputStream var2) throws IOException {
      this(var1, new Headers(var2, var1.getDefaultContentTransferEncoding()), var2);
   }

   public BasicMimeParser(Headers var1, InputStream var2) {
      this(null, var1, var2);
   }

   public BasicMimeParser(MimeParserContext var1, Headers var2, InputStream var3) {
      if (var2.isMultipart()) {
         this.isMultipart = true;
         this.boundary = var2.getBoundary();
      } else {
         this.boundary = null;
      }

      this.headers = var2;
      this.parserContext = var1;
      this.src = var3;
      this.defaultContentTransferEncoding = var1 != null ? var1.getDefaultContentTransferEncoding() : "7bit";
   }

   @Override
   public void parse(MimeParserListener var1) throws IOException {
      MimeContext var2 = var1.createContext(this.parserContext, this.headers);
      if (this.isMultipart) {
         MimeMultipartContext var4 = (MimeMultipartContext)var2;
         String var5 = "--" + this.boundary;
         boolean var6 = false;
         int var7 = 0;
         LineReader var8 = new LineReader(this.src);

         String var3;
         while ((var3 = var8.readLine()) != null && !"--".equals(var3)) {
            if (var6) {
               BoundaryLimitedInputStream var9 = new BoundaryLimitedInputStream(this.src, this.boundary);
               Headers var10 = new Headers(var9, this.defaultContentTransferEncoding);
               MimeContext var11 = var4.createContext(var7++);
               InputStream var13 = var11.applyContext(var10, var9);
               var1.object(this.parserContext, var10, this.processStream(var10, var13));
               if (var13.read() >= 0) {
                  throw new IOException("MIME object not fully processed");
               }
            } else if (var5.equals(var3)) {
               var6 = true;
               BoundaryLimitedInputStream var14 = new BoundaryLimitedInputStream(this.src, this.boundary);
               Headers var16 = new Headers(var14, this.defaultContentTransferEncoding);
               MimeContext var17 = var4.createContext(var7++);
               InputStream var15 = var17.applyContext(var16, var14);
               var1.object(this.parserContext, var16, this.processStream(var16, var15));
               if (var15.read() >= 0) {
                  throw new IOException("MIME object not fully processed");
               }
            }
         }
      } else {
         InputStream var12 = var2.applyContext(this.headers, this.src);
         var1.object(this.parserContext, this.headers, this.processStream(this.headers, var12));
      }
   }

   public boolean isMultipart() {
      return this.isMultipart;
   }

   private InputStream processStream(Headers var1, InputStream var2) {
      if (var1.getContentTransferEncoding().equals("base64")) {
         return new Base64InputStream(var2);
      } else {
         return (InputStream)(var1.getContentTransferEncoding().equals("quoted-printable") ? new QuotedPrintableInputStream(var2) : var2);
      }
   }
}
