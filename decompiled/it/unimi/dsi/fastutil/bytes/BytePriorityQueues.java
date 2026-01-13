package it.unimi.dsi.fastutil.bytes;

import java.io.IOException;
import java.io.ObjectOutputStream;

public final class BytePriorityQueues {
   private BytePriorityQueues() {
   }

   public static BytePriorityQueue synchronize(BytePriorityQueue q) {
      return new BytePriorityQueues.SynchronizedPriorityQueue(q);
   }

   public static BytePriorityQueue synchronize(BytePriorityQueue q, Object sync) {
      return new BytePriorityQueues.SynchronizedPriorityQueue(q, sync);
   }

   public static class SynchronizedPriorityQueue implements BytePriorityQueue {
      protected final BytePriorityQueue q;
      protected final Object sync;

      protected SynchronizedPriorityQueue(BytePriorityQueue q, Object sync) {
         this.q = q;
         this.sync = sync;
      }

      protected SynchronizedPriorityQueue(BytePriorityQueue q) {
         this.q = q;
         this.sync = this;
      }

      @Override
      public void enqueue(byte x) {
         synchronized (this.sync) {
            this.q.enqueue(x);
         }
      }

      @Override
      public byte dequeueByte() {
         synchronized (this.sync) {
            return this.q.dequeueByte();
         }
      }

      @Override
      public byte firstByte() {
         synchronized (this.sync) {
            return this.q.firstByte();
         }
      }

      @Override
      public byte lastByte() {
         synchronized (this.sync) {
            return this.q.lastByte();
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
      public ByteComparator comparator() {
         synchronized (this.sync) {
            return this.q.comparator();
         }
      }

      @Deprecated
      @Override
      public void enqueue(Byte x) {
         synchronized (this.sync) {
            this.q.enqueue(x);
         }
      }

      @Deprecated
      @Override
      public Byte dequeue() {
         synchronized (this.sync) {
            return this.q.dequeue();
         }
      }

      @Deprecated
      @Override
      public Byte first() {
         synchronized (this.sync) {
            return this.q.first();
         }
      }

      @Deprecated
      @Override
      public Byte last() {
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
