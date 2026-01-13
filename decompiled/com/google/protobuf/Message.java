package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;

@CheckReturnValue
public interface Message extends MessageLite, MessageOrBuilder {
   @Override
   Parser<? extends Message> getParserForType();

   @Override
   boolean equals(Object other);

   @Override
   int hashCode();

   @Override
   String toString();

   Message.Builder newBuilderForType();

   Message.Builder toBuilder();

   public interface Builder extends MessageLite.Builder, MessageOrBuilder {
      @CanIgnoreReturnValue
      Message.Builder clear();

      @CanIgnoreReturnValue
      Message.Builder mergeFrom(Message other);

      @CanIgnoreReturnValue
      Message.Builder mergeFrom(CodedInputStream input) throws IOException;

      @CanIgnoreReturnValue
      Message.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException;

      @CanIgnoreReturnValue
      Message.Builder mergeFrom(ByteString data) throws InvalidProtocolBufferException;

      @CanIgnoreReturnValue
      Message.Builder mergeFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException;

      @CanIgnoreReturnValue
      Message.Builder mergeFrom(byte[] data) throws InvalidProtocolBufferException;

      @CanIgnoreReturnValue
      Message.Builder mergeFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException;

      @CanIgnoreReturnValue
      Message.Builder mergeFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException;

      @CanIgnoreReturnValue
      Message.Builder mergeFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException;

      @CanIgnoreReturnValue
      Message.Builder mergeFrom(InputStream input) throws IOException;

      @CanIgnoreReturnValue
      Message.Builder mergeFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException;

      Message build();

      Message buildPartial();

      Message.Builder clone();

      @Override
      Descriptors.Descriptor getDescriptorForType();

      Message.Builder newBuilderForField(Descriptors.FieldDescriptor field);

      Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field);

      Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index);

      @CanIgnoreReturnValue
      Message.Builder setField(Descriptors.FieldDescriptor field, Object value);

      @CanIgnoreReturnValue
      Message.Builder clearField(Descriptors.FieldDescriptor field);

      @CanIgnoreReturnValue
      Message.Builder clearOneof(Descriptors.OneofDescriptor oneof);

      @CanIgnoreReturnValue
      Message.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value);

      @CanIgnoreReturnValue
      Message.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value);

      @CanIgnoreReturnValue
      Message.Builder setUnknownFields(UnknownFieldSet unknownFields);

      @CanIgnoreReturnValue
      Message.Builder mergeUnknownFields(UnknownFieldSet unknownFields);

      @Override
      boolean mergeDelimitedFrom(InputStream input) throws IOException;

      @Override
      boolean mergeDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException;
   }
}
