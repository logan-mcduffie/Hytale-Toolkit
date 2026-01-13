package io.netty.handler.codec;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public interface Headers<K, V, T extends Headers<K, V, T>> extends Iterable<Entry<K, V>> {
   V get(K var1);

   V get(K var1, V var2);

   V getAndRemove(K var1);

   V getAndRemove(K var1, V var2);

   List<V> getAll(K var1);

   List<V> getAllAndRemove(K var1);

   Boolean getBoolean(K var1);

   boolean getBoolean(K var1, boolean var2);

   Byte getByte(K var1);

   byte getByte(K var1, byte var2);

   Character getChar(K var1);

   char getChar(K var1, char var2);

   Short getShort(K var1);

   short getShort(K var1, short var2);

   Integer getInt(K var1);

   int getInt(K var1, int var2);

   Long getLong(K var1);

   long getLong(K var1, long var2);

   Float getFloat(K var1);

   float getFloat(K var1, float var2);

   Double getDouble(K var1);

   double getDouble(K var1, double var2);

   Long getTimeMillis(K var1);

   long getTimeMillis(K var1, long var2);

   Boolean getBooleanAndRemove(K var1);

   boolean getBooleanAndRemove(K var1, boolean var2);

   Byte getByteAndRemove(K var1);

   byte getByteAndRemove(K var1, byte var2);

   Character getCharAndRemove(K var1);

   char getCharAndRemove(K var1, char var2);

   Short getShortAndRemove(K var1);

   short getShortAndRemove(K var1, short var2);

   Integer getIntAndRemove(K var1);

   int getIntAndRemove(K var1, int var2);

   Long getLongAndRemove(K var1);

   long getLongAndRemove(K var1, long var2);

   Float getFloatAndRemove(K var1);

   float getFloatAndRemove(K var1, float var2);

   Double getDoubleAndRemove(K var1);

   double getDoubleAndRemove(K var1, double var2);

   Long getTimeMillisAndRemove(K var1);

   long getTimeMillisAndRemove(K var1, long var2);

   boolean contains(K var1);

   boolean contains(K var1, V var2);

   boolean containsObject(K var1, Object var2);

   boolean containsBoolean(K var1, boolean var2);

   boolean containsByte(K var1, byte var2);

   boolean containsChar(K var1, char var2);

   boolean containsShort(K var1, short var2);

   boolean containsInt(K var1, int var2);

   boolean containsLong(K var1, long var2);

   boolean containsFloat(K var1, float var2);

   boolean containsDouble(K var1, double var2);

   boolean containsTimeMillis(K var1, long var2);

   int size();

   boolean isEmpty();

   Set<K> names();

   T add(K var1, V var2);

   T add(K var1, Iterable<? extends V> var2);

   T add(K var1, V... var2);

   T addObject(K var1, Object var2);

   T addObject(K var1, Iterable<?> var2);

   T addObject(K var1, Object... var2);

   T addBoolean(K var1, boolean var2);

   T addByte(K var1, byte var2);

   T addChar(K var1, char var2);

   T addShort(K var1, short var2);

   T addInt(K var1, int var2);

   T addLong(K var1, long var2);

   T addFloat(K var1, float var2);

   T addDouble(K var1, double var2);

   T addTimeMillis(K var1, long var2);

   T add(Headers<? extends K, ? extends V, ?> var1);

   T set(K var1, V var2);

   T set(K var1, Iterable<? extends V> var2);

   T set(K var1, V... var2);

   T setObject(K var1, Object var2);

   T setObject(K var1, Iterable<?> var2);

   T setObject(K var1, Object... var2);

   T setBoolean(K var1, boolean var2);

   T setByte(K var1, byte var2);

   T setChar(K var1, char var2);

   T setShort(K var1, short var2);

   T setInt(K var1, int var2);

   T setLong(K var1, long var2);

   T setFloat(K var1, float var2);

   T setDouble(K var1, double var2);

   T setTimeMillis(K var1, long var2);

   T set(Headers<? extends K, ? extends V, ?> var1);

   T setAll(Headers<? extends K, ? extends V, ?> var1);

   boolean remove(K var1);

   T clear();

   @Override
   Iterator<Entry<K, V>> iterator();
}
