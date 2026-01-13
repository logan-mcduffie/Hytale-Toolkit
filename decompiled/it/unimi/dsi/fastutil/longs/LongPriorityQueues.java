package it.unimi.dsi.fastutil.longs;

import java.io.IOException;
import java.io.ObjectOutputStream;

public final class LongPriorityQueues {
   private LongPriorityQueues() {
   }

   public static LongPriorityQueue synchronize(LongPriorityQueue q) {
      return new LongPriorityQueues.SynchronizedPriorityQueue(q);
   }

   public static LongPriorityQueue synchronize(LongPriorityQueue q, Object sync) {
      return new LongPriorityQueues.SynchronizedPriorityQueue(q, sync);
   }

   public static class SynchronizedPriorityQueue implements LongPriorityQueue {
      protected final LongPriorityQueue q;
      protected final Object sync;

      protected SynchronizedPriorityQueue(LongPriorityQueue q, Object sync) {
         this.q = q;
         this.sync = sync;
      }

      protected SynchronizedPriorityQueue(LongPriorityQueue q) {
         this.q = q;
         this.sync = this;
      }

      @Override
      public void enqueue(long x) {
         synchronized (this.sync) {
            this.q.enqueue(x);
         }
      }

      @Override
      public long dequeueLong() {
         synchronized (this.sync) {
            return this.q.dequeueLong();
         }
      }

      @Override
      public long firstLong() {
         synchronized (this.sync) {
            return this.q.firstLong();
         }
      }

      @Override
      public long lastLong() {
         synchronized (this.sync) {
            return this.q.lastLong();
         }
      }

      @Override
      public boolean isEmpty() {
         synchronized (this.sync) {
            return this.q.isEmpty();
         }
      }

      @Override
      public int size() {
         synchronized (this.sync) {
            return this.q.size();
         }
      }

      @Override
      public void clear() {
         synchronized (this.sync) {
            this.q.clear();
         }
      }

      @Override
      public void changed() {
         synchronized (this.sync) {
            this.q.changed();
         }
      }

      @Override
      public LongComparator comparator() {
         synchronized (this.sync) {
            return this.q.comparator();
         }
      }

      @Deprecated
      @Override
      public void enqueue(Long x) {
         synchronized (this.sync) {
            this.q.enqueue(x);
         }
      }

      @Deprecated
      @Override
      public Long dequeue() {
         synchronized (this.sync) {
            return this.q.dequeue();
         }
      }

      @Deprecated
      @Override
      public Long first() {
         synchronized (this.sync) {
            return this.q.first();
         }
      }

      @Deprecated
      @Override
      public Long last() {
         synchronized (this.sync) {
            return this.q.last();
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.sync) {
            return this.q.hashCode();
         }
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            synchronized (this.sync) {
               return this.q.equals(o);
            }
         }
      }

      private void writeObject(ObjectOutputStream s) throws IOException {
         synchronized (this.sync) {
            s.defaultWriteObject();
         }
      }
   }
}
