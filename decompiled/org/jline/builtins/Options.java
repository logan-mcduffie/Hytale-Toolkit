package org.jline.builtins;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jline.utils.AttributedCharSequence;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.StyleResolver;

public class Options {
   public static final String NL = System.getProperty("line.separator", "\n");
   private static final String regex = "(?x)\\s*(?:-([^-]))?(?:,?\\s*-(\\w))?(?:,?\\s*--(\\w[\\w-]*)(=\\w+)?)?(?:,?\\s*--(\\w[\\w-]*))?.*?(?:\\(default=(.*)\\))?\\s*";
   private static final int GROUP_SHORT_OPT_1 = 1;
   private static final int GROUP_SHORT_OPT_2 = 2;
   private static final int GROUP_LONG_OPT_1 = 3;
   private static final int GROUP_ARG_1 = 4;
   private static final int GROUP_LONG_OPT_2 = 5;
   private static final int GROUP_DEFAULT = 6;
   private static final Pattern parser = Pattern.compile(
      "(?x)\\s*(?:-([^-]))?(?:,?\\s*-(\\w))?(?:,?\\s*--(\\w[\\w-]*)(=\\w+)?)?(?:,?\\s*--(\\w[\\w-]*))?.*?(?:\\(default=(.*)\\))?\\s*"
   );
   private static final Pattern uname = Pattern.compile("^Usage:\\s+(\\w+)");
   private final Map<String, Boolean> unmodifiableOptSet;
   private final Map<String, Object> unmodifiableOptArg;
   private final Map<String, Boolean> optSet = new HashMap<>();
   private final Map<String, Object> optArg = new HashMap<>();
   private final Map<String, String> optName = new HashMap<>();
   private final Map<String, String> optAlias = new HashMap<>();
   private final List<Object> xargs = new ArrayList<>();
   private List<String> args = null;
   private static final String UNKNOWN = "unknown";
   private String usageName = "unknown";
   private int usageIndex = 0;
   private final String[] spec;
   private final String[] gspec;
   private final String defOpts;
   private final String[] defArgs;
   private String error = null;
   private boolean optionsFirst = false;
   private boolean stopOnBadOption = false;

   public static Options compile(String[] optSpec) {
      return new Options(optSpec, null, null, System::getenv);
   }

   public static Options compile(String[] optSpec, Function<String, String> env) {
      return new Options(optSpec, null, null, env);
   }

   public static Options compile(String optSpec) {
      return compile(optSpec.split("\\n"), System::getenv);
   }

   public static Options compile(String optSpec, Function<String, String> env) {
      return compile(optSpec.split("\\n"), env);
   }

   public static Options compile(String[] optSpec, Options gopt) {
      return new Options(optSpec, null, gopt, System::getenv);
   }

   public static Options compile(String[] optSpec, String[] gspec) {
      return new Options(optSpec, gspec, null, System::getenv);
   }

   public Options setStopOnBadOption(boolean stopOnBadOption) {
      this.stopOnBadOption = stopOnBadOption;
      return this;
   }

   public Options setOptionsFirst(boolean optionsFirst) {
      this.optionsFirst = optionsFirst;
      return this;
   }

   public boolean isSet(String name) {
      Boolean isSet = this.optSet.get(name);
      if (isSet == null) {
         throw new IllegalArgumentException("option not defined in spec: " + name);
      } else {
         return isSet;
      }
   }

   public Object getObject(String name) {
      if (!this.optArg.containsKey(name)) {
         throw new IllegalArgumentException("option not defined with argument: " + name);
      } else {
         List<Object> list = this.getObjectList(name);
         return list.isEmpty() ? "" : list.get(list.size() - 1);
      }
   }

   public List<Object> getObjectList(String name) {
      Object arg = this.optArg.get(name);
      if (arg == null) {
         throw new IllegalArgumentException("option not defined with argument: " + name);
      } else {
         List<Object> list;
         if (arg instanceof String) {
            list = new ArrayList<>();
            if (!"".equals(arg)) {
               list.add(arg);
            }
         } else {
            list = (List<Object>)arg;
         }

         return list;
      }
   }

   public List<String> getList(String name) {
      ArrayList<String> list = new ArrayList<>();

      for (Object o : this.getObjectList(name)) {
         try {
            list.add((String)o);
         } catch (ClassCastException var6) {
            throw new IllegalArgumentException("option not String: " + name);
         }
      }

      return list;
   }

