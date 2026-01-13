package org.bson.json;

import java.io.Reader;
import org.bson.BsonRegularExpression;

class JsonScanner {
   private final JsonBuffer buffer;

   JsonScanner(JsonBuffer buffer) {
      this.buffer = buffer;
   }

   JsonScanner(String json) {
      this(new JsonStringBuffer(json));
   }

   JsonScanner(Reader reader) {
      this(new JsonStreamBuffer(reader));
   }

   public void reset(int markPos) {
      this.buffer.reset(markPos);
   }

   public int mark() {
      return this.buffer.mark();
   }

   public void discard(int markPos) {
      this.buffer.discard(markPos);
   }

   public JsonToken nextToken() {
      int c = this.buffer.read();

      while (c != -1 && Character.isWhitespace(c)) {
         c = this.buffer.read();
      }

      if (c == -1) {
         return new JsonToken(JsonTokenType.END_OF_FILE, "<eof>");
      } else {
         switch (c) {
            case 34:
            case 39:
               return this.scanString((char)c);
            case 40:
               return new JsonToken(JsonTokenType.LEFT_PAREN, "(");
            case 41:
               return new JsonToken(JsonTokenType.RIGHT_PAREN, ")");
            case 44:
               return new JsonToken(JsonTokenType.COMMA, ",");
            case 47:
               return this.scanRegularExpression();
            case 58:
               return new JsonToken(JsonTokenType.COLON, ":");
            case 91:
               return new JsonToken(JsonTokenType.BEGIN_ARRAY, "[");
            case 93:
               return new JsonToken(JsonTokenType.END_ARRAY, "]");
            case 123:
               return new JsonToken(JsonTokenType.BEGIN_OBJECT, "{");
            case 125:
               return new JsonToken(JsonTokenType.END_OBJECT, "}");
            default:
               if (c == 45 || Character.isDigit(c)) {
                  return this.scanNumber((char)c);
               } else if (c != 36 && c != 95 && !Character.isLetter(c)) {
                  int position = this.buffer.getPosition();
                  this.buffer.unread(c);
                  throw new JsonParseException("Invalid JSON input. Position: %d. Character: '%c'.", position, c);
               } else {
                  return this.scanUnquotedString((char)c);
               }
         }
      }
   }

   private JsonToken scanRegularExpression() {
      StringBuilder patternBuilder = new StringBuilder();
      StringBuilder optionsBuilder = new StringBuilder();
      JsonScanner.RegularExpressionState state = JsonScanner.RegularExpressionState.IN_PATTERN;

      while (true) {
         int c;
         c = this.buffer.read();
         label33:
         switch (state) {
            case IN_PATTERN:
               switch (c) {
                  case -1:
                     state = JsonScanner.RegularExpressionState.INVALID;
                     break label33;
                  case 47:
                     state = JsonScanner.RegularExpressionState.IN_OPTIONS;
                     break label33;
                  case 92:
                     state = JsonScanner.RegularExpressionState.IN_ESCAPE_SEQUENCE;
                     break label33;
                  default:
                     state = JsonScanner.RegularExpressionState.IN_PATTERN;
                     break label33;
               }
            case IN_ESCAPE_SEQUENCE:
               state = JsonScanner.RegularExpressionState.IN_PATTERN;
               break;
            case IN_OPTIONS:
               switch (c) {
                  case -1:
                  case 41:
                  case 44:
                  case 93:
                  case 125:
                     state = JsonScanner.RegularExpressionState.DONE;
                     break;
                  case 105:
                  case 109:
                  case 115:
                  case 120:
                     state = JsonScanner.RegularExpressionState.IN_OPTIONS;
                     break;
                  default:
                     if (Character.isWhitespace(c)) {
                        state = JsonScanner.RegularExpressionState.DONE;
                     } else {
                        state = JsonScanner.RegularExpressionState.INVALID;
                     }
               }
         }

         switch (state) {
            case DONE:
               this.buffer.unread(c);
               BsonRegularExpression regex = new BsonRegularExpression(patternBuilder.toString(), optionsBuilder.toString());
               return new JsonToken(JsonTokenType.REGULAR_EXPRESSION, regex);
            case INVALID:
               throw new JsonParseException("Invalid JSON regular expression. Position: %d.", this.buffer.getPosition());
         }

         switch (state) {
            case IN_OPTIONS:
               if (c != 47) {
                  optionsBuilder.append((char)c);
               }
               break;
            default:
               patternBuilder.append((char)c);
         }
      }
   }

