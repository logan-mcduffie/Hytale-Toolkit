package com.google.protobuf;

import java.io.IOException;
import java.util.Arrays;

public final class UnknownFieldSetLite {
   private static final int MIN_CAPACITY = 8;
   private static final UnknownFieldSetLite DEFAULT_INSTANCE = new UnknownFieldSetLite(0, new int[0], new Object[0], false);
   private int count;
   private int[] tags;
   private Object[] objects;
   private int memoizedSerializedSize = -1;
   private boolean isMutable;

   public static UnknownFieldSetLite getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   static UnknownFieldSetLite newInstance() {
      return new UnknownFieldSetLite();
   }

   static UnknownFieldSetLite mutableCopyOf(UnknownFieldSetLite first, UnknownFieldSetLite second) {
      int count = first.count + second.count;
      int[] tags = Arrays.copyOf(first.tags, count);
      System.arraycopy(second.tags, 0, tags, first.count, second.count);
      Object[] objects = Arrays.copyOf(first.objects, count);
      System.arraycopy(second.objects, 0, objects, first.count, second.count);
      return new UnknownFieldSetLite(count, tags, objects, true);
   }

   private UnknownFieldSetLite() {
      this(0, new int[8], new Object[8], true);
   }

   private UnknownFieldSetLite(int count, int[] tags, Object[] objects, boolean isMutable) {
      this.count = count;
      this.tags = tags;
      this.objects = objects;
      this.isMutable = isMutable;
   }

   public void makeImmutable() {
      if (this.isMutable) {
         this.isMutable = false;
      }
   }

   void checkMutable() {
      if (!this.isMutable) {
         throw new UnsupportedOperationException();
      }
   }

   public void writeTo(CodedOutputStream output) throws IOException {
      for (int i = 0; i < this.count; i++) {
         int tag = this.tags[i];
         int fieldNumber = WireFormat.getTagFieldNumber(tag);
         switch (WireFormat.getTagWireType(tag)) {
            case 0:
               output.writeUInt64(fieldNumber, (Long)this.objects[i]);
               break;
            case 1:
               output.writeFixed64(fieldNumber, (Long)this.objects[i]);
               break;
            case 2:
               output.writeBytes(fieldNumber, (ByteString)this.objects[i]);
               break;
            case 3:
               output.writeTag(fieldNumber, 3);
               ((UnknownFieldSetLite)this.objects[i]).writeTo(output);
               output.writeTag(fieldNumber, 4);
               break;
            case 4:
            default:
               throw InvalidProtocolBufferException.invalidWireType();
            case 5:
               output.writeFixed32(fieldNumber, (Integer)this.objects[i]);
         }
      }
   }

   public void writeAsMessageSetTo(CodedOutputStream output) throws IOException {
      for (int i = 0; i < this.count; i++) {
         int fieldNumber = WireFormat.getTagFieldNumber(this.tags[i]);
         output.writeRawMessageSetExtension(fieldNumber, (ByteString)this.objects[i]);
      }
   }

   void writeAsMessageSetTo(Writer writer) throws IOException {
      if (writer.fieldOrder() == Writer.FieldOrder.DESCENDING) {
         for (int i = this.count - 1; i >= 0; i--) {
            int fieldNumber = WireFormat.getTagFieldNumber(this.tags[i]);
            writer.writeMessageSetItem(fieldNumber, this.objects[i]);
         }
      } else {
         for (int i = 0; i < this.count; i++) {
            int fieldNumber = WireFormat.getTagFieldNumber(this.tags[i]);
            writer.writeMessageSetItem(fieldNumber, this.objects[i]);
         }
      }
   }

   public void writeTo(Writer writer) throws IOException {
      if (this.count != 0) {
         if (writer.fieldOrder() == Writer.FieldOrder.ASCENDING) {
            for (int i = 0; i < this.count; i++) {
               writeField(this.tags[i], this.objects[i], writer);
            }
         } else {
            for (int i = this.count - 1; i >= 0; i--) {
               writeField(this.tags[i], this.objects[i], writer);
            }
         }
      }
   }

