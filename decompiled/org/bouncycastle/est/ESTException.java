package org.bouncycastle.est;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ESTException extends IOException {
   private Throwable cause;
   private InputStream body;
   private int statusCode;
   private static final long MAX_ERROR_BODY = 8192L;

   public ESTException(String var1) {
      this(var1, null);
   }

   public ESTException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
      this.body = null;
      this.statusCode = 0;
   }

   public ESTException(String var1, Throwable var2, int var3, InputStream var4) {
      super(var1);
      this.cause = var2;
      this.statusCode = var3;
      if (var4 != null) {
         byte[] var5 = new byte[8192];
         ByteArrayOutputStream var6 = new ByteArrayOutputStream();

         try {
            for (int var7 = var4.read(var5); var7 >= 0; var7 = var4.read(var5)) {
               if (var6.size() + var7 > 8192L) {
                  var7 = 8192 - var6.size();
                  var6.write(var5, 0, var7);
                  break;
               }

               var6.write(var5, 0, var7);
            }

            var6.flush();
            var6.close();
            this.body = new ByteArrayInputStream(var6.toByteArray());
            var4.close();
         } catch (Exception var8) {
         }
      } else {
         this.body = null;
      }
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }

   @Override
   public String getMessage() {
      return super.getMessage() + " HTTP Status Code: " + this.statusCode;
   }

   public InputStream getBody() {
      return this.body == null ? new InputStream() {
         @Override
         public int read() throws IOException {
            return -1;
         }
      } : this.body;
   }

   public int getStatusCode() {
      return this.statusCode;
   }
}