   private JsonToken scanUnquotedString(char firstChar) {
      StringBuilder sb = new StringBuilder();
      sb.append(firstChar);

      int c;
      for (c = this.buffer.read(); c == 36 || c == 95 || Character.isLetterOrDigit(c); c = this.buffer.read()) {
         sb.append((char)c);
      }

      this.buffer.unread(c);
      String lexeme = sb.toString();
      return new JsonToken(JsonTokenType.UNQUOTED_STRING, lexeme);
   }

   private JsonToken scanNumber(char firstChar) {
      StringBuilder sb = new StringBuilder();
      sb.append(firstChar);
      JsonScanner.NumberState state;
      switch (firstChar) {
         case '-':
            state = JsonScanner.NumberState.SAW_LEADING_MINUS;
            break;
         case '0':
            state = JsonScanner.NumberState.SAW_LEADING_ZERO;
            break;
         default:
            state = JsonScanner.NumberState.SAW_INTEGER_DIGITS;
      }

      JsonTokenType type = JsonTokenType.INT64;

      while (true) {
         int c;
         c = this.buffer.read();
         label111:
         switch (state) {
            case SAW_LEADING_MINUS:
               switch (c) {
                  case 48:
                     state = JsonScanner.NumberState.SAW_LEADING_ZERO;
                     break label111;
                  case 73:
                     state = JsonScanner.NumberState.SAW_MINUS_I;
                     break label111;
                  default:
                     if (Character.isDigit(c)) {
                        state = JsonScanner.NumberState.SAW_INTEGER_DIGITS;
                     } else {
                        state = JsonScanner.NumberState.INVALID;
                     }
                     break label111;
               }
            case SAW_LEADING_ZERO:
               switch (c) {
                  case -1:
                  case 41:
                  case 44:
                  case 93:
                  case 125:
                     state = JsonScanner.NumberState.DONE;
                     break label111;
                  case 46:
                     state = JsonScanner.NumberState.SAW_DECIMAL_POINT;
                     break label111;
                  case 69:
                  case 101:
                     state = JsonScanner.NumberState.SAW_EXPONENT_LETTER;
                     break label111;
                  default:
                     if (Character.isDigit(c)) {
                        state = JsonScanner.NumberState.SAW_INTEGER_DIGITS;
                     } else if (Character.isWhitespace(c)) {
                        state = JsonScanner.NumberState.DONE;
                     } else {
                        state = JsonScanner.NumberState.INVALID;
                     }
                     break label111;
               }
            case SAW_INTEGER_DIGITS:
               switch (c) {
                  case -1:
                  case 41:
                  case 44:
                  case 93:
                  case 125:
                     state = JsonScanner.NumberState.DONE;
                     break label111;
                  case 46:
                     state = JsonScanner.NumberState.SAW_DECIMAL_POINT;
                     break label111;
                  case 69:
                  case 101:
                     state = JsonScanner.NumberState.SAW_EXPONENT_LETTER;
                     break label111;
                  default:
                     if (Character.isDigit(c)) {
                        state = JsonScanner.NumberState.SAW_INTEGER_DIGITS;
                     } else if (Character.isWhitespace(c)) {
                        state = JsonScanner.NumberState.DONE;
                     } else {
                        state = JsonScanner.NumberState.INVALID;
                     }
                     break label111;
               }
            case SAW_DECIMAL_POINT:
               type = JsonTokenType.DOUBLE;
               if (Character.isDigit(c)) {
                  state = JsonScanner.NumberState.SAW_FRACTION_DIGITS;
               } else {
                  state = JsonScanner.NumberState.INVALID;
               }
               break;
            case SAW_FRACTION_DIGITS:
               switch (c) {
                  case -1:
                  case 41:
                  case 44:
                  case 93:
                  case 125:
                     state = JsonScanner.NumberState.DONE;
                     break label111;
                  case 69:
                  case 101:
                     state = JsonScanner.NumberState.SAW_EXPONENT_LETTER;
                     break label111;
                  default:
                     if (Character.isDigit(c)) {
                        state = JsonScanner.NumberState.SAW_FRACTION_DIGITS;
                     } else if (Character.isWhitespace(c)) {
                        state = JsonScanner.NumberState.DONE;
                     } else {
                        state = JsonScanner.NumberState.INVALID;
                     }
                     break label111;
               }
            case SAW_EXPONENT_LETTER:
               type = JsonTokenType.DOUBLE;
               switch (c) {
                  case 43:
                  case 45:
                     state = JsonScanner.NumberState.SAW_EXPONENT_SIGN;
                     break label111;
                  default:
                     if (Character.isDigit(c)) {
                        state = JsonScanner.NumberState.SAW_EXPONENT_DIGITS;
                     } else {
                        state = JsonScanner.NumberState.INVALID;
                     }
                     break label111;
               }
            case SAW_EXPONENT_SIGN:
               if (Character.isDigit(c)) {
                  state = JsonScanner.NumberState.SAW_EXPONENT_DIGITS;
               } else {
                  state = JsonScanner.NumberState.INVALID;
               }
               break;
            case SAW_EXPONENT_DIGITS:
               switch (c) {
                  case 41:
                  case 44:
                  case 93:
                  case 125:
                     state = JsonScanner.NumberState.DONE;
                     break label111;
                  default:
                     if (Character.isDigit(c)) {
                        state = JsonScanner.NumberState.SAW_EXPONENT_DIGITS;
                     } else if (Character.isWhitespace(c)) {
                        state = JsonScanner.NumberState.DONE;
                     } else {
                        state = JsonScanner.NumberState.INVALID;
                     }
                     break label111;
               }
            case SAW_MINUS_I:
               boolean sawMinusInfinity = true;
               char[] nfinity = new char[]{'n', 'f', 'i', 'n', 'i', 't', 'y'};

               for (int i = 0; i < nfinity.length; i++) {
                  if (c != nfinity[i]) {
                     sawMinusInfinity = false;
                     break;
                  }

                  sb.append((char)c);
                  c = this.buffer.read();
               }

               if (sawMinusInfinity) {
                  type = JsonTokenType.DOUBLE;
                  switch (c) {
                     case -1:
                     case 41:
                     case 44:
                     case 93:
                     case 125:
                        state = JsonScanner.NumberState.DONE;
                        break;
                     default:
                        if (Character.isWhitespace(c)) {
                           state = JsonScanner.NumberState.DONE;
                        } else {
                           state = JsonScanner.NumberState.INVALID;
                        }
                  }
               } else {
                  state = JsonScanner.NumberState.INVALID;
               }
         }

         switch (state) {
            case INVALID:
               throw new JsonParseException("Invalid JSON number");
            case DONE:
               this.buffer.unread(c);
               String lexeme = sb.toString();
               if (type == JsonTokenType.DOUBLE) {
                  return new JsonToken(JsonTokenType.DOUBLE, Double.parseDouble(lexeme));
               }

               long value = Long.parseLong(lexeme);
               if (value >= -2147483648L && value <= 2147483647L) {
                  return new JsonToken(JsonTokenType.INT32, (int)value);
               }

               return new JsonToken(JsonTokenType.INT64, value);
            default:
               sb.append((char)c);
         }
      }
   }

