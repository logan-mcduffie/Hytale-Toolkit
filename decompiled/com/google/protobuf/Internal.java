package com.google.protobuf;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;
import java.util.Map.Entry;

public final class Internal {
   static final Charset UTF_8 = Charset.forName("UTF-8");
   static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
   private static final int DEFAULT_BUFFER_SIZE = 4096;
   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
   public static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.wrap(EMPTY_BYTE_ARRAY);
   public static final CodedInputStream EMPTY_CODED_INPUT_STREAM = CodedInputStream.newInstance(EMPTY_BYTE_ARRAY);

   private Internal() {
   }

   static <T> T checkNotNull(T obj) {
      if (obj == null) {
         throw new NullPointerException();
      } else {
         return obj;
      }
   }

   static <T> T checkNotNull(T obj, String message) {
      if (obj == null) {
         throw new NullPointerException(message);
      } else {
         return obj;
      }
   }

   public static String stringDefaultValue(String bytes) {
      return new String(bytes.getBytes(ISO_8859_1), UTF_8);
   }

   public static ByteString bytesDefaultValue(String bytes) {
      return ByteString.copyFrom(bytes.getBytes(ISO_8859_1));
   }

   public static byte[] byteArrayDefaultValue(String bytes) {
      return bytes.getBytes(ISO_8859_1);
   }

   public static ByteBuffer byteBufferDefaultValue(String bytes) {
      return ByteBuffer.wrap(byteArrayDefaultValue(bytes));
   }

   public static ByteBuffer copyByteBuffer(ByteBuffer source) {
      ByteBuffer temp = source.duplicate();
      ((Buffer)temp).clear();
      ByteBuffer result = ByteBuffer.allocate(temp.capacity());
      result.put(temp);
      ((Buffer)result).clear();
      return result;
   }

   public static boolean isValidUtf8(ByteString byteString) {
      return byteString.isValidUtf8();
   }

   public static boolean isValidUtf8(byte[] byteArray) {
      return Utf8.isValidUtf8(byteArray);
   }

   public static byte[] toByteArray(String value) {
      return value.getBytes(UTF_8);
   }

   public static String toStringUtf8(byte[] bytes) {
      return new String(bytes, UTF_8);
   }

   public static int hashLong(long n) {
      return (int)(n ^ n >>> 32);
   }

   public static int hashBoolean(boolean b) {
      return b ? 1231 : 1237;
   }

   public static int hashEnum(Internal.EnumLite e) {
      return e.getNumber();
   }

   public static int hashEnumList(List<? extends Internal.EnumLite> list) {
      int hash = 1;

      for (Internal.EnumLite e : list) {
         hash = 31 * hash + hashEnum(e);
      }

      return hash;
   }

