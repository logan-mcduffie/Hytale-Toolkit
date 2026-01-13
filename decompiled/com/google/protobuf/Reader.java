package com.google.protobuf;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@CheckReturnValue
interface Reader {
   int READ_DONE = Integer.MAX_VALUE;
   int TAG_UNKNOWN = 0;

   boolean shouldDiscardUnknownFields();

   int getFieldNumber() throws IOException;

   int getTag();

   boolean skipField() throws IOException;

   double readDouble() throws IOException;

   float readFloat() throws IOException;

   long readUInt64() throws IOException;

   long readInt64() throws IOException;

   int readInt32() throws IOException;

   long readFixed64() throws IOException;

   int readFixed32() throws IOException;

   boolean readBool() throws IOException;

   String readString() throws IOException;

   String readStringRequireUtf8() throws IOException;

   <T> T readMessageBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException;

   <T> T readMessage(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException;

   @Deprecated
   <T> T readGroup(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException;

   @Deprecated
   <T> T readGroupBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException;

   <T> void mergeMessageField(T target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException;

   <T> void mergeGroupField(T target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException;

   ByteString readBytes() throws IOException;

   int readUInt32() throws IOException;

   int readEnum() throws IOException;

   int readSFixed32() throws IOException;

   long readSFixed64() throws IOException;

   int readSInt32() throws IOException;

   long readSInt64() throws IOException;

   void readDoubleList(List<Double> target) throws IOException;

   void readFloatList(List<Float> target) throws IOException;

   void readUInt64List(List<Long> target) throws IOException;

   void readInt64List(List<Long> target) throws IOException;

   void readInt32List(List<Integer> target) throws IOException;

   void readFixed64List(List<Long> target) throws IOException;

   void readFixed32List(List<Integer> target) throws IOException;

   void readBoolList(List<Boolean> target) throws IOException;

   void readStringList(List<String> target) throws IOException;

   void readStringListRequireUtf8(List<String> target) throws IOException;

   <T> void readMessageList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException;

   <T> void readMessageList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException;

   @Deprecated
   <T> void readGroupList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException;

   @Deprecated
   <T> void readGroupList(List<T> target, Schema<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException;

   void readBytesList(List<ByteString> target) throws IOException;

   void readUInt32List(List<Integer> target) throws IOException;

   void readEnumList(List<Integer> target) throws IOException;

   void readSFixed32List(List<Integer> target) throws IOException;

   void readSFixed64List(List<Long> target) throws IOException;

   void readSInt32List(List<Integer> target) throws IOException;

   void readSInt64List(List<Long> target) throws IOException;

   <K, V> void readMap(Map<K, V> target, MapEntryLite.Metadata<K, V> mapDefaultEntry, ExtensionRegistryLite extensionRegistry) throws IOException;
}
