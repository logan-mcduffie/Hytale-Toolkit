package org.bouncycastle.crypto.prng.drbg;

import java.util.Hashtable;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;

public class HashSP800DRBG implements SP80090DRBG {
   private static final byte[] ONE = new byte[]{1};
   private static final long RESEED_MAX = 140737488355328L;
   private static final int MAX_BITS_REQUEST = 262144;
   private static final Hashtable seedlens = new Hashtable();
   private Digest _digest;
   private byte[] _V;
   private byte[] _C;
   private long _reseedCounter;
   private EntropySource _entropySource;
   private int _securityStrength;
   private int _seedLength;

   public HashSP800DRBG(Digest var1, int var2, EntropySource var3, byte[] var4, byte[] var5) {
      if (var2 > Utils.getMaxSecurityStrength(var1)) {
         throw new IllegalArgumentException("Requested security strength is not supported by the derivation function");
      } else if (var3.entropySize() < var2) {
         throw new IllegalArgumentException("Not enough entropy for security strength required");
      } else {
         this._digest = var1;
         this._entropySource = var3;
         this._securityStrength = var2;
         this._seedLength = (Integer)seedlens.get(var1.getAlgorithmName());
         byte[] var6 = this.getEntropy();
         byte[] var7 = Arrays.concatenate(var6, var5, var4);
         byte[] var8 = Utils.hash_df(this._digest, var7, this._seedLength);
         this._V = var8;
         byte[] var9 = new byte[this._V.length + 1];
         System.arraycopy(this._V, 0, var9, 1, this._V.length);
         this._C = Utils.hash_df(this._digest, var9, this._seedLength);
         this._reseedCounter = 1L;
      }
   }

   @Override
   public int getBlockSize() {
      return this._digest.getDigestSize() * 8;
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
            byte[] var5 = new byte[1 + this._V.length + var2.length];
            var5[0] = 2;
            System.arraycopy(this._V, 0, var5, 1, this._V.length);
            System.arraycopy(var2, 0, var5, 1 + this._V.length, var2.length);
            byte[] var6 = this.hash(var5);
            this.addTo(this._V, var6);
         }

         byte[] var9 = this.hashgen(this._V, var4);
         byte[] var10 = new byte[this._V.length + 1];
         System.arraycopy(this._V, 0, var10, 1, this._V.length);
         var10[0] = 3;
         byte[] var7 = this.hash(var10);
         this.addTo(this._V, var7);
         this.addTo(this._V, this._C);
         byte[] var8 = new byte[]{
            (byte)(this._reseedCounter >> 24), (byte)(this._reseedCounter >> 16), (byte)(this._reseedCounter >> 8), (byte)this._reseedCounter
         };
         this.addTo(this._V, var8);
         this._reseedCounter++;
         System.arraycopy(var9, 0, var1, 0, var1.length);
         return var4;
      }
   }

   private byte[] getEntropy() {
      byte[] var1 = this._entropySource.getEntropy();
      if (var1.length < (this._securityStrength + 7) / 8) {
         throw new IllegalStateException("Insufficient entropy provided by entropy source");
      } else {
         return var1;
      }
   }

   private void addTo(byte[] var1, byte[] var2) {
      int var3 = 0;

      for (int var4 = 1; var4 <= var2.length; var4++) {
         int var5 = (var1[var1.length - var4] & 255) + (var2[var2.length - var4] & 255) + var3;
         var3 = var5 > 255 ? 1 : 0;
         var1[var1.length - var4] = (byte)var5;
      }

      for (int var6 = var2.length + 1; var6 <= var1.length; var6++) {
         int var7 = (var1[var1.length - var6] & 255) + var3;
         var3 = var7 > 255 ? 1 : 0;
         var1[var1.length - var6] = (byte)var7;
      }
   }

   @Override
   public void reseed(byte[] var1) {
      byte[] var2 = this.getEntropy();
      byte[] var3 = Arrays.concatenate(ONE, this._V, var2, var1);
      byte[] var4 = Utils.hash_df(this._digest, var3, this._seedLength);
      this._V = var4;
      byte[] var5 = new byte[this._V.length + 1];
      var5[0] = 0;
      System.arraycopy(this._V, 0, var5, 1, this._V.length);
      this._C = Utils.hash_df(this._digest, var5, this._seedLength);
      this._reseedCounter = 1L;
   }

   private byte[] hash(byte[] var1) {
      byte[] var2 = new byte[this._digest.getDigestSize()];
      this.doHash(var1, var2);
      return var2;
   }

   private void doHash(byte[] var1, byte[] var2) {
      this._digest.update(var1, 0, var1.length);
      this._digest.doFinal(var2, 0);
   }

   private byte[] hashgen(byte[] var1, int var2) {
      int var3 = this._digest.getDigestSize();
      int var4 = var2 / 8 / var3;
      byte[] var5 = new byte[var1.length];
      System.arraycopy(var1, 0, var5, 0, var1.length);
      byte[] var6 = new byte[var2 / 8];
      byte[] var7 = new byte[this._digest.getDigestSize()];

      for (int var8 = 0; var8 <= var4; var8++) {
         this.doHash(var5, var7);
         int var9 = var6.length - var8 * var7.length > var7.length ? var7.length : var6.length - var8 * var7.length;
         System.arraycopy(var7, 0, var6, var8 * var7.length, var9);
         this.addTo(var5, ONE);
      }

      return var6;
   }

   static {
      seedlens.put("SHA-1", Integers.valueOf(440));
      seedlens.put("SHA-224", Integers.valueOf(440));
      seedlens.put("SHA-256", Integers.valueOf(440));
      seedlens.put("SHA-512/256", Integers.valueOf(440));
      seedlens.put("SHA-512/224", Integers.valueOf(440));
      seedlens.put("SHA-384", Integers.valueOf(888));
      seedlens.put("SHA-512", Integers.valueOf(888));
   }
}
