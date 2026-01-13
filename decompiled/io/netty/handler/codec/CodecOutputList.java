package io.netty.handler.codec;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.AbstractList;
import java.util.RandomAccess;

final class CodecOutputList extends AbstractList<Object> implements RandomAccess {
   private static final CodecOutputList.CodecOutputListRecycler NOOP_RECYCLER = new CodecOutputList.CodecOutputListRecycler() {
      @Override
      public void recycle(CodecOutputList object) {
      }
   };
   private static final FastThreadLocal<CodecOutputList.CodecOutputLists> CODEC_OUTPUT_LISTS_POOL = new FastThreadLocal<CodecOutputList.CodecOutputLists>() {
      protected CodecOutputList.CodecOutputLists initialValue() throws Exception {
         return new CodecOutputList.CodecOutputLists(16);
      }
   };
   private final CodecOutputList.CodecOutputListRecycler recycler;
   private int size;
   private int maxSeenSize;
   private Object[] array;
   private boolean insertSinceRecycled;

   static CodecOutputList newInstance() {
      return CODEC_OUTPUT_LISTS_POOL.get().getOrCreate();
   }

   private CodecOutputList(CodecOutputList.CodecOutputListRecycler recycler, int size) {
      this.recycler = recycler;
      this.array = new Object[size];
   }

   @Override
   public Object get(int index) {
      this.checkIndex(index);
      return this.array[index];
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean add(Object element) {
      ObjectUtil.checkNotNull(element, "element");

      try {
         this.insert(this.size, element);
      } catch (IndexOutOfBoundsException var3) {
         this.expandArray();
         this.insert(this.size, element);
      }

      this.size++;
      return true;
   }

   @Override
   public Object set(int index, Object element) {
      ObjectUtil.checkNotNull(element, "element");
      this.checkIndex(index);
      Object old = this.array[index];
      this.insert(index, element);
      return old;
   }

   @Override
   public void add(int index, Object element) {
      ObjectUtil.checkNotNull(element, "element");
      this.checkIndex(index);
      if (this.size == this.array.length) {
         this.expandArray();
      }

      if (index != this.size) {
         System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
      }

      this.insert(index, element);
      this.size++;
   }

   @Override
   public Object remove(int index) {
      this.checkIndex(index);
      Object old = this.array[index];
      int len = this.size - index - 1;
      if (len > 0) {
         System.arraycopy(this.array, index + 1, this.array, index, len);
      }

      this.array[--this.size] = null;
      return old;
   }

   @Override
   public void clear() {
      this.maxSeenSize = Math.max(this.maxSeenSize, this.size);
      this.size = 0;
   }

   boolean insertSinceRecycled() {
      return this.insertSinceRecycled;
   }

   void recycle() {
      int len = Math.max(this.maxSeenSize, this.size);

      for (int i = 0; i < len; i++) {
         this.array[i] = null;
      }

      this.size = 0;
      this.maxSeenSize = 0;
      this.insertSinceRecycled = false;
      this.recycler.recycle(this);
   }

   Object getUnsafe(int index) {
      return this.array[index];
   }

   private void checkIndex(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("expected: index < (" + this.size + "),but actual is (" + this.size + ")");
      }
   }

   private void insert(int index, Object element) {
      this.array[index] = element;
      this.insertSinceRecycled = true;
   }

   private void expandArray() {
      int newCapacity = this.array.length << 1;
      if (newCapacity < 0) {
         throw new OutOfMemoryError();
      } else {
         Object[] newArray = new Object[newCapacity];
         System.arraycopy(this.array, 0, newArray, 0, this.array.length);
         this.array = newArray;
      }
   }

   private interface CodecOutputListRecycler {
      void recycle(CodecOutputList var1);
   }

   private static final class CodecOutputLists implements CodecOutputList.CodecOutputListRecycler {
      private final CodecOutputList[] elements;
      private final int mask;
      private int currentIdx;
      private int count;

      CodecOutputLists(int numElements) {
         this.elements = new CodecOutputList[MathUtil.safeFindNextPositivePowerOfTwo(numElements)];

         for (int i = 0; i < this.elements.length; i++) {
            this.elements[i] = new CodecOutputList(this, 16);
         }

         this.count = this.elements.length;
         this.currentIdx = this.elements.length;
         this.mask = this.elements.length - 1;
      }

      public CodecOutputList getOrCreate() {
         if (this.count == 0) {
            return new CodecOutputList(CodecOutputList.NOOP_RECYCLER, 4);
         } else {
            this.count--;
            int idx = this.currentIdx - 1 & this.mask;
            CodecOutputList list = this.elements[idx];
            this.currentIdx = idx;
            return list;
         }
      }

      @Override
      public void recycle(CodecOutputList codecOutputList) {
         int idx = this.currentIdx;
         this.elements[idx] = codecOutputList;
         this.currentIdx = idx + 1 & this.mask;
         this.count++;

         assert this.count <= this.elements.length;
      }
   }
}
