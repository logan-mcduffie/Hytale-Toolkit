package org.bouncycastle.est;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.Strings;

public class ESTResponse {
   private final ESTRequest originalRequest;
   private final HttpUtil.Headers headers;
   private final byte[] lineBuffer;
   private final Source source;
   private String HttpVersion;
   private int statusCode;
   private String statusMessage;
   private InputStream inputStream;
   private Long contentLength;
   private long read = 0L;
   private Long absoluteReadLimit;
   private static final Long ZERO = 0L;

   public long getAbsoluteReadLimit() {
      return this.absoluteReadLimit == null ? Long.MAX_VALUE : this.absoluteReadLimit;
   }

   public ESTResponse(ESTRequest var1, Source var2) throws IOException {
      this.originalRequest = var1;
      this.source = var2;
      if (var2 instanceof LimitedSource) {
         this.absoluteReadLimit = ((LimitedSource)var2).getAbsoluteReadLimit();
      }

      Set var3 = Properties.asKeySet("org.bouncycastle.debug.est");
      if (!var3.contains("input") && !var3.contains("all")) {
         this.inputStream = var2.getInputStream();
      } else {
         this.inputStream = new ESTResponse.PrintingInputStream(var2.getInputStream());
      }

      this.headers = new HttpUtil.Headers();
      this.lineBuffer = new byte[1024];
      this.process();
   }

   private void process() throws IOException {
      this.HttpVersion = this.readStringIncluding(' ');
      this.statusCode = Integer.parseInt(this.readStringIncluding(' '));
      this.statusMessage = this.readStringIncluding('\n');

      for (String var1 = this.readStringIncluding('\n'); var1.length() > 0; var1 = this.readStringIncluding('\n')) {
         int var2 = var1.indexOf(58);
         if (var2 > -1) {
            String var3 = Strings.toLowerCase(var1.substring(0, var2).trim());
            this.headers.add(var3, var1.substring(var2 + 1).trim());
         }
      }

      boolean var4 = this.headers.getFirstValueOrEmpty("Transfer-Encoding").equalsIgnoreCase("chunked");
      if (var4) {
         this.contentLength = 0L;
      } else {
         this.contentLength = this.getContentLength();
      }

      if (this.statusCode == 204 || this.statusCode == 202) {
         if (this.contentLength == null) {
            this.contentLength = 0L;
         } else if (this.statusCode == 204 && this.contentLength > 0L) {
            throw new IOException("Got HTTP status 204 but Content-length > 0.");
         }
      }

      if (this.contentLength == null) {
         throw new IOException("No Content-length header.");
      } else {
         if (this.contentLength.equals(ZERO) && !var4) {
            this.inputStream = new InputStream() {
               @Override
               public int read() throws IOException {
                  return -1;
               }
            };
         }

         if (this.contentLength < 0L) {
            throw new IOException("Server returned negative content length: " + this.absoluteReadLimit);
         } else if (this.absoluteReadLimit != null && this.contentLength >= this.absoluteReadLimit) {
            throw new IOException("Content length longer than absolute read limit: " + this.absoluteReadLimit + " Content-Length: " + this.contentLength);
         } else {
            this.inputStream = this.wrapWithCounter(this.inputStream, this.absoluteReadLimit);
            if (var4) {
               this.inputStream = new CTEChunkedInputStream(this.inputStream);
            }

            if ("base64".equalsIgnoreCase(this.getHeader("content-transfer-encoding"))) {
               if (var4) {
                  this.inputStream = new CTEBase64InputStream(this.inputStream);
               } else {
                  this.inputStream = new CTEBase64InputStream(this.inputStream, this.contentLength);
               }
            }
         }
      }
   }

   public String getHeader(String var1) {
      return this.headers.getFirstValue(var1);
   }

   public String getHeaderOrEmpty(String var1) {
      return this.headers.getFirstValueOrEmpty(var1);
   }

   protected InputStream wrapWithCounter(final InputStream var1, final Long var2) {
      return new InputStream() {
         @Override
         public int read() throws IOException {
            int var1x = var1.read();
            if (var1x > -1) {
               ESTResponse.this.read++;
               if (var2 != null && ESTResponse.this.read >= var2) {
                  throw new IOException("Absolute Read Limit exceeded: " + var2);
               }
            }

            return var1x;
         }

         @Override
         public void close() throws IOException {
            if (ESTResponse.this.contentLength != null && ESTResponse.this.contentLength - 1L > ESTResponse.this.read) {
               throw new IOException(
                  "Stream closed before limit fully read, Read: " + ESTResponse.this.read + " ContentLength: " + ESTResponse.this.contentLength
               );
            } else if (var1.available() > 0) {
               throw new IOException("Stream closed with extra content in pipe that exceeds content length.");
            } else {
               var1.close();
            }
         }
      };
   }

   protected String readStringIncluding(char var1) throws IOException {
      int var2 = 0;

      int var3;
      do {
         var3 = this.inputStream.read();
         this.lineBuffer[var2++] = (byte)var3;
         if (var2 >= this.lineBuffer.length) {
            throw new IOException("Server sent line > " + this.lineBuffer.length);
         }
      } while (var3 != var1 && var3 > -1);

      if (var3 == -1) {
         throw new EOFException();
      } else {
         return new String(this.lineBuffer, 0, var2).trim();
      }
   }

   public ESTRequest getOriginalRequest() {
      return this.originalRequest;
   }

   public HttpUtil.Headers getHeaders() {
      return this.headers;
   }

   public String getHttpVersion() {
      return this.HttpVersion;
   }

   public int getStatusCode() {
      return this.statusCode;
   }

   public String getStatusMessage() {
      return this.statusMessage;
   }

   public InputStream getInputStream() {
      return this.inputStream;
   }

   public Source getSource() {
      return this.source;
   }

   public Long getContentLength() {
      String var1 = this.headers.getFirstValue("Content-Length");
      if (var1 == null) {
         return null;
      } else {
         try {
            return Long.parseLong(var1);
         } catch (RuntimeException var3) {
            throw new RuntimeException("Content Length: '" + var1 + "' invalid. " + var3.getMessage());
         }
      }
   }

   public void close() throws IOException {
      if (this.inputStream != null) {
         this.inputStream.close();
      }

      this.source.close();
   }

   private static class PrintingInputStream extends InputStream {
      private final InputStream src;

      private PrintingInputStream(InputStream var1) {
         this.src = var1;
      }

      @Override
      public int read() throws IOException {
         return this.src.read();
      }

      @Override
      public int available() throws IOException {
         return this.src.available();
      }

      @Override
      public void close() throws IOException {
         this.src.close();
      }
   }
}
