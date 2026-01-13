package com.google.protobuf;

import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;

final class DoubleArrayList extends AbstractProtobufList<Double> implements Internal.DoubleList, RandomAccess, PrimitiveNonBoxingCollection {
   private static final double[] EMPTY_ARRAY = new double[0];
   private static final DoubleArrayList EMPTY_LIST = new DoubleArrayList(EMPTY_ARRAY, 0, false);
   private double[] array;
   private int size;

   public static DoubleArrayList emptyList() {
      return EMPTY_LIST;
   }

   DoubleArrayList() {
      this(EMPTY_ARRAY, 0, true);
   }

   private DoubleArrayList(double[] other, int size, boolean isMutable) {
      super(isMutable);
      this.array = other;
      this.size = size;
   }

   DoubleArrayList(DoubleArrayList other, boolean isMutable) {
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
      } else if (!(o instanceof DoubleArrayList)) {
         return super.equals(o);
      } else {
         DoubleArrayList other = (DoubleArrayList)o;
         if (this.size != other.size) {
            return false;
         } else {
            double[] arr = other.array;

            for (int i = 0; i < this.size; i++) {
               if (Double.doubleToLongBits(this.array[i]) != Double.doubleToLongBits(arr[i])) {
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
         long bits = Double.doubleToLongBits(this.array[i]);
         result = 31 * result + Internal.hashLong(bits);
      }

      return result;
   }

   @Override
   public Internal.DoubleList mutableCopyWithCapacity(int capacity) {
      if (capacity < this.size) {
         throw new IllegalArgumentException();
      } else {
         double[] newArray = capacity == 0 ? EMPTY_ARRAY : Arrays.copyOf(this.array, capacity);
         return new DoubleArrayList(newArray, this.size, true);
      }
   }

   public Double get(int index) {
      return this.getDouble(index);
   }

   @Override
   public double getDouble(int index) {
      this.ensureIndexInRange(index);
      return this.array[index];
   }

   @Override
   public int indexOf(Object element) {
      if (!(element instanceof Double)) {
         return -1;
      } else {
         double unboxedElement = (Double)element;
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

   public Double set(int index, Double element) {
      return this.setDouble(index, element);
   }

   @Override
   public double setDouble(int index, double element) {
      this.ensureIsMutable();
      this.ensureIndexInRange(index);
      double previousValue = this.array[index];
      this.array[index] = element;
      return previousValue;
   }

   public boolean add(Double element) {
      this.addDouble(element);
      return true;
   }

   public void add(int index, Double element) {
      this.addDouble(index, element);
   }

   @Override
   public void addDouble(double element) {
      this.ensureIsMutable();
      if (this.size == this.array.length) {
         int length = growSize(this.array.length);
         double[] newArray = new double[length];
         System.arraycopy(this.array, 0, newArray, 0, this.size);
         this.array = newArray;
      }

      this.array[this.size++] = element;
   }

   private void addDouble(int index, double element) {
      this.ensureIsMutable();
      if (index >= 0 && index <= this.size) {
         if (this.size < this.array.length) {
            System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
         } else {
            int length = growSize(this.array.length);
            double[] newArray = new double[length];
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
   public boolean addAll(Collection<? extends Double> collection) {
      this.ensureIsMutable();
      Internal.checkNotNull(collection);
      if (!(collection instanceof DoubleArrayList)) {
         return super.addAll(collection);
      } else {
         DoubleArrayList list = (DoubleArrayList)collection;
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

   public Double remove(int index) {
      this.ensureIsMutable();
      this.ensureIndexInRange(index);
      double value = this.array[index];
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
            this.array = new double[Math.max(minCapacity, 10)];
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
