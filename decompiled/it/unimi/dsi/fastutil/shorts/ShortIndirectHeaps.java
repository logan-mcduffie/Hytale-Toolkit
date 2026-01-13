package it.unimi.dsi.fastutil.shorts;

import java.util.Arrays;

public final class ShortIndirectHeaps {
   private ShortIndirectHeaps() {
   }

   public static int downHeap(short[] refArray, int[] heap, int[] inv, int size, int i, ShortComparator c) {
      assert i < size;

      int e = heap[i];
      short E = refArray[e];
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

   public static int upHeap(short[] refArray, int[] heap, int[] inv, int size, int i, ShortComparator c) {
      assert i < size;

      int e = heap[i];
      short E = refArray[e];
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

   public static void makeHeap(short[] refArray, int offset, int length, int[] heap, int[] inv, ShortComparator c) {
      ShortArrays.ensureOffsetLength(refArray, offset, length);
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

   public static void makeHeap(short[] refArray, int[] heap, int[] inv, int size, ShortComparator c) {
      int i = size >>> 1;

      while (i-- != 0) {
         downHeap(refArray, heap, inv, size, i, c);
      }
   }
}
