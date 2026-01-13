package com.google.common.flogger.context;

import com.google.common.flogger.util.Checks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

abstract class SegmentTrie<T> {
   private final T defaultValue;

   public static <T> SegmentTrie<T> create(Map<String, ? extends T> map, char separator, T defaultValue) {
      switch (map.size()) {
         case 0:
            return new SegmentTrie.EmptyTrie<>(defaultValue);
         case 1:
            Entry<String, ? extends T> e = map.entrySet().iterator().next();
            return new SegmentTrie.SingletonTrie<>(e.getKey(), (T)e.getValue(), separator, defaultValue);
         default:
            return new SegmentTrie.SortedTrie<>(map, separator, defaultValue);
      }
   }

   SegmentTrie(T defaultValue) {
      this.defaultValue = defaultValue;
   }

   public final T getDefaultValue() {
      return this.defaultValue;
   }

   public abstract T find(String var1);

   public abstract Map<String, T> getEntryMap();

   private static final class EmptyTrie<T> extends SegmentTrie<T> {
      EmptyTrie(T defaultValue) {
         super(defaultValue);
      }

      @Override
      public T find(String k) {
         return this.getDefaultValue();
      }

      @Override
      public Map<String, T> getEntryMap() {
         return Collections.emptyMap();
      }
   }

   private static final class SingletonTrie<T> extends SegmentTrie<T> {
      private final String key;
      private final T value;
      private final char separator;

      SingletonTrie(String key, T value, char separator, T defaultValue) {
         super(defaultValue);
         this.key = Checks.checkNotNull(key, "key");
         this.value = value;
         this.separator = separator;
      }

      @Override
      public T find(String k) {
         return !k.regionMatches(0, this.key, 0, this.key.length()) || k.length() != this.key.length() && k.charAt(this.key.length()) != this.separator
            ? this.getDefaultValue()
            : this.value;
      }

      @Override
      public Map<String, T> getEntryMap() {
         Map<String, T> map = new HashMap<>();
         map.put(this.key, this.value);
         return Collections.unmodifiableMap(map);
      }
   }

   private static final class SortedTrie<T> extends SegmentTrie<T> {
      private final String[] keys;
      private final List<T> values;
      private final int[] parent;
      private final char separator;

      SortedTrie(Map<String, ? extends T> entries, char separator, T defaultValue) {
         super(defaultValue);
         TreeMap<String, T> sorted = new TreeMap<>(entries);
         this.keys = sorted.keySet().toArray(new String[0]);
         this.values = new ArrayList<>(sorted.values());
         this.parent = buildParentMap(this.keys, separator);
         this.separator = separator;
      }

      @Override
      public T find(String key) {
         int keyLen = key.length();
         int lhsIdx = 0;
         int lhsPrefix = prefixCompare(key, this.keys[lhsIdx], 0);
         if (lhsPrefix == keyLen) {
            return this.values.get(lhsIdx);
         } else if (lhsPrefix < 0) {
            return this.getDefaultValue();
         } else {
            int rhsIdx = this.keys.length - 1;
            int rhsPrefix = prefixCompare(key, this.keys[rhsIdx], 0);
            if (rhsPrefix == keyLen) {
               return this.values.get(rhsIdx);
            } else if (rhsPrefix >= 0) {
               return this.findParent(key, rhsIdx, rhsPrefix);
            } else {
               rhsPrefix = ~rhsPrefix;

               while (true) {
                  int midIdx = lhsIdx + rhsIdx >>> 1;
                  if (midIdx == lhsIdx) {
                     return this.findParent(key, lhsIdx, lhsPrefix);
                  }

                  int midPrefix = prefixCompare(key, this.keys[midIdx], Math.min(lhsPrefix, rhsPrefix));
                  if (keyLen == midPrefix) {
                     return this.values.get(midIdx);
                  }

                  if (midPrefix >= 0) {
                     lhsIdx = midIdx;
                     lhsPrefix = midPrefix;
                  } else {
                     rhsIdx = midIdx;
                     rhsPrefix = ~midPrefix;
                  }
               }
            }
         }
      }

      private T findParent(String k, int idx, int len) {
         while (!this.isParent(this.keys[idx], k, len)) {
            idx = this.parent[idx];
            if (idx == -1) {
               return this.getDefaultValue();
            }
         }

         return this.values.get(idx);
      }

      private boolean isParent(String p, String k, int len) {
         return p.length() <= len && k.charAt(p.length()) == this.separator;
      }

      private static int prefixCompare(String lhs, String rhs, int start) {
         if (start < 0) {
            throw new IllegalStateException("lhs=" + lhs + ", rhs=" + rhs + ", start=" + start);
         } else {
            int len = Math.min(lhs.length(), rhs.length());

            for (int n = start; n < len; n++) {
               int diff = lhs.charAt(n) - rhs.charAt(n);
               if (diff != 0) {
                  return diff < 0 ? ~n : n;
               }
            }

            return len < rhs.length() ? ~len : len;
         }
      }

      private static int[] buildParentMap(String[] keys, char separator) {
         int[] pmap = new int[keys.length];
         pmap[0] = -1;

         for (int n = 1; n < keys.length; n++) {
            pmap[n] = -1;
            String key = keys[n];

            for (int sidx = key.lastIndexOf(separator); sidx >= 0; sidx = key.lastIndexOf(separator)) {
               key = key.substring(0, sidx);
               int i = Arrays.binarySearch(keys, 0, n, key);
               if (i >= 0) {
                  pmap[n] = i;
                  break;
               }
            }
         }

         return pmap;
      }

      @Override
      public Map<String, T> getEntryMap() {
         Map<String, T> map = new LinkedHashMap<>();

         for (int n = 0; n < this.keys.length; n++) {
            map.put(this.keys[n], this.values.get(n));
         }

         return Collections.unmodifiableMap(map);
      }
   }
}
