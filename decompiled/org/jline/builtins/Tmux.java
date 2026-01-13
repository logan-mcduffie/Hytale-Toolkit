package org.jline.builtins;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.text.DateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Attributes;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.LineDisciplineTerminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.Colors;
import org.jline.utils.Display;
import org.jline.utils.InfoCmp;
import org.jline.utils.Log;

public class Tmux {
   public static final String OPT_PREFIX = "prefix";
   public static final String CMD_COMMANDS = "commands";
   public static final String CMD_SEND_PREFIX = "send-prefix";
   public static final String CMD_SPLIT_WINDOW = "split-window";
   public static final String CMD_SPLITW = "splitw";
   public static final String CMD_SELECT_PANE = "select-pane";
   public static final String CMD_SELECTP = "selectp";
   public static final String CMD_RESIZE_PANE = "resize-pane";
   public static final String CMD_RESIZEP = "resizep";
   public static final String CMD_DISPLAY_PANES = "display-panes";
   public static final String CMD_DISPLAYP = "displayp";
   public static final String CMD_CLOCK_MODE = "clock-mode";
   public static final String CMD_SET_OPTION = "set-option";
   public static final String CMD_SET = "set";
   public static final String CMD_LIST_KEYS = "list-keys";
   public static final String CMD_LSK = "lsk";
   public static final String CMD_SEND_KEYS = "send-keys";
   public static final String CMD_SEND = "send";
   public static final String CMD_BIND_KEY = "bind-key";
   public static final String CMD_BIND = "bind";
   public static final String CMD_UNBIND_KEY = "unbind-key";
   public static final String CMD_UNBIND = "unbind";
   public static final String CMD_NEW_WINDOW = "new-window";
   public static final String CMD_NEWW = "neww";
   public static final String CMD_NEXT_WINDOW = "next-window";
   public static final String CMD_NEXT = "next";
   public static final String CMD_PREVIOUS_WINDOW = "previous-window";
   public static final String CMD_PREV = "prev";
   public static final String CMD_LIST_WINDOWS = "list-windows";
   public static final String CMD_LSW = "lsw";
   private static final int[][][] WINDOW_CLOCK_TABLE = new int[][][]{
      {{1, 1, 1, 1, 1}, {1, 0, 0, 0, 1}, {1, 0, 0, 0, 1}, {1, 0, 0, 0, 1}, {1, 1, 1, 1, 1}},
      {{0, 0, 0, 0, 1}, {0, 0, 0, 0, 1}, {0, 0, 0, 0, 1}, {0, 0, 0, 0, 1}, {0, 0, 0, 0, 1}},
      {{1, 1, 1, 1, 1}, {0, 0, 0, 0, 1}, {1, 1, 1, 1, 1}, {1, 0, 0, 0, 0}, {1, 1, 1, 1, 1}},
      {{1, 1, 1, 1, 1}, {0, 0, 0, 0, 1}, {1, 1, 1, 1, 1}, {0, 0, 0, 0, 1}, {1, 1, 1, 1, 1}},
      {{1, 0, 0, 0, 1}, {1, 0, 0, 0, 1}, {1, 1, 1, 1, 1}, {0, 0, 0, 0, 1}, {0, 0, 0, 0, 1}},
      {{1, 1, 1, 1, 1}, {1, 0, 0, 0, 0}, {1, 1, 1, 1, 1}, {0, 0, 0, 0, 1}, {1, 1, 1, 1, 1}},
      {{1, 1, 1, 1, 1}, {1, 0, 0, 0, 0}, {1, 1, 1, 1, 1}, {1, 0, 0, 0, 1}, {1, 1, 1, 1, 1}},
      {{1, 1, 1, 1, 1}, {0, 0, 0, 0, 1}, {0, 0, 0, 0, 1}, {0, 0, 0, 0, 1}, {0, 0, 0, 0, 1}},
      {{1, 1, 1, 1, 1}, {1, 0, 0, 0, 1}, {1, 1, 1, 1, 1}, {1, 0, 0, 0, 1}, {1, 1, 1, 1, 1}},
      {{1, 1, 1, 1, 1}, {1, 0, 0, 0, 1}, {1, 1, 1, 1, 1}, {0, 0, 0, 0, 1}, {1, 1, 1, 1, 1}},
      {{0, 0, 0, 0, 0}, {0, 0, 1, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 1, 0, 0}, {0, 0, 0, 0, 0}},
      {{1, 1, 1, 1, 1}, {1, 0, 0, 0, 1}, {1, 1, 1, 1, 1}, {1, 0, 0, 0, 1}, {1, 0, 0, 0, 1}},
      {{1, 1, 1, 1, 1}, {1, 0, 0, 0, 1}, {1, 1, 1, 1, 1}, {1, 0, 0, 0, 0}, {1, 0, 0, 0, 0}},
      {{1, 0, 0, 0, 1}, {1, 1, 0, 1, 1}, {1, 0, 1, 0, 1}, {1, 0, 0, 0, 1}, {1, 0, 0, 0, 1}}
   };
   private final AtomicBoolean dirty = new AtomicBoolean(true);
   private final AtomicBoolean resized = new AtomicBoolean(true);
   private final Terminal terminal;
   private final Display display;
   private final PrintStream err;
   private final String term;
   private final Consumer<Terminal> runner;
   private List<Tmux.Window> windows = new ArrayList<>();
   private Integer windowsId = 0;
   private int activeWindow = 0;
   private final AtomicBoolean running = new AtomicBoolean(true);
   private final Size size = new Size();
   private boolean identify;
   private ScheduledExecutorService executor;
   private ScheduledFuture<?> clockFuture;
   private final Map<String, String> serverOptions = new HashMap<>();
   private KeyMap<Object> keyMap;
   int ACTIVE_COLOR = 3908;
   int INACTIVE_COLOR = 1103;
   int CLOCK_COLOR = 1103;

   public Tmux(Terminal terminal, PrintStream err, Consumer<Terminal> runner) throws IOException {
      this.terminal = terminal;
      this.err = err;
      this.runner = runner;
      this.display = new Display(terminal, true);
      Integer colors = terminal.getNumericCapability(InfoCmp.Capability.max_colors);
      this.term = colors != null && colors >= 256 ? "screen-256color" : "screen";
      this.serverOptions.put("prefix", "`");
      this.keyMap = this.createKeyMap(this.serverOptions.get("prefix"));
   }

   protected KeyMap<Object> createKeyMap(String prefix) {
      KeyMap<Object> keyMap = this.createEmptyKeyMap(prefix);
      keyMap.bind("send-prefix", prefix + prefix);
      keyMap.bind("split-window -v", prefix + "\"");
      keyMap.bind("split-window -h", prefix + "%");
      keyMap.bind("select-pane -U", prefix + KeyMap.key(this.terminal, InfoCmp.Capability.key_up));
      keyMap.bind("select-pane -D", prefix + KeyMap.key(this.terminal, InfoCmp.Capability.key_down));
      keyMap.bind("select-pane -L", prefix + KeyMap.key(this.terminal, InfoCmp.Capability.key_left));
      keyMap.bind("select-pane -R", prefix + KeyMap.key(this.terminal, InfoCmp.Capability.key_right));
      keyMap.bind("resize-pane -U 5", prefix + KeyMap.esc() + KeyMap.key(this.terminal, InfoCmp.Capability.key_up));
      keyMap.bind("resize-pane -D 5", prefix + KeyMap.esc() + KeyMap.key(this.terminal, InfoCmp.Capability.key_down));
      keyMap.bind("resize-pane -L 5", prefix + KeyMap.esc() + KeyMap.key(this.terminal, InfoCmp.Capability.key_left));
      keyMap.bind("resize-pane -R 5", prefix + KeyMap.esc() + KeyMap.key(this.terminal, InfoCmp.Capability.key_right));
      keyMap.bind("resize-pane -U", prefix + KeyMap.translate("^[[1;5A"), prefix + KeyMap.alt(KeyMap.translate("^[[A")));
      keyMap.bind("resize-pane -D", prefix + KeyMap.translate("^[[1;5B"), prefix + KeyMap.alt(KeyMap.translate("^[[B")));
      keyMap.bind("resize-pane -L", prefix + KeyMap.translate("^[[1;5C"), prefix + KeyMap.alt(KeyMap.translate("^[[C")));
      keyMap.bind("resize-pane -R", prefix + KeyMap.translate("^[[1;5D"), prefix + KeyMap.alt(KeyMap.translate("^[[D")));
      keyMap.bind("display-panes", prefix + "q");
      keyMap.bind("clock-mode", prefix + "t");
      keyMap.bind("new-window", prefix + "c");
      keyMap.bind("next-window", prefix + "n");
      keyMap.bind("previous-window", prefix + "p");
      return keyMap;
   }

