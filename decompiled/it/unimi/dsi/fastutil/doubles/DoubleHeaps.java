package it.unimi.dsi.fastutil.doubles;

public final class DoubleHeaps {
   private DoubleHeaps() {
   }

   public static int downHeap(double[] heap, int size, int i, DoubleComparator c) {
      assert i < size;

      double e = heap[i];
      int child;
      if (c == null) {
         while ((child = (i << 1) + 1) < size) {
            double t = heap[child];
            int right = child + 1;
            if (right < size && Double.compare(heap[right], t) < 0) {
               child = right;
               t = heap[right];
            }

            if (Double.compare(e, t) <= 0) {
               break;
            }

            heap[i] = t;
            i = child;
         }
      } else {
         while ((child = (i << 1) + 1) < size) {
            double tx = heap[child];
            int rightx = child + 1;
            if (rightx < size && c.compare(heap[rightx], tx) < 0) {
               child = rightx;
               tx = heap[rightx];
            }

            if (c.compare(e, tx) <= 0) {
               break;
            }

            heap[i] = tx;
            i = child;
         }
      }

      heap[i] = e;
      return i;
   }

   public static int upHeap(double[] heap, int size, int i, DoubleComparator c) {
      assert i < size;

      double e = heap[i];
      if (c == null) {
         while (i != 0) {
            int parent = i - 1 >>> 1;
            double t = heap[parent];
            if (Double.compare(t, e) <= 0) {
               break;
            }

            heap[i] = t;
            i = parent;
         }
      } else {
         while (i != 0) {
            int parent = i - 1 >>> 1;
            double t = heap[parent];
            if (c.compare(t, e) <= 0) {
               break;
            }

            heap[i] = t;
            i = parent;
         }
      }

      heap[i] = e;
      return i;
   }

   public static void makeHeap(double[] heap, int size, DoubleComparator c) {
      int i = size >>> 1;

      while (i-- != 0) {
         downHeap(heap, size, i, c);
      }
   }
}
