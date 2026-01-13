package io.netty.util.internal;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class InternalThreadLocalMap extends UnpaddedInternalThreadLocalMap {
   private static final ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = new ThreadLocal<>();
   private static final AtomicInteger nextIndex = new AtomicInteger();
   public static final int VARIABLES_TO_REMOVE_INDEX = nextVariableIndex();
   private static final int DEFAULT_ARRAY_LIST_INITIAL_CAPACITY = 8;
   private static final int ARRAY_LIST_CAPACITY_EXPAND_THRESHOLD = 1073741824;
   private static final int ARRAY_LIST_CAPACITY_MAX_SIZE = 2147483639;
   private static final int HANDLER_SHARABLE_CACHE_INITIAL_CAPACITY = 4;
   private static final int INDEXED_VARIABLE_TABLE_INITIAL_SIZE = 32;
   private static final int STRING_BUILDER_INITIAL_SIZE = SystemPropertyUtil.getInt("io.netty.threadLocalMap.stringBuilder.initialSize", 1024);
   private static final int STRING_BUILDER_MAX_SIZE = SystemPropertyUtil.getInt("io.netty.threadLocalMap.stringBuilder.maxSize", 4096);
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(InternalThreadLocalMap.class);
   public static final Object UNSET = new Object();
   private Object[] indexedVariables = newIndexedVariableTable();
   private int futureListenerStackDepth;
   private int localChannelReaderStackDepth;
   private Map<Class<?>, Boolean> handlerSharableCache;
   private Map<Class<?>, TypeParameterMatcher> typeParameterMatcherGetCache;
   private Map<Class<?>, Map<String, TypeParameterMatcher>> typeParameterMatcherFindCache;
   private StringBuilder stringBuilder;
   private Map<Charset, CharsetEncoder> charsetEncoderCache;
   private Map<Charset, CharsetDecoder> charsetDecoderCache;
   private ArrayList<Object> arrayList;
   private BitSet cleanerFlags;
   /** @deprecated */
   public long rp1;
   /** @deprecated */
   public long rp2;
   /** @deprecated */
   public long rp3;
   /** @deprecated */
   public long rp4;
   /** @deprecated */
   public long rp5;
   /** @deprecated */
   public long rp6;
   /** @deprecated */
   public long rp7;
   /** @deprecated */
   public long rp8;

   public static InternalThreadLocalMap getIfSet() {
      Thread thread = Thread.currentThread();
      return thread instanceof FastThreadLocalThread ? ((FastThreadLocalThread)thread).threadLocalMap() : slowThreadLocalMap.get();
   }

   public static InternalThreadLocalMap get() {
      Thread thread = Thread.currentThread();
      return thread instanceof FastThreadLocalThread ? fastGet((FastThreadLocalThread)thread) : slowGet();
   }

   private static InternalThreadLocalMap fastGet(FastThreadLocalThread thread) {
      InternalThreadLocalMap threadLocalMap = thread.threadLocalMap();
      if (threadLocalMap == null) {
         thread.setThreadLocalMap(threadLocalMap = new InternalThreadLocalMap());
      }

      return threadLocalMap;
   }

   private static InternalThreadLocalMap slowGet() {
      InternalThreadLocalMap ret = slowThreadLocalMap.get();
      if (ret == null) {
         ret = new InternalThreadLocalMap();
         slowThreadLocalMap.set(ret);
      }

      return ret;
   }

   public static void remove() {
      Thread thread = Thread.currentThread();
      if (thread instanceof FastThreadLocalThread) {
         ((FastThreadLocalThread)thread).setThreadLocalMap(null);
      } else {
         slowThreadLocalMap.remove();
      }
   }

   public static void destroy() {
      slowThreadLocalMap.remove();
   }

   public static int nextVariableIndex() {
      int index = nextIndex.getAndIncrement();
      if (index < 2147483639 && index >= 0) {
         return index;
      } else {
         nextIndex.set(2147483639);
         throw new IllegalStateException("too many thread-local indexed variables");
      }
   }

   public static int lastVariableIndex() {
      return nextIndex.get() - 1;
   }

   private InternalThreadLocalMap() {
   }

   private static Object[] newIndexedVariableTable() {
      Object[] array = new Object[32];
      Arrays.fill(array, UNSET);
      return array;
   }

   public int size() {
      int count = 0;
      if (this.futureListenerStackDepth != 0) {
         count++;
      }

      if (this.localChannelReaderStackDepth != 0) {
         count++;
      }

      if (this.handlerSharableCache != null) {
         count++;
      }

      if (this.typeParameterMatcherGetCache != null) {
         count++;
      }

      if (this.typeParameterMatcherFindCache != null) {
         count++;
      }

      if (this.stringBuilder != null) {
         count++;
      }

      if (this.charsetEncoderCache != null) {
         count++;
      }

      if (this.charsetDecoderCache != null) {
         count++;
      }

      if (this.arrayList != null) {
         count++;
      }

      Object v = this.indexedVariable(VARIABLES_TO_REMOVE_INDEX);
      if (v != null && v != UNSET) {
         Set<FastThreadLocal<?>> variablesToRemove = (Set<FastThreadLocal<?>>)v;
         count += variablesToRemove.size();
      }

      return count;
   }

   public StringBuilder stringBuilder() {
      StringBuilder sb = this.stringBuilder;
      if (sb == null) {
         return this.stringBuilder = new StringBuilder(STRING_BUILDER_INITIAL_SIZE);
      } else {
         if (sb.capacity() > STRING_BUILDER_MAX_SIZE) {
            sb.setLength(STRING_BUILDER_INITIAL_SIZE);
            sb.trimToSize();
         }

         sb.setLength(0);
         return sb;
      }
   }

   public Map<Charset, CharsetEncoder> charsetEncoderCache() {
      Map<Charset, CharsetEncoder> cache = this.charsetEncoderCache;
      if (cache == null) {
         this.charsetEncoderCache = cache = new IdentityHashMap<>();
      }

      return cache;
   }

   public Map<Charset, CharsetDecoder> charsetDecoderCache() {
      Map<Charset, CharsetDecoder> cache = this.charsetDecoderCache;
      if (cache == null) {
         this.charsetDecoderCache = cache = new IdentityHashMap<>();
      }

      return cache;
   }

   public <E> ArrayList<E> arrayList() {
      return this.arrayList(8);
   }

   public <E> ArrayList<E> arrayList(int minCapacity) {
      ArrayList<E> list = (ArrayList<E>)this.arrayList;
      if (list == null) {
         this.arrayList = new ArrayList<>(minCapacity);
         return (ArrayList<E>)this.arrayList;
      } else {
         list.clear();
         list.ensureCapacity(minCapacity);
         return list;
      }
   }

   public int futureListenerStackDepth() {
      return this.futureListenerStackDepth;
   }

   public void setFutureListenerStackDepth(int futureListenerStackDepth) {
      this.futureListenerStackDepth = futureListenerStackDepth;
   }

   @Deprecated
   public ThreadLocalRandom random() {
      return new ThreadLocalRandom();
   }

   public Map<Class<?>, TypeParameterMatcher> typeParameterMatcherGetCache() {
      Map<Class<?>, TypeParameterMatcher> cache = this.typeParameterMatcherGetCache;
      if (cache == null) {
         this.typeParameterMatcherGetCache = cache = new IdentityHashMap<>();
      }

      return cache;
   }

   public Map<Class<?>, Map<String, TypeParameterMatcher>> typeParameterMatcherFindCache() {
      Map<Class<?>, Map<String, TypeParameterMatcher>> cache = this.typeParameterMatcherFindCache;
      if (cache == null) {
         this.typeParameterMatcherFindCache = cache = new IdentityHashMap<>();
      }

      return cache;
   }

   @Deprecated
   public IntegerHolder counterHashCode() {
      return new IntegerHolder();
   }

   @Deprecated
   public void setCounterHashCode(IntegerHolder counterHashCode) {
   }

   public Map<Class<?>, Boolean> handlerSharableCache() {
      Map<Class<?>, Boolean> cache = this.handlerSharableCache;
      if (cache == null) {
         this.handlerSharableCache = cache = new WeakHashMap<>(4);
      }

      return cache;
   }

   public int localChannelReaderStackDepth() {
      return this.localChannelReaderStackDepth;
   }

   public void setLocalChannelReaderStackDepth(int localChannelReaderStackDepth) {
      this.localChannelReaderStackDepth = localChannelReaderStackDepth;
   }

   public Object indexedVariable(int index) {
      Object[] lookup = this.indexedVariables;
      return index < lookup.length ? lookup[index] : UNSET;
   }

   public boolean setIndexedVariable(int index, Object value) {
      return this.getAndSetIndexedVariable(index, value) == UNSET;
   }

   public Object getAndSetIndexedVariable(int index, Object value) {
      Object[] lookup = this.indexedVariables;
      if (index < lookup.length) {
         Object oldValue = lookup[index];
         lookup[index] = value;
         return oldValue;
      } else {
         this.expandIndexedVariableTableAndSet(index, value);
         return UNSET;
      }
   }

   private void expandIndexedVariableTableAndSet(int index, Object value) {
      Object[] oldArray = this.indexedVariables;
      int oldCapacity = oldArray.length;
      int newCapacity;
      if (index < 1073741824) {
         newCapacity = index | index >>> 1;
         newCapacity |= newCapacity >>> 2;
         newCapacity |= newCapacity >>> 4;
         newCapacity |= newCapacity >>> 8;
         newCapacity |= newCapacity >>> 16;
         newCapacity++;
      } else {
         newCapacity = 2147483639;
      }

      Object[] newArray = Arrays.copyOf(oldArray, newCapacity);
      Arrays.fill(newArray, oldCapacity, newArray.length, UNSET);
      newArray[index] = value;
      this.indexedVariables = newArray;
   }

   public Object removeIndexedVariable(int index) {
      Object[] lookup = this.indexedVariables;
      if (index < lookup.length) {
         Object v = lookup[index];
         lookup[index] = UNSET;
         return v;
      } else {
         return UNSET;
      }
   }

   public boolean isIndexedVariableSet(int index) {
      Object[] lookup = this.indexedVariables;
      return index < lookup.length && lookup[index] != UNSET;
   }

   @Deprecated
   public boolean isCleanerFlagSet(int index) {
      return this.cleanerFlags != null && this.cleanerFlags.get(index);
   }

   @Deprecated
   public void setCleanerFlag(int index) {
      if (this.cleanerFlags == null) {
         this.cleanerFlags = new BitSet();
      }

      this.cleanerFlags.set(index);
   }

   static {
      logger.debug("-Dio.netty.threadLocalMap.stringBuilder.initialSize: {}", STRING_BUILDER_INITIAL_SIZE);
      logger.debug("-Dio.netty.threadLocalMap.stringBuilder.maxSize: {}", STRING_BUILDER_MAX_SIZE);
   }
}