   public static boolean equals(List<byte[]> a, List<byte[]> b) {
      if (a.size() != b.size()) {
         return false;
      } else {
         for (int i = 0; i < a.size(); i++) {
            if (!Arrays.equals(a.get(i), b.get(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static int hashCode(List<byte[]> list) {
      int hash = 1;

      for (byte[] bytes : list) {
         hash = 31 * hash + hashCode(bytes);
      }

      return hash;
   }

   public static int hashCode(byte[] bytes) {
      return hashCode(bytes, 0, bytes.length);
   }

   static int hashCode(byte[] bytes, int offset, int length) {
      int h = partialHash(length, bytes, offset, length);
      return h == 0 ? 1 : h;
   }

   static int partialHash(int h, byte[] bytes, int offset, int length) {
      for (int i = offset; i < offset + length; i++) {
         h = h * 31 + bytes[i];
      }

      return h;
   }

   public static boolean equalsByteBuffer(ByteBuffer a, ByteBuffer b) {
      if (a.capacity() != b.capacity()) {
         return false;
      } else {
         ByteBuffer aDuplicate = a.duplicate();
         Java8Compatibility.clear(aDuplicate);
         ByteBuffer bDuplicate = b.duplicate();
         Java8Compatibility.clear(bDuplicate);
         return aDuplicate.equals(bDuplicate);
      }
   }

   public static boolean equalsByteBuffer(List<ByteBuffer> a, List<ByteBuffer> b) {
      if (a.size() != b.size()) {
         return false;
      } else {
         for (int i = 0; i < a.size(); i++) {
            if (!equalsByteBuffer(a.get(i), b.get(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static int hashCodeByteBuffer(List<ByteBuffer> list) {
      int hash = 1;

      for (ByteBuffer bytes : list) {
         hash = 31 * hash + hashCodeByteBuffer(bytes);
      }

      return hash;
   }

   public static int hashCodeByteBuffer(ByteBuffer bytes) {
      if (bytes.hasArray()) {
         int h = partialHash(bytes.capacity(), bytes.array(), bytes.arrayOffset(), bytes.capacity());
         return h == 0 ? 1 : h;
      } else {
         int bufferSize = bytes.capacity() > 4096 ? 4096 : bytes.capacity();
         byte[] buffer = new byte[bufferSize];
         ByteBuffer duplicated = bytes.duplicate();
         Java8Compatibility.clear(duplicated);
         int h = bytes.capacity();

         while (duplicated.remaining() > 0) {
            int length = duplicated.remaining() <= bufferSize ? duplicated.remaining() : bufferSize;
            duplicated.get(buffer, 0, length);
            h = partialHash(h, buffer, 0, length);
         }

         return h == 0 ? 1 : h;
      }
   }

   public static <T extends MessageLite> T getDefaultInstance(Class<T> clazz) {
      try {
         java.lang.reflect.Method method = clazz.getMethod("getDefaultInstance");
         return (T)method.invoke(method);
      } catch (Exception var2) {
         throw new RuntimeException("Failed to get default instance for " + clazz, var2);
      }
   }

   static Object mergeMessage(Object destination, Object source) {
      return ((MessageLite)destination).toBuilder().mergeFrom((MessageLite)source).buildPartial();
   }

   public interface BooleanList extends Internal.ProtobufList<Boolean> {
      boolean getBoolean(int index);

      void addBoolean(boolean element);

      @CanIgnoreReturnValue
      boolean setBoolean(int index, boolean element);

      Internal.BooleanList mutableCopyWithCapacity(int capacity);
   }

   public interface DoubleList extends Internal.ProtobufList<Double> {
      double getDouble(int index);

      void addDouble(double element);

      @CanIgnoreReturnValue
      double setDouble(int index, double element);

      Internal.DoubleList mutableCopyWithCapacity(int capacity);
   }

   public interface EnumLite {
      int getNumber();
   }

   public interface EnumLiteMap<T extends Internal.EnumLite> {
      T findValueByNumber(int number);
   }

   public interface EnumVerifier {
      boolean isInRange(int number);
   }

   public interface FloatList extends Internal.ProtobufList<Float> {
      float getFloat(int index);

      void addFloat(float element);

      @CanIgnoreReturnValue
      float setFloat(int index, float element);

      Internal.FloatList mutableCopyWithCapacity(int capacity);
   }

   public interface IntList extends Internal.ProtobufList<Integer> {
      int getInt(int index);

      void addInt(int element);

      @CanIgnoreReturnValue
      int setInt(int index, int element);

      Internal.IntList mutableCopyWithCapacity(int capacity);
   }

   public static class IntListAdapter<T> extends AbstractList<T> {
      private final Internal.IntList fromList;
      private final Internal.IntListAdapter.IntConverter<T> converter;

      public IntListAdapter(Internal.IntList fromList, Internal.IntListAdapter.IntConverter<T> converter) {
         this.fromList = fromList;
         this.converter = converter;
      }

      @Override
      public T get(int index) {
         return this.converter.convert(this.fromList.getInt(index));
      }

      @Override
      public int size() {
         return this.fromList.size();
      }

      public interface IntConverter<T> {
         T convert(int from);
      }
   }

   public static class ListAdapter<F, T> extends AbstractList<T> {
      private final List<F> fromList;
      private final Internal.ListAdapter.Converter<F, T> converter;

      public ListAdapter(List<F> fromList, Internal.ListAdapter.Converter<F, T> converter) {
         this.fromList = fromList;
         this.converter = converter;
      }

      @Override
      public T get(int index) {
         return this.converter.convert(this.fromList.get(index));
      }

      @Override
      public int size() {
         return this.fromList.size();
      }

      public interface Converter<F, T> {
         T convert(F from);
      }
   }

   public interface LongList extends Internal.ProtobufList<Long> {
      long getLong(int index);

      void addLong(long element);

      @CanIgnoreReturnValue
      long setLong(int index, long element);

      Internal.LongList mutableCopyWithCapacity(int capacity);
   }

   public static class MapAdapter<K, V, RealValue> extends AbstractMap<K, V> {
      private final Map<K, RealValue> realMap;
      private final Internal.MapAdapter.Converter<RealValue, V> valueConverter;

      public static <T extends Internal.EnumLite> Internal.MapAdapter.Converter<Integer, T> newEnumConverter(
         final Internal.EnumLiteMap<T> enumMap, final T unrecognizedValue
      ) {
         return new Internal.MapAdapter.Converter<Integer, T>() {
            public T doForward(Integer value) {
               T result = enumMap.findValueByNumber(value);
               return result == null ? unrecognizedValue : result;
            }

            public Integer doBackward(T value) {
               return value.getNumber();
            }
         };
      }

      public MapAdapter(Map<K, RealValue> realMap, Internal.MapAdapter.Converter<RealValue, V> valueConverter) {
         this.realMap = realMap;
         this.valueConverter = valueConverter;
      }

      @Override
      public V get(Object key) {
         RealValue result = this.realMap.get(key);
         return result == null ? null : this.valueConverter.doForward(result);
      }

      @Override
      public V put(K key, V value) {
         RealValue oldValue = this.realMap.put(key, this.valueConverter.doBackward(value));
         return oldValue == null ? null : this.valueConverter.doForward(oldValue);
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         return new Internal.MapAdapter.SetAdapter(this.realMap.entrySet());
      }

      public interface Converter<A, B> {
         B doForward(A object);

         A doBackward(B object);
      }

      private class EntryAdapter implements Entry<K, V> {
         private final Entry<K, RealValue> realEntry;

         public EntryAdapter(Entry<K, RealValue> realEntry) {
            this.realEntry = realEntry;
         }

         @Override
         public K getKey() {
            return this.realEntry.getKey();
         }

         @Override
         public V getValue() {
            return MapAdapter.this.valueConverter.doForward(this.realEntry.getValue());
         }

         @Override
         public V setValue(V value) {
            RealValue oldValue = this.realEntry.setValue(MapAdapter.this.valueConverter.doBackward(value));
            return oldValue == null ? null : MapAdapter.this.valueConverter.doForward(oldValue);
         }

         @Override
         public boolean equals(Object o) {
            if (o == this) {
               return true;
            } else if (!(o instanceof Entry)) {
               return false;
            } else {
               Entry<?, ?> other = (Entry<?, ?>)o;
               return this.getKey().equals(other.getKey()) && this.getValue().equals(this.getValue());
            }
         }

         @Override
         public int hashCode() {
            return this.realEntry.hashCode();
         }
      }

      private class IteratorAdapter implements Iterator<Entry<K, V>> {
         private final Iterator<Entry<K, RealValue>> realIterator;

         public IteratorAdapter(Iterator<Entry<K, RealValue>> realIterator) {
            this.realIterator = realIterator;
         }

         @Override
         public boolean hasNext() {
            return this.realIterator.hasNext();
         }

         public Entry<K, V> next() {
            return MapAdapter.this.new EntryAdapter(this.realIterator.next());
         }

         @Override
         public void remove() {
            this.realIterator.remove();
         }
      }

      private class SetAdapter extends AbstractSet<Entry<K, V>> {
         private final Set<Entry<K, RealValue>> realSet;

         public SetAdapter(Set<Entry<K, RealValue>> realSet) {
            this.realSet = realSet;
         }

         @Override
         public Iterator<Entry<K, V>> iterator() {
            return MapAdapter.this.new IteratorAdapter(this.realSet.iterator());
         }

         @Override
         public int size() {
            return this.realSet.size();
         }
      }
   }

   public interface ProtobufList<E> extends List<E>, RandomAccess {
      void makeImmutable();

      boolean isModifiable();

      Internal.ProtobufList<E> mutableCopyWithCapacity(int capacity);
   }
}