   protected KeyMap<Object> createEmptyKeyMap(String prefix) {
      KeyMap<Object> keyMap = new KeyMap<>();
      keyMap.setUnicode(Tmux.Binding.SelfInsert);
      keyMap.setNomatch(Tmux.Binding.SelfInsert);

      for (int i = 0; i < 255; i++) {
         keyMap.bind(Tmux.Binding.Discard, prefix + (char)i);
      }

      keyMap.bind(Tmux.Binding.Mouse, KeyMap.key(this.terminal, InfoCmp.Capability.key_mouse));
      return keyMap;
   }

   public void run() throws IOException {
      Terminal.SignalHandler prevWinchHandler = this.terminal.handle(Terminal.Signal.WINCH, this::resize);
      Terminal.SignalHandler prevIntHandler = this.terminal.handle(Terminal.Signal.INT, this::interrupt);
      Terminal.SignalHandler prevSuspHandler = this.terminal.handle(Terminal.Signal.TSTP, this::suspend);
      Attributes attributes = this.terminal.enterRawMode();
      this.terminal.puts(InfoCmp.Capability.enter_ca_mode);
      this.terminal.puts(InfoCmp.Capability.keypad_xmit);
      this.terminal.trackMouse(Terminal.MouseTracking.Any);
      this.terminal.flush();
      this.executor = Executors.newSingleThreadScheduledExecutor();

      try {
         this.size.copy(this.terminal.getSize());
         this.windows.add(new Tmux.Window(this));
         this.activeWindow = 0;
         this.runner.accept(this.active().getConsole());
         new Thread(this::inputLoop, "Mux input loop").start();
         this.redrawLoop();
      } catch (RuntimeException var9) {
         throw var9;
      } finally {
         this.executor.shutdown();
         this.terminal.trackMouse(Terminal.MouseTracking.Off);
         this.terminal.puts(InfoCmp.Capability.keypad_local);
         this.terminal.puts(InfoCmp.Capability.exit_ca_mode);
         this.terminal.flush();
         this.terminal.setAttributes(attributes);
         this.terminal.handle(Terminal.Signal.WINCH, prevWinchHandler);
         this.terminal.handle(Terminal.Signal.INT, prevIntHandler);
         this.terminal.handle(Terminal.Signal.TSTP, prevSuspHandler);
      }
   }

   private Tmux.VirtualConsole active() {
      return this.windows.get(this.activeWindow).getActive();
   }

   private List<Tmux.VirtualConsole> panes() {
      return this.windows.get(this.activeWindow).getPanes();
   }

   private Tmux.Window window() {
      return this.windows.get(this.activeWindow);
   }

   private void redrawLoop() {
      while (this.running.get()) {
         try {
            synchronized (this.dirty) {
               while (this.running.get() && !this.dirty.compareAndSet(true, false)) {
                  this.dirty.wait();
               }
            }
         } catch (InterruptedException var4) {
            var4.printStackTrace();
         }

         this.handleResize();
         this.redraw();
      }
   }

   private void setDirty() {
      synchronized (this.dirty) {
         this.dirty.set(true);
         this.dirty.notifyAll();
      }
   }

   private void inputLoop() {
      try {
         BindingReader reader = new BindingReader(this.terminal.reader());
         boolean first = true;

         while (this.running.get()) {
            Object b;
            if (first) {
               b = reader.readBinding(this.keyMap);
            } else if (reader.peekCharacter(100L) >= 0) {
               b = reader.readBinding(this.keyMap, null, false);
            } else {
               b = null;
            }

            if (b == Tmux.Binding.SelfInsert) {
               if (this.active().clock) {
                  this.active().clock = false;
                  if (this.clockFuture != null && this.panes().stream().noneMatch(vc -> vc.clock)) {
                     this.clockFuture.cancel(false);
                     this.clockFuture = null;
                  }

                  this.setDirty();
               } else {
                  this.active().getMasterInputOutput().write(reader.getLastBinding().getBytes());
                  first = false;
               }
            } else {
               if (first) {
                  first = false;
               } else {
                  this.active().getMasterInputOutput().flush();
                  first = true;
               }

               if (b == Tmux.Binding.Mouse) {
                  MouseEvent out = this.terminal.readMouseEvent(reader::readCharacter, reader.getLastBinding());
               } else if (b instanceof String || b instanceof String[]) {
                  ByteArrayOutputStream out = new ByteArrayOutputStream();
                  ByteArrayOutputStream err = new ByteArrayOutputStream();

                  try {
                     PrintStream pout = new PrintStream(out);

                     try {
                        PrintStream perr = new PrintStream(err);

                        try {
                           if (b instanceof String) {
                              this.execute(pout, perr, (String)b);
                           } else {
                              this.execute(pout, perr, Arrays.asList((String[])b));
                           }
                        } catch (Throwable var20) {
                           try {
                              perr.close();
                           } catch (Throwable var19) {
                              var20.addSuppressed(var19);
                           }

                           throw var20;
                        }

                        perr.close();
                     } catch (Throwable var21) {
                        try {
                           pout.close();
                        } catch (Throwable var18) {
                           var21.addSuppressed(var18);
                        }

                        throw var21;
                     }

                     pout.close();
                  } catch (Exception var22) {
                  }
               }
            }
         }
      } catch (IOException var23) {
         if (this.running.get()) {
            Log.info("Error in tmux input loop", var23);
         }
      } finally {
         this.running.set(false);
         this.setDirty();
      }
   }

   private synchronized void close(Tmux.VirtualConsole terminal) {
      int idx = -1;
      Tmux.Window window = null;

      for (Tmux.Window w : this.windows) {
         idx = w.getPanes().indexOf(terminal);
         if (idx >= 0) {
            window = w;
            break;
         }
      }

      if (idx >= 0) {
         window.remove(terminal);
         if (window.getPanes().isEmpty()) {
            if (this.windows.size() > 1) {
               this.windows.remove(window);
               if (this.activeWindow >= this.windows.size()) {
                  this.activeWindow--;
               }

               this.resize(Terminal.Signal.WINCH);
            } else {
               this.running.set(false);
               this.setDirty();
            }
         } else {
            this.resize(Terminal.Signal.WINCH);
         }
      }
   }

   private void resize(Terminal.Signal signal) {
      this.resized.set(true);
      this.setDirty();
   }

   private void interrupt(Terminal.Signal signal) {
      this.active().getConsole().raise(signal);
   }

   private void suspend(Terminal.Signal signal) {
      this.active().getConsole().raise(signal);
   }

   private void handleResize() {
      if (this.resized.compareAndSet(true, false)) {
         this.size.copy(this.terminal.getSize());
      }

      this.window().handleResize();
   }

   public void execute(PrintStream out, PrintStream err, String command) throws Exception {
      ParsedLine line = new DefaultParser().parse(command.trim(), 0);
      this.execute(out, err, line.words());
   }

   public synchronized void execute(PrintStream out, PrintStream err, List<String> command) throws Exception {
      String name = command.get(0);
      List<String> args = command.subList(1, command.size());
      switch (name) {
         case "send-prefix":
            this.sendPrefix(out, err, args);
            break;
         case "split-window":
         case "splitw":
            this.splitWindow(out, err, args);
            break;
         case "select-pane":
         case "selectp":
            this.selectPane(out, err, args);
            break;
         case "resize-pane":
         case "resizep":
            this.resizePane(out, err, args);
            break;
         case "display-panes":
         case "displayp":
            this.displayPanes(out, err, args);
            break;
         case "clock-mode":
            this.clockMode(out, err, args);
            break;
         case "bind-key":
         case "bind":
            this.bindKey(out, err, args);
            break;
         case "unbind-key":
         case "unbind":
            this.unbindKey(out, err, args);
            break;
         case "list-keys":
         case "lsk":
            this.listKeys(out, err, args);
            break;
         case "send-keys":
         case "send":
            this.sendKeys(out, err, args);
            break;
         case "set-option":
         case "set":
            this.setOption(out, err, args);
            break;
         case "new-window":
         case "neww":
            this.newWindow(out, err, args);
            break;
         case "next-window":
         case "next":
            this.nextWindow(out, err, args);
            break;
         case "previous-window":
         case "prev":
            this.previousWindow(out, err, args);
            break;
         case "list-windows":
         case "lsw":
            this.listWindows(out, err, args);
      }
   }

