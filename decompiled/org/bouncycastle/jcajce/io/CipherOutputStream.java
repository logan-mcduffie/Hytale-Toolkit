package org.bouncycastle.jcajce.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import org.bouncycastle.crypto.io.InvalidCipherTextIOException;

public class CipherOutputStream extends FilterOutputStream {
   private final Cipher cipher;
   private final byte[] oneByte = new byte[1];

   public CipherOutputStream(OutputStream var1, Cipher var2) {
      super(var1);
      this.cipher = var2;
   }

   @Override
   public void write(int var1) throws IOException {
      this.oneByte[0] = (byte)var1;
      this.write(this.oneByte, 0, 1);
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      byte[] var4 = this.cipher.update(var1, var2, var3);
      if (var4 != null) {
         this.out.write(var4);
      }
   }

   @Override
   public void flush() throws IOException {
      this.out.flush();
   }

   @Override
   public void close() throws IOException {
      Object var1 = null;

      try {
         byte[] var2 = this.cipher.doFinal();
         if (var2 != null) {
            this.out.write(var2);
         }
      } catch (GeneralSecurityException var3) {
         var1 = new InvalidCipherTextIOException("Error during cipher finalisation", var3);
      } catch (Exception var4) {
         var1 = new IOException("Error closing stream: " + var4);
      }

      try {
         this.flush();
         this.out.close();
      } catch (IOException var5) {
         if (var1 == null) {
            var1 = var5;
         }
      }

      if (var1 != null) {
         throw var1;
      }
   }
}
