package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class DynamicMessage extends AbstractMessage {
   private final Descriptors.Descriptor type;
   private final FieldSet<Descriptors.FieldDescriptor> fields;
   private final Descriptors.FieldDescriptor[] oneofCases;
   private final UnknownFieldSet unknownFields;
   private int memoizedSize = -1;

   DynamicMessage(
      Descriptors.Descriptor type, FieldSet<Descriptors.FieldDescriptor> fields, Descriptors.FieldDescriptor[] oneofCases, UnknownFieldSet unknownFields
   ) {
      this.type = type;
      this.fields = fields;
      this.oneofCases = oneofCases;
      this.unknownFields = unknownFields;
   }

   public static DynamicMessage getDefaultInstance(Descriptors.Descriptor type) {
      int oneofDeclCount = type.toProto().getOneofDeclCount();
      Descriptors.FieldDescriptor[] oneofCases = new Descriptors.FieldDescriptor[oneofDeclCount];
      return new DynamicMessage(type, FieldSet.emptySet(), oneofCases, UnknownFieldSet.getDefaultInstance());
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, CodedInputStream input) throws IOException {
      return newBuilder(type).mergeFrom(input).buildParsed();
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, CodedInputStream input, ExtensionRegistry extensionRegistry) throws IOException {
      return newBuilder(type).mergeFrom(input, extensionRegistry).buildParsed();
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, ByteString data) throws InvalidProtocolBufferException {
      return newBuilder(type).mergeFrom(data).buildParsed();
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, ByteString data, ExtensionRegistry extensionRegistry) throws InvalidProtocolBufferException {
      return newBuilder(type).mergeFrom(data, extensionRegistry).buildParsed();
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, byte[] data) throws InvalidProtocolBufferException {
      return newBuilder(type).mergeFrom(data).buildParsed();
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, byte[] data, ExtensionRegistry extensionRegistry) throws InvalidProtocolBufferException {
      return newBuilder(type).mergeFrom(data, extensionRegistry).buildParsed();
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, InputStream input) throws IOException {
      return newBuilder(type).mergeFrom(input).buildParsed();
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, InputStream input, ExtensionRegistry extensionRegistry) throws IOException {
      return newBuilder(type).mergeFrom(input, extensionRegistry).buildParsed();
   }

   public static DynamicMessage.Builder newBuilder(Descriptors.Descriptor type) {
      return new DynamicMessage.Builder(type);
   }

   public static DynamicMessage.Builder newBuilder(Message prototype) {
      return new DynamicMessage.Builder(prototype.getDescriptorForType()).mergeFrom(prototype);
   }

   @Override
   public Descriptors.Descriptor getDescriptorForType() {
      return this.type;
   }

   public DynamicMessage getDefaultInstanceForType() {
      return getDefaultInstance(this.type);
   }

   @Override
   public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
      return this.fields.getAllFields();
   }

   @Override
   public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
      this.verifyOneofContainingType(oneof);
      Descriptors.FieldDescriptor field = this.oneofCases[oneof.getIndex()];
      return field != null;
   }

   @Override
   public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
      this.verifyOneofContainingType(oneof);
      return this.oneofCases[oneof.getIndex()];
   }

   @Override
   public boolean hasField(Descriptors.FieldDescriptor field) {
      this.verifyContainingType(field);
      return this.fields.hasField(field);
   }

   @Override
   public Object getField(Descriptors.FieldDescriptor field) {
      this.verifyContainingType(field);
      Object result = this.fields.getField(field);
      if (result == null) {
         if (field.isRepeated()) {
            result = Collections.emptyList();
         } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            result = getDefaultInstance(field.getMessageType());
         } else {
            result = field.getDefaultValue();
         }
      }

      return result;
   }

   @Override
   public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
      this.verifyContainingType(field);
      return this.fields.getRepeatedFieldCount(field);
   }

   @Override
   public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
      this.verifyContainingType(field);
      return this.fields.getRepeatedField(field, index);
   }

   @Override
   public UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   static boolean isInitialized(Descriptors.Descriptor type, FieldSet<Descriptors.FieldDescriptor> fields) {
      for (Descriptors.FieldDescriptor field : type.getFields()) {
         if (field.isRequired() && !fields.hasField(field)) {
            return false;
         }
      }

      return fields.isInitialized();
   }

   @Override
   public boolean isInitialized() {
      return isInitialized(this.type, this.fields);
   }

   @Override
   public void writeTo(CodedOutputStream output) throws IOException {
      if (this.type.getOptions().getMessageSetWireFormat()) {
         this.fields.writeMessageSetTo(output);
         this.unknownFields.writeAsMessageSetTo(output);
      } else {
         this.fields.writeTo(output);
         this.unknownFields.writeTo(output);
      }
   }

   @Override
   public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1) {
         return size;
      } else {
         if (this.type.getOptions().getMessageSetWireFormat()) {
            size = this.fields.getMessageSetSerializedSize();
            size += this.unknownFields.getSerializedSizeAsMessageSet();
         } else {
            size = this.fields.getSerializedSize();
            size += this.unknownFields.getSerializedSize();
         }

         this.memoizedSize = size;
         return size;
      }
   }

   public DynamicMessage.Builder newBuilderForType() {
      return new DynamicMessage.Builder(this.type);
   }

   public DynamicMessage.Builder toBuilder() {
      return this.newBuilderForType().mergeFrom(this);
   }

   @Override
   public Parser<DynamicMessage> getParserForType() {
      return new AbstractParser<DynamicMessage>() {
         public DynamicMessage parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            DynamicMessage.Builder builder = DynamicMessage.newBuilder(DynamicMessage.this.type);

            try {
               builder.mergeFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var5) {
               throw var5.setUnfinishedMessage(builder.buildPartial());
            } catch (IOException var6) {
               throw new InvalidProtocolBufferException(var6).setUnfinishedMessage(builder.buildPartial());
            }

            return builder.buildPartial();
         }
      };
   }

   private void verifyContainingType(Descriptors.FieldDescriptor field) {
      if (field.getContainingType() != this.type) {
         throw new IllegalArgumentException("FieldDescriptor does not match message type.");
      }
   }

   private void verifyOneofContainingType(Descriptors.OneofDescriptor oneof) {
      if (oneof.getContainingType() != this.type) {
         throw new IllegalArgumentException("OneofDescriptor does not match message type.");
      }
   }

   public static final class Builder extends AbstractMessage.Builder<DynamicMessage.Builder> {
      private final Descriptors.Descriptor type;
      private FieldSet.Builder<Descriptors.FieldDescriptor> fields;
      private final Descriptors.FieldDescriptor[] oneofCases;
      private UnknownFieldSet unknownFields;

      private Builder(Descriptors.Descriptor type) {
         this.type = type;
         this.fields = FieldSet.newBuilder();
         this.unknownFields = UnknownFieldSet.getDefaultInstance();
         this.oneofCases = new Descriptors.FieldDescriptor[type.toProto().getOneofDeclCount()];
      }

      public DynamicMessage.Builder clear() {
         this.fields = FieldSet.newBuilder();
         this.unknownFields = UnknownFieldSet.getDefaultInstance();
         return this;
      }

      public DynamicMessage.Builder mergeFrom(Message other) {
         if (other instanceof DynamicMessage) {
            DynamicMessage otherDynamicMessage = (DynamicMessage)other;
            if (otherDynamicMessage.type != this.type) {
               throw new IllegalArgumentException("mergeFrom(Message) can only merge messages of the same type.");
            } else {
               this.fields.mergeFrom(otherDynamicMessage.fields);
               this.mergeUnknownFields(otherDynamicMessage.unknownFields);

               for (int i = 0; i < this.oneofCases.length; i++) {
                  if (this.oneofCases[i] == null) {
                     this.oneofCases[i] = otherDynamicMessage.oneofCases[i];
                  } else if (otherDynamicMessage.oneofCases[i] != null && this.oneofCases[i] != otherDynamicMessage.oneofCases[i]) {
                     this.fields.clearField(this.oneofCases[i]);
                     this.oneofCases[i] = otherDynamicMessage.oneofCases[i];
                  }
               }

               return this;
            }
         } else {
            return (DynamicMessage.Builder)super.mergeFrom(other);
         }
      }

      public DynamicMessage build() {
         if (!this.isInitialized()) {
            throw newUninitializedMessageException(
               new DynamicMessage(this.type, this.fields.build(), Arrays.copyOf(this.oneofCases, this.oneofCases.length), this.unknownFields)
            );
         } else {
            return this.buildPartial();
         }
      }

      private DynamicMessage buildParsed() throws InvalidProtocolBufferException {
         if (!this.isInitialized()) {
            throw newUninitializedMessageException(
                  new DynamicMessage(this.type, this.fields.build(), Arrays.copyOf(this.oneofCases, this.oneofCases.length), this.unknownFields)
               )
               .asInvalidProtocolBufferException();
         } else {
            return this.buildPartial();
         }
      }

      public DynamicMessage buildPartial() {
         if (this.type.getOptions().getMapEntry()) {
            for (Descriptors.FieldDescriptor field : this.type.getFields()) {
               if (field.isOptional() && !this.fields.hasField(field)) {
                  if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                     this.fields.setField(field, DynamicMessage.getDefaultInstance(field.getMessageType()));
                  } else {
                     this.fields.setField(field, field.getDefaultValue());
                  }
               }
            }
         }

         return new DynamicMessage(this.type, this.fields.buildPartial(), Arrays.copyOf(this.oneofCases, this.oneofCases.length), this.unknownFields);
      }

      public DynamicMessage.Builder clone() {
         DynamicMessage.Builder result = new DynamicMessage.Builder(this.type);
         result.fields.mergeFrom(this.fields.build());
         result.mergeUnknownFields(this.unknownFields);
         System.arraycopy(this.oneofCases, 0, result.oneofCases, 0, this.oneofCases.length);
         return result;
      }

      @Override
      public boolean isInitialized() {
         for (Descriptors.FieldDescriptor field : this.type.getFields()) {
            if (field.isRequired() && !this.fields.hasField(field)) {
               return false;
            }
         }

         return this.fields.isInitialized();
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return this.type;
      }

      public DynamicMessage getDefaultInstanceForType() {
         return DynamicMessage.getDefaultInstance(this.type);
      }

      @Override
      public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
         return this.fields.getAllFields();
      }

      public DynamicMessage.Builder newBuilderForField(Descriptors.FieldDescriptor field) {
         this.verifyContainingType(field);
         if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            throw new IllegalArgumentException("newBuilderForField is only valid for fields with message type.");
         } else {
            return new DynamicMessage.Builder(field.getMessageType());
         }
      }

      @Override
      public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
         this.verifyOneofContainingType(oneof);
         Descriptors.FieldDescriptor field = this.oneofCases[oneof.getIndex()];
         return field != null;
      }

      @Override
      public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
         this.verifyOneofContainingType(oneof);
         return this.oneofCases[oneof.getIndex()];
      }

      public DynamicMessage.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
         this.verifyOneofContainingType(oneof);
         Descriptors.FieldDescriptor field = this.oneofCases[oneof.getIndex()];
         if (field != null) {
            this.clearField(field);
         }

         return this;
      }

      @Override
      public boolean hasField(Descriptors.FieldDescriptor field) {
         this.verifyContainingType(field);
         return this.fields.hasField(field);
      }

      @Override
      public Object getField(Descriptors.FieldDescriptor field) {
         this.verifyContainingType(field);
         Object result = this.fields.getField(field);
         if (result == null) {
            if (field.isRepeated()) {
               result = Collections.emptyList();
            } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
               result = DynamicMessage.getDefaultInstance(field.getMessageType());
            } else {
               result = field.getDefaultValue();
            }
         }

         return result;
      }

      public DynamicMessage.Builder setField(Descriptors.FieldDescriptor field, Object value) {
         this.verifyContainingType(field);
         this.verifyType(field, value);
         Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
         if (oneofDescriptor != null) {
            int index = oneofDescriptor.getIndex();
            Descriptors.FieldDescriptor oldField = this.oneofCases[index];
            if (oldField != null && oldField != field) {
               this.fields.clearField(oldField);
            }

            this.oneofCases[index] = field;
         } else if (!field.hasPresence() && (field.isRepeated() ? ((List)value).isEmpty() : value.equals(field.getDefaultValue()))) {
            this.fields.clearField(field);
            return this;
         }

         this.fields.setField(field, value);
         return this;
      }

      public DynamicMessage.Builder clearField(Descriptors.FieldDescriptor field) {
         this.verifyContainingType(field);
         Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
         if (oneofDescriptor != null) {
            int index = oneofDescriptor.getIndex();
            if (this.oneofCases[index] == field) {
               this.oneofCases[index] = null;
            }
         }

         this.fields.clearField(field);
         return this;
      }

      @Override
      public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
         this.verifyContainingType(field);
         return this.fields.getRepeatedFieldCount(field);
      }

      @Override
      public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
         this.verifyContainingType(field);
         return this.fields.getRepeatedField(field, index);
      }

      public DynamicMessage.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
         this.verifyContainingType(field);
         this.verifySingularValueType(field, value);
         this.fields.setRepeatedField(field, index, value);
         return this;
      }

      public DynamicMessage.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
         this.verifyContainingType(field);
         this.verifySingularValueType(field, value);
         this.fields.addRepeatedField(field, value);
         return this;
      }

      @Override
      public UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      public DynamicMessage.Builder setUnknownFields(UnknownFieldSet unknownFields) {
         this.unknownFields = unknownFields;
         return this;
      }

      public DynamicMessage.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
         this.unknownFields = UnknownFieldSet.newBuilder(this.unknownFields).mergeFrom(unknownFields).build();
         return this;
      }

      private void verifyContainingType(Descriptors.FieldDescriptor field) {
         if (field.getContainingType() != this.type) {
            throw new IllegalArgumentException("FieldDescriptor does not match message type.");
         }
      }

      private void verifyOneofContainingType(Descriptors.OneofDescriptor oneof) {
         if (oneof.getContainingType() != this.type) {
            throw new IllegalArgumentException("OneofDescriptor does not match message type.");
         }
      }

      private void verifySingularValueType(Descriptors.FieldDescriptor field, Object value) {
         switch (field.getType()) {
            case ENUM:
               Internal.checkNotNull(value);
               if (!(value instanceof Descriptors.EnumValueDescriptor)) {
                  throw new IllegalArgumentException("DynamicMessage should use EnumValueDescriptor to set Enum Value.");
               }
               break;
            case MESSAGE:
               if (value instanceof Message.Builder) {
                  throw new IllegalArgumentException(
                     String.format(
                        "Wrong object type used with protocol message reflection.\nField number: %d, field java type: %s, value type: %s\n",
                        field.getNumber(),
                        field.getLiteType().getJavaType(),
                        value.getClass().getName()
                     )
                  );
               }
         }
      }

      private void verifyType(Descriptors.FieldDescriptor field, Object value) {
         if (field.isRepeated()) {
            for (Object item : (List)value) {
               this.verifySingularValueType(field, item);
            }
         } else {
            this.verifySingularValueType(field, value);
         }
      }

      @Override
      public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
         this.verifyContainingType(field);
         if (field.isMapField()) {
            throw new UnsupportedOperationException("Nested builder not supported for map fields.");
         } else if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
         } else {
            Object existingValue = this.fields.getFieldAllowBuilders(field);
            Message.Builder builder = (Message.Builder)(existingValue == null
               ? new DynamicMessage.Builder(field.getMessageType())
               : toMessageBuilder(existingValue));
            this.fields.setField(field, builder);
            return builder;
         }
      }

      @Override
      public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
         this.verifyContainingType(field);
         if (field.isMapField()) {
            throw new UnsupportedOperationException("Map fields cannot be repeated");
         } else if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
         } else {
            Message.Builder builder = toMessageBuilder(this.fields.getRepeatedFieldAllowBuilders(field, index));
            this.fields.setRepeatedField(field, index, builder);
            return builder;
         }
      }

      private static Message.Builder toMessageBuilder(Object o) {
         if (o instanceof Message.Builder) {
            return (Message.Builder)o;
         } else {
            if (o instanceof LazyField) {
               o = ((LazyField)o).getValue();
            }

            if (o instanceof Message) {
               return ((Message)o).toBuilder();
            } else {
               throw new IllegalArgumentException(String.format("Cannot convert %s to Message.Builder", o.getClass()));
            }
         }
      }
   }
}
