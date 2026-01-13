package io.netty.util.internal;

import java.util.Collection;
import java.util.Map;

public final class ObjectUtil {
   private static final float FLOAT_ZERO = 0.0F;
   private static final double DOUBLE_ZERO = 0.0;
   private static final long LONG_ZERO = 0L;
   private static final int INT_ZERO = 0;
   private static final short SHORT_ZERO = 0;

   private ObjectUtil() {
   }

   public static <T> T checkNotNull(T arg, String text) {
      if (arg == null) {
         throw new NullPointerException(text);
      } else {
         return arg;
      }
   }

   public static <T> T[] deepCheckNotNull(String text, T... varargs) {
      if (varargs == null) {
         throw new NullPointerException(text);
      } else {
         for (T element : varargs) {
            if (element == null) {
               throw new NullPointerException(text);
            }
         }

         return varargs;
      }
   }

   public static <T> T checkNotNullWithIAE(T arg, String paramName) throws IllegalArgumentException {
      if (arg == null) {
         throw new IllegalArgumentException("Param '" + paramName + "' must not be null");
      } else {
         return arg;
      }
   }

   public static <T> T checkNotNullArrayParam(T value, int index, String name) throws IllegalArgumentException {
      if (value == null) {
         throw new IllegalArgumentException("Array index " + index + " of parameter '" + name + "' must not be null");
      } else {
         return value;
      }
   }

   public static int checkPositive(int i, String name) {
      if (i <= 0) {
         throw new IllegalArgumentException(name + " : " + i + " (expected: > 0)");
      } else {
         return i;
      }
   }

   public static long checkPositive(long l, String name) {
      if (l <= 0L) {
         throw new IllegalArgumentException(name + " : " + l + " (expected: > 0)");
      } else {
         return l;
      }
   }

   public static double checkPositive(double d, String name) {
      if (d <= 0.0) {
         throw new IllegalArgumentException(name + " : " + d + " (expected: > 0)");
      } else {
         return d;
      }
   }

   public static float checkPositive(float f, String name) {
      if (f <= 0.0F) {
         throw new IllegalArgumentException(name + " : " + f + " (expected: > 0)");
      } else {
         return f;
      }
   }

   public static short checkPositive(short s, String name) {
      if (s <= 0) {
         throw new IllegalArgumentException(name + " : " + s + " (expected: > 0)");
      } else {
         return s;
      }
   }

   public static int checkPositiveOrZero(int i, String name) {
      if (i < 0) {
         throw new IllegalArgumentException(name + " : " + i + " (expected: >= 0)");
      } else {
         return i;
      }
   }

   public static long checkPositiveOrZero(long l, String name) {
      if (l < 0L) {
         throw new IllegalArgumentException(name + " : " + l + " (expected: >= 0)");
      } else {
         return l;
      }
   }

   public static double checkPositiveOrZero(double d, String name) {
      if (d < 0.0) {
         throw new IllegalArgumentException(name + " : " + d + " (expected: >= 0)");
      } else {
         return d;
      }
   }

   public static float checkPositiveOrZero(float f, String name) {
      if (f < 0.0F) {
         throw new IllegalArgumentException(name + " : " + f + " (expected: >= 0)");
      } else {
         return f;
      }
   }

   public static int checkInRange(int i, int start, int end, String name) {
      if (i >= start && i <= end) {
         return i;
      } else {
         throw new IllegalArgumentException(name + ": " + i + " (expected: " + start + "-" + end + ")");
      }
   }

   public static long checkInRange(long l, long start, long end, String name) {
      if (l >= start && l <= end) {
         return l;
      } else {
         throw new IllegalArgumentException(name + ": " + l + " (expected: " + start + "-" + end + ")");
      }
   }

   public static double checkInRange(double d, double start, double end, String name) {
      if (!(d < start) && !(d > end)) {
         return d;
      } else {
         throw new IllegalArgumentException(name + ": " + d + " (expected: " + start + "-" + end + ")");
      }
   }

   public static <T> T[] checkNonEmpty(T[] array, String name) {
      if (((Object[])checkNotNull((T)array, name)).length == 0) {
         throw new IllegalArgumentException("Param '" + name + "' must not be empty");
      } else {
         return array;
      }
   }

   public static byte[] checkNonEmpty(byte[] array, String name) {
      if (checkNotNull(array, name).length == 0) {
         throw new IllegalArgumentException("Param '" + name + "' must not be empty");
      } else {
         return array;
      }
   }

   public static char[] checkNonEmpty(char[] array, String name) {
      if (checkNotNull(array, name).length == 0) {
         throw new IllegalArgumentException("Param '" + name + "' must not be empty");
      } else {
         return array;
      }
   }

   public static <T extends Collection<?>> T checkNonEmpty(T collection, String name) {
      if (checkNotNull(collection, name).isEmpty()) {
         throw new IllegalArgumentException("Param '" + name + "' must not be empty");
      } else {
         return collection;
      }
   }

   public static String checkNonEmpty(String value, String name) {
      if (checkNotNull(value, name).isEmpty()) {
         throw new IllegalArgumentException("Param '" + name + "' must not be empty");
      } else {
         return value;
      }
   }

   public static <K, V, T extends Map<K, V>> T checkNonEmpty(T value, String name) {
      if (checkNotNull(value, name).isEmpty()) {
         throw new IllegalArgumentException("Param '" + name + "' must not be empty");
      } else {
         return value;
      }
   }

   public static CharSequence checkNonEmpty(CharSequence value, String name) {
      if (checkNotNull(value, name).length() == 0) {
         throw new IllegalArgumentException("Param '" + name + "' must not be empty");
      } else {
         return value;
      }
   }

   public static String checkNonEmptyAfterTrim(String value, String name) {
      String trimmed = checkNotNull(value, name).trim();
      return checkNonEmpty(trimmed, name);
   }

   public static int intValue(Integer wrapper, int defaultValue) {
      return wrapper != null ? wrapper : defaultValue;
   }

   public static long longValue(Long wrapper, long defaultValue) {
      return wrapper != null ? wrapper : defaultValue;
   }
}
