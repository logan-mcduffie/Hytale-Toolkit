package org.bson.json;

import org.bson.BsonBinary;
import org.bson.BsonMaxKey;
import org.bson.BsonMinKey;
import org.bson.BsonNull;
import org.bson.BsonRegularExpression;
import org.bson.BsonTimestamp;
import org.bson.BsonUndefined;
import org.bson.BsonWriterSettings;
import org.bson.assertions.Assertions;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

public final class JsonWriterSettings extends BsonWriterSettings {
   private static final JsonNullConverter JSON_NULL_CONVERTER = new JsonNullConverter();
   private static final JsonStringConverter JSON_STRING_CONVERTER = new JsonStringConverter();
   private static final JsonBooleanConverter JSON_BOOLEAN_CONVERTER = new JsonBooleanConverter();
   private static final JsonDoubleConverter JSON_DOUBLE_CONVERTER = new JsonDoubleConverter();
   private static final ExtendedJsonDoubleConverter EXTENDED_JSON_DOUBLE_CONVERTER = new ExtendedJsonDoubleConverter();
   private static final RelaxedExtendedJsonDoubleConverter RELAXED_EXTENDED_JSON_DOUBLE_CONVERTER = new RelaxedExtendedJsonDoubleConverter();
   private static final JsonInt32Converter JSON_INT_32_CONVERTER = new JsonInt32Converter();
   private static final ExtendedJsonInt32Converter EXTENDED_JSON_INT_32_CONVERTER = new ExtendedJsonInt32Converter();
   private static final JsonSymbolConverter JSON_SYMBOL_CONVERTER = new JsonSymbolConverter();
   private static final ExtendedJsonMinKeyConverter EXTENDED_JSON_MIN_KEY_CONVERTER = new ExtendedJsonMinKeyConverter();
   private static final ShellMinKeyConverter SHELL_MIN_KEY_CONVERTER = new ShellMinKeyConverter();
   private static final ExtendedJsonMaxKeyConverter EXTENDED_JSON_MAX_KEY_CONVERTER = new ExtendedJsonMaxKeyConverter();
   private static final ShellMaxKeyConverter SHELL_MAX_KEY_CONVERTER = new ShellMaxKeyConverter();
   private static final ExtendedJsonUndefinedConverter EXTENDED_JSON_UNDEFINED_CONVERTER = new ExtendedJsonUndefinedConverter();
   private static final ShellUndefinedConverter SHELL_UNDEFINED_CONVERTER = new ShellUndefinedConverter();
   private static final LegacyExtendedJsonDateTimeConverter LEGACY_EXTENDED_JSON_DATE_TIME_CONVERTER = new LegacyExtendedJsonDateTimeConverter();
   private static final ExtendedJsonDateTimeConverter EXTENDED_JSON_DATE_TIME_CONVERTER = new ExtendedJsonDateTimeConverter();
   private static final RelaxedExtendedJsonDateTimeConverter RELAXED_EXTENDED_JSON_DATE_TIME_CONVERTER = new RelaxedExtendedJsonDateTimeConverter();
   private static final ShellDateTimeConverter SHELL_DATE_TIME_CONVERTER = new ShellDateTimeConverter();
   private static final ExtendedJsonBinaryConverter EXTENDED_JSON_BINARY_CONVERTER = new ExtendedJsonBinaryConverter();
   private static final LegacyExtendedJsonBinaryConverter LEGACY_EXTENDED_JSON_BINARY_CONVERTER = new LegacyExtendedJsonBinaryConverter();
   private static final ShellBinaryConverter SHELL_BINARY_CONVERTER = new ShellBinaryConverter();
   private static final ExtendedJsonInt64Converter EXTENDED_JSON_INT_64_CONVERTER = new ExtendedJsonInt64Converter();
   private static final RelaxedExtendedJsonInt64Converter RELAXED_JSON_INT_64_CONVERTER = new RelaxedExtendedJsonInt64Converter();
   private static final ShellInt64Converter SHELL_INT_64_CONVERTER = new ShellInt64Converter();
   private static final ExtendedJsonDecimal128Converter EXTENDED_JSON_DECIMAL_128_CONVERTER = new ExtendedJsonDecimal128Converter();
   private static final ShellDecimal128Converter SHELL_DECIMAL_128_CONVERTER = new ShellDecimal128Converter();
   private static final ExtendedJsonObjectIdConverter EXTENDED_JSON_OBJECT_ID_CONVERTER = new ExtendedJsonObjectIdConverter();
   private static final ShellObjectIdConverter SHELL_OBJECT_ID_CONVERTER = new ShellObjectIdConverter();
   private static final ExtendedJsonTimestampConverter EXTENDED_JSON_TIMESTAMP_CONVERTER = new ExtendedJsonTimestampConverter();
   private static final ShellTimestampConverter SHELL_TIMESTAMP_CONVERTER = new ShellTimestampConverter();
   private static final ExtendedJsonRegularExpressionConverter EXTENDED_JSON_REGULAR_EXPRESSION_CONVERTER = new ExtendedJsonRegularExpressionConverter();
   private static final LegacyExtendedJsonRegularExpressionConverter LEGACY_EXTENDED_JSON_REGULAR_EXPRESSION_CONVERTER = new LegacyExtendedJsonRegularExpressionConverter();
   private static final ShellRegularExpressionConverter SHELL_REGULAR_EXPRESSION_CONVERTER = new ShellRegularExpressionConverter();
   private final boolean indent;
   private final String newLineCharacters;
   private final String indentCharacters;
   private final int maxLength;
   private final JsonMode outputMode;
   private final Converter<BsonNull> nullConverter;
   private final Converter<String> stringConverter;
   private final Converter<Long> dateTimeConverter;
   private final Converter<BsonBinary> binaryConverter;
   private final Converter<Boolean> booleanConverter;
   private final Converter<Double> doubleConverter;
   private final Converter<Integer> int32Converter;
   private final Converter<Long> int64Converter;
   private final Converter<Decimal128> decimal128Converter;
   private final Converter<ObjectId> objectIdConverter;
   private final Converter<BsonTimestamp> timestampConverter;
   private final Converter<BsonRegularExpression> regularExpressionConverter;
   private final Converter<String> symbolConverter;
   private final Converter<BsonUndefined> undefinedConverter;
   private final Converter<BsonMinKey> minKeyConverter;
   private final Converter<BsonMaxKey> maxKeyConverter;
   private final Converter<String> javaScriptConverter;

