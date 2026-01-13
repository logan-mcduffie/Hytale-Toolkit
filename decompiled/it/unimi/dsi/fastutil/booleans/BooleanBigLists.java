package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public final class BooleanBigLists {
   public static final BooleanBigLists.EmptyBigList EMPTY_BIG_LIST = new BooleanBigLists.EmptyBigList();

   private BooleanBigLists() {
   }

   public static BooleanBigList shuffle(BooleanBigList l, Random random) {
      long i = l.size64();

      while (i-- != 0L) {
         long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
         boolean t = l.getBoolean(i);
         l.set(i, l.getBoolean(p));
         l.set(p, t);
      }

      return l;
   }

   public static BooleanBigList singleton(boolean element) {
      return new BooleanBigLists.Singleton(element);
   }

   public static BooleanBigList singleton(Object element) {
      return new BooleanBigLists.Singleton((Boolean)element);
   }

   public static BooleanBigList synchronize(BooleanBigList l) {
      return new BooleanBigLists.SynchronizedBigList(l);
   }

   public static BooleanBigList synchronize(BooleanBigList l, Object sync) {
      return new BooleanBigLists.SynchronizedBigList(l, sync);
   }

   public static BooleanBigList unmodifiable(BooleanBigList l) {
      return new BooleanBigLists.UnmodifiableBigList(l);
   }

   public static BooleanBigList asBigList(BooleanList list) {
      return new BooleanBigLists.ListBigList(list);
   }

   public static class EmptyBigList extends BooleanCollections.EmptyCollection implements BooleanBigList, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigList() {
      }

      @Override
      public boolean getBoolean(long i) {
         throw new IndexOutOfBoundsException();
      }

      @Override
      public boolean rem(boolean k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeBoolean(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(long index, boolean k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean set(long index, boolean k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long indexOf(boolean k) {
         return -1L;
      }

      @Override
      public long lastIndexOf(boolean k) {
         return -1L;
      }

      @Override
      public boolean addAll(long i, Collection<? extends Boolean> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(BooleanCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(BooleanBigList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, BooleanCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, BooleanBigList c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void add(long index, Boolean k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean add(Boolean k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Boolean get(long i) {
         throw new IndexOutOfBoundsException();
      }

      @Deprecated
      @Override
      public Boolean set(long index, Boolean k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Boolean remove(long k) {
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
      public BooleanBigListIterator listIterator() {
         return BooleanBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      @Override
      public BooleanBigListIterator iterator() {
         return BooleanBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      @Override
      public BooleanBigListIterator listIterator(long i) {
         if (i == 0L) {
            return BooleanBigListIterators.EMPTY_BIG_LIST_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(i));
         }
      }

      @Override
      public BooleanSpliterator spliterator() {
         return BooleanSpliterators.EMPTY_SPLITERATOR;
      }

      @Override
      public BooleanBigList subList(long from, long to) {
         if (from == 0L && to == 0L) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public void getElements(long from, boolean[][] a, long offset, long length) {
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
      public void addElements(long index, boolean[][] a, long offset, long length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, boolean[][] a) {
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

      public int compareTo(BigList<? extends Boolean> o) {
         if (o == this) {
            return 0;
         } else {
            return o.isEmpty() ? 0 : -1;
         }
      }

      @Override
      public Object clone() {
         return BooleanBigLists.EMPTY_BIG_LIST;
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
         return BooleanBigLists.EMPTY_BIG_LIST;
      }
   }

   public static class ListBigList extends AbstractBooleanBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final BooleanList list;

      protected ListBigList(BooleanList list) {
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
      public BooleanBigListIterator iterator() {
         return BooleanBigListIterators.asBigListIterator(this.list.iterator());
      }

      @Override
      public BooleanBigListIterator listIterator() {
         return BooleanBigListIterators.asBigListIterator(this.list.listIterator());
      }

      @Override
      public BooleanBigListIterator listIterator(long index) {
         return BooleanBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(index)));
      }

      @Override
      public boolean addAll(long index, Collection<? extends Boolean> c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public BooleanBigList subList(long from, long to) {
         return new BooleanBigLists.ListBigList(this.list.subList(this.intIndex(from), this.intIndex(to)));
      }

      @Override
      public boolean contains(boolean key) {
         return this.list.contains(key);
      }

      @Override
      public boolean[] toBooleanArray() {
         return this.list.toBooleanArray();
      }

      @Override
      public void removeElements(long from, long to) {
         this.list.removeElements(this.intIndex(from), this.intIndex(to));
      }

      @Deprecated
      @Override
      public boolean[] toBooleanArray(boolean[] a) {
         return this.list.toArray(a);
      }

      @Override
      public boolean addAll(long index, BooleanCollection c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public boolean addAll(BooleanCollection c) {
         return this.list.addAll(c);
      }

      @Override
      public boolean addAll(long index, BooleanBigList c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public boolean addAll(BooleanBigList c) {
         return this.list.addAll(c);
      }

      @Override
      public boolean containsAll(BooleanCollection c) {
         return this.list.containsAll(c);
      }

      @Override
      public boolean removeAll(BooleanCollection c) {
         return this.list.removeAll(c);
      }

      @Override
      public boolean retainAll(BooleanCollection c) {
         return this.list.retainAll(c);
      }

      @Override
      public void add(long index, boolean key) {
         this.list.add(this.intIndex(index), key);
      }

      @Override
      public boolean add(boolean key) {
         return this.list.add(key);
      }

      @Override
      public boolean getBoolean(long index) {
         return this.list.getBoolean(this.intIndex(index));
      }

      @Override
      public long indexOf(boolean k) {
         return this.list.indexOf(k);
      }

      @Override
      public long lastIndexOf(boolean k) {
         return this.list.lastIndexOf(k);
      }

      @Override
      public boolean removeBoolean(long index) {
         return this.list.removeBoolean(this.intIndex(index));
      }

      @Override
      public boolean set(long index, boolean k) {
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
      public boolean addAll(Collection<? extends Boolean> c) {
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

   public static class Singleton extends AbstractBooleanBigList implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final boolean element;

      protected Singleton(boolean element) {
         this.element = element;
      }

      @Override
      public boolean getBoolean(long i) {
         if (i == 0L) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public boolean rem(boolean k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeBoolean(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean contains(boolean k) {
         return k == this.element;
      }

      @Override
      public long indexOf(boolean k) {
         return k == this.element ? 0L : -1L;
      }

      @Override
      public boolean[] toBooleanArray() {
         return new boolean[]{this.element};
      }

      @Override
      public BooleanBigListIterator listIterator() {
         return BooleanBigListIterators.singleton(this.element);
      }

      @Override
      public BooleanBigListIterator listIterator(long i) {
         if (i <= 1L && i >= 0L) {
            BooleanBigListIterator l = this.listIterator();
            if (i == 1L) {
               l.nextBoolean();
            }

            return l;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public BooleanSpliterator spliterator() {
         return BooleanSpliterators.singleton(this.element);
      }

      @Override
      public BooleanBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return (BooleanBigList)(from == 0L && to == 1L ? this : BooleanBigLists.EMPTY_BIG_LIST);
         }
      }

      @Override
      public boolean addAll(long i, Collection<? extends Boolean> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends Boolean> c) {
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
      public boolean addAll(BooleanBigList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, BooleanBigList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, BooleanCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(BooleanCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(BooleanCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(BooleanCollection c) {
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

   public static class SynchronizedBigList extends BooleanCollections.SynchronizedCollection implements BooleanBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final BooleanBigList list;

      protected SynchronizedBigList(BooleanBigList l, Object sync) {
         super(l, sync);
         this.list = l;
      }

      protected SynchronizedBigList(BooleanBigList l) {
         super(l);
         this.list = l;
      }

      @Override
      public boolean getBoolean(long i) {
         synchronized (this.sync) {
            return this.list.getBoolean(i);
         }
      }

      @Override
      public boolean set(long i, boolean k) {
         synchronized (this.sync) {
            return this.list.set(i, k);
         }
      }

      @Override
      public void add(long i, boolean k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Override
      public boolean removeBoolean(long i) {
         synchronized (this.sync) {
            return this.list.removeBoolean(i);
         }
      }

      @Override
      public long indexOf(boolean k) {
         synchronized (this.sync) {
            return this.list.indexOf(k);
         }
      }

      @Override
      public long lastIndexOf(boolean k) {
         synchronized (this.sync) {
            return this.list.lastIndexOf(k);
         }
      }

      @Override
      public boolean addAll(long index, Collection<? extends Boolean> c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public void getElements(long from, boolean[][] a, long offset, long length) {
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
      public void addElements(long index, boolean[][] a, long offset, long length) {
         synchronized (this.sync) {
            this.list.addElements(index, a, offset, length);
         }
      }

      @Override
      public void addElements(long index, boolean[][] a) {
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
      public BooleanBigListIterator iterator() {
         return this.list.listIterator();
      }

      @Override
      public BooleanBigListIterator listIterator() {
         return this.list.listIterator();
      }

      @Override
      public BooleanBigListIterator listIterator(long i) {
         return this.list.listIterator(i);
      }

      @Override
      public BooleanBigList subList(long from, long to) {
         synchronized (this.sync) {
            return BooleanBigLists.synchronize(this.list.subList(from, to), this.sync);
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

      public int compareTo(BigList<? extends Boolean> o) {
         synchronized (this.sync) {
            return this.list.compareTo(o);
         }
      }

      @Override
      public boolean addAll(long index, BooleanCollection c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public boolean addAll(long index, BooleanBigList l) {
         synchronized (this.sync) {
            return this.list.addAll(index, l);
         }
      }

      @Override
      public boolean addAll(BooleanBigList l) {
         synchronized (this.sync) {
            return this.list.addAll(l);
         }
      }

      @Deprecated
      @Override
      public void add(long i, Boolean k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Deprecated
      @Override
      public Boolean get(long i) {
         synchronized (this.sync) {
            return this.list.get(i);
         }
      }

      @Deprecated
      @Override
      public Boolean set(long index, Boolean k) {
         synchronized (this.sync) {
            return this.list.set(index, k);
         }
      }

      @Deprecated
      @Override
      public Boolean remove(long i) {
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

   public static class UnmodifiableBigList extends BooleanCollections.UnmodifiableCollection implements BooleanBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final BooleanBigList list;

      protected UnmodifiableBigList(BooleanBigList l) {
         super(l);
         this.list = l;
      }

      @Override
      public boolean getBoolean(long i) {
         return this.list.getBoolean(i);
      }

      @Override
      public boolean set(long i, boolean k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(long i, boolean k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeBoolean(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long indexOf(boolean k) {
         return this.list.indexOf(k);
      }

      @Override
      public long lastIndexOf(boolean k) {
         return this.list.lastIndexOf(k);
      }

      @Override
      public boolean addAll(long index, Collection<? extends Boolean> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void getElements(long from, boolean[][] a, long offset, long length) {
         this.list.getElements(from, a, offset, length);
      }

      @Override
      public void removeElements(long from, long to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, boolean[][] a, long offset, long length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, boolean[][] a) {
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
      public BooleanBigListIterator iterator() {
         return this.listIterator();
      }

      @Override
      public BooleanBigListIterator listIterator() {
         return BooleanBigListIterators.unmodifiable(this.list.listIterator());
      }

      @Override
      public BooleanBigListIterator listIterator(long i) {
         return BooleanBigListIterators.unmodifiable(this.list.listIterator(i));
      }

      @Override
      public BooleanBigList subList(long from, long to) {
         return BooleanBigLists.unmodifiable(this.list.subList(from, to));
      }

      @Override
      public boolean equals(Object o) {
         return o == this ? true : this.list.equals(o);
      }

      @Override
      public int hashCode() {
         return this.list.hashCode();
      }

      public int compareTo(BigList<? extends Boolean> o) {
         return this.list.compareTo(o);
      }

      @Override
      public boolean addAll(long index, BooleanCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(BooleanBigList l) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long index, BooleanBigList l) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Boolean get(long i) {
         return this.list.get(i);
      }

      @Deprecated
      @Override
      public void add(long i, Boolean k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Boolean set(long index, Boolean k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Boolean remove(long i) {
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
