package it.unimi.dsi.fastutil.bytes;

import java.util.Set;

public abstract class AbstractByteSet extends AbstractByteCollection implements Cloneable, ByteSet {
   protected AbstractByteSet() {
   }

   @Override
   public abstract ByteIterator iterator();

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
            return s instanceof ByteSet ? this.containsAll((ByteSet)s) : this.containsAll(s);
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ByteIterator i = this.iterator();

      while (n-- != 0) {
         byte k = i.nextByte();
         h += k;
      }

      return h;
   }

   @Override
   public boolean remove(byte k) {
      return super.rem(k);
   }

   @Deprecated
   @Override
   public boolean rem(byte k) {
      return this.remove(k);
   }
}
