package com.nimbusds.jose.shaded.gson.stream;

import com.nimbusds.jose.shaded.gson.Strictness;
import com.nimbusds.jose.shaded.gson.internal.JsonReaderInternalAccess;
import com.nimbusds.jose.shaded.gson.internal.TroubleshootingGuide;
import com.nimbusds.jose.shaded.gson.internal.bind.JsonTreeReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;

public class JsonReader implements Closeable {
   private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
   private static final int PEEKED_NONE = 0;
   private static final int PEEKED_BEGIN_OBJECT = 1;
   private static final int PEEKED_END_OBJECT = 2;
   private static final int PEEKED_BEGIN_ARRAY = 3;
   private static final int PEEKED_END_ARRAY = 4;
   private static final int PEEKED_TRUE = 5;
   private static final int PEEKED_FALSE = 6;
   private static final int PEEKED_NULL = 7;
   private static final int PEEKED_SINGLE_QUOTED = 8;
   private static final int PEEKED_DOUBLE_QUOTED = 9;
   private static final int PEEKED_UNQUOTED = 10;
   private static final int PEEKED_BUFFERED = 11;
   private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
   private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
   private static final int PEEKED_UNQUOTED_NAME = 14;
   private static final int PEEKED_LONG = 15;
   private static final int PEEKED_NUMBER = 16;
   private static final int PEEKED_EOF = 17;
   private static final int NUMBER_CHAR_NONE = 0;
   private static final int NUMBER_CHAR_SIGN = 1;
   private static final int NUMBER_CHAR_DIGIT = 2;
   private static final int NUMBER_CHAR_DECIMAL = 3;
   private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
   private static final int NUMBER_CHAR_EXP_E = 5;
   private static final int NUMBER_CHAR_EXP_SIGN = 6;
   private static final int NUMBER_CHAR_EXP_DIGIT = 7;
   private final Reader in;
   private Strictness strictness = Strictness.LEGACY_STRICT;
   static final int DEFAULT_NESTING_LIMIT = 255;
   private int nestingLimit = 255;
   static final int BUFFER_SIZE = 1024;
   private final char[] buffer = new char[1024];
   private int pos = 0;
   private int limit = 0;
   private int lineNumber = 0;
   private int lineStart = 0;
   int peeked = 0;
   private long peekedLong;
   private int peekedNumberLength;
   private String peekedString;
   private int[] stack = new int[32];
   private int stackSize = 0;
   private String[] pathNames;
   private int[] pathIndices;

   public JsonReader(Reader in) {
      this.stack[this.stackSize++] = 6;
      this.pathNames = new String[32];
      this.pathIndices = new int[32];
      this.in = Objects.requireNonNull(in, "in == null");
   }

   @Deprecated
   public final void setLenient(boolean lenient) {
      this.setStrictness(lenient ? Strictness.LENIENT : Strictness.LEGACY_STRICT);
   }

   public final boolean isLenient() {
      return this.strictness == Strictness.LENIENT;
   }

   public final void setStrictness(Strictness strictness) {
      Objects.requireNonNull(strictness);
      this.strictness = strictness;
   }

   public final Strictness getStrictness() {
      return this.strictness;
   }

   public final void setNestingLimit(int limit) {
      if (limit < 0) {
         throw new IllegalArgumentException("Invalid nesting limit: " + limit);
      } else {
         this.nestingLimit = limit;
      }
   }

   public final int getNestingLimit() {
      return this.nestingLimit;
   }

   public void beginArray() throws IOException {
      int p = this.peeked;
      if (p == 0) {
         p = this.doPeek();
      }

      if (p == 3) {
         this.push(1);
         this.pathIndices[this.stackSize - 1] = 0;
         this.peeked = 0;
      } else {
         throw this.unexpectedTokenError("BEGIN_ARRAY");
      }
   }

   public void endArray() throws IOException {
      int p = this.peeked;
      if (p == 0) {
         p = this.doPeek();
      }

      if (p == 4) {
         this.stackSize--;
         this.pathIndices[this.stackSize - 1]++;
         this.peeked = 0;
      } else {
         throw this.unexpectedTokenError("END_ARRAY");
      }
   }

