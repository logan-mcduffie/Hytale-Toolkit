package com.google.protobuf;

import java.io.IOException;

@CheckReturnValue
final class ArrayDecoders {
   static final int DEFAULT_RECURSION_LIMIT = 100;
   private static volatile int recursionLimit = 100;

   private ArrayDecoders() {
   }

   static int decodeVarint32(byte[] data, int position, ArrayDecoders.Registers registers) {
      int value = data[position++];
      if (value >= 0) {
         registers.int1 = value;
         return position;
      } else {
         return decodeVarint32(value, data, position, registers);
      }
   }

   static int decodeVarint32(int firstByte, byte[] data, int position, ArrayDecoders.Registers registers) {
      int value = firstByte & 127;
      byte b2 = data[position++];
      if (b2 >= 0) {
         registers.int1 = value | b2 << 7;
         return position;
      } else {
         value |= (b2 & 127) << 7;
         byte b3 = data[position++];
         if (b3 >= 0) {
            registers.int1 = value | b3 << 14;
            return position;
         } else {
            value |= (b3 & 127) << 14;
            byte b4 = data[position++];
            if (b4 >= 0) {
               registers.int1 = value | b4 << 21;
               return position;
            } else {
               value |= (b4 & 127) << 21;
               byte b5 = data[position++];
               if (b5 >= 0) {
                  registers.int1 = value | b5 << 28;
                  return position;
               } else {
                  value |= (b5 & 127) << 28;

                  while (data[position++] < 0) {
                  }

                  registers.int1 = value;
                  return position;
               }
            }
         }
      }
   }

   static int decodeVarint64(byte[] data, int position, ArrayDecoders.Registers registers) {
      long value = data[position++];
      if (value >= 0L) {
         registers.long1 = value;
         return position;
      } else {
         return decodeVarint64(value, data, position, registers);
      }
   }

   static int decodeVarint64(long firstByte, byte[] data, int position, ArrayDecoders.Registers registers) {
      long value = firstByte & 127L;
      byte next = data[position++];
      int shift = 7;

      for (value |= (long)(next & 127) << 7; next < 0; value |= (long)(next & 127) << shift) {
         next = data[position++];
         shift += 7;
      }

      registers.long1 = value;
      return position;
   }

   static int decodeFixed32(byte[] data, int position) {
      return data[position] & 0xFF | (data[position + 1] & 0xFF) << 8 | (data[position + 2] & 0xFF) << 16 | (data[position + 3] & 0xFF) << 24;
   }

   static long decodeFixed64(byte[] data, int position) {
      return data[position] & 255L
         | (data[position + 1] & 255L) << 8
         | (data[position + 2] & 255L) << 16
         | (data[position + 3] & 255L) << 24
         | (data[position + 4] & 255L) << 32
         | (data[position + 5] & 255L) << 40
         | (data[position + 6] & 255L) << 48
         | (data[position + 7] & 255L) << 56;
   }

   static double decodeDouble(byte[] data, int position) {
      return Double.longBitsToDouble(decodeFixed64(data, position));
   }

   static float decodeFloat(byte[] data, int position) {
      return Float.intBitsToFloat(decodeFixed32(data, position));
   }

   static int decodeString(byte[] data, int position, ArrayDecoders.Registers registers) throws InvalidProtocolBufferException {
      position = decodeVarint32(data, position, registers);
      int length = registers.int1;
      if (length < 0) {
         throw InvalidProtocolBufferException.negativeSize();
      } else if (length == 0) {
         registers.object1 = "";
         return position;
      } else {
         registers.object1 = new String(data, position, length, Internal.UTF_8);
         return position + length;
      }
   }

   static int decodeStringRequireUtf8(byte[] data, int position, ArrayDecoders.Registers registers) throws InvalidProtocolBufferException {
      position = decodeVarint32(data, position, registers);
      int length = registers.int1;
      if (length < 0) {
         throw InvalidProtocolBufferException.negativeSize();
      } else if (length == 0) {
         registers.object1 = "";
         return position;
      } else {
         registers.object1 = Utf8.decodeUtf8(data, position, length);
         return position + length;
      }
   }

