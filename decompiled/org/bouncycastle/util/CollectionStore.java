package org.bouncycastle.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CollectionStore<T> implements Store<T>, Iterable<T> {
   private Collection<T> _local;

   public CollectionStore(Collection<T> var1) {
      this._local = new ArrayList<>(var1);
   }

   @Override
   public Collection<T> getMatches(Selector<T> var1) {
      if (var1 == null) {
         return new ArrayList<>(this._local);
      } else {
         ArrayList var2 = new ArrayList();

         for (Object var4 : this._local) {
            if (var1.match(var4)) {
               var2.add(var4);
            }
         }

         return var2;
      }
   }

   @Override
   public Iterator<T> iterator() {
      return this.getMatches(null).iterator();
   }
}
