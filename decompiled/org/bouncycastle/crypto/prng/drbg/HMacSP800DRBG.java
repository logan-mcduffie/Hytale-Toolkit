package org.bouncycastle.crypto.prng.drbg;

import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.util.Arrays;

public class HMacSP800DRBG implements SP80090DRBG {
   private static final long RESEED_MAX = 140737488355328L;
   private static final int MAX_BITS_REQUEST = 262144;
   private byte[] _K;
   private byte[] _V;
   private long _reseedCounter;
   private EntropySource _entropySource;
   private Mac _hMac;
   private int _securityStrength;

   public HMacSP800DRBG(Mac var1, int var2, EntropySource var3, byte[] var4, byte[] var5) {
      if (var2 > Utils.getMaxSecurityStrength(var1)) {
         throw new IllegalArgumentException("Requested security strength is not supported by the derivation function");
      } else if (var3.entropySize() < var2) {
         throw new IllegalArgumentException("Not enough entropy for security strength required");
      } else {
         this._securityStrength = var2;
         this._entropySource = var3;
         this._hMac = var1;
         byte[] var6 = this.getEntropy();
         byte[] var7 = Arrays.concatenate(var6, var5, var4);
         this._K = new byte[var1.getMacSize()];
         this._V = new byte[this._K.length];
         Arrays.fill(this._V, (byte)1);
         this.hmac_DRBG_Update(var7);
         this._reseedCounter = 1L;
      }
   }

   private void hmac_DRBG_Update(byte[] var1) {
      this.hmac_DRBG_Update_Func(var1, (byte)0);
      if (var1 != null) {
         this.hmac_DRBG_Update_Func(var1, (byte)1);
      }
   }

   private void hmac_DRBG_Update_Func(byte[] var1, byte var2) {
      this._hMac.init(new KeyParameter(this._K));
      this._hMac.update(this._V, 0, this._V.length);
      this._hMac.update(var2);
      if (var1 != null) {
         this._hMac.update(var1, 0, var1.length);
      }

      this._hMac.doFinal(this._K, 0);
      this._hMac.init(new KeyParameter(this._K));
      this._hMac.update(this._V, 0, this._V.length);
      this._hMac.doFinal(this._V, 0);
   }

   @Override
   public int getBlockSize() {
      return this._V.length * 8;
   }

   @Override
   public int generate(byte[] var1, byte[] var2, boolean var3) {
      int var4 = var1.length * 8;
      if (var4 > 262144) {
         throw new IllegalArgumentException("Number of bits per request limited to 262144");
      } else if (this._reseedCounter > 140737488355328L) {
         return -1;
      } else {
         if (var3) {
            this.reseed(var2);
            var2 = null;
         }

         if (var2 != null) {
            this.hmac_DRBG_Update(var2);
         }

         byte[] var5 = new byte[var1.length];
         int var6 = var1.length / this._V.length;
         this._hMac.init(new KeyParameter(this._K));

         for (int var7 = 0; var7 < var6; var7++) {
            this._hMac.update(this._V, 0, this._V.length);
            this._hMac.doFinal(this._V, 0);
            System.arraycopy(this._V, 0, var5, var7 * this._V.length, this._V.length);
         }

         if (var6 * this._V.length < var5.length) {
            this._hMac.update(this._V, 0, this._V.length);
            this._hMac.doFinal(this._V, 0);
            System.arraycopy(this._V, 0, var5, var6 * this._V.length, var5.length - var6 * this._V.length);
         }

         this.hmac_DRBG_Update(var2);
         this._reseedCounter++;
         System.arraycopy(var5, 0, var1, 0, var1.length);
         return var4;
      }
   }

   @Override
   public void reseed(byte[] var1) {
      byte[] var2 = this.getEntropy();
      byte[] var3 = Arrays.concatenate(var2, var1);
      this.hmac_DRBG_Update(var3);
      this._reseedCounter = 1L;
   }

   private byte[] getEntropy() {
      byte[] var1 = this._entropySource.getEntropy();
      if (var1.length < (this._securityStrength + 7) / 8) {
         throw new IllegalStateException("Insufficient entropy provided by entropy source");
      } else {
         return var1;
      }
   }
}
