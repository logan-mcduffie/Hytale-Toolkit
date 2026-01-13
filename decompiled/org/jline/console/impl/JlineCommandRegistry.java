package org.jline.console.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jline.builtins.Completers;
import org.jline.builtins.Options;
import org.jline.console.ArgDesc;
import org.jline.console.CmdDesc;
import org.jline.console.CommandRegistry;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.utils.AttributedString;
import org.jline.utils.Log;

public abstract class JlineCommandRegistry extends AbstractCommandRegistry {
   @Override
   public List<String> commandInfo(String command) {
      try {
         Object[] args = new Object[]{"--help"};
         if (command.equals("help")) {
            args = new Object[0];
         }

         this.invoke(new CommandRegistry.CommandSession(), command, args);
      } catch (Options.HelpException var3) {
         return compileCommandInfo(var3.getMessage());
      } catch (Exception var4) {
         Log.info("Error while getting command info", var4);
         if (Log.isDebugEnabled()) {
            var4.printStackTrace();
         }

         return new ArrayList<>();
      }

      throw new IllegalArgumentException("JlineCommandRegistry.commandInfo() method must be overridden in class " + this.getClass().getCanonicalName());
   }

   @Override
   public CmdDesc commandDescription(List<String> args) {
      String command = args != null && !args.isEmpty() ? args.get(0) : "";

      try {
         this.invoke(new CommandRegistry.CommandSession(), command, "--help");
      } catch (Options.HelpException var4) {
         return compileCommandDescription(var4.getMessage());
      } catch (Exception var5) {
      }

      throw new IllegalArgumentException("JlineCommandRegistry.commandDescription() method must be overridden in class " + this.getClass().getCanonicalName());
   }

   public List<Completers.OptDesc> commandOptions(String command) {
      try {
         this.invoke(new CommandRegistry.CommandSession(), command, "--help");
      } catch (Options.HelpException var3) {
         return compileCommandOptions(var3.getMessage());
      } catch (Exception var4) {
      }

      return null;
   }

   public List<Completer> defaultCompleter(String command) {
      List<Completer> completers = new ArrayList<>();
      completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new Completers.OptionCompleter(NullCompleter.INSTANCE, this::commandOptions, 1)));
      return completers;
   }

   public Options parseOptions(String[] usage, Object[] args) throws Options.HelpException {
      Options opt = Options.compile(usage).parse(args);
      if (opt.isSet("help")) {
         throw new Options.HelpException(opt.usage());
      } else {
         return opt;
      }
   }

   private static AttributedString highlightComment(String comment) {
      return Options.HelpException.highlightComment(comment, Options.HelpException.defaultStyle());
   }

   private static String[] helpLines(String helpMessage, boolean body) {
      return new JlineCommandRegistry.HelpLines(helpMessage, body).lines();
   }

   public static CmdDesc compileCommandDescription(String helpMessage) {
      List<AttributedString> main = new ArrayList<>();
      Map<String, List<AttributedString>> options = new HashMap<>();
      String prevOpt = null;
      boolean mainDone = false;
      JlineCommandRegistry.HelpLines hl = new JlineCommandRegistry.HelpLines(helpMessage, true);

      for (String s : hl.lines()) {
         if (s.matches("^\\s+-.*$")) {
            mainDone = true;
            int ind = s.lastIndexOf("  ");
            if (ind > 0) {
               String o = s.substring(0, ind);
               String d = s.substring(ind);
               if (!o.trim().isEmpty()) {
                  prevOpt = o.trim();
                  options.put(prevOpt, new ArrayList<>(Collections.singletonList(highlightComment(d.trim()))));
               }
            }
         } else if (s.matches("^[\\s]{20}.*$") && prevOpt != null && options.containsKey(prevOpt)) {
            int ind = s.lastIndexOf("  ");
            if (ind > 0) {
               options.get(prevOpt).add(highlightComment(s.substring(ind).trim()));
            }
         } else {
            prevOpt = null;
         }

         if (!mainDone) {
            main.add(Options.HelpException.highlightSyntax(s.trim(), Options.HelpException.defaultStyle(), hl.subcommands()));
         }
      }

      return new CmdDesc(main, ArgDesc.doArgNames(Collections.singletonList("")), options);
   }

   public static List<Completers.OptDesc> compileCommandOptions(String helpMessage) {
      List<Completers.OptDesc> out = new ArrayList<>();

      for (String s : helpLines(helpMessage, true)) {
         if (s.matches("^\\s+-.*$")) {
            int ind = s.lastIndexOf("  ");
            if (ind > 0) {
               String[] op = s.substring(0, ind).trim().split("\\s+");
               String d = s.substring(ind).trim();
               String so = null;
               String lo = null;
               if (op.length == 1) {
                  if (op[0].startsWith("--")) {
                     lo = op[0];
                  } else {
                     so = op[0];
                  }
               } else {
                  so = op[0];
                  lo = op[1];
               }

               boolean hasValue = false;
               if (lo != null && lo.contains("=")) {
                  hasValue = true;
                  lo = lo.split("=")[0];
               }

               out.add(new Completers.OptDesc(so, lo, d, hasValue ? Completers.AnyCompleter.INSTANCE : null));
            }
         }
      }

      return out;
   }

   public static List<String> compileCommandInfo(String helpMessage) {
      List<String> out = new ArrayList<>();
      boolean first = true;

      for (String s : helpLines(helpMessage, false)) {
         if (first && s.contains(" - ")) {
            out.add(s.substring(s.indexOf(" - ") + 3).trim());
         } else {
            out.add(s.trim());
         }

         first = false;
      }

      return out;
   }

   private static class HelpLines {
      private final String helpMessage;
      private final boolean body;
      private boolean subcommands;

      public HelpLines(String helpMessage, boolean body) {
         this.helpMessage = helpMessage;
         this.body = body;
      }

      public String[] lines() {
         String out = "";
         Matcher tm = Pattern.compile("(^|\\n)(Usage|Summary)(:)").matcher(this.helpMessage);
         if (tm.find()) {
            this.subcommands = tm.group(2).matches("Summary");
            if (this.body) {
               out = this.helpMessage.substring(tm.end(3));
            } else {
               out = this.helpMessage.substring(0, tm.start(1));
            }
         } else if (!this.body) {
            out = this.helpMessage;
         }

         return out.split("\\r?\\n");
      }

      public boolean subcommands() {
         return this.subcommands;
      }
   }
}
