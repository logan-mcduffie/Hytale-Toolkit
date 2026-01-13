package com.google.protobuf;

import java.util.Map;
import java.util.Map.Entry;

@CheckReturnValue
final class MapFieldSchemaLite implements MapFieldSchema {
   @Override
   public Map<?, ?> forMutableMapData(Object mapField) {
      return (MapFieldLite)mapField;
   }

   @Override
   public MapEntryLite.Metadata<?, ?> forMapMetadata(Object mapDefaultEntry) {
      return ((MapEntryLite)mapDefaultEntry).getMetadata();
   }

   @Override
   public Map<?, ?> forMapData(Object mapField) {
      return (MapFieldLite)mapField;
   }

   @Override
   public boolean isImmutable(Object mapField) {
      return !((MapFieldLite)mapField).isMutable();
   }

   @Override
   public Object toImmutable(Object mapField) {
      ((MapFieldLite)mapField).makeImmutable();
      return mapField;
   }

   @Override
   public Object newMapField(Object unused) {
      return MapFieldLite.emptyMapField().mutableCopy();
   }

   @Override
   public Object mergeFrom(Object destMapField, Object srcMapField) {
      return mergeFromLite(destMapField, srcMapField);
   }

   private static <K, V> MapFieldLite<K, V> mergeFromLite(Object destMapField, Object srcMapField) {
      MapFieldLite<K, V> mine = (MapFieldLite<K, V>)destMapField;
      MapFieldLite<K, V> other = (MapFieldLite<K, V>)srcMapField;
      if (!other.isEmpty()) {
         if (!mine.isMutable()) {
            mine = mine.mutableCopy();
         }

         mine.mergeFrom(other);
      }

      return mine;
   }

   @Override
   public int getSerializedSize(int fieldNumber, Object mapField, Object mapDefaultEntry) {
      return getSerializedSizeLite(fieldNumber, mapField, mapDefaultEntry);
   }

   private static <K, V> int getSerializedSizeLite(int fieldNumber, Object mapField, Object defaultEntry) {
      MapFieldLite<K, V> mapFieldLite = (MapFieldLite<K, V>)mapField;
      MapEntryLite<K, V> defaultEntryLite = (MapEntryLite<K, V>)defaultEntry;
      if (mapFieldLite.isEmpty()) {
         return 0;
      } else {
         int size = 0;

         for (Entry<K, V> entry : mapFieldLite.entrySet()) {
            size += defaultEntryLite.computeMessageSize(fieldNumber, entry.getKey(), entry.getValue());
         }

         return size;
      }
   }
}
