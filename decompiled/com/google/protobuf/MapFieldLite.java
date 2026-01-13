package com.google.protobuf;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class MapFieldLite<K, V> extends LinkedHashMap<K, V> {
   private boolean isMutable = true;
   private static final MapFieldLite<?, ?> EMPTY_MAP_FIELD = new MapFieldLite();

   private MapFieldLite() {
   }

   private MapFieldLite(Map<K, V> mapData) {
      super(mapData);
   }

   public static <K, V> MapFieldLite<K, V> emptyMapField() {
      return (MapFieldLite<K, V>)EMPTY_MAP_FIELD;
   }

   public void mergeFrom(MapFieldLite<K, V> other) {
      this.ensureMutable();
      if (!other.isEmpty()) {
         this.putAll(other);
      }
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return this.isEmpty() ? Collections.emptySet() : super.entrySet();
   }

   @Override
   public void clear() {
      this.ensureMutable();
      super.clear();
   }

   @Override
   public V put(K key, V value) {
      this.ensureMutable();
      Internal.checkNotNull(key);
      Internal.checkNotNull(value);
      return super.put(key, value);
   }

   public V put(Entry<K, V> entry) {
      return this.put(entry.getKey(), entry.getValue());
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> m) {
      this.ensureMutable();
      checkForNullKeysAndValues(m);
      super.putAll(m);
   }

   @Override
   public V remove(Object key) {
      this.ensureMutable();
      return super.remove(key);
   }

   private static void checkForNullKeysAndValues(Map<?, ?> m) {
      for (Object key : m.keySet()) {
         Internal.checkNotNull(key);
         Internal.checkNotNull(m.get(key));
      }
   }

   private static boolean equals(Object a, Object b) {
      return a instanceof byte[] && b instanceof byte[] ? Arrays.equals((byte[])a, (byte[])b) : a.equals(b);
   }

   static <K, V> boolean equals(Map<K, V> a, Map<K, V> b) {
      if (a == b) {
         return true;
      } else if (a.size() != b.size()) {
         return false;
      } else {
         for (Entry<K, V> entry : a.entrySet()) {
            if (!b.containsKey(entry.getKey())) {
               return false;
            }

            if (!equals(entry.getValue(), b.get(entry.getKey()))) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public boolean equals(Object object) {
      return object instanceof Map && equals(this, (Map<K, V>)object);
   }

   private static int calculateHashCodeForObject(Object a) {
      if (a instanceof byte[]) {
         return Internal.hashCode((byte[])a);
      } else if (a instanceof Internal.EnumLite) {
         throw new UnsupportedOperationException();
      } else {
         return a.hashCode();
      }
   }

   static <K, V> int calculateHashCodeForMap(Map<K, V> a) {
      int result = 0;

      for (Entry<K, V> entry : a.entrySet()) {
         result += calculateHashCodeForObject(entry.getKey()) ^ calculateHashCodeForObject(entry.getValue());
      }

      return result;
   }

   @Override
   public int hashCode() {
      return calculateHashCodeForMap(this);
   }

   private static Object copy(Object object) {
      if (object instanceof byte[]) {
         byte[] data = (byte[])object;
         return Arrays.copyOf(data, data.length);
      } else {
         return object;
      }
   }

   static <K, V> Map<K, V> copy(Map<K, V> map) {
      Map<K, V> result = new LinkedHashMap<>(map.size() * 4 / 3 + 1);

      for (Entry<K, V> entry : map.entrySet()) {
         result.put(entry.getKey(), (V)copy(entry.getValue()));
      }

      return result;
   }

   public MapFieldLite<K, V> mutableCopy() {
      return this.isEmpty() ? new MapFieldLite<>() : new MapFieldLite<>(this);
   }

   public void makeImmutable() {
      this.isMutable = false;
   }

   public boolean isMutable() {
      return this.isMutable;
   }

   private void ensureMutable() {
      if (!this.isMutable()) {
         throw new UnsupportedOperationException();
      }
   }

   static {
      EMPTY_MAP_FIELD.makeImmutable();
   }
}