   private void addArg(String name, Object value) {
      Object arg = this.optArg.get(name);
      List<Object> list;
      if (arg instanceof String) {
         list = new ArrayList<>();
         this.optArg.put(name, list);
      } else {
         list = (List<Object>)arg;
      }

      list.add(value);
   }

   public String get(String name) {
      try {
         return (String)this.getObject(name);
      } catch (ClassCastException var3) {
         throw new IllegalArgumentException("option not String: " + name);
      }
   }

   public int getNumber(String name) {
      String number = this.get(name);

      try {
         return number != null ? Integer.parseInt(number) : 0;
      } catch (NumberFormatException var4) {
         throw new IllegalArgumentException("option '" + name + "' not Number: " + number);
      }
   }

   public List<Object> argObjects() {
      return this.xargs;
   }

   public List<String> args() {
      if (this.args == null) {
         this.args = new ArrayList<>();

         for (Object arg : this.xargs) {
            this.args.add(arg == null ? "null" : arg.toString());
         }
      }

      return this.args;
   }

   public void usage(PrintStream err) {
      err.print(this.usage());
   }

   public String usage() {
      StringBuilder buf = new StringBuilder();
      int index = 0;
      if (this.error != null) {
         buf.append(this.error);
         buf.append(NL);
         index = this.usageIndex;
      }

      for (int i = index; i < this.spec.length; i++) {
         buf.append(this.spec[i]);
         buf.append(NL);
      }

      return buf.toString();
   }

   public IllegalArgumentException usageError(String s) {
      this.error = this.usageName + ": " + s;
      return new IllegalArgumentException(this.error);
   }

   private Options(String[] spec, String[] gspec, Options opt, Function<String, String> env) {
      this.gspec = gspec;
      if (gspec == null && opt == null) {
         this.spec = spec;
      } else {
         ArrayList<String> list = new ArrayList<>();
         list.addAll(Arrays.asList(spec));
         list.addAll(Arrays.asList(gspec != null ? gspec : opt.gspec));
         this.spec = list.toArray(new String[list.size()]);
      }

      Map<String, Boolean> myOptSet = new HashMap<>();
      Map<String, Object> myOptArg = new HashMap<>();
      this.parseSpec(myOptSet, myOptArg);
      if (opt != null) {
         for (Entry<String, Boolean> e : opt.optSet.entrySet()) {
            if (e.getValue()) {
               myOptSet.put(e.getKey(), true);
            }
         }

         for (Entry<String, Object> ex : opt.optArg.entrySet()) {
            if (!ex.getValue().equals("")) {
               myOptArg.put(ex.getKey(), ex.getValue());
            }
         }

         opt.reset();
      }

      this.unmodifiableOptSet = Collections.unmodifiableMap(myOptSet);
      this.unmodifiableOptArg = Collections.unmodifiableMap(myOptArg);
      this.defOpts = env != null ? env.apply(this.usageName.toUpperCase() + "_OPTS") : null;
      this.defArgs = this.defOpts != null ? this.defOpts.split("\\s+") : new String[0];
   }

   private void parseSpec(Map<String, Boolean> myOptSet, Map<String, Object> myOptArg) {
      int index = 0;

      for (String line : this.spec) {
         Matcher m = parser.matcher(line);
         if (m.matches()) {
            String opt = m.group(3);
            String name = opt != null ? opt : m.group(1);
            if (name != null && myOptSet.putIfAbsent(name, false) != null) {
               throw new IllegalArgumentException("duplicate option in spec: --" + name);
            }

            String dflt = m.group(6) != null ? m.group(6) : "";
            if (m.group(4) != null) {
               myOptArg.put(opt, dflt);
            }

            String opt2 = m.group(5);
            if (opt2 != null) {
               this.optAlias.put(opt2, opt);
               myOptSet.put(opt2, false);
               if (m.group(4) != null) {
                  myOptArg.put(opt2, "");
               }
            }

            for (int i = 0; i < 2; i++) {
               String sopt = m.group(i == 0 ? 1 : 2);
               if (sopt != null && this.optName.putIfAbsent(sopt, name) != null) {
                  throw new IllegalArgumentException("duplicate option in spec: -" + sopt);
               }
            }
         }

         if (Objects.equals(this.usageName, "unknown")) {
            Matcher u = uname.matcher(line);
            if (u.find()) {
               this.usageName = u.group(1);
               this.usageIndex = index;
            }
         }

         index++;
      }
   }

