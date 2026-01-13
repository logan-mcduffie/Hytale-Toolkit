package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Double2ByteSortedMap extends Double2ByteMap, SortedMap<Double, Byte> {
   Double2ByteSortedMap subMap(double var1, double var3);

   Double2ByteSortedMap headMap(double var1);

   Double2ByteSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   @Deprecated
   default Double2ByteSortedMap subMap(Double from, Double to) {
      return this.subMap(from.doubleValue(), to.doubleValue());
   }

   @Deprecated
   default Double2ByteSortedMap headMap(Double to) {
      return this.headMap(to.doubleValue());
   }

   @Deprecated
   default Double2ByteSortedMap tailMap(Double from) {
      return this.tailMap(from.doubleValue());
   }

   @Deprecated
   default Double firstKey() {
      return this.firstDoubleKey();
   }

   @Deprecated
   default Double lastKey() {
      return this.lastDoubleKey();
   }

   @Deprecated
   default ObjectSortedSet<Entry<Double, Byte>> entrySet() {
      return this.double2ByteEntrySet();
   }

   ObjectSortedSet<Double2ByteMap.Entry> double2ByteEntrySet();

   DoubleSortedSet keySet();

   @Override
   ByteCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2ByteMap.Entry>, Double2ByteMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2ByteMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2ByteMap.Entry> fastIterator(Double2ByteMap.Entry var1);
   }
}
