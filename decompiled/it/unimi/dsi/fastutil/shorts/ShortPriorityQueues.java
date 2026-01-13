package it.unimi.dsi.fastutil.shorts;

import java.io.IOException;
import java.io.ObjectOutputStream;

public final class ShortPriorityQueues {
   private ShortPriorityQueues() {
   }

   public static ShortPriorityQueue synchronize(ShortPriorityQueue q) {
      return new ShortPriorityQueues.SynchronizedPriorityQueue(q);
   }

   public static ShortPriorityQueue synchronize(ShortPriorityQueue q, Object sync) {
      return new ShortPriorityQueues.SynchronizedPriorityQueue(q, sync);
   }

   public static class SynchronizedPriorityQueue implements ShortPriorityQueue {
      protected final ShortPriorityQueue q;
      protected final Object sync;

      protected SynchronizedPriorityQueue(ShortPriorityQueue q, Object sync) {
         this.q = q;
         this.sync = sync;
      }

      protected SynchronizedPriorityQueue(ShortPriorityQueue q) {
         this.q = q;
         this.sync = this;
      }

      @Override
      public void enqueue(short x) {
         synchronized (this.sync) {
            this.q.enqueue(x);
         }
      }

      @Override
      public short dequeueShort() {
         synchronized (this.sync) {
            return this.q.dequeueShort();
         }
      }

      @Override
      public short firstShort() {
         synchronized (this.sync) {
            return this.q.firstShort();
         }
      }

      @Override
      public short lastShort() {
         synchronized (this.sync) {
            return this.q.lastShort();
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
      public ShortComparator comparator() {
         synchronized (this.sync) {
            return this.q.comparator();
         }
      }

      @Deprecated
      @Override
      public void enqueue(Short x) {
         synchronized (this.sync) {
            this.q.enqueue(x);
         }
      }

      @Deprecated
      @Override
      public Short dequeue() {
         synchronized (this.sync) {
            return this.q.dequeue();
         }
      }

      @Deprecated
      @Override
      public Short first() {
         synchronized (this.sync) {
            return this.q.first();
         }
      }

      @Deprecated
      @Override
      public Short last() {
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
