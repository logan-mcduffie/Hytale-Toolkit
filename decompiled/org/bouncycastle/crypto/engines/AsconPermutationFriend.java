package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.digests.ISAPDigest;
import org.bouncycastle.util.Longs;

public class AsconPermutationFriend {
   public static AsconPermutationFriend.AsconPermutation getAsconPermutation(ISAPDigest.Friend var0) {
      if (null == var0) {
         throw new NullPointerException("This method is only for use by ISAPDigest or Ascon Digest");
      } else {
         return new AsconPermutationFriend.AsconPermutation();
      }
   }

   public static class AsconPermutation {
      public long x0;
      public long x1;
      public long x2;
      public long x3;
      public long x4;

      AsconPermutation() {
      }

      public void round(long var1) {
         this.x2 ^= var1;
         long var3 = this.x0 ^ this.x4;
         long var5 = this.x1 ^ this.x2;
         long var7 = this.x1 | this.x2;
         long var9 = this.x3 ^ var7 ^ this.x0 ^ this.x1 & var3;
         long var11 = var3 ^ (var7 | this.x3) ^ this.x1 & this.x2 & this.x3;
         long var13 = var5 ^ this.x4 & ~this.x3;
         long var15 = (this.x0 | this.x3 ^ this.x4) ^ var5;
         long var17 = this.x3 ^ (this.x1 | this.x4) ^ this.x0 & this.x1;
         this.x0 = var9 ^ Longs.rotateRight(var9, 19) ^ Longs.rotateRight(var9, 28);
         this.x1 = var11 ^ Longs.rotateRight(var11, 39) ^ Longs.rotateRight(var11, 61);
         this.x2 = ~(var13 ^ Longs.rotateRight(var13, 1) ^ Longs.rotateRight(var13, 6));
         this.x3 = var15 ^ Longs.rotateRight(var15, 10) ^ Longs.rotateRight(var15, 17);
         this.x4 = var17 ^ Longs.rotateRight(var17, 7) ^ Longs.rotateRight(var17, 41);
      }

      public void p(int var1) {
         if (var1 == 12) {
            this.round(240L);
            this.round(225L);
            this.round(210L);
            this.round(195L);
         }

         if (var1 >= 8) {
            this.round(180L);
            this.round(165L);
         }

         this.round(150L);
         this.round(135L);
         this.round(120L);
         this.round(105L);
         this.round(90L);
         this.round(75L);
      }

      public void set(long var1, long var3, long var5, long var7, long var9) {
         this.x0 = var1;
         this.x1 = var3;
         this.x2 = var5;
         this.x3 = var7;
         this.x4 = var9;
      }
   }
}
