package it.unimi.dsi.fastutil.shorts;

public final class ShortHeaps {
   private ShortHeaps() {
   }

   public static int downHeap(short[] heap, int size, int i, ShortComparator c) {
      assert i < size;

      short e = heap[i];
      int child;
      if (c == null) {
         while ((child = (i << 1) + 1) < size) {
            short t = heap[child];
            int right = child + 1;
            if (right < size && heap[right] < t) {
               child = right;
               t = heap[right];
            }

            if (e <= t) {
               break;
            }

            heap[i] = t;
            i = child;
         }
      } else {
         while ((child = (i << 1) + 1) < size) {
            short tx = heap[child];
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

   public static int upHeap(short[] heap, int size, int i, ShortComparator c) {
      assert i < size;

      short e = heap[i];
      if (c == null) {
         while (i != 0) {
            int parent = i - 1 >>> 1;
            short t = heap[parent];
            if (t <= e) {
               break;
            }

            heap[i] = t;
            i = parent;
         }
      } else {
         while (i != 0) {
            int parent = i - 1 >>> 1;
            short t = heap[parent];
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

   public static void makeHeap(short[] heap, int size, ShortComparator c) {
      int i = size >>> 1;

      while (i-- != 0) {
         downHeap(heap, size, i, c);
      }
   }
}
