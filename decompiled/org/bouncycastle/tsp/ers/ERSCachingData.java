package org.bouncycastle.tsp.ers;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Arrays;

public abstract class ERSCachingData implements ERSData {
   private Map<ERSCachingData.CacheIndex, byte[]> preCalcs = new HashMap<>();

   @Override
   public byte[] getHash(DigestCalculator var1, byte[] var2) {
      ERSCachingData.CacheIndex var3 = new ERSCachingData.CacheIndex(var1.getAlgorithmIdentifier(), var2);
      if (this.preCalcs.containsKey(var3)) {
         return this.preCalcs.get(var3);
      } else {
         byte[] var4 = this.calculateHash(var1, var2);
         this.preCalcs.put(var3, var4);
         return var4;
      }
   }

   protected abstract byte[] calculateHash(DigestCalculator var1, byte[] var2);

   private static class CacheIndex {
      final AlgorithmIdentifier algId;
      final byte[] chainHash;

      private CacheIndex(AlgorithmIdentifier var1, byte[] var2) {
         this.algId = var1;
         this.chainHash = var2;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof ERSCachingData.CacheIndex)) {
            return false;
         } else {
            ERSCachingData.CacheIndex var2 = (ERSCachingData.CacheIndex)var1;
            return this.algId.equals(var2.algId) && Arrays.areEqual(this.chainHash, var2.chainHash);
         }
      }

      @Override
      public int hashCode() {
         int var1 = this.algId.hashCode();
         return 31 * var1 + Arrays.hashCode(this.chainHash);
      }
   }
}
