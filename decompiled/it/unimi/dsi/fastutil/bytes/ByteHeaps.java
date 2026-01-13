package it.unimi.dsi.fastutil.bytes;

public final class ByteHeaps {
   private ByteHeaps() {
   }

   public static int downHeap(byte[] heap, int size, int i, ByteComparator c) {
      assert i < size;

      byte e = heap[i];
      int child;
      if (c == null) {
         while ((child = (i << 1) + 1) < size) {
            byte t = heap[child];
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
            byte tx = heap[child];
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

   public static int upHeap(byte[] heap, int size, int i, ByteComparator c) {
      assert i < size;

      byte e = heap[i];
      if (c == null) {
         while (i != 0) {
            int parent = i - 1 >>> 1;
            byte t = heap[parent];
            if (t <= e) {
               break;
            }

            heap[i] = t;
            i = parent;
         }
      } else {
         while (i != 0) {
            int parent = i - 1 >>> 1;
            byte t = heap[parent];
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

   public static void makeHeap(byte[] heap, int size, ByteComparator c) {
      int i = size >>> 1;

      while (i-- != 0) {
         downHeap(heap, size, i, c);
      }
   }
}
