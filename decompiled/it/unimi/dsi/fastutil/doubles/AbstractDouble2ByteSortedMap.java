package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractDouble2ByteSortedMap extends AbstractDouble2ByteMap implements Double2ByteSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2ByteSortedMap() {
   }

   @Override
   public DoubleSortedSet keySet() {
      return new AbstractDouble2ByteSortedMap.KeySet();
   }

   @Override
   public ByteCollection values() {
      return new AbstractDouble2ByteSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractDoubleSortedSet {
      @Override
      public boolean contains(double k) {
         return AbstractDouble2ByteSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractDouble2ByteSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractDouble2ByteSortedMap.this.clear();
      }

      @Override
      public DoubleComparator comparator() {
         return AbstractDouble2ByteSortedMap.this.comparator();
      }

      @Override
      public double firstDouble() {
         return AbstractDouble2ByteSortedMap.this.firstDoubleKey();
      }

      @Override
      public double lastDouble() {
         return AbstractDouble2ByteSortedMap.this.lastDoubleKey();
      }

      @Override
      public DoubleSortedSet headSet(double to) {
         return AbstractDouble2ByteSortedMap.this.headMap(to).keySet();
      }

      @Override
      public DoubleSortedSet tailSet(double from) {
         return AbstractDouble2ByteSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public DoubleSortedSet subSet(double from, double to) {
         return AbstractDouble2ByteSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public DoubleBidirectionalIterator iterator(double from) {
         return new AbstractDouble2ByteSortedMap.KeySetIterator(
            AbstractDouble2ByteSortedMap.this.double2ByteEntrySet().iterator(new AbstractDouble2ByteMap.BasicEntry(from, (byte)0))
         );
      }

      @Override
      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2ByteSortedMap.KeySetIterator(Double2ByteSortedMaps.fastIterator(AbstractDouble2ByteSortedMap.this));
      }
   }

   protected static class KeySetIterator implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2ByteMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2ByteMap.Entry> i) {
         this.i = i;
      }

      @Override
      public double nextDouble() {
         return this.i.next().getDoubleKey();
      }

      @Override
      public double previousDouble() {
         return this.i.previous().getDoubleKey();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }

      @Override
      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class ValuesCollection extends AbstractByteCollection {
      @Override
      public ByteIterator iterator() {
         return new AbstractDouble2ByteSortedMap.ValuesIterator(Double2ByteSortedMaps.fastIterator(AbstractDouble2ByteSortedMap.this));
      }

      @Override
      public boolean contains(byte k) {
         return AbstractDouble2ByteSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractDouble2ByteSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractDouble2ByteSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements ByteIterator {
      protected final ObjectBidirectionalIterator<Double2ByteMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2ByteMap.Entry> i) {
         this.i = i;
      }

      @Override
      public byte nextByte() {
         return this.i.next().getByteValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
