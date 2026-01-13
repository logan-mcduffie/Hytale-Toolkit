package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SortedIndexedHashList {
   private static final Comparator<byte[]> hashComp = new ByteArrayComparator();
   private final LinkedList<IndexedHash> baseList = new LinkedList<>();

   public IndexedHash getFirst() {
      return this.baseList.getFirst();
   }

   public void add(IndexedHash var1) {
      if (this.baseList.size() == 0) {
         this.baseList.addFirst(var1);
      } else if (hashComp.compare(var1.digest, this.baseList.get(0).digest) < 0) {
         this.baseList.addFirst(var1);
      } else {
         int var2 = 1;

         while (var2 < this.baseList.size() && hashComp.compare(this.baseList.get(var2).digest, var1.digest) <= 0) {
            var2++;
         }

         if (var2 == this.baseList.size()) {
            this.baseList.add(var1);
         } else {
            this.baseList.add(var2, var1);
         }
      }
   }

   public int size() {
      return this.baseList.size();
   }

   public List<IndexedHash> toList() {
      return new ArrayList<>(this.baseList);
   }
}
