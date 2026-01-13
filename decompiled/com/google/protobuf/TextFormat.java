package com.google.protobuf;

import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.logging.Logger;

public final class TextFormat {
   private static final Logger logger = Logger.getLogger(TextFormat.class.getName());
   private static final String DEBUG_STRING_SILENT_MARKER = " \t ";
   private static final String ENABLE_INSERT_SILENT_MARKER_ENV_NAME = "SILENT_MARKER_INSERTION_ENABLED";
   private static final boolean ENABLE_INSERT_SILENT_MARKER = System.getenv().getOrDefault("SILENT_MARKER_INSERTION_ENABLED", "false").equals("true");
   private static final String REDACTED_MARKER = "[REDACTED]";
   private static final TextFormat.Parser PARSER = TextFormat.Parser.newBuilder().build();

   private TextFormat() {
   }

   @Deprecated
   public static String shortDebugString(final MessageOrBuilder message) {
      return printer().emittingSingleLine(true).printToString(message, TextFormat.Printer.FieldReporterLevel.SHORT_DEBUG_STRING);
   }

   public static void printUnknownFieldValue(final int tag, final Object value, final Appendable output) throws IOException {
      printUnknownFieldValue(tag, value, setSingleLineOutput(output, false), false);
   }

   private static void printUnknownFieldValue(final int tag, final Object value, final TextFormat.TextGenerator generator, boolean redact) throws IOException {
      switch (WireFormat.getTagWireType(tag)) {
         case 0:
            generator.print(unsignedToString((Long)value));
            break;
         case 1:
            generator.print(String.format((Locale)null, "0x%016x", (Long)value));
            break;
         case 2:
            try {
               UnknownFieldSet message = UnknownFieldSet.parseFrom((ByteString)value);
               generator.print("{");
               generator.eol();
               generator.indent();
               TextFormat.Printer.printUnknownFields(message, generator, redact);
               generator.outdent();
               generator.print("}");
            } catch (InvalidProtocolBufferException var5) {
               generator.print("\"");
               generator.print(escapeBytes((ByteString)value));
               generator.print("\"");
            }
            break;
         case 3:
            TextFormat.Printer.printUnknownFields((UnknownFieldSet)value, generator, redact);
            break;
         case 4:
         default:
            throw new IllegalArgumentException("Bad tag: " + tag);
         case 5:
            generator.print(String.format((Locale)null, "0x%08x", (Integer)value));
      }
   }

   public static TextFormat.Printer printer() {
      return TextFormat.Printer.DEFAULT_TEXT_FORMAT;
   }

   public static TextFormat.Printer debugFormatPrinter() {
      return TextFormat.Printer.DEFAULT_DEBUG_FORMAT;
   }

   public static TextFormat.Printer defaultFormatPrinter() {
      return TextFormat.Printer.DEFAULT_FORMAT;
   }

   @Deprecated
   @InlineMe(replacement = "TextFormat.printer().print(message, output)", imports = "com.google.protobuf.TextFormat")
   public static void print(final MessageOrBuilder message, final Appendable output) throws IOException {
      printer().print(message, output);
   }

   @Deprecated
   public static void printUnicode(final MessageOrBuilder message, final Appendable output) throws IOException {
      printer().escapingNonAscii(false).print(message, output, TextFormat.Printer.FieldReporterLevel.PRINT_UNICODE);
   }

   @Deprecated
   public static String printToString(final MessageOrBuilder message) {
      return printer().printToString(message, TextFormat.Printer.FieldReporterLevel.TEXTFORMAT_PRINT_TO_STRING);
   }

   @Deprecated
   public static String printToUnicodeString(final MessageOrBuilder message) {
      return printer().escapingNonAscii(false).printToString(message, TextFormat.Printer.FieldReporterLevel.PRINT_UNICODE);
   }

   @Deprecated
   @InlineMe(replacement = "TextFormat.printer().printFieldValue(field, value, output)", imports = "com.google.protobuf.TextFormat")
   public static void printFieldValue(final Descriptors.FieldDescriptor field, final Object value, final Appendable output) throws IOException {
      printer().printFieldValue(field, value, output);
   }

   public static String unsignedToString(final int value) {
      return value >= 0 ? Integer.toString(value) : Long.toString(value & 4294967295L);
   }

   public static String unsignedToString(final long value) {
      return value >= 0L ? Long.toString(value) : BigInteger.valueOf(value & Long.MAX_VALUE).setBit(63).toString();
   }

   private static TextFormat.TextGenerator setSingleLineOutput(Appendable output, boolean singleLine) {
      return new TextFormat.TextGenerator(output, singleLine, null, TextFormat.Printer.FieldReporterLevel.TEXT_GENERATOR, false);
   }

   private static TextFormat.TextGenerator setSingleLineOutput(
      Appendable output,
      boolean singleLine,
      Descriptors.Descriptor rootMessageType,
      TextFormat.Printer.FieldReporterLevel fieldReporterLevel,
      boolean shouldEmitSilentMarker
   ) {
      return new TextFormat.TextGenerator(output, singleLine, rootMessageType, fieldReporterLevel, shouldEmitSilentMarker);
   }

   public static TextFormat.Parser getParser() {
      return PARSER;
   }

   public static void merge(final Readable input, final Message.Builder builder) throws IOException {
      PARSER.merge(input, builder);
   }

   public static void merge(final CharSequence input, final Message.Builder builder) throws TextFormat.ParseException {
      PARSER.merge(input, builder);
   }

   public static <T extends Message> T parse(final CharSequence input, final Class<T> protoClass) throws TextFormat.ParseException {
      Message.Builder builder = Internal.<T>getDefaultInstance(protoClass).newBuilderForType();
      merge(input, builder);
      return (T)builder.build();
   }

   public static void merge(final Readable input, final ExtensionRegistry extensionRegistry, final Message.Builder builder) throws IOException {
      PARSER.merge(input, extensionRegistry, builder);
   }

   public static void merge(final CharSequence input, final ExtensionRegistry extensionRegistry, final Message.Builder builder) throws TextFormat.ParseException {
      PARSER.merge(input, extensionRegistry, builder);
   }

   public static <T extends Message> T parse(final CharSequence input, final ExtensionRegistry extensionRegistry, final Class<T> protoClass) throws TextFormat.ParseException {
      Message.Builder builder = Internal.<T>getDefaultInstance(protoClass).newBuilderForType();
      merge(input, extensionRegistry, builder);
      return (T)builder.build();
   }

   public static String escapeBytes(ByteString input) {
      return TextFormatEscaper.escapeBytes(input);
   }

   public static String escapeBytes(byte[] input) {
      return TextFormatEscaper.escapeBytes(input);
   }