   static int decodeBytes(byte[] data, int position, ArrayDecoders.Registers registers) throws InvalidProtocolBufferException {
      position = decodeVarint32(data, position, registers);
      int length = registers.int1;
      if (length < 0) {
         throw InvalidProtocolBufferException.negativeSize();
      } else if (length > data.length - position) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else if (length == 0) {
         registers.object1 = ByteString.EMPTY;
         return position;
      } else {
         registers.object1 = ByteString.copyFrom(data, position, length);
         return position + length;
      }
   }

   static <T> int decodeMessageField(Schema<T> schema, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException {
      T msg = schema.newInstance();
      int offset = mergeMessageField(msg, schema, data, position, limit, registers);
      schema.makeImmutable(msg);
      registers.object1 = msg;
      return offset;
   }

   static <T> int decodeGroupField(Schema<T> schema, byte[] data, int position, int limit, int endGroup, ArrayDecoders.Registers registers) throws IOException {
      T msg = schema.newInstance();
      int offset = mergeGroupField(msg, schema, data, position, limit, endGroup, registers);
      schema.makeImmutable(msg);
      registers.object1 = msg;
      return offset;
   }

   static <T> int mergeMessageField(Object msg, Schema<T> schema, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException {
      int length = data[position++];
      if (length < 0) {
         position = decodeVarint32(length, data, position, registers);
         length = registers.int1;
      }

      if (length >= 0 && length <= limit - position) {
         registers.recursionDepth++;
         checkRecursionLimit(registers.recursionDepth);
         schema.mergeFrom((T)msg, data, position, position + length, registers);
         registers.recursionDepth--;
         registers.object1 = msg;
         return position + length;
      } else {
         throw InvalidProtocolBufferException.truncatedMessage();
      }
   }

   static <T> int mergeGroupField(Object msg, Schema<T> schema, byte[] data, int position, int limit, int endGroup, ArrayDecoders.Registers registers) throws IOException {
      MessageSchema<T> messageSchema = (MessageSchema<T>)schema;
      registers.recursionDepth++;
      checkRecursionLimit(registers.recursionDepth);
      int endPosition = messageSchema.parseMessage((T)msg, data, position, limit, endGroup, registers);
      registers.recursionDepth--;
      registers.object1 = msg;
      return endPosition;
   }

   static int decodeVarint32List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) {
      IntArrayList output = (IntArrayList)list;
      position = decodeVarint32(data, position, registers);
      output.addInt(registers.int1);

      while (position < limit) {
         int nextPosition = decodeVarint32(data, position, registers);
         if (tag != registers.int1) {
            break;
         }

         position = decodeVarint32(data, nextPosition, registers);
         output.addInt(registers.int1);
      }

      return position;
   }

   static int decodeVarint64List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) {
      LongArrayList output = (LongArrayList)list;
      position = decodeVarint64(data, position, registers);
      output.addLong(registers.long1);

      while (position < limit) {
         int nextPosition = decodeVarint32(data, position, registers);
         if (tag != registers.int1) {
            break;
         }

         position = decodeVarint64(data, nextPosition, registers);
         output.addLong(registers.long1);
      }

      return position;
   }

