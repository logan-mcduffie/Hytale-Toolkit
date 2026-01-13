package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Size64;
import java.util.Set;

public interface ShortSet extends ShortCollection, Set<Short> {
   @Override
   ShortIterator iterator();

   @Override
   default ShortSpliterator spliterator() {
      return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 321);
   }

   boolean remove(short var1);

   @Deprecated
   @Override
   default boolean remove(Object o) {
      return ShortCollection.super.remove(o);
   }

   @Deprecated
   @Override
   default boolean add(Short o) {
      return ShortCollection.super.add(o);
   }

   @Deprecated
   @Override
   default boolean contains(Object o) {
      return ShortCollection.super.contains(o);
   }

   @Deprecated
   @Override
   default boolean rem(short k) {
      return this.remove(k);
   }

   static ShortSet of() {
      return ShortSets.UNMODIFIABLE_EMPTY_SET;
   }

   static ShortSet of(short e) {
      return ShortSets.singleton(e);
   }

   static ShortSet of(short e0, short e1) {
      ShortArraySet innerSet = new ShortArraySet(2);
      innerSet.add(e0);
      if (!innerSet.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else {
         return ShortSets.unmodifiable(innerSet);
      }
   }

   static ShortSet of(short e0, short e1, short e2) {
      ShortArraySet innerSet = new ShortArraySet(3);
      innerSet.add(e0);
      if (!innerSet.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else if (!innerSet.add(e2)) {
         throw new IllegalArgumentException("Duplicate element: " + e2);
      } else {
         return ShortSets.unmodifiable(innerSet);
      }
   }

   static ShortSet of(short... a) {
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
            ShortSet innerSet = (ShortSet)(a.length <= 4 ? new ShortArraySet(a.length) : new ShortOpenHashSet(a.length));

            for (short element : a) {
               if (!innerSet.add(element)) {
                  throw new IllegalArgumentException("Duplicate element: " + element);
               }
            }

            return ShortSets.unmodifiable(innerSet);
      }
   }
}
