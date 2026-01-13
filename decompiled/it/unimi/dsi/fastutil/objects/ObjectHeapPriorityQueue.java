package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.PriorityQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class ObjectHeapPriorityQueue<K> implements PriorityQueue<K>, Serializable {
   private static final long serialVersionUID = 1L;
   protected transient K[] heap = (K[])ObjectArrays.EMPTY_ARRAY;
   protected int size;
   protected Comparator<? super K> c;

   public ObjectHeapPriorityQueue(int capacity, Comparator<? super K> c) {
      if (capacity > 0) {
         this.heap = (K[])(new Object[capacity]);
      }

      this.c = c;
   }

   public ObjectHeapPriorityQueue(int capacity) {
      this(capacity, null);
   }

   public ObjectHeapPriorityQueue(Comparator<? super K> c) {
      this(0, c);
   }

   public ObjectHeapPriorityQueue() {
      this(0, null);
   }

   public ObjectHeapPriorityQueue(K[] a, int size, Comparator<? super K> c) {
      this(c);
      this.heap = a;
      this.size = size;
      ObjectHeaps.makeHeap(a, size, c);
   }

   public ObjectHeapPriorityQueue(K[] a, Comparator<? super K> c) {
      this(a, a.length, c);
   }

   public ObjectHeapPriorityQueue(K[] a, int size) {
      this(a, size, null);
   }

   public ObjectHeapPriorityQueue(K[] a) {
      this(a, a.length);
   }

   public ObjectHeapPriorityQueue(Collection<? extends K> collection, Comparator<? super K> c) {
      this((K[])collection.toArray(), c);
   }

   public ObjectHeapPriorityQueue(Collection<? extends K> collection) {
      this(collection, null);
   }

   @Override
   public void enqueue(K x) {
      if (this.size == this.heap.length) {
         this.heap = (K[])ObjectArrays.grow(this.heap, this.size + 1);
      }

      this.heap[this.size++] = x;
      ObjectHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
   }

   @Override
   public K dequeue() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         K result = this.heap[0];
         this.heap[0] = this.heap[--this.size];
         this.heap[this.size] = null;
         if (this.size != 0) {
            ObjectHeaps.downHeap(this.heap, this.size, 0, this.c);
         }

         return result;
      }
   }

   @Override
   public K first() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.heap[0];
      }
   }

   @Override
   public void changed() {
      ObjectHeaps.downHeap(this.heap, this.size, 0, this.c);
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public void clear() {
      Arrays.fill(this.heap, 0, this.size, null);
      this.size = 0;
   }

   public void trim() {
      this.heap = (K[])ObjectArrays.trim(this.heap, this.size);
   }

   @Override
   public Comparator<? super K> comparator() {
      return this.c;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();
      s.writeInt(this.heap.length);

      for (int i = 0; i < this.size; i++) {
         s.writeObject(this.heap[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.heap = (K[])(new Object[s.readInt()]);

      for (int i = 0; i < this.size; i++) {
         this.heap[i] = (K)s.readObject();
      }
   }
}
