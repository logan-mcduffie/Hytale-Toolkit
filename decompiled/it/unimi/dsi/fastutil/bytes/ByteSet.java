package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Size64;
import java.util.Set;

public interface ByteSet extends ByteCollection, Set<Byte> {
   @Override
   ByteIterator iterator();

   @Override
   default ByteSpliterator spliterator() {
      return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 321);
   }

   boolean remove(byte var1);

   @Deprecated
   @Override
   default boolean remove(Object o) {
      return ByteCollection.super.remove(o);
   }

   @Deprecated
   @Override
   default boolean add(Byte o) {
      return ByteCollection.super.add(o);
   }

   @Deprecated
   @Override
   default boolean contains(Object o) {
      return ByteCollection.super.contains(o);
   }

   @Deprecated
   @Override
   default boolean rem(byte k) {
      return this.remove(k);
   }

   static ByteSet of() {
      return ByteSets.UNMODIFIABLE_EMPTY_SET;
   }

   static ByteSet of(byte e) {
      return ByteSets.singleton(e);
   }

   static ByteSet of(byte e0, byte e1) {
      ByteArraySet innerSet = new ByteArraySet(2);
      innerSet.add(e0);
      if (!innerSet.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else {
         return ByteSets.unmodifiable(innerSet);
      }
   }

   static ByteSet of(byte e0, byte e1, byte e2) {
      ByteArraySet innerSet = new ByteArraySet(3);
      innerSet.add(e0);
      if (!innerSet.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else if (!innerSet.add(e2)) {
         throw new IllegalArgumentException("Duplicate element: " + e2);
      } else {
         return ByteSets.unmodifiable(innerSet);
      }
   }

   static ByteSet of(byte... a) {
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
            ByteSet innerSet = (ByteSet)(a.length <= 4 ? new ByteArraySet(a.length) : new ByteOpenHashSet(a.length));

            for (byte element : a) {
               if (!innerSet.add(element)) {
                  throw new IllegalArgumentException("Duplicate element: " + element);
               }
            }

            return ByteSets.unmodifiable(innerSet);
      }
   }
}