   public void beginObject() throws IOException {
      int p = this.peeked;
      if (p == 0) {
         p = this.doPeek();
      }

      if (p == 1) {
         this.push(3);
         this.peeked = 0;
      } else {
         throw this.unexpectedTokenError("BEGIN_OBJECT");
      }
   }

   public void endObject() throws IOException {
      int p = this.peeked;
      if (p == 0) {
         p = this.doPeek();
      }

      if (p == 2) {
         this.stackSize--;
         this.pathNames[this.stackSize] = null;
         this.pathIndices[this.stackSize - 1]++;
         this.peeked = 0;
      } else {
         throw this.unexpectedTokenError("END_OBJECT");
      }
   }

   public boolean hasNext() throws IOException {
      int p = this.peeked;
      if (p == 0) {
         p = this.doPeek();
      }

      return p != 2 && p != 4 && p != 17;
   }

   public JsonToken peek() throws IOException {
      int p = this.peeked;
      if (p == 0) {
         p = this.doPeek();
      }

      switch (p) {
         case 1:
            return JsonToken.BEGIN_OBJECT;
         case 2:
            return JsonToken.END_OBJECT;
         case 3:
            return JsonToken.BEGIN_ARRAY;
         case 4:
            return JsonToken.END_ARRAY;
         case 5:
         case 6:
            return JsonToken.BOOLEAN;
         case 7:
            return JsonToken.NULL;
         case 8:
         case 9:
         case 10:
         case 11:
            return JsonToken.STRING;
         case 12:
         case 13:
         case 14:
            return JsonToken.NAME;
         case 15:
         case 16:
            return JsonToken.NUMBER;
         case 17:
            return JsonToken.END_DOCUMENT;
         default:
            throw new AssertionError();
      }
   }

   int doPeek() throws IOException {
      int peekStack = this.stack[this.stackSize - 1];
      if (peekStack == 1) {
         this.stack[this.stackSize - 1] = 2;
      } else if (peekStack == 2) {
         int c = this.nextNonWhitespace(true);
         switch (c) {
            case 44:
               break;
            case 59:
               this.checkLenient();
               break;
            case 93:
               this.peeked = 4;
               return this.peeked;
            default:
               throw this.syntaxError("Unterminated array");
         }
      } else {
         if (peekStack == 3 || peekStack == 5) {
            this.stack[this.stackSize - 1] = 4;
            if (peekStack == 5) {
               int c = this.nextNonWhitespace(true);
               switch (c) {
                  case 44:
                     break;
                  case 59:
                     this.checkLenient();
                     break;
                  case 125:
                     this.peeked = 2;
                     return this.peeked;
                  default:
                     throw this.syntaxError("Unterminated object");
               }
            }

            int c = this.nextNonWhitespace(true);
            switch (c) {
               case 34:
                  this.peeked = 13;
                  return this.peeked;
               case 39:
                  this.checkLenient();
                  this.peeked = 12;
                  return this.peeked;
               case 125:
                  if (peekStack != 5) {
                     this.peeked = 2;
                     return this.peeked;
                  }

                  throw this.syntaxError("Expected name");
               default:
                  this.checkLenient();
                  this.pos--;
                  if (this.isLiteral((char)c)) {
                     this.peeked = 14;
                     return this.peeked;
                  }

                  throw this.syntaxError("Expected name");
            }
         }

         if (peekStack == 4) {
            this.stack[this.stackSize - 1] = 5;
            int c = this.nextNonWhitespace(true);
            switch (c) {
               case 58:
                  break;
               case 61:
                  this.checkLenient();
                  if ((this.pos < this.limit || this.fillBuffer(1)) && this.buffer[this.pos] == '>') {
                     this.pos++;
                  }
                  break;
               default:
                  throw this.syntaxError("Expected ':'");
            }
         } else if (peekStack == 6) {
            if (this.strictness == Strictness.LENIENT) {
               this.consumeNonExecutePrefix();
            }

            this.stack[this.stackSize - 1] = 7;
         } else if (peekStack == 7) {
            int c = this.nextNonWhitespace(false);
            if (c == -1) {
               this.peeked = 17;
               return this.peeked;
            }

            this.checkLenient();
            this.pos--;
         } else if (peekStack == 8) {
            throw new IllegalStateException("JsonReader is closed");
         }
      }

      int c = this.nextNonWhitespace(true);
      switch (c) {
         case 34:
            this.peeked = 9;
            return this.peeked;
         case 39:
            this.checkLenient();
            this.peeked = 8;
            return this.peeked;
         case 91:
            this.peeked = 3;
            return this.peeked;
         case 93:
            if (peekStack == 1) {
               this.peeked = 4;
               return this.peeked;
            }
         case 44:
         case 59:
            if (peekStack != 1 && peekStack != 2) {
               throw this.syntaxError("Unexpected value");
            }

            this.checkLenient();
            this.pos--;
            this.peeked = 7;
            return this.peeked;
         case 123:
            this.peeked = 1;
            return this.peeked;
         default:
            this.pos--;
            int result = this.peekKeyword();
            if (result != 0) {
               return result;
            } else {
               result = this.peekNumber();
               if (result != 0) {
                  return result;
               } else if (!this.isLiteral(this.buffer[this.pos])) {
                  throw this.syntaxError("Expected value");
               } else {
                  this.checkLenient();
                  this.peeked = 10;
                  return this.peeked;
               }
            }
      }
   }

