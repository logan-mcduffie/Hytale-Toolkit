package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GeneratedMessageLite<MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends GeneratedMessageLite.Builder<MessageType, BuilderType>>
   extends AbstractMessageLite<MessageType, BuilderType> {
   static final int UNINITIALIZED_SERIALIZED_SIZE = Integer.MAX_VALUE;
   private static final int MUTABLE_FLAG_MASK = Integer.MIN_VALUE;
   private static final int MEMOIZED_SERIALIZED_SIZE_MASK = Integer.MAX_VALUE;
   private int memoizedSerializedSize = -1;
   static final int UNINITIALIZED_HASH_CODE = 0;
   protected UnknownFieldSetLite unknownFields = UnknownFieldSetLite.getDefaultInstance();
   private static Map<Class<?>, GeneratedMessageLite<?, ?>> defaultInstanceMap = new ConcurrentHashMap<>();

   boolean isMutable() {
      return (this.memoizedSerializedSize & -2147483648) != 0;
   }

   void markImmutable() {
      this.memoizedSerializedSize &= Integer.MAX_VALUE;
   }

   int getMemoizedHashCode() {
      return this.memoizedHashCode;
   }

   void setMemoizedHashCode(int value) {
      this.memoizedHashCode = value;
   }

   void clearMemoizedHashCode() {
      this.memoizedHashCode = 0;
   }

   boolean hashCodeIsNotMemoized() {
      return 0 == this.getMemoizedHashCode();
   }

   @Override
   public final Parser<MessageType> getParserForType() {
      return (Parser<MessageType>)this.dynamicMethod(GeneratedMessageLite.MethodToInvoke.GET_PARSER, null, null);
   }

   public final MessageType getDefaultInstanceForType() {
      return (MessageType)this.dynamicMethod(GeneratedMessageLite.MethodToInvoke.GET_DEFAULT_INSTANCE, null, null);
   }

   public final BuilderType newBuilderForType() {
      return (BuilderType)this.dynamicMethod(GeneratedMessageLite.MethodToInvoke.NEW_BUILDER, null, null);
   }

   MessageType newMutableInstance() {
      return (MessageType)this.dynamicMethod(GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE, null, null);
   }

   @Override
   public String toString() {
      return MessageLiteToString.toString(this, super.toString());
   }

   @Override
   public int hashCode() {
      if (this.isMutable()) {
         return this.computeHashCode();
      } else {
         if (this.hashCodeIsNotMemoized()) {
            this.setMemoizedHashCode(this.computeHashCode());
         }

         return this.getMemoizedHashCode();
      }
   }

   int computeHashCode() {
      return Protobuf.getInstance().schemaFor(this).hashCode(this);
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other == null) {
         return false;
      } else {
         return this.getClass() != other.getClass()
            ? false
            : Protobuf.getInstance().schemaFor(this).equals(this, (GeneratedMessageLite<MessageType, BuilderType>)other);
      }
   }

   private void ensureUnknownFieldsInitialized() {
      if (this.unknownFields == UnknownFieldSetLite.getDefaultInstance()) {
         this.unknownFields = UnknownFieldSetLite.newInstance();
      }
   }

   protected boolean parseUnknownField(int tag, CodedInputStream input) throws IOException {
      if (WireFormat.getTagWireType(tag) == 4) {
         return false;
      } else {
         this.ensureUnknownFieldsInitialized();
         return this.unknownFields.mergeFieldFrom(tag, input);
      }
   }

   protected void mergeVarintField(int tag, int value) {
      this.ensureUnknownFieldsInitialized();
      this.unknownFields.mergeVarintField(tag, value);
   }

   protected void mergeLengthDelimitedField(int fieldNumber, ByteString value) {
      this.ensureUnknownFieldsInitialized();
      this.unknownFields.mergeLengthDelimitedField(fieldNumber, value);
   }

   protected void makeImmutable() {
      Protobuf.getInstance().schemaFor(this).makeImmutable(this);
      this.markImmutable();
   }

   protected final <MessageType2 extends GeneratedMessageLite<MessageType2, BuilderType2>, BuilderType2 extends GeneratedMessageLite.Builder<MessageType2, BuilderType2>> BuilderType2 createBuilder() {
      return (BuilderType2)this.dynamicMethod(GeneratedMessageLite.MethodToInvoke.NEW_BUILDER, null, null);
   }

   protected final <MessageType2 extends GeneratedMessageLite<MessageType2, BuilderType2>, BuilderType2 extends GeneratedMessageLite.Builder<MessageType2, BuilderType2>> BuilderType2 createBuilder(
      MessageType2 prototype
   ) {
      return (BuilderType2)this.createBuilder().mergeFrom((MessageType)prototype);
   }

   @Override
   public final boolean isInitialized() {
      return isInitialized(this, true);
   }

   public final BuilderType toBuilder() {
      BuilderType builder = (BuilderType)this.dynamicMethod(GeneratedMessageLite.MethodToInvoke.NEW_BUILDER, null, null);
      return builder.mergeFrom((MessageType)this);
   }

   protected abstract Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1);

   void clearMemoizedSerializedSize() {
      this.setMemoizedSerializedSize(Integer.MAX_VALUE);
   }

   @Override
   int getMemoizedSerializedSize() {
      return this.memoizedSerializedSize & 2147483647;
   }

   @Override
   void setMemoizedSerializedSize(int size) {
      if (size < 0) {
         throw new IllegalStateException("serialized size must be non-negative, was " + size);
      } else {
         this.memoizedSerializedSize = this.memoizedSerializedSize & -2147483648 | size & 2147483647;
      }
   }

   @Override
   public void writeTo(CodedOutputStream output) throws IOException {
      Protobuf.getInstance().schemaFor(this).writeTo(this, CodedOutputStreamWriter.forCodedOutput(output));
   }

   @Override
   int getSerializedSize(Schema schema) {
      if (this.isMutable()) {
         int size = this.computeSerializedSize(schema);
         if (size < 0) {
            throw new IllegalStateException("serialized size must be non-negative, was " + size);
         } else {
            return size;
         }
      } else if (this.getMemoizedSerializedSize() != Integer.MAX_VALUE) {
         return this.getMemoizedSerializedSize();
      } else {
         int size = this.computeSerializedSize(schema);
         this.setMemoizedSerializedSize(size);
         return size;
      }
   }

   @Override
   public int getSerializedSize() {
      return this.getSerializedSize(null);
   }

   private int computeSerializedSize(Schema<?> nullableSchema) {
      return nullableSchema == null
         ? Protobuf.getInstance().schemaFor(this).getSerializedSize(this)
         : ((Schema<GeneratedMessageLite<MessageType, BuilderType>>)nullableSchema).getSerializedSize(this);
   }

   Object buildMessageInfo() throws Exception {
      return this.dynamicMethod(GeneratedMessageLite.MethodToInvoke.BUILD_MESSAGE_INFO, null, null);
   }

   static <T extends GeneratedMessageLite<?, ?>> T getDefaultInstance(Class<T> clazz) {
      T result = (T)defaultInstanceMap.get(clazz);
      if (result == null) {
         try {
            Class.forName(clazz.getName(), true, clazz.getClassLoader());
         } catch (ClassNotFoundException var3) {
            throw new IllegalStateException("Class initialization cannot fail.", var3);
         }

         result = (T)defaultInstanceMap.get(clazz);
      }

      if (result == null) {
         result = (T)UnsafeUtil.<T>allocateInstance(clazz).getDefaultInstanceForType();
         if (result == null) {
            throw new IllegalStateException();
         }

         defaultInstanceMap.put(clazz, result);
      }

      return result;
   }

   protected static <T extends GeneratedMessageLite<?, ?>> void registerDefaultInstance(Class<T> clazz, T defaultInstance) {
      defaultInstance.markImmutable();
      defaultInstanceMap.put(clazz, defaultInstance);
   }

   protected static Object newMessageInfo(MessageLite defaultInstance, String info, Object[] objects) {
      return new RawMessageInfo(defaultInstance, info, objects);
   }

   protected final void mergeUnknownFields(UnknownFieldSetLite unknownFields) {
      this.unknownFields = UnknownFieldSetLite.mutableCopyOf(this.unknownFields, unknownFields);
   }

   public static <ContainingType extends MessageLite, Type> GeneratedMessageLite.GeneratedExtension<ContainingType, Type> newSingularGeneratedExtension(
      final ContainingType containingTypeDefaultInstance,
      final Type defaultValue,
      final MessageLite messageDefaultInstance,
      final Internal.EnumLiteMap<?> enumTypeMap,
      final int number,
      final WireFormat.FieldType type,
      final Class singularType
   ) {
      return new GeneratedMessageLite.GeneratedExtension<>(
         containingTypeDefaultInstance,
         defaultValue,
         messageDefaultInstance,
         new GeneratedMessageLite.ExtensionDescriptor(enumTypeMap, number, type, false, false),
         singularType
      );
   }

   public static <ContainingType extends MessageLite, Type> GeneratedMessageLite.GeneratedExtension<ContainingType, Type> newRepeatedGeneratedExtension(
      final ContainingType containingTypeDefaultInstance,
      final MessageLite messageDefaultInstance,
      final Internal.EnumLiteMap<?> enumTypeMap,
      final int number,
      final WireFormat.FieldType type,
      final boolean isPacked,
      final Class singularType
   ) {
      Type emptyList = (Type)ProtobufArrayList.emptyList();
      return new GeneratedMessageLite.GeneratedExtension<>(
         containingTypeDefaultInstance,
         emptyList,
         messageDefaultInstance,
         new GeneratedMessageLite.ExtensionDescriptor(enumTypeMap, number, type, true, isPacked),
         singularType
      );
   }

   static java.lang.reflect.Method getMethodOrDie(Class clazz, String name, Class... params) {
      try {
         return clazz.getMethod(name, params);
      } catch (NoSuchMethodException var4) {
         throw new RuntimeException("Generated message class \"" + clazz.getName() + "\" missing method \"" + name + "\".", var4);
      }
   }

   static Object invokeOrDie(java.lang.reflect.Method method, Object object, Object... params) {
      try {
         return method.invoke(object, params);
      } catch (IllegalAccessException var5) {
         throw new RuntimeException("Couldn't use Java reflection to implement protocol message reflection.", var5);
      } catch (InvocationTargetException var6) {
         Throwable cause = var6.getCause();
         if (cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
         } else if (cause instanceof Error) {
            throw (Error)cause;
         } else {
            throw new RuntimeException("Unexpected exception thrown by generated accessor method.", cause);
         }
      }
   }

   private static <MessageType extends GeneratedMessageLite.ExtendableMessage<MessageType, BuilderType>, BuilderType extends GeneratedMessageLite.ExtendableBuilder<MessageType, BuilderType>, T> GeneratedMessageLite.GeneratedExtension<MessageType, T> checkIsLite(
      ExtensionLite<MessageType, T> extension
   ) {
      if (!extension.isLite()) {
         throw new IllegalArgumentException("Expected a lite extension.");
      } else {
         return (GeneratedMessageLite.GeneratedExtension<MessageType, T>)extension;
      }
   }

   private static final <T extends GeneratedMessageLite<T, ?>> boolean isInitialized(T message, boolean shouldMemoize) {
      byte memoizedIsInitialized = (Byte)message.dynamicMethod(GeneratedMessageLite.MethodToInvoke.GET_MEMOIZED_IS_INITIALIZED, null, null);
      if (memoizedIsInitialized == 1) {
         return true;
      } else if (memoizedIsInitialized == 0) {
         return false;
      } else {
         boolean isInitialized = Protobuf.getInstance().schemaFor(message).isInitialized(message);
         if (shouldMemoize) {
            Object var4 = message.dynamicMethod(GeneratedMessageLite.MethodToInvoke.SET_MEMOIZED_IS_INITIALIZED, isInitialized ? message : null, null);
         }

         return isInitialized;
      }
   }

   protected static Internal.IntList emptyIntList() {
      return IntArrayList.emptyList();
   }

   protected static Internal.IntList mutableCopy(Internal.IntList list) {
      int size = list.size();
      return list.mutableCopyWithCapacity(size * 2);
   }

   protected static Internal.LongList emptyLongList() {
      return LongArrayList.emptyList();
   }

   protected static Internal.LongList mutableCopy(Internal.LongList list) {
      int size = list.size();
      return list.mutableCopyWithCapacity(size * 2);
   }

   protected static Internal.FloatList emptyFloatList() {
      return FloatArrayList.emptyList();
   }

   protected static Internal.FloatList mutableCopy(Internal.FloatList list) {
      int size = list.size();
      return list.mutableCopyWithCapacity(size * 2);
   }

   protected static Internal.DoubleList emptyDoubleList() {
      return DoubleArrayList.emptyList();
   }

   protected static Internal.DoubleList mutableCopy(Internal.DoubleList list) {
      int size = list.size();
      return list.mutableCopyWithCapacity(size * 2);
   }

   protected static Internal.BooleanList emptyBooleanList() {
      return BooleanArrayList.emptyList();
   }

   protected static Internal.BooleanList mutableCopy(Internal.BooleanList list) {
      int size = list.size();
      return list.mutableCopyWithCapacity(size * 2);
   }

   protected static <E> Internal.ProtobufList<E> emptyProtobufList() {
      return ProtobufArrayList.emptyList();
   }

   protected static <E> Internal.ProtobufList<E> mutableCopy(Internal.ProtobufList<E> list) {
      int size = list.size();
      return list.mutableCopyWithCapacity(size * 2);
   }

   static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T instance, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      T result = instance.newMutableInstance();

      try {
         Schema<T> schema = Protobuf.getInstance().schemaFor(result);
         schema.mergeFrom(result, CodedInputStreamReader.forCodedInput(input), extensionRegistry);
         schema.makeImmutable(result);
         return result;
      } catch (InvalidProtocolBufferException var5) {
         InvalidProtocolBufferException e = var5;
         if (var5.getThrownFromInputStream()) {
            e = new InvalidProtocolBufferException((IOException)var5);
         }

         throw e.setUnfinishedMessage(result);
      } catch (UninitializedMessageException var6) {
         throw var6.asInvalidProtocolBufferException().setUnfinishedMessage(result);
      } catch (IOException var7) {
         if (var7.getCause() instanceof InvalidProtocolBufferException) {
            throw (InvalidProtocolBufferException)var7.getCause();
         } else {
            throw new InvalidProtocolBufferException(var7).setUnfinishedMessage(result);
         }
      } catch (RuntimeException var8) {
         if (var8.getCause() instanceof InvalidProtocolBufferException) {
            throw (InvalidProtocolBufferException)var8.getCause();
         } else {
            throw var8;
         }
      }
   }

   private static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(
      T defaultInstance, byte[] input, int offset, int length, ExtensionRegistryLite extensionRegistry
   ) throws InvalidProtocolBufferException {
      if (length == 0) {
         return defaultInstance;
      } else {
         T result = defaultInstance.newMutableInstance();

         try {
            Schema<T> schema = Protobuf.getInstance().schemaFor(result);
            schema.mergeFrom(result, input, offset, offset + length, new ArrayDecoders.Registers(extensionRegistry));
            schema.makeImmutable(result);
            return result;
         } catch (InvalidProtocolBufferException var7) {
            InvalidProtocolBufferException e = var7;
            if (var7.getThrownFromInputStream()) {
               e = new InvalidProtocolBufferException((IOException)var7);
            }

            throw e.setUnfinishedMessage(result);
         } catch (UninitializedMessageException var8) {
            throw var8.asInvalidProtocolBufferException().setUnfinishedMessage(result);
         } catch (IOException var9) {
            if (var9.getCause() instanceof InvalidProtocolBufferException) {
               throw (InvalidProtocolBufferException)var9.getCause();
            } else {
               throw new InvalidProtocolBufferException(var9).setUnfinishedMessage(result);
            }
         } catch (IndexOutOfBoundsException var10) {
            throw InvalidProtocolBufferException.truncatedMessage().setUnfinishedMessage(result);
         }
      }
   }

   protected static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T defaultInstance, CodedInputStream input) throws InvalidProtocolBufferException {
      return parsePartialFrom(defaultInstance, input, ExtensionRegistryLite.getEmptyRegistry());
   }

   private static <T extends GeneratedMessageLite<T, ?>> T checkMessageInitialized(T message) throws InvalidProtocolBufferException {
      if (message != null && !message.isInitialized()) {
         throw message.newUninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(message);
      } else {
         return message;
      }
   }

   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return checkMessageInitialized(parseFrom(defaultInstance, CodedInputStream.newInstance(data), extensionRegistry));
   }

   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteBuffer data) throws InvalidProtocolBufferException {
      return parseFrom(defaultInstance, data, ExtensionRegistryLite.getEmptyRegistry());
   }

   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteString data) throws InvalidProtocolBufferException {
      return checkMessageInitialized(parseFrom(defaultInstance, data, ExtensionRegistryLite.getEmptyRegistry()));
   }

   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return checkMessageInitialized(parsePartialFrom(defaultInstance, data, extensionRegistry));
   }

   private static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T defaultInstance, ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      CodedInputStream input = data.newCodedInput();
      T message = parsePartialFrom(defaultInstance, input, extensionRegistry);

      try {
         input.checkLastTagWas(0);
         return message;
      } catch (InvalidProtocolBufferException var6) {
         throw var6.setUnfinishedMessage(message);
      }
   }

   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, byte[] data) throws InvalidProtocolBufferException {
      return checkMessageInitialized(parsePartialFrom(defaultInstance, data, 0, data.length, ExtensionRegistryLite.getEmptyRegistry()));
   }

   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return checkMessageInitialized(parsePartialFrom(defaultInstance, data, 0, data.length, extensionRegistry));
   }

   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, InputStream input) throws InvalidProtocolBufferException {
      return checkMessageInitialized(parsePartialFrom(defaultInstance, CodedInputStream.newInstance(input), ExtensionRegistryLite.getEmptyRegistry()));
   }

   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return checkMessageInitialized(parsePartialFrom(defaultInstance, CodedInputStream.newInstance(input), extensionRegistry));
   }

   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, CodedInputStream input) throws InvalidProtocolBufferException {
      return parseFrom(defaultInstance, input, ExtensionRegistryLite.getEmptyRegistry());
   }

   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return checkMessageInitialized(parsePartialFrom(defaultInstance, input, extensionRegistry));
   }

   protected static <T extends GeneratedMessageLite<T, ?>> T parseDelimitedFrom(T defaultInstance, InputStream input) throws InvalidProtocolBufferException {
      return checkMessageInitialized(parsePartialDelimitedFrom(defaultInstance, input, ExtensionRegistryLite.getEmptyRegistry()));
   }

   protected static <T extends GeneratedMessageLite<T, ?>> T parseDelimitedFrom(T defaultInstance, InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return checkMessageInitialized(parsePartialDelimitedFrom(defaultInstance, input, extensionRegistry));
   }

   private static <T extends GeneratedMessageLite<T, ?>> T parsePartialDelimitedFrom(
      T defaultInstance, InputStream input, ExtensionRegistryLite extensionRegistry
   ) throws InvalidProtocolBufferException {
      int size;
      try {
         int firstByte = input.read();
         if (firstByte == -1) {
            return null;
         }

         size = CodedInputStream.readRawVarint32(firstByte, input);
      } catch (InvalidProtocolBufferException var9) {
         InvalidProtocolBufferException e = var9;
         if (var9.getThrownFromInputStream()) {
            e = new InvalidProtocolBufferException((IOException)var9);
         }

         throw e;
      } catch (IOException var10) {
         throw new InvalidProtocolBufferException(var10);
      }

      InputStream limitedInput = new AbstractMessageLite.Builder.LimitedInputStream(input, size);
      CodedInputStream codedInput = CodedInputStream.newInstance(limitedInput);
      T message = parsePartialFrom(defaultInstance, codedInput, extensionRegistry);

      try {
         codedInput.checkLastTagWas(0);
         return message;
      } catch (InvalidProtocolBufferException var8) {
         throw var8.setUnfinishedMessage(message);
      }
   }

   public abstract static class Builder<MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends GeneratedMessageLite.Builder<MessageType, BuilderType>>
      extends AbstractMessageLite.Builder<MessageType, BuilderType> {
      private final MessageType defaultInstance;
      protected MessageType instance;

      protected Builder(MessageType defaultInstance) {
         this.defaultInstance = defaultInstance;
         if (defaultInstance.isMutable()) {
            throw new IllegalArgumentException("Default instance must be immutable.");
         } else {
            this.instance = this.newMutableInstance();
         }
      }

      private MessageType newMutableInstance() {
         return this.defaultInstance.newMutableInstance();
      }

      protected final void copyOnWrite() {
         if (!this.instance.isMutable()) {
            this.copyOnWriteInternal();
         }
      }

      protected void copyOnWriteInternal() {
         MessageType newInstance = this.newMutableInstance();
         mergeFromInstance(newInstance, this.instance);
         this.instance = newInstance;
      }

      @Override
      public final boolean isInitialized() {
         return GeneratedMessageLite.isInitialized(this.instance, false);
      }

      public final BuilderType clear() {
         if (this.defaultInstance.isMutable()) {
            throw new IllegalArgumentException("Default instance must be immutable.");
         } else {
            this.instance = this.newMutableInstance();
            return (BuilderType)this;
         }
      }

      public BuilderType clone() {
         BuilderType builder = this.getDefaultInstanceForType().newBuilderForType();
         builder.instance = this.buildPartial();
         return builder;
      }

      public MessageType buildPartial() {
         if (!this.instance.isMutable()) {
            return this.instance;
         } else {
            this.instance.makeImmutable();
            return this.instance;
         }
      }

      public final MessageType build() {
         MessageType result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      protected BuilderType internalMergeFrom(MessageType message) {
         return this.mergeFrom(message);
      }

      public BuilderType mergeFrom(MessageType message) {
         if (this.getDefaultInstanceForType().equals(message)) {
            return (BuilderType)this;
         } else {
            this.copyOnWrite();
            mergeFromInstance(this.instance, message);
            return (BuilderType)this;
         }
      }

      public BuilderType mergeFrom(byte[] input, int offset, int length, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this.copyOnWrite();

         try {
            Protobuf.getInstance()
               .schemaFor(this.instance)
               .mergeFrom(this.instance, input, offset, offset + length, new ArrayDecoders.Registers(extensionRegistry));
            return (BuilderType)this;
         } catch (InvalidProtocolBufferException var6) {
            throw var6;
         } catch (IndexOutOfBoundsException var7) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } catch (IOException var8) {
            throw new RuntimeException("Reading from byte array should not throw IOException.", var8);
         }
      }

      public BuilderType mergeFrom(byte[] input, int offset, int length) throws InvalidProtocolBufferException {
         return this.mergeFrom(input, offset, length, ExtensionRegistryLite.getEmptyRegistry());
      }

      public BuilderType mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         this.copyOnWrite();

         try {
            Protobuf.getInstance().schemaFor(this.instance).mergeFrom(this.instance, CodedInputStreamReader.forCodedInput(input), extensionRegistry);
            return (BuilderType)this;
         } catch (RuntimeException var4) {
            if (var4.getCause() instanceof IOException) {
               throw (IOException)var4.getCause();
            } else {
               throw var4;
            }
         }
      }

      private static <MessageType> void mergeFromInstance(MessageType dest, MessageType src) {
         Protobuf.getInstance().schemaFor(dest).mergeFrom(dest, src);
      }

      public MessageType getDefaultInstanceForType() {
         return this.defaultInstance;
      }
   }

   protected static class DefaultInstanceBasedParser<T extends GeneratedMessageLite<T, ?>> extends AbstractParser<T> {
      private final T defaultInstance;

      public DefaultInstanceBasedParser(T defaultInstance) {
         this.defaultInstance = defaultInstance;
      }

      public T parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parsePartialFrom(this.defaultInstance, input, extensionRegistry);
      }

      public T parsePartialFrom(byte[] input, int offset, int length, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parsePartialFrom(this.defaultInstance, input, offset, length, extensionRegistry);
      }
   }

   public abstract static class ExtendableBuilder<MessageType extends GeneratedMessageLite.ExtendableMessage<MessageType, BuilderType>, BuilderType extends GeneratedMessageLite.ExtendableBuilder<MessageType, BuilderType>>
      extends GeneratedMessageLite.Builder<MessageType, BuilderType>
      implements GeneratedMessageLite.ExtendableMessageOrBuilder<MessageType, BuilderType> {
      protected ExtendableBuilder(MessageType defaultInstance) {
         super(defaultInstance);
      }

      void internalSetExtensionSet(FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) {
         this.copyOnWrite();
         ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions = extensions;
      }

      @Override
      protected void copyOnWriteInternal() {
         super.copyOnWriteInternal();
         if (((GeneratedMessageLite.ExtendableMessage)this.instance).extensions != FieldSet.emptySet()) {
            ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions = ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions.clone();
         }
      }

      private FieldSet<GeneratedMessageLite.ExtensionDescriptor> ensureExtensionsAreMutable() {
         FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions = ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions;
         if (extensions.isImmutable()) {
            extensions = extensions.clone();
            ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions = extensions;
         }

         return extensions;
      }

      public final MessageType buildPartial() {
         if (!((GeneratedMessageLite.ExtendableMessage)this.instance).isMutable()) {
            return (MessageType)((GeneratedMessageLite.ExtendableMessage)this.instance);
         } else {
            ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions.makeImmutable();
            return (MessageType)((GeneratedMessageLite.ExtendableMessage)super.buildPartial());
         }
      }

      private void verifyExtensionContainingType(final GeneratedMessageLite.GeneratedExtension<MessageType, ?> extension) {
         if (extension.getContainingTypeDefaultInstance() != this.getDefaultInstanceForType()) {
            throw new IllegalArgumentException(
               "This extension is for a different message type.  Please make sure that you are not suppressing any generics type warnings."
            );
         }
      }

      @Override
      public final <Type> boolean hasExtension(final ExtensionLite<MessageType, Type> extension) {
         return ((GeneratedMessageLite.ExtendableMessage)this.instance).hasExtension(extension);
      }

      @Override
      public final <Type> int getExtensionCount(final ExtensionLite<MessageType, List<Type>> extension) {
         return ((GeneratedMessageLite.ExtendableMessage)this.instance).getExtensionCount(extension);
      }

      @Override
      public final <Type> Type getExtension(final ExtensionLite<MessageType, Type> extension) {
         return ((GeneratedMessageLite.ExtendableMessage)this.instance).getExtension(extension);
      }

      @Override
      public final <Type> Type getExtension(final ExtensionLite<MessageType, List<Type>> extension, final int index) {
         return ((GeneratedMessageLite.ExtendableMessage)this.instance).getExtension(extension, index);
      }

      public final <Type> BuilderType setExtension(final ExtensionLite<MessageType, Type> extension, final Type value) {
         GeneratedMessageLite.GeneratedExtension<MessageType, Type> extensionLite = GeneratedMessageLite.checkIsLite(extension);
         this.verifyExtensionContainingType(extensionLite);
         this.copyOnWrite();
         this.ensureExtensionsAreMutable().setField(extensionLite.descriptor, extensionLite.toFieldSetType(value));
         return (BuilderType)this;
      }

      public final <Type> BuilderType setExtension(final ExtensionLite<MessageType, List<Type>> extension, final int index, final Type value) {
         GeneratedMessageLite.GeneratedExtension<MessageType, List<Type>> extensionLite = GeneratedMessageLite.checkIsLite(extension);
         this.verifyExtensionContainingType(extensionLite);
         this.copyOnWrite();
         this.ensureExtensionsAreMutable().setRepeatedField(extensionLite.descriptor, index, extensionLite.singularToFieldSetType(value));
         return (BuilderType)this;
      }

      public final <Type> BuilderType addExtension(final ExtensionLite<MessageType, List<Type>> extension, final Type value) {
         GeneratedMessageLite.GeneratedExtension<MessageType, List<Type>> extensionLite = GeneratedMessageLite.checkIsLite(extension);
         this.verifyExtensionContainingType(extensionLite);
         this.copyOnWrite();
         this.ensureExtensionsAreMutable().addRepeatedField(extensionLite.descriptor, extensionLite.singularToFieldSetType(value));
         return (BuilderType)this;
      }

      public final BuilderType clearExtension(final ExtensionLite<MessageType, ?> extension) {
         GeneratedMessageLite.GeneratedExtension<MessageType, ?> extensionLite = GeneratedMessageLite.checkIsLite(extension);
         this.verifyExtensionContainingType(extensionLite);
         this.copyOnWrite();
         this.ensureExtensionsAreMutable().clearField(extensionLite.descriptor);
         return (BuilderType)this;
      }
   }

   public abstract static class ExtendableMessage<MessageType extends GeneratedMessageLite.ExtendableMessage<MessageType, BuilderType>, BuilderType extends GeneratedMessageLite.ExtendableBuilder<MessageType, BuilderType>>
      extends GeneratedMessageLite<MessageType, BuilderType>
      implements GeneratedMessageLite.ExtendableMessageOrBuilder<MessageType, BuilderType> {
      protected FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions = FieldSet.emptySet();

      protected final void mergeExtensionFields(final MessageType other) {
         if (this.extensions.isImmutable()) {
            this.extensions = this.extensions.clone();
         }

         this.extensions.mergeFrom(other.extensions);
      }

      protected <MessageType2 extends MessageLite> boolean parseUnknownField(
         MessageType2 defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry, int tag
      ) throws IOException {
         int fieldNumber = WireFormat.getTagFieldNumber(tag);
         GeneratedMessageLite.GeneratedExtension<MessageType2, ?> extension = extensionRegistry.findLiteExtensionByNumber(defaultInstance, fieldNumber);
         return this.parseExtension(input, extensionRegistry, extension, tag, fieldNumber);
      }

      private boolean parseExtension(
         CodedInputStream input, ExtensionRegistryLite extensionRegistry, GeneratedMessageLite.GeneratedExtension<?, ?> extension, int tag, int fieldNumber
      ) throws IOException {
         int wireType = WireFormat.getTagWireType(tag);
         boolean unknown = false;
         boolean packed = false;
         if (extension == null) {
            unknown = true;
         } else if (wireType == FieldSet.getWireFormatForFieldType(extension.descriptor.getLiteType(), false)) {
            packed = false;
         } else if (extension.descriptor.isRepeated
            && extension.descriptor.type.isPackable()
            && wireType == FieldSet.getWireFormatForFieldType(extension.descriptor.getLiteType(), true)) {
            packed = true;
         } else {
            unknown = true;
         }

         if (unknown) {
            return this.parseUnknownField(tag, input);
         } else {
            FieldSet<GeneratedMessageLite.ExtensionDescriptor> unused = this.ensureExtensionsAreMutable();
            if (packed) {
               int length = input.readRawVarint32();
               int limit = input.pushLimit(length);
               if (extension.descriptor.getLiteType() == WireFormat.FieldType.ENUM) {
                  while (input.getBytesUntilLimit() > 0) {
                     int rawValue = input.readEnum();
                     Object value = extension.descriptor.getEnumType().findValueByNumber(rawValue);
                     if (value == null) {
                        return true;
                     }

                     this.extensions.addRepeatedField(extension.descriptor, extension.singularToFieldSetType(value));
                  }
               } else {
                  while (input.getBytesUntilLimit() > 0) {
                     Object value = FieldSet.readPrimitiveField(input, extension.descriptor.getLiteType(), false);
                     this.extensions.addRepeatedField(extension.descriptor, value);
                  }
               }

               input.popLimit(limit);
            } else {
               Object value;
               switch (extension.descriptor.getLiteJavaType()) {
                  case MESSAGE:
                     MessageLite.Builder subBuilder = null;
                     if (!extension.descriptor.isRepeated()) {
                        MessageLite existingValue = (MessageLite)this.extensions.getField(extension.descriptor);
                        if (existingValue != null) {
                           subBuilder = existingValue.toBuilder();
                        }
                     }

                     if (subBuilder == null) {
                        subBuilder = extension.getMessageDefaultInstance().newBuilderForType();
                     }

                     if (extension.descriptor.getLiteType() == WireFormat.FieldType.GROUP) {
                        input.readGroup(extension.getNumber(), subBuilder, extensionRegistry);
                     } else {
                        input.readMessage(subBuilder, extensionRegistry);
                     }

                     value = subBuilder.build();
                     break;
                  case ENUM:
                     int rawValue = input.readEnum();
                     value = extension.descriptor.getEnumType().findValueByNumber(rawValue);
                     if (value == null) {
                        this.mergeVarintField(fieldNumber, rawValue);
                        return true;
                     }
                     break;
                  default:
                     value = FieldSet.readPrimitiveField(input, extension.descriptor.getLiteType(), false);
               }

               if (extension.descriptor.isRepeated()) {
                  this.extensions.addRepeatedField(extension.descriptor, extension.singularToFieldSetType(value));
               } else {
                  this.extensions.setField(extension.descriptor, extension.singularToFieldSetType(value));
               }
            }

            return true;
         }
      }

      protected <MessageType2 extends MessageLite> boolean parseUnknownFieldAsMessageSet(
         MessageType2 defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry, int tag
      ) throws IOException {
         if (tag == WireFormat.MESSAGE_SET_ITEM_TAG) {
            this.mergeMessageSetExtensionFromCodedStream(defaultInstance, input, extensionRegistry);
            return true;
         } else {
            int wireType = WireFormat.getTagWireType(tag);
            return wireType == 2 ? this.parseUnknownField(defaultInstance, input, extensionRegistry, tag) : input.skipField(tag);
         }
      }

      private <MessageType2 extends MessageLite> void mergeMessageSetExtensionFromCodedStream(
         MessageType2 defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry
      ) throws IOException {
         int typeId = 0;
         ByteString rawBytes = null;
         GeneratedMessageLite.GeneratedExtension<?, ?> extension = null;

         while (true) {
            int tag = input.readTag();
            if (tag == 0) {
               break;
            }

            if (tag == WireFormat.MESSAGE_SET_TYPE_ID_TAG) {
               typeId = input.readUInt32();
               if (typeId != 0) {
                  extension = extensionRegistry.findLiteExtensionByNumber(defaultInstance, typeId);
               }
            } else if (tag == WireFormat.MESSAGE_SET_MESSAGE_TAG) {
               if (typeId != 0 && extension != null) {
                  this.eagerlyMergeMessageSetExtension(input, extension, extensionRegistry, typeId);
                  rawBytes = null;
               } else {
                  rawBytes = input.readBytes();
               }
            } else if (!input.skipField(tag)) {
               break;
            }
         }

         input.checkLastTagWas(WireFormat.MESSAGE_SET_ITEM_END_TAG);
         if (rawBytes != null && typeId != 0) {
            if (extension != null) {
               this.mergeMessageSetExtensionFromBytes(rawBytes, extensionRegistry, extension);
            } else if (rawBytes != null) {
               this.mergeLengthDelimitedField(typeId, rawBytes);
            }
         }
      }

      private void eagerlyMergeMessageSetExtension(
         CodedInputStream input, GeneratedMessageLite.GeneratedExtension<?, ?> extension, ExtensionRegistryLite extensionRegistry, int typeId
      ) throws IOException {
         int tag = WireFormat.makeTag(typeId, 2);
         boolean unused = this.parseExtension(input, extensionRegistry, extension, tag, typeId);
      }

      private void mergeMessageSetExtensionFromBytes(
         ByteString rawBytes, ExtensionRegistryLite extensionRegistry, GeneratedMessageLite.GeneratedExtension<?, ?> extension
      ) throws IOException {
         MessageLite.Builder subBuilder = null;
         MessageLite existingValue = (MessageLite)this.extensions.getField(extension.descriptor);
         if (existingValue != null) {
            subBuilder = existingValue.toBuilder();
         }

         if (subBuilder == null) {
            subBuilder = extension.getMessageDefaultInstance().newBuilderForType();
         }

         subBuilder.mergeFrom(rawBytes, extensionRegistry);
         MessageLite value = subBuilder.build();
         this.ensureExtensionsAreMutable().setField(extension.descriptor, extension.singularToFieldSetType(value));
      }

      @CanIgnoreReturnValue
      FieldSet<GeneratedMessageLite.ExtensionDescriptor> ensureExtensionsAreMutable() {
         if (this.extensions.isImmutable()) {
            this.extensions = this.extensions.clone();
         }

         return this.extensions;
      }

      private void verifyExtensionContainingType(final GeneratedMessageLite.GeneratedExtension<MessageType, ?> extension) {
         if (extension.getContainingTypeDefaultInstance() != this.getDefaultInstanceForType()) {
            throw new IllegalArgumentException(
               "This extension is for a different message type.  Please make sure that you are not suppressing any generics type warnings."
            );
         }
      }

      @Override
      public final <Type> boolean hasExtension(final ExtensionLite<MessageType, Type> extension) {
         GeneratedMessageLite.GeneratedExtension<MessageType, Type> extensionLite = GeneratedMessageLite.checkIsLite(extension);
         this.verifyExtensionContainingType(extensionLite);
         return this.extensions.hasField(extensionLite.descriptor);
      }

      @Override
      public final <Type> int getExtensionCount(final ExtensionLite<MessageType, List<Type>> extension) {
         GeneratedMessageLite.GeneratedExtension<MessageType, List<Type>> extensionLite = GeneratedMessageLite.checkIsLite(extension);
         this.verifyExtensionContainingType(extensionLite);
         return this.extensions.getRepeatedFieldCount(extensionLite.descriptor);
      }

      @Override
      public final <Type> Type getExtension(final ExtensionLite<MessageType, Type> extension) {
         GeneratedMessageLite.GeneratedExtension<MessageType, Type> extensionLite = GeneratedMessageLite.checkIsLite(extension);
         this.verifyExtensionContainingType(extensionLite);
         Object value = this.extensions.getField(extensionLite.descriptor);
         return (Type)(value == null ? extensionLite.defaultValue : extensionLite.fromFieldSetType(value));
      }

      @Override
      public final <Type> Type getExtension(final ExtensionLite<MessageType, List<Type>> extension, final int index) {
         GeneratedMessageLite.GeneratedExtension<MessageType, List<Type>> extensionLite = GeneratedMessageLite.checkIsLite(extension);
         this.verifyExtensionContainingType(extensionLite);
         return (Type)extensionLite.singularFromFieldSetType(this.extensions.getRepeatedField(extensionLite.descriptor, index));
      }

      protected boolean extensionsAreInitialized() {
         return this.extensions.isInitialized();
      }

      protected GeneratedMessageLite.ExtendableMessage<MessageType, BuilderType>.ExtensionWriter newExtensionWriter() {
         return new GeneratedMessageLite.ExtendableMessage.ExtensionWriter(false);
      }

      protected GeneratedMessageLite.ExtendableMessage<MessageType, BuilderType>.ExtensionWriter newMessageSetExtensionWriter() {
         return new GeneratedMessageLite.ExtendableMessage.ExtensionWriter(true);
      }

      protected int extensionsSerializedSize() {
         return this.extensions.getSerializedSize();
      }

      protected int extensionsSerializedSizeAsMessageSet() {
         return this.extensions.getMessageSetSerializedSize();
      }

      protected class ExtensionWriter {
         private final Iterator<Entry<GeneratedMessageLite.ExtensionDescriptor, Object>> iter = ExtendableMessage.this.extensions.iterator();
         private Entry<GeneratedMessageLite.ExtensionDescriptor, Object> next;
         private final boolean messageSetWireFormat;

         private ExtensionWriter(boolean messageSetWireFormat) {
            if (this.iter.hasNext()) {
               this.next = this.iter.next();
            }

            this.messageSetWireFormat = messageSetWireFormat;
         }

         public void writeUntil(final int end, final CodedOutputStream output) throws IOException {
            while (this.next != null && this.next.getKey().getNumber() < end) {
               GeneratedMessageLite.ExtensionDescriptor extension = this.next.getKey();
               if (this.messageSetWireFormat && extension.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !extension.isRepeated()) {
                  output.writeMessageSetExtension(extension.getNumber(), (MessageLite)this.next.getValue());
               } else {
                  FieldSet.writeField(extension, this.next.getValue(), output);
               }

               if (this.iter.hasNext()) {
                  this.next = this.iter.next();
               } else {
                  this.next = null;
               }
            }
         }
      }
   }

   public interface ExtendableMessageOrBuilder<MessageType extends GeneratedMessageLite.ExtendableMessage<MessageType, BuilderType>, BuilderType extends GeneratedMessageLite.ExtendableBuilder<MessageType, BuilderType>>
      extends MessageLiteOrBuilder {
      <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extension);

      <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extension);

      <Type> Type getExtension(ExtensionLite<MessageType, Type> extension);

      <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extension, int index);
   }

   static final class ExtensionDescriptor implements FieldSet.FieldDescriptorLite<GeneratedMessageLite.ExtensionDescriptor> {
      final Internal.EnumLiteMap<?> enumTypeMap;
      final int number;
      final WireFormat.FieldType type;
      final boolean isRepeated;
      final boolean isPacked;

      ExtensionDescriptor(
         final Internal.EnumLiteMap<?> enumTypeMap, final int number, final WireFormat.FieldType type, final boolean isRepeated, final boolean isPacked
      ) {
         this.enumTypeMap = enumTypeMap;
         this.number = number;
         this.type = type;
         this.isRepeated = isRepeated;
         this.isPacked = isPacked;
      }

      @Override
      public int getNumber() {
         return this.number;
      }

      @Override
      public WireFormat.FieldType getLiteType() {
         return this.type;
      }

      @Override
      public WireFormat.JavaType getLiteJavaType() {
         return this.type.getJavaType();
      }

      @Override
      public boolean isRepeated() {
         return this.isRepeated;
      }

      @Override
      public boolean isPacked() {
         return this.isPacked;
      }

      @Override
      public Internal.EnumLiteMap<?> getEnumType() {
         return this.enumTypeMap;
      }

      @Override
      public boolean internalMessageIsImmutable(Object message) {
         return message instanceof MessageLite;
      }

      @Override
      public void internalMergeFrom(Object to, Object from) {
         ((GeneratedMessageLite.Builder)to).mergeFrom((MessageType)from);
      }

      public int compareTo(GeneratedMessageLite.ExtensionDescriptor other) {
         return this.number - other.number;
      }
   }

   public static class GeneratedExtension<ContainingType extends MessageLite, Type> extends ExtensionLite<ContainingType, Type> {
      final ContainingType containingTypeDefaultInstance;
      final Type defaultValue;
      final MessageLite messageDefaultInstance;
      final GeneratedMessageLite.ExtensionDescriptor descriptor;

      GeneratedExtension(
         final ContainingType containingTypeDefaultInstance,
         final Type defaultValue,
         final MessageLite messageDefaultInstance,
         final GeneratedMessageLite.ExtensionDescriptor descriptor,
         final Class singularType
      ) {
         if (containingTypeDefaultInstance == null) {
            throw new IllegalArgumentException("Null containingTypeDefaultInstance");
         } else if (descriptor.getLiteType() == WireFormat.FieldType.MESSAGE && messageDefaultInstance == null) {
            throw new IllegalArgumentException("Null messageDefaultInstance");
         } else {
            this.containingTypeDefaultInstance = containingTypeDefaultInstance;
            this.defaultValue = defaultValue;
            this.messageDefaultInstance = messageDefaultInstance;
            this.descriptor = descriptor;
         }
      }

      public ContainingType getContainingTypeDefaultInstance() {
         return this.containingTypeDefaultInstance;
      }

      @Override
      public int getNumber() {
         return this.descriptor.getNumber();
      }

      @Override
      public MessageLite getMessageDefaultInstance() {
         return this.messageDefaultInstance;
      }

      Object fromFieldSetType(Object value) {
         if (!this.descriptor.isRepeated()) {
            return this.singularFromFieldSetType(value);
         } else if (this.descriptor.getLiteJavaType() != WireFormat.JavaType.ENUM) {
            return value;
         } else {
            ProtobufArrayList<Object> result = new ProtobufArrayList<>();
            result.ensureCapacity(((List)value).size());

            for (Object element : (List)value) {
               result.add(this.singularFromFieldSetType(element));
            }

            result.makeImmutable();
            return result;
         }
      }

      Object singularFromFieldSetType(Object value) {
         return this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM ? this.descriptor.enumTypeMap.findValueByNumber((Integer)value) : value;
      }

      Object toFieldSetType(Object value) {
         if (!this.descriptor.isRepeated()) {
            return this.singularToFieldSetType(value);
         } else if (this.descriptor.getLiteJavaType() != WireFormat.JavaType.ENUM) {
            return value;
         } else {
            List<Object> result = new ArrayList<>();

            for (Object element : (List)value) {
               result.add(this.singularToFieldSetType(element));
            }

            return result;
         }
      }

      Object singularToFieldSetType(Object value) {
         return this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM ? ((Internal.EnumLite)value).getNumber() : value;
      }

      @Override
      public WireFormat.FieldType getLiteType() {
         return this.descriptor.getLiteType();
      }

      @Override
      public boolean isRepeated() {
         return this.descriptor.isRepeated;
      }

      @Override
      public Type getDefaultValue() {
         return this.defaultValue;
      }
   }

   public static enum MethodToInvoke {
      GET_MEMOIZED_IS_INITIALIZED,
      SET_MEMOIZED_IS_INITIALIZED,
      BUILD_MESSAGE_INFO,
      NEW_MUTABLE_INSTANCE,
      NEW_BUILDER,
      GET_DEFAULT_INSTANCE,
      GET_PARSER;
   }

   protected static final class SerializedForm implements Serializable {
      private static final long serialVersionUID = 0L;
      private final Class<?> messageClass;
      private final String messageClassName;
      private final byte[] asBytes;

      public static GeneratedMessageLite.SerializedForm of(MessageLite message) {
         return new GeneratedMessageLite.SerializedForm(message);
      }

      SerializedForm(MessageLite regularForm) {
         this.messageClass = regularForm.getClass();
         this.messageClassName = regularForm.getClass().getName();
         this.asBytes = regularForm.toByteArray();
      }

      protected Object readResolve() throws ObjectStreamException {
         try {
            Class<?> messageClass = this.resolveMessageClass();
            java.lang.reflect.Field defaultInstanceField = messageClass.getDeclaredField("DEFAULT_INSTANCE");
            defaultInstanceField.setAccessible(true);
            MessageLite.Builder builder = ((MessageLite)defaultInstanceField.get(null)).newBuilderForType();
            builder.mergeFrom(this.asBytes);
            return builder.buildPartial();
         } catch (ClassNotFoundException var4) {
            throw new RuntimeException("Unable to find proto buffer class: " + this.messageClassName, var4);
         } catch (NoSuchFieldException var5) {
            throw new RuntimeException("Unable to find DEFAULT_INSTANCE in " + this.messageClassName, var5);
         } catch (SecurityException var6) {
            throw new RuntimeException("Unable to call DEFAULT_INSTANCE in " + this.messageClassName, var6);
         } catch (IllegalAccessException var7) {
            throw new RuntimeException("Unable to call parsePartialFrom", var7);
         } catch (InvalidProtocolBufferException var8) {
            throw new RuntimeException("Unable to understand proto buffer", var8);
         }
      }

      private Class<?> resolveMessageClass() throws ClassNotFoundException {
         if (this.messageClass == null) {
            Class<?> clazz = Class.forName(this.messageClassName, false, this.getClass().getClassLoader());
            if (!MessageLite.class.isAssignableFrom(clazz)) {
               throw new ClassNotFoundException();
            } else {
               return clazz;
            }
         } else {
            return this.messageClass;
         }
      }
   }
}
