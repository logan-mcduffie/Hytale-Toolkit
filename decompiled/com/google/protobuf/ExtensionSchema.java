package com.google.protobuf;

import java.io.IOException;
import java.util.Map.Entry;

@CheckReturnValue
abstract class ExtensionSchema<T extends FieldSet.FieldDescriptorLite<T>> {
   abstract boolean hasExtensions(MessageLite prototype);

   abstract FieldSet<T> getExtensions(Object message);

   abstract void setExtensions(Object message, FieldSet<T> extensions);

   abstract FieldSet<T> getMutableExtensions(Object message);

   abstract void makeImmutable(Object message);

   abstract <UT, UB> UB parseExtension(
      Object containerMessage,
      Reader reader,
      Object extension,
      ExtensionRegistryLite extensionRegistry,
      FieldSet<T> extensions,
      UB unknownFields,
      UnknownFieldSchema<UT, UB> unknownFieldSchema
   ) throws IOException;

   abstract int extensionNumber(Entry<?, ?> extension);

   abstract void serializeExtension(Writer writer, Entry<?, ?> extension) throws IOException;

   abstract Object findExtensionByNumber(ExtensionRegistryLite extensionRegistry, MessageLite defaultInstance, int number);

   abstract void parseLengthPrefixedMessageSetItem(Reader reader, Object extension, ExtensionRegistryLite extensionRegistry, FieldSet<T> extensions) throws IOException;

   abstract void parseMessageSetItem(ByteString data, Object extension, ExtensionRegistryLite extensionRegistry, FieldSet<T> extensions) throws IOException;
}