   private int peekKeyword() throws IOException {
      char c = this.buffer[this.pos];
      String keyword;
      String keywordUpper;
      int peeking;
      if (c == 't' || c == 'T') {
         keyword = "true";
         keywordUpper = "TRUE";
         peeking = 5;
      } else if (c != 'f' && c != 'F') {
         if (c != 'n' && c != 'N') {
            return 0;
         }

         keyword = "null";
         keywordUpper = "NULL";
         peeking = 7;
      } else {
         keyword = "false";
         keywordUpper = "FALSE";
         peeking = 6;
      }

      boolean allowsUpperCased = this.strictness != Strictness.STRICT;
      int length = keyword.length();

      for (int i = 0; i < length; i++) {
         if (this.pos + i >= this.limit && !this.fillBuffer(i + 1)) {
            return 0;
         }

         c = this.buffer[this.pos + i];
         boolean matched = c == keyword.charAt(i) || allowsUpperCased && c == keywordUpper.charAt(i);
         if (!matched) {
            return 0;
         }
      }

      if ((this.pos + length < this.limit || this.fillBuffer(length + 1)) && this.isLiteral(this.buffer[this.pos + length])) {
         return 0;
      } else {
         this.pos += length;
         this.peeked = peeking;
         return this.peeked;
      }
   }

   private int peekNumber() throws IOException {
      char[] buffer = this.buffer;
      int p = this.pos;
      int l = this.limit;
      long value = 0L;
      boolean negative = false;
      boolean fitsInLong = true;
      int last = 0;
      int i = 0;

      label132:
      while (true) {
         if (p + i == l) {
            if (i == buffer.length) {
               return 0;
            }

            if (!this.fillBuffer(i + 1)) {
               break;
            }

            p = this.pos;
            l = this.limit;
         }

         char c = buffer[p + i];
         switch (c) {
            case '+':
               if (last != 5) {
                  return 0;
               }

               last = 6;
               break;
            case '-':
               if (last == 0) {
                  negative = true;
                  last = 1;
               } else {
                  if (last != 5) {
                     return 0;
                  }

                  last = 6;
               }
               break;
            case '.':
               if (last != 2) {
                  return 0;
               }

               last = 3;
               break;
            case 'E':
            case 'e':
               if (last != 2 && last != 4) {
                  return 0;
               }

               last = 5;
               break;
            default:
               if (c < '0' || c > '9') {
                  if (this.isLiteral(c)) {
                     return 0;
                  }
                  break label132;
               }

               if (last == 1 || last == 0) {
                  value = -(c - '0');
                  last = 2;
               } else if (last == 2) {
                  if (value == 0L) {
                     return 0;
                  }

                  long newValue = value * 10L - (c - '0');
                  fitsInLong &= value > -922337203685477580L || value == -922337203685477580L && newValue < value;
                  value = newValue;
               } else if (last == 3) {
                  last = 4;
               } else if (last == 5 || last == 6) {
                  last = 7;
               }
         }

         i++;
      }

      if (last != 2 || !fitsInLong || value == Long.MIN_VALUE && !negative || value == 0L && negative) {
         if (last != 2 && last != 4 && last != 7) {
            return 0;
         } else {
            this.peekedNumberLength = i;
            this.peeked = 16;
            return this.peeked;
         }
      } else {
         this.peekedLong = negative ? value : -value;
         this.pos += i;
         this.peeked = 15;
         return this.peeked;
      }
   }

