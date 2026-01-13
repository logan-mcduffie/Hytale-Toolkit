package it.unimi.dsi.fastutil.floats;

public final class FloatHeaps {
   private FloatHeaps() {
   }

   public static int downHeap(float[] heap, int size, int i, FloatComparator c) {
      assert i < size;

      float e = heap[i];
      int child;
      if (c == null) {
         while ((child = (i << 1) + 1) < size) {
            float t = heap[child];
            int right = child + 1;
            if (right < size && Float.compare(heap[right], t) < 0) {
               child = right;
               t = heap[right];
            }

            if (Float.compare(e, t) <= 0) {
               break;
            }

            heap[i] = t;
            i = child;
         }
      } else {
         while ((child = (i << 1) + 1) < size) {
            float tx = heap[child];
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

   public static int upHeap(float[] heap, int size, int i, FloatComparator c) {
      assert i < size;

      float e = heap[i];
      if (c == null) {
         while (i != 0) {
            int parent = i - 1 >>> 1;
            float t = heap[parent];
            if (Float.compare(t, e) <= 0) {
               break;
            }

            heap[i] = t;
            i = parent;
         }
      } else {
         while (i != 0) {
            int parent = i - 1 >>> 1;
            float t = heap[parent];
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

   public static void makeHeap(float[] heap, int size, FloatComparator c) {
      int i = size >>> 1;

      while (i-- != 0) {
         downHeap(heap, size, i, c);
      }
   }
}
