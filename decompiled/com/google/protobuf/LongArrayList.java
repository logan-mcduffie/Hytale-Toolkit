package com.google.protobuf;

import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;

final class LongArrayList extends AbstractProtobufList<Long> implements Internal.LongList, RandomAccess, PrimitiveNonBoxingCollection {
   private static final long[] EMPTY_ARRAY = new long[0];
   private static final LongArrayList EMPTY_LIST = new LongArrayList(EMPTY_ARRAY, 0, false);
   private long[] array;
   private int size;

   public static LongArrayList emptyList() {
      return EMPTY_LIST;
   }

   LongArrayList() {
      this(EMPTY_ARRAY, 0, true);
   }

   private LongArrayList(long[] other, int size, boolean isMutable) {
      super(isMutable);
      this.array = other;
      this.size = size;
   }

   LongArrayList(LongArrayList other, boolean isMutable) {
      this(other.size == 0 ? EMPTY_ARRAY : Arrays.copyOf(other.array, other.size), other.size, isMutable);
   }

   @Override
   protected void removeRange(int fromIndex, int toIndex) {
      this.ensureIsMutable();
      if (toIndex < fromIndex) {
         throw new IndexOutOfBoundsException("toIndex < fromIndex");
      } else {
         System.arraycopy(this.array, toIndex, this.array, fromIndex, this.size - toIndex);
         this.size -= toIndex - fromIndex;
         this.modCount++;
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof LongArrayList)) {
         return super.equals(o);
      } else {
         LongArrayList other = (LongArrayList)o;
         if (this.size != other.size) {
            return false;
         } else {
            long[] arr = other.array;

            for (int i = 0; i < this.size; i++) {
               if (this.array[i] != arr[i]) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Override
   public int hashCode() {
      int result = 1;

      for (int i = 0; i < this.size; i++) {
         result = 31 * result + Internal.hashLong(this.array[i]);
      }

      return result;
   }

   @Override
   public Internal.LongList mutableCopyWithCapacity(int capacity) {
      if (capacity < this.size) {
         throw new IllegalArgumentException();
      } else {
         long[] newArray = capacity == 0 ? EMPTY_ARRAY : Arrays.copyOf(this.array, capacity);
         return new LongArrayList(newArray, this.size, true);
      }
   }

   public Long get(int index) {
      return this.getLong(index);
   }

   @Override
   public long getLong(int index) {
      this.ensureIndexInRange(index);
      return this.array[index];
   }

   @Override
   public int indexOf(Object element) {
      if (!(element instanceof Long)) {
         return -1;
      } else {
         long unboxedElement = (Long)element;
         int numElems = this.size();

         for (int i = 0; i < numElems; i++) {
            if (this.array[i] == unboxedElement) {
               return i;
            }
         }

         return -1;
      }
   }

   @Override
   public boolean contains(Object element) {
      return this.indexOf(element) != -1;
   }

   @Override
   public int size() {
      return this.size;
   }

   public Long set(int index, Long element) {
      return this.setLong(index, element);
   }

   @Override
   public long setLong(int index, long element) {
      this.ensureIsMutable();
      this.ensureIndexInRange(index);
      long previousValue = this.array[index];
      this.array[index] = element;
      return previousValue;
   }

   public boolean add(Long element) {
      this.addLong(element);
      return true;
   }

   public void add(int index, Long element) {
      this.addLong(index, element);
   }

   @Override
   public void addLong(long element) {
      this.ensureIsMutable();
      if (this.size == this.array.length) {
         int length = growSize(this.array.length);
         long[] newArray = new long[length];
         System.arraycopy(this.array, 0, newArray, 0, this.size);
         this.array = newArray;
      }

      this.array[this.size++] = element;
   }

   private void addLong(int index, long element) {
      this.ensureIsMutable();
      if (index >= 0 && index <= this.size) {
         if (this.size < this.array.length) {
            System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
         } else {
            int length = growSize(this.array.length);
            long[] newArray = new long[length];
            System.arraycopy(this.array, 0, newArray, 0, index);
            System.arraycopy(this.array, index, newArray, index + 1, this.size - index);
            this.array = newArray;
         }

         this.array[index] = element;
         this.size++;
         this.modCount++;
      } else {
         throw new IndexOutOfBoundsException(this.makeOutOfBoundsExceptionMessage(index));
      }
   }

   @Override
   public boolean addAll(Collection<? extends Long> collection) {
      this.ensureIsMutable();
      Internal.checkNotNull(collection);
      if (!(collection instanceof LongArrayList)) {
         return super.addAll(collection);
      } else {
         LongArrayList list = (LongArrayList)collection;
         if (list.size == 0) {
            return false;
         } else {
            int overflow = Integer.MAX_VALUE - this.size;
            if (overflow < list.size) {
               throw new OutOfMemoryError();
            } else {
               int newSize = this.size + list.size;
               if (newSize > this.array.length) {
                  this.array = Arrays.copyOf(this.array, newSize);
               }

               System.arraycopy(list.array, 0, this.array, this.size, list.size);
               this.size = newSize;
               this.modCount++;
               return true;
            }
         }
      }
   }

   public Long remove(int index) {
      this.ensureIsMutable();
      this.ensureIndexInRange(index);
      long value = this.array[index];
      if (index < this.size - 1) {
         System.arraycopy(this.array, index + 1, this.array, index, this.size - index - 1);
      }

      this.size--;
      this.modCount++;
      return value;
   }

   void ensureCapacity(int minCapacity) {
      if (minCapacity > this.array.length) {
         if (this.array.length == 0) {
            this.array = new long[Math.max(minCapacity, 10)];
         } else {
            int n = this.array.length;

            while (n < minCapacity) {
               n = growSize(n);
            }

            this.array = Arrays.copyOf(this.array, n);
         }
      }
   }

   private static int growSize(int previousSize) {
      return Math.max(previousSize * 3 / 2 + 1, 10);
   }

   private void ensureIndexInRange(int index) {
      if (index < 0 || index >= this.size) {
         throw new IndexOutOfBoundsException(this.makeOutOfBoundsExceptionMessage(index));
      }
   }

   private String makeOutOfBoundsExceptionMessage(int index) {
      return "Index:" + index + ", Size:" + this.size;
   }
}
