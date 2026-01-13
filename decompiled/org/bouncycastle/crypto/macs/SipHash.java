package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class SipHash implements Mac {
   protected final int c;
   protected final int d;
   protected long k0;
   protected long k1;
   protected long v0;
   protected long v1;
   protected long v2;
   protected long v3;
   protected long m = 0L;
   protected int wordPos = 0;
   protected int wordCount = 0;

   public SipHash() {
      this.c = 2;
      this.d = 4;
   }

   public SipHash(int var1, int var2) {
      this.c = var1;
      this.d = var2;
   }

   @Override
   public String getAlgorithmName() {
      return "SipHash-" + this.c + "-" + this.d;
   }

   @Override
   public int getMacSize() {
      return 8;
   }

   @Override
   public void init(CipherParameters var1) throws IllegalArgumentException {
      if (!(var1 instanceof KeyParameter)) {
         throw new IllegalArgumentException("'params' must be an instance of KeyParameter");
      } else {
         KeyParameter var2 = (KeyParameter)var1;
         byte[] var3 = var2.getKey();
         if (var3.length != 16) {
            throw new IllegalArgumentException("'params' must be a 128-bit key");
         } else {
            this.k0 = Pack.littleEndianToLong(var3, 0);
            this.k1 = Pack.littleEndianToLong(var3, 8);
            this.reset();
         }
      }
   }

   @Override
   public void update(byte var1) throws IllegalStateException {
      this.m >>>= 8;
      this.m |= (var1 & 255L) << 56;
      if (++this.wordPos == 8) {
         this.processMessageWord();
         this.wordPos = 0;
      }
   }

   @Override
   public void update(byte[] var1, int var2, int var3) throws DataLengthException, IllegalStateException {
      int var4 = 0;
      int var5 = var3 & -8;
      if (this.wordPos != 0) {
         for (int var6 = this.wordPos << 3; var4 < var5; var4 += 8) {
            long var7 = Pack.littleEndianToLong(var1, var2 + var4);
            this.m = var7 << var6 | this.m >>> -var6;
            this.processMessageWord();
            this.m = var7;
         }

         for (; var4 < var3; var4++) {
            this.m >>>= 8;
            this.m = this.m | (var1[var2 + var4] & 255L) << 56;
            if (++this.wordPos == 8) {
               this.processMessageWord();
               this.wordPos = 0;
            }
         }
      } else {
         while (var4 < var5) {
            this.m = Pack.littleEndianToLong(var1, var2 + var4);
            this.processMessageWord();
            var4 += 8;
         }

         while (var4 < var3) {
            this.m >>>= 8;
            this.m = this.m | (var1[var2 + var4] & 255L) << 56;
            var4++;
         }

         this.wordPos = var3 - var5;
      }
   }

   public long doFinal() throws DataLengthException, IllegalStateException {
      this.m = this.m >>> (7 - this.wordPos << 3);
      this.m >>>= 8;
      this.m = this.m | ((this.wordCount << 3) + this.wordPos & 255L) << 56;
      this.processMessageWord();
      this.v2 ^= 255L;
      this.applySipRounds(this.d);
      long var1 = this.v0 ^ this.v1 ^ this.v2 ^ this.v3;
      this.reset();
      return var1;
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException {
      long var3 = this.doFinal();
      Pack.longToLittleEndian(var3, var1, var2);
      return 8;
   }

   @Override
   public void reset() {
      this.v0 = this.k0 ^ 8317987319222330741L;
      this.v1 = this.k1 ^ 7237128888997146477L;
      this.v2 = this.k0 ^ 7816392313619706465L;
      this.v3 = this.k1 ^ 8387220255154660723L;
      this.m = 0L;
      this.wordPos = 0;
      this.wordCount = 0;
   }

   protected void processMessageWord() {
      this.wordCount++;
      this.v3 = this.v3 ^ this.m;
      this.applySipRounds(this.c);
      this.v0 = this.v0 ^ this.m;
   }

   protected void applySipRounds(int var1) {
      long var2 = this.v0;
      long var4 = this.v1;
      long var6 = this.v2;
      long var8 = this.v3;

      for (int var10 = 0; var10 < var1; var10++) {
         long var11 = var2 + var4;
         var6 += var8;
         var4 = rotateLeft(var4, 13);
         var8 = rotateLeft(var8, 16);
         var4 ^= var11;
         var8 ^= var6;
         long var12 = rotateLeft(var11, 32);
         var6 += var4;
         var2 = var12 + var8;
         var4 = rotateLeft(var4, 17);
         var8 = rotateLeft(var8, 21);
         var4 ^= var6;
         var8 ^= var2;
         var6 = rotateLeft(var6, 32);
      }

      this.v0 = var2;
      this.v1 = var4;
      this.v2 = var6;
      this.v3 = var8;
   }

   protected static long rotateLeft(long var0, int var2) {
      return var0 << var2 | var0 >>> -var2;
   }
}