   public static ByteString unescapeBytes(CharSequence charString) throws TextFormat.InvalidEscapeSequenceException {
      ByteString input = ByteString.copyFromUtf8(charString.toString());
      byte[] result = new byte[input.size()];
      int pos = 0;

      for (int i = 0; i < input.size(); i++) {
         byte c = input.byteAt(i);
         if (c != 92) {
            result[pos++] = c;
         } else {
            if (i + 1 >= input.size()) {
               throw new TextFormat.InvalidEscapeSequenceException("Invalid escape sequence: '\\' at end of string.");
            }

            c = input.byteAt(++i);
            if (isOctal(c)) {
               int code = digitValue(c);
               if (i + 1 < input.size() && isOctal(input.byteAt(i + 1))) {
                  code = code * 8 + digitValue(input.byteAt(++i));
               }

               if (i + 1 < input.size() && isOctal(input.byteAt(i + 1))) {
                  code = code * 8 + digitValue(input.byteAt(++i));
               }

               result[pos++] = (byte)code;
            } else {
               switch (c) {
                  case 34:
                     result[pos++] = 34;
                     break;
                  case 39:
                     result[pos++] = 39;
                     break;
                  case 63:
                     result[pos++] = 63;
                     break;
                  case 85:
                     if (++i + 7 >= input.size()) {
                        throw new TextFormat.InvalidEscapeSequenceException("Invalid escape sequence: '\\U' with too few hex chars");
                     }

                     int codepoint = 0;

                     for (int offset = i; offset < i + 8; offset++) {
                        byte b = input.byteAt(offset);
                        if (!isHex(b)) {
                           throw new TextFormat.InvalidEscapeSequenceException("Invalid escape sequence: '\\U' with too few hex chars");
                        }

                        codepoint = codepoint << 4 | digitValue(b);
                     }

                     if (!Character.isValidCodePoint(codepoint)) {
                        throw new TextFormat.InvalidEscapeSequenceException(
                           "Invalid escape sequence: '\\U" + input.substring(i, i + 8).toStringUtf8() + "' is not a valid code point value"
                        );
                     }

                     UnicodeBlock unicodeBlock = UnicodeBlock.of(codepoint);
                     if (unicodeBlock != null
                        && (
                           unicodeBlock.equals(UnicodeBlock.LOW_SURROGATES)
                              || unicodeBlock.equals(UnicodeBlock.HIGH_SURROGATES)
                              || unicodeBlock.equals(UnicodeBlock.HIGH_PRIVATE_USE_SURROGATES)
                        )) {
                        throw new TextFormat.InvalidEscapeSequenceException(
                           "Invalid escape sequence: '\\U" + input.substring(i, i + 8).toStringUtf8() + "' refers to a surrogate code unit"
                        );
                     }

                     int[] codepoints = new int[]{codepoint};
                     byte[] chUtf8 = new String(codepoints, 0, 1).getBytes(Internal.UTF_8);
                     System.arraycopy(chUtf8, 0, result, pos, chUtf8.length);
                     pos += chUtf8.length;
                     i += 7;
                     break;
                  case 92:
                     result[pos++] = 92;
                     break;
                  case 97:
                     result[pos++] = 7;
                     break;
                  case 98:
                     result[pos++] = 8;
                     break;
                  case 102:
                     result[pos++] = 12;
                     break;
                  case 110:
                     result[pos++] = 10;
                     break;
                  case 114:
                     result[pos++] = 13;
                     break;
                  case 116:
                     result[pos++] = 9;
                     break;
                  case 117:
                     i++;
                     if (i + 3 >= input.size()
                        || !isHex(input.byteAt(i))
                        || !isHex(input.byteAt(i + 1))
                        || !isHex(input.byteAt(i + 2))
                        || !isHex(input.byteAt(i + 3))) {
                        throw new TextFormat.InvalidEscapeSequenceException("Invalid escape sequence: '\\u' with too few hex chars");
                     }

                     char ch = (char)(
                        digitValue(input.byteAt(i)) << 12
                           | digitValue(input.byteAt(i + 1)) << 8
                           | digitValue(input.byteAt(i + 2)) << 4
                           | digitValue(input.byteAt(i + 3))
                     );
                     if (ch >= '\ud800' && ch <= '\udfff') {
                        throw new TextFormat.InvalidEscapeSequenceException("Invalid escape sequence: '\\u' refers to a surrogate");
                     }

                     byte[] chUtf8 = Character.toString(ch).getBytes(Internal.UTF_8);
                     System.arraycopy(chUtf8, 0, result, pos, chUtf8.length);
                     pos += chUtf8.length;
                     i += 3;
                     break;
                  case 118:
                     result[pos++] = 11;
                     break;
                  case 120:
                     int codex = 0;
                     if (i + 1 >= input.size() || !isHex(input.byteAt(i + 1))) {
                        throw new TextFormat.InvalidEscapeSequenceException("Invalid escape sequence: '\\x' with no digits");
                     }

                     codex = digitValue(input.byteAt(++i));
                     if (i + 1 < input.size() && isHex(input.byteAt(i + 1))) {
                        codex = codex * 16 + digitValue(input.byteAt(++i));
                     }

                     result[pos++] = (byte)codex;
                     break;
                  default:
                     throw new TextFormat.InvalidEscapeSequenceException("Invalid escape sequence: '\\" + (char)c + '\'');
               }
            }
         }
      }

      return result.length == pos ? ByteString.wrap(result) : ByteString.copyFrom(result, 0, pos);
   }

   static String escapeText(final String input) {
      return escapeBytes(ByteString.copyFromUtf8(input));
   }

   public static String escapeDoubleQuotesAndBackslashes(final String input) {
      return TextFormatEscaper.escapeDoubleQuotesAndBackslashes(input);
   }

   static String unescapeText(final String input) throws TextFormat.InvalidEscapeSequenceException {
      return unescapeBytes(input).toStringUtf8();
   }

   private static boolean isOctal(final byte c) {
      return 48 <= c && c <= 55;
   }

   private static boolean isHex(final byte c) {
      return 48 <= c && c <= 57 || 97 <= c && c <= 102 || 65 <= c && c <= 70;
   }

   private static int digitValue(final byte c) {
      if (48 <= c && c <= 57) {
         return c - 48;
      } else {
         return 97 <= c && c <= 122 ? c - 97 + 10 : c - 65 + 10;
      }
   }

   static int parseInt32(final String text) throws NumberFormatException {
      return (int)parseInteger(text, true, false);
   }

   static int parseUInt32(final String text) throws NumberFormatException {
      return (int)parseInteger(text, false, false);
   }

   static long parseInt64(final String text) throws NumberFormatException {
      return parseInteger(text, true, true);
   }

   static long parseUInt64(final String text) throws NumberFormatException {
      return parseInteger(text, false, true);
   }

   private static long parseInteger(final String text, final boolean isSigned, final boolean isLong) throws NumberFormatException {
      int pos = 0;
      boolean negative = false;
      if (text.startsWith("-", pos)) {
         if (!isSigned) {
            throw new NumberFormatException("Number must be positive: " + text);
         }

         pos++;
         negative = true;
      }

      int radix = 10;
      if (text.startsWith("0x", pos)) {
         pos += 2;
         radix = 16;
      } else if (text.startsWith("0", pos)) {
         radix = 8;
      }

      String numberText = text.substring(pos);
      long result = 0L;
      if (numberText.length() < 16) {
         result = Long.parseLong(numberText, radix);
         if (negative) {
            result = -result;
         }

         if (!isLong) {
            if (isSigned) {
               if (result > 2147483647L || result < -2147483648L) {
                  throw new NumberFormatException("Number out of range for 32-bit signed integer: " + text);
               }
            } else if (result >= 4294967296L || result < 0L) {
               throw new NumberFormatException("Number out of range for 32-bit unsigned integer: " + text);
            }
         }
      } else {
         BigInteger bigValue = new BigInteger(numberText, radix);
         if (negative) {
            bigValue = bigValue.negate();
         }

         if (!isLong) {
            if (isSigned) {
               if (bigValue.bitLength() > 31) {
                  throw new NumberFormatException("Number out of range for 32-bit signed integer: " + text);
               }
            } else if (bigValue.bitLength() > 32) {
               throw new NumberFormatException("Number out of range for 32-bit unsigned integer: " + text);
            }
         } else if (isSigned) {
            if (bigValue.bitLength() > 63) {
               throw new NumberFormatException("Number out of range for 64-bit signed integer: " + text);
            }
         } else if (bigValue.bitLength() > 64) {
            throw new NumberFormatException("Number out of range for 64-bit unsigned integer: " + text);
         }

         result = bigValue.longValue();
      }

      return result;
   }

   public static class InvalidEscapeSequenceException extends IOException {
      private static final long serialVersionUID = -8164033650142593304L;

      InvalidEscapeSequenceException(final String description) {
         super(description);
      }
   }

   public static class ParseException extends IOException {
      private static final long serialVersionUID = 3196188060225107702L;
      private final int line;
      private final int column;

      public ParseException(final String message) {
         this(-1, -1, message);
      }

      public ParseException(final int line, final int column, final String message) {
         super(Integer.toString(line) + ":" + column + ": " + message);
         this.line = line;
         this.column = column;
      }

      public int getLine() {
         return this.line;
      }

      public int getColumn() {
         return this.column;
      }
   }

   public static class Parser {
      private final TypeRegistry typeRegistry;
      private final boolean allowUnknownFields;
      private final boolean allowUnknownEnumValues;
      private final boolean allowUnknownExtensions;
      private final TextFormat.Parser.SingularOverwritePolicy singularOverwritePolicy;
      private TextFormatParseInfoTree.Builder parseInfoTreeBuilder;
      private final int recursionLimit;
      private static final int BUFFER_SIZE = 4096;

      private void detectSilentMarker(TextFormat.Tokenizer tokenizer, Descriptors.Descriptor immediateMessageType, String fieldName) {
      }

      private Parser(
         TypeRegistry typeRegistry,
         boolean allowUnknownFields,
         boolean allowUnknownEnumValues,
         boolean allowUnknownExtensions,
         TextFormat.Parser.SingularOverwritePolicy singularOverwritePolicy,
         TextFormatParseInfoTree.Builder parseInfoTreeBuilder,
         int recursionLimit
      ) {
         this.typeRegistry = typeRegistry;
         this.allowUnknownFields = allowUnknownFields;
         this.allowUnknownEnumValues = allowUnknownEnumValues;
         this.allowUnknownExtensions = allowUnknownExtensions;
         this.singularOverwritePolicy = singularOverwritePolicy;
         this.parseInfoTreeBuilder = parseInfoTreeBuilder;
         this.recursionLimit = recursionLimit;
      }

      public static TextFormat.Parser.Builder newBuilder() {
         return new TextFormat.Parser.Builder();
      }

      public void merge(final Readable input, final Message.Builder builder) throws IOException {
         this.merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
      }

      public void merge(final CharSequence input, final Message.Builder builder) throws TextFormat.ParseException {
         this.merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
      }

      public void merge(final Readable input, final ExtensionRegistry extensionRegistry, final Message.Builder builder) throws IOException {
         this.merge(toStringBuilder(input), extensionRegistry, builder);
      }

      private static StringBuilder toStringBuilder(final Readable input) throws IOException {
         StringBuilder text = new StringBuilder();
         CharBuffer buffer = CharBuffer.allocate(4096);

         while (true) {
            int n = input.read(buffer);
            if (n == -1) {
               return text;
            }

            Java8Compatibility.flip(buffer);
            text.append(buffer, 0, n);
         }
      }

