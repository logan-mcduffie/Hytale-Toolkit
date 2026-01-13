package com.google.protobuf;

import java.util.Arrays;
import java.util.RandomAccess;

final class ProtobufArrayList<E> extends AbstractProtobufList<E> implements RandomAccess {
   private static final Object[] EMPTY_ARRAY = new Object[0];
   private static final ProtobufArrayList<Object> EMPTY_LIST = new ProtobufArrayList<>(EMPTY_ARRAY, 0, false);
   private E[] array;
   private int size;

   public static <E> ProtobufArrayList<E> emptyList() {
      return (ProtobufArrayList<E>)EMPTY_LIST;
   }

   ProtobufArrayList() {
      this((E[])EMPTY_ARRAY, 0, true);
   }

   private ProtobufArrayList(E[] array, int size, boolean isMutable) {
      super(isMutable);
      this.array = array;
      this.size = size;
   }

   public ProtobufArrayList<E> mutableCopyWithCapacity(int capacity) {
      if (capacity < this.size) {
         throw new IllegalArgumentException();
      } else {
         E[] newArray = (E[])(capacity == 0 ? EMPTY_ARRAY : Arrays.copyOf(this.array, capacity));
         return new ProtobufArrayList<>(newArray, this.size, true);
      }
   }

   @Override
   public boolean add(E element) {
      this.ensureIsMutable();
      if (this.size == this.array.length) {
         int length = growSize(this.array.length);
         E[] newArray = Arrays.copyOf(this.array, length);
         this.array = newArray;
      }

      this.array[this.size++] = element;
      this.modCount++;
      return true;
   }

   private static int growSize(int previousSize) {
      return Math.max(previousSize * 3 / 2 + 1, 10);
   }

   @Override
   public void add(int index, E element) {
      this.ensureIsMutable();
      if (index >= 0 && index <= this.size) {
         if (this.size < this.array.length) {
            System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
         } else {
            int length = growSize(this.array.length);
            E[] newArray = (E[])createArray(length);
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
   public E get(int index) {
      this.ensureIndexInRange(index);
      return this.array[index];
   }

   @Override
   public E remove(int index) {
      this.ensureIsMutable();
      this.ensureIndexInRange(index);
      E value = this.array[index];
      if (index < this.size - 1) {
         System.arraycopy(this.array, index + 1, this.array, index, this.size - index - 1);
      }

      this.size--;
      this.modCount++;
      return value;
   }

   @Override
   public E set(int index, E element) {
      this.ensureIsMutable();
      this.ensureIndexInRange(index);
      E toReturn = this.array[index];
      this.array[index] = element;
      this.modCount++;
      return toReturn;
   }

   @Override
   public int size() {
      return this.size;
   }

   void ensureCapacity(int minCapacity) {
      if (minCapacity > this.array.length) {
         if (this.array.length == 0) {
            this.array = (E[])(new Object[Math.max(minCapacity, 10)]);
         } else {
            int n = this.array.length;

            while (n < minCapacity) {
               n = growSize(n);
            }

            this.array = Arrays.copyOf(this.array, n);
         }
      }
   }

   private static <E> E[] createArray(int capacity) {
      return (E[])(new Object[capacity]);
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
