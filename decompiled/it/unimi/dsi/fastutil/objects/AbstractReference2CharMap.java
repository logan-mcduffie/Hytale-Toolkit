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
import java.util.Map.Entry;
import java.util.function.Consumer;

public abstract class AbstractReference2CharMap<K> extends AbstractReference2CharFunction<K> implements Reference2CharMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractReference2CharMap() {
   }

   @Override
   public boolean containsKey(Object k) {
      ObjectIterator<Reference2CharMap.Entry<K>> i = this.reference2CharEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(char v) {
      ObjectIterator<Reference2CharMap.Entry<K>> i = this.reference2CharEntrySet().iterator();

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
   public ReferenceSet<K> keySet() {
      return new AbstractReferenceSet<K>() {
         @Override
         public boolean contains(Object k) {
            return AbstractReference2CharMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractReference2CharMap.this.size();
         }

         @Override
         public void clear() {
            AbstractReference2CharMap.this.clear();
         }

         @Override
         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Reference2CharMap.Entry<K>> i = Reference2CharMaps.fastIterator(AbstractReference2CharMap.this);

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
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2CharMap.this), 65);
         }
      };
   }

   @Override
   public CharCollection values() {
      return new AbstractCharCollection() {
         @Override
         public boolean contains(char k) {
            return AbstractReference2CharMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractReference2CharMap.this.size();
         }

         @Override
         public void clear() {
            AbstractReference2CharMap.this.clear();
         }

         @Override
         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Reference2CharMap.Entry<K>> i = Reference2CharMaps.fastIterator(AbstractReference2CharMap.this);

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
            return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2CharMap.this), 320);
         }
      };
   }

   @Override
   public void putAll(Map<? extends K, ? extends Character> m) {
      if (m instanceof Reference2CharMap) {
         ObjectIterator<Reference2CharMap.Entry<K>> i = Reference2CharMaps.fastIterator((Reference2CharMap<K>)m);

         while (i.hasNext()) {
            Reference2CharMap.Entry<? extends K> e = i.next();
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
      ObjectIterator<Reference2CharMap.Entry<K>> i = Reference2CharMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.reference2CharEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Reference2CharMap.Entry<K>> i = Reference2CharMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Reference2CharMap.Entry<K> e = i.next();
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

   public static class BasicEntry<K> implements Reference2CharMap.Entry<K> {
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
         } else if (o instanceof Reference2CharMap.Entry) {
            Reference2CharMap.Entry<K> e = (Reference2CharMap.Entry<K>)o;
            return this.key == e.getKey() && this.value == e.getCharValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            Object value = e.getValue();
            return value != null && value instanceof Character ? this.key == key && this.value == (Character)value : false;
         }
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(this.key) ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Reference2CharMap.Entry<K>> {
      protected final Reference2CharMap<K> map;

      public BasicEntrySet(Reference2CharMap<K> map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Reference2CharMap.Entry) {
            Reference2CharMap.Entry<K> e = (Reference2CharMap.Entry<K>)o;
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
         } else if (o instanceof Reference2CharMap.Entry) {
            Reference2CharMap.Entry<K> e = (Reference2CharMap.Entry<K>)o;
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
      public ObjectSpliterator<Reference2CharMap.Entry<K>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