   public static JsonWriterSettings.Builder builder() {
      return new JsonWriterSettings.Builder();
   }

   private JsonWriterSettings(JsonWriterSettings.Builder builder) {
      this.indent = builder.indent;
      this.newLineCharacters = builder.newLineCharacters != null ? builder.newLineCharacters : System.getProperty("line.separator");
      this.indentCharacters = builder.indentCharacters;
      this.outputMode = builder.outputMode;
      this.maxLength = builder.maxLength;
      if (builder.nullConverter != null) {
         this.nullConverter = builder.nullConverter;
      } else {
         this.nullConverter = JSON_NULL_CONVERTER;
      }

      if (builder.stringConverter != null) {
         this.stringConverter = builder.stringConverter;
      } else {
         this.stringConverter = JSON_STRING_CONVERTER;
      }

      if (builder.booleanConverter != null) {
         this.booleanConverter = builder.booleanConverter;
      } else {
         this.booleanConverter = JSON_BOOLEAN_CONVERTER;
      }

      if (builder.doubleConverter != null) {
         this.doubleConverter = builder.doubleConverter;
      } else if (this.outputMode == JsonMode.EXTENDED) {
         this.doubleConverter = EXTENDED_JSON_DOUBLE_CONVERTER;
      } else if (this.outputMode == JsonMode.RELAXED) {
         this.doubleConverter = RELAXED_EXTENDED_JSON_DOUBLE_CONVERTER;
      } else {
         this.doubleConverter = JSON_DOUBLE_CONVERTER;
      }

      if (builder.int32Converter != null) {
         this.int32Converter = builder.int32Converter;
      } else if (this.outputMode == JsonMode.EXTENDED) {
         this.int32Converter = EXTENDED_JSON_INT_32_CONVERTER;
      } else {
         this.int32Converter = JSON_INT_32_CONVERTER;
      }

      if (builder.symbolConverter != null) {
         this.symbolConverter = builder.symbolConverter;
      } else {
         this.symbolConverter = JSON_SYMBOL_CONVERTER;
      }

      if (builder.javaScriptConverter != null) {
         this.javaScriptConverter = builder.javaScriptConverter;
      } else {
         this.javaScriptConverter = new JsonJavaScriptConverter();
      }

      if (builder.minKeyConverter != null) {
         this.minKeyConverter = builder.minKeyConverter;
      } else if (this.outputMode != JsonMode.STRICT && this.outputMode != JsonMode.EXTENDED && this.outputMode != JsonMode.RELAXED) {
         this.minKeyConverter = SHELL_MIN_KEY_CONVERTER;
      } else {
         this.minKeyConverter = EXTENDED_JSON_MIN_KEY_CONVERTER;
      }

      if (builder.maxKeyConverter != null) {
         this.maxKeyConverter = builder.maxKeyConverter;
      } else if (this.outputMode != JsonMode.STRICT && this.outputMode != JsonMode.EXTENDED && this.outputMode != JsonMode.RELAXED) {
         this.maxKeyConverter = SHELL_MAX_KEY_CONVERTER;
      } else {
         this.maxKeyConverter = EXTENDED_JSON_MAX_KEY_CONVERTER;
      }

      if (builder.undefinedConverter != null) {
         this.undefinedConverter = builder.undefinedConverter;
      } else if (this.outputMode != JsonMode.STRICT && this.outputMode != JsonMode.EXTENDED && this.outputMode != JsonMode.RELAXED) {
         this.undefinedConverter = SHELL_UNDEFINED_CONVERTER;
      } else {
         this.undefinedConverter = EXTENDED_JSON_UNDEFINED_CONVERTER;
      }

      if (builder.dateTimeConverter != null) {
         this.dateTimeConverter = builder.dateTimeConverter;
      } else if (this.outputMode == JsonMode.STRICT) {
         this.dateTimeConverter = LEGACY_EXTENDED_JSON_DATE_TIME_CONVERTER;
      } else if (this.outputMode == JsonMode.EXTENDED) {
         this.dateTimeConverter = EXTENDED_JSON_DATE_TIME_CONVERTER;
      } else if (this.outputMode == JsonMode.RELAXED) {
         this.dateTimeConverter = RELAXED_EXTENDED_JSON_DATE_TIME_CONVERTER;
      } else {
         this.dateTimeConverter = SHELL_DATE_TIME_CONVERTER;
      }

      if (builder.binaryConverter != null) {
         this.binaryConverter = builder.binaryConverter;
      } else if (this.outputMode == JsonMode.STRICT) {
         this.binaryConverter = LEGACY_EXTENDED_JSON_BINARY_CONVERTER;
      } else if (this.outputMode != JsonMode.EXTENDED && this.outputMode != JsonMode.RELAXED) {
         this.binaryConverter = SHELL_BINARY_CONVERTER;
      } else {
         this.binaryConverter = EXTENDED_JSON_BINARY_CONVERTER;
      }

      if (builder.int64Converter != null) {
         this.int64Converter = builder.int64Converter;
      } else if (this.outputMode == JsonMode.STRICT || this.outputMode == JsonMode.EXTENDED) {
         this.int64Converter = EXTENDED_JSON_INT_64_CONVERTER;
      } else if (this.outputMode == JsonMode.RELAXED) {
         this.int64Converter = RELAXED_JSON_INT_64_CONVERTER;
      } else {
         this.int64Converter = SHELL_INT_64_CONVERTER;
      }

      if (builder.decimal128Converter != null) {
         this.decimal128Converter = builder.decimal128Converter;
      } else if (this.outputMode != JsonMode.STRICT && this.outputMode != JsonMode.EXTENDED && this.outputMode != JsonMode.RELAXED) {
         this.decimal128Converter = SHELL_DECIMAL_128_CONVERTER;
      } else {
         this.decimal128Converter = EXTENDED_JSON_DECIMAL_128_CONVERTER;
      }

      if (builder.objectIdConverter != null) {
         this.objectIdConverter = builder.objectIdConverter;
      } else if (this.outputMode != JsonMode.STRICT && this.outputMode != JsonMode.EXTENDED && this.outputMode != JsonMode.RELAXED) {
         this.objectIdConverter = SHELL_OBJECT_ID_CONVERTER;
      } else {
         this.objectIdConverter = EXTENDED_JSON_OBJECT_ID_CONVERTER;
      }

      if (builder.timestampConverter != null) {
         this.timestampConverter = builder.timestampConverter;
      } else if (this.outputMode != JsonMode.STRICT && this.outputMode != JsonMode.EXTENDED && this.outputMode != JsonMode.RELAXED) {
         this.timestampConverter = SHELL_TIMESTAMP_CONVERTER;
      } else {
         this.timestampConverter = EXTENDED_JSON_TIMESTAMP_CONVERTER;
      }

      if (builder.regularExpressionConverter != null) {
         this.regularExpressionConverter = builder.regularExpressionConverter;
      } else if (this.outputMode == JsonMode.EXTENDED || this.outputMode == JsonMode.RELAXED) {
         this.regularExpressionConverter = EXTENDED_JSON_REGULAR_EXPRESSION_CONVERTER;
      } else if (this.outputMode == JsonMode.STRICT) {
         this.regularExpressionConverter = LEGACY_EXTENDED_JSON_REGULAR_EXPRESSION_CONVERTER;
      } else {
         this.regularExpressionConverter = SHELL_REGULAR_EXPRESSION_CONVERTER;
      }
   }

