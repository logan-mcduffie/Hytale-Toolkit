package io.netty.util;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractConstant<T extends AbstractConstant<T>> implements Constant<T> {
   private static final AtomicLong uniqueIdGenerator = new AtomicLong();
   private final int id;
   private final String name;
   private final long uniquifier;

   protected AbstractConstant(int id, String name) {
      this.id = id;
      this.name = name;
      this.uniquifier = uniqueIdGenerator.getAndIncrement();
   }

   @Override
   public final String name() {
      return this.name;
   }

   @Override
   public final int id() {
      return this.id;
   }

   @Override
   public final String toString() {
      return this.name();
   }

   @Override
   public final int hashCode() {
      return super.hashCode();
   }

   @Override
   public final boolean equals(Object obj) {
      return super.equals(obj);
   }

   public final int compareTo(T o) {
      if (this == o) {
         return 0;
      } else {
         int returnCode = this.hashCode() - o.hashCode();
         if (returnCode != 0) {
            return returnCode;
         } else if (this.uniquifier < o.uniquifier) {
            return -1;
         } else if (this.uniquifier > o.uniquifier) {
            return 1;
         } else {
            throw new Error("failed to compare two different constants");
         }
      }
   }
}
