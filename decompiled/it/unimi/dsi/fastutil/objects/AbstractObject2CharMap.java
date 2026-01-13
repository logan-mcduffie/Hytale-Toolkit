package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;

public abstract class AbstractObject2CharMap<K> extends AbstractObject2CharFunction<K> implements Object2CharMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractObject2CharMap() {
   }

   @Override
   public boolean containsKey(Object k) {
      ObjectIterator<Object2CharMap.Entry<K>> i = this.object2CharEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(char v) {
      ObjectIterator<Object2CharMap.Entry<K>> i = this.object2CharEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getCharValue() == v) {
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
   public ObjectSet<K> keySet() {
      return new AbstractObjectSet<K>() {
         @Override
         public boolean contains(Object k) {
            return AbstractObject2CharMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractObject2CharMap.this.size();
         }

         @Override
         public void clear() {
            AbstractObject2CharMap.this.clear();
         }

         @Override
         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Object2CharMap.Entry<K>> i = Object2CharMaps.fastIterator(AbstractObject2CharMap.this);

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
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractObject2CharMap.this), 65);
         }
      };
   }

   @Override
   public CharCollection values() {
      return new AbstractCharCollection() {
         @Override
         public boolean contains(char k) {
            return AbstractObject2CharMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractObject2CharMap.this.size();
         }

         @Override
         public void clear() {
            AbstractObject2CharMap.this.clear();
         }

         @Override
         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Object2CharMap.Entry<K>> i = Object2CharMaps.fastIterator(AbstractObject2CharMap.this);

               @Override
               public char nextChar() {
                  return this.i.next().getCharValue();
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
               public void forEachRemaining(CharConsumer action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getCharValue()));
               }
            };
         }

         @Override
         public CharSpliterator spliterator() {
            return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractObject2CharMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends K, ? extends Character> m) {
      if (m instanceof Object2CharMap) {
         ObjectIterator<Object2CharMap.Entry<K>> i = Object2CharMaps.fastIterator((Object2CharMap<K>)m);

         while (i.hasNext()) {
            Object2CharMap.Entry<? extends K> e = i.next();
            this.put((K)e.getKey(), e.getCharValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends K, ? extends Character>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends K, ? extends Character> e = (Entry<? extends K, ? extends Character>)i.next();
            this.put((K)e.getKey(), e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Object2CharMap.Entry<K>> i = Object2CharMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.object2CharEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Object2CharMap.Entry<K>> i = Object2CharMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Object2CharMap.Entry<K> e = i.next();
         if (this == e.getKey()) {
            s.append("(this map)");
         } else {
            s.append(String.valueOf(e.getKey()));
         }

         s.append("=>");
         s.append(String.valueOf(e.getCharValue()));
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry<K> implements Object2CharMap.Entry<K> {
      protected K key;
      protected char value;

      public BasicEntry() {
      }

      public BasicEntry(K key, Character value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(K key, char value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public K getKey() {
         return this.key;
      }

      @Override
      public char getCharValue() {
         return this.value;
      }

      @Override
      public char setValue(char value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Object2CharMap.Entry) {
            Object2CharMap.Entry<K> e = (Object2CharMap.Entry<K>)o;
            return Objects.equals(this.key, e.getKey()) && this.value == e.getCharValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            Object value = e.getValue();
            return value != null && value instanceof Character ? Objects.equals(this.key, key) && this.value == (Character)value : false;
         }
      }

      @Override
      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Object2CharMap.Entry<K>> {
      protected final Object2CharMap<K> map;

      public BasicEntrySet(Object2CharMap<K> map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Object2CharMap.Entry) {
            Object2CharMap.Entry<K> e = (Object2CharMap.Entry<K>)o;
            K k = e.getKey();
            return this.map.containsKey(k) && this.map.getChar(k) == e.getCharValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object value = e.getValue();
            return value != null && value instanceof Character ? this.map.containsKey(k) && this.map.getChar(k) == (Character)value : false;
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Object2CharMap.Entry) {
            Object2CharMap.Entry<K> e = (Object2CharMap.Entry<K>)o;
            return this.map.remove(e.getKey(), e.getCharValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value != null && value instanceof Character) {
               char v = (Character)value;
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
      public ObjectSpliterator<Object2CharMap.Entry<K>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
