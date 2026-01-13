package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;
import java.util.Comparator;

public final class ShortComparators {
   public static final ShortComparator NATURAL_COMPARATOR = new ShortComparators.NaturalImplicitComparator();
   public static final ShortComparator OPPOSITE_COMPARATOR = new ShortComparators.OppositeImplicitComparator();

   private ShortComparators() {
   }

   public static ShortComparator oppositeComparator(ShortComparator c) {
      return (ShortComparator)(c instanceof ShortComparators.OppositeComparator
         ? ((ShortComparators.OppositeComparator)c).comparator
         : new ShortComparators.OppositeComparator(c));
   }

   public static ShortComparator asShortComparator(final Comparator<? super Short> c) {
      return c != null && !(c instanceof ShortComparator) ? new ShortComparator() {
         @Override
         public int compare(short x, short y) {
            return c.compare(x, y);
         }

         @Override
         public int compare(Short x, Short y) {
            return c.compare(x, y);
         }
      } : (ShortComparator)c;
   }

   protected static class NaturalImplicitComparator implements ShortComparator, Serializable {
      private static final long serialVersionUID = 1L;

      @Override
      public final int compare(short a, short b) {
         return Short.compare(a, b);
      }

      @Override
      public ShortComparator reversed() {
         return ShortComparators.OPPOSITE_COMPARATOR;
      }

      private Object readResolve() {
         return ShortComparators.NATURAL_COMPARATOR;
      }
   }

   protected static class OppositeComparator implements ShortComparator, Serializable {
      private static final long serialVersionUID = 1L;
      final ShortComparator comparator;

      protected OppositeComparator(ShortComparator c) {
         this.comparator = c;
      }

      @Override
      public final int compare(short a, short b) {
         return this.comparator.compare(b, a);
      }

      @Override
      public final ShortComparator reversed() {
         return this.comparator;
      }
   }

   protected static class OppositeImplicitComparator implements ShortComparator, Serializable {
      private static final long serialVersionUID = 1L;

      @Override
      public final int compare(short a, short b) {
         return -Short.compare(a, b);
      }

      @Override
      public ShortComparator reversed() {
         return ShortComparators.NATURAL_COMPARATOR;
      }

      private Object readResolve() {
         return ShortComparators.OPPOSITE_COMPARATOR;
      }
   }
}