   private boolean isLiteral(char c) throws IOException {
      switch (c) {
         case '#':
         case '/':
         case ';':
         case '=':
         case '\\':
            this.checkLenient();
         case '\t':
         case '\n':
         case '\f':
         case '\r':
         case ' ':
         case ',':
         case ':':
         case '[':
         case ']':
         case '{':
         case '}':
            return false;
         default:
            return true;
      }
   }

   public String nextName() throws IOException {
      int p = this.peeked;
      if (p == 0) {
         p = this.doPeek();
      }

      String result;
      if (p == 14) {
         result = this.nextUnquotedValue();
      } else if (p == 12) {
         result = this.nextQuotedValue('\'');
      } else {
         if (p != 13) {
            throw this.unexpectedTokenError("a name");
         }

         result = this.nextQuotedValue('"');
      }

      this.peeked = 0;
      this.pathNames[this.stackSize - 1] = result;
      return result;
   }

   public String nextString() throws IOException {
      int p = this.peeked;
      if (p == 0) {
         p = this.doPeek();
      }

      String result;
      if (p == 10) {
         result = this.nextUnquotedValue();
      } else if (p == 8) {
         result = this.nextQuotedValue('\'');
      } else if (p == 9) {
         result = this.nextQuotedValue('"');
      } else if (p == 11) {
         result = this.peekedString;
         this.peekedString = null;
      } else if (p == 15) {
         result = Long.toString(this.peekedLong);
      } else {
         if (p != 16) {
            throw this.unexpectedTokenError("a string");
         }

         result = new String(this.buffer, this.pos, this.peekedNumberLength);
         this.pos = this.pos + this.peekedNumberLength;
      }

      this.peeked = 0;
      this.pathIndices[this.stackSize - 1]++;
      return result;
   }

   public boolean nextBoolean() throws IOException {
      int p = this.peeked;
      if (p == 0) {
         p = this.doPeek();
      }

      if (p == 5) {
         this.peeked = 0;
         this.pathIndices[this.stackSize - 1]++;
         return true;
      } else if (p == 6) {
         this.peeked = 0;
         this.pathIndices[this.stackSize - 1]++;
         return false;
      } else {
         throw this.unexpectedTokenError("a boolean");
      }
   }

   public void nextNull() throws IOException {
      int p = this.peeked;
      if (p == 0) {
         p = this.doPeek();
      }

      if (p == 7) {
         this.peeked = 0;
         this.pathIndices[this.stackSize - 1]++;
      } else {
         throw this.unexpectedTokenError("null");
      }
   }

   public double nextDouble() throws IOException {
      int p = this.peeked;
      if (p == 0) {
         p = this.doPeek();
      }

      if (p == 15) {
         this.peeked = 0;
         this.pathIndices[this.stackSize - 1]++;
         return this.peekedLong;
      } else {
         if (p == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos = this.pos + this.peekedNumberLength;
         } else if (p == 8 || p == 9) {
            this.peekedString = this.nextQuotedValue((char)(p == 8 ? '\'' : '"'));
         } else if (p == 10) {
            this.peekedString = this.nextUnquotedValue();
         } else if (p != 11) {
            throw this.unexpectedTokenError("a double");
         }

         this.peeked = 11;
         double result = Double.parseDouble(this.peekedString);
         if (this.strictness == Strictness.LENIENT || !Double.isNaN(result) && !Double.isInfinite(result)) {
            this.peekedString = null;
            this.peeked = 0;
            this.pathIndices[this.stackSize - 1]++;
            return result;
         } else {
            throw this.syntaxError("JSON forbids NaN and infinities: " + result);
         }
      }
   }

