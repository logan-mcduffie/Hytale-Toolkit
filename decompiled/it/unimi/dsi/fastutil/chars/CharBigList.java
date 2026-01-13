package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;

public interface CharBigList extends BigList<Character>, CharCollection, Comparable<BigList<? extends Character>> {
   CharBigListIterator iterator();

   CharBigListIterator listIterator();

   CharBigListIterator listIterator(long var1);

   @Override
   default CharSpliterator spliterator() {
      return CharSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
   }

   CharBigList subList(long var1, long var3);

   void getElements(long var1, char[][] var3, long var4, long var6);

   default void getElements(long from, char[] a, int offset, int length) {
      this.getElements(from, new char[][]{a}, offset, length);
   }

   void removeElements(long var1, long var3);

   void addElements(long var1, char[][] var3);

   void addElements(long var1, char[][] var3, long var4, long var6);

   default void setElements(char[][] a) {
      this.setElements(0L, a);
   }

   default void setElements(long index, char[][] a) {
      this.setElements(index, a, 0L, BigArrays.length(a));
   }

   default void setElements(long index, char[][] a, long offset, long length) {
      if (index < 0L) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size64()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
      } else {
         BigArrays.ensureOffsetLength(a, offset, length);
         if (index + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size64() + ")");
         } else {
            CharBigListIterator iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.nextChar();
               iter.set(BigArrays.get(a, offset + i++));
            }
         }
      }
   }

   void add(long var1, char var3);

   boolean addAll(long var1, CharCollection var3);

   char getChar(long var1);

   char removeChar(long var1);

   char set(long var1, char var3);

   long indexOf(char var1);

   long lastIndexOf(char var1);

   @Deprecated
   void add(long var1, Character var3);

   @Deprecated
   Character get(long var1);

   @Deprecated
   @Override
   long indexOf(Object var1);

   @Deprecated
   @Override
   long lastIndexOf(Object var1);

   @Deprecated
   Character remove(long var1);

   @Deprecated
   Character set(long var1, Character var3);

   default boolean addAll(long index, CharBigList l) {
      return this.addAll(index, (CharCollection)l);
   }

   default boolean addAll(CharBigList l) {
      return this.addAll(this.size64(), l);
   }

   default boolean addAll(long index, CharList l) {
      return this.addAll(index, (CharCollection)l);
   }

   default boolean addAll(CharList l) {
      return this.addAll(this.size64(), l);
   }
}
