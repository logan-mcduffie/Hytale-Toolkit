package it.unimi.dsi.fastutil.ints;

import java.util.Arrays;

public final class IntIndirectHeaps {
   private IntIndirectHeaps() {
   }

   public static int downHeap(int[] refArray, int[] heap, int[] inv, int size, int i, IntComparator c) {
      assert i < size;

      int e = heap[i];
      int E = refArray[e];
      int child;
      if (c == null) {
         while ((child = (i << 1) + 1) < size) {
            int t = heap[child];
            int right = child + 1;
            if (right < size && refArray[heap[right]] < refArray[t]) {
               child = right;
               t = heap[right];
            }

            if (E <= refArray[t]) {
               break;
            }

            heap[i] = t;
            inv[heap[i]] = i;
            i = child;
         }
      } else {
         while ((child = (i << 1) + 1) < size) {
            int tx = heap[child];
            int rightx = child + 1;
            if (rightx < size && c.compare(refArray[heap[rightx]], refArray[tx]) < 0) {
               child = rightx;
               tx = heap[rightx];
            }

            if (c.compare(E, refArray[tx]) <= 0) {
               break;
            }

            heap[i] = tx;
            inv[heap[i]] = i;
            i = child;
         }
      }

      heap[i] = e;
      inv[e] = i;
      return i;
   }

   public static int upHeap(int[] refArray, int[] heap, int[] inv, int size, int i, IntComparator c) {
      assert i < size;

      int e = heap[i];
      int E = refArray[e];
      if (c == null) {
         while (i != 0) {
            int parent = i - 1 >>> 1;
            int t = heap[parent];
            if (refArray[t] <= E) {
               break;
            }

            heap[i] = t;
            inv[heap[i]] = i;
            i = parent;
         }
      } else {
         while (i != 0) {
            int parent = i - 1 >>> 1;
            int t = heap[parent];
            if (c.compare(refArray[t], E) <= 0) {
               break;
            }

            heap[i] = t;
            inv[heap[i]] = i;
            i = parent;
         }
      }

      heap[i] = e;
      inv[e] = i;
      return i;
   }

   public static void makeHeap(int[] refArray, int offset, int length, int[] heap, int[] inv, IntComparator c) {
      IntArrays.ensureOffsetLength(refArray, offset, length);
      if (heap.length < length) {
         throw new IllegalArgumentException("The heap length (" + heap.length + ") is smaller than the number of elements (" + length + ")");
      } else if (inv.length < refArray.length) {
         throw new IllegalArgumentException(
            "The inversion array length (" + heap.length + ") is smaller than the length of the reference array (" + refArray.length + ")"
         );
      } else {
         Arrays.fill(inv, 0, refArray.length, -1);
         int i = length;

         while (i-- != 0) {
            inv[heap[i] = offset + i] = i;
         }

         i = length >>> 1;

         while (i-- != 0) {
            downHeap(refArray, heap, inv, length, i, c);
         }
      }
   }

   public static void makeHeap(int[] refArray, int[] heap, int[] inv, int size, IntComparator c) {
      int i = size >>> 1;

      while (i-- != 0) {
         downHeap(refArray, heap, inv, size, i, c);
      }
   }
}