   public long nextLong() throws IOException {
      int p = this.peeked;
      if (p == 0) {
         p = this.doPeek();
      }

      if (p == 15) {
         this.peeked = 0;
         this.pathIndices[this.stackSize - 1]++;
         return this.peekedLong;
      } else {
         if (p == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos = this.pos + this.peekedNumberLength;
         } else {
            if (p != 8 && p != 9 && p != 10) {
               throw this.unexpectedTokenError("a long");
            }

            if (p == 10) {
               this.peekedString = this.nextUnquotedValue();
            } else {
               this.peekedString = this.nextQuotedValue((char)(p == 8 ? '\'' : '"'));
            }

            try {
               long result = Long.parseLong(this.peekedString);
               this.peeked = 0;
               this.pathIndices[this.stackSize - 1]++;
               return result;
            } catch (NumberFormatException var6) {
            }
         }

         this.peeked = 11;
         double asDouble = Double.parseDouble(this.peekedString);
         long result = (long)asDouble;
         if (result != asDouble) {
            throw new NumberFormatException("Expected a long but was " + this.peekedString + this.locationString());
         } else {
            this.peekedString = null;
            this.peeked = 0;
            this.pathIndices[this.stackSize - 1]++;
            return result;
         }
      }
   }

   private String nextQuotedValue(char quote) throws IOException {
      char[] buffer = this.buffer;
      StringBuilder builder = null;

      do {
         int p = this.pos;
         int l = this.limit;
         int start = p;

         while (p < l) {
            int c = buffer[p++];
            if (this.strictness == Strictness.STRICT && c < 32) {
               throw this.syntaxError("Unescaped control characters (\\u0000-\\u001F) are not allowed in strict mode");
            }

            if (c == quote) {
               this.pos = p;
               int len = p - start - 1;
               if (builder == null) {
                  return new String(buffer, start, len);
               }

               builder.append(buffer, start, len);
               return builder.toString();
            }

            if (c == 92) {
               this.pos = p;
               int len = p - start - 1;
               if (builder == null) {
                  int estimatedLength = (len + 1) * 2;
                  builder = new StringBuilder(Math.max(estimatedLength, 16));
               }

               builder.append(buffer, start, len);
               builder.append(this.readEscapeCharacter());
               p = this.pos;
               l = this.limit;
               start = p;
            } else if (c == 10) {
               this.lineNumber++;
               this.lineStart = p;
            }
         }

         if (builder == null) {
            int estimatedLength = (p - start) * 2;
            builder = new StringBuilder(Math.max(estimatedLength, 16));
         }

         builder.append(buffer, start, p - start);
         this.pos = p;
      } while (this.fillBuffer(1));

      throw this.syntaxError("Unterminated string");
   }

   private String nextUnquotedValue() throws IOException {
      StringBuilder builder = null;
      int i = 0;

      label34:
      while (true) {
         if (this.pos + i < this.limit) {
            switch (this.buffer[this.pos + i]) {
               case '\t':
               case '\n':
               case '\f':
               case '\r':
               case ' ':
               case ',':
               case ':':
               case '[':
               case ']':
               case '{':
               case '}':
                  break label34;
               case '#':
               case '/':
               case ';':
               case '=':
               case '\\':
                  this.checkLenient();
                  break label34;
               default:
                  i++;
            }
         } else if (i < this.buffer.length) {
            if (this.fillBuffer(i + 1)) {
               continue;
            }
            break;
         } else {
            if (builder == null) {
               builder = new StringBuilder(Math.max(i, 16));
            }

            builder.append(this.buffer, this.pos, i);
            this.pos += i;
            i = 0;
            if (!this.fillBuffer(1)) {
               break;
            }
         }
      }

      String result = builder == null ? new String(this.buffer, this.pos, i) : builder.append(this.buffer, this.pos, i).toString();
      this.pos += i;
      return result;
   }

   private void skipQuotedValue(char quote) throws IOException {
      char[] buffer = this.buffer;

      do {
         int p = this.pos;
         int l = this.limit;

         while (p < l) {
            int c = buffer[p++];
            if (c == quote) {
               this.pos = p;
               return;
            }

            if (c == 92) {
               this.pos = p;
               char unused = this.readEscapeCharacter();
               p = this.pos;
               l = this.limit;
            } else if (c == 10) {
               this.lineNumber++;
               this.lineStart = p;
            }
         }

         this.pos = p;
      } while (this.fillBuffer(1));

      throw this.syntaxError("Unterminated string");
   }

