package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public final class ObjectBigArrays {
   public static final Object[][] EMPTY_BIG_ARRAY = new Object[0][];
   public static final Object[][] DEFAULT_EMPTY_BIG_ARRAY = new Object[0][];
   public static final Hash.Strategy HASH_STRATEGY = new ObjectBigArrays.BigArrayHashStrategy();
   private static final int QUICKSORT_NO_REC = 7;
   private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
   private static final int MEDIUM = 40;

   private ObjectBigArrays() {
   }

   @Deprecated
   public static <K> K get(K[][] array, long index) {
      return array[BigArrays.segment(index)][BigArrays.displacement(index)];
   }

   @Deprecated
   public static <K> void set(K[][] array, long index, K value) {
      array[BigArrays.segment(index)][BigArrays.displacement(index)] = value;
   }

   @Deprecated
   public static <K> void swap(K[][] array, long first, long second) {
      K t = array[BigArrays.segment(first)][BigArrays.displacement(first)];
      array[BigArrays.segment(first)][BigArrays.displacement(first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
      array[BigArrays.segment(second)][BigArrays.displacement(second)] = t;
   }

   @Deprecated
   public static <K> long length(K[][] array) {
      int length = array.length;
      return length == 0 ? 0L : BigArrays.start(length - 1) + array[length - 1].length;
   }

   @Deprecated
   public static <K> void copy(K[][] srcArray, long srcPos, K[][] destArray, long destPos, long length) {
      BigArrays.copy(srcArray, srcPos, destArray, destPos, length);
   }

   @Deprecated
   public static <K> void copyFromBig(K[][] srcArray, long srcPos, K[] destArray, int destPos, int length) {
      BigArrays.copyFromBig(srcArray, srcPos, destArray, destPos, length);
   }

   @Deprecated
   public static <K> void copyToBig(K[] srcArray, int srcPos, K[][] destArray, long destPos, long length) {
      BigArrays.copyToBig(srcArray, srcPos, destArray, destPos, length);
   }

   public static <K> K[][] newBigArray(K[][] prototype, long length) {
      return (K[][])newBigArray(prototype.getClass().getComponentType(), length);
   }

   public static Object[][] newBigArray(Class<?> componentType, long length) {
      if (length == 0L && componentType == Object[].class) {
         return EMPTY_BIG_ARRAY;
      } else {
         BigArrays.ensureLength(length);
         int baseLength = (int)(length + 134217727L >>> 27);
         Object[][] base = (Object[][])Array.newInstance(componentType, baseLength);
         int residual = (int)(length & 134217727L);
         if (residual != 0) {
            for (int i = 0; i < baseLength - 1; i++) {
               base[i] = (Object[])Array.newInstance(componentType.getComponentType(), 134217728);
            }

            base[baseLength - 1] = (Object[])Array.newInstance(componentType.getComponentType(), residual);
         } else {
            for (int i = 0; i < baseLength; i++) {
               base[i] = (Object[])Array.newInstance(componentType.getComponentType(), 134217728);
            }
         }

         return base;
      }
   }

   public static Object[][] newBigArray(long length) {
      if (length == 0L) {
         return EMPTY_BIG_ARRAY;
      } else {
         BigArrays.ensureLength(length);
         int baseLength = (int)(length + 134217727L >>> 27);
         Object[][] base = new Object[baseLength][];
         int residual = (int)(length & 134217727L);
         if (residual != 0) {
            for (int i = 0; i < baseLength - 1; i++) {
               base[i] = new Object[134217728];
            }

            base[baseLength - 1] = new Object[residual];
         } else {
            for (int i = 0; i < baseLength; i++) {
               base[i] = new Object[134217728];
            }
         }

         return base;
      }
   }

   @Deprecated
   public static <K> K[][] wrap(K[] array) {
      return (K[][])BigArrays.wrap(array);
   }

   @Deprecated
   public static <K> K[][] ensureCapacity(K[][] array, long length) {
      return (K[][])ensureCapacity(array, length, length(array));
   }

   @Deprecated
   public static <K> K[][] forceCapacity(K[][] array, long length, long preserve) {
      return (K[][])BigArrays.forceCapacity(array, length, preserve);
   }

   @Deprecated
   public static <K> K[][] ensureCapacity(K[][] array, long length, long preserve) {
      return (K[][])(length > length(array) ? forceCapacity(array, length, preserve) : array);
   }

   @Deprecated
   public static <K> K[][] grow(K[][] array, long length) {
      long oldLength = length(array);
      return (K[][])(length > oldLength ? grow(array, length, oldLength) : array);
   }

   @Deprecated
   public static <K> K[][] grow(K[][] array, long length, long preserve) {
      long oldLength = length(array);
      return (K[][])(length > oldLength ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1), length), preserve) : array);
   }

   @Deprecated
   public static <K> K[][] trim(K[][] array, long length) {
      return (K[][])BigArrays.trim(array, length);
   }

   @Deprecated
   public static <K> K[][] setLength(K[][] array, long length) {
      return (K[][])BigArrays.setLength(array, length);
   }

   @Deprecated
   public static <K> K[][] copy(K[][] array, long offset, long length) {
      return (K[][])BigArrays.copy(array, offset, length);
   }

   @Deprecated
   public static <K> K[][] copy(K[][] array) {
      return (K[][])BigArrays.copy(array);
   }

   @Deprecated
   public static <K> void fill(K[][] array, K value) {
      int i = array.length;

      while (i-- != 0) {
         Arrays.fill(array[i], value);
      }
   }

   @Deprecated
   public static <K> void fill(K[][] array, long from, long to, K value) {
      BigArrays.fill(array, from, to, value);
   }

   @Deprecated
   public static <K> boolean equals(K[][] a1, K[][] a2) {
      return BigArrays.equals(a1, a2);
   }

   @Deprecated
   public static <K> String toString(K[][] a) {
      return BigArrays.toString(a);
   }

   @Deprecated
   public static <K> void ensureFromTo(K[][] a, long from, long to) {
      BigArrays.ensureFromTo(length(a), from, to);
   }

   @Deprecated
   public static <K> void ensureOffsetLength(K[][] a, long offset, long length) {
      BigArrays.ensureOffsetLength(length(a), offset, length);
   }

   @Deprecated
   public static <K> void ensureSameLength(K[][] a, K[][] b) {
      if (length(a) != length(b)) {
         throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b));
      }
   }

   private static ForkJoinPool getPool() {
      ForkJoinPool current = ForkJoinTask.getPool();
      return current == null ? ForkJoinPool.commonPool() : current;
   }

   private static <K> void swap(K[][] x, long a, long b, long n) {
      int i = 0;

      while (i < n) {
         BigArrays.swap(x, a, b);
         i++;
         a++;
         b++;
      }
   }

   private static <K> long med3(K[][] x, long a, long b, long c, Comparator<K> comp) {
      int ab = comp.compare(BigArrays.get(x, a), BigArrays.get(x, b));
      int ac = comp.compare(BigArrays.get(x, a), BigArrays.get(x, c));
      int bc = comp.compare(BigArrays.get(x, b), BigArrays.get(x, c));
      return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
   }

   private static <K> void selectionSort(K[][] a, long from, long to, Comparator<K> comp) {
      for (long i = from; i < to - 1L; i++) {
         long m = i;

         for (long j = i + 1L; j < to; j++) {
            if (comp.compare(BigArrays.get(a, j), BigArrays.get(a, m)) < 0) {
               m = j;
            }
         }

         if (m != i) {
            BigArrays.swap(a, i, m);
         }
      }
   }

   public static <K> void quickSort(K[][] x, long from, long to, Comparator<K> comp) {
      long len = to - from;
      if (len < 7L) {
         selectionSort(x, from, to, comp);
      } else {
         long m = from + len / 2L;
         if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
               long s = len / 8L;
               l = med3(x, from, from + s, from + 2L * s, comp);
               m = med3(x, m - s, m, m + s, comp);
               n = med3(x, n - 2L * s, n - s, n, comp);
            }

            m = med3(x, l, m, n, comp);
         }

         K v = BigArrays.get(x, m);
         long a = from;
         long b = from;
         long c = to - 1L;
         long d = c;

         while (true) {
            int comparison;
            while (b > c || (comparison = comp.compare(BigArrays.get(x, b), v)) > 0) {
               for (; c >= b && (comparison = comp.compare(BigArrays.get(x, c), v)) >= 0; c--) {
                  if (comparison == 0) {
                     BigArrays.swap(x, c, d--);
                  }
               }

               if (b > c) {
                  long s = Math.min(a - from, b - a);
                  swap(x, from, b - s, s);
                  long var26 = Math.min(d - c, to - d - 1L);
                  swap(x, b, to - var26, var26);
                  long var27;
                  if ((var27 = b - a) > 1L) {
                     quickSort(x, from, from + var27, comp);
                  }

                  long var28;
                  if ((var28 = d - c) > 1L) {
                     quickSort(x, to - var28, to, comp);
                  }

                  return;
               }

               BigArrays.swap(x, b++, c--);
            }

            if (comparison == 0) {
               BigArrays.swap(x, a++, b);
            }

            b++;
         }
      }
   }

   private static <K> long med3(K[][] x, long a, long b, long c) {
      int ab = BigArrays.<Comparable<Object>>get(x, a).compareTo(BigArrays.get(x, b));
      int ac = BigArrays.<Comparable<Object>>get(x, a).compareTo(BigArrays.get(x, c));
      int bc = BigArrays.<Comparable<Object>>get(x, b).compareTo(BigArrays.get(x, c));
      return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
   }

   private static <K> void selectionSort(K[][] a, long from, long to) {
      for (long i = from; i < to - 1L; i++) {
         long m = i;

         for (long j = i + 1L; j < to; j++) {
            if (BigArrays.<Comparable<Object>>get(a, j).compareTo(BigArrays.get(a, m)) < 0) {
               m = j;
            }
         }

         if (m != i) {
            BigArrays.swap(a, i, m);
         }
      }
   }

   public static <K> void quickSort(K[][] x, Comparator<K> comp) {
      quickSort(x, 0L, BigArrays.length(x), comp);
   }

   public static <K> void quickSort(K[][] x, long from, long to) {
      long len = to - from;
      if (len < 7L) {
         selectionSort(x, from, to);
      } else {
         long m = from + len / 2L;
         if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
               long s = len / 8L;
               l = med3(x, from, from + s, from + 2L * s);
               m = med3(x, m - s, m, m + s);
               n = med3(x, n - 2L * s, n - s, n);
            }

            m = med3(x, l, m, n);
         }

         K v = BigArrays.get(x, m);
         long a = from;
         long b = from;
         long c = to - 1L;
         long d = c;

         while (true) {
            int comparison;
            while (b > c || (comparison = BigArrays.<Comparable<K>>get(x, b).compareTo(v)) > 0) {
               for (; c >= b && (comparison = BigArrays.<Comparable<K>>get(x, c).compareTo(v)) >= 0; c--) {
                  if (comparison == 0) {
                     BigArrays.swap(x, c, d--);
                  }
               }

               if (b > c) {
                  long s = Math.min(a - from, b - a);
                  swap(x, from, b - s, s);
                  long var25 = Math.min(d - c, to - d - 1L);
                  swap(x, b, to - var25, var25);
                  long var26;
                  if ((var26 = b - a) > 1L) {
                     quickSort(x, from, from + var26);
                  }

                  long var27;
                  if ((var27 = d - c) > 1L) {
                     quickSort(x, to - var27, to);
                  }

                  return;
               }

               BigArrays.swap(x, b++, c--);
            }

            if (comparison == 0) {
               BigArrays.swap(x, a++, b);
            }

            b++;
         }
      }
   }

   public static <K> void quickSort(K[][] x) {
      quickSort(x, 0L, BigArrays.length(x));
   }

   public static <K> void parallelQuickSort(K[][] x, long from, long to) {
      ForkJoinPool pool = getPool();
      if (to - from >= 8192L && pool.getParallelism() != 1) {
         pool.invoke(new ObjectBigArrays.ForkJoinQuickSort(x, from, to));
      } else {
         quickSort(x, from, to);
      }
   }

   public static <K> void parallelQuickSort(K[][] x) {
      parallelQuickSort(x, 0L, BigArrays.length(x));
   }

   public static <K> void parallelQuickSort(K[][] x, long from, long to, Comparator<K> comp) {
      ForkJoinPool pool = getPool();
      if (to - from >= 8192L && pool.getParallelism() != 1) {
         pool.invoke(new ObjectBigArrays.ForkJoinQuickSortComp<>(x, from, to, comp));
      } else {
         quickSort(x, from, to, comp);
      }
   }

   public static <K> void parallelQuickSort(K[][] x, Comparator<K> comp) {
      parallelQuickSort(x, 0L, BigArrays.length(x), comp);
   }

   public static <K> long binarySearch(K[][] a, long from, long to, K key) {
      to--;

      while (from <= to) {
         long mid = from + to >>> 1;
         K midVal = BigArrays.get(a, mid);
         int cmp = ((Comparable)midVal).compareTo(key);
         if (cmp < 0) {
            from = mid + 1L;
         } else {
            if (cmp <= 0) {
               return mid;
            }

            to = mid - 1L;
         }
      }

      return -(from + 1L);
   }

   public static <K> long binarySearch(K[][] a, Object key) {
      return binarySearch(a, 0L, BigArrays.length(a), key);
   }

   public static <K> long binarySearch(K[][] a, long from, long to, K key, Comparator<K> c) {
      to--;

      while (from <= to) {
         long mid = from + to >>> 1;
         K midVal = BigArrays.get(a, mid);
         int cmp = c.compare(midVal, key);
         if (cmp < 0) {
            from = mid + 1L;
         } else {
            if (cmp <= 0) {
               return mid;
            }

            to = mid - 1L;
         }
      }

      return -(from + 1L);
   }

   public static <K> long binarySearch(K[][] a, K key, Comparator<K> c) {
      return binarySearch(a, 0L, BigArrays.length(a), key, c);
   }

   public static <K> K[][] shuffle(K[][] a, long from, long to, Random random) {
      return (K[][])BigArrays.shuffle(a, from, to, random);
   }

   public static <K> K[][] shuffle(K[][] a, Random random) {
      return (K[][])BigArrays.shuffle(a, random);
   }

   private static final class BigArrayHashStrategy<K> implements Hash.Strategy<K[][]>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      private BigArrayHashStrategy() {
      }

      public int hashCode(K[][] o) {
         return Arrays.deepHashCode(o);
      }

      public boolean equals(K[][] a, K[][] b) {
         return ObjectBigArrays.equals(a, b);
      }
   }

   protected static class ForkJoinQuickSort<K> extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final long from;
      private final long to;
      private final K[][] x;

      public ForkJoinQuickSort(K[][] x, long from, long to) {
         this.from = from;
         this.to = to;
         this.x = x;
      }

      @Override
      protected void compute() {
         K[][] x = this.x;
         long len = this.to - this.from;
         if (len < 8192L) {
            ObjectBigArrays.quickSort(x, this.from, this.to);
         } else {
            long m = this.from + len / 2L;
            long l = this.from;
            long n = this.to - 1L;
            long s = len / 8L;
            l = ObjectBigArrays.med3(x, l, l + s, l + 2L * s);
            m = ObjectBigArrays.med3(x, m - s, m, m + s);
            n = ObjectBigArrays.med3(x, n - 2L * s, n - s, n);
            m = ObjectBigArrays.med3(x, l, m, n);
            K v = BigArrays.get(x, m);
            long a = this.from;
            long b = a;
            long c = this.to - 1L;
            long d = c;

            while (true) {
               int comparison;
               while (b > c || (comparison = BigArrays.<Comparable<K>>get(x, b).compareTo(v)) > 0) {
                  for (; c >= b && (comparison = BigArrays.<Comparable<K>>get(x, c).compareTo(v)) >= 0; c--) {
                     if (comparison == 0) {
                        BigArrays.swap(x, c, d--);
                     }
                  }

                  if (b > c) {
                     s = Math.min(a - this.from, b - a);
                     ObjectBigArrays.swap(x, this.from, b - s, s);
                     s = Math.min(d - c, this.to - d - 1L);
                     ObjectBigArrays.swap(x, b, this.to - s, s);
                     s = b - a;
                     long t = d - c;
                     if (s > 1L && t > 1L) {
                        invokeAll(
                           new ObjectBigArrays.ForkJoinQuickSort(x, this.from, this.from + s), new ObjectBigArrays.ForkJoinQuickSort(x, this.to - t, this.to)
                        );
                     } else if (s > 1L) {
                        invokeAll(new ObjectBigArrays.ForkJoinQuickSort(x, this.from, this.from + s));
                     } else {
                        invokeAll(new ObjectBigArrays.ForkJoinQuickSort(x, this.to - t, this.to));
                     }

                     return;
                  }

                  BigArrays.swap(x, b++, c--);
               }

               if (comparison == 0) {
                  BigArrays.swap(x, a++, b);
               }

               b++;
            }
         }
      }
   }

   protected static class ForkJoinQuickSortComp<K> extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final long from;
      private final long to;
      private final K[][] x;
      private final Comparator<K> comp;

      public ForkJoinQuickSortComp(K[][] x, long from, long to, Comparator<K> comp) {
         this.from = from;
         this.to = to;
         this.x = x;
         this.comp = comp;
      }

      @Override
      protected void compute() {
         K[][] x = this.x;
         long len = this.to - this.from;
         if (len < 8192L) {
            ObjectBigArrays.quickSort(x, this.from, this.to, this.comp);
         } else {
            long m = this.from + len / 2L;
            long l = this.from;
            long n = this.to - 1L;
            long s = len / 8L;
            l = ObjectBigArrays.med3(x, l, l + s, l + 2L * s, this.comp);
            m = ObjectBigArrays.med3(x, m - s, m, m + s, this.comp);
            n = ObjectBigArrays.med3(x, n - 2L * s, n - s, n, this.comp);
            m = ObjectBigArrays.med3(x, l, m, n, this.comp);
            K v = BigArrays.get(x, m);
            long a = this.from;
            long b = a;
            long c = this.to - 1L;
            long d = c;

            while (true) {
               int comparison;
               while (b > c || (comparison = this.comp.compare(BigArrays.get(x, b), v)) > 0) {
                  for (; c >= b && (comparison = this.comp.compare(BigArrays.get(x, c), v)) >= 0; c--) {
                     if (comparison == 0) {
                        BigArrays.swap(x, c, d--);
                     }
                  }

                  if (b > c) {
                     s = Math.min(a - this.from, b - a);
                     ObjectBigArrays.swap(x, this.from, b - s, s);
                     s = Math.min(d - c, this.to - d - 1L);
                     ObjectBigArrays.swap(x, b, this.to - s, s);
                     s = b - a;
                     long t = d - c;
                     if (s > 1L && t > 1L) {
                        invokeAll(
                           new ObjectBigArrays.ForkJoinQuickSortComp<>(x, this.from, this.from + s, this.comp),
                           new ObjectBigArrays.ForkJoinQuickSortComp<>(x, this.to - t, this.to, this.comp)
                        );
                     } else if (s > 1L) {
                        invokeAll(new ObjectBigArrays.ForkJoinQuickSortComp<>(x, this.from, this.from + s, this.comp));
                     } else {
                        invokeAll(new ObjectBigArrays.ForkJoinQuickSortComp<>(x, this.to - t, this.to, this.comp));
                     }

                     return;
                  }

                  BigArrays.swap(x, b++, c--);
               }

               if (comparison == 0) {
                  BigArrays.swap(x, a++, b);
               }

               b++;
            }
         }
      }
   }
}
