package io.netty.handler.codec;

import io.netty.util.HashingStrategy;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public class DefaultHeaders<K, V, T extends Headers<K, V, T>> implements Headers<K, V, T> {
   static final int HASH_CODE_SEED = -1028477387;
   private final DefaultHeaders.HeaderEntry<K, V>[] entries;
   protected final DefaultHeaders.HeaderEntry<K, V> head;
   private final byte hashMask;
   private final ValueConverter<V> valueConverter;
   private final DefaultHeaders.NameValidator<K> nameValidator;
   private final DefaultHeaders.ValueValidator<V> valueValidator;
   private final HashingStrategy<K> hashingStrategy;
   int size;

   public DefaultHeaders(ValueConverter<V> valueConverter) {
      this(HashingStrategy.JAVA_HASHER, valueConverter);
   }

   public DefaultHeaders(ValueConverter<V> valueConverter, DefaultHeaders.NameValidator<K> nameValidator) {
      this(HashingStrategy.JAVA_HASHER, valueConverter, nameValidator);
   }

   public DefaultHeaders(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter) {
      this(nameHashingStrategy, valueConverter, DefaultHeaders.NameValidator.NOT_NULL);
   }

   public DefaultHeaders(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, DefaultHeaders.NameValidator<K> nameValidator) {
      this(nameHashingStrategy, valueConverter, nameValidator, 16);
   }

   public DefaultHeaders(
      HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, DefaultHeaders.NameValidator<K> nameValidator, int arraySizeHint
   ) {
      this(nameHashingStrategy, valueConverter, nameValidator, arraySizeHint, (DefaultHeaders.ValueValidator<V>)DefaultHeaders.ValueValidator.NO_VALIDATION);
   }

   public DefaultHeaders(
      HashingStrategy<K> nameHashingStrategy,
      ValueConverter<V> valueConverter,
      DefaultHeaders.NameValidator<K> nameValidator,
      int arraySizeHint,
      DefaultHeaders.ValueValidator<V> valueValidator
   ) {
      this.valueConverter = ObjectUtil.checkNotNull(valueConverter, "valueConverter");
      this.nameValidator = ObjectUtil.checkNotNull(nameValidator, "nameValidator");
      this.hashingStrategy = ObjectUtil.checkNotNull(nameHashingStrategy, "nameHashingStrategy");
      this.valueValidator = ObjectUtil.checkNotNull(valueValidator, "valueValidator");
      this.entries = new DefaultHeaders.HeaderEntry[MathUtil.findNextPositivePowerOfTwo(Math.max(2, Math.min(arraySizeHint, 128)))];
      this.hashMask = (byte)(this.entries.length - 1);
      this.head = new DefaultHeaders.HeaderEntry<>();
   }

   @Override
   public V get(K name) {
      ObjectUtil.checkNotNull(name, "name");
      int h = this.hashingStrategy.hashCode(name);
      int i = this.index(h);
      DefaultHeaders.HeaderEntry<K, V> e = this.entries[i];

      V value;
      for (value = null; e != null; e = e.next) {
         if (e.hash == h && this.hashingStrategy.equals(name, e.key)) {
            value = e.value;
         }
      }

      return value;
   }

   @Override
   public V get(K name, V defaultValue) {
      V value = this.get(name);
      return value == null ? defaultValue : value;
   }

   @Override
   public V getAndRemove(K name) {
      int h = this.hashingStrategy.hashCode(name);
      return this.remove0(h, this.index(h), ObjectUtil.checkNotNull(name, "name"));
   }

   @Override
   public V getAndRemove(K name, V defaultValue) {
      V value = this.getAndRemove(name);
      return value == null ? defaultValue : value;
   }

   @Override
   public List<V> getAll(K name) {
      ObjectUtil.checkNotNull(name, "name");
      LinkedList<V> values = new LinkedList<>();
      int h = this.hashingStrategy.hashCode(name);
      int i = this.index(h);

      for (DefaultHeaders.HeaderEntry<K, V> e = this.entries[i]; e != null; e = e.next) {
         if (e.hash == h && this.hashingStrategy.equals(name, e.key)) {
            values.addFirst(e.getValue());
         }
      }

      return values;
   }

   public Iterator<V> valueIterator(K name) {
      return new DefaultHeaders.ValueIterator(name);
   }

   @Override
   public List<V> getAllAndRemove(K name) {
      List<V> all = this.getAll(name);
      this.remove(name);
      return all;
   }

   @Override
   public boolean contains(K name) {
      return this.get(name) != null;
   }

   @Override
   public boolean containsObject(K name, Object value) {
      return this.contains(name, this.fromObject(name, value));
   }

   @Override
   public boolean containsBoolean(K name, boolean value) {
      return this.contains(name, this.fromBoolean(name, value));
   }

   @Override
   public boolean containsByte(K name, byte value) {
      return this.contains(name, this.fromByte(name, value));
   }

   @Override
   public boolean containsChar(K name, char value) {
      return this.contains(name, this.fromChar(name, value));
   }

   @Override
   public boolean containsShort(K name, short value) {
      return this.contains(name, this.fromShort(name, value));
   }

   @Override
   public boolean containsInt(K name, int value) {
      return this.contains(name, this.fromInt(name, value));
   }

   @Override
   public boolean containsLong(K name, long value) {
      return this.contains(name, this.fromLong(name, value));
   }

   @Override
   public boolean containsFloat(K name, float value) {
      return this.contains(name, this.fromFloat(name, value));
   }

   @Override
   public boolean containsDouble(K name, double value) {
      return this.contains(name, this.fromDouble(name, value));
   }

   @Override
   public boolean containsTimeMillis(K name, long value) {
      return this.contains(name, this.fromTimeMillis(name, value));
   }

   @Override
   public boolean contains(K name, V value) {
      return this.contains(name, value, HashingStrategy.JAVA_HASHER);
   }

   public final boolean contains(K name, V value, HashingStrategy<? super V> valueHashingStrategy) {
      ObjectUtil.checkNotNull(name, "name");
      int h = this.hashingStrategy.hashCode(name);
      int i = this.index(h);

      for (DefaultHeaders.HeaderEntry<K, V> e = this.entries[i]; e != null; e = e.next) {
         if (e.hash == h && this.hashingStrategy.equals(name, e.key) && valueHashingStrategy.equals(value, e.value)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.head == this.head.after;
   }

   @Override
   public Set<K> names() {
      if (this.isEmpty()) {
         return Collections.emptySet();
      } else {
         Set<K> names = new LinkedHashSet<>(this.size());

         for (DefaultHeaders.HeaderEntry<K, V> e = this.head.after; e != this.head; e = e.after) {
            names.add(e.getKey());
         }

         return names;
      }
   }

   @Override
   public T add(K name, V value) {
      this.validateName(this.nameValidator, true, name);
      this.validateValue(this.valueValidator, name, value);
      ObjectUtil.checkNotNull(value, "value");
      int h = this.hashingStrategy.hashCode(name);
      int i = this.index(h);
      this.add0(h, i, name, value);
      return this.thisT();
   }

   @Override
   public T add(K name, Iterable<? extends V> values) {
      this.validateName(this.nameValidator, true, name);
      int h = this.hashingStrategy.hashCode(name);
      int i = this.index(h);

      for (V v : values) {
         this.validateValue(this.valueValidator, name, v);
         this.add0(h, i, name, v);
      }

      return this.thisT();
   }

   @Override
   public T add(K name, V... values) {
      this.validateName(this.nameValidator, true, name);
      int h = this.hashingStrategy.hashCode(name);
      int i = this.index(h);

      for (V v : values) {
         this.validateValue(this.valueValidator, name, v);
         this.add0(h, i, name, v);
      }

      return this.thisT();
   }

   @Override
   public T addObject(K name, Object value) {
      return this.add(name, this.fromObject(name, value));
   }

   @Override
   public T addObject(K name, Iterable<?> values) {
      for (Object value : values) {
         this.addObject(name, value);
      }

      return this.thisT();
   }

   @Override
   public T addObject(K name, Object... values) {
      for (Object value : values) {
         this.addObject(name, value);
      }

      return this.thisT();
   }

   @Override
   public T addInt(K name, int value) {
      return this.add(name, this.fromInt(name, value));
   }

   @Override
   public T addLong(K name, long value) {
      return this.add(name, this.fromLong(name, value));
   }

   @Override
   public T addDouble(K name, double value) {
      return this.add(name, this.fromDouble(name, value));
   }

   @Override
   public T addTimeMillis(K name, long value) {
      return this.add(name, this.fromTimeMillis(name, value));
   }

   @Override
   public T addChar(K name, char value) {
      return this.add(name, this.fromChar(name, value));
   }

   @Override
   public T addBoolean(K name, boolean value) {
      return this.add(name, this.fromBoolean(name, value));
   }

   @Override
   public T addFloat(K name, float value) {
      return this.add(name, this.fromFloat(name, value));
   }

   @Override
   public T addByte(K name, byte value) {
      return this.add(name, this.fromByte(name, value));
   }

   @Override
   public T addShort(K name, short value) {
      return this.add(name, this.fromShort(name, value));
   }

   @Override
   public T add(Headers<? extends K, ? extends V, ?> headers) {
      if (headers == this) {
         throw new IllegalArgumentException("can't add to itself.");
      } else {
         this.addImpl(headers);
         return this.thisT();
      }
   }

   protected void addImpl(Headers<? extends K, ? extends V, ?> headers) {
      if (headers instanceof DefaultHeaders) {
         DefaultHeaders<? extends K, ? extends V, T> defaultHeaders = (DefaultHeaders<? extends K, ? extends V, T>)headers;
         DefaultHeaders.HeaderEntry<? extends K, ? extends V> e = defaultHeaders.head.after;
         if (defaultHeaders.hashingStrategy == this.hashingStrategy && defaultHeaders.nameValidator == this.nameValidator) {
            while (e != defaultHeaders.head) {
               this.add0(e.hash, this.index(e.hash), (K)e.key, (V)e.value);
               e = e.after;
            }
         } else {
            while (e != defaultHeaders.head) {
               this.add((K)e.key, (V)e.value);
               e = e.after;
            }
         }
      } else {
         for (Entry<? extends K, ? extends V> header : headers) {
            this.add((K)header.getKey(), (V)header.getValue());
         }
      }
   }

   @Override
   public T set(K name, V value) {
      this.validateName(this.nameValidator, false, name);
      this.validateValue(this.valueValidator, name, value);
      ObjectUtil.checkNotNull(value, "value");
      int h = this.hashingStrategy.hashCode(name);
      int i = this.index(h);
      this.remove0(h, i, name);
      this.add0(h, i, name, value);
      return this.thisT();
   }

   @Override
   public T set(K name, Iterable<? extends V> values) {
      this.validateName(this.nameValidator, false, name);
      ObjectUtil.checkNotNull(values, "values");
      int h = this.hashingStrategy.hashCode(name);
      int i = this.index(h);
      this.remove0(h, i, name);

      for (V v : values) {
         if (v == null) {
            break;
         }

         this.validateValue(this.valueValidator, name, v);
         this.add0(h, i, name, v);
      }

      return this.thisT();
   }

   @Override
   public T set(K name, V... values) {
      this.validateName(this.nameValidator, false, name);
      ObjectUtil.checkNotNull((T)values, "values");
      int h = this.hashingStrategy.hashCode(name);
      int i = this.index(h);
      this.remove0(h, i, name);

      for (V v : values) {
         if (v == null) {
            break;
         }

         this.validateValue(this.valueValidator, name, v);
         this.add0(h, i, name, v);
      }

      return this.thisT();
   }

   @Override
   public T setObject(K name, Object value) {
      V convertedValue = ObjectUtil.checkNotNull(this.fromObject(name, value), "convertedValue");
      return this.set(name, convertedValue);
   }

   @Override
   public T setObject(K name, Iterable<?> values) {
      this.validateName(this.nameValidator, false, name);
      int h = this.hashingStrategy.hashCode(name);
      int i = this.index(h);
      this.remove0(h, i, name);

      for (Object v : values) {
         if (v == null) {
            break;
         }

         V converted = this.fromObject(name, v);
         this.validateValue(this.valueValidator, name, converted);
         this.add0(h, i, name, converted);
      }

      return this.thisT();
   }

   @Override
   public T setObject(K name, Object... values) {
      this.validateName(this.nameValidator, false, name);
      int h = this.hashingStrategy.hashCode(name);
      int i = this.index(h);
      this.remove0(h, i, name);

      for (Object v : values) {
         if (v == null) {
            break;
         }

         V converted = this.fromObject(name, v);
         this.validateValue(this.valueValidator, name, converted);
         this.add0(h, i, name, converted);
      }

      return this.thisT();
   }

   @Override
   public T setInt(K name, int value) {
      return this.set(name, this.fromInt(name, value));
   }

   @Override
   public T setLong(K name, long value) {
      return this.set(name, this.fromLong(name, value));
   }

   @Override
   public T setDouble(K name, double value) {
      return this.set(name, this.fromDouble(name, value));
   }

   @Override
   public T setTimeMillis(K name, long value) {
      return this.set(name, this.fromTimeMillis(name, value));
   }

   @Override
   public T setFloat(K name, float value) {
      return this.set(name, this.fromFloat(name, value));
   }

   @Override
   public T setChar(K name, char value) {
      return this.set(name, this.fromChar(name, value));
   }

   @Override
   public T setBoolean(K name, boolean value) {
      return this.set(name, this.fromBoolean(name, value));
   }

   @Override
   public T setByte(K name, byte value) {
      return this.set(name, this.fromByte(name, value));
   }

   @Override
   public T setShort(K name, short value) {
      return this.set(name, this.fromShort(name, value));
   }

   @Override
   public T set(Headers<? extends K, ? extends V, ?> headers) {
      if (headers != this) {
         this.clear();
         this.addImpl(headers);
      }

      return this.thisT();
   }

   @Override
   public T setAll(Headers<? extends K, ? extends V, ?> headers) {
      if (headers != this) {
         for (K key : headers.names()) {
            this.remove(key);
         }

         this.addImpl(headers);
      }

      return this.thisT();
   }

   @Override
   public boolean remove(K name) {
      return this.getAndRemove(name) != null;
   }

   @Override
   public T clear() {
      Arrays.fill(this.entries, null);
      this.head.before = this.head.after = this.head;
      this.size = 0;
      return this.thisT();
   }

   @Override
   public Iterator<Entry<K, V>> iterator() {
      return new DefaultHeaders.HeaderIterator();
   }

   @Override
   public Boolean getBoolean(K name) {
      V v = this.get(name);

      try {
         return v != null ? this.toBoolean(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public boolean getBoolean(K name, boolean defaultValue) {
      Boolean v = this.getBoolean(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Byte getByte(K name) {
      V v = this.get(name);

      try {
         return v != null ? this.toByte(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public byte getByte(K name, byte defaultValue) {
      Byte v = this.getByte(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Character getChar(K name) {
      V v = this.get(name);

      try {
         return v != null ? this.toChar(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public char getChar(K name, char defaultValue) {
      Character v = this.getChar(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Short getShort(K name) {
      V v = this.get(name);

      try {
         return v != null ? this.toShort(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public short getShort(K name, short defaultValue) {
      Short v = this.getShort(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Integer getInt(K name) {
      V v = this.get(name);

      try {
         return v != null ? this.toInt(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public int getInt(K name, int defaultValue) {
      Integer v = this.getInt(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Long getLong(K name) {
      V v = this.get(name);

      try {
         return v != null ? this.toLong(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public long getLong(K name, long defaultValue) {
      Long v = this.getLong(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Float getFloat(K name) {
      V v = this.get(name);

      try {
         return v != null ? this.toFloat(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public float getFloat(K name, float defaultValue) {
      Float v = this.getFloat(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Double getDouble(K name) {
      V v = this.get(name);

      try {
         return v != null ? this.toDouble(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public double getDouble(K name, double defaultValue) {
      Double v = this.getDouble(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Long getTimeMillis(K name) {
      V v = this.get(name);

      try {
         return v != null ? this.toTimeMillis(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public long getTimeMillis(K name, long defaultValue) {
      Long v = this.getTimeMillis(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Boolean getBooleanAndRemove(K name) {
      V v = this.getAndRemove(name);

      try {
         return v != null ? this.toBoolean(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public boolean getBooleanAndRemove(K name, boolean defaultValue) {
      Boolean v = this.getBooleanAndRemove(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Byte getByteAndRemove(K name) {
      V v = this.getAndRemove(name);

      try {
         return v != null ? this.toByte(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public byte getByteAndRemove(K name, byte defaultValue) {
      Byte v = this.getByteAndRemove(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Character getCharAndRemove(K name) {
      V v = this.getAndRemove(name);

      try {
         return v != null ? this.toChar(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public char getCharAndRemove(K name, char defaultValue) {
      Character v = this.getCharAndRemove(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Short getShortAndRemove(K name) {
      V v = this.getAndRemove(name);

      try {
         return v != null ? this.toShort(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public short getShortAndRemove(K name, short defaultValue) {
      Short v = this.getShortAndRemove(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Integer getIntAndRemove(K name) {
      V v = this.getAndRemove(name);

      try {
         return v != null ? this.toInt(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public int getIntAndRemove(K name, int defaultValue) {
      Integer v = this.getIntAndRemove(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Long getLongAndRemove(K name) {
      V v = this.getAndRemove(name);

      try {
         return v != null ? this.toLong(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public long getLongAndRemove(K name, long defaultValue) {
      Long v = this.getLongAndRemove(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Float getFloatAndRemove(K name) {
      V v = this.getAndRemove(name);

      try {
         return v != null ? this.toFloat(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public float getFloatAndRemove(K name, float defaultValue) {
      Float v = this.getFloatAndRemove(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Double getDoubleAndRemove(K name) {
      V v = this.getAndRemove(name);

      try {
         return v != null ? this.toDouble(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public double getDoubleAndRemove(K name, double defaultValue) {
      Double v = this.getDoubleAndRemove(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public Long getTimeMillisAndRemove(K name) {
      V v = this.getAndRemove(name);

      try {
         return v != null ? this.toTimeMillis(name, v) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public long getTimeMillisAndRemove(K name, long defaultValue) {
      Long v = this.getTimeMillisAndRemove(name);
      return v != null ? v : defaultValue;
   }

   @Override
   public boolean equals(Object o) {
      return !(o instanceof Headers) ? false : this.equals((Headers<K, V, ?>)o, HashingStrategy.JAVA_HASHER);
   }

   @Override
   public int hashCode() {
      return this.hashCode(HashingStrategy.JAVA_HASHER);
   }

   public final boolean equals(Headers<K, V, ?> h2, HashingStrategy<V> valueHashingStrategy) {
      if (h2.size() != this.size()) {
         return false;
      } else if (this == h2) {
         return true;
      } else {
         for (K name : this.names()) {
            List<V> otherValues = h2.getAll(name);
            List<V> values = this.getAll(name);
            if (otherValues.size() != values.size()) {
               return false;
            }

            for (int i = 0; i < otherValues.size(); i++) {
               if (!valueHashingStrategy.equals(otherValues.get(i), values.get(i))) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public final int hashCode(HashingStrategy<V> valueHashingStrategy) {
      int result = -1028477387;

      for (K name : this.names()) {
         result = 31 * result + this.hashingStrategy.hashCode(name);
         List<V> values = this.getAll(name);

         for (int i = 0; i < values.size(); i++) {
            result = 31 * result + valueHashingStrategy.hashCode(values.get(i));
         }
      }

      return result;
   }

   @Override
   public String toString() {
      return HeadersUtils.toString(this.getClass(), this.iterator(), this.size());
   }

   protected void validateName(DefaultHeaders.NameValidator<K> validator, boolean forAdd, K name) {
      validator.validateName(name);
   }

   protected void validateValue(DefaultHeaders.ValueValidator<V> validator, K name, V value) {
      try {
         validator.validate(value);
      } catch (IllegalArgumentException var5) {
         throw new IllegalArgumentException("Validation failed for header '" + name + "'", var5);
      }
   }

   protected DefaultHeaders.HeaderEntry<K, V> newHeaderEntry(int h, K name, V value, DefaultHeaders.HeaderEntry<K, V> next) {
      return new DefaultHeaders.HeaderEntry<>(h, name, value, next, this.head);
   }

   protected ValueConverter<V> valueConverter() {
      return this.valueConverter;
   }

   protected DefaultHeaders.NameValidator<K> nameValidator() {
      return this.nameValidator;
   }

   protected DefaultHeaders.ValueValidator<V> valueValidator() {
      return this.valueValidator;
   }

   private int index(int hash) {
      return hash & this.hashMask;
   }

   private void add0(int h, int i, K name, V value) {
      this.entries[i] = this.newHeaderEntry(h, name, value, this.entries[i]);
      this.size++;
   }

   private V remove0(int h, int i, K name) {
      DefaultHeaders.HeaderEntry<K, V> e = this.entries[i];
      if (e == null) {
         return null;
      } else {
         V value = null;

         for (DefaultHeaders.HeaderEntry<K, V> next = e.next; next != null; next = e.next) {
            if (next.hash == h && this.hashingStrategy.equals(name, next.key)) {
               value = next.value;
               e.next = next.next;
               next.remove();
               this.size--;
            } else {
               e = next;
            }
         }

         e = this.entries[i];
         if (e.hash == h && this.hashingStrategy.equals(name, e.key)) {
            if (value == null) {
               value = e.value;
            }

            this.entries[i] = e.next;
            e.remove();
            this.size--;
         }

         return value;
      }
   }

   DefaultHeaders.HeaderEntry<K, V> remove0(DefaultHeaders.HeaderEntry<K, V> entry, DefaultHeaders.HeaderEntry<K, V> previous) {
      int i = this.index(entry.hash);
      DefaultHeaders.HeaderEntry<K, V> firstEntry = this.entries[i];
      if (firstEntry == entry) {
         this.entries[i] = entry.next;
         previous = this.entries[i];
      } else if (previous == null) {
         previous = firstEntry;

         DefaultHeaders.HeaderEntry<K, V> next;
         for (next = firstEntry.next; next != null && next != entry; next = next.next) {
            previous = next;
         }

         assert next != null : "Entry not found in its hash bucket: " + entry;

         previous.next = entry.next;
      } else {
         previous.next = entry.next;
      }

      entry.remove();
      this.size--;
      return previous;
   }

   private T thisT() {
      return (T)this;
   }

   private V fromObject(K name, Object value) {
      try {
         return this.valueConverter.convertObject(ObjectUtil.checkNotNull(value, "value"));
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert object value for header '" + name + '\'', var4);
      }
   }

   private V fromBoolean(K name, boolean value) {
      try {
         return this.valueConverter.convertBoolean(value);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert boolean value for header '" + name + '\'', var4);
      }
   }

   private V fromByte(K name, byte value) {
      try {
         return this.valueConverter.convertByte(value);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert byte value for header '" + name + '\'', var4);
      }
   }

   private V fromChar(K name, char value) {
      try {
         return this.valueConverter.convertChar(value);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert char value for header '" + name + '\'', var4);
      }
   }

   private V fromShort(K name, short value) {
      try {
         return this.valueConverter.convertShort(value);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert short value for header '" + name + '\'', var4);
      }
   }

   private V fromInt(K name, int value) {
      try {
         return this.valueConverter.convertInt(value);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert int value for header '" + name + '\'', var4);
      }
   }

   private V fromLong(K name, long value) {
      try {
         return this.valueConverter.convertLong(value);
      } catch (IllegalArgumentException var5) {
         throw new IllegalArgumentException("Failed to convert long value for header '" + name + '\'', var5);
      }
   }

   private V fromFloat(K name, float value) {
      try {
         return this.valueConverter.convertFloat(value);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert float value for header '" + name + '\'', var4);
      }
   }

   private V fromDouble(K name, double value) {
      try {
         return this.valueConverter.convertDouble(value);
      } catch (IllegalArgumentException var5) {
         throw new IllegalArgumentException("Failed to convert double value for header '" + name + '\'', var5);
      }
   }

   private V fromTimeMillis(K name, long value) {
      try {
         return this.valueConverter.convertTimeMillis(value);
      } catch (IllegalArgumentException var5) {
         throw new IllegalArgumentException("Failed to convert millsecond value for header '" + name + '\'', var5);
      }
   }

   private boolean toBoolean(K name, V value) {
      try {
         return this.valueConverter.convertToBoolean(value);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to boolean for header '" + name + '\'');
      }
   }

   private byte toByte(K name, V value) {
      try {
         return this.valueConverter.convertToByte(value);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to byte for header '" + name + '\'');
      }
   }

   private char toChar(K name, V value) {
      try {
         return this.valueConverter.convertToChar(value);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to char for header '" + name + '\'');
      }
   }

   private short toShort(K name, V value) {
      try {
         return this.valueConverter.convertToShort(value);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to short for header '" + name + '\'');
      }
   }

   private int toInt(K name, V value) {
      try {
         return this.valueConverter.convertToInt(value);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to int for header '" + name + 39);
      }
   }

   private long toLong(K name, V value) {
      try {
         return this.valueConverter.convertToLong(value);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to long for header '" + name + '\'');
      }
   }

   private float toFloat(K name, V value) {
      try {
         return this.valueConverter.convertToFloat(value);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to float for header '" + name + '\'');
      }
   }

   private double toDouble(K name, V value) {
      try {
         return this.valueConverter.convertToDouble(value);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to double for header '" + name + '\'');
      }
   }

   private long toTimeMillis(K name, V value) {
      try {
         return this.valueConverter.convertToTimeMillis(value);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to millsecond for header '" + name + '\'');
      }
   }

   public DefaultHeaders<K, V, T> copy() {
      DefaultHeaders<K, V, T> copy = new DefaultHeaders<>(this.hashingStrategy, this.valueConverter, this.nameValidator, this.entries.length);
      copy.addImpl(this);
      return copy;
   }

   protected static class HeaderEntry<K, V> implements Entry<K, V> {
      protected final int hash;
      protected final K key;
      protected V value;
      protected DefaultHeaders.HeaderEntry<K, V> next;
      protected DefaultHeaders.HeaderEntry<K, V> before;
      protected DefaultHeaders.HeaderEntry<K, V> after;

      protected HeaderEntry(int hash, K key) {
         this.hash = hash;
         this.key = key;
      }

      HeaderEntry(int hash, K key, V value, DefaultHeaders.HeaderEntry<K, V> next, DefaultHeaders.HeaderEntry<K, V> head) {
         this.hash = hash;
         this.key = key;
         this.value = value;
         this.next = next;
         this.after = head;
         this.before = head.before;
         this.pointNeighborsToThis();
      }

      HeaderEntry() {
         this.hash = -1;
         this.key = null;
         this.before = this.after = this;
      }

      protected final void pointNeighborsToThis() {
         this.before.after = this;
         this.after.before = this;
      }

      public final DefaultHeaders.HeaderEntry<K, V> before() {
         return this.before;
      }

      public final DefaultHeaders.HeaderEntry<K, V> after() {
         return this.after;
      }

      protected void remove() {
         this.before.after = this.after;
         this.after.before = this.before;
      }

      @Override
      public final K getKey() {
         return this.key;
      }

      @Override
      public final V getValue() {
         return this.value;
      }

      @Override
      public final V setValue(V value) {
         ObjectUtil.checkNotNull(value, "value");
         V oldValue = this.value;
         this.value = value;
         return oldValue;
      }

      @Override
      public final String toString() {
         return this.key.toString() + '=' + this.value.toString();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> other = (Entry<?, ?>)o;
            return (this.getKey() == null ? other.getKey() == null : this.getKey().equals(other.getKey()))
               && (this.getValue() == null ? other.getValue() == null : this.getValue().equals(other.getValue()));
         }
      }

      @Override
      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }
   }

   private final class HeaderIterator implements Iterator<Entry<K, V>> {
      private DefaultHeaders.HeaderEntry<K, V> current = DefaultHeaders.this.head;

      private HeaderIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.current.after != DefaultHeaders.this.head;
      }

      public Entry<K, V> next() {
         this.current = this.current.after;
         if (this.current == DefaultHeaders.this.head) {
            throw new NoSuchElementException();
         } else {
            return this.current;
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("read only");
      }
   }

   public interface NameValidator<K> {
      DefaultHeaders.NameValidator NOT_NULL = new DefaultHeaders.NameValidator() {
         @Override
         public void validateName(Object name) {
            ObjectUtil.checkNotNull(name, "name");
         }
      };

      void validateName(K var1);
   }

   private final class ValueIterator implements Iterator<V> {
      private final K name;
      private final int hash;
      private DefaultHeaders.HeaderEntry<K, V> removalPrevious;
      private DefaultHeaders.HeaderEntry<K, V> previous;
      private DefaultHeaders.HeaderEntry<K, V> next;

      ValueIterator(K name) {
         this.name = ObjectUtil.checkNotNull(name, "name");
         this.hash = DefaultHeaders.this.hashingStrategy.hashCode(name);
         this.calculateNext(DefaultHeaders.this.entries[DefaultHeaders.this.index(this.hash)]);
      }

      @Override
      public boolean hasNext() {
         return this.next != null;
      }

      @Override
      public V next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            if (this.previous != null) {
               this.removalPrevious = this.previous;
            }

            this.previous = this.next;
            this.calculateNext(this.next.next);
            return this.previous.value;
         }
      }

      @Override
      public void remove() {
         if (this.previous == null) {
            throw new IllegalStateException();
         } else {
            this.removalPrevious = DefaultHeaders.this.remove0(this.previous, this.removalPrevious);
            this.previous = null;
         }
      }

      private void calculateNext(DefaultHeaders.HeaderEntry<K, V> entry) {
         while (entry != null) {
            if (entry.hash == this.hash && DefaultHeaders.this.hashingStrategy.equals(this.name, entry.key)) {
               this.next = entry;
               return;
            }

            entry = entry.next;
         }

         this.next = null;
      }
   }

   public interface ValueValidator<V> {
      DefaultHeaders.ValueValidator<?> NO_VALIDATION = new DefaultHeaders.ValueValidator<Object>() {
         @Override
         public void validate(Object value) {
         }
      };

      void validate(V var1);
   }
}
