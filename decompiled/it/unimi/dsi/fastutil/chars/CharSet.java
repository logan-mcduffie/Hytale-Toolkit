package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Size64;
import java.util.Set;

public interface CharSet extends CharCollection, Set<Character> {
   @Override
   CharIterator iterator();

   @Override
   default CharSpliterator spliterator() {
      return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 321);
   }

   boolean remove(char var1);

   @Deprecated
   @Override
   default boolean remove(Object o) {
      return CharCollection.super.remove(o);
   }

   @Deprecated
   @Override
   default boolean add(Character o) {
      return CharCollection.super.add(o);
   }

   @Deprecated
   @Override
   default boolean contains(Object o) {
      return CharCollection.super.contains(o);
   }

   @Deprecated
   @Override
   default boolean rem(char k) {
      return this.remove(k);
   }

   static CharSet of() {
      return CharSets.UNMODIFIABLE_EMPTY_SET;
   }

   static CharSet of(char e) {
      return CharSets.singleton(e);
   }

   static CharSet of(char e0, char e1) {
      CharArraySet innerSet = new CharArraySet(2);
      innerSet.add(e0);
      if (!innerSet.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else {
         return CharSets.unmodifiable(innerSet);
      }
   }

   static CharSet of(char e0, char e1, char e2) {
      CharArraySet innerSet = new CharArraySet(3);
      innerSet.add(e0);
      if (!innerSet.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else if (!innerSet.add(e2)) {
         throw new IllegalArgumentException("Duplicate element: " + e2);
      } else {
         return CharSets.unmodifiable(innerSet);
      }
   }

   static CharSet of(char... a) {
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
            CharSet innerSet = (CharSet)(a.length <= 4 ? new CharArraySet(a.length) : new CharOpenHashSet(a.length));

            for (char element : a) {
               if (!innerSet.add(element)) {
                  throw new IllegalArgumentException("Duplicate element: " + element);
               }
            }

            return CharSets.unmodifiable(innerSet);
      }
   }
}
