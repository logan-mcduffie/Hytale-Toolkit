package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public final class ReferenceBigLists {
   public static final ReferenceBigLists.EmptyBigList EMPTY_BIG_LIST = new ReferenceBigLists.EmptyBigList();

   private ReferenceBigLists() {
   }

   public static <K> ReferenceBigList<K> shuffle(ReferenceBigList<K> l, Random random) {
      long i = l.size64();

      while (i-- != 0L) {
         long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
         K t = l.get(i);
         l.set(i, l.get(p));
         l.set(p, t);
      }

      return l;
   }

   public static <K> ReferenceBigList<K> emptyList() {
      return EMPTY_BIG_LIST;
   }

   public static <K> ReferenceBigList<K> singleton(K element) {
      return new ReferenceBigLists.Singleton<>(element);
   }

   public static <K> ReferenceBigList<K> synchronize(ReferenceBigList<K> l) {
      return new ReferenceBigLists.SynchronizedBigList<>(l);
   }

   public static <K> ReferenceBigList<K> synchronize(ReferenceBigList<K> l, Object sync) {
      return new ReferenceBigLists.SynchronizedBigList<>(l, sync);
   }

   public static <K> ReferenceBigList<K> unmodifiable(ReferenceBigList<? extends K> l) {
      return new ReferenceBigLists.UnmodifiableBigList<>(l);
   }

   public static <K> ReferenceBigList<K> asBigList(ReferenceList<K> list) {
      return new ReferenceBigLists.ListBigList<>(list);
   }

   public static class EmptyBigList<K> extends ReferenceCollections.EmptyCollection<K> implements ReferenceBigList<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigList() {
      }

      @Override
      public K get(long i) {
         throw new IndexOutOfBoundsException();
      }

      @Override
      public boolean remove(Object k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public K remove(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(long index, K k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public K set(long index, K k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long indexOf(Object k) {
         return -1L;
      }

      @Override
      public long lastIndexOf(Object k) {
         return -1L;
      }

      @Override
      public boolean addAll(long i, Collection<? extends K> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectBigListIterator<K> listIterator() {
         return ObjectBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      @Override
      public ObjectBigListIterator<K> iterator() {
         return ObjectBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      @Override
      public ObjectBigListIterator<K> listIterator(long i) {
         if (i == 0L) {
            return ObjectBigListIterators.EMPTY_BIG_LIST_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(i));
         }
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return ObjectSpliterators.EMPTY_SPLITERATOR;
      }

      @Override
      public ReferenceBigList<K> subList(long from, long to) {
         if (from == 0L && to == 0L) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public void getElements(long from, Object[][] a, long offset, long length) {
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
      public void addElements(long index, K[][] a, long offset, long length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, K[][] a) {
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

      @Override
      public Object clone() {
         return ReferenceBigLists.EMPTY_BIG_LIST;
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
         return ReferenceBigLists.EMPTY_BIG_LIST;
      }
   }

   public static class ListBigList<K> extends AbstractReferenceBigList<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final ReferenceList<K> list;

      protected ListBigList(ReferenceList<K> list) {
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
      public ObjectBigListIterator<K> iterator() {
         return ObjectBigListIterators.asBigListIterator(this.list.iterator());
      }

      @Override
      public ObjectBigListIterator<K> listIterator() {
         return ObjectBigListIterators.asBigListIterator(this.list.listIterator());
      }

      @Override
      public ObjectBigListIterator<K> listIterator(long index) {
         return ObjectBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(index)));
      }

      @Override
      public boolean addAll(long index, Collection<? extends K> c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public ReferenceBigList<K> subList(long from, long to) {
         return new ReferenceBigLists.ListBigList<>(this.list.subList(this.intIndex(from), this.intIndex(to)));
      }

      @Override
      public boolean contains(Object key) {
         return this.list.contains(key);
      }

      @Override
      public Object[] toArray() {
         return this.list.toArray();
      }

      @Override
      public void removeElements(long from, long to) {
         this.list.removeElements(this.intIndex(from), this.intIndex(to));
      }

      @Override
      public void add(long index, K key) {
         this.list.add(this.intIndex(index), key);
      }

      @Override
      public boolean add(K key) {
         return this.list.add(key);
      }

      @Override
      public K get(long index) {
         return this.list.get(this.intIndex(index));
      }

      @Override
      public long indexOf(Object k) {
         return this.list.indexOf(k);
      }

      @Override
      public long lastIndexOf(Object k) {
         return this.list.lastIndexOf(k);
      }

      @Override
      public K remove(long index) {
         return this.list.remove(this.intIndex(index));
      }

      @Override
      public K set(long index, K k) {
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
      public boolean addAll(Collection<? extends K> c) {
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

   public static class Singleton<K> extends AbstractReferenceBigList<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final K element;

      protected Singleton(K element) {
         this.element = element;
      }

      @Override
      public K get(long i) {
         if (i == 0L) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public boolean remove(Object k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public K remove(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean contains(Object k) {
         return k == this.element;
      }

      @Override
      public long indexOf(Object k) {
         return k == this.element ? 0L : -1L;
      }

      @Override
      public Object[] toArray() {
         return new Object[]{this.element};
      }

      @Override
      public ObjectBigListIterator<K> listIterator() {
         return ObjectBigListIterators.singleton(this.element);
      }

      @Override
      public ObjectBigListIterator<K> listIterator(long i) {
         if (i <= 1L && i >= 0L) {
            ObjectBigListIterator<K> l = this.listIterator();
            if (i == 1L) {
               l.next();
            }

            return l;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return ObjectSpliterators.singleton(this.element);
      }

      @Override
      public ReferenceBigList<K> subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return (ReferenceBigList<K>)(from == 0L && to == 1L ? this : ReferenceBigLists.EMPTY_BIG_LIST);
         }
      }

      @Override
      public boolean addAll(long i, Collection<? extends K> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends K> c) {
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

   public static class SynchronizedBigList<K> extends ReferenceCollections.SynchronizedCollection<K> implements ReferenceBigList<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ReferenceBigList<K> list;

      protected SynchronizedBigList(ReferenceBigList<K> l, Object sync) {
         super(l, sync);
         this.list = l;
      }

      protected SynchronizedBigList(ReferenceBigList<K> l) {
         super(l);
         this.list = l;
      }

      @Override
      public K get(long i) {
         synchronized (this.sync) {
            return this.list.get(i);
         }
      }

      @Override
      public K set(long i, K k) {
         synchronized (this.sync) {
            return this.list.set(i, k);
         }
      }

      @Override
      public void add(long i, K k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Override
      public K remove(long i) {
         synchronized (this.sync) {
            return this.list.remove(i);
         }
      }

      @Override
      public long indexOf(Object k) {
         synchronized (this.sync) {
            return this.list.indexOf(k);
         }
      }

      @Override
      public long lastIndexOf(Object k) {
         synchronized (this.sync) {
            return this.list.lastIndexOf(k);
         }
      }

      @Override
      public boolean addAll(long index, Collection<? extends K> c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public void getElements(long from, Object[][] a, long offset, long length) {
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
      public void addElements(long index, K[][] a, long offset, long length) {
         synchronized (this.sync) {
            this.list.addElements(index, a, offset, length);
         }
      }

      @Override
      public void addElements(long index, K[][] a) {
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
      public ObjectBigListIterator<K> iterator() {
         return this.list.listIterator();
      }

      @Override
      public ObjectBigListIterator<K> listIterator() {
         return this.list.listIterator();
      }

      @Override
      public ObjectBigListIterator<K> listIterator(long i) {
         return this.list.listIterator(i);
      }

      @Override
      public ReferenceBigList<K> subList(long from, long to) {
         synchronized (this.sync) {
            return ReferenceBigLists.synchronize(this.list.subList(from, to), this.sync);
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
   }

   public static class UnmodifiableBigList<K> extends ReferenceCollections.UnmodifiableCollection<K> implements ReferenceBigList<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ReferenceBigList<? extends K> list;

      protected UnmodifiableBigList(ReferenceBigList<? extends K> l) {
         super(l);
         this.list = l;
      }

      @Override
      public K get(long i) {
         return (K)this.list.get(i);
      }

      @Override
      public K set(long i, K k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(long i, K k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public K remove(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long indexOf(Object k) {
         return this.list.indexOf(k);
      }

      @Override
      public long lastIndexOf(Object k) {
         return this.list.lastIndexOf(k);
      }

      @Override
      public boolean addAll(long index, Collection<? extends K> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void getElements(long from, Object[][] a, long offset, long length) {
         this.list.getElements(from, a, offset, length);
      }

      @Override
      public void removeElements(long from, long to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, K[][] a, long offset, long length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, K[][] a) {
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
      public ObjectBigListIterator<K> iterator() {
         return this.listIterator();
      }

      @Override
      public ObjectBigListIterator<K> listIterator() {
         return ObjectBigListIterators.unmodifiable(this.list.listIterator());
      }

      @Override
      public ObjectBigListIterator<K> listIterator(long i) {
         return ObjectBigListIterators.unmodifiable(this.list.listIterator(i));
      }

      @Override
      public ReferenceBigList<K> subList(long from, long to) {
         return ReferenceBigLists.unmodifiable(this.list.subList(from, to));
      }

      @Override
      public boolean equals(Object o) {
         return o == this ? true : this.list.equals(o);
      }

      @Override
      public int hashCode() {
         return this.list.hashCode();
      }
   }
}
