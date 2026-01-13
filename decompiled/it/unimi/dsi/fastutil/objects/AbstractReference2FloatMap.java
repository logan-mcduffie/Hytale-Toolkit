package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public abstract class AbstractReference2FloatMap<K> extends AbstractReference2FloatFunction<K> implements Reference2FloatMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractReference2FloatMap() {
   }

   @Override
   public boolean containsKey(Object k) {
      ObjectIterator<Reference2FloatMap.Entry<K>> i = this.reference2FloatEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(float v) {
      ObjectIterator<Reference2FloatMap.Entry<K>> i = this.reference2FloatEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getFloatValue() == v) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public ReferenceSet<K> keySet() {
      return new AbstractReferenceSet<K>() {
         @Override
         public boolean contains(Object k) {
            return AbstractReference2FloatMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractReference2FloatMap.this.size();
         }

         @Override
         public void clear() {
            AbstractReference2FloatMap.this.clear();
         }

         @Override
         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Reference2FloatMap.Entry<K>> i = Reference2FloatMaps.fastIterator(AbstractReference2FloatMap.this);

               @Override
               public K next() {
                  return this.i.next().getKey();
               }

               @Override
               public boolean hasNext() {
                  return this.i.hasNext();
               }

               @Override
               public void remove() {
                  this.i.remove();
               }

               @Override
               public void forEachRemaining(Consumer<? super K> action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getKey()));
               }
            };
         }

         @Override
         public ObjectSpliterator<K> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2FloatMap.this), 65);
         }
      };
   }

   @Override
   public FloatCollection values() {
      return new AbstractFloatCollection() {
         @Override
         public boolean contains(float k) {
            return AbstractReference2FloatMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractReference2FloatMap.this.size();
         }

         @Override
         public void clear() {
            AbstractReference2FloatMap.this.clear();
         }

         @Override
         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Reference2FloatMap.Entry<K>> i = Reference2FloatMaps.fastIterator(AbstractReference2FloatMap.this);

               @Override
               public float nextFloat() {
                  return this.i.next().getFloatValue();
               }

               @Override
               public boolean hasNext() {
                  return this.i.hasNext();
               }

               @Override
               public void remove() {
                  this.i.remove();
               }

               @Override
               public void forEachRemaining(FloatConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getFloatValue()));
               }
            };
         }

         @Override
         public FloatSpliterator spliterator() {
            return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2FloatMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends K, ? extends Float> m) {
      if (m instanceof Reference2FloatMap) {
         ObjectIterator<Reference2FloatMap.Entry<K>> i = Reference2FloatMaps.fastIterator((Reference2FloatMap<K>)m);

         while (i.hasNext()) {
            Reference2FloatMap.Entry<? extends K> e = i.next();
            this.put((K)e.getKey(), e.getFloatValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends K, ? extends Float>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends K, ? extends Float> e = (Entry<? extends K, ? extends Float>)i.next();
            this.put((K)e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Reference2FloatMap.Entry<K>> i = Reference2FloatMaps.fastIterator(this);

      while (n-- != 0) {
         h += i.next().hashCode();
      }

      return h;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Map)) {
         return false;
      } else {
         Map<?, ?> m = (Map<?, ?>)o;
         return m.size() != this.size() ? false : this.reference2FloatEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Reference2FloatMap.Entry<K>> i = Reference2FloatMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Reference2FloatMap.Entry<K> e = i.next();
         if (this == e.getKey()) {
            s.append("(this map)");
         } else {
            s.append(String.valueOf(e.getKey()));
         }

         s.append("=>");
         s.append(String.valueOf(e.getFloatValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry<K> implements Reference2FloatMap.Entry<K> {
      protected K key;
      protected float value;

      public BasicEntry() {
      }

      public BasicEntry(K key, Float value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(K key, float value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public K getKey() {
         return this.key;
      }

      @Override
      public float getFloatValue() {
         return this.value;
      }

      @Override
      public float setValue(float value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2FloatMap.Entry) {
            Reference2FloatMap.Entry<K> e = (Reference2FloatMap.Entry<K>)o;
            return this.key == e.getKey() && Float.floatToIntBits(this.value) == Float.floatToIntBits(e.getFloatValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            Object value = e.getValue();
            return value != null && value instanceof Float ? this.key == key && Float.floatToIntBits(this.value) == Float.floatToIntBits((Float)value) : false;
         }
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(this.key) ^ HashCommon.float2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Reference2FloatMap.Entry<K>> {
      protected final Reference2FloatMap<K> map;

      public BasicEntrySet(Reference2FloatMap<K> map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2FloatMap.Entry) {
            Reference2FloatMap.Entry<K> e = (Reference2FloatMap.Entry<K>)o;
            K k = e.getKey();
            return this.map.containsKey(k) && Float.floatToIntBits(this.map.getFloat(k)) == Float.floatToIntBits(e.getFloatValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object value = e.getValue();
            return value != null && value instanceof Float
               ? this.map.containsKey(k) && Float.floatToIntBits(this.map.getFloat(k)) == Float.floatToIntBits((Float)value)
               : false;
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2FloatMap.Entry) {
            Reference2FloatMap.Entry<K> e = (Reference2FloatMap.Entry<K>)o;
            return this.map.remove(e.getKey(), e.getFloatValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value != null && value instanceof Float) {
               float v = (Float)value;
               return this.map.remove(k, v);
            } else {
               return false;
            }
         }
      }

      @Override
      public int size() {
         return this.map.size();
      }

      @Override
      public ObjectSpliterator<Reference2FloatMap.Entry<K>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
