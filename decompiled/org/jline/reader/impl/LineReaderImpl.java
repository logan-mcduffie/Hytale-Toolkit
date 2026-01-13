package org.jline.reader.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.Buffer;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.CompletingParsedLine;
import org.jline.reader.CompletionMatcher;
import org.jline.reader.EOFError;
import org.jline.reader.Editor;
import org.jline.reader.EndOfFileException;
import org.jline.reader.Expander;
import org.jline.reader.Highlighter;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.Macro;
import org.jline.reader.MaskingCallback;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.Reference;
import org.jline.reader.SyntaxError;
import org.jline.reader.UserInterruptException;
import org.jline.reader.Widget;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Attributes;
import org.jline.terminal.Cursor;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.MouseSupport;
import org.jline.utils.AttributedCharSequence;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Curses;
import org.jline.utils.Display;
import org.jline.utils.InfoCmp;
import org.jline.utils.Log;
import org.jline.utils.Status;
import org.jline.utils.StyleResolver;
import org.jline.utils.WCWidth;

public class LineReaderImpl implements LineReader, Flushable {
   public static final char NULL_MASK = '\u0000';
   @Deprecated
   public static final int TAB_WIDTH = 4;
   public static final int DEFAULT_TAB_WIDTH = 4;
   public static final String DEFAULT_WORDCHARS = "*?_-.[]~=/&;!#$%^(){}<>";
   public static final String DEFAULT_REMOVE_SUFFIX_CHARS = " \t\n;&|";
   public static final String DEFAULT_COMMENT_BEGIN = "#";
   public static final String DEFAULT_SEARCH_TERMINATORS = "\u001b\n";
   public static final String DEFAULT_BELL_STYLE = "";
   public static final int DEFAULT_LIST_MAX = 100;
   public static final int DEFAULT_MENU_LIST_MAX = Integer.MAX_VALUE;
   public static final int DEFAULT_ERRORS = 2;
   public static final long DEFAULT_BLINK_MATCHING_PAREN = 500L;
   public static final long DEFAULT_AMBIGUOUS_BINDING = 1000L;
   public static final String DEFAULT_SECONDARY_PROMPT_PATTERN = "%M> ";
   public static final String DEFAULT_OTHERS_GROUP_NAME = "others";
   public static final String DEFAULT_ORIGINAL_GROUP_NAME = "original";
   public static final String DEFAULT_COMPLETION_STYLE_STARTING = "fg:cyan";
   public static final String DEFAULT_COMPLETION_STYLE_DESCRIPTION = "fg:bright-black";
   public static final String DEFAULT_COMPLETION_STYLE_GROUP = "fg:bright-magenta,bold";
   public static final String DEFAULT_COMPLETION_STYLE_SELECTION = "inverse";
   public static final String DEFAULT_COMPLETION_STYLE_BACKGROUND = "bg:default";
   public static final String DEFAULT_COMPLETION_STYLE_LIST_STARTING = "fg:cyan";
   public static final String DEFAULT_COMPLETION_STYLE_LIST_DESCRIPTION = "fg:bright-black";
   public static final String DEFAULT_COMPLETION_STYLE_LIST_GROUP = "fg:black,bold";
   public static final String DEFAULT_COMPLETION_STYLE_LIST_SELECTION = "inverse";
   public static final String DEFAULT_COMPLETION_STYLE_LIST_BACKGROUND = "bg:bright-magenta";
   public static final int DEFAULT_INDENTATION = 0;
   public static final int DEFAULT_FEATURES_MAX_BUFFER_SIZE = 1000;
   public static final int DEFAULT_SUGGESTIONS_MIN_BUFFER_SIZE = 1;
   public static final String DEFAULT_SYSTEM_PROPERTY_PREFIX = "org.jline.reader.props.";
   private static final int MIN_ROWS = 3;
   public static final String BRACKETED_PASTE_ON = "\u001b[?2004h";
   public static final String BRACKETED_PASTE_OFF = "\u001b[?2004l";
   public static final String BRACKETED_PASTE_BEGIN = "\u001b[200~";
   public static final String BRACKETED_PASTE_END = "\u001b[201~";
   public static final String FOCUS_IN_SEQ = "\u001b[I";
   public static final String FOCUS_OUT_SEQ = "\u001b[O";
   public static final int DEFAULT_MAX_REPEAT_COUNT = 9999;
   protected final Terminal terminal;
   protected final String appName;
   protected final Map<String, KeyMap<Binding>> keyMaps;
   protected final Map<String, Object> variables;
   protected History history = new DefaultHistory();
   protected Completer completer = null;
   protected Highlighter highlighter = new DefaultHighlighter();
   protected Parser parser = new DefaultParser();
   protected Expander expander = new DefaultExpander();
   protected CompletionMatcher completionMatcher = new CompletionMatcherImpl();
   protected final Map<LineReader.Option, Boolean> options = new HashMap<>();
   protected Thread maskThread = null;
   protected final Buffer buf = new BufferImpl();
   protected String tailTip = "";
   protected LineReader.SuggestionType autosuggestion = LineReader.SuggestionType.NONE;
   protected final Size size = new Size();
   protected AttributedString prompt = AttributedString.EMPTY;
   protected AttributedString rightPrompt = AttributedString.EMPTY;
   protected MaskingCallback maskingCallback;
   protected Map<Integer, String> modifiedHistory = new HashMap<>();
   protected Buffer historyBuffer = null;
   protected CharSequence searchBuffer;
   protected StringBuffer searchTerm = null;
   protected boolean searchFailing;
   protected boolean searchBackward;
   protected int searchIndex = -1;
   protected boolean doAutosuggestion;
   protected final BindingReader bindingReader;
   protected int findChar;
   protected int findDir;
   protected int findTailAdd;
   private int searchDir;
   private String searchString;
   protected int regionMark;
   protected LineReader.RegionType regionActive;
   private boolean forceChar;
   private boolean forceLine;
   protected String yankBuffer = "";
   protected LineReaderImpl.ViMoveMode viMoveMode = LineReaderImpl.ViMoveMode.NORMAL;
   protected KillRing killRing = new KillRing();
   protected UndoTree<Buffer> undo;
   protected boolean isUndo;
   protected final ReentrantLock lock = new ReentrantLock();
   protected LineReaderImpl.State state = LineReaderImpl.State.DONE;
   protected final AtomicBoolean startedReading = new AtomicBoolean();
   protected boolean reading;
   protected Supplier<AttributedString> post;
   protected Map<String, Widget> builtinWidgets;
   protected Map<String, Widget> widgets;
   protected int count;
   protected int mult;
   protected int universal = 4;
   protected int repeatCount;
   protected boolean isArgDigit;
   protected ParsedLine parsedLine;
   protected boolean skipRedisplay;
   protected Display display;
   protected boolean overTyping = false;
   protected String keyMap;
   protected int smallTerminalOffset = 0;
   protected boolean nextCommandFromHistory = false;
   protected int nextHistoryId = -1;
   protected List<String> commandsBuffer = new ArrayList<>();
   protected int candidateStartPosition = 0;
   protected String alternateIn;
   protected String alternateOut;
   protected int currentLine;
   private static final String DESC_PREFIX = "(";
   private static final String DESC_SUFFIX = ")";
   private static final int MARGIN_BETWEEN_DISPLAY_AND_DESC = 1;
   private static final int MARGIN_BETWEEN_COLUMNS = 3;
   private static final int MENU_LIST_WIDTH = 25;

   public LineReaderImpl(Terminal terminal) throws IOException {
      this(terminal, terminal.getName(), null);
   }

   public LineReaderImpl(Terminal terminal, String appName) throws IOException {
      this(terminal, appName, null);
   }

   public LineReaderImpl(Terminal terminal, String appName, Map<String, Object> variables) {
      Objects.requireNonNull(terminal, "terminal can not be null");
      this.terminal = terminal;
      if (appName == null) {
         appName = "JLine";
      }

      this.appName = appName;
      if (variables != null) {
         this.variables = variables;
      } else {
         this.variables = new HashMap<>();
      }

      String prefix = this.getString("system-property-prefix", "org.jline.reader.props.");
      if (prefix != null) {
         Properties sysProps = System.getProperties();

         for (String s : sysProps.stringPropertyNames()) {
            if (s.startsWith(prefix)) {
               String key = s.substring(prefix.length());
               InputRC.setVar(this, key, sysProps.getProperty(s));
            }
         }
      }

      this.keyMaps = this.defaultKeyMaps();
      if (!Boolean.getBoolean("org.jline.utils.disableAlternateCharset")) {
         this.alternateIn = Curses.tputs(terminal.getStringCapability(InfoCmp.Capability.enter_alt_charset_mode));
         this.alternateOut = Curses.tputs(terminal.getStringCapability(InfoCmp.Capability.exit_alt_charset_mode));
      }

      this.undo = new UndoTree<>(this::setBuffer);
      this.builtinWidgets = this.builtinWidgets();
      this.widgets = new HashMap<>(this.builtinWidgets);
      this.bindingReader = new BindingReader(terminal.reader());
      String inputRc = this.getString("input-rc-file-name", null);
      if (inputRc != null) {
         Path inputRcPath = Paths.get(inputRc);
         if (Files.exists(inputRcPath)) {
            try {
               InputStream is = Files.newInputStream(inputRcPath);

               try {
                  InputRC.configure(this, is);
               } catch (Throwable var11) {
                  if (is != null) {
                     try {
                        is.close();
                     } catch (Throwable var10) {
                        var11.addSuppressed(var10);
                     }
                  }

                  throw var11;
               }

               if (is != null) {
                  is.close();
               }
            } catch (Exception var12) {
               Log.debug("Error reading inputRc config file: ", inputRc, var12);
            }
         }
      }

      this.doDisplay();
   }

   @Override
   public Terminal getTerminal() {
      return this.terminal;
   }

   @Override
   public String getAppName() {
      return this.appName;
   }

   @Override
   public Map<String, KeyMap<Binding>> getKeyMaps() {
      return this.keyMaps;
   }

   @Override
   public KeyMap<Binding> getKeys() {
      return this.keyMaps.get(this.keyMap);
   }

   @Override
   public Map<String, Widget> getWidgets() {
      return this.widgets;
   }

   @Override
   public Map<String, Widget> getBuiltinWidgets() {
      return Collections.unmodifiableMap(this.builtinWidgets);
   }

   @Override
   public Buffer getBuffer() {
      return this.buf;
   }

   @Override
   public void setAutosuggestion(LineReader.SuggestionType type) {
      this.autosuggestion = type;
   }

   @Override
   public LineReader.SuggestionType getAutosuggestion() {
      return this.autosuggestion;
   }

   @Override
   public String getTailTip() {
      return this.tailTip;
   }

   @Override
   public void setTailTip(String tailTip) {
      this.tailTip = tailTip;
   }

   @Override
   public void runMacro(String macro) {
      this.bindingReader.runMacro(macro);
   }

   @Override
   public MouseEvent readMouseEvent() {
      return this.terminal.readMouseEvent(this.bindingReader::readCharacter, this.bindingReader.getLastBinding());
   }

   public void setCompleter(Completer completer) {
      this.completer = completer;
   }

   public Completer getCompleter() {
      return this.completer;
   }

   public void setHistory(History history) {
      Objects.requireNonNull(history);
      this.history = history;
   }

   @Override
   public History getHistory() {
      return this.history;
   }

   public void setHighlighter(Highlighter highlighter) {
      this.highlighter = highlighter;
   }

   @Override
   public Highlighter getHighlighter() {
      return this.highlighter;
   }

   @Override
   public Parser getParser() {
      return this.parser;
   }

   public void setParser(Parser parser) {
      this.parser = parser;
   }

   @Override
   public Expander getExpander() {
      return this.expander;
   }

   public void setExpander(Expander expander) {
      this.expander = expander;
   }

   public void setCompletionMatcher(CompletionMatcher completionMatcher) {
      this.completionMatcher = completionMatcher;
   }

   @Override
   public String readLine() throws UserInterruptException, EndOfFileException {
      return this.readLine(null, null, (MaskingCallback)null, null);
   }

   @Override
   public String readLine(Character mask) throws UserInterruptException, EndOfFileException {
      return this.readLine(null, null, mask, null);
   }

   @Override
   public String readLine(String prompt) throws UserInterruptException, EndOfFileException {
      return this.readLine(prompt, null, (MaskingCallback)null, null);
   }

   @Override
   public String readLine(String prompt, Character mask) throws UserInterruptException, EndOfFileException {
      return this.readLine(prompt, null, mask, null);
   }

   @Override
   public String readLine(String prompt, Character mask, String buffer) throws UserInterruptException, EndOfFileException {
      return this.readLine(prompt, null, mask, buffer);
   }

   @Override
   public String readLine(String prompt, String rightPrompt, Character mask, String buffer) throws UserInterruptException, EndOfFileException {
      return this.readLine(prompt, rightPrompt, mask != null ? new SimpleMaskingCallback(mask) : null, buffer);
   }

   @Override
   public String readLine(String prompt, String rightPrompt, MaskingCallback maskingCallback, String buffer) throws UserInterruptException, EndOfFileException {
      if (!this.commandsBuffer.isEmpty()) {
         String cmd = this.commandsBuffer.remove(0);
         boolean done = false;

         do {
            try {
               this.parser.parse(cmd, cmd.length() + 1, Parser.ParseContext.ACCEPT_LINE);
               done = true;
            } catch (EOFError var83) {
               if (this.commandsBuffer.isEmpty()) {
                  throw new IllegalArgumentException("Incompleted command: \n" + cmd);
               }

               cmd = cmd + "\n";
               cmd = cmd + this.commandsBuffer.remove(0);
            } catch (SyntaxError var84) {
               done = true;
            } catch (Exception var85) {
               this.commandsBuffer.clear();
               throw new IllegalArgumentException(var85.getMessage());
            }
         } while (!done);

         AttributedStringBuilder sb = new AttributedStringBuilder();
         sb.styled(AttributedStyle::bold, cmd);
         sb.toAttributedString().println(this.terminal);
         this.terminal.flush();
         return this.finish(cmd);
      } else if (!this.startedReading.compareAndSet(false, true)) {
         throw new IllegalStateException();
      } else {
         Thread readLineThread = Thread.currentThread();
         Terminal.SignalHandler previousIntrHandler = null;
         Terminal.SignalHandler previousWinchHandler = null;
         Terminal.SignalHandler previousContHandler = null;
         Attributes originalAttributes = null;
         boolean dumb = this.isTerminalDumb();

         try {
            this.maskingCallback = maskingCallback;
            this.repeatCount = 0;
            this.mult = 1;
            this.regionActive = LineReader.RegionType.NONE;
            this.regionMark = -1;
            this.smallTerminalOffset = 0;
            this.state = LineReaderImpl.State.NORMAL;
            this.modifiedHistory.clear();
            this.setPrompt(prompt);
            this.setRightPrompt(rightPrompt);
            this.buf.clear();
            if (buffer != null) {
               this.buf.write(buffer);
            }

            if (this.nextCommandFromHistory && this.nextHistoryId > 0) {
               if (this.history.size() > this.nextHistoryId) {
                  this.history.moveTo(this.nextHistoryId);
               } else {
                  this.history.moveTo(this.history.last());
               }

               this.buf.write(this.history.current());
            } else {
               this.nextHistoryId = -1;
            }

            this.nextCommandFromHistory = false;
            this.undo.clear();
            this.parsedLine = null;
            this.keyMap = "main";
            if (this.history != null) {
               this.history.attach(this);
            }

            try {
               this.lock.lock();
               this.reading = true;
               previousIntrHandler = this.terminal.handle(Terminal.Signal.INT, signal -> readLineThread.interrupt());
               previousWinchHandler = this.terminal.handle(Terminal.Signal.WINCH, this::handleSignal);
               previousContHandler = this.terminal.handle(Terminal.Signal.CONT, this::handleSignal);
               originalAttributes = this.terminal.enterRawMode();
               this.doDisplay();
               if (!dumb) {
                  this.terminal.puts(InfoCmp.Capability.keypad_xmit);
                  if (this.isSet(LineReader.Option.AUTO_FRESH_LINE)) {
                     this.callWidget("fresh-line");
                  }

                  if (this.isSet(LineReader.Option.MOUSE)) {
                     this.terminal.trackMouse(Terminal.MouseTracking.Normal);
                  }

                  if (this.isSet(LineReader.Option.BRACKETED_PASTE)) {
                     this.terminal.writer().write("\u001b[?2004h");
                  }
               } else if (this.isTerminalDumb() && maskingCallback != null) {
                  this.setupMaskThread(prompt != null ? prompt : "");
               }

               this.callWidget("callback-init");
               if (!this.isSet(LineReader.Option.DISABLE_UNDO)) {
                  this.undo.newState(this.buf.copy());
               }

               this.redrawLine();
               this.redisplay();
            } finally {
               this.lock.unlock();
            }

            while (true) {
               KeyMap<Binding> local = null;
               if (this.isInViCmdMode() && this.regionActive != LineReader.RegionType.NONE) {
                  local = this.keyMaps.get("visual");
               }

               Binding o = this.readBinding(this.getKeys(), local);
               if (o == null) {
                  throw new EndOfFileException().partialLine(this.buf.length() > 0 ? this.buf.toString() : null);
               }

               Log.trace("Binding: ", o);
               if (this.buf.length() == 0 && this.getLastBinding().charAt(0) == originalAttributes.getControlChar(Attributes.ControlChar.VEOF)) {
                  throw new EndOfFileException();
               }

               this.isArgDigit = false;
               this.count = (this.repeatCount == 0 ? 1 : this.repeatCount) * this.mult;
               this.isUndo = false;
               if (this.regionActive == LineReader.RegionType.PASTE) {
                  this.regionActive = LineReader.RegionType.NONE;
               }

               try {
                  this.lock.lock();
                  Buffer copy = this.buf.length() <= this.getInt("features-max-buffer-size", 1000) ? this.buf.copy() : null;
                  Widget w = this.getWidget(o);
                  if (!w.apply()) {
                     this.beep();
                  }

                  if (!this.isSet(LineReader.Option.DISABLE_UNDO)
                     && !this.isUndo
                     && copy != null
                     && this.buf.length() <= this.getInt("features-max-buffer-size", 1000)
                     && !copy.toString().equals(this.buf.toString())) {
                     this.undo.newState(this.buf.copy());
                  }

                  switch (this.state) {
                     case DONE:
                        return this.finishBuffer();
                     case IGNORE:
                        return "";
                     case EOF:
                        throw new EndOfFileException();
                     case INTERRUPT:
                        throw new UserInterruptException(this.buf.toString());
                     default:
                        if (!this.isArgDigit) {
                           this.repeatCount = 0;
                           this.mult = 1;
                        }

                        if (!dumb) {
                           this.redisplay();
                        }
                  }
               } finally {
                  this.lock.unlock();
               }
            }
         } catch (IOError var87) {
            if (var87.getCause() instanceof InterruptedIOException) {
               throw new UserInterruptException(this.buf.toString());
            } else {
               throw var87;
            }
         } finally {
            AtomicBoolean interrupted = new AtomicBoolean(Thread.interrupted());

            try {
               this.lock.lock();
               this.reading = false;
               Terminal.SignalHandler tmpHandler = this.terminal.handle(Terminal.Signal.INT, s -> interrupted.set(true));
               if (previousIntrHandler == null) {
                  previousIntrHandler = tmpHandler;
               }

               this.cleanup();
               if (originalAttributes != null) {
                  this.terminal.setAttributes(originalAttributes);
               }

               if (previousIntrHandler != null) {
                  this.terminal.handle(Terminal.Signal.INT, previousIntrHandler);
               }

               if (previousWinchHandler != null) {
                  this.terminal.handle(Terminal.Signal.WINCH, previousWinchHandler);
               }

               if (previousContHandler != null) {
                  this.terminal.handle(Terminal.Signal.CONT, previousContHandler);
               }
            } finally {
               this.lock.unlock();
               this.startedReading.set(false);
               if (interrupted.get()) {
                  Thread.currentThread().interrupt();
               }
            }
         }
      }
   }

   private boolean isTerminalDumb() {
      return "dumb".equals(this.terminal.getType()) || "dumb-color".equals(this.terminal.getType());
   }

   private void doDisplay() {
      this.size.copy(this.terminal.getBufferSize());
      this.display = new Display(this.terminal, false);
      this.display.resize(this.size.getRows(), this.size.getColumns());
      if (this.isSet(LineReader.Option.DELAY_LINE_WRAP)) {
         this.display.setDelayLineWrap(true);
      }
   }

   private void setupMaskThread(String prompt) {
      if (this.isTerminalDumb() && this.maskThread == null) {
         final String fullPrompt = "\r" + prompt + "                                                   \r" + prompt;
         this.maskThread = new Thread("JLine Mask Thread") {
            @Override
            public void run() {
               while (!Thread.interrupted()) {
                  try {
                     LineReaderImpl.this.terminal.writer().write(fullPrompt);
                     LineReaderImpl.this.terminal.writer().flush();
                     sleep(3L);
                  } catch (InterruptedException var2) {
                     return;
                  }
               }
            }
         };
         this.maskThread.setPriority(10);
         this.maskThread.setDaemon(true);
         this.maskThread.start();
      }
   }

   private void stopMaskThread() {
      if (this.maskThread != null && this.maskThread.isAlive()) {
         this.maskThread.interrupt();
      }

      this.maskThread = null;
   }

   @Override
   public void printAbove(String str) {
      try {
         this.lock.lock();
         boolean reading = this.reading;
         if (reading) {
            this.display.update(Collections.emptyList(), 0);
         }

         if (!str.endsWith("\n") && !str.endsWith("\n\u001b[m") && !str.endsWith("\n\u001b[0m")) {
            this.terminal.writer().println(str);
         } else {
            this.terminal.writer().print(str);
         }

         if (reading) {
            this.redisplay(false);
         }

         this.terminal.flush();
      } finally {
         this.lock.unlock();
      }
   }

   @Override
   public void printAbove(AttributedString str) {
      this.printAbove(str.toAnsi(this.terminal));
   }

