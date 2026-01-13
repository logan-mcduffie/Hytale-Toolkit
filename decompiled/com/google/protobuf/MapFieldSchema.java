package com.google.protobuf;

import java.util.Map;

@CheckReturnValue
interface MapFieldSchema {
   Map<?, ?> forMutableMapData(Object mapField);

   Map<?, ?> forMapData(Object mapField);

   boolean isImmutable(Object mapField);

   Object toImmutable(Object mapField);

   Object newMapField(Object mapDefaultEntry);

   MapEntryLite.Metadata<?, ?> forMapMetadata(Object mapDefaultEntry);

   @CanIgnoreReturnValue
   Object mergeFrom(Object destMapField, Object srcMapField);

   int getSerializedSize(int fieldNumber, Object mapField, Object mapDefaultEntry);
}
