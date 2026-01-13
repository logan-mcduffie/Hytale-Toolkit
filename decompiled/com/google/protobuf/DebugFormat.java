package com.google.protobuf;

public final class DebugFormat {
   private final boolean isSingleLine;

   private TextFormat.Printer getPrinter() {
      TextFormat.Printer printer = TextFormat.debugFormatPrinter();
      return this.isSingleLine ? printer.emittingSingleLine(true) : printer;
   }

   private DebugFormat(boolean singleLine) {
      this.isSingleLine = singleLine;
   }

   public static DebugFormat singleLine() {
      return new DebugFormat(true);
   }

   public static DebugFormat multiline() {
      return new DebugFormat(false);
   }

   public String toString(MessageOrBuilder message) {
      TextFormat.Printer.FieldReporterLevel fieldReporterLevel = this.isSingleLine
         ? TextFormat.Printer.FieldReporterLevel.DEBUG_SINGLE_LINE
         : TextFormat.Printer.FieldReporterLevel.DEBUG_MULTILINE;
      return this.getPrinter().printToString(message, fieldReporterLevel);
   }

   public String toString(Descriptors.FieldDescriptor field, Object value) {
      return this.getPrinter().printFieldToString(field, value);
   }

   public String toString(UnknownFieldSet fields) {
      return this.getPrinter().printToString(fields);
   }

   public Object lazyToString(MessageOrBuilder message) {
      return new DebugFormat.LazyDebugOutput(message, this);
   }

   public Object lazyToString(UnknownFieldSet fields) {
      return new DebugFormat.LazyDebugOutput(fields, this);
   }

   private static class LazyDebugOutput {
      private final MessageOrBuilder message;
      private final UnknownFieldSet fields;
      private final DebugFormat format;

      LazyDebugOutput(MessageOrBuilder message, DebugFormat format) {
         this.message = message;
         this.fields = null;
         this.format = format;
      }

      LazyDebugOutput(UnknownFieldSet fields, DebugFormat format) {
         this.message = null;
         this.fields = fields;
         this.format = format;
      }

      @Override
      public String toString() {
         return this.message != null ? this.format.toString(this.message) : this.format.toString(this.fields);
      }
   }
}
