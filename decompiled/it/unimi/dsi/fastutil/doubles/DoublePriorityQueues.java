package it.unimi.dsi.fastutil.doubles;

import java.io.IOException;
import java.io.ObjectOutputStream;

public final class DoublePriorityQueues {
   private DoublePriorityQueues() {
   }

   public static DoublePriorityQueue synchronize(DoublePriorityQueue q) {
      return new DoublePriorityQueues.SynchronizedPriorityQueue(q);
   }

   public static DoublePriorityQueue synchronize(DoublePriorityQueue q, Object sync) {
      return new DoublePriorityQueues.SynchronizedPriorityQueue(q, sync);
   }

   public static class SynchronizedPriorityQueue implements DoublePriorityQueue {
      protected final DoublePriorityQueue q;
      protected final Object sync;

      protected SynchronizedPriorityQueue(DoublePriorityQueue q, Object sync) {
         this.q = q;
         this.sync = sync;
      }

      protected SynchronizedPriorityQueue(DoublePriorityQueue q) {
         this.q = q;
         this.sync = this;
      }

      @Override
      public void enqueue(double x) {
         synchronized (this.sync) {
            this.q.enqueue(x);
         }
      }

      @Override
      public double dequeueDouble() {
         synchronized (this.sync) {
            return this.q.dequeueDouble();
         }
      }

      @Override
      public double firstDouble() {
         synchronized (this.sync) {
            return this.q.firstDouble();
         }
      }

      @Override
      public double lastDouble() {
         synchronized (this.sync) {
            return this.q.lastDouble();
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
      public DoubleComparator comparator() {
         synchronized (this.sync) {
            return this.q.comparator();
         }
      }

      @Deprecated
      @Override
      public void enqueue(Double x) {
         synchronized (this.sync) {
            this.q.enqueue(x);
         }
      }

      @Deprecated
      @Override
      public Double dequeue() {
         synchronized (this.sync) {
            return this.q.dequeue();
         }
      }

      @Deprecated
      @Override
      public Double first() {
         synchronized (this.sync) {
            return this.q.first();
         }
      }

      @Deprecated
      @Override
      public Double last() {
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
