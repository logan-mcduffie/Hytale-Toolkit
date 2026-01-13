package com.google.common.flogger.backend;

import com.google.common.flogger.MetadataKey;
import com.google.common.flogger.util.Checks;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class MetadataHandler<C> {
   protected abstract <T> void handle(MetadataKey<T> var1, T var2, C var3);

   protected <T> void handleRepeated(MetadataKey<T> key, Iterator<T> values, C context) {
      while (values.hasNext()) {
         this.handle(key, values.next(), context);
      }
   }

   public static <C> MetadataHandler.Builder<C> builder(MetadataHandler.ValueHandler<Object, C> defaultHandler) {
      return new MetadataHandler.Builder<>(defaultHandler);
   }

   public static final class Builder<C> {
      private static final MetadataHandler.ValueHandler<Object, Object> IGNORE_VALUE = new MetadataHandler.ValueHandler<Object, Object>() {
         @Override
         public void handle(MetadataKey<Object> key, Object value, Object context) {
         }
      };
      private static final MetadataHandler.RepeatedValueHandler<Object, Object> IGNORE_REPEATED_VALUE = new MetadataHandler.RepeatedValueHandler<Object, Object>() {
         @Override
         public void handle(MetadataKey<Object> key, Iterator<Object> value, Object context) {
         }
      };
      private final Map<MetadataKey<?>, MetadataHandler.ValueHandler<?, ? super C>> singleValueHandlers = new HashMap<>();
      private final Map<MetadataKey<?>, MetadataHandler.RepeatedValueHandler<?, ? super C>> repeatedValueHandlers = new HashMap<>();
      private final MetadataHandler.ValueHandler<Object, ? super C> defaultHandler;
      private MetadataHandler.RepeatedValueHandler<Object, ? super C> defaultRepeatedHandler = null;

      private Builder(MetadataHandler.ValueHandler<Object, ? super C> defaultHandler) {
         this.defaultHandler = Checks.checkNotNull(defaultHandler, "default handler");
      }

      public MetadataHandler.Builder<C> setDefaultRepeatedHandler(MetadataHandler.RepeatedValueHandler<Object, ? super C> defaultHandler) {
         this.defaultRepeatedHandler = Checks.checkNotNull(defaultHandler, "handler");
         return this;
      }

      public <T> MetadataHandler.Builder<C> addHandler(MetadataKey<T> key, MetadataHandler.ValueHandler<? super T, ? super C> handler) {
         Checks.checkNotNull(key, "key");
         Checks.checkNotNull(handler, "handler");
         this.repeatedValueHandlers.remove(key);
         this.singleValueHandlers.put(key, handler);
         return this;
      }

      public <T> MetadataHandler.Builder<C> addRepeatedHandler(MetadataKey<? extends T> key, MetadataHandler.RepeatedValueHandler<T, ? super C> handler) {
         Checks.checkNotNull(key, "key");
         Checks.checkNotNull(handler, "handler");
         Checks.checkArgument(key.canRepeat(), "key must be repeating");
         this.singleValueHandlers.remove(key);
         this.repeatedValueHandlers.put(key, handler);
         return this;
      }

      public MetadataHandler.Builder<C> ignoring(MetadataKey<?> key, MetadataKey<?>... rest) {
         this.checkAndIgnore(key);

         for (MetadataKey<?> k : rest) {
            this.checkAndIgnore(k);
         }

         return this;
      }

      public MetadataHandler.Builder<C> ignoring(Iterable<MetadataKey<?>> keys) {
         for (MetadataKey<?> k : keys) {
            this.checkAndIgnore(k);
         }

         return this;
      }

      <T> void checkAndIgnore(MetadataKey<T> key) {
         Checks.checkNotNull(key, "key");
         if (key.canRepeat()) {
            this.addRepeatedHandler(key, (MetadataHandler.RepeatedValueHandler<T, ? super C>)IGNORE_REPEATED_VALUE);
         } else {
            this.addHandler(key, IGNORE_VALUE);
         }
      }

      public MetadataHandler.Builder<C> removeHandlers(MetadataKey<?> key, MetadataKey<?>... rest) {
         this.checkAndRemove(key);

         for (MetadataKey<?> k : rest) {
            this.checkAndRemove(k);
         }

         return this;
      }

      void checkAndRemove(MetadataKey<?> key) {
         Checks.checkNotNull(key, "key");
         this.singleValueHandlers.remove(key);
         this.repeatedValueHandlers.remove(key);
      }

      public MetadataHandler<C> build() {
         return new MetadataHandler.MapBasedhandler<>(this);
      }
   }

   private static final class MapBasedhandler<C> extends MetadataHandler<C> {
      private final Map<MetadataKey<?>, MetadataHandler.ValueHandler<?, ? super C>> singleValueHandlers = new HashMap<>();
      private final Map<MetadataKey<?>, MetadataHandler.RepeatedValueHandler<?, ? super C>> repeatedValueHandlers = new HashMap<>();
      private final MetadataHandler.ValueHandler<Object, ? super C> defaultHandler;
      private final MetadataHandler.RepeatedValueHandler<Object, ? super C> defaultRepeatedHandler;

      private MapBasedhandler(MetadataHandler.Builder<C> builder) {
         this.singleValueHandlers.putAll(builder.singleValueHandlers);
         this.repeatedValueHandlers.putAll(builder.repeatedValueHandlers);
         this.defaultHandler = builder.defaultHandler;
         this.defaultRepeatedHandler = builder.defaultRepeatedHandler;
      }

      @Override
      protected <T> void handle(MetadataKey<T> key, T value, C context) {
         MetadataHandler.ValueHandler<T, ? super C> handler = (MetadataHandler.ValueHandler<T, ? super C>)this.singleValueHandlers.get(key);
         if (handler != null) {
            handler.handle(key, value, context);
         } else {
            this.defaultHandler.handle(key, value, context);
         }
      }

      @Override
      protected <T> void handleRepeated(MetadataKey<T> key, Iterator<T> values, C context) {
         MetadataHandler.RepeatedValueHandler<T, ? super C> handler = (MetadataHandler.RepeatedValueHandler<T, ? super C>)this.repeatedValueHandlers.get(key);
         if (handler != null) {
            handler.handle(key, values, context);
         } else if (this.defaultRepeatedHandler != null && !this.singleValueHandlers.containsKey(key)) {
            this.defaultRepeatedHandler.handle(key, values, context);
         } else {
            super.handleRepeated(key, values, context);
         }
      }
   }

   public interface RepeatedValueHandler<T, C> {
      void handle(MetadataKey<T> var1, Iterator<T> var2, C var3);
   }

   public interface ValueHandler<T, C> {
      void handle(MetadataKey<T> var1, T var2, C var3);
   }
}
