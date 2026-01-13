package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public final class ByteBigLists {
   public static final ByteBigLists.EmptyBigList EMPTY_BIG_LIST = new ByteBigLists.EmptyBigList();

   private ByteBigLists() {
   }

   public static ByteBigList shuffle(ByteBigList l, Random random) {
      long i = l.size64();

      while (i-- != 0L) {
         long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
         byte t = l.getByte(i);
         l.set(i, l.getByte(p));
         l.set(p, t);
      }

      return l;
   }

   public static ByteBigList singleton(byte element) {
      return new ByteBigLists.Singleton(element);
   }

   public static ByteBigList singleton(Object element) {
      return new ByteBigLists.Singleton((Byte)element);
   }

   public static ByteBigList synchronize(ByteBigList l) {
      return new ByteBigLists.SynchronizedBigList(l);
   }

   public static ByteBigList synchronize(ByteBigList l, Object sync) {
      return new ByteBigLists.SynchronizedBigList(l, sync);
   }

   public static ByteBigList unmodifiable(ByteBigList l) {
      return new ByteBigLists.UnmodifiableBigList(l);
   }

   public static ByteBigList asBigList(ByteList list) {
      return new ByteBigLists.ListBigList(list);
   }

   public static class EmptyBigList extends ByteCollections.EmptyCollection implements ByteBigList, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigList() {
      }

      @Override
      public byte getByte(long i) {
         throw new IndexOutOfBoundsException();
      }

      @Override
      public boolean rem(byte k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte removeByte(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(long index, byte k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte set(long index, byte k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long indexOf(byte k) {
         return -1L;
      }

      @Override
      public long lastIndexOf(byte k) {
         return -1L;
      }

      @Override
      public boolean addAll(long i, Collection<? extends Byte> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(ByteCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(ByteBigList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, ByteCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, ByteBigList c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void add(long index, Byte k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean add(Byte k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte get(long i) {
         throw new IndexOutOfBoundsException();
      }

      @Deprecated
      @Override
      public Byte set(long index, Byte k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte remove(long k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public long indexOf(Object k) {
         return -1L;
      }

      @Deprecated
      @Override
      public long lastIndexOf(Object k) {
         return -1L;
      }

      @Override
      public ByteBigListIterator listIterator() {
         return ByteBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      @Override
      public ByteBigListIterator iterator() {
         return ByteBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      @Override
      public ByteBigListIterator listIterator(long i) {
         if (i == 0L) {
            return ByteBigListIterators.EMPTY_BIG_LIST_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(i));
         }
      }

      @Override
      public ByteSpliterator spliterator() {
         return ByteSpliterators.EMPTY_SPLITERATOR;
      }

      @Override
      public ByteBigList subList(long from, long to) {
         if (from == 0L && to == 0L) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public void getElements(long from, byte[][] a, long offset, long length) {
         BigArrays.ensureOffsetLength(a, offset, length);
         if (from != 0L) {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public void removeElements(long from, long to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, byte[][] a, long offset, long length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, byte[][] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void size(long s) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long size64() {
         return 0L;
      }

      public int compareTo(BigList<? extends Byte> o) {
         if (o == this) {
            return 0;
         } else {
            return o.isEmpty() ? 0 : -1;
         }
      }

      @Override
      public Object clone() {
         return ByteBigLists.EMPTY_BIG_LIST;
      }

      @Override
      public int hashCode() {
         return 1;
      }

      @Override
      public boolean equals(Object o) {
         return o instanceof BigList && ((BigList)o).isEmpty();
      }

      @Override
      public String toString() {
         return "[]";
      }

      private Object readResolve() {
         return ByteBigLists.EMPTY_BIG_LIST;
      }
   }

   public static class ListBigList extends AbstractByteBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final ByteList list;

      protected ListBigList(ByteList list) {
         this.list = list;
      }

      private int intIndex(long index) {
         if (index >= 2147483647L) {
            throw new IndexOutOfBoundsException("This big list is restricted to 32-bit indices");
         } else {
            return (int)index;
         }
      }

      @Override
      public long size64() {
         return this.list.size();
      }

      @Override
      public void size(long size) {
         this.list.size(this.intIndex(size));
      }

      @Override
      public ByteBigListIterator iterator() {
         return ByteBigListIterators.asBigListIterator(this.list.iterator());
      }

      @Override
      public ByteBigListIterator listIterator() {
         return ByteBigListIterators.asBigListIterator(this.list.listIterator());
      }

      @Override
      public ByteBigListIterator listIterator(long index) {
         return ByteBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(index)));
      }

      @Override
      public boolean addAll(long index, Collection<? extends Byte> c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public ByteBigList subList(long from, long to) {
         return new ByteBigLists.ListBigList(this.list.subList(this.intIndex(from), this.intIndex(to)));
      }

      @Override
      public boolean contains(byte key) {
         return this.list.contains(key);
      }

      @Override
      public byte[] toByteArray() {
         return this.list.toByteArray();
      }

      @Override
      public void removeElements(long from, long to) {
         this.list.removeElements(this.intIndex(from), this.intIndex(to));
      }

      @Deprecated
      @Override
      public byte[] toByteArray(byte[] a) {
         return this.list.toArray(a);
      }

      @Override
      public boolean addAll(long index, ByteCollection c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public boolean addAll(ByteCollection c) {
         return this.list.addAll(c);
      }

      @Override
      public boolean addAll(long index, ByteBigList c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public boolean addAll(ByteBigList c) {
         return this.list.addAll(c);
      }

      @Override
      public boolean containsAll(ByteCollection c) {
         return this.list.containsAll(c);
      }

      @Override
      public boolean removeAll(ByteCollection c) {
         return this.list.removeAll(c);
      }

      @Override
      public boolean retainAll(ByteCollection c) {
         return this.list.retainAll(c);
      }

      @Override
      public void add(long index, byte key) {
         this.list.add(this.intIndex(index), key);
      }

      @Override
      public boolean add(byte key) {
         return this.list.add(key);
      }

      @Override
      public byte getByte(long index) {
         return this.list.getByte(this.intIndex(index));
      }

      @Override
      public long indexOf(byte k) {
         return this.list.indexOf(k);
      }

      @Override
      public long lastIndexOf(byte k) {
         return this.list.lastIndexOf(k);
      }

      @Override
      public byte removeByte(long index) {
         return this.list.removeByte(this.intIndex(index));
      }

      @Override
      public byte set(long index, byte k) {
         return this.list.set(this.intIndex(index), k);
      }

      @Override
      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      @Override
      public <T> T[] toArray(T[] a) {
         return (T[])this.list.toArray(a);
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return this.list.containsAll(c);
      }

      @Override
      public boolean addAll(Collection<? extends Byte> c) {
         return this.list.addAll(c);
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         return this.list.removeAll(c);
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         return this.list.retainAll(c);
      }

      @Override
      public void clear() {
         this.list.clear();
      }

      @Override
      public int hashCode() {
         return this.list.hashCode();
      }
   }

   public static class Singleton extends AbstractByteBigList implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final byte element;

      protected Singleton(byte element) {
         this.element = element;
      }

      @Override
      public byte getByte(long i) {
         if (i == 0L) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public boolean rem(byte k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte removeByte(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean contains(byte k) {
         return k == this.element;
      }

      @Override
      public long indexOf(byte k) {
         return k == this.element ? 0L : -1L;
      }

      @Override
      public byte[] toByteArray() {
         return new byte[]{this.element};
      }

      @Override
      public ByteBigListIterator listIterator() {
         return ByteBigListIterators.singleton(this.element);
      }

      @Override
      public ByteBigListIterator listIterator(long i) {
         if (i <= 1L && i >= 0L) {
            ByteBigListIterator l = this.listIterator();
            if (i == 1L) {
               l.nextByte();
            }

            return l;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public ByteSpliterator spliterator() {
         return ByteSpliterators.singleton(this.element);
      }

      @Override
      public ByteBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return (ByteBigList)(from == 0L && to == 1L ? this : ByteBigLists.EMPTY_BIG_LIST);
         }
      }

      @Override
      public boolean addAll(long i, Collection<? extends Byte> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends Byte> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(ByteBigList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, ByteBigList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, ByteCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(ByteCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(ByteCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(ByteCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Override
      public long size64() {
         return 1L;
      }

      @Override
      public Object clone() {
         return this;
      }
   }

   public static class SynchronizedBigList extends ByteCollections.SynchronizedCollection implements ByteBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteBigList list;

      protected SynchronizedBigList(ByteBigList l, Object sync) {
         super(l, sync);
         this.list = l;
      }

      protected SynchronizedBigList(ByteBigList l) {
         super(l);
         this.list = l;
      }

      @Override
      public byte getByte(long i) {
         synchronized (this.sync) {
            return this.list.getByte(i);
         }
      }

      @Override
      public byte set(long i, byte k) {
         synchronized (this.sync) {
            return this.list.set(i, k);
         }
      }

      @Override
      public void add(long i, byte k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Override
      public byte removeByte(long i) {
         synchronized (this.sync) {
            return this.list.removeByte(i);
         }
      }

      @Override
      public long indexOf(byte k) {
         synchronized (this.sync) {
            return this.list.indexOf(k);
         }
      }

      @Override
      public long lastIndexOf(byte k) {
         synchronized (this.sync) {
            return this.list.lastIndexOf(k);
         }
      }

      @Override
      public boolean addAll(long index, Collection<? extends Byte> c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public void getElements(long from, byte[][] a, long offset, long length) {
         synchronized (this.sync) {
            this.list.getElements(from, a, offset, length);
         }
      }

      @Override
      public void removeElements(long from, long to) {
         synchronized (this.sync) {
            this.list.removeElements(from, to);
         }
      }

      @Override
      public void addElements(long index, byte[][] a, long offset, long length) {
         synchronized (this.sync) {
            this.list.addElements(index, a, offset, length);
         }
      }

      @Override
      public void addElements(long index, byte[][] a) {
         synchronized (this.sync) {
            this.list.addElements(index, a);
         }
      }

      @Deprecated
      @Override
      public void size(long size) {
         synchronized (this.sync) {
            this.list.size(size);
         }
      }

      @Override
      public long size64() {
         synchronized (this.sync) {
            return this.list.size64();
         }
      }

      @Override
      public ByteBigListIterator iterator() {
         return this.list.listIterator();
      }

      @Override
      public ByteBigListIterator listIterator() {
         return this.list.listIterator();
      }

      @Override
      public ByteBigListIterator listIterator(long i) {
         return this.list.listIterator(i);
      }

      @Override
      public ByteBigList subList(long from, long to) {
         synchronized (this.sync) {
            return ByteBigLists.synchronize(this.list.subList(from, to), this.sync);
         }
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            synchronized (this.sync) {
               return this.list.equals(o);
            }
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.sync) {
            return this.list.hashCode();
         }
      }

      public int compareTo(BigList<? extends Byte> o) {
         synchronized (this.sync) {
            return this.list.compareTo(o);
         }
      }

      @Override
      public boolean addAll(long index, ByteCollection c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public boolean addAll(long index, ByteBigList l) {
         synchronized (this.sync) {
            return this.list.addAll(index, l);
         }
      }

      @Override
      public boolean addAll(ByteBigList l) {
         synchronized (this.sync) {
            return this.list.addAll(l);
         }
      }

      @Deprecated
      @Override
      public void add(long i, Byte k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Deprecated
      @Override
      public Byte get(long i) {
         synchronized (this.sync) {
            return this.list.get(i);
         }
      }

      @Deprecated
      @Override
      public Byte set(long index, Byte k) {
         synchronized (this.sync) {
            return this.list.set(index, k);
         }
      }

      @Deprecated
      @Override
      public Byte remove(long i) {
         synchronized (this.sync) {
            return this.list.remove(i);
         }
      }

      @Deprecated
      @Override
      public long indexOf(Object o) {
         synchronized (this.sync) {
            return this.list.indexOf(o);
         }
      }

      @Deprecated
      @Override
      public long lastIndexOf(Object o) {
         synchronized (this.sync) {
            return this.list.lastIndexOf(o);
         }
      }
   }

   public static class UnmodifiableBigList extends ByteCollections.UnmodifiableCollection implements ByteBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteBigList list;

      protected UnmodifiableBigList(ByteBigList l) {
         super(l);
         this.list = l;
      }

      @Override
      public byte getByte(long i) {
         return this.list.getByte(i);
      }

      @Override
      public byte set(long i, byte k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(long i, byte k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte removeByte(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long indexOf(byte k) {
         return this.list.indexOf(k);
      }

      @Override
      public long lastIndexOf(byte k) {
         return this.list.lastIndexOf(k);
      }

      @Override
      public boolean addAll(long index, Collection<? extends Byte> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void getElements(long from, byte[][] a, long offset, long length) {
         this.list.getElements(from, a, offset, length);
      }

      @Override
      public void removeElements(long from, long to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, byte[][] a, long offset, long length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, byte[][] a) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void size(long size) {
         this.list.size(size);
      }

      @Override
      public long size64() {
         return this.list.size64();
      }

      @Override
      public ByteBigListIterator iterator() {
         return this.listIterator();
      }

      @Override
      public ByteBigListIterator listIterator() {
         return ByteBigListIterators.unmodifiable(this.list.listIterator());
      }

      @Override
      public ByteBigListIterator listIterator(long i) {
         return ByteBigListIterators.unmodifiable(this.list.listIterator(i));
      }

      @Override
      public ByteBigList subList(long from, long to) {
         return ByteBigLists.unmodifiable(this.list.subList(from, to));
      }

      @Override
      public boolean equals(Object o) {
         return o == this ? true : this.list.equals(o);
      }

      @Override
      public int hashCode() {
         return this.list.hashCode();
      }

      public int compareTo(BigList<? extends Byte> o) {
         return this.list.compareTo(o);
      }

      @Override
      public boolean addAll(long index, ByteCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(ByteBigList l) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long index, ByteBigList l) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte get(long i) {
         return this.list.get(i);
      }

      @Deprecated
      @Override
      public void add(long i, Byte k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte set(long index, Byte k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Byte remove(long i) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public long indexOf(Object o) {
         return this.list.indexOf(o);
      }

      @Deprecated
      @Override
      public long lastIndexOf(Object o) {
         return this.list.lastIndexOf(o);
      }
   }
}
