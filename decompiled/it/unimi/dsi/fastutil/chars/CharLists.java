package it.unimi.dsi.fastutil.chars;

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

public final class CharLists {
   public static final CharLists.EmptyList EMPTY_LIST = new CharLists.EmptyList();

   private CharLists() {
   }

   public static CharList shuffle(CharList l, Random random) {
      int i = l.size();

      while (i-- != 0) {
         int p = random.nextInt(i + 1);
         char t = l.getChar(i);
         l.set(i, l.getChar(p));
         l.set(p, t);
      }

      return l;
   }

   public static CharList emptyList() {
      return EMPTY_LIST;
   }

   public static CharList singleton(char element) {
      return new CharLists.Singleton(element);
   }

   public static CharList singleton(Object element) {
      return new CharLists.Singleton((Character)element);
   }

   public static CharList synchronize(CharList l) {
      return (CharList)(l instanceof RandomAccess ? new CharLists.SynchronizedRandomAccessList(l) : new CharLists.SynchronizedList(l));
   }

   public static CharList synchronize(CharList l, Object sync) {
      return (CharList)(l instanceof RandomAccess ? new CharLists.SynchronizedRandomAccessList(l, sync) : new CharLists.SynchronizedList(l, sync));
   }

   public static CharList unmodifiable(CharList l) {
      return (CharList)(l instanceof RandomAccess ? new CharLists.UnmodifiableRandomAccessList(l) : new CharLists.UnmodifiableList(l));
   }

   public static class EmptyList extends CharCollections.EmptyCollection implements CharList, RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyList() {
      }

      @Override
      public char getChar(int i) {
         throw new IndexOutOfBoundsException();
      }

