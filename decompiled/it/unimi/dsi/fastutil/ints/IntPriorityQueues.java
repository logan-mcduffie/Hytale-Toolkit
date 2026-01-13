package it.unimi.dsi.fastutil.ints;

import java.io.IOException;
import java.io.ObjectOutputStream;

public final class IntPriorityQueues {
   private IntPriorityQueues() {
   }

   public static IntPriorityQueue synchronize(IntPriorityQueue q) {
      return new IntPriorityQueues.SynchronizedPriorityQueue(q);
   }

   public static IntPriorityQueue synchronize(IntPriorityQueue q, Object sync) {
      return new IntPriorityQueues.SynchronizedPriorityQueue(q, sync);
   }

   public static class SynchronizedPriorityQueue implements IntPriorityQueue {
      protected final IntPriorityQueue q;
      protected final Object sync;

      protected SynchronizedPriorityQueue(IntPriorityQueue q, Object sync) {
         this.q = q;
         this.sync = sync;
      }

      protected SynchronizedPriorityQueue(IntPriorityQueue q) {
         this.q = q;
         this.sync = this;
      }

      @Override
      public void enqueue(int x) {
         synchronized (this.sync) {
            this.q.enqueue(x);
         }
      }

      @Override
      public int dequeueInt() {
         synchronized (this.sync) {
            return this.q.dequeueInt();
         }
      }

      @Override
      public int firstInt() {
         synchronized (this.sync) {
            return this.q.firstInt();
         }
      }

      @Override
      public int lastInt() {
         synchronized (this.sync) {
            return this.q.lastInt();
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
      public IntComparator comparator() {
         synchronized (this.sync) {
            return this.q.comparator();
         }
      }

      @Deprecated
      @Override
      public void enqueue(Integer x) {
         synchronized (this.sync) {
            this.q.enqueue(x);
         }
      }

      @Deprecated
      @Override
      public Integer dequeue() {
         synchronized (this.sync) {
            return this.q.dequeue();
         }
      }

      @Deprecated
      @Override
      public Integer first() {
         synchronized (this.sync) {
            return this.q.first();
         }
      }

      @Deprecated
      @Override
      public Integer last() {
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
