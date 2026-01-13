package com.google.protobuf;

@CheckReturnValue
interface SchemaFactory {
   <T> Schema<T> createSchema(Class<T> messageType);
}
