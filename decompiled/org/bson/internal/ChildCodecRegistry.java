package org.bson.internal;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;

class ChildCodecRegistry<T> implements CodecRegistry {
   private final ChildCodecRegistry<?> parent;
   private final CycleDetectingCodecRegistry registry;
   private final Class<T> codecClass;

   ChildCodecRegistry(CycleDetectingCodecRegistry registry, Class<T> codecClass) {
      this.codecClass = codecClass;
      this.parent = null;
      this.registry = registry;
   }

   private ChildCodecRegistry(ChildCodecRegistry<?> parent, Class<T> codecClass) {
      this.parent = parent;
      this.codecClass = codecClass;
      this.registry = parent.registry;
   }

   public Class<T> getCodecClass() {
      return this.codecClass;
   }

   @Override
   public <U> Codec<U> get(Class<U> clazz) {
      return (Codec<U>)(this.hasCycles(clazz) ? new LazyCodec<>(this.registry, clazz) : this.registry.get(new ChildCodecRegistry<>(this, clazz)));
   }

   @Override
   public <U> Codec<U> get(Class<U> clazz, CodecRegistry registry) {
      return this.registry.get(clazz, registry);
   }

   private <U> Boolean hasCycles(Class<U> theClass) {
      for (ChildCodecRegistry current = this; current != null; current = current.parent) {
         if (current.codecClass.equals(theClass)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ChildCodecRegistry<?> that = (ChildCodecRegistry<?>)o;
         if (!this.codecClass.equals(that.codecClass)) {
            return false;
         } else {
            return (this.parent != null ? this.parent.equals(that.parent) : that.parent == null) ? this.registry.equals(that.registry) : false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.parent != null ? this.parent.hashCode() : 0;
      result = 31 * result + this.registry.hashCode();
      return 31 * result + this.codecClass.hashCode();
   }
}
