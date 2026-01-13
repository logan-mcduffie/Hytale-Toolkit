package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public final class BooleanArrays {
   public static final boolean[] EMPTY_ARRAY = new boolean[0];
   public static final boolean[] DEFAULT_EMPTY_ARRAY = new boolean[0];
   private static final int QUICKSORT_NO_REC = 16;
   private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
   private static final int QUICKSORT_MEDIAN_OF_9 = 128;
   private static final int MERGESORT_NO_REC = 16;
   public static final Hash.Strategy<boolean[]> HASH_STRATEGY = new BooleanArrays.ArrayHashStrategy();

   private BooleanArrays() {
   }

   public static boolean[] forceCapacity(boolean[] array, int length, int preserve) {
      boolean[] t = new boolean[length];
      System.arraycopy(array, 0, t, 0, preserve);
      return t;
   }

   public static boolean[] ensureCapacity(boolean[] array, int length) {
      return ensureCapacity(array, length, array.length);
   }

   public static boolean[] ensureCapacity(boolean[] array, int length, int preserve) {
      return length > array.length ? forceCapacity(array, length, preserve) : array;
   }

   public static boolean[] grow(boolean[] array, int length) {
      return grow(array, length, array.length);
   }

   public static boolean[] grow(boolean[] array, int length, int preserve) {
      if (length > array.length) {
         int newLength = (int)Math.max(Math.min((long)array.length + (array.length >> 1), 2147483639L), (long)length);
         boolean[] t = new boolean[newLength];
         System.arraycopy(array, 0, t, 0, preserve);
         return t;
      } else {
         return array;
      }
   }

   public static boolean[] trim(boolean[] array, int length) {
      if (length >= array.length) {
         return array;
      } else {
         boolean[] t = length == 0 ? EMPTY_ARRAY : new boolean[length];
         System.arraycopy(array, 0, t, 0, length);
         return t;
      }
   }

   public static boolean[] setLength(boolean[] array, int length) {
      if (length == array.length) {
         return array;
      } else {
         return length < array.length ? trim(array, length) : ensureCapacity(array, length);
      }
   }

   public static boolean[] copy(boolean[] array, int offset, int length) {
      ensureOffsetLength(array, offset, length);
      boolean[] a = length == 0 ? EMPTY_ARRAY : new boolean[length];
      System.arraycopy(array, offset, a, 0, length);
      return a;
   }

   public static boolean[] copy(boolean[] array) {
      return (boolean[])array.clone();
   }

   @Deprecated
   public static void fill(boolean[] array, boolean value) {
      int i = array.length;

      while (i-- != 0) {
         array[i] = value;
      }
   }

   @Deprecated
   public static void fill(boolean[] array, int from, int to, boolean value) {
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
   public static boolean equals(boolean[] a1, boolean[] a2) {
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

   public static void ensureFromTo(boolean[] a, int from, int to) {
      Arrays.ensureFromTo(a.length, from, to);
   }

   public static void ensureOffsetLength(boolean[] a, int offset, int length) {
      Arrays.ensureOffsetLength(a.length, offset, length);
   }

   public static void ensureSameLength(boolean[] a, boolean[] b) {
      if (a.length != b.length) {
         throw new IllegalArgumentException("Array size mismatch: " + a.length + " != " + b.length);
      }
   }

   private static ForkJoinPool getPool() {
      ForkJoinPool current = ForkJoinTask.getPool();
      return current == null ? ForkJoinPool.commonPool() : current;
   }

   public static void swap(boolean[] x, int a, int b) {
      boolean t = x[a];
      x[a] = x[b];
      x[b] = t;
   }

   public static void swap(boolean[] x, int a, int b, int n) {
      int i = 0;

      while (i < n) {
         swap(x, a, b);
         i++;
         a++;
         b++;
      }
   }

   private static int med3(boolean[] x, int a, int b, int c, BooleanComparator comp) {
      int ab = comp.compare(x[a], x[b]);
      int ac = comp.compare(x[a], x[c]);
      int bc = comp.compare(x[b], x[c]);
      return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
   }

   private static void selectionSort(boolean[] a, int from, int to, BooleanComparator comp) {
      for (int i = from; i < to - 1; i++) {
         int m = i;

         for (int j = i + 1; j < to; j++) {
            if (comp.compare(a[j], a[m]) < 0) {
               m = j;
            }
         }

         if (m != i) {
            boolean u = a[i];
            a[i] = a[m];
            a[m] = u;
         }
      }
   }

   private static void insertionSort(boolean[] a, int from, int to, BooleanComparator comp) {
      int i = from;

      while (++i < to) {
         boolean t = a[i];
         int j = i;

         for (boolean u = a[i - 1]; comp.compare(t, u) < 0; u = a[j - 1]) {
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

   public static void quickSort(boolean[] x, int from, int to, BooleanComparator comp) {
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
         boolean v = x[m];
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

   public static void quickSort(boolean[] x, BooleanComparator comp) {
      quickSort(x, 0, x.length, comp);
   }

   public static void parallelQuickSort(boolean[] x, int from, int to, BooleanComparator comp) {
      ForkJoinPool pool = getPool();
      if (to - from >= 8192 && pool.getParallelism() != 1) {
         pool.invoke(new BooleanArrays.ForkJoinQuickSortComp(x, from, to, comp));
      } else {
         quickSort(x, from, to, comp);
      }
   }

   public static void parallelQuickSort(boolean[] x, BooleanComparator comp) {
      parallelQuickSort(x, 0, x.length, comp);
   }

   private static int med3(boolean[] x, int a, int b, int c) {
      int ab = Boolean.compare(x[a], x[b]);
      int ac = Boolean.compare(x[a], x[c]);
      int bc = Boolean.compare(x[b], x[c]);
      return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
   }

   private static void selectionSort(boolean[] a, int from, int to) {
      for (int i = from; i < to - 1; i++) {
         int m = i;

         for (int j = i + 1; j < to; j++) {
            if (!a[j] && a[m]) {
               m = j;
            }
         }

         if (m != i) {
            boolean u = a[i];
            a[i] = a[m];
            a[m] = u;
         }
      }
   }

   private static void insertionSort(boolean[] a, int from, int to) {
      int i = from;

      while (++i < to) {
         boolean t = a[i];
         int j = i;

         for (boolean u = a[i - 1]; !t && u; u = a[j - 1]) {
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

   public static void quickSort(boolean[] x, int from, int to) {
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
         boolean v = x[m];
         int a = from;
         int b = from;
         int c = to - 1;
         int d = c;

         while (true) {
            int comparison;
            while (b > c || (comparison = Boolean.compare(x[b], v)) > 0) {
               for (; c >= b && (comparison = Boolean.compare(x[c], v)) >= 0; c--) {
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

   public static void quickSort(boolean[] x) {
      quickSort(x, 0, x.length);
   }

   public static void parallelQuickSort(boolean[] x, int from, int to) {
      ForkJoinPool pool = getPool();
      if (to - from >= 8192 && pool.getParallelism() != 1) {
         pool.invoke(new BooleanArrays.ForkJoinQuickSort(x, from, to));
      } else {
         quickSort(x, from, to);
      }
   }

   public static void parallelQuickSort(boolean[] x) {
      parallelQuickSort(x, 0, x.length);
   }

   private static int med3Indirect(int[] perm, boolean[] x, int a, int b, int c) {
      boolean aa = x[perm[a]];
      boolean bb = x[perm[b]];
      boolean cc = x[perm[c]];
      int ab = Boolean.compare(aa, bb);
      int ac = Boolean.compare(aa, cc);
      int bc = Boolean.compare(bb, cc);
      return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
   }

   private static void insertionSortIndirect(int[] perm, boolean[] a, int from, int to) {
      int i = from;

      while (++i < to) {
         int t = perm[i];
         int j = i;

         for (int u = perm[i - 1]; !a[t] && a[u]; u = perm[j - 1]) {
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

   public static void quickSortIndirect(int[] perm, boolean[] x, int from, int to) {
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
         boolean v = x[perm[m]];
         int a = from;
         int b = from;
         int c = to - 1;
         int d = c;

         while (true) {
            int comparison;
            while (b > c || (comparison = Boolean.compare(x[perm[b]], v)) > 0) {
               for (; c >= b && (comparison = Boolean.compare(x[perm[c]], v)) >= 0; c--) {
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

   public static void quickSortIndirect(int[] perm, boolean[] x) {
      quickSortIndirect(perm, x, 0, x.length);
   }

   public static void parallelQuickSortIndirect(int[] perm, boolean[] x, int from, int to) {
      ForkJoinPool pool = getPool();
      if (to - from >= 8192 && pool.getParallelism() != 1) {
         pool.invoke(new BooleanArrays.ForkJoinQuickSortIndirect(perm, x, from, to));
      } else {
         quickSortIndirect(perm, x, from, to);
      }
   }

   public static void parallelQuickSortIndirect(int[] perm, boolean[] x) {
      parallelQuickSortIndirect(perm, x, 0, x.length);
   }

   public static void stabilize(int[] perm, boolean[] x, int from, int to) {
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

   public static void stabilize(int[] perm, boolean[] x) {
      stabilize(perm, x, 0, perm.length);
   }

   private static int med3(boolean[] x, boolean[] y, int a, int b, int c) {
      int t;
      int ab = (t = Boolean.compare(x[a], x[b])) == 0 ? Boolean.compare(y[a], y[b]) : t;
      int ac = (t = Boolean.compare(x[a], x[c])) == 0 ? Boolean.compare(y[a], y[c]) : t;
      int bc = (t = Boolean.compare(x[b], x[c])) == 0 ? Boolean.compare(y[b], y[c]) : t;
      return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
   }

   private static void swap(boolean[] x, boolean[] y, int a, int b) {
      boolean t = x[a];
      boolean u = y[a];
      x[a] = x[b];
      y[a] = y[b];
      x[b] = t;
      y[b] = u;
   }

   private static void swap(boolean[] x, boolean[] y, int a, int b, int n) {
      int i = 0;

      while (i < n) {
         swap(x, y, a, b);
         i++;
         a++;
         b++;
      }
   }

   private static void selectionSort(boolean[] a, boolean[] b, int from, int to) {
      for (int i = from; i < to - 1; i++) {
         int m = i;

         for (int j = i + 1; j < to; j++) {
            int u;
            if ((u = Boolean.compare(a[j], a[m])) < 0 || u == false && !b[j] && b[m]) {
               m = j;
            }
         }

         if (m != i) {
            boolean t = a[i];
            a[i] = a[m];
            a[m] = t;
            t = b[i];
            b[i] = b[m];
            b[m] = t;
         }
      }
   }

   public static void quickSort(boolean[] x, boolean[] y, int from, int to) {
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
         boolean v = x[m];
         boolean w = y[m];
         int a = from;
         int b = from;
         int c = to - 1;
         int d = c;

         while (true) {
            int comparison;
            int t;
            while (b > c || (comparison = (t = Boolean.compare(x[b], v)) == 0 ? Boolean.compare(y[b], w) : t) > 0) {
               for (; c >= b && (comparison = (t = Boolean.compare(x[c], v)) == 0 ? Boolean.compare(y[c], w) : t) >= 0; c--) {
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

   public static void quickSort(boolean[] x, boolean[] y) {
      ensureSameLength(x, y);
      quickSort(x, y, 0, x.length);
   }

   public static void parallelQuickSort(boolean[] x, boolean[] y, int from, int to) {
      ForkJoinPool pool = getPool();
      if (to - from >= 8192 && pool.getParallelism() != 1) {
         pool.invoke(new BooleanArrays.ForkJoinQuickSort2(x, y, from, to));
      } else {
         quickSort(x, y, from, to);
      }
   }

   public static void parallelQuickSort(boolean[] x, boolean[] y) {
      ensureSameLength(x, y);
      parallelQuickSort(x, y, 0, x.length);
   }

   public static void unstableSort(boolean[] a, int from, int to) {
      quickSort(a, from, to);
   }

   public static void unstableSort(boolean[] a) {
      unstableSort(a, 0, a.length);
   }

   public static void unstableSort(boolean[] a, int from, int to, BooleanComparator comp) {
      quickSort(a, from, to, comp);
   }

   public static void unstableSort(boolean[] a, BooleanComparator comp) {
      unstableSort(a, 0, a.length, comp);
   }

   public static void mergeSort(boolean[] a, int from, int to, boolean[] supp) {
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
         if (supp[mid - 1] && !supp[mid]) {
            int i = from;
            int p = from;

            for (int q = mid; i < to; i++) {
               if (q < to && (p >= mid || supp[p] && !supp[q])) {
                  a[i] = supp[q++];
               } else {
                  a[i] = supp[p++];
               }
            }
         } else {
            System.arraycopy(supp, from, a, from, len);
         }
      }
   }

   public static void mergeSort(boolean[] a, int from, int to) {
      mergeSort(a, from, to, (boolean[])null);
   }

   public static void mergeSort(boolean[] a) {
      mergeSort(a, 0, a.length);
   }

   public static void mergeSort(boolean[] a, int from, int to, BooleanComparator comp, boolean[] supp) {
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

   public static void mergeSort(boolean[] a, int from, int to, BooleanComparator comp) {
      mergeSort(a, from, to, comp, (boolean[])null);
   }

   public static void mergeSort(boolean[] a, BooleanComparator comp) {
      mergeSort(a, 0, a.length, comp);
   }

   public static void stableSort(boolean[] a, int from, int to) {
      unstableSort(a, from, to);
   }

   public static void stableSort(boolean[] a) {
      stableSort(a, 0, a.length);
   }

   public static void stableSort(boolean[] a, int from, int to, BooleanComparator comp) {
      mergeSort(a, from, to, comp);
   }

   public static void stableSort(boolean[] a, BooleanComparator comp) {
      stableSort(a, 0, a.length, comp);
   }

   public static boolean[] shuffle(boolean[] a, int from, int to, Random random) {
      int i = to - from;

      while (i-- != 0) {
         int p = random.nextInt(i + 1);
         boolean t = a[from + i];
         a[from + i] = a[from + p];
         a[from + p] = t;
      }

      return a;
   }

   public static boolean[] shuffle(boolean[] a, Random random) {
      int i = a.length;

      while (i-- != 0) {
         int p = random.nextInt(i + 1);
         boolean t = a[i];
         a[i] = a[p];
         a[p] = t;
      }

      return a;
   }

   public static boolean[] reverse(boolean[] a) {
      int length = a.length;
      int i = length / 2;

      while (i-- != 0) {
         boolean t = a[length - i - 1];
         a[length - i - 1] = a[i];
         a[i] = t;
      }

      return a;
   }

   public static boolean[] reverse(boolean[] a, int from, int to) {
      int length = to - from;
      int i = length / 2;

      while (i-- != 0) {
         boolean t = a[from + length - i - 1];
         a[from + length - i - 1] = a[from + i];
         a[from + i] = t;
      }

      return a;
   }

   private static final class ArrayHashStrategy implements Hash.Strategy<boolean[]>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      private ArrayHashStrategy() {
      }

      public int hashCode(boolean[] o) {
         return java.util.Arrays.hashCode(o);
      }

      public boolean equals(boolean[] a, boolean[] b) {
         return java.util.Arrays.equals(a, b);
      }
   }

   protected static class ForkJoinQuickSort extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final boolean[] x;

      public ForkJoinQuickSort(boolean[] x, int from, int to) {
         this.from = from;
         this.to = to;
         this.x = x;
      }

      @Override
      protected void compute() {
         boolean[] x = this.x;
         int len = this.to - this.from;
         if (len < 8192) {
            BooleanArrays.quickSort(x, this.from, this.to);
         } else {
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = BooleanArrays.med3(x, l, l + s, l + 2 * s);
            m = BooleanArrays.med3(x, m - s, m, m + s);
            n = BooleanArrays.med3(x, n - 2 * s, n - s, n);
            m = BooleanArrays.med3(x, l, m, n);
            boolean v = x[m];
            int a = this.from;
            int b = a;
            int c = this.to - 1;
            int d = c;

            while (true) {
               int comparison;
               while (b > c || (comparison = Boolean.compare(x[b], v)) > 0) {
                  for (; c >= b && (comparison = Boolean.compare(x[c], v)) >= 0; c--) {
                     if (comparison == 0) {
                        BooleanArrays.swap(x, c, d--);
                     }
                  }

                  if (b > c) {
                     s = Math.min(a - this.from, b - a);
                     BooleanArrays.swap(x, this.from, b - s, s);
                     s = Math.min(d - c, this.to - d - 1);
                     BooleanArrays.swap(x, b, this.to - s, s);
                     s = b - a;
                     comparison = d - c;
                     if (s > 1 && comparison > 1) {
                        invokeAll(
                           new BooleanArrays.ForkJoinQuickSort(x, this.from, this.from + s),
                           new BooleanArrays.ForkJoinQuickSort(x, this.to - comparison, this.to)
                        );
                     } else if (s > 1) {
                        invokeAll(new BooleanArrays.ForkJoinQuickSort(x, this.from, this.from + s));
                     } else {
                        invokeAll(new BooleanArrays.ForkJoinQuickSort(x, this.to - comparison, this.to));
                     }

                     return;
                  }

                  BooleanArrays.swap(x, b++, c--);
               }

               if (comparison == 0) {
                  BooleanArrays.swap(x, a++, b);
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
      private final boolean[] x;
      private final boolean[] y;

      public ForkJoinQuickSort2(boolean[] x, boolean[] y, int from, int to) {
         this.from = from;
         this.to = to;
         this.x = x;
         this.y = y;
      }

      @Override
      protected void compute() {
         boolean[] x = this.x;
         boolean[] y = this.y;
         int len = this.to - this.from;
         if (len < 8192) {
            BooleanArrays.quickSort(x, y, this.from, this.to);
         } else {
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = BooleanArrays.med3(x, y, l, l + s, l + 2 * s);
            m = BooleanArrays.med3(x, y, m - s, m, m + s);
            n = BooleanArrays.med3(x, y, n - 2 * s, n - s, n);
            m = BooleanArrays.med3(x, y, l, m, n);
            boolean v = x[m];
            boolean w = y[m];
            int a = this.from;
            int b = a;
            int c = this.to - 1;
            int d = c;

            while (true) {
               int comparison;
               int t;
               while (b > c || (comparison = (t = Boolean.compare(x[b], v)) == 0 ? Boolean.compare(y[b], w) : t) > 0) {
                  for (; c >= b && (comparison = (t = Boolean.compare(x[c], v)) == 0 ? Boolean.compare(y[c], w) : t) >= 0; c--) {
                     if (comparison == 0) {
                        BooleanArrays.swap(x, y, c, d--);
                     }
                  }

                  if (b > c) {
                     s = Math.min(a - this.from, b - a);
                     BooleanArrays.swap(x, y, this.from, b - s, s);
                     s = Math.min(d - c, this.to - d - 1);
                     BooleanArrays.swap(x, y, b, this.to - s, s);
                     s = b - a;
                     comparison = d - c;
                     if (s > 1 && comparison > 1) {
                        invokeAll(
                           new BooleanArrays.ForkJoinQuickSort2(x, y, this.from, this.from + s),
                           new BooleanArrays.ForkJoinQuickSort2(x, y, this.to - comparison, this.to)
                        );
                     } else if (s > 1) {
                        invokeAll(new BooleanArrays.ForkJoinQuickSort2(x, y, this.from, this.from + s));
                     } else {
                        invokeAll(new BooleanArrays.ForkJoinQuickSort2(x, y, this.to - comparison, this.to));
                     }

                     return;
                  }

                  BooleanArrays.swap(x, y, b++, c--);
               }

               if (comparison == 0) {
                  BooleanArrays.swap(x, y, a++, b);
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
      private final boolean[] x;
      private final BooleanComparator comp;

      public ForkJoinQuickSortComp(boolean[] x, int from, int to, BooleanComparator comp) {
         this.from = from;
         this.to = to;
         this.x = x;
         this.comp = comp;
      }

      @Override
      protected void compute() {
         boolean[] x = this.x;
         int len = this.to - this.from;
         if (len < 8192) {
            BooleanArrays.quickSort(x, this.from, this.to, this.comp);
         } else {
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = BooleanArrays.med3(x, l, l + s, l + 2 * s, this.comp);
            m = BooleanArrays.med3(x, m - s, m, m + s, this.comp);
            n = BooleanArrays.med3(x, n - 2 * s, n - s, n, this.comp);
            m = BooleanArrays.med3(x, l, m, n, this.comp);
            boolean v = x[m];
            int a = this.from;
            int b = a;
            int c = this.to - 1;
            int d = c;

            while (true) {
               int comparison;
               while (b > c || (comparison = this.comp.compare(x[b], v)) > 0) {
                  for (; c >= b && (comparison = this.comp.compare(x[c], v)) >= 0; c--) {
                     if (comparison == 0) {
                        BooleanArrays.swap(x, c, d--);
                     }
                  }

                  if (b > c) {
                     s = Math.min(a - this.from, b - a);
                     BooleanArrays.swap(x, this.from, b - s, s);
                     s = Math.min(d - c, this.to - d - 1);
                     BooleanArrays.swap(x, b, this.to - s, s);
                     s = b - a;
                     comparison = d - c;
                     if (s > 1 && comparison > 1) {
                        invokeAll(
                           new BooleanArrays.ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp),
                           new BooleanArrays.ForkJoinQuickSortComp(x, this.to - comparison, this.to, this.comp)
                        );
                     } else if (s > 1) {
                        invokeAll(new BooleanArrays.ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp));
                     } else {
                        invokeAll(new BooleanArrays.ForkJoinQuickSortComp(x, this.to - comparison, this.to, this.comp));
                     }

                     return;
                  }

                  BooleanArrays.swap(x, b++, c--);
               }

               if (comparison == 0) {
                  BooleanArrays.swap(x, a++, b);
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
      private final boolean[] x;

      public ForkJoinQuickSortIndirect(int[] perm, boolean[] x, int from, int to) {
         this.from = from;
         this.to = to;
         this.x = x;
         this.perm = perm;
      }

      @Override
      protected void compute() {
         boolean[] x = this.x;
         int len = this.to - this.from;
         if (len < 8192) {
            BooleanArrays.quickSortIndirect(this.perm, x, this.from, this.to);
         } else {
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = BooleanArrays.med3Indirect(this.perm, x, l, l + s, l + 2 * s);
            m = BooleanArrays.med3Indirect(this.perm, x, m - s, m, m + s);
            n = BooleanArrays.med3Indirect(this.perm, x, n - 2 * s, n - s, n);
            m = BooleanArrays.med3Indirect(this.perm, x, l, m, n);
            boolean v = x[this.perm[m]];
            int a = this.from;
            int b = a;
            int c = this.to - 1;
            int d = c;

            while (true) {
               int comparison;
               while (b > c || (comparison = Boolean.compare(x[this.perm[b]], v)) > 0) {
                  for (; c >= b && (comparison = Boolean.compare(x[this.perm[c]], v)) >= 0; c--) {
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
                           new BooleanArrays.ForkJoinQuickSortIndirect(this.perm, x, this.from, this.from + s),
                           new BooleanArrays.ForkJoinQuickSortIndirect(this.perm, x, this.to - comparison, this.to)
                        );
                     } else if (s > 1) {
                        invokeAll(new BooleanArrays.ForkJoinQuickSortIndirect(this.perm, x, this.from, this.from + s));
                     } else {
                        invokeAll(new BooleanArrays.ForkJoinQuickSortIndirect(this.perm, x, this.to - comparison, this.to));
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
}