   @Override
   public boolean isReading() {
      boolean var1;
      try {
         this.lock.lock();
         var1 = this.reading;
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   protected boolean freshLine() {
      boolean wrapAtEol = this.terminal.getBooleanCapability(InfoCmp.Capability.auto_right_margin);
      boolean delayedWrapAtEol = wrapAtEol && this.terminal.getBooleanCapability(InfoCmp.Capability.eat_newline_glitch);
      AttributedStringBuilder sb = new AttributedStringBuilder();
      sb.style(AttributedStyle.DEFAULT.foreground(8));
      sb.append("~");
      sb.style(AttributedStyle.DEFAULT);
      if (wrapAtEol && !delayedWrapAtEol) {
         String el = this.terminal.getStringCapability(InfoCmp.Capability.clr_eol);
         if (el != null) {
            Curses.tputs(sb, el);
         }

         for (int i = 0; i < this.size.getColumns() - 2; i++) {
            sb.append(" ");
         }

         sb.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
         sb.append(" ");
         sb.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
      } else {
         for (int i = 0; i < this.size.getColumns() - 1; i++) {
            sb.append(" ");
         }

         sb.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
         sb.append(" ");
         sb.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
      }

      sb.print(this.terminal);
      return true;
   }

   @Override
   public void callWidget(String name) {
      try {
         this.lock.lock();
         if (!this.reading) {
            throw new IllegalStateException("Widgets can only be called during a `readLine` call");
         }

         try {
            Widget w;
            if (name.startsWith(".")) {
               w = this.builtinWidgets.get(name.substring(1));
            } else {
               w = this.widgets.get(name);
            }

            if (w != null) {
               w.apply();
            }
         } catch (Throwable var6) {
            Log.debug("Error executing widget '", name, "'", var6);
         }
      } finally {
         this.lock.unlock();
      }
   }

   public boolean redrawLine() {
      this.display.reset();
      return true;
   }

   public void putString(CharSequence str) {
      this.buf.write(str, this.overTyping);
   }

   @Override
   public void flush() {
      this.terminal.flush();
   }

   public boolean isKeyMap(String name) {
      return this.keyMap.equals(name);
   }

   public int readCharacter() {
      if (this.lock.isHeldByCurrentThread()) {
         int var1;
         try {
            this.lock.unlock();
            var1 = this.bindingReader.readCharacter();
         } finally {
            this.lock.lock();
         }

         return var1;
      } else {
         return this.bindingReader.readCharacter();
      }
   }

   public int peekCharacter(long timeout) {
      return this.bindingReader.peekCharacter(timeout);
   }

   protected <T> T doReadBinding(KeyMap<T> keys, KeyMap<T> local) {
      if (this.lock.isHeldByCurrentThread()) {
         Object var3;
         try {
            this.lock.unlock();
            var3 = this.bindingReader.readBinding(keys, local);
         } finally {
            this.lock.lock();
         }

         return (T)var3;
      } else {
         return this.bindingReader.readBinding(keys, local);
      }
   }

   protected String doReadStringUntil(String sequence) {
      if (this.lock.isHeldByCurrentThread()) {
         String var2;
         try {
            this.lock.unlock();
            var2 = this.bindingReader.readStringUntil(sequence);
         } finally {
            this.lock.lock();
         }

         return var2;
      } else {
         return this.bindingReader.readStringUntil(sequence);
      }
   }

   public Binding readBinding(KeyMap<Binding> keys) {
      return this.readBinding(keys, null);
   }

   public Binding readBinding(KeyMap<Binding> keys, KeyMap<Binding> local) {
      Binding o = this.doReadBinding(keys, local);
      if (o instanceof Reference) {
         String ref = ((Reference)o).name();
         if (!"yank-pop".equals(ref) && !"yank".equals(ref)) {
            this.killRing.resetLastYank();
         }

         if (!"kill-line".equals(ref) && !"kill-whole-line".equals(ref) && !"backward-kill-word".equals(ref) && !"kill-word".equals(ref)) {
            this.killRing.resetLastKill();
         }
      }

      return o;
   }

   @Override
   public ParsedLine getParsedLine() {
      return this.parsedLine;
   }

   @Override
   public String getLastBinding() {
      return this.bindingReader.getLastBinding();
   }

   @Override
   public String getSearchTerm() {
      return this.searchTerm != null ? this.searchTerm.toString() : null;
   }

   @Override
   public LineReader.RegionType getRegionActive() {
      return this.regionActive;
   }

   @Override
   public int getRegionMark() {
      return this.regionMark;
   }

   @Override
   public boolean setKeyMap(String name) {
      KeyMap<Binding> map = this.keyMaps.get(name);
      if (map == null) {
         return false;
      } else {
         this.keyMap = name;
         if (this.reading) {
            this.callWidget("callback-keymap");
         }

         return true;
      }
   }

   @Override
   public String getKeyMap() {
      return this.keyMap;
   }

   @Override
   public LineReader variable(String name, Object value) {
      this.variables.put(name, value);
      return this;
   }

   @Override
   public Map<String, Object> getVariables() {
      return this.variables;
   }

   @Override
   public Object getVariable(String name) {
      return this.variables.get(name);
   }

   @Override
   public void setVariable(String name, Object value) {
      this.variables.put(name, value);
   }

   @Override
   public LineReader option(LineReader.Option option, boolean value) {
      this.options.put(option, value);
      return this;
   }

   @Override
   public boolean isSet(LineReader.Option option) {
      return option.isSet(this.options);
   }

   @Override
   public void setOpt(LineReader.Option option) {
      this.options.put(option, Boolean.TRUE);
   }

   @Override
   public void unsetOpt(LineReader.Option option) {
      this.options.put(option, Boolean.FALSE);
   }

   @Override
   public void addCommandsInBuffer(Collection<String> commands) {
      this.commandsBuffer.addAll(commands);
   }

   @Override
   public void editAndAddInBuffer(Path file) throws Exception {
      if (this.isSet(LineReader.Option.BRACKETED_PASTE)) {
         this.terminal.writer().write("\u001b[?2004l");
      }

      Constructor<?> ctor = Class.forName("org.jline.builtins.Nano").getConstructor(Terminal.class, Path.class);
      Editor editor = (Editor)ctor.newInstance(this.terminal, file.getParent());
      editor.setRestricted(true);
      editor.open(Collections.singletonList(file.getFileName().toString()));
      editor.run();
      BufferedReader br = Files.newBufferedReader(file);

      try {
         this.commandsBuffer.clear();

         String line;
         while ((line = br.readLine()) != null) {
            this.commandsBuffer.add(line);
         }
      } catch (Throwable var8) {
         if (br != null) {
            try {
               br.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (br != null) {
         br.close();
      }
   }

   protected int getTabWidth() {
      return this.getInt("tab-width", 4);
   }

   protected String finishBuffer() {
      return this.finish(this.buf.toString());
   }

   protected String finish(String str) {
      String historyLine = str;
      if (!this.isSet(LineReader.Option.DISABLE_EVENT_EXPANSION)) {
         StringBuilder sb = new StringBuilder();
         boolean escaped = false;

         for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (escaped) {
               escaped = false;
               if (ch != '\n') {
                  sb.append(ch);
               }
            } else if (this.parser.isEscapeChar(ch)) {
               escaped = true;
            } else {
               sb.append(ch);
            }
         }

         str = sb.toString();
      }

      if (this.maskingCallback != null) {
         historyLine = this.maskingCallback.history(historyLine);
      }

      if (historyLine != null && historyLine.length() > 0) {
         this.history.add(Instant.now(), historyLine);
      }

      return str;
   }

   protected synchronized void handleSignal(Terminal.Signal signal) {
      this.doAutosuggestion = false;
      if (signal == Terminal.Signal.WINCH) {
         this.size.copy(this.terminal.getBufferSize());
         this.display.resize(this.size.getRows(), this.size.getColumns());
         Status status = Status.getStatus(this.terminal, false);
         if (status != null) {
            status.resize(this.size);
            status.reset();
         }

         this.terminal.puts(InfoCmp.Capability.carriage_return);
         this.terminal.puts(InfoCmp.Capability.clr_eos);
         this.redrawLine();
         this.redisplay();
      } else if (signal == Terminal.Signal.CONT) {
         this.terminal.enterRawMode();
         this.size.copy(this.terminal.getBufferSize());
         this.display.resize(this.size.getRows(), this.size.getColumns());
         this.terminal.puts(InfoCmp.Capability.keypad_xmit);
         this.redrawLine();
         this.redisplay();
      }
   }

   protected Widget getWidget(Object binding) {
      Widget w;
      if (binding instanceof Widget) {
         w = (Widget)binding;
      } else if (binding instanceof Macro) {
         String macro = ((Macro)binding).getSequence();
         w = () -> {
            this.bindingReader.runMacro(macro);
            return true;
         };
      } else if (binding instanceof Reference) {
         String name = ((Reference)binding).name();
         w = this.widgets.get(name);
         if (w == null) {
            w = () -> {
               this.post = () -> new AttributedString("No such widget `" + name + "'");
               return false;
            };
         }
      } else {
         w = () -> {
            this.post = () -> new AttributedString("Unsupported widget");
            return false;
         };
      }

      return w;
   }

   public void setPrompt(String prompt) {
      this.prompt = prompt == null ? AttributedString.EMPTY : this.expandPromptPattern(prompt, 0, "", 0);
   }

   public void setRightPrompt(String rightPrompt) {
      this.rightPrompt = rightPrompt == null ? AttributedString.EMPTY : this.expandPromptPattern(rightPrompt, 0, "", 0);
   }

   protected void setBuffer(Buffer buffer) {
      this.buf.copyFrom(buffer);
   }

   protected void setBuffer(String buffer) {
      this.buf.clear();
      this.buf.write(buffer);
   }

   protected String viDeleteChangeYankToRemap(String op) {
      switch (op) {
         case "abort":
         case "backward-char":
         case "forward-char":
         case "end-of-line":
         case "vi-match-bracket":
         case "vi-digit-or-beginning-of-line":
         case "neg-argument":
         case "digit-argument":
         case "vi-backward-char":
         case "vi-backward-word":
         case "vi-forward-char":
         case "vi-forward-word":
         case "vi-forward-word-end":
         case "vi-first-non-blank":
         case "vi-goto-column":
         case "vi-delete":
         case "vi-yank":
         case "vi-change-to":
         case "vi-find-next-char":
         case "vi-find-next-char-skip":
         case "vi-find-prev-char":
         case "vi-find-prev-char-skip":
         case "vi-repeat-find":
         case "vi-rev-repeat-find":
            return op;
         default:
            return "vi-cmd-mode";
      }
   }

   protected int switchCase(int ch) {
      if (Character.isUpperCase(ch)) {
         return Character.toLowerCase(ch);
      } else {
         return Character.isLowerCase(ch) ? Character.toUpperCase(ch) : ch;
      }
   }

   protected boolean isInViMoveOperation() {
      return this.viMoveMode != LineReaderImpl.ViMoveMode.NORMAL;
   }

   protected boolean isInViChangeOperation() {
      return this.viMoveMode == LineReaderImpl.ViMoveMode.CHANGE;
   }

   protected boolean isInViCmdMode() {
      return "vicmd".equals(this.keyMap);
   }

   protected boolean viForwardChar() {
      if (this.count < 0) {
         return this.callNeg(this::viBackwardChar);
      } else {
         int lim = this.findeol();
         if (this.isInViCmdMode() && !this.isInViMoveOperation()) {
            lim--;
         }

         if (this.buf.cursor() >= lim) {
            return false;
         } else {
            while (this.count-- > 0 && this.buf.cursor() < lim) {
               this.buf.move(1);
            }

            return true;
         }
      }
   }

   protected boolean viBackwardChar() {
      if (this.count < 0) {
         return this.callNeg(this::viForwardChar);
      } else {
         int lim = this.findbol();
         if (this.buf.cursor() == lim) {
            return false;
         } else {
            while (this.count-- > 0 && this.buf.cursor() > 0) {
               this.buf.move(-1);
               if (this.buf.currChar() == 10) {
                  this.buf.move(1);
                  break;
               }
            }

            return true;
         }
      }
   }

   protected boolean forwardWord() {
      if (this.count < 0) {
         return this.callNeg(this::backwardWord);
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar())) {
               this.buf.move(1);
            }

            if (this.isInViChangeOperation() && this.count == 0) {
               break;
            }

            while (this.buf.cursor() < this.buf.length() && !this.isWord(this.buf.currChar())) {
               this.buf.move(1);
            }
         }

         return true;
      }
   }

   protected boolean viForwardWord() {
      if (this.count < 0) {
         return this.callNeg(this::viBackwardWord);
      } else {
         while (this.count-- > 0) {
            if (this.isViAlphaNum(this.buf.currChar())) {
               while (this.buf.cursor() < this.buf.length() && this.isViAlphaNum(this.buf.currChar())) {
                  this.buf.move(1);
               }
            } else {
               while (this.buf.cursor() < this.buf.length() && !this.isViAlphaNum(this.buf.currChar()) && !this.isWhitespace(this.buf.currChar())) {
                  this.buf.move(1);
               }
            }

            if (this.isInViChangeOperation() && this.count == 0) {
               return true;
            }

            for (int nl = this.buf.currChar() == 10 ? 1 : 0;
               this.buf.cursor() < this.buf.length() && nl < 2 && this.isWhitespace(this.buf.currChar());
               nl += this.buf.currChar() == 10 ? 1 : 0
            ) {
               this.buf.move(1);
            }
         }

         return true;
      }
   }

   protected boolean viForwardBlankWord() {
      if (this.count < 0) {
         return this.callNeg(this::viBackwardBlankWord);
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() < this.buf.length() && !this.isWhitespace(this.buf.currChar())) {
               this.buf.move(1);
            }

            if (this.isInViChangeOperation() && this.count == 0) {
               return true;
            }

            for (int nl = this.buf.currChar() == 10 ? 1 : 0;
               this.buf.cursor() < this.buf.length() && nl < 2 && this.isWhitespace(this.buf.currChar());
               nl += this.buf.currChar() == 10 ? 1 : 0
            ) {
               this.buf.move(1);
            }
         }

         return true;
      }
   }

   protected boolean emacsForwardWord() {
      return this.forwardWord();
   }

   protected boolean viForwardBlankWordEnd() {
      if (this.count < 0) {
         return false;
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() < this.buf.length()) {
               this.buf.move(1);
               if (!this.isWhitespace(this.buf.currChar())) {
                  break;
               }
            }

            while (this.buf.cursor() < this.buf.length()) {
               this.buf.move(1);
               if (this.isWhitespace(this.buf.currChar())) {
                  break;
               }
            }
         }

         return true;
      }
   }

   protected boolean viForwardWordEnd() {
      if (this.count < 0) {
         return this.callNeg(this::backwardWord);
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() < this.buf.length() && this.isWhitespace(this.buf.nextChar())) {
               this.buf.move(1);
            }

            if (this.buf.cursor() < this.buf.length()) {
               if (this.isViAlphaNum(this.buf.nextChar())) {
                  this.buf.move(1);

                  while (this.buf.cursor() < this.buf.length() && this.isViAlphaNum(this.buf.nextChar())) {
                     this.buf.move(1);
                  }
               } else {
                  this.buf.move(1);

                  while (this.buf.cursor() < this.buf.length() && !this.isViAlphaNum(this.buf.nextChar()) && !this.isWhitespace(this.buf.nextChar())) {
                     this.buf.move(1);
                  }
               }
            }
         }

         if (this.buf.cursor() < this.buf.length() && this.isInViMoveOperation()) {
            this.buf.move(1);
         }

         return true;
      }
   }

   protected boolean backwardWord() {
      if (this.count < 0) {
         return this.callNeg(this::forwardWord);
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() > 0 && !this.isWord(this.buf.atChar(this.buf.cursor() - 1))) {
               this.buf.move(-1);
            }

            while (this.buf.cursor() > 0 && this.isWord(this.buf.atChar(this.buf.cursor() - 1))) {
               this.buf.move(-1);
            }
         }

         return true;
      }
   }

   protected boolean viBackwardWord() {
      if (this.count < 0) {
         return this.callNeg(this::viForwardWord);
      } else {
         while (this.count-- > 0) {
            int nl = 0;

            while (this.buf.cursor() > 0) {
               this.buf.move(-1);
               if (!this.isWhitespace(this.buf.currChar())) {
                  break;
               }

               nl += this.buf.currChar() == 10 ? 1 : 0;
               if (nl == 2) {
                  this.buf.move(1);
                  break;
               }
            }

            if (this.buf.cursor() > 0) {
               if (this.isViAlphaNum(this.buf.currChar())) {
                  while (this.buf.cursor() > 0 && this.isViAlphaNum(this.buf.prevChar())) {
                     this.buf.move(-1);
                  }
               } else {
                  while (this.buf.cursor() > 0 && !this.isViAlphaNum(this.buf.prevChar()) && !this.isWhitespace(this.buf.prevChar())) {
                     this.buf.move(-1);
                  }
               }
            }
         }

         return true;
      }
   }

   protected boolean viBackwardBlankWord() {
      if (this.count < 0) {
         return this.callNeg(this::viForwardBlankWord);
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() > 0) {
               this.buf.move(-1);
               if (!this.isWhitespace(this.buf.currChar())) {
                  break;
               }
            }

            while (this.buf.cursor() > 0) {
               this.buf.move(-1);
               if (this.isWhitespace(this.buf.currChar())) {
                  break;
               }
            }
         }

         return true;
      }
   }

   protected boolean viBackwardWordEnd() {
      if (this.count < 0) {
         return this.callNeg(this::viForwardWordEnd);
      } else {
         while (this.count-- > 0 && this.buf.cursor() > 1) {
            int start;
            if (this.isViAlphaNum(this.buf.currChar())) {
               start = 1;
            } else if (!this.isWhitespace(this.buf.currChar())) {
               start = 2;
            } else {
               start = 0;
            }

            while (this.buf.cursor() > 0) {
               boolean same = start != 1 && this.isWhitespace(this.buf.currChar());
               if (start != 0) {
                  same |= this.isViAlphaNum(this.buf.currChar());
               }

               if (same == (start == 2)) {
                  break;
               }

               this.buf.move(-1);
            }

            while (this.buf.cursor() > 0 && this.isWhitespace(this.buf.currChar())) {
               this.buf.move(-1);
            }
         }

         return true;
      }
   }

   protected boolean viBackwardBlankWordEnd() {
      if (this.count < 0) {
         return this.callNeg(this::viForwardBlankWordEnd);
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() > 0 && !this.isWhitespace(this.buf.currChar())) {
               this.buf.move(-1);
            }

            while (this.buf.cursor() > 0 && this.isWhitespace(this.buf.currChar())) {
               this.buf.move(-1);
            }
         }

         return true;
      }
   }

   protected boolean emacsBackwardWord() {
      return this.backwardWord();
   }

   protected boolean backwardDeleteWord() {
      if (this.count < 0) {
         return this.callNeg(this::deleteWord);
      } else {
         int cursor = this.buf.cursor();

         while (this.count-- > 0) {
            while (cursor > 0 && !this.isWord(this.buf.atChar(cursor - 1))) {
               cursor--;
            }

            while (cursor > 0 && this.isWord(this.buf.atChar(cursor - 1))) {
               cursor--;
            }
         }

         this.buf.backspace(this.buf.cursor() - cursor);
         return true;
      }
   }

   protected boolean viBackwardKillWord() {
      if (this.count < 0) {
         return false;
      } else {
         int lim = this.findbol();
         int x = this.buf.cursor();

         while (this.count-- > 0) {
            while (x > lim && this.isWhitespace(this.buf.atChar(x - 1))) {
               x--;
            }

            if (x > lim) {
               if (this.isViAlphaNum(this.buf.atChar(x - 1))) {
                  while (x > lim && this.isViAlphaNum(this.buf.atChar(x - 1))) {
                     x--;
                  }
               } else {
                  while (x > lim && !this.isViAlphaNum(this.buf.atChar(x - 1)) && !this.isWhitespace(this.buf.atChar(x - 1))) {
                     x--;
                  }
               }
            }
         }

         this.killRing.addBackwards(this.buf.substring(x, this.buf.cursor()));
         this.buf.backspace(this.buf.cursor() - x);
         return true;
      }
   }

   protected boolean backwardKillWord() {
      if (this.count < 0) {
         return this.callNeg(this::killWord);
      } else {
         int x = this.buf.cursor();

         while (this.count-- > 0) {
            while (x > 0 && !this.isWord(this.buf.atChar(x - 1))) {
               x--;
            }

            while (x > 0 && this.isWord(this.buf.atChar(x - 1))) {
               x--;
            }
         }

         this.killRing.addBackwards(this.buf.substring(x, this.buf.cursor()));
         this.buf.backspace(this.buf.cursor() - x);
         return true;
      }
   }

   protected boolean copyPrevWord() {
      if (this.count <= 0) {
         return false;
      } else {
         int t0 = this.buf.cursor();

         do {
            int t1 = t0;

            while (t0 > 0 && !this.isWord(this.buf.atChar(t0 - 1))) {
               t0--;
            }

            while (t0 > 0 && this.isWord(this.buf.atChar(t0 - 1))) {
               t0--;
            }

            if (--this.count == 0) {
               this.buf.write(this.buf.substring(t0, t1));
               return true;
            }
         } while (t0 != 0);

         return false;
      }
   }

   protected boolean upCaseWord() {
      int count = Math.abs(this.count);
      int cursor = this.buf.cursor();

      while (count-- > 0) {
         while (this.buf.cursor() < this.buf.length() && !this.isWord(this.buf.currChar())) {
            this.buf.move(1);
         }

         while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar())) {
            this.buf.currChar(Character.toUpperCase(this.buf.currChar()));
            this.buf.move(1);
         }
      }

      if (this.count < 0) {
         this.buf.cursor(cursor);
      }

      return true;
   }

   protected boolean downCaseWord() {
      int count = Math.abs(this.count);
      int cursor = this.buf.cursor();

      while (count-- > 0) {
         while (this.buf.cursor() < this.buf.length() && !this.isWord(this.buf.currChar())) {
            this.buf.move(1);
         }

         while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar())) {
            this.buf.currChar(Character.toLowerCase(this.buf.currChar()));
            this.buf.move(1);
         }
      }

      if (this.count < 0) {
         this.buf.cursor(cursor);
      }

      return true;
   }

   protected boolean capitalizeWord() {
      int count = Math.abs(this.count);
      int cursor = this.buf.cursor();

      while (count-- > 0) {
         boolean first = true;

         while (this.buf.cursor() < this.buf.length() && !this.isWord(this.buf.currChar())) {
            this.buf.move(1);
         }

         while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar()) && !this.isAlpha(this.buf.currChar())) {
            this.buf.move(1);
         }

         while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar())) {
            this.buf.currChar(first ? Character.toUpperCase(this.buf.currChar()) : Character.toLowerCase(this.buf.currChar()));
            this.buf.move(1);
            first = false;
         }
      }

      if (this.count < 0) {
         this.buf.cursor(cursor);
      }

      return true;
   }

   protected boolean deleteWord() {
      if (this.count < 0) {
         return this.callNeg(this::backwardDeleteWord);
      } else {
         int x = this.buf.cursor();

         while (this.count-- > 0) {
            while (x < this.buf.length() && !this.isWord(this.buf.atChar(x))) {
               x++;
            }

            while (x < this.buf.length() && this.isWord(this.buf.atChar(x))) {
               x++;
            }
         }

         this.buf.delete(x - this.buf.cursor());
         return true;
      }
   }

   protected boolean killWord() {
      if (this.count < 0) {
         return this.callNeg(this::backwardKillWord);
      } else {
         int x = this.buf.cursor();

         while (this.count-- > 0) {
            while (x < this.buf.length() && !this.isWord(this.buf.atChar(x))) {
               x++;
            }

            while (x < this.buf.length() && this.isWord(this.buf.atChar(x))) {
               x++;
            }
         }

         this.killRing.add(this.buf.substring(this.buf.cursor(), x));
         this.buf.delete(x - this.buf.cursor());
         return true;
      }
   }

   protected boolean transposeWords() {
      int lstart = this.buf.cursor() - 1;
      int lend = this.buf.cursor();

      while (this.buf.atChar(lstart) != 0 && this.buf.atChar(lstart) != 10) {
         lstart--;
      }

      lstart++;

      while (this.buf.atChar(lend) != 0 && this.buf.atChar(lend) != 10) {
         lend++;
      }

      if (lend - lstart < 2) {
         return false;
      } else {
         int words = 0;
         boolean inWord = false;
         if (!this.isDelimiter(this.buf.atChar(lstart))) {
            words++;
            inWord = true;
         }

         for (int i = lstart; i < lend; i++) {
            if (this.isDelimiter(this.buf.atChar(i))) {
               inWord = false;
            } else {
               if (!inWord) {
                  words++;
               }

               inWord = true;
            }
         }

         if (words < 2) {
            return false;
         } else {
            boolean neg = this.count < 0;

            for (int count = Math.max(this.count, -this.count); count > 0; count--) {
               int sta1 = this.buf.cursor();

               while (sta1 > lstart && !this.isDelimiter(this.buf.atChar(sta1 - 1))) {
                  sta1--;
               }

               int end1 = sta1;

               while (end1 < lend) {
                  if (this.isDelimiter(this.buf.atChar(++end1))) {
                     break;
                  }
               }

               int sta2;
               int end2;
               if (neg) {
                  end2 = sta1 - 1;

                  while (end2 > lstart && this.isDelimiter(this.buf.atChar(end2 - 1))) {
                     end2--;
                  }

                  if (end2 >= lstart) {
                     sta2 = end2;

                     while (sta2 > lstart && !this.isDelimiter(this.buf.atChar(sta2 - 1))) {
                        sta2--;
                     }
                  } else {
                     sta2 = end1;

                     while (this.isDelimiter(this.buf.atChar(++sta2))) {
                     }

                     end2 = sta2;

                     while (end2 < lend) {
                        if (this.isDelimiter(this.buf.atChar(++end2))) {
                           break;
                        }
                     }
                  }
               } else {
                  sta2 = end1;

                  while (sta2 < lend) {
                     if (!this.isDelimiter(this.buf.atChar(++sta2))) {
                        break;
                     }
                  }

                  if (sta2 != lend) {
                     end2 = sta2;

                     while (end2 < lend) {
                        if (this.isDelimiter(this.buf.atChar(++end2))) {
                           break;
                        }
                     }
                  } else {
                     end2 = sta1;

                     while (this.isDelimiter(this.buf.atChar(end2 - 1))) {
                        end2--;
                     }

                     sta2 = end2;

                     while (sta2 > lstart && !this.isDelimiter(this.buf.atChar(sta2 - 1))) {
                        sta2--;
                     }
                  }
               }

               if (sta1 < sta2) {
                  String res = this.buf.substring(0, sta1)
                     + this.buf.substring(sta2, end2)
                     + this.buf.substring(end1, sta2)
                     + this.buf.substring(sta1, end1)
                     + this.buf.substring(end2);
                  this.buf.clear();
                  this.buf.write(res);
                  this.buf.cursor(neg ? end1 : end2);
               } else {
                  String res = this.buf.substring(0, sta2)
                     + this.buf.substring(sta1, end1)
                     + this.buf.substring(end2, sta1)
                     + this.buf.substring(sta2, end2)
                     + this.buf.substring(end1);
                  this.buf.clear();
                  this.buf.write(res);
                  this.buf.cursor(neg ? end2 : end1);
               }
            }

            return true;
         }
      }
   }

   private int findbol() {
      int x = this.buf.cursor();

      while (x > 0 && this.buf.atChar(x - 1) != 10) {
         x--;
      }

      return x;
   }

   private int findeol() {
      int x = this.buf.cursor();

      while (x < this.buf.length() && this.buf.atChar(x) != 10) {
         x++;
      }

      return x;
   }

   protected boolean insertComment() {
      return this.doInsertComment(false);
   }

   protected boolean viInsertComment() {
      return this.doInsertComment(true);
   }

   protected boolean doInsertComment(boolean isViMode) {
      String comment = this.getString("comment-begin", "#");
      this.beginningOfLine();
      this.putString(comment);
      if (isViMode) {
         this.setKeyMap("viins");
      }

      return this.acceptLine();
   }

   protected boolean viFindNextChar() {
      if ((this.findChar = this.vigetkey()) > 0) {
         this.findDir = 1;
         this.findTailAdd = 0;
         return this.vifindchar(false);
      } else {
         return false;
      }
   }

   protected boolean viFindPrevChar() {
      if ((this.findChar = this.vigetkey()) > 0) {
         this.findDir = -1;
         this.findTailAdd = 0;
         return this.vifindchar(false);
      } else {
         return false;
      }
   }

   protected boolean viFindNextCharSkip() {
      if ((this.findChar = this.vigetkey()) > 0) {
         this.findDir = 1;
         this.findTailAdd = -1;
         return this.vifindchar(false);
      } else {
         return false;
      }
   }

   protected boolean viFindPrevCharSkip() {
      if ((this.findChar = this.vigetkey()) > 0) {
         this.findDir = -1;
         this.findTailAdd = 1;
         return this.vifindchar(false);
      } else {
         return false;
      }
   }

   protected boolean viRepeatFind() {
      return this.vifindchar(true);
   }

   protected boolean viRevRepeatFind() {
      if (this.count < 0) {
         return this.callNeg(() -> this.vifindchar(true));
      } else {
         this.findTailAdd = -this.findTailAdd;
         this.findDir = -this.findDir;
         boolean ret = this.vifindchar(true);
         this.findTailAdd = -this.findTailAdd;
         this.findDir = -this.findDir;
         return ret;
      }
   }

   private int vigetkey() {
      int ch = this.readCharacter();
      KeyMap<Binding> km = this.keyMaps.get("main");
      if (km != null) {
         Binding b = km.getBound(new String(Character.toChars(ch)));
         if (b instanceof Reference) {
            String func = ((Reference)b).name();
            if ("abort".equals(func)) {
               return -1;
            }
         }
      }

      return ch;
   }

   private boolean vifindchar(boolean repeat) {
      if (this.findDir == 0) {
         return false;
      } else if (this.count < 0) {
         return this.callNeg(this::viRevRepeatFind);
      } else {
         if (repeat && this.findTailAdd != 0) {
            if (this.findDir > 0) {
               if (this.buf.cursor() < this.buf.length() && this.buf.nextChar() == this.findChar) {
                  this.buf.move(1);
               }
            } else if (this.buf.cursor() > 0 && this.buf.prevChar() == this.findChar) {
               this.buf.move(-1);
            }
         }

         int cursor = this.buf.cursor();

         while (this.count-- > 0) {
            do {
               this.buf.move(this.findDir);
            } while (this.buf.cursor() > 0 && this.buf.cursor() < this.buf.length() && this.buf.currChar() != this.findChar && this.buf.currChar() != 10);

            if (this.buf.cursor() <= 0 || this.buf.cursor() >= this.buf.length() || this.buf.currChar() == 10) {
               this.buf.cursor(cursor);
               return false;
            }
         }

         if (this.findTailAdd != 0) {
            this.buf.move(this.findTailAdd);
         }

         if (this.findDir == 1 && this.isInViMoveOperation()) {
            this.buf.move(1);
         }

         return true;
      }
   }

   private boolean callNeg(Widget widget) {
      this.count = -this.count;
      boolean ret = widget.apply();
      this.count = -this.count;
      return ret;
   }

   protected boolean viHistorySearchForward() {
      this.searchDir = 1;
      this.searchIndex = 0;
      return this.getViSearchString() && this.viRepeatSearch();
   }

   protected boolean viHistorySearchBackward() {
      this.searchDir = -1;
      this.searchIndex = this.history.size() - 1;
      return this.getViSearchString() && this.viRepeatSearch();
   }

   protected boolean viRepeatSearch() {
      if (this.searchDir == 0) {
         return false;
      } else {
         int si = this.searchDir < 0
            ? this.searchBackwards(this.searchString, this.searchIndex, false)
            : this.searchForwards(this.searchString, this.searchIndex, false);
         if (si != -1 && si != this.history.index()) {
            this.searchIndex = si;
            this.buf.clear();
            this.history.moveTo(this.searchIndex);
            this.buf.write(this.history.get(this.searchIndex));
            if ("vicmd".equals(this.keyMap)) {
               this.buf.move(-1);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   protected boolean viRevRepeatSearch() {
      this.searchDir = -this.searchDir;
      boolean ret = this.viRepeatSearch();
      this.searchDir = -this.searchDir;
      return ret;
   }

   private boolean getViSearchString() {
      if (this.searchDir == 0) {
         return false;
      } else {
         String searchPrompt = this.searchDir < 0 ? "?" : "/";
         Buffer searchBuffer = new BufferImpl();
         KeyMap<Binding> keyMap = this.keyMaps.get("main");
         if (keyMap == null) {
            keyMap = this.keyMaps.get(".safe");
         }

         while (true) {
            this.post = () -> new AttributedString(searchPrompt + searchBuffer.toString() + "_");
            this.redisplay();
            Binding b = this.doReadBinding(keyMap, null);
            if (b instanceof Reference) {
               String func = ((Reference)b).name();
               switch (func) {
                  case "abort":
                     this.post = null;
                     return false;
                  case "accept-line":
                  case "vi-cmd-mode":
                     this.searchString = searchBuffer.toString();
                     this.post = null;
                     return true;
                  case "magic-space":
                     searchBuffer.write(32);
                     break;
                  case "redisplay":
                     this.redisplay();
                     break;
                  case "clear-screen":
                     this.clearScreen();
                     break;
                  case "self-insert":
                     searchBuffer.write(this.getLastBinding());
                     break;
                  case "self-insert-unmeta":
                     if (this.getLastBinding().charAt(0) == 27) {
                        String s = this.getLastBinding().substring(1);
                        if ("\r".equals(s)) {
                           s = "\n";
                        }

                        searchBuffer.write(s);
                     }
                     break;
                  case "backward-delete-char":
                  case "vi-backward-delete-char":
                     if (searchBuffer.length() > 0) {
                        searchBuffer.backspace();
                     }
                     break;
                  case "backward-kill-word":
                  case "vi-backward-kill-word":
                     if (searchBuffer.length() > 0 && !this.isWhitespace(searchBuffer.prevChar())) {
                        searchBuffer.backspace();
                     }

                     if (searchBuffer.length() > 0 && this.isWhitespace(searchBuffer.prevChar())) {
                        searchBuffer.backspace();
                     }
                     break;
                  case "quoted-insert":
                  case "vi-quoted-insert":
                     int c = this.readCharacter();
                     if (c >= 0) {
                        searchBuffer.write(c);
                     } else {
                        this.beep();
                     }
                     break;
                  default:
                     this.beep();
               }
            }
         }
      }
   }

   protected boolean insertCloseCurly() {
      return this.insertClose("}");
   }

   protected boolean insertCloseParen() {
      return this.insertClose(")");
   }

   protected boolean insertCloseSquare() {
      return this.insertClose("]");
   }

   protected boolean insertClose(String s) {
      this.putString(s);
      long blink = this.getLong("blink-matching-paren", 500L);
      if (blink <= 0L) {
         this.removeIndentation();
         return true;
      } else {
         int closePosition = this.buf.cursor();
         this.buf.move(-1);
         this.doViMatchBracket();
         this.redisplay();
         this.peekCharacter(blink);
         int blinkPosition = this.buf.cursor();
         this.buf.cursor(closePosition);
         if (blinkPosition != closePosition - 1) {
            this.removeIndentation();
         }

         return true;
      }
   }

   private void removeIndentation() {
      int indent = this.getInt("indentation", 0);
      if (indent > 0) {
         this.buf.move(-1);

         for (int i = 0; i < indent; i++) {
            this.buf.move(-1);
            if (this.buf.currChar() != 32) {
               this.buf.move(1);
               break;
            }

            this.buf.delete();
         }

         this.buf.move(1);
      }
   }

   protected boolean viMatchBracket() {
      return this.doViMatchBracket();
   }

   protected boolean undefinedKey() {
      return false;
   }

   protected boolean doViMatchBracket() {
      int pos = this.buf.cursor();
      if (pos == this.buf.length()) {
         return false;
      } else {
         int type = this.getBracketType(this.buf.atChar(pos));
         int move = type < 0 ? -1 : 1;
         int count = 1;
         if (type == 0) {
            return false;
         } else {
            while (count > 0) {
               pos += move;
               if (pos < 0 || pos >= this.buf.length()) {
                  return false;
               }

               int curType = this.getBracketType(this.buf.atChar(pos));
               if (curType == type) {
                  count++;
               } else if (curType == -type) {
                  count--;
               }
            }

            if (move > 0 && this.isInViMoveOperation()) {
               pos++;
            }

            this.buf.cursor(pos);
            return true;
         }
      }
   }

   protected int getBracketType(int ch) {
      switch (ch) {
         case 40:
            return 3;
         case 41:
            return -3;
         case 91:
            return 1;
         case 93:
            return -1;
         case 123:
            return 2;
         case 125:
            return -2;
         default:
            return 0;
      }
   }

   protected boolean transposeChars() {
      int lstart = this.buf.cursor() - 1;
      int lend = this.buf.cursor();

      while (this.buf.atChar(lstart) != 0 && this.buf.atChar(lstart) != 10) {
         lstart--;
      }

      lstart++;

      while (this.buf.atChar(lend) != 0 && this.buf.atChar(lend) != 10) {
         lend++;
      }

      if (lend - lstart < 2) {
         return false;
      } else {
         boolean neg = this.count < 0;

         for (int count = Math.max(this.count, -this.count); count > 0; count--) {
            while (this.buf.cursor() <= lstart) {
               this.buf.move(1);
            }

            while (this.buf.cursor() >= lend) {
               this.buf.move(-1);
            }

            int c = this.buf.currChar();
            this.buf.currChar(this.buf.prevChar());
            this.buf.move(-1);
            this.buf.currChar(c);
            this.buf.move(neg ? 0 : 2);
         }

         return true;
      }
   }

   protected boolean undo() {
      this.isUndo = true;
      if (this.undo.canUndo()) {
         this.undo.undo();
         return true;
      } else {
         return false;
      }
   }

   protected boolean redo() {
      this.isUndo = true;
      if (this.undo.canRedo()) {
         this.undo.redo();
         return true;
      } else {
         return false;
      }
   }

   protected boolean sendBreak() {
      if (this.searchTerm == null) {
         this.buf.clear();
         this.println();
         this.redrawLine();
         return false;
      } else {
         return true;
      }
   }

   protected boolean backwardChar() {
      return this.buf.move(-this.count) != 0;
   }

   protected boolean forwardChar() {
      return this.buf.move(this.count) != 0;
   }

   protected boolean viDigitOrBeginningOfLine() {
      return this.repeatCount > 0 ? this.digitArgument() : this.beginningOfLine();
   }

   protected boolean universalArgument() {
      this.mult = this.mult * this.universal;
      this.isArgDigit = true;
      return true;
   }

   protected boolean argumentBase() {
      if (this.repeatCount > 0 && this.repeatCount < 32) {
         this.universal = this.repeatCount;
         this.isArgDigit = true;
         return true;
      } else {
         return false;
      }
   }

   protected boolean negArgument() {
      this.mult *= -1;
      this.isArgDigit = true;
      return true;
   }

   protected boolean digitArgument() {
      String s = this.getLastBinding();
      this.repeatCount = this.repeatCount * 10 + s.charAt(s.length() - 1) - 48;
      int maxRepeatCount = this.getInt("max-repeat-count", 9999);
      if (this.repeatCount > maxRepeatCount) {
         throw new IllegalArgumentException("digit argument should be less than " + maxRepeatCount);
      } else {
         this.isArgDigit = true;
         return true;
      }
   }

   protected boolean viDelete() {
      int cursorStart = this.buf.cursor();
      Binding o = this.readBinding(this.getKeys());
      if (o instanceof Reference) {
         String op = this.viDeleteChangeYankToRemap(((Reference)o).name());
         if ("vi-delete".equals(op)) {
            this.killWholeLine();
         } else {
            this.viMoveMode = LineReaderImpl.ViMoveMode.DELETE;
            Widget widget = this.widgets.get(op);
            if (widget != null && !widget.apply()) {
               this.viMoveMode = LineReaderImpl.ViMoveMode.NORMAL;
               return false;
            }

            this.viMoveMode = LineReaderImpl.ViMoveMode.NORMAL;
         }

         return this.viDeleteTo(cursorStart, this.buf.cursor());
      } else {
         this.pushBackBinding();
         return false;
      }
   }

   protected boolean viYankTo() {
      int cursorStart = this.buf.cursor();
      Binding o = this.readBinding(this.getKeys());
      if (o instanceof Reference) {
         String op = this.viDeleteChangeYankToRemap(((Reference)o).name());
         if ("vi-yank".equals(op)) {
            this.yankBuffer = this.buf.toString();
            return true;
         } else {
            this.viMoveMode = LineReaderImpl.ViMoveMode.YANK;
            Widget widget = this.widgets.get(op);
            if (widget != null && !widget.apply()) {
               return false;
            } else {
               this.viMoveMode = LineReaderImpl.ViMoveMode.NORMAL;
               return this.viYankTo(cursorStart, this.buf.cursor());
            }
         }
      } else {
         this.pushBackBinding();
         return false;
      }
   }

   protected boolean viYankWholeLine() {
      int p = this.buf.cursor();

      while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10) {
      }

      int s = this.buf.cursor();

      for (int i = 0; i < this.repeatCount; i++) {
         while (this.buf.move(1) == 1 && this.buf.prevChar() != 10) {
         }
      }

      int e = this.buf.cursor();
      this.yankBuffer = this.buf.substring(s, e);
      if (!this.yankBuffer.endsWith("\n")) {
         this.yankBuffer = this.yankBuffer + "\n";
      }

      this.buf.cursor(p);
      return true;
   }

   protected boolean viChange() {
      int cursorStart = this.buf.cursor();
      Binding o = this.readBinding(this.getKeys());
      if (o instanceof Reference) {
         String op = this.viDeleteChangeYankToRemap(((Reference)o).name());
         if ("vi-change-to".equals(op)) {
            this.killWholeLine();
         } else {
            this.viMoveMode = LineReaderImpl.ViMoveMode.CHANGE;
            Widget widget = this.widgets.get(op);
            if (widget != null && !widget.apply()) {
               this.viMoveMode = LineReaderImpl.ViMoveMode.NORMAL;
               return false;
            }

            this.viMoveMode = LineReaderImpl.ViMoveMode.NORMAL;
         }

         boolean res = this.viChange(cursorStart, this.buf.cursor());
         this.setKeyMap("viins");
         return res;
      } else {
         this.pushBackBinding();
         return false;
      }
   }

   protected void cleanup() {
      if (this.isSet(LineReader.Option.ERASE_LINE_ON_FINISH)) {
         Buffer oldBuffer = this.buf.copy();
         AttributedString oldPrompt = this.prompt;
         this.buf.clear();
         this.prompt = new AttributedString("");
         this.doCleanup(false);
         this.prompt = oldPrompt;
         this.buf.copyFrom(oldBuffer);
      } else {
         this.doCleanup(true);
      }
   }

   protected void doCleanup(boolean nl) {
      this.buf.cursor(this.buf.length());
      this.post = null;
      if (this.size.getColumns() > 0 || this.size.getRows() > 0) {
         this.doAutosuggestion = false;
         this.redisplay(false);
         if (nl) {
            this.println();
         }

         this.terminal.puts(InfoCmp.Capability.keypad_local);
         this.terminal.trackMouse(Terminal.MouseTracking.Off);
         if (this.isSet(LineReader.Option.BRACKETED_PASTE) && !this.isTerminalDumb()) {
            this.terminal.writer().write("\u001b[?2004l");
         }

         this.stopMaskThread();
         this.flush();
      }

      this.history.moveToEnd();
   }

   protected boolean historyIncrementalSearchForward() {
      return this.doSearchHistory(false);
   }

   protected boolean historyIncrementalSearchBackward() {
      return this.doSearchHistory(true);
   }

   protected boolean doSearchHistory(boolean backward) {
      if (this.history.isEmpty()) {
         return false;
      } else {
         KeyMap<Binding> terminators = new KeyMap<>();
         this.getString("search-terminators", "\u001b\n").codePoints().forEach(c -> this.bind(terminators, "accept-line", new String(Character.toChars(c))));
         Buffer originalBuffer = this.buf.copy();
         this.searchIndex = -1;
         this.searchTerm = new StringBuffer();
         this.searchBackward = backward;
         this.searchFailing = false;
         this.post = () -> new AttributedString(
            (this.searchFailing ? "failing " : "") + (this.searchBackward ? "bck-i-search" : "fwd-i-search") + ": " + this.searchTerm + "_"
         );
         this.redisplay();

         try {
            while (true) {
               int prevSearchIndex = this.searchIndex;
               Binding operation = this.readBinding(this.getKeys(), terminators);
               String ref = operation instanceof Reference ? ((Reference)operation).name() : "";
               boolean next = false;
               switch (ref) {
                  case "abort":
                     this.beep();
                     this.buf.copyFrom(originalBuffer);
                     return true;
                  case "history-incremental-search-backward":
                     this.searchBackward = true;
                     next = true;
                     break;
                  case "history-incremental-search-forward":
                     this.searchBackward = false;
                     next = true;
                     break;
                  case "backward-delete-char":
                     if (this.searchTerm.length() > 0) {
                        this.searchTerm.deleteCharAt(this.searchTerm.length() - 1);
                     }
                     break;
                  case "self-insert":
                     this.searchTerm.append(this.getLastBinding());
                     break;
                  default:
                     if (this.searchIndex != -1) {
                        this.history.moveTo(this.searchIndex);
                     }

                     this.pushBackBinding();
                     return true;
               }

               String pattern = this.doGetSearchPattern();
               if (pattern.length() == 0) {
                  this.buf.copyFrom(originalBuffer);
                  this.searchFailing = false;
               } else {
                  boolean caseInsensitive = this.isSet(LineReader.Option.CASE_INSENSITIVE_SEARCH);
                  Pattern pat = Pattern.compile(pattern, caseInsensitive ? 66 : 64);
                  LineReaderImpl.Pair<Integer, Integer> pair = null;
                  if (this.searchBackward) {
                     boolean nextOnly = next;
                     pair = this.matches(pat, this.buf.toString(), this.searchIndex)
                        .stream()
                        .filter(p -> nextOnly ? p.v < this.buf.cursor() : p.v <= this.buf.cursor())
                        .max(Comparator.comparing(LineReaderImpl.Pair::getV))
                        .orElse(null);
                     if (pair == null) {
                        pair = StreamSupport.stream(
                              Spliterators.spliteratorUnknownSize(
                                 this.history.reverseIterator(this.searchIndex < 0 ? this.history.last() : this.searchIndex - 1), 16
                              ),
                              false
                           )
                           .flatMap(ex -> this.matches(pat, ex.line(), ex.index()).stream())
                           .findFirst()
                           .orElse(null);
                     }
                  } else {
                     boolean nextOnly = next;
                     pair = this.matches(pat, this.buf.toString(), this.searchIndex)
                        .stream()
                        .filter(p -> nextOnly ? p.v > this.buf.cursor() : p.v >= this.buf.cursor())
                        .min(Comparator.comparing(LineReaderImpl.Pair::getV))
                        .orElse(null);
                     if (pair == null) {
                        pair = StreamSupport.stream(
                              Spliterators.spliteratorUnknownSize(
                                 this.history.iterator((this.searchIndex < 0 ? this.history.last() : this.searchIndex) + 1), 16
                              ),
                              false
                           )
                           .flatMap(ex -> this.matches(pat, ex.line(), ex.index()).stream())
                           .findFirst()
                           .orElse(null);
                        if (pair == null && this.searchIndex >= 0) {
                           pair = this.matches(pat, originalBuffer.toString(), -1).stream().min(Comparator.comparing(LineReaderImpl.Pair::getV)).orElse(null);
                        }
                     }
                  }

                  if (pair != null) {
                     this.searchIndex = pair.u;
                     this.buf.clear();
                     if (this.searchIndex >= 0) {
                        this.buf.write(this.history.get(this.searchIndex));
                     } else {
                        this.buf.write(originalBuffer.toString());
                     }

                     this.buf.cursor(pair.v);
                     this.searchFailing = false;
                  } else {
                     this.searchFailing = true;
                     this.beep();
                  }
               }

               this.redisplay();
            }
         } catch (IOError var16) {
            if (!(var16.getCause() instanceof InterruptedException)) {
               throw var16;
            } else {
               return true;
            }
         } finally {
            this.searchTerm = null;
            this.searchIndex = -1;
            this.post = null;
         }
      }
   }

   private List<LineReaderImpl.Pair<Integer, Integer>> matches(Pattern p, String line, int index) {
      List<LineReaderImpl.Pair<Integer, Integer>> starts = new ArrayList<>();
      Matcher m = p.matcher(line);

      while (m.find()) {
         starts.add(new LineReaderImpl.Pair<>(index, m.start()));
      }

      return starts;
   }

   private String doGetSearchPattern() {
      StringBuilder sb = new StringBuilder();
      boolean inQuote = false;

      for (int i = 0; i < this.searchTerm.length(); i++) {
         char c = this.searchTerm.charAt(i);
         if (Character.isLowerCase(c)) {
            if (inQuote) {
               sb.append("\\E");
               inQuote = false;
            }

            sb.append("[").append(Character.toLowerCase(c)).append(Character.toUpperCase(c)).append("]");
         } else {
            if (!inQuote) {
               sb.append("\\Q");
               inQuote = true;
            }

            sb.append(c);
         }
      }

      if (inQuote) {
         sb.append("\\E");
      }

      return sb.toString();
   }

   private void pushBackBinding() {
      this.pushBackBinding(false);
   }

   private void pushBackBinding(boolean skip) {
      String s = this.getLastBinding();
      if (s != null) {
         this.bindingReader.runMacro(s);
         this.skipRedisplay = skip;
      }
   }

   protected boolean historySearchForward() {
      if (this.historyBuffer == null || this.buf.length() == 0 || !this.buf.toString().equals(this.history.current())) {
         this.historyBuffer = this.buf.copy();
         this.searchBuffer = this.getFirstWord();
      }

      int index = this.history.index() + 1;
      if (index < this.history.last() + 1) {
         int searchIndex = this.searchForwards(this.searchBuffer.toString(), index, true);
         if (searchIndex == -1) {
            this.history.moveToEnd();
            if (this.buf.toString().equals(this.historyBuffer.toString())) {
               return false;
            }

            this.setBuffer(this.historyBuffer.toString());
            this.historyBuffer = null;
         } else {
            if (!this.history.moveTo(searchIndex)) {
               this.history.moveToEnd();
               this.setBuffer(this.historyBuffer.toString());
               return false;
            }

            this.setBuffer(this.history.current());
         }
      } else {
         this.history.moveToEnd();
         if (this.buf.toString().equals(this.historyBuffer.toString())) {
            return false;
         }

         this.setBuffer(this.historyBuffer.toString());
         this.historyBuffer = null;
      }

      return true;
   }

   private CharSequence getFirstWord() {
      String s = this.buf.toString();
      int i = 0;

      while (i < s.length() && !Character.isWhitespace(s.charAt(i))) {
         i++;
      }

      return s.substring(0, i);
   }

   protected boolean historySearchBackward() {
      if (this.historyBuffer == null || this.buf.length() == 0 || !this.buf.toString().equals(this.history.current())) {
         this.historyBuffer = this.buf.copy();
         this.searchBuffer = this.getFirstWord();
      }

      int searchIndex = this.searchBackwards(this.searchBuffer.toString(), this.history.index(), true);
      if (searchIndex == -1) {
         return false;
      } else if (this.history.moveTo(searchIndex)) {
         this.setBuffer(this.history.current());
         return true;
      } else {
         return false;
      }
   }

   public int searchBackwards(String searchTerm, int startIndex) {
      return this.searchBackwards(searchTerm, startIndex, false);
   }

   public int searchBackwards(String searchTerm) {
      return this.searchBackwards(searchTerm, this.history.index(), false);
   }

   public int searchBackwards(String searchTerm, int startIndex, boolean startsWith) {
      boolean caseInsensitive = this.isSet(LineReader.Option.CASE_INSENSITIVE_SEARCH);
      if (caseInsensitive) {
         searchTerm = searchTerm.toLowerCase();
      }

      ListIterator<History.Entry> it = this.history.iterator(startIndex);

      while (it.hasPrevious()) {
         History.Entry e = it.previous();
         String line = e.line();
         if (caseInsensitive) {
            line = line.toLowerCase();
         }

         int idx = line.indexOf(searchTerm);
         if (startsWith && idx == 0 || !startsWith && idx >= 0) {
            return e.index();
         }
      }

      return -1;
   }

   public int searchForwards(String searchTerm, int startIndex, boolean startsWith) {
      boolean caseInsensitive = this.isSet(LineReader.Option.CASE_INSENSITIVE_SEARCH);
      if (caseInsensitive) {
         searchTerm = searchTerm.toLowerCase();
      }

      if (startIndex > this.history.last()) {
         startIndex = this.history.last();
      }

      ListIterator<History.Entry> it = this.history.iterator(startIndex);
      if (this.searchIndex != -1 && it.hasNext()) {
         it.next();
      }

      while (it.hasNext()) {
         History.Entry e = it.next();
         String line = e.line();
         if (caseInsensitive) {
            line = line.toLowerCase();
         }

         int idx = line.indexOf(searchTerm);
         if (startsWith && idx == 0 || !startsWith && idx >= 0) {
            return e.index();
         }
      }

      return -1;
   }

   public int searchForwards(String searchTerm, int startIndex) {
      return this.searchForwards(searchTerm, startIndex, false);
   }

   public int searchForwards(String searchTerm) {
      return this.searchForwards(searchTerm, this.history.index());
   }

   protected boolean quit() {
      this.getBuffer().clear();
      return this.acceptLine();
   }

   protected boolean acceptAndHold() {
      this.nextCommandFromHistory = false;
      this.acceptLine();
      if (!this.buf.toString().isEmpty()) {
         this.nextHistoryId = Integer.MAX_VALUE;
         this.nextCommandFromHistory = true;
      }

      return this.nextCommandFromHistory;
   }

   protected boolean acceptLineAndDownHistory() {
      this.nextCommandFromHistory = false;
      this.acceptLine();
      if (this.nextHistoryId < 0) {
         this.nextHistoryId = this.history.index();
      }

      if (this.history.size() > this.nextHistoryId + 1) {
         this.nextHistoryId++;
         this.nextCommandFromHistory = true;
      }

      return this.nextCommandFromHistory;
   }

   protected boolean acceptAndInferNextHistory() {
      this.nextCommandFromHistory = false;
      this.acceptLine();
      if (!this.buf.toString().isEmpty()) {
         this.nextHistoryId = this.searchBackwards(this.buf.toString(), this.history.last());
         if (this.nextHistoryId >= 0 && this.history.size() > this.nextHistoryId + 1) {
            this.nextHistoryId++;
            this.nextCommandFromHistory = true;
         }
      }

      return this.nextCommandFromHistory;
   }

   protected boolean acceptLine() {
      this.parsedLine = null;
      int curPos = 0;
      if (!this.isSet(LineReader.Option.DISABLE_EVENT_EXPANSION)) {
         try {
            String str = this.buf.toString();
            String exp = this.expander.expandHistory(this.history, str);
            if (!exp.equals(str)) {
               this.buf.clear();
               this.buf.write(exp);
               if (this.isSet(LineReader.Option.HISTORY_VERIFY)) {
                  return true;
               }
            }
         } catch (IllegalArgumentException var5) {
         }
      }

      try {
         curPos = this.buf.cursor();
         this.parsedLine = this.parser.parse(this.buf.toString(), this.buf.cursor(), Parser.ParseContext.ACCEPT_LINE);
      } catch (EOFError var6) {
         StringBuilder sb = new StringBuilder("\n");
         this.indention(var6.getOpenBrackets(), sb);
         int curMove = sb.length();
         if (this.isSet(LineReader.Option.INSERT_BRACKET) && var6.getOpenBrackets() > 1 && var6.getNextClosingBracket() != null) {
            sb.append('\n');
            this.indention(var6.getOpenBrackets() - 1, sb);
            sb.append(var6.getNextClosingBracket());
         }

         this.buf.write(sb.toString());
         this.buf.cursor(curPos + curMove);
         return true;
      } catch (SyntaxError var7) {
      }

      this.callWidget("callback-finish");
      this.state = LineReaderImpl.State.DONE;
      return true;
   }

   void indention(int nb, StringBuilder sb) {
      int indent = this.getInt("indentation", 0) * nb;

      for (int i = 0; i < indent; i++) {
         sb.append(' ');
      }
   }

   protected boolean selfInsert() {
      for (int count = this.count; count > 0; count--) {
         this.putString(this.getLastBinding());
      }

      return true;
   }

   protected boolean selfInsertUnmeta() {
      if (this.getLastBinding().charAt(0) != 27) {
         return false;
      } else {
         String s = this.getLastBinding().substring(1);
         if ("\r".equals(s)) {
            s = "\n";
         }

         for (int count = this.count; count > 0; count--) {
            this.putString(s);
         }

         return true;
      }
   }

   protected boolean overwriteMode() {
      this.overTyping = !this.overTyping;
      return true;
   }

   protected boolean beginningOfBufferOrHistory() {
      if (this.findbol() != 0) {
         this.buf.cursor(0);
         return true;
      } else {
         return this.beginningOfHistory();
      }
   }

   protected boolean beginningOfHistory() {
      if (this.history.moveToFirst()) {
         this.setBuffer(this.history.current());
         return true;
      } else {
         return false;
      }
   }

   protected boolean endOfBufferOrHistory() {
      if (this.findeol() != this.buf.length()) {
         this.buf.cursor(this.buf.length());
         return true;
      } else {
         return this.endOfHistory();
      }
   }

   protected boolean endOfHistory() {
      if (this.history.moveToLast()) {
         this.setBuffer(this.history.current());
         return true;
      } else {
         return false;
      }
   }

   protected boolean beginningOfLineHist() {
      if (this.count < 0) {
         return this.callNeg(this::endOfLineHist);
      } else {
         while (this.count-- > 0) {
            int bol = this.findbol();
            if (bol != this.buf.cursor()) {
               this.buf.cursor(bol);
            } else {
               this.moveHistory(false);
               this.buf.cursor(0);
            }
         }

         return true;
      }
   }

   protected boolean endOfLineHist() {
      if (this.count < 0) {
         return this.callNeg(this::beginningOfLineHist);
      } else {
         while (this.count-- > 0) {
            int eol = this.findeol();
            if (eol != this.buf.cursor()) {
               this.buf.cursor(eol);
            } else {
               this.moveHistory(true);
            }
         }

         return true;
      }
   }

   protected boolean upHistory() {
      while (this.count-- > 0) {
         if (!this.moveHistory(false)) {
            return !this.isSet(LineReader.Option.HISTORY_BEEP);
         }
      }

      return true;
   }

   protected boolean downHistory() {
      while (this.count-- > 0) {
         if (!this.moveHistory(true)) {
            return !this.isSet(LineReader.Option.HISTORY_BEEP);
         }
      }

      return true;
   }

   protected boolean viUpLineOrHistory() {
      return this.upLine() || this.upHistory() && this.viFirstNonBlank();
   }

   protected boolean viDownLineOrHistory() {
      return this.downLine() || this.downHistory() && this.viFirstNonBlank();
   }

   protected boolean upLine() {
      return this.buf.up();
   }

   protected boolean downLine() {
      return this.buf.down();
   }

   protected boolean upLineOrHistory() {
      return this.upLine() || this.upHistory();
   }

   protected boolean upLineOrSearch() {
      return this.upLine() || this.historySearchBackward();
   }

   protected boolean downLineOrHistory() {
      return this.downLine() || this.downHistory();
   }

   protected boolean downLineOrSearch() {
      return this.downLine() || this.historySearchForward();
   }

   protected boolean viCmdMode() {
      if (this.state == LineReaderImpl.State.NORMAL) {
         this.buf.move(-1);
      }

      return this.setKeyMap("vicmd");
   }

   protected boolean viInsert() {
      return this.setKeyMap("viins");
   }

   protected boolean viAddNext() {
      this.buf.move(1);
      return this.setKeyMap("viins");
   }

   protected boolean viAddEol() {
      return this.endOfLine() && this.setKeyMap("viins");
   }

   protected boolean emacsEditingMode() {
      return this.setKeyMap("emacs");
   }

   protected boolean viChangeWholeLine() {
      return this.viFirstNonBlank() && this.viChangeEol();
   }

   protected boolean viChangeEol() {
      return this.viChange(this.buf.cursor(), this.buf.length()) && this.setKeyMap("viins");
   }

   protected boolean viKillEol() {
      int eol = this.findeol();
      if (this.buf.cursor() == eol) {
         return false;
      } else {
         this.killRing.add(this.buf.substring(this.buf.cursor(), eol));
         this.buf.delete(eol - this.buf.cursor());
         return true;
      }
   }

   protected boolean quotedInsert() {
      int c = this.readCharacter();

      while (this.count-- > 0) {
         this.putString(new String(Character.toChars(c)));
      }

      return true;
   }

   protected boolean viJoin() {
      if (!this.buf.down()) {
         return false;
      } else {
         while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10) {
         }

         this.buf.backspace();
         this.buf.write(32);
         this.buf.move(-1);
         return true;
      }
   }

   protected boolean viKillWholeLine() {
      return this.killWholeLine() && this.setKeyMap("viins");
   }

   protected boolean viInsertBol() {
      return this.beginningOfLine() && this.setKeyMap("viins");
   }

   protected boolean backwardDeleteChar() {
      if (this.count < 0) {
         return this.callNeg(this::deleteChar);
      } else if (this.buf.cursor() == 0) {
         return false;
      } else {
         this.buf.backspace(this.count);
         return true;
      }
   }

   protected boolean viFirstNonBlank() {
      this.beginningOfLine();

      while (this.buf.cursor() < this.buf.length() && this.isWhitespace(this.buf.currChar())) {
         this.buf.move(1);
      }

      return true;
   }

   protected boolean viBeginningOfLine() {
      this.buf.cursor(this.findbol());
      return true;
   }

   protected boolean viEndOfLine() {
      if (this.count < 0) {
         return false;
      } else {
         while (this.count-- > 0) {
            this.buf.cursor(this.findeol() + 1);
         }

         this.buf.move(-1);
         return true;
      }
   }

   protected boolean beginningOfLine() {
      while (this.count-- > 0) {
         while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10) {
         }
      }

      return true;
   }

   protected boolean endOfLine() {
      while (this.count-- > 0) {
         while (this.buf.move(1) == 1 && this.buf.currChar() != 10) {
         }
      }

      return true;
   }

   protected boolean deleteChar() {
      if (this.count < 0) {
         return this.callNeg(this::backwardDeleteChar);
      } else if (this.buf.cursor() == this.buf.length()) {
         return false;
      } else {
         this.buf.delete(this.count);
         return true;
      }
   }

   protected boolean viBackwardDeleteChar() {
      for (int i = 0; i < this.count; i++) {
         if (!this.buf.backspace()) {
            return false;
         }
      }

      return true;
   }

   protected boolean viDeleteChar() {
      for (int i = 0; i < this.count; i++) {
         if (!this.buf.delete()) {
            return false;
         }
      }

      return true;
   }

   protected boolean viSwapCase() {
      for (int i = 0; i < this.count; i++) {
         if (this.buf.cursor() >= this.buf.length()) {
            return false;
         }

         int ch = this.buf.atChar(this.buf.cursor());
         ch = this.switchCase(ch);
         this.buf.currChar(ch);
         this.buf.move(1);
      }

      return true;
   }

   protected boolean viReplaceChars() {
      int c = this.readCharacter();
      if (c >= 0 && c != 27 && c != 3) {
         for (int i = 0; i < this.count; i++) {
            if (!this.buf.currChar((char)c)) {
               return false;
            }

            if (i < this.count - 1) {
               this.buf.move(1);
            }
         }

         return true;
      } else {
         return true;
      }
   }

   protected boolean viChange(int startPos, int endPos) {
      return this.doViDeleteOrChange(startPos, endPos, true);
   }

   protected boolean viDeleteTo(int startPos, int endPos) {
      return this.doViDeleteOrChange(startPos, endPos, false);
   }

   protected boolean doViDeleteOrChange(int startPos, int endPos, boolean isChange) {
      if (startPos == endPos) {
         return true;
      } else {
         if (endPos < startPos) {
            int tmp = endPos;
            endPos = startPos;
            startPos = tmp;
         }

         this.buf.cursor(startPos);
         this.buf.delete(endPos - startPos);
         if (!isChange && startPos > 0 && startPos == this.buf.length()) {
            this.buf.move(-1);
         }

         return true;
      }
   }

   protected boolean viYankTo(int startPos, int endPos) {
      int cursorPos = startPos;
      if (endPos < startPos) {
         int tmp = endPos;
         endPos = startPos;
         startPos = tmp;
      }

      if (startPos == endPos) {
         this.yankBuffer = "";
         return true;
      } else {
         this.yankBuffer = this.buf.substring(startPos, endPos);
         this.buf.cursor(cursorPos);
         return true;
      }
   }

   protected boolean viOpenLineAbove() {
      while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10) {
      }

      this.buf.write(10);
      this.buf.move(-1);
      return this.setKeyMap("viins");
   }

   protected boolean viOpenLineBelow() {
      while (this.buf.move(1) == 1 && this.buf.currChar() != 10) {
      }

      this.buf.write(10);
      return this.setKeyMap("viins");
   }

   protected boolean viPutAfter() {
      if (this.yankBuffer.indexOf(10) < 0) {
         if (!this.yankBuffer.isEmpty()) {
            if (this.buf.cursor() < this.buf.length()) {
               this.buf.move(1);
            }

            for (int i = 0; i < this.count; i++) {
               this.putString(this.yankBuffer);
            }

            this.buf.move(-1);
         }
      } else {
         while (this.buf.move(1) == 1 && this.buf.currChar() != 10) {
         }

         this.buf.move(1);
         this.putString(this.yankBuffer);
         this.buf.move(-this.yankBuffer.length());
      }

      return true;
   }

   protected boolean viPutBefore() {
      if (this.yankBuffer.indexOf(10) < 0) {
         if (!this.yankBuffer.isEmpty()) {
            if (this.buf.cursor() > 0) {
               this.buf.move(-1);
            }

            for (int i = 0; i < this.count; i++) {
               this.putString(this.yankBuffer);
            }

            this.buf.move(-1);
         }
      } else {
         while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10) {
         }

         this.putString(this.yankBuffer);
         this.buf.move(-this.yankBuffer.length());
      }

      return true;
   }

   protected boolean doLowercaseVersion() {
      this.bindingReader.runMacro(this.getLastBinding().toLowerCase());
      return true;
   }

   protected boolean setMarkCommand() {
      if (this.count < 0) {
         this.regionActive = LineReader.RegionType.NONE;
         return true;
      } else {
         this.regionMark = this.buf.cursor();
         this.regionActive = LineReader.RegionType.CHAR;
         return true;
      }
   }

   protected boolean exchangePointAndMark() {
      if (this.count == 0) {
         this.regionActive = LineReader.RegionType.CHAR;
         return true;
      } else {
         int x = this.regionMark;
         this.regionMark = this.buf.cursor();
         this.buf.cursor(x);
         if (this.buf.cursor() > this.buf.length()) {
            this.buf.cursor(this.buf.length());
         }

         if (this.count > 0) {
            this.regionActive = LineReader.RegionType.CHAR;
         }

         return true;
      }
   }

   protected boolean visualMode() {
      if (this.isInViMoveOperation()) {
         this.isArgDigit = true;
         this.forceLine = false;
         this.forceChar = true;
         return true;
      } else {
         if (this.regionActive == LineReader.RegionType.NONE) {
            this.regionMark = this.buf.cursor();
            this.regionActive = LineReader.RegionType.CHAR;
         } else if (this.regionActive == LineReader.RegionType.CHAR) {
            this.regionActive = LineReader.RegionType.NONE;
         } else if (this.regionActive == LineReader.RegionType.LINE) {
            this.regionActive = LineReader.RegionType.CHAR;
         }

         return true;
      }
   }

   protected boolean visualLineMode() {
      if (this.isInViMoveOperation()) {
         this.isArgDigit = true;
         this.forceLine = true;
         this.forceChar = false;
         return true;
      } else {
         if (this.regionActive == LineReader.RegionType.NONE) {
            this.regionMark = this.buf.cursor();
            this.regionActive = LineReader.RegionType.LINE;
         } else if (this.regionActive == LineReader.RegionType.CHAR) {
            this.regionActive = LineReader.RegionType.LINE;
         } else if (this.regionActive == LineReader.RegionType.LINE) {
            this.regionActive = LineReader.RegionType.NONE;
         }

         return true;
      }
   }

   protected boolean deactivateRegion() {
      this.regionActive = LineReader.RegionType.NONE;
      return true;
   }

   protected boolean whatCursorPosition() {
      this.post = () -> {
         AttributedStringBuilder sb = new AttributedStringBuilder();
         if (this.buf.cursor() < this.buf.length()) {
            int c = this.buf.currChar();
            sb.append("Char: ");
            if (c == 32) {
               sb.append("SPC");
            } else if (c == 10) {
               sb.append("LFD");
            } else if (c < 32) {
               sb.append('^');
               sb.append((char)(c + 65 - 1));
            } else if (c == 127) {
               sb.append("^?");
            } else {
               sb.append((char)c);
            }

            sb.append(" (");
            sb.append("0").append(Integer.toOctalString(c)).append(" ");
            sb.append(Integer.toString(c)).append(" ");
            sb.append("0x").append(Integer.toHexString(c)).append(" ");
            sb.append(")");
         } else {
            sb.append("EOF");
         }

         sb.append("   ");
         sb.append("point ");
         sb.append(Integer.toString(this.buf.cursor() + 1));
         sb.append(" of ");
         sb.append(Integer.toString(this.buf.length() + 1));
         sb.append(" (");
         sb.append(Integer.toString(this.buf.length() == 0 ? 100 : 100 * this.buf.cursor() / this.buf.length()));
         sb.append("%)");
         sb.append("   ");
         sb.append("column ");
         sb.append(Integer.toString(this.buf.cursor() - this.findbol()));
         return sb.toAttributedString();
      };
      return true;
   }

   protected boolean editAndExecute() {
      boolean out = true;
      File file = null;

      try {
         file = File.createTempFile("jline-execute-", null);
         FileWriter writer = new FileWriter(file);

         try {
            writer.write(this.buf.toString());
         } catch (Throwable var12) {
            try {
               writer.close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }

            throw var12;
         }

         writer.close();
         this.editAndAddInBuffer(file);
      } catch (Exception var13) {
         var13.printStackTrace(this.terminal.writer());
         out = false;
      } finally {
         this.state = LineReaderImpl.State.IGNORE;
         if (file != null && file.exists()) {
            file.delete();
         }
      }

      return out;
   }

   protected Map<String, Widget> builtinWidgets() {
      Map<String, Widget> widgets = new HashMap<>();
      this.addBuiltinWidget(widgets, "accept-and-infer-next-history", this::acceptAndInferNextHistory);
      this.addBuiltinWidget(widgets, "accept-and-hold", this::acceptAndHold);
      this.addBuiltinWidget(widgets, "accept-line", this::acceptLine);
      this.addBuiltinWidget(widgets, "accept-line-and-down-history", this::acceptLineAndDownHistory);
      this.addBuiltinWidget(widgets, "argument-base", this::argumentBase);
      this.addBuiltinWidget(widgets, "backward-char", this::backwardChar);
      this.addBuiltinWidget(widgets, "backward-delete-char", this::backwardDeleteChar);
      this.addBuiltinWidget(widgets, "backward-delete-word", this::backwardDeleteWord);
      this.addBuiltinWidget(widgets, "backward-kill-line", this::backwardKillLine);
      this.addBuiltinWidget(widgets, "backward-kill-word", this::backwardKillWord);
      this.addBuiltinWidget(widgets, "backward-word", this::backwardWord);
      this.addBuiltinWidget(widgets, "beep", this::beep);
      this.addBuiltinWidget(widgets, "beginning-of-buffer-or-history", this::beginningOfBufferOrHistory);
      this.addBuiltinWidget(widgets, "beginning-of-history", this::beginningOfHistory);
      this.addBuiltinWidget(widgets, "beginning-of-line", this::beginningOfLine);
      this.addBuiltinWidget(widgets, "beginning-of-line-hist", this::beginningOfLineHist);
      this.addBuiltinWidget(widgets, "capitalize-word", this::capitalizeWord);
      this.addBuiltinWidget(widgets, "clear", this::clear);
      this.addBuiltinWidget(widgets, "clear-screen", this::clearScreen);
      this.addBuiltinWidget(widgets, "complete-prefix", this::completePrefix);
      this.addBuiltinWidget(widgets, "complete-word", this::completeWord);
      this.addBuiltinWidget(widgets, "copy-prev-word", this::copyPrevWord);
      this.addBuiltinWidget(widgets, "copy-region-as-kill", this::copyRegionAsKill);
      this.addBuiltinWidget(widgets, "delete-char", this::deleteChar);
      this.addBuiltinWidget(widgets, "delete-char-or-list", this::deleteCharOrList);
      this.addBuiltinWidget(widgets, "delete-word", this::deleteWord);
      this.addBuiltinWidget(widgets, "digit-argument", this::digitArgument);
      this.addBuiltinWidget(widgets, "do-lowercase-version", this::doLowercaseVersion);
      this.addBuiltinWidget(widgets, "down-case-word", this::downCaseWord);
      this.addBuiltinWidget(widgets, "down-line", this::downLine);
      this.addBuiltinWidget(widgets, "down-line-or-history", this::downLineOrHistory);
      this.addBuiltinWidget(widgets, "down-line-or-search", this::downLineOrSearch);
      this.addBuiltinWidget(widgets, "down-history", this::downHistory);
      this.addBuiltinWidget(widgets, "edit-and-execute-command", this::editAndExecute);
      this.addBuiltinWidget(widgets, "emacs-editing-mode", this::emacsEditingMode);
      this.addBuiltinWidget(widgets, "emacs-backward-word", this::emacsBackwardWord);
      this.addBuiltinWidget(widgets, "emacs-forward-word", this::emacsForwardWord);
      this.addBuiltinWidget(widgets, "end-of-buffer-or-history", this::endOfBufferOrHistory);
      this.addBuiltinWidget(widgets, "end-of-history", this::endOfHistory);
      this.addBuiltinWidget(widgets, "end-of-line", this::endOfLine);
      this.addBuiltinWidget(widgets, "end-of-line-hist", this::endOfLineHist);
      this.addBuiltinWidget(widgets, "exchange-point-and-mark", this::exchangePointAndMark);
      this.addBuiltinWidget(widgets, "expand-history", this::expandHistory);
      this.addBuiltinWidget(widgets, "expand-or-complete", this::expandOrComplete);
      this.addBuiltinWidget(widgets, "expand-or-complete-prefix", this::expandOrCompletePrefix);
      this.addBuiltinWidget(widgets, "expand-word", this::expandWord);
      this.addBuiltinWidget(widgets, "fresh-line", this::freshLine);
      this.addBuiltinWidget(widgets, "forward-char", this::forwardChar);
      this.addBuiltinWidget(widgets, "forward-word", this::forwardWord);
      this.addBuiltinWidget(widgets, "history-incremental-search-backward", this::historyIncrementalSearchBackward);
      this.addBuiltinWidget(widgets, "history-incremental-search-forward", this::historyIncrementalSearchForward);
      this.addBuiltinWidget(widgets, "history-search-backward", this::historySearchBackward);
      this.addBuiltinWidget(widgets, "history-search-forward", this::historySearchForward);
      this.addBuiltinWidget(widgets, "insert-close-curly", this::insertCloseCurly);
      this.addBuiltinWidget(widgets, "insert-close-paren", this::insertCloseParen);
      this.addBuiltinWidget(widgets, "insert-close-square", this::insertCloseSquare);
      this.addBuiltinWidget(widgets, "insert-comment", this::insertComment);
      this.addBuiltinWidget(widgets, "kill-buffer", this::killBuffer);
      this.addBuiltinWidget(widgets, "kill-line", this::killLine);
      this.addBuiltinWidget(widgets, "kill-region", this::killRegion);
      this.addBuiltinWidget(widgets, "kill-whole-line", this::killWholeLine);
      this.addBuiltinWidget(widgets, "kill-word", this::killWord);
      this.addBuiltinWidget(widgets, "list-choices", this::listChoices);
      this.addBuiltinWidget(widgets, "menu-complete", this::menuComplete);
      this.addBuiltinWidget(widgets, "menu-expand-or-complete", this::menuExpandOrComplete);
      this.addBuiltinWidget(widgets, "neg-argument", this::negArgument);
      this.addBuiltinWidget(widgets, "overwrite-mode", this::overwriteMode);
      this.addBuiltinWidget(widgets, "quoted-insert", this::quotedInsert);
      this.addBuiltinWidget(widgets, "redisplay", this::redisplay);
      this.addBuiltinWidget(widgets, "redraw-line", this::redrawLine);
      this.addBuiltinWidget(widgets, "redo", this::redo);
      this.addBuiltinWidget(widgets, "self-insert", this::selfInsert);
      this.addBuiltinWidget(widgets, "self-insert-unmeta", this::selfInsertUnmeta);
      this.addBuiltinWidget(widgets, "abort", this::sendBreak);
      this.addBuiltinWidget(widgets, "set-mark-command", this::setMarkCommand);
      this.addBuiltinWidget(widgets, "transpose-chars", this::transposeChars);
      this.addBuiltinWidget(widgets, "transpose-words", this::transposeWords);
      this.addBuiltinWidget(widgets, "undefined-key", this::undefinedKey);
      this.addBuiltinWidget(widgets, "universal-argument", this::universalArgument);
      this.addBuiltinWidget(widgets, "undo", this::undo);
      this.addBuiltinWidget(widgets, "up-case-word", this::upCaseWord);
      this.addBuiltinWidget(widgets, "up-history", this::upHistory);
      this.addBuiltinWidget(widgets, "up-line", this::upLine);
      this.addBuiltinWidget(widgets, "up-line-or-history", this::upLineOrHistory);
      this.addBuiltinWidget(widgets, "up-line-or-search", this::upLineOrSearch);
      this.addBuiltinWidget(widgets, "vi-add-eol", this::viAddEol);
      this.addBuiltinWidget(widgets, "vi-add-next", this::viAddNext);
      this.addBuiltinWidget(widgets, "vi-backward-char", this::viBackwardChar);
      this.addBuiltinWidget(widgets, "vi-backward-delete-char", this::viBackwardDeleteChar);
      this.addBuiltinWidget(widgets, "vi-backward-blank-word", this::viBackwardBlankWord);
      this.addBuiltinWidget(widgets, "vi-backward-blank-word-end", this::viBackwardBlankWordEnd);
      this.addBuiltinWidget(widgets, "vi-backward-kill-word", this::viBackwardKillWord);
      this.addBuiltinWidget(widgets, "vi-backward-word", this::viBackwardWord);
      this.addBuiltinWidget(widgets, "vi-backward-word-end", this::viBackwardWordEnd);
      this.addBuiltinWidget(widgets, "vi-beginning-of-line", this::viBeginningOfLine);
      this.addBuiltinWidget(widgets, "vi-cmd-mode", this::viCmdMode);
      this.addBuiltinWidget(widgets, "vi-digit-or-beginning-of-line", this::viDigitOrBeginningOfLine);
      this.addBuiltinWidget(widgets, "vi-down-line-or-history", this::viDownLineOrHistory);
      this.addBuiltinWidget(widgets, "vi-change-to", this::viChange);
      this.addBuiltinWidget(widgets, "vi-change-eol", this::viChangeEol);
      this.addBuiltinWidget(widgets, "vi-change-whole-line", this::viChangeWholeLine);
      this.addBuiltinWidget(widgets, "vi-delete-char", this::viDeleteChar);
      this.addBuiltinWidget(widgets, "vi-delete", this::viDelete);
      this.addBuiltinWidget(widgets, "vi-end-of-line", this::viEndOfLine);
      this.addBuiltinWidget(widgets, "vi-kill-eol", this::viKillEol);
      this.addBuiltinWidget(widgets, "vi-first-non-blank", this::viFirstNonBlank);
      this.addBuiltinWidget(widgets, "vi-find-next-char", this::viFindNextChar);
      this.addBuiltinWidget(widgets, "vi-find-next-char-skip", this::viFindNextCharSkip);
      this.addBuiltinWidget(widgets, "vi-find-prev-char", this::viFindPrevChar);
      this.addBuiltinWidget(widgets, "vi-find-prev-char-skip", this::viFindPrevCharSkip);
      this.addBuiltinWidget(widgets, "vi-forward-blank-word", this::viForwardBlankWord);
      this.addBuiltinWidget(widgets, "vi-forward-blank-word-end", this::viForwardBlankWordEnd);
      this.addBuiltinWidget(widgets, "vi-forward-char", this::viForwardChar);
      this.addBuiltinWidget(widgets, "vi-forward-word", this::viForwardWord);
      this.addBuiltinWidget(widgets, "vi-forward-word", this::viForwardWord);
      this.addBuiltinWidget(widgets, "vi-forward-word-end", this::viForwardWordEnd);
      this.addBuiltinWidget(widgets, "vi-history-search-backward", this::viHistorySearchBackward);
      this.addBuiltinWidget(widgets, "vi-history-search-forward", this::viHistorySearchForward);
      this.addBuiltinWidget(widgets, "vi-insert", this::viInsert);
      this.addBuiltinWidget(widgets, "vi-insert-bol", this::viInsertBol);
      this.addBuiltinWidget(widgets, "vi-insert-comment", this::viInsertComment);
      this.addBuiltinWidget(widgets, "vi-join", this::viJoin);
      this.addBuiltinWidget(widgets, "vi-kill-line", this::viKillWholeLine);
      this.addBuiltinWidget(widgets, "vi-match-bracket", this::viMatchBracket);
      this.addBuiltinWidget(widgets, "vi-open-line-above", this::viOpenLineAbove);
      this.addBuiltinWidget(widgets, "vi-open-line-below", this::viOpenLineBelow);
      this.addBuiltinWidget(widgets, "vi-put-after", this::viPutAfter);
      this.addBuiltinWidget(widgets, "vi-put-before", this::viPutBefore);
      this.addBuiltinWidget(widgets, "vi-repeat-find", this::viRepeatFind);
      this.addBuiltinWidget(widgets, "vi-repeat-search", this::viRepeatSearch);
      this.addBuiltinWidget(widgets, "vi-replace-chars", this::viReplaceChars);
      this.addBuiltinWidget(widgets, "vi-rev-repeat-find", this::viRevRepeatFind);
      this.addBuiltinWidget(widgets, "vi-rev-repeat-search", this::viRevRepeatSearch);
      this.addBuiltinWidget(widgets, "vi-swap-case", this::viSwapCase);
      this.addBuiltinWidget(widgets, "vi-up-line-or-history", this::viUpLineOrHistory);
      this.addBuiltinWidget(widgets, "vi-yank", this::viYankTo);
      this.addBuiltinWidget(widgets, "vi-yank-whole-line", this::viYankWholeLine);
      this.addBuiltinWidget(widgets, "visual-line-mode", this::visualLineMode);
      this.addBuiltinWidget(widgets, "visual-mode", this::visualMode);
      this.addBuiltinWidget(widgets, "what-cursor-position", this::whatCursorPosition);
      this.addBuiltinWidget(widgets, "yank", this::yank);
      this.addBuiltinWidget(widgets, "yank-pop", this::yankPop);
      this.addBuiltinWidget(widgets, "mouse", this::mouse);
      this.addBuiltinWidget(widgets, "begin-paste", this::beginPaste);
      this.addBuiltinWidget(widgets, "terminal-focus-in", this::focusIn);
      this.addBuiltinWidget(widgets, "terminal-focus-out", this::focusOut);
      return widgets;
   }

   private void addBuiltinWidget(Map<String, Widget> widgets, String name, Widget widget) {
      widgets.put(name, this.namedWidget("." + name, widget));
   }

   private Widget namedWidget(final String name, final Widget widget) {
      return new Widget() {
         @Override
         public String toString() {
            return name;
         }

         @Override
         public boolean apply() {
            return widget.apply();
         }
      };
   }

   public boolean redisplay() {
      this.redisplay(true);
      return true;
   }

   protected void redisplay(boolean flush) {
      try {
         this.lock.lock();
         if (this.skipRedisplay) {
            this.skipRedisplay = false;
            return;
         }

         Status status = Status.getStatus(this.terminal, false);
         if (status != null) {
            status.redraw();
         }

         if (this.size.getRows() <= 0 || this.size.getRows() >= 3) {
            List<AttributedString> secondaryPrompts = new ArrayList<>();
            AttributedString full = this.getDisplayedBufferWithPrompts(secondaryPrompts);
            List<AttributedString> newLines;
            if (this.size.getColumns() <= 0) {
               newLines = new ArrayList<>();
               newLines.add(full);
            } else {
               newLines = full.columnSplitLength(this.size.getColumns(), true, this.display.delayLineWrap());
            }

            List<AttributedString> rightPromptLines;
            if (this.rightPrompt.length() != 0 && this.size.getColumns() > 0) {
               rightPromptLines = this.rightPrompt.columnSplitLength(this.size.getColumns());
            } else {
               rightPromptLines = new ArrayList<>();
            }

            while (newLines.size() < rightPromptLines.size()) {
               newLines.add(new AttributedString(""));
            }

            for (int i = 0; i < rightPromptLines.size(); i++) {
               AttributedString line = rightPromptLines.get(i);
               newLines.set(i, this.addRightPrompt(line, newLines.get(i)));
            }

            int cursorPos = -1;
            int cursorNewLinesId = -1;
            int cursorColPos = -1;
            if (this.size.getColumns() > 0) {
               AttributedStringBuilder sb = new AttributedStringBuilder().tabs(this.getTabWidth());
               sb.append(this.prompt);
               String buffer = this.buf.upToCursor();
               if (this.maskingCallback != null) {
                  buffer = this.maskingCallback.display(buffer);
               }

               sb.append(this.insertSecondaryPrompts(new AttributedString(buffer), secondaryPrompts, false));
               List<AttributedString> promptLines = sb.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap());
               if (!promptLines.isEmpty()) {
                  cursorNewLinesId = promptLines.size() - 1;
                  cursorColPos = promptLines.get(promptLines.size() - 1).columnLength();
                  cursorPos = this.size.cursorPos(cursorNewLinesId, cursorColPos);
               }
            }

            List<AttributedString> newLinesToDisplay = new ArrayList<>();
            int displaySize = this.displayRows(status);
            if (newLines.size() > displaySize && !this.isTerminalDumb()) {
               StringBuilder sbx = new StringBuilder(">....");

               for (int i = sbx.toString().length(); i < this.size.getColumns(); i++) {
                  sbx.append(" ");
               }

               AttributedString partialCommandInfo = new AttributedString(sbx.toString());
               int lineId = newLines.size() - displaySize + 1;
               int endId = displaySize;
               int startId = 1;
               if (lineId > cursorNewLinesId) {
                  lineId = cursorNewLinesId;
                  endId = displaySize - 1;
                  startId = 0;
               } else {
                  newLinesToDisplay.add(partialCommandInfo);
               }

               int cursorRowPos = 0;

               for (int i = startId; i < endId; i++) {
                  if (cursorNewLinesId == lineId) {
                     cursorRowPos = i;
                  }

                  newLinesToDisplay.add(newLines.get(lineId++));
               }

               if (startId == 0) {
                  newLinesToDisplay.add(partialCommandInfo);
               }

               cursorPos = this.size.cursorPos(cursorRowPos, cursorColPos);
            } else {
               newLinesToDisplay = newLines;
            }

            this.display.update(newLinesToDisplay, cursorPos, flush);
            return;
         }

         AttributedStringBuilder sbx = new AttributedStringBuilder().tabs(this.getTabWidth());
         sbx.append(this.prompt);
         this.concat(this.getHighlightedBuffer(this.buf.toString()).columnSplitLength(Integer.MAX_VALUE), sbx);
         AttributedString fullx = sbx.toAttributedString();
         sbx.setLength(0);
         sbx.append(this.prompt);
         String line = this.buf.upToCursor();
         if (this.maskingCallback != null) {
            line = this.maskingCallback.display(line);
         }

         this.concat(new AttributedString(line).columnSplitLength(Integer.MAX_VALUE), sbx);
         AttributedString toCursor = sbx.toAttributedString();
         int w = WCWidth.wcwidth(8230);
         int width = this.size.getColumns();
         int cursor = toCursor.columnLength();
         int inc = width / 2 + 1;

         while (cursor <= this.smallTerminalOffset + w) {
            this.smallTerminalOffset -= inc;
         }

         while (cursor >= this.smallTerminalOffset + width - w) {
            this.smallTerminalOffset += inc;
         }

         if (this.smallTerminalOffset > 0) {
            sbx.setLength(0);
            sbx.append("…");
            sbx.append(fullx.columnSubSequence(this.smallTerminalOffset + w, Integer.MAX_VALUE));
            fullx = sbx.toAttributedString();
         }

         int length = fullx.columnLength();
         if (length >= this.smallTerminalOffset + width) {
            sbx.setLength(0);
            sbx.append(fullx.columnSubSequence(0, width - w));
            sbx.append("…");
            fullx = sbx.toAttributedString();
         }

         this.display.update(Collections.singletonList(fullx), cursor - this.smallTerminalOffset, flush);
      } finally {
         this.lock.unlock();
      }
   }

   private void concat(List<AttributedString> lines, AttributedStringBuilder sb) {
      if (lines.size() > 1) {
         for (int i = 0; i < lines.size() - 1; i++) {
            sb.append(lines.get(i));
            sb.style(sb.style().inverse());
            sb.append("\\n");
            sb.style(sb.style().inverseOff());
         }
      }

      sb.append(lines.get(lines.size() - 1));
   }

   private String matchPreviousCommand(String buffer) {
      if (buffer.length() == 0) {
         return "";
      } else {
         History history = this.getHistory();
         StringBuilder sb = new StringBuilder();

         for (char c : buffer.replace("\\", "\\\\").toCharArray()) {
            if (c == '('
               || c == ')'
               || c == '['
               || c == ']'
               || c == '{'
               || c == '}'
               || c == '^'
               || c == '*'
               || c == '$'
               || c == '.'
               || c == '?'
               || c == '+'
               || c == '|'
               || c == '<'
               || c == '>'
               || c == '!'
               || c == '-') {
               sb.append('\\');
            }

            sb.append(c);
         }

         Pattern pattern = Pattern.compile(sb.toString() + ".*", 32);
         Iterator<History.Entry> iter = history.reverseIterator(history.last());
         String suggestion = "";

         for (int tot = 0; iter.hasNext(); tot++) {
            History.Entry entry = iter.next();
            Matcher matcher = pattern.matcher(entry.line());
            if (matcher.matches()) {
               suggestion = entry.line().substring(buffer.length());
               break;
            }

            if (tot > 200) {
               break;
            }
         }

         return suggestion;
      }
   }

   public AttributedString getDisplayedBufferWithPrompts(List<AttributedString> secondaryPrompts) {
      AttributedString attBuf = this.getHighlightedBuffer(this.buf.toString());
      AttributedString tNewBuf = this.insertSecondaryPrompts(attBuf, secondaryPrompts);
      AttributedStringBuilder full = new AttributedStringBuilder().tabs(this.getTabWidth());
      full.append(this.prompt);
      full.append(tNewBuf);
      if (this.doAutosuggestion && !this.isTerminalDumb()) {
         String lastBinding = this.getLastBinding() != null ? this.getLastBinding() : "";
         if (this.autosuggestion == LineReader.SuggestionType.HISTORY) {
            AttributedStringBuilder sb = new AttributedStringBuilder();
            this.tailTip = this.matchPreviousCommand(this.buf.toString());
            sb.styled(AttributedStyle::faint, this.tailTip);
            full.append(sb.toAttributedString());
         } else if (this.autosuggestion == LineReader.SuggestionType.COMPLETER) {
            if (this.buf.length() < this.getInt("suggestions-min-buffer-size", 1)
               || this.buf.length() != this.buf.cursor()
               || lastBinding.equals("\t") && this.buf.prevChar() != 32 && this.buf.prevChar() != 61) {
               if (!lastBinding.equals("\t")) {
                  this.clearChoices();
               }
            } else {
               this.clearChoices();
               this.listChoices(true);
            }
         } else if (this.autosuggestion == LineReader.SuggestionType.TAIL_TIP && this.buf.length() == this.buf.cursor()) {
            if (!lastBinding.equals("\t") || this.buf.prevChar() == 32) {
               this.clearChoices();
            }

            AttributedStringBuilder sb = new AttributedStringBuilder();
            if (this.buf.prevChar() != 32) {
               if (this.tailTip.startsWith("[")) {
                  sb.append(" ");
               } else {
                  int idx = this.tailTip.indexOf(32);
                  int idb = this.buf.toString().lastIndexOf(32);
                  int idd = this.buf.toString().lastIndexOf(45);
                  if (idx <= 0 || (idb != -1 || idb != idd) && (idb < 0 || idb <= idd)) {
                     if (idb >= 0 && idb < idd) {
                        sb.append(" ");
                     }
                  } else {
                     this.tailTip = this.tailTip.substring(idx);
                  }
               }
            }

            sb.styled(AttributedStyle::faint, this.tailTip);
            full.append(sb.toAttributedString());
         }
      }

      if (this.post != null) {
         full.append("\n");
         full.append(this.post.get());
      }

      this.doAutosuggestion = true;
      return full.toAttributedString();
   }

   private AttributedString getHighlightedBuffer(String buffer) {
      if (this.maskingCallback != null) {
         buffer = this.maskingCallback.display(buffer);
      }

      return this.highlighter != null && !this.isSet(LineReader.Option.DISABLE_HIGHLIGHTER) && buffer.length() < this.getInt("features-max-buffer-size", 1000)
         ? this.highlighter.highlight(this, buffer)
         : new AttributedString(buffer);
   }

   private AttributedString expandPromptPattern(String pattern, int padToWidth, String message, int line) {
      ArrayList<AttributedString> parts = new ArrayList<>();
      boolean isHidden = false;
      int padPartIndex = -1;
      StringBuilder padPartString = null;
      StringBuilder sb = new StringBuilder();
      pattern = pattern + "%{";
      int plen = pattern.length();
      int padChar = -1;
      int padPos = -1;
      int cols = 0;
      int i = 0;

      label108:
      while (i < plen) {
         char ch = pattern.charAt(i++);
         if (ch == '%' && i < plen) {
            int count = 0;
            boolean countSeen = false;

            while (true) {
               ch = pattern.charAt(i++);
               switch (ch) {
                  case '%':
                     sb.append(ch);
                     continue label108;
                  case '*':
                     if (this.currentLine == line) {
                        sb.append("*");
                     } else {
                        sb.append(" ");
                     }
                     continue label108;
                  case '-':
                  case '0':
                  case '1':
                  case '2':
                  case '3':
                  case '4':
                  case '5':
                  case '6':
                  case '7':
                  case '8':
                  case '9':
                     boolean neg = false;
                     if (ch == '-') {
                        neg = true;
                        ch = pattern.charAt(i++);
                     }

                     countSeen = true;

                     for (count = 0; ch >= '0' && ch <= '9'; ch = pattern.charAt(i++)) {
                        count = (count < 0 ? 0 : 10 * count) + (ch - '0');
                     }

                     if (neg) {
                        count = -count;
                     }

                     i--;
                     break;
                  case 'M':
                     if (message != null) {
                        sb.append(message);
                     }
                     continue label108;
                  case 'N':
                     sb.append(this.getInt("line-offset", 0) + line);
                     continue label108;
                  case 'P':
                     if (countSeen && count >= 0) {
                        padToWidth = count;
                     }

                     if (i < plen) {
                        padChar = pattern.charAt(i++);
                     }

                     padPos = sb.length();
                     padPartIndex = parts.size();
                     continue label108;
                  case '{':
                  case '}':
                     String str = sb.toString();
                     AttributedString astr;
                     if (!isHidden) {
                        astr = this.fromAnsi(str);
                        cols += astr.columnLength();
                     } else {
                        astr = new AttributedString(str, AttributedStyle.HIDDEN);
                     }

                     if (padPartIndex == parts.size()) {
                        padPartString = sb;
                        if (i < plen) {
                           sb = new StringBuilder();
                        }
                     } else {
                        sb.setLength(0);
                     }

                     parts.add(astr);
                     isHidden = ch == '{';
                  default:
                     continue label108;
               }
            }
         } else {
            sb.append(ch);
         }
      }

      if (padToWidth > cols) {
         i = WCWidth.wcwidth(padChar);
         int padCount = (padToWidth - cols) / i;
         sb = padPartString;

         while (--padCount >= 0) {
            sb.insert(padPos, (char)padChar);
         }

         parts.set(padPartIndex, this.fromAnsi(sb.toString()));
      }

      return AttributedString.join(null, parts);
   }

   private AttributedString fromAnsi(String str) {
      return AttributedString.fromAnsi(str, Collections.singletonList(0), this.alternateIn, this.alternateOut);
   }

   private AttributedString insertSecondaryPrompts(AttributedString str, List<AttributedString> prompts) {
      return this.insertSecondaryPrompts(str, prompts, true);
   }

   private AttributedString insertSecondaryPrompts(AttributedString strAtt, List<AttributedString> prompts, boolean computePrompts) {
      Objects.requireNonNull(prompts);
      List<AttributedString> lines = strAtt.columnSplitLength(Integer.MAX_VALUE);
      AttributedStringBuilder sb = new AttributedStringBuilder();
      String secondaryPromptPattern = this.getString("secondary-prompt-pattern", "%M> ");
      boolean needsMessage = secondaryPromptPattern.contains("%M") && strAtt.length() < this.getInt("features-max-buffer-size", 1000);
      AttributedStringBuilder buf = new AttributedStringBuilder();
      int width = 0;
      List<String> missings = new ArrayList<>();
      if (computePrompts && secondaryPromptPattern.contains("%P")) {
         width = this.prompt.columnLength();
         if (width > this.size.getColumns() || this.prompt.contains('\n')) {
            width = new LineReaderImpl.TerminalLine(this.prompt.toString(), 0, this.size.getColumns()).getEndLine().length();
         }

         for (int line = 0; line < lines.size() - 1; line++) {
            buf.append(lines.get(line)).append("\n");
            String missing = "";
            if (needsMessage) {
               try {
                  this.parser.parse(buf.toString(), buf.length(), Parser.ParseContext.SECONDARY_PROMPT);
               } catch (EOFError var19) {
                  missing = var19.getMissing();
               } catch (SyntaxError var20) {
               }
            }

            missings.add(missing);
            AttributedString prompt = this.expandPromptPattern(secondaryPromptPattern, 0, missing, line + 1);
            width = Math.max(width, prompt.columnLength());
         }

         buf.setLength(0);
      }

      int line = 0;
      this.currentLine = -1;
      int cursor = this.buf.cursor();
      int start = 0;

      for (int l = 0; l < lines.size(); l++) {
         int end = start + lines.get(l).length();
         if (cursor >= start && cursor <= end) {
            this.currentLine = l;
            break;
         }

         start = end + 1;
      }

      while (line < lines.size() - 1) {
         sb.append(lines.get(line)).append("\n");
         buf.append(lines.get(line)).append("\n");
         AttributedString prompt;
         if (computePrompts) {
            String missing = "";
            if (needsMessage) {
               if (missings.isEmpty()) {
                  try {
                     this.parser.parse(buf.toString(), buf.length(), Parser.ParseContext.SECONDARY_PROMPT);
                  } catch (EOFError var17) {
                     missing = var17.getMissing();
                  } catch (SyntaxError var18) {
                  }
               } else {
                  missing = missings.get(line);
               }
            }

            prompt = this.expandPromptPattern(secondaryPromptPattern, width, missing, line + 1);
         } else {
            prompt = prompts.get(line);
         }

         prompts.add(prompt);
         sb.append(prompt);
         line++;
      }

      sb.append(lines.get(line));
      buf.append(lines.get(line));
      return sb.toAttributedString();
   }

   private AttributedString addRightPrompt(AttributedString prompt, AttributedString line) {
      int width = prompt.columnLength();
      boolean endsWithNl = line.length() > 0 && line.charAt(line.length() - 1) == '\n';
      int nb = this.size.getColumns() - width - (line.columnLength() + (endsWithNl ? 1 : 0));
      if (nb >= 3) {
         AttributedStringBuilder sb = new AttributedStringBuilder(this.size.getColumns());
         sb.append(line, 0, endsWithNl ? line.length() - 1 : line.length());

         for (int j = 0; j < nb; j++) {
            sb.append(' ');
         }

         sb.append(prompt);
         if (endsWithNl) {
            sb.append('\n');
         }

         line = sb.toAttributedString();
      }

      return line;
   }

   protected boolean insertTab() {
      return this.isSet(LineReader.Option.INSERT_TAB) && this.getLastBinding().equals("\t") && this.buf.toString().matches("(^|[\\s\\S]*\n)[\r\n\t ]*");
   }

   protected boolean expandHistory() {
      String str = this.buf.toString();
      String exp = this.expander.expandHistory(this.history, str);
      if (!exp.equals(str)) {
         this.buf.clear();
         this.buf.write(exp);
         return true;
      } else {
         return false;
      }
   }

   protected boolean expandWord() {
      return this.insertTab() ? this.selfInsert() : this.doComplete(LineReaderImpl.CompletionType.Expand, this.isSet(LineReader.Option.MENU_COMPLETE), false);
   }

   protected boolean expandOrComplete() {
      return this.insertTab()
         ? this.selfInsert()
         : this.doComplete(LineReaderImpl.CompletionType.ExpandComplete, this.isSet(LineReader.Option.MENU_COMPLETE), false);
   }

   protected boolean expandOrCompletePrefix() {
      return this.insertTab()
         ? this.selfInsert()
         : this.doComplete(LineReaderImpl.CompletionType.ExpandComplete, this.isSet(LineReader.Option.MENU_COMPLETE), true);
   }

   protected boolean completeWord() {
      return this.insertTab() ? this.selfInsert() : this.doComplete(LineReaderImpl.CompletionType.Complete, this.isSet(LineReader.Option.MENU_COMPLETE), false);
   }

   protected boolean menuComplete() {
      return this.insertTab() ? this.selfInsert() : this.doComplete(LineReaderImpl.CompletionType.Complete, true, false);
   }

   protected boolean menuExpandOrComplete() {
      return this.insertTab() ? this.selfInsert() : this.doComplete(LineReaderImpl.CompletionType.ExpandComplete, true, false);
   }

   protected boolean completePrefix() {
      return this.insertTab() ? this.selfInsert() : this.doComplete(LineReaderImpl.CompletionType.Complete, this.isSet(LineReader.Option.MENU_COMPLETE), true);
   }

   protected boolean listChoices() {
      return this.listChoices(false);
   }

   private boolean listChoices(boolean forSuggestion) {
      return this.doComplete(LineReaderImpl.CompletionType.List, this.isSet(LineReader.Option.MENU_COMPLETE), false, forSuggestion);
   }

   protected boolean deleteCharOrList() {
      return this.buf.cursor() == this.buf.length() && this.buf.length() != 0
         ? this.doComplete(LineReaderImpl.CompletionType.List, this.isSet(LineReader.Option.MENU_COMPLETE), false)
         : this.deleteChar();
   }

   protected boolean doComplete(LineReaderImpl.CompletionType lst, boolean useMenu, boolean prefix) {
      return this.doComplete(lst, useMenu, prefix, false);
   }

   protected boolean doComplete(LineReaderImpl.CompletionType lst, boolean useMenu, boolean prefix, boolean forSuggestion) {
      if (this.getBoolean("disable-completion", false)) {
         return true;
      } else {
         if (!this.isSet(LineReader.Option.DISABLE_EVENT_EXPANSION)) {
            try {
               if (this.expandHistory()) {
                  return true;
               }
            } catch (Exception var21) {
               Log.info("Error while expanding history", var21);
               return false;
            }
         }

         CompletingParsedLine line;
         try {
            line = wrap(this.parser.parse(this.buf.toString(), this.buf.cursor(), Parser.ParseContext.COMPLETE));
         } catch (Exception var20) {
            Log.info("Error while parsing line", var20);
            return false;
         }

         List<Candidate> candidates = new ArrayList<>();

         try {
            if (this.completer != null) {
               this.completer.complete(this, line, candidates);
            }
         } catch (Exception var22) {
            Log.info("Error while finding completion candidates", var22);
            if (Log.isDebugEnabled()) {
               var22.printStackTrace();
            }

            return false;
         }

         if (lst == LineReaderImpl.CompletionType.ExpandComplete || lst == LineReaderImpl.CompletionType.Expand) {
            String w = this.expander.expandVar(line.word());
            if (!line.word().equals(w)) {
               if (prefix) {
                  this.buf.backspace(line.wordCursor());
               } else {
                  this.buf.move(line.word().length() - line.wordCursor());
                  this.buf.backspace(line.word().length());
               }

               this.buf.write(w);
               return true;
            }

            if (lst == LineReaderImpl.CompletionType.Expand) {
               return false;
            }

            lst = LineReaderImpl.CompletionType.Complete;
         }

         boolean caseInsensitive = this.isSet(LineReader.Option.CASE_INSENSITIVE);
         int errors = this.getInt("errors", 2);
         this.completionMatcher.compile(this.options, prefix, line, caseInsensitive, errors, this.getOriginalGroupName());
         List<Candidate> possible = this.completionMatcher.matches(candidates);
         if (possible.isEmpty()) {
            return false;
         } else {
            this.size.copy(this.terminal.getSize());

            boolean var26;
            try {
               if (lst == LineReaderImpl.CompletionType.List) {
                  this.doList(possible, line.word(), false, line::escape, forSuggestion);
                  return !possible.isEmpty();
               }

               Candidate completion = null;
               if (possible.size() == 1) {
                  completion = possible.get(0);
               } else if (this.isSet(LineReader.Option.RECOGNIZE_EXACT)) {
                  completion = this.completionMatcher.exactMatch();
               }

               if (completion == null || completion.value().isEmpty()) {
                  if (useMenu) {
                     this.buf.move(line.word().length() - line.wordCursor());
                     this.buf.backspace(line.word().length());
                     this.doMenu(possible, line.word(), line::escape);
                     return true;
                  }

                  String current;
                  if (prefix) {
                     current = line.word().substring(0, line.wordCursor());
                  } else {
                     current = line.word();
                     this.buf.move(line.rawWordLength() - line.rawWordCursor());
                  }

                  String commonPrefix = this.completionMatcher.getCommonPrefix();
                  boolean hasUnambiguous = commonPrefix.startsWith(current) && !commonPrefix.equals(current);
                  if (hasUnambiguous) {
                     this.buf.backspace(line.rawWordLength());
                     this.buf.write(line.escape(commonPrefix, false));
                     this.callWidget("redisplay");
                     current = commonPrefix;
                     if ((
                           !this.isSet(LineReader.Option.AUTO_LIST) && this.isSet(LineReader.Option.AUTO_MENU)
                              || this.isSet(LineReader.Option.AUTO_LIST) && this.isSet(LineReader.Option.LIST_AMBIGUOUS)
                        )
                        && !this.nextBindingIsComplete()) {
                        return true;
                     }
                  }

                  if (!this.isSet(LineReader.Option.AUTO_LIST) || this.doList(possible, current, true, line::escape)) {
                     if (this.isSet(LineReader.Option.AUTO_MENU)) {
                        this.buf.backspace(current.length());
                        this.doMenu(possible, line.word(), line::escape);
                     }

                     return true;
                  }

                  return true;
               }

               if (prefix) {
                  this.buf.backspace(line.rawWordCursor());
               } else {
                  this.buf.move(line.rawWordLength() - line.rawWordCursor());
                  this.buf.backspace(line.rawWordLength());
               }

               this.buf.write(line.escape(completion.value(), completion.complete()));
               if (completion.complete()) {
                  if (this.buf.currChar() != 32) {
                     this.buf.write(" ");
                  } else {
                     this.buf.move(1);
                  }
               }

               if (completion.suffix() != null) {
                  if (this.autosuggestion == LineReader.SuggestionType.COMPLETER) {
                     this.listChoices(true);
                  }

                  this.redisplay();
                  Binding op = this.readBinding(this.getKeys());
                  if (op != null) {
                     String chars = this.getString("REMOVE_SUFFIX_CHARS", " \t\n;&|");
                     String ref = op instanceof Reference ? ((Reference)op).name() : null;
                     if ("self-insert".equals(ref) && chars.indexOf(this.getLastBinding().charAt(0)) >= 0 || "accept-line".equals(ref)) {
                        this.buf.backspace(completion.suffix().length());
                        if (this.getLastBinding().charAt(0) != ' ') {
                           this.buf.write(32);
                        }
                     }

                     this.pushBackBinding(true);
                  }
               }

               var26 = true;
            } finally {
               this.size.copy(this.terminal.getBufferSize());
            }

            return var26;
         }
      }
   }

   protected static CompletingParsedLine wrap(final ParsedLine line) {
      return line instanceof CompletingParsedLine ? (CompletingParsedLine)line : new CompletingParsedLine() {
         @Override
         public String word() {
            return line.word();
         }

         @Override
         public int wordCursor() {
            return line.wordCursor();
         }

         @Override
         public int wordIndex() {
            return line.wordIndex();
         }

         @Override
         public List<String> words() {
            return line.words();
         }

         @Override
         public String line() {
            return line.line();
         }

         @Override
         public int cursor() {
            return line.cursor();
         }

         @Override
         public CharSequence escape(CharSequence candidate, boolean complete) {
            return candidate;
         }

         @Override
         public int rawWordCursor() {
            return this.wordCursor();
         }

         @Override
         public int rawWordLength() {
            return this.word().length();
         }
      };
   }

   protected Comparator<Candidate> getCandidateComparator(boolean caseInsensitive, String word) {
      String wdi = caseInsensitive ? word.toLowerCase() : word;
      ToIntFunction<String> wordDistance = w -> ReaderUtils.distance(wdi, caseInsensitive ? w.toLowerCase() : w);
      return Comparator.comparing(Candidate::value, Comparator.comparingInt(wordDistance)).thenComparing(Comparator.naturalOrder());
   }

   protected String getOthersGroupName() {
      return this.getString("OTHERS_GROUP_NAME", "others");
   }

   protected String getOriginalGroupName() {
      return this.getString("ORIGINAL_GROUP_NAME", "original");
   }

   protected Comparator<String> getGroupComparator() {
      return Comparator.<String>comparingInt(s -> this.getOthersGroupName().equals(s) ? 1 : (this.getOriginalGroupName().equals(s) ? -1 : 0))
         .thenComparing(String::toLowerCase, Comparator.naturalOrder());
   }

   private void mergeCandidates(List<Candidate> possible) {
      Map<String, List<Candidate>> keyedCandidates = new HashMap<>();

      for (Candidate candidate : possible) {
         if (candidate.key() != null) {
            List<Candidate> cands = keyedCandidates.computeIfAbsent(candidate.key(), s -> new ArrayList<>());
            cands.add(candidate);
         }
      }

      if (!keyedCandidates.isEmpty()) {
         for (List<Candidate> candidates : keyedCandidates.values()) {
            if (candidates.size() >= 1) {
               possible.removeAll(candidates);
               candidates.sort(Comparator.comparing(Candidate::value));
               Candidate first = candidates.get(0);
               String disp = candidates.stream().map(Candidate::displ).collect(Collectors.joining(" "));
               possible.add(new Candidate(first.value(), disp, first.group(), first.descr(), first.suffix(), null, first.complete()));
            }
         }
      }
   }

   protected boolean nextBindingIsComplete() {
      this.redisplay();
      KeyMap<Binding> keyMap = this.keyMaps.get("menu");
      Binding operation = this.readBinding(this.getKeys(), keyMap);
      if (operation instanceof Reference && "menu-complete".equals(((Reference)operation).name())) {
         return true;
      } else {
         this.pushBackBinding();
         return false;
      }
   }

   private int displayRows() {
      return this.displayRows(Status.getStatus(this.terminal, false));
   }

   private int displayRows(Status status) {
      return this.size.getRows() - (status != null ? status.size() : 0);
   }

   private int visibleDisplayRows() {
      Status status = Status.getStatus(this.terminal, false);
      return this.terminal.getSize().getRows() - (status != null ? status.size() : 0);
   }

   private int promptLines() {
      AttributedString text = this.insertSecondaryPrompts(AttributedStringBuilder.append(this.prompt, this.buf.toString()), new ArrayList<>());
      return text.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap()).size();
   }

   protected boolean doMenu(List<Candidate> original, String completed, BiFunction<CharSequence, Boolean, CharSequence> escaper) {
      List<Candidate> possible = new ArrayList<>();
      boolean caseInsensitive = this.isSet(LineReader.Option.CASE_INSENSITIVE);
      original.sort(this.getCandidateComparator(caseInsensitive, completed));
      this.mergeCandidates(original);
      this.computePost(original, null, possible, completed);
      boolean defaultAutoGroup = this.isSet(LineReader.Option.AUTO_GROUP);
      boolean defaultGroup = this.isSet(LineReader.Option.GROUP);
      if (!this.isSet(LineReader.Option.GROUP_PERSIST)) {
         this.option(LineReader.Option.AUTO_GROUP, false);
         this.option(LineReader.Option.GROUP, false);
      }

      LineReaderImpl.MenuSupport menuSupport = new LineReaderImpl.MenuSupport(original, completed, escaper);
      this.post = menuSupport;
      this.callWidget("redisplay");
      KeyMap<Binding> keyMap = this.keyMaps.get("menu");

      Binding operation;
      while ((operation = this.readBinding(this.getKeys(), keyMap)) != null) {
         String ref = operation instanceof Reference ? ((Reference)operation).name() : "";
         switch (ref) {
            case "menu-complete":
               menuSupport.next();
               break;
            case "reverse-menu-complete":
               menuSupport.previous();
               break;
            case "up-line-or-history":
            case "up-line-or-search":
               menuSupport.up();
               break;
            case "down-line-or-history":
            case "down-line-or-search":
               menuSupport.down();
               break;
            case "forward-char":
               menuSupport.right();
               break;
            case "backward-char":
               menuSupport.left();
               break;
            case "clear-screen":
               this.clearScreen();
               break;
            default:
               Candidate completion = menuSupport.completion();
               if (completion.suffix() != null) {
                  String chars = this.getString("REMOVE_SUFFIX_CHARS", " \t\n;&|");
                  if ("self-insert".equals(ref) && chars.indexOf(this.getLastBinding().charAt(0)) >= 0 || "backward-delete-char".equals(ref)) {
                     this.buf.backspace(completion.suffix().length());
                  }
               }

               if (completion.complete() && this.getLastBinding().charAt(0) != ' ' && ("self-insert".equals(ref) || this.getLastBinding().charAt(0) != ' ')) {
                  this.buf.write(32);
               }

               if (!"accept-line".equals(ref)
                  && (!"self-insert".equals(ref) || completion.suffix() == null || !completion.suffix().startsWith(this.getLastBinding()))) {
                  this.pushBackBinding(true);
               }

               this.post = null;
               this.option(LineReader.Option.AUTO_GROUP, defaultAutoGroup);
               this.option(LineReader.Option.GROUP, defaultGroup);
               return true;
         }

         this.doAutosuggestion = false;
         this.callWidget("redisplay");
      }

      this.option(LineReader.Option.AUTO_GROUP, defaultAutoGroup);
      this.option(LineReader.Option.GROUP, defaultGroup);
      return false;
   }

   protected boolean clearChoices() {
      return this.doList(new ArrayList<>(), "", false, null, false);
   }

   protected boolean doList(List<Candidate> possible, String completed, boolean runLoop, BiFunction<CharSequence, Boolean, CharSequence> escaper) {
      return this.doList(possible, completed, runLoop, escaper, false);
   }

   protected boolean doList(
      List<Candidate> possible, String completed, boolean runLoop, BiFunction<CharSequence, Boolean, CharSequence> escaper, boolean forSuggestion
   ) {
      this.mergeCandidates(possible);
      AttributedString text = this.insertSecondaryPrompts(AttributedStringBuilder.append(this.prompt, this.buf.toString()), new ArrayList<>());
      int promptLines = text.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap()).size();
      LineReaderImpl.PostResult postResult = this.computePost(possible, null, null, completed);
      int lines = postResult.lines;
      int listMax = this.getInt("list-max", 100);
      int possibleSize = possible.size();
      if (possibleSize != 0 && this.size.getRows() != 0) {
         if (listMax > 0 && possibleSize >= listMax || lines >= this.size.getRows() - promptLines) {
            if (forSuggestion) {
               return false;
            }

            this.post = () -> new AttributedString(this.getAppName() + ": do you wish to see all " + possibleSize + " possibilities (" + lines + " lines)?");
            this.redisplay(true);
            int c = this.readCharacter();
            if (c != 121 && c != 89 && c != 9) {
               this.post = null;
               return false;
            }
         }

         boolean caseInsensitive = this.isSet(LineReader.Option.CASE_INSENSITIVE);
         StringBuilder sb = new StringBuilder();
         this.candidateStartPosition = 0;

         while (true) {
            String current = completed + sb.toString();
            List<Candidate> cands;
            if (sb.length() > 0) {
               this.completionMatcher.compile(this.options, false, new LineReaderImpl.CompletingWord(current), caseInsensitive, 0, null);
               cands = this.completionMatcher
                  .matches(possible)
                  .stream()
                  .sorted(this.getCandidateComparator(caseInsensitive, current))
                  .collect(Collectors.toList());
            } else {
               cands = possible.stream().sorted(this.getCandidateComparator(caseInsensitive, current)).collect(Collectors.toList());
            }

            if (this.isSet(LineReader.Option.AUTO_MENU_LIST) && this.candidateStartPosition == 0) {
               this.candidateStartPosition = this.candidateStartPosition(cands);
            }

            this.post = () -> {
               AttributedString t = this.insertSecondaryPrompts(AttributedStringBuilder.append(this.prompt, this.buf.toString()), new ArrayList<>());
               int pl = t.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap()).size();
               LineReaderImpl.PostResult pr = this.computePost(cands, null, null, current);
               if (pr.lines >= this.size.getRows() - pl) {
                  this.post = null;
                  int oldCursor = this.buf.cursor();
                  this.buf.cursor(this.buf.length());
                  this.redisplay(false);
                  this.buf.cursor(oldCursor);
                  this.println();
                  List<AttributedString> ls = pr.post.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap());
                  Display d = new Display(this.terminal, false);
                  d.resize(this.size.getRows(), this.size.getColumns());
                  d.update(ls, -1);
                  this.println();
                  this.redrawLine();
                  return new AttributedString("");
               } else {
                  return pr.post;
               }
            };
            if (!runLoop) {
               return false;
            }

            this.redisplay();
            Binding b = this.doReadBinding(this.getKeys(), null);
            if (b instanceof Reference) {
               String name = ((Reference)b).name();
               if (!"backward-delete-char".equals(name) && !"vi-backward-delete-char".equals(name)) {
                  if (!"self-insert".equals(name)) {
                     if (!"\t".equals(this.getLastBinding())) {
                        this.pushBackBinding();
                        this.post = null;
                        return false;
                     }

                     if (cands.size() == 1 || sb.length() > 0) {
                        this.post = null;
                        this.pushBackBinding();
                     } else if (this.isSet(LineReader.Option.AUTO_MENU)) {
                        this.buf.backspace(escaper.apply(current, false).length());
                        this.doMenu(cands, current, escaper);
                     }

                     return false;
                  }

                  sb.append(this.getLastBinding());
                  this.callWidget(name);
                  if (cands.isEmpty()) {
                     this.post = null;
                     return false;
                  }
               } else {
                  if (sb.length() == 0) {
                     this.pushBackBinding();
                     this.post = null;
                     return false;
                  }

                  sb.setLength(sb.length() - 1);
                  this.buf.backspace();
               }
            } else if (b == null) {
               this.post = null;
               return false;
            }
         }
      } else {
         return false;
      }
   }

   protected LineReaderImpl.PostResult computePost(List<Candidate> possible, Candidate selection, List<Candidate> ordered, String completed) {
      return this.computePost(
         possible,
         selection,
         ordered,
         completed,
         this.display::wcwidth,
         this.size.getColumns(),
         this.isSet(LineReader.Option.AUTO_GROUP),
         this.isSet(LineReader.Option.GROUP),
         this.isSet(LineReader.Option.LIST_ROWS_FIRST)
      );
   }

   protected LineReaderImpl.PostResult computePost(
      List<Candidate> possible,
      Candidate selection,
      List<Candidate> ordered,
      String completed,
      Function<String, Integer> wcwidth,
      int width,
      boolean autoGroup,
      boolean groupName,
      boolean rowsFirst
   ) {
      List<Object> strings = new ArrayList<>();
      if (groupName) {
         Comparator<String> groupComparator = this.getGroupComparator();
         Map<String, List<Candidate>> sorted = (Map<String, List<Candidate>>)(groupComparator != null ? new TreeMap<>(groupComparator) : new LinkedHashMap<>());

         for (Candidate cand : possible) {
            String group = cand.group();
            sorted.computeIfAbsent(group != null ? group : "", s -> new ArrayList<>()).add(cand);
         }

         for (Entry<String, List<Candidate>> entry : sorted.entrySet()) {
            String group = entry.getKey();
            if (group.isEmpty() && sorted.size() > 1) {
               group = this.getOthersGroupName();
            }

            if (!group.isEmpty() && autoGroup) {
               strings.add(group);
            }

            List<Candidate> candidates = entry.getValue();
            Collections.sort(candidates);
            strings.add(candidates);
            if (ordered != null) {
               ordered.addAll(candidates);
            }
         }
      } else {
         Set<String> groups = new LinkedHashSet<>();
         List<Candidate> sorted = new ArrayList<>();

         for (Candidate cand : possible) {
            String groupx = cand.group();
            if (groupx != null) {
               groups.add(groupx);
            }

            sorted.add(cand);
         }

         if (autoGroup) {
            strings.addAll(groups);
         }

         Collections.sort(sorted);
         strings.add(sorted);
         if (ordered != null) {
            ordered.addAll(sorted);
         }
      }

      return this.toColumns(strings, selection, completed, wcwidth, width, rowsFirst);
   }

   private int candidateStartPosition(List<Candidate> cands) {
      List<String> values = cands.stream()
         .map(c -> AttributedString.stripAnsi(c.displ()))
         .filter(c -> !c.matches("\\w+") && c.length() > 1)
         .collect(Collectors.toList());
      Set<String> notDelimiters = new HashSet<>();
      values.forEach(
         v -> v.substring(0, v.length() - 1)
            .chars()
            .filter(c -> !Character.isDigit(c) && !Character.isAlphabetic(c))
            .forEach(c -> notDelimiters.add(Character.toString((char)c)))
      );
      int width = this.size.getColumns();
      int promptLength = this.prompt != null ? this.prompt.length() : 0;
      if (promptLength > 0) {
         LineReaderImpl.TerminalLine tp = new LineReaderImpl.TerminalLine(this.prompt.toString(), 0, width);
         promptLength = tp.getEndLine().length();
      }

      LineReaderImpl.TerminalLine tl = new LineReaderImpl.TerminalLine(this.buf.substring(0, this.buf.cursor()), promptLength, width);
      int out = tl.getStartPos();
      String buffer = tl.getEndLine();

      for (int i = buffer.length(); i > 0; i--) {
         if (buffer.substring(0, i).matches(".*\\W") && !notDelimiters.contains(buffer.substring(i - 1, i))) {
            out += i;
            break;
         }
      }

      return out;
   }

   protected LineReaderImpl.PostResult toColumns(
      List<Object> items, Candidate selection, String completed, Function<String, Integer> wcwidth, int width, boolean rowsFirst
   ) {
      int[] out = new int[2];
      int maxWidth = 0;
      int listSize = 0;

      for (Object item : items) {
         if (item instanceof String) {
            int len = wcwidth.apply((String)item);
            maxWidth = Math.max(maxWidth, len);
         } else if (item instanceof List) {
            for (Candidate cand : (List)item) {
               listSize++;
               int len = wcwidth.apply(cand.displ());
               if (cand.descr() != null) {
                  len = ++len + "(".length();
                  len += wcwidth.apply(cand.descr());
                  len += ")".length();
               }

               maxWidth = Math.max(maxWidth, len);
            }
         }
      }

      AttributedStringBuilder sb = new AttributedStringBuilder();
      if (listSize > 0) {
         if (this.isSet(LineReader.Option.AUTO_MENU_LIST)
            && listSize < Math.min(this.getInt("menu-list-max", Integer.MAX_VALUE), this.visibleDisplayRows() - this.promptLines())) {
            maxWidth = Math.max(maxWidth, 25);
            sb.tabs(Math.max(Math.min(this.candidateStartPosition, width - maxWidth - 1), 1));
            width = maxWidth + 2;
            if (!this.isSet(LineReader.Option.GROUP_PERSIST)) {
               List<Candidate> list = new ArrayList<>();

               for (Object o : items) {
                  if (o instanceof Collection) {
                     list.addAll((Collection<? extends Candidate>)o);
                  }
               }

               list = list.stream().sorted(this.getCandidateComparator(this.isSet(LineReader.Option.CASE_INSENSITIVE), "")).collect(Collectors.toList());
               this.toColumns(list, width, maxWidth, sb, selection, completed, rowsFirst, true, out);
            } else {
               for (Object list : items) {
                  this.toColumns(list, width, maxWidth, sb, selection, completed, rowsFirst, true, out);
               }
            }
         } else {
            for (Object list : items) {
               this.toColumns(list, width, maxWidth, sb, selection, completed, rowsFirst, false, out);
            }
         }
      }

      if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
         sb.setLength(sb.length() - 1);
      }

      return new LineReaderImpl.PostResult(sb.toAttributedString(), out[0], out[1]);
   }

   protected void toColumns(
      Object items,
      int width,
      int maxWidth,
      AttributedStringBuilder sb,
      Candidate selection,
      String completed,
      boolean rowsFirst,
      boolean doMenuList,
      int[] out
   ) {
      if (maxWidth > 0 && width > 0) {
         if (items instanceof String) {
            if (doMenuList) {
               sb.style(AttributedStyle.DEFAULT);
               sb.append('\t');
            }

            AttributedStringBuilder asb = new AttributedStringBuilder();
            asb.style(this.getCompletionStyleGroup(doMenuList)).append((String)items).style(AttributedStyle.DEFAULT);
            if (doMenuList) {
               for (int k = ((String)items).length(); k < maxWidth + 1; k++) {
                  asb.append(' ');
               }
            }

            sb.style(this.getCompletionStyleBackground(doMenuList));
            sb.append((AttributedCharSequence)asb);
            sb.append("\n");
            out[0]++;
         } else if (items instanceof List) {
            List<Candidate> candidates = (List<Candidate>)items;
            maxWidth = Math.min(width, maxWidth);
            int c = width / maxWidth;

            while (c > 1 && c * maxWidth + (c - 1) * 3 >= width) {
               c--;
            }

            int lines = (candidates.size() + c - 1) / c;
            int columns = (candidates.size() + lines - 1) / lines;
            IntBinaryOperator index;
            if (rowsFirst) {
               index = (ix, jx) -> ix * columns + jx;
            } else {
               index = (ix, jx) -> jx * lines + ix;
            }

            for (int i = 0; i < lines; i++) {
               if (doMenuList) {
                  sb.style(AttributedStyle.DEFAULT);
                  sb.append('\t');
               }

               AttributedStringBuilder asb = new AttributedStringBuilder();

               for (int j = 0; j < columns; j++) {
                  int idx = index.applyAsInt(i, j);
                  if (idx < candidates.size()) {
                     Candidate cand = candidates.get(idx);
                     boolean hasRightItem = j < columns - 1 && index.applyAsInt(i, j + 1) < candidates.size();
                     AttributedString left = this.fromAnsi(cand.displ());
                     AttributedString right = this.fromAnsi(cand.descr());
                     int lw = left.columnLength();
                     int rw = 0;
                     if (right != null) {
                        int rem = maxWidth - (lw + 1 + "(".length() + ")".length());
                        rw = right.columnLength();
                        if (rw > rem) {
                           right = AttributedStringBuilder.append(right.columnSubSequence(0, rem - WCWidth.wcwidth(8230)), "…");
                           rw = right.columnLength();
                        }

                        right = AttributedStringBuilder.append("(", right, ")");
                        rw += "(".length() + ")".length();
                     }

                     if (cand == selection) {
                        out[1] = i;
                        asb.style(this.getCompletionStyleSelection(doMenuList));
                        if (left.toString().regionMatches(this.isSet(LineReader.Option.CASE_INSENSITIVE), 0, completed, 0, completed.length())) {
                           asb.append(left.toString(), 0, completed.length());
                           asb.append(left.toString(), completed.length(), left.length());
                        } else {
                           asb.append(left.toString());
                        }

                        for (int k = 0; k < maxWidth - lw - rw; k++) {
                           asb.append(' ');
                        }

                        if (right != null) {
                           asb.append(right);
                        }

                        asb.style(AttributedStyle.DEFAULT);
                     } else {
                        if (left.toString().regionMatches(this.isSet(LineReader.Option.CASE_INSENSITIVE), 0, completed, 0, completed.length())) {
                           asb.style(this.getCompletionStyleStarting(doMenuList));
                           asb.append(left, 0, completed.length());
                           asb.style(AttributedStyle.DEFAULT);
                           asb.append(left, completed.length(), left.length());
                        } else {
                           asb.append(left);
                        }

                        if (right != null || hasRightItem) {
                           for (int k = 0; k < maxWidth - lw - rw; k++) {
                              asb.append(' ');
                           }
                        }

                        if (right != null) {
                           asb.style(this.getCompletionStyleDescription(doMenuList));
                           asb.append(right);
                           asb.style(AttributedStyle.DEFAULT);
                        } else if (doMenuList) {
                           for (int k = lw; k < maxWidth; k++) {
                              asb.append(' ');
                           }
                        }
                     }

                     if (hasRightItem) {
                        for (int k = 0; k < 3; k++) {
                           asb.append(' ');
                        }
                     }

                     if (doMenuList) {
                        asb.append(' ');
                     }
                  }
               }

               sb.style(this.getCompletionStyleBackground(doMenuList));
               sb.append((AttributedCharSequence)asb);
               sb.append('\n');
            }

            out[0] += lines;
         }
      }
   }

   protected AttributedStyle getCompletionStyleStarting(boolean menuList) {
      return menuList ? this.getCompletionStyleListStarting() : this.getCompletionStyleStarting();
   }

   protected AttributedStyle getCompletionStyleDescription(boolean menuList) {
      return menuList ? this.getCompletionStyleListDescription() : this.getCompletionStyleDescription();
   }

   protected AttributedStyle getCompletionStyleGroup(boolean menuList) {
      return menuList ? this.getCompletionStyleListGroup() : this.getCompletionStyleGroup();
   }

   protected AttributedStyle getCompletionStyleSelection(boolean menuList) {
      return menuList ? this.getCompletionStyleListSelection() : this.getCompletionStyleSelection();
   }

   protected AttributedStyle getCompletionStyleBackground(boolean menuList) {
      return menuList ? this.getCompletionStyleListBackground() : this.getCompletionStyleBackground();
   }

   protected AttributedStyle getCompletionStyleStarting() {
      return this.getCompletionStyle("COMPLETION_STYLE_STARTING", "fg:cyan");
   }

   protected AttributedStyle getCompletionStyleDescription() {
      return this.getCompletionStyle("COMPLETION_STYLE_DESCRIPTION", "fg:bright-black");
   }

   protected AttributedStyle getCompletionStyleGroup() {
      return this.getCompletionStyle("COMPLETION_STYLE_GROUP", "fg:bright-magenta,bold");
   }

   protected AttributedStyle getCompletionStyleSelection() {
      return this.getCompletionStyle("COMPLETION_STYLE_SELECTION", "inverse");
   }

   protected AttributedStyle getCompletionStyleBackground() {
      return this.getCompletionStyle("COMPLETION_STYLE_BACKGROUND", "bg:default");
   }

   protected AttributedStyle getCompletionStyleListStarting() {
      return this.getCompletionStyle("COMPLETION_STYLE_LIST_STARTING", "fg:cyan");
   }

   protected AttributedStyle getCompletionStyleListDescription() {
      return this.getCompletionStyle("COMPLETION_STYLE_LIST_DESCRIPTION", "fg:bright-black");
   }

   protected AttributedStyle getCompletionStyleListGroup() {
      return this.getCompletionStyle("COMPLETION_STYLE_LIST_GROUP", "fg:black,bold");
   }

   protected AttributedStyle getCompletionStyleListSelection() {
      return this.getCompletionStyle("COMPLETION_STYLE_LIST_SELECTION", "inverse");
   }

   protected AttributedStyle getCompletionStyleListBackground() {
      return this.getCompletionStyle("COMPLETION_STYLE_LIST_BACKGROUND", "bg:bright-magenta");
   }

   protected AttributedStyle getCompletionStyle(String name, String value) {
      return new StyleResolver(s -> this.getString(s, null)).resolve("." + name, value);
   }

   protected AttributedStyle buildStyle(String str) {
      return this.fromAnsi("\u001b[" + str + "m ").styleAt(0);
   }

   protected boolean moveHistory(boolean next, int count) {
      boolean ok = true;
      int i = 0;

      while (i < count && (ok = this.moveHistory(next))) {
         i++;
      }

      return ok;
   }

   protected boolean moveHistory(boolean next) {
      if (!this.buf.toString().equals(this.history.current())) {
         this.modifiedHistory.put(this.history.index(), this.buf.toString());
      }

      if (next && !this.history.next()) {
         return false;
      } else if (!next && !this.history.previous()) {
         return false;
      } else {
         this.setBuffer(this.modifiedHistory.containsKey(this.history.index()) ? this.modifiedHistory.get(this.history.index()) : this.history.current());
         return true;
      }
   }

   void print(String str) {
      this.terminal.writer().write(str);
   }

   void println(String s) {
      this.print(s);
      this.println();
   }

   void println() {
      this.terminal.puts(InfoCmp.Capability.carriage_return);
      this.print("\n");
      this.redrawLine();
   }

   protected boolean killBuffer() {
      this.killRing.add(this.buf.toString());
      this.buf.clear();
      return true;
   }

   protected boolean killWholeLine() {
      if (this.buf.length() == 0) {
         return false;
      } else {
         int start;
         int end;
         if (this.count < 0) {
            end = this.buf.cursor();

            while (this.buf.atChar(end) != 0 && this.buf.atChar(end) != 10) {
               end++;
            }

            start = end;

            for (int count = -this.count; count > 0; count--) {
               while (start > 0 && this.buf.atChar(start - 1) != 10) {
                  start--;
               }

               start--;
            }
         } else {
            start = this.buf.cursor();

            while (start > 0 && this.buf.atChar(start - 1) != 10) {
               start--;
            }

            end = start;

            while (this.count-- > 0) {
               while (end < this.buf.length() && this.buf.atChar(end) != 10) {
                  end++;
               }

               if (end < this.buf.length()) {
                  end++;
               }
            }
         }

         String killed = this.buf.substring(start, end);
         this.buf.cursor(start);
         this.buf.delete(end - start);
         this.killRing.add(killed);
         return true;
      }
   }

   public boolean killLine() {
      if (this.count < 0) {
         return this.callNeg(this::backwardKillLine);
      } else if (this.buf.cursor() == this.buf.length()) {
         return false;
      } else {
         int cp = this.buf.cursor();
         int len = cp;

         while (this.count-- > 0) {
            if (this.buf.atChar(len) == 10) {
               len++;
            } else {
               while (this.buf.atChar(len) != 0 && this.buf.atChar(len) != 10) {
                  len++;
               }
            }
         }

         int num = len - cp;
         String killed = this.buf.substring(cp, cp + num);
         this.buf.delete(num);
         this.killRing.add(killed);
         return true;
      }
   }

   public boolean backwardKillLine() {
      if (this.count < 0) {
         return this.callNeg(this::killLine);
      } else if (this.buf.cursor() == 0) {
         return false;
      } else {
         int cp = this.buf.cursor();
         int beg = cp;

         while (this.count-- > 0 && beg != 0) {
            if (this.buf.atChar(beg - 1) == 10) {
               beg--;
            } else {
               while (beg > 0 && this.buf.atChar(beg - 1) != 0 && this.buf.atChar(beg - 1) != 10) {
                  beg--;
               }
            }
         }

         int num = cp - beg;
         String killed = this.buf.substring(cp - beg, cp);
         this.buf.cursor(beg);
         this.buf.delete(num);
         this.killRing.add(killed);
         return true;
      }
   }

   public boolean killRegion() {
      return this.doCopyKillRegion(true);
   }

   public boolean copyRegionAsKill() {
      return this.doCopyKillRegion(false);
   }

   private boolean doCopyKillRegion(boolean kill) {
      if (this.regionMark > this.buf.length()) {
         this.regionMark = this.buf.length();
      }

      if (this.regionActive == LineReader.RegionType.LINE) {
         int start = this.regionMark;
         int end = this.buf.cursor();
         if (start < end) {
            while (start > 0 && this.buf.atChar(start - 1) != 10) {
               start--;
            }

            while (end < this.buf.length() - 1 && this.buf.atChar(end + 1) != 10) {
               end++;
            }

            if (this.isInViCmdMode()) {
               end++;
            }

            this.killRing.add(this.buf.substring(start, end));
            if (kill) {
               this.buf.backspace(end - start);
            }
         } else {
            while (end > 0 && this.buf.atChar(end - 1) != 10) {
               end--;
            }

            while (start < this.buf.length() && this.buf.atChar(start) != 10) {
               start++;
            }

            if (this.isInViCmdMode()) {
               start++;
            }

            this.killRing.addBackwards(this.buf.substring(end, start));
            if (kill) {
               this.buf.cursor(end);
               this.buf.delete(start - end);
            }
         }
      } else if (this.regionMark > this.buf.cursor()) {
         if (this.isInViCmdMode()) {
            this.regionMark++;
         }

         this.killRing.add(this.buf.substring(this.buf.cursor(), this.regionMark));
         if (kill) {
            this.buf.delete(this.regionMark - this.buf.cursor());
         }
      } else {
         if (this.isInViCmdMode()) {
            this.buf.move(1);
         }

         this.killRing.add(this.buf.substring(this.regionMark, this.buf.cursor()));
         if (kill) {
            this.buf.backspace(this.buf.cursor() - this.regionMark);
         }
      }

      if (kill) {
         this.regionActive = LineReader.RegionType.NONE;
      }

      return true;
   }

   public boolean yank() {
      String yanked = this.killRing.yank();
      if (yanked == null) {
         return false;
      } else {
         this.putString(yanked);
         return true;
      }
   }

   public boolean yankPop() {
      if (!this.killRing.lastYank()) {
         return false;
      } else {
         String current = this.killRing.yank();
         if (current == null) {
            return false;
         } else {
            this.buf.backspace(current.length());
            String yanked = this.killRing.yankPop();
            if (yanked == null) {
               return false;
            } else {
               this.putString(yanked);
               return true;
            }
         }
      }
   }

   public boolean mouse() {
      MouseEvent event = this.readMouseEvent();
      if (event.getType() == MouseEvent.Type.Released && event.getButton() == MouseEvent.Button.Button1) {
         StringBuilder tsb = new StringBuilder();
         Cursor cursor = this.terminal.getCursorPosition(c -> tsb.append((char)c));
         this.bindingReader.runMacro(tsb.toString());
         List<AttributedString> secondaryPrompts = new ArrayList<>();
         this.getDisplayedBufferWithPrompts(secondaryPrompts);
         AttributedStringBuilder sb = new AttributedStringBuilder().tabs(this.getTabWidth());
         sb.append(this.prompt);
         sb.append(this.insertSecondaryPrompts(new AttributedString(this.buf.upToCursor()), secondaryPrompts, false));
         List<AttributedString> promptLines = sb.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap());
         int currentLine = promptLines.size() - 1;
         int wantedLine = Math.max(0, Math.min(currentLine + event.getY() - cursor.getY(), secondaryPrompts.size()));
         int pl0 = currentLine == 0 ? this.prompt.columnLength() : secondaryPrompts.get(currentLine - 1).columnLength();
         int pl1 = wantedLine == 0 ? this.prompt.columnLength() : secondaryPrompts.get(wantedLine - 1).columnLength();
         int adjust = pl1 - pl0;
         this.buf.moveXY(event.getX() - cursor.getX() - adjust, event.getY() - cursor.getY());
      }

      return true;
   }

   public boolean beginPaste() {
      String str = this.doReadStringUntil("\u001b[201~");
      this.regionActive = LineReader.RegionType.PASTE;
      this.regionMark = this.getBuffer().cursor();
      this.getBuffer().write(str.replace('\r', '\n'));
      return true;
   }

   public boolean focusIn() {
      return false;
   }

   public boolean focusOut() {
      return false;
   }

   public boolean clear() {
      this.display.update(Collections.emptyList(), 0);
      return true;
   }

   public boolean clearScreen() {
      if (this.terminal.puts(InfoCmp.Capability.clear_screen)) {
         if ("windows-conemu".equals(this.terminal.getType()) && !Boolean.getBoolean("org.jline.terminal.conemu.disable-activate")) {
            this.terminal.writer().write("\u001b[9999E");
         }

         Status status = Status.getStatus(this.terminal, false);
         if (status != null) {
            status.reset();
         }

         this.redrawLine();
      } else {
         this.println();
      }

      return true;
   }

   public boolean beep() {
      LineReaderImpl.BellType bell_preference = LineReaderImpl.BellType.AUDIBLE;
      String var2 = this.getString("bell-style", "").toLowerCase();
      switch (var2) {
         case "none":
         case "off":
            bell_preference = LineReaderImpl.BellType.NONE;
            break;
         case "audible":
            bell_preference = LineReaderImpl.BellType.AUDIBLE;
            break;
         case "visible":
            bell_preference = LineReaderImpl.BellType.VISIBLE;
            break;
         case "on":
            bell_preference = this.getBoolean("prefer-visible-bell", false) ? LineReaderImpl.BellType.VISIBLE : LineReaderImpl.BellType.AUDIBLE;
      }

      if (bell_preference == LineReaderImpl.BellType.VISIBLE) {
         if (this.terminal.puts(InfoCmp.Capability.flash_screen) || this.terminal.puts(InfoCmp.Capability.bell)) {
            this.flush();
         }
      } else if (bell_preference == LineReaderImpl.BellType.AUDIBLE && this.terminal.puts(InfoCmp.Capability.bell)) {
         this.flush();
      }

      return true;
   }

   protected boolean isDelimiter(int c) {
      return !Character.isLetterOrDigit(c);
   }

   protected boolean isWhitespace(int c) {
      return Character.isWhitespace(c);
   }

   protected boolean isViAlphaNum(int c) {
      return c == 95 || Character.isLetterOrDigit(c);
   }

   protected boolean isAlpha(int c) {
      return Character.isLetter(c);
   }

   protected boolean isWord(int c) {
      String wordchars = this.getString("WORDCHARS", "*?_-.[]~=/&;!#$%^(){}<>");
      return Character.isLetterOrDigit(c) || c < 128 && wordchars.indexOf((char)c) >= 0;
   }

   String getString(String name, String def) {
      return ReaderUtils.getString(this, name, def);
   }

   boolean getBoolean(String name, boolean def) {
      return ReaderUtils.getBoolean(this, name, def);
   }

   int getInt(String name, int def) {
      return ReaderUtils.getInt(this, name, def);
   }

   long getLong(String name, long def) {
      return ReaderUtils.getLong(this, name, def);
   }

   @Override
   public Map<String, KeyMap<Binding>> defaultKeyMaps() {
      Map<String, KeyMap<Binding>> keyMaps = new HashMap<>();
      keyMaps.put("emacs", this.emacs());
      keyMaps.put("vicmd", this.viCmd());
      keyMaps.put("viins", this.viInsertion());
      keyMaps.put("menu", this.menu());
      keyMaps.put("viopp", this.viOpp());
      keyMaps.put("visual", this.visual());
      keyMaps.put(".safe", this.safe());
      keyMaps.put("dumb", this.dumb());
      if (this.getBoolean("bind-tty-special-chars", true)) {
         Attributes attr = this.terminal.getAttributes();
         this.bindConsoleChars(keyMaps.get("emacs"), attr);
         this.bindConsoleChars(keyMaps.get("viins"), attr);
      }

      for (KeyMap<Binding> keyMap : keyMaps.values()) {
         keyMap.setUnicode(new Reference("self-insert"));
         keyMap.setAmbiguousTimeout(this.getLong("ambiguous-binding", 1000L));
      }

      keyMaps.put("main", keyMaps.get(this.isTerminalDumb() ? "dumb" : "emacs"));
      return keyMaps;
   }

   public KeyMap<Binding> emacs() {
      KeyMap<Binding> emacs = new KeyMap<>();
      this.bindKeys(emacs);
      this.bind(emacs, "set-mark-command", KeyMap.ctrl('@'));
      this.bind(emacs, "beginning-of-line", KeyMap.ctrl('A'));
      this.bind(emacs, "backward-char", KeyMap.ctrl('B'));
      this.bind(emacs, "delete-char-or-list", KeyMap.ctrl('D'));
      this.bind(emacs, "end-of-line", KeyMap.ctrl('E'));
      this.bind(emacs, "forward-char", KeyMap.ctrl('F'));
      this.bind(emacs, "abort", KeyMap.ctrl('G'));
      this.bind(emacs, "backward-delete-char", KeyMap.ctrl('H'));
      this.bind(emacs, "expand-or-complete", KeyMap.ctrl('I'));
      this.bind(emacs, "accept-line", KeyMap.ctrl('J'));
      this.bind(emacs, "kill-line", KeyMap.ctrl('K'));
      this.bind(emacs, "clear-screen", KeyMap.ctrl('L'));
      this.bind(emacs, "accept-line", KeyMap.ctrl('M'));
      this.bind(emacs, "down-line-or-history", KeyMap.ctrl('N'));
      this.bind(emacs, "accept-line-and-down-history", KeyMap.ctrl('O'));
      this.bind(emacs, "up-line-or-history", KeyMap.ctrl('P'));
      this.bind(emacs, "history-incremental-search-backward", KeyMap.ctrl('R'));
      this.bind(emacs, "history-incremental-search-forward", KeyMap.ctrl('S'));
      this.bind(emacs, "transpose-chars", KeyMap.ctrl('T'));
      this.bind(emacs, "kill-whole-line", KeyMap.ctrl('U'));
      this.bind(emacs, "quoted-insert", KeyMap.ctrl('V'));
      this.bind(emacs, "backward-kill-word", KeyMap.ctrl('W'));
      this.bind(emacs, "yank", KeyMap.ctrl('Y'));
      this.bind(emacs, "character-search", KeyMap.ctrl(']'));
      this.bind(emacs, "undo", KeyMap.ctrl('_'));
      this.bind(emacs, "self-insert", KeyMap.range(" -~"));
      this.bind(emacs, "insert-close-paren", ")");
      this.bind(emacs, "insert-close-square", "]");
      this.bind(emacs, "insert-close-curly", "}");
      this.bind(emacs, "backward-delete-char", KeyMap.del());
      this.bind(emacs, "vi-match-bracket", KeyMap.translate("^X^B"));
      this.bind(emacs, "abort", KeyMap.translate("^X^G"));
      this.bind(emacs, "edit-and-execute-command", KeyMap.translate("^X^E"));
      this.bind(emacs, "vi-find-next-char", KeyMap.translate("^X^F"));
      this.bind(emacs, "vi-join", KeyMap.translate("^X^J"));
      this.bind(emacs, "kill-buffer", KeyMap.translate("^X^K"));
      this.bind(emacs, "infer-next-history", KeyMap.translate("^X^N"));
      this.bind(emacs, "overwrite-mode", KeyMap.translate("^X^O"));
      this.bind(emacs, "redo", KeyMap.translate("^X^R"));
      this.bind(emacs, "undo", KeyMap.translate("^X^U"));
      this.bind(emacs, "vi-cmd-mode", KeyMap.translate("^X^V"));
      this.bind(emacs, "exchange-point-and-mark", KeyMap.translate("^X^X"));
      this.bind(emacs, "do-lowercase-version", KeyMap.translate("^XA-^XZ"));
      this.bind(emacs, "what-cursor-position", KeyMap.translate("^X="));
      this.bind(emacs, "kill-line", KeyMap.translate("^X^?"));
      this.bind(emacs, "abort", KeyMap.alt(KeyMap.ctrl('G')));
      this.bind(emacs, "backward-kill-word", KeyMap.alt(KeyMap.ctrl('H')));
      this.bind(emacs, "self-insert-unmeta", KeyMap.alt(KeyMap.ctrl('M')));
      this.bind(emacs, "complete-word", KeyMap.alt(KeyMap.esc()));
      this.bind(emacs, "character-search-backward", KeyMap.alt(KeyMap.ctrl(']')));
      this.bind(emacs, "copy-prev-word", KeyMap.alt(KeyMap.ctrl('_')));
      this.bind(emacs, "set-mark-command", KeyMap.alt(' '));
      this.bind(emacs, "neg-argument", KeyMap.alt('-'));
      this.bind(emacs, "digit-argument", KeyMap.range("\\E0-\\E9"));
      this.bind(emacs, "beginning-of-history", KeyMap.alt('<'));
      this.bind(emacs, "list-choices", KeyMap.alt('='));
      this.bind(emacs, "end-of-history", KeyMap.alt('>'));
      this.bind(emacs, "list-choices", KeyMap.alt('?'));
      this.bind(emacs, "do-lowercase-version", KeyMap.range("^[A-^[Z"));
      this.bind(emacs, "accept-and-hold", KeyMap.alt('a'));
      this.bind(emacs, "backward-word", KeyMap.alt('b'));
      this.bind(emacs, "capitalize-word", KeyMap.alt('c'));
      this.bind(emacs, "kill-word", KeyMap.alt('d'));
      this.bind(emacs, "kill-word", KeyMap.translate("^[[3;5~"));
      this.bind(emacs, "forward-word", KeyMap.alt('f'));
      this.bind(emacs, "down-case-word", KeyMap.alt('l'));
      this.bind(emacs, "history-search-forward", KeyMap.alt('n'));
      this.bind(emacs, "history-search-backward", KeyMap.alt('p'));
      this.bind(emacs, "transpose-words", KeyMap.alt('t'));
      this.bind(emacs, "up-case-word", KeyMap.alt('u'));
      this.bind(emacs, "yank-pop", KeyMap.alt('y'));
      this.bind(emacs, "backward-kill-word", KeyMap.alt(KeyMap.del()));
      this.bindArrowKeys(emacs);
      this.bind(emacs, "forward-word", KeyMap.translate("^[[1;5C"));
      this.bind(emacs, "backward-word", KeyMap.translate("^[[1;5D"));
      this.bind(emacs, "forward-word", KeyMap.alt(this.key(InfoCmp.Capability.key_right)));
      this.bind(emacs, "backward-word", KeyMap.alt(this.key(InfoCmp.Capability.key_left)));
      this.bind(emacs, "forward-word", KeyMap.alt(KeyMap.translate("^[[C")));
      this.bind(emacs, "backward-word", KeyMap.alt(KeyMap.translate("^[[D")));
      return emacs;
   }

   public KeyMap<Binding> viInsertion() {
      KeyMap<Binding> viins = new KeyMap<>();
      this.bindKeys(viins);
      this.bind(viins, "self-insert", KeyMap.range("^@-^_"));
      this.bind(viins, "list-choices", KeyMap.ctrl('D'));
      this.bind(viins, "abort", KeyMap.ctrl('G'));
      this.bind(viins, "backward-delete-char", KeyMap.ctrl('H'));
      this.bind(viins, "expand-or-complete", KeyMap.ctrl('I'));
      this.bind(viins, "accept-line", KeyMap.ctrl('J'));
      this.bind(viins, "clear-screen", KeyMap.ctrl('L'));
      this.bind(viins, "accept-line", KeyMap.ctrl('M'));
      this.bind(viins, "menu-complete", KeyMap.ctrl('N'));
      this.bind(viins, "reverse-menu-complete", KeyMap.ctrl('P'));
      this.bind(viins, "history-incremental-search-backward", KeyMap.ctrl('R'));
      this.bind(viins, "history-incremental-search-forward", KeyMap.ctrl('S'));
      this.bind(viins, "transpose-chars", KeyMap.ctrl('T'));
      this.bind(viins, "kill-whole-line", KeyMap.ctrl('U'));
      this.bind(viins, "quoted-insert", KeyMap.ctrl('V'));
      this.bind(viins, "backward-kill-word", KeyMap.ctrl('W'));
      this.bind(viins, "yank", KeyMap.ctrl('Y'));
      this.bind(viins, "vi-cmd-mode", KeyMap.ctrl('['));
      this.bind(viins, "undo", KeyMap.ctrl('_'));
      this.bind(viins, "history-incremental-search-backward", KeyMap.ctrl('X') + "r");
      this.bind(viins, "history-incremental-search-forward", KeyMap.ctrl('X') + "s");
      this.bind(viins, "self-insert", KeyMap.range(" -~"));
      this.bind(viins, "insert-close-paren", ")");
      this.bind(viins, "insert-close-square", "]");
      this.bind(viins, "insert-close-curly", "}");
      this.bind(viins, "backward-delete-char", KeyMap.del());
      this.bindArrowKeys(viins);
      return viins;
   }

   public KeyMap<Binding> viCmd() {
      KeyMap<Binding> vicmd = new KeyMap<>();
      this.bind(vicmd, "list-choices", KeyMap.ctrl('D'));
      this.bind(vicmd, "emacs-editing-mode", KeyMap.ctrl('E'));
      this.bind(vicmd, "abort", KeyMap.ctrl('G'));
      this.bind(vicmd, "vi-backward-char", KeyMap.ctrl('H'));
      this.bind(vicmd, "accept-line", KeyMap.ctrl('J'));
      this.bind(vicmd, "kill-line", KeyMap.ctrl('K'));
      this.bind(vicmd, "clear-screen", KeyMap.ctrl('L'));
      this.bind(vicmd, "accept-line", KeyMap.ctrl('M'));
      this.bind(vicmd, "vi-down-line-or-history", KeyMap.ctrl('N'));
      this.bind(vicmd, "vi-up-line-or-history", KeyMap.ctrl('P'));
      this.bind(vicmd, "quoted-insert", KeyMap.ctrl('Q'));
      this.bind(vicmd, "history-incremental-search-backward", KeyMap.ctrl('R'));
      this.bind(vicmd, "history-incremental-search-forward", KeyMap.ctrl('S'));
      this.bind(vicmd, "transpose-chars", KeyMap.ctrl('T'));
      this.bind(vicmd, "kill-whole-line", KeyMap.ctrl('U'));
      this.bind(vicmd, "quoted-insert", KeyMap.ctrl('V'));
      this.bind(vicmd, "backward-kill-word", KeyMap.ctrl('W'));
      this.bind(vicmd, "yank", KeyMap.ctrl('Y'));
      this.bind(vicmd, "history-incremental-search-backward", KeyMap.ctrl('X') + "r");
      this.bind(vicmd, "history-incremental-search-forward", KeyMap.ctrl('X') + "s");
      this.bind(vicmd, "abort", KeyMap.alt(KeyMap.ctrl('G')));
      this.bind(vicmd, "backward-kill-word", KeyMap.alt(KeyMap.ctrl('H')));
      this.bind(vicmd, "self-insert-unmeta", KeyMap.alt(KeyMap.ctrl('M')));
      this.bind(vicmd, "complete-word", KeyMap.alt(KeyMap.esc()));
      this.bind(vicmd, "character-search-backward", KeyMap.alt(KeyMap.ctrl(']')));
      this.bind(vicmd, "set-mark-command", KeyMap.alt(' '));
      this.bind(vicmd, "digit-argument", KeyMap.alt('-'));
      this.bind(vicmd, "beginning-of-history", KeyMap.alt('<'));
      this.bind(vicmd, "list-choices", KeyMap.alt('='));
      this.bind(vicmd, "end-of-history", KeyMap.alt('>'));
      this.bind(vicmd, "list-choices", KeyMap.alt('?'));
      this.bind(vicmd, "do-lowercase-version", KeyMap.range("^[A-^[Z"));
      this.bind(vicmd, "backward-word", KeyMap.alt('b'));
      this.bind(vicmd, "capitalize-word", KeyMap.alt('c'));
      this.bind(vicmd, "kill-word", KeyMap.alt('d'));
      this.bind(vicmd, "forward-word", KeyMap.alt('f'));
      this.bind(vicmd, "down-case-word", KeyMap.alt('l'));
      this.bind(vicmd, "history-search-forward", KeyMap.alt('n'));
      this.bind(vicmd, "history-search-backward", KeyMap.alt('p'));
      this.bind(vicmd, "transpose-words", KeyMap.alt('t'));
      this.bind(vicmd, "up-case-word", KeyMap.alt('u'));
      this.bind(vicmd, "yank-pop", KeyMap.alt('y'));
      this.bind(vicmd, "backward-kill-word", KeyMap.alt(KeyMap.del()));
      this.bind(vicmd, "forward-char", " ");
      this.bind(vicmd, "vi-insert-comment", "#");
      this.bind(vicmd, "end-of-line", "$");
      this.bind(vicmd, "vi-match-bracket", "%");
      this.bind(vicmd, "vi-down-line-or-history", "+");
      this.bind(vicmd, "vi-rev-repeat-find", ",");
      this.bind(vicmd, "vi-up-line-or-history", "-");
      this.bind(vicmd, "vi-repeat-change", ".");
      this.bind(vicmd, "vi-history-search-backward", "/");
      this.bind(vicmd, "vi-digit-or-beginning-of-line", "0");
      this.bind(vicmd, "digit-argument", KeyMap.range("1-9"));
      this.bind(vicmd, "vi-repeat-find", ";");
      this.bind(vicmd, "list-choices", "=");
      this.bind(vicmd, "vi-history-search-forward", "?");
      this.bind(vicmd, "vi-add-eol", "A");
      this.bind(vicmd, "vi-backward-blank-word", "B");
      this.bind(vicmd, "vi-change-eol", "C");
      this.bind(vicmd, "vi-kill-eol", "D");
      this.bind(vicmd, "vi-forward-blank-word-end", "E");
      this.bind(vicmd, "vi-find-prev-char", "F");
      this.bind(vicmd, "vi-fetch-history", "G");
      this.bind(vicmd, "vi-insert-bol", "I");
      this.bind(vicmd, "vi-join", "J");
      this.bind(vicmd, "vi-rev-repeat-search", "N");
      this.bind(vicmd, "vi-open-line-above", "O");
      this.bind(vicmd, "vi-put-before", "P");
      this.bind(vicmd, "vi-replace", "R");
      this.bind(vicmd, "vi-kill-line", "S");
      this.bind(vicmd, "vi-find-prev-char-skip", "T");
      this.bind(vicmd, "redo", "U");
      this.bind(vicmd, "visual-line-mode", "V");
      this.bind(vicmd, "vi-forward-blank-word", "W");
      this.bind(vicmd, "vi-backward-delete-char", "X");
      this.bind(vicmd, "vi-yank-whole-line", "Y");
      this.bind(vicmd, "vi-first-non-blank", "^");
      this.bind(vicmd, "vi-add-next", "a");
      this.bind(vicmd, "vi-backward-word", "b");
      this.bind(vicmd, "vi-change-to", "c");
      this.bind(vicmd, "vi-delete", "d");
      this.bind(vicmd, "vi-forward-word-end", "e");
      this.bind(vicmd, "vi-find-next-char", "f");
      this.bind(vicmd, "what-cursor-position", "ga");
      this.bind(vicmd, "vi-backward-blank-word-end", "gE");
      this.bind(vicmd, "vi-backward-word-end", "ge");
      this.bind(vicmd, "vi-backward-char", "h");
      this.bind(vicmd, "vi-insert", "i");
      this.bind(vicmd, "down-line-or-history", "j");
      this.bind(vicmd, "up-line-or-history", "k");
      this.bind(vicmd, "vi-forward-char", "l");
      this.bind(vicmd, "vi-repeat-search", "n");
      this.bind(vicmd, "vi-open-line-below", "o");
      this.bind(vicmd, "vi-put-after", "p");
      this.bind(vicmd, "vi-replace-chars", "r");
      this.bind(vicmd, "vi-substitute", "s");
      this.bind(vicmd, "vi-find-next-char-skip", "t");
      this.bind(vicmd, "undo", "u");
      this.bind(vicmd, "visual-mode", "v");
      this.bind(vicmd, "vi-forward-word", "w");
      this.bind(vicmd, "vi-delete-char", "x");
      this.bind(vicmd, "vi-yank", "y");
      this.bind(vicmd, "vi-goto-column", "|");
      this.bind(vicmd, "vi-swap-case", "~");
      this.bind(vicmd, "vi-backward-char", KeyMap.del());
      this.bindArrowKeys(vicmd);
      return vicmd;
   }

   public KeyMap<Binding> menu() {
      KeyMap<Binding> menu = new KeyMap<>();
      this.bind(menu, "menu-complete", "\t");
      this.bind(menu, "reverse-menu-complete", this.key(InfoCmp.Capability.back_tab));
      this.bind(menu, "accept-line", "\r", "\n");
      this.bindArrowKeys(menu);
      return menu;
   }

   public KeyMap<Binding> safe() {
      KeyMap<Binding> safe = new KeyMap<>();
      this.bind(safe, "self-insert", KeyMap.range("^@-^?"));
      this.bind(safe, "accept-line", "\r", "\n");
      this.bind(safe, "abort", KeyMap.ctrl('G'));
      return safe;
   }

   public KeyMap<Binding> dumb() {
      KeyMap<Binding> dumb = new KeyMap<>();
      this.bind(dumb, "self-insert", KeyMap.range("^@-^?"));
      this.bind(dumb, "accept-line", "\r", "\n");
      this.bind(dumb, "beep", KeyMap.ctrl('G'));
      return dumb;
   }

   public KeyMap<Binding> visual() {
      KeyMap<Binding> visual = new KeyMap<>();
      this.bind(visual, "up-line", this.key(InfoCmp.Capability.key_up), "k");
      this.bind(visual, "down-line", this.key(InfoCmp.Capability.key_down), "j");
      this.bind(visual, this::deactivateRegion, KeyMap.esc());
      this.bind(visual, "exchange-point-and-mark", "o");
      this.bind(visual, "put-replace-selection", "p");
      this.bind(visual, "vi-delete", "x");
      this.bind(visual, "vi-oper-swap-case", "~");
      return visual;
   }

   public KeyMap<Binding> viOpp() {
      KeyMap<Binding> viOpp = new KeyMap<>();
      this.bind(viOpp, "up-line", this.key(InfoCmp.Capability.key_up), "k");
      this.bind(viOpp, "down-line", this.key(InfoCmp.Capability.key_down), "j");
      this.bind(viOpp, "vi-cmd-mode", KeyMap.esc());
      return viOpp;
   }

   private void bind(KeyMap<Binding> map, String widget, Iterable<? extends CharSequence> keySeqs) {
      map.bind(new Reference(widget), keySeqs);
   }

   private void bind(KeyMap<Binding> map, String widget, CharSequence... keySeqs) {
      map.bind(new Reference(widget), keySeqs);
   }

   private void bind(KeyMap<Binding> map, Widget widget, CharSequence... keySeqs) {
      map.bind(widget, keySeqs);
   }

   private String key(InfoCmp.Capability capability) {
      return KeyMap.key(this.terminal, capability);
   }

   private void bindKeys(KeyMap<Binding> emacs) {
      Widget beep = this.namedWidget("beep", this::beep);
      Stream.of(InfoCmp.Capability.values()).filter(c -> c.name().startsWith("key_")).map(this::key).forEach(k -> this.bind(emacs, beep, k));
   }

   private void bindArrowKeys(KeyMap<Binding> map) {
      this.bind(map, "up-line-or-search", this.key(InfoCmp.Capability.key_up));
      this.bind(map, "down-line-or-search", this.key(InfoCmp.Capability.key_down));
      this.bind(map, "backward-char", this.key(InfoCmp.Capability.key_left));
      this.bind(map, "forward-char", this.key(InfoCmp.Capability.key_right));
      this.bind(map, "beginning-of-line", this.key(InfoCmp.Capability.key_home));
      this.bind(map, "end-of-line", this.key(InfoCmp.Capability.key_end));
      this.bind(map, "delete-char", this.key(InfoCmp.Capability.key_dc));
      this.bind(map, "kill-whole-line", this.key(InfoCmp.Capability.key_dl));
      this.bind(map, "overwrite-mode", this.key(InfoCmp.Capability.key_ic));
      this.bind(map, "mouse", MouseSupport.keys(this.terminal));
      this.bind(map, "begin-paste", "\u001b[200~");
      this.bind(map, "terminal-focus-in", "\u001b[I");
      this.bind(map, "terminal-focus-out", "\u001b[O");
   }

   private void bindConsoleChars(KeyMap<Binding> keyMap, Attributes attr) {
      if (attr != null) {
         this.rebind(keyMap, "backward-delete-char", KeyMap.del(), (char)attr.getControlChar(Attributes.ControlChar.VERASE));
         this.rebind(keyMap, "backward-kill-word", KeyMap.ctrl('W'), (char)attr.getControlChar(Attributes.ControlChar.VWERASE));
         this.rebind(keyMap, "kill-whole-line", KeyMap.ctrl('U'), (char)attr.getControlChar(Attributes.ControlChar.VKILL));
         this.rebind(keyMap, "quoted-insert", KeyMap.ctrl('V'), (char)attr.getControlChar(Attributes.ControlChar.VLNEXT));
      }
   }

   private void rebind(KeyMap<Binding> keyMap, String operation, String prevBinding, char newBinding) {
      if (newBinding > 0 && newBinding < 128) {
         Reference ref = new Reference(operation);
         this.bind(keyMap, "self-insert", prevBinding);
         keyMap.bind(ref, Character.toString(newBinding));
      }
   }

   @Override
   public void zeroOut() {
      this.buf.zeroOut();
      this.parsedLine = null;
   }

   protected static enum BellType {
      NONE,
      AUDIBLE,
      VISIBLE;
   }

   private static class CompletingWord implements CompletingParsedLine {
      private final String word;

      public CompletingWord(String word) {
         this.word = word;
      }

      @Override
      public CharSequence escape(CharSequence candidate, boolean complete) {
         return null;
      }

      @Override
      public int rawWordCursor() {
         return this.word.length();
      }

      @Override
      public int rawWordLength() {
         return this.word.length();
      }

      @Override
      public String word() {
         return this.word;
      }

      @Override
      public int wordCursor() {
         return this.word.length();
      }

      @Override
      public int wordIndex() {
         return 0;
      }

      @Override
      public List<String> words() {
         return null;
      }

      @Override
      public String line() {
         return this.word;
      }

      @Override
      public int cursor() {
         return this.word.length();
      }
   }

   protected static enum CompletionType {
      Expand,
      ExpandComplete,
      Complete,
      List;
   }

   private class MenuSupport implements Supplier<AttributedString> {
      final List<Candidate> possible = new ArrayList<>();
      final BiFunction<CharSequence, Boolean, CharSequence> escaper;
      int selection;
      int topLine;
      String word;
      AttributedString computed;
      int lines;
      int columns;
      String completed;

      public MenuSupport(List<Candidate> original, String completed, BiFunction<CharSequence, Boolean, CharSequence> escaper) {
         this.escaper = escaper;
         this.selection = -1;
         this.topLine = 0;
         this.word = "";
         this.completed = completed;
         LineReaderImpl.this.computePost(original, null, this.possible, completed);
         this.next();
      }

      public Candidate completion() {
         return this.possible.get(this.selection);
      }

      public void next() {
         this.selection = (this.selection + 1) % this.possible.size();
         this.update();
      }

      public void previous() {
         this.selection = (this.selection + this.possible.size() - 1) % this.possible.size();
         this.update();
      }

      private void major(int step) {
         int axis = LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST) ? this.columns : this.lines;
         int sel = this.selection + step * axis;
         if (sel < 0) {
            int pos = (sel + axis) % axis;
            int remainders = this.possible.size() % axis;
            sel = this.possible.size() - remainders + pos;
            if (sel >= this.possible.size()) {
               sel -= axis;
            }
         } else if (sel >= this.possible.size()) {
            sel %= axis;
         }

         this.selection = sel;
         this.update();
      }

      private void minor(int step) {
         int axis = LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST) ? this.columns : this.lines;
         int row = this.selection % axis;
         int options = this.possible.size();
         if (this.selection - row + axis > options) {
            axis = options % axis;
         }

         this.selection = this.selection - row + (axis + row + step) % axis;
         this.update();
      }

      public void up() {
         if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
            this.major(-1);
         } else {
            this.minor(-1);
         }
      }

      public void down() {
         if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
            this.major(1);
         } else {
            this.minor(1);
         }
      }

      public void left() {
         if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
            this.minor(-1);
         } else {
            this.major(-1);
         }
      }

      public void right() {
         if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
            this.minor(1);
         } else {
            this.major(1);
         }
      }

      private void update() {
         LineReaderImpl.this.buf.backspace(this.word.length());
         this.word = this.escaper.apply(this.completion().value(), true).toString();
         LineReaderImpl.this.buf.write(this.word);
         LineReaderImpl.PostResult pr = LineReaderImpl.this.computePost(this.possible, this.completion(), null, this.completed);
         int displaySize = LineReaderImpl.this.displayRows() - LineReaderImpl.this.promptLines();
         if (pr.lines > displaySize) {
            int displayed = displaySize - 1;
            if (pr.selectedLine >= 0) {
               if (pr.selectedLine < this.topLine) {
                  this.topLine = pr.selectedLine;
               } else if (pr.selectedLine >= this.topLine + displayed) {
                  this.topLine = pr.selectedLine - displayed + 1;
               }
            }

            AttributedString post = pr.post;
            if (post.length() > 0 && post.charAt(post.length() - 1) != '\n') {
               post = new AttributedStringBuilder(post.length() + 1).append(post).append("\n").toAttributedString();
            }

            List<AttributedString> lines = post.columnSplitLength(LineReaderImpl.this.size.getColumns(), true, LineReaderImpl.this.display.delayLineWrap());
            List<AttributedString> sub = new ArrayList<>(lines.subList(this.topLine, this.topLine + displayed));
            sub.add(
               new AttributedStringBuilder()
                  .style(AttributedStyle.DEFAULT.foreground(6))
                  .append("rows ")
                  .append(Integer.toString(this.topLine + 1))
                  .append(" to ")
                  .append(Integer.toString(this.topLine + displayed))
                  .append(" of ")
                  .append(Integer.toString(lines.size()))
                  .append("\n")
                  .style(AttributedStyle.DEFAULT)
                  .toAttributedString()
            );
            this.computed = AttributedString.join(AttributedString.EMPTY, sub);
         } else {
            this.computed = pr.post;
         }

         this.lines = pr.lines;
         this.columns = (this.possible.size() + this.lines - 1) / this.lines;
      }

      public AttributedString get() {
         return this.computed;
      }
   }

   static class Pair<U, V> {
      final U u;
      final V v;

      public Pair(U u, V v) {
         this.u = u;
         this.v = v;
      }

      public U getU() {
         return this.u;
      }

      public V getV() {
         return this.v;
      }
   }

   protected static class PostResult {
      final AttributedString post;
      final int lines;
      final int selectedLine;

      public PostResult(AttributedString post, int lines, int selectedLine) {
         this.post = post;
         this.lines = lines;
         this.selectedLine = selectedLine;
      }
   }

   protected static enum State {
      NORMAL,
      DONE,
      IGNORE,
      EOF,
      INTERRUPT;
   }

   private static class TerminalLine {
      private String endLine;
      private int startPos;

      public TerminalLine(String line, int startPos, int width) {
         this.startPos = startPos;
         this.endLine = line.substring(line.lastIndexOf(10) + 1);

         boolean first;
         for (first = true; this.endLine.length() + (first ? startPos : 0) > width && width > 0; first = false) {
            if (first) {
               this.endLine = this.endLine.substring(width - startPos);
            } else {
               this.endLine = this.endLine.substring(width);
            }
         }

         if (!first) {
            this.startPos = 0;
         }
      }

      public int getStartPos() {
         return this.startPos;
      }

      public String getEndLine() {
         return this.endLine;
      }
   }

   protected static enum ViMoveMode {
      NORMAL,
      YANK,
      DELETE,
      CHANGE;
   }
}
