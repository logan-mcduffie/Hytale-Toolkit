package com.google.common.flogger;

import com.google.common.flogger.util.Checks;
import java.util.Iterator;

public class MetadataKey<T> {
   private final String label;
   private final Class<? extends T> clazz;
   private final boolean canRepeat;
   private final long bloomFilterMask;

   public static <T> MetadataKey<T> single(String label, Class<? extends T> clazz) {
      return new MetadataKey<>(label, clazz, false);
   }

   public static <T> MetadataKey<T> repeated(String label, Class<T> clazz) {
      return new MetadataKey<>(label, clazz, true);
   }

   protected MetadataKey(String label, Class<? extends T> clazz, boolean canRepeat) {
      this.label = Checks.checkMetadataIdentifier(label);
      this.clazz = Checks.checkNotNull(clazz, "class");
      this.canRepeat = canRepeat;
      this.bloomFilterMask = this.createBloomFilterMaskFromSystemHashcode();
   }

   public final String getLabel() {
      return this.label;
   }

   public final T cast(Object value) {
      return (T)this.clazz.cast(value);
   }

   public final boolean canRepeat() {
      return this.canRepeat;
   }

   public void emit(T value, MetadataKey.KeyValueHandler out) {
      out.handle(this.getLabel(), value);
   }

   public void emitRepeated(Iterator<T> values, MetadataKey.KeyValueHandler out) {
      Checks.checkState(this.canRepeat, "non repeating key");

      while (values.hasNext()) {
         this.emit(values.next(), out);
      }
   }

   public final long getBloomFilterMask() {
      return this.bloomFilterMask;
   }

   @Override
   public final int hashCode() {
      return super.hashCode();
   }

   @Override
   public final boolean equals(Object obj) {
      return super.equals(obj);
   }

   @Override
   public final String toString() {
      return this.getClass().getName() + "/" + this.label + "[" + this.clazz.getName() + "]";
   }

   private long createBloomFilterMaskFromSystemHashcode() {
      int hash = System.identityHashCode(this);
      long bloom = 0L;

      for (int n = 0; n < 5; n++) {
         bloom |= 1L << (hash & 63);
         hash >>>= 6;
      }

      return bloom;
   }

   public interface KeyValueHandler {
      void handle(String var1, Object var2);
   }
}
