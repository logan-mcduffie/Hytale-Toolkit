package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class LongHeapIndirectPriorityQueue extends LongHeapSemiIndirectPriorityQueue {
   protected final int[] inv;

   public LongHeapIndirectPriorityQueue(long[] refArray, int capacity, LongComparator c) {
      super(refArray, capacity, c);
      this.inv = new int[refArray.length];
      Arrays.fill(this.inv, -1);
   }

   public LongHeapIndirectPriorityQueue(long[] refArray, int capacity) {
      this(refArray, capacity, null);
   }

   public LongHeapIndirectPriorityQueue(long[] refArray, LongComparator c) {
      this(refArray, refArray.length, c);
   }

   public LongHeapIndirectPriorityQueue(long[] refArray) {
      this(refArray, refArray.length, null);
   }

   public LongHeapIndirectPriorityQueue(long[] refArray, int[] a, int size, LongComparator c) {
      this(refArray, 0, c);
      this.heap = a;
      this.size = size;

      for (int i = size; i-- != 0; this.inv[a[i]] = i) {
         if (this.inv[a[i]] != -1) {
            throw new IllegalArgumentException("Index " + a[i] + " appears twice in the heap");
         }
      }

      LongIndirectHeaps.makeHeap(refArray, a, this.inv, size, c);
   }

   public LongHeapIndirectPriorityQueue(long[] refArray, int[] a, LongComparator c) {
      this(refArray, a, a.length, c);
   }

   public LongHeapIndirectPriorityQueue(long[] refArray, int[] a, int size) {
      this(refArray, a, size, null);
   }

   public LongHeapIndirectPriorityQueue(long[] refArray, int[] a) {
      this(refArray, a, a.length);
   }

   @Override
   public void enqueue(int x) {
      if (this.inv[x] >= 0) {
         throw new IllegalArgumentException("Index " + x + " belongs to the queue");
      } else {
         if (this.size == this.heap.length) {
            this.heap = IntArrays.grow(this.heap, this.size + 1);
         }

         this.inv[this.heap[this.size] = x] = this.size++;
         LongIndirectHeaps.upHeap(this.refArray, this.heap, this.inv, this.size, this.size - 1, this.c);
      }
   }

   @Override
   public boolean contains(int index) {
      return this.inv[index] >= 0;
   }

   @Override
   public int dequeue() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         int result = this.heap[0];
         if (--this.size != 0) {
            this.inv[this.heap[0] = this.heap[this.size]] = 0;
         }

         this.inv[result] = -1;
         if (this.size != 0) {
            LongIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, 0, this.c);
         }

         return result;
      }
   }

   @Override
   public void changed() {
      LongIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, 0, this.c);
   }

   @Override
   public void changed(int index) {
      int pos = this.inv[index];
      if (pos < 0) {
         throw new IllegalArgumentException("Index " + index + " does not belong to the queue");
      } else {
         int newPos = LongIndirectHeaps.upHeap(this.refArray, this.heap, this.inv, this.size, pos, this.c);
         LongIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, newPos, this.c);
      }
   }

   @Override
   public void allChanged() {
      LongIndirectHeaps.makeHeap(this.refArray, this.heap, this.inv, this.size, this.c);
   }

   @Override
   public boolean remove(int index) {
      int result = this.inv[index];
      if (result < 0) {
         return false;
      } else {
         this.inv[index] = -1;
         if (result < --this.size) {
            this.inv[this.heap[result] = this.heap[this.size]] = result;
            int newPos = LongIndirectHeaps.upHeap(this.refArray, this.heap, this.inv, this.size, result, this.c);
            LongIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, newPos, this.c);
         }

         return true;
      }
   }

   @Override
   public void clear() {
      this.size = 0;
      Arrays.fill(this.inv, -1);
   }
}