   private void skipUnquotedValue() throws IOException {
      do {
         int i;
         for (i = 0; this.pos + i < this.limit; i++) {
            switch (this.buffer[this.pos + i]) {
               case '#':
               case '/':
               case ';':
               case '=':
               case '\\':
                  this.checkLenient();
               case '\t':
               case '\n':
               case '\f':
               case '\r':
               case ' ':
               case ',':
               case ':':
               case '[':
               case ']':
               case '{':
               case '}':
                  this.pos += i;
                  return;
            }
         }

         this.pos += i;
      } while (this.fillBuffer(1));
   }

   public int nextInt() throws IOException {
      int p = this.peeked;
      if (p == 0) {
         p = this.doPeek();
      }

      if (p == 15) {
         int result = (int)this.peekedLong;
         if (this.peekedLong != result) {
            throw new NumberFormatException("Expected an int but was " + this.peekedLong + this.locationString());
         } else {
            this.peeked = 0;
            this.pathIndices[this.stackSize - 1]++;
            return result;
         }
      } else {
         if (p == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos = this.pos + this.peekedNumberLength;
         } else {
            if (p != 8 && p != 9 && p != 10) {
               throw this.unexpectedTokenError("an int");
            }

            if (p == 10) {
               this.peekedString = this.nextUnquotedValue();
            } else {
               this.peekedString = this.nextQuotedValue((char)(p == 8 ? '\'' : '"'));
            }

            try {
               int result = Integer.parseInt(this.peekedString);
               this.peeked = 0;
               this.pathIndices[this.stackSize - 1]++;
               return result;
            } catch (NumberFormatException var5) {
            }
         }

         this.peeked = 11;
         double asDouble = Double.parseDouble(this.peekedString);
         int result = (int)asDouble;
         if (result != asDouble) {
            throw new NumberFormatException("Expected an int but was " + this.peekedString + this.locationString());
         } else {
            this.peekedString = null;
            this.peeked = 0;
            this.pathIndices[this.stackSize - 1]++;
            return result;
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.peeked = 0;
      this.stack[0] = 8;
      this.stackSize = 1;
      this.in.close();
   }

   public void skipValue() throws IOException {
      int count = 0;

      do {
         int p = this.peeked;
         if (p == 0) {
            p = this.doPeek();
         }

         switch (p) {
            case 1:
               this.push(3);
               count++;
               break;
            case 2:
               if (count == 0) {
                  this.pathNames[this.stackSize - 1] = null;
               }

               this.stackSize--;
               count--;
               break;
            case 3:
               this.push(1);
               count++;
               break;
            case 4:
               this.stackSize--;
               count--;
            case 5:
            case 6:
            case 7:
            case 11:
            case 15:
            default:
               break;
            case 8:
               this.skipQuotedValue('\'');
               break;
            case 9:
               this.skipQuotedValue('"');
               break;
            case 10:
               this.skipUnquotedValue();
               break;
            case 12:
               this.skipQuotedValue('\'');
               if (count == 0) {
                  this.pathNames[this.stackSize - 1] = "<skipped>";
               }
               break;
            case 13:
               this.skipQuotedValue('"');
               if (count == 0) {
                  this.pathNames[this.stackSize - 1] = "<skipped>";
               }
               break;
            case 14:
               this.skipUnquotedValue();
               if (count == 0) {
                  this.pathNames[this.stackSize - 1] = "<skipped>";
               }
               break;
            case 16:
               this.pos = this.pos + this.peekedNumberLength;
               break;
            case 17:
               return;
         }

         this.peeked = 0;
      } while (count > 0);

      this.pathIndices[this.stackSize - 1]++;
   }

   private void push(int newTop) throws MalformedJsonException {
      if (this.stackSize - 1 >= this.nestingLimit) {
         throw new MalformedJsonException("Nesting limit " + this.nestingLimit + " reached" + this.locationString());
      } else {
         if (this.stackSize == this.stack.length) {
            int newLength = this.stackSize * 2;
            this.stack = Arrays.copyOf(this.stack, newLength);
            this.pathIndices = Arrays.copyOf(this.pathIndices, newLength);
            this.pathNames = Arrays.copyOf(this.pathNames, newLength);
         }

         this.stack[this.stackSize++] = newTop;
      }
   }

   private boolean fillBuffer(int minimum) throws IOException {
      char[] buffer = this.buffer;
      this.lineStart = this.lineStart - this.pos;
      if (this.limit != this.pos) {
         this.limit = this.limit - this.pos;
         System.arraycopy(buffer, this.pos, buffer, 0, this.limit);
      } else {
         this.limit = 0;
      }

      this.pos = 0;

      int total;
      while ((total = this.in.read(buffer, this.limit, buffer.length - this.limit)) != -1) {
         this.limit += total;
         if (this.lineNumber == 0 && this.lineStart == 0 && this.limit > 0 && buffer[0] == '\ufeff') {
            this.pos++;
            this.lineStart++;
            minimum++;
         }

         if (this.limit >= minimum) {
            return true;
         }
      }

      return false;
   }

   private int nextNonWhitespace(boolean throwOnEof) throws IOException {
      char[] buffer = this.buffer;
      int p = this.pos;
      int l = this.limit;

      while (true) {
         if (p == l) {
            this.pos = p;
            if (!this.fillBuffer(1)) {
               if (throwOnEof) {
                  throw new EOFException("End of input" + this.locationString());
               }

               return -1;
            }

            p = this.pos;
            l = this.limit;
         }

         int c = buffer[p++];
         if (c == 10) {
            this.lineNumber++;
            this.lineStart = p;
         } else if (c != 32 && c != 13 && c != 9) {
            if (c == 47) {
               this.pos = p;
               if (p == l) {
                  this.pos--;
                  boolean charsLoaded = this.fillBuffer(2);
                  this.pos++;
                  if (!charsLoaded) {
                     return c;
                  }
               }

               this.checkLenient();
               char peek = buffer[this.pos];
               switch (peek) {
                  case '*':
                     this.pos++;
                     if (!this.skipTo("*/")) {
                        throw this.syntaxError("Unterminated comment");
                     }

                     p = this.pos + 2;
                     l = this.limit;
                     break;
                  case '/':
                     this.pos++;
                     this.skipToEndOfLine();
                     p = this.pos;
                     l = this.limit;
                     break;
                  default:
                     return c;
               }
            } else {
               if (c != 35) {
                  this.pos = p;
                  return c;
               }

               this.pos = p;
               this.checkLenient();
               this.skipToEndOfLine();
               p = this.pos;
               l = this.limit;
            }
         }
      }
   }

   private void checkLenient() throws MalformedJsonException {
      if (this.strictness != Strictness.LENIENT) {
         throw this.syntaxError("Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON");
      }
   }

   private void skipToEndOfLine() throws IOException {
      while (this.pos < this.limit || this.fillBuffer(1)) {
         char c = this.buffer[this.pos++];
         if (c == '\n') {
            this.lineNumber++;
            this.lineStart = this.pos;
         } else if (c != '\r') {
            continue;
         }
         break;
      }
   }

   private boolean skipTo(String toFind) throws IOException {
      int length = toFind.length();

      while (this.pos + length <= this.limit || this.fillBuffer(length)) {
         if (this.buffer[this.pos] == '\n') {
            this.lineNumber++;
            this.lineStart = this.pos + 1;
         } else {
            int c = 0;

            while (true) {
               if (c >= length) {
                  return true;
               }

               if (this.buffer[this.pos + c] != toFind.charAt(c)) {
                  break;
               }

               c++;
            }
         }

         this.pos++;
      }

      return false;
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + this.locationString();
   }

   String locationString() {
      int line = this.lineNumber + 1;
      int column = this.pos - this.lineStart + 1;
      return " at line " + line + " column " + column + " path " + this.getPath();
   }

   private String getPath(boolean usePreviousPath) {
      StringBuilder result = new StringBuilder().append('$');

      for (int i = 0; i < this.stackSize; i++) {
         int scope = this.stack[i];
         switch (scope) {
            case 1:
            case 2:
               int pathIndex = this.pathIndices[i];
               if (usePreviousPath && pathIndex > 0 && i == this.stackSize - 1) {
                  pathIndex--;
               }

               result.append('[').append(pathIndex).append(']');
               break;
            case 3:
            case 4:
            case 5:
               result.append('.');
               if (this.pathNames[i] != null) {
                  result.append(this.pathNames[i]);
               }
            case 6:
            case 7:
            case 8:
               break;
            default:
               throw new AssertionError("Unknown scope value: " + scope);
         }
      }

      return result.toString();
   }

   public String getPath() {
      return this.getPath(false);
   }

   public String getPreviousPath() {
      return this.getPath(true);
   }

   private char readEscapeCharacter() throws IOException {
      if (this.pos == this.limit && !this.fillBuffer(1)) {
         throw this.syntaxError("Unterminated escape sequence");
      } else {
         char escaped = this.buffer[this.pos++];
         switch (escaped) {
            case '\n':
               if (this.strictness == Strictness.STRICT) {
                  throw this.syntaxError("Cannot escape a newline character in strict mode");
               } else {
                  this.lineNumber++;
                  this.lineStart = this.pos;
               }
            case '\'':
               if (this.strictness == Strictness.STRICT) {
                  throw this.syntaxError("Invalid escaped character \"'\" in strict mode");
               }
            case '"':
            case '/':
            case '\\':
               return escaped;
            case 'b':
               return '\b';
            case 'f':
               return '\f';
            case 'n':
               return '\n';
            case 'r':
               return '\r';
            case 't':
               return '\t';
            case 'u':
               if (this.pos + 4 > this.limit && !this.fillBuffer(4)) {
                  throw this.syntaxError("Unterminated escape sequence");
               } else {
                  int result = 0;
                  int i = this.pos;

                  for (int end = i + 4; i < end; i++) {
                     char c = this.buffer[i];
                     result <<= 4;
                     if (c >= '0' && c <= '9') {
                        result += c - '0';
                     } else if (c >= 'a' && c <= 'f') {
                        result += c - 'a' + 10;
                     } else {
                        if (c < 'A' || c > 'F') {
                           throw this.syntaxError("Malformed Unicode escape \\u" + new String(this.buffer, this.pos, 4));
                        }

                        result += c - 'A' + 10;
                     }
                  }

                  this.pos += 4;
                  return (char)result;
               }
            default:
               throw this.syntaxError("Invalid escape sequence");
         }
      }
   }

   private MalformedJsonException syntaxError(String message) throws MalformedJsonException {
      throw new MalformedJsonException(message + this.locationString() + "\nSee " + TroubleshootingGuide.createUrl("malformed-json"));
   }

   private IllegalStateException unexpectedTokenError(String expected) throws IOException {
      JsonToken peeked = this.peek();
      String troubleshootingId = peeked == JsonToken.NULL ? "adapter-not-null-safe" : "unexpected-json-structure";
      return new IllegalStateException(
         "Expected " + expected + " but was " + this.peek() + this.locationString() + "\nSee " + TroubleshootingGuide.createUrl(troubleshootingId)
      );
   }

   private void consumeNonExecutePrefix() throws IOException {
      int unused = this.nextNonWhitespace(true);
      this.pos--;
      if (this.pos + 5 <= this.limit || this.fillBuffer(5)) {
         int p = this.pos;
         char[] buf = this.buffer;
         if (buf[p] == ')' && buf[p + 1] == ']' && buf[p + 2] == '}' && buf[p + 3] == '\'' && buf[p + 4] == '\n') {
            this.pos += 5;
         }
      }
   }

   static {
      JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess() {
         @Override
         public void promoteNameToValue(JsonReader reader) throws IOException {
            if (reader instanceof JsonTreeReader) {
               ((JsonTreeReader)reader).promoteNameToValue();
            } else {
               int p = reader.peeked;
               if (p == 0) {
                  p = reader.doPeek();
               }

               if (p == 13) {
                  reader.peeked = 9;
               } else if (p == 12) {
                  reader.peeked = 8;
               } else {
                  if (p != 14) {
                     throw reader.unexpectedTokenError("a name");
                  }

                  reader.peeked = 10;
               }
            }
         }
      };
   }
}