   static int decodeFixed32List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) {
      IntArrayList output = (IntArrayList)list;
      output.addInt(decodeFixed32(data, position));
      position += 4;

      while (position < limit) {
         int nextPosition = decodeVarint32(data, position, registers);
         if (tag != registers.int1) {
            break;
         }

         output.addInt(decodeFixed32(data, nextPosition));
         position = nextPosition + 4;
      }

      return position;
   }

   static int decodeFixed64List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) {
      LongArrayList output = (LongArrayList)list;
      output.addLong(decodeFixed64(data, position));
      position += 8;

      while (position < limit) {
         int nextPosition = decodeVarint32(data, position, registers);
         if (tag != registers.int1) {
            break;
         }

         output.addLong(decodeFixed64(data, nextPosition));
         position = nextPosition + 8;
      }

      return position;
   }

   static int decodeFloatList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) {
      FloatArrayList output = (FloatArrayList)list;
      output.addFloat(decodeFloat(data, position));
      position += 4;

      while (position < limit) {
         int nextPosition = decodeVarint32(data, position, registers);
         if (tag != registers.int1) {
            break;
         }

         output.addFloat(decodeFloat(data, nextPosition));
         position = nextPosition + 4;
      }

      return position;
   }

   static int decodeDoubleList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) {
      DoubleArrayList output = (DoubleArrayList)list;
      output.addDouble(decodeDouble(data, position));
      position += 8;

      while (position < limit) {
         int nextPosition = decodeVarint32(data, position, registers);
         if (tag != registers.int1) {
            break;
         }

         output.addDouble(decodeDouble(data, nextPosition));
         position = nextPosition + 8;
      }

      return position;
   }

   static int decodeBoolList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) {
      BooleanArrayList output = (BooleanArrayList)list;
      position = decodeVarint64(data, position, registers);
      output.addBoolean(registers.long1 != 0L);

      while (position < limit) {
         int nextPosition = decodeVarint32(data, position, registers);
         if (tag != registers.int1) {
            break;
         }

         position = decodeVarint64(data, nextPosition, registers);
         output.addBoolean(registers.long1 != 0L);
      }

      return position;
   }

   static int decodeSInt32List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) {
      IntArrayList output = (IntArrayList)list;
      position = decodeVarint32(data, position, registers);
      output.addInt(CodedInputStream.decodeZigZag32(registers.int1));

      while (position < limit) {
         int nextPosition = decodeVarint32(data, position, registers);
         if (tag != registers.int1) {
            break;
         }

         position = decodeVarint32(data, nextPosition, registers);
         output.addInt(CodedInputStream.decodeZigZag32(registers.int1));
      }

      return position;
   }

   static int decodeSInt64List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) {
      LongArrayList output = (LongArrayList)list;
      position = decodeVarint64(data, position, registers);
      output.addLong(CodedInputStream.decodeZigZag64(registers.long1));

      while (position < limit) {
         int nextPosition = decodeVarint32(data, position, registers);
         if (tag != registers.int1) {
            break;
         }

         position = decodeVarint64(data, nextPosition, registers);
         output.addLong(CodedInputStream.decodeZigZag64(registers.long1));
      }

      return position;
   }

   static int decodePackedVarint32List(byte[] data, int position, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) throws IOException {
      IntArrayList output = (IntArrayList)list;
      position = decodeVarint32(data, position, registers);
      int fieldLimit = position + registers.int1;

      while (position < fieldLimit) {
         position = decodeVarint32(data, position, registers);
         output.addInt(registers.int1);
      }

      if (position != fieldLimit) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return position;
      }
   }

   static int decodePackedVarint64List(byte[] data, int position, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) throws IOException {
      LongArrayList output = (LongArrayList)list;
      position = decodeVarint32(data, position, registers);
      int fieldLimit = position + registers.int1;

      while (position < fieldLimit) {
         position = decodeVarint64(data, position, registers);
         output.addLong(registers.long1);
      }

      if (position != fieldLimit) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return position;
      }
   }

   static int decodePackedFixed32List(byte[] data, int position, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) throws InvalidProtocolBufferException {
      IntArrayList output = (IntArrayList)list;
      position = decodeVarint32(data, position, registers);
      int packedDataByteSize = registers.int1;
      int fieldLimit = position + packedDataByteSize;
      if (fieldLimit > data.length) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         output.ensureCapacity(output.size() + packedDataByteSize / 4);

         while (position < fieldLimit) {
            output.addInt(decodeFixed32(data, position));
            position += 4;
         }

         if (position != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            return position;
         }
      }
   }

   static int decodePackedFixed64List(byte[] data, int position, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) throws InvalidProtocolBufferException {
      LongArrayList output = (LongArrayList)list;
      position = decodeVarint32(data, position, registers);
      int packedDataByteSize = registers.int1;
      int fieldLimit = position + packedDataByteSize;
      if (fieldLimit > data.length) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         output.ensureCapacity(output.size() + packedDataByteSize / 8);

         while (position < fieldLimit) {
            output.addLong(decodeFixed64(data, position));
            position += 8;
         }

         if (position != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            return position;
         }
      }
   }

   static int decodePackedFloatList(byte[] data, int position, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) throws InvalidProtocolBufferException {
      FloatArrayList output = (FloatArrayList)list;
      position = decodeVarint32(data, position, registers);
      int packedDataByteSize = registers.int1;
      int fieldLimit = position + packedDataByteSize;
      if (fieldLimit > data.length) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         output.ensureCapacity(output.size() + packedDataByteSize / 4);

         while (position < fieldLimit) {
            output.addFloat(decodeFloat(data, position));
            position += 4;
         }

         if (position != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            return position;
         }
      }
   }

   static int decodePackedDoubleList(byte[] data, int position, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) throws InvalidProtocolBufferException {
      DoubleArrayList output = (DoubleArrayList)list;
      position = decodeVarint32(data, position, registers);
      int packedDataByteSize = registers.int1;
      int fieldLimit = position + packedDataByteSize;
      if (fieldLimit > data.length) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         output.ensureCapacity(output.size() + packedDataByteSize / 8);

         while (position < fieldLimit) {
            output.addDouble(decodeDouble(data, position));
            position += 8;
         }

         if (position != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            return position;
         }
      }
   }

   static int decodePackedBoolList(byte[] data, int position, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) throws InvalidProtocolBufferException {
      BooleanArrayList output = (BooleanArrayList)list;
      position = decodeVarint32(data, position, registers);
      int fieldLimit = position + registers.int1;

      while (position < fieldLimit) {
         position = decodeVarint64(data, position, registers);
         output.addBoolean(registers.long1 != 0L);
      }

      if (position != fieldLimit) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return position;
      }
   }

   static int decodePackedSInt32List(byte[] data, int position, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) throws InvalidProtocolBufferException {
      IntArrayList output = (IntArrayList)list;
      position = decodeVarint32(data, position, registers);
      int fieldLimit = position + registers.int1;

      while (position < fieldLimit) {
         position = decodeVarint32(data, position, registers);
         output.addInt(CodedInputStream.decodeZigZag32(registers.int1));
      }

      if (position != fieldLimit) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return position;
      }
   }

   static int decodePackedSInt64List(byte[] data, int position, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) throws InvalidProtocolBufferException {
      LongArrayList output = (LongArrayList)list;
      position = decodeVarint32(data, position, registers);
      int fieldLimit = position + registers.int1;

      while (position < fieldLimit) {
         position = decodeVarint64(data, position, registers);
         output.addLong(CodedInputStream.decodeZigZag64(registers.long1));
      }

      if (position != fieldLimit) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return position;
      }
   }

   static int decodeStringList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) throws InvalidProtocolBufferException {
      Internal.ProtobufList<String> output = (Internal.ProtobufList<String>)list;
      position = decodeVarint32(data, position, registers);
      int length = registers.int1;
      if (length < 0) {
         throw InvalidProtocolBufferException.negativeSize();
      } else {
         if (length == 0) {
            ((Internal.ProtobufList<String>)list).add("");
         } else {
            String value = new String(data, position, length, Internal.UTF_8);
            ((Internal.ProtobufList<String>)list).add(value);
            position += length;
         }

         while (position < limit) {
            int nextPosition = decodeVarint32(data, position, registers);
            if (tag != registers.int1) {
               break;
            }

            position = decodeVarint32(data, nextPosition, registers);
            int nextLength = registers.int1;
            if (nextLength < 0) {
               throw InvalidProtocolBufferException.negativeSize();
            }

            if (nextLength == 0) {
               output.add("");
            } else {
               String value = new String(data, position, nextLength, Internal.UTF_8);
               output.add(value);
               position += nextLength;
            }
         }

         return position;
      }
   }

   static int decodeStringListRequireUtf8(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) throws InvalidProtocolBufferException {
      Internal.ProtobufList<String> output = (Internal.ProtobufList<String>)list;
      position = decodeVarint32(data, position, registers);
      int length = registers.int1;
      if (length < 0) {
         throw InvalidProtocolBufferException.negativeSize();
      } else {
         if (length == 0) {
            ((Internal.ProtobufList<String>)list).add("");
         } else {
            if (!Utf8.isValidUtf8(data, position, position + length)) {
               throw InvalidProtocolBufferException.invalidUtf8();
            }

            String value = new String(data, position, length, Internal.UTF_8);
            ((Internal.ProtobufList<String>)list).add(value);
            position += length;
         }

         while (position < limit) {
            int nextPosition = decodeVarint32(data, position, registers);
            if (tag != registers.int1) {
               break;
            }

            position = decodeVarint32(data, nextPosition, registers);
            int nextLength = registers.int1;
            if (nextLength < 0) {
               throw InvalidProtocolBufferException.negativeSize();
            }

            if (nextLength == 0) {
               output.add("");
            } else {
               if (!Utf8.isValidUtf8(data, position, position + nextLength)) {
                  throw InvalidProtocolBufferException.invalidUtf8();
               }

               String value = new String(data, position, nextLength, Internal.UTF_8);
               output.add(value);
               position += nextLength;
            }
         }

         return position;
      }
   }

   static int decodeBytesList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers) throws InvalidProtocolBufferException {
      Internal.ProtobufList<ByteString> output = (Internal.ProtobufList<ByteString>)list;
      position = decodeVarint32(data, position, registers);
      int length = registers.int1;
      if (length < 0) {
         throw InvalidProtocolBufferException.negativeSize();
      } else if (length > data.length - position) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         if (length == 0) {
            ((Internal.ProtobufList<ByteString>)list).add(ByteString.EMPTY);
         } else {
            ((Internal.ProtobufList<ByteString>)list).add(ByteString.copyFrom(data, position, length));
            position += length;
         }

         while (position < limit) {
            int nextPosition = decodeVarint32(data, position, registers);
            if (tag != registers.int1) {
               break;
            }

            position = decodeVarint32(data, nextPosition, registers);
            int nextLength = registers.int1;
            if (nextLength < 0) {
               throw InvalidProtocolBufferException.negativeSize();
            }

            if (nextLength > data.length - position) {
               throw InvalidProtocolBufferException.truncatedMessage();
            }

            if (nextLength == 0) {
               output.add(ByteString.EMPTY);
            } else {
               output.add(ByteString.copyFrom(data, position, nextLength));
               position += nextLength;
            }
         }

         return position;
      }
   }

   static int decodeMessageList(
      Schema<?> schema, int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, ArrayDecoders.Registers registers
   ) throws IOException {
      Internal.ProtobufList<Object> output = (Internal.ProtobufList<Object>)list;
      position = decodeMessageField(schema, data, position, limit, registers);
      ((Internal.ProtobufList<Object>)list).add(registers.object1);

      while (position < limit) {
         int nextPosition = decodeVarint32(data, position, registers);
         if (tag != registers.int1) {
            break;
         }

         position = decodeMessageField(schema, data, nextPosition, limit, registers);
         output.add(registers.object1);
      }

      return position;
   }

   static int decodeGroupList(
      Schema<?> schema, int tag, byte[] data, int position, int limit, Internal.ProtobufList<Object> output, ArrayDecoders.Registers registers
   ) throws IOException {
      int endgroup = tag & -8 | 4;
      position = decodeGroupField(schema, data, position, limit, endgroup, registers);
      output.add(registers.object1);

      while (position < limit) {
         int nextPosition = decodeVarint32(data, position, registers);
         if (tag != registers.int1) {
            break;
         }

         position = decodeGroupField(schema, data, nextPosition, limit, endgroup, registers);
         output.add(registers.object1);
      }

      return position;
   }

   static int decodeExtensionOrUnknownField(
      int tag,
      byte[] data,
      int position,
      int limit,
      Object message,
      MessageLite defaultInstance,
      UnknownFieldSchema<UnknownFieldSetLite, UnknownFieldSetLite> unknownFieldSchema,
      ArrayDecoders.Registers registers
   ) throws IOException {
      int number = tag >>> 3;
      GeneratedMessageLite.GeneratedExtension<?, ?> extension = registers.extensionRegistry.findLiteExtensionByNumber(defaultInstance, number);
      if (extension == null) {
         return decodeUnknownField(tag, data, position, limit, MessageSchema.getMutableUnknownFields(message), registers);
      } else {
         FieldSet<GeneratedMessageLite.ExtensionDescriptor> unused = ((GeneratedMessageLite.ExtendableMessage)message).ensureExtensionsAreMutable();
         return decodeExtension(tag, data, position, limit, (GeneratedMessageLite.ExtendableMessage<?, ?>)message, extension, unknownFieldSchema, registers);
      }
   }

   static int decodeExtension(
      int tag,
      byte[] data,
      int position,
      int limit,
      GeneratedMessageLite.ExtendableMessage<?, ?> message,
      GeneratedMessageLite.GeneratedExtension<?, ?> extension,
      UnknownFieldSchema<UnknownFieldSetLite, UnknownFieldSetLite> unknownFieldSchema,
      ArrayDecoders.Registers registers
   ) throws IOException {
      FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions = message.extensions;
      int fieldNumber = tag >>> 3;
      if (extension.descriptor.isRepeated() && extension.descriptor.isPacked()) {
         switch (extension.getLiteType()) {
            case DOUBLE: {
               DoubleArrayList list = new DoubleArrayList();
               position = decodePackedDoubleList(data, position, list, registers);
               extensions.setField(extension.descriptor, list);
               break;
            }
            case FLOAT: {
               FloatArrayList list = new FloatArrayList();
               position = decodePackedFloatList(data, position, list, registers);
               extensions.setField(extension.descriptor, list);
               break;
            }
            case INT64:
            case UINT64: {
               LongArrayList list = new LongArrayList();
               position = decodePackedVarint64List(data, position, list, registers);
               extensions.setField(extension.descriptor, list);
               break;
            }
            case INT32:
            case UINT32: {
               IntArrayList list = new IntArrayList();
               position = decodePackedVarint32List(data, position, list, registers);
               extensions.setField(extension.descriptor, list);
               break;
            }
            case FIXED64:
            case SFIXED64: {
               LongArrayList list = new LongArrayList();
               position = decodePackedFixed64List(data, position, list, registers);
               extensions.setField(extension.descriptor, list);
               break;
            }
            case FIXED32:
            case SFIXED32: {
               IntArrayList list = new IntArrayList();
               position = decodePackedFixed32List(data, position, list, registers);
               extensions.setField(extension.descriptor, list);
               break;
            }
            case BOOL: {
               BooleanArrayList list = new BooleanArrayList();
               position = decodePackedBoolList(data, position, list, registers);
               extensions.setField(extension.descriptor, list);
               break;
            }
            case SINT32: {
               IntArrayList list = new IntArrayList();
               position = decodePackedSInt32List(data, position, list, registers);
               extensions.setField(extension.descriptor, list);
               break;
            }
            case SINT64: {
               LongArrayList list = new LongArrayList();
               position = decodePackedSInt64List(data, position, list, registers);
               extensions.setField(extension.descriptor, list);
               break;
            }
            case ENUM: {
               IntArrayList list = new IntArrayList();
               position = decodePackedVarint32List(data, position, list, registers);
               SchemaUtil.filterUnknownEnumList(message, fieldNumber, list, extension.descriptor.getEnumType(), null, unknownFieldSchema);
               extensions.setField(extension.descriptor, list);
               break;
            }
            default:
               throw new IllegalStateException("Type cannot be packed: " + extension.descriptor.getLiteType());
         }
      } else {
         Object value = null;
         if (extension.getLiteType() == WireFormat.FieldType.ENUM) {
            position = decodeVarint32(data, position, registers);
            Object enumValue = extension.descriptor.getEnumType().findValueByNumber(registers.int1);
            if (enumValue == null) {
               SchemaUtil.storeUnknownEnum(message, fieldNumber, registers.int1, null, unknownFieldSchema);
               return position;
            }

            value = registers.int1;
         } else {
            switch (extension.getLiteType()) {
               case DOUBLE:
                  value = decodeDouble(data, position);
                  position += 8;
                  break;
               case FLOAT:
                  value = decodeFloat(data, position);
                  position += 4;
                  break;
               case INT64:
               case UINT64:
                  position = decodeVarint64(data, position, registers);
                  value = registers.long1;
                  break;
               case INT32:
               case UINT32:
                  position = decodeVarint32(data, position, registers);
                  value = registers.int1;
                  break;
               case FIXED64:
               case SFIXED64:
                  value = decodeFixed64(data, position);
                  position += 8;
                  break;
               case FIXED32:
               case SFIXED32:
                  value = decodeFixed32(data, position);
                  position += 4;
                  break;
               case BOOL:
                  position = decodeVarint64(data, position, registers);
                  value = registers.long1 != 0L;
                  break;
               case SINT32:
                  position = decodeVarint32(data, position, registers);
                  value = CodedInputStream.decodeZigZag32(registers.int1);
                  break;
               case SINT64:
                  position = decodeVarint64(data, position, registers);
                  value = CodedInputStream.decodeZigZag64(registers.long1);
                  break;
               case ENUM:
                  throw new IllegalStateException("Shouldn't reach here.");
               case BYTES:
                  position = decodeBytes(data, position, registers);
                  value = registers.object1;
                  break;
               case STRING:
                  position = decodeString(data, position, registers);
                  value = registers.object1;
                  break;
               case GROUP:
                  int endTag = fieldNumber << 3 | 4;
                  Schema<?> fieldSchema = Protobuf.getInstance().schemaFor(extension.getMessageDefaultInstance().getClass());
                  if (extension.isRepeated()) {
                     position = decodeGroupField(fieldSchema, data, position, limit, endTag, registers);
                     extensions.addRepeatedField(extension.descriptor, registers.object1);
                  } else {
                     Object oldValue = extensions.getField(extension.descriptor);
                     if (oldValue == null) {
                        oldValue = fieldSchema.newInstance();
                        extensions.setField(extension.descriptor, oldValue);
                     }

                     position = mergeGroupField(oldValue, fieldSchema, data, position, limit, endTag, registers);
                  }

                  return position;
               case MESSAGE:
                  Schema<?> fieldSchema = Protobuf.getInstance().schemaFor(extension.getMessageDefaultInstance().getClass());
                  if (extension.isRepeated()) {
                     position = decodeMessageField(fieldSchema, data, position, limit, registers);
                     extensions.addRepeatedField(extension.descriptor, registers.object1);
                  } else {
                     Object oldValue = extensions.getField(extension.descriptor);
                     if (oldValue == null) {
                        oldValue = fieldSchema.newInstance();
                        extensions.setField(extension.descriptor, oldValue);
                     }

                     position = mergeMessageField(oldValue, fieldSchema, data, position, limit, registers);
                  }

                  return position;
            }
         }

         if (extension.isRepeated()) {
            extensions.addRepeatedField(extension.descriptor, value);
         } else {
            extensions.setField(extension.descriptor, value);
         }
      }

      return position;
   }

   static int decodeUnknownField(int tag, byte[] data, int position, int limit, UnknownFieldSetLite unknownFields, ArrayDecoders.Registers registers) throws InvalidProtocolBufferException {
      if (WireFormat.getTagFieldNumber(tag) == 0) {
         throw InvalidProtocolBufferException.invalidTag();
      } else {
         switch (WireFormat.getTagWireType(tag)) {
            case 0:
               position = decodeVarint64(data, position, registers);
               unknownFields.storeField(tag, registers.long1);
               return position;
            case 1:
               unknownFields.storeField(tag, decodeFixed64(data, position));
               return position + 8;
            case 2:
               position = decodeVarint32(data, position, registers);
               int length = registers.int1;
               if (length < 0) {
                  throw InvalidProtocolBufferException.negativeSize();
               } else {
                  if (length > data.length - position) {
                     throw InvalidProtocolBufferException.truncatedMessage();
                  }

                  if (length == 0) {
                     unknownFields.storeField(tag, ByteString.EMPTY);
                  } else {
                     unknownFields.storeField(tag, ByteString.copyFrom(data, position, length));
                  }

                  return position + length;
               }
            case 3:
               UnknownFieldSetLite child = UnknownFieldSetLite.newInstance();
               int endGroup = tag & -8 | 4;
               int lastTag = 0;
               registers.recursionDepth++;
               checkRecursionLimit(registers.recursionDepth);

               while (position < limit) {
                  position = decodeVarint32(data, position, registers);
                  lastTag = registers.int1;
                  if (lastTag == endGroup) {
                     break;
                  }

                  position = decodeUnknownField(lastTag, data, position, limit, child, registers);
               }

               registers.recursionDepth--;
               if (position <= limit && lastTag == endGroup) {
                  unknownFields.storeField(tag, child);
                  return position;
               } else {
                  throw InvalidProtocolBufferException.parseFailure();
               }
            case 4:
            default:
               throw InvalidProtocolBufferException.invalidTag();
            case 5:
               unknownFields.storeField(tag, decodeFixed32(data, position));
               return position + 4;
         }
      }
   }

   static int skipField(int tag, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws InvalidProtocolBufferException {
      if (WireFormat.getTagFieldNumber(tag) == 0) {
         throw InvalidProtocolBufferException.invalidTag();
      } else {
         switch (WireFormat.getTagWireType(tag)) {
            case 0:
               return decodeVarint64(data, position, registers);
            case 1:
               return position + 8;
            case 2:
               position = decodeVarint32(data, position, registers);
               return position + registers.int1;
            case 3:
               int endGroup = tag & -8 | 4;
               int lastTag = 0;

               while (position < limit) {
                  position = decodeVarint32(data, position, registers);
                  lastTag = registers.int1;
                  if (lastTag == endGroup) {
                     break;
                  }

                  position = skipField(lastTag, data, position, limit, registers);
               }

               if (position <= limit && lastTag == endGroup) {
                  return position;
               } else {
                  throw InvalidProtocolBufferException.parseFailure();
               }
            case 4:
            default:
               throw InvalidProtocolBufferException.invalidTag();
            case 5:
               return position + 4;
         }
      }
   }

   public static void setRecursionLimit(int limit) {
      recursionLimit = limit;
   }

   private static void checkRecursionLimit(int depth) throws InvalidProtocolBufferException {
      if (depth >= recursionLimit) {
         throw InvalidProtocolBufferException.recursionLimitExceeded();
      }
   }

   static final class Registers {
      public int int1;
      public long long1;
      public Object object1;
      public final ExtensionRegistryLite extensionRegistry;
      public int recursionDepth;

      Registers() {
         this.extensionRegistry = ExtensionRegistryLite.getEmptyRegistry();
      }

      Registers(ExtensionRegistryLite extensionRegistry) {
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            this.extensionRegistry = extensionRegistry;
         }
      }
   }
}
