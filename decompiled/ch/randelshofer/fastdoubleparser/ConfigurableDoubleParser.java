package ch.randelshofer.fastdoubleparser;

import java.text.DecimalFormatSymbols;
import java.util.Objects;

public final class ConfigurableDoubleParser {
   private final NumberFormatSymbols symbols;
   private ConfigurableDoubleBitsFromCharSequence charSequenceParser;
   private ConfigurableDoubleBitsFromCharArray charArrayParser;
   private final boolean ignoreCase;
   private final boolean isAllSingleCharSymbolsAscii;
   private final boolean isDigitsAscii;
   private final boolean isAscii;
   private ConfigurableDoubleBitsFromByteArrayAscii byteArrayAsciiParser;
   private ConfigurableDoubleBitsFromByteArrayUtf8 byteArrayUtf8Parser;

   public ConfigurableDoubleParser(NumberFormatSymbols symbols) {
      this(symbols, false);
   }

   public ConfigurableDoubleParser(DecimalFormatSymbols symbols) {
      this(symbols, false);
   }

   public ConfigurableDoubleParser(NumberFormatSymbols symbols, boolean ignoreCase) {
      Objects.requireNonNull(symbols, "symbols");
      this.symbols = symbols;
      this.ignoreCase = ignoreCase;
      this.isAllSingleCharSymbolsAscii = NumberFormatSymbolsInfo.isMostlyAscii(symbols);
      this.isDigitsAscii = NumberFormatSymbolsInfo.isDigitsTokensAscii(symbols);
      this.isAscii = NumberFormatSymbolsInfo.isAscii(symbols);
   }

   public NumberFormatSymbols getNumberFormatSymbols() {
      return this.symbols;
   }

   public boolean isIgnoreCase() {
      return this.ignoreCase;
   }

   public ConfigurableDoubleParser(DecimalFormatSymbols symbols, boolean ignoreCase) {
      this(NumberFormatSymbols.fromDecimalFormatSymbols(symbols), ignoreCase);
   }

   public ConfigurableDoubleParser() {
      this(NumberFormatSymbols.fromDefault(), false);
   }

   private ConfigurableDoubleBitsFromCharArray getCharArrayParser() {
      if (this.charArrayParser == null) {
         this.charArrayParser = new ConfigurableDoubleBitsFromCharArray(this.symbols, this.ignoreCase);
      }

      return this.charArrayParser;
   }

   private ConfigurableDoubleBitsFromByteArrayAscii getByteArrayAsciiParser() {
      if (this.byteArrayAsciiParser == null) {
         this.byteArrayAsciiParser = new ConfigurableDoubleBitsFromByteArrayAscii(this.symbols, this.ignoreCase);
      }

      return this.byteArrayAsciiParser;
   }

   private ConfigurableDoubleBitsFromByteArrayUtf8 getByteArrayUtf8Parser() {
      if (this.byteArrayUtf8Parser == null) {
         this.byteArrayUtf8Parser = new ConfigurableDoubleBitsFromByteArrayUtf8(this.symbols, this.ignoreCase);
      }

      return this.byteArrayUtf8Parser;
   }

   private ConfigurableDoubleBitsFromCharSequence getCharSequenceParser() {
      if (this.charSequenceParser == null) {
         this.charSequenceParser = new ConfigurableDoubleBitsFromCharSequence(this.symbols, this.ignoreCase);
      }

      return this.charSequenceParser;
   }

   public double parseDouble(CharSequence str) {
      return this.parseDouble(str, 0, str.length());
   }

   public double parseDouble(CharSequence str, int offset, int length) {
      long bitPattern = this.getCharSequenceParser().parseFloatingPointLiteral(str, offset, length);
      if (bitPattern == 9221120237041090561L) {
         throw new NumberFormatException("illegal syntax");
      } else {
         return Double.longBitsToDouble(bitPattern);
      }
   }

   public double parseDouble(char[] str) {
      return this.parseDouble(str, 0, str.length);
   }

   public double parseDouble(char[] str, int offset, int length) {
      long bitPattern = this.getCharArrayParser().parseFloatingPointLiteral(str, offset, length);
      if (bitPattern == 9221120237041090561L) {
         throw new NumberFormatException("illegal syntax");
      } else {
         return Double.longBitsToDouble(bitPattern);
      }
   }

   public double parseDouble(byte[] str) {
      return this.parseDouble(str, 0, str.length);
   }

   public double parseDouble(byte[] str, int offset, int length) {
      long bitPattern;
      if (!this.isAscii && (this.ignoreCase || !this.isAllSingleCharSymbolsAscii)) {
         if (this.isDigitsAscii) {
            bitPattern = this.getByteArrayUtf8Parser().parseFloatingPointLiteral(str, offset, length);
         } else {
            Utf8Decoder.Result result = Utf8Decoder.decode(str, offset, length);
            bitPattern = this.getCharArrayParser().parseFloatingPointLiteral(result.chars(), 0, result.length());
         }
      } else {
         bitPattern = this.getByteArrayAsciiParser().parseFloatingPointLiteral(str, offset, length);
      }

      if (bitPattern == 9221120237041090561L) {
         throw new NumberFormatException("illegal syntax");
      } else {
         return Double.longBitsToDouble(bitPattern);
      }
   }
}
