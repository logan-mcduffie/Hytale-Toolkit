package com.google.protobuf;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class LegacyUnredactedTextFormat {
   private LegacyUnredactedTextFormat() {
   }

   public static String legacyUnredactedMultilineString(MessageOrBuilder message) {
      return TextFormat.printer().printToString(message, TextFormat.Printer.FieldReporterLevel.LEGACY_MULTILINE);
   }

   public static String legacyUnredactedMultilineString(UnknownFieldSet fields) {
      return TextFormat.printer().printToString(fields);
   }

   public static String legacyUnredactedSingleLineString(MessageOrBuilder message) {
      return TextFormat.printer().emittingSingleLine(true).printToString(message, TextFormat.Printer.FieldReporterLevel.LEGACY_SINGLE_LINE);
   }

   public static String legacyUnredactedSingleLineString(UnknownFieldSet fields) {
      return TextFormat.printer().emittingSingleLine(true).printToString(fields);
   }

   public static String legacyUnredactedToString(Object object) {
      String[] result = new String[1];
      ProtobufToStringOutput.callWithTextFormat(() -> result[0] = object.toString());
      return result[0];
   }

   public static String legacyUnredactedStringValueOf(Object object) {
      return object == null ? String.valueOf(object) : legacyUnredactedToString(object);
   }

   @Deprecated
   public static Iterable<String> legacyUnredactedToStringList(Iterable<?> iterable) {
      return iterable == null
         ? null
         : StreamSupport.stream(iterable.spliterator(), false).map(LegacyUnredactedTextFormat::legacyUnredactedStringValueOf).collect(Collectors.toList());
   }

   @Deprecated
   public static String[] legacyUnredactedToStringArray(Object[] objects) {
      return objects == null ? null : Arrays.stream(objects).map(LegacyUnredactedTextFormat::legacyUnredactedStringValueOf).toArray(String[]::new);
   }

   public static String legacyUnredactedStringFormat(String format, Object... args) {
      String[] result = new String[1];
      ProtobufToStringOutput.callWithTextFormat(() -> result[0] = String.format(format, args));
      return result[0];
   }
}
