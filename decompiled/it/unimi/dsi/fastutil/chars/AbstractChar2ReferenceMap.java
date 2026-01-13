package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public abstract class AbstractChar2ReferenceMap<V> extends AbstractChar2ReferenceFunction<V> implements Char2ReferenceMap<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractChar2ReferenceMap() {
   }

   @Override
   public boolean containsKey(char k) {
      ObjectIterator<Char2ReferenceMap.Entry<V>> i = this.char2ReferenceEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getCharKey() == k) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(Object v) {
      ObjectIterator<Char2ReferenceMap.Entry<V>> i = this.char2ReferenceEntrySet().iterator();

      while (i.hasNext()) {
         if (i.next().getValue() == v) {
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
   public CharSet keySet() {
      return new AbstractCharSet() {
         @Override
         public boolean contains(char k) {
            return AbstractChar2ReferenceMap.this.containsKey(k);
         }

         @Override
         public int size() {
            return AbstractChar2ReferenceMap.this.size();
         }

         @Override
         public void clear() {
            AbstractChar2ReferenceMap.this.clear();
         }

         @Override
         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Char2ReferenceMap.Entry<V>> i = Char2ReferenceMaps.fastIterator(AbstractChar2ReferenceMap.this);

               @Override
               public char nextChar() {
                  return this.i.next().getCharKey();
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
                  this.i.forEachRemaining(entry -> action.accept(entry.getCharKey()));
               }
            };
         }

         @Override
         public CharSpliterator spliterator() {
            return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractChar2ReferenceMap.this), 321);
         }
      };
   }

   @Override
   public ReferenceCollection<V> values() {
      return new AbstractReferenceCollection<V>() {
         @Override
         public boolean contains(Object k) {
            return AbstractChar2ReferenceMap.this.containsValue(k);
         }

         @Override
         public int size() {
            return AbstractChar2ReferenceMap.this.size();
         }

         @Override
         public void clear() {
            AbstractChar2ReferenceMap.this.clear();
         }

         @Override
         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               private final ObjectIterator<Char2ReferenceMap.Entry<V>> i = Char2ReferenceMaps.fastIterator(AbstractChar2ReferenceMap.this);

               @Override
               public V next() {
                  return this.i.next().getValue();
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
               public void forEachRemaining(Consumer<? super V> action) {
                  this.i.forEachRemaining(entry -> action.accept(entry.getValue()));
               }
            };
         }

         @Override
         public ObjectSpliterator<V> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractChar2ReferenceMap.this), 64);
         }
      };
   }

   @Override
   public void putAll(Map<? extends Character, ? extends V> m) {
      if (m instanceof Char2ReferenceMap) {
         ObjectIterator<Char2ReferenceMap.Entry<V>> i = Char2ReferenceMaps.fastIterator((Char2ReferenceMap<V>)m);

         while (i.hasNext()) {
            Char2ReferenceMap.Entry<? extends V> e = i.next();
            this.put(e.getCharKey(), (V)e.getValue());
         }
      } else {
         int n = m.size();
         Iterator<? extends Entry<? extends Character, ? extends V>> i = m.entrySet().iterator();

         while (n-- != 0) {
            Entry<? extends Character, ? extends V> e = (Entry<? extends Character, ? extends V>)i.next();
            this.put(e.getKey(), (V)e.getValue());
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<Char2ReferenceMap.Entry<V>> i = Char2ReferenceMaps.fastIterator(this);

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
         return m.size() != this.size() ? false : this.char2ReferenceEntrySet().containsAll(m.entrySet());
      }
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Char2ReferenceMap.Entry<V>> i = Char2ReferenceMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Char2ReferenceMap.Entry<V> e = i.next();
         s.append(String.valueOf(e.getCharKey()));
         s.append("=>");
         if (this == e.getValue()) {
            s.append("(this map)");
         } else {
            s.append(String.valueOf(e.getValue()));
         }
      }

      s.append("}");
      return s.toString();
   }

   public static class BasicEntry<V> implements Char2ReferenceMap.Entry<V> {
      protected char key;
      protected V value;

      public BasicEntry() {
      }

      public BasicEntry(Character key, V value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(char key, V value) {
         this.key = key;
         this.value = value;
      }

      @Override
      public char getCharKey() {
         return this.key;
      }

      @Override
      public V getValue() {
         return this.value;
      }

      @Override
      public V setValue(V value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Char2ReferenceMap.Entry) {
            Char2ReferenceMap.Entry<V> e = (Char2ReferenceMap.Entry<V>)o;
            return this.key == e.getCharKey() && this.value == e.getValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Character) {
               Object value = e.getValue();
               return this.key == (Character)key && this.value == value;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
      }

      @Override
      public String toString() {
         return this.key + "->" + this.value;
      }
   }

   public abstract static class BasicEntrySet<V> extends AbstractObjectSet<Char2ReferenceMap.Entry<V>> {
      protected final Char2ReferenceMap<V> map;

      public BasicEntrySet(Char2ReferenceMap<V> map) {
         this.map = map;
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Char2ReferenceMap.Entry) {
            Char2ReferenceMap.Entry<V> e = (Char2ReferenceMap.Entry<V>)o;
            char k = e.getCharKey();
            return this.map.containsKey(k) && this.map.get(k) == e.getValue();
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Character) {
               char k = (Character)key;
               Object value = e.getValue();
               return this.map.containsKey(k) && this.map.get(k) == value;
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else if (o instanceof Char2ReferenceMap.Entry) {
            Char2ReferenceMap.Entry<V> e = (Char2ReferenceMap.Entry<V>)o;
            return this.map.remove(e.getCharKey(), e.getValue());
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            Object key = e.getKey();
            if (key != null && key instanceof Character) {
               char k = (Character)key;
               Object v = e.getValue();
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
      public ObjectSpliterator<Char2ReferenceMap.Entry<V>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
      }
   }
}
