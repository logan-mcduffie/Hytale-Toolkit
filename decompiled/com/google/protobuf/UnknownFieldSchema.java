package com.google.protobuf;

import java.io.IOException;

@CheckReturnValue
abstract class UnknownFieldSchema<T, B> {
   static final int DEFAULT_RECURSION_LIMIT = 100;
   private static volatile int recursionLimit = 100;

   abstract boolean shouldDiscardUnknownFields(Reader reader);

   abstract void addVarint(B fields, int number, long value);

   abstract void addFixed32(B fields, int number, int value);

   abstract void addFixed64(B fields, int number, long value);

   abstract void addLengthDelimited(B fields, int number, ByteString value);

   abstract void addGroup(B fields, int number, T subFieldSet);

   abstract B newBuilder();

   abstract T toImmutable(B fields);

   abstract void setToMessage(Object message, T fields);

   abstract T getFromMessage(Object message);

   abstract B getBuilderFromMessage(Object message);

   abstract void setBuilderToMessage(Object message, B builder);

   abstract void makeImmutable(Object message);

   final boolean mergeOneFieldFrom(B unknownFields, Reader reader, int currentDepth) throws IOException {
      int tag = reader.getTag();
      int fieldNumber = WireFormat.getTagFieldNumber(tag);
      switch (WireFormat.getTagWireType(tag)) {
         case 0:
            this.addVarint(unknownFields, fieldNumber, reader.readInt64());
            return true;
         case 1:
            this.addFixed64(unknownFields, fieldNumber, reader.readFixed64());
            return true;
         case 2:
            this.addLengthDelimited(unknownFields, fieldNumber, reader.readBytes());
            return true;
         case 3:
            B subFields = this.newBuilder();
            int endGroupTag = WireFormat.makeTag(fieldNumber, 4);
            if (++currentDepth >= recursionLimit) {
               throw InvalidProtocolBufferException.recursionLimitExceeded();
            } else {
               this.mergeFrom(subFields, reader, currentDepth);
               currentDepth--;
               if (endGroupTag != reader.getTag()) {
                  throw InvalidProtocolBufferException.invalidEndTag();
               }

               this.addGroup(unknownFields, fieldNumber, this.toImmutable(subFields));
               return true;
            }
         case 4:
            if (currentDepth == 0) {
               throw InvalidProtocolBufferException.invalidEndTag();
            }

            return false;
         case 5:
            this.addFixed32(unknownFields, fieldNumber, reader.readFixed32());
            return true;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
      }
   }

   private final void mergeFrom(B unknownFields, Reader reader, int currentDepth) throws IOException {
      while (reader.getFieldNumber() != Integer.MAX_VALUE && this.mergeOneFieldFrom(unknownFields, reader, currentDepth)) {
      }
   }

   abstract void writeTo(T unknownFields, Writer writer) throws IOException;

   abstract void writeAsMessageSetTo(T unknownFields, Writer writer) throws IOException;

   abstract T merge(T destination, T source);

   abstract int getSerializedSizeAsMessageSet(T message);

   abstract int getSerializedSize(T unknowns);

   public void setRecursionLimit(int limit) {
      recursionLimit = limit;
   }
}
