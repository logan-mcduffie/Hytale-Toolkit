package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class ShortLists {
   public static final ShortLists.EmptyList EMPTY_LIST = new ShortLists.EmptyList();

   private ShortLists() {
   }

   public static ShortList shuffle(ShortList l, Random random) {
      int i = l.size();

      while (i-- != 0) {
         int p = random.nextInt(i + 1);
         short t = l.getShort(i);
         l.set(i, l.getShort(p));
         l.set(p, t);
      }

      return l;
   }

   public static ShortList emptyList() {
      return EMPTY_LIST;
   }

   public static ShortList singleton(short element) {
      return new ShortLists.Singleton(element);
   }

   public static ShortList singleton(Object element) {
      return new ShortLists.Singleton((Short)element);
   }

   public static ShortList synchronize(ShortList l) {
      return (ShortList)(l instanceof RandomAccess ? new ShortLists.SynchronizedRandomAccessList(l) : new ShortLists.SynchronizedList(l));
   }

   public static ShortList synchronize(ShortList l, Object sync) {
      return (ShortList)(l instanceof RandomAccess ? new ShortLists.SynchronizedRandomAccessList(l, sync) : new ShortLists.SynchronizedList(l, sync));
   }

   public static ShortList unmodifiable(ShortList l) {
      return (ShortList)(l instanceof RandomAccess ? new ShortLists.UnmodifiableRandomAccessList(l) : new ShortLists.UnmodifiableList(l));
   }

   public static class EmptyList extends ShortCollections.EmptyCollection implements ShortList, RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyList() {
      }

      @Override
      public short getShort(int i) {
         throw new IndexOutOfBoundsException();
      }

      @Override
      public boolean rem(short k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short removeShort(int i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(int index, short k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short set(int index, short k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int indexOf(short k) {
         return -1;
      }

      @Override
      public int lastIndexOf(short k) {
         return -1;
      }

      @Override
      public boolean addAll(int i, Collection<? extends Short> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void replaceAll(UnaryOperator<Short> operator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void replaceAll(ShortUnaryOperator operator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(ShortList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int i, ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int i, ShortList c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void add(int index, Short k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short get(int index) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean add(Short k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short set(int index, Short k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short remove(int k) {
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
      public void sort(ShortComparator comparator) {
      }

      @Override
      public void unstableSort(ShortComparator comparator) {
      }

      @Deprecated
      @Override
      public void sort(Comparator<? super Short> comparator) {
      }

      @Deprecated
      @Override
      public void unstableSort(Comparator<? super Short> comparator) {
      }

      @Override
      public ShortListIterator listIterator() {
         return ShortIterators.EMPTY_ITERATOR;
      }

      @Override
      public ShortListIterator iterator() {
         return ShortIterators.EMPTY_ITERATOR;
      }

      @Override
      public ShortListIterator listIterator(int i) {
         if (i == 0) {
            return ShortIterators.EMPTY_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(i));
         }
      }

      @Override
      public ShortList subList(int from, int to) {
         if (from == 0 && to == 0) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public void getElements(int from, short[] a, int offset, int length) {
         if (from != 0 || length != 0 || offset < 0 || offset > a.length) {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public void removeElements(int from, int to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, short[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, short[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(short[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, short[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, short[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void size(int s) {
         throw new UnsupportedOperationException();
      }

      public int compareTo(List<? extends Short> o) {
         if (o == this) {
            return 0;
         } else {
            return o.isEmpty() ? 0 : -1;
         }
      }

      @Override
      public Object clone() {
         return ShortLists.EMPTY_LIST;
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
         return ShortLists.EMPTY_LIST;
      }
   }

   abstract static class ImmutableListBase extends AbstractShortList implements ShortList {
      @Deprecated
      @Override
      public final void add(int index, short k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean add(short k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(Collection<? extends Short> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(int index, Collection<? extends Short> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final short removeShort(int index) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean rem(short k) {
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
      public final boolean removeIf(Predicate<? super Short> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean removeIf(ShortPredicate c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void replaceAll(UnaryOperator<Short> operator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void replaceAll(IntUnaryOperator operator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void add(int index, Short k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean add(Short k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final Short remove(int index) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean remove(Object k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final Short set(int index, Short k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(ShortList c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(int index, ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(int index, ShortList c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean removeAll(ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean retainAll(ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final short set(int index, short k) {
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
      public final void addElements(int index, short[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void setElements(int index, short[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void sort(ShortComparator comp) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void unstableSort(ShortComparator comp) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void sort(Comparator<? super Short> comparator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void unstableSort(Comparator<? super Short> comparator) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractShortList implements RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final short element;

      protected Singleton(short element) {
         this.element = element;
      }

      @Override
      public short getShort(int i) {
         if (i == 0) {
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
      public short removeShort(int i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean contains(short k) {
         return k == this.element;
      }

      @Override
      public int indexOf(short k) {
         return k == this.element ? 0 : -1;
      }

      @Override
      public short[] toShortArray() {
         return new short[]{this.element};
      }

      @Override
      public ShortListIterator listIterator() {
         return ShortIterators.singleton(this.element);
      }

      @Override
      public ShortListIterator iterator() {
         return this.listIterator();
      }

      @Override
      public ShortSpliterator spliterator() {
         return ShortSpliterators.singleton(this.element);
      }

      @Override
      public ShortListIterator listIterator(int i) {
         if (i <= 1 && i >= 0) {
            ShortListIterator l = this.listIterator();
            if (i == 1) {
               l.nextShort();
            }

            return l;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public ShortList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return (ShortList)(from == 0 && to == 1 ? this : ShortLists.EMPTY_LIST);
         }
      }

      @Deprecated
      @Override
      public void forEach(Consumer<? super Short> action) {
         action.accept(this.element);
      }

      @Override
      public boolean addAll(int i, Collection<? extends Short> c) {
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

      @Deprecated
      @Override
      public boolean removeIf(Predicate<? super Short> filter) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void replaceAll(UnaryOperator<Short> operator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void replaceAll(ShortUnaryOperator operator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void forEach(ShortConsumer action) {
         action.accept(this.element);
      }

      @Override
      public boolean addAll(ShortList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int i, ShortList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int i, ShortCollection c) {
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
      public boolean removeIf(ShortPredicate filter) {
         throw new UnsupportedOperationException();
      }

      @Override
      public IntIterator intIterator() {
         return IntIterators.singleton(this.element);
      }

      @Override
      public IntSpliterator intSpliterator() {
         return IntSpliterators.singleton(this.element);
      }

      @Deprecated
      @Override
      public Object[] toArray() {
         return new Object[]{this.element};
      }

      @Override
      public void sort(ShortComparator comparator) {
      }

      @Override
      public void unstableSort(ShortComparator comparator) {
      }

      @Deprecated
      @Override
      public void sort(Comparator<? super Short> comparator) {
      }

      @Deprecated
      @Override
      public void unstableSort(Comparator<? super Short> comparator) {
      }

      @Override
      public void getElements(int from, short[] a, int offset, int length) {
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
      public void addElements(int index, short[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, short[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(short[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, short[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, short[] a, int offset, int length) {
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

   public static class SynchronizedList extends ShortCollections.SynchronizedCollection implements ShortList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortList list;

      protected SynchronizedList(ShortList l, Object sync) {
         super(l, sync);
         this.list = l;
      }

      protected SynchronizedList(ShortList l) {
         super(l);
         this.list = l;
      }

      @Override
      public short getShort(int i) {
         synchronized (this.sync) {
            return this.list.getShort(i);
         }
      }

      @Override
      public short set(int i, short k) {
         synchronized (this.sync) {
            return this.list.set(i, k);
         }
      }

      @Override
      public void add(int i, short k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Override
      public short removeShort(int i) {
         synchronized (this.sync) {
            return this.list.removeShort(i);
         }
      }

      @Override
      public int indexOf(short k) {
         synchronized (this.sync) {
            return this.list.indexOf(k);
         }
      }

      @Override
      public int lastIndexOf(short k) {
         synchronized (this.sync) {
            return this.list.lastIndexOf(k);
         }
      }

      @Override
      public boolean removeIf(ShortPredicate filter) {
         synchronized (this.sync) {
            return this.list.removeIf(filter);
         }
      }

      @Override
      public void replaceAll(ShortUnaryOperator operator) {
         synchronized (this.sync) {
            this.list.replaceAll(operator);
         }
      }

      @Override
      public boolean addAll(int index, Collection<? extends Short> c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public void getElements(int from, short[] a, int offset, int length) {
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
      public void addElements(int index, short[] a, int offset, int length) {
         synchronized (this.sync) {
            this.list.addElements(index, a, offset, length);
         }
      }

      @Override
      public void addElements(int index, short[] a) {
         synchronized (this.sync) {
            this.list.addElements(index, a);
         }
      }

      @Override
      public void setElements(short[] a) {
         synchronized (this.sync) {
            this.list.setElements(a);
         }
      }

      @Override
      public void setElements(int index, short[] a) {
         synchronized (this.sync) {
            this.list.setElements(index, a);
         }
      }

      @Override
      public void setElements(int index, short[] a, int offset, int length) {
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
      public ShortListIterator listIterator() {
         return this.list.listIterator();
      }

      @Override
      public ShortListIterator iterator() {
         return this.listIterator();
      }

      @Override
      public ShortListIterator listIterator(int i) {
         return this.list.listIterator(i);
      }

      @Override
      public ShortList subList(int from, int to) {
         synchronized (this.sync) {
            return new ShortLists.SynchronizedList(this.list.subList(from, to), this.sync);
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

      public int compareTo(List<? extends Short> o) {
         synchronized (this.sync) {
            return this.list.compareTo(o);
         }
      }

      @Override
      public boolean addAll(int index, ShortCollection c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public boolean addAll(int index, ShortList l) {
         synchronized (this.sync) {
            return this.list.addAll(index, l);
         }
      }

      @Override
      public boolean addAll(ShortList l) {
         synchronized (this.sync) {
            return this.list.addAll(l);
         }
      }

      @Deprecated
      @Override
      public Short get(int i) {
         synchronized (this.sync) {
            return this.list.get(i);
         }
      }

      @Deprecated
      @Override
      public void add(int i, Short k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Deprecated
      @Override
      public Short set(int index, Short k) {
         synchronized (this.sync) {
            return this.list.set(index, k);
         }
      }

      @Deprecated
      @Override
      public Short remove(int i) {
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
      public void sort(ShortComparator comparator) {
         synchronized (this.sync) {
            this.list.sort(comparator);
         }
      }

      @Override
      public void unstableSort(ShortComparator comparator) {
         synchronized (this.sync) {
            this.list.unstableSort(comparator);
         }
      }

      @Deprecated
      @Override
      public void sort(Comparator<? super Short> comparator) {
         synchronized (this.sync) {
            this.list.sort(comparator);
         }
      }

      @Deprecated
      @Override
      public void unstableSort(Comparator<? super Short> comparator) {
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

   public static class SynchronizedRandomAccessList extends ShortLists.SynchronizedList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected SynchronizedRandomAccessList(ShortList l, Object sync) {
         super(l, sync);
      }

      protected SynchronizedRandomAccessList(ShortList l) {
         super(l);
      }

      @Override
      public ShortList subList(int from, int to) {
         synchronized (this.sync) {
            return new ShortLists.SynchronizedRandomAccessList(this.list.subList(from, to), this.sync);
         }
      }
   }

   public static class UnmodifiableList extends ShortCollections.UnmodifiableCollection implements ShortList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortList list;

      protected UnmodifiableList(ShortList l) {
         super(l);
         this.list = l;
      }

      @Override
      public short getShort(int i) {
         return this.list.getShort(i);
      }

      @Override
      public short set(int i, short k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(int i, short k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public short removeShort(int i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int indexOf(short k) {
         return this.list.indexOf(k);
      }

      @Override
      public int lastIndexOf(short k) {
         return this.list.lastIndexOf(k);
      }

      @Override
      public boolean addAll(int index, Collection<? extends Short> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void replaceAll(UnaryOperator<Short> operator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void getElements(int from, short[] a, int offset, int length) {
         this.list.getElements(from, a, offset, length);
      }

      @Override
      public void removeElements(int from, int to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, short[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, short[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(short[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, short[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, short[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void size(int size) {
         this.list.size(size);
      }

      @Override
      public ShortListIterator listIterator() {
         return ShortIterators.unmodifiable(this.list.listIterator());
      }

      @Override
      public ShortListIterator iterator() {
         return this.listIterator();
      }

      @Override
      public ShortListIterator listIterator(int i) {
         return ShortIterators.unmodifiable(this.list.listIterator(i));
      }

      @Override
      public ShortList subList(int from, int to) {
         return new ShortLists.UnmodifiableList(this.list.subList(from, to));
      }

      @Override
      public boolean equals(Object o) {
         return o == this ? true : this.collection.equals(o);
      }

      @Override
      public int hashCode() {
         return this.collection.hashCode();
      }

      public int compareTo(List<? extends Short> o) {
         return this.list.compareTo(o);
      }

      @Override
      public boolean addAll(int index, ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(ShortList l) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int index, ShortList l) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void replaceAll(IntUnaryOperator operator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short get(int i) {
         return this.list.get(i);
      }

      @Deprecated
      @Override
      public void add(int i, Short k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short set(int index, Short k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Short remove(int i) {
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
      public void sort(ShortComparator comparator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void unstableSort(ShortComparator comparator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void sort(Comparator<? super Short> comparator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void unstableSort(Comparator<? super Short> comparator) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableRandomAccessList extends ShortLists.UnmodifiableList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected UnmodifiableRandomAccessList(ShortList l) {
         super(l);
      }

      @Override
      public ShortList subList(int from, int to) {
         return new ShortLists.UnmodifiableRandomAccessList(this.list.subList(from, to));
      }
   }
}
