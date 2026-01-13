package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;

public final class Struct extends GeneratedMessage implements StructOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int FIELDS_FIELD_NUMBER = 1;
   private MapField<String, Value> fields_;
   private byte memoizedIsInitialized = -1;
   private static final Struct DEFAULT_INSTANCE = new Struct();
   private static final Parser<Struct> PARSER = new AbstractParser<Struct>() {
      public Struct parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         Struct.Builder builder = Struct.newBuilder();

         try {
            builder.mergeFrom(input, extensionRegistry);
         } catch (InvalidProtocolBufferException var5) {
            throw var5.setUnfinishedMessage(builder.buildPartial());
         } catch (UninitializedMessageException var6) {
            throw var6.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
         } catch (IOException var7) {
            throw new InvalidProtocolBufferException(var7).setUnfinishedMessage(builder.buildPartial());
         }

         return builder.buildPartial();
      }
   };

   private Struct(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private Struct() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return StructProto.internal_static_google_protobuf_Struct_descriptor;
   }

   @Override
   protected MapFieldReflectionAccessor internalGetMapFieldReflection(int number) {
      switch (number) {
         case 1:
            return this.internalGetFields();
         default:
            throw new RuntimeException("Invalid map field number: " + number);
      }
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return StructProto.internal_static_google_protobuf_Struct_fieldAccessorTable.ensureFieldAccessorsInitialized(Struct.class, Struct.Builder.class);
   }

   private MapField<String, Value> internalGetFields() {
      return this.fields_ == null ? MapField.emptyMapField(Struct.FieldsDefaultEntryHolder.defaultEntry) : this.fields_;
   }

   @Override
   public int getFieldsCount() {
      return this.internalGetFields().getMap().size();
   }

   @Override
   public boolean containsFields(String key) {
      if (key == null) {
         throw new NullPointerException("map key");
      } else {
         return this.internalGetFields().getMap().containsKey(key);
      }
   }

   @Deprecated
   @Override
   public Map<String, Value> getFields() {
      return this.getFieldsMap();
   }

   @Override
   public Map<String, Value> getFieldsMap() {
      return this.internalGetFields().getMap();
   }

   @Override
   public Value getFieldsOrDefault(String key, Value defaultValue) {
      if (key == null) {
         throw new NullPointerException("map key");
      } else {
         Map<String, Value> map = this.internalGetFields().getMap();
         return map.containsKey(key) ? map.get(key) : defaultValue;
      }
   }

   @Override
   public Value getFieldsOrThrow(String key) {
      if (key == null) {
         throw new NullPointerException("map key");
      } else {
         Map<String, Value> map = this.internalGetFields().getMap();
         if (!map.containsKey(key)) {
            throw new IllegalArgumentException();
         } else {
            return map.get(key);
         }
      }
   }

   @Override
   public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1) {
         return true;
      } else if (isInitialized == 0) {
         return false;
      } else {
         this.memoizedIsInitialized = 1;
         return true;
      }
   }

   @Override
   public void writeTo(CodedOutputStream output) throws IOException {
      GeneratedMessage.serializeStringMapTo(output, this.internalGetFields(), Struct.FieldsDefaultEntryHolder.defaultEntry, 1);
      this.getUnknownFields().writeTo(output);
   }

   @Override
   public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1) {
         return size;
      } else {
         size = 0;

         for (Entry<String, Value> entry : this.internalGetFields().getMap().entrySet()) {
            MapEntry<String, Value> fields__ = Struct.FieldsDefaultEntryHolder.defaultEntry
               .newBuilderForType()
               .setKey(entry.getKey())
               .setValue(entry.getValue())
               .build();
            size += CodedOutputStream.computeMessageSize(1, fields__);
         }

         size += this.getUnknownFields().getSerializedSize();
         this.memoizedSize = size;
         return size;
      }
   }

   @Override
   public boolean equals(final Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Struct)) {
         return super.equals(obj);
      } else {
         Struct other = (Struct)obj;
         return !this.internalGetFields().equals(other.internalGetFields()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
      }
   }

   @Override
   public int hashCode() {
      if (this.memoizedHashCode != 0) {
         return this.memoizedHashCode;
      } else {
         int hash = 41;
         hash = 19 * hash + getDescriptor().hashCode();
         if (!this.internalGetFields().getMap().isEmpty()) {
            hash = 37 * hash + 1;
            hash = 53 * hash + this.internalGetFields().hashCode();
         }

         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static Struct parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Struct parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Struct parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Struct parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Struct parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Struct parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Struct parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Struct parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static Struct parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static Struct parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static Struct parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Struct parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Struct.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Struct.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Struct.Builder newBuilder(Struct prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Struct.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Struct.Builder() : new Struct.Builder().mergeFrom(this);
   }

   protected Struct.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new Struct.Builder(parent);
   }

   public static Struct getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Struct> parser() {
      return PARSER;
   }

   @Override
   public Parser<Struct> getParserForType() {
      return PARSER;
   }

   public Struct getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Struct");
   }

   public static final class Builder extends GeneratedMessage.Builder<Struct.Builder> implements StructOrBuilder {
      private int bitField0_;
      private static final Struct.Builder.FieldsConverter fieldsConverter = new Struct.Builder.FieldsConverter();
      private MapFieldBuilder<String, ValueOrBuilder, Value, Value.Builder> fields_;

      public static final Descriptors.Descriptor getDescriptor() {
         return StructProto.internal_static_google_protobuf_Struct_descriptor;
      }

      @Override
      protected MapFieldReflectionAccessor internalGetMapFieldReflection(int number) {
         switch (number) {
            case 1:
               return this.internalGetFields();
            default:
               throw new RuntimeException("Invalid map field number: " + number);
         }
      }

      @Override
      protected MapFieldReflectionAccessor internalGetMutableMapFieldReflection(int number) {
         switch (number) {
            case 1:
               return this.internalGetMutableFields();
            default:
               throw new RuntimeException("Invalid map field number: " + number);
         }
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return StructProto.internal_static_google_protobuf_Struct_fieldAccessorTable.ensureFieldAccessorsInitialized(Struct.class, Struct.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public Struct.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.internalGetMutableFields().clear();
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return StructProto.internal_static_google_protobuf_Struct_descriptor;
      }

      public Struct getDefaultInstanceForType() {
         return Struct.getDefaultInstance();
      }

      public Struct build() {
         Struct result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public Struct buildPartial() {
         Struct result = new Struct(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(Struct result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.fields_ = this.internalGetFields().build(Struct.FieldsDefaultEntryHolder.defaultEntry);
         }
      }

      public Struct.Builder mergeFrom(Message other) {
         if (other instanceof Struct) {
            return this.mergeFrom((Struct)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Struct.Builder mergeFrom(Struct other) {
         if (other == Struct.getDefaultInstance()) {
            return this;
         } else {
            this.internalGetMutableFields().mergeFrom(other.internalGetFields());
            this.bitField0_ |= 1;
            this.mergeUnknownFields(other.getUnknownFields());
            this.onChanged();
            return this;
         }
      }

      @Override
      public final boolean isInitialized() {
         return true;
      }

      public Struct.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        MapEntry<String, Value> fields__ = input.readMessage(Struct.FieldsDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
                        this.internalGetMutableFields().ensureBuilderMap().put(fields__.getKey(), fields__.getValue());
                        this.bitField0_ |= 1;
                        break;
                     default:
                        if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var9) {
               throw var9.unwrapIOException();
            } finally {
               this.onChanged();
            }

            return this;
         }
      }

      private MapFieldBuilder<String, ValueOrBuilder, Value, Value.Builder> internalGetFields() {
         return this.fields_ == null ? new MapFieldBuilder<>(fieldsConverter) : this.fields_;
      }

      private MapFieldBuilder<String, ValueOrBuilder, Value, Value.Builder> internalGetMutableFields() {
         if (this.fields_ == null) {
            this.fields_ = new MapFieldBuilder<>(fieldsConverter);
         }

         this.bitField0_ |= 1;
         this.onChanged();
         return this.fields_;
      }

      @Override
      public int getFieldsCount() {
         return this.internalGetFields().ensureBuilderMap().size();
      }

      @Override
      public boolean containsFields(String key) {
         if (key == null) {
            throw new NullPointerException("map key");
         } else {
            return this.internalGetFields().ensureBuilderMap().containsKey(key);
         }
      }

      @Deprecated
      @Override
      public Map<String, Value> getFields() {
         return this.getFieldsMap();
      }

      @Override
      public Map<String, Value> getFieldsMap() {
         return this.internalGetFields().getImmutableMap();
      }

      @Override
      public Value getFieldsOrDefault(String key, Value defaultValue) {
         if (key == null) {
            throw new NullPointerException("map key");
         } else {
            Map<String, ValueOrBuilder> map = this.internalGetMutableFields().ensureBuilderMap();
            return map.containsKey(key) ? fieldsConverter.build(map.get(key)) : defaultValue;
         }
      }

      @Override
      public Value getFieldsOrThrow(String key) {
         if (key == null) {
            throw new NullPointerException("map key");
         } else {
            Map<String, ValueOrBuilder> map = this.internalGetMutableFields().ensureBuilderMap();
            if (!map.containsKey(key)) {
               throw new IllegalArgumentException();
            } else {
               return fieldsConverter.build(map.get(key));
            }
         }
      }

      public Struct.Builder clearFields() {
         this.bitField0_ &= -2;
         this.internalGetMutableFields().clear();
         return this;
      }

      public Struct.Builder removeFields(String key) {
         if (key == null) {
            throw new NullPointerException("map key");
         } else {
            this.internalGetMutableFields().ensureBuilderMap().remove(key);
            return this;
         }
      }

      @Deprecated
      public Map<String, Value> getMutableFields() {
         this.bitField0_ |= 1;
         return this.internalGetMutableFields().ensureMessageMap();
      }

      public Struct.Builder putFields(String key, Value value) {
         if (key == null) {
            throw new NullPointerException("map key");
         } else if (value == null) {
            throw new NullPointerException("map value");
         } else {
            this.internalGetMutableFields().ensureBuilderMap().put(key, value);
            this.bitField0_ |= 1;
            return this;
         }
      }

      public Struct.Builder putAllFields(Map<String, Value> values) {
         for (Entry<String, Value> e : values.entrySet()) {
            if (e.getKey() == null || e.getValue() == null) {
               throw new NullPointerException();
            }
         }

         this.internalGetMutableFields().ensureBuilderMap().putAll(values);
         this.bitField0_ |= 1;
         return this;
      }

      public Value.Builder putFieldsBuilderIfAbsent(String key) {
         Map<String, ValueOrBuilder> builderMap = this.internalGetMutableFields().ensureBuilderMap();
         ValueOrBuilder entry = builderMap.get(key);
         if (entry == null) {
            entry = Value.newBuilder();
            builderMap.put(key, entry);
         }

         if (entry instanceof Value) {
            entry = ((Value)entry).toBuilder();
            builderMap.put(key, entry);
         }

         return (Value.Builder)entry;
      }

      private static final class FieldsConverter implements MapFieldBuilder.Converter<String, ValueOrBuilder, Value> {
         private FieldsConverter() {
         }

         public Value build(ValueOrBuilder val) {
            return val instanceof Value ? (Value)val : ((Value.Builder)val).build();
         }

         @Override
         public MapEntry<String, Value> defaultEntry() {
            return Struct.FieldsDefaultEntryHolder.defaultEntry;
         }
      }
   }

   private static final class FieldsDefaultEntryHolder {
      static final MapEntry<String, Value> defaultEntry = MapEntry.newDefaultInstance(
         StructProto.internal_static_google_protobuf_Struct_FieldsEntry_descriptor,
         WireFormat.FieldType.STRING,
         "",
         WireFormat.FieldType.MESSAGE,
         Value.getDefaultInstance()
      );
   }
}