      private void checkUnknownFields(final List<TextFormat.Parser.UnknownField> unknownFields) throws TextFormat.ParseException {
         if (!unknownFields.isEmpty()) {
            StringBuilder msg = new StringBuilder("Input contains unknown fields and/or extensions:");

            for (TextFormat.Parser.UnknownField field : unknownFields) {
               msg.append('\n').append(field.message);
            }

            if (this.allowUnknownFields) {
               TextFormat.logger.warning(msg.toString());
            } else {
               int firstErrorIndex = 0;
               if (this.allowUnknownExtensions) {
                  boolean allUnknownExtensions = true;

                  for (TextFormat.Parser.UnknownField field : unknownFields) {
                     if (field.type == TextFormat.Parser.UnknownField.Type.FIELD) {
                        allUnknownExtensions = false;
                        break;
                     }

                     firstErrorIndex++;
                  }

                  if (allUnknownExtensions) {
                     TextFormat.logger.warning(msg.toString());
                     return;
                  }
               }

               String[] lineColumn = unknownFields.get(firstErrorIndex).message.split(":");
               throw new TextFormat.ParseException(Integer.parseInt(lineColumn[0]), Integer.parseInt(lineColumn[1]), msg.toString());
            }
         }
      }

      public void merge(final CharSequence input, final ExtensionRegistry extensionRegistry, final Message.Builder builder) throws TextFormat.ParseException {
         TextFormat.Tokenizer tokenizer = new TextFormat.Tokenizer(input);
         MessageReflection.BuilderAdapter target = new MessageReflection.BuilderAdapter(builder);
         List<TextFormat.Parser.UnknownField> unknownFields = new ArrayList<>();

         while (!tokenizer.atEnd()) {
            this.mergeField(tokenizer, extensionRegistry, target, unknownFields, this.recursionLimit);
         }

         this.checkUnknownFields(unknownFields);
      }

      private void mergeField(
         final TextFormat.Tokenizer tokenizer,
         final ExtensionRegistry extensionRegistry,
         final MessageReflection.MergeTarget target,
         List<TextFormat.Parser.UnknownField> unknownFields,
         int recursionLimit
      ) throws TextFormat.ParseException {
         this.mergeField(tokenizer, extensionRegistry, target, this.parseInfoTreeBuilder, unknownFields, recursionLimit);
      }

      private void mergeField(
         final TextFormat.Tokenizer tokenizer,
         final ExtensionRegistry extensionRegistry,
         final MessageReflection.MergeTarget target,
         TextFormatParseInfoTree.Builder parseTreeBuilder,
         List<TextFormat.Parser.UnknownField> unknownFields,
         int recursionLimit
      ) throws TextFormat.ParseException {
         Descriptors.FieldDescriptor field = null;
         int startLine = tokenizer.getLine();
         int startColumn = tokenizer.getColumn();
         Descriptors.Descriptor type = target.getDescriptorForType();
         ExtensionRegistry.ExtensionInfo extension = null;
         if ("google.protobuf.Any".equals(type.getFullName()) && tokenizer.tryConsume("[")) {
            if (recursionLimit < 1) {
               throw tokenizer.parseException("Message is nested too deep");
            } else {
               this.mergeAnyFieldValue(tokenizer, extensionRegistry, target, parseTreeBuilder, unknownFields, type, recursionLimit - 1);
            }
         } else {
            String name;
            if (tokenizer.tryConsume("[")) {
               StringBuilder nameBuilder = new StringBuilder(tokenizer.consumeIdentifier());

               while (tokenizer.tryConsume(".")) {
                  nameBuilder.append('.');
                  nameBuilder.append(tokenizer.consumeIdentifier());
               }

               name = nameBuilder.toString();
               extension = target.findExtensionByName(extensionRegistry, name);
               if (extension == null) {
                  String message = tokenizer.getPreviousLine() + 1 + ":" + (tokenizer.getPreviousColumn() + 1) + ":\t" + type.getFullName() + ".[" + name + "]";
                  unknownFields.add(new TextFormat.Parser.UnknownField(message, TextFormat.Parser.UnknownField.Type.EXTENSION));
               } else {
                  if (extension.descriptor.getContainingType() != type) {
                     throw tokenizer.parseExceptionPreviousToken("Extension \"" + name + "\" does not extend message type \"" + type.getFullName() + "\".");
                  }

                  field = extension.descriptor;
               }

               tokenizer.consume("]");
            } else {
               name = tokenizer.consumeIdentifier();
               field = type.findFieldByName(name);
               if (field == null) {
                  String lowerName = name.toLowerCase(Locale.US);
                  field = type.findFieldByName(lowerName);
                  if (field != null && !field.isGroupLike()) {
                     field = null;
                  }

                  if (field != null && !field.getMessageType().getName().equals(name)) {
                     field = null;
                  }
               }

               if (field == null) {
                  String message = tokenizer.getPreviousLine() + 1 + ":" + (tokenizer.getPreviousColumn() + 1) + ":\t" + type.getFullName() + "." + name;
                  unknownFields.add(new TextFormat.Parser.UnknownField(message, TextFormat.Parser.UnknownField.Type.FIELD));
               }
            }

            if (field == null) {
               this.detectSilentMarker(tokenizer, type, name);
               this.guessFieldTypeAndSkip(tokenizer, type, recursionLimit);
            } else {
               if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                  this.detectSilentMarker(tokenizer, type, field.getFullName());
                  tokenizer.tryConsume(":");
                  if (parseTreeBuilder != null) {
                     TextFormatParseInfoTree.Builder childParseTreeBuilder = parseTreeBuilder.getBuilderForSubMessageField(field);
                     this.consumeFieldValues(tokenizer, extensionRegistry, target, field, extension, childParseTreeBuilder, unknownFields, recursionLimit);
                  } else {
                     this.consumeFieldValues(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields, recursionLimit);
                  }
               } else {
                  this.detectSilentMarker(tokenizer, type, field.getFullName());
                  tokenizer.consume(":");
                  this.consumeFieldValues(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields, recursionLimit);
               }

               if (parseTreeBuilder != null) {
                  parseTreeBuilder.setLocation(field, TextFormatParseLocation.create(startLine, startColumn));
               }

               if (!tokenizer.tryConsume(";")) {
                  tokenizer.tryConsume(",");
               }
            }
         }
      }

      private String consumeFullTypeName(TextFormat.Tokenizer tokenizer) throws TextFormat.ParseException {
         if (!tokenizer.tryConsume("[")) {
            return tokenizer.consumeIdentifier();
         } else {
            String name = tokenizer.consumeIdentifier();

            while (tokenizer.tryConsume(".")) {
               name = name + "." + tokenizer.consumeIdentifier();
            }

            if (tokenizer.tryConsume("/")) {
               name = name + "/" + tokenizer.consumeIdentifier();

               while (tokenizer.tryConsume(".")) {
                  name = name + "." + tokenizer.consumeIdentifier();
               }
            }

            tokenizer.consume("]");
            return name;
         }
      }

      private void consumeFieldValues(
         final TextFormat.Tokenizer tokenizer,
         final ExtensionRegistry extensionRegistry,
         final MessageReflection.MergeTarget target,
         final Descriptors.FieldDescriptor field,
         final ExtensionRegistry.ExtensionInfo extension,
         final TextFormatParseInfoTree.Builder parseTreeBuilder,
         List<TextFormat.Parser.UnknownField> unknownFields,
         int recursionLimit
      ) throws TextFormat.ParseException {
         if (field.isRepeated() && tokenizer.tryConsume("[")) {
            if (!tokenizer.tryConsume("]")) {
               while (true) {
                  this.consumeFieldValue(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields, recursionLimit);
                  if (tokenizer.tryConsume("]")) {
                     break;
                  }

                  tokenizer.consume(",");
               }
            }
         } else {
            this.consumeFieldValue(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields, recursionLimit);
         }
      }