   public boolean isIndent() {
      return this.indent;
   }

   public String getNewLineCharacters() {
      return this.newLineCharacters;
   }

   public String getIndentCharacters() {
      return this.indentCharacters;
   }

   public JsonMode getOutputMode() {
      return this.outputMode;
   }

   public int getMaxLength() {
      return this.maxLength;
   }

   public Converter<BsonNull> getNullConverter() {
      return this.nullConverter;
   }

   public Converter<String> getStringConverter() {
      return this.stringConverter;
   }

   public Converter<BsonBinary> getBinaryConverter() {
      return this.binaryConverter;
   }

   public Converter<Boolean> getBooleanConverter() {
      return this.booleanConverter;
   }

   public Converter<Long> getDateTimeConverter() {
      return this.dateTimeConverter;
   }

   public Converter<Double> getDoubleConverter() {
      return this.doubleConverter;
   }

   public Converter<Integer> getInt32Converter() {
      return this.int32Converter;
   }

   public Converter<Long> getInt64Converter() {
      return this.int64Converter;
   }

   public Converter<Decimal128> getDecimal128Converter() {
      return this.decimal128Converter;
   }

   public Converter<ObjectId> getObjectIdConverter() {
      return this.objectIdConverter;
   }

   public Converter<BsonRegularExpression> getRegularExpressionConverter() {
      return this.regularExpressionConverter;
   }

