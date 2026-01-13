package io.netty.util.internal;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class DefaultPriorityQueue<T extends PriorityQueueNode> extends AbstractQueue<T> implements PriorityQueue<T> {
   private static final PriorityQueueNode[] EMPTY_ARRAY = new PriorityQueueNode[0];
   private final Comparator<T> comparator;
   private T[] queue;
   private int size;

   public DefaultPriorityQueue(Comparator<T> comparator, int initialSize) {
      this.comparator = ObjectUtil.checkNotNull(comparator, "comparator");
      this.queue = (T[])(initialSize != 0 ? new PriorityQueueNode[initialSize] : EMPTY_ARRAY);
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.size == 0;
   }

   @Override
   public boolean contains(Object o) {
      if (!(o instanceof PriorityQueueNode)) {
         return false;
      } else {
         PriorityQueueNode node = (PriorityQueueNode)o;
         return this.contains(node, node.priorityQueueIndex(this));
      }
   }

   public boolean containsTyped(T node) {
      return this.contains(node, node.priorityQueueIndex(this));
   }

   @Override
   public void clear() {
      for (int i = 0; i < this.size; i++) {
         T node = this.queue[i];
         if (node != null) {
            node.priorityQueueIndex(this, -1);
            this.queue[i] = null;
         }
      }

      this.size = 0;
   }

   @Override
   public void clearIgnoringIndexes() {
      this.size = 0;
   }

   public boolean offer(T e) {
      if (e.priorityQueueIndex(this) != -1) {
         throw new IllegalArgumentException("e.priorityQueueIndex(): " + e.priorityQueueIndex(this) + " (expected: " + -1 + ") + e: " + e);
      } else {
         if (this.size >= this.queue.length) {
            this.queue = (T[])Arrays.copyOf(this.queue, this.queue.length + (this.queue.length < 64 ? this.queue.length + 2 : this.queue.length >>> 1));
         }

         this.bubbleUp(this.size++, e);
         return true;
      }
   }

   public T poll() {
      if (this.size == 0) {
         return null;
      } else {
         T result = this.queue[0];
         result.priorityQueueIndex(this, -1);
         T last = this.queue[--this.size];
         this.queue[this.size] = null;
         if (this.size != 0) {
            this.bubbleDown(0, last);
         }

         return result;
      }
   }

   public T peek() {
      return this.size == 0 ? null : this.queue[0];
   }

   @Override
   public boolean remove(Object o) {
      T node;
      try {
         node = (T)o;
      } catch (ClassCastException var4) {
         return false;
      }

      return this.removeTyped(node);
   }

   public boolean removeTyped(T node) {
      int i = node.priorityQueueIndex(this);
      if (!this.contains(node, i)) {
         return false;
      } else {
         node.priorityQueueIndex(this, -1);
         if (--this.size != 0 && this.size != i) {
            T moved = this.queue[i] = this.queue[this.size];
            this.queue[this.size] = null;
            if (this.comparator.compare(node, moved) < 0) {
               this.bubbleDown(i, moved);
            } else {
               this.bubbleUp(i, moved);
            }

            return true;
         } else {
            this.queue[i] = null;
            return true;
         }
      }
   }

   public void priorityChanged(T node) {
      int i = node.priorityQueueIndex(this);
      if (this.contains(node, i)) {
         if (i == 0) {
            this.bubbleDown(i, node);
         } else {
            int iParent = i - 1 >>> 1;
            T parent = this.queue[iParent];
            if (this.comparator.compare(node, parent) < 0) {
               this.bubbleUp(i, node);
            } else {
               this.bubbleDown(i, node);
            }
         }
      }
   }

   @Override
   public Object[] toArray() {
      return Arrays.copyOf(this.queue, this.size);
   }

   @Override
   public <X> X[] toArray(X[] a) {
      if (a.length < this.size) {
         return (X[])Arrays.copyOf(this.queue, this.size, (Class<? extends T[]>)a.getClass());
      } else {
         System.arraycopy(this.queue, 0, a, 0, this.size);
         if (a.length > this.size) {
            a[this.size] = null;
         }

         return a;
      }
   }

   @Override
   public Iterator<T> iterator() {
      return new DefaultPriorityQueue.PriorityQueueIterator();
   }

   private boolean contains(PriorityQueueNode node, int i) {
      return i >= 0 && i < this.size && node.equals(this.queue[i]);
   }

   private void bubbleDown(int k, T node) {
      int half = this.size >>> 1;

      while (k < half) {
         int iChild = (k << 1) + 1;
         T child = this.queue[iChild];
         int rightChild = iChild + 1;
         if (rightChild < this.size && this.comparator.compare(child, this.queue[rightChild]) > 0) {
            iChild = rightChild;
            child = this.queue[rightChild];
         }

         if (this.comparator.compare(node, child) <= 0) {
            break;
         }

         this.queue[k] = child;
         child.priorityQueueIndex(this, k);
         k = iChild;
      }

      this.queue[k] = node;
      node.priorityQueueIndex(this, k);
   }

   private void bubbleUp(int k, T node) {
      while (k > 0) {
         int iParent = k - 1 >>> 1;
         T parent = this.queue[iParent];
         if (this.comparator.compare(node, parent) < 0) {
            this.queue[k] = parent;
            parent.priorityQueueIndex(this, k);
            k = iParent;
            continue;
         }
         break;
      }

      this.queue[k] = node;
      node.priorityQueueIndex(this, k);
   }

   private final class PriorityQueueIterator implements Iterator<T> {
      private int index;

      private PriorityQueueIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.index < DefaultPriorityQueue.this.size;
      }

      public T next() {
         if (this.index >= DefaultPriorityQueue.this.size) {
            throw new NoSuchElementException();
         } else {
            return DefaultPriorityQueue.this.queue[this.index++];
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("remove");
      }
   }
}