   private void reset() {
      this.optSet.clear();
      this.optSet.putAll(this.unmodifiableOptSet);
      this.optArg.clear();
      this.optArg.putAll(this.unmodifiableOptArg);
      this.xargs.clear();
      this.args = null;
      this.error = null;
   }

   public Options parse(Object[] argv) {
      return this.parse(argv, false);
   }

   public Options parse(List<?> argv) {
      return this.parse(argv, false);
   }

   public Options parse(Object[] argv, boolean skipArg0) {
      if (null == argv) {
         throw new IllegalArgumentException("argv is null");
      } else {
         return this.parse(Arrays.asList(argv), skipArg0);
      }
   }

   public Options parse(List<?> argv, boolean skipArg0) {
      this.reset();
      List<Object> args = new ArrayList<>();
      args.addAll(Arrays.asList(this.defArgs));

      for (Object arg : argv) {
         if (skipArg0) {
            skipArg0 = false;
            this.usageName = arg.toString();
         } else {
            args.add(arg);
         }
      }

      String needArg = null;
      String needOpt = null;
      boolean endOpt = false;

      for (Object oarg : args) {
         String argx = oarg == null ? "null" : oarg.toString();
         if (endOpt) {
            this.xargs.add(oarg);
         } else if (needArg != null) {
            this.addArg(needArg, oarg);
            needArg = null;
            needOpt = null;
         } else if (argx.startsWith("-") && (argx.length() <= 1 || !Character.isDigit(argx.charAt(1))) && !"-".equals(oarg)) {
            if (argx.equals("--")) {
               endOpt = true;
            } else if (argx.startsWith("--")) {
               int eq = argx.indexOf("=");
               String value = eq == -1 ? null : argx.substring(eq + 1);
               String name = argx.substring(2, eq == -1 ? argx.length() : eq);
               List<String> names = new ArrayList<>();
               if (this.optSet.containsKey(name)) {
                  names.add(name);
               } else {
                  for (String k : this.optSet.keySet()) {
                     if (k.startsWith(name)) {
                        names.add(k);
                     }
                  }
               }

               switch (names.size()) {
                  case 0:
                     if (!this.stopOnBadOption) {
                        throw this.usageError("invalid option '--" + name + "'");
                     }

                     endOpt = true;
                     this.xargs.add(oarg);
                     break;
                  case 1:
                     name = names.get(0);
                     this.optSet.put(name, true);
                     if (this.optArg.containsKey(name)) {
                        if (value != null) {
                           this.addArg(name, value);
                        } else {
                           needArg = name;
                        }
                     } else if (value != null) {
                        throw this.usageError("option '--" + name + "' doesn't allow an argument");
                     }
                     break;
                  default:
                     throw this.usageError("option '--" + name + "' is ambiguous: " + names);
               }
            } else {
               for (int i = 1; i < argx.length(); i++) {
                  String c = String.valueOf(argx.charAt(i));
                  if (this.optName.containsKey(c)) {
                     String name = this.optName.get(c);
                     this.optSet.put(name, true);
                     if (this.optArg.containsKey(name)) {
                        int kx = i + 1;
                        if (kx < argx.length()) {
                           this.addArg(name, argx.substring(kx));
                        } else {
                           needOpt = c;
                           needArg = name;
                        }
                        break;
                     }
                  } else {
                     if (!this.stopOnBadOption) {
                        throw this.usageError("invalid option '" + c + "'");
                     }

                     this.xargs.add("-" + c);
                     endOpt = true;
                  }
               }
            }
         } else {
            if (this.optionsFirst) {
               endOpt = true;
            }

            this.xargs.add(oarg);
         }
      }

      if (needArg != null) {
         String name = needOpt != null ? needOpt : "--" + needArg;
         throw this.usageError("option '" + name + "' requires an argument");
      } else {
         for (Entry<String, String> alias : this.optAlias.entrySet()) {
            if (this.optSet.get(alias.getKey())) {
               this.optSet.put(alias.getValue(), true);
               if (this.optArg.containsKey(alias.getKey())) {
                  this.optArg.put(alias.getValue(), this.optArg.get(alias.getKey()));
               }
            }

            this.optSet.remove(alias.getKey());
            this.optArg.remove(alias.getKey());
         }

         return this;
      }
   }

