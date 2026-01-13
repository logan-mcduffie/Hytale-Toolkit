package org.jline.builtins;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.reader.Editor;
import org.jline.terminal.Attributes;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.MouseSupport;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Display;
import org.jline.utils.InfoCmp;
import org.jline.utils.Status;
import org.mozilla.universalchardet.UniversalDetector;

public class Nano implements Editor {
   protected final Terminal terminal;
   protected final Display display;
   protected final BindingReader bindingReader;
   protected final Size size;
   protected final Path root;
   protected final int vsusp;
   private final List<Path> syntaxFiles = new ArrayList<>();
   protected KeyMap<Nano.Operation> keys;
   public String title = "JLine Nano 3.0.0";
   public boolean printLineNumbers = false;
   public boolean wrapping = false;
   public boolean smoothScrolling = true;
   public boolean mouseSupport = false;
   public Terminal.MouseTracking mouseTracking = Terminal.MouseTracking.Off;
   public boolean oneMoreLine = true;
   public boolean constantCursor = false;
   public boolean quickBlank = false;
   public int tabs = 4;
   public String brackets = "\"’)>]}";
   public String matchBrackets = "(<[{)>]}";
   public String punct = "!.?";
   public String quoteStr = "^([ \\t]*[#:>\\|}])+";
   private boolean restricted = false;
   private String syntaxName;
   private boolean writeBackup = false;
   private boolean atBlanks = false;
   private boolean view = false;
   private boolean cut2end = false;
   private boolean tempFile = false;
   private String historyLog = null;
   private boolean tabsToSpaces = false;
   private boolean autoIndent = false;
   protected final List<Nano.Buffer> buffers = new ArrayList<>();
   protected int bufferIndex;
   protected Nano.Buffer buffer;
   protected String message;
   protected String errorMessage = null;
   protected int nbBindings = 0;
   protected LinkedHashMap<String, String> shortcuts;
   protected String editMessage;
   protected final StringBuilder editBuffer = new StringBuilder();
   protected boolean searchCaseSensitive;
   protected boolean searchRegexp;
   protected boolean searchBackwards;
   protected String searchTerm;
   protected int matchedLength = -1;
   protected Nano.PatternHistory patternHistory = new Nano.PatternHistory(null);
   protected Nano.WriteMode writeMode = Nano.WriteMode.WRITE;
   protected List<String> cutbuffer = new ArrayList<>();
   protected boolean mark = false;
   protected boolean highlight = true;
   private boolean searchToReplace = false;
   protected boolean readNewBuffer = true;
   private boolean nanorcIgnoreErrors;
   private final boolean windowsTerminal;
   private boolean insertHelp = false;
   private boolean help = false;
   private Nano.Box suggestionBox;
   private Map<AttributedString, List<AttributedString>> suggestions;
   private int mouseX;
   private int mouseY;

   public static String[] usage() {
      return new String[]{
         "nano -  edit files",
         "Usage: nano [OPTIONS] [FILES]",
         "  -? --help                    Show help",
         "  -B --backup                  When saving a file, back up the previous version of it, using the current filename",
         "                               suffixed with a tilde (~).",
         "  -I --ignorercfiles           Don't look at the system's nanorc nor at the user's nanorc.",
         "  -Q --quotestr=regex          Set the regular expression for matching the quoting part of a line.",
         "  -T --tabsize=number          Set the size (width) of a tab to number columns.",
         "  -U --quickblank              Do quick status-bar blanking: status-bar messages will disappear after 1 keystroke.",
         "  -c --constantshow            Constantly show the cursor position on the status bar.",
         "  -e --emptyline               Do not use the line below the title bar, leaving it entirely blank.",
         "  -j --jumpyscrolling          Scroll the buffer contents per half-screen instead of per line.",
         "  -l --linenumbers             Display line numbers to the left of the text area.",
         "  -m --mouse                   Enable mouse support, if available for your system.",
         "  -$ --softwrap                Enable 'soft wrapping'. ",
         "  -a --atblanks                Wrap lines at whitespace instead of always at the edge of the screen.",
         "  -R --restricted              Restricted mode: don't allow suspending; don't allow a file to be appended to,",
         "                               prepended to, or saved under a different name if it already has one;",
         "                               and don't use backup files.",
         "  -Y --syntax=name             The name of the syntax highlighting to use.",
         "  -z --suspend                 Enable the ability to suspend nano using the system's suspend keystroke (usually ^Z).",
         "  -v --view                    Don't allow the contents of the file to be altered: read-only mode.",
         "  -k --cutfromcursor           Make the 'Cut Text' command cut from the current cursor position to the end of the line",
         "  -t --tempfile                Save a changed buffer without prompting (when exiting with ^X).",
         "  -H --historylog=name         Log search strings to file, so they can be retrieved in later sessions",
         "  -E --tabstospaces            Convert typed tabs to spaces.",
         "  -i --autoindent              Indent new lines to the previous line's indentation."
      };
   }

   public Nano(Terminal terminal, File root) {
      this(terminal, root.toPath());
   }

   public Nano(Terminal terminal, Path root) {
      this(terminal, root, null);
   }

   public Nano(Terminal terminal, Path root, Options opts) {
      this(terminal, root, opts, null);
   }

