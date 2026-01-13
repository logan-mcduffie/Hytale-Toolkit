package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

public interface ByteList extends List<Byte>, Comparable<List<? extends Byte>>, ByteCollection {
   ByteListIterator iterator();

   @Override
   default ByteSpliterator spliterator() {
      return (ByteSpliterator)(this instanceof RandomAccess
         ? new AbstractByteList.IndexBasedSpliterator(this, 0)
         : ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 16720));
   }

   ByteListIterator listIterator();

   ByteListIterator listIterator(int var1);

   ByteList subList(int var1, int var2);

   void size(int var1);

   void getElements(int var1, byte[] var2, int var3, int var4);

   void removeElements(int var1, int var2);

   void addElements(int var1, byte[] var2);

   void addElements(int var1, byte[] var2, int var3, int var4);

   default void setElements(byte[] a) {
      this.setElements(0, a);
   }

   default void setElements(int index, byte[] a) {
      this.setElements(index, a, 0, a.length);
   }

   default void setElements(int index, byte[] a, int offset, int length) {
      if (index < 0) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
      } else {
         ByteArrays.ensureOffsetLength(a, offset, length);
         if (index + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
         } else {
            ByteListIterator iter = this.listIterator(index);
            int i = 0;

            while (i < length) {
               iter.nextByte();
               iter.set(a[offset + i++]);
            }
         }
      }
   }

   @Override
   boolean add(byte var1);

   void add(int var1, byte var2);

   @Deprecated
   default void add(int index, Byte key) {
      this.add(index, key.byteValue());
   }

   boolean addAll(int var1, ByteCollection var2);

   byte set(int var1, byte var2);

   default void replaceAll(ByteUnaryOperator operator) {
      ByteListIterator iter = this.listIterator();

      while (iter.hasNext()) {
         iter.set(operator.apply(iter.nextByte()));
      }
   }

   default void replaceAll(IntUnaryOperator operator) {
      this.replaceAll(operator instanceof ByteUnaryOperator ? (ByteUnaryOperator)operator : x -> SafeMath.safeIntToByte(operator.applyAsInt(x)));
   }

   @Deprecated
   @Override
   default void replaceAll(UnaryOperator<Byte> operator) {
      this.replaceAll(operator instanceof ByteUnaryOperator ? (ByteUnaryOperator)operator : operator::apply);
   }

   byte getByte(int var1);

   int indexOf(byte var1);

   int lastIndexOf(byte var1);

   @Deprecated
   @Override
   default boolean contains(Object key) {
      return ByteCollection.super.contains(key);
   }

   @Deprecated
   default Byte get(int index) {
      return this.getByte(index);
   }

   @Deprecated
   @Override
   default int indexOf(Object o) {
      return this.indexOf(((Byte)o).byteValue());
   }

   @Deprecated
   @Override
   default int lastIndexOf(Object o) {
      return this.lastIndexOf(((Byte)o).byteValue());
   }

   @Deprecated
   @Override
   default boolean add(Byte k) {
      return this.add(k.byteValue());
   }

   byte removeByte(int var1);

   @Deprecated
   @Override
   default boolean remove(Object key) {
      return ByteCollection.super.remove(key);
   }

   @Deprecated
   default Byte remove(int index) {
      return this.removeByte(index);
   }

   @Deprecated
   default Byte set(int index, Byte k) {
      return this.set(index, k.byteValue());
   }

   default boolean addAll(int index, ByteList l) {
      return this.addAll(index, (ByteCollection)l);
   }

   default boolean addAll(ByteList l) {
      return this.addAll(this.size(), l);
   }

   static ByteList of() {
      return ByteImmutableList.of();
   }

   static ByteList of(byte e) {
      return ByteLists.singleton(e);
   }

   static ByteList of(byte e0, byte e1) {
      return ByteImmutableList.of(e0, e1);
   }

   static ByteList of(byte e0, byte e1, byte e2) {
      return ByteImmutableList.of(e0, e1, e2);
   }

   static ByteList of(byte... a) {
      switch (a.length) {
         case 0:
            return of();
         case 1:
            return of(a[0]);
         default:
            return ByteImmutableList.of(a);
      }
   }

   @Deprecated
   @Override
   default void sort(Comparator<? super Byte> comparator) {
      this.sort(ByteComparators.asByteComparator(comparator));
   }

   default void sort(ByteComparator comparator) {
      if (comparator == null) {
         this.unstableSort(comparator);
      } else {
         byte[] elements = this.toByteArray();
         ByteArrays.stableSort(elements, comparator);
         this.setElements(elements);
      }
   }

   @Deprecated
   default void unstableSort(Comparator<? super Byte> comparator) {
      this.unstableSort(ByteComparators.asByteComparator(comparator));
   }

   default void unstableSort(ByteComparator comparator) {
      byte[] elements = this.toByteArray();
      if (comparator == null) {
         ByteArrays.unstableSort(elements);
      } else {
         ByteArrays.unstableSort(elements, comparator);
      }

      this.setElements(elements);
   }
}