   @Override
   public String toString() {
      return "isSet" + this.optSet + "\nArg" + this.optArg + "\nargs" + this.xargs;
   }

   public static class HelpException extends Exception {
      public HelpException(String message) {
         super(message);
      }

      public static StyleResolver defaultStyle() {
         return Styles.helpStyle();
      }

      public static AttributedString highlight(String msg, StyleResolver resolver) {
         Matcher tm = Pattern.compile("(^|\\n)(Usage|Summary)(:)").matcher(msg);
         if (tm.find()) {
            boolean subcommand = tm.group(2).equals("Summary");
            AttributedStringBuilder asb = new AttributedStringBuilder(msg.length());
            AttributedStringBuilder acommand = new AttributedStringBuilder()
               .append(msg.substring(0, tm.start(2)))
               .styleMatches(Pattern.compile("(?:^\\s*)([a-z]+[a-zA-Z0-9-]*)\\b"), Collections.singletonList(resolver.resolve(".co")));
            asb.append((AttributedCharSequence)acommand);
            asb.styled(resolver.resolve(".ti"), tm.group(2)).append(":");

            for (String line : msg.substring(tm.end(3)).split("\n")) {
               int ind = line.lastIndexOf("  ");
               String syntax;
               String comment;
               if (ind > 20) {
                  syntax = line.substring(0, ind);
                  comment = line.substring(ind + 1);
               } else {
                  syntax = line;
                  comment = "";
               }

               asb.append((AttributedCharSequence)_highlightSyntax(syntax, resolver, subcommand));
               asb.append((AttributedCharSequence)_highlightComment(comment, resolver));
               asb.append("\n");
            }

            return asb.toAttributedString();
         } else {
            return AttributedString.fromAnsi(msg);
         }
      }

      public static AttributedString highlightSyntax(String syntax, StyleResolver resolver, boolean subcommands) {
         return _highlightSyntax(syntax, resolver, subcommands).toAttributedString();
      }

      public static AttributedString highlightSyntax(String syntax, StyleResolver resolver) {
         return _highlightSyntax(syntax, resolver, false).toAttributedString();
      }

      public static AttributedString highlightComment(String comment, StyleResolver resolver) {
         return _highlightComment(comment, resolver).toAttributedString();
      }

      private static AttributedStringBuilder _highlightSyntax(String syntax, StyleResolver resolver, boolean subcommand) {
         StringBuilder indent = new StringBuilder();

         for (char c : syntax.toCharArray()) {
            if (c != ' ') {
               break;
            }

            indent.append(c);
         }

         AttributedStringBuilder asyntax = new AttributedStringBuilder().append(syntax.substring(indent.length()));
         asyntax.styleMatches(Pattern.compile("(?:^)([a-z]+[a-zA-Z0-9-]*)\\b"), Collections.singletonList(resolver.resolve(".co")));
         if (!subcommand) {
            asyntax.styleMatches(Pattern.compile("(?:<|\\[|\\s|=)([A-Za-z]+[A-Za-z_-]*)\\b"), Collections.singletonList(resolver.resolve(".ar")));
            asyntax.styleMatches(Pattern.compile("(?:^|\\s|\\[)(-\\$|-\\?|[-]{1,2}[A-Za-z-]+\\b)"), Collections.singletonList(resolver.resolve(".op")));
         }

         return new AttributedStringBuilder().append(indent).append((AttributedCharSequence)asyntax);
      }

      private static AttributedStringBuilder _highlightComment(String comment, StyleResolver resolver) {
         AttributedStringBuilder acomment = new AttributedStringBuilder().append(comment);
         acomment.styleMatches(Pattern.compile("(?:\\s|\\[)(-\\$|-\\?|[-]{1,2}[A-Za-z-]+\\b)"), Collections.singletonList(resolver.resolve(".op")));
         acomment.styleMatches(Pattern.compile("(?:\\s)([a-z]+[-]+[a-z]+|[A-Z_]{2,})(?:\\s)"), Collections.singletonList(resolver.resolve(".ar")));
         return acomment;
      }
   }
}
