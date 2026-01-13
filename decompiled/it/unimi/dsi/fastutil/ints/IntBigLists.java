package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public final class IntBigLists {
   public static final IntBigLists.EmptyBigList EMPTY_BIG_LIST = new IntBigLists.EmptyBigList();

   private IntBigLists() {
   }

   public static IntBigList shuffle(IntBigList l, Random random) {
      long i = l.size64();

      while (i-- != 0L) {
         long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
         int t = l.getInt(i);
         l.set(i, l.getInt(p));
         l.set(p, t);
      }

      return l;
   }

   public static IntBigList singleton(int element) {
      return new IntBigLists.Singleton(element);
   }

   public static IntBigList singleton(Object element) {
      return new IntBigLists.Singleton((Integer)element);
   }

   public static IntBigList synchronize(IntBigList l) {
      return new IntBigLists.SynchronizedBigList(l);
   }

   public static IntBigList synchronize(IntBigList l, Object sync) {
      return new IntBigLists.SynchronizedBigList(l, sync);
   }

   public static IntBigList unmodifiable(IntBigList l) {
      return new IntBigLists.UnmodifiableBigList(l);
   }

   public static IntBigList asBigList(IntList list) {
      return new IntBigLists.ListBigList(list);
   }

   public static class EmptyBigList extends IntCollections.EmptyCollection implements IntBigList, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigList() {
      }

      @Override
      public int getInt(long i) {
         throw new IndexOutOfBoundsException();
      }

      @Override
      public boolean rem(int k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int removeInt(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(long index, int k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int set(long index, int k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long indexOf(int k) {
         return -1L;
      }

      @Override
      public long lastIndexOf(int k) {
         return -1L;
      }

      @Override
      public boolean addAll(long i, Collection<? extends Integer> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(IntCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(IntBigList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, IntCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, IntBigList c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void add(long index, Integer k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean add(Integer k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer get(long i) {
         throw new IndexOutOfBoundsException();
      }

      @Deprecated
      @Override
      public Integer set(long index, Integer k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer remove(long k) {
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
      public IntBigListIterator listIterator() {
         return IntBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      @Override
      public IntBigListIterator iterator() {
         return IntBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      @Override
      public IntBigListIterator listIterator(long i) {
         if (i == 0L) {
            return IntBigListIterators.EMPTY_BIG_LIST_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(i));
         }
      }

      @Override
      public IntSpliterator spliterator() {
         return IntSpliterators.EMPTY_SPLITERATOR;
      }

      @Override
      public IntBigList subList(long from, long to) {
         if (from == 0L && to == 0L) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public void getElements(long from, int[][] a, long offset, long length) {
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
      public void addElements(long index, int[][] a, long offset, long length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, int[][] a) {
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

      public int compareTo(BigList<? extends Integer> o) {
         if (o == this) {
            return 0;
         } else {
            return o.isEmpty() ? 0 : -1;
         }
      }

      @Override
      public Object clone() {
         return IntBigLists.EMPTY_BIG_LIST;
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
         return IntBigLists.EMPTY_BIG_LIST;
      }
   }

   public static class ListBigList extends AbstractIntBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final IntList list;

      protected ListBigList(IntList list) {
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
      public IntBigListIterator iterator() {
         return IntBigListIterators.asBigListIterator(this.list.iterator());
      }

      @Override
      public IntBigListIterator listIterator() {
         return IntBigListIterators.asBigListIterator(this.list.listIterator());
      }

      @Override
      public IntBigListIterator listIterator(long index) {
         return IntBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(index)));
      }

      @Override
      public boolean addAll(long index, Collection<? extends Integer> c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public IntBigList subList(long from, long to) {
         return new IntBigLists.ListBigList(this.list.subList(this.intIndex(from), this.intIndex(to)));
      }

      @Override
      public boolean contains(int key) {
         return this.list.contains(key);
      }

      @Override
      public int[] toIntArray() {
         return this.list.toIntArray();
      }

      @Override
      public void removeElements(long from, long to) {
         this.list.removeElements(this.intIndex(from), this.intIndex(to));
      }

      @Deprecated
      @Override
      public int[] toIntArray(int[] a) {
         return this.list.toArray(a);
      }

      @Override
      public boolean addAll(long index, IntCollection c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public boolean addAll(IntCollection c) {
         return this.list.addAll(c);
      }

      @Override
      public boolean addAll(long index, IntBigList c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public boolean addAll(IntBigList c) {
         return this.list.addAll(c);
      }

      @Override
      public boolean containsAll(IntCollection c) {
         return this.list.containsAll(c);
      }

      @Override
      public boolean removeAll(IntCollection c) {
         return this.list.removeAll(c);
      }

      @Override
      public boolean retainAll(IntCollection c) {
         return this.list.retainAll(c);
      }

      @Override
      public void add(long index, int key) {
         this.list.add(this.intIndex(index), key);
      }

      @Override
      public boolean add(int key) {
         return this.list.add(key);
      }

      @Override
      public int getInt(long index) {
         return this.list.getInt(this.intIndex(index));
      }

      @Override
      public long indexOf(int k) {
         return this.list.indexOf(k);
      }

      @Override
      public long lastIndexOf(int k) {
         return this.list.lastIndexOf(k);
      }

      @Override
      public int removeInt(long index) {
         return this.list.removeInt(this.intIndex(index));
      }

      @Override
      public int set(long index, int k) {
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
      public boolean addAll(Collection<? extends Integer> c) {
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

   public static class Singleton extends AbstractIntBigList implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final int element;

      protected Singleton(int element) {
         this.element = element;
      }

      @Override
      public int getInt(long i) {
         if (i == 0L) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public boolean rem(int k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int removeInt(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean contains(int k) {
         return k == this.element;
      }

      @Override
      public long indexOf(int k) {
         return k == this.element ? 0L : -1L;
      }

      @Override
      public int[] toIntArray() {
         return new int[]{this.element};
      }

      @Override
      public IntBigListIterator listIterator() {
         return IntBigListIterators.singleton(this.element);
      }

      @Override
      public IntBigListIterator listIterator(long i) {
         if (i <= 1L && i >= 0L) {
            IntBigListIterator l = this.listIterator();
            if (i == 1L) {
               l.nextInt();
            }

            return l;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public IntSpliterator spliterator() {
         return IntSpliterators.singleton(this.element);
      }

      @Override
      public IntBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return (IntBigList)(from == 0L && to == 1L ? this : IntBigLists.EMPTY_BIG_LIST);
         }
      }

      @Override
      public boolean addAll(long i, Collection<? extends Integer> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends Integer> c) {
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
      public boolean addAll(IntBigList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, IntBigList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, IntCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(IntCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(IntCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(IntCollection c) {
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

   public static class SynchronizedBigList extends IntCollections.SynchronizedCollection implements IntBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntBigList list;

      protected SynchronizedBigList(IntBigList l, Object sync) {
         super(l, sync);
         this.list = l;
      }

      protected SynchronizedBigList(IntBigList l) {
         super(l);
         this.list = l;
      }

      @Override
      public int getInt(long i) {
         synchronized (this.sync) {
            return this.list.getInt(i);
         }
      }

      @Override
      public int set(long i, int k) {
         synchronized (this.sync) {
            return this.list.set(i, k);
         }
      }

      @Override
      public void add(long i, int k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Override
      public int removeInt(long i) {
         synchronized (this.sync) {
            return this.list.removeInt(i);
         }
      }

      @Override
      public long indexOf(int k) {
         synchronized (this.sync) {
            return this.list.indexOf(k);
         }
      }

      @Override
      public long lastIndexOf(int k) {
         synchronized (this.sync) {
            return this.list.lastIndexOf(k);
         }
      }

      @Override
      public boolean addAll(long index, Collection<? extends Integer> c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public void getElements(long from, int[][] a, long offset, long length) {
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
      public void addElements(long index, int[][] a, long offset, long length) {
         synchronized (this.sync) {
            this.list.addElements(index, a, offset, length);
         }
      }

      @Override
      public void addElements(long index, int[][] a) {
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
      public IntBigListIterator iterator() {
         return this.list.listIterator();
      }

      @Override
      public IntBigListIterator listIterator() {
         return this.list.listIterator();
      }

      @Override
      public IntBigListIterator listIterator(long i) {
         return this.list.listIterator(i);
      }

      @Override
      public IntBigList subList(long from, long to) {
         synchronized (this.sync) {
            return IntBigLists.synchronize(this.list.subList(from, to), this.sync);
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

      public int compareTo(BigList<? extends Integer> o) {
         synchronized (this.sync) {
            return this.list.compareTo(o);
         }
      }

      @Override
      public boolean addAll(long index, IntCollection c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public boolean addAll(long index, IntBigList l) {
         synchronized (this.sync) {
            return this.list.addAll(index, l);
         }
      }

      @Override
      public boolean addAll(IntBigList l) {
         synchronized (this.sync) {
            return this.list.addAll(l);
         }
      }

      @Deprecated
      @Override
      public void add(long i, Integer k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Deprecated
      @Override
      public Integer get(long i) {
         synchronized (this.sync) {
            return this.list.get(i);
         }
      }

      @Deprecated
      @Override
      public Integer set(long index, Integer k) {
         synchronized (this.sync) {
            return this.list.set(index, k);
         }
      }

      @Deprecated
      @Override
      public Integer remove(long i) {
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

   public static class UnmodifiableBigList extends IntCollections.UnmodifiableCollection implements IntBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntBigList list;

      protected UnmodifiableBigList(IntBigList l) {
         super(l);
         this.list = l;
      }

      @Override
      public int getInt(long i) {
         return this.list.getInt(i);
      }

      @Override
      public int set(long i, int k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(long i, int k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int removeInt(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long indexOf(int k) {
         return this.list.indexOf(k);
      }

      @Override
      public long lastIndexOf(int k) {
         return this.list.lastIndexOf(k);
      }

      @Override
      public boolean addAll(long index, Collection<? extends Integer> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void getElements(long from, int[][] a, long offset, long length) {
         this.list.getElements(from, a, offset, length);
      }

      @Override
      public void removeElements(long from, long to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, int[][] a, long offset, long length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, int[][] a) {
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
      public IntBigListIterator iterator() {
         return this.listIterator();
      }

      @Override
      public IntBigListIterator listIterator() {
         return IntBigListIterators.unmodifiable(this.list.listIterator());
      }

      @Override
      public IntBigListIterator listIterator(long i) {
         return IntBigListIterators.unmodifiable(this.list.listIterator(i));
      }

      @Override
      public IntBigList subList(long from, long to) {
         return IntBigLists.unmodifiable(this.list.subList(from, to));
      }

      @Override
      public boolean equals(Object o) {
         return o == this ? true : this.list.equals(o);
      }

      @Override
      public int hashCode() {
         return this.list.hashCode();
      }

      public int compareTo(BigList<? extends Integer> o) {
         return this.list.compareTo(o);
      }

      @Override
      public boolean addAll(long index, IntCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(IntBigList l) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long index, IntBigList l) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer get(long i) {
         return this.list.get(i);
      }

      @Deprecated
      @Override
      public void add(long i, Integer k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer set(long index, Integer k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Integer remove(long i) {
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
