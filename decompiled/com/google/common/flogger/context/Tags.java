package com.google.common.flogger.context;

import com.google.common.flogger.util.Checks;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public final class Tags {
   private static final Comparator<Object> VALUE_COMPARATOR = new Comparator<Object>() {
      @Override
      public int compare(Object lhs, Object rhs) {
         Tags.Type ltype = Tags.Type.of(lhs);
         Tags.Type rtype = Tags.Type.of(rhs);
         return ltype == rtype ? ltype.compare(lhs, rhs) : ltype.compareTo(rtype);
      }
   };
   private static final Comparator<Tags.KeyValuePair> KEY_VALUE_COMPARATOR = new Comparator<Tags.KeyValuePair>() {
      public int compare(Tags.KeyValuePair lhs, Tags.KeyValuePair rhs) {
         int signum = lhs.key.compareTo(rhs.key);
         if (signum == 0) {
            if (lhs.value != null) {
               signum = rhs.value != null ? Tags.VALUE_COMPARATOR.compare(lhs.value, rhs.value) : 1;
            } else {
               signum = rhs.value != null ? -1 : 0;
            }
         }

         return signum;
      }
   };
   private static final Tags EMPTY_TAGS = new Tags(new Tags.LightweightTagMap(Collections.emptyList()));
   private final Tags.LightweightTagMap map;

   public static Tags.Builder builder() {
      return new Tags.Builder();
   }

   public static Tags empty() {
      return EMPTY_TAGS;
   }

   public static Tags of(String name, String value) {
      return new Tags(name, value);
   }

   public static Tags of(String name, boolean value) {
      return new Tags(name, value);
   }

   public static Tags of(String name, long value) {
      return new Tags(name, value);
   }

   public static Tags of(String name, double value) {
      return new Tags(name, value);
   }

   private Tags(String name, Object value) {
      this(new Tags.LightweightTagMap(Checks.checkMetadataIdentifier(name), Checks.checkNotNull(value, "value")));
   }

   private Tags(Tags.LightweightTagMap map) {
      this.map = map;
   }

   public Map<String, Set<Object>> asMap() {
      return this.map;
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public Tags merge(Tags other) {
      if (other.isEmpty()) {
         return this;
      } else {
         return this.isEmpty() ? other : new Tags(new Tags.LightweightTagMap(this.map, other.map));
      }
   }

   @Override
   public boolean equals(@NullableDecl Object obj) {
      return obj instanceof Tags && ((Tags)obj).map.equals(this.map);
   }

   @Override
   public int hashCode() {
      return ~this.map.hashCode();
   }

   @Override
   public String toString() {
      return this.map.toString();
   }

   public static final class Builder {
      private final List<Tags.KeyValuePair> keyValuePairs = new ArrayList<>();

      @CanIgnoreReturnValue
      public Tags.Builder addTag(String name) {
         return this.addImpl(name, null);
      }

      @CanIgnoreReturnValue
      public Tags.Builder addTag(String name, String value) {
         Checks.checkArgument(value != null, "tag value");
         return this.addImpl(name, value);
      }

      @CanIgnoreReturnValue
      public Tags.Builder addTag(String name, boolean value) {
         return this.addImpl(name, value);
      }

      @CanIgnoreReturnValue
      public Tags.Builder addTag(String name, long value) {
         return this.addImpl(name, value);
      }

      @CanIgnoreReturnValue
      public Tags.Builder addTag(String name, double value) {
         return this.addImpl(name, value);
      }

      private Tags.Builder addImpl(String name, @NullableDecl Object value) {
         this.keyValuePairs.add(new Tags.KeyValuePair(Checks.checkMetadataIdentifier(name), value));
         return this;
      }

      public Tags build() {
         if (this.keyValuePairs.isEmpty()) {
            return Tags.EMPTY_TAGS;
         } else {
            Collections.sort(this.keyValuePairs, Tags.KEY_VALUE_COMPARATOR);
            return new Tags(new Tags.LightweightTagMap(this.keyValuePairs));
         }
      }

      @Override
      public String toString() {
         return this.build().toString();
      }
   }

   private static final class KeyValuePair {
      private final String key;
      @NullableDecl
      private final Object value;

      private KeyValuePair(String key, @NullableDecl Object value) {
         this.key = key;
         this.value = value;
      }
   }

   private static class LightweightTagMap extends AbstractMap<String, Set<Object>> {
      private static final Comparator<Object> ENTRY_COMPARATOR = new Comparator<Object>() {
         @Override
         public int compare(Object s1, Object s2) {
            return ((String)((Entry)s1).getKey()).compareTo((String)((Entry)s2).getKey());
         }
      };
      private static final int SMALL_ARRAY_LENGTH = 16;
      private static final int[] singletonOffsets = new int[]{1, 2};
      private final Object[] array;
      private final int[] offsets;
      private final Set<Entry<String, Set<Object>>> entrySet = new Tags.LightweightTagMap.SortedArraySet<>(-1);
      private Integer hashCode = null;
      private String toString = null;

      LightweightTagMap(String name, Object value) {
         this.offsets = singletonOffsets;
         this.array = new Object[]{this.newEntry(name, 0), value};
      }

      LightweightTagMap(List<Tags.KeyValuePair> sortedPairs) {
         int entryCount = countMapEntries(sortedPairs);
         Object[] array = new Object[entryCount + sortedPairs.size()];
         int[] offsets = new int[entryCount + 1];
         int totalElementCount = this.makeTagMap(sortedPairs, entryCount, array, offsets);
         this.array = maybeResizeElementArray(array, totalElementCount);
         this.offsets = offsets;
      }

      LightweightTagMap(Tags.LightweightTagMap lhs, Tags.LightweightTagMap rhs) {
         int maxEntryCount = lhs.size() + rhs.size();
         Object[] array = new Object[lhs.getTotalElementCount() + rhs.getTotalElementCount()];
         int[] offsets = new int[maxEntryCount + 1];
         int totalElementCount = this.mergeTagMaps(lhs, rhs, maxEntryCount, array, offsets);
         this.array = adjustOffsetsAndMaybeResize(array, offsets, totalElementCount);
         this.offsets = maybeResizeOffsetsArray(offsets);
      }

      private static int countMapEntries(List<Tags.KeyValuePair> sortedPairs) {
         String key = null;
         int count = 0;

         for (Tags.KeyValuePair pair : sortedPairs) {
            if (!pair.key.equals(key)) {
               key = pair.key;
               count++;
            }
         }

         return count;
      }

      private int makeTagMap(List<Tags.KeyValuePair> sortedPairs, int entryCount, Object[] array, int[] offsets) {
         String key = null;
         Object value = null;
         int newEntryIndex = 0;
         int valueStart = entryCount;

         for (Tags.KeyValuePair pair : sortedPairs) {
            if (!pair.key.equals(key)) {
               key = pair.key;
               array[newEntryIndex] = this.newEntry(key, newEntryIndex);
               offsets[newEntryIndex] = valueStart;
               newEntryIndex++;
               value = null;
            }

            if (pair.value != null && !pair.value.equals(value)) {
               value = pair.value;
               array[valueStart++] = value;
            }
         }

         if (newEntryIndex != entryCount) {
            throw new ConcurrentModificationException("corrupted tag map");
         } else {
            offsets[entryCount] = valueStart;
            return valueStart;
         }
      }

      private int mergeTagMaps(Tags.LightweightTagMap lhs, Tags.LightweightTagMap rhs, int maxEntryCount, Object[] array, int[] offsets) {
         int valueStart = maxEntryCount;
         offsets[0] = maxEntryCount;
         int lhsEntryIndex = 0;
         Entry<String, Tags.LightweightTagMap.SortedArraySet<Object>> lhsEntry = lhs.getEntryOrNull(lhsEntryIndex);
         int rhsEntryIndex = 0;
         Entry<String, Tags.LightweightTagMap.SortedArraySet<Object>> rhsEntry = rhs.getEntryOrNull(rhsEntryIndex);
         int newEntryIndex = 0;

         while (lhsEntry != null || rhsEntry != null) {
            int signum = lhsEntry == null ? 1 : (rhsEntry == null ? -1 : 0);
            if (signum == 0) {
               signum = lhsEntry.getKey().compareTo(rhsEntry.getKey());
               if (signum == 0) {
                  array[newEntryIndex] = this.newEntry(lhsEntry.getKey(), newEntryIndex);
                  newEntryIndex++;
                  valueStart = mergeValues(lhsEntry.getValue(), rhsEntry.getValue(), array, valueStart);
                  offsets[newEntryIndex] = valueStart;
                  lhsEntry = lhs.getEntryOrNull(++lhsEntryIndex);
                  rhsEntry = rhs.getEntryOrNull(++rhsEntryIndex);
                  continue;
               }
            }

            if (signum < 0) {
               valueStart = this.copyEntryAndValues(lhsEntry, newEntryIndex++, valueStart, array, offsets);
               lhsEntry = lhs.getEntryOrNull(++lhsEntryIndex);
            } else {
               valueStart = this.copyEntryAndValues(rhsEntry, newEntryIndex++, valueStart, array, offsets);
               rhsEntry = rhs.getEntryOrNull(++rhsEntryIndex);
            }
         }

         return newEntryIndex;
      }

      private static int mergeValues(Tags.LightweightTagMap.SortedArraySet<?> lhs, Tags.LightweightTagMap.SortedArraySet<?> rhs, Object[] array, int valueStart) {
         int lhsIndex = 0;
         int rhsIndex = 0;

         while (lhsIndex < lhs.size() || rhsIndex < rhs.size()) {
            int signum = lhsIndex == lhs.size() ? 1 : (rhsIndex == rhs.size() ? -1 : 0);
            if (signum == 0) {
               signum = Tags.VALUE_COMPARATOR.compare(lhs.getValue(lhsIndex), rhs.getValue(rhsIndex));
            }

            Object value;
            if (signum < 0) {
               value = lhs.getValue(lhsIndex++);
            } else {
               value = rhs.getValue(rhsIndex++);
               if (signum == 0) {
                  lhsIndex++;
               }
            }

            array[valueStart++] = value;
         }

         return valueStart;
      }

      private int copyEntryAndValues(
         Entry<String, Tags.LightweightTagMap.SortedArraySet<Object>> entry, int entryIndex, int valueStart, Object[] array, int[] offsets
      ) {
         Tags.LightweightTagMap.SortedArraySet<Object> values = entry.getValue();
         int valueCount = values.getEnd() - values.getStart();
         System.arraycopy(values.getValuesArray(), values.getStart(), array, valueStart, valueCount);
         array[entryIndex] = this.newEntry(entry.getKey(), entryIndex);
         int valueEnd = valueStart + valueCount;
         offsets[entryIndex + 1] = valueEnd;
         return valueEnd;
      }

      private static Object[] adjustOffsetsAndMaybeResize(Object[] array, int[] offsets, int entryCount) {
         int maxEntries = offsets[0];
         int offsetReduction = maxEntries - entryCount;
         if (offsetReduction == 0) {
            return array;
         } else {
            for (int i = 0; i <= entryCount; i++) {
               offsets[i] -= offsetReduction;
            }

            Object[] dstArray = array;
            int totalElementCount = offsets[entryCount];
            int valueCount = totalElementCount - entryCount;
            if (mustResize(array.length, totalElementCount)) {
               dstArray = new Object[totalElementCount];
               System.arraycopy(array, 0, dstArray, 0, entryCount);
            }

            System.arraycopy(array, maxEntries, dstArray, entryCount, valueCount);
            return dstArray;
         }
      }

      private static Object[] maybeResizeElementArray(Object[] array, int bestLength) {
         return mustResize(array.length, bestLength) ? Arrays.copyOf(array, bestLength) : array;
      }

      private static int[] maybeResizeOffsetsArray(int[] offsets) {
         int bestLength = offsets[0] + 1;
         return mustResize(offsets.length, bestLength) ? Arrays.copyOf(offsets, bestLength) : offsets;
      }

      private static boolean mustResize(int actualLength, int bestLength) {
         return actualLength > 16 && 9 * actualLength > 10 * bestLength;
      }

      private Entry<String, Tags.LightweightTagMap.SortedArraySet<Object>> newEntry(String key, int index) {
         return new SimpleImmutableEntry<>(key, new Tags.LightweightTagMap.SortedArraySet<>(index));
      }

      private Entry<String, Tags.LightweightTagMap.SortedArraySet<Object>> getEntryOrNull(int index) {
         return index < this.offsets[0] ? (Entry)this.array[index] : null;
      }

      private int getTotalElementCount() {
         return this.offsets[this.size()];
      }

      @Override
      public Set<Entry<String, Set<Object>>> entrySet() {
         return this.entrySet;
      }

      @Override
      public int hashCode() {
         if (this.hashCode == null) {
            this.hashCode = super.hashCode();
         }

         return this.hashCode;
      }

      @Override
      public String toString() {
         if (this.toString == null) {
            this.toString = super.toString();
         }

         return this.toString;
      }

      class SortedArraySet<T> extends AbstractSet<T> {
         final int index;

         SortedArraySet(int index) {
            this.index = index;
         }

         Object[] getValuesArray() {
            return LightweightTagMap.this.array;
         }

         Object getValue(int n) {
            return LightweightTagMap.this.array[this.getStart() + n];
         }

         int getStart() {
            return this.index == -1 ? 0 : LightweightTagMap.this.offsets[this.index];
         }

         int getEnd() {
            return LightweightTagMap.this.offsets[this.index + 1];
         }

         private Comparator<Object> getComparator() {
            return this.index == -1 ? Tags.LightweightTagMap.ENTRY_COMPARATOR : Tags.VALUE_COMPARATOR;
         }

         @Override
         public int size() {
            return this.getEnd() - this.getStart();
         }

         @Override
         public boolean contains(Object o) {
            return Arrays.binarySearch(LightweightTagMap.this.array, this.getStart(), this.getEnd(), o, this.getComparator()) >= 0;
         }

         @Override
         public Iterator<T> iterator() {
            return new Iterator<T>() {
               private int n = 0;

               @Override
               public boolean hasNext() {
                  return this.n < SortedArraySet.this.size();
               }

               @Override
               public T next() {
                  int index = this.n;
                  if (index < SortedArraySet.this.size()) {
                     T value = (T)LightweightTagMap.this.array[SortedArraySet.this.getStart() + index];
                     this.n = index + 1;
                     return value;
                  } else {
                     throw new NoSuchElementException();
                  }
               }
            };
         }
      }
   }

   private static enum Type {
      BOOLEAN {
         @Override
         int compare(Object lhs, Object rhs) {
            return ((Boolean)lhs).compareTo((Boolean)rhs);
         }
      },
      STRING {
         @Override
         int compare(Object lhs, Object rhs) {
            return ((String)lhs).compareTo((String)rhs);
         }
      },
      LONG {
         @Override
         int compare(Object lhs, Object rhs) {
            return ((Long)lhs).compareTo((Long)rhs);
         }
      },
      DOUBLE {
         @Override
         int compare(Object lhs, Object rhs) {
            return ((Double)lhs).compareTo((Double)rhs);
         }
      };

      private Type() {
      }

      abstract int compare(Object var1, Object var2);

      private static Tags.Type of(Object tag) {
         if (tag instanceof String) {
            return STRING;
         } else if (tag instanceof Boolean) {
            return BOOLEAN;
         } else if (tag instanceof Long) {
            return LONG;
         } else if (tag instanceof Double) {
            return DOUBLE;
         } else {
            throw new AssertionError("invalid tag type: " + tag.getClass());
         }
      }
   }
}
