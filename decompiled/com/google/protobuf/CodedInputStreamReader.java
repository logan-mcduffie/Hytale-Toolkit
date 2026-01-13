package com.google.protobuf;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@CheckReturnValue
final class CodedInputStreamReader implements Reader {
   private static final int FIXED32_MULTIPLE_MASK = 3;
   private static final int FIXED64_MULTIPLE_MASK = 7;
   private static final int NEXT_TAG_UNSET = 0;
   private final CodedInputStream input;
   private int tag;
   private int endGroupTag;
   private int nextTag = 0;

   public static CodedInputStreamReader forCodedInput(CodedInputStream input) {
      return input.wrapper != null ? (CodedInputStreamReader)input.wrapper : new CodedInputStreamReader(input);
   }

   private CodedInputStreamReader(CodedInputStream input) {
      this.input = Internal.checkNotNull(input, "input");
      this.input.wrapper = this;
   }

   @Override
   public boolean shouldDiscardUnknownFields() {
      return this.input.shouldDiscardUnknownFields();
   }

   @Override
   public int getFieldNumber() throws IOException {
      if (this.nextTag != 0) {
         this.tag = this.nextTag;
         this.nextTag = 0;
      } else {
         this.tag = this.input.readTag();
      }

      return this.tag != 0 && this.tag != this.endGroupTag ? WireFormat.getTagFieldNumber(this.tag) : Integer.MAX_VALUE;
   }

   @Override
   public int getTag() {
      return this.tag;
   }

   @Override
   public boolean skipField() throws IOException {
      return !this.input.isAtEnd() && this.tag != this.endGroupTag ? this.input.skipField(this.tag) : false;
   }

   private void requireWireType(int requiredWireType) throws IOException {
      if (WireFormat.getTagWireType(this.tag) != requiredWireType) {
         throw InvalidProtocolBufferException.invalidWireType();
      }
   }

   @Override
   public double readDouble() throws IOException {
      this.requireWireType(1);
      return this.input.readDouble();
   }

   @Override
   public float readFloat() throws IOException {
      this.requireWireType(5);
      return this.input.readFloat();
   }

   @Override
   public long readUInt64() throws IOException {
      this.requireWireType(0);
      return this.input.readUInt64();
   }

   @Override
   public long readInt64() throws IOException {
      this.requireWireType(0);
      return this.input.readInt64();
   }

   @Override
   public int readInt32() throws IOException {
      this.requireWireType(0);
      return this.input.readInt32();
   }

   @Override
   public long readFixed64() throws IOException {
      this.requireWireType(1);
      return this.input.readFixed64();
   }

   @Override
   public int readFixed32() throws IOException {
      this.requireWireType(5);
      return this.input.readFixed32();
   }

   @Override
   public boolean readBool() throws IOException {
      this.requireWireType(0);
      return this.input.readBool();
   }

   @Override
   public String readString() throws IOException {
      this.requireWireType(2);
      return this.input.readString();
   }

   @Override
   public String readStringRequireUtf8() throws IOException {
      this.requireWireType(2);
      return this.input.readStringRequireUtf8();
   }

   @Override
   public <T> T readMessage(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
      this.requireWireType(2);
      return this.readMessage(Protobuf.getInstance().schemaFor(clazz), extensionRegistry);
   }

