package com.google.protobuf;

import java.io.IOException;

@CheckReturnValue
interface Schema<T> {
   void writeTo(T message, Writer writer) throws IOException;

   void mergeFrom(T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException;

   void mergeFrom(T message, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException;

   void makeImmutable(T message);

   boolean isInitialized(T message);

   T newInstance();

   boolean equals(T message, T other);

   int hashCode(T message);

   void mergeFrom(T message, T other);

   int getSerializedSize(T message);
}
