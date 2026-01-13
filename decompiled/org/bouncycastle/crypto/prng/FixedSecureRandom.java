package org.bouncycastle.crypto.prng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

public class FixedSecureRandom extends SecureRandom {
   private byte[] _data;
   private int _index;
   private int _intPad;

   public FixedSecureRandom(byte[] var1) {
      this(false, new byte[][]{var1});
   }

   public FixedSecureRandom(byte[][] var1) {
      this(false, var1);
   }

   public FixedSecureRandom(boolean var1, byte[] var2) {
      this(var1, new byte[][]{var2});
   }

   public FixedSecureRandom(boolean var1, byte[][] var2) {
      ByteArrayOutputStream var3 = new ByteArrayOutputStream();

      for (int var4 = 0; var4 != var2.length; var4++) {
         try {
            var3.write(var2[var4]);
         } catch (IOException var6) {
            throw new IllegalArgumentException("can't save value array.");
         }
      }

      this._data = var3.toByteArray();
      if (var1) {
         this._intPad = this._data.length % 4;
      }
   }

   @Override
   public void nextBytes(byte[] var1) {
      System.arraycopy(this._data, this._index, var1, 0, var1.length);
      this._index += var1.length;
   }

   @Override
   public byte[] generateSeed(int var1) {
      byte[] var2 = new byte[var1];
      this.nextBytes(var2);
      return var2;
   }

   @Override
   public int nextInt() {
      int var1 = 0;
      var1 |= this.nextValue() << 24;
      var1 |= this.nextValue() << 16;
      if (this._intPad == 2) {
         this._intPad--;
      } else {
         var1 |= this.nextValue() << 8;
      }

      if (this._intPad == 1) {
         this._intPad--;
      } else {
         var1 |= this.nextValue();
      }

      return var1;
   }

   @Override
   public long nextLong() {
      long var1 = 0L;
      var1 |= (long)this.nextValue() << 56;
      var1 |= (long)this.nextValue() << 48;
      var1 |= (long)this.nextValue() << 40;
      var1 |= (long)this.nextValue() << 32;
      var1 |= (long)this.nextValue() << 24;
      var1 |= (long)this.nextValue() << 16;
      var1 |= (long)this.nextValue() << 8;
      return var1 | this.nextValue();
   }

   public boolean isExhausted() {
      return this._index == this._data.length;
   }

   private int nextValue() {
      return this._data[this._index++] & 0xFF;
   }
}