   protected void listWindows(PrintStream out, PrintStream err, List<String> args) throws Exception {
      String[] usage = new String[]{"list-windows - ", "Usage: list-windows", "  -? --help                    Show help"};
      Options opt = Options.compile(usage).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         IntStream.range(0, this.windows.size()).mapToObj(i -> {
            StringBuilder sb = new StringBuilder();
            sb.append(i);
            sb.append(": ");
            sb.append(this.windows.get(i).getName());
            sb.append(i == this.activeWindow ? "* " : " ");
            sb.append("(");
            sb.append(this.windows.get(i).getPanes().size());
            sb.append(" panes)");
            if (i == this.activeWindow) {
               sb.append(" (active)");
            }

            return sb.toString();
         }).sorted().forEach(out::println);
      }
   }

   protected void previousWindow(PrintStream out, PrintStream err, List<String> args) throws Exception {
      String[] usage = new String[]{"previous-window - ", "Usage: previous-window", "  -? --help                    Show help"};
      Options opt = Options.compile(usage).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         if (this.windows.size() > 1) {
            this.activeWindow--;
            if (this.activeWindow < 0) {
               this.activeWindow = this.windows.size() - 1;
            }

            this.setDirty();
         }
      }
   }

   protected void nextWindow(PrintStream out, PrintStream err, List<String> args) throws Exception {
      String[] usage = new String[]{"next-window - ", "Usage: next-window", "  -? --help                    Show help"};
      Options opt = Options.compile(usage).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         if (this.windows.size() > 1) {
            this.activeWindow++;
            if (this.activeWindow >= this.windows.size()) {
               this.activeWindow = 0;
            }

            this.setDirty();
         }
      }
   }

   protected void newWindow(PrintStream out, PrintStream err, List<String> args) throws Exception {
      String[] usage = new String[]{"new-window - ", "Usage: new-window", "  -? --help                    Show help"};
      Options opt = Options.compile(usage).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         this.windows.add(new Tmux.Window(this));
         this.activeWindow = this.windows.size() - 1;
         this.runner.accept(this.active().getConsole());
         this.setDirty();
      }
   }

   protected void setOption(PrintStream out, PrintStream err, List<String> args) throws Exception {
      String[] usage = new String[]{
         "set-option - ",
         "Usage: set-option [-agosquw] option [value]",
         "  -? --help                    Show help",
         "  -u --unset                   Unset the option"
      };
      Options opt = Options.compile(usage).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         int nbargs = opt.args().size();
         if (nbargs >= 1 && nbargs <= 2) {
            String name = opt.args().get(0);
            String value = nbargs > 1 ? opt.args().get(1) : null;
            if (!name.startsWith("@")) {
               switch (name) {
                  case "prefix":
                     if (value == null) {
                        throw new IllegalArgumentException("Missing argument");
                     }

                     String prefix = KeyMap.translate(value);
                     String oldPrefix = this.serverOptions.put("prefix", prefix);
                     KeyMap<Object> newKeys = this.createEmptyKeyMap(prefix);

                     for (Entry<String, Object> e : this.keyMap.getBoundKeys().entrySet()) {
                        if (e.getValue() instanceof String) {
                           if (e.getKey().equals(oldPrefix + oldPrefix)) {
                              newKeys.bind(e.getValue(), prefix + prefix);
                           } else if (e.getKey().startsWith(oldPrefix)) {
                              newKeys.bind(e.getValue(), prefix + e.getKey().substring(oldPrefix.length()));
                           } else {
                              newKeys.bind(e.getValue(), e.getKey());
                           }
                        }
                     }

                     this.keyMap = newKeys;
               }
            }
         } else {
            throw new Options.HelpException(opt.usage());
         }
      }
   }

   protected void bindKey(PrintStream out, PrintStream err, List<String> args) throws Exception {
      String[] usage = new String[]{"bind-key - ", "Usage: bind-key key command [arguments]", "  -? --help                    Show help"};
      Options opt = Options.compile(usage).setOptionsFirst(true).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         List<String> vargs = opt.args();
         if (vargs.size() < 2) {
            throw new Options.HelpException(opt.usage());
         } else {
            String prefix = this.serverOptions.get("prefix");
            String key = prefix + KeyMap.translate(vargs.remove(0));
            this.keyMap.unbind(key.substring(0, 2));
            this.keyMap.bind(vargs.toArray(new String[vargs.size()]), key);
         }
      }
   }

   protected void unbindKey(PrintStream out, PrintStream err, List<String> args) throws Exception {
      String[] usage = new String[]{"unbind-key - ", "Usage: unbind-key key", "  -? --help                    Show help"};
      Options opt = Options.compile(usage).setOptionsFirst(true).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         List<String> vargs = opt.args();
         if (vargs.size() != 1) {
            throw new Options.HelpException(opt.usage());
         } else {
            String prefix = this.serverOptions.get("prefix");
            String key = prefix + KeyMap.translate(vargs.remove(0));
            this.keyMap.unbind(key);
            this.keyMap.bind(Tmux.Binding.Discard, key);
         }
      }
   }

   protected void listKeys(PrintStream out, PrintStream err, List<String> args) throws Exception {
      String[] usage = new String[]{"list-keys - ", "Usage: list-keys ", "  -? --help                    Show help"};
      Options opt = Options.compile(usage).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         String prefix = this.serverOptions.get("prefix");
         this.keyMap.getBoundKeys().entrySet().stream().filter(e -> e.getValue() instanceof String).map(e -> {
            String key = e.getKey();
            String val = (String)e.getValue();
            StringBuilder sb = new StringBuilder();
            sb.append("bind-key -T ");
            if (key.startsWith(prefix)) {
               sb.append("prefix ");
               key = key.substring(prefix.length());
            } else {
               sb.append("root   ");
            }

            sb.append(KeyMap.display(key));

            while (sb.length() < 32) {
               sb.append(" ");
            }

            sb.append(val);
            return sb.toString();
         }).sorted().forEach(out::println);
      }
   }

   protected void sendKeys(PrintStream out, PrintStream err, List<String> args) throws Exception {
      String[] usage = new String[]{
         "send-keys - ",
         "Usage: send-keys [-lXRM] [-N repeat-count] [-t target-pane] key...",
         "  -? --help                    Show help",
         "  -l --literal                Send key literally",
         "  -N --number=repeat-count     Specifies a repeat count"
      };
      Options opt = Options.compile(usage).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         int i = 0;

         for (int n = opt.getNumber("number"); i < n; i++) {
            for (String arg : opt.args()) {
               String s = opt.isSet("literal") ? arg : KeyMap.translate(arg);
               this.active().getMasterInputOutput().write(s.getBytes());
            }
         }
      }
   }

   protected void clockMode(PrintStream out, PrintStream err, List<String> args) throws Exception {
      String[] usage = new String[]{"clock-mode - ", "Usage: clock-mode", "  -? --help                    Show help"};
      Options opt = Options.compile(usage).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         this.active().clock = true;
         if (this.clockFuture == null) {
            long initial = Instant.now().until(Instant.now().truncatedTo(ChronoUnit.MINUTES).plusSeconds(60L), ChronoUnit.MILLIS);
            long delay = TimeUnit.MILLISECONDS.convert(1L, TimeUnit.SECONDS);
            this.clockFuture = this.executor.scheduleWithFixedDelay(this::setDirty, initial, delay, TimeUnit.MILLISECONDS);
         }

         this.setDirty();
      }
   }

   protected void displayPanes(PrintStream out, PrintStream err, List<String> args) throws Exception {
      String[] usage = new String[]{"display-panes - ", "Usage: display-panes", "  -? --help                    Show help"};
      Options opt = Options.compile(usage).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         this.identify = true;
         this.setDirty();
         this.executor.schedule(() -> {
            this.identify = false;
            this.setDirty();
         }, 1L, TimeUnit.SECONDS);
      }
   }

   protected void resizePane(PrintStream out, PrintStream err, List<String> args) throws Exception {
      String[] usage = new String[]{
         "resize-pane - ",
         "Usage: resize-pane [-UDLR] [-x width] [-y height] [-t target-pane] [adjustment]",
         "  -? --help                    Show help",
         "  -U                           Resize pane upward",
         "  -D                           Select pane downward",
         "  -L                           Select pane to the left",
         "  -R                           Select pane to the right",
         "  -x --width=width             Set the width of the pane",
         "  -y --height=height           Set the height of the pane"
      };
      Options opt = Options.compile(usage).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         int adjust;
         if (opt.args().size() == 0) {
            adjust = 1;
         } else {
            if (opt.args().size() != 1) {
               throw new Options.HelpException(opt.usage());
            }

            adjust = Integer.parseInt(opt.args().get(0));
         }

         this.window().resizePane(opt, adjust);
         this.setDirty();
      }
   }

   protected void selectPane(PrintStream out, PrintStream err, List<String> args) throws Exception {
      String[] usage = new String[]{
         "select-pane - ",
         "Usage: select-pane [-UDLR] [-t target-pane]",
         "  -? --help                    Show help",
         "  -U                           Select pane up",
         "  -D                           Select pane down",
         "  -L                           Select pane left",
         "  -R                           Select pane right"
      };
      Options opt = Options.compile(usage).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         if (this.window().selectPane(opt)) {
            this.setDirty();
         }
      }
   }

   protected void sendPrefix(PrintStream out, PrintStream err, List<String> args) throws Exception {
      String[] usage = new String[]{"send-prefix - ", "Usage: send-prefix [-2] [-t target-pane]", "  -? --help                    Show help"};
      Options opt = Options.compile(usage).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         this.active().getMasterInputOutput().write(this.serverOptions.get("prefix").getBytes());
      }
   }

   protected void splitWindow(PrintStream out, PrintStream err, List<String> args) throws Exception {
      String[] usage = new String[]{
         "split-window - ",
         "Usage: split-window [-bdfhvP] [-c start-directory] [-F format] [-p percentage|-l size] [-t target-pane] [command]",
         "  -? --help                    Show help",
         "  -h --horizontal              Horizontal split",
         "  -v --vertical                Vertical split",
         "  -l --size=size               Size",
         "  -p --perc=percentage         Percentage",
         "  -b --before                  Insert the new pane before the active one",
         "  -f                           Split the full window instead of the active pane",
         "  -d                           Do not make the new pane the active one"
      };
      Options opt = Options.compile(usage).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         Tmux.VirtualConsole newConsole = this.window().splitPane(opt);
         this.runner.accept(newConsole.getConsole());
         this.setDirty();
      }
   }

   protected void layoutResize() {
   }

   protected synchronized void redraw() {
      long[] screen = new long[this.size.getRows() * this.size.getColumns()];
      Arrays.fill(screen, 32L);
      int[] cursor = new int[2];

      for (Tmux.VirtualConsole terminal : this.panes()) {
         if (terminal.clock) {
            String str = DateFormat.getTimeInstance(3).format(new Date());
            this.print(screen, terminal, str, this.CLOCK_COLOR);
         } else {
            terminal.dump(screen, terminal.top(), terminal.left(), this.size.getRows(), this.size.getColumns(), terminal == this.active() ? cursor : null);
         }

         if (this.identify) {
            String id = Integer.toString(terminal.id);
            this.print(screen, terminal, id, terminal == this.active() ? this.ACTIVE_COLOR : this.INACTIVE_COLOR);
         }

         this.drawBorder(screen, this.size, terminal, 0L);
      }

      this.drawBorder(screen, this.size, this.active(), 1155173304420532224L);
      Arrays.fill(screen, (this.size.getRows() - 1) * this.size.getColumns(), this.size.getRows() * this.size.getColumns(), 2305843558969507872L);
      List<AttributedString> lines = new ArrayList<>();
      int prevBg = 0;
      int prevFg = 0;
      boolean prevInv = false;
      boolean prevUl = false;
      boolean prevBold = false;
      boolean prevConceal = false;
      boolean prevHasFg = false;
      boolean prevHasBg = false;

      for (int y = 0; y < this.size.getRows(); y++) {
         AttributedStringBuilder sb = new AttributedStringBuilder(this.size.getColumns());

         for (int x = 0; x < this.size.getColumns(); x++) {
            long d = screen[y * this.size.getColumns() + x];
            int c = (int)(d & 4294967295L);
            int a = (int)(d >> 32);
            int bg = a & 4095;
            int fg = (a & 16773120) >> 12;
            boolean ul = (a & 16777216) != 0;
            boolean inv = (a & 33554432) != 0;
            boolean conceal = (a & 67108864) != 0;
            boolean bold = (a & 134217728) != 0;
            boolean hasFg = (a & 268435456) != 0;
            boolean hasBg = (a & 536870912) != 0;
            if (hasBg && prevHasBg && bg != prevBg || prevHasBg != hasBg) {
               if (!hasBg) {
                  sb.style(sb.style().backgroundDefault());
               } else {
                  int col = Colors.roundRgbColor((bg & 3840) >> 4, bg & 240, (bg & 15) << 4, 256);
                  sb.style(sb.style().background(col));
               }

               prevBg = bg;
               prevHasBg = hasBg;
            }

            if (hasFg && prevHasFg && fg != prevFg || prevHasFg != hasFg) {
               if (!hasFg) {
                  sb.style(sb.style().foregroundDefault());
               } else {
                  int var32 = Colors.roundRgbColor((fg & 3840) >> 4, fg & 240, (fg & 15) << 4, 256);
                  sb.style(sb.style().foreground(var32));
               }

               prevFg = fg;
               prevHasFg = hasFg;
            }

            if (conceal != prevConceal) {
               sb.style(conceal ? sb.style().conceal() : sb.style().concealOff());
               prevConceal = conceal;
            }

            if (inv != prevInv) {
               sb.style(inv ? sb.style().inverse() : sb.style().inverseOff());
               prevInv = inv;
            }

            if (ul != prevUl) {
               sb.style(ul ? sb.style().underline() : sb.style().underlineOff());
               prevUl = ul;
            }

            if (bold != prevBold) {
               sb.style(bold ? sb.style().bold() : sb.style().boldOff());
               prevBold = bold;
            }

            sb.append((char)c);
         }

         lines.add(sb.toAttributedString());
      }

      this.display.resize(this.size.getRows(), this.size.getColumns());
      this.display.update(lines, this.size.cursorPos(cursor[1], cursor[0]));
   }

   private void print(long[] screen, Tmux.VirtualConsole terminal, String id, int color) {
      if (terminal.height() > 5) {
         long attr = (long)color << 32 | 2305843009213693952L;
         int yoff = (terminal.height() - 5) / 2;
         int xoff = (terminal.width() - id.length() * 6) / 2;

         for (int i = 0; i < id.length(); i++) {
            char ch = id.charAt(i);
            int idx;
            switch (ch) {
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
                  idx = ch - '0';
                  break;
               case ':':
                  idx = 10;
                  break;
               case ';':
               case '<':
               case '=':
               case '>':
               case '?':
               case '@':
               case 'B':
               case 'C':
               case 'D':
               case 'E':
               case 'F':
               case 'G':
               case 'H':
               case 'I':
               case 'J':
               case 'K':
               case 'L':
               case 'N':
               case 'O':
               default:
                  idx = -1;
                  break;
               case 'A':
                  idx = 11;
                  break;
               case 'M':
                  idx = 13;
                  break;
               case 'P':
                  idx = 12;
            }

            if (idx >= 0) {
               int[][] data = WINDOW_CLOCK_TABLE[idx];

               for (int y = 0; y < data.length; y++) {
                  for (int x = 0; x < data[y].length; x++) {
                     if (data[y][x] != 0) {
                        int off = (terminal.top + yoff + y) * this.size.getColumns() + terminal.left() + xoff + x + 6 * i;
                        screen[off] = attr | 32L;
                     }
                  }
               }
            }
         }
      } else {
         long attr = (long)color << 44 | 1152921504606846976L;
         int yoff = (terminal.height() + 1) / 2;
         int xoff = (terminal.width() - id.length()) / 2;
         int off = (terminal.top + yoff) * this.size.getColumns() + terminal.left() + xoff;

         for (int i = 0; i < id.length(); i++) {
            screen[off + i] = attr | id.charAt(i);
         }
      }
   }

   private void drawBorder(long[] screen, Size size, Tmux.VirtualConsole terminal, long attr) {
      for (int i = terminal.left(); i < terminal.right(); i++) {
         int y0 = terminal.top() - 1;
         int y1 = terminal.bottom();
         this.drawBorderChar(screen, size, i, y0, attr, 9472);
         this.drawBorderChar(screen, size, i, y1, attr, 9472);
      }

      for (int i = terminal.top(); i < terminal.bottom(); i++) {
         int x0 = terminal.left() - 1;
         int x1 = terminal.right();
         this.drawBorderChar(screen, size, x0, i, attr, 9474);
         this.drawBorderChar(screen, size, x1, i, attr, 9474);
      }

      this.drawBorderChar(screen, size, terminal.left() - 1, terminal.top() - 1, attr, 9484);
      this.drawBorderChar(screen, size, terminal.right(), terminal.top() - 1, attr, 9488);
      this.drawBorderChar(screen, size, terminal.left() - 1, terminal.bottom(), attr, 9492);
      this.drawBorderChar(screen, size, terminal.right(), terminal.bottom(), attr, 9496);
   }

   private void drawBorderChar(long[] screen, Size size, int x, int y, long attr, int c) {
      if (x >= 0 && x < size.getColumns() && y >= 0 && y < size.getRows() - 1) {
         int oldc = (int)(screen[y * size.getColumns() + x] & 4294967295L);
         c = this.addBorder(c, oldc);
         screen[y * size.getColumns() + x] = attr | c;
      }
   }

   private int addBorder(int c, int oldc) {
      if (oldc == 32) {
         return c;
      } else if (oldc == 9532) {
         return 9532;
      } else {
         switch (c) {
            case 9472:
               return this.addBorder(9588, this.addBorder(9590, oldc));
            case 9474:
               return this.addBorder(9591, this.addBorder(9589, oldc));
            case 9484:
               return this.addBorder(9590, this.addBorder(9591, oldc));
            case 9488:
               return this.addBorder(9588, this.addBorder(9591, oldc));
            case 9492:
               return this.addBorder(9590, this.addBorder(9589, oldc));
            case 9496:
               return this.addBorder(9588, this.addBorder(9589, oldc));
            case 9500:
               return this.addBorder(9590, this.addBorder(9474, oldc));
            case 9508:
               return this.addBorder(9588, this.addBorder(9474, oldc));
            case 9516:
               return this.addBorder(9591, this.addBorder(9472, oldc));
            case 9524:
               return this.addBorder(9589, this.addBorder(9472, oldc));
            case 9588:
               switch (oldc) {
                  case 9472:
                     return 9472;
                  case 9474:
                     return 9508;
                  case 9484:
                     return 9516;
                  case 9488:
                     return 9488;
                  case 9492:
                     return 9524;
                  case 9496:
                     return 9496;
                  case 9500:
                     return 9532;
                  case 9508:
                     return 9508;
                  case 9516:
                     return 9516;
                  case 9524:
                     return 9524;
                  default:
                     throw new IllegalArgumentException();
               }
            case 9589:
               switch (oldc) {
                  case 9472:
                     return 9524;
                  case 9474:
                     return 9474;
                  case 9484:
                     return 9500;
                  case 9488:
                     return 9508;
                  case 9492:
                     return 9492;
                  case 9496:
                     return 9496;
                  case 9500:
                     return 9500;
                  case 9508:
                     return 9508;
                  case 9516:
                     return 9532;
                  case 9524:
                     return 9524;
                  default:
                     throw new IllegalArgumentException();
               }
            case 9590:
               switch (oldc) {
                  case 9472:
                     return 9472;
                  case 9474:
                     return 9500;
                  case 9484:
                     return 9484;
                  case 9488:
                     return 9516;
                  case 9492:
                     return 9492;
                  case 9496:
                     return 9524;
                  case 9500:
                     return 9500;
                  case 9508:
                     return 9532;
                  case 9516:
                     return 9516;
                  case 9524:
                     return 9524;
                  default:
                     throw new IllegalArgumentException();
               }
            case 9591:
               switch (oldc) {
                  case 9472:
                     return 9516;
                  case 9474:
                     return 9474;
                  case 9484:
                     return 9484;
                  case 9488:
                     return 9488;
                  case 9492:
                     return 9500;
                  case 9496:
                     return 9508;
                  case 9500:
                     return 9500;
                  case 9508:
                     return 9508;
                  case 9516:
                     return 9516;
                  case 9524:
                     return 9532;
                  default:
                     throw new IllegalArgumentException();
               }
            default:
               throw new IllegalArgumentException();
         }
      }
   }

   private static int findMatch(String layout, char c0, char c1) {
      if (layout.charAt(0) != c0) {
         throw new IllegalArgumentException();
      } else {
         int nb = 0;

         int i;
         for (i = 0; i < layout.length(); i++) {
            char c = layout.charAt(i);
            if (c == c0) {
               nb++;
            } else if (c == c1) {
               if (--nb == 0) {
                  return i;
               }
            }
         }

         if (nb > 0) {
            throw new IllegalArgumentException("No matching '" + c1 + "'");
         } else {
            return i;
         }
      }
   }

   static enum Binding {
      Discard,
      SelfInsert,
      Mouse;
   }

   static class Layout {
      static final Pattern PATTERN = Pattern.compile("([0-9]+)x([0-9]+),([0-9]+),([0-9]+)([^0-9]\\S*)?");
      private static final int PANE_MINIMUM = 3;
      Tmux.Layout.Type type;
      Tmux.Layout parent;
      int sx;
      int sy;
      int xoff;
      int yoff;
      List<Tmux.Layout> cells = new CopyOnWriteArrayList<>();

      public static Tmux.Layout parse(String layout) {
         if (layout.length() < 6) {
            throw new IllegalArgumentException("Bad syntax");
         } else {
            String chk = layout.substring(0, 4);
            if (layout.charAt(4) != ',') {
               throw new IllegalArgumentException("Bad syntax");
            } else {
               layout = layout.substring(5);
               if (Integer.parseInt(chk, 16) != checksum(layout)) {
                  throw new IllegalArgumentException("Bad checksum");
               } else {
                  return parseCell(null, layout);
               }
            }
         }
      }

      public String dump() {
         StringBuilder sb = new StringBuilder(64);
         sb.append("0000,");
         this.doDump(sb);
         int chk = checksum(sb, 5);
         sb.setCharAt(0, toHexChar(chk >> 12 & 15));
         sb.setCharAt(1, toHexChar(chk >> 8 & 15));
         sb.setCharAt(2, toHexChar(chk >> 4 & 15));
         sb.setCharAt(3, toHexChar(chk & 15));
         return sb.toString();
      }

      private static char toHexChar(int i) {
         return i < 10 ? (char)(i + 48) : (char)(i - 10 + 97);
      }

      private void doDump(StringBuilder sb) {
         sb.append(this.sx).append('x').append(this.sy).append(',').append(this.xoff).append(',').append(this.yoff);
         switch (this.type) {
            case LeftRight:
            case TopBottom:
               sb.append((char)(this.type == Tmux.Layout.Type.TopBottom ? '[' : '{'));
               boolean first = true;

               for (Tmux.Layout c : this.cells) {
                  if (first) {
                     first = false;
                  } else {
                     sb.append(',');
                  }

                  c.doDump(sb);
               }

               sb.append((char)(this.type == Tmux.Layout.Type.TopBottom ? ']' : '}'));
               break;
            case WindowPane:
               sb.append(',').append('0');
         }
      }

      public void resize(Tmux.Layout.Type type, int change, boolean opposite) {
         Tmux.Layout lc = this;

         Tmux.Layout lcparent;
         for (lcparent = this.parent; lcparent != null && lcparent.type != type; lcparent = lcparent.parent) {
            lc = lcparent;
         }

         if (lcparent != null) {
            if (lc.nextSibling() == null) {
               lc = lc.prevSibling();
            }

            int needed = change;

            while (needed != 0) {
               int size;
               if (change > 0) {
                  size = lc.resizePaneGrow(type, needed, opposite);
                  needed -= size;
               } else {
                  size = lc.resizePaneShrink(type, needed);
                  needed += size;
               }

               if (size == 0) {
                  break;
               }
            }

            this.fixOffsets();
            this.fixPanes();
         }
      }

      int resizePaneGrow(Tmux.Layout.Type type, int needed, boolean opposite) {
         int size = 0;

         Tmux.Layout lcremove;
         for (lcremove = this.nextSibling(); lcremove != null; lcremove = lcremove.nextSibling()) {
            size = lcremove.resizeCheck(type);
            if (size > 0) {
               break;
            }
         }

         if (opposite && lcremove == null) {
            for (lcremove = this.prevSibling(); lcremove != null; lcremove = lcremove.prevSibling()) {
               size = lcremove.resizeCheck(type);
               if (size > 0) {
                  break;
               }
            }
         }

         if (lcremove == null) {
            return 0;
         } else {
            if (size > needed) {
               size = needed;
            }

            this.resizeAdjust(type, size);
            lcremove.resizeAdjust(type, -size);
            return size;
         }
      }

      int resizePaneShrink(Tmux.Layout.Type type, int needed) {
         int size = 0;
         Tmux.Layout lcremove = this;

         do {
            size = lcremove.resizeCheck(type);
            if (size > 0) {
               break;
            }

            lcremove = lcremove.prevSibling();
         } while (lcremove != null);

         if (lcremove == null) {
            return 0;
         } else {
            Tmux.Layout lcadd = this.nextSibling();
            if (lcadd == null) {
               return 0;
            } else {
               if (size > -needed) {
                  size = -needed;
               }

               lcadd.resizeAdjust(type, size);
               lcremove.resizeAdjust(type, -size);
               return size;
            }
         }
      }

      Tmux.Layout prevSibling() {
         int idx = this.parent.cells.indexOf(this);
         return idx > 0 ? this.parent.cells.get(idx - 1) : null;
      }

      Tmux.Layout nextSibling() {
         int idx = this.parent.cells.indexOf(this);
         return idx < this.parent.cells.size() - 1 ? this.parent.cells.get(idx + 1) : null;
      }

      public void resizeTo(Tmux.Layout.Type type, int new_size) {
         Tmux.Layout lc = this;

         Tmux.Layout lcparent;
         for (lcparent = this.parent; lcparent != null && lcparent.type != type; lcparent = lcparent.parent) {
            lc = lcparent;
         }

         if (lcparent != null) {
            int size = type == Tmux.Layout.Type.LeftRight ? lc.sx : lc.sy;
            int change = lc.nextSibling() == null ? size - new_size : new_size - size;
            lc.resize(type, change, true);
         }
      }

      public void resize(int sx, int sy) {
         int xchange = sx - this.sx;
         int xlimit = this.resizeCheck(Tmux.Layout.Type.LeftRight);
         if (xchange < 0 && xchange < -xlimit) {
            xchange = -xlimit;
         }

         if (xlimit == 0) {
            if (sx <= this.sx) {
               xchange = 0;
            } else {
               xchange = sx - this.sx;
            }
         }

         if (xchange != 0) {
            this.resizeAdjust(Tmux.Layout.Type.LeftRight, xchange);
         }

         int ychange = sy - this.sy;
         int ylimit = this.resizeCheck(Tmux.Layout.Type.TopBottom);
         if (ychange < 0 && ychange < -ylimit) {
            ychange = -ylimit;
         }

         if (ylimit == 0) {
            if (sy <= this.sy) {
               ychange = 0;
            } else {
               ychange = sy - this.sy;
            }
         }

         if (ychange != 0) {
            this.resizeAdjust(Tmux.Layout.Type.TopBottom, ychange);
         }

         this.fixOffsets();
         this.fixPanes(sx, sy);
      }

      public void remove() {
         if (this.parent == null) {
            throw new IllegalStateException();
         } else {
            int idx = this.parent.cells.indexOf(this);
            Tmux.Layout other = this.parent.cells.get(idx == 0 ? 1 : idx - 1);
            other.resizeAdjust(this.parent.type, this.parent.type == Tmux.Layout.Type.LeftRight ? this.sx + 1 : this.sy + 1);
            this.parent.cells.remove(this);
            if (other.parent.cells.size() == 1) {
               if (other.parent.parent == null) {
                  other.parent = null;
               } else {
                  other.parent.parent.cells.set(other.parent.parent.cells.indexOf(other.parent), other);
                  other.parent = other.parent.parent;
               }
            }
         }
      }

      private int resizeCheck(Tmux.Layout.Type type) {
         if (this.type == Tmux.Layout.Type.WindowPane) {
            int min = 3;
            int avail;
            if (type == Tmux.Layout.Type.LeftRight) {
               avail = this.sx;
            } else {
               avail = this.sy;
               min++;
            }

            if (avail > min) {
               avail -= min;
            } else {
               avail = 0;
            }

            return avail;
         } else {
            return this.type == type
               ? this.cells.stream().mapToInt(c -> c != null ? c.resizeCheck(type) : 0).sum()
               : this.cells.stream().mapToInt(c -> c != null ? c.resizeCheck(type) : Integer.MAX_VALUE).min().orElse(Integer.MAX_VALUE);
         }
      }

      private void resizeAdjust(Tmux.Layout.Type type, int change) {
         if (type == Tmux.Layout.Type.LeftRight) {
            this.sx += change;
         } else {
            this.sy += change;
         }

         if (this.type != Tmux.Layout.Type.WindowPane) {
            if (this.type != type) {
               for (Tmux.Layout c : this.cells) {
                  c.resizeAdjust(type, change);
               }
            } else {
               while (change != 0) {
                  for (Tmux.Layout c : this.cells) {
                     if (change == 0) {
                        break;
                     }

                     if (change > 0) {
                        c.resizeAdjust(type, 1);
                        change--;
                     } else if (c.resizeCheck(type) > 0) {
                        c.resizeAdjust(type, -1);
                        change++;
                     }
                  }
               }
            }
         }
      }

      public void fixOffsets() {
         if (this.type == Tmux.Layout.Type.LeftRight) {
            int xoff = this.xoff;

            for (Tmux.Layout cell : this.cells) {
               cell.xoff = xoff;
               cell.yoff = this.yoff;
               cell.fixOffsets();
               xoff += cell.sx + 1;
            }
         } else if (this.type == Tmux.Layout.Type.TopBottom) {
            int yoff = this.yoff;

            for (Tmux.Layout cell : this.cells) {
               cell.xoff = this.xoff;
               cell.yoff = yoff;
               cell.fixOffsets();
               yoff += cell.sy + 1;
            }
         }
      }

      public void fixPanes() {
      }

      public void fixPanes(int sx, int sy) {
      }

      public int countCells() {
         switch (this.type) {
            case LeftRight:
            case TopBottom:
               return this.cells.stream().mapToInt(Tmux.Layout::countCells).sum();
            default:
               return 1;
         }
      }

      public Tmux.Layout split(Tmux.Layout.Type type, int size, boolean insertBefore) {
         if (type == Tmux.Layout.Type.WindowPane) {
            throw new IllegalStateException();
         } else if ((type == Tmux.Layout.Type.LeftRight ? this.sx : this.sy) < 7) {
            return null;
         } else if (this.parent == null) {
            throw new IllegalStateException();
         } else {
            int saved_size = type == Tmux.Layout.Type.LeftRight ? this.sx : this.sy;
            int size2 = size < 0 ? (saved_size + 1) / 2 - 1 : (insertBefore ? saved_size - size - 1 : size);
            if (size2 < 3) {
               size2 = 3;
            } else if (size2 > saved_size - 2) {
               size2 = saved_size - 2;
            }

            int size1 = saved_size - 1 - size2;
            if (this.parent.type != type) {
               Tmux.Layout p = new Tmux.Layout();
               p.type = type;
               p.parent = this.parent;
               p.sx = this.sx;
               p.sy = this.sy;
               p.xoff = this.xoff;
               p.yoff = this.yoff;
               this.parent.cells.set(this.parent.cells.indexOf(this), p);
               p.cells.add(this);
               this.parent = p;
            }

            Tmux.Layout cell = new Tmux.Layout();
            cell.type = Tmux.Layout.Type.WindowPane;
            cell.parent = this.parent;
            this.parent.cells.add(this.parent.cells.indexOf(this) + (insertBefore ? 0 : 1), cell);
            int sx = this.sx;
            int sy = this.sy;
            int xoff = this.xoff;
            int yoff = this.yoff;
            Tmux.Layout cell1;
            Tmux.Layout cell2;
            if (insertBefore) {
               cell1 = cell;
               cell2 = this;
            } else {
               cell1 = this;
               cell2 = cell;
            }

            if (type == Tmux.Layout.Type.LeftRight) {
               cell1.setSize(size1, sy, xoff, yoff);
               cell2.setSize(size2, sy, xoff + size1 + 1, yoff);
            } else {
               cell1.setSize(sx, size1, xoff, yoff);
               cell2.setSize(sx, size2, xoff, yoff + size1 + 1);
            }

            return cell;
         }
      }

      private void setSize(int sx, int sy, int xoff, int yoff) {
         this.sx = sx;
         this.sy = sy;
         this.xoff = xoff;
         this.yoff = yoff;
      }

      private static int checksum(CharSequence layout) {
         return checksum(layout, 0);
      }

      private static int checksum(CharSequence layout, int start) {
         int csum = 0;

         for (int i = start; i < layout.length(); i++) {
            csum = (csum >> 1) + ((csum & 1) << 15);
            csum += layout.charAt(i);
         }

         return csum;
      }

      private static Tmux.Layout parseCell(Tmux.Layout parent, String layout) {
         Matcher matcher = PATTERN.matcher(layout);
         if (matcher.matches()) {
            Tmux.Layout cell = new Tmux.Layout();
            cell.type = Tmux.Layout.Type.WindowPane;
            cell.parent = parent;
            cell.sx = Integer.parseInt(matcher.group(1));
            cell.sy = Integer.parseInt(matcher.group(2));
            cell.xoff = Integer.parseInt(matcher.group(3));
            cell.yoff = Integer.parseInt(matcher.group(4));
            if (parent != null) {
               parent.cells.add(cell);
            }

            layout = matcher.group(5);
            if (layout != null && !layout.isEmpty()) {
               if (layout.charAt(0) == ',') {
                  int i = 1;

                  while (i < layout.length() && Character.isDigit(layout.charAt(i))) {
                     i++;
                  }

                  if (i == layout.length()) {
                     return cell;
                  }

                  if (layout.charAt(i) == ',') {
                     layout = layout.substring(i);
                  }
               }

               switch (layout.charAt(0)) {
                  case ',':
                     parseCell(parent, layout.substring(1));
                     return cell;
                  case '[':
                     cell.type = Tmux.Layout.Type.TopBottom;
                     int i = Tmux.findMatch(layout, '[', ']');
                     parseCell(cell, layout.substring(1, i));
                     layout = layout.substring(i + 1);
                     if (!layout.isEmpty() && layout.charAt(0) == ',') {
                        parseCell(parent, layout.substring(1));
                     }

                     return cell;
                  case '{':
                     cell.type = Tmux.Layout.Type.LeftRight;
                     int i = Tmux.findMatch(layout, '{', '}');
                     parseCell(cell, layout.substring(1, i));
                     layout = layout.substring(i + 1);
                     if (!layout.isEmpty() && layout.charAt(0) == ',') {
                        parseCell(parent, layout.substring(1));
                     }

                     return cell;
                  default:
                     throw new IllegalArgumentException("Unexpected '" + layout.charAt(0) + "'");
               }
            } else {
               return cell;
            }
         } else {
            throw new IllegalArgumentException("Bad syntax");
         }
      }

      static enum Type {
         LeftRight,
         TopBottom,
         WindowPane;
      }
   }

   private static class VirtualConsole implements Closeable {
      private final ScreenTerminal terminal;
      private final Consumer<Tmux.VirtualConsole> closer;
      private final int id;
      private int left;
      private int top;
      private final Tmux.Layout layout;
      private int active;
      private boolean clock;
      private final OutputStream masterOutput;
      private final OutputStream masterInputOutput;
      private final LineDisciplineTerminal console;

      public VirtualConsole(
         int id, String type, int left, int top, int columns, int rows, final Runnable dirty, final Consumer<Tmux.VirtualConsole> closer, Tmux.Layout layout
      ) throws IOException {
         String name = String.format("tmux%02d", id);
         this.id = id;
         this.left = left;
         this.top = top;
         this.closer = closer;
         this.terminal = new ScreenTerminal(columns, rows) {
            @Override
            protected void setDirty() {
               super.setDirty();
               dirty.run();
            }
         };
         this.masterOutput = new Tmux.VirtualConsole.MasterOutputStream();
         this.masterInputOutput = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
               VirtualConsole.this.console.processInputByte(b);
            }
         };
         this.console = new LineDisciplineTerminal(name, type, this.masterOutput, null) {
            @Override
            protected void doClose() throws IOException {
               super.doClose();
               closer.accept(VirtualConsole.this);
            }
         };
         this.console.setSize(new Size(columns, rows));
         this.layout = layout;
      }

      Tmux.Layout layout() {
         return this.layout;
      }

      public int left() {
         return this.left;
      }

      public int top() {
         return this.top;
      }

      public int right() {
         return this.left() + this.width();
      }

      public int bottom() {
         return this.top() + this.height();
      }

      public int width() {
         return this.console.getWidth();
      }

      public int height() {
         return this.console.getHeight();
      }

      public LineDisciplineTerminal getConsole() {
         return this.console;
      }

      public OutputStream getMasterInputOutput() {
         return this.masterInputOutput;
      }

      public void resize(int left, int top, int width, int height) {
         this.left = left;
         this.top = top;
         this.console.setSize(new Size(width, height));
         this.terminal.setSize(width, height);
         this.console.raise(Terminal.Signal.WINCH);
      }

      public void dump(long[] fullscreen, int ftop, int fleft, int fheight, int fwidth, int[] cursor) {
         this.terminal.dump(fullscreen, ftop, fleft, fheight, fwidth, cursor);
      }

      @Override
      public void close() throws IOException {
         this.console.close();
      }

      private class MasterOutputStream extends OutputStream {
         private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         private final CharsetDecoder decoder = Charset.defaultCharset()
            .newDecoder()
            .onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE);

         private MasterOutputStream() {
         }

         @Override
         public synchronized void write(int b) {
            this.buffer.write(b);
         }

         @Override
         public void write(byte[] b, int off, int len) throws IOException {
            this.buffer.write(b, off, len);
         }

         @Override
         public synchronized void flush() throws IOException {
            int size = this.buffer.size();
            if (size > 0) {
               while (true) {
                  CharBuffer out = CharBuffer.allocate(size);
                  ByteBuffer in = ByteBuffer.wrap(this.buffer.toByteArray());
                  CoderResult result = this.decoder.decode(in, out, false);
                  if (!result.isOverflow()) {
                     this.buffer.reset();
                     this.buffer.write(in.array(), in.arrayOffset(), in.remaining());
                     if (out.position() > 0) {
                        ((Buffer)out).flip();
                        VirtualConsole.this.terminal.write(out);
                        VirtualConsole.this.masterInputOutput.write(VirtualConsole.this.terminal.read().getBytes());
                     }
                     break;
                  }

                  size *= 2;
               }
            }
         }

         @Override
         public void close() throws IOException {
            this.flush();
         }
      }
   }

   private class Window {
      private List<Tmux.VirtualConsole> panes = new CopyOnWriteArrayList<>();
      private Tmux.VirtualConsole active;
      private int lastActive;
      private final AtomicInteger paneId = new AtomicInteger();
      private Tmux.Layout layout;
      private Tmux tmux;
      private String name;

      public Window(Tmux tmux) throws IOException {
         this.tmux = tmux;
         this.layout = new Tmux.Layout();
         this.layout.sx = Tmux.this.size.getColumns();
         this.layout.sy = Tmux.this.size.getRows();
         this.layout.type = Tmux.Layout.Type.WindowPane;
         int var10003 = this.paneId.incrementAndGet();
         String var10004 = Tmux.this.term;
         int var10007 = Tmux.this.size.getColumns();
         int var10008 = Tmux.this.size.getRows() - 1;
         Runnable var10009 = () -> tmux.setDirty();
         Objects.requireNonNull(tmux);
         Tmux var3 = tmux;
         this.active = new Tmux.VirtualConsole(var10003, var10004, 0, 0, var10007, var10008, var10009, x$0 -> var3.close(x$0), this.layout);
         this.active.active = this.lastActive++;
         this.active.getConsole().setAttributes(Tmux.this.terminal.getAttributes());
         this.panes.add(this.active);
         this.name = "win" + (Tmux.this.windowsId < 10 ? "0" + Tmux.this.windowsId : Tmux.this.windowsId);
         Integer var4 = Tmux.this.windowsId;
         Tmux.this.windowsId = Tmux.this.windowsId + 1;
      }

      public String getName() {
         return this.name;
      }

      public List<Tmux.VirtualConsole> getPanes() {
         return this.panes;
      }

      public Tmux.VirtualConsole getActive() {
         return this.active;
      }

      public void remove(Tmux.VirtualConsole console) {
         this.panes.remove(console);
         if (!this.panes.isEmpty()) {
            console.layout.remove();
            if (this.active == console) {
               this.active = this.panes.stream().sorted(Comparator.<Tmux.VirtualConsole>comparingInt(p -> p.active).reversed()).findFirst().get();
            }

            this.layout = this.active.layout;

            while (this.layout.parent != null) {
               this.layout = this.layout.parent;
            }

            this.layout.fixOffsets();
            this.layout.fixPanes(Tmux.this.size.getColumns(), Tmux.this.size.getRows());
         }
      }

      public void handleResize() {
         this.layout.resize(Tmux.this.size.getColumns(), Tmux.this.size.getRows() - 1);
         this.panes.forEach(vc -> {
            if (vc.width() != vc.layout.sx || vc.height() != vc.layout.sy || vc.left() != vc.layout.xoff || vc.top() != vc.layout.yoff) {
               vc.resize(vc.layout.xoff, vc.layout.yoff, vc.layout.sx, vc.layout.sy);
               Tmux.this.display.clear();
            }
         });
      }

      public Tmux.VirtualConsole splitPane(Options opt) throws IOException {
         Tmux.Layout.Type type = opt.isSet("horizontal") ? Tmux.Layout.Type.LeftRight : Tmux.Layout.Type.TopBottom;
         if (this.layout.type == Tmux.Layout.Type.WindowPane) {
            Tmux.Layout p = new Tmux.Layout();
            p.sx = this.layout.sx;
            p.sy = this.layout.sy;
            p.type = type;
            p.cells.add(this.layout);
            this.layout.parent = p;
            this.layout = p;
         }

         Tmux.Layout cell = this.active.layout();
         if (opt.isSet("f")) {
            while (cell.parent != this.layout) {
               cell = cell.parent;
            }
         }

         int size = -1;
         if (opt.isSet("size")) {
            size = opt.getNumber("size");
         } else if (opt.isSet("perc")) {
            int p = opt.getNumber("perc");
            if (type == Tmux.Layout.Type.TopBottom) {
               size = cell.sy * p / 100;
            } else {
               size = cell.sx * p / 100;
            }
         }

         Tmux.Layout newCell = cell.split(type, size, opt.isSet("before"));
         if (newCell == null) {
            Tmux.this.err.println("create pane failed: pane too small");
            return null;
         } else {
            int var10002 = this.paneId.incrementAndGet();
            String var10003 = Tmux.this.term;
            int var10004 = newCell.xoff;
            int var10005 = newCell.yoff;
            int var10006 = newCell.sx;
            int var10007 = newCell.sy;
            Tmux var10008 = this.tmux;
            Objects.requireNonNull(this.tmux);
            Tmux var7 = var10008;
            Runnable var11 = () -> var7.setDirty();
            Tmux var10009 = this.tmux;
            Objects.requireNonNull(this.tmux);
            var7 = var10009;
            Tmux.VirtualConsole newConsole = new Tmux.VirtualConsole(
               var10002, var10003, var10004, var10005, var10006, var10007, var11, x$0 -> var7.close(x$0), newCell
            );
            this.panes.add(newConsole);
            newConsole.getConsole().setAttributes(Tmux.this.terminal.getAttributes());
            if (!opt.isSet("d")) {
               this.active = newConsole;
               this.active.active = this.lastActive++;
            }

            return newConsole;
         }
      }

      public boolean selectPane(Options opt) {
         Tmux.VirtualConsole prevActive = this.active;
         if (opt.isSet("L")) {
            this.active = this.panes
               .stream()
               .filter(c -> c.bottom() > this.active.top() && c.top() < this.active.bottom())
               .filter(c -> c != this.active)
               .sorted(
                  Comparator.<Tmux.VirtualConsole>comparingInt(c -> c.left() > this.active.left() ? c.left() : c.left() + Tmux.this.size.getColumns())
                     .reversed()
                     .thenComparingInt(c -> -c.active)
               )
               .findFirst()
               .orElse(this.active);
         } else if (opt.isSet("R")) {
            this.active = this.panes
               .stream()
               .filter(c -> c.bottom() > this.active.top() && c.top() < this.active.bottom())
               .filter(c -> c != this.active)
               .sorted(
                  Comparator.<Tmux.VirtualConsole>comparingInt(c -> c.left() > this.active.left() ? c.left() : c.left() + Tmux.this.size.getColumns())
                     .thenComparingInt(c -> -c.active)
               )
               .findFirst()
               .orElse(this.active);
         } else if (opt.isSet("U")) {
            this.active = this.panes
               .stream()
               .filter(c -> c.right() > this.active.left() && c.left() < this.active.right())
               .filter(c -> c != this.active)
               .sorted(
                  Comparator.<Tmux.VirtualConsole>comparingInt(c -> c.top() > this.active.top() ? c.top() : c.top() + Tmux.this.size.getRows())
                     .reversed()
                     .thenComparingInt(c -> -c.active)
               )
               .findFirst()
               .orElse(this.active);
         } else if (opt.isSet("D")) {
            this.active = this.panes
               .stream()
               .filter(c -> c.right() > this.active.left() && c.left() < this.active.right())
               .filter(c -> c != this.active)
               .sorted(
                  Comparator.<Tmux.VirtualConsole>comparingInt(c -> c.top() > this.active.top() ? c.top() : c.top() + Tmux.this.size.getRows())
                     .thenComparingInt(c -> -c.active)
               )
               .findFirst()
               .orElse(this.active);
         }

         boolean out = false;
         if (prevActive != this.active) {
            this.active.active = this.lastActive++;
            out = true;
         }

         return out;
      }

      public void resizePane(Options opt, int adjust) {
         if (opt.isSet("width")) {
            int x = opt.getNumber("width");
            this.active.layout().resizeTo(Tmux.Layout.Type.LeftRight, x);
         }

         if (opt.isSet("height")) {
            int y = opt.getNumber("height");
            this.active.layout().resizeTo(Tmux.Layout.Type.TopBottom, y);
         }

         if (opt.isSet("L")) {
            this.active.layout().resize(Tmux.Layout.Type.LeftRight, -adjust, true);
         } else if (opt.isSet("R")) {
            this.active.layout().resize(Tmux.Layout.Type.LeftRight, adjust, true);
         } else if (opt.isSet("U")) {
            this.active.layout().resize(Tmux.Layout.Type.TopBottom, -adjust, true);
         } else if (opt.isSet("D")) {
            this.active.layout().resize(Tmux.Layout.Type.TopBottom, adjust, true);
         }
      }
   }
}
