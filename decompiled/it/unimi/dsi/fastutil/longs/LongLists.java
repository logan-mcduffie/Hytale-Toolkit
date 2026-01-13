package it.unimi.dsi.fastutil.longs;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class LongLists {
   public static final LongLists.EmptyList EMPTY_LIST = new LongLists.EmptyList();

   private LongLists() {
   }

   public static LongList shuffle(LongList l, Random random) {
      int i = l.size();

      while (i-- != 0) {
         int p = random.nextInt(i + 1);
         long t = l.getLong(i);
         l.set(i, l.getLong(p));
         l.set(p, t);
      }

      return l;
   }

   public static LongList emptyList() {
      return EMPTY_LIST;
   }

   public static LongList singleton(long element) {
      return new LongLists.Singleton(element);
   }

   public static LongList singleton(Object element) {
      return new LongLists.Singleton((Long)element);
   }

   public static LongList synchronize(LongList l) {
      return (LongList)(l instanceof RandomAccess ? new LongLists.SynchronizedRandomAccessList(l) : new LongLists.SynchronizedList(l));
   }

   public static LongList synchronize(LongList l, Object sync) {
      return (LongList)(l instanceof RandomAccess ? new LongLists.SynchronizedRandomAccessList(l, sync) : new LongLists.SynchronizedList(l, sync));
   }

   public static LongList unmodifiable(LongList l) {
      return (LongList)(l instanceof RandomAccess ? new LongLists.UnmodifiableRandomAccessList(l) : new LongLists.UnmodifiableList(l));
   }

   public static class EmptyList extends LongCollections.EmptyCollection implements LongList, RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyList() {
      }

      @Override
      public long getLong(int i) {
         throw new IndexOutOfBoundsException();
      }

      @Override
      public boolean rem(long k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long removeLong(int i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(int index, long k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long set(int index, long k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int indexOf(long k) {
         return -1;
      }

      @Override
      public int lastIndexOf(long k) {
         return -1;
      }

      @Override
      public boolean addAll(int i, Collection<? extends Long> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void replaceAll(UnaryOperator<Long> operator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void replaceAll(java.util.function.LongUnaryOperator operator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(LongList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int i, LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int i, LongList c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void add(int index, Long k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long get(int index) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean add(Long k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long set(int index, Long k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long remove(int k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public int indexOf(Object k) {
         return -1;
      }

      @Deprecated
      @Override
      public int lastIndexOf(Object k) {
         return -1;
      }

      @Override
      public void sort(LongComparator comparator) {
      }

      @Override
      public void unstableSort(LongComparator comparator) {
      }

      @Deprecated
      @Override
      public void sort(Comparator<? super Long> comparator) {
      }

      @Deprecated
      @Override
      public void unstableSort(Comparator<? super Long> comparator) {
      }

      @Override
      public LongListIterator listIterator() {
         return LongIterators.EMPTY_ITERATOR;
      }

      @Override
      public LongListIterator iterator() {
         return LongIterators.EMPTY_ITERATOR;
      }

      @Override
      public LongListIterator listIterator(int i) {
         if (i == 0) {
            return LongIterators.EMPTY_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(i));
         }
      }

      @Override
      public LongList subList(int from, int to) {
         if (from == 0 && to == 0) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public void getElements(int from, long[] a, int offset, int length) {
         if (from != 0 || length != 0 || offset < 0 || offset > a.length) {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public void removeElements(int from, int to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, long[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, long[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(long[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, long[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, long[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void size(int s) {
         throw new UnsupportedOperationException();
      }

      public int compareTo(List<? extends Long> o) {
         if (o == this) {
            return 0;
         } else {
            return o.isEmpty() ? 0 : -1;
         }
      }

      @Override
      public Object clone() {
         return LongLists.EMPTY_LIST;
      }

      @Override
      public int hashCode() {
         return 1;
      }

      @Override
      public boolean equals(Object o) {
         return o instanceof List && ((List)o).isEmpty();
      }

      @Override
      public String toString() {
         return "[]";
      }

      private Object readResolve() {
         return LongLists.EMPTY_LIST;
      }
   }

   abstract static class ImmutableListBase extends AbstractLongList implements LongList {
      @Deprecated
      @Override
      public final void add(int index, long k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean add(long k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(Collection<? extends Long> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(int index, Collection<? extends Long> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final long removeLong(int index) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean rem(long k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean removeAll(Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean retainAll(Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean removeIf(Predicate<? super Long> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean removeIf(java.util.function.LongPredicate c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void replaceAll(UnaryOperator<Long> operator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void replaceAll(java.util.function.LongUnaryOperator operator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void add(int index, Long k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean add(Long k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final Long remove(int index) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean remove(Object k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final Long set(int index, Long k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(LongList c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(int index, LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(int index, LongList c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean removeAll(LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean retainAll(LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final long set(int index, long k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void clear() {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void size(int size) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void removeElements(int from, int to) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void addElements(int index, long[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void setElements(int index, long[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void sort(LongComparator comp) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void unstableSort(LongComparator comp) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void sort(Comparator<? super Long> comparator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void unstableSort(Comparator<? super Long> comparator) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractLongList implements RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final long element;

      protected Singleton(long element) {
         this.element = element;
      }

      @Override
      public long getLong(int i) {
         if (i == 0) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public boolean rem(long k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long removeLong(int i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean contains(long k) {
         return k == this.element;
      }

      @Override
      public int indexOf(long k) {
         return k == this.element ? 0 : -1;
      }

      @Override
      public long[] toLongArray() {
         return new long[]{this.element};
      }

      @Override
      public LongListIterator listIterator() {
         return LongIterators.singleton(this.element);
      }

      @Override
      public LongListIterator iterator() {
         return this.listIterator();
      }

      @Override
      public LongSpliterator spliterator() {
         return LongSpliterators.singleton(this.element);
      }

      @Override
      public LongListIterator listIterator(int i) {
         if (i <= 1 && i >= 0) {
            LongListIterator l = this.listIterator();
            if (i == 1) {
               l.nextLong();
            }

            return l;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public LongList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return (LongList)(from == 0 && to == 1 ? this : LongLists.EMPTY_LIST);
         }
      }

      @Deprecated
      @Override
      public void forEach(Consumer<? super Long> action) {
         action.accept(this.element);
      }

      @Override
      public boolean addAll(int i, Collection<? extends Long> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends Long> c) {
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

      @Deprecated
      @Override
      public boolean removeIf(Predicate<? super Long> filter) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void replaceAll(UnaryOperator<Long> operator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void replaceAll(java.util.function.LongUnaryOperator operator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void forEach(java.util.function.LongConsumer action) {
         action.accept(this.element);
      }

      @Override
      public boolean addAll(LongList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int i, LongList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int i, LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeIf(java.util.function.LongPredicate filter) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Object[] toArray() {
         return new Object[]{this.element};
      }

      @Override
      public void sort(LongComparator comparator) {
      }

      @Override
      public void unstableSort(LongComparator comparator) {
      }

      @Deprecated
      @Override
      public void sort(Comparator<? super Long> comparator) {
      }

      @Deprecated
      @Override
      public void unstableSort(Comparator<? super Long> comparator) {
      }

      @Override
      public void getElements(int from, long[] a, int offset, int length) {
         if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
         } else if (offset + length > a.length) {
            throw new ArrayIndexOutOfBoundsException("End index (" + (offset + length) + ") is greater than array length (" + a.length + ")");
         } else if (from + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
         } else if (length > 0) {
            a[offset] = this.element;
         }
      }

      @Override
      public void removeElements(int from, int to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, long[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, long[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(long[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, long[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, long[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int size() {
         return 1;
      }

      @Override
      public void size(int size) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object clone() {
         return this;
      }
   }

   public static class SynchronizedList extends LongCollections.SynchronizedCollection implements LongList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongList list;

      protected SynchronizedList(LongList l, Object sync) {
         super(l, sync);
         this.list = l;
      }

      protected SynchronizedList(LongList l) {
         super(l);
         this.list = l;
      }

      @Override
      public long getLong(int i) {
         synchronized (this.sync) {
            return this.list.getLong(i);
         }
      }

      @Override
      public long set(int i, long k) {
         synchronized (this.sync) {
            return this.list.set(i, k);
         }
      }

      @Override
      public void add(int i, long k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Override
      public long removeLong(int i) {
         synchronized (this.sync) {
            return this.list.removeLong(i);
         }
      }

      @Override
      public int indexOf(long k) {
         synchronized (this.sync) {
            return this.list.indexOf(k);
         }
      }

      @Override
      public int lastIndexOf(long k) {
         synchronized (this.sync) {
            return this.list.lastIndexOf(k);
         }
      }

      @Override
      public boolean removeIf(java.util.function.LongPredicate filter) {
         synchronized (this.sync) {
            return this.list.removeIf(filter);
         }
      }

      @Override
      public void replaceAll(java.util.function.LongUnaryOperator operator) {
         synchronized (this.sync) {
            this.list.replaceAll(operator);
         }
      }

      @Override
      public boolean addAll(int index, Collection<? extends Long> c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public void getElements(int from, long[] a, int offset, int length) {
         synchronized (this.sync) {
            this.list.getElements(from, a, offset, length);
         }
      }

      @Override
      public void removeElements(int from, int to) {
         synchronized (this.sync) {
            this.list.removeElements(from, to);
         }
      }

      @Override
      public void addElements(int index, long[] a, int offset, int length) {
         synchronized (this.sync) {
            this.list.addElements(index, a, offset, length);
         }
      }

      @Override
      public void addElements(int index, long[] a) {
         synchronized (this.sync) {
            this.list.addElements(index, a);
         }
      }

      @Override
      public void setElements(long[] a) {
         synchronized (this.sync) {
            this.list.setElements(a);
         }
      }

      @Override
      public void setElements(int index, long[] a) {
         synchronized (this.sync) {
            this.list.setElements(index, a);
         }
      }

      @Override
      public void setElements(int index, long[] a, int offset, int length) {
         synchronized (this.sync) {
            this.list.setElements(index, a, offset, length);
         }
      }

      @Override
      public void size(int size) {
         synchronized (this.sync) {
            this.list.size(size);
         }
      }

      @Override
      public LongListIterator listIterator() {
         return this.list.listIterator();
      }

      @Override
      public LongListIterator iterator() {
         return this.listIterator();
      }

      @Override
      public LongListIterator listIterator(int i) {
         return this.list.listIterator(i);
      }

      @Override
      public LongList subList(int from, int to) {
         synchronized (this.sync) {
            return new LongLists.SynchronizedList(this.list.subList(from, to), this.sync);
         }
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            synchronized (this.sync) {
               return this.collection.equals(o);
            }
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.sync) {
            return this.collection.hashCode();
         }
      }

      public int compareTo(List<? extends Long> o) {
         synchronized (this.sync) {
            return this.list.compareTo(o);
         }
      }

      @Override
      public boolean addAll(int index, LongCollection c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public boolean addAll(int index, LongList l) {
         synchronized (this.sync) {
            return this.list.addAll(index, l);
         }
      }

      @Override
      public boolean addAll(LongList l) {
         synchronized (this.sync) {
            return this.list.addAll(l);
         }
      }

      @Deprecated
      @Override
      public Long get(int i) {
         synchronized (this.sync) {
            return this.list.get(i);
         }
      }

      @Deprecated
      @Override
      public void add(int i, Long k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Deprecated
      @Override
      public Long set(int index, Long k) {
         synchronized (this.sync) {
            return this.list.set(index, k);
         }
      }

      @Deprecated
      @Override
      public Long remove(int i) {
         synchronized (this.sync) {
            return this.list.remove(i);
         }
      }

      @Deprecated
      @Override
      public int indexOf(Object o) {
         synchronized (this.sync) {
            return this.list.indexOf(o);
         }
      }

      @Deprecated
      @Override
      public int lastIndexOf(Object o) {
         synchronized (this.sync) {
            return this.list.lastIndexOf(o);
         }
      }

      @Override
      public void sort(LongComparator comparator) {
         synchronized (this.sync) {
            this.list.sort(comparator);
         }
      }

      @Override
      public void unstableSort(LongComparator comparator) {
         synchronized (this.sync) {
            this.list.unstableSort(comparator);
         }
      }

      @Deprecated
      @Override
      public void sort(Comparator<? super Long> comparator) {
         synchronized (this.sync) {
            this.list.sort(comparator);
         }
      }

      @Deprecated
      @Override
      public void unstableSort(Comparator<? super Long> comparator) {
         synchronized (this.sync) {
            this.list.unstableSort(comparator);
         }
      }

      private void writeObject(ObjectOutputStream s) throws IOException {
         synchronized (this.sync) {
            s.defaultWriteObject();
         }
      }
   }

   public static class SynchronizedRandomAccessList extends LongLists.SynchronizedList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected SynchronizedRandomAccessList(LongList l, Object sync) {
         super(l, sync);
      }

      protected SynchronizedRandomAccessList(LongList l) {
         super(l);
      }

      @Override
      public LongList subList(int from, int to) {
         synchronized (this.sync) {
            return new LongLists.SynchronizedRandomAccessList(this.list.subList(from, to), this.sync);
         }
      }
   }

   public static class UnmodifiableList extends LongCollections.UnmodifiableCollection implements LongList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongList list;

      protected UnmodifiableList(LongList l) {
         super(l);
         this.list = l;
      }

      @Override
      public long getLong(int i) {
         return this.list.getLong(i);
      }

      @Override
      public long set(int i, long k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(int i, long k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long removeLong(int i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int indexOf(long k) {
         return this.list.indexOf(k);
      }

      @Override
      public int lastIndexOf(long k) {
         return this.list.lastIndexOf(k);
      }

      @Override
      public boolean addAll(int index, Collection<? extends Long> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void replaceAll(UnaryOperator<Long> operator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void getElements(int from, long[] a, int offset, int length) {
         this.list.getElements(from, a, offset, length);
      }

      @Override
      public void removeElements(int from, int to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, long[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, long[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(long[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, long[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, long[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void size(int size) {
         this.list.size(size);
      }

      @Override
      public LongListIterator listIterator() {
         return LongIterators.unmodifiable(this.list.listIterator());
      }

      @Override
      public LongListIterator iterator() {
         return this.listIterator();
      }

      @Override
      public LongListIterator listIterator(int i) {
         return LongIterators.unmodifiable(this.list.listIterator(i));
      }

      @Override
      public LongList subList(int from, int to) {
         return new LongLists.UnmodifiableList(this.list.subList(from, to));
      }

      @Override
      public boolean equals(Object o) {
         return o == this ? true : this.collection.equals(o);
      }

      @Override
      public int hashCode() {
         return this.collection.hashCode();
      }

      public int compareTo(List<? extends Long> o) {
         return this.list.compareTo(o);
      }

      @Override
      public boolean addAll(int index, LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(LongList l) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int index, LongList l) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void replaceAll(java.util.function.LongUnaryOperator operator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long get(int i) {
         return this.list.get(i);
      }

      @Deprecated
      @Override
      public void add(int i, Long k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long set(int index, Long k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Long remove(int i) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public int indexOf(Object o) {
         return this.list.indexOf(o);
      }

      @Deprecated
      @Override
      public int lastIndexOf(Object o) {
         return this.list.lastIndexOf(o);
      }

      @Override
      public void sort(LongComparator comparator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void unstableSort(LongComparator comparator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void sort(Comparator<? super Long> comparator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void unstableSort(Comparator<? super Long> comparator) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableRandomAccessList extends LongLists.UnmodifiableList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected UnmodifiableRandomAccessList(LongList l) {
         super(l);
      }

      @Override
      public LongList subList(int from, int to) {
         return new LongLists.UnmodifiableRandomAccessList(this.list.subList(from, to));
      }
   }
}
