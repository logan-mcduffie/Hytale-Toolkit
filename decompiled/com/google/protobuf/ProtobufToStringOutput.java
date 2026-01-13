package com.google.protobuf;

public final class ProtobufToStringOutput {
   private static final ThreadLocal<ProtobufToStringOutput.OutputMode> outputMode = ThreadLocal.withInitial(
      () -> ProtobufToStringOutput.OutputMode.DEFAULT_FORMAT
   );

   private ProtobufToStringOutput() {
   }

   private static ProtobufToStringOutput.OutputMode setOutputMode(ProtobufToStringOutput.OutputMode newMode) {
      ProtobufToStringOutput.OutputMode oldMode = outputMode.get();
      outputMode.set(newMode);
      return oldMode;
   }

   private static void callWithSpecificFormat(Runnable impl, ProtobufToStringOutput.OutputMode mode) {
      ProtobufToStringOutput.OutputMode oldMode = setOutputMode(mode);

      try {
         impl.run();
      } finally {
         ProtobufToStringOutput.OutputMode var5 = setOutputMode(oldMode);
      }
   }

   public static void callWithDebugFormat(Runnable impl) {
      callWithSpecificFormat(impl, ProtobufToStringOutput.OutputMode.DEBUG_FORMAT);
   }

   public static void callWithTextFormat(Runnable impl) {
      callWithSpecificFormat(impl, ProtobufToStringOutput.OutputMode.TEXT_FORMAT);
   }

   public static boolean shouldOutputDebugFormat() {
      return outputMode.get() == ProtobufToStringOutput.OutputMode.DEBUG_FORMAT;
   }

   public static boolean isDefaultFormat() {
      return outputMode.get() == ProtobufToStringOutput.OutputMode.DEFAULT_FORMAT;
   }

   private static enum OutputMode {
      DEBUG_FORMAT,
      TEXT_FORMAT,
      DEFAULT_FORMAT;
   }
}
