package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Arrays;

public class BinaryTreeRootCalculator implements ERSRootNodeCalculator {
   private List<List<byte[]>> tree;

   @Override
   public byte[] computeRootHash(DigestCalculator var1, PartialHashtree[] var2) {
      SortedHashList var3 = new SortedHashList();

      for (int var4 = 0; var4 < var2.length; var4++) {
         byte[] var5 = ERSUtil.computeNodeHash(var1, var2[var4]);
         var3.add(var5);
      }

      Object var7 = var3.toList();
      this.tree = new ArrayList<>();
      this.tree.add((List<byte[]>)var7);
      ArrayList var8;
      if (var7.size() > 1) {
         do {
            var8 = new ArrayList(var7.size() / 2 + 1);

            for (byte var6 = 0; var6 <= var7.size() - 2; var6 += 2) {
               var8.add(ERSUtil.calculateBranchHash(var1, (byte[])var7.get(var6), (byte[])var7.get(var6 + 1)));
            }

            if (var7.size() % 2 == 1) {
               var8.add(var7.get(var7.size() - 1));
            }

            this.tree.add(var8);
            var7 = var8;
         } while (var8.size() > 1);
      }

      return (byte[])var7.get(0);
   }

   @Override
   public PartialHashtree[] computePathToRoot(DigestCalculator var1, PartialHashtree var2, int var3) {
      ArrayList var4 = new ArrayList();
      byte[] var5 = ERSUtil.computeNodeHash(var1, var2);
      var4.add(var2);

      for (int var6 = 0; var6 < this.tree.size() - 1; var6++) {
         if (var3 == this.tree.get(var6).size() - 1) {
            while (true) {
               List var7 = this.tree.get(var6 + 1);
               if (!Arrays.areEqual(var5, (byte[])var7.get(var7.size() - 1))) {
                  break;
               }

               var6++;
               var3 = this.tree.get(var6).size() - 1;
            }
         }

         byte[] var8;
         if ((var3 & 1) == 0) {
            var8 = this.tree.get(var6).get(var3 + 1);
         } else {
            var8 = this.tree.get(var6).get(var3 - 1);
         }

         var4.add(new PartialHashtree(var8));
         var5 = ERSUtil.calculateBranchHash(var1, var5, var8);
         var3 /= 2;
      }

      return var4.toArray(new PartialHashtree[0]);
   }

   @Override
   public byte[] recoverRootHash(DigestCalculator var1, PartialHashtree[] var2) {
      byte[] var3 = ERSUtil.computeNodeHash(var1, var2[0]);

      for (int var4 = 1; var4 < var2.length; var4++) {
         var3 = ERSUtil.calculateBranchHash(var1, var3, ERSUtil.computeNodeHash(var1, var2[var4]));
      }

      return var3;
   }
}
