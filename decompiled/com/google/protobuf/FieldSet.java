package com.google.protobuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

final class FieldSet<T extends FieldSet.FieldDescriptorLite<T>> {
   private final SmallSortedMap<T, Object> fields;
   private boolean isImmutable;
   private boolean hasLazyField;
   private static final FieldSet<?> DEFAULT_INSTANCE = new FieldSet(true);

   private FieldSet() {
      this.fields = SmallSortedMap.newFieldMap();
   }

   private FieldSet(final boolean dummy) {
      this(SmallSortedMap.newFieldMap());
      this.makeImmutable();
   }

   private FieldSet(SmallSortedMap<T, Object> fields) {
      this.fields = fields;
      this.makeImmutable();
   }

   public static <T extends FieldSet.FieldDescriptorLite<T>> FieldSet<T> newFieldSet() {
      return new FieldSet<>();
   }

   public static <T extends FieldSet.FieldDescriptorLite<T>> FieldSet<T> emptySet() {
      return (FieldSet<T>)DEFAULT_INSTANCE;
   }

   public static <T extends FieldSet.FieldDescriptorLite<T>> FieldSet.Builder<T> newBuilder() {
      return new FieldSet.Builder<>();
   }

   boolean isEmpty() {
      return this.fields.isEmpty();
   }

   public void makeImmutable() {
      if (!this.isImmutable) {
         int n = this.fields.getNumArrayEntries();

         for (int i = 0; i < n; i++) {
            Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
            Object value = entry.getValue();
            if (value instanceof GeneratedMessageLite) {
               ((GeneratedMessageLite)value).makeImmutable();
            }
         }

         for (Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            Object value = entry.getValue();
            if (value instanceof GeneratedMessageLite) {
               ((GeneratedMessageLite)value).makeImmutable();
            }
         }

         this.fields.makeImmutable();
         this.isImmutable = true;
      }
   }

