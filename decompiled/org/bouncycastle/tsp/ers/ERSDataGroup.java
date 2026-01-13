package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.operator.DigestCalculator;

public class ERSDataGroup extends ERSCachingData {
   protected List<ERSData> dataObjects;

   public ERSDataGroup(ERSData... var1) {
      this.dataObjects = new ArrayList<>(var1.length);
      this.dataObjects.addAll(Arrays.asList(var1));
   }

   public ERSDataGroup(List<ERSData> var1) {
      this.dataObjects = new ArrayList<>(var1.size());
      this.dataObjects.addAll(var1);
   }

   public ERSDataGroup(ERSData var1) {
      this.dataObjects = Collections.singletonList(var1);
   }

   public List<byte[]> getHashes(DigestCalculator var1, byte[] var2) {
      return ERSUtil.buildHashList(var1, this.dataObjects, var2);
   }

   @Override
   public byte[] getHash(DigestCalculator var1, byte[] var2) {
      List var3 = this.getHashes(var1, var2);
      return var3.size() > 1 ? ERSUtil.calculateDigest(var1, var3.iterator()) : (byte[])var3.get(0);
   }

   @Override
   protected byte[] calculateHash(DigestCalculator var1, byte[] var2) {
      List var3 = this.getHashes(var1, var2);
      if (var3.size() <= 1) {
         return (byte[])var3.get(0);
      } else {
         ArrayList var4 = new ArrayList(var3.size());

         for (int var5 = 0; var5 != var4.size(); var5++) {
            var4.add((byte[])var3.get(var5));
         }

         return ERSUtil.calculateDigest(var1, var4.iterator());
      }
   }

   public int size() {
      return this.dataObjects.size();
   }
}
