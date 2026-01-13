package com.google.common.flogger.backend;

import com.google.common.flogger.MetadataKey;
import com.google.common.flogger.util.Checks;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public abstract class MetadataProcessor {
   private static final MetadataProcessor EMPTY_PROCESSOR = new MetadataProcessor() {
      @Override
      public <C> void process(MetadataHandler<C> handler, C context) {
      }

      @Override
      public <C> void handle(MetadataKey<?> key, MetadataHandler<C> handler, C context) {
      }

      @Override
      public <T> T getSingleValue(MetadataKey<T> key) {
         return null;
      }

      @Override
      public int keyCount() {
         return 0;
      }

      @Override
      public Set<MetadataKey<?>> keySet() {
         return Collections.emptySet();
      }
   };

   public static MetadataProcessor forScopeAndLogSite(Metadata scopeMetadata, Metadata logMetadata) {
      int totalSize = scopeMetadata.size() + logMetadata.size();
      if (totalSize == 0) {
         return EMPTY_PROCESSOR;
      } else {
         return totalSize <= 28 ? getLightweightProcessor(scopeMetadata, logMetadata) : getSimpleProcessor(scopeMetadata, logMetadata);
      }
   }

   static MetadataProcessor getLightweightProcessor(Metadata scope, Metadata logged) {
      return new MetadataProcessor.LightweightProcessor(scope, logged);
   }

   static MetadataProcessor getSimpleProcessor(Metadata scope, Metadata logged) {
      return new MetadataProcessor.SimpleProcessor(scope, logged);
   }

   private MetadataProcessor() {
   }

   public abstract <C> void process(MetadataHandler<C> var1, C var2);

   public abstract <C> void handle(MetadataKey<?> var1, MetadataHandler<C> var2, C var3);

   public abstract <T> T getSingleValue(MetadataKey<T> var1);

   public abstract int keyCount();

   public abstract Set<MetadataKey<?>> keySet();

   private static final class LightweightProcessor extends MetadataProcessor {
      private static final int MAX_LIGHTWEIGHT_ELEMENTS = 28;
      private final Metadata scope;
      private final Metadata logged;
      private final int[] keyMap;
      private final int keyCount;

      private LightweightProcessor(Metadata scope, Metadata logged) {
         this.scope = Checks.checkNotNull(scope, "scope metadata");
         this.logged = Checks.checkNotNull(logged, "logged metadata");
         int maxKeyCount = scope.size() + logged.size();
         Checks.checkArgument(maxKeyCount <= 28, "metadata size too large");
         this.keyMap = new int[maxKeyCount];
         this.keyCount = this.prepareKeyMap(this.keyMap);
      }

      @Override
      public <C> void process(MetadataHandler<C> handler, C context) {
         for (int i = 0; i < this.keyCount; i++) {
            int n = this.keyMap[i];
            this.dispatch(this.getKey(n & 31), n, handler, context);
         }
      }

      @Override
      public <C> void handle(MetadataKey<?> key, MetadataHandler<C> handler, C context) {
         int index = this.indexOf(key, this.keyMap, this.keyCount);
         if (index >= 0) {
            this.dispatch(key, this.keyMap[index], handler, context);
         }
      }

      @Override
      public <T> T getSingleValue(MetadataKey<T> key) {
         Checks.checkArgument(!key.canRepeat(), "key must be single valued");
         int index = this.indexOf(key, this.keyMap, this.keyCount);
         return index >= 0 ? key.cast(this.getValue(this.keyMap[index])) : null;
      }

      @Override
      public int keyCount() {
         return this.keyCount;
      }

      @Override
      public Set<MetadataKey<?>> keySet() {
         return new AbstractSet<MetadataKey<?>>() {
            @Override
            public int size() {
               return LightweightProcessor.this.keyCount;
            }

            @Override
            public Iterator<MetadataKey<?>> iterator() {
               return new Iterator<MetadataKey<?>>() {
                  private int i = 0;

                  @Override
                  public boolean hasNext() {
                     return this.i < LightweightProcessor.this.keyCount;
                  }

                  public MetadataKey<?> next() {
                     return LightweightProcessor.this.getKey(LightweightProcessor.this.keyMap[this.i++] & 31);
                  }
               };
            }
         };
      }

      private <T, C> void dispatch(MetadataKey<T> key, int n, MetadataHandler<C> handler, C context) {
         if (!key.canRepeat()) {
            handler.handle(key, key.cast(this.getValue(n)), context);
         } else {
            handler.handleRepeated(key, new MetadataProcessor.LightweightProcessor.ValueIterator<>(key, n), context);
         }
      }

      private int prepareKeyMap(int[] keyMap) {
         long bloomFilterMask = 0L;
         int count = 0;

         for (int n = 0; n < keyMap.length; n++) {
            MetadataKey<?> key = this.getKey(n);
            long oldMask = bloomFilterMask;
            bloomFilterMask |= key.getBloomFilterMask();
            if (bloomFilterMask == oldMask) {
               int i = this.indexOf(key, keyMap, count);
               if (i != -1) {
                  keyMap[i] = key.canRepeat() ? keyMap[i] | 1 << n + 4 : n;
                  continue;
               }
            }

            keyMap[count++] = n;
         }

         return count;
      }

      private int indexOf(MetadataKey<?> key, int[] keyMap, int count) {
         for (int i = 0; i < count; i++) {
            if (key.equals(this.getKey(keyMap[i] & 31))) {
               return i;
            }
         }

         return -1;
      }

      private MetadataKey<?> getKey(int n) {
         int scopeSize = this.scope.size();
         return n >= scopeSize ? this.logged.getKey(n - scopeSize) : this.scope.getKey(n);
      }

      private Object getValue(int n) {
         int scopeSize = this.scope.size();
         return n >= scopeSize ? this.logged.getValue(n - scopeSize) : this.scope.getValue(n);
      }

      private final class ValueIterator<T> implements Iterator<T> {
         private final MetadataKey<T> key;
         private int nextIndex;
         private int mask;

         private ValueIterator(MetadataKey<T> key, int valueIndices) {
            this.key = key;
            this.nextIndex = valueIndices & 31;
            this.mask = valueIndices >>> 5 + this.nextIndex;
         }

         @Override
         public boolean hasNext() {
            return this.nextIndex >= 0;
         }

         @Override
         public T next() {
            T next = this.key.cast(LightweightProcessor.this.getValue(this.nextIndex));
            if (this.mask != 0) {
               int skip = 1 + Integer.numberOfTrailingZeros(this.mask);
               this.mask >>>= skip;
               this.nextIndex += skip;
            } else {
               this.nextIndex = -1;
            }

            return next;
         }
      }
   }

   private static final class SimpleProcessor extends MetadataProcessor {
      private final Map<MetadataKey<?>, Object> map;

      private SimpleProcessor(Metadata scope, Metadata logged) {
         LinkedHashMap<MetadataKey<?>, Object> map = new LinkedHashMap<>();
         addTo(map, scope);
         addTo(map, logged);

         for (Entry<MetadataKey<?>, Object> e : map.entrySet()) {
            if (e.getKey().canRepeat()) {
               e.setValue(Collections.unmodifiableList((List)e.getValue()));
            }
         }

         this.map = Collections.unmodifiableMap(map);
      }

      private static void addTo(Map<MetadataKey<?>, Object> map, Metadata metadata) {
         for (int i = 0; i < metadata.size(); i++) {
            MetadataKey<?> key = metadata.getKey(i);
            Object value = map.get(key);
            if (key.canRepeat()) {
               List<Object> list = (List<Object>)value;
               if (list == null) {
                  list = new ArrayList<>();
                  map.put(key, list);
               }

               list.add(key.cast(metadata.getValue(i)));
            } else {
               map.put(key, key.cast(metadata.getValue(i)));
            }
         }
      }

      @Override
      public <C> void process(MetadataHandler<C> handler, C context) {
         for (Entry<MetadataKey<?>, Object> e : this.map.entrySet()) {
            dispatch(e.getKey(), e.getValue(), handler, context);
         }
      }

      @Override
      public <C> void handle(MetadataKey<?> key, MetadataHandler<C> handler, C context) {
         Object value = this.map.get(key);
         if (value != null) {
            dispatch(key, value, handler, context);
         }
      }

      @Override
      public <T> T getSingleValue(MetadataKey<T> key) {
         Checks.checkArgument(!key.canRepeat(), "key must be single valued");
         Object value = this.map.get(key);
         return (T)(value != null ? value : null);
      }

      @Override
      public int keyCount() {
         return this.map.size();
      }

      @Override
      public Set<MetadataKey<?>> keySet() {
         return this.map.keySet();
      }

      private static <T, C> void dispatch(MetadataKey<T> key, Object value, MetadataHandler<C> handler, C context) {
         if (key.canRepeat()) {
            handler.handleRepeated(key, ((List)value).iterator(), context);
         } else {
            handler.handle(key, (T)value, context);
         }
      }
   }
}
