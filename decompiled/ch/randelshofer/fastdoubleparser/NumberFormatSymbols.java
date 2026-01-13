package ch.randelshofer.fastdoubleparser;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class NumberFormatSymbols {
   private final Set<Character> decimalSeparator;
   private final Set<Character> groupingSeparator;
   private final Set<String> exponentSeparator;
   private final Set<Character> minusSign;
   private final Set<Character> plusSign;
   private final Set<String> infinity;
   private final Set<String> nan;
   private final List<Character> digits;

   public NumberFormatSymbols(
      Set<Character> decimalSeparator,
      Set<Character> groupingSeparator,
      Set<String> exponentSeparator,
      Set<Character> minusSign,
      Set<Character> plusSign,
      Set<String> infinity,
      Set<String> nan,
      List<Character> digits
   ) {
      if (Objects.requireNonNull(digits, "digits").size() != 10) {
         throw new IllegalArgumentException("digits list must have size 10");
      } else {
         this.decimalSeparator = new LinkedHashSet<>(Objects.requireNonNull(decimalSeparator, "decimalSeparator"));
         this.groupingSeparator = new LinkedHashSet<>(Objects.requireNonNull(groupingSeparator, "groupingSeparator"));
         this.exponentSeparator = new LinkedHashSet<>(Objects.requireNonNull(exponentSeparator, "exponentSeparator"));
         this.minusSign = new LinkedHashSet<>(Objects.requireNonNull(minusSign, "minusSign"));
         this.plusSign = new LinkedHashSet<>(Objects.requireNonNull(plusSign, "plusSign"));
         this.infinity = new LinkedHashSet<>(Objects.requireNonNull(infinity, "infinity"));
         this.nan = new LinkedHashSet<>(Objects.requireNonNull(nan, "nan"));
         this.digits = new ArrayList<>(digits);
      }
   }

   public NumberFormatSymbols(
      String decimalSeparators,
      String groupingSeparators,
      Collection<String> exponentSeparators,
      String minusSigns,
      String plusSigns,
      Collection<String> infinity,
      Collection<String> nan,
      String digits
   ) {
      this(
         toSet(decimalSeparators),
         toSet(groupingSeparators),
         new LinkedHashSet<>(exponentSeparators),
         toSet(minusSigns),
         toSet(plusSigns),
         new LinkedHashSet<>(infinity),
         new LinkedHashSet<>(nan),
         toList(expandDigits(digits))
      );
   }

   private static String expandDigits(String digits) {
      if (digits.length() == 10) {
         return digits;
      } else if (digits.length() != 1) {
         throw new IllegalArgumentException("digits must have length 1 or 10, digits=\"" + digits + "\"");
      } else {
         StringBuilder buf = new StringBuilder(10);
         char zeroChar = digits.charAt(0);

         for (int i = 0; i < 10; i++) {
            buf.append((char)(zeroChar + i));
         }

         return buf.toString();
      }
   }

   public static NumberFormatSymbols fromDecimalFormatSymbols(DecimalFormatSymbols symbols) {
      List<Character> digits = new ArrayList<>(10);
      char zeroDigit = symbols.getZeroDigit();

      for (int i = 0; i < 10; i++) {
         digits.add((char)(zeroDigit + i));
      }

      return new NumberFormatSymbols(
         Collections.singleton(symbols.getDecimalSeparator()),
         Collections.singleton(symbols.getGroupingSeparator()),
         Collections.singleton(symbols.getExponentSeparator()),
         Collections.singleton(symbols.getMinusSign()),
         Collections.emptySet(),
         Collections.singleton(symbols.getInfinity()),
         Collections.singleton(symbols.getNaN()),
         digits
      );
   }

   public static NumberFormatSymbols fromDefault() {
      return new NumberFormatSymbols(
         Collections.singleton('.'),
         Collections.emptySet(),
         new HashSet<>(Arrays.asList("e", "E")),
         Collections.singleton('-'),
         Collections.singleton('+'),
         Collections.singleton("Infinity"),
         Collections.singleton("NaN"),
         Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
      );
   }

   private static List<Character> toList(String chars) {
      List<Character> set = new ArrayList<>(10);

      for (char ch : chars.toCharArray()) {
         set.add(ch);
      }

      return set;
   }

   private static Set<Character> toSet(String chars) {
      Set<Character> set = new LinkedHashSet<>(chars.length() * 2);

      for (char ch : chars.toCharArray()) {
         set.add(ch);
      }

      return set;
   }

   public Set<Character> decimalSeparator() {
      return this.decimalSeparator;
   }

   public List<Character> digits() {
      return this.digits;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj != null && obj.getClass() == this.getClass()) {
         NumberFormatSymbols that = (NumberFormatSymbols)obj;
         return Objects.equals(this.decimalSeparator, that.decimalSeparator)
            && Objects.equals(this.groupingSeparator, that.groupingSeparator)
            && Objects.equals(this.exponentSeparator, that.exponentSeparator)
            && Objects.equals(this.minusSign, that.minusSign)
            && Objects.equals(this.plusSign, that.plusSign)
            && Objects.equals(this.infinity, that.infinity)
            && Objects.equals(this.nan, that.nan)
            && Objects.equals(this.digits, that.digits);
      } else {
         return false;
      }
   }

   public Set<String> exponentSeparator() {
      return this.exponentSeparator;
   }

   public Set<Character> groupingSeparator() {
      return this.groupingSeparator;
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.decimalSeparator, this.groupingSeparator, this.exponentSeparator, this.minusSign, this.plusSign, this.infinity, this.nan, this.digits
      );
   }

   public Set<String> infinity() {
      return this.infinity;
   }

   public Set<Character> minusSign() {
      return this.minusSign;
   }

   public Set<String> nan() {
      return this.nan;
   }

   public Set<Character> plusSign() {
      return this.plusSign;
   }

   @Override
   public String toString() {
      return "NumberFormatSymbols[decimalSeparator="
         + this.decimalSeparator
         + ", groupingSeparator="
         + this.groupingSeparator
         + ", exponentSeparator="
         + this.exponentSeparator
         + ", minusSign="
         + this.minusSign
         + ", plusSign="
         + this.plusSign
         + ", infinity="
         + this.infinity
         + ", nan="
         + this.nan
         + ", digits="
         + this.digits
         + ']';
   }

   public NumberFormatSymbols withDecimalSeparator(Set<Character> newValue) {
      return new NumberFormatSymbols(
         newValue, this.groupingSeparator, this.exponentSeparator, this.minusSign, this.plusSign, this.infinity, this.nan, this.digits
      );
   }

   public NumberFormatSymbols withDigits(List<Character> newValue) {
      return new NumberFormatSymbols(
         this.decimalSeparator, this.groupingSeparator, this.exponentSeparator, this.minusSign, this.plusSign, this.infinity, this.nan, newValue
      );
   }

   public NumberFormatSymbols withExponentSeparator(Set<String> newValue) {
      return new NumberFormatSymbols(
         this.decimalSeparator, this.groupingSeparator, newValue, this.minusSign, this.plusSign, this.infinity, this.nan, this.digits
      );
   }

   public NumberFormatSymbols withGroupingSeparator(Set<Character> newValue) {
      return new NumberFormatSymbols(
         this.decimalSeparator, newValue, this.exponentSeparator, this.minusSign, this.plusSign, this.infinity, this.nan, this.digits
      );
   }

   public NumberFormatSymbols withInfinity(Set<String> newValue) {
      return new NumberFormatSymbols(
         this.decimalSeparator, this.groupingSeparator, this.exponentSeparator, this.minusSign, this.plusSign, newValue, this.nan, this.digits
      );
   }

   public NumberFormatSymbols withMinusSign(Set<Character> newValue) {
      return new NumberFormatSymbols(
         this.decimalSeparator, this.groupingSeparator, this.exponentSeparator, newValue, this.plusSign, this.infinity, this.nan, this.digits
      );
   }

   public NumberFormatSymbols withNaN(Set<String> newValue) {
      return new NumberFormatSymbols(
         this.decimalSeparator, this.groupingSeparator, this.exponentSeparator, this.minusSign, this.plusSign, this.infinity, newValue, this.digits
      );
   }

   public NumberFormatSymbols withPlusSign(Set<Character> newValue) {
      return new NumberFormatSymbols(
         this.decimalSeparator, this.groupingSeparator, this.exponentSeparator, this.minusSign, newValue, this.infinity, this.nan, this.digits
      );
   }
}
