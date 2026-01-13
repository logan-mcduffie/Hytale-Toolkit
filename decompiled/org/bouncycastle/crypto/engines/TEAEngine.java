package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

public class TEAEngine implements BlockCipher {
   private static final int rounds = 32;
   private static final int block_size = 8;
   private static final int delta = -1640531527;
   private static final int d_sum = -957401312;
   private int _a;
   private int _b;
   private int _c;
   private int _d;
   private boolean _initialised = false;
   private boolean _forEncryption;

   @Override
   public String getAlgorithmName() {
      return "TEA";
   }

   @Override
   public int getBlockSize() {
      return 8;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (!(var2 instanceof KeyParameter)) {
         throw new IllegalArgumentException("invalid parameter passed to TEA init - " + var2.getClass().getName());
      } else {
         this._forEncryption = var1;
         this._initialised = true;
         KeyParameter var3 = (KeyParameter)var2;
         this.setKey(var3.getKey());
         CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 128, var2, Utils.getPurpose(var1)));
      }
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) {
      if (!this._initialised) {
         throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
      } else if (var2 + 8 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + 8 > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         return this._forEncryption ? this.encryptBlock(var1, var2, var3, var4) : this.decryptBlock(var1, var2, var3, var4);
      }
   }

   @Override
   public void reset() {
   }

   private void setKey(byte[] var1) {
      if (var1.length != 16) {
         throw new IllegalArgumentException("Key size must be 128 bits.");
      } else {
         this._a = this.bytesToInt(var1, 0);
         this._b = this.bytesToInt(var1, 4);
         this._c = this.bytesToInt(var1, 8);
         this._d = this.bytesToInt(var1, 12);
      }
   }

   private int encryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = this.bytesToInt(var1, var2);
      int var6 = this.bytesToInt(var1, var2 + 4);
      int var7 = 0;

      for (int var8 = 0; var8 != 32; var8++) {
         var7 -= 1640531527;
         var5 += (var6 << 4) + this._a ^ var6 + var7 ^ (var6 >>> 5) + this._b;
         var6 += (var5 << 4) + this._c ^ var5 + var7 ^ (var5 >>> 5) + this._d;
      }

      this.unpackInt(var5, var3, var4);
      this.unpackInt(var6, var3, var4 + 4);
      return 8;
   }

   private int decryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = this.bytesToInt(var1, var2);
      int var6 = this.bytesToInt(var1, var2 + 4);
      int var7 = -957401312;

      for (int var8 = 0; var8 != 32; var8++) {
         var6 -= (var5 << 4) + this._c ^ var5 + var7 ^ (var5 >>> 5) + this._d;
         var5 -= (var6 << 4) + this._a ^ var6 + var7 ^ (var6 >>> 5) + this._b;
         var7 += 1640531527;
      }

      this.unpackInt(var5, var3, var4);
      this.unpackInt(var6, var3, var4 + 4);
      return 8;
   }

   private int bytesToInt(byte[] var1, int var2) {
      return var1[var2++] << 24 | (var1[var2++] & 0xFF) << 16 | (var1[var2++] & 0xFF) << 8 | var1[var2] & 0xFF;
   }

   private void unpackInt(int var1, byte[] var2, int var3) {
      var2[var3++] = (byte)(var1 >>> 24);
      var2[var3++] = (byte)(var1 >>> 16);
      var2[var3++] = (byte)(var1 >>> 8);
      var2[var3] = (byte)var1;
   }
}