   private static void writeField(int tag, Object object, Writer writer) throws IOException {
      int fieldNumber = WireFormat.getTagFieldNumber(tag);
      switch (WireFormat.getTagWireType(tag)) {
         case 0:
            writer.writeInt64(fieldNumber, (Long)object);
            break;
         case 1:
            writer.writeFixed64(fieldNumber, (Long)object);
            break;
         case 2:
            writer.writeBytes(fieldNumber, (ByteString)object);
            break;
         case 3:
            if (writer.fieldOrder() == Writer.FieldOrder.ASCENDING) {
               writer.writeStartGroup(fieldNumber);
               ((UnknownFieldSetLite)object).writeTo(writer);
               writer.writeEndGroup(fieldNumber);
            } else {
               writer.writeEndGroup(fieldNumber);
               ((UnknownFieldSetLite)object).writeTo(writer);
               writer.writeStartGroup(fieldNumber);
            }
            break;
         case 4:
         default:
            throw new RuntimeException(InvalidProtocolBufferException.invalidWireType());
         case 5:
            writer.writeFixed32(fieldNumber, (Integer)object);
      }
   }

   public int getSerializedSizeAsMessageSet() {
      int size = this.memoizedSerializedSize;
      if (size != -1) {
         return size;
      } else {
         size = 0;

         for (int i = 0; i < this.count; i++) {
            int tag = this.tags[i];
            int fieldNumber = WireFormat.getTagFieldNumber(tag);
            size += CodedOutputStream.computeRawMessageSetExtensionSize(fieldNumber, (ByteString)this.objects[i]);
         }

         this.memoizedSerializedSize = size;
         return size;
      }
   }

   public int getSerializedSize() {
      int size = this.memoizedSerializedSize;
      if (size != -1) {
         return size;
      } else {
         size = 0;

         for (int i = 0; i < this.count; i++) {
            int tag = this.tags[i];
            int fieldNumber = WireFormat.getTagFieldNumber(tag);
            switch (WireFormat.getTagWireType(tag)) {
               case 0:
                  size += CodedOutputStream.computeUInt64Size(fieldNumber, (Long)this.objects[i]);
                  break;
               case 1:
                  size += CodedOutputStream.computeFixed64Size(fieldNumber, (Long)this.objects[i]);
                  break;
               case 2:
                  size += CodedOutputStream.computeBytesSize(fieldNumber, (ByteString)this.objects[i]);
                  break;
               case 3:
                  size += CodedOutputStream.computeTagSize(fieldNumber) * 2 + ((UnknownFieldSetLite)this.objects[i]).getSerializedSize();
                  break;
               case 4:
               default:
                  throw new IllegalStateException(InvalidProtocolBufferException.invalidWireType());
               case 5:
                  size += CodedOutputStream.computeFixed32Size(fieldNumber, (Integer)this.objects[i]);
            }
         }

         this.memoizedSerializedSize = size;
         return size;
      }
   }

   private static boolean tagsEquals(int[] tags1, int[] tags2, int count) {
      for (int i = 0; i < count; i++) {
         if (tags1[i] != tags2[i]) {
            return false;
         }
      }

      return true;
   }

