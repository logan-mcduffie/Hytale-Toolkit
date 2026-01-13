package org.jline.builtins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import org.jline.utils.AttributedCharSequence;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Log;
import org.jline.utils.StyleResolver;

public class SyntaxHighlighter {
   public static final String REGEX_TOKEN_NAME = "[A-Z_]+";
   public static final String TYPE_NANORCTHEME = ".nanorctheme";
   public static final String DEFAULT_NANORC_FILE = "jnanorc";
   protected static final String DEFAULT_LESSRC_FILE = "jlessrc";
   protected static final String COMMAND_INCLUDE = "include";
   protected static final String COMMAND_THEME = "theme";
   private static final String TOKEN_NANORC = "NANORC";
   private final Path nanorc;
   private final String syntaxName;
   private final String nanorcUrl;
   private final Map<String, List<SyntaxHighlighter.HighlightRule>> rules = new HashMap<>();
   private Path currentTheme;
   private boolean startEndHighlight;
   private int ruleStartId = 0;
   private SyntaxHighlighter.Parser parser;

   private SyntaxHighlighter() {
      this(null, null, null);
   }

   private SyntaxHighlighter(String nanorcUrl) {
      this(null, null, nanorcUrl);
   }

   private SyntaxHighlighter(Path nanorc, String syntaxName) {
      this(nanorc, syntaxName, null);
   }

   private SyntaxHighlighter(Path nanorc, String syntaxName, String nanorcUrl) {
      this.nanorc = nanorc;
      this.syntaxName = syntaxName;
      this.nanorcUrl = nanorcUrl;
      Map<String, List<SyntaxHighlighter.HighlightRule>> defaultRules = new HashMap<>();
      defaultRules.put("NANORC", new ArrayList<>());
      this.rules.putAll(defaultRules);
   }

   protected static SyntaxHighlighter build(List<Path> syntaxFiles, String file, String syntaxName) {
      return build(syntaxFiles, file, syntaxName, false);
   }

   protected static SyntaxHighlighter build(List<Path> syntaxFiles, String file, String syntaxName, boolean ignoreErrors) {
      SyntaxHighlighter out = new SyntaxHighlighter();
      Map<String, String> colorTheme = new HashMap<>();

      try {
         if (syntaxName == null || !syntaxName.equals("none")) {
            for (Path p : syntaxFiles) {
               try {
                  if (colorTheme.isEmpty() && p.getFileName().toString().endsWith(".nanorctheme")) {
                     out.setCurrentTheme(p);
                     BufferedReader reader = Files.newBufferedReader(p);

                     String line;
                     try {
                        while ((line = reader.readLine()) != null) {
                           line = line.trim();
                           if (!line.isEmpty() && !line.startsWith("#")) {
                              List<String> parts = Arrays.asList(line.split("\\s+", 2));
                              colorTheme.put(parts.get(0), parts.get(1));
                           }
                        }
                     } catch (Throwable var12) {
                        if (reader != null) {
                           try {
                              reader.close();
                           } catch (Throwable var11) {
                              var12.addSuppressed(var11);
                           }
                        }

                        throw var12;
                     }

                     if (reader != null) {
                        reader.close();
                     }
                  } else {
                     SyntaxHighlighter.NanorcParser nanorcParser = new SyntaxHighlighter.NanorcParser(p, syntaxName, file, colorTheme);
                     nanorcParser.parse();
                     if (nanorcParser.matches()) {
                        out.addRules(nanorcParser.getHighlightRules());
                        out.setParser(nanorcParser.getParser());
                        return out;
                     }

                     if (nanorcParser.isDefault()) {
                        out.addRules(nanorcParser.getHighlightRules());
                     }
                  }
               } catch (IOException var13) {
               }
            }
         }
      } catch (PatternSyntaxException var14) {
         if (!ignoreErrors) {
            throw var14;
         }
      }

      return out;
   }

