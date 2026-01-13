package org.jline.builtins;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.Highlighter;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.Macro;
import org.jline.reader.Reference;
import org.jline.reader.Widget;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.StyleResolver;

public class Commands {
   public static void tmux(
      Terminal terminal, PrintStream out, PrintStream err, Supplier<Object> getter, Consumer<Object> setter, Consumer<Terminal> runner, String[] argv
   ) throws Exception {
      String[] usage = new String[]{"tmux -  terminal multiplexer", "Usage: tmux [command]", "  -? --help                    Show help"};
      Options opt = Options.compile(usage).parse(argv);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         if (argv.length == 0) {
            Object instance = getter.get();
            if (instance != null) {
               err.println("tmux: can't run tmux inside itself");
            } else {
               Tmux tmux = new Tmux(terminal, err, runner);
               setter.accept(tmux);

               try {
                  tmux.run();
               } finally {
                  setter.accept(null);
               }
            }
         } else {
            Object instance = getter.get();
            if (instance != null) {
               ((Tmux)instance).execute(out, err, Arrays.asList(argv));
            } else {
               err.println("tmux: no instance running");
            }
         }
      }
   }

   public static void nano(Terminal terminal, PrintStream out, PrintStream err, Path currentDir, String[] argv) throws Exception {
      nano(terminal, out, err, currentDir, argv, null);
   }

   public static void nano(Terminal terminal, PrintStream out, PrintStream err, Path currentDir, String[] argv, ConfigurationPath configPath) throws Exception {
      Options opt = Options.compile(Nano.usage()).parse(argv);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         Nano edit = new Nano(terminal, currentDir, opt, configPath);
         edit.open(opt.args());
         edit.run();
      }
   }

   public static void less(Terminal terminal, InputStream in, PrintStream out, PrintStream err, Path currentDir, Object[] argv) throws Exception {
      less(terminal, in, out, err, currentDir, argv, null);
   }

   public static void less(Terminal terminal, InputStream in, PrintStream out, PrintStream err, Path currentDir, Object[] argv, ConfigurationPath configPath) throws Exception {
      Options opt = Options.compile(Less.usage()).parse(argv);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         Less less = new Less(terminal, currentDir, opt, configPath);
         List<Source> sources = new ArrayList<>();
         if (opt.argObjects().isEmpty()) {
            opt.argObjects().add("-");
         }

         for (Object o : opt.argObjects()) {
            if (o instanceof String) {
               String arg = (String)o;
               arg = arg.startsWith("~") ? arg.replace("~", System.getProperty("user.home")) : arg;
               if ("-".equals(arg)) {
                  sources.add(new Source.StdInSource(in));
               } else if (!arg.contains("*") && !arg.contains("?")) {
                  sources.add(new Source.URLSource(currentDir.resolve(arg).toUri().toURL(), arg));
               } else {
                  for (Path p : findFiles(currentDir, arg)) {
                     sources.add(new Source.URLSource(p.toUri().toURL(), p.toString()));
                  }
               }
            } else if (o instanceof Source) {
               sources.add((Source)o);
            } else {
               ByteArrayInputStream bais = null;
               if (o instanceof String[]) {
                  bais = new ByteArrayInputStream(String.join("\n", (String[])o).getBytes());
               } else if (o instanceof ByteArrayInputStream) {
                  bais = (ByteArrayInputStream)o;
               } else if (o instanceof byte[]) {
                  bais = new ByteArrayInputStream((byte[])o);
               }

               if (bais != null) {
                  sources.add(new Source.InputStreamSource(bais, true, "Less"));
               }
            }
         }

         less.run(sources);
      }
   }

   protected static List<Path> findFiles(Path root, String files) throws IOException {
      files = files.startsWith("~") ? files.replace("~", System.getProperty("user.home")) : files;
      Path searchRoot = Paths.get("/");
      String regex;
      if (new File(files).isAbsolute()) {
         regex = files.replaceAll("\\\\", "/").replaceAll("//", "/");
         if (regex.contains("/")) {
            String sr = regex.substring(0, regex.lastIndexOf("/") + 1);

            while (sr.contains("*") || sr.contains("?")) {
               sr = sr.substring(0, sr.lastIndexOf("/"));
            }

            searchRoot = Paths.get(sr + "/");
         }
      } else {
         regex = (root.toString().length() == 0 ? "" : root + "/") + files;
         regex = regex.replaceAll("\\\\", "/").replaceAll("//", "/");
         searchRoot = root;
      }

      PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + regex);
      Stream<Path> pathStream = Files.walk(searchRoot);

      List var6;
      try {
         var6 = pathStream.filter(pathMatcher::matches).collect(Collectors.toList());
      } catch (Throwable var9) {
         if (pathStream != null) {
            try {
               pathStream.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (pathStream != null) {
         pathStream.close();
      }

      return var6;
   }

   public static void history(LineReader reader, PrintStream out, PrintStream err, Path currentDir, String[] argv) throws Exception {
      String[] usage = new String[]{
         "history -  list history of commands",
         "Usage: history [-dnrfEie] [-m match] [first] [last]",
         "       history -ARWI [filename]",
         "       history -s [old=new] [command]",
         "       history --clear",
         "       history --save",
         "  -? --help                       Displays command help",
         "     --clear                      Clear history",
         "     --save                       Save history",
         "  -m match                        If option -m is present the first argument is taken as a pattern",
         "                                  and only the history events matching the pattern will be shown",
         "  -d                              Print timestamps for each event",
         "  -f                              Print full time date stamps in the US format",
         "  -E                              Print full time date stamps in the European format",
         "  -i                              Print full time date stamps in ISO8601 format",
         "  -n                              Suppresses command numbers",
         "  -r                              Reverses the order of the commands",
         "  -A                              Appends the history out to the given file",
         "  -R                              Reads the history from the given file",
         "  -W                              Writes the history out to the given file",
         "  -I                              If added to -R, only the events that are not contained within the internal list are added",
         "                                  If added to -W or -A, only the events that are new since the last incremental operation",
         "                                  to the file are added",
         "  [first] [last]                  These optional arguments may be specified as a number or as a string. A negative number",
         "                                  is used as an offset to the current history event number. A string specifies the most",
         "                                  recent event beginning with the given string.",
         "  -e                              Uses the nano editor to edit the commands before executing",
         "  -s                              Re-executes the command without invoking an editor"
      };
      Options opt = Options.compile(usage).parse(argv);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         History history = reader.getHistory();
         boolean done = true;
         boolean increment = opt.isSet("I");
         if (opt.isSet("clear")) {
            history.purge();
         } else if (opt.isSet("save")) {
            history.save();
         } else if (opt.isSet("A")) {
            Path file = !opt.args().isEmpty() ? currentDir.resolve(opt.args().get(0)) : null;
            history.append(file, increment);
         } else if (opt.isSet("R")) {
            Path file = !opt.args().isEmpty() ? currentDir.resolve(opt.args().get(0)) : null;
            history.read(file, increment);
         } else if (opt.isSet("W")) {
            Path file = !opt.args().isEmpty() ? currentDir.resolve(opt.args().get(0)) : null;
            history.write(file, increment);
         } else {
            done = false;
         }

         if (!done) {
            Commands.ReExecute execute = new Commands.ReExecute(history, opt);
            int argId = execute.getArgId();
            Pattern pattern = null;
            if (opt.isSet("m") && opt.args().size() > argId) {
               StringBuilder sb = new StringBuilder();
               char prev = '0';

               for (char c : opt.args().get(argId++).toCharArray()) {
                  if (c == '*' && prev != '\\' && prev != '.') {
                     sb.append('.');
                  }

                  sb.append(c);
                  prev = c;
               }

               pattern = Pattern.compile(sb.toString(), 32);
            }

            boolean reverse = opt.isSet("r") || opt.isSet("s") && opt.args().size() <= argId;
            int firstId = opt.args().size() > argId ? retrieveHistoryId(history, opt.args().get(argId++)) : -17;
            int lastId = opt.args().size() > argId ? retrieveHistoryId(history, opt.args().get(argId++)) : -1;
            firstId = historyId(firstId, history.first(), history.last());
            lastId = historyId(lastId, history.first(), history.last());
            if (firstId > lastId) {
               int tmpId = firstId;
               firstId = lastId;
               lastId = tmpId;
               reverse = !reverse;
            }

            int tot = lastId - firstId + 1;
            int listed = 0;
            Highlighter highlighter = reader.getHighlighter();
            Iterator<History.Entry> iter = null;
            if (reverse) {
               iter = history.reverseIterator(lastId);
            } else {
               iter = history.iterator(firstId);
            }

            while (iter.hasNext() && listed < tot) {
               History.Entry entry = iter.next();
               listed++;
               if (pattern == null || pattern.matcher(entry.line()).matches()) {
                  if (execute.isExecute()) {
                     if (!execute.isEdit()) {
                        execute.addCommandInBuffer(reader, entry.line());
                        break;
                     }

                     execute.addCommandInFile(entry.line());
                  } else {
                     AttributedStringBuilder sb = new AttributedStringBuilder();
                     if (!opt.isSet("n")) {
                        sb.append("  ");
                        sb.styled(AttributedStyle::bold, String.format("%3d", entry.index()));
                     }

                     if (opt.isSet("d") || opt.isSet("f") || opt.isSet("E") || opt.isSet("i")) {
                        sb.append("  ");
                        if (opt.isSet("d")) {
                           LocalTime lt = LocalTime.from(entry.time().atZone(ZoneId.systemDefault())).truncatedTo(ChronoUnit.SECONDS);
                           DateTimeFormatter.ISO_LOCAL_TIME.formatTo(lt, sb);
                        } else {
                           LocalDateTime lt = LocalDateTime.from(entry.time().atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES));
                           String format = "yyyy-MM-dd hh:mm";
                           if (opt.isSet("f")) {
                              format = "MM/dd/yy hh:mm";
                           } else if (opt.isSet("E")) {
                              format = "dd.MM.yyyy hh:mm";
                           }

                           DateTimeFormatter.ofPattern(format).formatTo(lt, sb);
                        }
                     }

                     sb.append("  ");
                     sb.append(highlighter.highlight(reader, entry.line()));
                     out.println(sb.toAnsi(reader.getTerminal()));
                  }
               }
            }

            execute.editCommandsAndClose(reader);
         }
      }
   }

   private static int historyId(int id, int minId, int maxId) {
      int out = id;
      if (id < 0) {
         out = maxId + id + 1;
      }

      if (out < minId) {
         out = minId;
      } else if (out > maxId) {
         out = maxId;
      }

      return out;
   }

   private static int retrieveHistoryId(History history, String s) throws IllegalArgumentException {
      try {
         return Integer.parseInt(s);
      } catch (NumberFormatException var5) {
         for (History.Entry entry : history) {
            if (entry.line().startsWith(s)) {
               return entry.index();
            }
         }

         throw new IllegalArgumentException("history: event not found: " + s);
      }
   }

   public static void complete(LineReader reader, PrintStream out, PrintStream err, Map<String, List<Completers.CompletionData>> completions, String[] argv) throws Options.HelpException {
      String[] usage = new String[]{
         "complete -  edit command specific tab-completions",
         "Usage: complete",
         "  -? --help                       Displays command help",
         "  -c --command=COMMAND            Command to add completion to",
         "  -d --description=DESCRIPTION    Description of this completions",
         "  -e --erase                      Erase the completions",
         "  -s --short-option=SHORT_OPTION  Posix-style option to complete",
         "  -l --long-option=LONG_OPTION    GNU-style option to complete",
         "  -a --argument=ARGUMENTS         A list of possible arguments",
         "  -n --condition=CONDITION        The completion should only be used if the",
         "                                  specified command has a zero exit status"
      };
      Options opt = Options.compile(usage).parse(argv);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         String command = opt.get("command");
         if (opt.isSet("erase")) {
            completions.remove(command);
         } else {
            List<Completers.CompletionData> cmdCompletions = completions.computeIfAbsent(command, s -> new ArrayList<>());
            List<String> options = null;
            if (opt.isSet("short-option")) {
               for (String op : opt.getList("short-option")) {
                  if (options == null) {
                     options = new ArrayList<>();
                  }

                  options.add("-" + op);
               }
            }

            if (opt.isSet("long-option")) {
               for (String op : opt.getList("long-option")) {
                  if (options == null) {
                     options = new ArrayList<>();
                  }

                  options.add("--" + op);
               }
            }

            String description = opt.isSet("description") ? opt.get("description") : null;
            String argument = opt.isSet("argument") ? opt.get("argument") : null;
            String condition = opt.isSet("condition") ? opt.get("condition") : null;
            cmdCompletions.add(new Completers.CompletionData(options, description, argument, condition));
         }
      }
   }

   public static void widget(LineReader reader, PrintStream out, PrintStream err, Function<String, Widget> widgetCreator, String[] argv) throws Exception {
      String[] usage = new String[]{
         "widget -  manipulate widgets",
         "Usage: widget -N new-widget [function-name]",
         "       widget -D widget ...",
         "       widget -A old-widget new-widget",
         "       widget -U string ...",
         "       widget -l [options]",
         "  -? --help                       Displays command help",
         "  -A                              Create alias to widget",
         "  -N                              Create new widget",
         "  -D                              Delete widgets",
         "  -U                              Push characters to the stack",
         "  -l                              List user-defined widgets",
         "  -a                              With -l, list all widgets"
      };
      Options opt = Options.compile(usage).parse(argv);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         int actions = (opt.isSet("N") ? 1 : 0) + (opt.isSet("D") ? 1 : 0) + (opt.isSet("U") ? 1 : 0) + (opt.isSet("l") ? 1 : 0) + (opt.isSet("A") ? 1 : 0);
         if (actions > 1) {
            err.println("widget: incompatible operation selection options");
         } else {
            if (opt.isSet("l")) {
               TreeSet<String> ws = new TreeSet<>(reader.getWidgets().keySet());
               if (opt.isSet("a")) {
                  for (String s : new HashSet<>(ws)) {
                     ws.add(reader.getWidgets().get(s).toString());
                  }
               }

               for (String s : ws) {
                  if (opt.isSet("a")) {
                     out.println(s);
                  } else if (!reader.getWidgets().get(s).toString().startsWith(".")) {
                     out.println(s + " (" + reader.getWidgets().get(s) + ")");
                  }
               }
            } else if (opt.isSet("N")) {
               if (opt.args().size() < 1) {
                  err.println("widget: not enough arguments for -N");
                  return;
               }

               if (opt.args().size() > 2) {
                  err.println("widget: too many arguments for -N");
                  return;
               }

               String name = opt.args().get(0);
               String func = opt.args().size() == 2 ? opt.args().get(1) : name;
               reader.getWidgets().put(name, widgetCreator.apply(func));
            } else if (opt.isSet("D")) {
               for (String name : opt.args()) {
                  reader.getWidgets().remove(name);
               }
            } else if (opt.isSet("A")) {
               if (opt.args().size() < 2) {
                  err.println("widget: not enough arguments for -A");
                  return;
               }

               if (opt.args().size() > 2) {
                  err.println("widget: too many arguments for -A");
                  return;
               }

               Widget org = null;
               if (opt.args().get(0).startsWith(".")) {
                  org = reader.getBuiltinWidgets().get(opt.args().get(0).substring(1));
               } else {
                  org = reader.getWidgets().get(opt.args().get(0));
               }

               if (org == null) {
                  err.println("widget: no such widget `" + opt.args().get(0) + "'");
                  return;
               }

               reader.getWidgets().put(opt.args().get(1), org);
            } else if (opt.isSet("U")) {
               for (String arg : opt.args()) {
                  reader.runMacro(KeyMap.translate(arg));
               }
            } else if (opt.args().size() == 1) {
               reader.callWidget(opt.args().get(0));
            }
         }
      }
   }

   public static void keymap(LineReader reader, PrintStream out, PrintStream err, String[] argv) throws Options.HelpException {
      String[] usage = new String[]{
         "keymap -  manipulate keymaps",
         "Usage: keymap [options] -l [-L] [keymap ...]",
         "       keymap [options] -d",
         "       keymap [options] -D keymap ...",
         "       keymap [options] -A old-keymap new-keymap",
         "       keymap [options] -N new-keymap [old-keymap]",
         "       keymap [options] -M",
         "       keymap [options] -r in-string ...",
         "       keymap [options] -s in-string out-string ...",
         "       keymap [options] in-string command ...",
         "       keymap [options] [in-string]",
         "  -? --help                       Displays command help",
         "  -A                              Create alias to keymap",
         "  -D                              Delete named keymaps",
         "  -L                              Output in form of keymap commands",
         "  -M (default=main)               Specify keymap to select",
         "  -N                              Create new keymap",
         "  -R                              Interpret in-strings as ranges",
         "  -a                              Select vicmd keymap",
         "  -d                              Delete existing keymaps and reset to default state",
         "  -e                              Select emacs keymap and bind it to main",
         "  -l                              List existing keymap names",
         "  -p                              List bindings which have given key sequence as a a prefix",
         "  -r                              Unbind specified in-strings ",
         "  -s                              Bind each in-string to each out-string ",
         "  -v                              Select viins keymap and bind it to main"
      };
      Options opt = Options.compile(usage).parse(argv);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         Map<String, KeyMap<Binding>> keyMaps = reader.getKeyMaps();
         int actions = (opt.isSet("N") ? 1 : 0)
            + (opt.isSet("d") ? 1 : 0)
            + (opt.isSet("D") ? 1 : 0)
            + (opt.isSet("l") ? 1 : 0)
            + (opt.isSet("r") ? 1 : 0)
            + (opt.isSet("s") ? 1 : 0)
            + (opt.isSet("A") ? 1 : 0);
         if (actions > 1) {
            err.println("keymap: incompatible operation selection options");
         } else {
            if (opt.isSet("l")) {
               boolean commands = opt.isSet("L");
               if (!opt.args().isEmpty()) {
                  for (String arg : opt.args()) {
                     KeyMap<Binding> map = keyMaps.get(arg);
                     if (map == null) {
                        err.println("keymap: no such keymap: `" + arg + "'");
                     } else {
                        out.println(arg);
                     }
                  }
               } else {
                  keyMaps.keySet().forEach(out::println);
               }
            } else if (opt.isSet("N")) {
               if (opt.isSet("e") || opt.isSet("v") || opt.isSet("a") || opt.isSet("M")) {
                  err.println("keymap: keymap can not be selected with -N");
                  return;
               }

               if (opt.args().size() < 1) {
                  err.println("keymap: not enough arguments for -N");
                  return;
               }

               if (opt.args().size() > 2) {
                  err.println("keymap: too many arguments for -N");
                  return;
               }

               KeyMap<Binding> org = null;
               if (opt.args().size() == 2) {
                  org = keyMaps.get(opt.args().get(1));
                  if (org == null) {
                     err.println("keymap: no such keymap `" + opt.args().get(1) + "'");
                     return;
                  }
               }

               KeyMap<Binding> map = new KeyMap<>();
               if (org != null) {
                  for (Entry<String, Binding> bound : org.getBoundKeys().entrySet()) {
                     map.bind(bound.getValue(), bound.getKey());
                  }
               }

               keyMaps.put(opt.args().get(0), map);
            } else if (opt.isSet("A")) {
               if (opt.isSet("e") || opt.isSet("v") || opt.isSet("a") || opt.isSet("M")) {
                  err.println("keymap: keymap can not be selected with -N");
                  return;
               }

               if (opt.args().size() < 2) {
                  err.println("keymap: not enough arguments for -A");
                  return;
               }

               if (opt.args().size() > 2) {
                  err.println("keymap: too many arguments for -A");
                  return;
               }

               KeyMap<Binding> orgx = keyMaps.get(opt.args().get(0));
               if (orgx == null) {
                  err.println("keymap: no such keymap `" + opt.args().get(0) + "'");
                  return;
               }

               keyMaps.put(opt.args().get(1), orgx);
            } else if (opt.isSet("d")) {
               if (opt.isSet("e") || opt.isSet("v") || opt.isSet("a") || opt.isSet("M")) {
                  err.println("keymap: keymap can not be selected with -N");
                  return;
               }

               if (!opt.args().isEmpty()) {
                  err.println("keymap: too many arguments for -d");
                  return;
               }

               keyMaps.clear();
               keyMaps.putAll(reader.defaultKeyMaps());
            } else if (opt.isSet("D")) {
               if (opt.isSet("e") || opt.isSet("v") || opt.isSet("a") || opt.isSet("M")) {
                  err.println("keymap: keymap can not be selected with -N");
                  return;
               }

               if (opt.args().size() < 1) {
                  err.println("keymap: not enough arguments for -A");
                  return;
               }

               for (String name : opt.args()) {
                  if (keyMaps.remove(name) == null) {
                     err.println("keymap: no such keymap `" + name + "'");
                     return;
                  }
               }
            } else if (opt.isSet("r")) {
               String keyMapName = "main";
               int sel = (opt.isSet("a") ? 1 : 0) + (opt.isSet("e") ? 1 : 0) + (opt.isSet("v") ? 1 : 0) + (opt.isSet("M") ? 1 : 0);
               if (sel > 1) {
                  err.println("keymap: incompatible keymap selection options");
                  return;
               }

               if (opt.isSet("a")) {
                  keyMapName = "vicmd";
               } else if (opt.isSet("e")) {
                  keyMapName = "emacs";
               } else if (opt.isSet("v")) {
                  keyMapName = "viins";
               } else if (opt.isSet("M")) {
                  if (opt.args().isEmpty()) {
                     err.println("keymap: argument expected: -M");
                     return;
                  }

                  keyMapName = opt.args().remove(0);
               }

               KeyMap<Binding> map = keyMaps.get(keyMapName);
               if (map == null) {
                  err.println("keymap: no such keymap `" + keyMapName + "'");
                  return;
               }

               boolean range = opt.isSet("R");
               boolean prefix = opt.isSet("p");
               Set<String> toRemove = new HashSet<>();
               Map<String, Binding> bound = map.getBoundKeys();

               for (String argx : opt.args()) {
                  if (range) {
                     Collection<String> r = KeyMap.range(opt.args().get(0));
                     if (r == null) {
                        err.println("keymap: malformed key range `" + opt.args().get(0) + "'");
                        return;
                     }

                     toRemove.addAll(r);
                  } else {
                     String seq = KeyMap.translate(argx);

                     for (String k : bound.keySet()) {
                        if (prefix && k.startsWith(seq) && k.length() > seq.length() || !prefix && k.equals(seq)) {
                           toRemove.add(k);
                        }
                     }
                  }
               }

               for (String seq : toRemove) {
                  map.unbind(seq);
               }

               if (opt.isSet("e") || opt.isSet("v")) {
                  keyMaps.put("main", map);
               }
            } else if (!opt.isSet("s") && opt.args().size() <= 1) {
               String keyMapNamex = "main";
               int selx = (opt.isSet("a") ? 1 : 0) + (opt.isSet("e") ? 1 : 0) + (opt.isSet("v") ? 1 : 0) + (opt.isSet("M") ? 1 : 0);
               if (selx > 1) {
                  err.println("keymap: incompatible keymap selection options");
                  return;
               }

               if (opt.isSet("a")) {
                  keyMapNamex = "vicmd";
               } else if (opt.isSet("e")) {
                  keyMapNamex = "emacs";
               } else if (opt.isSet("v")) {
                  keyMapNamex = "viins";
               } else if (opt.isSet("M")) {
                  if (opt.args().isEmpty()) {
                     err.println("keymap: argument expected: -M");
                     return;
                  }

                  keyMapNamex = opt.args().remove(0);
               }

               KeyMap<Binding> mapx = keyMaps.get(keyMapNamex);
               if (mapx == null) {
                  err.println("keymap: no such keymap `" + keyMapNamex + "'");
                  return;
               }

               boolean prefix = opt.isSet("p");
               boolean commands = opt.isSet("L");
               if (prefix && opt.args().isEmpty()) {
                  err.println("keymap: option -p requires a prefix string");
                  return;
               }

               if (!opt.args().isEmpty() || !opt.isSet("e") && !opt.isSet("v")) {
                  Map<String, Binding> bound = mapx.getBoundKeys();
                  String seq = !opt.args().isEmpty() ? KeyMap.translate(opt.args().get(0)) : null;
                  Entry<String, Binding> begin = null;
                  String last = null;
                  Iterator<Entry<String, Binding>> iterator = bound.entrySet().iterator();

                  while (iterator.hasNext()) {
                     Entry<String, Binding> entry = iterator.next();
                     String key = entry.getKey();
                     if (seq == null || prefix && key.startsWith(seq) && !key.equals(seq) || !prefix && key.equals(seq)) {
                        if (begin == null && iterator.hasNext()) {
                           begin = entry;
                           last = key;
                        } else {
                           String n = (last.length() > 1 ? last.substring(0, last.length() - 1) : "") + (char)(last.charAt(last.length() - 1) + 1);
                           if (key.equals(n) && entry.getValue().equals(begin.getValue())) {
                              last = key;
                           } else {
                              StringBuilder sb = new StringBuilder();
                              if (commands) {
                                 sb.append("keymap -M ");
                                 sb.append(keyMapNamex);
                                 sb.append(" ");
                              }

                              if (begin.getKey().equals(last)) {
                                 sb.append(KeyMap.display(last));
                                 sb.append(" ");
                                 displayValue(sb, begin.getValue());
                                 out.println(sb);
                              } else {
                                 if (commands) {
                                    sb.append("-R ");
                                 }

                                 sb.append(KeyMap.display(begin.getKey()));
                                 sb.append("-");
                                 sb.append(KeyMap.display(last));
                                 sb.append(" ");
                                 displayValue(sb, begin.getValue());
                                 out.println(sb);
                              }

                              begin = entry;
                              last = key;
                           }
                        }
                     }
                  }
               }

               if (opt.isSet("e") || opt.isSet("v")) {
                  keyMaps.put("main", mapx);
               }
            } else {
               String keyMapNamexx = "main";
               int selxx = (opt.isSet("a") ? 1 : 0) + (opt.isSet("e") ? 1 : 0) + (opt.isSet("v") ? 1 : 0) + (opt.isSet("M") ? 1 : 0);
               if (selxx > 1) {
                  err.println("keymap: incompatible keymap selection options");
                  return;
               }

               if (opt.isSet("a")) {
                  keyMapNamexx = "vicmd";
               } else if (opt.isSet("e")) {
                  keyMapNamexx = "emacs";
               } else if (opt.isSet("v")) {
                  keyMapNamexx = "viins";
               } else if (opt.isSet("M")) {
                  if (opt.args().isEmpty()) {
                     err.println("keymap: argument expected: -M");
                     return;
                  }

                  keyMapNamexx = opt.args().remove(0);
               }

               KeyMap<Binding> mapxx = keyMaps.get(keyMapNamexx);
               if (mapxx == null) {
                  err.println("keymap: no such keymap `" + keyMapNamexx + "'");
                  return;
               }

               boolean range = opt.isSet("R");
               if (opt.args().size() % 2 == 1) {
                  err.println("keymap: even number of arguments required");
                  return;
               }

               for (int i = 0; i < opt.args().size(); i += 2) {
                  Binding bout = (Binding)(opt.isSet("s") ? new Macro(KeyMap.translate(opt.args().get(i + 1))) : new Reference(opt.args().get(i + 1)));
                  if (range) {
                     Collection<String> r = KeyMap.range(opt.args().get(i));
                     if (r == null) {
                        err.println("keymap: malformed key range `" + opt.args().get(i) + "'");
                        return;
                     }

                     mapxx.bind(bout, r);
                  } else {
                     String in = KeyMap.translate(opt.args().get(i));
                     mapxx.bind(bout, in);
                  }
               }

               if (opt.isSet("e") || opt.isSet("v")) {
                  keyMaps.put("main", mapxx);
               }
            }
         }
      }
   }

   public static void setopt(LineReader reader, PrintStream out, PrintStream err, String[] argv) throws Options.HelpException {
      String[] usage = new String[]{
         "setopt -  set options",
         "Usage: setopt [-m] option ...",
         "       setopt",
         "  -? --help                       Displays command help",
         "  -m                              Use pattern matching"
      };
      Options opt = Options.compile(usage).parse(argv);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         if (opt.args().isEmpty()) {
            for (LineReader.Option option : LineReader.Option.values()) {
               if (reader.isSet(option) != option.isDef()) {
                  out.println((option.isDef() ? "no-" : "") + option.toString().toLowerCase().replace('_', '-'));
               }
            }
         } else {
            boolean match = opt.isSet("m");
            doSetOpts(reader, out, err, opt.args(), match, true);
         }
      }
   }

   public static void unsetopt(LineReader reader, PrintStream out, PrintStream err, String[] argv) throws Options.HelpException {
      String[] usage = new String[]{
         "unsetopt -  unset options",
         "Usage: unsetopt [-m] option ...",
         "       unsetopt",
         "  -? --help                       Displays command help",
         "  -m                              Use pattern matching"
      };
      Options opt = Options.compile(usage).parse(argv);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         if (opt.args().isEmpty()) {
            for (LineReader.Option option : LineReader.Option.values()) {
               if (reader.isSet(option) == option.isDef()) {
                  out.println((option.isDef() ? "no-" : "") + option.toString().toLowerCase().replace('_', '-'));
               }
            }
         } else {
            boolean match = opt.isSet("m");
            doSetOpts(reader, out, err, opt.args(), match, false);
         }
      }
   }

   private static void doSetOpts(LineReader reader, PrintStream out, PrintStream err, List<String> options, boolean match, boolean set) {
      for (String name : options) {
         String tname = name.toLowerCase().replaceAll("[-_]", "");
         if (match) {
            tname = tname.replaceAll("\\*", "[a-z]*");
            tname = tname.replaceAll("\\?", "[a-z]");
         }

         boolean found = false;

         for (LineReader.Option option : LineReader.Option.values()) {
            String optName = option.name().toLowerCase().replaceAll("[-_]", "");
            if (match ? !optName.matches(tname) : !optName.equals(tname)) {
               if (match ? ("no" + optName).matches(tname) : ("no" + optName).equals(tname)) {
                  if (set) {
                     reader.unsetOpt(option);
                  } else {
                     reader.setOpt(option);
                  }

                  if (!match) {
                     found = true;
                  }
                  break;
               }
            } else {
               if (set) {
                  reader.setOpt(option);
               } else {
                  reader.unsetOpt(option);
               }

               found = true;
               if (!match) {
                  break;
               }
            }
         }

         if (!found) {
            err.println("No matching option: " + name);
         }
      }
   }

   private static void displayValue(StringBuilder sb, Object value) {
      if (value == null) {
         sb.append("undefined-key");
      } else if (value instanceof Macro) {
         sb.append(KeyMap.display(((Macro)value).getSequence()));
      } else if (value instanceof Reference) {
         sb.append(((Reference)value).name());
      } else {
         sb.append(value);
      }
   }

   public static void setvar(LineReader lineReader, PrintStream out, PrintStream err, String[] argv) throws Options.HelpException {
      String[] usage = new String[]{"setvar -  set lineReader variable value", "Usage: setvar [variable] [value]", "  -? --help                    Show help"};
      Options opt = Options.compile(usage).parse(argv);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         if (opt.args().isEmpty()) {
            for (Entry<String, Object> entry : lineReader.getVariables().entrySet()) {
               out.println(entry.getKey() + ": " + entry.getValue());
            }
         } else if (opt.args().size() == 1) {
            out.println(lineReader.getVariable(opt.args().get(0)));
         } else {
            lineReader.setVariable(opt.args().get(0), opt.args().get(1));
         }
      }
   }

   public static void colors(Terminal terminal, PrintStream out, String[] argv) throws Options.HelpException, IOException {
      String[] usage = new String[]{
         "colors -  view 256-color table and ANSI-styles",
         "Usage: colors [OPTIONS]",
         "  -? --help                     Displays command help",
         "  -a --ansistyles               List ANSI-styles",
         "  -c --columns=COLUMNS          Number of columns in name/rgb table",
         "                                COLUMNS = 1, display columns: color, style, ansi and HSL",
         "  -f --find=NAME                Find color names which contains NAME ",
         "  -l --lock=STYLE               Lock fore- or background color",
         "  -n --name                     Color name table (default number table)",
         "  -r --rgb                      Use and display rgb value",
         "  -s --small                    View 16-color table (default 256-color)",
         "  -v --view=COLOR               View 24bit color table of COLOR ",
         "                                COLOR = <colorName>, <color24bit> or hue<angle>"
      };
      Options opt = Options.compile(usage).parse(argv);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         Commands.Colors colors = new Commands.Colors(terminal, out);
         if (opt.isSet("ansistyles")) {
            colors.printStyles();
         } else {
            String style = null;
            if (opt.isSet("lock")) {
               style = opt.get("lock");
               if (style.length() - style.replace(":", "").length() > 1) {
                  style = null;
               }
            }

            if (!opt.isSet("view")) {
               boolean rgb = opt.isSet("rgb");
               int columns = terminal.getWidth() > (rgb ? 71 : 122) ? 6 : 5;
               String findName = null;
               boolean nameTable = opt.isSet("name");
               boolean table16 = opt.isSet("small");
               if (opt.isSet("find")) {
                  findName = opt.get("find").toLowerCase();
                  nameTable = true;
                  table16 = false;
                  columns = 4;
               }

               if (table16) {
                  columns += 2;
               }

               if (opt.isSet("columns")) {
                  columns = opt.getNumber("columns");
               }

               colors.printColors(nameTable, rgb, table16, columns, findName, style);
            } else {
               colors.printColor(opt.get("view").toLowerCase(), style);
            }
         }
      }
   }

   public static void highlighter(LineReader lineReader, Terminal terminal, PrintStream out, PrintStream err, String[] argv, ConfigurationPath configPath) throws Options.HelpException {
      String[] usage = new String[]{
         "highlighter -  manage nanorc theme system",
         "Usage: highlighter [OPTIONS]",
         "  -? --help                       Displays command help",
         "  -c --columns=COLUMNS            Number of columns in theme view",
         "  -l --list                       List available nanorc themes",
         "  -r --refresh                    Refresh highlighter config",
         "  -s --switch=THEME               Switch nanorc theme",
         "  -v --view=THEME                 View nanorc theme"
      };
      Options opt = Options.compile(usage).parse(argv);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         try {
            if (opt.isSet("refresh")) {
               lineReader.getHighlighter().refresh(lineReader);
            } else if (opt.isSet("switch")) {
               Path userConfig = configPath.getUserConfig("jnanorc");
               if (userConfig != null) {
                  SyntaxHighlighter sh = SyntaxHighlighter.build(userConfig, null);
                  Path currentTheme = sh.getCurrentTheme();
                  String newTheme = replaceFileName(currentTheme, opt.get("switch"));
                  File themeFile = new File(newTheme);
                  if (themeFile.exists()) {
                     switchTheme(err, userConfig, newTheme);
                     Path lessConfig = configPath.getUserConfig("jlessrc");
                     if (lessConfig != null) {
                        switchTheme(err, lessConfig, newTheme);
                     }

                     lineReader.getHighlighter().refresh(lineReader);
                  }
               }
            } else {
               Path config = configPath.getConfig("jnanorc");
               Path currentTheme = config != null ? SyntaxHighlighter.build(config, null).getCurrentTheme() : null;
               if (currentTheme != null) {
                  if (opt.isSet("list")) {
                     String parameter = replaceFileName(currentTheme, "*.nanorctheme");
                     out.println(currentTheme.getParent() + ":");
                     PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + parameter);
                     Stream<Path> pathStream = Files.walk(Paths.get(new File(parameter).getParent()));

                     try {
                        pathStream.filter(pathMatcher::matches).forEach(p -> out.println(p.getFileName()));
                     } catch (Throwable var25) {
                        if (pathStream != null) {
                           try {
                              pathStream.close();
                           } catch (Throwable var23) {
                              var25.addSuppressed(var23);
                           }
                        }

                        throw var25;
                     }

                     if (pathStream != null) {
                        pathStream.close();
                     }
                  } else {
                     Path themeFile;
                     if (opt.isSet("view")) {
                        themeFile = Paths.get(replaceFileName(currentTheme, opt.get("view")));
                     } else {
                        themeFile = currentTheme;
                     }

                     out.println(themeFile.toAbsolutePath());
                     BufferedReader reader = Files.newBufferedReader(themeFile);

                     try {
                        List<List<String>> tokens = new ArrayList<>();
                        int maxKeyLen = 0;
                        int maxValueLen = 0;

                        String line;
                        while ((line = reader.readLine()) != null) {
                           line = line.trim();
                           if (!line.isEmpty() && !line.startsWith("#")) {
                              List<String> parts = Arrays.asList(line.split("\\s+", 2));
                              if (parts.get(0).matches("[A-Z_]+")) {
                                 if (parts.get(0).length() > maxKeyLen) {
                                    maxKeyLen = parts.get(0).length();
                                 }

                                 if (parts.get(1).length() > maxValueLen) {
                                    maxValueLen = parts.get(1).length();
                                 }

                                 tokens.add(parts);
                              }
                           }
                        }

                        AttributedStringBuilder asb = new AttributedStringBuilder();
                        maxKeyLen += 2;
                        maxValueLen++;
                        int cols = opt.isSet("columns") ? opt.getNumber("columns") : 2;
                        List<Integer> tabstops = new ArrayList<>();

                        for (int c = 0; c < cols; c++) {
                           tabstops.add((c + 1) * maxKeyLen + c * maxValueLen);
                           tabstops.add((c + 1) * maxKeyLen + (c + 1) * maxValueLen);
                        }

                        asb.tabs(tabstops);
                        int ind = 0;

                        for (List<String> token : tokens) {
                           asb.style(AttributedStyle.DEFAULT).append(" ");
                           asb.style(compileStyle("token" + ind++, token.get(1)));
                           asb.append(token.get(0)).append("\t");
                           asb.append(token.get(1));
                           asb.style(AttributedStyle.DEFAULT).append("\t");
                           if (ind % cols == 0) {
                              asb.style(AttributedStyle.DEFAULT).append("\n");
                           }
                        }

                        asb.toAttributedString().println(terminal);
                     } catch (Throwable var24) {
                        if (reader != null) {
                           try {
                              reader.close();
                           } catch (Throwable var22) {
                              var24.addSuppressed(var22);
                           }
                        }

                        throw var24;
                     }

                     if (reader != null) {
                        reader.close();
                     }
                  }
               }
            }
         } catch (Exception var26) {
            err.println(var26.getMessage());
         }
      }
   }

   private static void switchTheme(PrintStream err, Path config, String theme) {
      try {
         Stream<String> stream = Files.lines(config, StandardCharsets.UTF_8);

         try {
            List<String> list = stream.<String>map(line -> line.matches("\\s*theme\\s+.*") ? "theme " + theme : line).collect(Collectors.toList());
            Files.write(config, list, StandardCharsets.UTF_8);
         } catch (Throwable var7) {
            if (stream != null) {
               try {
                  stream.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (stream != null) {
            stream.close();
         }
      } catch (IOException var8) {
         err.println(var8.getMessage());
      }
   }

   private static String replaceFileName(Path path, String name) {
      int nameLength = path.getFileName().toString().length();
      int pathLength = path.toString().length();
      return (path.toString().substring(0, pathLength - nameLength) + name).replace("\\", "\\\\");
   }

   private static AttributedStyle compileStyle(String reference, String colorDef) {
      Map<String, String> spec = new HashMap<>();
      spec.put(reference, colorDef);
      Styles.StyleCompiler sh = new Styles.StyleCompiler(spec, true);
      return new StyleResolver(sh::getStyle).resolve("." + reference);
   }

   private static class Colors {
      private static final String COLORS_24BIT = "[0-9a-fA-F]{6}";
      private static final List<String> COLORS_16 = Arrays.asList(
         "black", "red", "green", "yellow", "blue", "magenta", "cyan", "white", "!black", "!red", "!green", "!yellow", "!blue", "!magenta", "!cyan", "!white"
      );
      boolean name;
      boolean rgb;
      private final Terminal terminal;
      private final PrintStream out;
      private boolean fixedBg;
      private String fixedStyle;
      int r;
      int g;
      int b;

      public Colors(Terminal terminal, PrintStream out) {
         this.terminal = terminal;
         this.out = out;
      }

      private String getAnsiStyle(String style) {
         return style;
      }

      public void printStyles() {
         AttributedStringBuilder asb = new AttributedStringBuilder();
         asb.tabs(13);

         for (String s : Styles.ANSI_STYLES) {
            AttributedStyle as = new StyleResolver(this::getAnsiStyle).resolve("." + s);
            asb.style(as);
            asb.append(s);
            asb.style(AttributedStyle.DEFAULT);
            asb.append("\t");
            asb.append(this.getAnsiStyle(s));
            asb.append("\t");
            asb.append(as.toAnsi());
            asb.append("\n");
         }

         asb.toAttributedString().println(this.terminal);
      }

      private String getStyle(String color) {
         char fg = ' ';
         String out;
         if (this.name) {
            out = (this.fixedBg ? "fg:" : "bg:") + "~" + color.substring(1);
            fg = color.charAt(0);
         } else if (this.rgb) {
            out = (this.fixedBg ? "fg-rgb:" : "bg-rgb:") + "#" + color.substring(1);
            fg = color.charAt(0);
         } else if (color.substring(1).matches("\\d+")) {
            out = (this.fixedBg ? "38;5;" : "48;5;") + color.substring(1);
            fg = color.charAt(0);
         } else {
            out = (this.fixedBg ? "fg:" : "bg:") + color;
         }

         if (this.fixedStyle == null) {
            if (!color.startsWith("!") && !color.equals("white") && fg != 'b') {
               out = out + ",fg:!white";
            } else {
               out = out + ",fg:black";
            }
         } else {
            out = out + "," + this.fixedStyle;
         }

         return out;
      }

      private String foreground(int idx) {
         String fg = "w";
         if (idx > 6 && idx < 16
            || idx > 33 && idx < 52
            || idx > 69 && idx < 88
            || idx > 105 && idx < 124
            || idx > 141 && idx < 160
            || idx > 177 && idx < 196
            || idx > 213 && idx < 232
            || idx > 243) {
            fg = "b";
         }

         return fg;
      }

      private String addPadding(int width, String field) {
         int s = width - field.length();
         int left = s / 2;
         StringBuilder lp = new StringBuilder();
         StringBuilder rp = new StringBuilder();

         for (int i = 0; i < left; i++) {
            lp.append(" ");
         }

         for (int i = 0; i < s - left; i++) {
            rp.append(" ");
         }

         return lp + field + rp;
      }

      private String addLeftPadding(int width, String field) {
         int s = width - field.length();
         StringBuilder lp = new StringBuilder();

         for (int i = 0; i < s; i++) {
            lp.append(" ");
         }

         return lp + field;
      }

      private void setFixedStyle(String style) {
         this.fixedStyle = style;
         if (style != null && (style.contains("b:") || style.contains("b-") || style.contains("bg:") || style.contains("bg-") || style.contains("background"))) {
            this.fixedBg = true;
         }
      }

      private List<String> retrieveColorNames() throws IOException {
         InputStream is = new Source.ResourceSource("/org/jline/utils/colors.txt", null).read();

         List<String> out;
         try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            try {
               out = br.lines().map(String::trim).filter(s -> !s.startsWith("#")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
            } catch (Throwable var8) {
               try {
                  br.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            br.close();
         } catch (Throwable var9) {
            if (is != null) {
               try {
                  is.close();
               } catch (Throwable var6) {
                  var9.addSuppressed(var6);
               }
            }

            throw var9;
         }

         if (is != null) {
            is.close();
         }

         return out;
      }

      public void printColors(boolean name, boolean rgb, boolean small, int columns, String findName, String style) throws IOException {
         this.name = !rgb && name;
         this.rgb = rgb;
         this.setFixedStyle(style);
         AttributedStringBuilder asb = new AttributedStringBuilder();
         int width = this.terminal.getWidth();
         String tableName = small ? " 16-color " : "256-color ";
         if (!name && !rgb) {
            this.out.print(tableName);
            this.out.print("table, fg:<name> ");
            if (!small) {
               this.out.print("/ 38;5;<n>");
            }

            this.out.println();
            this.out.print("                 bg:<name> ");
            if (!small) {
               this.out.print("/ 48;5;<n>");
            }

            this.out.println("\n");
            boolean narrow = width < 180;

            for (String c : COLORS_16) {
               AttributedStyle ss = new StyleResolver(this::getStyle).resolve('.' + c, null);
               asb.style(ss);
               asb.append(this.addPadding(11, c));
               asb.style(AttributedStyle.DEFAULT);
               if (c.equals("white")) {
                  if (!narrow && !small) {
                     asb.append("    ");
                  } else {
                     asb.append('\n');
                  }
               } else if (c.equals("!white")) {
                  asb.append('\n');
               }
            }

            asb.append('\n');
            if (!small) {
               for (int i = 16; i < 256; i++) {
                  String fg = this.foreground(i);
                  String code = Integer.toString(i);
                  AttributedStyle ss = new StyleResolver(this::getStyle).resolve("." + fg + code, null);
                  asb.style(ss);
                  String str = " ";
                  if (i < 100) {
                     str = "  ";
                  } else if (i > 231) {
                     str = i % 2 == 0 ? "    " : "   ";
                  }

                  asb.append(str).append(code).append(' ');
                  if (i == 51
                     || i == 87
                     || i == 123
                     || i == 159
                     || i == 195
                     || i == 231
                     || narrow && (i == 33 || i == 69 || i == 105 || i == 141 || i == 177 || i == 213 || i == 243)) {
                     asb.style(AttributedStyle.DEFAULT);
                     asb.append('\n');
                     if (i == 231) {
                        asb.append('\n');
                     }
                  }
               }
            }
         } else {
            this.out.print(tableName);
            if (name) {
               asb.tabs(Arrays.asList(25, 60, 75));
               this.out.println("table, fg:~<name> OR 38;5;<n>");
               this.out.println("                 bg:~<name> OR 48;5;<n>");
            } else {
               asb.tabs(Arrays.asList(15, 45, 70));
               this.out.println("table, fg-rgb:<color24bit> OR 38;5;<n>");
               this.out.println("                 bg-rgb:<color24bit> OR 48;5;<n>");
            }

            this.out.println();
            int col = 0;
            int idx = 0;
            int colWidth = rgb ? 12 : 21;
            int lb = 1;
            if (findName != null && (findName.startsWith("#") || findName.startsWith("x"))) {
               findName = findName.substring(1);
            }

            for (String line : this.retrieveColorNames()) {
               if (!rgb) {
                  if (findName != null) {
                     if (!line.toLowerCase().contains(findName)) {
                        idx++;
                        continue;
                     }
                  } else if (small) {
                     colWidth = 15;
                     lb = 1;
                  } else if (columns > 4) {
                     if (idx > 15 && idx < 232) {
                        colWidth = columns == 6 && col != 1 && col != 2 && col != 3 ? 20 : 21;
                        lb = 1;
                     } else {
                        colWidth = columns == 6 && idx % 2 != 0 && col != 7 ? 16 : 15;
                        lb = -1;
                     }
                  }
               }

               String fgx = this.foreground(idx);
               if (rgb) {
                  line = Integer.toHexString(org.jline.utils.Colors.DEFAULT_COLORS_256[idx]);

                  for (int p = line.length(); p < 6; p++) {
                     line = "0" + line;
                  }

                  if (findName != null && !line.toLowerCase().matches(findName)) {
                     idx++;
                     continue;
                  }
               }

               AttributedStyle ssx = new StyleResolver(this::getStyle).resolve("." + fgx + line, null);
               if (rgb) {
                  line = "#" + line;
               }

               asb.style(ssx);
               String idxstr = Integer.toString(idx);
               if (rgb) {
                  if (idx < 10) {
                     idxstr = "  " + idxstr;
                  } else if (idx < 100) {
                     idxstr = " " + idxstr;
                  }
               }

               asb.append(idxstr).append(this.addPadding(colWidth - idxstr.length(), line));
               if (columns == 1) {
                  asb.style(AttributedStyle.DEFAULT);
                  asb.append("\t").append(this.getStyle(fgx + line.substring(rgb ? 1 : 0)));
                  asb.append("\t").append(ssx.toAnsi());
                  int[] rgb1 = this.rgb(org.jline.utils.Colors.DEFAULT_COLORS_256[idx]);
                  int[] hsl = this.rgb2hsl(rgb1[0], rgb1[1], rgb1[2]);
                  asb.append("\t")
                     .append(this.addLeftPadding(6, hsl[0] + ", "))
                     .append(this.addLeftPadding(4, hsl[1] + "%"))
                     .append(", ")
                     .append(this.addLeftPadding(4, hsl[2] + "%"));
               }

               col++;
               idx++;
               if ((col + 1) * colWidth > width || col + lb > columns) {
                  col = 0;
                  asb.style(AttributedStyle.DEFAULT);
                  asb.append('\n');
               }

               if (findName == null) {
                  if (idx == 16) {
                     if (small) {
                        break;
                     }

                     if (col != 0) {
                        col = 0;
                        asb.style(AttributedStyle.DEFAULT);
                        asb.append('\n');
                     }
                  } else if (idx == 232 && col != 0) {
                     col = 0;
                     asb.style(AttributedStyle.DEFAULT);
                     asb.append('\n');
                  }
               }
            }
         }

         asb.toAttributedString().println(this.terminal);
      }

      private int[] rgb(long color) {
         int[] rgb = new int[]{0, 0, 0};
         rgb[0] = (int)(color >> 16 & 255L);
         rgb[1] = (int)(color >> 8 & 255L);
         rgb[2] = (int)(color & 255L);
         return rgb;
      }

      private int[] hue2rgb(int degree) {
         int[] rgb = new int[]{0, 0, 0};
         double hue = degree / 60.0;
         double a = Math.tan(degree / 360.0 * 2.0 * Math.PI) / Math.sqrt(3.0);
         if (hue >= 0.0 && hue < 1.0) {
            rgb[0] = 255;
            rgb[1] = (int)(2.0 * a * 255.0 / (1.0 + a));
         } else if (hue >= 1.0 && hue < 2.0) {
            rgb[0] = (int)(255.0 * (1.0 + a) / (2.0 * a));
            rgb[1] = 255;
         } else if (hue >= 2.0 && hue < 3.0) {
            rgb[1] = 255;
            rgb[2] = (int)(255.0 * (1.0 + a) / (1.0 - a));
         } else if (hue >= 3.0 && hue < 4.0) {
            rgb[1] = (int)(255.0 * (1.0 - a) / (1.0 + a));
            rgb[2] = 255;
         } else if (hue >= 4.0 && hue <= 5.0) {
            rgb[0] = (int)(255.0 * (a - 1.0) / (2.0 * a));
            rgb[2] = 255;
         } else if (hue > 5.0 && hue <= 6.0) {
            rgb[0] = 255;
            rgb[2] = (int)(510.0 * a / (a - 1.0));
         }

         return rgb;
      }

      private int[] rgb2hsl(int r, int g, int b) {
         int[] hsl = new int[]{0, 0, 0};
         if (r != 0 || g != 0 || b != 0) {
            hsl[0] = (int)Math.round((180.0 / Math.PI) * Math.atan2(Math.sqrt(3.0) * (g - b), 2 * r - g - b));

            while (hsl[0] < 0) {
               hsl[0] += 360;
            }
         }

         double mx = Math.max(Math.max(r, g), b) / 255.0;
         double mn = Math.min(Math.min(r, g), b) / 255.0;
         double l = (mx + mn) / 2.0;
         hsl[1] = l != 0.0 && l != 1.0 ? (int)Math.round(100.0 * (mx - mn) / (1.0 - Math.abs(2.0 * l - 1.0))) : 0;
         hsl[2] = (int)Math.round(100.0 * l);
         return hsl;
      }

      String getStyleRGB(String s) {
         if (this.fixedStyle == null) {
            double ry = Math.pow(this.r / 255.0, 2.2);
            double by = Math.pow(this.b / 255.0, 2.2);
            double gy = Math.pow(this.g / 255.0, 2.2);
            double y = 0.2126 * ry + 0.7151 * gy + 0.0721 * by;
            String fg = "black";
            if (1.05 / (y + 0.05) > (y + 0.05) / 0.05) {
               fg = "white";
            }

            return "bg-rgb:" + String.format("#%02x%02x%02x", this.r, this.g, this.b) + ",fg:" + fg;
         } else {
            return (this.fixedBg ? "fg-rgb:" : "bg-rgb:") + String.format("#%02x%02x%02x", this.r, this.g, this.b) + "," + this.fixedStyle;
         }
      }

      public void printColor(String name, String style) throws IOException {
         this.setFixedStyle(style);
         double zoom = 1.0;
         int[] rgb = new int[]{0, 0, 0};
         if (name.matches("[0-9a-fA-F]{6}")) {
            rgb = this.rgb(Long.parseLong(name, 16));
            zoom = 2.0;
         } else if ((name.startsWith("#") || name.startsWith("x")) && name.substring(1).matches("[0-9a-fA-F]{6}")) {
            rgb = this.rgb(Long.parseLong(name.substring(1), 16));
            zoom = 2.0;
         } else if (COLORS_16.contains(name)) {
            for (int i = 0; i < 16; i++) {
               if (COLORS_16.get(i).equals(name)) {
                  rgb = this.rgb(org.jline.utils.Colors.DEFAULT_COLORS_256[i]);
                  break;
               }
            }
         } else if (name.matches("hue[1-3]?[0-9]{1,2}")) {
            int hueAngle = Integer.parseInt(name.substring(3));
            if (hueAngle > 360) {
               throw new IllegalArgumentException("Color not found: " + name);
            }

            rgb = this.hue2rgb(hueAngle);
         } else {
            if (!name.matches("[a-z0-9]+")) {
               throw new IllegalArgumentException("Color not found: " + name);
            }

            List<String> colors = this.retrieveColorNames();
            if (colors.contains(name)) {
               for (int ix = 0; ix < 256; ix++) {
                  if (colors.get(ix).equals(name)) {
                     rgb = this.rgb(org.jline.utils.Colors.DEFAULT_COLORS_256[ix]);
                     break;
                  }
               }
            } else {
               boolean found = false;

               for (int ixx = 0; ixx < 256; ixx++) {
                  if (colors.get(ixx).startsWith(name)) {
                     rgb = this.rgb(org.jline.utils.Colors.DEFAULT_COLORS_256[ixx]);
                     found = true;
                     break;
                  }
               }

               if (!found) {
                  for (int ixxx = 0; ixxx < 256; ixxx++) {
                     if (colors.get(ixxx).contains(name)) {
                        rgb = this.rgb(org.jline.utils.Colors.DEFAULT_COLORS_256[ixxx]);
                        found = true;
                        break;
                     }
                  }
               }

               if (!found) {
                  throw new IllegalArgumentException("Color not found: " + name);
               }
            }
         }

         double step = 32.0;
         int barSize = 14;
         int width = this.terminal.getWidth();
         if (width > 287) {
            step = 8.0;
            barSize = 58;
         } else if (width > 143) {
            step = 16.0;
            barSize = 29;
         } else if (width > 98) {
            step = 24.0;
            barSize = 18;
         }

         this.r = rgb[0];
         this.g = rgb[1];
         this.b = rgb[2];
         int[] hsl = this.rgb2hsl(this.r, this.g, this.b);
         int hueAngle = hsl[0];
         this.out.println("HSL: " + hsl[0] + "deg, " + hsl[1] + "%, " + hsl[2] + "%");
         if (hsl[2] > 85 || hsl[2] < 15 || hsl[1] < 15) {
            zoom = 1.0;
         }

         double div = zoom * 256.0 / step;
         int ndiv = (int)(div / zoom);
         double xrs = (255 - this.r) / div;
         double xgs = (255 - this.g) / div;
         double xbs = (255 - this.b) / div;
         double[] yrs = new double[ndiv];
         double[] ygs = new double[ndiv];
         double[] ybs = new double[ndiv];
         double[] ro = new double[ndiv];
         double[] go = new double[ndiv];
         double[] bo = new double[ndiv];
         AttributedStringBuilder asb = new AttributedStringBuilder();

         for (int y = 0; y < ndiv; y++) {
            for (int x = 0; x < ndiv; x++) {
               if (y == 0) {
                  yrs[x] = (rgb[0] + x * xrs) / div;
                  ygs[x] = (rgb[1] + x * xgs) / div;
                  ybs[x] = (rgb[2] + x * xbs) / div;
                  ro[x] = rgb[0] + x * xrs;
                  go[x] = rgb[1] + x * xgs;
                  bo[x] = rgb[2] + x * xbs;
                  this.r = (int)ro[x];
                  this.g = (int)go[x];
                  this.b = (int)bo[x];
               } else {
                  this.r = (int)(ro[x] - y * yrs[x]);
                  this.g = (int)(go[x] - y * ygs[x]);
                  this.b = (int)(bo[x] - y * ybs[x]);
               }

               String col = String.format("%02x%02x%02x", this.r, this.g, this.b);
               AttributedStyle s = new StyleResolver(this::getStyleRGB).resolve(".rgb" + col);
               asb.style(s);
               asb.append(" ").append("#").append(col).append(" ");
            }

            asb.style(AttributedStyle.DEFAULT).append("\n");
         }

         asb.toAttributedString().println(this.terminal);
         if (hueAngle != -1) {
            int dAngle = 5;
            int zero = (int)(hueAngle - dAngle / 2.0 * (barSize - 1));
            zero -= zero % 5;
            AttributedStringBuilder asb2 = new AttributedStringBuilder();

            for (int ixxxx = 0; ixxxx < barSize; ixxxx++) {
               int angle = zero + dAngle * ixxxx;

               while (angle < 0) {
                  angle += 360;
               }

               while (angle > 360) {
                  angle -= 360;
               }

               rgb = this.hue2rgb(angle);
               this.r = rgb[0];
               this.g = rgb[1];
               this.b = rgb[2];
               AttributedStyle s = new StyleResolver(this::getStyleRGB).resolve(".hue" + angle);
               asb2.style(s);
               asb2.append(" ").append(this.addPadding(3, "" + angle)).append(" ");
            }

            asb2.style(AttributedStyle.DEFAULT).append("\n");
            asb2.toAttributedString().println(this.terminal);
         }
      }
   }

   private static class ReExecute {
      private final boolean execute;
      private final boolean edit;
      private String oldParam;
      private String newParam;
      private FileWriter cmdWriter;
      private File cmdFile;
      private int argId = 0;

      public ReExecute(History history, Options opt) throws IOException {
         this.execute = opt.isSet("e") || opt.isSet("s");
         this.edit = opt.isSet("e");
         if (this.execute) {
            Iterator<History.Entry> iter = history.reverseIterator(history.last());
            if (iter.hasNext()) {
               iter.next();
               iter.remove();
            }

            if (this.edit) {
               this.cmdFile = File.createTempFile("jline-history-", null);
               this.cmdWriter = new FileWriter(this.cmdFile);
            } else if (!opt.args().isEmpty()) {
               String[] s = opt.args().get(this.argId).split("=");
               if (s.length == 2) {
                  this.argId++;
                  this.oldParam = s[0];
                  this.newParam = s[1];
               }
            }
         }
      }

      public int getArgId() {
         return this.argId;
      }

      public boolean isEdit() {
         return this.edit;
      }

      public boolean isExecute() {
         return this.execute;
      }

      public void addCommandInFile(String command) throws IOException {
         this.cmdWriter.write(command + "\n");
      }

      public void addCommandInBuffer(LineReader reader, String command) {
         reader.addCommandsInBuffer(Arrays.asList(this.replaceParam(command)));
      }

      private String replaceParam(String command) {
         String out = command;
         if (this.oldParam != null && this.newParam != null) {
            out = command.replaceAll(this.oldParam, this.newParam);
         }

         return out;
      }

      public void editCommandsAndClose(LineReader reader) throws Exception {
         if (this.edit) {
            this.cmdWriter.close();

            try {
               reader.editAndAddInBuffer(this.cmdFile);
            } finally {
               this.cmdFile.delete();
            }
         }
      }
   }
}
