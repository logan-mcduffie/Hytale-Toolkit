package io.sentry;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class DisabledQueue<E> extends AbstractCollection<E> implements Queue<E>, Serializable {
   private static final long serialVersionUID = -8423413834657610417L;

   public DisabledQueue() {
   }

   @Override
   public int size() {
      return 0;
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public void clear() {
   }

   @Override
   public boolean add(@NotNull E element) {
      return false;
   }

   @Override
   public boolean offer(@NotNull E element) {
      return false;
   }

   @Nullable
   @Override
   public E poll() {
      return null;
   }

   @Nullable
   @Override
   public E element() {
      return null;
   }

   @Nullable
   @Override
   public E peek() {
      return null;
   }

   @NotNull
   @Override
   public E remove() {
      throw new NoSuchElementException("queue is disabled");
   }

   @NotNull
   @Override
   public Iterator<E> iterator() {
      return new Iterator<E>() {
         @Override
         public boolean hasNext() {
            return false;
         }

         @Override
         public E next() {
            throw new NoSuchElementException();
         }

         @Override
         public void remove() {
            throw new IllegalStateException();
         }
      };
   }
}
