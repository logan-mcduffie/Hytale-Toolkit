package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.ints.IntArrays;

public final class FloatSemiIndirectHeaps {
   private FloatSemiIndirectHeaps() {
   }

   public static int downHeap(float[] refArray, int[] heap, int size, int i, FloatComparator c) {
      assert i < size;

      int e = heap[i];
      float E = refArray[e];
      int child;
      if (c == null) {
         while ((child = (i << 1) + 1) < size) {
            int t = heap[child];
            int right = child + 1;
            if (right < size && Float.compare(refArray[heap[right]], refArray[t]) < 0) {
               child = right;
               t = heap[right];
            }

            if (Float.compare(E, refArray[t]) <= 0) {
               break;
            }

            heap[i] = t;
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
            i = child;
         }
      }

      heap[i] = e;
      return i;
   }

   public static int upHeap(float[] refArray, int[] heap, int size, int i, FloatComparator c) {
      assert i < size;

      int e = heap[i];
      float E = refArray[e];
      if (c == null) {
         while (i != 0) {
            int parent = i - 1 >>> 1;
            int t = heap[parent];
            if (Float.compare(refArray[t], E) <= 0) {
               break;
            }

            heap[i] = t;
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
            i = parent;
         }
      }

      heap[i] = e;
      return i;
   }

   public static void makeHeap(float[] refArray, int offset, int length, int[] heap, FloatComparator c) {
      FloatArrays.ensureOffsetLength(refArray, offset, length);
      if (heap.length < length) {
         throw new IllegalArgumentException("The heap length (" + heap.length + ") is smaller than the number of elements (" + length + ")");
      } else {
         int i = length;

         while (i-- != 0) {
            heap[i] = offset + i;
         }

         i = length >>> 1;

         while (i-- != 0) {
            downHeap(refArray, heap, length, i, c);
         }
      }
   }

   public static int[] makeHeap(float[] refArray, int offset, int length, FloatComparator c) {
      int[] heap = length <= 0 ? IntArrays.EMPTY_ARRAY : new int[length];
      makeHeap(refArray, offset, length, heap, c);
      return heap;
   }

   public static void makeHeap(float[] refArray, int[] heap, int size, FloatComparator c) {
      int i = size >>> 1;

      while (i-- != 0) {
         downHeap(refArray, heap, size, i, c);
      }
   }

   public static int front(float[] refArray, int[] heap, int size, int[] a) {
      float top = refArray[heap[0]];
      int j = 0;
      int l = 0;
      int r = 1;
      int f = 0;

      for (int i = 0; i < r; i++) {
         if (i == f) {
            if (l >= r) {
               break;
            }

            f = (f << 1) + 1;
            i = l;
            l = -1;
         }

         if (Float.compare(top, refArray[heap[i]]) == 0) {
            a[j++] = heap[i];
            if (l == -1) {
               l = i * 2 + 1;
            }

            r = Math.min(size, i * 2 + 3);
         }
      }

      return j;
   }

   public static int front(float[] refArray, int[] heap, int size, int[] a, FloatComparator c) {
      float top = refArray[heap[0]];
      int j = 0;
      int l = 0;
      int r = 1;
      int f = 0;

      for (int i = 0; i < r; i++) {
         if (i == f) {
            if (l >= r) {
               break;
            }

            f = (f << 1) + 1;
            i = l;
            l = -1;
         }

         if (c.compare(top, refArray[heap[i]]) == 0) {
            a[j++] = heap[i];
            if (l == -1) {
               l = i * 2 + 1;
            }

            r = Math.min(size, i * 2 + 3);
         }
      }

      return j;
   }
}