      private void consumeFieldValue(
         final TextFormat.Tokenizer tokenizer,
         final ExtensionRegistry extensionRegistry,
         final MessageReflection.MergeTarget target,
         final Descriptors.FieldDescriptor field,
         final ExtensionRegistry.ExtensionInfo extension,
         final TextFormatParseInfoTree.Builder parseTreeBuilder,
         List<TextFormat.Parser.UnknownField> unknownFields,
         int recursionLimit
      ) throws TextFormat.ParseException {
         if (this.singularOverwritePolicy == TextFormat.Parser.SingularOverwritePolicy.FORBID_SINGULAR_OVERWRITES && !field.isRepeated()) {
            if (target.hasField(field)) {
               throw tokenizer.parseExceptionPreviousToken("Non-repeated field \"" + field.getFullName() + "\" cannot be overwritten.");
            }

            if (field.getContainingOneof() != null && target.hasOneof(field.getContainingOneof())) {
               Descriptors.OneofDescriptor oneof = field.getContainingOneof();
               throw tokenizer.parseExceptionPreviousToken(
                  "Field \""
                     + field.getFullName()
                     + "\" is specified along with field \""
                     + target.getOneofFieldDescriptor(oneof).getFullName()
                     + "\", another member of oneof \""
                     + oneof.getName()
                     + "\"."
               );
            }
         }

         Object value = null;
         if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            if (recursionLimit < 1) {
               throw tokenizer.parseException("Message is nested too deep");
            }

            String endToken;
            if (tokenizer.tryConsume("<")) {
               endToken = ">";
            } else {
               tokenizer.consume("{");
               endToken = "}";
            }

            Message defaultInstance = extension == null ? null : extension.defaultInstance;
            MessageReflection.MergeTarget subField = target.newMergeTargetForField(field, defaultInstance);

            while (!tokenizer.tryConsume(endToken)) {
               if (tokenizer.atEnd()) {
                  throw tokenizer.parseException("Expected \"" + endToken + "\".");
               }

               this.mergeField(tokenizer, extensionRegistry, subField, parseTreeBuilder, unknownFields, recursionLimit - 1);
            }

            value = subField.finish();
         } else {
            switch (field.getType()) {
               case INT32:
               case SINT32:
               case SFIXED32:
                  value = tokenizer.consumeInt32();
                  break;
               case INT64:
               case SINT64:
               case SFIXED64:
                  value = tokenizer.consumeInt64();
                  break;
               case BOOL:
                  value = tokenizer.consumeBoolean();
                  break;
               case FLOAT:
                  value = tokenizer.consumeFloat();
                  break;
               case DOUBLE:
                  value = tokenizer.consumeDouble();
                  break;
               case UINT32:
               case FIXED32:
                  value = tokenizer.consumeUInt32();
                  break;
               case UINT64:
               case FIXED64:
                  value = tokenizer.consumeUInt64();
                  break;
               case STRING:
                  value = tokenizer.consumeString();
                  break;
               case BYTES:
                  value = tokenizer.consumeByteString();
                  break;
               case ENUM:
                  Descriptors.EnumDescriptor enumType = field.getEnumType();
                  if (tokenizer.lookingAtInteger()) {
                     int number = tokenizer.consumeInt32();
                     value = enumType.isClosed() ? enumType.findValueByNumber(number) : enumType.findValueByNumberCreatingIfUnknown(number);
                     if (value == null) {
                        String unknownValueMsg = "Enum type \"" + enumType.getFullName() + "\" has no value with number " + number + '.';
                        if (this.allowUnknownEnumValues) {
                           TextFormat.logger.warning(unknownValueMsg);
                           return;
                        }

                        throw tokenizer.parseExceptionPreviousToken("Enum type \"" + enumType.getFullName() + "\" has no value with number " + number + '.');
                     }
                  } else {
                     String id = tokenizer.consumeIdentifier();
                     value = enumType.findValueByName(id);
                     if (value == null) {
                        String unknownValueMsg = "Enum type \"" + enumType.getFullName() + "\" has no value named \"" + id + "\".";
                        if (this.allowUnknownEnumValues) {
                           TextFormat.logger.warning(unknownValueMsg);
                           return;
                        }

                        throw tokenizer.parseExceptionPreviousToken(unknownValueMsg);
                     }
                  }
                  break;
               case MESSAGE:
               case GROUP:
                  throw new RuntimeException("Can't get here.");
            }
         }

