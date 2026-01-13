package org.jline.builtins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Display;
import org.jline.utils.InfoCmp;
import org.jline.utils.Status;

public class Less {
   private static final int ESCAPE = 27;
   private static final String MESSAGE_FILE_INFO = "FILE_INFO";
   public boolean quitAtSecondEof;
   public boolean quitAtFirstEof;
   public boolean quitIfOneScreen;
   public boolean printLineNumbers;
   public boolean quiet;
   public boolean veryQuiet;
   public boolean chopLongLines;
   public boolean ignoreCaseCond;
   public boolean ignoreCaseAlways;
   public boolean noKeypad;
   public boolean noInit;
   protected List<Integer> tabs = Collections.singletonList(4);
   protected String syntaxName;
   private String historyLog = null;
   protected final Terminal terminal;
   protected final Display display;
   protected final BindingReader bindingReader;
   protected final Path currentDir;
   protected List<Source> sources;
   protected int sourceIdx;
   protected BufferedReader reader;
   protected KeyMap<Less.Operation> keys;
   protected int firstLineInMemory = 0;
   protected List<AttributedString> lines = new ArrayList<>();
   protected int firstLineToDisplay = 0;
   protected int firstColumnToDisplay = 0;
   protected int offsetInLine = 0;
   protected String message;
   protected String errorMessage;
   protected final StringBuilder buffer = new StringBuilder();
   protected final Map<String, Less.Operation> options = new TreeMap<>();
   protected int window;
   protected int halfWindow;
   protected int nbEof;
   protected Nano.PatternHistory patternHistory = new Nano.PatternHistory(null);
   protected String pattern;
   protected String displayPattern;
   protected final Size size = new Size();
   SyntaxHighlighter syntaxHighlighter;
   private final List<Path> syntaxFiles = new ArrayList<>();
   private boolean highlight = true;
   private boolean nanorcIgnoreErrors;

   public static String[] usage() {
      return new String[]{
         "less -  file pager",
         "Usage: less [OPTIONS] [FILES]",
         "  -? --help                    Show help",
         "  -e --quit-at-eof             Exit on second EOF",
         "  -E --QUIT-AT-EOF             Exit on EOF",
         "  -F --quit-if-one-screen      Exit if entire file fits on first screen",
         "  -q --quiet --silent          Silent mode",
         "  -Q --QUIET --SILENT          Completely silent",
         "  -S --chop-long-lines         Do not fold long lines",
         "  -i --ignore-case             Search ignores lowercase case",
         "  -I --IGNORE-CASE             Search ignores all case",
         "  -x --tabs=N[,...]            Set tab stops",
         "  -N --LINE-NUMBERS            Display line number for each line",
         "  -Y --syntax=name             The name of the syntax highlighting to use.",
         "     --no-init                 Disable terminal initialization",
         "     --no-keypad               Disable keypad handling",
         "     --ignorercfiles           Don't look at the system's lessrc nor at the user's lessrc.",
         "  -H --historylog=name         Log search strings to file, so they can be retrieved in later sessions"
      };
   }

   public Less(Terminal terminal, Path currentDir) {
      this(terminal, currentDir, null);
   }

   public Less(Terminal terminal, Path currentDir, Options opts) {
      this(terminal, currentDir, opts, null);
   }

