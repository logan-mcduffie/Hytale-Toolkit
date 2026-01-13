package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Size64;
import java.util.Collection;
import java.util.function.Predicate;

public interface BooleanCollection extends Collection<Boolean>, BooleanIterable {
   @Override
   BooleanIterator iterator();

   @Override
   default BooleanSpliterator spliterator() {
      return BooleanSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 320);
   }

   boolean add(boolean var1);

   boolean contains(boolean var1);

   boolean rem(boolean var1);

   @Deprecated
   default boolean add(Boolean key) {
      return this.add(key.booleanValue());
   }

   @Deprecated
   @Override
   default boolean contains(Object key) {
      return key == null ? false : this.contains(((Boolean)key).booleanValue());
   }

   @Deprecated
   @Override
   default boolean remove(Object key) {
      return key == null ? false : this.rem((Boolean)key);
   }

   boolean[] toBooleanArray();

   @Deprecated
   default boolean[] toBooleanArray(boolean[] a) {
      return this.toArray(a);
   }

   boolean[] toArray(boolean[] var1);

   boolean addAll(BooleanCollection var1);

   boolean containsAll(BooleanCollection var1);

   boolean removeAll(BooleanCollection var1);

   @Deprecated
   @Override
   default boolean removeIf(Predicate<? super Boolean> filter) {
      return this.removeIf(filter instanceof BooleanPredicate ? (BooleanPredicate)filter : key -> filter.test(key));
   }

   default boolean removeIf(BooleanPredicate filter) {
      boolean removed = false;
      BooleanIterator each = this.iterator();

      while (each.hasNext()) {
         if (filter.test(each.nextBoolean())) {
            each.remove();
            removed = true;
         }
      }

      return removed;
   }

   boolean retainAll(BooleanCollection var1);
}