   public Nano(Terminal terminal, Path root, Options opts, ConfigurationPath configPath) {
      this.terminal = terminal;
      this.windowsTerminal = terminal.getClass().getSimpleName().endsWith("WinSysTerminal");
      this.root = root;
      this.display = new Display(terminal, true);
      this.bindingReader = new BindingReader(terminal.reader());
      this.size = new Size();
      Attributes attrs = terminal.getAttributes();
      this.vsusp = attrs.getControlChar(Attributes.ControlChar.VSUSP);
      if (this.vsusp > 0) {
         attrs.setControlChar(Attributes.ControlChar.VSUSP, 0);
         terminal.setAttributes(attrs);
      }

      Path nanorc = configPath != null ? configPath.getConfig("jnanorc") : null;
      boolean ignorercfiles = opts != null && opts.isSet("ignorercfiles");
      if (nanorc != null && !ignorercfiles) {
         try {
            this.parseConfig(nanorc);
         } catch (IOException var14) {
            this.errorMessage = "Encountered error while reading config file: " + nanorc;
         }
      } else if (new File("/usr/share/nano").exists() && !ignorercfiles) {
         PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:/usr/share/nano/*.nanorc");

         try {
            Stream<Path> pathStream = Files.walk(Paths.get("/usr/share/nano"));

            try {
               pathStream.filter(pathMatcher::matches).forEach(this.syntaxFiles::add);
               this.nanorcIgnoreErrors = true;
            } catch (Throwable var15) {
               if (pathStream != null) {
                  try {
                     pathStream.close();
                  } catch (Throwable var13) {
                     var15.addSuppressed(var13);
                  }
               }

               throw var15;
            }

            if (pathStream != null) {
               pathStream.close();
            }
         } catch (IOException var16) {
            this.errorMessage = "Encountered error while reading nanorc files";
         }
      }

      if (opts != null) {
         this.restricted = opts.isSet("restricted");
         this.syntaxName = null;
         if (opts.isSet("syntax")) {
            this.syntaxName = opts.get("syntax");
            this.nanorcIgnoreErrors = false;
         }

         if (opts.isSet("backup")) {
            this.writeBackup = true;
         }

         if (opts.isSet("quotestr")) {
            this.quoteStr = opts.get("quotestr");
         }

         if (opts.isSet("tabsize")) {
            this.tabs = opts.getNumber("tabsize");
         }

         if (opts.isSet("quickblank")) {
            this.quickBlank = true;
         }

         if (opts.isSet("constantshow")) {
            this.constantCursor = true;
         }

         if (opts.isSet("emptyline")) {
            this.oneMoreLine = false;
         }

         if (opts.isSet("jumpyscrolling")) {
            this.smoothScrolling = false;
         }

         if (opts.isSet("linenumbers")) {
            this.printLineNumbers = true;
         }

         if (opts.isSet("mouse")) {
            this.mouseSupport = true;
         }

         if (opts.isSet("softwrap")) {
            this.wrapping = true;
         }

         if (opts.isSet("atblanks")) {
            this.atBlanks = true;
         }

         if (opts.isSet("suspend")) {
            this.enableSuspension();
         }

         if (opts.isSet("view")) {
            this.view = true;
         }

         if (opts.isSet("cutfromcursor")) {
            this.cut2end = true;
         }

         if (opts.isSet("tempfile")) {
            this.tempFile = true;
         }

         if (opts.isSet("historylog")) {
            this.historyLog = opts.get("historyLog");
         }

         if (opts.isSet("tabstospaces")) {
            this.tabsToSpaces = true;
         }

         if (opts.isSet("autoindent")) {
            this.autoIndent = true;
         }
      }

      this.bindKeys();
      if (configPath != null && this.historyLog != null) {
         try {
            this.patternHistory = new Nano.PatternHistory(configPath.getUserConfig(this.historyLog, true));
         } catch (IOException var12) {
            this.errorMessage = "Encountered error while reading pattern-history file: " + this.historyLog;
         }
      }
   }

   private void parseConfig(Path file) throws IOException {
      BufferedReader reader = Files.newBufferedReader(file);

      String line;
      try {
         while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#")) {
               List<String> parts = SyntaxHighlighter.RuleSplitter.split(line);
               if (parts.get(0).equals("include")) {
                  SyntaxHighlighter.nanorcInclude(file, parts.get(1), this.syntaxFiles);
               } else if (parts.get(0).equals("theme")) {
                  SyntaxHighlighter.nanorcTheme(file, parts.get(1), this.syntaxFiles);
               } else if (parts.size() != 2 || !parts.get(0).equals("set") && !parts.get(0).equals("unset")) {
                  if (parts.size() == 3 && parts.get(0).equals("set")) {
                     String option = parts.get(1);
                     String val = parts.get(2);
                     switch (option) {
                        case "quotestr":
                           this.quoteStr = val;
                           break;
                        case "punct":
                           this.punct = val;
                           break;
                        case "matchbrackets":
                           this.matchBrackets = val;
                           break;
                        case "brackets":
                           this.brackets = val;
                           break;
                        case "historylog":
                           this.historyLog = val;
                           break;
                        default:
                           this.errorMessage = "Nano config: Unknown or unsupported configuration option " + option;
                     }
                  } else if (!parts.get(0).equals("bind") && !parts.get(0).equals("unbind")) {
                     this.errorMessage = "Nano config: Bad configuration '" + line + "'";
                  } else {
                     this.errorMessage = "Nano config: Key bindings can not be changed!";
                  }
               } else {
                  String option = parts.get(1);
                  boolean val = parts.get(0).equals("set");
                  switch (option) {
                     case "linenumbers":
                        this.printLineNumbers = val;
                        break;
                     case "jumpyscrolling":
                        this.smoothScrolling = !val;
                        break;
                     case "smooth":
                        this.smoothScrolling = val;
                        break;
                     case "softwrap":
                        this.wrapping = val;
                        break;
                     case "mouse":
                        this.mouseSupport = val;
                        break;
                     case "emptyline":
                        this.oneMoreLine = val;
                        break;
                     case "morespace":
                        this.oneMoreLine = !val;
                        break;
                     case "constantshow":
                        this.constantCursor = val;
                        break;
                     case "quickblank":
                        this.quickBlank = val;
                        break;
                     case "atblanks":
                        this.atBlanks = val;
                        break;
                     case "suspend":
                        this.enableSuspension();
                        break;
                     case "view":
                        this.view = val;
                        break;
                     case "cutfromcursor":
                        this.cut2end = val;
                        break;
                     case "tempfile":
                        this.tempFile = val;
                        break;
                     case "tabstospaces":
                        this.tabsToSpaces = val;
                        break;
                     case "autoindent":
                        this.autoIndent = val;
                        break;
                     default:
                        this.errorMessage = "Nano config: Unknown or unsupported configuration option " + option;
                  }
               }
            }
         }
      } catch (Throwable var10) {
         if (reader != null) {
            try {
               reader.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (reader != null) {
         reader.close();
      }
   }

   @Override
   public void setRestricted(boolean restricted) {
      this.restricted = restricted;
   }

   public void open(String... files) throws IOException {
      this.open(Arrays.asList(files));
   }

   @Override
   public void open(List<String> files) throws IOException {
      for (String file : files) {
         file = file.startsWith("~") ? file.replace("~", System.getProperty("user.home")) : file;
         if (!file.contains("*") && !file.contains("?")) {
            this.buffers.add(new Nano.Buffer(file));
         } else {
            for (Path p : Commands.findFiles(this.root, file)) {
               this.buffers.add(new Nano.Buffer(p.toString()));
            }
         }
      }
   }

   @Override
   public void run() throws IOException {
      if (this.buffers.isEmpty()) {
         this.buffers.add(new Nano.Buffer(null));
      }

      this.buffer = this.buffers.get(this.bufferIndex);
      Attributes attributes = this.terminal.getAttributes();
      Attributes newAttr = new Attributes(attributes);
      if (this.vsusp > 0) {
         attributes.setControlChar(Attributes.ControlChar.VSUSP, this.vsusp);
      }

      newAttr.setLocalFlags(EnumSet.of(Attributes.LocalFlag.ICANON, Attributes.LocalFlag.ECHO, Attributes.LocalFlag.IEXTEN, Attributes.LocalFlag.ISIG), false);
      newAttr.setInputFlags(EnumSet.of(Attributes.InputFlag.IXON, Attributes.InputFlag.ICRNL, Attributes.InputFlag.INLCR), false);
      newAttr.setControlChar(Attributes.ControlChar.VMIN, 1);
      newAttr.setControlChar(Attributes.ControlChar.VTIME, 0);
      newAttr.setControlChar(Attributes.ControlChar.VINTR, 0);
      this.terminal.setAttributes(newAttr);
      this.terminal.puts(InfoCmp.Capability.enter_ca_mode);
      this.terminal.puts(InfoCmp.Capability.keypad_xmit);
      if (this.mouseSupport) {
         this.mouseTracking = this.terminal.getCurrentMouseTracking();
         this.terminal.trackMouse(Terminal.MouseTracking.Any);
      }

      this.shortcuts = this.standardShortcuts();
      Terminal.SignalHandler prevHandler = null;
      Status status = Status.getStatus(this.terminal, false);

      try {
         this.size.copy(this.terminal.getSize());
         if (status != null) {
            status.suspend();
         }

         this.buffer.open();
         if (this.errorMessage != null) {
            this.setMessage(this.errorMessage);
            this.errorMessage = null;
         } else if (this.buffer.file != null) {
            this.setMessage("Read " + this.buffer.lines.size() + " lines");
         }

         this.display.clear();
         this.display.reset();
         this.display.resize(this.size.getRows(), this.size.getColumns());
         prevHandler = this.terminal.handle(Terminal.Signal.WINCH, this::handle);
         this.display();

         while (true) {
            Nano.Operation op;
            switch (op = this.readOperation(this.keys)) {
               case QUIT:
                  if (this.help) {
                     this.resetSuggestion();
                  } else if (this.quit()) {
                     return;
                  }
                  break;
               case WRITE:
                  this.write();
                  break;
               case READ:
                  this.read();
                  break;
               case GOTO:
                  this.gotoLine();
                  this.curPos();
                  break;
               case FIND:
               case NEXT_WORD:
               case PREV_WORD:
               case CASE_SENSITIVE:
               case BACKWARDS:
               case REGEXP:
               case ACCEPT:
               case CANCEL:
               case TOGGLE_REPLACE:
               case MAC_FORMAT:
               case DOS_FORMAT:
               case APPEND_MODE:
               case PREPEND_MODE:
               case BACKUP:
               case TO_FILES:
               case YES:
               case NO:
               case ALL:
               case NEW_BUFFER:
               case EXECUTE:
               case JUSTIFY_PARAGRAPH:
               case TO_SPELL:
               case INDENT:
               case UNINDENT:
               case BEGINNING_OF_PARAGRAPH:
               case END_OF_PARAGRAPH:
               case JUSTIFY_FILE:
               case COUNT:
               case WHITESPACE:
               case SMART_HOME_KEY:
               default:
                  this.setMessage("Unsupported " + op.name().toLowerCase().replace('_', '-'));
                  break;
               case WRAP:
                  this.wrap();
                  break;
               case NUMBERS:
                  this.numbers();
                  break;
               case SMOOTH_SCROLLING:
                  this.smoothScrolling();
                  break;
               case MOUSE_SUPPORT:
                  this.mouseSupport();
                  break;
               case ONE_MORE_LINE:
                  this.oneMoreLine();
                  break;
               case CLEAR_SCREEN:
                  this.clearScreen();
                  break;
               case UP:
                  if (this.help && this.suggestionBox != null) {
                     this.suggestionBox.up();
                  } else {
                     this.buffer.moveUp(1);
                  }
                  break;
               case DOWN:
                  if (this.help && this.suggestionBox != null) {
                     this.suggestionBox.down();
                  } else {
                     this.buffer.moveDown(1);
                  }
                  break;
               case LEFT:
                  this.buffer.moveLeft(1);
                  if (this.help) {
                     this.resetSuggestion();
                  }
                  break;
               case RIGHT:
                  this.buffer.moveRight(1);
                  if (this.help) {
                     this.resetSuggestion();
                  }
                  break;
               case INSERT:
                  if (this.help) {
                     this.insertHelp = true;
                  } else {
                     this.buffer.insert(this.bindingReader.getLastBinding());
                  }
                  break;
               case BACKSPACE:
                  this.buffer.backspace(1);
                  break;
               case NEXT_BUFFER:
                  this.nextBuffer();
                  break;
               case PREV_BUFFER:
                  this.prevBuffer();
                  break;
               case HELP:
                  this.help("nano-main-help.txt");
                  break;
               case NEXT_PAGE:
                  this.buffer.nextPage();
                  break;
               case PREV_PAGE:
                  this.buffer.prevPage();
                  break;
               case SCROLL_UP:
                  this.buffer.scrollUp(1);
                  break;
               case SCROLL_DOWN:
                  this.buffer.scrollDown(1);
                  break;
               case LSP_SUGGESTION:
                  this.help = true;
                  break;
               case BEGINNING_OF_LINE:
                  this.buffer.beginningOfLine();
                  break;
               case END_OF_LINE:
                  this.buffer.endOfLine();
                  break;
               case FIRST_LINE:
                  this.buffer.firstLine();
                  break;
               case LAST_LINE:
                  this.buffer.lastLine();
                  break;
               case CUR_POS:
                  this.curPos();
                  break;
               case SEARCH:
                  this.searchToReplace = false;
                  this.searchAndReplace();
                  break;
               case NEXT_SEARCH:
                  this.buffer.nextSearch();
                  break;
               case MATCHING:
                  this.buffer.matching();
                  break;
               case VERBATIM:
                  this.buffer.insert(new String(Character.toChars(this.bindingReader.readCharacter())));
                  break;
               case DELETE:
                  this.buffer.delete(1);
                  break;
               case CUT:
                  this.buffer.cut();
                  break;
               case REPLACE:
                  this.searchToReplace = true;
                  this.searchAndReplace();
                  break;
               case MARK:
                  this.mark = !this.mark;
                  this.setMessage("Mark " + (this.mark ? "Set" : "Unset"));
                  this.buffer.mark();
                  break;
               case COPY:
                  this.buffer.copy();
                  break;
               case CUT_TO_END:
                  this.buffer.cut(true);
                  break;
               case CONSTANT_CURSOR:
                  this.constantCursor();
                  break;
               case HIGHLIGHT:
                  this.highlight = !this.highlight;
                  this.setMessage("Highlight " + (this.highlight ? "enabled" : "disabled"));
                  break;
               case AUTO_INDENT:
                  this.autoIndent = !this.autoIndent;
                  this.setMessage("Auto indent " + (this.autoIndent ? "enabled" : "disabled"));
                  break;
               case CUT_TO_END_TOGGLE:
                  this.cut2end = !this.cut2end;
                  this.setMessage("Cut to end " + (this.cut2end ? "enabled" : "disabled"));
                  break;
               case TABS_TO_SPACE:
                  this.tabsToSpaces = !this.tabsToSpaces;
                  this.setMessage("Conversion of typed tabs to spaces " + (this.tabsToSpaces ? "enabled" : "disabled"));
                  break;
               case UNCUT:
                  this.buffer.uncut();
                  break;
               case MOUSE_EVENT:
                  this.mouseEvent();
                  break;
               case TOGGLE_SUSPENSION:
                  this.toggleSuspension();
            }

            this.display();
         }
      } finally {
         if (this.mouseSupport) {
            this.terminal.trackMouse(this.mouseTracking);
         }

         if (!this.terminal.puts(InfoCmp.Capability.exit_ca_mode)) {
            this.terminal.puts(InfoCmp.Capability.clear_screen);
         }

         this.terminal.puts(InfoCmp.Capability.keypad_local);
         this.terminal.flush();
         this.terminal.setAttributes(attributes);
         this.terminal.handle(Terminal.Signal.WINCH, prevHandler);
         if (status != null) {
            status.restore();
         }

         this.patternHistory.persist();
      }
   }

   private void resetSuggestion() {
      this.suggestions = null;
      this.suggestionBox = null;
      this.insertHelp = false;
      this.help = false;
   }

   private int editInputBuffer(Nano.Operation operation, int curPos) {
      switch (operation) {
         case LEFT:
            if (curPos > 0) {
               curPos--;
            }
            break;
         case RIGHT:
            if (curPos < this.editBuffer.length()) {
               curPos++;
            }
            break;
         case INSERT:
            this.editBuffer.insert(curPos++, this.bindingReader.getLastBinding());
            break;
         case BACKSPACE:
            if (curPos > 0) {
               this.editBuffer.deleteCharAt(--curPos);
            }
      }

      return curPos;
   }

   boolean write() throws IOException {
      KeyMap<Nano.Operation> writeKeyMap = new KeyMap<>();
      if (!this.restricted) {
         writeKeyMap.setUnicode(Nano.Operation.INSERT);

         for (char i = ' '; i < 256; i++) {
            writeKeyMap.bind(Nano.Operation.INSERT, Character.toString(i));
         }

         for (char i = 'A'; i <= 'Z'; i++) {
            writeKeyMap.bind(Nano.Operation.DO_LOWER_CASE, KeyMap.alt(i));
         }

         writeKeyMap.bind(Nano.Operation.BACKSPACE, KeyMap.del());
         writeKeyMap.bind(Nano.Operation.APPEND_MODE, KeyMap.alt('a'));
         writeKeyMap.bind(Nano.Operation.PREPEND_MODE, KeyMap.alt('p'));
         writeKeyMap.bind(Nano.Operation.BACKUP, KeyMap.alt('b'));
         writeKeyMap.bind(Nano.Operation.TO_FILES, KeyMap.ctrl('T'));
      }

      writeKeyMap.bind(Nano.Operation.MAC_FORMAT, KeyMap.alt('m'));
      writeKeyMap.bind(Nano.Operation.DOS_FORMAT, KeyMap.alt('d'));
      writeKeyMap.bind(Nano.Operation.ACCEPT, "\r");
      writeKeyMap.bind(Nano.Operation.CANCEL, KeyMap.ctrl('C'));
      writeKeyMap.bind(Nano.Operation.HELP, KeyMap.ctrl('G'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f1));
      writeKeyMap.bind(Nano.Operation.MOUSE_EVENT, MouseSupport.keys(this.terminal));
      writeKeyMap.bind(Nano.Operation.TOGGLE_SUSPENSION, KeyMap.alt('z'));
      writeKeyMap.bind(Nano.Operation.RIGHT, KeyMap.key(this.terminal, InfoCmp.Capability.key_right));
      writeKeyMap.bind(Nano.Operation.LEFT, KeyMap.key(this.terminal, InfoCmp.Capability.key_left));
      this.editMessage = this.getWriteMessage();
      this.editBuffer.setLength(0);
      this.editBuffer.append(this.buffer.file == null ? "" : this.buffer.file);
      int curPos = this.editBuffer.length();
      this.shortcuts = this.writeShortcuts();
      this.display(curPos);

      while (true) {
         Nano.Operation op = this.readOperation(writeKeyMap);
         switch (op) {
            case HELP:
               this.help("nano-write-help.txt");
               break;
            case ACCEPT:
               this.editMessage = null;
               if (this.save(this.editBuffer.toString())) {
                  this.shortcuts = this.standardShortcuts();
                  return true;
               }

               return false;
            case CANCEL:
               this.editMessage = null;
               this.shortcuts = this.standardShortcuts();
               return false;
            case MAC_FORMAT:
               this.buffer.format = this.buffer.format == Nano.WriteFormat.MAC ? Nano.WriteFormat.UNIX : Nano.WriteFormat.MAC;
               break;
            case DOS_FORMAT:
               this.buffer.format = this.buffer.format == Nano.WriteFormat.DOS ? Nano.WriteFormat.UNIX : Nano.WriteFormat.DOS;
               break;
            case APPEND_MODE:
               this.writeMode = this.writeMode == Nano.WriteMode.APPEND ? Nano.WriteMode.WRITE : Nano.WriteMode.APPEND;
               break;
            case PREPEND_MODE:
               this.writeMode = this.writeMode == Nano.WriteMode.PREPEND ? Nano.WriteMode.WRITE : Nano.WriteMode.PREPEND;
               break;
            case BACKUP:
               this.writeBackup = !this.writeBackup;
               break;
            case MOUSE_EVENT:
               this.mouseEvent();
               break;
            case TOGGLE_SUSPENSION:
               this.toggleSuspension();
               break;
            default:
               curPos = this.editInputBuffer(op, curPos);
         }

         this.editMessage = this.getWriteMessage();
         this.display(curPos);
      }
   }

   private Nano.Operation readOperation(KeyMap<Nano.Operation> keymap) {
      while (true) {
         Nano.Operation op = this.bindingReader.readBinding(keymap);
         if (op != Nano.Operation.DO_LOWER_CASE) {
            return op;
         }

         this.bindingReader.runMacro(this.bindingReader.getLastBinding().toLowerCase());
      }
   }

   private boolean save(String name) throws IOException {
      Path orgPath = this.buffer.file != null ? this.root.resolve(this.buffer.file) : null;
      Path newPath = this.root.resolve(name);
      boolean isSame = orgPath != null && Files.exists(orgPath) && Files.exists(newPath) && Files.isSameFile(orgPath, newPath);
      if (!isSame && Files.exists(Paths.get(name)) && this.writeMode == Nano.WriteMode.WRITE) {
         Nano.Operation op = this.getYNC("File exists, OVERWRITE ? ");
         if (op != Nano.Operation.YES) {
            return false;
         }
      } else if (!Files.exists(newPath)) {
         Files.createFile(newPath);
      }

      Path t = Files.createTempFile("jline-", ".temp");

      boolean w;
      try {
         OutputStream os = Files.newOutputStream(t, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);

         boolean var20;
         try {
            if (this.writeMode == Nano.WriteMode.APPEND && Files.isReadable(newPath)) {
               Files.copy(newPath, os);
            }

            Writer wx = new OutputStreamWriter(os, this.buffer.charset);

            for (int i = 0; i < this.buffer.lines.size(); i++) {
               wx.write(this.buffer.lines.get(i));
               switch (this.buffer.format) {
                  case UNIX:
                     wx.write("\n");
                     break;
                  case DOS:
                     wx.write("\r\n");
                     break;
                  case MAC:
                     wx.write("\r");
               }
            }

            wx.flush();
            if (this.writeMode == Nano.WriteMode.PREPEND && Files.isReadable(newPath)) {
               Files.copy(newPath, os);
            }

            if (this.writeBackup) {
               Files.move(newPath, newPath.resolveSibling(newPath.getFileName().toString() + "~"), StandardCopyOption.REPLACE_EXISTING);
            }

            Files.move(t, newPath, StandardCopyOption.REPLACE_EXISTING);
            if (this.writeMode == Nano.WriteMode.WRITE) {
               this.buffer.file = name;
               this.buffer.dirty = false;
            }

            this.setMessage("Wrote " + this.buffer.lines.size() + " lines");
            var20 = true;
         } catch (Throwable var15) {
            if (os != null) {
               try {
                  os.close();
               } catch (Throwable var14) {
                  var15.addSuppressed(var14);
               }
            }

            throw var15;
         }

         if (os != null) {
            os.close();
         }

         return var20;
      } catch (IOException var16) {
         this.setMessage("Error writing " + name + ": " + var16);
         w = false;
      } finally {
         Files.deleteIfExists(t);
         this.writeMode = Nano.WriteMode.WRITE;
      }

      return w;
   }

   private Nano.Operation getYNC(String message) {
      return this.getYNC(message, false);
   }

   private Nano.Operation getYNC(String message, boolean andAll) {
      String oldEditMessage = this.editMessage;
      String oldEditBuffer = this.editBuffer.toString();
      LinkedHashMap<String, String> oldShortcuts = this.shortcuts;

      Nano.Operation var7;
      try {
         this.editMessage = message;
         this.editBuffer.setLength(0);
         KeyMap<Nano.Operation> yncKeyMap = new KeyMap<>();
         yncKeyMap.bind(Nano.Operation.YES, "y", "Y");
         if (andAll) {
            yncKeyMap.bind(Nano.Operation.ALL, "a", "A");
         }

         yncKeyMap.bind(Nano.Operation.NO, "n", "N");
         yncKeyMap.bind(Nano.Operation.CANCEL, KeyMap.ctrl('C'));
         this.shortcuts = new LinkedHashMap<>();
         this.shortcuts.put(" Y", "Yes");
         if (andAll) {
            this.shortcuts.put(" A", "All");
         }

         this.shortcuts.put(" N", "No");
         this.shortcuts.put("^C", "Cancel");
         this.display();
         var7 = this.readOperation(yncKeyMap);
      } finally {
         this.editMessage = oldEditMessage;
         this.editBuffer.append(oldEditBuffer);
         this.shortcuts = oldShortcuts;
      }

      return var7;
   }

   private String getWriteMessage() {
      StringBuilder sb = new StringBuilder();
      sb.append("File Name to ");
      switch (this.writeMode) {
         case WRITE:
            sb.append("Write");
            break;
         case APPEND:
            sb.append("Append");
            break;
         case PREPEND:
            sb.append("Prepend");
      }

      switch (this.buffer.format) {
         case UNIX:
         default:
            break;
         case DOS:
            sb.append(" [DOS Format]");
            break;
         case MAC:
            sb.append(" [Mac Format]");
      }

      if (this.writeBackup) {
         sb.append(" [Backup]");
      }

      sb.append(": ");
      return sb.toString();
   }

   void read() {
      KeyMap<Nano.Operation> readKeyMap = new KeyMap<>();
      readKeyMap.setUnicode(Nano.Operation.INSERT);

      for (char i = ' '; i < 256; i++) {
         readKeyMap.bind(Nano.Operation.INSERT, Character.toString(i));
      }

      for (char i = 'A'; i <= 'Z'; i++) {
         readKeyMap.bind(Nano.Operation.DO_LOWER_CASE, KeyMap.alt(i));
      }

      readKeyMap.bind(Nano.Operation.BACKSPACE, KeyMap.del());
      readKeyMap.bind(Nano.Operation.NEW_BUFFER, KeyMap.alt('f'));
      readKeyMap.bind(Nano.Operation.TO_FILES, KeyMap.ctrl('T'));
      readKeyMap.bind(Nano.Operation.EXECUTE, KeyMap.ctrl('X'));
      readKeyMap.bind(Nano.Operation.ACCEPT, "\r");
      readKeyMap.bind(Nano.Operation.CANCEL, KeyMap.ctrl('C'));
      readKeyMap.bind(Nano.Operation.HELP, KeyMap.ctrl('G'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f1));
      readKeyMap.bind(Nano.Operation.MOUSE_EVENT, MouseSupport.keys(this.terminal));
      readKeyMap.bind(Nano.Operation.RIGHT, KeyMap.key(this.terminal, InfoCmp.Capability.key_right));
      readKeyMap.bind(Nano.Operation.LEFT, KeyMap.key(this.terminal, InfoCmp.Capability.key_left));
      this.editMessage = this.getReadMessage();
      this.editBuffer.setLength(0);
      int curPos = this.editBuffer.length();
      this.shortcuts = this.readShortcuts();
      this.display(curPos);

      while (true) {
         Nano.Operation op = this.readOperation(readKeyMap);
         switch (op) {
            case HELP:
               this.help("nano-read-help.txt");
               break;
            case ACCEPT:
               this.editMessage = null;
               String file = this.editBuffer.toString();
               boolean empty = file.isEmpty();
               Path p = empty ? null : this.root.resolve(file);
               if (!this.readNewBuffer && !empty && !Files.exists(p)) {
                  this.setMessage("\"" + file + "\" not found");
               } else if (!empty && Files.isDirectory(p)) {
                  this.setMessage("\"" + file + "\" is a directory");
               } else if (!empty && !Files.isRegularFile(p)) {
                  this.setMessage("\"" + file + "\" is not a regular file");
               } else {
                  Nano.Buffer buf = new Nano.Buffer(empty ? null : file);

                  try {
                     buf.open();
                     if (this.readNewBuffer) {
                        this.buffers.add(++this.bufferIndex, buf);
                        this.buffer = buf;
                     } else {
                        this.buffer.insert(String.join("\n", buf.lines));
                     }

                     this.setMessage(null);
                  } catch (IOException var9) {
                     this.setMessage("Error reading " + file + ": " + var9.getMessage());
                  }
               }

               this.shortcuts = this.standardShortcuts();
               return;
            case CANCEL:
               this.editMessage = null;
               this.shortcuts = this.standardShortcuts();
               return;
            case NEW_BUFFER:
               this.readNewBuffer = !this.readNewBuffer;
               break;
            case MOUSE_EVENT:
               this.mouseEvent();
               break;
            default:
               curPos = this.editInputBuffer(op, curPos);
         }

         this.editMessage = this.getReadMessage();
         this.display(curPos);
      }
   }

   private String getReadMessage() {
      StringBuilder sb = new StringBuilder();
      sb.append("File to insert");
      if (this.readNewBuffer) {
         sb.append(" into new buffer");
      }

      sb.append(" [from ./]: ");
      return sb.toString();
   }

   void gotoLine() {
      KeyMap<Nano.Operation> readKeyMap = new KeyMap<>();
      readKeyMap.setUnicode(Nano.Operation.INSERT);

      for (char i = ' '; i < 256; i++) {
         readKeyMap.bind(Nano.Operation.INSERT, Character.toString(i));
      }

      readKeyMap.bind(Nano.Operation.BACKSPACE, KeyMap.del());
      readKeyMap.bind(Nano.Operation.ACCEPT, "\r");
      readKeyMap.bind(Nano.Operation.HELP, KeyMap.ctrl('G'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f1));
      readKeyMap.bind(Nano.Operation.CANCEL, KeyMap.ctrl('C'));
      readKeyMap.bind(Nano.Operation.MOUSE_EVENT, MouseSupport.keys(this.terminal));
      readKeyMap.bind(Nano.Operation.RIGHT, KeyMap.key(this.terminal, InfoCmp.Capability.key_right));
      readKeyMap.bind(Nano.Operation.LEFT, KeyMap.key(this.terminal, InfoCmp.Capability.key_left));
      readKeyMap.bind(Nano.Operation.FIRST_LINE, KeyMap.ctrl('Y'));
      readKeyMap.bind(Nano.Operation.LAST_LINE, KeyMap.ctrl('V'));
      readKeyMap.bind(Nano.Operation.SEARCH, KeyMap.ctrl('T'));
      this.editMessage = "Enter line number, column number: ";
      this.editBuffer.setLength(0);
      int curPos = this.editBuffer.length();
      this.shortcuts = this.gotoShortcuts();
      this.display(curPos);

      while (true) {
         Nano.Operation op = this.readOperation(readKeyMap);
         switch (op) {
            case HELP:
               this.help("nano-goto-help.txt");
               break;
            case NEXT_PAGE:
            case PREV_PAGE:
            case SCROLL_UP:
            case SCROLL_DOWN:
            case NEXT_WORD:
            case PREV_WORD:
            case LSP_SUGGESTION:
            case BEGINNING_OF_LINE:
            case END_OF_LINE:
            case CUR_POS:
            case CASE_SENSITIVE:
            case BACKWARDS:
            case REGEXP:
            default:
               curPos = this.editInputBuffer(op, curPos);
               break;
            case FIRST_LINE:
               this.editMessage = null;
               this.buffer.firstLine();
               this.shortcuts = this.standardShortcuts();
               return;
            case LAST_LINE:
               this.editMessage = null;
               this.buffer.lastLine();
               this.shortcuts = this.standardShortcuts();
               return;
            case ACCEPT:
               this.editMessage = null;
               String[] pos = this.editBuffer.toString().split(",", 2);
               int[] args = new int[]{0, 0};

               try {
                  for (int i = 0; i < pos.length; i++) {
                     if (!pos[i].trim().isEmpty()) {
                        args[i] = Integer.parseInt(pos[i]) - 1;
                        if (args[i] < 0) {
                           throw new NumberFormatException();
                        }
                     }
                  }

                  this.buffer.gotoLine(args[1], args[0]);
               } catch (NumberFormatException var7) {
                  this.setMessage("Invalid line or column number");
               } catch (Exception var8) {
                  this.setMessage("Internal error: " + var8.getMessage());
               }

               this.shortcuts = this.standardShortcuts();
               return;
            case CANCEL:
               this.editMessage = null;
               this.shortcuts = this.standardShortcuts();
               return;
            case SEARCH:
               this.searchToReplace = false;
               this.searchAndReplace();
               return;
         }

         this.display(curPos);
      }
   }

   private LinkedHashMap<String, String> gotoShortcuts() {
      LinkedHashMap<String, String> shortcuts = new LinkedHashMap<>();
      shortcuts.put("^G", "Get Help");
      shortcuts.put("^Y", "First Line");
      shortcuts.put("^T", "Go To Text");
      shortcuts.put("^C", "Cancel");
      shortcuts.put("^V", "Last Line");
      return shortcuts;
   }

   private LinkedHashMap<String, String> readShortcuts() {
      LinkedHashMap<String, String> shortcuts = new LinkedHashMap<>();
      shortcuts.put("^G", "Get Help");
      shortcuts.put("^T", "To Files");
      shortcuts.put("M-F", "New Buffer");
      shortcuts.put("^C", "Cancel");
      shortcuts.put("^X", "Execute Command");
      return shortcuts;
   }

   private LinkedHashMap<String, String> writeShortcuts() {
      LinkedHashMap<String, String> s = new LinkedHashMap<>();
      s.put("^G", "Get Help");
      s.put("M-M", "Mac Format");
      s.put("^C", "Cancel");
      s.put("M-D", "DOS Format");
      if (!this.restricted) {
         s.put("^T", "To Files");
         s.put("M-P", "Prepend");
         s.put("M-A", "Append");
         s.put("M-B", "Backup File");
      }

      return s;
   }

   private LinkedHashMap<String, String> helpShortcuts() {
      LinkedHashMap<String, String> s = new LinkedHashMap<>();
      s.put("^L", "Refresh");
      s.put("^Y", "Prev Page");
      s.put("^P", "Prev Line");
      s.put("M-\\", "First Line");
      s.put("^X", "Exit");
      s.put("^V", "Next Page");
      s.put("^N", "Next Line");
      s.put("M-/", "Last Line");
      return s;
   }

   private LinkedHashMap<String, String> searchShortcuts() {
      LinkedHashMap<String, String> s = new LinkedHashMap<>();
      s.put("^G", "Get Help");
      s.put("^Y", "First Line");
      if (this.searchToReplace) {
         s.put("^R", "No Replace");
      } else {
         s.put("^R", "Replace");
         s.put("^W", "Beg of Par");
      }

      s.put("M-C", "Case Sens");
      s.put("M-R", "Regexp");
      s.put("^C", "Cancel");
      s.put("^V", "Last Line");
      s.put("^T", "Go To Line");
      if (!this.searchToReplace) {
         s.put("^O", "End of Par");
      }

      s.put("M-B", "Backwards");
      s.put("^P", "PrevHstory");
      return s;
   }

   private LinkedHashMap<String, String> replaceShortcuts() {
      LinkedHashMap<String, String> s = new LinkedHashMap<>();
      s.put("^G", "Get Help");
      s.put("^Y", "First Line");
      s.put("^P", "PrevHstory");
      s.put("^C", "Cancel");
      s.put("^V", "Last Line");
      s.put("^N", "NextHstory");
      return s;
   }

   private LinkedHashMap<String, String> standardShortcuts() {
      LinkedHashMap<String, String> s = new LinkedHashMap<>();
      s.put("^G", "Get Help");
      if (!this.view) {
         s.put("^O", "WriteOut");
      }

      s.put("^R", "Read File");
      s.put("^Y", "Prev Page");
      if (!this.view) {
         s.put("^K", "Cut Text");
      }

      s.put("^C", "Cur Pos");
      s.put("^X", "Exit");
      if (!this.view) {
         s.put("^J", "Justify");
      }

      s.put("^W", "Where Is");
      s.put("^V", "Next Page");
      if (!this.view) {
         s.put("^U", "UnCut Text");
      }

      s.put("^T", "To Spell");
      return s;
   }

   void help(String help) {
      Nano.Buffer org = this.buffer;
      Nano.Buffer newBuf = new Nano.Buffer(null);

      try {
         InputStream is = this.getClass().getResourceAsStream(help);

         try {
            newBuf.open(is);
         } catch (Throwable var17) {
            if (is != null) {
               try {
                  is.close();
               } catch (Throwable var16) {
                  var17.addSuppressed(var16);
               }
            }

            throw var17;
         }

         if (is != null) {
            is.close();
         }
      } catch (IOException var18) {
         this.setMessage("Unable to read help");
         return;
      }

      LinkedHashMap<String, String> oldShortcuts = this.shortcuts;
      this.shortcuts = this.helpShortcuts();
      boolean oldWrapping = this.wrapping;
      boolean oldPrintLineNumbers = this.printLineNumbers;
      boolean oldConstantCursor = this.constantCursor;
      boolean oldAtBlanks = this.atBlanks;
      boolean oldHighlight = this.highlight;
      String oldEditMessage = this.editMessage;
      this.editMessage = "";
      this.wrapping = true;
      this.atBlanks = true;
      this.printLineNumbers = false;
      this.constantCursor = false;
      this.highlight = false;
      this.buffer = newBuf;
      if (!oldWrapping) {
         this.buffer.computeAllOffsets();
      }

      try {
         this.message = null;
         this.terminal.puts(InfoCmp.Capability.cursor_invisible);
         this.display();

         while (true) {
            switch (this.readOperation(this.keys)) {
               case QUIT:
                  return;
               case CLEAR_SCREEN:
                  this.clearScreen();
                  break;
               case UP:
                  this.buffer.scrollUp(1);
                  break;
               case DOWN:
                  this.buffer.scrollDown(1);
                  break;
               case NEXT_PAGE:
                  this.buffer.nextPage();
                  break;
               case PREV_PAGE:
                  this.buffer.prevPage();
                  break;
               case FIRST_LINE:
                  this.buffer.firstLine();
                  break;
               case LAST_LINE:
                  this.buffer.lastLine();
                  break;
               case MOUSE_EVENT:
                  this.mouseEvent();
                  break;
               case TOGGLE_SUSPENSION:
                  this.toggleSuspension();
            }

            this.display();
         }
      } finally {
         this.buffer = org;
         this.wrapping = oldWrapping;
         this.printLineNumbers = oldPrintLineNumbers;
         this.constantCursor = oldConstantCursor;
         this.shortcuts = oldShortcuts;
         this.atBlanks = oldAtBlanks;
         this.highlight = oldHighlight;
         this.editMessage = oldEditMessage;
         this.terminal.puts(InfoCmp.Capability.cursor_visible);
         if (!oldWrapping) {
            this.buffer.computeAllOffsets();
         }
      }
   }

   void searchAndReplace() {
      try {
         this.search();
         if (this.searchToReplace) {
            String replaceTerm = this.replace();
            int replaced = 0;
            boolean all = false;
            boolean found = true;
            List<Integer> matches = new ArrayList<>();
            Nano.Operation op = Nano.Operation.NO;

            while (found) {
               found = this.buffer.nextSearch();
               if (found) {
                  int[] re = this.buffer.highlightStart();
                  int col = this.searchBackwards ? this.buffer.length(this.buffer.getLine(re[0])) - re[1] : re[1];
                  int match = re[0] * 10000 + col;
                  if (matches.contains(match)) {
                     break;
                  }

                  matches.add(match);
                  if (!all) {
                     op = this.getYNC("Replace this instance? ", true);
                  }
               } else {
                  op = Nano.Operation.NO;
               }

               switch (op) {
                  case CANCEL:
                     found = false;
                     break;
                  case YES:
                     this.buffer.replaceFromCursor(this.matchedLength, replaceTerm);
                     replaced++;
                  case NO:
                  default:
                     break;
                  case ALL:
                     all = true;
                     this.buffer.replaceFromCursor(this.matchedLength, replaceTerm);
                     replaced++;
               }
            }

            this.message = "Replaced " + replaced + " occurrences";
            return;
         }
      } catch (Exception var13) {
         return;
      } finally {
         this.searchToReplace = false;
         this.matchedLength = -1;
         this.shortcuts = this.standardShortcuts();
         this.editMessage = null;
      }
   }

   void search() throws IOException {
      KeyMap<Nano.Operation> searchKeyMap = new KeyMap<>();
      searchKeyMap.setUnicode(Nano.Operation.INSERT);

      for (char i = ' '; i < 256; i++) {
         searchKeyMap.bind(Nano.Operation.INSERT, Character.toString(i));
      }

      for (char i = 'A'; i <= 'Z'; i++) {
         searchKeyMap.bind(Nano.Operation.DO_LOWER_CASE, KeyMap.alt(i));
      }

      searchKeyMap.bind(Nano.Operation.BACKSPACE, KeyMap.del());
      searchKeyMap.bind(Nano.Operation.CASE_SENSITIVE, KeyMap.alt('c'));
      searchKeyMap.bind(Nano.Operation.BACKWARDS, KeyMap.alt('b'));
      searchKeyMap.bind(Nano.Operation.REGEXP, KeyMap.alt('r'));
      searchKeyMap.bind(Nano.Operation.ACCEPT, "\r");
      searchKeyMap.bind(Nano.Operation.CANCEL, KeyMap.ctrl('C'));
      searchKeyMap.bind(Nano.Operation.HELP, KeyMap.ctrl('G'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f1));
      searchKeyMap.bind(Nano.Operation.FIRST_LINE, KeyMap.ctrl('Y'));
      searchKeyMap.bind(Nano.Operation.LAST_LINE, KeyMap.ctrl('V'));
      searchKeyMap.bind(Nano.Operation.MOUSE_EVENT, MouseSupport.keys(this.terminal));
      searchKeyMap.bind(Nano.Operation.RIGHT, KeyMap.key(this.terminal, InfoCmp.Capability.key_right));
      searchKeyMap.bind(Nano.Operation.LEFT, KeyMap.key(this.terminal, InfoCmp.Capability.key_left));
      searchKeyMap.bind(Nano.Operation.UP, KeyMap.key(this.terminal, InfoCmp.Capability.key_up));
      searchKeyMap.bind(Nano.Operation.DOWN, KeyMap.key(this.terminal, InfoCmp.Capability.key_down));
      searchKeyMap.bind(Nano.Operation.TOGGLE_REPLACE, KeyMap.ctrl('R'));
      this.editMessage = this.getSearchMessage();
      this.editBuffer.setLength(0);
      String currentBuffer = this.editBuffer.toString();
      int curPos = this.editBuffer.length();
      this.shortcuts = this.searchShortcuts();
      this.display(curPos);

      try {
         while (true) {
            Nano.Operation op = this.readOperation(searchKeyMap);
            switch (op) {
               case UP:
                  this.editBuffer.setLength(0);
                  this.editBuffer.append(this.patternHistory.up(currentBuffer));
                  curPos = this.editBuffer.length();
                  break;
               case DOWN:
                  this.editBuffer.setLength(0);
                  this.editBuffer.append(this.patternHistory.down(currentBuffer));
                  curPos = this.editBuffer.length();
                  break;
               case HELP:
                  if (this.searchToReplace) {
                     this.help("nano-search-replace-help.txt");
                  } else {
                     this.help("nano-search-help.txt");
                  }
                  break;
               case FIRST_LINE:
                  this.buffer.firstLine();
                  break;
               case LAST_LINE:
                  this.buffer.lastLine();
                  break;
               case CASE_SENSITIVE:
                  this.searchCaseSensitive = !this.searchCaseSensitive;
                  break;
               case BACKWARDS:
                  this.searchBackwards = !this.searchBackwards;
                  break;
               case REGEXP:
                  this.searchRegexp = !this.searchRegexp;
                  break;
               case ACCEPT:
                  if (this.editBuffer.length() > 0) {
                     this.searchTerm = this.editBuffer.toString();
                  }

                  if (this.searchTerm == null || this.searchTerm.isEmpty()) {
                     this.setMessage("Cancelled");
                     throw new IllegalArgumentException();
                  }

                  this.patternHistory.add(this.searchTerm);
                  if (!this.searchToReplace) {
                     this.buffer.nextSearch();
                  }

                  return;
               case CANCEL:
                  throw new IllegalArgumentException();
               case TOGGLE_REPLACE:
                  this.searchToReplace = !this.searchToReplace;
                  this.shortcuts = this.searchShortcuts();
                  break;
               case MOUSE_EVENT:
                  this.mouseEvent();
                  break;
               default:
                  curPos = this.editInputBuffer(op, curPos);
                  currentBuffer = this.editBuffer.toString();
            }

            this.editMessage = this.getSearchMessage();
            this.display(curPos);
         }
      } finally {
         this.shortcuts = this.standardShortcuts();
         this.editMessage = null;
      }
   }

   String replace() {
      KeyMap<Nano.Operation> keyMap = new KeyMap<>();
      keyMap.setUnicode(Nano.Operation.INSERT);

      for (char i = ' '; i < 256; i++) {
         keyMap.bind(Nano.Operation.INSERT, Character.toString(i));
      }

      for (char i = 'A'; i <= 'Z'; i++) {
         keyMap.bind(Nano.Operation.DO_LOWER_CASE, KeyMap.alt(i));
      }

      keyMap.bind(Nano.Operation.BACKSPACE, KeyMap.del());
      keyMap.bind(Nano.Operation.ACCEPT, "\r");
      keyMap.bind(Nano.Operation.CANCEL, KeyMap.ctrl('C'));
      keyMap.bind(Nano.Operation.HELP, KeyMap.ctrl('G'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f1));
      keyMap.bind(Nano.Operation.FIRST_LINE, KeyMap.ctrl('Y'));
      keyMap.bind(Nano.Operation.LAST_LINE, KeyMap.ctrl('V'));
      keyMap.bind(Nano.Operation.MOUSE_EVENT, MouseSupport.keys(this.terminal));
      keyMap.bind(Nano.Operation.RIGHT, KeyMap.key(this.terminal, InfoCmp.Capability.key_right));
      keyMap.bind(Nano.Operation.LEFT, KeyMap.key(this.terminal, InfoCmp.Capability.key_left));
      keyMap.bind(Nano.Operation.UP, KeyMap.key(this.terminal, InfoCmp.Capability.key_up));
      keyMap.bind(Nano.Operation.DOWN, KeyMap.key(this.terminal, InfoCmp.Capability.key_down));
      this.editMessage = "Replace with: ";
      this.editBuffer.setLength(0);
      String currentBuffer = this.editBuffer.toString();
      int curPos = this.editBuffer.length();
      this.shortcuts = this.replaceShortcuts();
      this.display(curPos);

      try {
         while (true) {
            Nano.Operation op = this.readOperation(keyMap);
            switch (op) {
               case UP:
                  this.editBuffer.setLength(0);
                  this.editBuffer.append(this.patternHistory.up(currentBuffer));
                  curPos = this.editBuffer.length();
                  break;
               case DOWN:
                  this.editBuffer.setLength(0);
                  this.editBuffer.append(this.patternHistory.down(currentBuffer));
                  curPos = this.editBuffer.length();
                  break;
               case HELP:
                  this.help("nano-replace-help.txt");
                  break;
               case FIRST_LINE:
                  this.buffer.firstLine();
                  break;
               case LAST_LINE:
                  this.buffer.lastLine();
                  break;
               case ACCEPT:
                  String replaceTerm = "";
                  if (this.editBuffer.length() > 0) {
                     replaceTerm = this.editBuffer.toString();
                  }

                  this.patternHistory.add(replaceTerm);
                  return replaceTerm;
               case CANCEL:
                  throw new IllegalArgumentException();
               case MOUSE_EVENT:
                  this.mouseEvent();
                  break;
               default:
                  curPos = this.editInputBuffer(op, curPos);
                  currentBuffer = this.editBuffer.toString();
            }

            this.display(curPos);
         }
      } finally {
         this.shortcuts = this.standardShortcuts();
         this.editMessage = null;
      }
   }

   private String getSearchMessage() {
      StringBuilder sb = new StringBuilder();
      sb.append("Search");
      if (this.searchToReplace) {
         sb.append(" (to replace)");
      }

      if (this.searchCaseSensitive) {
         sb.append(" [Case Sensitive]");
      }

      if (this.searchRegexp) {
         sb.append(" [Regexp]");
      }

      if (this.searchBackwards) {
         sb.append(" [Backwards]");
      }

      if (this.searchTerm != null) {
         sb.append(" [");
         sb.append(this.searchTerm);
         sb.append("]");
      }

      sb.append(": ");
      return sb.toString();
   }

   String computeCurPos() {
      int chari = 0;
      int chart = 0;

      for (int i = 0; i < this.buffer.lines.size(); i++) {
         int l = this.buffer.lines.get(i).length() + 1;
         if (i < this.buffer.line) {
            chari += l;
         } else if (i == this.buffer.line) {
            chari += this.buffer.offsetInLine + this.buffer.column;
         }

         chart += l;
      }

      StringBuilder sb = new StringBuilder();
      sb.append("line ");
      sb.append(this.buffer.line + 1);
      sb.append("/");
      sb.append(this.buffer.lines.size());
      sb.append(" (");
      sb.append(Math.round(100.0 * this.buffer.line / this.buffer.lines.size()));
      sb.append("%), ");
      sb.append("col ");
      sb.append(this.buffer.offsetInLine + this.buffer.column + 1);
      sb.append("/");
      sb.append(this.buffer.length(this.buffer.lines.get(this.buffer.line)) + 1);
      sb.append(" (");
      if (!this.buffer.lines.get(this.buffer.line).isEmpty()) {
         sb.append(Math.round(100.0 * (this.buffer.offsetInLine + this.buffer.column) / this.buffer.length(this.buffer.lines.get(this.buffer.line))));
      } else {
         sb.append("100");
      }

      sb.append("%), ");
      sb.append("char ");
      sb.append(chari + 1);
      sb.append("/");
      sb.append(chart);
      sb.append(" (");
      sb.append(Math.round(100.0 * chari / chart));
      sb.append("%)");
      return sb.toString();
   }

   void curPos() {
      this.setMessage(this.computeCurPos());
   }

   void prevBuffer() throws IOException {
      if (this.buffers.size() > 1) {
         this.bufferIndex = (this.bufferIndex + this.buffers.size() - 1) % this.buffers.size();
         this.buffer = this.buffers.get(this.bufferIndex);
         this.setMessage("Switched to " + this.buffer.getTitle());
         this.buffer.open();
         this.display.clear();
      } else {
         this.setMessage("No more open file buffers");
      }
   }

   void nextBuffer() throws IOException {
      if (this.buffers.size() > 1) {
         this.bufferIndex = (this.bufferIndex + 1) % this.buffers.size();
         this.buffer = this.buffers.get(this.bufferIndex);
         this.setMessage("Switched to " + this.buffer.getTitle());
         this.buffer.open();
         this.display.clear();
      } else {
         this.setMessage("No more open file buffers");
      }
   }

   void setMessage(String message) {
      this.message = message;
      this.nbBindings = this.quickBlank ? 2 : 25;
   }

   boolean quit() throws IOException {
      if (this.buffer.dirty) {
         if (this.tempFile) {
            if (!this.write()) {
               return false;
            }
         } else {
            Nano.Operation op = this.getYNC("Save modified buffer (ANSWERING \"No\" WILL DESTROY CHANGES) ? ");
            switch (op) {
               case CANCEL:
                  return false;
               case YES:
                  if (!this.write()) {
                     return false;
                  }
               case NO:
            }
         }
      }

      this.buffers.remove(this.bufferIndex);
      if (this.bufferIndex == this.buffers.size() && this.bufferIndex > 0) {
         this.bufferIndex = this.buffers.size() - 1;
      }

      if (this.buffers.isEmpty()) {
         this.buffer = null;
         return true;
      } else {
         this.buffer = this.buffers.get(this.bufferIndex);
         this.buffer.open();
         this.display.clear();
         this.setMessage("Switched to " + this.buffer.getTitle());
         return false;
      }
   }

   void numbers() {
      this.printLineNumbers = !this.printLineNumbers;
      this.resetDisplay();
      this.setMessage("Lines numbering " + (this.printLineNumbers ? "enabled" : "disabled"));
   }

   void smoothScrolling() {
      this.smoothScrolling = !this.smoothScrolling;
      this.setMessage("Smooth scrolling " + (this.smoothScrolling ? "enabled" : "disabled"));
   }

   void mouseSupport() {
      this.mouseSupport = !this.mouseSupport;
      this.setMessage("Mouse support " + (this.mouseSupport ? "enabled" : "disabled"));
      this.terminal.trackMouse(this.mouseSupport ? Terminal.MouseTracking.Normal : Terminal.MouseTracking.Off);
   }

   void constantCursor() {
      this.constantCursor = !this.constantCursor;
      this.setMessage("Constant cursor position display " + (this.constantCursor ? "enabled" : "disabled"));
   }

   void oneMoreLine() {
      this.oneMoreLine = !this.oneMoreLine;
      this.setMessage("Use of one more line for editing " + (this.oneMoreLine ? "enabled" : "disabled"));
   }

   void wrap() {
      this.wrapping = !this.wrapping;
      this.buffer.computeAllOffsets();
      this.resetDisplay();
      this.setMessage("Lines wrapping " + (this.wrapping ? "enabled" : "disabled"));
   }

   void clearScreen() {
      this.resetDisplay();
   }

   void mouseEvent() {
      MouseEvent event = this.terminal.readMouseEvent(this.bindingReader::readCharacter, this.bindingReader.getLastBinding());
      if (this.mouseSupport) {
         MouseEvent.Type eventType = event.getType();
         if (eventType == MouseEvent.Type.Released && event.getModifiers().isEmpty() && event.getButton() == MouseEvent.Button.Button1) {
            int x = event.getX();
            int y = event.getY();
            int hdr = this.buffer.computeHeader().size();
            int ftr = this.computeFooter().size();
            if (y >= hdr) {
               if (y < this.size.getRows() - ftr) {
                  this.buffer.moveTo(x, y - hdr);
               } else {
                  int cols = (this.shortcuts.size() + 1) / 2;
                  int cw = this.size.getColumns() / cols;
                  int l = y - (this.size.getRows() - ftr) - 1;
                  int si = l * cols + x / cw;
                  String shortcut = null;
                  Iterator<String> it = this.shortcuts.keySet().iterator();

                  while (si-- >= 0 && it.hasNext()) {
                     shortcut = it.next();
                  }

                  if (shortcut != null) {
                     shortcut = shortcut.replaceAll("M-", "\\\\E");
                     String seq = KeyMap.translate(shortcut);
                     this.bindingReader.runMacro(seq);
                  }
               }
            }
         } else if (eventType == MouseEvent.Type.Wheel) {
            if (event.getButton() == MouseEvent.Button.WheelDown) {
               this.buffer.moveDown(1);
            } else if (event.getButton() == MouseEvent.Button.WheelUp) {
               this.buffer.moveUp(1);
            }
         } else if (eventType == MouseEvent.Type.Moved) {
            this.mouseX = event.getX();
            this.mouseY = event.getY();
         }
      }
   }

   void enableSuspension() {
      if (!this.restricted && this.vsusp < 0) {
         Attributes attrs = this.terminal.getAttributes();
         attrs.setControlChar(Attributes.ControlChar.VSUSP, this.vsusp);
         this.terminal.setAttributes(attrs);
      }
   }

   void toggleSuspension() {
      if (this.restricted) {
         this.setMessage("This function is disabled in restricted mode");
      } else if (this.vsusp < 0) {
         this.setMessage("This function is disabled");
      } else {
         Attributes attrs = this.terminal.getAttributes();
         int toggle = this.vsusp;
         String message = "enabled";
         if (attrs.getControlChar(Attributes.ControlChar.VSUSP) > 0) {
            toggle = 0;
            message = "disabled";
         }

         attrs.setControlChar(Attributes.ControlChar.VSUSP, toggle);
         this.terminal.setAttributes(attrs);
         this.setMessage("Suspension " + message);
      }
   }

   public String getTitle() {
      return this.title;
   }

   void resetDisplay() {
      this.display.clear();
      this.display.resize(this.size.getRows(), this.size.getColumns());

      for (Nano.Buffer buffer : this.buffers) {
         buffer.resetDisplay();
      }
   }

   synchronized void display() {
      this.display(null);
   }

   synchronized void display(Integer editCursor) {
      if (this.nbBindings > 0 && --this.nbBindings == 0) {
         this.message = null;
      }

      List<AttributedString> header = this.buffer.computeHeader();
      List<AttributedString> footer = this.computeFooter();
      int nbLines = this.size.getRows() - header.size() - footer.size();
      if (this.insertHelp) {
         this.insertHelp(this.suggestionBox.getSelected());
         this.resetSuggestion();
      }

      List<Nano.Diagnostic> diagnostics = this.computeDiagnostic();
      List<AttributedString> newLines = this.buffer.getDisplayedLines(nbLines, diagnostics);
      if (this.help) {
         this.showCompletion(newLines);
      }

      newLines.addAll(0, header);
      newLines.addAll(footer);
      int cursor;
      if (this.editMessage != null) {
         int crsr = editCursor != null ? editCursor : this.editBuffer.length();
         cursor = this.editMessage.length() + crsr;
         cursor = this.size.cursorPos(this.size.getRows() - footer.size(), cursor);
      } else {
         cursor = this.size.cursorPos(header.size(), this.buffer.getDisplayedCursor());
      }

      this.display.update(newLines, cursor);
      if (this.windowsTerminal) {
         this.resetDisplay();
      }
   }

   protected void insertHelp(int selected) {
   }

   private void showCompletion(List<AttributedString> newLines) {
      if (this.suggestions == null) {
         LinkedHashMap<AttributedString, List<AttributedString>> result = this.computeSuggestions();
         if (result == null || result.isEmpty()) {
            this.resetSuggestion();
            return;
         }

         this.suggestions = result;
      }

      this.initBoxes(newLines);
   }

   protected LinkedHashMap<AttributedString, List<AttributedString>> computeSuggestions() {
      return new LinkedHashMap<>();
   }

   protected List<Nano.Diagnostic> computeDiagnostic() {
      return Collections.emptyList();
   }

   private void initBoxes(List<AttributedString> screenLines) {
      List<AttributedString> suggestionList = new ArrayList<>(this.suggestions.keySet());
      if (this.suggestionBox == null) {
         this.suggestionBox = this.buildSuggestionBox(suggestionList, screenLines);
      }

      if (this.suggestionBox != null) {
         this.suggestionBox.draw(screenLines);
         int selectedIndex = this.suggestionBox.getSelected();
         if (selectedIndex >= 0 && selectedIndex < suggestionList.size()) {
            AttributedString selectedSuggestion = suggestionList.get(selectedIndex);
            List<AttributedString> documentation = this.suggestions.get(selectedSuggestion);
            if (documentation != null && !documentation.isEmpty()) {
               Nano.Box documentationBox = this.buildDocumentationBox(screenLines, this.suggestionBox, documentation);
               if (documentationBox != null) {
                  documentationBox.draw(screenLines);
               }
            }
         }
      }
   }

   private Nano.Box buildSuggestionBox(List<AttributedString> suggestions, List<AttributedString> screenLines) {
      if (suggestions != null && !suggestions.isEmpty()) {
         int cursorX = this.buffer.column;
         int xi = Math.max(this.printLineNumbers ? 8 : 0, cursorX);
         int maxSuggestionLength = suggestions.stream().mapToInt(AttributedString::length).max().orElse(10) + 2;
         int maxScreenWidth = (int)Math.round(this.size.getColumns() * 0.6);
         int xl = Math.min(xi + maxSuggestionLength, xi + maxScreenWidth);
         xl = Math.min(xl, this.size.getColumns() - 1);
         int maxHeight = screenLines.size() - 1;
         int maxVisibleItems = Math.min(10, maxHeight / 3);
         maxVisibleItems = Math.max(2, maxVisibleItems);
         int requiredHeight = Math.min(suggestions.size(), maxVisibleItems) + 2;
         int cursorLine = this.buffer.line;
         int cursorScreenLine = cursorLine - this.buffer.firstLineToDisplay;
         int yi = cursorScreenLine + 1;
         int spaceBelow = maxHeight - yi;
         boolean displayBelow = true;
         if (spaceBelow < requiredHeight) {
            if (cursorScreenLine <= spaceBelow && spaceBelow >= 3) {
               requiredHeight = Math.max(3, spaceBelow);
            } else {
               displayBelow = false;
               yi = Math.max(0, cursorScreenLine - requiredHeight - 1);
               if (cursorScreenLine - requiredHeight - 1 < 0) {
                  requiredHeight = Math.max(3, cursorScreenLine - 1);
                  yi = 0;
               }
            }
         }

         int yl;
         if (displayBelow) {
            yl = Math.min(maxHeight, yi + requiredHeight);
         } else {
            yl = cursorScreenLine - 1;
         }

         if (yl > yi && xl > xi) {
            if (xl - xi < 4) {
               xl = xi + 4;
            }

            Nano.Box box = new Nano.Box(xi, yi, xl, yl);
            box.setLines(suggestions);
            box.setSelectedStyle(AttributedStyle.DEFAULT.background(4).foreground(7));
            return box;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private Nano.Box buildDocumentationBox(List<AttributedString> screenLines, Nano.Box suggestionBox, List<AttributedString> documentation) {
      if (suggestionBox == null || screenLines == null || screenLines.isEmpty()) {
         return null;
      } else if (documentation != null && !documentation.isEmpty()) {
         int dXi = suggestionBox.xl;
         int dBoxSize = documentation.stream().mapToInt(AttributedString::length).max().orElse(10) + 2;
         int xi = Math.max(this.printLineNumbers ? 9 : 1, dXi);
         int maxWidth = (int)Math.round((this.size.getColumns() - xi) * 0.6);
         int xl = Math.min(dBoxSize + xi, xi + maxWidth);
         if (xl <= xi) {
            return null;
         } else {
            documentation = this.adjustLines(documentation, dBoxSize - 2, xl - xi - 2);
            int height = screenLines.size();
            int requiredHeight = documentation.size() + 2;
            int yi = suggestionBox.yi;
            int yl = yi + requiredHeight;
            if (yl >= height) {
               yl = Math.min(height - 1, yl);
               if (yl - yi < 3) {
                  return null;
               }
            }

            if (yl <= yi) {
               return null;
            } else {
               Nano.Box documentationBox = new Nano.Box(xi, yi, xl, yl);
               documentationBox.setLines(documentation);
               documentationBox.setSelectedStyle(AttributedStyle.DEFAULT);
               return documentationBox;
            }
         }
      } else {
         return null;
      }
   }

   private List<AttributedString> adjustLines(List<AttributedString> lines, int max, int boxLength) {
      if (max <= boxLength) {
         return lines;
      } else {
         List<AttributedString> adjustedLines = new ArrayList<>();

         for (AttributedString line : lines) {
            if (line.length() < boxLength) {
               adjustedLines.add(line);
            } else {
               int start = 0;

               while (start < line.length()) {
                  int stepSize = Math.min(start + boxLength, line.length());
                  int end = stepSize;
                  if (stepSize - start >= boxLength) {
                     while (end > start && !Character.isWhitespace(line.charAt(end - 1))) {
                        end--;
                     }
                  }

                  if (end == start) {
                     end = stepSize;
                  }

                  adjustedLines.add(line.substring(start, end));
                  start = end;
               }
            }
         }

         return adjustedLines;
      }
   }

   protected List<AttributedString> computeFooter() {
      List<AttributedString> footer = new ArrayList<>();
      if (this.editMessage != null) {
         AttributedStringBuilder sb = new AttributedStringBuilder();
         sb.style(AttributedStyle.INVERSE);
         sb.append(this.editMessage);
         sb.append(this.editBuffer);

         for (int i = this.editMessage.length() + this.editBuffer.length(); i < this.size.getColumns(); i++) {
            sb.append(' ');
         }

         sb.append('\n');
         footer.add(sb.toAttributedString());
      } else if (this.message == null && !this.constantCursor) {
         footer.add(new AttributedString("\n"));
      } else {
         int rwidth = this.size.getColumns();
         String text = "[ " + (this.message == null ? this.computeCurPos() : this.message) + " ]";
         int len = text.length();
         AttributedStringBuilder sb = new AttributedStringBuilder();

         for (int i = 0; i < (rwidth - len) / 2; i++) {
            sb.append(' ');
         }

         sb.style(AttributedStyle.INVERSE);
         sb.append(text);
         sb.append('\n');
         footer.add(sb.toAttributedString());
      }

      Iterator<Entry<String, String>> sit = this.shortcuts.entrySet().iterator();
      int cols = (this.shortcuts.size() + 1) / 2;
      int cw = (this.size.getColumns() - 1) / cols;
      int rem = (this.size.getColumns() - 1) % cols;

      for (int l = 0; l < 2; l++) {
         AttributedStringBuilder sb = new AttributedStringBuilder();

         for (int c = 0; c < cols; c++) {
            Entry<String, String> entry = sit.hasNext() ? sit.next() : null;
            String key = entry != null ? entry.getKey() : "";
            String val = entry != null ? entry.getValue() : "";
            sb.style(AttributedStyle.INVERSE);
            sb.append(key);
            sb.style(AttributedStyle.DEFAULT);
            sb.append(" ");
            int nb = cw - key.length() - 1 + (c < rem ? 1 : 0);
            if (val.length() > nb) {
               sb.append(val.substring(0, nb));
            } else {
               sb.append(val);
               if (c < cols - 1) {
                  for (int i = 0; i < nb - val.length(); i++) {
                     sb.append(" ");
                  }
               }
            }
         }

         sb.append('\n');
         footer.add(sb.toAttributedString());
      }

      return footer;
   }

   protected void handle(Terminal.Signal signal) {
      if (this.buffer != null) {
         this.size.copy(this.terminal.getSize());
         this.buffer.computeAllOffsets();
         this.buffer.moveToChar(this.buffer.offsetInLine + this.buffer.column);
         this.resetDisplay();
         this.display();
      }
   }

   protected void bindKeys() {
      this.keys = new KeyMap<>();
      if (!this.view) {
         this.keys.setUnicode(Nano.Operation.INSERT);

         for (char i = ' '; i < 128; i++) {
            this.keys.bind(Nano.Operation.INSERT, Character.toString(i));
         }

         this.keys.bind(Nano.Operation.BACKSPACE, KeyMap.del());

         for (char i = 'A'; i <= 'Z'; i++) {
            this.keys.bind(Nano.Operation.DO_LOWER_CASE, KeyMap.alt(i));
         }

         this.keys.bind(Nano.Operation.WRITE, KeyMap.ctrl('O'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f3));
         this.keys.bind(Nano.Operation.JUSTIFY_PARAGRAPH, KeyMap.ctrl('J'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f4));
         this.keys.bind(Nano.Operation.CUT, KeyMap.ctrl('K'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f9));
         this.keys.bind(Nano.Operation.UNCUT, KeyMap.ctrl('U'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f10));
         this.keys.bind(Nano.Operation.REPLACE, KeyMap.ctrl('\\'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f14), KeyMap.alt('r'));
         this.keys.bind(Nano.Operation.MARK, KeyMap.ctrl('^'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f15), KeyMap.alt('a'));
         this.keys.bind(Nano.Operation.COPY, KeyMap.alt('^'), KeyMap.alt('6'));
         this.keys.bind(Nano.Operation.INDENT, KeyMap.alt('}'));
         this.keys.bind(Nano.Operation.UNINDENT, KeyMap.alt('{'));
         this.keys.bind(Nano.Operation.VERBATIM, KeyMap.alt('v'));
         this.keys.bind(Nano.Operation.INSERT, KeyMap.ctrl('I'), KeyMap.ctrl('M'));
         this.keys.bind(Nano.Operation.DELETE, KeyMap.ctrl('D'), KeyMap.key(this.terminal, InfoCmp.Capability.key_dc));
         this.keys.bind(Nano.Operation.BACKSPACE, KeyMap.ctrl('H'));
         this.keys.bind(Nano.Operation.CUT_TO_END, KeyMap.alt('t'));
         this.keys.bind(Nano.Operation.JUSTIFY_FILE, KeyMap.alt('j'));
         this.keys.bind(Nano.Operation.AUTO_INDENT, KeyMap.alt('i'));
         this.keys.bind(Nano.Operation.CUT_TO_END_TOGGLE, KeyMap.alt('k'));
         this.keys.bind(Nano.Operation.TABS_TO_SPACE, KeyMap.alt('q'));
      } else {
         this.keys.bind(Nano.Operation.NEXT_PAGE, " ", "f");
         this.keys.bind(Nano.Operation.PREV_PAGE, "b");
      }

      this.keys.bind(Nano.Operation.NEXT_PAGE, KeyMap.ctrl('V'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f8));
      this.keys.bind(Nano.Operation.PREV_PAGE, KeyMap.ctrl('Y'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f7));
      this.keys.bind(Nano.Operation.HELP, KeyMap.ctrl('G'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f1));
      this.keys.bind(Nano.Operation.QUIT, KeyMap.ctrl('X'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f2));
      this.keys.bind(Nano.Operation.READ, KeyMap.ctrl('R'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f5));
      this.keys.bind(Nano.Operation.SEARCH, KeyMap.ctrl('W'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f6));
      this.keys.bind(Nano.Operation.CUR_POS, KeyMap.ctrl('C'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f11));
      this.keys.bind(Nano.Operation.TO_SPELL, KeyMap.ctrl('T'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f11));
      this.keys.bind(Nano.Operation.GOTO, KeyMap.ctrl('_'), KeyMap.key(this.terminal, InfoCmp.Capability.key_f13), KeyMap.alt('g'));
      this.keys.bind(Nano.Operation.NEXT_SEARCH, KeyMap.key(this.terminal, InfoCmp.Capability.key_f16), KeyMap.alt('w'));
      this.keys.bind(Nano.Operation.RIGHT, KeyMap.ctrl('F'));
      this.keys.bind(Nano.Operation.LEFT, KeyMap.ctrl('B'));
      this.keys.bind(Nano.Operation.NEXT_WORD, KeyMap.translate("^[[1;5C"));
      this.keys.bind(Nano.Operation.PREV_WORD, KeyMap.translate("^[[1;5D"));
      this.keys.bind(Nano.Operation.NEXT_WORD, KeyMap.alt(KeyMap.key(this.terminal, InfoCmp.Capability.key_right)));
      this.keys.bind(Nano.Operation.PREV_WORD, KeyMap.alt(KeyMap.key(this.terminal, InfoCmp.Capability.key_left)));
      this.keys.bind(Nano.Operation.NEXT_WORD, KeyMap.alt(KeyMap.translate("^[[C")));
      this.keys.bind(Nano.Operation.PREV_WORD, KeyMap.alt(KeyMap.translate("^[[D")));
      this.keys.bind(Nano.Operation.LSP_SUGGESTION, KeyMap.ctrl(' '));
      this.keys.bind(Nano.Operation.UP, KeyMap.ctrl('P'));
      this.keys.bind(Nano.Operation.DOWN, KeyMap.ctrl('N'));
      this.keys.bind(Nano.Operation.BEGINNING_OF_LINE, KeyMap.ctrl('A'), KeyMap.key(this.terminal, InfoCmp.Capability.key_home));
      this.keys.bind(Nano.Operation.END_OF_LINE, KeyMap.ctrl('E'), KeyMap.key(this.terminal, InfoCmp.Capability.key_end));
      this.keys.bind(Nano.Operation.BEGINNING_OF_PARAGRAPH, KeyMap.alt('('), KeyMap.alt('9'));
      this.keys.bind(Nano.Operation.END_OF_PARAGRAPH, KeyMap.alt(')'), KeyMap.alt('0'));
      this.keys.bind(Nano.Operation.FIRST_LINE, KeyMap.alt('\\'), KeyMap.alt('|'));
      this.keys.bind(Nano.Operation.LAST_LINE, KeyMap.alt('/'), KeyMap.alt('?'));
      this.keys.bind(Nano.Operation.MATCHING, KeyMap.alt(']'));
      this.keys.bind(Nano.Operation.SCROLL_UP, KeyMap.alt('-'), KeyMap.alt('_'));
      this.keys.bind(Nano.Operation.SCROLL_DOWN, KeyMap.alt('+'), KeyMap.alt('='));
      this.keys.bind(Nano.Operation.PREV_BUFFER, KeyMap.alt('<'));
      this.keys.bind(Nano.Operation.NEXT_BUFFER, KeyMap.alt('>'));
      this.keys.bind(Nano.Operation.PREV_BUFFER, KeyMap.alt(','));
      this.keys.bind(Nano.Operation.NEXT_BUFFER, KeyMap.alt('.'));
      this.keys.bind(Nano.Operation.COUNT, KeyMap.alt('d'));
      this.keys.bind(Nano.Operation.CLEAR_SCREEN, KeyMap.ctrl('L'));
      this.keys.bind(Nano.Operation.HELP, KeyMap.alt('x'));
      this.keys.bind(Nano.Operation.CONSTANT_CURSOR, KeyMap.alt('c'));
      this.keys.bind(Nano.Operation.ONE_MORE_LINE, KeyMap.alt('o'));
      this.keys.bind(Nano.Operation.SMOOTH_SCROLLING, KeyMap.alt('s'));
      this.keys.bind(Nano.Operation.MOUSE_SUPPORT, KeyMap.alt('m'));
      this.keys.bind(Nano.Operation.WHITESPACE, KeyMap.alt('p'));
      this.keys.bind(Nano.Operation.HIGHLIGHT, KeyMap.alt('y'));
      this.keys.bind(Nano.Operation.SMART_HOME_KEY, KeyMap.alt('h'));
      this.keys.bind(Nano.Operation.WRAP, KeyMap.alt('l'));
      this.keys.bind(Nano.Operation.BACKUP, KeyMap.alt('b'));
      this.keys.bind(Nano.Operation.NUMBERS, KeyMap.alt('n'));
      this.keys.bind(Nano.Operation.UP, KeyMap.key(this.terminal, InfoCmp.Capability.key_up));
      this.keys.bind(Nano.Operation.DOWN, KeyMap.key(this.terminal, InfoCmp.Capability.key_down));
      this.keys.bind(Nano.Operation.RIGHT, KeyMap.key(this.terminal, InfoCmp.Capability.key_right));
      this.keys.bind(Nano.Operation.LEFT, KeyMap.key(this.terminal, InfoCmp.Capability.key_left));
      this.keys.bind(Nano.Operation.MOUSE_EVENT, MouseSupport.keys(this.terminal));
      this.keys.bind(Nano.Operation.TOGGLE_SUSPENSION, KeyMap.alt('z'));
      this.keys.bind(Nano.Operation.NEXT_PAGE, KeyMap.key(this.terminal, InfoCmp.Capability.key_npage));
      this.keys.bind(Nano.Operation.PREV_PAGE, KeyMap.key(this.terminal, InfoCmp.Capability.key_ppage));
   }

   class Box {
      private final int xi;
      private final int xl;
      private final int yi;
      private final int yl;
      private List<AttributedString> lines;
      private int selected = 0;
      private int selectedInView = 0;
      private final int height;
      private AttributedStyle selectedStyle = AttributedStyle.DEFAULT;
      private List<AttributedString> visibleLines;

      private Box(int xi, int yi, int xl, int yl) {
         this.xi = xi;
         this.yi = yi;
         this.xl = xl;
         this.yl = yl;
         this.height = Math.max(1, yl - yi - 1);
      }

      private void setLines(List<AttributedString> lines) {
         if (lines == null) {
            this.lines = Collections.emptyList();
            this.visibleLines = Collections.emptyList();
         } else {
            this.lines = lines;
            if (this.height > 0 && !lines.isEmpty()) {
               this.visibleLines = lines.subList(0, Math.min(this.height, lines.size()));
            } else {
               this.visibleLines = Collections.emptyList();
            }
         }
      }

      public int getSelected() {
         return this.selected;
      }

      private void setSelectedStyle(AttributedStyle selectedStyle) {
         this.selectedStyle = selectedStyle;
      }

      private AttributedStyle getSelectedStyle() {
         return this.selectedStyle;
      }

      private void down() {
         this.selected = Math.floorMod(this.selected + 1, this.lines.size());
         if (this.scrollable() && this.selectedInView >= this.height - 1) {
            if (this.selected == 0) {
               this.selectedInView = 0;
               this.visibleLines = this.lines.subList(0, this.height);
            } else {
               this.visibleLines = this.lines.subList(this.selected - this.height + 1, this.selected + 1);
            }
         } else {
            this.selectedInView++;
         }
      }

      private void up() {
         this.selected = Math.floorMod(this.selected - 1, this.lines.size());
         if (this.scrollable() && this.selectedInView <= 0) {
            if (this.selected == this.lines.size() - 1) {
               this.selectedInView = this.height - 1;
               this.visibleLines = this.lines.subList(this.lines.size() - this.height, this.lines.size());
            } else {
               this.visibleLines = this.lines.subList(this.selected, this.selected + this.height);
            }
         } else {
            this.selectedInView--;
         }
      }

      private boolean scrollable() {
         return this.height < this.lines.size();
      }

      private int getSelectedInView() {
         return Math.floorMod(this.selectedInView, this.lines.size());
      }

      public void draw(List<AttributedString> screenLines) {
         this.addBoxBorders(screenLines);
         this.addBoxLines(screenLines);
      }

      protected void addBoxBorders(List<AttributedString> newLines) {
         if (newLines != null && !newLines.isEmpty()) {
            if (this.yi < newLines.size() && this.yl < newLines.size()) {
               int width = this.xl - this.xi;
               if (width > 0) {
                  if (width < 3) {
                     width = 3;
                  }

                  if (this.yi >= 0) {
                     AttributedStringBuilder top = new AttributedStringBuilder(width);
                     top.append('┌');
                     top.append('─', width - 2);
                     top.append('┐');
                     this.setLineInBox(newLines, this.yi, top.toAttributedString(), true);
                  }

                  AttributedStringBuilder sides = new AttributedStringBuilder(width);
                  sides.append('│');
                  sides.append(' ', width - 2);
                  sides.append('│');
                  AttributedString side = sides.toAttributedString();
                  int startY = Math.max(this.yi + 1, 0);
                  int endY = Math.min(this.yl, newLines.size() - 1);

                  for (int y = startY; y < endY; y++) {
                     this.setLineInBox(newLines, y, side, true);
                  }

                  if (this.yl >= 0 && this.yl < newLines.size()) {
                     AttributedStringBuilder bottom = new AttributedStringBuilder(width);
                     bottom.append('└');
                     bottom.append('─', width - 2);
                     bottom.append('┘');
                     this.setLineInBox(newLines, this.yl, bottom.toAttributedString(), true);
                  }
               }
            }
         }
      }

      protected void setLineInBox(List<AttributedString> newLines, int y, AttributedString line, boolean borders) {
         if (y >= 0 && y < newLines.size()) {
            int start = this.xi;
            int end = this.xl;
            if (!borders) {
               start++;
               end--;
            }

            start = Math.max(0, start);
            end = Math.min(end, Nano.this.size.getColumns() - 1);
            AttributedString currLine = newLines.get(y);
            AttributedStringBuilder newLine = new AttributedStringBuilder(Math.max(end + 1, currLine.length() + 1));
            int currLength = currLine.length();
            boolean hasNewline = false;
            if (currLength > 0 && currLine.charAt(currLength - 1) == '\n') {
               currLength--;
               hasNewline = true;
            }

            newLine.append(currLine, 0, Math.min(start, currLength));
            if (start > currLength) {
               newLine.append(' ', start - currLength);
            }

            int contentWidth = Math.min(line.length(), end - start);
            if (contentWidth > 0) {
               newLine.append(line, 0, contentWidth);
            }

            int afterBoxStart = start + contentWidth;
            if (afterBoxStart < currLength) {
               newLine.append(currLine, afterBoxStart, currLength);
            }

            if (hasNewline) {
               newLine.append('\n');
            }

            newLines.set(y, newLine.toAttributedString());
         }
      }

      protected void addBoxLines(List<AttributedString> screenLines) {
         if (screenLines != null && !screenLines.isEmpty() && this.visibleLines != null && !this.visibleLines.isEmpty()) {
            int maxLines = this.yl - this.yi - 1;
            if (maxLines > 0) {
               int linesToDisplay = Math.min(this.visibleLines.size(), maxLines);

               for (int i = 0; i < linesToDisplay; i++) {
                  AttributedStringBuilder line = new AttributedStringBuilder(this.xl - this.xi - 2);
                  AttributedStyle background = AttributedStyle.DEFAULT;
                  if (i == this.getSelectedInView()) {
                     background = this.getSelectedStyle();
                  }

                  line.append(this.visibleLines.get(i), background);
                  line.style(background);
                  line.append(' ', this.xl - this.xi - line.length() - 2);
                  int lineY = this.yi + 1 + i;
                  if (lineY < screenLines.size()) {
                     this.setLineInBox(screenLines, lineY, line.toAttributedString(), false);
                  }
               }
            }
         }
      }
   }

   protected class Buffer {
      String file;
      Charset charset;
      Nano.WriteFormat format = Nano.WriteFormat.UNIX;
      List<String> lines;
      int firstLineToDisplay;
      int firstColumnToDisplay = 0;
      int offsetInLineToDisplay;
      int line;
      List<LinkedList<Integer>> offsets = new ArrayList<>();
      int offsetInLine;
      int column;
      int wantedColumn;
      boolean uncut = false;
      int[] markPos = new int[]{-1, -1};
      SyntaxHighlighter syntaxHighlighter;
      boolean dirty;

      protected Buffer(String file) {
         this.file = file;
         this.syntaxHighlighter = SyntaxHighlighter.build(Nano.this.syntaxFiles, file, Nano.this.syntaxName, Nano.this.nanorcIgnoreErrors);
      }

      public void setDirty(boolean dirty) {
         this.dirty = dirty;
      }

      public String getFile() {
         return this.file;
      }

      public List<String> getLines() {
         return this.lines;
      }

      public int getFirstLineToDisplay() {
         return this.firstLineToDisplay;
      }

      public int getFirstColumnToDisplay() {
         return this.firstColumnToDisplay;
      }

      public int getOffsetInLineToDisplay() {
         return this.offsetInLineToDisplay;
      }

      public int getLine() {
         return this.line;
      }

      public Charset getCharset() {
         return this.charset;
      }

      public Nano.WriteFormat getFormat() {
         return this.format;
      }

      public boolean isDirty() {
         return this.dirty;
      }

      public SyntaxHighlighter getSyntaxHighlighter() {
         return this.syntaxHighlighter;
      }

      public int getOffsetInLine() {
         return this.offsetInLine;
      }

      public int getColumn() {
         return this.column;
      }

      public void open() throws IOException {
         if (this.lines == null) {
            this.lines = new ArrayList<>();
            this.lines.add("");
            this.charset = Charset.defaultCharset();
            this.computeAllOffsets();
            if (this.file != null) {
               Path path = Nano.this.root.resolve(this.file);
               if (Files.isDirectory(path)) {
                  Nano.this.setMessage("\"" + this.file + "\" is a directory");
               } else {
                  try {
                     InputStream fis = Files.newInputStream(path);

                     try {
                        this.read(fis);
                     } catch (Throwable var6) {
                        if (fis != null) {
                           try {
                              fis.close();
                           } catch (Throwable var5) {
                              var6.addSuppressed(var5);
                           }
                        }

                        throw var6;
                     }

                     if (fis != null) {
                        fis.close();
                     }
                  } catch (IOException var7) {
                     Nano.this.setMessage("Error reading " + this.file + ": " + var7.getMessage());
                  }
               }
            }
         }
      }

      public void open(InputStream is) throws IOException {
         if (this.lines == null) {
            this.lines = new ArrayList<>();
            this.lines.add("");
            this.charset = Charset.defaultCharset();
            this.computeAllOffsets();
            this.read(is);
         }
      }

      public void read(InputStream fis) throws IOException {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         byte[] buffer = new byte[4096];

         int remaining;
         while ((remaining = fis.read(buffer)) > 0) {
            bos.write(buffer, 0, remaining);
         }

         byte[] bytes = bos.toByteArray();

         try {
            UniversalDetector detector = new UniversalDetector(null);
            detector.handleData(bytes, 0, bytes.length);
            detector.dataEnd();
            if (detector.getDetectedCharset() != null) {
               this.charset = Charset.forName(detector.getDetectedCharset());
            }
         } catch (Throwable var10) {
         }

         BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes), this.charset));

         try {
            this.lines.clear();

            String line;
            while ((line = reader.readLine()) != null) {
               this.lines.add(line);
            }
         } catch (Throwable var11) {
            try {
               reader.close();
            } catch (Throwable var9) {
               var11.addSuppressed(var9);
            }

            throw var11;
         }

         reader.close();
         if (this.lines.isEmpty()) {
            this.lines.add("");
         }

         this.computeAllOffsets();
         this.moveToChar(0);
      }

      private int charPosition(int displayPosition) {
         return this.charPosition(this.line, displayPosition, Nano.CursorMovement.STILL);
      }

      private int charPosition(int displayPosition, Nano.CursorMovement move) {
         return this.charPosition(this.line, displayPosition, move);
      }

      private int charPosition(int line, int displayPosition) {
         return this.charPosition(line, displayPosition, Nano.CursorMovement.STILL);
      }

      private int charPosition(int line, int displayPosition, Nano.CursorMovement move) {
         int out = this.lines.get(line).length();
         if (!this.lines.get(line).contains("\t") || displayPosition == 0) {
            out = displayPosition;
         } else if (displayPosition < this.length(this.lines.get(line))) {
            int rdiff = 0;
            int ldiff = 0;

            for (int i = 0; i < this.lines.get(line).length(); i++) {
               int dp = this.length(this.lines.get(line).substring(0, i));
               if (move == Nano.CursorMovement.LEFT) {
                  if (dp > displayPosition) {
                     break;
                  }

                  out = i;
               } else if (move == Nano.CursorMovement.RIGHT) {
                  if (dp >= displayPosition) {
                     out = i;
                     break;
                  }
               } else if (move == Nano.CursorMovement.STILL) {
                  if (dp > displayPosition) {
                     rdiff = dp - displayPosition;
                     if (rdiff < ldiff) {
                        out = i;
                     }
                     break;
                  }

                  ldiff = displayPosition - dp;
                  out = i;
               }
            }
         }

         return out;
      }

      String blanks(int nb) {
         StringBuilder sb = new StringBuilder();

         for (int i = 0; i < nb; i++) {
            sb.append(' ');
         }

         return sb.toString();
      }

      public void insert(String insert) {
         String text = this.lines.get(this.line);
         int pos = this.charPosition(this.offsetInLine + this.column);
         insert = insert.replaceAll("\r\n", "\n");
         insert = insert.replaceAll("\r", "\n");
         if (Nano.this.tabsToSpaces && insert.length() == 1 && insert.charAt(0) == '\t') {
            int len = pos == text.length() ? this.length(text + insert) : this.length(text.substring(0, pos) + insert);
            insert = this.blanks(len - this.offsetInLine - this.column);
         }

         if (Nano.this.autoIndent && insert.length() == 1 && insert.charAt(0) == '\n') {
            String currentLine = this.lines.get(this.line);
            int indentLength = 0;

            while (indentLength < currentLine.length() && (currentLine.charAt(indentLength) == ' ' || currentLine.charAt(indentLength) == '\t')) {
               indentLength++;
            }

            if (indentLength > 0) {
               insert = insert + currentLine.substring(0, indentLength);
            }
         }

         String tail = "";
         String mod;
         if (pos == text.length()) {
            mod = text + insert;
         } else {
            mod = text.substring(0, pos) + insert;
            tail = text.substring(pos);
         }

         List<String> ins = new ArrayList<>();
         int last = 0;

         for (int idx = mod.indexOf(10, last); idx >= 0; idx = mod.indexOf(10, last)) {
            ins.add(mod.substring(last, idx));
            last = idx + 1;
         }

         ins.add(mod.substring(last) + tail);
         int curPos = this.length(mod.substring(last));
         this.lines.set(this.line, ins.get(0));
         this.offsets.set(this.line, this.computeOffsets(ins.get(0)));

         for (int i = 1; i < ins.size(); i++) {
            this.line++;
            this.lines.add(this.line, ins.get(i));
            this.offsets.add(this.line, this.computeOffsets(ins.get(i)));
         }

         this.moveToChar(curPos);
         this.ensureCursorVisible();
         this.dirty = true;
      }

      void computeAllOffsets() {
         this.offsets.clear();

         for (String text : this.lines) {
            this.offsets.add(this.computeOffsets(text));
         }
      }

      LinkedList<Integer> computeOffsets(String line) {
         String text = new AttributedStringBuilder().tabs(Nano.this.tabs).append(line).toString();
         int width = Nano.this.size.getColumns() - (Nano.this.printLineNumbers ? 8 : 0);
         LinkedList<Integer> offsets = new LinkedList<>();
         offsets.add(0);
         if (Nano.this.wrapping) {
            int last = 0;
            int prevword = 0;
            boolean inspace = false;

            for (int i = 0; i < text.length(); i++) {
               if (this.isBreakable(text.charAt(i))) {
                  inspace = true;
               } else if (inspace) {
                  prevword = i;
                  inspace = false;
               }

               if (i == last + width - 1) {
                  if (prevword == last) {
                     prevword = i;
                  }

                  offsets.add(prevword);
                  last = prevword;
               }
            }
         }

         return offsets;
      }

      public boolean isBreakable(char ch) {
         return !Nano.this.atBlanks || ch == ' ';
      }

      public void moveToChar(int pos) {
         this.moveToChar(pos, Nano.CursorMovement.STILL);
      }

      public void moveToChar(int pos, Nano.CursorMovement move) {
         if (!Nano.this.wrapping) {
            if (pos > this.column && pos - this.firstColumnToDisplay + 1 > this.width()) {
               this.firstColumnToDisplay = this.offsetInLine + this.column - 6;
            } else if (pos < this.column && this.firstColumnToDisplay + 5 > pos) {
               this.firstColumnToDisplay = Math.max(0, this.firstColumnToDisplay - this.width() + 5);
            }
         }

         if (this.lines.get(this.line).contains("\t")) {
            int cpos = this.charPosition(pos, move);
            if (cpos < this.lines.get(this.line).length()) {
               pos = this.length(this.lines.get(this.line).substring(0, cpos));
            } else {
               pos = this.length(this.lines.get(this.line));
            }
         }

         this.offsetInLine = this.prevLineOffset(this.line, pos + 1).get();
         this.column = pos - this.offsetInLine;
      }

      public void delete(int count) {
         do {
            count--;
         } while (count >= 0 && this.moveRight(1) && this.backspace(1));
      }

      public boolean backspace(int count) {
         while (count > 0) {
            String text = this.lines.get(this.line);
            int pos = this.charPosition(this.offsetInLine + this.column);
            if (pos == 0) {
               if (this.line == 0) {
                  this.bof();
                  return false;
               }

               String prev = this.lines.get(--this.line);
               this.lines.set(this.line, prev + text);
               this.offsets.set(this.line, this.computeOffsets(prev + text));
               this.moveToChar(this.length(prev));
               this.lines.remove(this.line + 1);
               this.offsets.remove(this.line + 1);
               count--;
            } else {
               int nb = Math.min(pos, count);
               int curPos = this.length(text.substring(0, pos - nb));
               text = text.substring(0, pos - nb) + text.substring(pos);
               this.lines.set(this.line, text);
               this.offsets.set(this.line, this.computeOffsets(text));
               this.moveToChar(curPos);
               count -= nb;
            }

            this.dirty = true;
         }

         this.ensureCursorVisible();
         return true;
      }

      public boolean moveLeft(int chars) {
         boolean ret = true;

         while (--chars >= 0) {
            if (this.offsetInLine + this.column > 0) {
               this.moveToChar(this.offsetInLine + this.column - 1, Nano.CursorMovement.LEFT);
            } else {
               if (this.line <= 0) {
                  this.bof();
                  ret = false;
                  break;
               }

               this.line--;
               this.moveToChar(this.length(this.getLine(this.line)));
            }
         }

         this.wantedColumn = this.column;
         this.ensureCursorVisible();
         return ret;
      }

      public boolean moveRight(int chars) {
         return this.moveRight(chars, false);
      }

      public int width() {
         return Nano.this.size.getColumns() - (Nano.this.printLineNumbers ? 8 : 0) - (Nano.this.wrapping ? 0 : 1) - (this.firstColumnToDisplay > 0 ? 1 : 0);
      }

      public boolean moveRight(int chars, boolean fromBeginning) {
         if (fromBeginning) {
            this.firstColumnToDisplay = 0;
            this.offsetInLine = 0;
            this.column = 0;
            chars = Math.min(chars, this.length(this.getLine(this.line)));
         }

         boolean ret = true;

         while (--chars >= 0) {
            int len = this.length(this.getLine(this.line));
            if (this.offsetInLine + this.column + 1 <= len) {
               this.moveToChar(this.offsetInLine + this.column + 1, Nano.CursorMovement.RIGHT);
            } else {
               if (this.getLine(this.line + 1) == null) {
                  this.eof();
                  ret = false;
                  break;
               }

               this.line++;
               this.firstColumnToDisplay = 0;
               this.offsetInLine = 0;
               this.column = 0;
            }
         }

         this.wantedColumn = this.column;
         this.ensureCursorVisible();
         return ret;
      }

      public void moveDown(int lines) {
         this.cursorDown(lines);
         this.ensureCursorVisible();
      }

      public void moveUp(int lines) {
         this.cursorUp(lines);
         this.ensureCursorVisible();
      }

      private Optional<Integer> prevLineOffset(int line, int offsetInLine) {
         if (line >= this.offsets.size()) {
            return Optional.empty();
         } else {
            Iterator<Integer> it = this.offsets.get(line).descendingIterator();

            while (it.hasNext()) {
               int off = it.next();
               if (off < offsetInLine) {
                  return Optional.of(off);
               }
            }

            return Optional.empty();
         }
      }

      private Optional<Integer> nextLineOffset(int line, int offsetInLine) {
         return line >= this.offsets.size() ? Optional.empty() : this.offsets.get(line).stream().filter(o -> o > offsetInLine).findFirst();
      }

      public void moveDisplayDown(int lines) {
         int height = Nano.this.size.getRows() - this.computeHeader().size() - Nano.this.computeFooter().size();

         while (--lines >= 0) {
            int lastLineToDisplay = this.firstLineToDisplay;
            if (!Nano.this.wrapping) {
               lastLineToDisplay += height - 1;
            } else {
               int off = this.offsetInLineToDisplay;

               for (int l = 0; l < height - 1; l++) {
                  Optional<Integer> next = this.nextLineOffset(lastLineToDisplay, off);
                  if (next.isPresent()) {
                     off = next.get();
                  } else {
                     off = 0;
                     lastLineToDisplay++;
                  }
               }
            }

            if (this.getLine(lastLineToDisplay) == null) {
               this.eof();
               return;
            }

            Optional<Integer> next = this.nextLineOffset(this.firstLineToDisplay, this.offsetInLineToDisplay);
            if (next.isPresent()) {
               this.offsetInLineToDisplay = next.get();
            } else {
               this.offsetInLineToDisplay = 0;
               this.firstLineToDisplay++;
            }
         }
      }

      public void moveDisplayUp(int lines) {
         int width = Nano.this.size.getColumns() - (Nano.this.printLineNumbers ? 8 : 0);

         while (--lines >= 0) {
            if (this.offsetInLineToDisplay > 0) {
               this.offsetInLineToDisplay = Math.max(0, this.offsetInLineToDisplay - (width - 1));
            } else {
               if (this.firstLineToDisplay <= 0) {
                  this.bof();
                  return;
               }

               this.firstLineToDisplay--;
               this.offsetInLineToDisplay = this.prevLineOffset(this.firstLineToDisplay, Integer.MAX_VALUE).get();
            }
         }
      }

      private void cursorDown(int lines) {
         this.firstColumnToDisplay = 0;

         while (--lines >= 0) {
            if (!Nano.this.wrapping) {
               if (this.getLine(this.line + 1) == null) {
                  this.bof();
                  break;
               }

               this.line++;
               this.offsetInLine = 0;
               this.column = Math.min(this.length(this.getLine(this.line)), this.wantedColumn);
            } else {
               String txt = this.getLine(this.line);
               Optional<Integer> off = this.nextLineOffset(this.line, this.offsetInLine);
               if (off.isPresent()) {
                  this.offsetInLine = off.get();
               } else {
                  if (this.getLine(this.line + 1) == null) {
                     this.eof();
                     break;
                  }

                  this.line++;
                  this.offsetInLine = 0;
                  txt = this.getLine(this.line);
               }

               int next = this.nextLineOffset(this.line, this.offsetInLine).orElse(this.length(txt));
               this.column = Math.min(this.wantedColumn, next - this.offsetInLine);
            }
         }

         this.moveToChar(this.offsetInLine + this.column);
      }

      private void cursorUp(int lines) {
         this.firstColumnToDisplay = 0;

         while (--lines >= 0) {
            if (!Nano.this.wrapping) {
               if (this.line <= 0) {
                  this.bof();
                  break;
               }

               this.line--;
               this.column = Math.min(this.length(this.getLine(this.line)) - this.offsetInLine, this.wantedColumn);
            } else {
               Optional<Integer> prev = this.prevLineOffset(this.line, this.offsetInLine);
               if (prev.isPresent()) {
                  this.offsetInLine = prev.get();
               } else {
                  if (this.line <= 0) {
                     this.bof();
                     break;
                  }

                  this.line--;
                  this.offsetInLine = this.prevLineOffset(this.line, Integer.MAX_VALUE).get();
                  int next = this.nextLineOffset(this.line, this.offsetInLine).orElse(this.length(this.getLine(this.line)));
                  this.column = Math.min(this.wantedColumn, next - this.offsetInLine);
               }
            }
         }

         this.moveToChar(this.offsetInLine + this.column);
      }

      void ensureCursorVisible() {
         List<AttributedString> header = this.computeHeader();
         int rwidth = Nano.this.size.getColumns();
         int height = Nano.this.size.getRows() - header.size() - Nano.this.computeFooter().size();

         while (this.line < this.firstLineToDisplay || this.line == this.firstLineToDisplay && this.offsetInLine < this.offsetInLineToDisplay) {
            this.moveDisplayUp(Nano.this.smoothScrolling ? 1 : height / 2);
         }

         while (true) {
            int cursor = this.computeCursorPosition(header.size() * Nano.this.size.getColumns() + (Nano.this.printLineNumbers ? 8 : 0), rwidth);
            if (cursor < (height + header.size()) * rwidth) {
               return;
            }

            this.moveDisplayDown(Nano.this.smoothScrolling ? 1 : height / 2);
         }
      }

      void eof() {
      }

      void bof() {
      }

      void resetDisplay() {
         this.column = this.offsetInLine + this.column;
         this.moveRight(this.column, true);
      }

      String getLine(int line) {
         return line < this.lines.size() ? this.lines.get(line) : null;
      }

      String getTitle() {
         return this.file != null ? "File: " + this.file : "New Buffer";
      }

      List<AttributedString> computeHeader() {
         String left = Nano.this.getTitle();
         String middle = null;
         String right = this.dirty ? "Modified" : "        ";
         int width = Nano.this.size.getColumns();
         int mstart = 2 + left.length() + 1;
         int mend = width - 2 - 8;
         if (this.file == null) {
            middle = "New Buffer";
         } else {
            int max = mend - mstart;
            String src = this.file;
            if ("File: ".length() + src.length() > max) {
               int lastSep = src.lastIndexOf(47);
               if (lastSep > 0) {
                  String p1 = src.substring(lastSep);
                  String p0 = src.substring(0, lastSep);

                  while (p0.startsWith(".")) {
                     p0 = p0.substring(1);
                  }

                  int nb = max - p1.length() - "File: ...".length();
                  int cut = Math.max(0, Math.min(p0.length(), p0.length() - nb));
                  middle = "File: ..." + p0.substring(cut) + p1;
               }

               if (middle == null || middle.length() > max) {
                  left = null;
                  max = mend - 2;
                  int nb = max - "File: ...".length();
                  int cut = Math.max(0, Math.min(src.length(), src.length() - nb));
                  middle = "File: ..." + src.substring(cut);
                  if (middle.length() > max) {
                     middle = middle.substring(0, max);
                  }
               }
            } else {
               middle = "File: " + src;
            }
         }

         int pos = 0;
         AttributedStringBuilder sb = new AttributedStringBuilder();
         sb.style(AttributedStyle.INVERSE);
         sb.append("  ");
         pos += 2;
         if (left != null) {
            sb.append(left);
            pos += left.length();
            sb.append(" ");
            pos++;

            for (int i = 1; i < (Nano.this.size.getColumns() - middle.length()) / 2 - left.length() - 1 - 2; i++) {
               sb.append(" ");
               pos++;
            }
         }

         sb.append(middle);

         for (int var18 = pos + middle.length(); var18 < width - 8 - 2; var18++) {
            sb.append(" ");
         }

         sb.append(right);
         sb.append("  \n");
         return Nano.this.oneMoreLine ? Collections.singletonList(sb.toAttributedString()) : Arrays.asList(sb.toAttributedString(), new AttributedString("\n"));
      }

      void highlightDisplayedLine(int curLine, int curOffset, int nextOffset, AttributedStringBuilder line) {
         AttributedString disp = Nano.this.highlight
            ? this.syntaxHighlighter.highlight(new AttributedStringBuilder().tabs(Nano.this.tabs).append(this.getLine(curLine)))
            : new AttributedStringBuilder().tabs(Nano.this.tabs).append(this.getLine(curLine)).toAttributedString();
         int[] hls = this.highlightStart();
         int[] hle = this.highlightEnd();
         if (hls[0] == -1 || hle[0] == -1) {
            line.append(disp.columnSubSequence(curOffset, nextOffset));
         } else if (hls[0] == hle[0]) {
            if (curLine == hls[0]) {
               if (hls[1] > nextOffset) {
                  line.append(disp.columnSubSequence(curOffset, nextOffset));
               } else if (hls[1] < curOffset) {
                  if (hle[1] > nextOffset) {
                     line.append(disp.columnSubSequence(curOffset, nextOffset), AttributedStyle.INVERSE);
                  } else if (hle[1] > curOffset) {
                     line.append(disp.columnSubSequence(curOffset, hle[1]), AttributedStyle.INVERSE);
                     line.append(disp.columnSubSequence(hle[1], nextOffset));
                  } else {
                     line.append(disp.columnSubSequence(curOffset, nextOffset));
                  }
               } else {
                  line.append(disp.columnSubSequence(curOffset, hls[1]));
                  if (hle[1] > nextOffset) {
                     line.append(disp.columnSubSequence(hls[1], nextOffset), AttributedStyle.INVERSE);
                  } else {
                     line.append(disp.columnSubSequence(hls[1], hle[1]), AttributedStyle.INVERSE);
                     line.append(disp.columnSubSequence(hle[1], nextOffset));
                  }
               }
            } else {
               line.append(disp.columnSubSequence(curOffset, nextOffset));
            }
         } else if (curLine > hls[0] && curLine < hle[0]) {
            line.append(disp.columnSubSequence(curOffset, nextOffset), AttributedStyle.INVERSE);
         } else if (curLine == hls[0]) {
            if (hls[1] > nextOffset) {
               line.append(disp.columnSubSequence(curOffset, nextOffset));
            } else if (hls[1] < curOffset) {
               line.append(disp.columnSubSequence(curOffset, nextOffset), AttributedStyle.INVERSE);
            } else {
               line.append(disp.columnSubSequence(curOffset, hls[1]));
               line.append(disp.columnSubSequence(hls[1], nextOffset), AttributedStyle.INVERSE);
            }
         } else if (curLine == hle[0]) {
            if (hle[1] < curOffset) {
               line.append(disp.columnSubSequence(curOffset, nextOffset));
            } else if (hle[1] > nextOffset) {
               line.append(disp.columnSubSequence(curOffset, nextOffset), AttributedStyle.INVERSE);
            } else {
               line.append(disp.columnSubSequence(curOffset, hle[1]), AttributedStyle.INVERSE);
               line.append(disp.columnSubSequence(hle[1], nextOffset));
            }
         } else {
            line.append(disp.columnSubSequence(curOffset, nextOffset));
         }
      }

      List<AttributedString> getDisplayedLines(int nbLines, List<Nano.Diagnostic> diagnostics) {
         AttributedStyle s = AttributedStyle.DEFAULT.foreground(8);
         AttributedString cut = new AttributedString("…", s);
         AttributedString ret = new AttributedString("↩", s);
         List<AttributedString> newLines = new ArrayList<>();
         int rwidth = Nano.this.size.getColumns();
         int width = rwidth - (Nano.this.printLineNumbers ? 8 : 0);
         int curLine = this.firstLineToDisplay;
         int curOffset = this.offsetInLineToDisplay;
         int prevLine = -1;
         if (Nano.this.highlight) {
            this.syntaxHighlighter.reset();

            for (int i = Math.max(0, curLine - nbLines); i < curLine; i++) {
               this.syntaxHighlighter.highlight(this.getLine(i));
            }
         }

         for (int terminalLine = 0; terminalLine < nbLines; terminalLine++) {
            AttributedStringBuilder line = new AttributedStringBuilder().tabs(Nano.this.tabs);
            if (Nano.this.printLineNumbers && curLine < this.lines.size()) {
               line.style(s);
               if (curLine != prevLine) {
                  line.append(String.format("%7d ", curLine + 1));
               } else {
                  line.append("      ‧ ");
               }

               line.style(AttributedStyle.DEFAULT);
               prevLine = curLine;
            }

            if (curLine < this.lines.size()) {
               if (!Nano.this.wrapping) {
                  AttributedString disp = new AttributedStringBuilder().tabs(Nano.this.tabs).append(this.getLine(curLine)).toAttributedString();
                  if (this.line == curLine) {
                     int cutCount = 1;
                     if (this.firstColumnToDisplay > 0) {
                        line.append(cut);
                        cutCount = 2;
                     }

                     if (disp.columnLength() - this.firstColumnToDisplay >= width - (cutCount - 1) * cut.columnLength()) {
                        this.highlightDisplayedLine(curLine, this.firstColumnToDisplay, this.firstColumnToDisplay + width - cutCount * cut.columnLength(), line);
                        line.append(cut);
                     } else {
                        this.highlightDisplayedLine(curLine, this.firstColumnToDisplay, disp.columnLength(), line);
                     }
                  } else if (disp.columnLength() >= width) {
                     this.highlightDisplayedLine(curLine, 0, width - cut.columnLength(), line);
                     line.append(cut);
                  } else {
                     this.highlightDisplayedLine(curLine, 0, disp.columnLength(), line);
                  }

                  curLine++;
               } else {
                  Optional<Integer> nextOffset = this.nextLineOffset(curLine, curOffset);
                  if (nextOffset.isPresent()) {
                     this.highlightDisplayedLine(curLine, curOffset, nextOffset.get(), line);
                     line.append(ret);
                     curOffset = nextOffset.get();
                  } else {
                     this.highlightDisplayedLine(curLine, curOffset, Integer.MAX_VALUE, line);
                     curLine++;
                     curOffset = 0;
                  }
               }
            }

            line.append('\n');
            newLines.add(line.toAttributedString());
         }

         if (diagnostics != null) {
            for (Nano.Diagnostic diagnostic : diagnostics) {
               if (diagnostic.getStartLine() == diagnostic.getEndLine()) {
                  int linex = diagnostic.getEndLine() - this.firstLineToDisplay;
                  AttributedString attributedString = newLines.get(linex);
                  AttributedStringBuilder builder = new AttributedStringBuilder(attributedString.length());
                  builder.append(attributedString.subSequence(0, diagnostic.getStartColumn()));
                  builder.append(
                     attributedString.subSequence(diagnostic.getStartColumn(), diagnostic.getEndColumn()), AttributedStyle.DEFAULT.underline().foreground(1)
                  );
                  builder.append(attributedString.subSequence(diagnostic.getEndColumn(), attributedString.length()));
                  newLines.set(linex, builder.toAttributedString());
                  if (linex == Nano.this.mouseY - 1 && Nano.this.mouseX >= diagnostic.getStartColumn() && Nano.this.mouseX <= diagnostic.getEndColumn()) {
                     String message = diagnostic.getMessage();
                     if (message != null && !message.isEmpty()) {
                        int xi = diagnostic.getStartColumn();
                        int dBoxSize = message.length() + 2;
                        int maxWidth = (int)Math.round((Nano.this.size.getColumns() - xi) * 0.6);
                        int xl = Math.min(dBoxSize + xi, xi + maxWidth);
                        List<AttributedString> boxLines = Nano.this.adjustLines(
                           Collections.singletonList(new AttributedString(message)), dBoxSize - 2, xl - xi - 2
                        );
                        int yi = diagnostic.getStartLine() - this.firstLineToDisplay + 1;
                        int yl = yi + boxLines.size() + 1;
                        if (yl >= newLines.size()) {
                           yi = diagnostic.getStartLine() - this.firstLineToDisplay - boxLines.size() - 2;
                           yl = yi + boxLines.size() + 1;
                           if (yi < 0) {
                              continue;
                           }
                        }

                        Nano.Box box = Nano.this.new Box(xi, yi, xl, yl);
                        box.setLines(boxLines);
                        box.draw(newLines);
                     }
                  }
               }
            }
         }

         return newLines;
      }

      public void moveTo(int x, int y) {
         if (Nano.this.printLineNumbers) {
            x = Math.max(x - 8, 0);
         }

         this.line = this.firstLineToDisplay;
         this.offsetInLine = this.offsetInLineToDisplay;
         this.wantedColumn = x;
         this.cursorDown(y);
      }

      public void gotoLine(int x, int y) {
         this.line = y < this.lines.size() ? y : this.lines.size() - 1;
         x = Math.min(x, this.length(this.lines.get(this.line)));
         this.firstLineToDisplay = this.line > 0 ? this.line - 1 : this.line;
         this.offsetInLine = 0;
         this.offsetInLineToDisplay = 0;
         this.column = 0;
         this.moveRight(x);
      }

      public int getDisplayedCursor() {
         return this.computeCursorPosition(Nano.this.printLineNumbers ? 8 : 0, Nano.this.size.getColumns() + 1);
      }

      private int computeCursorPosition(int cursor, int rwidth) {
         int cur = this.firstLineToDisplay;
         int off = this.offsetInLineToDisplay;

         while (cur < this.line || off < this.offsetInLine) {
            if (!Nano.this.wrapping) {
               cursor += rwidth;
               cur++;
            } else {
               cursor += rwidth;
               Optional<Integer> next = this.nextLineOffset(cur, off);
               if (next.isPresent()) {
                  off = next.get();
               } else {
                  cur++;
                  off = 0;
               }
            }
         }

         if (cur != this.line) {
            throw new IllegalStateException();
         } else {
            if (!Nano.this.wrapping && this.column > this.firstColumnToDisplay + this.width()) {
               while (this.column > this.firstColumnToDisplay + this.width()) {
                  this.firstColumnToDisplay = this.firstColumnToDisplay + this.width();
               }
            }

            return cursor + this.column - this.firstColumnToDisplay + (this.firstColumnToDisplay > 0 ? 1 : 0);
         }
      }

      char getCurrentChar() {
         String str = this.lines.get(this.line);
         if (this.column + this.offsetInLine < str.length()) {
            return str.charAt(this.column + this.offsetInLine);
         } else {
            return (char)(this.line < this.lines.size() - 1 ? '\n' : '\u0000');
         }
      }

      public void prevWord() {
         while (Character.isAlphabetic(this.getCurrentChar()) && this.moveLeft(1)) {
         }

         while (!Character.isAlphabetic(this.getCurrentChar()) && this.moveLeft(1)) {
         }

         while (Character.isAlphabetic(this.getCurrentChar()) && this.moveLeft(1)) {
         }

         this.moveRight(1);
      }

      public void nextWord() {
         while (Character.isAlphabetic(this.getCurrentChar()) && this.moveRight(1)) {
         }

         while (!Character.isAlphabetic(this.getCurrentChar()) && this.moveRight(1)) {
         }
      }

      public void beginningOfLine() {
         this.column = this.offsetInLine = 0;
         this.wantedColumn = 0;
         this.ensureCursorVisible();
      }

      public void endOfLine() {
         int x = this.length(this.lines.get(this.line));
         this.moveRight(x, true);
      }

      public void prevPage() {
         int height = Nano.this.size.getRows() - this.computeHeader().size() - Nano.this.computeFooter().size();
         this.scrollUp(height - 2);
         this.column = 0;
         this.firstLineToDisplay = this.line;
         this.offsetInLineToDisplay = this.offsetInLine;
      }

      public void nextPage() {
         int height = Nano.this.size.getRows() - this.computeHeader().size() - Nano.this.computeFooter().size();
         this.scrollDown(height - 2);
         this.column = 0;
         this.firstLineToDisplay = this.line;
         this.offsetInLineToDisplay = this.offsetInLine;
      }

      public void scrollUp(int lines) {
         this.cursorUp(lines);
         this.moveDisplayUp(lines);
      }

      public void scrollDown(int lines) {
         this.cursorDown(lines);
         this.moveDisplayDown(lines);
      }

      public void firstLine() {
         this.line = 0;
         this.offsetInLine = this.column = 0;
         this.ensureCursorVisible();
      }

      public void lastLine() {
         this.line = this.lines.size() - 1;
         this.offsetInLine = this.column = 0;
         this.ensureCursorVisible();
      }

      boolean nextSearch() {
         boolean out = false;
         if (Nano.this.searchTerm == null) {
            Nano.this.setMessage("No current search pattern");
            return false;
         } else {
            Nano.this.setMessage(null);
            int cur = this.line;
            int dir = Nano.this.searchBackwards ? -1 : 1;
            int newPos = -1;
            int newLine = -1;
            List<Integer> curRes = this.doSearch(this.lines.get(this.line));
            if (Nano.this.searchBackwards) {
               Collections.reverse(curRes);
            }

            for (int r : curRes) {
               if (Nano.this.searchBackwards ? r < this.offsetInLine + this.column : r > this.offsetInLine + this.column) {
                  newPos = r;
                  newLine = this.line;
                  break;
               }
            }

            if (newPos < 0) {
               while (true) {
                  cur = (cur + dir + this.lines.size()) % this.lines.size();
                  if (cur == this.line) {
                     break;
                  }

                  List<Integer> res = this.doSearch(this.lines.get(cur));
                  if (!res.isEmpty()) {
                     newPos = Nano.this.searchBackwards ? res.get(res.size() - 1) : res.get(0);
                     newLine = cur;
                     break;
                  }
               }
            }

            if (newPos < 0 && !curRes.isEmpty()) {
               newPos = curRes.get(0);
               newLine = this.line;
            }

            if (newPos >= 0) {
               if (newLine == this.line && newPos == this.offsetInLine + this.column) {
                  Nano.this.setMessage("This is the only occurence");
                  return false;
               }

               if (Nano.this.searchBackwards && (newLine > this.line || newLine == this.line && newPos > this.offsetInLine + this.column)
                  || !Nano.this.searchBackwards && (newLine < this.line || newLine == this.line && newPos < this.offsetInLine + this.column)) {
                  Nano.this.setMessage("Search Wrapped");
               }

               this.line = newLine;
               this.moveRight(newPos, true);
               out = true;
            } else {
               Nano.this.setMessage("\"" + Nano.this.searchTerm + "\" not found");
            }

            return out;
         }
      }

      private List<Integer> doSearch(String text) {
         Pattern pat = Pattern.compile(Nano.this.searchTerm, (Nano.this.searchCaseSensitive ? 0 : 66) | (Nano.this.searchRegexp ? 0 : 16));
         Matcher m = pat.matcher(text);

         List<Integer> res;
         for (res = new ArrayList<>(); m.find(); Nano.this.matchedLength = m.group(0).length()) {
            res.add(m.start());
         }

         return res;
      }

      protected int[] highlightStart() {
         int[] out = new int[]{-1, -1};
         if (Nano.this.mark) {
            out = this.getMarkStart();
         } else if (Nano.this.searchToReplace) {
            out[0] = this.line;
            out[1] = this.offsetInLine + this.column;
         }

         return out;
      }

      protected int[] highlightEnd() {
         int[] out = new int[]{-1, -1};
         if (Nano.this.mark) {
            out = this.getMarkEnd();
         } else if (Nano.this.searchToReplace && Nano.this.matchedLength > 0) {
            out[0] = this.line;
            int col = this.charPosition(this.offsetInLine + this.column) + Nano.this.matchedLength;
            if (col < this.lines.get(this.line).length()) {
               out[1] = this.length(this.lines.get(this.line).substring(0, col));
            } else {
               out[1] = this.length(this.lines.get(this.line));
            }
         }

         return out;
      }

      public void matching() {
         int opening = this.getCurrentChar();
         int idx = Nano.this.matchBrackets.indexOf(opening);
         if (idx < 0) {
            Nano.this.setMessage("Not a bracket");
         } else {
            int dir = idx >= Nano.this.matchBrackets.length() / 2 ? -1 : 1;
            int closing = Nano.this.matchBrackets.charAt((idx + Nano.this.matchBrackets.length() / 2) % Nano.this.matchBrackets.length());
            int lvl = 1;
            int cur = this.line;
            int pos = this.offsetInLine + this.column;

            while (true) {
               do {
                  if (pos + dir >= 0 && pos + dir < this.getLine(cur).length()) {
                     pos += dir;
                     break;
                  }

                  if (cur + dir < 0 || cur + dir >= this.lines.size()) {
                     Nano.this.setMessage("No matching bracket");
                     return;
                  }

                  cur += dir;
                  pos = dir > 0 ? 0 : this.lines.get(cur).length() - 1;
               } while (pos < 0 || pos >= this.lines.get(cur).length());

               int c = this.lines.get(cur).charAt(pos);
               if (c == opening) {
                  lvl++;
               } else if (c == closing) {
                  if (--lvl == 0) {
                     this.line = cur;
                     this.moveToChar(pos);
                     this.ensureCursorVisible();
                     return;
                  }
               }
            }
         }
      }

      private int length(String line) {
         return new AttributedStringBuilder().tabs(Nano.this.tabs).append(line).columnLength();
      }

      void copy() {
         if (this.uncut || Nano.this.cut2end || Nano.this.mark) {
            Nano.this.cutbuffer = new ArrayList<>();
         }

         if (Nano.this.mark) {
            int[] s = this.getMarkStart();
            int[] e = this.getMarkEnd();
            if (s[0] == e[0]) {
               Nano.this.cutbuffer.add(this.lines.get(s[0]).substring(this.charPosition(s[0], s[1]), this.charPosition(e[0], e[1])));
            } else {
               if (s[1] != 0) {
                  Nano.this.cutbuffer.add(this.lines.get(s[0]).substring(this.charPosition(s[0], s[1])));
                  s[0]++;
               }

               for (int i = s[0]; i < e[0]; i++) {
                  Nano.this.cutbuffer.add(this.lines.get(i));
               }

               if (e[1] != 0) {
                  Nano.this.cutbuffer.add(this.lines.get(e[0]).substring(0, this.charPosition(e[0], e[1])));
               }
            }

            Nano.this.mark = false;
            this.mark();
         } else if (Nano.this.cut2end) {
            String l = this.lines.get(this.line);
            int col = this.charPosition(this.offsetInLine + this.column);
            Nano.this.cutbuffer.add(l.substring(col));
            this.moveRight(l.substring(col).length());
         } else {
            Nano.this.cutbuffer.add(this.lines.get(this.line));
            this.cursorDown(1);
         }

         this.uncut = false;
      }

      void cut() {
         this.cut(false);
      }

      void cut(boolean toEnd) {
         if (this.lines.size() > 1) {
            if (this.uncut || Nano.this.cut2end || toEnd || Nano.this.mark) {
               Nano.this.cutbuffer = new ArrayList<>();
            }

            if (Nano.this.mark) {
               int[] s = this.getMarkStart();
               int[] e = this.getMarkEnd();
               if (s[0] == e[0]) {
                  String l = this.lines.get(s[0]);
                  int cols = this.charPosition(s[0], s[1]);
                  int cole = this.charPosition(e[0], e[1]);
                  Nano.this.cutbuffer.add(l.substring(cols, cole));
                  this.lines.set(s[0], l.substring(0, cols) + l.substring(cole));
                  this.computeAllOffsets();
                  this.moveRight(cols, true);
               } else {
                  int ls = s[0];
                  int cs = this.charPosition(s[0], s[1]);
                  if (s[1] != 0) {
                     String l = this.lines.get(s[0]);
                     Nano.this.cutbuffer.add(l.substring(cs));
                     this.lines.set(s[0], l.substring(0, cs));
                     s[0]++;
                  }

                  for (int i = s[0]; i < e[0]; i++) {
                     Nano.this.cutbuffer.add(this.lines.get(s[0]));
                     this.lines.remove(s[0]);
                  }

                  if (e[1] != 0) {
                     String l = this.lines.get(s[0]);
                     int col = this.charPosition(e[0], e[1]);
                     Nano.this.cutbuffer.add(l.substring(0, col));
                     this.lines.set(s[0], l.substring(col));
                  }

                  this.computeAllOffsets();
                  this.gotoLine(cs, ls);
               }

               Nano.this.mark = false;
               this.mark();
            } else if (!Nano.this.cut2end && !toEnd) {
               Nano.this.cutbuffer.add(this.lines.get(this.line));
               this.lines.remove(this.line);
               this.offsetInLine = 0;
               if (this.line > this.lines.size() - 1) {
                  this.line--;
               }
            } else {
               String l = this.lines.get(this.line);
               int col = this.charPosition(this.offsetInLine + this.column);
               Nano.this.cutbuffer.add(l.substring(col));
               this.lines.set(this.line, l.substring(0, col));
               if (toEnd) {
                  this.line++;

                  do {
                     Nano.this.cutbuffer.add(this.lines.get(this.line));
                     this.lines.remove(this.line);
                  } while (this.line <= this.lines.size() - 1);

                  this.line--;
               }
            }

            Nano.this.display.clear();
            this.computeAllOffsets();
            this.dirty = true;
            this.uncut = false;
         }
      }

      void uncut() {
         if (!Nano.this.cutbuffer.isEmpty()) {
            String l = this.lines.get(this.line);
            int col = this.charPosition(this.offsetInLine + this.column);
            if (Nano.this.cut2end) {
               this.lines.set(this.line, l.substring(0, col) + Nano.this.cutbuffer.get(0) + l.substring(col));
               this.computeAllOffsets();
               this.moveRight(col + Nano.this.cutbuffer.get(0).length(), true);
            } else if (col == 0) {
               this.lines.addAll(this.line, Nano.this.cutbuffer);
               this.computeAllOffsets();
               if (Nano.this.cutbuffer.size() > 1) {
                  this.gotoLine(Nano.this.cutbuffer.get(Nano.this.cutbuffer.size() - 1).length(), this.line + Nano.this.cutbuffer.size());
               } else {
                  this.moveRight(Nano.this.cutbuffer.get(0).length(), true);
               }
            } else {
               int gotol = this.line;
               if (Nano.this.cutbuffer.size() == 1) {
                  this.lines.set(this.line, l.substring(0, col) + Nano.this.cutbuffer.get(0) + l.substring(col));
               } else {
                  this.lines.set(this.line++, l.substring(0, col) + Nano.this.cutbuffer.get(0));
                  gotol = this.line;
                  this.lines.add(this.line, Nano.this.cutbuffer.get(Nano.this.cutbuffer.size() - 1) + l.substring(col));

                  for (int i = Nano.this.cutbuffer.size() - 2; i > 0; i--) {
                     gotol++;
                     this.lines.add(this.line, Nano.this.cutbuffer.get(i));
                  }
               }

               this.computeAllOffsets();
               if (Nano.this.cutbuffer.size() > 1) {
                  this.gotoLine(Nano.this.cutbuffer.get(Nano.this.cutbuffer.size() - 1).length(), gotol);
               } else {
                  this.moveRight(col + Nano.this.cutbuffer.get(0).length(), true);
               }
            }

            Nano.this.display.clear();
            this.dirty = true;
            this.uncut = true;
         }
      }

      void mark() {
         if (Nano.this.mark) {
            this.markPos[0] = this.line;
            this.markPos[1] = this.offsetInLine + this.column;
         } else {
            this.markPos[0] = -1;
            this.markPos[1] = -1;
         }
      }

      int[] getMarkStart() {
         int[] out = new int[]{-1, -1};
         if (!Nano.this.mark) {
            return out;
         } else {
            if (this.markPos[0] <= this.line && (this.markPos[0] != this.line || this.markPos[1] <= this.offsetInLine + this.column)) {
               out = this.markPos;
            } else {
               out[0] = this.line;
               out[1] = this.offsetInLine + this.column;
            }

            return out;
         }
      }

      int[] getMarkEnd() {
         int[] out = new int[]{-1, -1};
         if (!Nano.this.mark) {
            return out;
         } else {
            if (this.markPos[0] <= this.line && (this.markPos[0] != this.line || this.markPos[1] <= this.offsetInLine + this.column)) {
               out[0] = this.line;
               out[1] = this.offsetInLine + this.column;
            } else {
               out = this.markPos;
            }

            return out;
         }
      }

      void replaceFromCursor(int chars, String string) {
         int pos = this.charPosition(this.offsetInLine + this.column);
         String text = this.lines.get(this.line);
         String mod = text.substring(0, pos) + string;
         if (chars + pos < text.length()) {
            mod = mod + text.substring(chars + pos);
         }

         this.lines.set(this.line, mod);
         this.dirty = true;
      }
   }

   protected static enum CursorMovement {
      RIGHT,
      LEFT,
      STILL;
   }

   public interface Diagnostic {
      int getStartLine();

      int getStartColumn();

      int getEndLine();

      int getEndColumn();

      String getMessage();
   }

   protected static enum Operation {
      DO_LOWER_CASE,
      QUIT,
      WRITE,
      READ,
      GOTO,
      FIND,
      WRAP,
      NUMBERS,
      SMOOTH_SCROLLING,
      MOUSE_SUPPORT,
      ONE_MORE_LINE,
      CLEAR_SCREEN,
      UP,
      DOWN,
      LEFT,
      RIGHT,
      INSERT,
      BACKSPACE,
      NEXT_BUFFER,
      PREV_BUFFER,
      HELP,
      NEXT_PAGE,
      PREV_PAGE,
      SCROLL_UP,
      SCROLL_DOWN,
      NEXT_WORD,
      PREV_WORD,
      LSP_SUGGESTION,
      BEGINNING_OF_LINE,
      END_OF_LINE,
      FIRST_LINE,
      LAST_LINE,
      CUR_POS,
      CASE_SENSITIVE,
      BACKWARDS,
      REGEXP,
      ACCEPT,
      CANCEL,
      SEARCH,
      TOGGLE_REPLACE,
      MAC_FORMAT,
      DOS_FORMAT,
      APPEND_MODE,
      PREPEND_MODE,
      BACKUP,
      TO_FILES,
      YES,
      NO,
      ALL,
      NEW_BUFFER,
      EXECUTE,
      NEXT_SEARCH,
      MATCHING,
      VERBATIM,
      DELETE,
      JUSTIFY_PARAGRAPH,
      TO_SPELL,
      CUT,
      REPLACE,
      MARK,
      COPY,
      INDENT,
      UNINDENT,
      BEGINNING_OF_PARAGRAPH,
      END_OF_PARAGRAPH,
      CUT_TO_END,
      JUSTIFY_FILE,
      COUNT,
      CONSTANT_CURSOR,
      WHITESPACE,
      HIGHLIGHT,
      SMART_HOME_KEY,
      AUTO_INDENT,
      CUT_TO_END_TOGGLE,
      TABS_TO_SPACE,
      UNCUT,
      MOUSE_EVENT,
      TOGGLE_SUSPENSION;
   }

   protected static class PatternHistory {
      private final Path historyFile;
      private final int size = 100;
      private List<String> patterns = new ArrayList<>();
      private int patternId = -1;
      private boolean lastMoveUp = false;

      public PatternHistory(Path historyFile) {
         this.historyFile = historyFile;
         this.load();
      }

      public String up(String hint) {
         String out = hint;
         if (!this.patterns.isEmpty() && this.patternId < this.patterns.size()) {
            if (!this.lastMoveUp && this.patternId > 0 && this.patternId < this.patterns.size() - 1) {
               this.patternId++;
            }

            if (this.patternId < 0) {
               this.patternId = 0;
            }

            boolean found = false;

            for (int pid = this.patternId; pid < this.patterns.size(); pid++) {
               if (hint.isEmpty() || this.patterns.get(pid).startsWith(hint)) {
                  this.patternId = pid + 1;
                  out = this.patterns.get(pid);
                  found = true;
                  break;
               }
            }

            if (!found) {
               this.patternId = this.patterns.size();
            }
         }

         this.lastMoveUp = true;
         return out;
      }

      public String down(String hint) {
         String out = hint;
         if (!this.patterns.isEmpty()) {
            if (this.lastMoveUp) {
               this.patternId--;
            }

            if (this.patternId < 0) {
               this.patternId = -1;
            } else {
               boolean found = false;

               for (int pid = this.patternId; pid >= 0; pid--) {
                  if (hint.isEmpty() || this.patterns.get(pid).startsWith(hint)) {
                     this.patternId = pid - 1;
                     out = this.patterns.get(pid);
                     found = true;
                     break;
                  }
               }

               if (!found) {
                  this.patternId = -1;
               }
            }
         }

         this.lastMoveUp = false;
         return out;
      }

      public void add(String pattern) {
         if (!pattern.trim().isEmpty()) {
            this.patterns.remove(pattern);
            if (this.patterns.size() > 100) {
               this.patterns.remove(this.patterns.size() - 1);
            }

            this.patterns.add(0, pattern);
            this.patternId = -1;
         }
      }

      public void persist() {
         if (this.historyFile != null) {
            try {
               BufferedWriter writer = Files.newBufferedWriter(this.historyFile.toAbsolutePath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

               try {
                  for (String s : this.patterns) {
                     if (!s.trim().isEmpty()) {
                        writer.append(s);
                        writer.newLine();
                     }
                  }
               } catch (Throwable var5) {
                  if (writer != null) {
                     try {
                        writer.close();
                     } catch (Throwable var4) {
                        var5.addSuppressed(var4);
                     }
                  }

                  throw var5;
               }

               if (writer != null) {
                  writer.close();
               }
            } catch (Exception var6) {
            }
         }
      }

      private void load() {
         if (this.historyFile != null) {
            try {
               if (Files.exists(this.historyFile)) {
                  this.patterns = new ArrayList<>();
                  BufferedReader reader = Files.newBufferedReader(this.historyFile);

                  try {
                     reader.lines().forEach(line -> this.patterns.add(line));
                  } catch (Throwable var5) {
                     if (reader != null) {
                        try {
                           reader.close();
                        } catch (Throwable var4) {
                           var5.addSuppressed(var4);
                        }
                     }

                     throw var5;
                  }

                  if (reader != null) {
                     reader.close();
                  }
               }
            } catch (Exception var6) {
            }
         }
      }
   }

   protected static enum WriteFormat {
      UNIX,
      DOS,
      MAC;
   }

   protected static enum WriteMode {
      WRITE,
      APPEND,
      PREPEND;
   }
}
