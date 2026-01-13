package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import java.util.Set;

public abstract class AbstractLongSet extends AbstractLongCollection implements Cloneable, LongSet {
   protected AbstractLongSet() {
   }

   @Override
   public abstract LongIterator iterator();

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Set)) {
         return false;
      } else {
         Set<?> s = (Set<?>)o;
         if (s.size() != this.size()) {
            return false;
         } else {
            return s instanceof LongSet ? this.containsAll((LongSet)s) : this.containsAll(s);
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      LongIterator i = this.iterator();

      while (n-- != 0) {
         long k = i.nextLong();
         h += HashCommon.long2int(k);
      }

      return h;
   }

   @Override
   public boolean remove(long k) {
      return super.rem(k);
   }

   @Deprecated
   @Override
   public boolean rem(long k) {
      return this.remove(k);
   }
}
