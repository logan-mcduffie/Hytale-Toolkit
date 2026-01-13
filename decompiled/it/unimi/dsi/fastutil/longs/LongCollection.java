package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Size64;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface LongCollection extends Collection<Long>, LongIterable {
   @Override
   LongIterator iterator();

   @Override
   default LongIterator longIterator() {
      return this.iterator();
   }

   @Override
   default LongSpliterator spliterator() {
      return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 320);
   }

   @Override
   default LongSpliterator longSpliterator() {
      return this.spliterator();
   }

   boolean add(long var1);

   boolean contains(long var1);

   boolean rem(long var1);

   @Deprecated
   default boolean add(Long key) {
      return this.add(key.longValue());
   }

   @Deprecated
   @Override
   default boolean contains(Object key) {
      return key == null ? false : this.contains(((Long)key).longValue());
   }

   @Deprecated
   @Override
   default boolean remove(Object key) {
      return key == null ? false : this.rem((Long)key);
   }

   long[] toLongArray();

   @Deprecated
   default long[] toLongArray(long[] a) {
      return this.toArray(a);
   }

   long[] toArray(long[] var1);

   boolean addAll(LongCollection var1);

   boolean containsAll(LongCollection var1);

   boolean removeAll(LongCollection var1);

   @Deprecated
   @Override
   default boolean removeIf(Predicate<? super Long> filter) {
      return this.removeIf(filter instanceof java.util.function.LongPredicate ? (java.util.function.LongPredicate)filter : key -> filter.test(key));
   }

   default boolean removeIf(java.util.function.LongPredicate filter) {
      boolean removed = false;
      LongIterator each = this.iterator();

      while (each.hasNext()) {
         if (filter.test(each.nextLong())) {
            each.remove();
            removed = true;
         }
      }

      return removed;
   }

   default boolean removeIf(LongPredicate filter) {
      return this.removeIf(filter);
   }

   boolean retainAll(LongCollection var1);

   @Deprecated
   @Override
   default Stream<Long> stream() {
      return Collection.super.stream();
   }

   default LongStream longStream() {
      return StreamSupport.longStream(this.longSpliterator(), false);
   }

   @Deprecated
   @Override
   default Stream<Long> parallelStream() {
      return Collection.super.parallelStream();
   }

   default LongStream longParallelStream() {
      return StreamSupport.longStream(this.longSpliterator(), true);
   }
}
