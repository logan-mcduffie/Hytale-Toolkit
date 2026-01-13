package joptsimple.internal;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public class AbbreviationMap<V> implements OptionNameMap<V> {
   private final Map<Character, AbbreviationMap<V>> children = new TreeMap<>();
   private String key;
   private V value;
   private int keysBeyond;

   @Override
   public boolean contains(String key) {
      return this.get(key) != null;
   }

   @Override
   public V get(String key) {
      char[] chars = charsOf(key);
      AbbreviationMap<V> child = this;

      for (char each : chars) {
         child = child.children.get(each);
         if (child == null) {
            return null;
         }
      }

      return child.value;
   }

   @Override
   public void put(String key, V newValue) {
      if (newValue == null) {
         throw new NullPointerException();
      } else if (key.length() == 0) {
         throw new IllegalArgumentException();
      } else {
         char[] chars = charsOf(key);
         this.add(chars, newValue, 0, chars.length);
      }
   }

   @Override
   public void putAll(Iterable<String> keys, V newValue) {
      for (String each : keys) {
         this.put(each, newValue);
      }
   }

   private boolean add(char[] chars, V newValue, int offset, int length) {
      if (offset == length) {
         this.value = newValue;
         boolean wasAlreadyAKey = this.key != null;
         this.key = new String(chars);
         return !wasAlreadyAKey;
      } else {
         char nextChar = chars[offset];
         AbbreviationMap<V> child = this.children.get(nextChar);
         if (child == null) {
            child = new AbbreviationMap<>();
            this.children.put(nextChar, child);
         }

         boolean newKeyAdded = child.add(chars, newValue, offset + 1, length);
         if (newKeyAdded) {
            this.keysBeyond++;
         }

         if (this.key == null) {
            this.value = this.keysBeyond > 1 ? null : newValue;
         }

         return newKeyAdded;
      }
   }

   @Override
   public void remove(String key) {
      if (key.length() == 0) {
         throw new IllegalArgumentException();
      } else {
         char[] keyChars = charsOf(key);
         this.remove(keyChars, 0, keyChars.length);
      }
   }

   private boolean remove(char[] aKey, int offset, int length) {
      if (offset == length) {
         return this.removeAtEndOfKey();
      } else {
         char nextChar = aKey[offset];
         AbbreviationMap<V> child = this.children.get(nextChar);
         if (child != null && child.remove(aKey, offset + 1, length)) {
            this.keysBeyond--;
            if (child.keysBeyond == 0) {
               this.children.remove(nextChar);
            }

            if (this.keysBeyond == 1 && this.key == null) {
               this.setValueToThatOfOnlyChild();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   private void setValueToThatOfOnlyChild() {
      Entry<Character, AbbreviationMap<V>> entry = this.children.entrySet().iterator().next();
      AbbreviationMap<V> onlyChild = entry.getValue();
      this.value = onlyChild.value;
   }

   private boolean removeAtEndOfKey() {
      if (this.key == null) {
         return false;
      } else {
         this.key = null;
         if (this.keysBeyond == 1) {
            this.setValueToThatOfOnlyChild();
         } else {
            this.value = null;
         }

         return true;
      }
   }

   @Override
   public Map<String, V> toJavaUtilMap() {
      Map<String, V> mappings = new TreeMap<>();
      this.addToMappings(mappings);
      return mappings;
   }

   private void addToMappings(Map<String, V> mappings) {
      if (this.key != null) {
         mappings.put(this.key, this.value);
      }

      for (AbbreviationMap<V> each : this.children.values()) {
         each.addToMappings(mappings);
      }
   }

   private static char[] charsOf(String aKey) {
      char[] chars = new char[aKey.length()];
      aKey.getChars(0, aKey.length(), chars, 0);
      return chars;
   }
}
