package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class AtomicQueueUtil {
   public static <E> E lvRefElement(AtomicReferenceArray<E> buffer, int offset) {
      return buffer.get(offset);
   }

   public static <E> E lpRefElement(AtomicReferenceArray<E> buffer, int offset) {
      return buffer.get(offset);
   }

   public static <E> void spRefElement(AtomicReferenceArray<E> buffer, int offset, E value) {
      buffer.lazySet(offset, value);
   }

   public static void soRefElement(AtomicReferenceArray buffer, int offset, Object value) {
      buffer.lazySet(offset, value);
   }

   public static <E> void svRefElement(AtomicReferenceArray<E> buffer, int offset, E value) {
      buffer.set(offset, value);
   }

   public static int calcRefElementOffset(long index) {
      return (int)index;
   }

   public static int calcCircularRefElementOffset(long index, long mask) {
      return (int)(index & mask);
   }

   public static <E> AtomicReferenceArray<E> allocateRefArray(int capacity) {
      return new AtomicReferenceArray<>(capacity);
   }

   public static void spLongElement(AtomicLongArray buffer, int offset, long e) {
      buffer.lazySet(offset, e);
   }

   public static void soLongElement(AtomicLongArray buffer, int offset, long e) {
      buffer.lazySet(offset, e);
   }

   public static long lpLongElement(AtomicLongArray buffer, int offset) {
      return buffer.get(offset);
   }

   public static long lvLongElement(AtomicLongArray buffer, int offset) {
      return buffer.get(offset);
   }

   public static int calcLongElementOffset(long index) {
      return (int)index;
   }

   public static int calcCircularLongElementOffset(long index, int mask) {
      return (int)(index & mask);
   }

   public static AtomicLongArray allocateLongArray(int capacity) {
      return new AtomicLongArray(capacity);
   }

   public static int length(AtomicReferenceArray<?> buf) {
      return buf.length();
   }

   public static int modifiedCalcCircularRefElementOffset(long index, long mask) {
      return (int)(index & mask) >> 1;
   }

   public static int nextArrayOffset(AtomicReferenceArray<?> curr) {
      return length(curr) - 1;
   }
}
