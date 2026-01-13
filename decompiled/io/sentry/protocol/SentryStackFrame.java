package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLockReason;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryStackFrame implements JsonUnknown, JsonSerializable {
   @Nullable
   private List<String> preContext;
   @Nullable
   private List<String> postContext;
   @Nullable
   private Map<String, Object> vars;
   @Nullable
   private List<Integer> framesOmitted;
   @Nullable
   private String filename;
   @Nullable
   private String function;
   @Nullable
   private String module;
   @Nullable
   private Integer lineno;
   @Nullable
   private Integer colno;
   @Nullable
   private String absPath;
   @Nullable
   private String contextLine;
   @Nullable
   private Boolean inApp;
   @Nullable
   private String _package;
   @Nullable
   private Boolean _native;
   @Nullable
   private String platform;
   @Nullable
   private String imageAddr;
   @Nullable
   private String symbolAddr;
   @Nullable
   private String instructionAddr;
   @Nullable
   private String addrMode;
   @Nullable
   private String symbol;
   @Nullable
   private Map<String, Object> unknown;
   @Nullable
   private String rawFunction;
   @Nullable
   private SentryLockReason lock;

   @Nullable
   public List<String> getPreContext() {
      return this.preContext;
   }

   public void setPreContext(@Nullable List<String> preContext) {
      this.preContext = preContext;
   }

   @Nullable
   public List<String> getPostContext() {
      return this.postContext;
   }

   public void setPostContext(@Nullable List<String> postContext) {
      this.postContext = postContext;
   }

   @Nullable
   public Map<String, Object> getVars() {
      return this.vars;
   }

   public void setVars(@Nullable Map<String, Object> vars) {
      this.vars = vars;
   }

   @Nullable
   public List<Integer> getFramesOmitted() {
      return this.framesOmitted;
   }

   public void setFramesOmitted(@Nullable List<Integer> framesOmitted) {
      this.framesOmitted = framesOmitted;
   }

   @Nullable
   public String getFilename() {
      return this.filename;
   }

   public void setFilename(@Nullable String filename) {
      this.filename = filename;
   }

   @Nullable
   public String getFunction() {
      return this.function;
   }

   public void setFunction(@Nullable String function) {
      this.function = function;
   }

   @Nullable
   public String getModule() {
      return this.module;
   }

   public void setModule(@Nullable String module) {
      this.module = module;
   }

   @Nullable
   public Integer getLineno() {
      return this.lineno;
   }

   public void setLineno(@Nullable Integer lineno) {
      this.lineno = lineno;
   }

   @Nullable
   public Integer getColno() {
      return this.colno;
   }

   public void setColno(@Nullable Integer colno) {
      this.colno = colno;
   }

   @Nullable
   public String getAbsPath() {
      return this.absPath;
   }

   public void setAbsPath(@Nullable String absPath) {
      this.absPath = absPath;
   }

   @Nullable
   public String getContextLine() {
      return this.contextLine;
   }

   public void setContextLine(@Nullable String contextLine) {
      this.contextLine = contextLine;
   }

   @Nullable
   public Boolean isInApp() {
      return this.inApp;
   }

   public void setInApp(@Nullable Boolean inApp) {
      this.inApp = inApp;
   }

   @Nullable
   public String getPackage() {
      return this._package;
   }

   public void setPackage(@Nullable String _package) {
      this._package = _package;
   }

   @Nullable
   public String getPlatform() {
      return this.platform;
   }

   public void setPlatform(@Nullable String platform) {
      this.platform = platform;
   }

   @Nullable
   public String getImageAddr() {
      return this.imageAddr;
   }

   public void setImageAddr(@Nullable String imageAddr) {
      this.imageAddr = imageAddr;
   }

   @Nullable
   public String getSymbolAddr() {
      return this.symbolAddr;
   }

   public void setSymbolAddr(@Nullable String symbolAddr) {
      this.symbolAddr = symbolAddr;
   }

   @Nullable
   public String getInstructionAddr() {
      return this.instructionAddr;
   }

   public void setInstructionAddr(@Nullable String instructionAddr) {
      this.instructionAddr = instructionAddr;
   }

   @Nullable
   public String getAddrMode() {
      return this.addrMode;
   }

   public void setAddrMode(@Nullable String addrMode) {
      this.addrMode = addrMode;
   }

   @Nullable
   public Boolean isNative() {
      return this._native;
   }

   public void setNative(@Nullable Boolean _native) {
      this._native = _native;
   }

   @Nullable
   public String getRawFunction() {
      return this.rawFunction;
   }

   public void setRawFunction(@Nullable String rawFunction) {
      this.rawFunction = rawFunction;
   }

   @Nullable
   public String getSymbol() {
      return this.symbol;
   }

   public void setSymbol(@Nullable String symbol) {
      this.symbol = symbol;
   }

   @Nullable
   public SentryLockReason getLock() {
      return this.lock;
   }

   public void setLock(@Nullable SentryLockReason lock) {
      this.lock = lock;
   }

   @Nullable
   @Override
   public Map<String, Object> getUnknown() {
      return this.unknown;
   }

   @Override
   public void setUnknown(@Nullable Map<String, Object> unknown) {
      this.unknown = unknown;
   }

   @Override
   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         SentryStackFrame that = (SentryStackFrame)o;
         return Objects.equals(this.preContext, that.preContext)
            && Objects.equals(this.postContext, that.postContext)
            && Objects.equals(this.vars, that.vars)
            && Objects.equals(this.framesOmitted, that.framesOmitted)
            && Objects.equals(this.filename, that.filename)
            && Objects.equals(this.function, that.function)
            && Objects.equals(this.module, that.module)
            && Objects.equals(this.lineno, that.lineno)
            && Objects.equals(this.colno, that.colno)
            && Objects.equals(this.absPath, that.absPath)
            && Objects.equals(this.contextLine, that.contextLine)
            && Objects.equals(this.inApp, that.inApp)
            && Objects.equals(this._package, that._package)
            && Objects.equals(this._native, that._native)
            && Objects.equals(this.platform, that.platform)
            && Objects.equals(this.imageAddr, that.imageAddr)
            && Objects.equals(this.symbolAddr, that.symbolAddr)
            && Objects.equals(this.instructionAddr, that.instructionAddr)
            && Objects.equals(this.addrMode, that.addrMode)
            && Objects.equals(this.symbol, that.symbol)
            && Objects.equals(this.unknown, that.unknown)
            && Objects.equals(this.rawFunction, that.rawFunction)
            && Objects.equals(this.lock, that.lock);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.preContext,
         this.postContext,
         this.vars,
         this.framesOmitted,
         this.filename,
         this.function,
         this.module,
         this.lineno,
         this.colno,
         this.absPath,
         this.contextLine,
         this.inApp,
         this._package,
         this._native,
         this.platform,
         this.imageAddr,
         this.symbolAddr,
         this.instructionAddr,
         this.addrMode,
         this.symbol,
         this.unknown,
         this.rawFunction,
         this.lock
      );
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.filename != null) {
         writer.name("filename").value(this.filename);
      }

      if (this.function != null) {
         writer.name("function").value(this.function);
      }

      if (this.module != null) {
         writer.name("module").value(this.module);
      }

      if (this.lineno != null) {
         writer.name("lineno").value(this.lineno);
      }

      if (this.colno != null) {
         writer.name("colno").value(this.colno);
      }

      if (this.absPath != null) {
         writer.name("abs_path").value(this.absPath);
      }

      if (this.contextLine != null) {
         writer.name("context_line").value(this.contextLine);
      }

      if (this.inApp != null) {
         writer.name("in_app").value(this.inApp);
      }

      if (this._package != null) {
         writer.name("package").value(this._package);
      }

      if (this._native != null) {
         writer.name("native").value(this._native);
      }

      if (this.platform != null) {
         writer.name("platform").value(this.platform);
      }

      if (this.imageAddr != null) {
         writer.name("image_addr").value(this.imageAddr);
      }

      if (this.symbolAddr != null) {
         writer.name("symbol_addr").value(this.symbolAddr);
      }

      if (this.instructionAddr != null) {
         writer.name("instruction_addr").value(this.instructionAddr);
      }

      if (this.addrMode != null) {
         writer.name("addr_mode").value(this.addrMode);
      }

      if (this.rawFunction != null) {
         writer.name("raw_function").value(this.rawFunction);
      }

      if (this.symbol != null) {
         writer.name("symbol").value(this.symbol);
      }

      if (this.lock != null) {
         writer.name("lock").value(logger, this.lock);
      }

      if (this.preContext != null && !this.preContext.isEmpty()) {
         writer.name("pre_context").value(logger, this.preContext);
      }

      if (this.postContext != null && !this.postContext.isEmpty()) {
         writer.name("post_context").value(logger, this.postContext);
      }

      if (this.vars != null && !this.vars.isEmpty()) {
         writer.name("vars").value(logger, this.vars);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key);
            writer.value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<SentryStackFrame> {
      @NotNull
      public SentryStackFrame deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         SentryStackFrame sentryStackFrame = new SentryStackFrame();
         Map<String, Object> unknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "filename":
                  sentryStackFrame.filename = reader.nextStringOrNull();
                  break;
               case "function":
                  sentryStackFrame.function = reader.nextStringOrNull();
                  break;
               case "module":
                  sentryStackFrame.module = reader.nextStringOrNull();
                  break;
               case "lineno":
                  sentryStackFrame.lineno = reader.nextIntegerOrNull();
                  break;
               case "colno":
                  sentryStackFrame.colno = reader.nextIntegerOrNull();
                  break;
               case "abs_path":
                  sentryStackFrame.absPath = reader.nextStringOrNull();
                  break;
               case "context_line":
                  sentryStackFrame.contextLine = reader.nextStringOrNull();
                  break;
               case "in_app":
                  sentryStackFrame.inApp = reader.nextBooleanOrNull();
                  break;
               case "package":
                  sentryStackFrame._package = reader.nextStringOrNull();
                  break;
               case "native":
                  sentryStackFrame._native = reader.nextBooleanOrNull();
                  break;
               case "platform":
                  sentryStackFrame.platform = reader.nextStringOrNull();
                  break;
               case "image_addr":
                  sentryStackFrame.imageAddr = reader.nextStringOrNull();
                  break;
               case "symbol_addr":
                  sentryStackFrame.symbolAddr = reader.nextStringOrNull();
                  break;
               case "instruction_addr":
                  sentryStackFrame.instructionAddr = reader.nextStringOrNull();
                  break;
               case "addr_mode":
                  sentryStackFrame.addrMode = reader.nextStringOrNull();
                  break;
               case "raw_function":
                  sentryStackFrame.rawFunction = reader.nextStringOrNull();
                  break;
               case "symbol":
                  sentryStackFrame.symbol = reader.nextStringOrNull();
                  break;
               case "lock":
                  sentryStackFrame.lock = reader.nextOrNull(logger, new SentryLockReason.Deserializer());
                  break;
               case "pre_context":
                  sentryStackFrame.preContext = (List<String>)reader.nextObjectOrNull();
                  break;
               case "post_context":
                  sentryStackFrame.postContext = (List<String>)reader.nextObjectOrNull();
                  break;
               case "vars":
                  sentryStackFrame.vars = (Map<String, Object>)reader.nextObjectOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         sentryStackFrame.setUnknown(unknown);
         reader.endObject();
         return sentryStackFrame;
      }
   }

   public static final class JsonKeys {
      public static final String FILENAME = "filename";
      public static final String FUNCTION = "function";
      public static final String MODULE = "module";
      public static final String LINENO = "lineno";
      public static final String COLNO = "colno";
      public static final String ABS_PATH = "abs_path";
      public static final String CONTEXT_LINE = "context_line";
      public static final String IN_APP = "in_app";
      public static final String PACKAGE = "package";
      public static final String NATIVE = "native";
      public static final String PLATFORM = "platform";
      public static final String IMAGE_ADDR = "image_addr";
      public static final String SYMBOL_ADDR = "symbol_addr";
      public static final String INSTRUCTION_ADDR = "instruction_addr";
      public static final String ADDR_MODE = "addr_mode";
      public static final String RAW_FUNCTION = "raw_function";
      public static final String SYMBOL = "symbol";
      public static final String LOCK = "lock";
      public static final String PRE_CONTEXT = "pre_context";
      public static final String POST_CONTEXT = "post_context";
      public static final String VARS = "vars";
   }
}
