package org.jline.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jline.terminal.impl.AbstractPosixTerminal;
import org.jline.terminal.impl.AbstractTerminal;
import org.jline.terminal.impl.DumbTerminalProvider;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalExt;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.Log;
import org.jline.utils.OSUtils;

public final class TerminalBuilder {
   public static final String PROP_ENCODING = "org.jline.terminal.encoding";
   public static final String PROP_STDIN_ENCODING = "org.jline.terminal.stdin.encoding";
   public static final String PROP_STDOUT_ENCODING = "org.jline.terminal.stdout.encoding";
   public static final String PROP_STDERR_ENCODING = "org.jline.terminal.stderr.encoding";
   public static final String PROP_CODEPAGE = "org.jline.terminal.codepage";
   public static final String PROP_TYPE = "org.jline.terminal.type";
   public static final String PROP_PROVIDER = "org.jline.terminal.provider";
   public static final String PROP_PROVIDERS = "org.jline.terminal.providers";
   public static final String PROP_PROVIDER_FFM = "ffm";
   public static final String PROP_PROVIDER_JNI = "jni";
   public static final String PROP_PROVIDER_JANSI = "jansi";
   public static final String PROP_PROVIDER_JNA = "jna";
   public static final String PROP_PROVIDER_EXEC = "exec";
   public static final String PROP_PROVIDER_DUMB = "dumb";
   public static final String PROP_PROVIDERS_DEFAULT = String.join(",", "ffm", "jni", "jansi", "jna", "exec");
   public static final String PROP_FFM = "org.jline.terminal.ffm";
   public static final String PROP_JNI = "org.jline.terminal.jni";
   public static final String PROP_JANSI = "org.jline.terminal.jansi";
   public static final String PROP_JNA = "org.jline.terminal.jna";
   public static final String PROP_EXEC = "org.jline.terminal.exec";
   public static final String PROP_DUMB = "org.jline.terminal.dumb";
   public static final String PROP_DUMB_COLOR = "org.jline.terminal.dumb.color";
   public static final String PROP_OUTPUT = "org.jline.terminal.output";
   public static final String PROP_OUTPUT_OUT = "out";
   public static final String PROP_OUTPUT_ERR = "err";
   public static final String PROP_OUTPUT_OUT_ERR = "out-err";
   public static final String PROP_OUTPUT_ERR_OUT = "err-out";
   public static final String PROP_OUTPUT_FORCED_OUT = "forced-out";
   public static final String PROP_OUTPUT_FORCED_ERR = "forced-err";
   public static final String PROP_NON_BLOCKING_READS = "org.jline.terminal.pty.nonBlockingReads";
   public static final String PROP_COLOR_DISTANCE = "org.jline.utils.colorDistance";
   public static final String PROP_DISABLE_ALTERNATE_CHARSET = "org.jline.utils.disableAlternateCharset";
   public static final String PROP_FILE_DESCRIPTOR_CREATION_MODE = "org.jline.terminal.pty.fileDescriptorCreationMode";
   public static final String PROP_FILE_DESCRIPTOR_CREATION_MODE_NATIVE = "native";
   public static final String PROP_FILE_DESCRIPTOR_CREATION_MODE_REFLECTION = "reflection";
   public static final String PROP_FILE_DESCRIPTOR_CREATION_MODE_DEFAULT = String.join(",", "reflection", "native");
   public static final String PROP_REDIRECT_PIPE_CREATION_MODE = "org.jline.terminal.exec.redirectPipeCreationMode";
   public static final String PROP_REDIRECT_PIPE_CREATION_MODE_NATIVE = "native";
   public static final String PROP_REDIRECT_PIPE_CREATION_MODE_REFLECTION = "reflection";
   public static final String PROP_REDIRECT_PIPE_CREATION_MODE_DEFAULT = String.join(",", "reflection", "native");
   public static final Set<String> DEPRECATED_PROVIDERS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("jna", "jansi")));
   public static final String PROP_DISABLE_DEPRECATED_PROVIDER_WARNING = "org.jline.terminal.disableDeprecatedProviderWarning";
   private static final AtomicReference<Terminal> SYSTEM_TERMINAL = new AtomicReference<>();
   private static final AtomicReference<Terminal> TERMINAL_OVERRIDE = new AtomicReference<>();
   private String name;
   private InputStream in;
   private OutputStream out;
   private String type;
   private Charset encoding;
   private Charset stdinEncoding;
   private Charset stdoutEncoding;
   private Charset stderrEncoding;
   private int codepage;
   private Boolean system;
   private TerminalBuilder.SystemOutput systemOutput;
   private String provider;
   private String providers;
   private Boolean jna;
   private Boolean jansi;
   private Boolean jni;
   private Boolean exec;
   private Boolean ffm;
   private Boolean dumb;
   private Boolean color;
   private Attributes attributes;
   private Size size;
   private boolean nativeSignals = true;
   private Terminal.SignalHandler signalHandler = Terminal.SignalHandler.SIG_DFL;
   private boolean paused = false;
   private static final int UTF8_CODE_PAGE = 65001;

   public static Terminal terminal() throws IOException {
      return builder().build();
   }

   public static TerminalBuilder builder() {
      return new TerminalBuilder();
   }

   private TerminalBuilder() {
   }

   public TerminalBuilder name(String name) {
      this.name = name;
      return this;
   }

   public TerminalBuilder streams(InputStream in, OutputStream out) {
      this.in = in;
      this.out = out;
      return this;
   }

   public TerminalBuilder system(boolean system) {
      this.system = system;
      return this;
   }

   public TerminalBuilder systemOutput(TerminalBuilder.SystemOutput systemOutput) {
      this.systemOutput = systemOutput;
      return this;
   }

   public TerminalBuilder provider(String provider) {
      this.provider = provider;
      return this;
   }

   public TerminalBuilder providers(String providers) {
      this.providers = providers;
      return this;
   }

   public TerminalBuilder jna(boolean jna) {
      this.jna = jna;
      return this;
   }

   public TerminalBuilder jansi(boolean jansi) {
      this.jansi = jansi;
      return this;
   }

   public TerminalBuilder jni(boolean jni) {
      this.jni = jni;
      return this;
   }

   public TerminalBuilder exec(boolean exec) {
      this.exec = exec;
      return this;
   }

   public TerminalBuilder ffm(boolean ffm) {
      this.ffm = ffm;
      return this;
   }

   public TerminalBuilder dumb(boolean dumb) {
      this.dumb = dumb;
      return this;
   }

   public TerminalBuilder type(String type) {
      this.type = type;
      return this;
   }

   public TerminalBuilder color(boolean color) {
      this.color = color;
      return this;
   }

   public TerminalBuilder encoding(String encoding) throws UnsupportedCharsetException {
      return this.encoding(encoding != null ? Charset.forName(encoding) : null);
   }

   public TerminalBuilder encoding(Charset encoding) {
      this.encoding = encoding;
      return this;
   }

   public TerminalBuilder stdinEncoding(String encoding) throws UnsupportedCharsetException {
      return this.stdinEncoding(encoding != null ? Charset.forName(encoding) : null);
   }

   public TerminalBuilder stdinEncoding(Charset encoding) {
      this.stdinEncoding = encoding;
      return this;
   }

   public TerminalBuilder stdoutEncoding(String encoding) throws UnsupportedCharsetException {
      return this.stdoutEncoding(encoding != null ? Charset.forName(encoding) : null);
   }

   public TerminalBuilder stdoutEncoding(Charset encoding) {
      this.stdoutEncoding = encoding;
      return this;
   }

   public TerminalBuilder stderrEncoding(String encoding) throws UnsupportedCharsetException {
      return this.stderrEncoding(encoding != null ? Charset.forName(encoding) : null);
   }

   public TerminalBuilder stderrEncoding(Charset encoding) {
      this.stderrEncoding = encoding;
      return this;
   }

   @Deprecated
   public TerminalBuilder codepage(int codepage) {
      this.codepage = codepage;
      return this;
   }

   public TerminalBuilder attributes(Attributes attributes) {
      this.attributes = attributes;
      return this;
   }

   public TerminalBuilder size(Size size) {
      this.size = size;
      return this;
   }

   public TerminalBuilder nativeSignals(boolean nativeSignals) {
      this.nativeSignals = nativeSignals;
      return this;
   }

   public TerminalBuilder signalHandler(Terminal.SignalHandler signalHandler) {
      this.signalHandler = signalHandler;
      return this;
   }

   public TerminalBuilder paused(boolean paused) {
      this.paused = paused;
      return this;
   }

   public Terminal build() throws IOException {
      Terminal override = TERMINAL_OVERRIDE.get();
      Terminal terminal = override != null ? override : this.doBuild();
      if (override != null) {
         Log.debug(() -> "Overriding terminal with global value set by TerminalBuilder.setTerminalOverride");
      }

      Log.debug(() -> "Using terminal " + terminal.getClass().getSimpleName());
      if (terminal instanceof AbstractPosixTerminal) {
         Log.debug(() -> "Using pty " + ((AbstractPosixTerminal)terminal).getPty().getClass().getSimpleName());
      }

      return terminal;
   }

   private Terminal doBuild() throws IOException {
      String name = this.name;
      if (name == null) {
         name = "JLine terminal";
      }

      Charset encoding = this.computeEncoding();
      Charset stdinEncoding = this.computeStdinEncoding();
      Charset stdoutEncoding = this.computeStdoutEncoding();
      Charset stderrEncoding = this.computeStderrEncoding();
      String type = this.computeType();
      String provider = this.provider;
      if (provider == null) {
         provider = System.getProperty("org.jline.terminal.provider", null);
      }

      boolean forceDumb = "dumb".equals(type) || type != null && type.startsWith("dumb-color") || provider != null && provider.equals("dumb");
      Boolean dumb = this.dumb;
      if (dumb == null) {
         dumb = getBoolean("org.jline.terminal.dumb", null);
      }

      IllegalStateException exception = new IllegalStateException("Unable to create a terminal");
      List<TerminalProvider> providers = this.getProviders(provider, exception);
      Terminal terminal = null;
      if (this.system != null && this.system || this.system == null && this.in == null && this.out == null) {
         if (this.system != null
            && (this.in != null && !this.in.equals(System.in) || this.out != null && !this.out.equals(System.out) && !this.out.equals(System.err))) {
            throw new IllegalArgumentException("Cannot create a system terminal using non System streams");
         }

         if (this.attributes != null || this.size != null) {
            Log.warn("Attributes and size fields are ignored when creating a system terminal");
         }

         TerminalBuilder.SystemOutput systemOutput = this.computeSystemOutput();
         Map<SystemStream, Boolean> system = Stream.of(SystemStream.values())
            .collect(Collectors.toMap(stream -> (SystemStream)stream, stream -> providers.stream().anyMatch(p -> p.isSystemStream(stream))));
         SystemStream systemStream = this.select(system, systemOutput);
         if (!forceDumb && system.get(SystemStream.Input) && systemStream != null) {
            if (this.attributes != null || this.size != null) {
               Log.warn("Attributes and size fields are ignored when creating a system terminal");
            }

            boolean ansiPassThrough = OSUtils.IS_CONEMU;
            if ((OSUtils.IS_CYGWIN || OSUtils.IS_MSYSTEM) && "xterm".equals(type) && this.type == null && System.getProperty("org.jline.terminal.type") == null
               )
             {
               type = "xterm-256color";
            }

            for (TerminalProvider prov : providers) {
               if (terminal == null) {
                  try {
                     Charset outputEncoding = systemStream == SystemStream.Error ? stderrEncoding : stdoutEncoding;
                     terminal = prov.sysTerminal(
                        name, type, ansiPassThrough, encoding, stdinEncoding, outputEncoding, this.nativeSignals, this.signalHandler, this.paused, systemStream
                     );
                  } catch (Throwable var21) {
                     Log.debug("Error creating " + prov.name() + " based terminal: ", var21.getMessage(), var21);
                     exception.addSuppressed(var21);
                  }
               }
            }

            if (terminal == null && OSUtils.IS_WINDOWS && providers.isEmpty() && (dumb == null || !dumb)) {
               throw new IllegalStateException(
                  "Unable to create a system terminal. On Windows, either JLine's native libraries, JNA or Jansi library is required.  Make sure to add one of those in the classpath.",
                  exception
               );
            }
         }

         if (terminal instanceof AbstractTerminal) {
            AbstractTerminal t = (AbstractTerminal)terminal;
            if (SYSTEM_TERMINAL.compareAndSet(null, t)) {
               t.setOnClose(() -> SYSTEM_TERMINAL.compareAndSet(t, null));
            } else {
               exception.addSuppressed(
                  new IllegalStateException(
                     "A system terminal is already running. Make sure to use the created system Terminal on the LineReaderBuilder if you're using one or that previously created system Terminals have been correctly closed."
                  )
               );
               terminal.close();
               terminal = null;
            }
         }

         if (terminal == null && (forceDumb || dumb == null || dumb)) {
            if (!forceDumb && dumb == null) {
               if (Log.isDebugEnabled()) {
                  Log.warn("input is tty: " + system.get(SystemStream.Input));
                  Log.warn("output is tty: " + system.get(SystemStream.Output));
                  Log.warn("error is tty: " + system.get(SystemStream.Error));
                  Log.warn("Creating a dumb terminal", exception);
               } else {
                  Log.warn("Unable to create a system terminal, creating a dumb terminal (enable debug logging for more information)");
               }
            }

            type = this.getDumbTerminalType(dumb, systemStream);
            Charset outputEncoding = systemStream == SystemStream.Error ? stderrEncoding : stdoutEncoding;
            terminal = new DumbTerminalProvider()
               .sysTerminal(name, type, false, encoding, stdinEncoding, outputEncoding, this.nativeSignals, this.signalHandler, this.paused, systemStream);
            if (OSUtils.IS_WINDOWS) {
               Attributes attr = terminal.getAttributes();
               attr.setInputFlag(Attributes.InputFlag.IGNCR, true);
               terminal.setAttributes(attr);
            }
         }
      } else {
         for (TerminalProvider provx : providers) {
            if (terminal == null) {
               try {
                  terminal = provx.newTerminal(
                     name, type, this.in, this.out, encoding, stdinEncoding, stdoutEncoding, this.signalHandler, this.paused, this.attributes, this.size
                  );
               } catch (Throwable var20) {
                  Log.debug("Error creating " + provx.name() + " based terminal: ", var20.getMessage(), var20);
                  exception.addSuppressed(var20);
               }
            }
         }
      }

      if (terminal == null) {
         throw exception;
      } else {
         if (terminal instanceof TerminalExt) {
            TerminalExt te = (TerminalExt)terminal;
            if (DEPRECATED_PROVIDERS.contains(te.getProvider().name()) && !getBoolean("org.jline.terminal.disableDeprecatedProviderWarning", false)) {
               Log.warn(
                  "The terminal provider "
                     + te.getProvider().name()
                     + " has been deprecated, check your configuration. This warning can be disabled by setting the system property "
                     + "org.jline.terminal.disableDeprecatedProviderWarning"
                     + " to true."
               );
            }
         }

         return terminal;
      }
   }

   private String getDumbTerminalType(Boolean dumb, SystemStream systemStream) {
      Boolean color = this.color;
      if (color == null) {
         color = getBoolean("org.jline.terminal.dumb.color", null);
      }

      if (dumb == null) {
         if (color == null) {
            String emacs = System.getenv("INSIDE_EMACS");
            if (emacs != null && emacs.contains("comint")) {
               color = true;
            }
         }

         if (color == null) {
            String ideHome = System.getenv("IDE_HOME");
            if (ideHome != null) {
               color = true;
            } else {
               String command = getParentProcessCommand();
               if (command != null && command.endsWith("/idea")) {
                  color = true;
               }
            }
         }

         if (color == null) {
            color = systemStream != null && System.getenv("TERM") != null;
         }
      } else if (color == null) {
         color = false;
      }

      return color ? "dumb-color" : "dumb";
   }

   public TerminalBuilder.SystemOutput computeSystemOutput() {
      TerminalBuilder.SystemOutput systemOutput = null;
      if (this.out != null) {
         if (this.out.equals(System.out)) {
            systemOutput = TerminalBuilder.SystemOutput.SysOut;
         } else if (this.out.equals(System.err)) {
            systemOutput = TerminalBuilder.SystemOutput.SysErr;
         }
      }

      if (systemOutput == null) {
         systemOutput = this.systemOutput;
      }

      if (systemOutput == null) {
         String str = System.getProperty("org.jline.terminal.output");
         if (str != null) {
            String var3 = str.trim().toLowerCase(Locale.ROOT);
            switch (var3) {
               case "out":
                  systemOutput = TerminalBuilder.SystemOutput.SysOut;
                  break;
               case "err":
                  systemOutput = TerminalBuilder.SystemOutput.SysErr;
                  break;
               case "out-err":
                  systemOutput = TerminalBuilder.SystemOutput.SysOutOrSysErr;
                  break;
               case "err-out":
                  systemOutput = TerminalBuilder.SystemOutput.SysErrOrSysOut;
                  break;
               case "forced-out":
                  systemOutput = TerminalBuilder.SystemOutput.ForcedSysOut;
                  break;
               case "forced-err":
                  systemOutput = TerminalBuilder.SystemOutput.ForcedSysErr;
                  break;
               default:
                  Log.debug(
                     "Unsupported value for org.jline.terminal.output: "
                        + str
                        + ". Supported values are: "
                        + String.join(", ", "out", "err", "out-err", "err-out")
                        + "."
                  );
            }
         }
      }

      if (systemOutput == null) {
         systemOutput = TerminalBuilder.SystemOutput.SysOutOrSysErr;
      }

      return systemOutput;
   }

   public String computeType() {
      String type = this.type;
      if (type == null) {
         type = System.getProperty("org.jline.terminal.type");
      }

      if (type == null) {
         type = System.getenv("TERM");
      }

      return type;
   }

   public Charset computeEncoding() {
      Charset encoding = this.encoding;
      if (encoding == null) {
         String charsetName = System.getProperty("org.jline.terminal.encoding");
         if (charsetName != null && Charset.isSupported(charsetName)) {
            encoding = Charset.forName(charsetName);
         }
      }

      if (encoding == null) {
         int codepage = this.codepage;
         if (codepage <= 0) {
            String str = System.getProperty("org.jline.terminal.codepage");
            if (str != null) {
               codepage = Integer.parseInt(str);
            }
         }

         if (codepage >= 0) {
            encoding = getCodepageCharset(codepage);
         } else {
            encoding = StandardCharsets.UTF_8;
         }
      }

      return encoding;
   }

   public Charset computeStdinEncoding() {
      return this.computeSpecificEncoding(this.stdinEncoding, "org.jline.terminal.stdin.encoding", "stdin.encoding");
   }

   public Charset computeStdoutEncoding() {
      return this.computeSpecificEncoding(this.stdoutEncoding, "org.jline.terminal.stdout.encoding", "stdout.encoding");
   }

   public Charset computeStderrEncoding() {
      return this.computeSpecificEncoding(this.stderrEncoding, "org.jline.terminal.stderr.encoding", "stderr.encoding");
   }

   private Charset computeSpecificEncoding(Charset specificEncoding, String jlineProperty, String standardProperty) {
      Charset encoding = specificEncoding;
      if (specificEncoding == null) {
         String charsetName = System.getProperty(jlineProperty);
         if (charsetName != null && Charset.isSupported(charsetName)) {
            encoding = Charset.forName(charsetName);
         }

         if (encoding == null) {
            charsetName = System.getProperty(standardProperty);
            if (charsetName != null && Charset.isSupported(charsetName)) {
               encoding = Charset.forName(charsetName);
            }
         }
      }

      if (encoding == null) {
         encoding = this.computeEncoding();
      }

      return encoding;
   }

   public List<TerminalProvider> getProviders(String provider, IllegalStateException exception) {
      List<TerminalProvider> providers = new ArrayList<>();
      this.checkProvider(provider, exception, providers, this.ffm, "org.jline.terminal.ffm", "ffm");
      this.checkProvider(provider, exception, providers, this.jni, "org.jline.terminal.jni", "jni");
      this.checkProvider(provider, exception, providers, this.jansi, "org.jline.terminal.jansi", "jansi");
      this.checkProvider(provider, exception, providers, this.jna, "org.jline.terminal.jna", "jna");
      this.checkProvider(provider, exception, providers, this.exec, "org.jline.terminal.exec", "exec");
      List<String> order = Arrays.asList(
         (this.providers != null ? this.providers : System.getProperty("org.jline.terminal.providers", PROP_PROVIDERS_DEFAULT)).split(",")
      );
      providers.sort(Comparator.comparing(l -> {
         int idx = order.indexOf(l.name());
         return idx >= 0 ? idx : Integer.MAX_VALUE;
      }));
      String names = providers.stream().map(TerminalProvider::name).collect(Collectors.joining(", "));
      Log.debug("Available providers: " + names);
      return providers;
   }

   private void checkProvider(String provider, IllegalStateException exception, List<TerminalProvider> providers, Boolean load, String property, String name) {
      Boolean doLoad = provider != null ? name.equals(provider) : load;
      if (doLoad == null) {
         doLoad = getBoolean(property, true);
      }

      if (doLoad) {
         try {
            TerminalProvider prov = TerminalProvider.load(name);
            prov.isSystemStream(SystemStream.Output);
            providers.add(prov);
         } catch (Throwable var9) {
            Log.debug("Unable to load " + name + " provider: ", var9);
            exception.addSuppressed(var9);
         }
      }
   }

   private SystemStream select(Map<SystemStream, Boolean> system, TerminalBuilder.SystemOutput systemOutput) {
      switch (systemOutput) {
         case SysOut:
            return select(system, SystemStream.Output);
         case SysErr:
            return select(system, SystemStream.Error);
         case SysOutOrSysErr:
            return select(system, SystemStream.Output, SystemStream.Error);
         case SysErrOrSysOut:
            return select(system, SystemStream.Error, SystemStream.Output);
         case ForcedSysOut:
            return SystemStream.Output;
         case ForcedSysErr:
            return SystemStream.Error;
         default:
            return null;
      }
   }

   private static SystemStream select(Map<SystemStream, Boolean> system, SystemStream... streams) {
      for (SystemStream s : streams) {
         if (system.get(s)) {
            return s;
         }
      }

      return null;
   }

   private static String getParentProcessCommand() {
      try {
         Class<?> phClass = Class.forName("java.lang.ProcessHandle");
         Object current = phClass.getMethod("current").invoke(null);
         Object parent = ((Optional)phClass.getMethod("parent").invoke(current)).orElse(null);
         Method infoMethod = phClass.getMethod("info");
         Object info = infoMethod.invoke(parent);
         Object command = ((Optional)infoMethod.getReturnType().getMethod("command").invoke(info)).orElse(null);
         return (String)command;
      } catch (Throwable var6) {
         return null;
      }
   }

   private static Boolean getBoolean(String name, Boolean def) {
      try {
         String str = System.getProperty(name);
         if (str != null) {
            return Boolean.parseBoolean(str);
         }
      } catch (NullPointerException | IllegalArgumentException var3) {
      }

      return def;
   }

   private static <S> S load(Class<S> clazz) {
      return ServiceLoader.load(clazz, clazz.getClassLoader()).iterator().next();
   }

   private static Charset getCodepageCharset(int codepage) {
      if (codepage == 65001) {
         return StandardCharsets.UTF_8;
      } else {
         String charsetMS = "ms" + codepage;
         if (Charset.isSupported(charsetMS)) {
            return Charset.forName(charsetMS);
         } else {
            String charsetCP = "cp" + codepage;
            return Charset.isSupported(charsetCP) ? Charset.forName(charsetCP) : Charset.defaultCharset();
         }
      }
   }

   @Deprecated
   public static void setTerminalOverride(Terminal terminal) {
      TERMINAL_OVERRIDE.set(terminal);
   }

   public static enum SystemOutput {
      SysOut,
      SysErr,
      SysOutOrSysErr,
      SysErrOrSysOut,
      ForcedSysOut,
      ForcedSysErr;
   }
}
