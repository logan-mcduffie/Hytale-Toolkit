package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

public class XTEAEngine implements BlockCipher {
   private static final int rounds = 32;
   private static final int block_size = 8;
   private static final int delta = -1640531527;
   private int[] _S = new int[4];
   private int[] _sum0 = new int[32];
   private int[] _sum1 = new int[32];
   private boolean _initialised = false;
   private boolean _forEncryption;

   @Override
   public String getAlgorithmName() {
      return "XTEA";
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
         int var3 = 0;

         for (int var2 = 0; var2 < 4; var3 += 4) {
            this._S[var2] = this.bytesToInt(var1, var3);
            var2++;
         }

         var3 = 0;

         for (int var4 = 0; var4 < 32; var4++) {
            this._sum0[var4] = var3 + this._S[var3 & 3];
            var3 -= 1640531527;
            this._sum1[var4] = var3 + this._S[var3 >>> 11 & 3];
         }
      }
   }

   private int encryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = this.bytesToInt(var1, var2);
      int var6 = this.bytesToInt(var1, var2 + 4);

      for (int var7 = 0; var7 < 32; var7++) {
         var5 += (var6 << 4 ^ var6 >>> 5) + var6 ^ this._sum0[var7];
         var6 += (var5 << 4 ^ var5 >>> 5) + var5 ^ this._sum1[var7];
      }

      this.unpackInt(var5, var3, var4);
      this.unpackInt(var6, var3, var4 + 4);
      return 8;
   }

   private int decryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = this.bytesToInt(var1, var2);
      int var6 = this.bytesToInt(var1, var2 + 4);

      for (int var7 = 31; var7 >= 0; var7--) {
         var6 -= (var5 << 4 ^ var5 >>> 5) + var5 ^ this._sum1[var7];
         var5 -= (var6 << 4 ^ var6 >>> 5) + var6 ^ this._sum0[var7];
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
