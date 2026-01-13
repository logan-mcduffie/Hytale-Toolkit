package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Size64;
import java.util.Set;

public interface ReferenceSet<K> extends ReferenceCollection<K>, Set<K> {
   @Override
   ObjectIterator<K> iterator();

   @Override
   default ObjectSpliterator<K> spliterator() {
      return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 65);
   }

   static <K> ReferenceSet<K> of() {
      return ReferenceSets.UNMODIFIABLE_EMPTY_SET;
   }

   static <K> ReferenceSet<K> of(K e) {
      return ReferenceSets.singleton(e);
   }

   static <K> ReferenceSet<K> of(K e0, K e1) {
      ReferenceArraySet<K> innerSet = new ReferenceArraySet<>(2);
      innerSet.add(e0);
      if (!innerSet.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else {
         return ReferenceSets.unmodifiable(innerSet);
      }
   }

   static <K> ReferenceSet<K> of(K e0, K e1, K e2) {
      ReferenceArraySet<K> innerSet = new ReferenceArraySet<>(3);
      innerSet.add(e0);
      if (!innerSet.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else if (!innerSet.add(e2)) {
         throw new IllegalArgumentException("Duplicate element: " + e2);
      } else {
         return ReferenceSets.unmodifiable(innerSet);
      }
   }

   @SafeVarargs
   static <K> ReferenceSet<K> of(K... a) {
      switch (a.length) {
         case 0:
            return of();
         case 1:
            return of(a[0]);
         case 2:
            return of(a[0], a[1]);
         case 3:
            return of(a[0], a[1], a[2]);
         default:
            ReferenceSet<K> innerSet = (ReferenceSet<K>)(a.length <= 4 ? new ReferenceArraySet<>(a.length) : new ReferenceOpenHashSet<>(a.length));

            for (K element : a) {
               if (!innerSet.add(element)) {
                  throw new IllegalArgumentException("Duplicate element: " + element);
               }
            }

            return ReferenceSets.unmodifiable(innerSet);
      }
   }
}