   public Less(Terminal terminal, Path currentDir, Options opts, ConfigurationPath configPath) {
      this.terminal = terminal;
      this.display = new Display(terminal, true);
      this.bindingReader = new BindingReader(terminal.reader());
      this.currentDir = currentDir;
      Path lessrc = configPath != null ? configPath.getConfig("jlessrc") : null;
      boolean ignorercfiles = opts != null && opts.isSet("ignorercfiles");
      if (lessrc != null && !ignorercfiles) {
         try {
            this.parseConfig(lessrc);
         } catch (IOException var13) {
            this.errorMessage = "Encountered error while reading config file: " + lessrc;
         }
      } else if (new File("/usr/share/nano").exists() && !ignorercfiles) {
         PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:/usr/share/nano/*.nanorc");

         try {
            Stream<Path> pathStream = Files.walk(Paths.get("/usr/share/nano"));

            try {
               pathStream.filter(pathMatcher::matches).forEach(this.syntaxFiles::add);
               this.nanorcIgnoreErrors = true;
            } catch (Throwable var14) {
               if (pathStream != null) {
                  try {
                     pathStream.close();
                  } catch (Throwable var12) {
                     var14.addSuppressed(var12);
                  }
               }

               throw var14;
            }

            if (pathStream != null) {
               pathStream.close();
            }
         } catch (IOException var15) {
            this.errorMessage = "Encountered error while reading nanorc files";
         }
      }

      if (opts != null) {
         if (opts.isSet("QUIT-AT-EOF")) {
            this.quitAtFirstEof = true;
         }

         if (opts.isSet("quit-at-eof")) {
            this.quitAtSecondEof = true;
         }

         if (opts.isSet("quit-if-one-screen")) {
            this.quitIfOneScreen = true;
         }

         if (opts.isSet("quiet")) {
            this.quiet = true;
         }

         if (opts.isSet("QUIET")) {
            this.veryQuiet = true;
         }

         if (opts.isSet("chop-long-lines")) {
            this.chopLongLines = true;
         }

         if (opts.isSet("IGNORE-CASE")) {
            this.ignoreCaseAlways = true;
         }

         if (opts.isSet("ignore-case")) {
            this.ignoreCaseCond = true;
         }

         if (opts.isSet("LINE-NUMBERS")) {
            this.printLineNumbers = true;
         }

         if (opts.isSet("tabs")) {
            this.doTabs(opts.get("tabs"));
         }

         if (opts.isSet("syntax")) {
            this.syntaxName = opts.get("syntax");
            this.nanorcIgnoreErrors = false;
         }

         if (opts.isSet("no-init")) {
            this.noInit = true;
         }

         if (opts.isSet("no-keypad")) {
            this.noKeypad = true;
         }

         if (opts.isSet("historylog")) {
            this.historyLog = opts.get("historylog");
         }
      }

      if (configPath != null && this.historyLog != null) {
         try {
            this.patternHistory = new Nano.PatternHistory(configPath.getUserConfig(this.historyLog, true));
         } catch (IOException var11) {
            this.errorMessage = "Encountered error while reading pattern-history file: " + this.historyLog;
         }
      }
   }

   private void parseConfig(Path file) throws IOException {
      BufferedReader reader = Files.newBufferedReader(file);

      try {
         for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#")) {
               List<String> parts = SyntaxHighlighter.RuleSplitter.split(line);
               if (parts.get(0).equals("include")) {
                  SyntaxHighlighter.nanorcInclude(file, parts.get(1), this.syntaxFiles);
               } else if (parts.get(0).equals("theme")) {
                  SyntaxHighlighter.nanorcTheme(file, parts.get(1), this.syntaxFiles);
               } else if (parts.size() == 2 && (parts.get(0).equals("set") || parts.get(0).equals("unset"))) {
                  String option = parts.get(1);
                  boolean val = parts.get(0).equals("set");
                  if (option.equals("QUIT-AT-EOF")) {
                     this.quitAtFirstEof = val;
                  } else if (option.equals("quit-at-eof")) {
                     this.quitAtSecondEof = val;
                  } else if (option.equals("quit-if-one-screen")) {
                     this.quitIfOneScreen = val;
                  } else if (option.equals("quiet") || option.equals("silent")) {
                     this.quiet = val;
                  } else if (option.equals("QUIET") || option.equals("SILENT")) {
                     this.veryQuiet = val;
                  } else if (option.equals("chop-long-lines")) {
                     this.chopLongLines = val;
                  } else if (option.equals("IGNORE-CASE")) {
                     this.ignoreCaseAlways = val;
                  } else if (option.equals("ignore-case")) {
                     this.ignoreCaseCond = val;
                  } else if (option.equals("LINE-NUMBERS")) {
                     this.printLineNumbers = val;
                  } else {
                     this.errorMessage = "Less config: Unknown or unsupported configuration option " + option;
                  }
               } else if (parts.size() == 3 && parts.get(0).equals("set")) {
                  String option = parts.get(1);
                  String val = parts.get(2);
                  if (option.equals("tabs")) {
                     this.doTabs(val);
                  } else if (option.equals("historylog")) {
                     this.historyLog = val;
                  } else {
                     this.errorMessage = "Less config: Unknown or unsupported configuration option " + option;
                  }
               } else if (!parts.get(0).equals("bind") && !parts.get(0).equals("unbind")) {
                  this.errorMessage = "Less config: Bad configuration '" + line + "'";
               } else {
                  this.errorMessage = "Less config: Key bindings can not be changed!";
               }
            }
         }
      } catch (Throwable var8) {
         if (reader != null) {
            try {
               reader.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (reader != null) {
         reader.close();
      }
   }

   private void doTabs(String val) {
      this.tabs = new ArrayList<>();

      for (String s : val.split(",")) {
         try {
            this.tabs.add(Integer.parseInt(s));
         } catch (Exception var7) {
            this.errorMessage = "Less config: tabs option error parsing number: " + s;
         }
      }
   }

   public Less tabs(List<Integer> tabs) {
      this.tabs = tabs;
      return this;
   }

   public void handle(Terminal.Signal signal) {
      this.size.copy(this.terminal.getSize());

      try {
         this.display.clear();
         this.display(false);
      } catch (IOException var3) {
         var3.printStackTrace();
      }
   }

   public void run(Source... sources) throws IOException, InterruptedException {
      this.run(new ArrayList<>(Arrays.asList(sources)));
   }

   public void run(List<Source> sources) throws IOException, InterruptedException {
      if (sources != null && !sources.isEmpty()) {
         sources.add(0, new Source.ResourceSource("less-help.txt", "HELP -- Press SPACE for more, or q when done"));
         this.sources = sources;
         this.sourceIdx = 1;
         this.openSource();
         if (this.errorMessage != null) {
            this.message = this.errorMessage;
            this.errorMessage = null;
         }

         Status status = Status.getStatus(this.terminal, false);

         try {
            if (status != null) {
               status.suspend();
            }

            this.size.copy(this.terminal.getSize());
            if (!this.quitIfOneScreen || sources.size() != 2 || !this.display(true)) {
               Terminal.SignalHandler prevHandler = this.terminal.handle(Terminal.Signal.WINCH, this::handle);
               Attributes attr = this.terminal.enterRawMode();

               try {
                  this.window = this.size.getRows() - 1;
                  this.halfWindow = this.window / 2;
                  this.keys = new KeyMap<>();
                  this.bindKeys(this.keys);
                  if (!this.noInit) {
                     this.terminal.puts(InfoCmp.Capability.enter_ca_mode);
                  }

                  if (!this.noKeypad) {
                     this.terminal.puts(InfoCmp.Capability.keypad_xmit);
                  }

                  this.terminal.writer().flush();
                  this.display.clear();
                  this.display(false);
                  checkInterrupted();
                  this.options.put("-e", Less.Operation.OPT_QUIT_AT_SECOND_EOF);
                  this.options.put("--quit-at-eof", Less.Operation.OPT_QUIT_AT_SECOND_EOF);
                  this.options.put("-E", Less.Operation.OPT_QUIT_AT_FIRST_EOF);
                  this.options.put("-QUIT-AT-EOF", Less.Operation.OPT_QUIT_AT_FIRST_EOF);
                  this.options.put("-N", Less.Operation.OPT_PRINT_LINES);
                  this.options.put("--LINE-NUMBERS", Less.Operation.OPT_PRINT_LINES);
                  this.options.put("-q", Less.Operation.OPT_QUIET);
                  this.options.put("--quiet", Less.Operation.OPT_QUIET);
                  this.options.put("--silent", Less.Operation.OPT_QUIET);
                  this.options.put("-Q", Less.Operation.OPT_VERY_QUIET);
                  this.options.put("--QUIET", Less.Operation.OPT_VERY_QUIET);
                  this.options.put("--SILENT", Less.Operation.OPT_VERY_QUIET);
                  this.options.put("-S", Less.Operation.OPT_CHOP_LONG_LINES);
                  this.options.put("--chop-long-lines", Less.Operation.OPT_CHOP_LONG_LINES);
                  this.options.put("-i", Less.Operation.OPT_IGNORE_CASE_COND);
                  this.options.put("--ignore-case", Less.Operation.OPT_IGNORE_CASE_COND);
                  this.options.put("-I", Less.Operation.OPT_IGNORE_CASE_ALWAYS);
                  this.options.put("--IGNORE-CASE", Less.Operation.OPT_IGNORE_CASE_ALWAYS);
                  this.options.put("-Y", Less.Operation.OPT_SYNTAX_HIGHLIGHT);
                  this.options.put("--syntax", Less.Operation.OPT_SYNTAX_HIGHLIGHT);
                  boolean forward = true;

                  Less.Operation op;
                  do {
                     checkInterrupted();
                     op = null;
                     if (this.buffer.length() > 0 && this.buffer.charAt(0) == '-') {
                        int c = this.terminal.reader().read();
                        this.message = null;
                        if (this.buffer.length() == 1) {
                           this.buffer.append((char)c);
                           if (c != 45) {
                              op = this.options.get(this.buffer.toString());
                              if (op == null) {
                                 this.message = "There is no " + this.printable(this.buffer.toString()) + " option";
                                 this.buffer.setLength(0);
                              }
                           }
                        } else if (c == 13) {
                           op = this.options.get(this.buffer.toString());
                           if (op == null) {
                              this.message = "There is no " + this.printable(this.buffer.toString()) + " option";
                              this.buffer.setLength(0);
                           }
                        } else {
                           this.buffer.append((char)c);
                           Map<String, Less.Operation> matching = new HashMap<>();

                           for (Entry<String, Less.Operation> entry : this.options.entrySet()) {
                              if (entry.getKey().startsWith(this.buffer.toString())) {
                                 matching.put(entry.getKey(), entry.getValue());
                              }
                           }

                           switch (matching.size()) {
                              case 0:
                                 this.buffer.setLength(0);
                                 break;
                              case 1:
                                 this.buffer.setLength(0);
                                 this.buffer.append(matching.keySet().iterator().next());
                           }
                        }
                     } else if (this.buffer.length() <= 0 || this.buffer.charAt(0) != '/' && this.buffer.charAt(0) != '?' && this.buffer.charAt(0) != '&') {
                        Less.Operation obj = this.bindingReader.readBinding(this.keys, null, false);
                        if (obj == Less.Operation.CHAR) {
                           char c = this.bindingReader.getLastBinding().charAt(0);
                           if (c == '-' || c == '/' || c == '?' || c == '&') {
                              this.buffer.setLength(0);
                           }

                           this.buffer.append(c);
                        } else if (obj == Less.Operation.BACKSPACE) {
                           if (this.buffer.length() > 0) {
                              this.buffer.deleteCharAt(this.buffer.length() - 1);
                           }
                        } else {
                           op = obj;
                        }
                     } else {
                        forward = this.search();
                     }

                     if (op != null) {
                        this.message = null;
                        switch (op) {
                           case HELP:
                              this.help();
                           case EXIT:
                           case FORWARD_FOREVER:
                           case GO_TO_PERCENT_OR_N:
                           case GO_TO_NEXT_TAG:
                           case GO_TO_PREVIOUS_TAG:
                           case FIND_CLOSE_BRACKET:
                           case FIND_OPEN_BRACKET:
                           case OPT_QUIT_AT_FIRST_EOF:
                           case OPT_QUIT_AT_SECOND_EOF:
                           case CHAR:
                           case INSERT:
                           case RIGHT:
                           case LEFT:
                           case NEXT_WORD:
                           case PREV_WORD:
                           default:
                              break;
                           case FORWARD_ONE_LINE:
                              this.moveForward(this.getStrictPositiveNumberInBuffer(1));
                              break;
                           case BACKWARD_ONE_LINE:
                              this.moveBackward(this.getStrictPositiveNumberInBuffer(1));
                              break;
                           case FORWARD_ONE_WINDOW_OR_LINES:
                              this.moveForward(this.getStrictPositiveNumberInBuffer(this.window));
                              break;
                           case BACKWARD_ONE_WINDOW_OR_LINES:
                              this.moveBackward(this.getStrictPositiveNumberInBuffer(this.window));
                              break;
                           case FORWARD_ONE_WINDOW_AND_SET:
                              this.window = this.getStrictPositiveNumberInBuffer(this.window);
                              this.moveForward(this.window);
                              break;
                           case BACKWARD_ONE_WINDOW_AND_SET:
                              this.window = this.getStrictPositiveNumberInBuffer(this.window);
                              this.moveBackward(this.window);
                              break;
                           case FORWARD_ONE_WINDOW_NO_STOP:
                              this.moveForward(this.window);
                              break;
                           case FORWARD_HALF_WINDOW_AND_SET:
                              this.halfWindow = this.getStrictPositiveNumberInBuffer(this.halfWindow);
                              this.moveForward(this.halfWindow);
                              break;
                           case BACKWARD_HALF_WINDOW_AND_SET:
                              this.halfWindow = this.getStrictPositiveNumberInBuffer(this.halfWindow);
                              this.moveBackward(this.halfWindow);
                              break;
                           case LEFT_ONE_HALF_SCREEN:
                              this.firstColumnToDisplay = Math.max(0, this.firstColumnToDisplay - this.size.getColumns() / 2);
                              break;
                           case RIGHT_ONE_HALF_SCREEN:
                              this.firstColumnToDisplay = this.firstColumnToDisplay + this.size.getColumns() / 2;
                              break;
                           case REPAINT:
                              this.size.copy(this.terminal.getSize());
                              this.display.clear();
                              break;
                           case REPAINT_AND_DISCARD:
                              this.message = null;
                              this.size.copy(this.terminal.getSize());
                              this.display.clear();
                              break;
                           case REPEAT_SEARCH_FORWARD:
                              this.moveToMatch(forward, false);
                              break;
                           case REPEAT_SEARCH_BACKWARD:
                              this.moveToMatch(!forward, false);
                              break;
                           case REPEAT_SEARCH_FORWARD_SPAN_FILES:
                              this.moveToMatch(forward, true);
                              break;
                           case REPEAT_SEARCH_BACKWARD_SPAN_FILES:
                              this.moveToMatch(!forward, true);
                              break;
                           case UNDO_SEARCH:
                              this.pattern = null;
                              break;
                           case GO_TO_FIRST_LINE_OR_N:
                              this.moveTo(this.getStrictPositiveNumberInBuffer(1) - 1);
                              break;
                           case GO_TO_LAST_LINE_OR_N:
                              int lineNum = this.getStrictPositiveNumberInBuffer(0) - 1;
                              if (lineNum < 0) {
                                 this.moveForward(Integer.MAX_VALUE);
                              } else {
                                 this.moveTo(lineNum);
                              }
                              break;
                           case OPT_PRINT_LINES:
                              this.buffer.setLength(0);
                              this.printLineNumbers = !this.printLineNumbers;
                              this.message = this.printLineNumbers ? "Constantly display line numbers" : "Don't use line numbers";
                              break;
                           case OPT_CHOP_LONG_LINES:
                              this.buffer.setLength(0);
                              this.offsetInLine = 0;
                              this.chopLongLines = !this.chopLongLines;
                              this.message = this.chopLongLines ? "Chop long lines" : "Fold long lines";
                              this.display.clear();
                              break;
                           case OPT_QUIET:
                              this.buffer.setLength(0);
                              this.quiet = !this.quiet;
                              this.veryQuiet = false;
                              this.message = this.quiet ? "Ring the bell for errors but not at eof/bof" : "Ring the bell for errors AND at eof/bof";
                              break;
                           case OPT_VERY_QUIET:
                              this.buffer.setLength(0);
                              this.veryQuiet = !this.veryQuiet;
                              this.quiet = false;
                              this.message = this.veryQuiet ? "Never ring the bell" : "Ring the bell for errors AND at eof/bof";
                              break;
                           case OPT_IGNORE_CASE_COND:
                              this.ignoreCaseCond = !this.ignoreCaseCond;
                              this.ignoreCaseAlways = false;
                              this.message = this.ignoreCaseCond ? "Ignore case in searches" : "Case is significant in searches";
                              break;
                           case OPT_IGNORE_CASE_ALWAYS:
                              this.ignoreCaseAlways = !this.ignoreCaseAlways;
                              this.ignoreCaseCond = false;
                              this.message = this.ignoreCaseAlways ? "Ignore case in searches and in patterns" : "Case is significant in searches";
                              break;
                           case OPT_SYNTAX_HIGHLIGHT:
                              this.highlight = !this.highlight;
                              this.message = "Highlight " + (this.highlight ? "enabled" : "disabled");
                              break;
                           case ADD_FILE:
                              this.addFile();
                              break;
                           case NEXT_FILE:
                              int next = this.getStrictPositiveNumberInBuffer(1);
                              if (this.sourceIdx < sources.size() - next) {
                                 Less.SavedSourcePositions ssp = new Less.SavedSourcePositions();
                                 this.sourceIdx += next;
                                 String newSource = sources.get(this.sourceIdx).getName();

                                 try {
                                    this.openSource();
                                 } catch (FileNotFoundException var30) {
                                    ssp.restore(newSource);
                                 }
                              } else {
                                 this.message = "No next file";
                              }
                              break;
                           case PREV_FILE:
                              int prev = this.getStrictPositiveNumberInBuffer(1);
                              if (this.sourceIdx > prev) {
                                 Less.SavedSourcePositions ssp = new Less.SavedSourcePositions(-1);
                                 this.sourceIdx -= prev;
                                 String newSource = sources.get(this.sourceIdx).getName();

                                 try {
                                    this.openSource();
                                 } catch (FileNotFoundException var29) {
                                    ssp.restore(newSource);
                                 }
                              } else {
                                 this.message = "No previous file";
                              }
                              break;
                           case GOTO_FILE:
                              int tofile = this.getStrictPositiveNumberInBuffer(1);
                              if (tofile < sources.size()) {
                                 Less.SavedSourcePositions ssp = new Less.SavedSourcePositions(tofile < this.sourceIdx ? -1 : 0);
                                 this.sourceIdx = tofile;
                                 String newSource = sources.get(this.sourceIdx).getName();

                                 try {
                                    this.openSource();
                                 } catch (FileNotFoundException var28) {
                                    ssp.restore(newSource);
                                 }
                              } else {
                                 this.message = "No such file";
                              }
                              break;
                           case INFO_FILE:
                              this.message = "FILE_INFO";
                              break;
                           case DELETE_FILE:
                              if (sources.size() > 2) {
                                 sources.remove(this.sourceIdx);
                                 if (this.sourceIdx >= sources.size()) {
                                    this.sourceIdx = sources.size() - 1;
                                 }

                                 this.openSource();
                              }
                              break;
                           case HOME:
                              this.moveTo(0);
                              break;
                           case END:
                              this.moveForward(Integer.MAX_VALUE);
                        }

                        this.buffer.setLength(0);
                     }

                     if (this.quitAtFirstEof && this.nbEof > 0 || this.quitAtSecondEof && this.nbEof > 1) {
                        if (this.sourceIdx < sources.size() - 1) {
                           this.sourceIdx++;
                           this.openSource();
                        } else {
                           op = Less.Operation.EXIT;
                        }
                     }

                     this.display(false);
                  } while (op != Less.Operation.EXIT);

                  return;
               } catch (InterruptedException var31) {
                  return;
               } finally {
                  this.terminal.setAttributes(attr);
                  if (prevHandler != null) {
                     this.terminal.handle(Terminal.Signal.WINCH, prevHandler);
                  }

                  if (!this.noInit) {
                     this.terminal.puts(InfoCmp.Capability.exit_ca_mode);
                  }

                  if (!this.noKeypad) {
                     this.terminal.puts(InfoCmp.Capability.keypad_local);
                  }

                  this.terminal.writer().flush();
               }
            }
         } finally {
            if (this.reader != null) {
               this.reader.close();
            }

            if (status != null) {
               status.restore();
            }

            this.patternHistory.persist();
         }
      } else {
         throw new IllegalArgumentException("No sources");
      }
   }

   private void moveToMatch(boolean forward, boolean spanFiles) throws IOException {
      if (forward) {
         this.moveToNextMatch(spanFiles);
      } else {
         this.moveToPreviousMatch(spanFiles);
      }
   }

   private void addSource(String file) throws IOException {
      if (!file.contains("*") && !file.contains("?")) {
         this.sources.add(new Source.URLSource(this.currentDir.resolve(file).toUri().toURL(), file));
      } else {
         for (Path p : Commands.findFiles(this.currentDir, file)) {
            this.sources.add(new Source.URLSource(p.toUri().toURL(), p.toString()));
         }
      }

      this.sourceIdx = this.sources.size() - 1;
   }

   private void addFile() throws IOException, InterruptedException {
      KeyMap<Less.Operation> fileKeyMap = new KeyMap<>();
      fileKeyMap.setUnicode(Less.Operation.INSERT);

      for (char i = ' '; i < 256; i++) {
         fileKeyMap.bind(Less.Operation.INSERT, Character.toString(i));
      }

      fileKeyMap.bind(Less.Operation.RIGHT, KeyMap.key(this.terminal, InfoCmp.Capability.key_right), KeyMap.alt('l'));
      fileKeyMap.bind(Less.Operation.LEFT, KeyMap.key(this.terminal, InfoCmp.Capability.key_left), KeyMap.alt('h'));
      fileKeyMap.bind(Less.Operation.HOME, KeyMap.key(this.terminal, InfoCmp.Capability.key_home), KeyMap.alt('0'));
      fileKeyMap.bind(Less.Operation.END, KeyMap.key(this.terminal, InfoCmp.Capability.key_end), KeyMap.alt('$'));
      fileKeyMap.bind(Less.Operation.BACKSPACE, KeyMap.del());
      fileKeyMap.bind(Less.Operation.DELETE, KeyMap.alt('x'));
      fileKeyMap.bind(Less.Operation.DELETE_WORD, KeyMap.alt('X'));
      fileKeyMap.bind(Less.Operation.DELETE_LINE, KeyMap.ctrl('U'));
      fileKeyMap.bind(Less.Operation.ACCEPT, "\r");
      Less.SavedSourcePositions ssp = new Less.SavedSourcePositions();
      this.message = null;
      this.buffer.append("Examine: ");
      int curPos = this.buffer.length();
      int begPos = curPos;
      this.display(false, curPos);
      Less.LineEditor lineEditor = new Less.LineEditor(curPos);

      while (true) {
         checkInterrupted();
         Less.Operation op = this.bindingReader.readBinding(fileKeyMap);
         if (op == Less.Operation.ACCEPT) {
            String name = this.buffer.substring(begPos);
            this.addSource(name);

            try {
               this.openSource();
            } catch (Exception var9) {
               ssp.restore(name);
            }

            return;
         }

         if (op != null) {
            curPos = lineEditor.editBuffer(op, curPos);
         }

         if (curPos <= begPos) {
            this.buffer.setLength(0);
            return;
         }

         this.display(false, curPos);
      }
   }

   private boolean search() throws IOException, InterruptedException {
      KeyMap<Less.Operation> searchKeyMap = new KeyMap<>();
      searchKeyMap.setUnicode(Less.Operation.INSERT);

      for (char i = ' '; i < 256; i++) {
         searchKeyMap.bind(Less.Operation.INSERT, Character.toString(i));
      }

      searchKeyMap.bind(Less.Operation.RIGHT, KeyMap.key(this.terminal, InfoCmp.Capability.key_right), KeyMap.alt('l'));
      searchKeyMap.bind(Less.Operation.LEFT, KeyMap.key(this.terminal, InfoCmp.Capability.key_left), KeyMap.alt('h'));
      searchKeyMap.bind(Less.Operation.NEXT_WORD, KeyMap.alt('w'));
      searchKeyMap.bind(Less.Operation.PREV_WORD, KeyMap.alt('b'));
      searchKeyMap.bind(Less.Operation.HOME, KeyMap.key(this.terminal, InfoCmp.Capability.key_home), KeyMap.alt('0'));
      searchKeyMap.bind(Less.Operation.END, KeyMap.key(this.terminal, InfoCmp.Capability.key_end), KeyMap.alt('$'));
      searchKeyMap.bind(Less.Operation.BACKSPACE, KeyMap.del());
      searchKeyMap.bind(Less.Operation.DELETE, KeyMap.alt('x'));
      searchKeyMap.bind(Less.Operation.DELETE_WORD, KeyMap.alt('X'));
      searchKeyMap.bind(Less.Operation.DELETE_LINE, KeyMap.ctrl('U'));
      searchKeyMap.bind(Less.Operation.UP, KeyMap.key(this.terminal, InfoCmp.Capability.key_up), KeyMap.alt('k'));
      searchKeyMap.bind(Less.Operation.DOWN, KeyMap.key(this.terminal, InfoCmp.Capability.key_down), KeyMap.alt('j'));
      searchKeyMap.bind(Less.Operation.ACCEPT, "\r");
      boolean forward = true;
      this.message = null;
      int curPos = this.buffer.length();
      int begPos = curPos;
      char type = this.buffer.charAt(0);
      String currentBuffer = this.buffer.toString();
      Less.LineEditor lineEditor = new Less.LineEditor(curPos);

      while (true) {
         checkInterrupted();
         Less.Operation op;
         switch (op = this.bindingReader.readBinding(searchKeyMap)) {
            case ACCEPT:
               try {
                  String _pattern = this.buffer.substring(1);
                  if (type == '&') {
                     this.displayPattern = !_pattern.isEmpty() ? _pattern : null;
                     this.getPattern(true);
                  } else {
                     this.pattern = _pattern;
                     this.getPattern();
                     if (type == '/') {
                        this.moveToNextMatch();
                     } else {
                        if (this.lines.size() - this.firstLineToDisplay <= this.size.getRows()) {
                           this.firstLineToDisplay = this.lines.size();
                        } else {
                           this.moveForward(this.size.getRows() - 1);
                        }

                        this.moveToPreviousMatch();
                        forward = false;
                     }
                  }

                  this.patternHistory.add(_pattern);
                  this.buffer.setLength(0);
               } catch (PatternSyntaxException var11) {
                  String str = var11.getMessage();
                  if (str.indexOf(10) > 0) {
                     str = str.substring(0, str.indexOf(10));
                  }

                  if (type == '&') {
                     this.displayPattern = null;
                  } else {
                     this.pattern = null;
                  }

                  this.buffer.setLength(0);
                  this.message = "Invalid pattern: " + str + " (Press a key)";
                  this.display(false);
                  this.terminal.reader().read();
                  this.message = null;
               }

               return forward;
            case UP:
               this.buffer.setLength(0);
               this.buffer.append(type);
               this.buffer.append(this.patternHistory.up(currentBuffer.substring(1)));
               curPos = this.buffer.length();
               break;
            case DOWN:
               this.buffer.setLength(0);
               this.buffer.append(type);
               this.buffer.append(this.patternHistory.down(currentBuffer.substring(1)));
               curPos = this.buffer.length();
               break;
            default:
               curPos = lineEditor.editBuffer(op, curPos);
               currentBuffer = this.buffer.toString();
         }

         if (curPos < begPos) {
            this.buffer.setLength(0);
            return forward;
         }

         this.display(false, curPos);
      }
   }

   private void help() throws IOException {
      Less.SavedSourcePositions ssp = new Less.SavedSourcePositions();
      this.printLineNumbers = false;
      this.sourceIdx = 0;

      try {
         this.openSource();
         this.display(false);

         Less.Operation op;
         do {
            checkInterrupted();
            op = this.bindingReader.readBinding(this.keys, null, false);
            if (op != null) {
               switch (op) {
                  case FORWARD_ONE_WINDOW_OR_LINES:
                     this.moveForward(this.getStrictPositiveNumberInBuffer(this.window));
                     break;
                  case BACKWARD_ONE_WINDOW_OR_LINES:
                     this.moveBackward(this.getStrictPositiveNumberInBuffer(this.window));
               }
            }

            this.display(false);
         } while (op != Less.Operation.EXIT);
      } catch (InterruptedException | IOException var6) {
      } finally {
         ssp.restore(null);
      }
   }

   protected void openSource() throws IOException {
      boolean wasOpen = false;
      if (this.reader != null) {
         this.reader.close();
         wasOpen = true;
      }

      boolean displayMessage = false;

      boolean open;
      do {
         Source source = this.sources.get(this.sourceIdx);

         try {
            InputStream in = source.read();
            if (this.sources.size() != 2 && this.sourceIdx != 0) {
               this.message = source.getName() + " (file " + this.sourceIdx + " of " + (this.sources.size() - 1) + ")";
            } else {
               this.message = source.getName();
            }

            this.reader = new BufferedReader(new InputStreamReader(new Less.InterruptibleInputStream(in)));
            this.firstLineInMemory = 0;
            this.lines = new ArrayList<>();
            this.firstLineToDisplay = 0;
            this.firstColumnToDisplay = 0;
            this.offsetInLine = 0;
            this.display.clear();
            if (this.sourceIdx == 0) {
               this.syntaxHighlighter = SyntaxHighlighter.build(this.syntaxFiles, null, "none");
            } else {
               this.syntaxHighlighter = SyntaxHighlighter.build(this.syntaxFiles, source.getName(), this.syntaxName, this.nanorcIgnoreErrors);
            }

            open = true;
            if (displayMessage) {
               AttributedStringBuilder asb = new AttributedStringBuilder();
               asb.style(AttributedStyle.INVERSE);
               asb.append(source.getName()).append(" (press RETURN)");
               asb.toAttributedString().println(this.terminal);
               this.terminal.writer().flush();
               this.terminal.reader().read();
            }
         } catch (FileNotFoundException var7) {
            this.sources.remove(this.sourceIdx);
            if (this.sourceIdx > this.sources.size() - 1) {
               this.sourceIdx = this.sources.size() - 1;
            }

            if (wasOpen) {
               throw var7;
            }

            AttributedStringBuilder asb = new AttributedStringBuilder();
            asb.append(source.getName()).append(" not found!");
            asb.toAttributedString().println(this.terminal);
            this.terminal.writer().flush();
            open = false;
            displayMessage = true;
         }
      } while (!open && this.sourceIdx > 0);

      if (!open) {
         throw new FileNotFoundException();
      }
   }

   void moveTo(int lineNum) throws IOException {
      AttributedString line = this.getLine(lineNum);
      if (line != null) {
         this.display.clear();
         if (this.firstLineInMemory > lineNum) {
            this.openSource();
         }

         this.firstLineToDisplay = lineNum;
         this.offsetInLine = 0;
      } else {
         this.message = "Cannot seek to line number " + (lineNum + 1);
      }
   }

   private void moveToNextMatch() throws IOException {
      this.moveToNextMatch(false);
   }

   private void moveToNextMatch(boolean spanFiles) throws IOException {
      Pattern compiled = this.getPattern();
      Pattern dpCompiled = this.getPattern(true);
      if (compiled != null) {
         int lineNumber = this.firstLineToDisplay + 1;

         while (true) {
            AttributedString line = this.getLine(lineNumber);
            if (line == null) {
               break;
            }

            if (this.toBeDisplayed(line, dpCompiled) && compiled.matcher(line).find()) {
               this.display.clear();
               this.firstLineToDisplay = lineNumber;
               this.offsetInLine = 0;
               return;
            }

            lineNumber++;
         }
      }

      if (spanFiles) {
         if (this.sourceIdx < this.sources.size() - 1) {
            Less.SavedSourcePositions ssp = new Less.SavedSourcePositions();
            String newSource = this.sources.get(++this.sourceIdx).getName();

            try {
               this.openSource();
               this.moveToNextMatch(true);
            } catch (FileNotFoundException var7) {
               ssp.restore(newSource);
            }
         } else {
            this.message = "Pattern not found";
         }
      } else {
         this.message = "Pattern not found";
      }
   }

   private void moveToPreviousMatch() throws IOException {
      this.moveToPreviousMatch(false);
   }

   private void moveToPreviousMatch(boolean spanFiles) throws IOException {
      Pattern compiled = this.getPattern();
      Pattern dpCompiled = this.getPattern(true);
      if (compiled != null) {
         for (int lineNumber = this.firstLineToDisplay - 1; lineNumber >= this.firstLineInMemory; lineNumber--) {
            AttributedString line = this.getLine(lineNumber);
            if (line == null) {
               break;
            }

            if (this.toBeDisplayed(line, dpCompiled) && compiled.matcher(line).find()) {
               this.display.clear();
               this.firstLineToDisplay = lineNumber;
               this.offsetInLine = 0;
               return;
            }
         }
      }

      if (spanFiles) {
         if (this.sourceIdx > 1) {
            Less.SavedSourcePositions ssp = new Less.SavedSourcePositions(-1);
            String newSource = this.sources.get(--this.sourceIdx).getName();

            try {
               this.openSource();
               this.moveTo(Integer.MAX_VALUE);
               this.moveToPreviousMatch(true);
            } catch (FileNotFoundException var7) {
               ssp.restore(newSource);
            }
         } else {
            this.message = "Pattern not found";
         }
      } else {
         this.message = "Pattern not found";
      }
   }

   private String printable(String s) {
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < s.length(); i++) {
         char c = s.charAt(i);
         if (c == 27) {
            sb.append("ESC");
         } else if (c < ' ') {
            sb.append('^').append((char)(c + '@'));
         } else if (c < 128) {
            sb.append(c);
         } else {
            sb.append('\\').append(String.format("%03o", Integer.valueOf(c)));
         }
      }

      return sb.toString();
   }

   void moveForward(int lines) throws IOException {
      Pattern dpCompiled = this.getPattern(true);
      int width = this.size.getColumns() - (this.printLineNumbers ? 8 : 0);
      int height = this.size.getRows();
      boolean doOffsets = this.firstColumnToDisplay == 0 && !this.chopLongLines;
      if (lines >= this.size.getRows() - 1) {
         this.display.clear();
      }

      if (lines == Integer.MAX_VALUE) {
         this.moveTo(Integer.MAX_VALUE);
         this.firstLineToDisplay = height - 1;

         for (int l = 0; l < height - 1; l++) {
            this.firstLineToDisplay = this.prevLine2display(this.firstLineToDisplay, dpCompiled).getU();
         }
      }

      while (--lines >= 0) {
         int lastLineToDisplay = this.firstLineToDisplay;
         if (!doOffsets) {
            for (int l = 0; l < height - 1; l++) {
               lastLineToDisplay = this.nextLine2display(lastLineToDisplay, dpCompiled).getU();
            }
         } else {
            int off = this.offsetInLine;

            for (int l = 0; l < height - 1; l++) {
               Less.Pair<Integer, AttributedString> nextLine = this.nextLine2display(lastLineToDisplay, dpCompiled);
               AttributedString line = nextLine.getV();
               if (line == null) {
                  lastLineToDisplay = nextLine.getU();
                  break;
               }

               if (line.columnLength() > off + width) {
                  off += width;
               } else {
                  off = 0;
                  lastLineToDisplay = nextLine.getU();
               }
            }
         }

         if (this.getLine(lastLineToDisplay) == null) {
            this.eof();
            return;
         }

         Less.Pair<Integer, AttributedString> nextLinex = this.nextLine2display(this.firstLineToDisplay, dpCompiled);
         AttributedString linex = nextLinex.getV();
         if (doOffsets && linex.columnLength() > width + this.offsetInLine) {
            this.offsetInLine += width;
         } else {
            this.offsetInLine = 0;
            this.firstLineToDisplay = nextLinex.getU();
         }
      }
   }

   void moveBackward(int lines) throws IOException {
      Pattern dpCompiled = this.getPattern(true);
      int width = this.size.getColumns() - (this.printLineNumbers ? 8 : 0);
      if (lines >= this.size.getRows() - 1) {
         this.display.clear();
      }

      while (--lines >= 0) {
         if (this.offsetInLine > 0) {
            this.offsetInLine = Math.max(0, this.offsetInLine - width);
         } else {
            if (this.firstLineInMemory >= this.firstLineToDisplay) {
               this.bof();
               return;
            }

            Less.Pair<Integer, AttributedString> prevLine = this.prevLine2display(this.firstLineToDisplay, dpCompiled);
            this.firstLineToDisplay = prevLine.getU();
            AttributedString line = prevLine.getV();
            if (line != null && this.firstColumnToDisplay == 0 && !this.chopLongLines) {
               int length = line.columnLength();
               this.offsetInLine = length - length % width;
            }
         }
      }
   }

   private void eof() {
      this.nbEof++;
      if (this.sourceIdx > 0 && this.sourceIdx < this.sources.size() - 1) {
         this.message = "(END) - Next: " + this.sources.get(this.sourceIdx + 1).getName();
      } else {
         this.message = "(END)";
      }

      if (!this.quiet && !this.veryQuiet && !this.quitAtFirstEof && !this.quitAtSecondEof) {
         this.terminal.puts(InfoCmp.Capability.bell);
         this.terminal.writer().flush();
      }
   }

   private void bof() {
      if (!this.quiet && !this.veryQuiet) {
         this.terminal.puts(InfoCmp.Capability.bell);
         this.terminal.writer().flush();
      }
   }

   int getStrictPositiveNumberInBuffer(int def) {
      int var3;
      try {
         int n = Integer.parseInt(this.buffer.toString());
         return n > 0 ? n : def;
      } catch (NumberFormatException var7) {
         var3 = def;
      } finally {
         this.buffer.setLength(0);
      }

      return var3;
   }

   private Less.Pair<Integer, AttributedString> nextLine2display(int line, Pattern dpCompiled) throws IOException {
      AttributedString curLine;
      do {
         curLine = this.getLine(line++);
      } while (!this.toBeDisplayed(curLine, dpCompiled));

      return new Less.Pair<>(line, curLine);
   }

   private Less.Pair<Integer, AttributedString> prevLine2display(int line, Pattern dpCompiled) throws IOException {
      AttributedString curLine;
      do {
         curLine = this.getLine(line--);
      } while (line > 0 && !this.toBeDisplayed(curLine, dpCompiled));

      if (line == 0 && !this.toBeDisplayed(curLine, dpCompiled)) {
         curLine = null;
      }

      return new Less.Pair<>(line, curLine);
   }

   private boolean toBeDisplayed(AttributedString curLine, Pattern dpCompiled) {
      return curLine == null || dpCompiled == null || this.sourceIdx == 0 || dpCompiled.matcher(curLine).find();
   }

   synchronized boolean display(boolean oneScreen) throws IOException {
      return this.display(oneScreen, null);
   }

   synchronized boolean display(boolean oneScreen, Integer curPos) throws IOException {
      List<AttributedString> newLines = new ArrayList<>();
      int width = this.size.getColumns() - (this.printLineNumbers ? 8 : 0);
      int height = this.size.getRows();
      int inputLine = this.firstLineToDisplay;
      AttributedString curLine = null;
      Pattern compiled = this.getPattern();
      Pattern dpCompiled = this.getPattern(true);
      boolean fitOnOneScreen = false;
      boolean eof = false;
      if (this.highlight) {
         this.syntaxHighlighter.reset();

         for (int i = Math.max(0, inputLine - height); i < inputLine; i++) {
            this.syntaxHighlighter.highlight(this.getLine(i));
         }
      }

      for (int terminalLine = 0; terminalLine < height - 1; terminalLine++) {
         if (curLine == null) {
            Less.Pair<Integer, AttributedString> nextLine = this.nextLine2display(inputLine, dpCompiled);
            inputLine = nextLine.getU();
            curLine = nextLine.getV();
            if (curLine == null) {
               if (oneScreen) {
                  fitOnOneScreen = true;
                  break;
               }

               eof = true;
               curLine = new AttributedString("~");
            } else if (this.highlight) {
               curLine = this.syntaxHighlighter.highlight(curLine);
            }

            if (compiled != null) {
               curLine = curLine.styleMatches(compiled, AttributedStyle.DEFAULT.inverse());
            }
         }

         AttributedString toDisplay;
         if (this.firstColumnToDisplay <= 0 && !this.chopLongLines) {
            if (terminalLine == 0 && this.offsetInLine > 0) {
               curLine = curLine.columnSubSequence(this.offsetInLine, Integer.MAX_VALUE);
            }

            toDisplay = curLine.columnSubSequence(0, width);
            curLine = curLine.columnSubSequence(width, Integer.MAX_VALUE);
            if (curLine.length() == 0) {
               curLine = null;
            }
         } else {
            int off = this.firstColumnToDisplay;
            if (terminalLine == 0 && this.offsetInLine > 0) {
               off = Math.max(this.offsetInLine, off);
            }

            toDisplay = curLine.columnSubSequence(off, off + width);
            curLine = null;
         }

         if (this.printLineNumbers && !eof) {
            AttributedStringBuilder sb = new AttributedStringBuilder();
            sb.append(String.format("%7d ", inputLine));
            sb.append(toDisplay);
            newLines.add(sb.toAttributedString());
         } else {
            newLines.add(toDisplay);
         }
      }

      if (oneScreen) {
         if (fitOnOneScreen) {
            newLines.forEach(l -> l.println(this.terminal));
         }

         return fitOnOneScreen;
      } else {
         AttributedStringBuilder msg = new AttributedStringBuilder();
         if ("FILE_INFO".equals(this.message)) {
            Source source = this.sources.get(this.sourceIdx);
            Long allLines = source.lines();
            this.message = source.getName()
               + (this.sources.size() > 2 ? " (file " + this.sourceIdx + " of " + (this.sources.size() - 1) + ")" : "")
               + " lines "
               + (this.firstLineToDisplay + 1)
               + "-"
               + inputLine
               + "/"
               + (allLines != null ? allLines : this.lines.size())
               + (eof ? " (END)" : "");
         }

         if (this.buffer.length() > 0) {
            msg.append(" ").append(this.buffer);
         } else if (!this.bindingReader.getCurrentBuffer().isEmpty() && this.terminal.reader().peek(1L) == -2) {
            msg.append(" ").append(this.printable(this.bindingReader.getCurrentBuffer()));
         } else if (this.message != null) {
            msg.style(AttributedStyle.INVERSE);
            msg.append(this.message);
            msg.style(AttributedStyle.INVERSE.inverseOff());
         } else if (this.displayPattern != null) {
            msg.append("&");
         } else {
            msg.append(":");
         }

         newLines.add(msg.toAttributedString());
         this.display.resize(this.size.getRows(), this.size.getColumns());
         if (curPos == null) {
            this.display.update(newLines, -1);
         } else {
            this.display.update(newLines, this.size.cursorPos(this.size.getRows() - 1, curPos + 1));
         }

         return false;
      }
   }

   private Pattern getPattern() {
      return this.getPattern(false);
   }

   private Pattern getPattern(boolean doDisplayPattern) {
      Pattern compiled = null;
      String _pattern = doDisplayPattern ? this.displayPattern : this.pattern;
      if (_pattern != null) {
         boolean insensitive = this.ignoreCaseAlways || this.ignoreCaseCond && _pattern.toLowerCase().equals(_pattern);
         compiled = Pattern.compile("(" + _pattern + ")", insensitive ? 66 : 0);
      }

      return compiled;
   }

   AttributedString getLine(int line) throws IOException {
      while (line >= this.lines.size()) {
         String str = this.reader.readLine();
         if (str != null) {
            this.lines.add(AttributedString.fromAnsi(str, this.tabs));
            continue;
         }
         break;
      }

      return line < this.lines.size() ? this.lines.get(line) : null;
   }

   public static void checkInterrupted() throws InterruptedException {
      Thread.yield();
      if (Thread.currentThread().isInterrupted()) {
         throw new InterruptedException();
      }
   }

   private void bindKeys(KeyMap<Less.Operation> map) {
      map.bind(Less.Operation.HELP, "h", "H");
      map.bind(Less.Operation.EXIT, "q", ":q", "Q", ":Q", "ZZ");
      map.bind(Less.Operation.FORWARD_ONE_LINE, "e", KeyMap.ctrl('E'), "j", KeyMap.ctrl('N'), "\r", KeyMap.key(this.terminal, InfoCmp.Capability.key_down));
      map.bind(
         Less.Operation.BACKWARD_ONE_LINE, "y", KeyMap.ctrl('Y'), "k", KeyMap.ctrl('K'), KeyMap.ctrl('P'), KeyMap.key(this.terminal, InfoCmp.Capability.key_up)
      );
      map.bind(
         Less.Operation.FORWARD_ONE_WINDOW_OR_LINES, "f", KeyMap.ctrl('F'), KeyMap.ctrl('V'), " ", KeyMap.key(this.terminal, InfoCmp.Capability.key_npage)
      );
      map.bind(Less.Operation.BACKWARD_ONE_WINDOW_OR_LINES, "b", KeyMap.ctrl('B'), KeyMap.alt('v'), KeyMap.key(this.terminal, InfoCmp.Capability.key_ppage));
      map.bind(Less.Operation.FORWARD_ONE_WINDOW_AND_SET, "z");
      map.bind(Less.Operation.BACKWARD_ONE_WINDOW_AND_SET, "w");
      map.bind(Less.Operation.FORWARD_ONE_WINDOW_NO_STOP, KeyMap.alt(' '));
      map.bind(Less.Operation.FORWARD_HALF_WINDOW_AND_SET, "d", KeyMap.ctrl('D'));
      map.bind(Less.Operation.BACKWARD_HALF_WINDOW_AND_SET, "u", KeyMap.ctrl('U'));
      map.bind(Less.Operation.RIGHT_ONE_HALF_SCREEN, KeyMap.alt(')'), KeyMap.key(this.terminal, InfoCmp.Capability.key_right));
      map.bind(Less.Operation.LEFT_ONE_HALF_SCREEN, KeyMap.alt('('), KeyMap.key(this.terminal, InfoCmp.Capability.key_left));
      map.bind(Less.Operation.FORWARD_FOREVER, "F");
      map.bind(Less.Operation.REPAINT, "r", KeyMap.ctrl('R'), KeyMap.ctrl('L'));
      map.bind(Less.Operation.REPAINT_AND_DISCARD, "R");
      map.bind(Less.Operation.REPEAT_SEARCH_FORWARD, "n");
      map.bind(Less.Operation.REPEAT_SEARCH_BACKWARD, "N");
      map.bind(Less.Operation.REPEAT_SEARCH_FORWARD_SPAN_FILES, KeyMap.alt('n'));
      map.bind(Less.Operation.REPEAT_SEARCH_BACKWARD_SPAN_FILES, KeyMap.alt('N'));
      map.bind(Less.Operation.UNDO_SEARCH, KeyMap.alt('u'));
      map.bind(Less.Operation.GO_TO_FIRST_LINE_OR_N, "g", "<", KeyMap.alt('<'));
      map.bind(Less.Operation.GO_TO_LAST_LINE_OR_N, "G", ">", KeyMap.alt('>'));
      map.bind(Less.Operation.HOME, KeyMap.key(this.terminal, InfoCmp.Capability.key_home));
      map.bind(Less.Operation.END, KeyMap.key(this.terminal, InfoCmp.Capability.key_end));
      map.bind(Less.Operation.ADD_FILE, ":e", KeyMap.ctrl('X') + KeyMap.ctrl('V'));
      map.bind(Less.Operation.NEXT_FILE, ":n");
      map.bind(Less.Operation.PREV_FILE, ":p");
      map.bind(Less.Operation.GOTO_FILE, ":x");
      map.bind(Less.Operation.INFO_FILE, "=", ":f", KeyMap.ctrl('G'));
      map.bind(Less.Operation.DELETE_FILE, ":d");
      map.bind(Less.Operation.BACKSPACE, KeyMap.del());
      "-/0123456789?&".chars().forEach(c -> map.bind(Less.Operation.CHAR, Character.toString((char)c)));
   }

   static class InterruptibleInputStream extends FilterInputStream {
      InterruptibleInputStream(InputStream in) {
         super(in);
      }

      @Override
      public int read(byte[] b, int off, int len) throws IOException {
         if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedIOException();
         } else {
            return super.read(b, off, len);
         }
      }
   }

   private class LineEditor {
      private final int begPos;

      public LineEditor(int begPos) {
         this.begPos = begPos;
      }

      public int editBuffer(Less.Operation op, int curPos) {
         switch (op) {
            case INSERT:
               Less.this.buffer.insert(curPos++, Less.this.bindingReader.getLastBinding());
               break;
            case RIGHT:
               if (curPos < Less.this.buffer.length()) {
                  curPos++;
               }
               break;
            case LEFT:
               if (curPos > this.begPos) {
                  curPos--;
               }
               break;
            case NEXT_WORD:
               int newPos = Less.this.buffer.length();

               for (int ix = curPos; ix < Less.this.buffer.length(); ix++) {
                  if (Less.this.buffer.charAt(ix) == ' ') {
                     newPos = ix + 1;
                     break;
                  }
               }

               curPos = newPos;
               break;
            case PREV_WORD:
               int newPos = this.begPos;

               for (int i = curPos - 2; i > this.begPos; i--) {
                  if (Less.this.buffer.charAt(i) == ' ') {
                     newPos = i + 1;
                     break;
                  }
               }

               curPos = newPos;
               break;
            case HOME:
               curPos = this.begPos;
               break;
            case END:
               curPos = Less.this.buffer.length();
               break;
            case BACKSPACE:
               if (curPos > this.begPos - 1) {
                  Less.this.buffer.deleteCharAt(--curPos);
               }
               break;
            case DELETE:
               if (curPos >= this.begPos && curPos < Less.this.buffer.length()) {
                  Less.this.buffer.deleteCharAt(curPos);
               }
               break;
            case DELETE_WORD:
               while (curPos < Less.this.buffer.length() && Less.this.buffer.charAt(curPos) != ' ') {
                  Less.this.buffer.deleteCharAt(curPos);
               }

               while (curPos - 1 >= this.begPos) {
                  if (Less.this.buffer.charAt(curPos - 1) == ' ') {
                     Less.this.buffer.deleteCharAt(--curPos);
                     return curPos;
                  }

                  Less.this.buffer.deleteCharAt(--curPos);
               }
               break;
            case DELETE_LINE:
               Less.this.buffer.setLength(this.begPos);
               curPos = 1;
         }

         return curPos;
      }
   }

   protected static enum Operation {
      HELP,
      EXIT,
      FORWARD_ONE_LINE,
      BACKWARD_ONE_LINE,
      FORWARD_ONE_WINDOW_OR_LINES,
      BACKWARD_ONE_WINDOW_OR_LINES,
      FORWARD_ONE_WINDOW_AND_SET,
      BACKWARD_ONE_WINDOW_AND_SET,
      FORWARD_ONE_WINDOW_NO_STOP,
      FORWARD_HALF_WINDOW_AND_SET,
      BACKWARD_HALF_WINDOW_AND_SET,
      LEFT_ONE_HALF_SCREEN,
      RIGHT_ONE_HALF_SCREEN,
      FORWARD_FOREVER,
      REPAINT,
      REPAINT_AND_DISCARD,
      REPEAT_SEARCH_FORWARD,
      REPEAT_SEARCH_BACKWARD,
      REPEAT_SEARCH_FORWARD_SPAN_FILES,
      REPEAT_SEARCH_BACKWARD_SPAN_FILES,
      UNDO_SEARCH,
      GO_TO_FIRST_LINE_OR_N,
      GO_TO_LAST_LINE_OR_N,
      GO_TO_PERCENT_OR_N,
      GO_TO_NEXT_TAG,
      GO_TO_PREVIOUS_TAG,
      FIND_CLOSE_BRACKET,
      FIND_OPEN_BRACKET,
      OPT_PRINT_LINES,
      OPT_CHOP_LONG_LINES,
      OPT_QUIT_AT_FIRST_EOF,
      OPT_QUIT_AT_SECOND_EOF,
      OPT_QUIET,
      OPT_VERY_QUIET,
      OPT_IGNORE_CASE_COND,
      OPT_IGNORE_CASE_ALWAYS,
      OPT_SYNTAX_HIGHLIGHT,
      ADD_FILE,
      NEXT_FILE,
      PREV_FILE,
      GOTO_FILE,
      INFO_FILE,
      DELETE_FILE,
      CHAR,
      INSERT,
      RIGHT,
      LEFT,
      NEXT_WORD,
      PREV_WORD,
      HOME,
      END,
      BACKSPACE,
      DELETE,
      DELETE_WORD,
      DELETE_LINE,
      ACCEPT,
      UP,
      DOWN;
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

   private class SavedSourcePositions {
      int saveSourceIdx;
      int saveFirstLineToDisplay;
      int saveFirstColumnToDisplay;
      int saveOffsetInLine;
      boolean savePrintLineNumbers;

      public SavedSourcePositions() {
         this(0);
      }

      public SavedSourcePositions(int dec) {
         this.saveSourceIdx = Less.this.sourceIdx + dec;
         this.saveFirstLineToDisplay = Less.this.firstLineToDisplay;
         this.saveFirstColumnToDisplay = Less.this.firstColumnToDisplay;
         this.saveOffsetInLine = Less.this.offsetInLine;
         this.savePrintLineNumbers = Less.this.printLineNumbers;
      }

      public void restore(String failingSource) throws IOException {
         Less.this.sourceIdx = this.saveSourceIdx;
         Less.this.openSource();
         Less.this.firstLineToDisplay = this.saveFirstLineToDisplay;
         Less.this.firstColumnToDisplay = this.saveFirstColumnToDisplay;
         Less.this.offsetInLine = this.saveOffsetInLine;
         Less.this.printLineNumbers = this.savePrintLineNumbers;
         if (failingSource != null) {
            Less.this.message = failingSource + " not found!";
         }
      }
   }
}
