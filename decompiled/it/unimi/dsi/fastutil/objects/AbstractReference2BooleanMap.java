package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public abstract class AbstractReference2BooleanMap<K> extends AbstractReference2BooleanFunction<K> implements Reference2BooleanMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractReference2BooleanMap() {
   }

   @Override
   public boolean containsKey(Object k) {
      ObjectIterator<Reference2BooleanMap.Entry<K>> i = this.reference2BooleanEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(boolean v) {
      ObjectIterator<Reference2BooleanMap.Entry<K>> i = this.reference2BooleanEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getBooleanValue() == v) {
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
            return AbstractReference2BooleanMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractReference2BooleanMap.this.size();
         }

         @Override
         public void clear() {
            AbstractReference2BooleanMap.this.clear();
         }

         @Override
         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Reference2BooleanMap.Entry<K>> i = Reference2BooleanMaps.fastIterator(AbstractReference2BooleanMap.this);

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
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2BooleanMap.this), 65);
         }
      };
   }

   @Override
   public BooleanCollection values() {
      return new AbstractBooleanCollection() {
         @Override
         public boolean contains(boolean k) {
            return AbstractReference2BooleanMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractReference2BooleanMap.this.size();
         }

         @Override
         public void clear() {
            AbstractReference2BooleanMap.this.clear();
         }

         @Override
         public BooleanIterator iterator() {
            return new BooleanIterator() {
               private final ObjectIterator<Reference2BooleanMap.Entry<K>> i = Reference2BooleanMaps.fastIterator(AbstractReference2BooleanMap.this);

               @Override
               public boolean nextBoolean() {
                  return this.i.next().getBooleanValue();
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
               public void forEachRemaining(BooleanConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getBooleanValue()));
               }
            };
         }

         @Override
         public BooleanSpliterator spliterator() {
            return BooleanSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2BooleanMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends K, ? extends Boolean> m) {
      if (m instanceof Reference2BooleanMap) {
         ObjectIterator<Reference2BooleanMap.Entry<K>> i = Reference2BooleanMaps.fastIterator((Reference2BooleanMap<K>)m);

         while (i.hasNext()) {
            Reference2BooleanMap.Entry<? extends K> e = i.next();
            this.put((K)e.getKey(), e.getBooleanValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends K, ? extends Boolean>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends K, ? extends Boolean> e = (Entry<? extends K, ? extends Boolean>)i.next();
            this.put((K)e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Reference2BooleanMap.Entry<K>> i = Reference2BooleanMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.reference2BooleanEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Reference2BooleanMap.Entry<K>> i = Reference2BooleanMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Reference2BooleanMap.Entry<K> e = i.next();
         if (this == e.getKey()) {
            s.append("(this map)");
         } else {
            s.append(String.valueOf(e.getKey()));
         }

         s.append("=>");
         s.append(String.valueOf(e.getBooleanValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry<K> implements Reference2BooleanMap.Entry<K> {
      protected K key;
      protected boolean value;

      public BasicEntry() {
      }

      public BasicEntry(K key, Boolean value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(K key, boolean value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public K getKey() {
         return this.key;
      }

      @Override
      public boolean getBooleanValue() {
         return this.value;
      }

      @Override
      public boolean setValue(boolean value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2BooleanMap.Entry) {
            Reference2BooleanMap.Entry<K> e = (Reference2BooleanMap.Entry<K>)o;
            return this.key == e.getKey() && this.value == e.getBooleanValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            Object value = e.getValue();
            return value != null && value instanceof Boolean ? this.key == key && this.value == (Boolean)value : false;
         }
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(this.key) ^ (this.value ? 1231 : 1237);
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Reference2BooleanMap.Entry<K>> {
      protected final Reference2BooleanMap<K> map;

      public BasicEntrySet(Reference2BooleanMap<K> map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2BooleanMap.Entry) {
            Reference2BooleanMap.Entry<K> e = (Reference2BooleanMap.Entry<K>)o;
            K k = e.getKey();
            return this.map.containsKey(k) && this.map.getBoolean(k) == e.getBooleanValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object value = e.getValue();
            return value != null && value instanceof Boolean ? this.map.containsKey(k) && this.map.getBoolean(k) == (Boolean)value : false;
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2BooleanMap.Entry) {
            Reference2BooleanMap.Entry<K> e = (Reference2BooleanMap.Entry<K>)o;
            return this.map.remove(e.getKey(), e.getBooleanValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value != null && value instanceof Boolean) {
               boolean v = (Boolean)value;
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
      public ObjectSpliterator<Reference2BooleanMap.Entry<K>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
