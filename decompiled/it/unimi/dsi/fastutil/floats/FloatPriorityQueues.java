package it.unimi.dsi.fastutil.floats;

import java.io.IOException;
import java.io.ObjectOutputStream;

public final class FloatPriorityQueues {
   private FloatPriorityQueues() {
   }

   public static FloatPriorityQueue synchronize(FloatPriorityQueue q) {
      return new FloatPriorityQueues.SynchronizedPriorityQueue(q);
   }

   public static FloatPriorityQueue synchronize(FloatPriorityQueue q, Object sync) {
      return new FloatPriorityQueues.SynchronizedPriorityQueue(q, sync);
   }

   public static class SynchronizedPriorityQueue implements FloatPriorityQueue {
      protected final FloatPriorityQueue q;
      protected final Object sync;

      protected SynchronizedPriorityQueue(FloatPriorityQueue q, Object sync) {
         this.q = q;
         this.sync = sync;
      }

      protected SynchronizedPriorityQueue(FloatPriorityQueue q) {
         this.q = q;
         this.sync = this;
      }

      @Override
      public void enqueue(float x) {
         synchronized (this.sync) {
            this.q.enqueue(x);
         }
      }

      @Override
      public float dequeueFloat() {
         synchronized (this.sync) {
            return this.q.dequeueFloat();
         }
      }

      @Override
      public float firstFloat() {
         synchronized (this.sync) {
            return this.q.firstFloat();
         }
      }

      @Override
      public float lastFloat() {
         synchronized (this.sync) {
            return this.q.lastFloat();
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
      public FloatComparator comparator() {
         synchronized (this.sync) {
            return this.q.comparator();
         }
      }

      @Deprecated
      @Override
      public void enqueue(Float x) {
         synchronized (this.sync) {
            this.q.enqueue(x);
         }
      }

      @Deprecated
      @Override
      public Float dequeue() {
         synchronized (this.sync) {
            return this.q.dequeue();
         }
      }

      @Deprecated
      @Override
      public Float first() {
         synchronized (this.sync) {
            return this.q.first();
         }
      }

      @Deprecated
      @Override
      public Float last() {
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