   @Override
   public <T> T readMessageBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      this.requireWireType(2);
      return this.readMessage(schema, extensionRegistry);
   }

   @Deprecated
   @Override
   public <T> T readGroup(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
      this.requireWireType(3);
      return this.readGroup(Protobuf.getInstance().schemaFor(clazz), extensionRegistry);
   }

   @Deprecated
   @Override
   public <T> T readGroupBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      this.requireWireType(3);
      return this.readGroup(schema, extensionRegistry);
   }

   @Override
   public <T> void mergeMessageField(T target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      this.requireWireType(2);
      this.mergeMessageFieldInternal(target, schema, extensionRegistry);
   }

   private <T> void mergeMessageFieldInternal(T target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      int size = this.input.readUInt32();
      this.input.checkRecursionLimit();
      int prevLimit = this.input.pushLimit(size);
      this.input.messageDepth++;
      schema.mergeFrom(target, this, extensionRegistry);
      this.input.checkLastTagWas(0);
      this.input.messageDepth--;
      this.input.popLimit(prevLimit);
   }

   private <T> T readMessage(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      T newInstance = schema.newInstance();
      this.mergeMessageFieldInternal(newInstance, schema, extensionRegistry);
      schema.makeImmutable(newInstance);
      return newInstance;
   }

   @Override
   public <T> void mergeGroupField(T target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      this.requireWireType(3);
      this.mergeGroupFieldInternal(target, schema, extensionRegistry);
   }

   private <T> void mergeGroupFieldInternal(T target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      int prevEndGroupTag = this.endGroupTag;
      this.endGroupTag = WireFormat.makeTag(WireFormat.getTagFieldNumber(this.tag), 4);

      try {
         schema.mergeFrom(target, this, extensionRegistry);
         if (this.tag != this.endGroupTag) {
            throw InvalidProtocolBufferException.parseFailure();
         }
      } finally {
         this.endGroupTag = prevEndGroupTag;
      }
   }

   private <T> T readGroup(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      T newInstance = schema.newInstance();
      this.mergeGroupFieldInternal(newInstance, schema, extensionRegistry);
      schema.makeImmutable(newInstance);
      return newInstance;
   }

   @Override
   public ByteString readBytes() throws IOException {
      this.requireWireType(2);
      return this.input.readBytes();
   }

   @Override
   public int readUInt32() throws IOException {
      this.requireWireType(0);
      return this.input.readUInt32();
   }

   @Override
   public int readEnum() throws IOException {
      this.requireWireType(0);
      return this.input.readEnum();
   }

   @Override
   public int readSFixed32() throws IOException {
      this.requireWireType(5);
      return this.input.readSFixed32();
   }

   @Override
   public long readSFixed64() throws IOException {
      this.requireWireType(1);
      return this.input.readSFixed64();
   }

   @Override
   public int readSInt32() throws IOException {
      this.requireWireType(0);
      return this.input.readSInt32();
   }

   @Override
   public long readSInt64() throws IOException {
      this.requireWireType(0);
      return this.input.readSInt64();
   }

   @Override
   public void readDoubleList(List<Double> target) throws IOException {
      if (target instanceof DoubleArrayList) {
         DoubleArrayList plist = (DoubleArrayList)target;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 1:
               int nextTag;
               do {
                  plist.addDouble(this.input.readDouble());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               this.verifyPackedFixed64Length(bytes);
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  plist.addDouble(this.input.readDouble());
               } while (this.input.getTotalBytesRead() < endPos);

               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 1:
               int nextTag;
               do {
                  target.add(this.input.readDouble());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               this.verifyPackedFixed64Length(bytes);
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  target.add(this.input.readDouble());
               } while (this.input.getTotalBytesRead() < endPos);

               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   @Override
   public void readFloatList(List<Float> target) throws IOException {
      if (target instanceof FloatArrayList) {
         FloatArrayList plist = (FloatArrayList)target;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 2:
               int bytes = this.input.readUInt32();
               this.verifyPackedFixed32Length(bytes);
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  plist.addFloat(this.input.readFloat());
               } while (this.input.getTotalBytesRead() < endPos);

               return;
            case 5:
               int nextTag;
               do {
                  plist.addFloat(this.input.readFloat());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 2:
               int bytes = this.input.readUInt32();
               this.verifyPackedFixed32Length(bytes);
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  target.add(this.input.readFloat());
               } while (this.input.getTotalBytesRead() < endPos);

               return;
            case 5:
               int nextTag;
               do {
                  target.add(this.input.readFloat());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   @Override
   public void readUInt64List(List<Long> target) throws IOException {
      if (target instanceof LongArrayList) {
         LongArrayList plist = (LongArrayList)target;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  plist.addLong(this.input.readUInt64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  plist.addLong(this.input.readUInt64());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  target.add(this.input.readUInt64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  target.add(this.input.readUInt64());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   @Override
   public void readInt64List(List<Long> target) throws IOException {
      if (target instanceof LongArrayList) {
         LongArrayList plist = (LongArrayList)target;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  plist.addLong(this.input.readInt64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  plist.addLong(this.input.readInt64());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  target.add(this.input.readInt64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  target.add(this.input.readInt64());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   @Override
   public void readInt32List(List<Integer> target) throws IOException {
      if (target instanceof IntArrayList) {
         IntArrayList plist = (IntArrayList)target;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  plist.addInt(this.input.readInt32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  plist.addInt(this.input.readInt32());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  target.add(this.input.readInt32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  target.add(this.input.readInt32());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   @Override
   public void readFixed64List(List<Long> target) throws IOException {
      if (target instanceof LongArrayList) {
         LongArrayList plist = (LongArrayList)target;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 1:
               int nextTag;
               do {
                  plist.addLong(this.input.readFixed64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               this.verifyPackedFixed64Length(bytes);
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  plist.addLong(this.input.readFixed64());
               } while (this.input.getTotalBytesRead() < endPos);

               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 1:
               int nextTag;
               do {
                  target.add(this.input.readFixed64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               this.verifyPackedFixed64Length(bytes);
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  target.add(this.input.readFixed64());
               } while (this.input.getTotalBytesRead() < endPos);

               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   @Override
   public void readFixed32List(List<Integer> target) throws IOException {
      if (target instanceof IntArrayList) {
         IntArrayList plist = (IntArrayList)target;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 2:
               int bytes = this.input.readUInt32();
               this.verifyPackedFixed32Length(bytes);
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  plist.addInt(this.input.readFixed32());
               } while (this.input.getTotalBytesRead() < endPos);

               return;
            case 5:
               int nextTag;
               do {
                  plist.addInt(this.input.readFixed32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 2:
               int bytes = this.input.readUInt32();
               this.verifyPackedFixed32Length(bytes);
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  target.add(this.input.readFixed32());
               } while (this.input.getTotalBytesRead() < endPos);

               return;
            case 5:
               int nextTag;
               do {
                  target.add(this.input.readFixed32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   @Override
   public void readBoolList(List<Boolean> target) throws IOException {
      if (target instanceof BooleanArrayList) {
         BooleanArrayList plist = (BooleanArrayList)target;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  plist.addBoolean(this.input.readBool());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  plist.addBoolean(this.input.readBool());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  target.add(this.input.readBool());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  target.add(this.input.readBool());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   @Override
   public void readStringList(List<String> target) throws IOException {
      this.readStringListInternal(target, false);
   }

   @Override
   public void readStringListRequireUtf8(List<String> target) throws IOException {
      this.readStringListInternal(target, true);
   }

   public void readStringListInternal(List<String> target, boolean requireUtf8) throws IOException {
      if (WireFormat.getTagWireType(this.tag) != 2) {
         throw InvalidProtocolBufferException.invalidWireType();
      } else if (target instanceof LazyStringList && !requireUtf8) {
         LazyStringList lazyList = (LazyStringList)target;

         int nextTag;
         do {
            lazyList.add(this.readBytes());
            if (this.input.isAtEnd()) {
               return;
            }

            nextTag = this.input.readTag();
         } while (nextTag == this.tag);

         this.nextTag = nextTag;
      } else {
         int nextTag;
         do {
            target.add(requireUtf8 ? this.readStringRequireUtf8() : this.readString());
            if (this.input.isAtEnd()) {
               return;
            }

            nextTag = this.input.readTag();
         } while (nextTag == this.tag);

         this.nextTag = nextTag;
      }
   }

   @Override
   public <T> void readMessageList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
      Schema<T> schema = Protobuf.getInstance().schemaFor(targetType);
      this.readMessageList(target, schema, extensionRegistry);
   }

   @Override
   public <T> void readMessageList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      if (WireFormat.getTagWireType(this.tag) != 2) {
         throw InvalidProtocolBufferException.invalidWireType();
      } else {
         int listTag = this.tag;

         int nextTag;
         do {
            target.add(this.readMessage(schema, extensionRegistry));
            if (this.input.isAtEnd() || this.nextTag != 0) {
               return;
            }

            nextTag = this.input.readTag();
         } while (nextTag == listTag);

         this.nextTag = nextTag;
      }
   }

   @Deprecated
   @Override
   public <T> void readGroupList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
      Schema<T> schema = Protobuf.getInstance().schemaFor(targetType);
      this.readGroupList(target, schema, extensionRegistry);
   }

   @Deprecated
   @Override
   public <T> void readGroupList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      if (WireFormat.getTagWireType(this.tag) != 3) {
         throw InvalidProtocolBufferException.invalidWireType();
      } else {
         int listTag = this.tag;

         int nextTag;
         do {
            target.add(this.readGroup(schema, extensionRegistry));
            if (this.input.isAtEnd() || this.nextTag != 0) {
               return;
            }

            nextTag = this.input.readTag();
         } while (nextTag == listTag);

         this.nextTag = nextTag;
      }
   }

   @Override
   public void readBytesList(List<ByteString> target) throws IOException {
      if (WireFormat.getTagWireType(this.tag) != 2) {
         throw InvalidProtocolBufferException.invalidWireType();
      } else {
         int nextTag;
         do {
            target.add(this.readBytes());
            if (this.input.isAtEnd()) {
               return;
            }

            nextTag = this.input.readTag();
         } while (nextTag == this.tag);

         this.nextTag = nextTag;
      }
   }

   @Override
   public void readUInt32List(List<Integer> target) throws IOException {
      if (target instanceof IntArrayList) {
         IntArrayList plist = (IntArrayList)target;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  plist.addInt(this.input.readUInt32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  plist.addInt(this.input.readUInt32());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  target.add(this.input.readUInt32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  target.add(this.input.readUInt32());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   @Override
   public void readEnumList(List<Integer> target) throws IOException {
      if (target instanceof IntArrayList) {
         IntArrayList plist = (IntArrayList)target;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  plist.addInt(this.input.readEnum());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  plist.addInt(this.input.readEnum());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  target.add(this.input.readEnum());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  target.add(this.input.readEnum());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   @Override
   public void readSFixed32List(List<Integer> target) throws IOException {
      if (target instanceof IntArrayList) {
         IntArrayList plist = (IntArrayList)target;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 2:
               int bytes = this.input.readUInt32();
               this.verifyPackedFixed32Length(bytes);
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  plist.addInt(this.input.readSFixed32());
               } while (this.input.getTotalBytesRead() < endPos);

               return;
            case 5:
               int nextTag;
               do {
                  plist.addInt(this.input.readSFixed32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 2:
               int bytes = this.input.readUInt32();
               this.verifyPackedFixed32Length(bytes);
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  target.add(this.input.readSFixed32());
               } while (this.input.getTotalBytesRead() < endPos);

               return;
            case 5:
               int nextTag;
               do {
                  target.add(this.input.readSFixed32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   @Override
   public void readSFixed64List(List<Long> target) throws IOException {
      if (target instanceof LongArrayList) {
         LongArrayList plist = (LongArrayList)target;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 1:
               int nextTag;
               do {
                  plist.addLong(this.input.readSFixed64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               this.verifyPackedFixed64Length(bytes);
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  plist.addLong(this.input.readSFixed64());
               } while (this.input.getTotalBytesRead() < endPos);

               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 1:
               int nextTag;
               do {
                  target.add(this.input.readSFixed64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               this.verifyPackedFixed64Length(bytes);
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  target.add(this.input.readSFixed64());
               } while (this.input.getTotalBytesRead() < endPos);

               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   @Override
   public void readSInt32List(List<Integer> target) throws IOException {
      if (target instanceof IntArrayList) {
         IntArrayList plist = (IntArrayList)target;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  plist.addInt(this.input.readSInt32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  plist.addInt(this.input.readSInt32());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  target.add(this.input.readSInt32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  target.add(this.input.readSInt32());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   @Override
   public void readSInt64List(List<Long> target) throws IOException {
      if (target instanceof LongArrayList) {
         LongArrayList plist = (LongArrayList)target;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  plist.addLong(this.input.readSInt64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  plist.addLong(this.input.readSInt64());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int nextTag;
               do {
                  target.add(this.input.readSInt64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  nextTag = this.input.readTag();
               } while (nextTag == this.tag);

               this.nextTag = nextTag;
               return;
            case 2:
               int bytes = this.input.readUInt32();
               int endPos = this.input.getTotalBytesRead() + bytes;

               do {
                  target.add(this.input.readSInt64());
               } while (this.input.getTotalBytesRead() < endPos);

               this.requirePosition(endPos);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   private void verifyPackedFixed64Length(int bytes) throws IOException {
      if ((bytes & 7) != 0) {
         throw InvalidProtocolBufferException.parseFailure();
      }
   }

   @Override
   public <K, V> void readMap(Map<K, V> target, MapEntryLite.Metadata<K, V> metadata, ExtensionRegistryLite extensionRegistry) throws IOException {
      this.requireWireType(2);
      int size = this.input.readUInt32();
      int prevLimit = this.input.pushLimit(size);
      K key = metadata.defaultKey;
      V value = metadata.defaultValue;

      try {
         while (true) {
            int number = this.getFieldNumber();
            if (number == Integer.MAX_VALUE || this.input.isAtEnd()) {
               target.put(key, value);
               return;
            }

            try {
               switch (number) {
                  case 1:
                     key = (K)this.readField(metadata.keyType, null, null);
                     break;
                  case 2:
                     value = (V)this.readField(metadata.valueType, metadata.defaultValue.getClass(), extensionRegistry);
                     break;
                  default:
                     if (!this.skipField()) {
                        throw new InvalidProtocolBufferException("Unable to parse map entry.");
                     }
               }
            } catch (InvalidProtocolBufferException.InvalidWireTypeException var13) {
               if (!this.skipField()) {
                  throw new InvalidProtocolBufferException("Unable to parse map entry.", (IOException)var13);
               }
            }
         }
      } finally {
         this.input.popLimit(prevLimit);
      }
   }

   private Object readField(WireFormat.FieldType fieldType, Class<?> messageType, ExtensionRegistryLite extensionRegistry) throws IOException {
      switch (fieldType) {
         case BOOL:
            return this.readBool();
         case BYTES:
            return this.readBytes();
         case DOUBLE:
            return this.readDouble();
         case ENUM:
            return this.readEnum();
         case FIXED32:
            return this.readFixed32();
         case FIXED64:
            return this.readFixed64();
         case FLOAT:
            return this.readFloat();
         case INT32:
            return this.readInt32();
         case INT64:
            return this.readInt64();
         case MESSAGE:
            return this.readMessage(messageType, extensionRegistry);
         case SFIXED32:
            return this.readSFixed32();
         case SFIXED64:
            return this.readSFixed64();
         case SINT32:
            return this.readSInt32();
         case SINT64:
            return this.readSInt64();
         case STRING:
            return this.readStringRequireUtf8();
         case UINT32:
            return this.readUInt32();
         case UINT64:
            return this.readUInt64();
         default:
            throw new IllegalArgumentException("unsupported field type.");
      }
   }

   private void verifyPackedFixed32Length(int bytes) throws IOException {
      if ((bytes & 3) != 0) {
         throw InvalidProtocolBufferException.parseFailure();
      }
   }

   private void requirePosition(int expectedPosition) throws IOException {
      if (this.input.getTotalBytesRead() != expectedPosition) {
         throw InvalidProtocolBufferException.truncatedMessage();
      }
   }
}
