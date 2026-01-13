package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Size64;
import java.util.Set;

public interface BooleanSet extends BooleanCollection, Set<Boolean> {
   @Override
   BooleanIterator iterator();

   @Override
   default BooleanSpliterator spliterator() {
      return BooleanSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 321);
   }

   boolean remove(boolean var1);

   @Deprecated
   @Override
   default boolean remove(Object o) {
      return BooleanCollection.super.remove(o);
   }

   @Deprecated
   @Override
   default boolean add(Boolean o) {
      return BooleanCollection.super.add(o);
   }

   @Deprecated
   @Override
   default boolean contains(Object o) {
      return BooleanCollection.super.contains(o);
   }

   @Deprecated
   @Override
   default boolean rem(boolean k) {
      return this.remove(k);
   }

   static BooleanSet of() {
      return BooleanSets.UNMODIFIABLE_EMPTY_SET;
   }

   static BooleanSet of(boolean e) {
      return BooleanSets.singleton(e);
   }

   static BooleanSet of(boolean e0, boolean e1) {
      BooleanArraySet innerSet = new BooleanArraySet(2);
      innerSet.add(e0);
      if (!innerSet.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else {
         return BooleanSets.unmodifiable(innerSet);
      }
   }

   static BooleanSet of(boolean e0, boolean e1, boolean e2) {
      BooleanArraySet innerSet = new BooleanArraySet(3);
      innerSet.add(e0);
      if (!innerSet.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else if (!innerSet.add(e2)) {
         throw new IllegalArgumentException("Duplicate element: " + e2);
      } else {
         return BooleanSets.unmodifiable(innerSet);
      }
   }

   static BooleanSet of(boolean... a) {
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
            BooleanSet innerSet = (BooleanSet)(a.length <= 4 ? new BooleanArraySet(a.length) : new BooleanOpenHashSet(a.length));

            for (boolean element : a) {
               if (!innerSet.add(element)) {
                  throw new IllegalArgumentException("Duplicate element: " + element);
               }
            }

            return BooleanSets.unmodifiable(innerSet);
      }
   }
}
