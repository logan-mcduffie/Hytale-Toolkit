package org.bouncycastle.pqc.crypto.crystals.dilithium;

class Reduce {
   static int montgomeryReduce(long var0) {
      int var2 = (int)(var0 * 58728449L);
      return (int)(var0 - var2 * 8380417L >>> 32);
   }

   static int reduce32(int var0) {
      int var1 = var0 + 4194304 >> 23;
      return var0 - var1 * 8380417;
   }

   static int conditionalAddQ(int var0) {
      return var0 + (var0 >> 31 & 8380417);
   }
}
