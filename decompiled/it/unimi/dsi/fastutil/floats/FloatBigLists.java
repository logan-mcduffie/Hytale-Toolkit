package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public final class FloatBigLists {
   public static final FloatBigLists.EmptyBigList EMPTY_BIG_LIST = new FloatBigLists.EmptyBigList();

   private FloatBigLists() {
   }

   public static FloatBigList shuffle(FloatBigList l, Random random) {
      long i = l.size64();

      while (i-- != 0L) {
         long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
         float t = l.getFloat(i);
         l.set(i, l.getFloat(p));
         l.set(p, t);
      }

      return l;
   }

   public static FloatBigList singleton(float element) {
      return new FloatBigLists.Singleton(element);
   }

   public static FloatBigList singleton(Object element) {
      return new FloatBigLists.Singleton((Float)element);
   }

   public static FloatBigList synchronize(FloatBigList l) {
      return new FloatBigLists.SynchronizedBigList(l);
   }

   public static FloatBigList synchronize(FloatBigList l, Object sync) {
      return new FloatBigLists.SynchronizedBigList(l, sync);
   }

   public static FloatBigList unmodifiable(FloatBigList l) {
      return new FloatBigLists.UnmodifiableBigList(l);
   }

   public static FloatBigList asBigList(FloatList list) {
      return new FloatBigLists.ListBigList(list);
   }

   public static class EmptyBigList extends FloatCollections.EmptyCollection implements FloatBigList, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigList() {
      }

      @Override
      public float getFloat(long i) {
         throw new IndexOutOfBoundsException();
      }

      @Override
      public boolean rem(float k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float removeFloat(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(long index, float k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float set(long index, float k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long indexOf(float k) {
         return -1L;
      }

      @Override
      public long lastIndexOf(float k) {
         return -1L;
      }

      @Override
      public boolean addAll(long i, Collection<? extends Float> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(FloatCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(FloatBigList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, FloatCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, FloatBigList c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void add(long index, Float k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean add(Float k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float get(long i) {
         throw new IndexOutOfBoundsException();
      }

      @Deprecated
      @Override
      public Float set(long index, Float k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float remove(long k) {
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
      public FloatBigListIterator listIterator() {
         return FloatBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      @Override
      public FloatBigListIterator iterator() {
         return FloatBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      @Override
      public FloatBigListIterator listIterator(long i) {
         if (i == 0L) {
            return FloatBigListIterators.EMPTY_BIG_LIST_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(i));
         }
      }

      @Override
      public FloatSpliterator spliterator() {
         return FloatSpliterators.EMPTY_SPLITERATOR;
      }

      @Override
      public FloatBigList subList(long from, long to) {
         if (from == 0L && to == 0L) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public void getElements(long from, float[][] a, long offset, long length) {
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
      public void addElements(long index, float[][] a, long offset, long length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, float[][] a) {
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

      public int compareTo(BigList<? extends Float> o) {
         if (o == this) {
            return 0;
         } else {
            return o.isEmpty() ? 0 : -1;
         }
      }

      @Override
      public Object clone() {
         return FloatBigLists.EMPTY_BIG_LIST;
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
         return FloatBigLists.EMPTY_BIG_LIST;
      }
   }

   public static class ListBigList extends AbstractFloatBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final FloatList list;

      protected ListBigList(FloatList list) {
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
      public FloatBigListIterator iterator() {
         return FloatBigListIterators.asBigListIterator(this.list.iterator());
      }

      @Override
      public FloatBigListIterator listIterator() {
         return FloatBigListIterators.asBigListIterator(this.list.listIterator());
      }

      @Override
      public FloatBigListIterator listIterator(long index) {
         return FloatBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(index)));
      }

      @Override
      public boolean addAll(long index, Collection<? extends Float> c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public FloatBigList subList(long from, long to) {
         return new FloatBigLists.ListBigList(this.list.subList(this.intIndex(from), this.intIndex(to)));
      }

      @Override
      public boolean contains(float key) {
         return this.list.contains(key);
      }

      @Override
      public float[] toFloatArray() {
         return this.list.toFloatArray();
      }

      @Override
      public void removeElements(long from, long to) {
         this.list.removeElements(this.intIndex(from), this.intIndex(to));
      }

      @Deprecated
      @Override
      public float[] toFloatArray(float[] a) {
         return this.list.toArray(a);
      }

      @Override
      public boolean addAll(long index, FloatCollection c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public boolean addAll(FloatCollection c) {
         return this.list.addAll(c);
      }

      @Override
      public boolean addAll(long index, FloatBigList c) {
         return this.list.addAll(this.intIndex(index), c);
      }

      @Override
      public boolean addAll(FloatBigList c) {
         return this.list.addAll(c);
      }

      @Override
      public boolean containsAll(FloatCollection c) {
         return this.list.containsAll(c);
      }

      @Override
      public boolean removeAll(FloatCollection c) {
         return this.list.removeAll(c);
      }

      @Override
      public boolean retainAll(FloatCollection c) {
         return this.list.retainAll(c);
      }

      @Override
      public void add(long index, float key) {
         this.list.add(this.intIndex(index), key);
      }

      @Override
      public boolean add(float key) {
         return this.list.add(key);
      }

      @Override
      public float getFloat(long index) {
         return this.list.getFloat(this.intIndex(index));
      }

      @Override
      public long indexOf(float k) {
         return this.list.indexOf(k);
      }

      @Override
      public long lastIndexOf(float k) {
         return this.list.lastIndexOf(k);
      }

      @Override
      public float removeFloat(long index) {
         return this.list.removeFloat(this.intIndex(index));
      }

      @Override
      public float set(long index, float k) {
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
      public boolean addAll(Collection<? extends Float> c) {
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

   public static class Singleton extends AbstractFloatBigList implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final float element;

      protected Singleton(float element) {
         this.element = element;
      }

      @Override
      public float getFloat(long i) {
         if (i == 0L) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public boolean rem(float k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float removeFloat(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean contains(float k) {
         return Float.floatToIntBits(k) == Float.floatToIntBits(this.element);
      }

      @Override
      public long indexOf(float k) {
         return Float.floatToIntBits(k) == Float.floatToIntBits(this.element) ? 0L : -1L;
      }

      @Override
      public float[] toFloatArray() {
         return new float[]{this.element};
      }

      @Override
      public FloatBigListIterator listIterator() {
         return FloatBigListIterators.singleton(this.element);
      }

      @Override
      public FloatBigListIterator listIterator(long i) {
         if (i <= 1L && i >= 0L) {
            FloatBigListIterator l = this.listIterator();
            if (i == 1L) {
               l.nextFloat();
            }

            return l;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public FloatSpliterator spliterator() {
         return FloatSpliterators.singleton(this.element);
      }

      @Override
      public FloatBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return (FloatBigList)(from == 0L && to == 1L ? this : FloatBigLists.EMPTY_BIG_LIST);
         }
      }

      @Override
      public boolean addAll(long i, Collection<? extends Float> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends Float> c) {
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
      public boolean addAll(FloatBigList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, FloatBigList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long i, FloatCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(FloatCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(FloatCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(FloatCollection c) {
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

   public static class SynchronizedBigList extends FloatCollections.SynchronizedCollection implements FloatBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatBigList list;

      protected SynchronizedBigList(FloatBigList l, Object sync) {
         super(l, sync);
         this.list = l;
      }

      protected SynchronizedBigList(FloatBigList l) {
         super(l);
         this.list = l;
      }

      @Override
      public float getFloat(long i) {
         synchronized (this.sync) {
            return this.list.getFloat(i);
         }
      }

      @Override
      public float set(long i, float k) {
         synchronized (this.sync) {
            return this.list.set(i, k);
         }
      }

      @Override
      public void add(long i, float k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Override
      public float removeFloat(long i) {
         synchronized (this.sync) {
            return this.list.removeFloat(i);
         }
      }

      @Override
      public long indexOf(float k) {
         synchronized (this.sync) {
            return this.list.indexOf(k);
         }
      }

      @Override
      public long lastIndexOf(float k) {
         synchronized (this.sync) {
            return this.list.lastIndexOf(k);
         }
      }

      @Override
      public boolean addAll(long index, Collection<? extends Float> c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public void getElements(long from, float[][] a, long offset, long length) {
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
      public void addElements(long index, float[][] a, long offset, long length) {
         synchronized (this.sync) {
            this.list.addElements(index, a, offset, length);
         }
      }

      @Override
      public void addElements(long index, float[][] a) {
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
      public FloatBigListIterator iterator() {
         return this.list.listIterator();
      }

      @Override
      public FloatBigListIterator listIterator() {
         return this.list.listIterator();
      }

      @Override
      public FloatBigListIterator listIterator(long i) {
         return this.list.listIterator(i);
      }

      @Override
      public FloatBigList subList(long from, long to) {
         synchronized (this.sync) {
            return FloatBigLists.synchronize(this.list.subList(from, to), this.sync);
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

      public int compareTo(BigList<? extends Float> o) {
         synchronized (this.sync) {
            return this.list.compareTo(o);
         }
      }

      @Override
      public boolean addAll(long index, FloatCollection c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public boolean addAll(long index, FloatBigList l) {
         synchronized (this.sync) {
            return this.list.addAll(index, l);
         }
      }

      @Override
      public boolean addAll(FloatBigList l) {
         synchronized (this.sync) {
            return this.list.addAll(l);
         }
      }

      @Deprecated
      @Override
      public void add(long i, Float k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Deprecated
      @Override
      public Float get(long i) {
         synchronized (this.sync) {
            return this.list.get(i);
         }
      }

      @Deprecated
      @Override
      public Float set(long index, Float k) {
         synchronized (this.sync) {
            return this.list.set(index, k);
         }
      }

      @Deprecated
      @Override
      public Float remove(long i) {
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

   public static class UnmodifiableBigList extends FloatCollections.UnmodifiableCollection implements FloatBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatBigList list;

      protected UnmodifiableBigList(FloatBigList l) {
         super(l);
         this.list = l;
      }

      @Override
      public float getFloat(long i) {
         return this.list.getFloat(i);
      }

      @Override
      public float set(long i, float k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(long i, float k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public float removeFloat(long i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long indexOf(float k) {
         return this.list.indexOf(k);
      }

      @Override
      public long lastIndexOf(float k) {
         return this.list.lastIndexOf(k);
      }

      @Override
      public boolean addAll(long index, Collection<? extends Float> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void getElements(long from, float[][] a, long offset, long length) {
         this.list.getElements(from, a, offset, length);
      }

      @Override
      public void removeElements(long from, long to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, float[][] a, long offset, long length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(long index, float[][] a) {
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
      public FloatBigListIterator iterator() {
         return this.listIterator();
      }

      @Override
      public FloatBigListIterator listIterator() {
         return FloatBigListIterators.unmodifiable(this.list.listIterator());
      }

      @Override
      public FloatBigListIterator listIterator(long i) {
         return FloatBigListIterators.unmodifiable(this.list.listIterator(i));
      }

      @Override
      public FloatBigList subList(long from, long to) {
         return FloatBigLists.unmodifiable(this.list.subList(from, to));
      }

      @Override
      public boolean equals(Object o) {
         return o == this ? true : this.list.equals(o);
      }

      @Override
      public int hashCode() {
         return this.list.hashCode();
      }

      public int compareTo(BigList<? extends Float> o) {
         return this.list.compareTo(o);
      }

      @Override
      public boolean addAll(long index, FloatCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(FloatBigList l) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long index, FloatBigList l) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float get(long i) {
         return this.list.get(i);
      }

      @Deprecated
      @Override
      public void add(long i, Float k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float set(long index, Float k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Float remove(long i) {
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
