package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

public interface CharList extends List<Character>, Comparable<List<? extends Character>>, CharCollection {
   CharListIterator iterator();

   @Override
   default CharSpliterator spliterator() {
      return (CharSpliterator)(this instanceof RandomAccess
         ? new AbstractCharList.IndexBasedSpliterator(this, 0)
         : CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 16720));
   }

   CharListIterator listIterator();

   CharListIterator listIterator(int var1);

   CharList subList(int var1, int var2);

   void size(int var1);

   void getElements(int var1, char[] var2, int var3, int var4);

   void removeElements(int var1, int var2);

   void addElements(int var1, char[] var2);

   void addElements(int var1, char[] var2, int var3, int var4);

   default void setElements(char[] a) {
      this.setElements(0, a);
   }

   default void setElements(int index, char[] a) {
      this.setElements(index, a, 0, a.length);
   }

   default void setElements(int index, char[] a, int offset, int length) {
      if (index < 0) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
      } else {
         CharArrays.ensureOffsetLength(a, offset, length);
         if (index + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
         } else {
            CharListIterator iter = this.listIterator(index);
            int i = 0;

            while (i < length) {
               iter.nextChar();
               iter.set(a[offset + i++]);
            }
         }
      }
   }

   @Override
   boolean add(char var1);

   void add(int var1, char var2);

   @Deprecated
   default void add(int index, Character key) {
      this.add(index, key.charValue());
   }

   boolean addAll(int var1, CharCollection var2);

   char set(int var1, char var2);

   default void replaceAll(CharUnaryOperator operator) {
      CharListIterator iter = this.listIterator();

      while (iter.hasNext()) {
         iter.set(operator.apply(iter.nextChar()));
      }
   }

   default void replaceAll(IntUnaryOperator operator) {
      this.replaceAll(operator instanceof CharUnaryOperator ? (CharUnaryOperator)operator : x -> SafeMath.safeIntToChar(operator.applyAsInt(x)));
   }

   @Deprecated
   @Override
   default void replaceAll(UnaryOperator<Character> operator) {
      this.replaceAll(operator instanceof CharUnaryOperator ? (CharUnaryOperator)operator : operator::apply);
   }

   char getChar(int var1);

   int indexOf(char var1);

   int lastIndexOf(char var1);

   @Deprecated
   @Override
   default boolean contains(Object key) {
      return CharCollection.super.contains(key);
   }

   @Deprecated
   default Character get(int index) {
      return this.getChar(index);
   }

   @Deprecated
   @Override
   default int indexOf(Object o) {
      return this.indexOf(((Character)o).charValue());
   }

   @Deprecated
   @Override
   default int lastIndexOf(Object o) {
      return this.lastIndexOf(((Character)o).charValue());
   }

   @Deprecated
   @Override
   default boolean add(Character k) {
      return this.add(k.charValue());
   }

   char removeChar(int var1);

   @Deprecated
   @Override
   default boolean remove(Object key) {
      return CharCollection.super.remove(key);
   }

   @Deprecated
   default Character remove(int index) {
      return this.removeChar(index);
   }

   @Deprecated
   default Character set(int index, Character k) {
      return this.set(index, k.charValue());
   }

   default boolean addAll(int index, CharList l) {
      return this.addAll(index, (CharCollection)l);
   }

   default boolean addAll(CharList l) {
      return this.addAll(this.size(), l);
   }

   static CharList of() {
      return CharImmutableList.of();
   }

   static CharList of(char e) {
      return CharLists.singleton(e);
   }

   static CharList of(char e0, char e1) {
      return CharImmutableList.of(e0, e1);
   }

   static CharList of(char e0, char e1, char e2) {
      return CharImmutableList.of(e0, e1, e2);
   }

   static CharList of(char... a) {
      switch (a.length) {
         case 0:
            return of();
         case 1:
            return of(a[0]);
         default:
            return CharImmutableList.of(a);
      }
   }

   @Deprecated
   @Override
   default void sort(Comparator<? super Character> comparator) {
      this.sort(CharComparators.asCharComparator(comparator));
   }

   default void sort(CharComparator comparator) {
      if (comparator == null) {
         this.unstableSort(comparator);
      } else {
         char[] elements = this.toCharArray();
         CharArrays.stableSort(elements, comparator);
         this.setElements(elements);
      }
   }

   @Deprecated
   default void unstableSort(Comparator<? super Character> comparator) {
      this.unstableSort(CharComparators.asCharComparator(comparator));
   }

   default void unstableSort(CharComparator comparator) {
      char[] elements = this.toCharArray();
      if (comparator == null) {
         CharArrays.unstableSort(elements);
      } else {
         CharArrays.unstableSort(elements, comparator);
      }

      this.setElements(elements);
   }
}
