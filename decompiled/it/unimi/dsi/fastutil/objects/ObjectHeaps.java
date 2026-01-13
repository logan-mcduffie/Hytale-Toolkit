package it.unimi.dsi.fastutil.objects;

import java.util.Comparator;

public final class ObjectHeaps {
   private ObjectHeaps() {
   }

   public static <K> int downHeap(K[] heap, int size, int i, Comparator<? super K> c) {
      assert i < size;

      K e = heap[i];
      int child;
      if (c == null) {
         while ((child = (i << 1) + 1) < size) {
            K t = heap[child];
            int right = child + 1;
            if (right < size && ((Comparable)heap[right]).compareTo(t) < 0) {
               child = right;
               t = heap[right];
            }

            if (((Comparable)e).compareTo(t) <= 0) {
               break;
            }

            heap[i] = t;
            i = child;
         }
      } else {
         while ((child = (i << 1) + 1) < size) {
            K tx = heap[child];
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

   public static <K> int upHeap(K[] heap, int size, int i, Comparator<K> c) {
      assert i < size;

      K e = heap[i];
      if (c == null) {
         while (i != 0) {
            int parent = i - 1 >>> 1;
            K t = heap[parent];
            if (((Comparable)t).compareTo(e) <= 0) {
               break;
            }

            heap[i] = t;
            i = parent;
         }
      } else {
         while (i != 0) {
            int parent = i - 1 >>> 1;
            K t = heap[parent];
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

   public static <K> void makeHeap(K[] heap, int size, Comparator<K> c) {
      int i = size >>> 1;

      while (i-- != 0) {
         downHeap(heap, size, i, c);
      }
   }
}
