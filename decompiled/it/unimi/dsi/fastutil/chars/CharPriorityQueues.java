package it.unimi.dsi.fastutil.chars;

import java.io.IOException;
import java.io.ObjectOutputStream;

public final class CharPriorityQueues {
   private CharPriorityQueues() {
   }

   public static CharPriorityQueue synchronize(CharPriorityQueue q) {
      return new CharPriorityQueues.SynchronizedPriorityQueue(q);
   }

   public static CharPriorityQueue synchronize(CharPriorityQueue q, Object sync) {
      return new CharPriorityQueues.SynchronizedPriorityQueue(q, sync);
   }

   public static class SynchronizedPriorityQueue implements CharPriorityQueue {
      protected final CharPriorityQueue q;
      protected final Object sync;

      protected SynchronizedPriorityQueue(CharPriorityQueue q, Object sync) {
         this.q = q;
         this.sync = sync;
      }

      protected SynchronizedPriorityQueue(CharPriorityQueue q) {
         this.q = q;
         this.sync = this;
      }

      @Override
      public void enqueue(char x) {
         synchronized (this.sync) {
            this.q.enqueue(x);
         }
      }

      @Override
      public char dequeueChar() {
         synchronized (this.sync) {
            return this.q.dequeueChar();
         }
      }

      @Override
      public char firstChar() {
         synchronized (this.sync) {
            return this.q.firstChar();
         }
      }

      @Override
      public char lastChar() {
         synchronized (this.sync) {
            return this.q.lastChar();
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
      public CharComparator comparator() {
         synchronized (this.sync) {
            return this.q.comparator();
         }
      }

      @Deprecated
      @Override
      public void enqueue(Character x) {
         synchronized (this.sync) {
            this.q.enqueue(x);
         }
      }

      @Deprecated
      @Override
      public Character dequeue() {
         synchronized (this.sync) {
            return this.q.dequeue();
         }
      }

      @Deprecated
      @Override
      public Character first() {
         synchronized (this.sync) {
            return this.q.first();
         }
      }

      @Deprecated
      @Override
      public Character last() {
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