   public boolean isImmutable() {
      return this.isImmutable;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof FieldSet)) {
         return false;
      } else {
         FieldSet<?> other = (FieldSet<?>)o;
         return this.fields.equals(other.fields);
      }
   }

   @Override
   public int hashCode() {
      return this.fields.hashCode();
   }

   public FieldSet<T> clone() {
      FieldSet<T> clone = newFieldSet();
      int n = this.fields.getNumArrayEntries();

      for (int i = 0; i < n; i++) {
         Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
         clone.setField(entry.getKey(), entry.getValue());
      }

      for (Entry<T, Object> entry : this.fields.getOverflowEntries()) {
         clone.setField(entry.getKey(), entry.getValue());
      }

      clone.hasLazyField = this.hasLazyField;
      return clone;
   }

   public void clear() {
      this.fields.clear();
      this.hasLazyField = false;
   }

   public Map<T, Object> getAllFields() {
      if (this.hasLazyField) {
         SmallSortedMap<T, Object> result = cloneAllFieldsMap(this.fields, false, true);
         if (this.fields.isImmutable()) {
            result.makeImmutable();
         }

         return result;
      } else {
         return (Map<T, Object>)(this.fields.isImmutable() ? this.fields : Collections.unmodifiableMap(this.fields));
      }
   }

   private static <T extends FieldSet.FieldDescriptorLite<T>> SmallSortedMap<T, Object> cloneAllFieldsMap(
      SmallSortedMap<T, Object> fields, boolean copyList, boolean resolveLazyFields
   ) {
      SmallSortedMap<T, Object> result = SmallSortedMap.newFieldMap();
      int n = fields.getNumArrayEntries();

      for (int i = 0; i < n; i++) {
         cloneFieldEntry(result, fields.getArrayEntryAt(i), copyList, resolveLazyFields);
      }

      for (Entry<T, Object> entry : fields.getOverflowEntries()) {
         cloneFieldEntry(result, entry, copyList, resolveLazyFields);
      }

      return result;
   }

   private static <T extends FieldSet.FieldDescriptorLite<T>> void cloneFieldEntry(
      Map<T, Object> map, Entry<T, Object> entry, boolean copyList, boolean resolveLazyFields
   ) {
      T key = (T)entry.getKey();
      Object value = entry.getValue();
      if (resolveLazyFields && value instanceof LazyField) {
         map.put(key, ((LazyField)value).getValue());
      } else if (copyList && value instanceof List) {
         map.put(key, new ArrayList((List)value));
      } else {
         map.put(key, value);
      }
   }

   public Iterator<Entry<T, Object>> iterator() {
      if (this.isEmpty()) {
         return Collections.emptyIterator();
      } else {
         return (Iterator<Entry<T, Object>>)(this.hasLazyField
            ? new LazyField.LazyIterator<>(this.fields.entrySet().iterator())
            : this.fields.entrySet().iterator());
      }
   }

   Iterator<Entry<T, Object>> descendingIterator() {
      if (this.isEmpty()) {
         return Collections.emptyIterator();
      } else {
         return (Iterator<Entry<T, Object>>)(this.hasLazyField
            ? new LazyField.LazyIterator<>(this.fields.descendingEntrySet().iterator())
            : this.fields.descendingEntrySet().iterator());
      }
   }

   public boolean hasField(final T descriptor) {
      if (descriptor.isRepeated()) {
         throw new IllegalArgumentException("hasField() can only be called on non-repeated fields.");
      } else {
         return this.fields.get(descriptor) != null;
      }
   }

   public Object getField(final T descriptor) {
      Object o = this.fields.get(descriptor);
      return o instanceof LazyField ? ((LazyField)o).getValue() : o;
   }

   boolean lazyFieldCorrupted(final T descriptor) {
      Object o = this.fields.get(descriptor);
      return o instanceof LazyField && ((LazyField)o).isCorrupted();
   }

   public void setField(final T descriptor, Object value) {
      if (descriptor.isRepeated()) {
         if (!(value instanceof List)) {
            throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
         }

         List<?> list = (List<?>)value;
         int listSize = list.size();
         List<Object> newList = new ArrayList<>(listSize);

         for (int i = 0; i < listSize; i++) {
            Object element = list.get(i);
            this.verifyType(descriptor, element);
            newList.add(element);
         }

         value = newList;
      } else {
         this.verifyType(descriptor, value);
      }

      if (value instanceof LazyField) {
         this.hasLazyField = true;
      }

      this.fields.put(descriptor, value);
   }

   public void clearField(final T descriptor) {
      this.fields.remove(descriptor);
      if (this.fields.isEmpty()) {
         this.hasLazyField = false;
      }
   }

   public int getRepeatedFieldCount(final T descriptor) {
      if (!descriptor.isRepeated()) {
         throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
      } else {
         Object value = this.getField(descriptor);
         return value == null ? 0 : ((List)value).size();
      }
   }

   public Object getRepeatedField(final T descriptor, final int index) {
      if (!descriptor.isRepeated()) {
         throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
      } else {
         Object value = this.getField(descriptor);
         if (value == null) {
            throw new IndexOutOfBoundsException();
         } else {
            return ((List)value).get(index);
         }
      }
   }

   public void setRepeatedField(final T descriptor, final int index, final Object value) {
      if (!descriptor.isRepeated()) {
         throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
      } else {
         Object list = this.getField(descriptor);
         if (list == null) {
            throw new IndexOutOfBoundsException();
         } else {
            this.verifyType(descriptor, value);
            ((List)list).set(index, value);
         }
      }
   }

   public void addRepeatedField(final T descriptor, final Object value) {
      if (!descriptor.isRepeated()) {
         throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
      } else {
         this.verifyType(descriptor, value);
         Object existingValue = this.getField(descriptor);
         List<Object> list;
         if (existingValue == null) {
            list = new ArrayList<>();
            this.fields.put(descriptor, list);
         } else {
            list = (List<Object>)existingValue;
         }

         list.add(value);
      }
   }

   private void verifyType(final T descriptor, final Object value) {
      if (!isValidType(descriptor.getLiteType(), value)) {
         throw new IllegalArgumentException(
            String.format(
               "Wrong object type used with protocol message reflection.\nField number: %d, field java type: %s, value type: %s\n",
               descriptor.getNumber(),
               descriptor.getLiteType().getJavaType(),
               value.getClass().getName()
            )
         );
      }
   }

   private static boolean isValidType(final WireFormat.FieldType type, final Object value) {
      Internal.checkNotNull(value);
      switch (type.getJavaType()) {
         case INT:
            return value instanceof Integer;
         case LONG:
            return value instanceof Long;
         case FLOAT:
            return value instanceof Float;
         case DOUBLE:
            return value instanceof Double;
         case BOOLEAN:
            return value instanceof Boolean;
         case STRING:
            return value instanceof String;
         case BYTE_STRING:
            return value instanceof ByteString || value instanceof byte[];
         case ENUM:
            return value instanceof Integer || value instanceof Internal.EnumLite;
         case MESSAGE:
            return value instanceof MessageLite || value instanceof LazyField;
         default:
            return false;
      }
   }

   public boolean isInitialized() {
      int n = this.fields.getNumArrayEntries();

      for (int i = 0; i < n; i++) {
         if (!isInitialized(this.fields.getArrayEntryAt(i))) {
            return false;
         }
      }

      for (Entry<T, Object> entry : this.fields.getOverflowEntries()) {
         if (!isInitialized(entry)) {
            return false;
         }
      }

      return true;
   }

   private static <T extends FieldSet.FieldDescriptorLite<T>> boolean isInitialized(final Entry<T, Object> entry) {
      T descriptor = (T)entry.getKey();
      if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
         if (!descriptor.isRepeated()) {
            return isMessageFieldValueInitialized(entry.getValue());
         }

         List<?> list = (List<?>)entry.getValue();
         int listSize = list.size();

         for (int i = 0; i < listSize; i++) {
            Object element = list.get(i);
            if (!isMessageFieldValueInitialized(element)) {
               return false;
            }
         }
      }

      return true;
   }

   private static boolean isMessageFieldValueInitialized(Object value) {
      if (value instanceof MessageLiteOrBuilder) {
         return ((MessageLiteOrBuilder)value).isInitialized();
      } else if (value instanceof LazyField) {
         return true;
      } else {
         throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
      }
   }

   static int getWireFormatForFieldType(final WireFormat.FieldType type, boolean isPacked) {
      return isPacked ? 2 : type.getWireType();
   }

   public void mergeFrom(final FieldSet<T> other) {
      int n = other.fields.getNumArrayEntries();

      for (int i = 0; i < n; i++) {
         this.mergeFromField(other.fields.getArrayEntryAt(i));
      }

      for (Entry<T, Object> entry : other.fields.getOverflowEntries()) {
         this.mergeFromField(entry);
      }
   }

   private static Object cloneIfMutable(Object value) {
      if (value instanceof byte[]) {
         byte[] bytes = (byte[])value;
         byte[] copy = new byte[bytes.length];
         System.arraycopy(bytes, 0, copy, 0, bytes.length);
         return copy;
      } else {
         return value;
      }
   }

   private void mergeFromField(final Entry<T, Object> entry) {
      T descriptor = entry.getKey();
      Object otherValue = entry.getValue();
      boolean isLazyField = otherValue instanceof LazyField;
      if (descriptor.isRepeated()) {
         if (isLazyField) {
            throw new IllegalStateException("Lazy fields can not be repeated");
         }

         Object value = this.getField(descriptor);
         List<?> otherList = (List<?>)otherValue;
         int otherListSize = otherList.size();
         if (value == null) {
            value = new ArrayList(otherListSize);
         }

         List<Object> list = (List<Object>)value;

         for (int i = 0; i < otherListSize; i++) {
            Object element = otherList.get(i);
            list.add(cloneIfMutable(element));
         }

         this.fields.put(descriptor, value);
      } else if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
         Object value = this.getField(descriptor);
         if (value == null) {
            this.fields.put(descriptor, cloneIfMutable(otherValue));
            if (isLazyField) {
               this.hasLazyField = true;
            }
         } else {
            if (otherValue instanceof LazyField) {
               otherValue = ((LazyField)otherValue).getValue();
            }

            if (descriptor.internalMessageIsImmutable(value)) {
               MessageLite.Builder builder = ((MessageLite)value).toBuilder();
               descriptor.internalMergeFrom(builder, otherValue);
               this.fields.put(descriptor, builder.build());
            } else {
               descriptor.internalMergeFrom(value, otherValue);
            }
         }
      } else {
         if (isLazyField) {
            throw new IllegalStateException("Lazy fields must be message-valued");
         }

         this.fields.put(descriptor, cloneIfMutable(otherValue));
      }
   }

   public static Object readPrimitiveField(CodedInputStream input, final WireFormat.FieldType type, boolean checkUtf8) throws IOException {
      return checkUtf8 ? input.readPrimitiveField(type, WireFormat.Utf8Validation.STRICT) : input.readPrimitiveField(type, WireFormat.Utf8Validation.LOOSE);
   }

   public void writeTo(final CodedOutputStream output) throws IOException {
      int n = this.fields.getNumArrayEntries();

      for (int i = 0; i < n; i++) {
         Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
         writeField(entry.getKey(), entry.getValue(), output);
      }

      for (Entry<T, Object> entry : this.fields.getOverflowEntries()) {
         writeField(entry.getKey(), entry.getValue(), output);
      }
   }

   public void writeMessageSetTo(final CodedOutputStream output) throws IOException {
      int n = this.fields.getNumArrayEntries();

      for (int i = 0; i < n; i++) {
         this.writeMessageSetTo(this.fields.getArrayEntryAt(i), output);
      }

      for (Entry<T, Object> entry : this.fields.getOverflowEntries()) {
         this.writeMessageSetTo(entry, output);
      }
   }

   private void writeMessageSetTo(final Entry<T, Object> entry, final CodedOutputStream output) throws IOException {
      T descriptor = entry.getKey();
      if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !descriptor.isRepeated() && !descriptor.isPacked()) {
         Object value = entry.getValue();
         if (value instanceof LazyField) {
            ByteString valueBytes = ((LazyField)value).toByteString();
            output.writeRawMessageSetExtension(entry.getKey().getNumber(), valueBytes);
         } else {
            output.writeMessageSetExtension(entry.getKey().getNumber(), (MessageLite)value);
         }
      } else {
         writeField(descriptor, entry.getValue(), output);
      }
   }

   static void writeElement(final CodedOutputStream output, final WireFormat.FieldType type, final int number, final Object value) throws IOException {
      if (type == WireFormat.FieldType.GROUP) {
         output.writeGroup(number, (MessageLite)value);
      } else {
         output.writeTag(number, getWireFormatForFieldType(type, false));
         writeElementNoTag(output, type, value);
      }
   }

   static void writeElementNoTag(final CodedOutputStream output, final WireFormat.FieldType type, final Object value) throws IOException {
      switch (type) {
         case DOUBLE:
            output.writeDoubleNoTag((Double)value);
            break;
         case FLOAT:
            output.writeFloatNoTag((Float)value);
            break;
         case INT64:
            output.writeInt64NoTag((Long)value);
            break;
         case UINT64:
            output.writeUInt64NoTag((Long)value);
            break;
         case INT32:
            output.writeInt32NoTag((Integer)value);
            break;
         case FIXED64:
            output.writeFixed64NoTag((Long)value);
            break;
         case FIXED32:
            output.writeFixed32NoTag((Integer)value);
            break;
         case BOOL:
            output.writeBoolNoTag((Boolean)value);
            break;
         case GROUP:
            output.writeGroupNoTag((MessageLite)value);
            break;
         case MESSAGE:
            output.writeMessageNoTag((MessageLite)value);
            break;
         case STRING:
            if (value instanceof ByteString) {
               output.writeBytesNoTag((ByteString)value);
            } else {
               output.writeStringNoTag((String)value);
            }
            break;
         case BYTES:
            if (value instanceof ByteString) {
               output.writeBytesNoTag((ByteString)value);
            } else {
               output.writeByteArrayNoTag((byte[])value);
            }
            break;
         case UINT32:
            output.writeUInt32NoTag((Integer)value);
            break;
         case SFIXED32:
            output.writeSFixed32NoTag((Integer)value);
            break;
         case SFIXED64:
            output.writeSFixed64NoTag((Long)value);
            break;
         case SINT32:
            output.writeSInt32NoTag((Integer)value);
            break;
         case SINT64:
            output.writeSInt64NoTag((Long)value);
            break;
         case ENUM:
            if (value instanceof Internal.EnumLite) {
               output.writeEnumNoTag(((Internal.EnumLite)value).getNumber());
            } else {
               output.writeEnumNoTag((Integer)value);
            }
      }
   }

   public static void writeField(final FieldSet.FieldDescriptorLite<?> descriptor, final Object value, final CodedOutputStream output) throws IOException {
      WireFormat.FieldType type = descriptor.getLiteType();
      int number = descriptor.getNumber();
      if (descriptor.isRepeated()) {
         List<?> valueList = (List<?>)value;
         int valueListSize = valueList.size();
         if (descriptor.isPacked()) {
            if (valueList.isEmpty()) {
               return;
            }

            output.writeTag(number, 2);
            int dataSize = 0;

            for (int i = 0; i < valueListSize; i++) {
               Object element = valueList.get(i);
               dataSize += computeElementSizeNoTag(type, element);
            }

            output.writeUInt32NoTag(dataSize);

            for (int i = 0; i < valueListSize; i++) {
               Object element = valueList.get(i);
               writeElementNoTag(output, type, element);
            }
         } else {
            for (int i = 0; i < valueListSize; i++) {
               Object element = valueList.get(i);
               writeElement(output, type, number, element);
            }
         }
      } else if (value instanceof LazyField) {
         writeElement(output, type, number, ((LazyField)value).getValue());
      } else {
         writeElement(output, type, number, value);
      }
   }

   public int getSerializedSize() {
      int size = 0;
      int n = this.fields.getNumArrayEntries();

      for (int i = 0; i < n; i++) {
         Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
         size += computeFieldSize(entry.getKey(), entry.getValue());
      }

      for (Entry<T, Object> entry : this.fields.getOverflowEntries()) {
         size += computeFieldSize(entry.getKey(), entry.getValue());
      }

      return size;
   }

   public int getMessageSetSerializedSize() {
      int size = 0;
      int n = this.fields.getNumArrayEntries();

      for (int i = 0; i < n; i++) {
         size += this.getMessageSetSerializedSize(this.fields.getArrayEntryAt(i));
      }

      for (Entry<T, Object> entry : this.fields.getOverflowEntries()) {
         size += this.getMessageSetSerializedSize(entry);
      }

      return size;
   }

   private int getMessageSetSerializedSize(final Entry<T, Object> entry) {
      T descriptor = entry.getKey();
      Object value = entry.getValue();
      if (descriptor.getLiteJavaType() != WireFormat.JavaType.MESSAGE || descriptor.isRepeated() || descriptor.isPacked()) {
         return computeFieldSize(descriptor, value);
      } else {
         return value instanceof LazyField
            ? ((LazyField)value).computeMessageSetExtensionSize(entry.getKey().getNumber())
            : CodedOutputStream.computeMessageSetExtensionSize(entry.getKey().getNumber(), (MessageLite)value);
      }
   }

   static int computeElementSize(final WireFormat.FieldType type, final int number, final Object value) {
      int tagSize = CodedOutputStream.computeTagSize(number);
      if (type == WireFormat.FieldType.GROUP) {
         tagSize *= 2;
      }

      return tagSize + computeElementSizeNoTag(type, value);
   }

   static int computeElementSizeNoTag(final WireFormat.FieldType type, final Object value) {
      switch (type) {
         case DOUBLE:
            return CodedOutputStream.computeDoubleSizeNoTag((Double)value);
         case FLOAT:
            return CodedOutputStream.computeFloatSizeNoTag((Float)value);
         case INT64:
            return CodedOutputStream.computeInt64SizeNoTag((Long)value);
         case UINT64:
            return CodedOutputStream.computeUInt64SizeNoTag((Long)value);
         case INT32:
            return CodedOutputStream.computeInt32SizeNoTag((Integer)value);
         case FIXED64:
            return CodedOutputStream.computeFixed64SizeNoTag((Long)value);
         case FIXED32:
            return CodedOutputStream.computeFixed32SizeNoTag((Integer)value);
         case BOOL:
            return CodedOutputStream.computeBoolSizeNoTag((Boolean)value);
         case GROUP:
            return ((MessageLite)value).getSerializedSize();
         case MESSAGE:
            if (value instanceof LazyField) {
               return ((LazyField)value).computeSizeNoTag();
            }

            return CodedOutputStream.computeMessageSizeNoTag((MessageLite)value);
         case STRING:
            if (value instanceof ByteString) {
               return CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
            }

            return CodedOutputStream.computeStringSizeNoTag((String)value);
         case BYTES:
            if (value instanceof ByteString) {
               return CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
            }

            return CodedOutputStream.computeByteArraySizeNoTag((byte[])value);
         case UINT32:
            return CodedOutputStream.computeUInt32SizeNoTag((Integer)value);
         case SFIXED32:
            return CodedOutputStream.computeSFixed32SizeNoTag((Integer)value);
         case SFIXED64:
            return CodedOutputStream.computeSFixed64SizeNoTag((Long)value);
         case SINT32:
            return CodedOutputStream.computeSInt32SizeNoTag((Integer)value);
         case SINT64:
            return CodedOutputStream.computeSInt64SizeNoTag((Long)value);
         case ENUM:
            if (value instanceof Internal.EnumLite) {
               return CodedOutputStream.computeEnumSizeNoTag(((Internal.EnumLite)value).getNumber());
            }

            return CodedOutputStream.computeEnumSizeNoTag((Integer)value);
         default:
            throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
      }
   }

   public static int computeFieldSize(final FieldSet.FieldDescriptorLite<?> descriptor, final Object value) {
      WireFormat.FieldType type = descriptor.getLiteType();
      int number = descriptor.getNumber();
      if (!descriptor.isRepeated()) {
         return computeElementSize(type, number, value);
      } else {
         List<?> valueList = (List<?>)value;
         int valueListSize = valueList.size();
         if (descriptor.isPacked()) {
            if (valueList.isEmpty()) {
               return 0;
            } else {
               int dataSize = 0;

               for (int i = 0; i < valueListSize; i++) {
                  Object element = valueList.get(i);
                  dataSize += computeElementSizeNoTag(type, element);
               }

               return dataSize + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(dataSize);
            }
         } else {
            int size = 0;

            for (int i = 0; i < valueListSize; i++) {
               Object element = valueList.get(i);
               size += computeElementSize(type, number, element);
            }

            return size;
         }
      }
   }

   static final class Builder<T extends FieldSet.FieldDescriptorLite<T>> {
      private SmallSortedMap<T, Object> fields;
      private boolean hasLazyField;
      private boolean isMutable;
      private boolean hasNestedBuilders;

      private Builder() {
         this(SmallSortedMap.newFieldMap());
      }

      private Builder(SmallSortedMap<T, Object> fields) {
         this.fields = fields;
         this.isMutable = true;
      }

      public FieldSet<T> build() {
         return this.buildImpl(false);
      }

      public FieldSet<T> buildPartial() {
         return this.buildImpl(true);
      }

      private FieldSet<T> buildImpl(boolean partial) {
         if (this.fields.isEmpty()) {
            return FieldSet.emptySet();
         } else {
            this.isMutable = false;
            SmallSortedMap<T, Object> fieldsForBuild = this.fields;
            if (this.hasNestedBuilders) {
               fieldsForBuild = FieldSet.cloneAllFieldsMap(this.fields, false, false);
               replaceBuilders(fieldsForBuild, partial);
            }

            FieldSet<T> fieldSet = new FieldSet<>(fieldsForBuild);
            fieldSet.hasLazyField = this.hasLazyField;
            return fieldSet;
         }
      }

      private static <T extends FieldSet.FieldDescriptorLite<T>> void replaceBuilders(SmallSortedMap<T, Object> fieldMap, boolean partial) {
         int n = fieldMap.getNumArrayEntries();

         for (int i = 0; i < n; i++) {
            replaceBuilders(fieldMap.getArrayEntryAt(i), partial);
         }

         for (Entry<T, Object> entry : fieldMap.getOverflowEntries()) {
            replaceBuilders(entry, partial);
         }
      }

      private static <T extends FieldSet.FieldDescriptorLite<T>> void replaceBuilders(Entry<T, Object> entry, boolean partial) {
         entry.setValue(replaceBuilders(entry.getKey(), entry.getValue(), partial));
      }

      private static <T extends FieldSet.FieldDescriptorLite<T>> Object replaceBuilders(T descriptor, Object value, boolean partial) {
         if (value == null) {
            return value;
         } else if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
            if (descriptor.isRepeated()) {
               if (!(value instanceof List)) {
                  throw new IllegalStateException("Repeated field should contains a List but actually contains type: " + value.getClass());
               } else {
                  List<Object> list = (List<Object>)value;

                  for (int i = 0; i < list.size(); i++) {
                     Object oldElement = list.get(i);
                     Object newElement = replaceBuilder(oldElement, partial);
                     if (newElement != oldElement) {
                        if (list == value) {
                           list = new ArrayList<>(list);
                        }

                        list.set(i, newElement);
                     }
                  }

                  return list;
               }
            } else {
               return replaceBuilder(value, partial);
            }
         } else {
            return value;
         }
      }

      private static Object replaceBuilder(Object value, boolean partial) {
         if (!(value instanceof MessageLite.Builder)) {
            return value;
         } else {
            MessageLite.Builder builder = (MessageLite.Builder)value;
            return partial ? builder.buildPartial() : builder.build();
         }
      }

      public static <T extends FieldSet.FieldDescriptorLite<T>> FieldSet.Builder<T> fromFieldSet(FieldSet<T> fieldSet) {
         FieldSet.Builder<T> builder = new FieldSet.Builder<>(FieldSet.cloneAllFieldsMap(fieldSet.fields, true, false));
         builder.hasLazyField = fieldSet.hasLazyField;
         return builder;
      }

      public Map<T, Object> getAllFields() {
         if (this.hasLazyField) {
            SmallSortedMap<T, Object> result = FieldSet.cloneAllFieldsMap(this.fields, false, true);
            if (this.fields.isImmutable()) {
               result.makeImmutable();
            } else {
               replaceBuilders(result, true);
            }

            return result;
         } else {
            return (Map<T, Object>)(this.fields.isImmutable() ? this.fields : Collections.unmodifiableMap(this.fields));
         }
      }

      public boolean hasField(final T descriptor) {
         if (descriptor.isRepeated()) {
            throw new IllegalArgumentException("hasField() can only be called on non-repeated fields.");
         } else {
            return this.fields.get(descriptor) != null;
         }
      }

      public Object getField(final T descriptor) {
         Object value = this.getFieldAllowBuilders(descriptor);
         return replaceBuilders(descriptor, value, true);
      }

      Object getFieldAllowBuilders(final T descriptor) {
         Object o = this.fields.get(descriptor);
         return o instanceof LazyField ? ((LazyField)o).getValue() : o;
      }

      private void ensureIsMutable() {
         if (!this.isMutable) {
            this.fields = FieldSet.cloneAllFieldsMap(this.fields, true, false);
            this.isMutable = true;
         }
      }

      public void setField(final T descriptor, Object value) {
         this.ensureIsMutable();
         if (descriptor.isRepeated()) {
            if (!(value instanceof List)) {
               throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
            }

            List<Object> newList = new ArrayList<>((List)value);
            int newListSize = newList.size();

            for (int i = 0; i < newListSize; i++) {
               Object element = newList.get(i);
               this.verifyType(descriptor, element);
               this.hasNestedBuilders = this.hasNestedBuilders || element instanceof MessageLite.Builder;
            }

            value = newList;
         } else {
            this.verifyType(descriptor, value);
         }

         if (value instanceof LazyField) {
            this.hasLazyField = true;
         }

         this.hasNestedBuilders = this.hasNestedBuilders || value instanceof MessageLite.Builder;
         this.fields.put(descriptor, value);
      }

      public void clearField(final T descriptor) {
         this.ensureIsMutable();
         this.fields.remove(descriptor);
         if (this.fields.isEmpty()) {
            this.hasLazyField = false;
         }
      }

      public int getRepeatedFieldCount(final T descriptor) {
         if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedFieldCount() can only be called on repeated fields.");
         } else {
            Object value = this.getFieldAllowBuilders(descriptor);
            return value == null ? 0 : ((List)value).size();
         }
      }

      public Object getRepeatedField(final T descriptor, final int index) {
         if (this.hasNestedBuilders) {
            this.ensureIsMutable();
         }

         Object value = this.getRepeatedFieldAllowBuilders(descriptor, index);
         return replaceBuilder(value, true);
      }

      Object getRepeatedFieldAllowBuilders(final T descriptor, final int index) {
         if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
         } else {
            Object value = this.getFieldAllowBuilders(descriptor);
            if (value == null) {
               throw new IndexOutOfBoundsException();
            } else {
               return ((List)value).get(index);
            }
         }
      }

      public void setRepeatedField(final T descriptor, final int index, final Object value) {
         this.ensureIsMutable();
         if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
         } else {
            this.hasNestedBuilders = this.hasNestedBuilders || value instanceof MessageLite.Builder;
            Object list = this.getFieldAllowBuilders(descriptor);
            if (list == null) {
               throw new IndexOutOfBoundsException();
            } else {
               this.verifyType(descriptor, value);
               ((List)list).set(index, value);
            }
         }
      }

      public void addRepeatedField(final T descriptor, final Object value) {
         this.ensureIsMutable();
         if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
         } else {
            this.hasNestedBuilders = this.hasNestedBuilders || value instanceof MessageLite.Builder;
            this.verifyType(descriptor, value);
            Object existingValue = this.getFieldAllowBuilders(descriptor);
            List<Object> list;
            if (existingValue == null) {
               list = new ArrayList<>();
               this.fields.put(descriptor, list);
            } else {
               list = (List<Object>)existingValue;
            }

            list.add(value);
         }
      }

      private void verifyType(final T descriptor, final Object value) {
         if (!FieldSet.isValidType(descriptor.getLiteType(), value)) {
            if (descriptor.getLiteType().getJavaType() != WireFormat.JavaType.MESSAGE || !(value instanceof MessageLite.Builder)) {
               throw new IllegalArgumentException(
                  String.format(
                     "Wrong object type used with protocol message reflection.\nField number: %d, field java type: %s, value type: %s\n",
                     descriptor.getNumber(),
                     descriptor.getLiteType().getJavaType(),
                     value.getClass().getName()
                  )
               );
            }
         }
      }

      public boolean isInitialized() {
         int n = this.fields.getNumArrayEntries();

         for (int i = 0; i < n; i++) {
            if (!FieldSet.isInitialized(this.fields.getArrayEntryAt(i))) {
               return false;
            }
         }

         for (Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            if (!FieldSet.isInitialized(entry)) {
               return false;
            }
         }

         return true;
      }

      public void mergeFrom(final FieldSet<T> other) {
         this.ensureIsMutable();
         int n = other.fields.getNumArrayEntries();

         for (int i = 0; i < n; i++) {
            this.mergeFromField(other.fields.getArrayEntryAt(i));
         }

         for (Entry<T, Object> entry : other.fields.getOverflowEntries()) {
            this.mergeFromField(entry);
         }
      }

      private void mergeFromField(final Entry<T, Object> entry) {
         T descriptor = entry.getKey();
         Object otherValue = entry.getValue();
         boolean isLazyField = otherValue instanceof LazyField;
         if (descriptor.isRepeated()) {
            if (isLazyField) {
               throw new IllegalStateException("Lazy fields can not be repeated");
            }

            List<Object> value = (List<Object>)this.getFieldAllowBuilders(descriptor);
            List<?> otherList = (List<?>)otherValue;
            int otherListSize = otherList.size();
            if (value == null) {
               value = new ArrayList<>(otherListSize);
               this.fields.put(descriptor, value);
            }

            for (int i = 0; i < otherListSize; i++) {
               Object element = otherList.get(i);
               value.add(FieldSet.cloneIfMutable(element));
            }
         } else if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
            Object value = this.getFieldAllowBuilders(descriptor);
            if (value == null) {
               this.fields.put(descriptor, FieldSet.cloneIfMutable(otherValue));
               if (isLazyField) {
                  this.hasLazyField = true;
               }
            } else {
               if (otherValue instanceof LazyField) {
                  otherValue = ((LazyField)otherValue).getValue();
               }

               if (descriptor.internalMessageIsImmutable(value)) {
                  MessageLite.Builder builder = ((MessageLite)value).toBuilder();
                  descriptor.internalMergeFrom(builder, otherValue);
                  value = builder.build();
                  this.fields.put(descriptor, value);
               } else {
                  descriptor.internalMergeFrom(value, otherValue);
               }
            }
         } else {
            if (isLazyField) {
               throw new IllegalStateException("Lazy fields must be message-valued");
            }

            this.fields.put(descriptor, FieldSet.cloneIfMutable(otherValue));
         }
      }
   }

   public interface FieldDescriptorLite<T extends FieldSet.FieldDescriptorLite<T>> extends Comparable<T> {
      int getNumber();

      WireFormat.FieldType getLiteType();

      WireFormat.JavaType getLiteJavaType();

      boolean isRepeated();

      boolean isPacked();

      Internal.EnumLiteMap<?> getEnumType();

      boolean internalMessageIsImmutable(Object message);

      void internalMergeFrom(Object to, Object from);
   }
}