   private static boolean objectsEquals(Object[] objects1, Object[] objects2, int count) {
      for (int i = 0; i < count; i++) {
         if (!objects1[i].equals(objects2[i])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (!(obj instanceof UnknownFieldSetLite)) {
         return false;
      } else {
         UnknownFieldSetLite other = (UnknownFieldSetLite)obj;
         return this.count == other.count && tagsEquals(this.tags, other.tags, this.count) && objectsEquals(this.objects, other.objects, this.count);
      }
   }

   private static int hashCode(int[] tags, int count) {
      int hashCode = 17;

      for (int i = 0; i < count; i++) {
         hashCode = 31 * hashCode + tags[i];
      }

      return hashCode;
   }

   private static int hashCode(Object[] objects, int count) {
      int hashCode = 17;

      for (int i = 0; i < count; i++) {
         hashCode = 31 * hashCode + objects[i].hashCode();
      }

      return hashCode;
   }

   @Override
   public int hashCode() {
      int hashCode = 17;
      hashCode = 31 * hashCode + this.count;
      hashCode = 31 * hashCode + hashCode(this.tags, this.count);
      return 31 * hashCode + hashCode(this.objects, this.count);
   }

   final void printWithIndent(StringBuilder buffer, int indent) {
      for (int i = 0; i < this.count; i++) {
         int fieldNumber = WireFormat.getTagFieldNumber(this.tags[i]);
         MessageLiteToString.printField(buffer, indent, String.valueOf(fieldNumber), this.objects[i]);
      }
   }

   void storeField(int tag, Object value) {
      this.checkMutable();
      this.ensureCapacity(this.count + 1);
      this.tags[this.count] = tag;
      this.objects[this.count] = value;
      this.count++;
   }

   private void ensureCapacity(int minCapacity) {
      if (minCapacity > this.tags.length) {
         int newCapacity = this.count + this.count / 2;
         if (newCapacity < minCapacity) {
            newCapacity = minCapacity;
         }

         if (newCapacity < 8) {
            newCapacity = 8;
         }

         this.tags = Arrays.copyOf(this.tags, newCapacity);
         this.objects = Arrays.copyOf(this.objects, newCapacity);
      }
   }

   boolean mergeFieldFrom(final int tag, final CodedInputStream input) throws IOException {
      this.checkMutable();
      int fieldNumber = WireFormat.getTagFieldNumber(tag);
      switch (WireFormat.getTagWireType(tag)) {
         case 0:
            this.storeField(tag, input.readInt64());
            return true;
         case 1:
            this.storeField(tag, input.readFixed64());
            return true;
         case 2:
            this.storeField(tag, input.readBytes());
            return true;
         case 3:
            UnknownFieldSetLite subFieldSet = new UnknownFieldSetLite();
            subFieldSet.mergeFrom(input);
            input.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            this.storeField(tag, subFieldSet);
            return true;
         case 4:
            input.checkValidEndTag();
            return false;
         case 5:
            this.storeField(tag, input.readFixed32());
            return true;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
      }
   }

   UnknownFieldSetLite mergeVarintField(int fieldNumber, int value) {
      this.checkMutable();
      if (fieldNumber == 0) {
         throw new IllegalArgumentException("Zero is not a valid field number.");
      } else {
         this.storeField(WireFormat.makeTag(fieldNumber, 0), (long)value);
         return this;
      }
   }

   UnknownFieldSetLite mergeLengthDelimitedField(final int fieldNumber, final ByteString value) {
      this.checkMutable();
      if (fieldNumber == 0) {
         throw new IllegalArgumentException("Zero is not a valid field number.");
      } else {
         this.storeField(WireFormat.makeTag(fieldNumber, 2), value);
         return this;
      }
   }

   private UnknownFieldSetLite mergeFrom(final CodedInputStream input) throws IOException {
      int tag;
      do {
         tag = input.readTag();
      } while (tag != 0 && this.mergeFieldFrom(tag, input));

      return this;
   }

   @CanIgnoreReturnValue
   UnknownFieldSetLite mergeFrom(UnknownFieldSetLite other) {
      if (other.equals(getDefaultInstance())) {
         return this;
      } else {
         this.checkMutable();
         int newCount = this.count + other.count;
         this.ensureCapacity(newCount);
         System.arraycopy(other.tags, 0, this.tags, this.count, other.count);
         System.arraycopy(other.objects, 0, this.objects, this.count, other.count);
         this.count = newCount;
         return this;
      }
   }
}
