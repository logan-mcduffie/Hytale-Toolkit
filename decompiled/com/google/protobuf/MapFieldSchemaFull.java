package com.google.protobuf;

import java.util.Map;
import java.util.Map.Entry;

final class MapFieldSchemaFull implements MapFieldSchema {
   @Override
   public Map<?, ?> forMutableMapData(Object mapField) {
      return ((MapField)mapField).getMutableMap();
   }

   @Override
   public Map<?, ?> forMapData(Object mapField) {
      return ((MapField)mapField).getMap();
   }

   @Override
   public boolean isImmutable(Object mapField) {
      return !((MapField)mapField).isMutable();
   }

   @Override
   public Object toImmutable(Object mapField) {
      ((MapField)mapField).makeImmutable();
      return mapField;
   }

   @Override
   public Object newMapField(Object mapDefaultEntry) {
      return MapField.newMapField((MapEntry)mapDefaultEntry);
   }

   @Override
   public MapEntryLite.Metadata<?, ?> forMapMetadata(Object mapDefaultEntry) {
      return ((MapEntry)mapDefaultEntry).getMetadata();
   }

   @Override
   public Object mergeFrom(Object destMapField, Object srcMapField) {
      return mergeFromFull(destMapField, srcMapField);
   }

   private static <K, V> Object mergeFromFull(Object destMapField, Object srcMapField) {
      MapField<K, V> mine = (MapField<K, V>)destMapField;
      MapField<K, V> other = (MapField<K, V>)srcMapField;
      if (!mine.isMutable()) {
         mine.copy();
      }

      mine.mergeFrom(other);
      return mine;
   }

   @Override
   public int getSerializedSize(int number, Object mapField, Object mapDefaultEntry) {
      return getSerializedSizeFull(number, mapField, mapDefaultEntry);
   }

   private static <K, V> int getSerializedSizeFull(int number, Object mapField, Object defaultEntryObject) {
      if (mapField == null) {
         return 0;
      } else {
         Map<K, V> map = ((MapField)mapField).getMap();
         MapEntry<K, V> defaultEntry = (MapEntry<K, V>)defaultEntryObject;
         if (map.isEmpty()) {
            return 0;
         } else {
            int size = 0;

            for (Entry<K, V> entry : map.entrySet()) {
               size += CodedOutputStream.computeTagSize(number)
                  + CodedOutputStream.computeLengthDelimitedFieldSize(
                     MapEntryLite.computeSerializedSize(defaultEntry.getMetadata(), entry.getKey(), entry.getValue())
                  );
            }

            return size;
         }
      }
   }
}
