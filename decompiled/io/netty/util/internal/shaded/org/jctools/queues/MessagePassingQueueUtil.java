package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;

public final class MessagePassingQueueUtil {
   public static <E> int drain(MessagePassingQueue<E> queue, MessagePassingQueue.Consumer<E> c, int limit) {
      if (null == c) {
         throw new IllegalArgumentException("c is null");
      } else if (limit < 0) {
         throw new IllegalArgumentException("limit is negative: " + limit);
      } else if (limit == 0) {
         return 0;
      } else {
         E e;
         int i;
         for (i = 0; i < limit && (e = queue.relaxedPoll()) != null; i++) {
            c.accept(e);
         }

         return i;
      }
   }

   public static <E> int drain(MessagePassingQueue<E> queue, MessagePassingQueue.Consumer<E> c) {
      if (null == c) {
         throw new IllegalArgumentException("c is null");
      } else {
         int i = 0;

         E e;
         while ((e = queue.relaxedPoll()) != null) {
            i++;
            c.accept(e);
         }

         return i;
      }
   }

   public static <E> void drain(
      MessagePassingQueue<E> queue, MessagePassingQueue.Consumer<E> c, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit
   ) {
      if (null == c) {
         throw new IllegalArgumentException("c is null");
      } else if (null == wait) {
         throw new IllegalArgumentException("wait is null");
      } else if (null == exit) {
         throw new IllegalArgumentException("exit condition is null");
      } else {
         int idleCounter = 0;

         while (exit.keepRunning()) {
            E e = queue.relaxedPoll();
            if (e == null) {
               idleCounter = wait.idle(idleCounter);
            } else {
               idleCounter = 0;
               c.accept(e);
            }
         }
      }
   }

   public static <E> void fill(
      MessagePassingQueue<E> q, MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit
   ) {
      if (null == wait) {
         throw new IllegalArgumentException("waiter is null");
      } else if (null == exit) {
         throw new IllegalArgumentException("exit condition is null");
      } else {
         int idleCounter = 0;

         while (exit.keepRunning()) {
            if (q.fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH) == 0) {
               idleCounter = wait.idle(idleCounter);
            } else {
               idleCounter = 0;
            }
         }
      }
   }

   public static <E> int fillBounded(MessagePassingQueue<E> q, MessagePassingQueue.Supplier<E> s) {
      return fillInBatchesToLimit(q, s, PortableJvmInfo.RECOMENDED_OFFER_BATCH, q.capacity());
   }

   public static <E> int fillInBatchesToLimit(MessagePassingQueue<E> q, MessagePassingQueue.Supplier<E> s, int batch, int limit) {
      long result = 0L;

      do {
         int filled = q.fill(s, batch);
         if (filled == 0) {
            return (int)result;
         }

         result += filled;
      } while (result <= limit);

      return (int)result;
   }

   public static <E> int fillUnbounded(MessagePassingQueue<E> q, MessagePassingQueue.Supplier<E> s) {
      return fillInBatchesToLimit(q, s, PortableJvmInfo.RECOMENDED_OFFER_BATCH, 4096);
   }
}
