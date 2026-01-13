package it.unimi.dsi.fastutil.floats;

import java.io.Serializable;
import java.util.Comparator;

public final class FloatComparators {
   public static final FloatComparator NATURAL_COMPARATOR = new FloatComparators.NaturalImplicitComparator();
   public static final FloatComparator OPPOSITE_COMPARATOR = new FloatComparators.OppositeImplicitComparator();

   private FloatComparators() {
   }

   public static FloatComparator oppositeComparator(FloatComparator c) {
      return (FloatComparator)(c instanceof FloatComparators.OppositeComparator
         ? ((FloatComparators.OppositeComparator)c).comparator
         : new FloatComparators.OppositeComparator(c));
   }

   public static FloatComparator asFloatComparator(final Comparator<? super Float> c) {
      return c != null && !(c instanceof FloatComparator) ? new FloatComparator() {
         @Override
         public int compare(float x, float y) {
            return c.compare(x, y);
         }

         @Override
         public int compare(Float x, Float y) {
            return c.compare(x, y);
         }
      } : (FloatComparator)c;
   }

   protected static class NaturalImplicitComparator implements FloatComparator, Serializable {
      private static final long serialVersionUID = 1L;

      @Override
      public final int compare(float a, float b) {
         return Float.compare(a, b);
      }

      @Override
      public FloatComparator reversed() {
         return FloatComparators.OPPOSITE_COMPARATOR;
      }

      private Object readResolve() {
         return FloatComparators.NATURAL_COMPARATOR;
      }
   }

   protected static class OppositeComparator implements FloatComparator, Serializable {
      private static final long serialVersionUID = 1L;
      final FloatComparator comparator;

      protected OppositeComparator(FloatComparator c) {
         this.comparator = c;
      }

      @Override
      public final int compare(float a, float b) {
         return this.comparator.compare(b, a);
      }

      @Override
      public final FloatComparator reversed() {
         return this.comparator;
      }
   }

   protected static class OppositeImplicitComparator implements FloatComparator, Serializable {
      private static final long serialVersionUID = 1L;

      @Override
      public final int compare(float a, float b) {
         return -Float.compare(a, b);
      }

      @Override
      public FloatComparator reversed() {
         return FloatComparators.NATURAL_COMPARATOR;
      }

      private Object readResolve() {
         return FloatComparators.OPPOSITE_COMPARATOR;
      }
   }
}
