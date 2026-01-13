package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Arrays;

public final class InputStreamWithMAC extends InputStream {
   private final InputStream base;
   private MACProvider macProvider;
   private byte[] mac;
   private boolean baseFinished;
   private int index;

   InputStreamWithMAC(InputStream var1, MACProvider var2) {
      this.base = var1;
      this.macProvider = var2;
      this.baseFinished = false;
      this.index = 0;
   }

   public InputStreamWithMAC(InputStream var1, byte[] var2) {
      this.base = var1;
      this.mac = var2;
      this.baseFinished = false;
      this.index = 0;
   }

   @Override
   public int read() throws IOException {
      if (!this.baseFinished) {
         int var1 = this.base.read();
         if (var1 < 0) {
            this.baseFinished = true;
            if (this.macProvider != null) {
               this.macProvider.init();
               this.mac = this.macProvider.getMAC();
            }

            return this.mac[this.index++] & 0xFF;
         } else {
            return var1;
         }
      } else {
         return this.index >= this.mac.length ? -1 : this.mac[this.index++] & 0xFF;
      }
   }

   public byte[] getMAC() {
      if (!this.baseFinished) {
         throw new IllegalStateException("input stream not fully processed");
      } else {
         return Arrays.clone(this.mac);
      }
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("input array is null");
      } else if (var2 < 0 || var1.length < var2 + var3) {
         throw new IndexOutOfBoundsException("invalid off(" + var2 + ") and len(" + var3 + ")");
      } else if (!this.baseFinished) {
         int var4 = this.base.read(var1, var2, var3);
         if (var4 < 0) {
            this.baseFinished = true;
            if (this.macProvider != null) {
               this.macProvider.init();
               this.mac = this.macProvider.getMAC();
            }

            if (var3 >= this.mac.length) {
               System.arraycopy(this.mac, 0, var1, var2, this.mac.length);
               this.index = this.mac.length;
               return this.mac.length;
            } else {
               System.arraycopy(this.mac, 0, var1, var2, var3);
               this.index += var3;
               return var3;
            }
         } else {
            return var4;
         }
      } else if (this.index >= this.mac.length) {
         return -1;
      } else if (var3 >= this.mac.length - this.index) {
         System.arraycopy(this.mac, this.index, var1, var2, this.mac.length - this.index);
         int var5 = this.mac.length - this.index;
         this.index = this.mac.length;
         return var5;
      } else {
         System.arraycopy(this.mac, this.index, var1, var2, var3);
         this.index += var3;
         return var3;
      }
   }
}