         if (field.isRepeated()) {
            target.addRepeatedField(field, value);
         } else {
            target.setField(field, value);
         }
      }

      private void mergeAnyFieldValue(
         final TextFormat.Tokenizer tokenizer,
         final ExtensionRegistry extensionRegistry,
         MessageReflection.MergeTarget target,
         final TextFormatParseInfoTree.Builder parseTreeBuilder,
         List<TextFormat.Parser.UnknownField> unknownFields,
         Descriptors.Descriptor anyDescriptor,
         int recursionLimit
      ) throws TextFormat.ParseException {
         StringBuilder typeUrlBuilder = new StringBuilder();

         while (true) {
            typeUrlBuilder.append(tokenizer.consumeIdentifier());
            if (tokenizer.tryConsume("]")) {
               this.detectSilentMarker(tokenizer, anyDescriptor, typeUrlBuilder.toString());
               tokenizer.tryConsume(":");
               String anyEndToken;
               if (tokenizer.tryConsume("<")) {
                  anyEndToken = ">";
               } else {
                  tokenizer.consume("{");
                  anyEndToken = "}";
               }

               String typeUrl = typeUrlBuilder.toString();
               Descriptors.Descriptor contentType = null;

               try {
                  contentType = this.typeRegistry.getDescriptorForTypeUrl(typeUrl);
               } catch (InvalidProtocolBufferException var14) {
                  throw tokenizer.parseException("Invalid valid type URL. Found: " + typeUrl);
               }

               if (contentType == null) {
                  throw tokenizer.parseException(
                     "Unable to parse Any of type: " + typeUrl + ". Please make sure that the TypeRegistry contains the descriptors for the given types."
                  );
               } else {
                  Message.Builder contentBuilder = DynamicMessage.getDefaultInstance(contentType).newBuilderForType();
                  MessageReflection.BuilderAdapter contentTarget = new MessageReflection.BuilderAdapter(contentBuilder);

                  while (!tokenizer.tryConsume(anyEndToken)) {
                     this.mergeField(tokenizer, extensionRegistry, contentTarget, parseTreeBuilder, unknownFields, recursionLimit);
                  }

                  target.setField(anyDescriptor.findFieldByName("type_url"), typeUrlBuilder.toString());
                  target.setField(anyDescriptor.findFieldByName("value"), contentBuilder.build().toByteString());
                  return;
               }
            }

            if (tokenizer.tryConsume("/")) {
               typeUrlBuilder.append("/");
            } else {
               if (!tokenizer.tryConsume(".")) {
                  throw tokenizer.parseExceptionPreviousToken("Expected a valid type URL.");
               }

               typeUrlBuilder.append(".");
            }
         }
      }

      private void skipField(TextFormat.Tokenizer tokenizer, Descriptors.Descriptor type, int recursionLimit) throws TextFormat.ParseException {
         String name = this.consumeFullTypeName(tokenizer);
         this.detectSilentMarker(tokenizer, type, name);
         this.guessFieldTypeAndSkip(tokenizer, type, recursionLimit);
         if (!tokenizer.tryConsume(";")) {
            tokenizer.tryConsume(",");
         }
      }

      private void skipFieldMessage(TextFormat.Tokenizer tokenizer, Descriptors.Descriptor type, int recursionLimit) throws TextFormat.ParseException {
         String delimiter;
         if (tokenizer.tryConsume("<")) {
            delimiter = ">";
         } else {
            tokenizer.consume("{");
            delimiter = "}";
         }

         while (!tokenizer.lookingAt(">") && !tokenizer.lookingAt("}")) {
            this.skipField(tokenizer, type, recursionLimit);
         }

         tokenizer.consume(delimiter);
      }

      private void skipFieldValue(TextFormat.Tokenizer tokenizer) throws TextFormat.ParseException {
         if (!tokenizer.tryConsumeByteString()
            && !tokenizer.tryConsumeIdentifier()
            && !tokenizer.tryConsumeInt64()
            && !tokenizer.tryConsumeUInt64()
            && !tokenizer.tryConsumeDouble()
            && !tokenizer.tryConsumeFloat()) {
            throw tokenizer.parseException("Invalid field value: " + tokenizer.currentToken);
         }
      }

      private void guessFieldTypeAndSkip(TextFormat.Tokenizer tokenizer, Descriptors.Descriptor type, int recursionLimit) throws TextFormat.ParseException {
         boolean semicolonConsumed = tokenizer.tryConsume(":");
         if (tokenizer.lookingAt("[")) {
            this.skipFieldShortFormedRepeated(tokenizer, semicolonConsumed, type, recursionLimit);
         } else if (semicolonConsumed && !tokenizer.lookingAt("{") && !tokenizer.lookingAt("<")) {
            this.skipFieldValue(tokenizer);
         } else {
            if (recursionLimit < 1) {
               throw tokenizer.parseException("Message is nested too deep");
            }

            this.skipFieldMessage(tokenizer, type, recursionLimit - 1);
         }
      }

      private void skipFieldShortFormedRepeated(TextFormat.Tokenizer tokenizer, boolean scalarAllowed, Descriptors.Descriptor type, int recursionLimit) throws TextFormat.ParseException {
         if (tokenizer.tryConsume("[") && !tokenizer.tryConsume("]")) {
            while (true) {
               if (!tokenizer.lookingAt("{") && !tokenizer.lookingAt("<")) {
                  if (!scalarAllowed) {
                     throw tokenizer.parseException("Invalid repeated scalar field: missing \":\" before \"[\".");
                  }

                  this.skipFieldValue(tokenizer);
               } else {
                  if (recursionLimit < 1) {
                     throw tokenizer.parseException("Message is nested too deep");
                  }

                  this.skipFieldMessage(tokenizer, type, recursionLimit - 1);
               }

               if (tokenizer.tryConsume("]")) {
                  return;
               }

               tokenizer.consume(",");
            }
         }
      }

      public static class Builder {
         private boolean allowUnknownFields = false;
         private boolean allowUnknownEnumValues = false;
         private boolean allowUnknownExtensions = false;
         private TextFormat.Parser.SingularOverwritePolicy singularOverwritePolicy = TextFormat.Parser.SingularOverwritePolicy.ALLOW_SINGULAR_OVERWRITES;
         private TextFormatParseInfoTree.Builder parseInfoTreeBuilder = null;
         private TypeRegistry typeRegistry = TypeRegistry.getEmptyTypeRegistry();
         private int recursionLimit = 100;

         public TextFormat.Parser.Builder setTypeRegistry(TypeRegistry typeRegistry) {
            this.typeRegistry = typeRegistry;
            return this;
         }

         public TextFormat.Parser.Builder setAllowUnknownFields(boolean allowUnknownFields) {
            this.allowUnknownFields = allowUnknownFields;
            return this;
         }

         public TextFormat.Parser.Builder setAllowUnknownExtensions(boolean allowUnknownExtensions) {
            this.allowUnknownExtensions = allowUnknownExtensions;
            return this;
         }

         public TextFormat.Parser.Builder setSingularOverwritePolicy(TextFormat.Parser.SingularOverwritePolicy p) {
            this.singularOverwritePolicy = p;
            return this;
         }

         public TextFormat.Parser.Builder setParseInfoTreeBuilder(TextFormatParseInfoTree.Builder parseInfoTreeBuilder) {
            this.parseInfoTreeBuilder = parseInfoTreeBuilder;
            return this;
         }

         public TextFormat.Parser.Builder setRecursionLimit(int recursionLimit) {
            this.recursionLimit = recursionLimit;
            return this;
         }

         public TextFormat.Parser build() {
            return new TextFormat.Parser(
               this.typeRegistry,
               this.allowUnknownFields,
               this.allowUnknownEnumValues,
               this.allowUnknownExtensions,
               this.singularOverwritePolicy,
               this.parseInfoTreeBuilder,
               this.recursionLimit
            );
         }
      }

      public static enum SingularOverwritePolicy {
         ALLOW_SINGULAR_OVERWRITES,
         FORBID_SINGULAR_OVERWRITES;
      }

      static final class UnknownField {
         final String message;
         final TextFormat.Parser.UnknownField.Type type;

         UnknownField(String message, TextFormat.Parser.UnknownField.Type type) {
            this.message = message;
            this.type = type;
         }

         static enum Type {
            FIELD,
            EXTENSION;
         }
      }
   }

   public static final class Printer {
      private static final TextFormat.Printer DEFAULT_TEXT_FORMAT = new TextFormat.Printer(
         true, false, TypeRegistry.getEmptyTypeRegistry(), ExtensionRegistryLite.getEmptyRegistry(), false, false
      );
      private static final TextFormat.Printer DEFAULT_DEBUG_FORMAT = new TextFormat.Printer(
         true, false, TypeRegistry.getEmptyTypeRegistry(), ExtensionRegistryLite.getEmptyRegistry(), true, false
      );
      private static final TextFormat.Printer DEFAULT_FORMAT = new TextFormat.Printer(
            true, false, TypeRegistry.getEmptyTypeRegistry(), ExtensionRegistryLite.getEmptyRegistry(), false, false
         )
         .setInsertSilentMarker(TextFormat.ENABLE_INSERT_SILENT_MARKER);
      private final boolean escapeNonAscii;
      private final boolean useShortRepeatedPrimitives;
      private final TypeRegistry typeRegistry;
      private final ExtensionRegistryLite extensionRegistry;
      private final boolean enablingSafeDebugFormat;
      private final boolean singleLine;
      private boolean insertSilentMarker;
      private static final ThreadLocal<TextFormat.Printer.FieldReporterLevel> sensitiveFieldReportingLevel = new ThreadLocal<TextFormat.Printer.FieldReporterLevel>() {
         protected TextFormat.Printer.FieldReporterLevel initialValue() {
            return TextFormat.Printer.FieldReporterLevel.ABSTRACT_TO_STRING;
         }
      };

      static TextFormat.Printer getOutputModePrinter() {
         if (ProtobufToStringOutput.isDefaultFormat()) {
            return TextFormat.defaultFormatPrinter();
         } else {
            return ProtobufToStringOutput.shouldOutputDebugFormat() ? TextFormat.debugFormatPrinter() : TextFormat.printer();
         }
      }

      @CanIgnoreReturnValue
      private TextFormat.Printer setInsertSilentMarker(boolean insertSilentMarker) {
         this.insertSilentMarker = insertSilentMarker;
         return this;
      }

      private Printer(
         boolean escapeNonAscii,
         boolean useShortRepeatedPrimitives,
         TypeRegistry typeRegistry,
         ExtensionRegistryLite extensionRegistry,
         boolean enablingSafeDebugFormat,
         boolean singleLine
      ) {
         this.escapeNonAscii = escapeNonAscii;
         this.useShortRepeatedPrimitives = useShortRepeatedPrimitives;
         this.typeRegistry = typeRegistry;
         this.extensionRegistry = extensionRegistry;
         this.enablingSafeDebugFormat = enablingSafeDebugFormat;
         this.singleLine = singleLine;
         this.insertSilentMarker = false;
      }

      public TextFormat.Printer escapingNonAscii(boolean escapeNonAscii) {
         return new TextFormat.Printer(
            escapeNonAscii, this.useShortRepeatedPrimitives, this.typeRegistry, this.extensionRegistry, this.enablingSafeDebugFormat, this.singleLine
         );
      }

      public TextFormat.Printer usingTypeRegistry(TypeRegistry typeRegistry) {
         if (this.typeRegistry != TypeRegistry.getEmptyTypeRegistry()) {
            throw new IllegalArgumentException("Only one typeRegistry is allowed.");
         } else {
            return new TextFormat.Printer(
               this.escapeNonAscii, this.useShortRepeatedPrimitives, typeRegistry, this.extensionRegistry, this.enablingSafeDebugFormat, this.singleLine
            );
         }
      }

      public TextFormat.Printer usingExtensionRegistry(ExtensionRegistryLite extensionRegistry) {
         if (this.extensionRegistry != ExtensionRegistryLite.getEmptyRegistry()) {
            throw new IllegalArgumentException("Only one extensionRegistry is allowed.");
         } else {
            return new TextFormat.Printer(
               this.escapeNonAscii, this.useShortRepeatedPrimitives, this.typeRegistry, extensionRegistry, this.enablingSafeDebugFormat, this.singleLine
            );
         }
      }

      TextFormat.Printer enablingSafeDebugFormat(boolean enablingSafeDebugFormat) {
         return new TextFormat.Printer(
            this.escapeNonAscii, this.useShortRepeatedPrimitives, this.typeRegistry, this.extensionRegistry, enablingSafeDebugFormat, this.singleLine
         );
      }

      public TextFormat.Printer usingShortRepeatedPrimitives(boolean useShortRepeatedPrimitives) {
         return new TextFormat.Printer(
            this.escapeNonAscii, useShortRepeatedPrimitives, this.typeRegistry, this.extensionRegistry, this.enablingSafeDebugFormat, this.singleLine
         );
      }

      public TextFormat.Printer emittingSingleLine(boolean singleLine) {
         return new TextFormat.Printer(
            this.escapeNonAscii, this.useShortRepeatedPrimitives, this.typeRegistry, this.extensionRegistry, this.enablingSafeDebugFormat, singleLine
         );
      }

      void setSensitiveFieldReportingLevel(TextFormat.Printer.FieldReporterLevel level) {
         sensitiveFieldReportingLevel.set(level);
      }

      public void print(final MessageOrBuilder message, final Appendable output) throws IOException {
         this.print(message, output, TextFormat.Printer.FieldReporterLevel.PRINT);
      }

      void print(final MessageOrBuilder message, final Appendable output, TextFormat.Printer.FieldReporterLevel level) throws IOException {
         TextFormat.TextGenerator generator = TextFormat.setSingleLineOutput(
            output, this.singleLine, message.getDescriptorForType(), level, this.insertSilentMarker
         );
         this.print(message, generator);
      }

      public void print(final UnknownFieldSet fields, final Appendable output) throws IOException {
         printUnknownFields(fields, TextFormat.setSingleLineOutput(output, this.singleLine), this.enablingSafeDebugFormat);
      }

      private void print(final MessageOrBuilder message, final TextFormat.TextGenerator generator) throws IOException {
         if (!message.getDescriptorForType().getFullName().equals("google.protobuf.Any") || !this.printAny(message, generator)) {
            this.printMessage(message, generator);
         }
      }

      private void applyUnstablePrefix(final Appendable output) {
         try {
            output.append("");
         } catch (IOException var3) {
            throw new IllegalStateException(var3);
         }
      }

      private boolean printAny(final MessageOrBuilder message, final TextFormat.TextGenerator generator) throws IOException {
         Descriptors.Descriptor messageType = message.getDescriptorForType();
         Descriptors.FieldDescriptor typeUrlField = messageType.findFieldByNumber(1);
         Descriptors.FieldDescriptor valueField = messageType.findFieldByNumber(2);
         if (typeUrlField != null
            && typeUrlField.getType() == Descriptors.FieldDescriptor.Type.STRING
            && valueField != null
            && valueField.getType() == Descriptors.FieldDescriptor.Type.BYTES) {
            String typeUrl = (String)message.getField(typeUrlField);
            if (typeUrl.isEmpty()) {
               return false;
            } else {
               Object value = message.getField(valueField);
               Message.Builder contentBuilder = null;

               try {
                  Descriptors.Descriptor contentType = this.typeRegistry.getDescriptorForTypeUrl(typeUrl);
                  if (contentType == null) {
                     return false;
                  }

                  contentBuilder = DynamicMessage.getDefaultInstance(contentType).newBuilderForType();
                  contentBuilder.mergeFrom((ByteString)value, this.extensionRegistry);
               } catch (InvalidProtocolBufferException var10) {
                  return false;
               }

               generator.print("[");
               generator.print(typeUrl);
               generator.print("]");
               generator.maybePrintSilentMarker();
               generator.print("{");
               generator.eol();
               generator.indent();
               this.print(contentBuilder, generator);
               generator.outdent();
               generator.print("}");
               generator.eol();
               return true;
            }
         } else {
            return false;
         }
      }

      public String printFieldToString(final Descriptors.FieldDescriptor field, final Object value) {
         try {
            StringBuilder text = new StringBuilder();
            if (this.enablingSafeDebugFormat) {
               this.applyUnstablePrefix(text);
            }

            this.printField(field, value, text);
            return text.toString();
         } catch (IOException var4) {
            throw new IllegalStateException(var4);
         }
      }

      public void printField(final Descriptors.FieldDescriptor field, final Object value, final Appendable output) throws IOException {
         this.printField(field, value, TextFormat.setSingleLineOutput(output, this.singleLine));
      }

      private void printField(final Descriptors.FieldDescriptor field, final Object value, final TextFormat.TextGenerator generator) throws IOException {
         if (field.isMapField()) {
            List<TextFormat.Printer.MapEntryAdapter> adapters = new ArrayList<>();

            for (Object entry : (List)value) {
               adapters.add(new TextFormat.Printer.MapEntryAdapter(entry, field));
            }

            Collections.sort(adapters);

            for (TextFormat.Printer.MapEntryAdapter adapter : adapters) {
               this.printSingleField(field, adapter.getEntry(), generator);
            }
         } else if (field.isRepeated()) {
            if (this.useShortRepeatedPrimitives && field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
               this.printShortRepeatedField(field, value, generator);
            } else {
               for (Object element : (List)value) {
                  this.printSingleField(field, element, generator);
               }
            }
         } else {
            this.printSingleField(field, value, generator);
         }
      }

      public void printFieldValue(final Descriptors.FieldDescriptor field, final Object value, final Appendable output) throws IOException {
         this.printFieldValue(field, value, TextFormat.setSingleLineOutput(output, this.singleLine));
      }

      private void printFieldValue(final Descriptors.FieldDescriptor field, final Object value, final TextFormat.TextGenerator generator) throws IOException {
         if (this.shouldRedact(field, generator)) {
            generator.print("[REDACTED]");
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
               generator.eol();
            }
         } else {
            switch (field.getType()) {
               case INT32:
               case SINT32:
               case SFIXED32:
                  generator.print(((Integer)value).toString());
                  break;
               case INT64:
               case SINT64:
               case SFIXED64:
                  generator.print(((Long)value).toString());
                  break;
               case BOOL:
                  generator.print(((Boolean)value).toString());
                  break;
               case FLOAT:
                  generator.print(((Float)value).toString());
                  break;
               case DOUBLE:
                  generator.print(((Double)value).toString());
                  break;
               case UINT32:
               case FIXED32:
                  generator.print(TextFormat.unsignedToString((Integer)value));
                  break;
               case UINT64:
               case FIXED64:
                  generator.print(TextFormat.unsignedToString((Long)value));
                  break;
               case STRING:
                  generator.print("\"");
                  generator.print(
                     this.escapeNonAscii
                        ? TextFormatEscaper.escapeText((String)value)
                        : TextFormat.escapeDoubleQuotesAndBackslashes((String)value).replace("\n", "\\n")
                  );
                  generator.print("\"");
                  break;
               case BYTES:
                  generator.print("\"");
                  if (value instanceof ByteString) {
                     generator.print(TextFormat.escapeBytes((ByteString)value));
                  } else {
                     generator.print(TextFormat.escapeBytes((byte[])value));
                  }

                  generator.print("\"");
                  break;
               case ENUM:
                  if (((Descriptors.EnumValueDescriptor)value).getIndex() == -1) {
                     generator.print(Integer.toString(((Descriptors.EnumValueDescriptor)value).getNumber()));
                  } else {
                     generator.print(((Descriptors.EnumValueDescriptor)value).getName());
                  }
                  break;
               case MESSAGE:
               case GROUP:
                  this.print((MessageOrBuilder)value, generator);
            }
         }
      }

      private boolean shouldRedact(final Descriptors.FieldDescriptor field, TextFormat.TextGenerator generator) {
         Descriptors.FieldDescriptor.RedactionState state = field.getRedactionState();
         return this.enablingSafeDebugFormat && state.redact;
      }

      public String printToString(final MessageOrBuilder message) {
         return this.printToString(message, TextFormat.Printer.FieldReporterLevel.PRINTER_PRINT_TO_STRING);
      }

      String printToString(final MessageOrBuilder message, TextFormat.Printer.FieldReporterLevel level) {
         try {
            StringBuilder text = new StringBuilder();
            if (this.enablingSafeDebugFormat) {
               this.applyUnstablePrefix(text);
            }

            this.print(message, text, level);
            return text.toString();
         } catch (IOException var4) {
            throw new IllegalStateException(var4);
         }
      }

      public String printToString(final UnknownFieldSet fields) {
         try {
            StringBuilder text = new StringBuilder();
            if (this.enablingSafeDebugFormat) {
               this.applyUnstablePrefix(text);
            }

            this.print(fields, text);
            return text.toString();
         } catch (IOException var3) {
            throw new IllegalStateException(var3);
         }
      }

      @Deprecated
      public String shortDebugString(final MessageOrBuilder message) {
         return this.emittingSingleLine(true).printToString(message, TextFormat.Printer.FieldReporterLevel.SHORT_DEBUG_STRING);
      }

      @Deprecated
      @InlineMe(replacement = "this.emittingSingleLine(true).printFieldToString(field, value)")
      public String shortDebugString(final Descriptors.FieldDescriptor field, final Object value) {
         return this.emittingSingleLine(true).printFieldToString(field, value);
      }

      @Deprecated
      @InlineMe(replacement = "this.emittingSingleLine(true).printToString(fields)")
      public String shortDebugString(final UnknownFieldSet fields) {
         return this.emittingSingleLine(true).printToString(fields);
      }

      private static void printUnknownFieldValue(final int tag, final Object value, final TextFormat.TextGenerator generator, boolean redact) throws IOException {
         switch (WireFormat.getTagWireType(tag)) {
            case 0:
               generator.print(redact ? String.format("UNKNOWN_VARINT %s", "[REDACTED]") : TextFormat.unsignedToString((Long)value));
               break;
            case 1:
               generator.print(redact ? String.format("UNKNOWN_FIXED64 %s", "[REDACTED]") : String.format((Locale)null, "0x%016x", (Long)value));
               break;
            case 2:
               try {
                  UnknownFieldSet message = UnknownFieldSet.parseFrom((ByteString)value);
                  generator.print("{");
                  generator.eol();
                  generator.indent();
                  printUnknownFields(message, generator, redact);
                  generator.outdent();
                  generator.print("}");
               } catch (InvalidProtocolBufferException var5) {
                  if (redact) {
                     generator.print(String.format("UNKNOWN_STRING %s", "[REDACTED]"));
                  } else {
                     generator.print("\"");
                     generator.print(TextFormat.escapeBytes((ByteString)value));
                     generator.print("\"");
                  }
               }
               break;
            case 3:
               printUnknownFields((UnknownFieldSet)value, generator, redact);
               break;
            case 4:
            default:
               throw new IllegalArgumentException("Bad tag: " + tag);
            case 5:
               generator.print(redact ? String.format("UNKNOWN_FIXED32 %s", "[REDACTED]") : String.format((Locale)null, "0x%08x", (Integer)value));
         }
      }

      private void printMessage(final MessageOrBuilder message, final TextFormat.TextGenerator generator) throws IOException {
         for (Entry<Descriptors.FieldDescriptor, Object> field : message.getAllFields().entrySet()) {
            this.printField(field.getKey(), field.getValue(), generator);
         }

         printUnknownFields(message.getUnknownFields(), generator, this.enablingSafeDebugFormat);
      }

      private void printShortRepeatedField(final Descriptors.FieldDescriptor field, final Object value, final TextFormat.TextGenerator generator) throws IOException {
         generator.print(field.getName());
         generator.print(": ");
         generator.print("[");
         String separator = "";

         for (Object element : (List)value) {
            generator.print(separator);
            this.printFieldValue(field, element, generator);
            separator = ", ";
         }

         generator.print("]");
         generator.eol();
      }

      private void printSingleField(final Descriptors.FieldDescriptor field, final Object value, final TextFormat.TextGenerator generator) throws IOException {
         if (field.isExtension()) {
            generator.print("[");
            if (field.getContainingType().getOptions().getMessageSetWireFormat()
               && field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE
               && field.isOptional()
               && field.getExtensionScope() == field.getMessageType()) {
               generator.print(field.getMessageType().getFullName());
            } else {
               generator.print(field.getFullName());
            }

            generator.print("]");
         } else if (field.isGroupLike()) {
            generator.print(field.getMessageType().getName());
         } else {
            generator.print(field.getName());
         }

         if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            generator.maybePrintSilentMarker();
            generator.print("{");
            generator.eol();
            generator.indent();
         } else {
            generator.print(":");
            generator.maybePrintSilentMarker();
         }

         this.printFieldValue(field, value, generator);
         if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            generator.outdent();
            generator.print("}");
         }

         generator.eol();
      }

      private static void printUnknownFields(final UnknownFieldSet unknownFields, final TextFormat.TextGenerator generator, boolean redact) throws IOException {
         if (!unknownFields.isEmpty()) {
            for (Entry<Integer, UnknownFieldSet.Field> entry : unknownFields.asMap().entrySet()) {
               int number = entry.getKey();
               UnknownFieldSet.Field field = entry.getValue();
               printUnknownField(number, 0, field.getVarintList(), generator, redact);
               printUnknownField(number, 5, field.getFixed32List(), generator, redact);
               printUnknownField(number, 1, field.getFixed64List(), generator, redact);
               printUnknownField(number, 2, field.getLengthDelimitedList(), generator, redact);

               for (UnknownFieldSet value : field.getGroupList()) {
                  generator.print(entry.getKey().toString());
                  generator.maybePrintSilentMarker();
                  generator.print("{");
                  generator.eol();
                  generator.indent();
                  printUnknownFields(value, generator, redact);
                  generator.outdent();
                  generator.print("}");
                  generator.eol();
               }
            }
         }
      }

      private static void printUnknownField(
         final int number, final int wireType, final List<?> values, final TextFormat.TextGenerator generator, boolean redact
      ) throws IOException {
         for (Object value : values) {
            generator.print(String.valueOf(number));
            generator.print(":");
            generator.maybePrintSilentMarker();
            printUnknownFieldValue(wireType, value, generator, redact);
            generator.eol();
         }
      }

      static enum FieldReporterLevel {
         REPORT_ALL(0),
         TEXT_GENERATOR(1),
         PRINT(2),
         PRINTER_PRINT_TO_STRING(3),
         TEXTFORMAT_PRINT_TO_STRING(4),
         PRINT_UNICODE(5),
         SHORT_DEBUG_STRING(6),
         LEGACY_MULTILINE(7),
         LEGACY_SINGLE_LINE(8),
         DEBUG_MULTILINE(9),
         DEBUG_SINGLE_LINE(10),
         ABSTRACT_TO_STRING(11),
         ABSTRACT_BUILDER_TO_STRING(12),
         ABSTRACT_MUTABLE_TO_STRING(13),
         REPORT_NONE(14);

         private final int index;

         private FieldReporterLevel(int index) {
            this.index = index;
         }
      }

      static class MapEntryAdapter implements Comparable<TextFormat.Printer.MapEntryAdapter> {
         private Object entry;
         private Message messageEntry;
         private final Descriptors.FieldDescriptor keyField;

         MapEntryAdapter(Object entry, Descriptors.FieldDescriptor fieldDescriptor) {
            if (entry instanceof Message) {
               this.messageEntry = (Message)entry;
            } else {
               this.entry = entry;
            }

            this.keyField = fieldDescriptor.getMessageType().findFieldByName("key");
         }

         Object getKey() {
            return this.messageEntry != null && this.keyField != null ? this.messageEntry.getField(this.keyField) : null;
         }

         Object getEntry() {
            return this.messageEntry != null ? this.messageEntry : this.entry;
         }

         public int compareTo(TextFormat.Printer.MapEntryAdapter b) {
            Object aKey = this.getKey();
            Object bKey = b.getKey();
            if (aKey == null && bKey == null) {
               return 0;
            } else if (aKey == null) {
               return -1;
            } else if (bKey == null) {
               return 1;
            } else {
               switch (this.keyField.getJavaType()) {
                  case BOOLEAN:
                     return ((Boolean)aKey).compareTo((Boolean)bKey);
                  case LONG:
                     return ((Long)aKey).compareTo((Long)bKey);
                  case INT:
                     return ((Integer)aKey).compareTo((Integer)bKey);
                  case STRING:
                     return ((String)aKey).compareTo((String)bKey);
                  default:
                     return 0;
               }
            }
         }
      }
   }

   private static final class TextGenerator {
      private final Appendable output;
      private final StringBuilder indent = new StringBuilder();
      private final boolean singleLineMode;
      private boolean shouldEmitSilentMarker;
      private boolean atStartOfLine = false;
      private final TextFormat.Printer.FieldReporterLevel fieldReporterLevel;
      private final Descriptors.Descriptor rootMessageType;

      private TextGenerator(
         final Appendable output,
         boolean singleLineMode,
         Descriptors.Descriptor rootMessageType,
         TextFormat.Printer.FieldReporterLevel fieldReporterLevel,
         boolean shouldEmitSilentMarker
      ) {
         this.output = output;
         this.singleLineMode = singleLineMode;
         this.rootMessageType = rootMessageType;
         this.fieldReporterLevel = fieldReporterLevel;
         this.shouldEmitSilentMarker = shouldEmitSilentMarker;
      }

      public void indent() {
         this.indent.append("  ");
      }

      public void outdent() {
         int length = this.indent.length();
         if (length == 0) {
            throw new IllegalArgumentException(" Outdent() without matching Indent().");
         } else {
            this.indent.setLength(length - 2);
         }
      }

      public void print(final CharSequence text) throws IOException {
         if (this.atStartOfLine) {
            this.atStartOfLine = false;
            this.output.append((CharSequence)(this.singleLineMode ? " " : this.indent));
         }

         this.output.append(text);
      }

      public void eol() throws IOException {
         if (!this.singleLineMode) {
            this.output.append("\n");
         }

         this.atStartOfLine = true;
      }

      void maybePrintSilentMarker() throws IOException {
         if (this.shouldEmitSilentMarker) {
            this.output.append(" \t ");
            this.shouldEmitSilentMarker = false;
         } else {
            this.output.append(" ");
         }
      }
   }

   private static final class Tokenizer {
      private final CharSequence text;
      private String currentToken;
      private int pos = 0;
      private int line = 0;
      private int column = 0;
      private int lineInfoTrackingPos = 0;
      private int previousLine = 0;
      private int previousColumn = 0;
      private boolean containsSilentMarkerAfterCurrentToken = false;
      private boolean containsSilentMarkerAfterPrevToken = false;

      private Tokenizer(final CharSequence text) {
         this.text = text;
         this.skipWhitespace();
         this.nextToken();
      }

      int getPreviousLine() {
         return this.previousLine;
      }

      int getPreviousColumn() {
         return this.previousColumn;
      }

      int getLine() {
         return this.line;
      }

      int getColumn() {
         return this.column;
      }

      boolean getContainsSilentMarkerAfterCurrentToken() {
         return this.containsSilentMarkerAfterCurrentToken;
      }

      boolean getContainsSilentMarkerAfterPrevToken() {
         return this.containsSilentMarkerAfterPrevToken;
      }

      boolean atEnd() {
         return this.currentToken.length() == 0;
      }

      void nextToken() {
         this.previousLine = this.line;

         for (this.previousColumn = this.column; this.lineInfoTrackingPos < this.pos; this.lineInfoTrackingPos++) {
            if (this.text.charAt(this.lineInfoTrackingPos) == '\n') {
               this.line++;
               this.column = 0;
            } else {
               this.column++;
            }
         }

         if (this.pos == this.text.length()) {
            this.currentToken = "";
         } else {
            this.currentToken = this.nextTokenInternal();
            this.skipWhitespace();
         }
      }

      private String nextTokenInternal() {
         int textLength = this.text.length();
         int startPos = this.pos;
         char startChar = this.text.charAt(startPos);
         int endPos = this.pos;
         if (isAlphaUnder(startChar)) {
            while (++endPos != textLength) {
               char c = this.text.charAt(endPos);
               if (!isAlphaUnder(c) && !isDigitPlusMinus(c)) {
                  break;
               }
            }
         } else if (!isDigitPlusMinus(startChar) && startChar != '.') {
            if (startChar != '"' && startChar != '\'') {
               return this.nextTokenSingleChar();
            }

            while (++endPos != textLength) {
               char c = this.text.charAt(endPos);
               if (c == startChar) {
                  endPos++;
                  break;
               }

               if (c == '\n') {
                  break;
               }

               if (c == '\\') {
                  endPos++;
                  if (endPos == textLength || this.text.charAt(endPos) == '\n') {
                     break;
                  }
               }
            }
         } else {
            if (startChar == '.') {
               if (++endPos == textLength) {
                  return this.nextTokenSingleChar();
               }

               if (!isDigitPlusMinus(this.text.charAt(endPos))) {
                  return this.nextTokenSingleChar();
               }
            }

            while (++endPos != textLength) {
               char cx = this.text.charAt(endPos);
               if (!isDigitPlusMinus(cx) && !isAlphaUnder(cx) && cx != '.') {
                  break;
               }
            }
         }

         this.pos = endPos;
         return this.text.subSequence(startPos, endPos).toString();
      }

      private static boolean isAlphaUnder(char c) {
         return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || c == '_';
      }

      private static boolean isDigitPlusMinus(char c) {
         return '0' <= c && c <= '9' || c == '+' || c == '-';
      }

      private static boolean isWhitespace(char c) {
         return c == ' ' || c == '\f' || c == '\n' || c == '\r' || c == '\t';
      }

      private String nextTokenSingleChar() {
         char c = this.text.charAt(this.pos++);
         switch (c) {
            case ',':
               return ",";
            case ':':
               return ":";
            case '<':
               return "<";
            case '>':
               return ">";
            case '[':
               return "[";
            case ']':
               return "]";
            case '{':
               return "{";
            case '}':
               return "}";
            default:
               return String.valueOf(c);
         }
      }

      private void skipWhitespace() {
         int textLength = this.text.length();
         int startPos = this.pos;
         int endPos = this.pos - 1;

         while (++endPos != textLength) {
            char c = this.text.charAt(endPos);
            if (c != '#') {
               if (isWhitespace(c)) {
                  continue;
               }
               break;
            } else {
               do {
                  endPos++;
               } while (endPos != textLength && this.text.charAt(endPos) != '\n');

               if (endPos == textLength) {
                  break;
               }
            }
         }

         this.pos = endPos;
      }

      boolean tryConsume(final String token) {
         if (this.currentToken.equals(token)) {
            this.nextToken();
            return true;
         } else {
            return false;
         }
      }

      void consume(final String token) throws TextFormat.ParseException {
         if (!this.tryConsume(token)) {
            throw this.parseException("Expected \"" + token + "\".");
         }
      }

      boolean lookingAtInteger() {
         return this.currentToken.length() == 0 ? false : isDigitPlusMinus(this.currentToken.charAt(0));
      }

      boolean lookingAt(String text) {
         return this.currentToken.equals(text);
      }

      String consumeIdentifier() throws TextFormat.ParseException {
         for (int i = 0; i < this.currentToken.length(); i++) {
            char c = this.currentToken.charAt(i);
            if (!isAlphaUnder(c) && ('0' > c || c > '9') && c != '.') {
               throw this.parseException("Expected identifier. Found '" + this.currentToken + "'");
            }
         }

         String result = this.currentToken;
         this.nextToken();
         return result;
      }

      boolean tryConsumeIdentifier() {
         try {
            this.consumeIdentifier();
            return true;
         } catch (TextFormat.ParseException var2) {
            return false;
         }
      }

      int consumeInt32() throws TextFormat.ParseException {
         try {
            int result = TextFormat.parseInt32(this.currentToken);
            this.nextToken();
            return result;
         } catch (NumberFormatException var2) {
            throw this.integerParseException(var2);
         }
      }

      int consumeUInt32() throws TextFormat.ParseException {
         try {
            int result = TextFormat.parseUInt32(this.currentToken);
            this.nextToken();
            return result;
         } catch (NumberFormatException var2) {
            throw this.integerParseException(var2);
         }
      }

      long consumeInt64() throws TextFormat.ParseException {
         try {
            long result = TextFormat.parseInt64(this.currentToken);
            this.nextToken();
            return result;
         } catch (NumberFormatException var3) {
            throw this.integerParseException(var3);
         }
      }

      boolean tryConsumeInt64() {
         try {
            this.consumeInt64();
            return true;
         } catch (TextFormat.ParseException var2) {
            return false;
         }
      }

      long consumeUInt64() throws TextFormat.ParseException {
         try {
            long result = TextFormat.parseUInt64(this.currentToken);
            this.nextToken();
            return result;
         } catch (NumberFormatException var3) {
            throw this.integerParseException(var3);
         }
      }

      public boolean tryConsumeUInt64() {
         try {
            this.consumeUInt64();
            return true;
         } catch (TextFormat.ParseException var2) {
            return false;
         }
      }

      public double consumeDouble() throws TextFormat.ParseException {
         String e = this.currentToken.toLowerCase(Locale.ROOT);
         switch (e) {
            case "-inf":
            case "-infinity":
               this.nextToken();
               return Double.NEGATIVE_INFINITY;
            case "inf":
            case "infinity":
               this.nextToken();
               return Double.POSITIVE_INFINITY;
            case "nan":
               this.nextToken();
               return Double.NaN;
            default:
               try {
                  double result = Double.parseDouble(this.currentToken);
                  this.nextToken();
                  return result;
               } catch (NumberFormatException var3) {
                  throw this.floatParseException(var3);
               }
         }
      }

      public boolean tryConsumeDouble() {
         try {
            this.consumeDouble();
            return true;
         } catch (TextFormat.ParseException var2) {
            return false;
         }
      }

      public float consumeFloat() throws TextFormat.ParseException {
         String e = this.currentToken.toLowerCase(Locale.ROOT);
         switch (e) {
            case "-inf":
            case "-inff":
            case "-infinity":
            case "-infinityf":
               this.nextToken();
               return Float.NEGATIVE_INFINITY;
            case "inf":
            case "inff":
            case "infinity":
            case "infinityf":
               this.nextToken();
               return Float.POSITIVE_INFINITY;
            case "nan":
            case "nanf":
               this.nextToken();
               return Float.NaN;
            default:
               try {
                  float result = Float.parseFloat(this.currentToken);
                  this.nextToken();
                  return result;
               } catch (NumberFormatException var3) {
                  throw this.floatParseException(var3);
               }
         }
      }

      public boolean tryConsumeFloat() {
         try {
            this.consumeFloat();
            return true;
         } catch (TextFormat.ParseException var2) {
            return false;
         }
      }

      public boolean consumeBoolean() throws TextFormat.ParseException {
         if (this.currentToken.equals("true") || this.currentToken.equals("True") || this.currentToken.equals("t") || this.currentToken.equals("1")) {
            this.nextToken();
            return true;
         } else if (!this.currentToken.equals("false")
            && !this.currentToken.equals("False")
            && !this.currentToken.equals("f")
            && !this.currentToken.equals("0")) {
            throw this.parseException("Expected \"true\" or \"false\". Found \"" + this.currentToken + "\".");
         } else {
            this.nextToken();
            return false;
         }
      }

      public String consumeString() throws TextFormat.ParseException {
         return this.consumeByteString().toStringUtf8();
      }

      @CanIgnoreReturnValue
      ByteString consumeByteString() throws TextFormat.ParseException {
         List<ByteString> list = new ArrayList<>();
         this.consumeByteString(list);

         while (this.currentToken.startsWith("'") || this.currentToken.startsWith("\"")) {
            this.consumeByteString(list);
         }

         return ByteString.copyFrom(list);
      }

      boolean tryConsumeByteString() {
         try {
            this.consumeByteString();
            return true;
         } catch (TextFormat.ParseException var2) {
            return false;
         }
      }

      private void consumeByteString(List<ByteString> list) throws TextFormat.ParseException {
         char quote = this.currentToken.length() > 0 ? this.currentToken.charAt(0) : 0;
         if (quote != '"' && quote != '\'') {
            throw this.parseException("Expected string.");
         } else if (this.currentToken.length() >= 2 && this.currentToken.charAt(this.currentToken.length() - 1) == quote) {
            try {
               String escaped = this.currentToken.substring(1, this.currentToken.length() - 1);
               ByteString result = TextFormat.unescapeBytes(escaped);
               this.nextToken();
               list.add(result);
            } catch (TextFormat.InvalidEscapeSequenceException var5) {
               throw this.parseException(var5.getMessage());
            }
         } else {
            throw this.parseException("String missing ending quote.");
         }
      }

      TextFormat.ParseException parseException(final String description) {
         return new TextFormat.ParseException(this.line + 1, this.column + 1, description);
      }

      TextFormat.ParseException parseExceptionPreviousToken(final String description) {
         return new TextFormat.ParseException(this.previousLine + 1, this.previousColumn + 1, description);
      }

      private TextFormat.ParseException integerParseException(final NumberFormatException e) {
         return this.parseException("Couldn't parse integer: " + e.getMessage());
      }

      private TextFormat.ParseException floatParseException(final NumberFormatException e) {
         return this.parseException("Couldn't parse number: " + e.getMessage());
      }
   }

   @Deprecated
   public static class UnknownFieldParseException extends TextFormat.ParseException {
      private final String unknownField;

      public UnknownFieldParseException(final String message) {
         this(-1, -1, "", message);
      }

      public UnknownFieldParseException(final int line, final int column, final String unknownField, final String message) {
         super(line, column, message);
         this.unknownField = unknownField;
      }

      public String getUnknownField() {
         return this.unknownField;
      }
   }
}
