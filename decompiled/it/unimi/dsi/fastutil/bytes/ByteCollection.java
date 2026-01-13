package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import java.util.Collection;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface ByteCollection extends Collection<Byte>, ByteIterable {
   @Override
   ByteIterator iterator();

   @Override
   default IntIterator intIterator() {
      return ByteIterable.super.intIterator();
   }

   @Override
   default ByteSpliterator spliterator() {
      return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 320);
   }

   @Override
   default IntSpliterator intSpliterator() {
      return ByteIterable.super.intSpliterator();
   }

   boolean add(byte var1);

   boolean contains(byte var1);

   boolean rem(byte var1);

   @Deprecated
   default boolean add(Byte key) {
      return this.add(key.byteValue());
   }

   @Deprecated
   @Override
   default boolean contains(Object key) {
      return key == null ? false : this.contains(((Byte)key).byteValue());
   }

   @Deprecated
   @Override
   default boolean remove(Object key) {
      return key == null ? false : this.rem((Byte)key);
   }

   byte[] toByteArray();

   @Deprecated
   default byte[] toByteArray(byte[] a) {
      return this.toArray(a);
   }

   byte[] toArray(byte[] var1);

   boolean addAll(ByteCollection var1);

   boolean containsAll(ByteCollection var1);

   boolean removeAll(ByteCollection var1);

   @Deprecated
   @Override
   default boolean removeIf(Predicate<? super Byte> filter) {
      return this.removeIf(filter instanceof BytePredicate ? (BytePredicate)filter : key -> filter.test(SafeMath.safeIntToByte(key)));
   }

   default boolean removeIf(BytePredicate filter) {
      boolean removed = false;
      ByteIterator each = this.iterator();

      while (each.hasNext()) {
         if (filter.test(each.nextByte())) {
            each.remove();
            removed = true;
         }
      }

      return removed;
   }

   default boolean removeIf(IntPredicate filter) {
      return this.removeIf(filter instanceof BytePredicate ? (BytePredicate)filter : filter::test);
   }

   boolean retainAll(ByteCollection var1);

   @Deprecated
   @Override
   default Stream<Byte> stream() {
      return Collection.super.stream();
   }

   default IntStream intStream() {
      return StreamSupport.intStream(this.intSpliterator(), false);
   }

   @Deprecated
   @Override
   default Stream<Byte> parallelStream() {
      return Collection.super.parallelStream();
   }

   default IntStream intParallelStream() {
      return StreamSupport.intStream(this.intSpliterator(), true);
   }
}
