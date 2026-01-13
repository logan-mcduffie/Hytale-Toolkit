package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.util.Encodable;

public class Composer {
   private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

   private Composer() {
   }

   public static Composer compose() {
      return new Composer();
   }

   public Composer u64str(long var1) {
      this.u32str((int)(var1 >>> 32));
      this.u32str((int)var1);
      return this;
   }

   public Composer u32str(int var1) {
      this.bos.write((byte)(var1 >>> 24));
      this.bos.write((byte)(var1 >>> 16));
      this.bos.write((byte)(var1 >>> 8));
      this.bos.write((byte)var1);
      return this;
   }

   public Composer u16str(int var1) {
      var1 &= 65535;
      this.bos.write((byte)(var1 >>> 8));
      this.bos.write((byte)var1);
      return this;
   }

   public Composer bytes(Encodable[] var1) {
      try {
         for (Encodable var5 : var1) {
            this.bos.write(var5.getEncoded());
         }

         return this;
      } catch (Exception var6) {
         throw new RuntimeException(var6.getMessage(), var6);
      }
   }

   public Composer bytes(Encodable var1) {
      try {
         this.bos.write(var1.getEncoded());
         return this;
      } catch (Exception var3) {
         throw new RuntimeException(var3.getMessage(), var3);
      }
   }

   public Composer pad(int var1, int var2) {
      while (var2 >= 0) {
         try {
            this.bos.write(var1);
         } catch (Exception var4) {
            throw new RuntimeException(var4.getMessage(), var4);
         }

         var2--;
      }

      return this;
   }

   public Composer bytes(byte[][] var1) {
      try {
         for (byte[] var5 : var1) {
            this.bos.write(var5);
         }

         return this;
      } catch (Exception var6) {
         throw new RuntimeException(var6.getMessage(), var6);
      }
   }

   public Composer bytes(byte[][] var1, int var2, int var3) {
      try {
         for (int var4 = var2; var4 != var3; var4++) {
            this.bos.write(var1[var4]);
         }

         return this;
      } catch (Exception var5) {
         throw new RuntimeException(var5.getMessage(), var5);
      }
   }

   public Composer bytes(byte[] var1) {
      try {
         this.bos.write(var1);
         return this;
      } catch (Exception var3) {
         throw new RuntimeException(var3.getMessage(), var3);
      }
   }

   public Composer bytes(byte[] var1, int var2, int var3) {
      try {
         this.bos.write(var1, var2, var3);
         return this;
      } catch (Exception var5) {
         throw new RuntimeException(var5.getMessage(), var5);
      }
   }

   public byte[] build() {
      return this.bos.toByteArray();
   }

   public Composer padUntil(int var1, int var2) {
      while (this.bos.size() < var2) {
         this.bos.write(var1);
      }

      return this;
   }

   public Composer bool(boolean var1) {
      this.bos.write(var1 ? 1 : 0);
      return this;
   }
}