      @Override
      public boolean rem(char k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char removeChar(int i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(int index, char k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char set(int index, char k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int indexOf(char k) {
         return -1;
      }

      @Override
      public int lastIndexOf(char k) {
         return -1;
      }

      @Override
      public boolean addAll(int i, Collection<? extends Character> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void replaceAll(UnaryOperator<Character> operator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void replaceAll(CharUnaryOperator operator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(CharList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int i, CharCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int i, CharList c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void add(int index, Character k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character get(int index) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean add(Character k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character set(int index, Character k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character remove(int k) {
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
      public void sort(CharComparator comparator) {
      }

      @Override
      public void unstableSort(CharComparator comparator) {
      }

      @Deprecated
      @Override
      public void sort(Comparator<? super Character> comparator) {
      }

      @Deprecated
      @Override
      public void unstableSort(Comparator<? super Character> comparator) {
      }

      @Override
      public CharListIterator listIterator() {
         return CharIterators.EMPTY_ITERATOR;
      }

      @Override
      public CharListIterator iterator() {
         return CharIterators.EMPTY_ITERATOR;
      }

      @Override
      public CharListIterator listIterator(int i) {
         if (i == 0) {
            return CharIterators.EMPTY_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(i));
         }
      }

      @Override
      public CharList subList(int from, int to) {
         if (from == 0 && to == 0) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public void getElements(int from, char[] a, int offset, int length) {
         if (from != 0 || length != 0 || offset < 0 || offset > a.length) {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public void removeElements(int from, int to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, char[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, char[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(char[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, char[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, char[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void size(int s) {
         throw new UnsupportedOperationException();
      }

      public int compareTo(List<? extends Character> o) {
         if (o == this) {
            return 0;
         } else {
            return o.isEmpty() ? 0 : -1;
         }
      }

      @Override
      public Object clone() {
         return CharLists.EMPTY_LIST;
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
         return CharLists.EMPTY_LIST;
      }
   }

   abstract static class ImmutableListBase extends AbstractCharList implements CharList {
      @Deprecated
      @Override
      public final void add(int index, char k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean add(char k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(Collection<? extends Character> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(int index, Collection<? extends Character> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final char removeChar(int index) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean rem(char k) {
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
      public final boolean removeIf(Predicate<? super Character> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean removeIf(CharPredicate c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void replaceAll(UnaryOperator<Character> operator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void replaceAll(IntUnaryOperator operator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void add(int index, Character k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean add(Character k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final Character remove(int index) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean remove(Object k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final Character set(int index, Character k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(CharCollection c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(CharList c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(int index, CharCollection c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean addAll(int index, CharList c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean removeAll(CharCollection c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final boolean retainAll(CharCollection c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final char set(int index, char k) {
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
      public final void addElements(int index, char[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void setElements(int index, char[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void sort(CharComparator comp) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void unstableSort(CharComparator comp) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void sort(Comparator<? super Character> comparator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public final void unstableSort(Comparator<? super Character> comparator) {
         throw new UnsupportedOperationException();
      }
   }

   public static class Singleton extends AbstractCharList implements RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final char element;

      protected Singleton(char element) {
         this.element = element;
      }

      @Override
      public char getChar(int i) {
         if (i == 0) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public boolean rem(char k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char removeChar(int i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean contains(char k) {
         return k == this.element;
      }

      @Override
      public int indexOf(char k) {
         return k == this.element ? 0 : -1;
      }

      @Override
      public char[] toCharArray() {
         return new char[]{this.element};
      }

      @Override
      public CharListIterator listIterator() {
         return CharIterators.singleton(this.element);
      }

      @Override
      public CharListIterator iterator() {
         return this.listIterator();
      }

      @Override
      public CharSpliterator spliterator() {
         return CharSpliterators.singleton(this.element);
      }

      @Override
      public CharListIterator listIterator(int i) {
         if (i <= 1 && i >= 0) {
            CharListIterator l = this.listIterator();
            if (i == 1) {
               l.nextChar();
            }

            return l;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public CharList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return (CharList)(from == 0 && to == 1 ? this : CharLists.EMPTY_LIST);
         }
      }

      @Deprecated
      @Override
      public void forEach(Consumer<? super Character> action) {
         action.accept(this.element);
      }

      @Override
      public boolean addAll(int i, Collection<? extends Character> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends Character> c) {
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
      public boolean removeIf(Predicate<? super Character> filter) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void replaceAll(UnaryOperator<Character> operator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void replaceAll(CharUnaryOperator operator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void forEach(CharConsumer action) {
         action.accept(this.element);
      }

      @Override
      public boolean addAll(CharList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int i, CharList c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int i, CharCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(CharCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(CharCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(CharCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeIf(CharPredicate filter) {
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
      public void sort(CharComparator comparator) {
      }

      @Override
      public void unstableSort(CharComparator comparator) {
      }

      @Deprecated
      @Override
      public void sort(Comparator<? super Character> comparator) {
      }

      @Deprecated
      @Override
      public void unstableSort(Comparator<? super Character> comparator) {
      }

      @Override
      public void getElements(int from, char[] a, int offset, int length) {
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
      public void addElements(int index, char[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, char[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(char[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, char[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, char[] a, int offset, int length) {
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

   public static class SynchronizedList extends CharCollections.SynchronizedCollection implements CharList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharList list;

      protected SynchronizedList(CharList l, Object sync) {
         super(l, sync);
         this.list = l;
      }

      protected SynchronizedList(CharList l) {
         super(l);
         this.list = l;
      }

      @Override
      public char getChar(int i) {
         synchronized (this.sync) {
            return this.list.getChar(i);
         }
      }

      @Override
      public char set(int i, char k) {
         synchronized (this.sync) {
            return this.list.set(i, k);
         }
      }

      @Override
      public void add(int i, char k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Override
      public char removeChar(int i) {
         synchronized (this.sync) {
            return this.list.removeChar(i);
         }
      }

      @Override
      public int indexOf(char k) {
         synchronized (this.sync) {
            return this.list.indexOf(k);
         }
      }

      @Override
      public int lastIndexOf(char k) {
         synchronized (this.sync) {
            return this.list.lastIndexOf(k);
         }
      }

      @Override
      public boolean removeIf(CharPredicate filter) {
         synchronized (this.sync) {
            return this.list.removeIf(filter);
         }
      }

      @Override
      public void replaceAll(CharUnaryOperator operator) {
         synchronized (this.sync) {
            this.list.replaceAll(operator);
         }
      }

      @Override
      public boolean addAll(int index, Collection<? extends Character> c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public void getElements(int from, char[] a, int offset, int length) {
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
      public void addElements(int index, char[] a, int offset, int length) {
         synchronized (this.sync) {
            this.list.addElements(index, a, offset, length);
         }
      }

      @Override
      public void addElements(int index, char[] a) {
         synchronized (this.sync) {
            this.list.addElements(index, a);
         }
      }

      @Override
      public void setElements(char[] a) {
         synchronized (this.sync) {
            this.list.setElements(a);
         }
      }

      @Override
      public void setElements(int index, char[] a) {
         synchronized (this.sync) {
            this.list.setElements(index, a);
         }
      }

      @Override
      public void setElements(int index, char[] a, int offset, int length) {
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
      public CharListIterator listIterator() {
         return this.list.listIterator();
      }

      @Override
      public CharListIterator iterator() {
         return this.listIterator();
      }

      @Override
      public CharListIterator listIterator(int i) {
         return this.list.listIterator(i);
      }

      @Override
      public CharList subList(int from, int to) {
         synchronized (this.sync) {
            return new CharLists.SynchronizedList(this.list.subList(from, to), this.sync);
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

      public int compareTo(List<? extends Character> o) {
         synchronized (this.sync) {
            return this.list.compareTo(o);
         }
      }

      @Override
      public boolean addAll(int index, CharCollection c) {
         synchronized (this.sync) {
            return this.list.addAll(index, c);
         }
      }

      @Override
      public boolean addAll(int index, CharList l) {
         synchronized (this.sync) {
            return this.list.addAll(index, l);
         }
      }

      @Override
      public boolean addAll(CharList l) {
         synchronized (this.sync) {
            return this.list.addAll(l);
         }
      }

      @Deprecated
      @Override
      public Character get(int i) {
         synchronized (this.sync) {
            return this.list.get(i);
         }
      }

      @Deprecated
      @Override
      public void add(int i, Character k) {
         synchronized (this.sync) {
            this.list.add(i, k);
         }
      }

      @Deprecated
      @Override
      public Character set(int index, Character k) {
         synchronized (this.sync) {
            return this.list.set(index, k);
         }
      }

      @Deprecated
      @Override
      public Character remove(int i) {
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
      public void sort(CharComparator comparator) {
         synchronized (this.sync) {
            this.list.sort(comparator);
         }
      }

      @Override
      public void unstableSort(CharComparator comparator) {
         synchronized (this.sync) {
            this.list.unstableSort(comparator);
         }
      }

      @Deprecated
      @Override
      public void sort(Comparator<? super Character> comparator) {
         synchronized (this.sync) {
            this.list.sort(comparator);
         }
      }

      @Deprecated
      @Override
      public void unstableSort(Comparator<? super Character> comparator) {
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

   public static class SynchronizedRandomAccessList extends CharLists.SynchronizedList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected SynchronizedRandomAccessList(CharList l, Object sync) {
         super(l, sync);
      }

      protected SynchronizedRandomAccessList(CharList l) {
         super(l);
      }

      @Override
      public CharList subList(int from, int to) {
         synchronized (this.sync) {
            return new CharLists.SynchronizedRandomAccessList(this.list.subList(from, to), this.sync);
         }
      }
   }

   public static class UnmodifiableList extends CharCollections.UnmodifiableCollection implements CharList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharList list;

      protected UnmodifiableList(CharList l) {
         super(l);
         this.list = l;
      }

      @Override
      public char getChar(int i) {
         return this.list.getChar(i);
      }

      @Override
      public char set(int i, char k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(int i, char k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public char removeChar(int i) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int indexOf(char k) {
         return this.list.indexOf(k);
      }

      @Override
      public int lastIndexOf(char k) {
         return this.list.lastIndexOf(k);
      }

      @Override
      public boolean addAll(int index, Collection<? extends Character> c) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void replaceAll(UnaryOperator<Character> operator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void getElements(int from, char[] a, int offset, int length) {
         this.list.getElements(from, a, offset, length);
      }

      @Override
      public void removeElements(int from, int to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, char[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addElements(int index, char[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(char[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, char[] a) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setElements(int index, char[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void size(int size) {
         this.list.size(size);
      }

      @Override
      public CharListIterator listIterator() {
         return CharIterators.unmodifiable(this.list.listIterator());
      }

      @Override
      public CharListIterator iterator() {
         return this.listIterator();
      }

      @Override
      public CharListIterator listIterator(int i) {
         return CharIterators.unmodifiable(this.list.listIterator(i));
      }

      @Override
      public CharList subList(int from, int to) {
         return new CharLists.UnmodifiableList(this.list.subList(from, to));
      }

      @Override
      public boolean equals(Object o) {
         return o == this ? true : this.collection.equals(o);
      }

      @Override
      public int hashCode() {
         return this.collection.hashCode();
      }

      public int compareTo(List<? extends Character> o) {
         return this.list.compareTo(o);
      }

      @Override
      public boolean addAll(int index, CharCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(CharList l) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int index, CharList l) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void replaceAll(IntUnaryOperator operator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character get(int i) {
         return this.list.get(i);
      }

      @Deprecated
      @Override
      public void add(int i, Character k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character set(int index, Character k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Character remove(int i) {
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
      public void sort(CharComparator comparator) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void unstableSort(CharComparator comparator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void sort(Comparator<? super Character> comparator) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public void unstableSort(Comparator<? super Character> comparator) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableRandomAccessList extends CharLists.UnmodifiableList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected UnmodifiableRandomAccessList(CharList l) {
         super(l);
      }

      @Override
      public CharList subList(int from, int to) {
         return new CharLists.UnmodifiableRandomAccessList(this.list.subList(from, to));
      }
   }
}
