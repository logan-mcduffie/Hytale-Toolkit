package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public final class ShortArrays {
   public static final short[] EMPTY_ARRAY = new short[0];
   public static final short[] DEFAULT_EMPTY_ARRAY = new short[0];
   private static final int QUICKSORT_NO_REC = 16;
   private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
   private static final int QUICKSORT_MEDIAN_OF_9 = 128;
   private static final int MERGESORT_NO_REC = 16;
   private static final int DIGIT_BITS = 8;
   private static final int DIGIT_MASK = 255;
   private static final int DIGITS_PER_ELEMENT = 2;
   private static final int RADIXSORT_NO_REC = 1024;
   private static final int RADIXSORT_NO_REC_SMALL = 64;
   private static final int PARALLEL_RADIXSORT_NO_FORK = 1024;
   static final int RADIX_SORT_MIN_THRESHOLD = 1000;
   protected static final ShortArrays.Segment POISON_PILL = new ShortArrays.Segment(-1, -1, -1);
   public static final Hash.Strategy<short[]> HASH_STRATEGY = new ShortArrays.ArrayHashStrategy();

   private ShortArrays() {
   }

   public static short[] forceCapacity(short[] array, int length, int preserve) {
      short[] t = new short[length];
      System.arraycopy(array, 0, t, 0, preserve);
      return t;
   }

   public static short[] ensureCapacity(short[] array, int length) {
      return ensureCapacity(array, length, array.length);
   }

   public static short[] ensureCapacity(short[] array, int length, int preserve) {
      return length > array.length ? forceCapacity(array, length, preserve) : array;
   }

   public static short[] grow(short[] array, int length) {
      return grow(array, length, array.length);
   }

   public static short[] grow(short[] array, int length, int preserve) {
      if (length > array.length) {
         int newLength = (int)Math.max(Math.min((long)array.length + (array.length >> 1), 2147483639L), (long)length);
         short[] t = new short[newLength];
         System.arraycopy(array, 0, t, 0, preserve);
         return t;
      } else {
         return array;
      }
   }

   public static short[] trim(short[] array, int length) {
      if (length >= array.length) {
         return array;
      } else {
         short[] t = length == 0 ? EMPTY_ARRAY : new short[length];
         System.arraycopy(array, 0, t, 0, length);
         return t;
      }
   }

   public static short[] setLength(short[] array, int length) {
      if (length == array.length) {
         return array;
      } else {
         return length < array.length ? trim(array, length) : ensureCapacity(array, length);
      }
   }

   public static short[] copy(short[] array, int offset, int length) {
      ensureOffsetLength(array, offset, length);
      short[] a = length == 0 ? EMPTY_ARRAY : new short[length];
      System.arraycopy(array, offset, a, 0, length);
      return a;
   }

   public static short[] copy(short[] array) {
      return (short[])array.clone();
   }

   @Deprecated
   public static void fill(short[] array, short value) {
      int i = array.length;

      while (i-- != 0) {
         array[i] = value;
      }
   }

   @Deprecated
   public static void fill(short[] array, int from, int to, short value) {
      ensureFromTo(array, from, to);
      if (from == 0) {
         while (to-- != 0) {
            array[to] = value;
         }
      } else {
         for (int i = from; i < to; i++) {
            array[i] = value;
         }
      }
   }

   @Deprecated
   public static boolean equals(short[] a1, short[] a2) {
      int i = a1.length;
      if (i != a2.length) {
         return false;
      } else {
         while (i-- != 0) {
            if (a1[i] != a2[i]) {
               return false;
            }
         }

         return true;
      }
   }

   public static void ensureFromTo(short[] a, int from, int to) {
      Arrays.ensureFromTo(a.length, from, to);
   }

   public static void ensureOffsetLength(short[] a, int offset, int length) {
      Arrays.ensureOffsetLength(a.length, offset, length);
   }

   public static void ensureSameLength(short[] a, short[] b) {
      if (a.length != b.length) {
         throw new IllegalArgumentException("Array size mismatch: " + a.length + " != " + b.length);
      }
   }

   private static ForkJoinPool getPool() {
      ForkJoinPool current = ForkJoinTask.getPool();
      return current == null ? ForkJoinPool.commonPool() : current;
   }

   public static void swap(short[] x, int a, int b) {
      short t = x[a];
      x[a] = x[b];
      x[b] = t;
   }

   public static void swap(short[] x, int a, int b, int n) {
      int i = 0;

      while (i < n) {
         swap(x, a, b);
         i++;
         a++;
         b++;
      }
   }

   private static int med3(short[] x, int a, int b, int c, ShortComparator comp) {
      int ab = comp.compare(x[a], x[b]);
      int ac = comp.compare(x[a], x[c]);
      int bc = comp.compare(x[b], x[c]);
      return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
   }

   private static void selectionSort(short[] a, int from, int to, ShortComparator comp) {
      for (int i = from; i < to - 1; i++) {
         int m = i;

         for (int j = i + 1; j < to; j++) {
            if (comp.compare(a[j], a[m]) < 0) {
               m = j;
            }
         }

         if (m != i) {
            short u = a[i];
            a[i] = a[m];
            a[m] = u;
         }
      }
   }

   private static void insertionSort(short[] a, int from, int to, ShortComparator comp) {
      int i = from;

      while (++i < to) {
         short t = a[i];
         int j = i;

         for (short u = a[i - 1]; comp.compare(t, u) < 0; u = a[j - 1]) {
            a[j] = u;
            if (from == j - 1) {
               j--;
               break;
            }

            j--;
         }

         a[j] = t;
      }
   }

   public static void quickSort(short[] x, int from, int to, ShortComparator comp) {
      int len = to - from;
      if (len < 16) {
         selectionSort(x, from, to, comp);
      } else {
         int m = from + len / 2;
         int l = from;
         int n = to - 1;
         if (len > 128) {
            int s = len / 8;
            l = med3(x, from, from + s, from + 2 * s, comp);
            m = med3(x, m - s, m, m + s, comp);
            n = med3(x, n - 2 * s, n - s, n, comp);
         }

         m = med3(x, l, m, n, comp);
         short v = x[m];
         int a = from;
         int b = from;
         int c = to - 1;
         int d = c;

         while (true) {
            int comparison;
            while (b > c || (comparison = comp.compare(x[b], v)) > 0) {
               for (; c >= b && (comparison = comp.compare(x[c], v)) >= 0; c--) {
                  if (comparison == 0) {
                     swap(x, c, d--);
                  }
               }

               if (b > c) {
                  comparison = Math.min(a - from, b - a);
                  swap(x, from, b - comparison, comparison);
                  comparison = Math.min(d - c, to - d - 1);
                  swap(x, b, to - comparison, comparison);
                  if ((comparison = b - a) > 1) {
                     quickSort(x, from, from + comparison, comp);
                  }

                  if ((comparison = d - c) > 1) {
                     quickSort(x, to - comparison, to, comp);
                  }

                  return;
               }

               swap(x, b++, c--);
            }

            if (comparison == 0) {
               swap(x, a++, b);
            }

            b++;
         }
      }
   }

   public static void quickSort(short[] x, ShortComparator comp) {
      quickSort(x, 0, x.length, comp);
   }

   public static void parallelQuickSort(short[] x, int from, int to, ShortComparator comp) {
      ForkJoinPool pool = getPool();
      if (to - from >= 8192 && pool.getParallelism() != 1) {
         pool.invoke(new ShortArrays.ForkJoinQuickSortComp(x, from, to, comp));
      } else {
         quickSort(x, from, to, comp);
      }
   }

   public static void parallelQuickSort(short[] x, ShortComparator comp) {
      parallelQuickSort(x, 0, x.length, comp);
   }

   private static int med3(short[] x, int a, int b, int c) {
      int ab = Short.compare(x[a], x[b]);
      int ac = Short.compare(x[a], x[c]);
      int bc = Short.compare(x[b], x[c]);
      return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
   }

   private static void selectionSort(short[] a, int from, int to) {
      for (int i = from; i < to - 1; i++) {
         int m = i;

         for (int j = i + 1; j < to; j++) {
            if (a[j] < a[m]) {
               m = j;
            }
         }

         if (m != i) {
            short u = a[i];
            a[i] = a[m];
            a[m] = u;
         }
      }
   }

   private static void insertionSort(short[] a, int from, int to) {
      int i = from;

      while (++i < to) {
         short t = a[i];
         int j = i;

         for (short u = a[i - 1]; t < u; u = a[j - 1]) {
            a[j] = u;
            if (from == j - 1) {
               j--;
               break;
            }

            j--;
         }

         a[j] = t;
      }
   }

   public static void quickSort(short[] x, int from, int to) {
      int len = to - from;
      if (len < 16) {
         selectionSort(x, from, to);
      } else {
         int m = from + len / 2;
         int l = from;
         int n = to - 1;
         if (len > 128) {
            int s = len / 8;
            l = med3(x, from, from + s, from + 2 * s);
            m = med3(x, m - s, m, m + s);
            n = med3(x, n - 2 * s, n - s, n);
         }

         m = med3(x, l, m, n);
         short v = x[m];
         int a = from;
         int b = from;
         int c = to - 1;
         int d = c;

         while (true) {
            int comparison;
            while (b > c || (comparison = Short.compare(x[b], v)) > 0) {
               for (; c >= b && (comparison = Short.compare(x[c], v)) >= 0; c--) {
                  if (comparison == 0) {
                     swap(x, c, d--);
                  }
               }

               if (b > c) {
                  comparison = Math.min(a - from, b - a);
                  swap(x, from, b - comparison, comparison);
                  comparison = Math.min(d - c, to - d - 1);
                  swap(x, b, to - comparison, comparison);
                  if ((comparison = b - a) > 1) {
                     quickSort(x, from, from + comparison);
                  }

                  if ((comparison = d - c) > 1) {
                     quickSort(x, to - comparison, to);
                  }

                  return;
               }

               swap(x, b++, c--);
            }

            if (comparison == 0) {
               swap(x, a++, b);
            }

            b++;
         }
      }
   }

   public static void quickSort(short[] x) {
      quickSort(x, 0, x.length);
   }

   public static void parallelQuickSort(short[] x, int from, int to) {
      ForkJoinPool pool = getPool();
      if (to - from >= 8192 && pool.getParallelism() != 1) {
         pool.invoke(new ShortArrays.ForkJoinQuickSort(x, from, to));
      } else {
         quickSort(x, from, to);
      }
   }

   public static void parallelQuickSort(short[] x) {
      parallelQuickSort(x, 0, x.length);
   }

   private static int med3Indirect(int[] perm, short[] x, int a, int b, int c) {
      short aa = x[perm[a]];
      short bb = x[perm[b]];
      short cc = x[perm[c]];
      int ab = Short.compare(aa, bb);
      int ac = Short.compare(aa, cc);
      int bc = Short.compare(bb, cc);
      return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
   }

   private static void insertionSortIndirect(int[] perm, short[] a, int from, int to) {
      int i = from;

      while (++i < to) {
         int t = perm[i];
         int j = i;

         for (int u = perm[i - 1]; a[t] < a[u]; u = perm[j - 1]) {
            perm[j] = u;
            if (from == j - 1) {
               j--;
               break;
            }

            j--;
         }

         perm[j] = t;
      }
   }

   public static void quickSortIndirect(int[] perm, short[] x, int from, int to) {
      int len = to - from;
      if (len < 16) {
         insertionSortIndirect(perm, x, from, to);
      } else {
         int m = from + len / 2;
         int l = from;
         int n = to - 1;
         if (len > 128) {
            int s = len / 8;
            l = med3Indirect(perm, x, from, from + s, from + 2 * s);
            m = med3Indirect(perm, x, m - s, m, m + s);
            n = med3Indirect(perm, x, n - 2 * s, n - s, n);
         }

         m = med3Indirect(perm, x, l, m, n);
         short v = x[perm[m]];
         int a = from;
         int b = from;
         int c = to - 1;
         int d = c;

         while (true) {
            int comparison;
            while (b > c || (comparison = Short.compare(x[perm[b]], v)) > 0) {
               for (; c >= b && (comparison = Short.compare(x[perm[c]], v)) >= 0; c--) {
                  if (comparison == 0) {
                     IntArrays.swap(perm, c, d--);
                  }
               }

               if (b > c) {
                  comparison = Math.min(a - from, b - a);
                  IntArrays.swap(perm, from, b - comparison, comparison);
                  comparison = Math.min(d - c, to - d - 1);
                  IntArrays.swap(perm, b, to - comparison, comparison);
                  if ((comparison = b - a) > 1) {
                     quickSortIndirect(perm, x, from, from + comparison);
                  }

                  if ((comparison = d - c) > 1) {
                     quickSortIndirect(perm, x, to - comparison, to);
                  }

                  return;
               }

               IntArrays.swap(perm, b++, c--);
            }

            if (comparison == 0) {
               IntArrays.swap(perm, a++, b);
            }

            b++;
         }
      }
   }

   public static void quickSortIndirect(int[] perm, short[] x) {
      quickSortIndirect(perm, x, 0, x.length);
   }

   public static void parallelQuickSortIndirect(int[] perm, short[] x, int from, int to) {
      ForkJoinPool pool = getPool();
      if (to - from >= 8192 && pool.getParallelism() != 1) {
         pool.invoke(new ShortArrays.ForkJoinQuickSortIndirect(perm, x, from, to));
      } else {
         quickSortIndirect(perm, x, from, to);
      }
   }

   public static void parallelQuickSortIndirect(int[] perm, short[] x) {
      parallelQuickSortIndirect(perm, x, 0, x.length);
   }

   public static void stabilize(int[] perm, short[] x, int from, int to) {
      int curr = from;

      for (int i = from + 1; i < to; i++) {
         if (x[perm[i]] != x[perm[curr]]) {
            if (i - curr > 1) {
               IntArrays.parallelQuickSort(perm, curr, i);
            }

            curr = i;
         }
      }

      if (to - curr > 1) {
         IntArrays.parallelQuickSort(perm, curr, to);
      }
   }

   public static void stabilize(int[] perm, short[] x) {
      stabilize(perm, x, 0, perm.length);
   }

   private static int med3(short[] x, short[] y, int a, int b, int c) {
      int t;
      int ab = (t = Short.compare(x[a], x[b])) == 0 ? Short.compare(y[a], y[b]) : t;
      int ac = (t = Short.compare(x[a], x[c])) == 0 ? Short.compare(y[a], y[c]) : t;
      int bc = (t = Short.compare(x[b], x[c])) == 0 ? Short.compare(y[b], y[c]) : t;
      return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
   }

   private static void swap(short[] x, short[] y, int a, int b) {
      short t = x[a];
      short u = y[a];
      x[a] = x[b];
      y[a] = y[b];
      x[b] = t;
      y[b] = u;
   }

   private static void swap(short[] x, short[] y, int a, int b, int n) {
      int i = 0;

      while (i < n) {
         swap(x, y, a, b);
         i++;
         a++;
         b++;
      }
   }

   private static void selectionSort(short[] a, short[] b, int from, int to) {
      for (int i = from; i < to - 1; i++) {
         int m = i;

         for (int j = i + 1; j < to; j++) {
            int u;
            if ((u = Short.compare(a[j], a[m])) < 0 || u == false && b[j] < b[m]) {
               m = j;
            }
         }

         if (m != i) {
            short t = a[i];
            a[i] = a[m];
            a[m] = t;
            t = b[i];
            b[i] = b[m];
            b[m] = t;
         }
      }
   }

   public static void quickSort(short[] x, short[] y, int from, int to) {
      int len = to - from;
      if (len < 16) {
         selectionSort(x, y, from, to);
      } else {
         int m = from + len / 2;
         int l = from;
         int n = to - 1;
         if (len > 128) {
            int s = len / 8;
            l = med3(x, y, from, from + s, from + 2 * s);
            m = med3(x, y, m - s, m, m + s);
            n = med3(x, y, n - 2 * s, n - s, n);
         }

         m = med3(x, y, l, m, n);
         short v = x[m];
         short w = y[m];
         int a = from;
         int b = from;
         int c = to - 1;
         int d = c;

         while (true) {
            int comparison;
            int t;
            while (b > c || (comparison = (t = Short.compare(x[b], v)) == 0 ? Short.compare(y[b], w) : t) > 0) {
               for (; c >= b && (comparison = (t = Short.compare(x[c], v)) == 0 ? Short.compare(y[c], w) : t) >= 0; c--) {
                  if (comparison == 0) {
                     swap(x, y, c, d--);
                  }
               }

               if (b > c) {
                  comparison = Math.min(a - from, b - a);
                  swap(x, y, from, b - comparison, comparison);
                  comparison = Math.min(d - c, to - d - 1);
                  swap(x, y, b, to - comparison, comparison);
                  if ((comparison = b - a) > 1) {
                     quickSort(x, y, from, from + comparison);
                  }

                  if ((comparison = d - c) > 1) {
                     quickSort(x, y, to - comparison, to);
                  }

                  return;
               }

               swap(x, y, b++, c--);
            }

            if (comparison == 0) {
               swap(x, y, a++, b);
            }

            b++;
         }
      }
   }

   public static void quickSort(short[] x, short[] y) {
      ensureSameLength(x, y);
      quickSort(x, y, 0, x.length);
   }

   public static void parallelQuickSort(short[] x, short[] y, int from, int to) {
      ForkJoinPool pool = getPool();
      if (to - from >= 8192 && pool.getParallelism() != 1) {
         pool.invoke(new ShortArrays.ForkJoinQuickSort2(x, y, from, to));
      } else {
         quickSort(x, y, from, to);
      }
   }

   public static void parallelQuickSort(short[] x, short[] y) {
      ensureSameLength(x, y);
      parallelQuickSort(x, y, 0, x.length);
   }

   public static void unstableSort(short[] a, int from, int to) {
      if (to - from >= 1000) {
         radixSort(a, from, to);
      } else {
         quickSort(a, from, to);
      }
   }

   public static void unstableSort(short[] a) {
      unstableSort(a, 0, a.length);
   }

   public static void unstableSort(short[] a, int from, int to, ShortComparator comp) {
      quickSort(a, from, to, comp);
   }

   public static void unstableSort(short[] a, ShortComparator comp) {
      unstableSort(a, 0, a.length, comp);
   }

   public static void mergeSort(short[] a, int from, int to, short[] supp) {
      int len = to - from;
      if (len < 16) {
         insertionSort(a, from, to);
      } else {
         if (supp == null) {
            supp = java.util.Arrays.copyOf(a, to);
         }

         int mid = from + to >>> 1;
         mergeSort(supp, from, mid, a);
         mergeSort(supp, mid, to, a);
         if (supp[mid - 1] <= supp[mid]) {
            System.arraycopy(supp, from, a, from, len);
         } else {
            int i = from;
            int p = from;

            for (int q = mid; i < to; i++) {
               if (q < to && (p >= mid || supp[p] > supp[q])) {
                  a[i] = supp[q++];
               } else {
                  a[i] = supp[p++];
               }
            }
         }
      }
   }

   public static void mergeSort(short[] a, int from, int to) {
      mergeSort(a, from, to, (short[])null);
   }

   public static void mergeSort(short[] a) {
      mergeSort(a, 0, a.length);
   }

   public static void mergeSort(short[] a, int from, int to, ShortComparator comp, short[] supp) {
      int len = to - from;
      if (len < 16) {
         insertionSort(a, from, to, comp);
      } else {
         if (supp == null) {
            supp = java.util.Arrays.copyOf(a, to);
         }

         int mid = from + to >>> 1;
         mergeSort(supp, from, mid, comp, a);
         mergeSort(supp, mid, to, comp, a);
         if (comp.compare(supp[mid - 1], supp[mid]) <= 0) {
            System.arraycopy(supp, from, a, from, len);
         } else {
            int i = from;
            int p = from;

            for (int q = mid; i < to; i++) {
               if (q < to && (p >= mid || comp.compare(supp[p], supp[q]) > 0)) {
                  a[i] = supp[q++];
               } else {
                  a[i] = supp[p++];
               }
            }
         }
      }
   }

   public static void mergeSort(short[] a, int from, int to, ShortComparator comp) {
      mergeSort(a, from, to, comp, (short[])null);
   }

   public static void mergeSort(short[] a, ShortComparator comp) {
      mergeSort(a, 0, a.length, comp);
   }

   public static void stableSort(short[] a, int from, int to) {
      unstableSort(a, from, to);
   }

   public static void stableSort(short[] a) {
      stableSort(a, 0, a.length);
   }

   public static void stableSort(short[] a, int from, int to, ShortComparator comp) {
      mergeSort(a, from, to, comp);
   }

   public static void stableSort(short[] a, ShortComparator comp) {
      stableSort(a, 0, a.length, comp);
   }

   public static int binarySearch(short[] a, int from, int to, short key) {
      to--;

      while (from <= to) {
         int mid = from + to >>> 1;
         short midVal = a[mid];
         if (midVal < key) {
            from = mid + 1;
         } else {
            if (midVal <= key) {
               return mid;
            }

            to = mid - 1;
         }
      }

      return -(from + 1);
   }

   public static int binarySearch(short[] a, short key) {
      return binarySearch(a, 0, a.length, key);
   }

   public static int binarySearch(short[] a, int from, int to, short key, ShortComparator c) {
      to--;

      while (from <= to) {
         int mid = from + to >>> 1;
         short midVal = a[mid];
         int cmp = c.compare(midVal, key);
         if (cmp < 0) {
            from = mid + 1;
         } else {
            if (cmp <= 0) {
               return mid;
            }

            to = mid - 1;
         }
      }

      return -(from + 1);
   }

   public static int binarySearch(short[] a, short key, ShortComparator c) {
      return binarySearch(a, 0, a.length, key, c);
   }

   public static void radixSort(short[] a) {
      radixSort(a, 0, a.length);
   }

   public static void radixSort(short[] a, int from, int to) {
      if (to - from < 1024) {
         quickSort(a, from, to);
      } else {
         int maxLevel = 1;
         int stackSize = 256;
         int stackPos = 0;
         int[] offsetStack = new int[256];
         int[] lengthStack = new int[256];
         int[] levelStack = new int[256];
         offsetStack[stackPos] = from;
         lengthStack[stackPos] = to - from;
         levelStack[stackPos++] = 0;
         int[] count = new int[256];
         int[] pos = new int[256];

         while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 2 == 0 ? 128 : 0;
            int shift = (1 - level % 2) * 8;
            int i = first + length;

            while (i-- != first) {
               count[a[i] >>> shift & 0xFF ^ signMask]++;
            }

            i = -1;
            int ix = 0;

            for (int p = first; ix < 256; ix++) {
               if (count[ix] != 0) {
                  i = ix;
               }

               pos[ix] = p += count[ix];
            }

            ix = first + length - count[i];
            int ixx = first;
            int c = -1;

            while (ixx <= ix) {
               short t = a[ixx];
               c = t >>> shift & 0xFF ^ signMask;
               if (ixx < ix) {
                  int d;
                  while ((d = --pos[c]) > ixx) {
                     short z = t;
                     t = a[d];
                     a[d] = z;
                     c = t >>> shift & 0xFF ^ signMask;
                  }

                  a[ixx] = t;
               }

               if (level < 1 && count[c] > 1) {
                  if (count[c] < 1024) {
                     quickSort(a, ixx, ixx + count[c]);
                  } else {
                     offsetStack[stackPos] = ixx;
                     lengthStack[stackPos] = count[c];
                     levelStack[stackPos++] = level + 1;
                  }
               }

               ixx += count[c];
               count[c] = 0;
            }
         }
      }
   }

   public static void parallelRadixSort(short[] a, int from, int to) {
      ForkJoinPool pool = getPool();
      if (to - from >= 1024 && pool.getParallelism() != 1) {
         int maxLevel = 1;
         LinkedBlockingQueue<ShortArrays.Segment> queue = new LinkedBlockingQueue<>();
         queue.add(new ShortArrays.Segment(from, to - from, 0));
         AtomicInteger queueSize = new AtomicInteger(1);
         int numberOfThreads = pool.getParallelism();
         ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<>(pool);
         int j = numberOfThreads;

         while (j-- != 0) {
            executorCompletionService.submit(() -> {
               int[] count = new int[256];
               int[] pos = new int[256];

               while (true) {
                  if (queueSize.get() == 0) {
                     int ixxxx = numberOfThreads;

                     while (ixxxx-- != 0) {
                        queue.add(POISON_PILL);
                     }
                  }

                  ShortArrays.Segment segment = queue.take();
                  if (segment == POISON_PILL) {
                     return null;
                  }

                  int first = segment.offset;
                  int length = segment.length;
                  int level = segment.level;
                  int signMask = level % 2 == 0 ? 128 : 0;
                  int shift = (1 - level % 2) * 8;
                  int ixxx = first + length;

                  while (ixxx-- != first) {
                     count[a[ixxx] >>> shift & 0xFF ^ signMask]++;
                  }

                  ixxx = -1;
                  int ix = 0;

                  for (int p = first; ix < 256; ix++) {
                     if (count[ix] != 0) {
                        ixxx = ix;
                     }

                     pos[ix] = p += count[ix];
                  }

                  ix = first + length - count[ixxx];
                  int ixx = first;
                  int c = -1;

                  while (ixx <= ix) {
                     short t = a[ixx];
                     c = t >>> shift & 0xFF ^ signMask;
                     if (ixx < ix) {
                        int d;
                        while ((d = --pos[c]) > ixx) {
                           short z = t;
                           t = a[d];
                           a[d] = z;
                           c = t >>> shift & 0xFF ^ signMask;
                        }

                        a[ixx] = t;
                     }

                     if (level < 1 && count[c] > 1) {
                        if (count[c] < 1024) {
                           quickSort(a, ixx, ixx + count[c]);
                        } else {
                           queueSize.incrementAndGet();
                           queue.add(new ShortArrays.Segment(ixx, count[c], level + 1));
                        }
                     }

                     ixx += count[c];
                     count[c] = 0;
                  }

                  queueSize.decrementAndGet();
               }
            });
         }

         Throwable problem = null;
         int i = numberOfThreads;

         while (i-- != 0) {
            try {
               executorCompletionService.take().get();
            } catch (Exception var12) {
               problem = var12.getCause();
            }
         }

         if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
         }
      } else {
         quickSort(a, from, to);
      }
   }

   public static void parallelRadixSort(short[] a) {
      parallelRadixSort(a, 0, a.length);
   }

   public static void radixSortIndirect(int[] perm, short[] a, boolean stable) {
      radixSortIndirect(perm, a, 0, perm.length, stable);
   }

   public static void radixSortIndirect(int[] perm, short[] a, int from, int to, boolean stable) {
      if (to - from < 1024) {
         quickSortIndirect(perm, a, from, to);
         if (stable) {
            stabilize(perm, a, from, to);
         }
      } else {
         int maxLevel = 1;
         int stackSize = 256;
         int stackPos = 0;
         int[] offsetStack = new int[256];
         int[] lengthStack = new int[256];
         int[] levelStack = new int[256];
         offsetStack[stackPos] = from;
         lengthStack[stackPos] = to - from;
         levelStack[stackPos++] = 0;
         int[] count = new int[256];
         int[] pos = new int[256];
         int[] support = stable ? new int[perm.length] : null;

         while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 2 == 0 ? 128 : 0;
            int shift = (1 - level % 2) * 8;
            int i = first + length;

            while (i-- != first) {
               count[a[perm[i]] >>> shift & 0xFF ^ signMask]++;
            }

            i = -1;
            int ix = 0;

            for (int p = stable ? 0 : first; ix < 256; ix++) {
               if (count[ix] != 0) {
                  i = ix;
               }

               pos[ix] = p += count[ix];
            }

            if (stable) {
               ix = first + length;

               while (ix-- != first) {
                  support[--pos[a[perm[ix]] >>> shift & 0xFF ^ signMask]] = perm[ix];
               }

               System.arraycopy(support, 0, perm, first, length);
               ix = 0;

               for (int p = first; ix <= i; ix++) {
                  if (level < 1 && count[ix] > 1) {
                     if (count[ix] < 1024) {
                        quickSortIndirect(perm, a, p, p + count[ix]);
                        if (stable) {
                           stabilize(perm, a, p, p + count[ix]);
                        }
                     } else {
                        offsetStack[stackPos] = p;
                        lengthStack[stackPos] = count[ix];
                        levelStack[stackPos++] = level + 1;
                     }
                  }

                  p += count[ix];
               }

               java.util.Arrays.fill(count, 0);
            } else {
               ix = first + length - count[i];
               int ixx = first;
               int c = -1;

               while (ixx <= ix) {
                  int t = perm[ixx];
                  c = a[t] >>> shift & 0xFF ^ signMask;
                  if (ixx < ix) {
                     int d;
                     while ((d = --pos[c]) > ixx) {
                        int z = t;
                        t = perm[d];
                        perm[d] = z;
                        c = a[t] >>> shift & 0xFF ^ signMask;
                     }

                     perm[ixx] = t;
                  }

                  if (level < 1 && count[c] > 1) {
                     if (count[c] < 1024) {
                        quickSortIndirect(perm, a, ixx, ixx + count[c]);
                        if (stable) {
                           stabilize(perm, a, ixx, ixx + count[c]);
                        }
                     } else {
                        offsetStack[stackPos] = ixx;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                     }
                  }

                  ixx += count[c];
                  count[c] = 0;
               }
            }
         }
      }
   }

   public static void parallelRadixSortIndirect(int[] perm, short[] a, int from, int to, boolean stable) {
      ForkJoinPool pool = getPool();
      if (to - from >= 1024 && pool.getParallelism() != 1) {
         int maxLevel = 1;
         LinkedBlockingQueue<ShortArrays.Segment> queue = new LinkedBlockingQueue<>();
         queue.add(new ShortArrays.Segment(from, to - from, 0));
         AtomicInteger queueSize = new AtomicInteger(1);
         int numberOfThreads = pool.getParallelism();
         ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<>(pool);
         int[] support = stable ? new int[perm.length] : null;
         int j = numberOfThreads;

         while (j-- != 0) {
            executorCompletionService.submit(() -> {
               int[] count = new int[256];
               int[] pos = new int[256];

               while (true) {
                  if (queueSize.get() == 0) {
                     int ixxx = numberOfThreads;

                     while (ixxx-- != 0) {
                        queue.add(POISON_PILL);
                     }
                  }

                  ShortArrays.Segment segment = queue.take();
                  if (segment == POISON_PILL) {
                     return null;
                  }

                  int first = segment.offset;
                  int length = segment.length;
                  int level = segment.level;
                  int signMask = level % 2 == 0 ? 128 : 0;
                  int shift = (1 - level % 2) * 8;
                  int ixx = first + length;

                  while (ixx-- != first) {
                     count[a[perm[ixx]] >>> shift & 0xFF ^ signMask]++;
                  }

                  ixx = -1;
                  int ix = 0;

                  for (int p = first; ix < 256; ix++) {
                     if (count[ix] != 0) {
                        ixx = ix;
                     }

                     pos[ix] = p += count[ix];
                  }

                  if (stable) {
                     ix = first + length;

                     while (ix-- != first) {
                        support[--pos[a[perm[ix]] >>> shift & 0xFF ^ signMask]] = perm[ix];
                     }

                     System.arraycopy(support, first, perm, first, length);
                     ix = 0;

                     for (int p = first; ix <= ixx; ix++) {
                        if (level < 1 && count[ix] > 1) {
                           if (count[ix] < 1024) {
                              radixSortIndirect(perm, a, p, p + count[ix], stable);
                           } else {
                              queueSize.incrementAndGet();
                              queue.add(new ShortArrays.Segment(p, count[ix], level + 1));
                           }
                        }

                        p += count[ix];
                     }

                     java.util.Arrays.fill(count, 0);
                  } else {
                     ix = first + length - count[ixx];
                     int ixx = first;
                     int c = -1;

                     while (ixx <= ix) {
                        int t = perm[ixx];
                        c = a[t] >>> shift & 0xFF ^ signMask;
                        if (ixx < ix) {
                           int d;
                           while ((d = --pos[c]) > ixx) {
                              int z = t;
                              t = perm[d];
                              perm[d] = z;
                              c = a[t] >>> shift & 0xFF ^ signMask;
                           }

                           perm[ixx] = t;
                        }

                        if (level < 1 && count[c] > 1) {
                           if (count[c] < 1024) {
                              radixSortIndirect(perm, a, ixx, ixx + count[c], stable);
                           } else {
                              queueSize.incrementAndGet();
                              queue.add(new ShortArrays.Segment(ixx, count[c], level + 1));
                           }
                        }

                        ixx += count[c];
                        count[c] = 0;
                     }
                  }

                  queueSize.decrementAndGet();
               }
            });
         }

         Throwable problem = null;
         int i = numberOfThreads;

         while (i-- != 0) {
            try {
               executorCompletionService.take().get();
            } catch (Exception var15) {
               problem = var15.getCause();
            }
         }

         if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
         }
      } else {
         radixSortIndirect(perm, a, from, to, stable);
      }
   }

   public static void parallelRadixSortIndirect(int[] perm, short[] a, boolean stable) {
      parallelRadixSortIndirect(perm, a, 0, a.length, stable);
   }

   public static void radixSort(short[] a, short[] b) {
      ensureSameLength(a, b);
      radixSort(a, b, 0, a.length);
   }

   public static void radixSort(short[] a, short[] b, int from, int to) {
      if (to - from < 1024) {
         quickSort(a, b, from, to);
      } else {
         int layers = 2;
         int maxLevel = 3;
         int stackSize = 766;
         int stackPos = 0;
         int[] offsetStack = new int[766];
         int[] lengthStack = new int[766];
         int[] levelStack = new int[766];
         offsetStack[stackPos] = from;
         lengthStack[stackPos] = to - from;
         levelStack[stackPos++] = 0;
         int[] count = new int[256];
         int[] pos = new int[256];

         while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 2 == 0 ? 128 : 0;
            short[] k = level < 2 ? a : b;
            int shift = (1 - level % 2) * 8;
            int i = first + length;

            while (i-- != first) {
               count[k[i] >>> shift & 0xFF ^ signMask]++;
            }

            i = -1;
            int ix = 0;

            for (int p = first; ix < 256; ix++) {
               if (count[ix] != 0) {
                  i = ix;
               }

               pos[ix] = p += count[ix];
            }

            ix = first + length - count[i];
            int ixx = first;
            int c = -1;

            while (ixx <= ix) {
               short t = a[ixx];
               short u = b[ixx];
               c = k[ixx] >>> shift & 0xFF ^ signMask;
               if (ixx < ix) {
                  int d;
                  while ((d = --pos[c]) > ixx) {
                     c = k[d] >>> shift & 0xFF ^ signMask;
                     short z = t;
                     t = a[d];
                     a[d] = z;
                     z = u;
                     u = b[d];
                     b[d] = z;
                  }

                  a[ixx] = t;
                  b[ixx] = u;
               }

               if (level < 3 && count[c] > 1) {
                  if (count[c] < 1024) {
                     quickSort(a, b, ixx, ixx + count[c]);
                  } else {
                     offsetStack[stackPos] = ixx;
                     lengthStack[stackPos] = count[c];
                     levelStack[stackPos++] = level + 1;
                  }
               }

               ixx += count[c];
               count[c] = 0;
            }
         }
      }
   }

   public static void parallelRadixSort(short[] a, short[] b, int from, int to) {
      ForkJoinPool pool = getPool();
      if (to - from >= 1024 && pool.getParallelism() != 1) {
         int layers = 2;
         if (a.length != b.length) {
            throw new IllegalArgumentException("Array size mismatch.");
         } else {
            int maxLevel = 3;
            LinkedBlockingQueue<ShortArrays.Segment> queue = new LinkedBlockingQueue<>();
            queue.add(new ShortArrays.Segment(from, to - from, 0));
            AtomicInteger queueSize = new AtomicInteger(1);
            int numberOfThreads = pool.getParallelism();
            ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<>(pool);
            int j = numberOfThreads;

            while (j-- != 0) {
               executorCompletionService.submit(() -> {
                  int[] count = new int[256];
                  int[] pos = new int[256];

                  while (true) {
                     if (queueSize.get() == 0) {
                        int ixx = numberOfThreads;

                        while (ixx-- != 0) {
                           queue.add(POISON_PILL);
                        }
                     }

                     ShortArrays.Segment segment = queue.take();
                     if (segment == POISON_PILL) {
                        return null;
                     }

                     int first = segment.offset;
                     int length = segment.length;
                     int level = segment.level;
                     int signMask = level % 2 == 0 ? 128 : 0;
                     short[] k = level < 2 ? a : b;
                     int shift = (1 - level % 2) * 8;
                     int ixxxx = first + length;

                     while (ixxxx-- != first) {
                        count[k[ixxxx] >>> shift & 0xFF ^ signMask]++;
                     }

                     ixxxx = -1;
                     int ix = 0;

                     for (int p = first; ix < 256; ix++) {
                        if (count[ix] != 0) {
                           ixxxx = ix;
                        }

                        pos[ix] = p += count[ix];
                     }

                     ix = first + length - count[ixxxx];
                     int ixx = first;
                     int c = -1;

                     while (ixx <= ix) {
                        short t = a[ixx];
                        short u = b[ixx];
                        c = k[ixx] >>> shift & 0xFF ^ signMask;
                        if (ixx < ix) {
                           int d;
                           while ((d = --pos[c]) > ixx) {
                              c = k[d] >>> shift & 0xFF ^ signMask;
                              short z = t;
                              short w = u;
                              t = a[d];
                              u = b[d];
                              a[d] = z;
                              b[d] = w;
                           }

                           a[ixx] = t;
                           b[ixx] = u;
                        }

                        if (level < 3 && count[c] > 1) {
                           if (count[c] < 1024) {
                              quickSort(a, b, ixx, ixx + count[c]);
                           } else {
                              queueSize.incrementAndGet();
                              queue.add(new ShortArrays.Segment(ixx, count[c], level + 1));
                           }
                        }

                        ixx += count[c];
                        count[c] = 0;
                     }

                     queueSize.decrementAndGet();
                  }
               });
            }

            Throwable problem = null;
            int i = numberOfThreads;

            while (i-- != 0) {
               try {
                  executorCompletionService.take().get();
               } catch (Exception var14) {
                  problem = var14.getCause();
               }
            }

            if (problem != null) {
               throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
            }
         }
      } else {
         quickSort(a, b, from, to);
      }
   }

   public static void parallelRadixSort(short[] a, short[] b) {
      ensureSameLength(a, b);
      parallelRadixSort(a, b, 0, a.length);
   }

   private static void insertionSortIndirect(int[] perm, short[] a, short[] b, int from, int to) {
      int i = from;

      while (++i < to) {
         int t = perm[i];
         int j = i;

         for (int u = perm[i - 1]; a[t] < a[u] || a[t] == a[u] && b[t] < b[u]; u = perm[j - 1]) {
            perm[j] = u;
            if (from == j - 1) {
               j--;
               break;
            }

            j--;
         }

         perm[j] = t;
      }
   }

   public static void radixSortIndirect(int[] perm, short[] a, short[] b, boolean stable) {
      ensureSameLength(a, b);
      radixSortIndirect(perm, a, b, 0, a.length, stable);
   }

   public static void radixSortIndirect(int[] perm, short[] a, short[] b, int from, int to, boolean stable) {
      if (to - from < 64) {
         insertionSortIndirect(perm, a, b, from, to);
      } else {
         int layers = 2;
         int maxLevel = 3;
         int stackSize = 766;
         int stackPos = 0;
         int[] offsetStack = new int[766];
         int[] lengthStack = new int[766];
         int[] levelStack = new int[766];
         offsetStack[stackPos] = from;
         lengthStack[stackPos] = to - from;
         levelStack[stackPos++] = 0;
         int[] count = new int[256];
         int[] pos = new int[256];
         int[] support = stable ? new int[perm.length] : null;

         while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 2 == 0 ? 128 : 0;
            short[] k = level < 2 ? a : b;
            int shift = (1 - level % 2) * 8;
            int i = first + length;

            while (i-- != first) {
               count[k[perm[i]] >>> shift & 0xFF ^ signMask]++;
            }

            i = -1;
            int ix = 0;

            for (int p = stable ? 0 : first; ix < 256; ix++) {
               if (count[ix] != 0) {
                  i = ix;
               }

               pos[ix] = p += count[ix];
            }

            if (stable) {
               ix = first + length;

               while (ix-- != first) {
                  support[--pos[k[perm[ix]] >>> shift & 0xFF ^ signMask]] = perm[ix];
               }

               System.arraycopy(support, 0, perm, first, length);
               ix = 0;

               for (int p = first; ix < 256; ix++) {
                  if (level < 3 && count[ix] > 1) {
                     if (count[ix] < 64) {
                        insertionSortIndirect(perm, a, b, p, p + count[ix]);
                     } else {
                        offsetStack[stackPos] = p;
                        lengthStack[stackPos] = count[ix];
                        levelStack[stackPos++] = level + 1;
                     }
                  }

                  p += count[ix];
               }

               java.util.Arrays.fill(count, 0);
            } else {
               ix = first + length - count[i];
               int ixx = first;
               int c = -1;

               while (ixx <= ix) {
                  int t = perm[ixx];
                  c = k[t] >>> shift & 0xFF ^ signMask;
                  if (ixx < ix) {
                     int d;
                     while ((d = --pos[c]) > ixx) {
                        int z = t;
                        t = perm[d];
                        perm[d] = z;
                        c = k[t] >>> shift & 0xFF ^ signMask;
                     }

                     perm[ixx] = t;
                  }

                  if (level < 3 && count[c] > 1) {
                     if (count[c] < 64) {
                        insertionSortIndirect(perm, a, b, ixx, ixx + count[c]);
                     } else {
                        offsetStack[stackPos] = ixx;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                     }
                  }

                  ixx += count[c];
                  count[c] = 0;
               }
            }
         }
      }
   }

   private static void selectionSort(short[][] a, int from, int to, int level) {
      int layers = a.length;
      int firstLayer = level / 2;

      for (int i = from; i < to - 1; i++) {
         int m = i;

         for (int j = i + 1; j < to; j++) {
            for (int p = firstLayer; p < layers; p++) {
               if (a[p][j] < a[p][m]) {
                  m = j;
                  break;
               }

               if (a[p][j] > a[p][m]) {
                  break;
               }
            }
         }

         if (m != i) {
            int p = layers;

            while (p-- != 0) {
               short u = a[p][i];
               a[p][i] = a[p][m];
               a[p][m] = u;
            }
         }
      }
   }

   public static void radixSort(short[][] a) {
      radixSort(a, 0, a[0].length);
   }

   public static void radixSort(short[][] a, int from, int to) {
      if (to - from < 64) {
         selectionSort(a, from, to, 0);
      } else {
         int layers = a.length;
         int maxLevel = 2 * layers - 1;
         int p = layers;
         int l = a[0].length;

         while (p-- != 0) {
            if (a[p].length != l) {
               throw new IllegalArgumentException("The array of index " + p + " has not the same length of the array of index 0.");
            }
         }

         p = 255 * (layers * 2 - 1) + 1;
         l = 0;
         int[] offsetStack = new int[p];
         int[] lengthStack = new int[p];
         int[] levelStack = new int[p];
         offsetStack[l] = from;
         lengthStack[l] = to - from;
         levelStack[l++] = 0;
         int[] count = new int[256];
         int[] pos = new int[256];
         short[] t = new short[layers];

         while (l > 0) {
            int first = offsetStack[--l];
            int length = lengthStack[l];
            int level = levelStack[l];
            int signMask = level % 2 == 0 ? 128 : 0;
            short[] k = a[level / 2];
            int shift = (1 - level % 2) * 8;
            int i = first + length;

            while (i-- != first) {
               count[k[i] >>> shift & 0xFF ^ signMask]++;
            }

            i = -1;
            int ix = 0;

            for (int px = first; ix < 256; ix++) {
               if (count[ix] != 0) {
                  i = ix;
               }

               pos[ix] = px += count[ix];
            }

            ix = first + length - count[i];
            int ixx = first;
            int c = -1;

            while (ixx <= ix) {
               int px = layers;

               while (px-- != 0) {
                  t[px] = a[px][ixx];
               }

               c = k[ixx] >>> shift & 0xFF ^ signMask;
               if (ixx < ix) {
                  int d;
                  while ((d = --pos[c]) > ixx) {
                     c = k[d] >>> shift & 0xFF ^ signMask;
                     px = layers;

                     while (px-- != 0) {
                        short u = t[px];
                        t[px] = a[px][d];
                        a[px][d] = u;
                     }
                  }

                  px = layers;

                  while (px-- != 0) {
                     a[px][ixx] = t[px];
                  }
               }

               if (level < maxLevel && count[c] > 1) {
                  if (count[c] < 64) {
                     selectionSort(a, ixx, ixx + count[c], level + 1);
                  } else {
                     offsetStack[l] = ixx;
                     lengthStack[l] = count[c];
                     levelStack[l++] = level + 1;
                  }
               }

               ixx += count[c];
               count[c] = 0;
            }
         }
      }
   }

   public static short[] shuffle(short[] a, int from, int to, Random random) {
      int i = to - from;

      while (i-- != 0) {
         int p = random.nextInt(i + 1);
         short t = a[from + i];
         a[from + i] = a[from + p];
         a[from + p] = t;
      }

      return a;
   }

   public static short[] shuffle(short[] a, Random random) {
      int i = a.length;

      while (i-- != 0) {
         int p = random.nextInt(i + 1);
         short t = a[i];
         a[i] = a[p];
         a[p] = t;
      }

      return a;
   }

   public static short[] reverse(short[] a) {
      int length = a.length;
      int i = length / 2;

      while (i-- != 0) {
         short t = a[length - i - 1];
         a[length - i - 1] = a[i];
         a[i] = t;
      }

      return a;
   }

   public static short[] reverse(short[] a, int from, int to) {
      int length = to - from;
      int i = length / 2;

      while (i-- != 0) {
         short t = a[from + length - i - 1];
         a[from + length - i - 1] = a[from + i];
         a[from + i] = t;
      }

      return a;
   }

   private static final class ArrayHashStrategy implements Hash.Strategy<short[]>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      private ArrayHashStrategy() {
      }

      public int hashCode(short[] o) {
         return java.util.Arrays.hashCode(o);
      }

      public boolean equals(short[] a, short[] b) {
         return java.util.Arrays.equals(a, b);
      }
   }

   protected static class ForkJoinQuickSort extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final short[] x;

      public ForkJoinQuickSort(short[] x, int from, int to) {
         this.from = from;
         this.to = to;
         this.x = x;
      }

      @Override
      protected void compute() {
         short[] x = this.x;
         int len = this.to - this.from;
         if (len < 8192) {
            ShortArrays.quickSort(x, this.from, this.to);
         } else {
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ShortArrays.med3(x, l, l + s, l + 2 * s);
            m = ShortArrays.med3(x, m - s, m, m + s);
            n = ShortArrays.med3(x, n - 2 * s, n - s, n);
            m = ShortArrays.med3(x, l, m, n);
            short v = x[m];
            int a = this.from;
            int b = a;
            int c = this.to - 1;
            int d = c;

            while (true) {
               int comparison;
               while (b > c || (comparison = Short.compare(x[b], v)) > 0) {
                  for (; c >= b && (comparison = Short.compare(x[c], v)) >= 0; c--) {
                     if (comparison == 0) {
                        ShortArrays.swap(x, c, d--);
                     }
                  }

                  if (b > c) {
                     s = Math.min(a - this.from, b - a);
                     ShortArrays.swap(x, this.from, b - s, s);
                     s = Math.min(d - c, this.to - d - 1);
                     ShortArrays.swap(x, b, this.to - s, s);
                     s = b - a;
                     comparison = d - c;
                     if (s > 1 && comparison > 1) {
                        invokeAll(
                           new ShortArrays.ForkJoinQuickSort(x, this.from, this.from + s), new ShortArrays.ForkJoinQuickSort(x, this.to - comparison, this.to)
                        );
                     } else if (s > 1) {
                        invokeAll(new ShortArrays.ForkJoinQuickSort(x, this.from, this.from + s));
                     } else {
                        invokeAll(new ShortArrays.ForkJoinQuickSort(x, this.to - comparison, this.to));
                     }

                     return;
                  }

                  ShortArrays.swap(x, b++, c--);
               }

               if (comparison == 0) {
                  ShortArrays.swap(x, a++, b);
               }

               b++;
            }
         }
      }
   }

   protected static class ForkJoinQuickSort2 extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final short[] x;
      private final short[] y;

      public ForkJoinQuickSort2(short[] x, short[] y, int from, int to) {
         this.from = from;
         this.to = to;
         this.x = x;
         this.y = y;
      }

      @Override
      protected void compute() {
         short[] x = this.x;
         short[] y = this.y;
         int len = this.to - this.from;
         if (len < 8192) {
            ShortArrays.quickSort(x, y, this.from, this.to);
         } else {
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ShortArrays.med3(x, y, l, l + s, l + 2 * s);
            m = ShortArrays.med3(x, y, m - s, m, m + s);
            n = ShortArrays.med3(x, y, n - 2 * s, n - s, n);
            m = ShortArrays.med3(x, y, l, m, n);
            short v = x[m];
            short w = y[m];
            int a = this.from;
            int b = a;
            int c = this.to - 1;
            int d = c;

            while (true) {
               int comparison;
               int t;
               while (b > c || (comparison = (t = Short.compare(x[b], v)) == 0 ? Short.compare(y[b], w) : t) > 0) {
                  for (; c >= b && (comparison = (t = Short.compare(x[c], v)) == 0 ? Short.compare(y[c], w) : t) >= 0; c--) {
                     if (comparison == 0) {
                        ShortArrays.swap(x, y, c, d--);
                     }
                  }

                  if (b > c) {
                     s = Math.min(a - this.from, b - a);
                     ShortArrays.swap(x, y, this.from, b - s, s);
                     s = Math.min(d - c, this.to - d - 1);
                     ShortArrays.swap(x, y, b, this.to - s, s);
                     s = b - a;
                     comparison = d - c;
                     if (s > 1 && comparison > 1) {
                        invokeAll(
                           new ShortArrays.ForkJoinQuickSort2(x, y, this.from, this.from + s),
                           new ShortArrays.ForkJoinQuickSort2(x, y, this.to - comparison, this.to)
                        );
                     } else if (s > 1) {
                        invokeAll(new ShortArrays.ForkJoinQuickSort2(x, y, this.from, this.from + s));
                     } else {
                        invokeAll(new ShortArrays.ForkJoinQuickSort2(x, y, this.to - comparison, this.to));
                     }

                     return;
                  }

                  ShortArrays.swap(x, y, b++, c--);
               }

               if (comparison == 0) {
                  ShortArrays.swap(x, y, a++, b);
               }

               b++;
            }
         }
      }
   }

   protected static class ForkJoinQuickSortComp extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final short[] x;
      private final ShortComparator comp;

      public ForkJoinQuickSortComp(short[] x, int from, int to, ShortComparator comp) {
         this.from = from;
         this.to = to;
         this.x = x;
         this.comp = comp;
      }

      @Override
      protected void compute() {
         short[] x = this.x;
         int len = this.to - this.from;
         if (len < 8192) {
            ShortArrays.quickSort(x, this.from, this.to, this.comp);
         } else {
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ShortArrays.med3(x, l, l + s, l + 2 * s, this.comp);
            m = ShortArrays.med3(x, m - s, m, m + s, this.comp);
            n = ShortArrays.med3(x, n - 2 * s, n - s, n, this.comp);
            m = ShortArrays.med3(x, l, m, n, this.comp);
            short v = x[m];
            int a = this.from;
            int b = a;
            int c = this.to - 1;
            int d = c;

            while (true) {
               int comparison;
               while (b > c || (comparison = this.comp.compare(x[b], v)) > 0) {
                  for (; c >= b && (comparison = this.comp.compare(x[c], v)) >= 0; c--) {
                     if (comparison == 0) {
                        ShortArrays.swap(x, c, d--);
                     }
                  }

                  if (b > c) {
                     s = Math.min(a - this.from, b - a);
                     ShortArrays.swap(x, this.from, b - s, s);
                     s = Math.min(d - c, this.to - d - 1);
                     ShortArrays.swap(x, b, this.to - s, s);
                     s = b - a;
                     comparison = d - c;
                     if (s > 1 && comparison > 1) {
                        invokeAll(
                           new ShortArrays.ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp),
                           new ShortArrays.ForkJoinQuickSortComp(x, this.to - comparison, this.to, this.comp)
                        );
                     } else if (s > 1) {
                        invokeAll(new ShortArrays.ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp));
                     } else {
                        invokeAll(new ShortArrays.ForkJoinQuickSortComp(x, this.to - comparison, this.to, this.comp));
                     }

                     return;
                  }

                  ShortArrays.swap(x, b++, c--);
               }

               if (comparison == 0) {
                  ShortArrays.swap(x, a++, b);
               }

               b++;
            }
         }
      }
   }

   protected static class ForkJoinQuickSortIndirect extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final int[] perm;
      private final short[] x;

      public ForkJoinQuickSortIndirect(int[] perm, short[] x, int from, int to) {
         this.from = from;
         this.to = to;
         this.x = x;
         this.perm = perm;
      }

      @Override
      protected void compute() {
         short[] x = this.x;
         int len = this.to - this.from;
         if (len < 8192) {
            ShortArrays.quickSortIndirect(this.perm, x, this.from, this.to);
         } else {
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ShortArrays.med3Indirect(this.perm, x, l, l + s, l + 2 * s);
            m = ShortArrays.med3Indirect(this.perm, x, m - s, m, m + s);
            n = ShortArrays.med3Indirect(this.perm, x, n - 2 * s, n - s, n);
            m = ShortArrays.med3Indirect(this.perm, x, l, m, n);
            short v = x[this.perm[m]];
            int a = this.from;
            int b = a;
            int c = this.to - 1;
            int d = c;

            while (true) {
               int comparison;
               while (b > c || (comparison = Short.compare(x[this.perm[b]], v)) > 0) {
                  for (; c >= b && (comparison = Short.compare(x[this.perm[c]], v)) >= 0; c--) {
                     if (comparison == 0) {
                        IntArrays.swap(this.perm, c, d--);
                     }
                  }

                  if (b > c) {
                     s = Math.min(a - this.from, b - a);
                     IntArrays.swap(this.perm, this.from, b - s, s);
                     s = Math.min(d - c, this.to - d - 1);
                     IntArrays.swap(this.perm, b, this.to - s, s);
                     s = b - a;
                     comparison = d - c;
                     if (s > 1 && comparison > 1) {
                        invokeAll(
                           new ShortArrays.ForkJoinQuickSortIndirect(this.perm, x, this.from, this.from + s),
                           new ShortArrays.ForkJoinQuickSortIndirect(this.perm, x, this.to - comparison, this.to)
                        );
                     } else if (s > 1) {
                        invokeAll(new ShortArrays.ForkJoinQuickSortIndirect(this.perm, x, this.from, this.from + s));
                     } else {
                        invokeAll(new ShortArrays.ForkJoinQuickSortIndirect(this.perm, x, this.to - comparison, this.to));
                     }

                     return;
                  }

                  IntArrays.swap(this.perm, b++, c--);
               }

               if (comparison == 0) {
                  IntArrays.swap(this.perm, a++, b);
               }

               b++;
            }
         }
      }
   }

   protected static final class Segment {
      protected final int offset;
      protected final int length;
      protected final int level;

      protected Segment(int offset, int length, int level) {
         this.offset = offset;
         this.length = length;
         this.level = level;
      }

      @Override
      public String toString() {
         return "Segment [offset=" + this.offset + ", length=" + this.length + ", level=" + this.level + "]";
      }
   }
}
