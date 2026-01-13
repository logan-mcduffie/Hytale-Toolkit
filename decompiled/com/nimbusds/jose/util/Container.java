package com.nimbusds.jose.util;

import com.nimbusds.jose.shaded.jcip.NotThreadSafe;

@NotThreadSafe
public class Container<T> {
   private T item;

   public Container() {
   }

   public Container(T item) {
      this.item = item;
   }

   public T get() {
      return this.item;
   }

   public void set(T item) {
      this.item = item;
   }
}
