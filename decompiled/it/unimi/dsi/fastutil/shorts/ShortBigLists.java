package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public final class ShortBigLists {
   public static final ShortBigLists.EmptyBigList EMPTY_BIG_LIST = new ShortBigLists.EmptyBigList();

   private ShortBigLists() {
   }

   public static ShortBigList shuffle(ShortBigList l, Random random) {
      long i = l.size64();

      while (i-- != 0L) {
         long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
         short t = l.getShort(i);
         l.set(i, l.getShort(p));
         l.set(p, t);
      }

      return l;
   }

   public static ShortBigList singleton(short element) {
      return new ShortBigLists.Singleton(element);
   }

   public static ShortBigList singleton(Object element) {
      return new ShortBigLists.Singleton((Short)element);
   }

   public static ShortBigList synchronize(ShortBigList l) {
      return new ShortBigLists.SynchronizedBigList(l);
   }

   public static ShortBigList synchronize(ShortBigList l, Object sync) {
      return new ShortBigLists.SynchronizedBigList(l, sync);
   }

   public static ShortBigList unmodifiable(ShortBigList l) {
      return new ShortBigLists.UnmodifiableBigList(l);
   }

   public static ShortBigList asBigList(ShortList list) {
      return new ShortBigLists.ListBigList(list);
   }

   public static class EmptyBigList extends ShortCollections.EmptyCollection implements ShortBigList, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigList() {
      }

      @Override
      public short getShort(long i) {
         throw new IndexOutOfBoundsException();
      }

      @Override
      public boolean rem(short k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short removeShort(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(long index, short k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short set(long index, short k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long indexOf(short k) {
         return -1L;
      }

      @Override
      public long lastIndexOf(short k) {
         return -1L;
      }

      @Override
      public boolean addAll(long i, Collection<? extends Short> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(ShortBigList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, ShortBigList c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void add(long index, Short k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean add(Short k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short get(long i) {
         throw new IndexOutOfBoundsException();
      }

      @Deprecated
      @Override
      public Short set(long index, Short k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short remove(long k) {
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
      public ShortBigListIterator listIterator() {
         return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      @Override
      public ShortBigListIterator iterator() {
         return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      @Override
      public ShortBigListIterator listIterator(long i) {
         if (i == 0L) {
            return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(i));
         }
      }

      @Override
      public ShortSpliterator spliterator() {
         return ShortSpliterators.EMPTY_SPLITERATOR;
      }

      @Override
      public ShortBigList subList(long from, long to) {
         if (from == 0L && to == 0L) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public void getElements(long from, short[][] a, long offset, long length) {
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
      public void addElements(long index, short[][] a, long offset, long length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, short[][] a) {
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

      public int compareTo(BigList<? extends Short> o) {
         if (o == this) {
            return 0;
         } else {
            return o.isEmpty() ? 0 : -1;
         }
      }

      @Override
      public Object clone() {
         return ShortBigLists.EMPTY_BIG_LIST;
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
         return ShortBigLists.EMPTY_BIG_LIST;
      }
   }

   public static class ListBigList extends AbstractShortBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final ShortList list;

      protected ListBigList(ShortList list) {
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
      public ShortBigListIterator iterator() {
         return ShortBigListIterators.asBigListIterator(this.list.iterator());
      }

      @Override
      public ShortBigListIterator listIterator() {
         return ShortBigListIterators.asBigListIterator(this.list.listIterator());
      }

      @Override
      public ShortBigListIterator listIterator(long index) {
         return ShortBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(index)));
      }

      @Override
      public boolean addAll(long index, Collection<? extends Short> c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public ShortBigList subList(long from, long to) {
         return new ShortBigLists.ListBigList(this.list.subList(this.intIndex(from), this.intIndex(to)));
      }

      @Override
      public boolean contains(short key) {
         return this.list.contains(key);
      }

      @Override
      public short[] toShortArray() {
         return this.list.toShortArray();
      }

      @Override
      public void removeElements(long from, long to) {
         this.list.removeElements(this.intIndex(from), this.intIndex(to));
      }

      @Deprecated
      @Override
      public short[] toShortArray(short[] a) {
         return this.list.toArray(a);
      }

      @Override
      public boolean addAll(long index, ShortCollection c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public boolean addAll(ShortCollection c) {
         return this.list.addAll(c);
      }

      @Override
      public boolean addAll(long index, ShortBigList c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public boolean addAll(ShortBigList c) {
         return this.list.addAll(c);
      }

      @Override
      public boolean containsAll(ShortCollection c) {
         return this.list.containsAll(c);
      }

      @Override
      public boolean removeAll(ShortCollection c) {
         return this.list.removeAll(c);
      }

      @Override
      public boolean retainAll(ShortCollection c) {
         return this.list.retainAll(c);
      }

      @Override
      public void add(long index, short key) {
         this.list.add(this.intIndex(index), key);
      }

      @Override
      public boolean add(short key) {
         return this.list.add(key);
      }

      @Override
      public short getShort(long index) {
         return this.list.getShort(this.intIndex(index));
      }

      @Override
      public long indexOf(short k) {
         return this.list.indexOf(k);
      }

      @Override
      public long lastIndexOf(short k) {
         return this.list.lastIndexOf(k);
      }

      @Override
      public short removeShort(long index) {
         return this.list.removeShort(this.intIndex(index));
      }

      @Override
      public short set(long index, short k) {
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
      public boolean addAll(Collection<? extends Short> c) {
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

   public static class Singleton extends AbstractShortBigList implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final short element;

      protected Singleton(short element) {
         this.element = element;
      }

      @Override
      public short getShort(long i) {
         if (i == 0L) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public boolean rem(short k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short removeShort(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean contains(short k) {
         return k == this.element;
      }

      @Override
      public long indexOf(short k) {
         return k == this.element ? 0L : -1L;
      }

      @Override
      public short[] toShortArray() {
         return new short[]{this.element};
      }

      @Override
      public ShortBigListIterator listIterator() {
         return ShortBigListIterators.singleton(this.element);
      }

      @Override
      public ShortBigListIterator listIterator(long i) {
         if (i <= 1L && i >= 0L) {
            ShortBigListIterator l = this.listIterator();
            if (i == 1L) {
               l.nextShort();
            }

            return l;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public ShortSpliterator spliterator() {
         return ShortSpliterators.singleton(this.element);
      }

      @Override
      public ShortBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return (ShortBigList)(from == 0L && to == 1L ? this : ShortBigLists.EMPTY_BIG_LIST);
         }
      }

      @Override
      public boolean addAll(long i, Collection<? extends Short> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends Short> c) {
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
      public boolean addAll(ShortBigList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, ShortBigList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(ShortCollection c) {
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

   public static class SynchronizedBigList extends ShortCollections.SynchronizedCollection implements ShortBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortBigList list;

      protected SynchronizedBigList(ShortBigList l, Object sync) {
         super(l, sync);
         this.list = l;
      }

      protected SynchronizedBigList(ShortBigList l) {
         super(l);
         this.list = l;
      }

      @Override
      public short getShort(long i) {
         synchronized (this.sync) {
            return this.list.getShort(i);
         }
      }

      @Override
      public short set(long i, short k) {
         synchronized (this.sync) {
            return this.list.set(i, k);
         }
      }

      @Override
      public void add(long i, short k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Override
      public short removeShort(long i) {
         synchronized (this.sync) {
            return this.list.removeShort(i);
         }
      }

      @Override
      public long indexOf(short k) {
         synchronized (this.sync) {
            return this.list.indexOf(k);
         }
      }

      @Override
      public long lastIndexOf(short k) {
         synchronized (this.sync) {
            return this.list.lastIndexOf(k);
         }
      }

      @Override
      public boolean addAll(long index, Collection<? extends Short> c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public void getElements(long from, short[][] a, long offset, long length) {
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
      public void addElements(long index, short[][] a, long offset, long length) {
         synchronized (this.sync) {
            this.list.addElements(index, a, offset, length);
         }
      }

      @Override
      public void addElements(long index, short[][] a) {
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
      public ShortBigListIterator iterator() {
         return this.list.listIterator();
      }

      @Override
      public ShortBigListIterator listIterator() {
         return this.list.listIterator();
      }

      @Override
      public ShortBigListIterator listIterator(long i) {
         return this.list.listIterator(i);
      }

      @Override
      public ShortBigList subList(long from, long to) {
         synchronized (this.sync) {
            return ShortBigLists.synchronize(this.list.subList(from, to), this.sync);
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

      public int compareTo(BigList<? extends Short> o) {
         synchronized (this.sync) {
            return this.list.compareTo(o);
         }
      }

      @Override
      public boolean addAll(long index, ShortCollection c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public boolean addAll(long index, ShortBigList l) {
         synchronized (this.sync) {
            return this.list.addAll(index, l);
         }
      }

      @Override
      public boolean addAll(ShortBigList l) {
         synchronized (this.sync) {
            return this.list.addAll(l);
         }
      }

      @Deprecated
      @Override
      public void add(long i, Short k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Deprecated
      @Override
      public Short get(long i) {
         synchronized (this.sync) {
            return this.list.get(i);
         }
      }

      @Deprecated
      @Override
      public Short set(long index, Short k) {
         synchronized (this.sync) {
            return this.list.set(index, k);
         }
      }

      @Deprecated
      @Override
      public Short remove(long i) {
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

   public static class UnmodifiableBigList extends ShortCollections.UnmodifiableCollection implements ShortBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortBigList list;

      protected UnmodifiableBigList(ShortBigList l) {
         super(l);
         this.list = l;
      }

      @Override
      public short getShort(long i) {
         return this.list.getShort(i);
      }

      @Override
      public short set(long i, short k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(long i, short k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short removeShort(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long indexOf(short k) {
         return this.list.indexOf(k);
      }

      @Override
      public long lastIndexOf(short k) {
         return this.list.lastIndexOf(k);
      }

      @Override
      public boolean addAll(long index, Collection<? extends Short> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void getElements(long from, short[][] a, long offset, long length) {
         this.list.getElements(from, a, offset, length);
      }

      @Override
      public void removeElements(long from, long to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, short[][] a, long offset, long length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, short[][] a) {
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
      public ShortBigListIterator iterator() {
         return this.listIterator();
      }

      @Override
      public ShortBigListIterator listIterator() {
         return ShortBigListIterators.unmodifiable(this.list.listIterator());
      }

      @Override
      public ShortBigListIterator listIterator(long i) {
         return ShortBigListIterators.unmodifiable(this.list.listIterator(i));
      }

      @Override
      public ShortBigList subList(long from, long to) {
         return ShortBigLists.unmodifiable(this.list.subList(from, to));
      }

      @Override
      public boolean equals(Object o) {
         return o == this ? true : this.list.equals(o);
      }

      @Override
      public int hashCode() {
         return this.list.hashCode();
      }

      public int compareTo(BigList<? extends Short> o) {
         return this.list.compareTo(o);
      }

      @Override
      public boolean addAll(long index, ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(ShortBigList l) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long index, ShortBigList l) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short get(long i) {
         return this.list.get(i);
      }

      @Deprecated
      @Override
      public void add(long i, Short k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short set(long index, Short k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short remove(long i) {
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
