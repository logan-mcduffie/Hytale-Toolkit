package it.unimi.dsi.fastutil.chars;

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

public interface CharCollection extends Collection<Character>, CharIterable {
   @Override
   CharIterator iterator();

   @Override
   default IntIterator intIterator() {
      return CharIterable.super.intIterator();
   }

   @Override
   default CharSpliterator spliterator() {
      return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 320);
   }

   @Override
   default IntSpliterator intSpliterator() {
      return CharIterable.super.intSpliterator();
   }

   boolean add(char var1);

   boolean contains(char var1);

   boolean rem(char var1);

   @Deprecated
   default boolean add(Character key) {
      return this.add(key.charValue());
   }

   @Deprecated
   @Override
   default boolean contains(Object key) {
      return key == null ? false : this.contains(((Character)key).charValue());
   }

   @Deprecated
   @Override
   default boolean remove(Object key) {
      return key == null ? false : this.rem((Character)key);
   }

   char[] toCharArray();

   @Deprecated
   default char[] toCharArray(char[] a) {
      return this.toArray(a);
   }

   char[] toArray(char[] var1);

   boolean addAll(CharCollection var1);

   boolean containsAll(CharCollection var1);

   boolean removeAll(CharCollection var1);

   @Deprecated
   @Override
   default boolean removeIf(Predicate<? super Character> filter) {
      return this.removeIf(filter instanceof CharPredicate ? (CharPredicate)filter : key -> filter.test(SafeMath.safeIntToChar(key)));
   }

   default boolean removeIf(CharPredicate filter) {
      boolean removed = false;
      CharIterator each = this.iterator();

      while (each.hasNext()) {
         if (filter.test(each.nextChar())) {
            each.remove();
            removed = true;
         }
      }

      return removed;
   }

   default boolean removeIf(IntPredicate filter) {
      return this.removeIf(filter instanceof CharPredicate ? (CharPredicate)filter : filter::test);
   }

   boolean retainAll(CharCollection var1);

   @Deprecated
   @Override
   default Stream<Character> stream() {
      return Collection.super.stream();
   }

   default IntStream intStream() {
      return StreamSupport.intStream(this.intSpliterator(), false);
   }

   @Deprecated
   @Override
   default Stream<Character> parallelStream() {
      return Collection.super.parallelStream();
   }

   default IntStream intParallelStream() {
      return StreamSupport.intStream(this.intSpliterator(), true);
   }
}