   public static SyntaxHighlighter build(Path nanorc, String syntaxName) {
      SyntaxHighlighter out = new SyntaxHighlighter(nanorc, syntaxName);
      List<Path> syntaxFiles = new ArrayList<>();

      try {
         BufferedReader reader = Files.newBufferedReader(nanorc);

         String line;
         try {
            while ((line = reader.readLine()) != null) {
               line = line.trim();
               if (!line.isEmpty() && !line.startsWith("#")) {
                  List<String> parts = SyntaxHighlighter.RuleSplitter.split(line);
                  if (parts.get(0).equals("include")) {
                     nanorcInclude(nanorc, parts.get(1), syntaxFiles);
                  } else if (parts.get(0).equals("theme")) {
                     nanorcTheme(nanorc, parts.get(1), syntaxFiles);
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

         SyntaxHighlighter sh = build(syntaxFiles, null, syntaxName);
         out.addRules(sh.rules);
         out.setParser(sh.parser);
         out.setCurrentTheme(sh.currentTheme);
      } catch (Exception var9) {
      }

      return out;
   }

   protected static void nanorcInclude(Path nanorc, String parameter, List<Path> syntaxFiles) throws IOException {
      addFiles(nanorc, parameter, s -> s.forEach(syntaxFiles::add));
   }

   protected static void nanorcTheme(Path nanorc, String parameter, List<Path> syntaxFiles) throws IOException {
      addFiles(nanorc, parameter, s -> s.findFirst().ifPresent(p -> syntaxFiles.add(0, p)));
   }

   protected static void addFiles(Path nanorc, String parameter, Consumer<Stream<Path>> consumer) throws IOException {
      SyntaxHighlighter.PathParts parts = extractPathParts(parameter);
      Path searchRoot = nanorc.resolveSibling(parts.staticPrefix);
      if (Files.exists(searchRoot)) {
         if (parts.globPattern.isEmpty()) {
            consumer.accept(Stream.of(searchRoot));
         } else {
            PathMatcher pathMatcher = searchRoot.getFileSystem().getPathMatcher("glob:" + parts.globPattern);
            Stream<Path> pathStream = Files.walk(searchRoot);

            try {
               consumer.accept(pathStream.filter(p -> pathMatcher.matches(searchRoot.relativize(p))));
            } catch (Throwable var10) {
               if (pathStream != null) {
                  try {
                     pathStream.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (pathStream != null) {
               pathStream.close();
            }
         }
      }
   }

   private static SyntaxHighlighter.PathParts extractPathParts(String pattern) {
      int firstWildcard = Math.min(
         pattern.indexOf(42) == -1 ? Integer.MAX_VALUE : pattern.indexOf(42), pattern.indexOf(63) == -1 ? Integer.MAX_VALUE : pattern.indexOf(63)
      );
      if (firstWildcard == Integer.MAX_VALUE) {
         return new SyntaxHighlighter.PathParts(pattern, "");
      } else {
         int lastSlashBeforeWildcard = -1;

         for (int i = firstWildcard - 1; i >= 0; i--) {
            char c = pattern.charAt(i);
            if (c == '/' || c == '\\') {
               lastSlashBeforeWildcard = i;
               break;
            }
         }

         if (lastSlashBeforeWildcard == -1) {
            return new SyntaxHighlighter.PathParts("", pattern);
         } else {
            String staticPrefix = pattern.substring(0, lastSlashBeforeWildcard);
            String globPattern = pattern.substring(lastSlashBeforeWildcard + 1);
            return new SyntaxHighlighter.PathParts(staticPrefix, globPattern);
         }
      }
   }

   public static SyntaxHighlighter build(String nanorcUrl) {
      SyntaxHighlighter out = new SyntaxHighlighter(nanorcUrl);

      try {
         InputStream inputStream;
         if (nanorcUrl.startsWith("classpath:")) {
            String resourcePath = nanorcUrl.substring(10);

            try {
               Path resourceAsPath = ClasspathResourceUtil.getResourcePath(resourcePath);
               inputStream = Files.newInputStream(resourceAsPath);
            } catch (Exception var5) {
               inputStream = new Source.ResourceSource(resourcePath, null).read();
            }
         } else {
            inputStream = new Source.URLSource(new URI(nanorcUrl).toURL(), null).read();
         }

         SyntaxHighlighter.NanorcParser parser = new SyntaxHighlighter.NanorcParser(inputStream, null, null);
         parser.parse();
         out.addRules(parser.getHighlightRules());
      } catch (URISyntaxException | IOException var6) {
      }

      return out;
   }

   private void addRules(Map<String, List<SyntaxHighlighter.HighlightRule>> rules) {
      this.rules.putAll(rules);
   }

   public void setCurrentTheme(Path currentTheme) {
      this.currentTheme = currentTheme;
   }

   public Path getCurrentTheme() {
      return this.currentTheme;
   }

   public void setParser(SyntaxHighlighter.Parser parser) {
      this.parser = parser;
   }

   public SyntaxHighlighter reset() {
      this.ruleStartId = 0;
      this.startEndHighlight = false;
      if (this.parser != null) {
         this.parser.reset();
      }

      return this;
   }

   public void refresh() {
      SyntaxHighlighter sh;
      if (this.nanorc != null && this.syntaxName != null) {
         sh = build(this.nanorc, this.syntaxName);
      } else {
         if (this.nanorcUrl == null) {
            throw new IllegalStateException("Not possible to refresh highlighter!");
         }

         sh = build(this.nanorcUrl);
      }

      this.rules.clear();
      this.addRules(sh.rules);
      this.parser = sh.parser;
      this.currentTheme = sh.currentTheme;
   }

   public AttributedString highlight(String string) {
      return this.splitAndHighlight(new AttributedString(string));
   }

   public AttributedString highlight(AttributedStringBuilder asb) {
      return this.splitAndHighlight(asb.toAttributedString());
   }

   public AttributedString highlight(AttributedString attributedString) {
      return this.splitAndHighlight(attributedString);
   }

   private AttributedString splitAndHighlight(AttributedString attributedString) {
      AttributedStringBuilder asb = new AttributedStringBuilder();
      boolean first = true;

      for (AttributedString line : attributedString.columnSplitLength(Integer.MAX_VALUE)) {
         if (!first) {
            asb.append("\n");
         }

         List<SyntaxHighlighter.ParsedToken> tokens = new ArrayList<>();
         if (this.parser != null) {
            this.parser.parse(line);
            tokens = this.parser.getTokens();
         }

         if (tokens.isEmpty()) {
            asb.append((AttributedCharSequence)this._highlight(line, this.rules.get("NANORC")));
         } else {
            int pos = 0;

            for (SyntaxHighlighter.ParsedToken t : tokens) {
               if (t.getStart() > pos) {
                  AttributedStringBuilder head = this._highlight(line.columnSubSequence(pos, t.getStart() + 1), this.rules.get("NANORC"));
                  asb.append(head.columnSubSequence(0, head.length() - 1));
               }

               asb.append(
                  (AttributedCharSequence)this._highlight(
                     line.columnSubSequence(t.getStart(), t.getEnd()),
                     this.rules.get(t.getName()),
                     t.getStartWith(),
                     line.columnSubSequence(t.getEnd(), line.length())
                  )
               );
               pos = t.getEnd();
            }

            if (pos < line.length()) {
               asb.append((AttributedCharSequence)this._highlight(line.columnSubSequence(pos, line.length()), this.rules.get("NANORC")));
            }
         }

         first = false;
      }

      return asb.toAttributedString();
   }

   private AttributedStringBuilder _highlight(AttributedString line, List<SyntaxHighlighter.HighlightRule> rules) {
      return this._highlight(line, rules, null, null);
   }

   private AttributedStringBuilder _highlight(
      AttributedString line, List<SyntaxHighlighter.HighlightRule> rules, CharSequence startWith, CharSequence continueAs
   ) {
      AttributedStringBuilder asb = new AttributedStringBuilder();
      asb.append(line);
      if (rules.isEmpty()) {
         return asb;
      } else {
         int startId = this.ruleStartId;
         boolean endHighlight = this.startEndHighlight;

         for (int i = startId; i < (endHighlight ? startId + 1 : rules.size()); i++) {
            SyntaxHighlighter.HighlightRule rule = rules.get(i);
            switch (rule.getType()) {
               case PATTERN:
                  asb.styleMatches(rule.getPattern(), rule.getStyle());
                  break;
               case START_END:
                  boolean done = false;
                  Matcher start = rule.getStart().matcher(asb.toAttributedString());
                  Matcher end = rule.getEnd().matcher(asb.toAttributedString());

                  while (!done) {
                     AttributedStringBuilder a = new AttributedStringBuilder();
                     if (this.startEndHighlight && this.ruleStartId == i) {
                        if (end.find()) {
                           this.ruleStartId = 0;
                           this.startEndHighlight = false;
                           a.append(asb.columnSubSequence(0, end.end()), rule.getStyle());
                           a.append((AttributedCharSequence)this._highlight(asb.columnSubSequence(end.end(), asb.length()).toAttributedString(), rules));
                        } else {
                           a.append(asb, rule.getStyle());
                           done = true;
                        }

                        asb = a;
                     } else if (start.find()) {
                        a.append(asb.columnSubSequence(0, start.start()));
                        if (end.find()) {
                           a.append(asb.columnSubSequence(start.start(), end.end()), rule.getStyle());
                           a.append(asb.columnSubSequence(end.end(), asb.length()));
                        } else {
                           this.ruleStartId = i;
                           this.startEndHighlight = true;
                           a.append(asb.columnSubSequence(start.start(), asb.length()), rule.getStyle());
                           done = true;
                        }

                        asb = a;
                     } else {
                        done = true;
                     }
                  }
                  break;
               case PARSER_START_WITH:
                  if (startWith != null && startWith.toString().startsWith(rule.getStartWith())) {
                     asb.styleMatches(rule.getPattern(), rule.getStyle());
                  }
                  break;
               case PARSER_CONTINUE_AS:
                  if (continueAs != null && continueAs.toString().matches(rule.getContinueAs() + ".*")) {
                     asb.styleMatches(rule.getPattern(), rule.getStyle());
                  }
            }
         }

         return asb;
      }
   }

   private static class BlockCommentDelimiters {
      private final String start;
      private final String end;

      public BlockCommentDelimiters(String[] args) {
         if (args.length == 2 && args[0] != null && args[1] != null && !args[0].isEmpty() && !args[1].isEmpty() && !args[0].equals(args[1])) {
            this.start = args[0];
            this.end = args[1];
         } else {
            throw new IllegalArgumentException("Bad block comment delimiters!");
         }
      }

      public String getStart() {
         return this.start;
      }

      public String getEnd() {
         return this.end;
      }
   }

   static class HighlightRule {
      private final SyntaxHighlighter.HighlightRule.RuleType type;
      private Pattern pattern;
      private final AttributedStyle style;
      private Pattern start;
      private Pattern end;
      private String startWith;
      private String continueAs;

      public HighlightRule(AttributedStyle style, Pattern pattern) {
         this.type = SyntaxHighlighter.HighlightRule.RuleType.PATTERN;
         this.pattern = pattern;
         this.style = style;
      }

      public HighlightRule(AttributedStyle style, Pattern start, Pattern end) {
         this.type = SyntaxHighlighter.HighlightRule.RuleType.START_END;
         this.style = style;
         this.start = start;
         this.end = end;
      }

      public HighlightRule(SyntaxHighlighter.HighlightRule.RuleType parserRuleType, AttributedStyle style, String value) {
         this.type = parserRuleType;
         this.style = style;
         this.pattern = Pattern.compile(".*");
         if (parserRuleType == SyntaxHighlighter.HighlightRule.RuleType.PARSER_START_WITH) {
            this.startWith = value;
         } else {
            if (parserRuleType != SyntaxHighlighter.HighlightRule.RuleType.PARSER_CONTINUE_AS) {
               throw new IllegalArgumentException("Bad RuleType: " + parserRuleType);
            }

            this.continueAs = value;
         }
      }

      public SyntaxHighlighter.HighlightRule.RuleType getType() {
         return this.type;
      }

      public AttributedStyle getStyle() {
         return this.style;
      }

      public Pattern getPattern() {
         if (this.type == SyntaxHighlighter.HighlightRule.RuleType.START_END) {
            throw new IllegalAccessError();
         } else {
            return this.pattern;
         }
      }

      public Pattern getStart() {
         if (this.type == SyntaxHighlighter.HighlightRule.RuleType.PATTERN) {
            throw new IllegalAccessError();
         } else {
            return this.start;
         }
      }

      public Pattern getEnd() {
         if (this.type == SyntaxHighlighter.HighlightRule.RuleType.PATTERN) {
            throw new IllegalAccessError();
         } else {
            return this.end;
         }
      }

      public String getStartWith() {
         return this.startWith;
      }

      public String getContinueAs() {
         return this.continueAs;
      }

      public static SyntaxHighlighter.HighlightRule.RuleType evalRuleType(List<String> colorCfg) {
         SyntaxHighlighter.HighlightRule.RuleType out = null;
         if (colorCfg.get(0).equals("color") || colorCfg.get(0).equals("icolor")) {
            out = SyntaxHighlighter.HighlightRule.RuleType.PATTERN;
            if (colorCfg.size() == 3) {
               if (colorCfg.get(2).startsWith("startWith=")) {
                  out = SyntaxHighlighter.HighlightRule.RuleType.PARSER_START_WITH;
               } else if (colorCfg.get(2).startsWith("continueAs=")) {
                  out = SyntaxHighlighter.HighlightRule.RuleType.PARSER_CONTINUE_AS;
               }
            } else if (colorCfg.size() == 4 && colorCfg.get(2).startsWith("start=") && colorCfg.get(3).startsWith("end=")) {
               out = SyntaxHighlighter.HighlightRule.RuleType.START_END;
            }
         }

         return out;
      }

      @Override
      public String toString() {
         return "{type:"
            + this.type
            + ", startWith: "
            + this.startWith
            + ", continueAs: "
            + this.continueAs
            + ", start: "
            + this.start
            + ", end: "
            + this.end
            + ", pattern: "
            + this.pattern
            + "}";
      }

      public static enum RuleType {
         PATTERN,
         START_END,
         PARSER_START_WITH,
         PARSER_CONTINUE_AS;
      }
   }

   static class NanorcParser {
      private static final String DEFAULT_SYNTAX = "default";
      private final String name;
      private final String target;
      private final Map<String, List<SyntaxHighlighter.HighlightRule>> highlightRules = new HashMap<>();
      private final BufferedReader reader;
      private Map<String, String> colorTheme = new HashMap<>();
      private boolean matches = false;
      private String syntaxName = "unknown";
      private SyntaxHighlighter.Parser parser;

      public NanorcParser(Path file, String name, String target, Map<String, String> colorTheme) throws IOException {
         this(new Source.PathSource(file, null).read(), name, target);
         this.colorTheme = colorTheme;
      }

      public NanorcParser(InputStream in, String name, String target) {
         this.reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
         this.name = name;
         this.target = target;
         this.highlightRules.put("NANORC", new ArrayList<>());
      }

      public void parse() throws IOException {
         int idx = 0;

         String line;
         try {
            while ((line = this.reader.readLine()) != null) {
               idx++;
               line = line.trim();
               if (!line.isEmpty() && !line.startsWith("#")) {
                  List<String> parts = SyntaxHighlighter.RuleSplitter.split(line);
                  if (!parts.get(0).equals("syntax")) {
                     if (parts.get(0).startsWith("$")) {
                        String key = this.themeKey(parts.get(0));
                        if (this.colorTheme.containsKey(key)) {
                           if (this.parser == null) {
                              this.parser = new SyntaxHighlighter.Parser();
                           }

                           String[] args = parts.get(1).split(",\\s*");
                           boolean validKey = true;
                           if (key.startsWith("$BLOCK_COMMENT")) {
                              this.parser.setBlockCommentDelimiters(key, args);
                           } else if (key.startsWith("$LINE_COMMENT")) {
                              this.parser.setLineCommentDelimiters(key, args);
                           } else if (key.startsWith("$BALANCED_DELIMITERS")) {
                              this.parser.setBalancedDelimiters(key, args);
                           } else {
                              Log.warn("Unknown token type: ", key);
                              validKey = false;
                           }

                           if (validKey) {
                              if (!this.highlightRules.containsKey(key)) {
                                 this.highlightRules.put(key, new ArrayList<>());
                              }

                              for (String l : this.colorTheme.get(key).split("\\\\n")) {
                                 this.addHighlightRule(SyntaxHighlighter.RuleSplitter.split(l), ++idx, key);
                              }
                           }
                        } else {
                           Log.warn("Unknown token type: ", key);
                        }
                     } else if (!this.addHighlightRule(parts, idx, "NANORC") && parts.get(0).matches("\\+[A-Z_]+")) {
                        String key = this.themeKey(parts.get(0));
                        String theme = this.colorTheme.get(key);
                        if (theme != null) {
                           for (String l : theme.split("\\\\n")) {
                              this.addHighlightRule(SyntaxHighlighter.RuleSplitter.split(l), ++idx, "NANORC");
                           }
                        } else {
                           Log.warn("Unknown token type: ", key);
                        }
                     }
                  } else {
                     this.syntaxName = parts.get(1);
                     List<Pattern> filePatterns = new ArrayList<>();
                     if (this.name != null) {
                        if (!this.name.equals(this.syntaxName)) {
                           break;
                        }

                        this.matches = true;
                     } else if (this.target == null) {
                        this.matches = true;
                     } else {
                        for (int i = 2; i < parts.size(); i++) {
                           filePatterns.add(Pattern.compile(parts.get(i)));
                        }

                        for (Pattern p : filePatterns) {
                           if (p.matcher(this.target).find()) {
                              this.matches = true;
                              break;
                           }
                        }

                        if (!this.matches && !this.syntaxName.equals("default")) {
                           break;
                        }
                     }
                  }
               }
            }
         } finally {
            this.reader.close();
         }
      }

      private boolean addHighlightRule(List<String> parts, int idx, String tokenName) {
         boolean out = true;
         if (parts.get(0).equals("color")) {
            this.addHighlightRule(this.syntaxName + idx, parts, false, tokenName);
         } else if (parts.get(0).equals("icolor")) {
            this.addHighlightRule(this.syntaxName + idx, parts, true, tokenName);
         } else if (parts.get(0).matches("[A-Z_]+[:]?")) {
            String key = this.themeKey(parts.get(0));
            String theme = this.colorTheme.get(key);
            if (theme != null) {
               parts.set(0, "color");
               parts.add(1, theme);
               this.addHighlightRule(this.syntaxName + idx, parts, false, tokenName);
            } else {
               Log.warn("Unknown token type: ", key);
            }
         } else if (parts.get(0).matches("~[A-Z_]+[:]?")) {
            String key = this.themeKey(parts.get(0));
            String theme = this.colorTheme.get(key);
            if (theme != null) {
               parts.set(0, "icolor");
               parts.add(1, theme);
               this.addHighlightRule(this.syntaxName + idx, parts, true, tokenName);
            } else {
               Log.warn("Unknown token type: ", key);
            }
         } else {
            out = false;
         }

         return out;
      }

      private String themeKey(String key) {
         if (key.startsWith("+")) {
            return key;
         } else {
            int keyEnd = key.endsWith(":") ? key.length() - 1 : key.length();
            return key.startsWith("~") ? key.substring(1, keyEnd) : key.substring(0, keyEnd);
         }
      }

      public boolean matches() {
         return this.matches;
      }

      public SyntaxHighlighter.Parser getParser() {
         return this.parser;
      }

      public Map<String, List<SyntaxHighlighter.HighlightRule>> getHighlightRules() {
         return this.highlightRules;
      }

      public boolean isDefault() {
         return this.syntaxName.equals("default");
      }

      private void addHighlightRule(String reference, List<String> parts, boolean caseInsensitive, String tokenName) {
         Map<String, String> spec = new HashMap<>();
         spec.put(reference, parts.get(1));
         Styles.StyleCompiler sh = new Styles.StyleCompiler(spec, true);
         AttributedStyle style = new StyleResolver(sh::getStyle).resolve("." + reference);

         try {
            if (SyntaxHighlighter.HighlightRule.evalRuleType(parts) == SyntaxHighlighter.HighlightRule.RuleType.PATTERN) {
               if (parts.size() == 2) {
                  this.highlightRules.get(tokenName).add(new SyntaxHighlighter.HighlightRule(style, this.doPattern(".*", caseInsensitive)));
               } else {
                  for (int i = 2; i < parts.size(); i++) {
                     this.highlightRules.get(tokenName).add(new SyntaxHighlighter.HighlightRule(style, this.doPattern(parts.get(i), caseInsensitive)));
                  }
               }
            } else if (SyntaxHighlighter.HighlightRule.evalRuleType(parts) == SyntaxHighlighter.HighlightRule.RuleType.START_END) {
               String s = parts.get(2);
               String e = parts.get(3);
               this.highlightRules
                  .get(tokenName)
                  .add(
                     new SyntaxHighlighter.HighlightRule(
                        style, this.doPattern(s.substring(7, s.length() - 1), caseInsensitive), this.doPattern(e.substring(5, e.length() - 1), caseInsensitive)
                     )
                  );
            } else if (SyntaxHighlighter.HighlightRule.evalRuleType(parts) == SyntaxHighlighter.HighlightRule.RuleType.PARSER_START_WITH) {
               this.highlightRules
                  .get(tokenName)
                  .add(new SyntaxHighlighter.HighlightRule(SyntaxHighlighter.HighlightRule.RuleType.PARSER_START_WITH, style, parts.get(2).substring(10)));
            } else if (SyntaxHighlighter.HighlightRule.evalRuleType(parts) == SyntaxHighlighter.HighlightRule.RuleType.PARSER_CONTINUE_AS) {
               this.highlightRules
                  .get(tokenName)
                  .add(new SyntaxHighlighter.HighlightRule(SyntaxHighlighter.HighlightRule.RuleType.PARSER_CONTINUE_AS, style, parts.get(2).substring(11)));
            }
         } catch (PatternSyntaxException var10) {
            Log.warn("Invalid highlight regex", reference, parts, var10);
         } catch (Exception var11) {
            Log.warn("Failure while handling highlight regex", reference, parts, var11);
         }
      }

      private Pattern doPattern(String regex, boolean caseInsensitive) {
         regex = SyntaxHighlighter.Parser.fixRegexes(regex);
         return caseInsensitive ? Pattern.compile(regex, 2) : Pattern.compile(regex);
      }
   }

   private static class ParsedToken {
      private final String name;
      private final CharSequence startWith;
      private final int start;
      private final int end;

      public ParsedToken(String name, CharSequence startWith, int start, int end) {
         this.name = name;
         this.startWith = startWith;
         this.start = start;
         this.end = end;
      }

      public String getName() {
         return this.name;
      }

      public CharSequence getStartWith() {
         return this.startWith;
      }

      public int getStart() {
         return this.start;
      }

      public int getEnd() {
         return this.end;
      }
   }

   static class Parser {
      private static final char escapeChar = '\\';
      private String blockCommentTokenName;
      private SyntaxHighlighter.BlockCommentDelimiters blockCommentDelimiters;
      private String lineCommentTokenName;
      private String[] lineCommentDelimiters;
      private String balancedDelimiterTokenName;
      private String[] balancedDelimiters;
      private String balancedDelimiter;
      private List<SyntaxHighlighter.ParsedToken> tokens;
      private CharSequence startWith;
      private int tokenStart = 0;
      private boolean blockComment;
      private boolean lineComment;
      private boolean balancedQuoted;

      public Parser() {
      }

      public void setBlockCommentDelimiters(String tokenName, String[] args) {
         try {
            this.blockCommentTokenName = tokenName;
            this.blockCommentDelimiters = new SyntaxHighlighter.BlockCommentDelimiters(args);
         } catch (Exception var4) {
            Log.warn(var4.getMessage());
         }
      }

      public void setLineCommentDelimiters(String tokenName, String[] args) {
         this.lineCommentTokenName = tokenName;
         this.lineCommentDelimiters = args;
      }

      public void setBalancedDelimiters(String tokenName, String[] args) {
         this.balancedDelimiterTokenName = tokenName;
         this.balancedDelimiters = args;
      }

      public void reset() {
         this.startWith = null;
         this.blockComment = false;
         this.lineComment = false;
         this.balancedQuoted = false;
         this.tokenStart = 0;
      }

      public void parse(CharSequence line) {
         if (line != null) {
            this.tokens = new ArrayList<>();
            if (this.blockComment || this.balancedQuoted) {
               this.tokenStart = 0;
            }

            for (int i = 0; i < line.length(); i++) {
               if (!this.isEscapeChar(line, i) && !this.isEscaped(line, i)) {
                  if (!this.blockComment && !this.lineComment && !this.balancedQuoted) {
                     if (this.blockCommentDelimiters != null && this.isDelimiter(line, i, this.blockCommentDelimiters.getStart())) {
                        this.blockComment = true;
                        this.tokenStart = i;
                        this.startWith = this.startWithSubstring(line, i);
                        i = i + this.blockCommentDelimiters.getStart().length() - 1;
                     } else {
                        if (this.isLineCommentDelimiter(line, i)) {
                           this.lineComment = true;
                           this.tokenStart = i;
                           this.startWith = this.startWithSubstring(line, i);
                           break;
                        }

                        if ((this.balancedDelimiter = this.balancedDelimiter(line, i)) != null) {
                           this.balancedQuoted = true;
                           this.tokenStart = i;
                           this.startWith = this.startWithSubstring(line, i);
                           i = i + this.balancedDelimiter.length() - 1;
                        }
                     }
                  } else if (this.blockComment) {
                     if (this.isDelimiter(line, i, this.blockCommentDelimiters.getEnd())) {
                        this.blockComment = false;
                        i = i + this.blockCommentDelimiters.getEnd().length() - 1;
                        this.tokens.add(new SyntaxHighlighter.ParsedToken(this.blockCommentTokenName, this.startWith, this.tokenStart, i + 1));
                     }
                  } else if (this.balancedQuoted && this.isDelimiter(line, i, this.balancedDelimiter)) {
                     this.balancedQuoted = false;
                     i = i + this.balancedDelimiter.length() - 1;
                     if (i - this.tokenStart + 1 > 2 * this.balancedDelimiter.length()) {
                        this.tokens.add(new SyntaxHighlighter.ParsedToken(this.balancedDelimiterTokenName, this.startWith, this.tokenStart, i + 1));
                     }
                  }
               }
            }

            if (this.blockComment) {
               this.tokens.add(new SyntaxHighlighter.ParsedToken(this.blockCommentTokenName, this.startWith, this.tokenStart, line.length()));
            } else if (this.lineComment) {
               this.lineComment = false;
               this.tokens.add(new SyntaxHighlighter.ParsedToken(this.lineCommentTokenName, this.startWith, this.tokenStart, line.length()));
            } else if (this.balancedQuoted) {
               this.tokens.add(new SyntaxHighlighter.ParsedToken(this.balancedDelimiterTokenName, this.startWith, this.tokenStart, line.length()));
            }
         }
      }

      private CharSequence startWithSubstring(CharSequence line, int pos) {
         return line.subSequence(pos, Math.min(pos + 5, line.length()));
      }

      public List<SyntaxHighlighter.ParsedToken> getTokens() {
         return this.tokens;
      }

      private String balancedDelimiter(CharSequence buffer, int pos) {
         if (this.balancedDelimiters != null) {
            for (String delimiter : this.balancedDelimiters) {
               if (this.isDelimiter(buffer, pos, delimiter)) {
                  return delimiter;
               }
            }
         }

         return null;
      }

      private boolean isDelimiter(CharSequence buffer, int pos, String delimiter) {
         if (pos >= 0 && delimiter != null) {
            int length = delimiter.length();
            if (length <= buffer.length() - pos) {
               for (int i = 0; i < length; i++) {
                  if (delimiter.charAt(i) != buffer.charAt(pos + i)) {
                     return false;
                  }
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      private boolean isLineCommentDelimiter(CharSequence buffer, int pos) {
         if (this.lineCommentDelimiters != null) {
            for (String delimiter : this.lineCommentDelimiters) {
               if (this.isDelimiter(buffer, pos, delimiter)) {
                  return true;
               }
            }
         }

         return false;
      }

      private boolean isEscapeChar(char ch) {
         return '\\' == ch;
      }

      private boolean isEscapeChar(CharSequence buffer, int pos) {
         if (pos < 0) {
            return false;
         } else {
            char ch = buffer.charAt(pos);
            return this.isEscapeChar(ch) && !this.isEscaped(buffer, pos);
         }
      }

      private boolean isEscaped(CharSequence buffer, int pos) {
         return pos <= 0 ? false : this.isEscapeChar(buffer, pos - 1);
      }

      static String fixRegexes(String posix) {
         int len = posix.length();
         StringBuilder java = new StringBuilder();
         boolean inBracketExpression = false;
         int i = 0;

         try {
            for (; i < len; i++) {
               char c = posix.charAt(i);
               switch (c) {
                  case '[':
                     if (i == len - 1) {
                        throw new IllegalArgumentException("Lone [ at the end of (index " + i + "): " + posix);
                     }

                     if (posix.charAt(i + 1) == ':') {
                        int afterClass = nextAfterClass(posix, i + 2);
                        if (!posix.regionMatches(afterClass, ":]", 0, 2)) {
                           java.append("[:");
                           i++;
                           inBracketExpression = true;
                        } else {
                           String className = posix.substring(i + 2, afterClass);
                           java.append(replaceClass(className));
                           i = afterClass + 1;
                        }
                     } else if (inBracketExpression) {
                        java.append('\\').append(c);
                     } else {
                        inBracketExpression = true;
                        java.append(c);
                        char next = posix.charAt(i + 1);
                        if (next == ']') {
                           i++;
                           java.append("\\]");
                        } else if (next == '^' && posix.charAt(i + 2) == ']') {
                           i += 2;
                           java.append("^\\]");
                        }
                     }
                     break;
                  case '\\':
                     char next = posix.charAt(++i);
                     if (inBracketExpression && next == ']') {
                        inBracketExpression = false;
                        java.append("\\\\").append(next);
                     } else {
                        if (next == '<' || next == '>') {
                           next = 'b';
                        }

                        java.append(c).append(next);
                     }
                     break;
                  case ']':
                     inBracketExpression = false;
                     java.append(c);
                     break;
                  default:
                     java.append(c);
               }
            }
         } catch (Exception var9) {
            throw new IllegalArgumentException("Posix-to-Java regex translation failed around index " + i + " of: " + posix, var9);
         }

         return java.toString();
      }

      private static String replaceClass(String className) {
         switch (className) {
            case "alnum":
               return "\\p{Alnum}";
            case "alpha":
               return "\\p{Alpha}";
            case "blank":
               return "\\p{Blank}";
            case "cntrl":
               return "\\p{Cntrl}";
            case "digit":
               return "\\p{Digit}";
            case "graph":
               return "\\p{Graph}";
            case "lower":
               return "\\p{Lower}";
            case "print":
               return "\\p{Print}";
            case "punct":
               return "\\p{Punct}";
            case "space":
               return "\\s";
            case "upper":
               return "\\p{Upper}";
            case "xdigit":
               return "\\p{XDigit}";
            default:
               throw new IllegalArgumentException("Unknown class '" + className + "'");
         }
      }

      private static int nextAfterClass(String s, int idx) {
         if (s.charAt(idx) == ':') {
            idx++;
         }

         while (true) {
            char c = s.charAt(idx);
            if (!Character.isLetterOrDigit(c)) {
               return idx;
            }

            idx++;
         }
      }
   }

   private static class PathParts {
      final String staticPrefix;
      final String globPattern;

      PathParts(String staticPrefix, String globPattern) {
         this.staticPrefix = staticPrefix;
         this.globPattern = globPattern;
      }
   }

   protected static class RuleSplitter {
      protected static List<String> split(String s) {
         List<String> out = new ArrayList<>();
         if (s.length() == 0) {
            return out;
         } else {
            int depth = 0;
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < s.length(); i++) {
               char c = s.charAt(i);
               if (c == '"') {
                  if (depth == 0) {
                     depth = 1;
                  } else {
                     char nextChar = i < s.length() - 1 ? s.charAt(i + 1) : 32;
                     if (nextChar == ' ') {
                        depth = 0;
                     }
                  }
               } else if (c == ' ' && depth == 0 && sb.length() > 0) {
                  out.add(stripQuotes(sb.toString()));
                  sb = new StringBuilder();
                  continue;
               }

               if (sb.length() > 0 || c != ' ' && c != '\t') {
                  sb.append(c);
               }
            }

            if (sb.length() > 0) {
               out.add(stripQuotes(sb.toString()));
            }

            return out;
         }
      }

      private static String stripQuotes(String s) {
         String out = s.trim();
         if (s.startsWith("\"") && s.endsWith("\"")) {
            out = s.substring(1, s.length() - 1);
         }

         return out;
      }
   }
}
