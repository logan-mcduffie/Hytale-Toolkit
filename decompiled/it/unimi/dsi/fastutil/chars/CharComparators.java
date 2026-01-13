package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;
import java.util.Comparator;

public final class CharComparators {
   public static final CharComparator NATURAL_COMPARATOR = new CharComparators.NaturalImplicitComparator();
   public static final CharComparator OPPOSITE_COMPARATOR = new CharComparators.OppositeImplicitComparator();

   private CharComparators() {
   }

   public static CharComparator oppositeComparator(CharComparator c) {
      return (CharComparator)(c instanceof CharComparators.OppositeComparator
         ? ((CharComparators.OppositeComparator)c).comparator
         : new CharComparators.OppositeComparator(c));
   }

   public static CharComparator asCharComparator(final Comparator<? super Character> c) {
      return c != null && !(c instanceof CharComparator) ? new CharComparator() {
         @Override
         public int compare(char x, char y) {
            return c.compare(x, y);
         }

         @Override
         public int compare(Character x, Character y) {
            return c.compare(x, y);
         }
      } : (CharComparator)c;
   }

   protected static class NaturalImplicitComparator implements CharComparator, Serializable {
      private static final long serialVersionUID = 1L;

      @Override
      public final int compare(char a, char b) {
         return Character.compare(a, b);
      }

      @Override
      public CharComparator reversed() {
         return CharComparators.OPPOSITE_COMPARATOR;
      }

      private Object readResolve() {
         return CharComparators.NATURAL_COMPARATOR;
      }
   }

   protected static class OppositeComparator implements CharComparator, Serializable {
      private static final long serialVersionUID = 1L;
      final CharComparator comparator;

      protected OppositeComparator(CharComparator c) {
         this.comparator = c;
      }

      @Override
      public final int compare(char a, char b) {
         return this.comparator.compare(b, a);
      }

      @Override
      public final CharComparator reversed() {
         return this.comparator;
      }
   }

   protected static class OppositeImplicitComparator implements CharComparator, Serializable {
      private static final long serialVersionUID = 1L;

      @Override
      public final int compare(char a, char b) {
         return -Character.compare(a, b);
      }

      @Override
      public CharComparator reversed() {
         return CharComparators.NATURAL_COMPARATOR;
      }

      private Object readResolve() {
         return CharComparators.OPPOSITE_COMPARATOR;
      }
   }
}
