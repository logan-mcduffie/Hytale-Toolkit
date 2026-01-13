package com.google.protobuf;

import java.io.IOException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

public class MapEntryLite<K, V> {
   private static final int KEY_FIELD_NUMBER = 1;
   private static final int VALUE_FIELD_NUMBER = 2;
   private final MapEntryLite.Metadata<K, V> metadata;
   private final K key;
   private final V value;

   private MapEntryLite(WireFormat.FieldType keyType, K defaultKey, WireFormat.FieldType valueType, V defaultValue) {
      this.metadata = new MapEntryLite.Metadata<>(keyType, defaultKey, valueType, defaultValue);
      this.key = defaultKey;
      this.value = defaultValue;
   }

   private MapEntryLite(MapEntryLite.Metadata<K, V> metadata, K key, V value) {
      this.metadata = metadata;
      this.key = key;
      this.value = value;
   }

   public K getKey() {
      return this.key;
   }

   public V getValue() {
      return this.value;
   }

   public static <K, V> MapEntryLite<K, V> newDefaultInstance(WireFormat.FieldType keyType, K defaultKey, WireFormat.FieldType valueType, V defaultValue) {
      return new MapEntryLite<>(keyType, defaultKey, valueType, defaultValue);
   }

   static <K, V> void writeTo(CodedOutputStream output, MapEntryLite.Metadata<K, V> metadata, K key, V value) throws IOException {
      FieldSet.writeElement(output, metadata.keyType, 1, key);
      FieldSet.writeElement(output, metadata.valueType, 2, value);
   }

   static <K, V> int computeSerializedSize(MapEntryLite.Metadata<K, V> metadata, K key, V value) {
      return FieldSet.computeElementSize(metadata.keyType, 1, key) + FieldSet.computeElementSize(metadata.valueType, 2, value);
   }

   static <T> T parseField(CodedInputStream input, ExtensionRegistryLite extensionRegistry, WireFormat.FieldType type, T value) throws IOException {
      switch (type) {
         case MESSAGE:
            MessageLite.Builder subBuilder = ((MessageLite)value).toBuilder();
            input.readMessage(subBuilder, extensionRegistry);
            return (T)subBuilder.buildPartial();
         case ENUM:
            return (T)input.readEnum();
         case GROUP:
            throw new RuntimeException("Groups are not allowed in maps.");
         default:
            return (T)FieldSet.readPrimitiveField(input, type, true);
      }
   }

   public void serializeTo(CodedOutputStream output, int fieldNumber, K key, V value) throws IOException {
      output.writeTag(fieldNumber, 2);
      output.writeUInt32NoTag(computeSerializedSize(this.metadata, key, value));
      writeTo(output, this.metadata, key, value);
   }

   public int computeMessageSize(int fieldNumber, K key, V value) {
      return CodedOutputStream.computeTagSize(fieldNumber)
         + CodedOutputStream.computeLengthDelimitedFieldSize(computeSerializedSize(this.metadata, key, value));
   }

   public Entry<K, V> parseEntry(ByteString bytes, ExtensionRegistryLite extensionRegistry) throws IOException {
      return parseEntry(bytes.newCodedInput(), this.metadata, extensionRegistry);
   }

   static <K, V> Entry<K, V> parseEntry(CodedInputStream input, MapEntryLite.Metadata<K, V> metadata, ExtensionRegistryLite extensionRegistry) throws IOException {
      K key = metadata.defaultKey;
      V value = metadata.defaultValue;

      while (true) {
         int tag = input.readTag();
         if (tag == 0) {
            break;
         }

         if (tag == WireFormat.makeTag(1, metadata.keyType.getWireType())) {
            key = parseField(input, extensionRegistry, metadata.keyType, key);
         } else if (tag == WireFormat.makeTag(2, metadata.valueType.getWireType())) {
            value = parseField(input, extensionRegistry, metadata.valueType, value);
         } else if (!input.skipField(tag)) {
            break;
         }
      }

      return new SimpleImmutableEntry<>(key, value);
   }

   public void parseInto(MapFieldLite<K, V> map, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      int length = input.readRawVarint32();
      int oldLimit = input.pushLimit(length);
      K key = this.metadata.defaultKey;
      V value = this.metadata.defaultValue;

      while (true) {
         int tag = input.readTag();
         if (tag == 0) {
            break;
         }

         if (tag == WireFormat.makeTag(1, this.metadata.keyType.getWireType())) {
            key = parseField(input, extensionRegistry, this.metadata.keyType, key);
         } else if (tag == WireFormat.makeTag(2, this.metadata.valueType.getWireType())) {
            value = parseField(input, extensionRegistry, this.metadata.valueType, value);
         } else if (!input.skipField(tag)) {
            break;
         }
      }

      input.checkLastTagWas(0);
      input.popLimit(oldLimit);
      map.put(key, value);
   }

   MapEntryLite.Metadata<K, V> getMetadata() {
      return this.metadata;
   }

   static class Metadata<K, V> {
      public final WireFormat.FieldType keyType;
      public final K defaultKey;
      public final WireFormat.FieldType valueType;
      public final V defaultValue;

      public Metadata(WireFormat.FieldType keyType, K defaultKey, WireFormat.FieldType valueType, V defaultValue) {
         this.keyType = keyType;
         this.defaultKey = defaultKey;
         this.valueType = valueType;
         this.defaultValue = defaultValue;
      }
   }
}