   public Converter<BsonTimestamp> getTimestampConverter() {
      return this.timestampConverter;
   }

   public Converter<String> getSymbolConverter() {
      return this.symbolConverter;
   }

   public Converter<BsonMinKey> getMinKeyConverter() {
      return this.minKeyConverter;
   }

   public Converter<BsonMaxKey> getMaxKeyConverter() {
      return this.maxKeyConverter;
   }

   public Converter<BsonUndefined> getUndefinedConverter() {
      return this.undefinedConverter;
   }

   public Converter<String> getJavaScriptConverter() {
      return this.javaScriptConverter;
   }

   public static final class Builder {
      private boolean indent;
      private String newLineCharacters = System.getProperty("line.separator");
      private String indentCharacters = "  ";
      private JsonMode outputMode = JsonMode.RELAXED;
      private int maxLength;
      private Converter<BsonNull> nullConverter;
      private Converter<String> stringConverter;
      private Converter<Long> dateTimeConverter;
      private Converter<BsonBinary> binaryConverter;
      private Converter<Boolean> booleanConverter;
      private Converter<Double> doubleConverter;
      private Converter<Integer> int32Converter;
      private Converter<Long> int64Converter;
      private Converter<Decimal128> decimal128Converter;
      private Converter<ObjectId> objectIdConverter;
      private Converter<BsonTimestamp> timestampConverter;
      private Converter<BsonRegularExpression> regularExpressionConverter;
      private Converter<String> symbolConverter;
      private Converter<BsonUndefined> undefinedConverter;
      private Converter<BsonMinKey> minKeyConverter;
      private Converter<BsonMaxKey> maxKeyConverter;
      private Converter<String> javaScriptConverter;

