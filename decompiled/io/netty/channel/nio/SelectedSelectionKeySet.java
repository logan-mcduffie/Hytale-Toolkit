package io.netty.channel.nio;

import java.nio.channels.SelectionKey;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class SelectedSelectionKeySet extends AbstractSet<SelectionKey> {
   SelectionKey[] keys = new SelectionKey[1024];
   int size;

   public boolean add(SelectionKey o) {
      if (o == null) {
         return false;
      } else {
         if (this.size == this.keys.length) {
            this.increaseCapacity();
         }

         this.keys[this.size++] = o;
         return true;
      }
   }

   @Override
   public boolean remove(Object o) {
      return false;
   }

   @Override
   public boolean contains(Object o) {
      SelectionKey[] array = this.keys;
      int i = 0;

      for (int s = this.size; i < s; i++) {
         SelectionKey k = array[i];
         if (k.equals(o)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public Iterator<SelectionKey> iterator() {
      return new Iterator<SelectionKey>() {
         private int idx;

         @Override
         public boolean hasNext() {
            return this.idx < SelectedSelectionKeySet.this.size;
         }

         public SelectionKey next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return SelectedSelectionKeySet.this.keys[this.idx++];
            }
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   void reset() {
      this.reset(0);
   }

   void reset(int start) {
      Arrays.fill(this.keys, start, this.size, null);
      this.size = 0;
   }

   private void increaseCapacity() {
      SelectionKey[] newKeys = new SelectionKey[this.keys.length << 1];
      System.arraycopy(this.keys, 0, newKeys, 0, this.size);
      this.keys = newKeys;
   }
}
