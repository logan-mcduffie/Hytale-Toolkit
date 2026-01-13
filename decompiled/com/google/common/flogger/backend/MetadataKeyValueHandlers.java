package com.google.common.flogger.backend;

import com.google.common.flogger.MetadataKey;
import java.util.Iterator;
import java.util.Set;

public final class MetadataKeyValueHandlers {
   private static final MetadataHandler.ValueHandler<Object, MetadataKey.KeyValueHandler> EMIT_METADATA = new MetadataHandler.ValueHandler<Object, MetadataKey.KeyValueHandler>() {
      public void handle(MetadataKey<Object> key, Object value, MetadataKey.KeyValueHandler kvf) {
         key.emit(value, kvf);
      }
   };
   private static final MetadataHandler.RepeatedValueHandler<Object, MetadataKey.KeyValueHandler> EMIT_REPEATED_METADATA = new MetadataHandler.RepeatedValueHandler<Object, MetadataKey.KeyValueHandler>() {
      public void handle(MetadataKey<Object> key, Iterator<Object> value, MetadataKey.KeyValueHandler kvf) {
         key.emitRepeated(value, kvf);
      }
   };

   public static MetadataHandler.ValueHandler<Object, MetadataKey.KeyValueHandler> getDefaultValueHandler() {
      return EMIT_METADATA;
   }

   public static MetadataHandler.RepeatedValueHandler<Object, MetadataKey.KeyValueHandler> getDefaultRepeatedValueHandler() {
      return EMIT_REPEATED_METADATA;
   }

   public static MetadataHandler.Builder<MetadataKey.KeyValueHandler> getDefaultBuilder(Set<MetadataKey<?>> ignored) {
      return MetadataHandler.builder(getDefaultValueHandler()).setDefaultRepeatedHandler(getDefaultRepeatedValueHandler()).ignoring(ignored);
   }

   public static MetadataHandler<MetadataKey.KeyValueHandler> getDefaultHandler(Set<MetadataKey<?>> ignored) {
      return getDefaultBuilder(ignored).build();
   }

   private MetadataKeyValueHandlers() {
   }
}