      public JsonWriterSettings build() {
         return new JsonWriterSettings(this);
      }

      public JsonWriterSettings.Builder indent(boolean indent) {
         this.indent = indent;
         return this;
      }

      public JsonWriterSettings.Builder newLineCharacters(String newLineCharacters) {
         Assertions.notNull("newLineCharacters", newLineCharacters);
         this.newLineCharacters = newLineCharacters;
         return this;
      }

      public JsonWriterSettings.Builder indentCharacters(String indentCharacters) {
         Assertions.notNull("indentCharacters", indentCharacters);
         this.indentCharacters = indentCharacters;
         return this;
      }

      public JsonWriterSettings.Builder outputMode(JsonMode outputMode) {
         Assertions.notNull("outputMode", outputMode);
         this.outputMode = outputMode;
         return this;
      }

      public JsonWriterSettings.Builder maxLength(int maxLength) {
         Assertions.isTrueArgument("maxLength >= 0", maxLength >= 0);
         this.maxLength = maxLength;
         return this;
      }

      public JsonWriterSettings.Builder nullConverter(Converter<BsonNull> nullConverter) {
         this.nullConverter = nullConverter;
         return this;
      }

      public JsonWriterSettings.Builder stringConverter(Converter<String> stringConverter) {
         this.stringConverter = stringConverter;
         return this;
      }

      public JsonWriterSettings.Builder dateTimeConverter(Converter<Long> dateTimeConverter) {
         this.dateTimeConverter = dateTimeConverter;
         return this;
      }

      public JsonWriterSettings.Builder binaryConverter(Converter<BsonBinary> binaryConverter) {
         this.binaryConverter = binaryConverter;
         return this;
      }

      public JsonWriterSettings.Builder booleanConverter(Converter<Boolean> booleanConverter) {
         this.booleanConverter = booleanConverter;
         return this;
      }

      public JsonWriterSettings.Builder doubleConverter(Converter<Double> doubleConverter) {
         this.doubleConverter = doubleConverter;
         return this;
      }

      public JsonWriterSettings.Builder int32Converter(Converter<Integer> int32Converter) {
         this.int32Converter = int32Converter;
         return this;
      }

      public JsonWriterSettings.Builder int64Converter(Converter<Long> int64Converter) {
         this.int64Converter = int64Converter;
         return this;
      }

      public JsonWriterSettings.Builder decimal128Converter(Converter<Decimal128> decimal128Converter) {
         this.decimal128Converter = decimal128Converter;
         return this;
      }

      public JsonWriterSettings.Builder objectIdConverter(Converter<ObjectId> objectIdConverter) {
         this.objectIdConverter = objectIdConverter;
         return this;
      }

      public JsonWriterSettings.Builder timestampConverter(Converter<BsonTimestamp> timestampConverter) {
         this.timestampConverter = timestampConverter;
         return this;
      }

      public JsonWriterSettings.Builder regularExpressionConverter(Converter<BsonRegularExpression> regularExpressionConverter) {
         this.regularExpressionConverter = regularExpressionConverter;
         return this;
      }

      public JsonWriterSettings.Builder symbolConverter(Converter<String> symbolConverter) {
         this.symbolConverter = symbolConverter;
         return this;
      }

      public JsonWriterSettings.Builder minKeyConverter(Converter<BsonMinKey> minKeyConverter) {
         this.minKeyConverter = minKeyConverter;
         return this;
      }

      public JsonWriterSettings.Builder maxKeyConverter(Converter<BsonMaxKey> maxKeyConverter) {
         this.maxKeyConverter = maxKeyConverter;
         return this;
      }

      public JsonWriterSettings.Builder undefinedConverter(Converter<BsonUndefined> undefinedConverter) {
         this.undefinedConverter = undefinedConverter;
         return this;
      }

      public JsonWriterSettings.Builder javaScriptConverter(Converter<String> javaScriptConverter) {
         this.javaScriptConverter = javaScriptConverter;
         return this;
      }

      private Builder() {
      }
   }
}