   private JsonToken scanString(char quoteCharacter) {
      StringBuilder sb = new StringBuilder();

      int c;
      do {
         c = this.buffer.read();
         switch (c) {
            case 92:
               c = this.buffer.read();
               switch (c) {
                  case 34:
                     sb.append('"');
                     continue;
                  case 39:
                     sb.append('\'');
                     continue;
                  case 47:
                     sb.append('/');
                     continue;
                  case 92:
                     sb.append('\\');
                     continue;
                  case 98:
                     sb.append('\b');
                     continue;
                  case 102:
                     sb.append('\f');
                     continue;
                  case 110:
                     sb.append('\n');
                     continue;
                  case 114:
                     sb.append('\r');
                     continue;
                  case 116:
                     sb.append('\t');
                     continue;
                  case 117:
                     int u1 = this.buffer.read();
                     int u2 = this.buffer.read();
                     int u3 = this.buffer.read();
                     int u4 = this.buffer.read();
                     if (u4 != -1) {
                        String hex = new String(new char[]{(char)u1, (char)u2, (char)u3, (char)u4});
                        sb.append((char)Integer.parseInt(hex, 16));
                     }
                     continue;
                  default:
                     throw new JsonParseException("Invalid escape sequence in JSON string '\\%c'.", c);
               }
            default:
               if (c == quoteCharacter) {
                  return new JsonToken(JsonTokenType.STRING, sb.toString());
               }

               if (c != -1) {
                  sb.append((char)c);
               }
         }
      } while (c != -1);

      throw new JsonParseException("End of file in JSON string.");
   }

   private static enum NumberState {
      SAW_LEADING_MINUS,
      SAW_LEADING_ZERO,
      SAW_INTEGER_DIGITS,
      SAW_DECIMAL_POINT,
      SAW_FRACTION_DIGITS,
      SAW_EXPONENT_LETTER,
      SAW_EXPONENT_SIGN,
      SAW_EXPONENT_DIGITS,
      SAW_MINUS_I,
      DONE,
      INVALID;
   }

   private static enum RegularExpressionState {
      IN_PATTERN,
      IN_ESCAPE_SEQUENCE,
      IN_OPTIONS,
      DONE,
      INVALID;
   }
}
