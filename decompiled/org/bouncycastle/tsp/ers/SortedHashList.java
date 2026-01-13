package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SortedHashList {
   private static final Comparator<byte[]> hashComp = new ByteArrayComparator();
   private final LinkedList<byte[]> baseList = new LinkedList<>();

   public byte[] getFirst() {
      return this.baseList.getFirst();
   }

   public void add(byte[] var1) {
      if (this.baseList.size() == 0) {
         this.baseList.addFirst(var1);
      } else if (hashComp.compare(var1, this.baseList.get(0)) < 0) {
         this.baseList.addFirst(var1);
      } else {
         int var2 = 1;

         while (var2 < this.baseList.size() && hashComp.compare(this.baseList.get(var2), var1) <= 0) {
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

   public List<byte[]> toList() {
      return new ArrayList<>(this.baseList);
   }
}
