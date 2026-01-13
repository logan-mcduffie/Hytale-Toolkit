package it.unimi.dsi.fastutil.shorts;

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

public interface ShortCollection extends Collection<Short>, ShortIterable {
   @Override
   ShortIterator iterator();

   @Override
   default IntIterator intIterator() {
      return ShortIterable.super.intIterator();
   }

   @Override
   default ShortSpliterator spliterator() {
      return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 320);
   }

   @Override
   default IntSpliterator intSpliterator() {
      return ShortIterable.super.intSpliterator();
   }

   boolean add(short var1);

   boolean contains(short var1);

   boolean rem(short var1);

   @Deprecated
   default boolean add(Short key) {
      return this.add(key.shortValue());
   }

   @Deprecated
   @Override
   default boolean contains(Object key) {
      return key == null ? false : this.contains(((Short)key).shortValue());
   }

   @Deprecated
   @Override
   default boolean remove(Object key) {
      return key == null ? false : this.rem((Short)key);
   }

   short[] toShortArray();

   @Deprecated
   default short[] toShortArray(short[] a) {
      return this.toArray(a);
   }

   short[] toArray(short[] var1);

   boolean addAll(ShortCollection var1);

   boolean containsAll(ShortCollection var1);

   boolean removeAll(ShortCollection var1);

   @Deprecated
   @Override
   default boolean removeIf(Predicate<? super Short> filter) {
      return this.removeIf(filter instanceof ShortPredicate ? (ShortPredicate)filter : key -> filter.test(SafeMath.safeIntToShort(key)));
   }

   default boolean removeIf(ShortPredicate filter) {
      boolean removed = false;
      ShortIterator each = this.iterator();

      while (each.hasNext()) {
         if (filter.test(each.nextShort())) {
            each.remove();
            removed = true;
         }
      }

      return removed;
   }

   default boolean removeIf(IntPredicate filter) {
      return this.removeIf(filter instanceof ShortPredicate ? (ShortPredicate)filter : filter::test);
   }

   boolean retainAll(ShortCollection var1);

   @Deprecated
   @Override
   default Stream<Short> stream() {
      return Collection.super.stream();
   }

   default IntStream intStream() {
      return StreamSupport.intStream(this.intSpliterator(), false);
   }

   @Deprecated
   @Override
   default Stream<Short> parallelStream() {
      return Collection.super.parallelStream();
   }

   default IntStream intParallelStream() {
      return StreamSupport.intStream(this.intSpliterator(), true);
   }
}
