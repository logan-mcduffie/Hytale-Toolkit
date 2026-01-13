package org.jline.console.impl;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jline.builtins.ConfigurationPath;
import org.jline.builtins.Options;
import org.jline.builtins.Styles;
import org.jline.builtins.SyntaxHighlighter;
import org.jline.console.CmdDesc;
import org.jline.console.CommandInput;
import org.jline.console.Printer;
import org.jline.console.ScriptEngine;
import org.jline.console.SystemRegistry;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Log;
import org.jline.utils.StyleResolver;

public class DefaultPrinter extends JlineCommandRegistry implements Printer {
   protected static final String VAR_PRNT_OPTIONS = "PRNT_OPTIONS";
   protected static final int PRNT_MAX_ROWS = 100000;
   protected static final int PRNT_MAX_DEPTH = 1;
   protected static final int PRNT_INDENTION = 4;
   private static final int NANORC_MAX_STRING_LENGTH = 400;
   private static final int HIGHLIGHTER_CACHE_SIZE = 5;
   private Map<Class<?>, Function<Object, Map<String, Object>>> objectToMap = new HashMap<>();
   private Map<Class<?>, Function<Object, String>> objectToString = new HashMap<>();
   private Map<String, Function<Object, AttributedString>> highlightValue = new HashMap<>();
   private int totLines;
   private final ScriptEngine engine;
   private final ConfigurationPath configPath;
   private StyleResolver prntStyle;
   private final LinkedHashMap<String, SyntaxHighlighter> highlighters = new LinkedHashMap<String, SyntaxHighlighter>(6, 0.75F, false) {
      @Override
      protected boolean removeEldestEntry(Entry<String, SyntaxHighlighter> eldest) {
         return this.size() > 5;
      }
   };

   public DefaultPrinter(ConfigurationPath configPath) {
      this(null, configPath);
   }

   public DefaultPrinter(ScriptEngine engine, ConfigurationPath configPath) {
      this.engine = engine;
      this.configPath = configPath;
   }

   @Override
   public void println(Object object) {
      this.internalPrintln(this.defaultPrntOptions(false), object);
   }

   @Override
   public void println(Map<String, Object> optionsIn, Object object) {
      Map<String, Object> options = new HashMap<>(optionsIn);

      for (Entry<String, Object> entry : this.defaultPrntOptions(options.containsKey("skipDefaultOptions")).entrySet()) {
         options.putIfAbsent(entry.getKey(), entry.getValue());
      }

      this.manageBooleanOptions(options);
      this.internalPrintln(options, object);
   }

   @Override
   public boolean refresh() {
      this.highlighters.clear();
      return true;
   }

   public String[] appendUsage(String[] customUsage) {
      String[] usage = new String[]{
         "prnt -  print object",
         "Usage: prnt [OPTIONS] object",
         "  -? --help                       Displays command help",
         "  -a --all                        Ignore columnsOut configuration",
         "  -b --border=CHAR                Table cell vertical border character",
         "  -c --columns=COLUMNS,...        Display given columns on map/table",
         "  -e --exclude=COLUMNS,...        Exclude given columns on table",
         "  -i --include=COLUMNS,...        Include given columns on table",
         "     --indention=INDENTION        Indention size",
         "     --maxColumnWidth=WIDTH       Maximum column width",
         "  -d --maxDepth=DEPTH             Maximum depth objects are resolved",
         "  -n --maxrows=ROWS               Maximum number of lines to display",
         "  -m --multiColumns               Display the collection of simple data in multiple columns",
         "     --oneRowTable                Display one row data on table",
         "  -h --rowHighlight=ROW           Highlight table rows. ROW = EVEN, ODD, ALL",
         "  -r --rownum                     Display table row numbers",
         "     --shortNames                 Truncate table column names (property.field -> field)",
         "     --skipDefaultOptions         Ignore all options defined in PRNT_OPTIONS",
         "     --structsOnTable             Display structs and lists on table",
         "  -s --style=STYLE                Use nanorc STYLE to highlight Object.",
         "                                  STYLE = JSON serialize object to JSON string before printing",
         "     --toString                   Use object's toString() method to get print value",
         "                                  DEFAULT: object's fields are put to property map before printing",
         "     --valueStyle=STYLE           Use nanorc style to highlight string and column/map values",
         "  -w --width=WIDTH                Display width (default terminal width)"
      };
      String[] out;
      if (customUsage != null && customUsage.length != 0) {
         out = new String[usage.length + customUsage.length];
         System.arraycopy(usage, 0, out, 0, usage.length);
         System.arraycopy(customUsage, 0, out, usage.length, customUsage.length);
      } else {
         out = usage;
      }

      return out;
   }

   public Map<String, Object> compileOptions(Options opt) {
      Map<String, Object> options = new HashMap<>();
      if (opt.isSet("skipDefaultOptions")) {
         options.put("skipDefaultOptions", true);
      } else if (opt.isSet("style")) {
         options.put("style", opt.get("style"));
      }

      if (opt.isSet("toString")) {
         options.put("toString", true);
      }

      if (opt.isSet("width")) {
         options.put("width", opt.getNumber("width"));
      }

      if (opt.isSet("rownum")) {
         options.put("rownum", true);
      }

      if (opt.isSet("oneRowTable")) {
         options.put("oneRowTable", true);
      }

      if (opt.isSet("shortNames")) {
         options.put("shortNames", true);
      }

      if (opt.isSet("structsOnTable")) {
         options.put("structsOnTable", true);
      }

      if (opt.isSet("columns")) {
         options.put("columns", Arrays.asList(opt.get("columns").split(",")));
      }

      if (opt.isSet("exclude")) {
         options.put("exclude", Arrays.asList(opt.get("exclude").split(",")));
      }

      if (opt.isSet("include")) {
         options.put("include", Arrays.asList(opt.get("include").split(",")));
      }

      if (opt.isSet("all")) {
         options.put("all", true);
      }

      if (opt.isSet("maxrows")) {
         options.put("maxrows", opt.getNumber("maxrows"));
      }

      if (opt.isSet("maxColumnWidth")) {
         options.put("maxColumnWidth", opt.getNumber("maxColumnWidth"));
      }

      if (opt.isSet("maxDepth")) {
         options.put("maxDepth", opt.getNumber("maxDepth"));
      }

      if (opt.isSet("indention")) {
         options.put("indention", opt.getNumber("indention"));
      }

      if (opt.isSet("valueStyle")) {
         options.put("valueStyle", opt.get("valueStyle"));
      }

      if (opt.isSet("border")) {
         options.put("border", opt.get("border"));
      }

      if (opt.isSet("rowHighlight")) {
         try {
            options.put("rowHighlight", this.optionRowHighlight(opt.get("rowHighlight")));
         } catch (Exception var5) {
            RuntimeException exception = new DefaultPrinter.BadOptionValueException("rowHighlight has a bad value: " + opt.get("rowHighlight"));
            exception.addSuppressed(var5);
            throw exception;
         }
      }

      if (opt.isSet("multiColumns")) {
         options.put("multiColumns", true);
      }

      options.put("exception", "stack");
      return options;
   }

   private Printer.TableRows optionRowHighlight(Object value) {
      if (value instanceof Printer.TableRows || value == null) {
         return (Printer.TableRows)value;
      } else if (value instanceof String) {
         String val = ((String)value).trim().toUpperCase();
         return !val.isEmpty() && !val.equals("NULL") ? Printer.TableRows.valueOf(val) : null;
      } else {
         throw new IllegalArgumentException("rowHighlight has a bad option value type: " + value.getClass());
      }
   }

   @Override
   public Exception prntCommand(CommandInput input) {
      Exception out = null;
      String[] usage = this.appendUsage(null);

      try {
         Options opt = this.parseOptions(usage, input.xargs());
         Map<String, Object> options = this.compileOptions(opt);
         List<Object> args = opt.argObjects();
         if (!args.isEmpty()) {
            this.println(options, args.get(0));
         }
      } catch (Exception var7) {
         out = var7;
      }

      return out;
   }

   public void setObjectToMap(Map<Class<?>, Function<Object, Map<String, Object>>> objectToMap) {
      this.objectToMap = objectToMap;
   }

   public void setObjectToString(Map<Class<?>, Function<Object, String>> objectToString) {
      this.objectToString = objectToString;
   }

   public void setHighlightValue(Map<String, Function<Object, AttributedString>> highlightValue) {
      this.highlightValue = highlightValue;
   }

   protected Terminal terminal() {
      return SystemRegistry.get().terminal();
   }

   protected void manageBooleanOptions(Map<String, Object> options) {
      for (String key : Printer.BOOLEAN_KEYS) {
         Object option = options.get(key);
         boolean value = option instanceof Boolean && (Boolean)option;
         if (!value) {
            options.remove(key);
         }
      }
   }

   protected Map<String, Object> defaultPrntOptions(boolean skipDefault) {
      Map<String, Object> out = new HashMap<>();
      if (this.engine != null && !skipDefault && this.engine.hasVariable("PRNT_OPTIONS")) {
         out.putAll((Map<? extends String, ? extends Object>)this.engine.get("PRNT_OPTIONS"));
         out.remove("skipDefaultOptions");
         this.manageBooleanOptions(out);
      }

      out.putIfAbsent("maxrows", 100000);
      out.putIfAbsent("maxDepth", 1);
      out.putIfAbsent("indention", 4);
      out.putIfAbsent("columnsOut", new ArrayList());
      out.putIfAbsent("columnsIn", new ArrayList());
      if (this.engine == null) {
         out.remove("objectToMap");
         out.remove("objectToString");
         out.remove("highlightValue");
      }

      return out;
   }

   private void internalPrintln(Map<String, Object> options, Object object) {
      if (object != null) {
         long start = new Date().getTime();
         if (options.containsKey("exclude")) {
            List<String> colOut = this.optionList("exclude", options);
            List<String> colIn = this.optionList("columnsIn", options);
            colIn.removeAll(colOut);
            colOut.addAll((List)options.get("columnsOut"));
            options.put("columnsIn", colIn);
            options.put("columnsOut", colOut);
         }

         if (options.containsKey("include")) {
            List<String> colIn = this.optionList("include", options);
            colIn.addAll((List)options.get("columnsIn"));
            options.put("columnsIn", colIn);
         }

         options.put("valueStyle", this.valueHighlighter((String)options.getOrDefault("valueStyle", null)));
         this.prntStyle = Styles.prntStyle();
         options.putIfAbsent("width", this.terminal().getSize().getColumns());
         String style = (String)options.getOrDefault("style", "");
         options.put("style", this.valueHighlighter(style));
         int width = (Integer)options.get("width");
         int maxrows = (Integer)options.get("maxrows");
         if (!style.isEmpty() && object instanceof String) {
            this.highlightAndPrint(width, (SyntaxHighlighter)options.get("style"), (String)object, true, maxrows);
         } else if (style.equalsIgnoreCase("JSON")) {
            if (this.engine == null) {
               throw new IllegalArgumentException("JSON style not supported!");
            }

            String json = this.engine.toJson(object);
            this.highlightAndPrint(width, (SyntaxHighlighter)options.get("style"), json, true, maxrows);
         } else if (options.containsKey("skipDefaultOptions")) {
            this.highlightAndPrint(options, object);
         } else if (object instanceof Exception) {
            this.highlightAndPrint(options, (Throwable)((Exception)object));
         } else if (object instanceof CmdDesc) {
            this.highlight((CmdDesc)object).println(this.terminal());
         } else if (!(object instanceof String) && !(object instanceof Number)) {
            this.highlightAndPrint(options, object);
         } else {
            String str = object.toString();
            SyntaxHighlighter highlighter = (SyntaxHighlighter)options.getOrDefault("valueStyle", null);
            this.highlightAndPrint(width, highlighter, str, this.doValueHighlight(options, str), maxrows);
         }

         this.terminal().flush();
         Log.debug("println: ", new Date().getTime() - start, " msec");
      }
   }

   protected void highlightAndPrint(Map<String, Object> options, Throwable exception) {
      SystemRegistry.get().trace(options.getOrDefault("exception", "stack").equals("stack"), exception);
   }

   private AttributedString highlight(CmdDesc cmdDesc) {
      StringBuilder sb = new StringBuilder();

      for (AttributedString as : cmdDesc.getMainDesc()) {
         sb.append(as.toString());
         sb.append("\n");
      }

      List<Integer> tabs = Arrays.asList(0, 2, 33);

      for (Entry<String, List<AttributedString>> entry : cmdDesc.getOptsDesc().entrySet()) {
         AttributedStringBuilder asb = new AttributedStringBuilder();
         asb.tabs(tabs);
         asb.append("\t");
         asb.append(entry.getKey());
         asb.append("\t");
         boolean first = true;

         for (AttributedString as : entry.getValue()) {
            if (!first) {
               asb.append("\t");
               asb.append("\t");
            }

            asb.append(as);
            asb.append("\n");
            first = false;
         }

         sb.append((CharSequence)asb);
      }

      return Options.HelpException.highlight(sb.toString(), Styles.helpStyle());
   }

   private SyntaxHighlighter valueHighlighter(String style) {
      SyntaxHighlighter out;
      if (style == null || style.isEmpty()) {
         out = null;
      } else if (this.highlighters.containsKey(style)) {
         out = this.highlighters.get(style);
      } else if (style.matches("[a-z]+:.*")) {
         out = SyntaxHighlighter.build(style);
         this.highlighters.put(style, out);
      } else {
         Path nanorc = this.configPath != null ? this.configPath.getConfig("jnanorc") : null;
         if (this.engine != null && this.engine.hasVariable("NANORC")) {
            nanorc = Paths.get((String)this.engine.get("NANORC"));
         }

         if (nanorc == null) {
            nanorc = Paths.get("/etc/nanorc");
         }

         out = SyntaxHighlighter.build(nanorc, style);
         this.highlighters.put(style, out);
      }

      return out;
   }

   private String truncate4nanorc(String obj) {
      String val = obj;
      if (obj.length() > 400 && !obj.contains("\n")) {
         val = obj.substring(0, 399);
      }

      return val;
   }

   private AttributedString highlight(Integer width, SyntaxHighlighter highlighter, String object, boolean doValueHighlight) {
      AttributedStringBuilder asb = new AttributedStringBuilder();
      String val = object;
      if (highlighter != null && doValueHighlight) {
         val = this.truncate4nanorc(object);
      }

      asb.append(val);
      AttributedString out;
      if (highlighter != null && val.length() < 400 && doValueHighlight) {
         out = highlighter.highlight(asb);
      } else {
         out = asb.toAttributedString();
      }

      if (width != null) {
         out = out.columnSubSequence(0, width);
      }

      return out;
   }

   private boolean doValueHighlight(Map<String, Object> options, String value) {
      return !options.containsKey("valueStyleAll")
            && !value.matches("\"(\\.|[^\"])*\"|'(\\.|[^'])*'")
            && (!value.startsWith("[") || !value.endsWith("]"))
            && (!value.startsWith("(") || !value.endsWith(")"))
            && (!value.startsWith("{") || !value.endsWith("}"))
            && (!value.startsWith("<") || !value.endsWith(">"))
         ? !value.contains(" ") && !value.contains("\t")
         : true;
   }

   private void highlightAndPrint(int width, SyntaxHighlighter highlighter, String object, boolean doValueHighlight, int maxRows) {
      String lineBreak = null;
      if (object.indexOf("\r\n") >= 0) {
         lineBreak = "\r\n";
      } else if (object.indexOf("\n") >= 0) {
         lineBreak = "\n";
      } else if (object.indexOf("\r") >= 0) {
         lineBreak = "\r";
      }

      if (lineBreak == null) {
         this.highlightAndPrint(width, highlighter, object, doValueHighlight);
      } else {
         int rows = 0;
         int i0 = 0;

         while (rows < maxRows) {
            rows++;
            int i1 = object.indexOf(lineBreak, i0);
            String line = i1 >= 0 ? object.substring(i0, i1) : object.substring(i0);
            this.highlightAndPrint(width, highlighter, line, doValueHighlight);
            if (i1 < 0) {
               break;
            }

            i0 = i1 + lineBreak.length();
         }

         if (rows == maxRows) {
            throw new DefaultPrinter.TruncatedOutputException("Truncated output: " + maxRows);
         }
      }
   }

   private void highlightAndPrint(int width, SyntaxHighlighter highlighter, String object, boolean doValueHighlight) {
      AttributedStringBuilder asb = new AttributedStringBuilder();

      for (AttributedString as : asb.append(object).columnSplitLength(width)) {
         this.highlight(width, highlighter, as.toString(), doValueHighlight).println(this.terminal());
      }
   }

   private Map<String, Object> keysToString(Map<Object, Object> map) {
      Map<String, Object> out = new HashMap<>();

      for (Entry<Object, Object> entry : map.entrySet()) {
         if (entry.getKey() instanceof String) {
            out.put((String)entry.getKey(), entry.getValue());
         } else if (entry.getKey() != null) {
            out.put(entry.getKey().toString(), entry.getValue());
         } else {
            out.put("null", entry.getValue());
         }
      }

      return out;
   }

   private Object mapValue(Map<String, Object> options, String key, Map<String, Object> map) {
      Object out = null;
      if (map.containsKey(key)) {
         out = map.get(key);
      } else if (key.contains(".")) {
         String[] keys = key.split("\\.");
         out = map.get(keys[0]);

         for (int i = 1; i < keys.length; i++) {
            if (out instanceof Map) {
               Map<String, Object> m = this.keysToString((Map<Object, Object>)out);
               out = m.get(keys[i]);
            } else {
               if (!this.canConvert(out)) {
                  break;
               }

               out = this.engine.toMap(out).get(keys[i]);
            }
         }
      }

      if (!(out instanceof Map) && this.canConvert(out)) {
         out = this.objectToMap(options, out);
      }

      return out;
   }

   private List<String> optionList(String key, Map<String, Object> options) {
      List<String> out = new ArrayList<>();
      Object option = options.get(key);
      if (option instanceof String) {
         out.addAll(Arrays.asList(((String)option).split(",")));
      } else if (option instanceof Collection) {
         out.addAll((Collection<? extends String>)option);
      } else if (option != null) {
         throw new IllegalArgumentException("Unsupported option list: {key: " + key + ", type: " + option.getClass() + "}");
      }

      return out;
   }

   private boolean hasMatch(List<String> regexes, String value) {
      for (String r : regexes) {
         if (value.matches(r)) {
            return true;
         }
      }

      return false;
   }

   private AttributedString addPadding(AttributedString str, int width) {
      AttributedStringBuilder sb = new AttributedStringBuilder();

      for (int i = str.columnLength(); i < width; i++) {
         sb.append(" ");
      }

      sb.append(str);
      return sb.toAttributedString();
   }

   private String addPadding(String str, int width) {
      AttributedStringBuilder sb = new AttributedStringBuilder();

      for (int i = str.length(); i < width; i++) {
         sb.append(" ");
      }

      sb.append(str);
      return sb.toString();
   }

   private String columnValue(String value) {
      return value.replaceAll("\r", "CR").replaceAll("\n", "LF");
   }

   private Map<String, Object> objectToMap(Map<String, Object> options, Object obj) {
      if (obj != null) {
         Map<Class<?>, Object> toMap = (Map<Class<?>, Object>)options.getOrDefault("objectToMap", Collections.emptyMap());
         if (toMap.containsKey(obj.getClass())) {
            return (Map<String, Object>)this.engine.execute(toMap.get(obj.getClass()), obj);
         }

         if (this.objectToMap.containsKey(obj.getClass())) {
            return this.objectToMap.get(obj.getClass()).apply(obj);
         }
      }

      return this.engine.toMap(obj);
   }

   private String objectToString(Map<String, Object> options, Object obj) {
      String out = "null";
      if (obj != null) {
         Map<Class<?>, Object> toString = (Map<Class<?>, Object>)(options.containsKey("objectToString") ? (Map)options.get("objectToString") : new HashMap<>());
         if (toString.containsKey(obj.getClass())) {
            out = (String)this.engine.execute(toString.get(obj.getClass()), obj);
         } else if (this.objectToString.containsKey(obj.getClass())) {
            out = this.objectToString.get(obj.getClass()).apply(obj);
         } else if (obj instanceof Class) {
            out = ((Class)obj).getName();
         } else if (this.engine != null) {
            out = this.engine.toString(obj);
         } else {
            out = obj.toString();
         }
      }

      return out;
   }

   private AttributedString highlightMapValue(Map<String, Object> options, String key, Map<String, Object> map) {
      return this.highlightValue(options, key, this.mapValue(options, key, map));
   }

   private boolean isHighlighted(AttributedString value) {
      for (int i = 0; i < value.length(); i++) {
         if (value.styleAt(i).getStyle() != AttributedStyle.DEFAULT.getStyle()) {
            return true;
         }
      }

      return false;
   }

   private AttributedString highlightValue(Map<String, Object> options, String column, Object obj) {
      AttributedString out = null;
      Object raw = options.containsKey("toString") && obj != null ? this.objectToString(options, obj) : obj;
      Map<String, Object> hv = (Map<String, Object>)(options.containsKey("highlightValue") ? (Map)options.get("highlightValue") : new HashMap<>());
      if (column != null && this.simpleObject(raw)) {
         for (Entry<String, Object> entry : hv.entrySet()) {
            if (!entry.getKey().equals("*") && column.matches(entry.getKey())) {
               out = (AttributedString)this.engine.execute(hv.get(entry.getKey()), raw);
               break;
            }
         }

         if (out == null) {
            for (Entry<String, Function<Object, AttributedString>> entryx : this.highlightValue.entrySet()) {
               if (!entryx.getKey().equals("*") && column.matches(entryx.getKey())) {
                  out = this.highlightValue.get(entryx.getKey()).apply(raw);
                  break;
               }
            }
         }
      }

      if (out == null) {
         if (raw instanceof String) {
            out = new AttributedString(this.columnValue((String)raw));
         } else {
            out = new AttributedString(this.columnValue(this.objectToString(options, raw)));
         }
      }

      if ((this.simpleObject(raw) || raw == null) && (hv.containsKey("*") || this.highlightValue.containsKey("*")) && !this.isHighlighted(out)) {
         if (hv.containsKey("*")) {
            out = (AttributedString)this.engine.execute(hv.get("*"), out);
         }

         Function<Object, AttributedString> func = this.highlightValue.get("*");
         if (func != null) {
            out = func.apply(out);
         }
      }

      if (options.containsKey("valueStyle") && !this.isHighlighted(out)) {
         out = this.highlight(null, (SyntaxHighlighter)options.get("valueStyle"), out.toString(), this.doValueHighlight(options, out.toString()));
      }

      return this.truncateValue(options, out);
   }

   private AttributedString truncateValue(Map<String, Object> options, AttributedString value) {
      if (value.columnLength() > (Integer)options.getOrDefault("maxColumnWidth", Integer.MAX_VALUE)) {
         AttributedStringBuilder asb = new AttributedStringBuilder();
         asb.append(value.columnSubSequence(0, (Integer)options.get("maxColumnWidth") - 3));
         asb.append("...");
         return asb.toAttributedString();
      } else {
         return value;
      }
   }

   private String truncateValue(int maxWidth, String value) {
      return value.length() > maxWidth ? value.subSequence(0, maxWidth - 3) + "..." : value;
   }

   private List<Object> objectToList(Object obj) {
      List<Object> out = new ArrayList<>();
      if (obj instanceof List) {
         out = (List<Object>)obj;
      } else if (obj instanceof Collection) {
         out.addAll((Collection<? extends Object>)obj);
      } else if (obj instanceof Object[]) {
         out.addAll(Arrays.asList((Object[])obj));
      } else if (obj instanceof Iterator) {
         ((Iterator)obj).forEachRemaining(out::add);
      } else if (obj instanceof Iterable) {
         ((Iterable)obj).forEach(out::add);
      } else {
         out.add(obj);
      }

      return out;
   }

   private boolean similarSets(List<String> ref, Set<String> c2, int matchLimit) {
      boolean out = false;
      int limit = matchLimit;

      for (String s : ref) {
         if (c2.contains(s)) {
            if (--limit == 0) {
               out = true;
               break;
            }
         }
      }

      return out;
   }

   private void println(AttributedString line, int maxrows) {
      line.println(this.terminal());
      this.totLines++;
      if (this.totLines > maxrows) {
         this.totLines = 0;
         throw new DefaultPrinter.TruncatedOutputException("Truncated output: " + maxrows);
      }
   }

   private String columnName(String name, boolean shortName) {
      String out = name;
      if (shortName) {
         String[] p = name.split("\\.");
         out = p[p.length - 1];
      }

      return out;
   }

   private boolean isNumber(String str) {
      return str.matches("-?\\d+(\\.\\d+)?");
   }

   private void highlightAndPrint(Map<String, Object> options, Object obj) {
      int width = (Integer)options.get("width");
      int maxrows = (Integer)options.get("maxrows");
      this.totLines = 0;
      String message = null;
      RuntimeException runtimeException = null;
      if (obj != null) {
         if (obj instanceof Map) {
            this.highlightMap(options, this.keysToString((Map<Object, Object>)obj), width);
         } else if (this.collectionObject(obj)) {
            List<Object> collection = this.objectToList(obj);
            if (collection.size() > maxrows) {
               message = "Truncated output: " + maxrows + "/" + collection.size();
               collection = collection.subList(collection.size() - maxrows, collection.size());
            }

            if (!collection.isEmpty()) {
               if (collection.size() == 1 && !options.containsKey("oneRowTable")) {
                  Object elem = collection.iterator().next();
                  if (elem instanceof Map) {
                     this.highlightMap(options, this.keysToString((Map<Object, Object>)elem), width);
                  } else if (this.canConvert(elem) && !options.containsKey("toString")) {
                     this.highlightMap(options, this.objectToMap(options, elem), width);
                  } else if (elem instanceof String && options.get("style") != null) {
                     this.highlightAndPrint(width, (SyntaxHighlighter)options.get("style"), (String)elem, true, maxrows);
                  } else {
                     this.highlightValue(options, null, this.objectToString(options, obj)).println(this.terminal());
                  }
               } else {
                  String columnSep = "";
                  Printer.TableRows tableRows = null;
                  boolean rownum = options.containsKey("rownum");

                  try {
                     columnSep = (String)options.getOrDefault("border", "");
                     tableRows = this.optionRowHighlight(options.getOrDefault("rowHighlight", null));
                  } catch (Exception var34) {
                     runtimeException = new DefaultPrinter.BadOptionValueException("Option border or rowHighlight has a bad value!");
                     runtimeException.addSuppressed(var34);
                  }

                  try {
                     Object elem = collection.iterator().next();
                     boolean convert = this.canConvert(elem);
                     if ((elem instanceof Map || convert) && !options.containsKey("toString")) {
                        List<Map<String, Object>> convertedCollection = new ArrayList<>();
                        Set<String> keys = new HashSet<>();

                        for (Object o : collection) {
                           Map<String, Object> m = convert ? this.objectToMap(options, o) : this.keysToString((Map<Object, Object>)o);
                           convertedCollection.add(m);
                           keys.addAll(m.keySet());
                        }

                        List<String> columnsIn = this.optionList("columnsIn", options);
                        List<String> columnsOut = (List<String>)(!options.containsKey("all") ? this.optionList("columnsOut", options) : new ArrayList<>());
                        List<String> _header;
                        if (options.containsKey("columns")) {
                           _header = (List<String>)options.get("columns");
                        } else {
                           _header = columnsIn;
                           columnsIn.addAll(keys.stream().filter(k -> !columnsIn.contains(k) && !this.hasMatch(columnsOut, k)).collect(Collectors.toList()));
                        }

                        List<String> header = new ArrayList<>();
                        List<Integer> columns = new ArrayList<>();
                        int headerWidth = 0;
                        List<String> refKeys = new ArrayList<>();

                        for (String v : _header) {
                           String value = v.split("\\.")[0];
                           if (keys.contains(value) || keys.contains(v)) {
                              boolean addKey = false;

                              for (Map<String, Object> m : convertedCollection) {
                                 Object val = this.mapValue(options, v, m);
                                 if (val != null) {
                                    addKey = this.simpleObject(val) || options.containsKey("columns") || options.containsKey("structsOnTable");
                                    break;
                                 }
                              }

                              if (addKey) {
                                 refKeys.add(value);
                                 header.add(v);
                                 String cn = this.columnName(v, options.containsKey("shortNames"));
                                 columns.add(cn.length() + 1);
                                 headerWidth += cn.length() + 1;
                                 if (headerWidth > width) {
                                    break;
                                 }
                              }
                           }
                        }

                        if (header.size() == 0) {
                           throw new Exception("No columns for table!");
                        }

                        double mapSimilarity = ((BigDecimal)options.getOrDefault("mapSimilarity", new BigDecimal("0.8"))).doubleValue();
                        int matchLimit = (int)Math.ceil(header.size() * mapSimilarity);

                        for (Map<String, Object> mx : convertedCollection) {
                           if (!this.similarSets(refKeys, mx.keySet(), matchLimit)) {
                              throw new Exception("Not homogenous list!");
                           }

                           for (int i = 0; i < header.size(); i++) {
                              int cw = this.highlightMapValue(options, header.get(i), mx).columnLength();
                              if (cw > columns.get(i) - 1) {
                                 columns.set(i, cw + 1);
                              }
                           }
                        }

                        this.toTabStops(columns, collection.size(), rownum, columnSep);
                        AttributedStringBuilder asb = new AttributedStringBuilder().tabs(columns);
                        asb.style(this.prntStyle.resolve(".th"));
                        int firstColumn = 0;
                        if (rownum) {
                           asb.append(this.addPadding("", columns.get(0) - columnSep.length() - 1));
                           asb.append(columnSep);
                           asb.append("\t");
                           firstColumn = 1;
                        }

                        boolean first = true;

                        for (String s : header) {
                           if (!first) {
                              asb.append(columnSep);
                           }

                           asb.append(this.columnName(s, options.containsKey("shortNames")));
                           asb.append("\t");
                           first = false;
                        }

                        asb.columnSubSequence(0, width).println(this.terminal());
                        int row = 0;

                        for (Map<String, Object> mx : convertedCollection) {
                           AttributedStringBuilder asb2 = new AttributedStringBuilder().tabs(columns);
                           if (this.doRowHighlight(row, tableRows)) {
                              asb2.style(this.prntStyle.resolve(".rs"));
                           }

                           if (rownum) {
                              asb2.styled(this.prntStyle.resolve(".rn"), this.addPadding(Integer.toString(row), columns.get(0) - columnSep.length() - 1));
                              asb2.append(columnSep);
                              asb2.append("\t");
                           }

                           row++;

                           for (int ix = 0; ix < header.size(); ix++) {
                              if (ix > 0) {
                                 asb2.append(columnSep);
                              }

                              AttributedString vx = this.highlightMapValue(options, header.get(ix), mx);
                              if (this.isNumber(vx.toString())) {
                                 vx = this.addPadding(vx, this.cellWidth(firstColumn + ix, columns, rownum, columnSep) - 1);
                              }

                              asb2.append(vx);
                              asb2.append("\t");
                           }

                           asb2.columnSubSequence(0, width).println(this.terminal());
                        }
                     } else if (this.collectionObject(elem) && !options.containsKey("toString")) {
                        List<Integer> columns = new ArrayList<>();

                        for (Object o : collection) {
                           List<Object> inner = this.objectToList(o);

                           for (int ix = 0; ix < inner.size(); ix++) {
                              int len1 = this.objectToString(options, inner.get(ix)).length() + 1;
                              if (columns.size() <= ix) {
                                 columns.add(len1);
                              } else if (len1 > columns.get(ix)) {
                                 columns.set(ix, len1);
                              }
                           }
                        }

                        this.toTabStops(columns, collection.size(), rownum, columnSep);
                        int row = 0;
                        int firstColumn = rownum ? 1 : 0;

                        for (Object o : collection) {
                           AttributedStringBuilder asb = new AttributedStringBuilder().tabs(columns);
                           if (this.doRowHighlight(row, tableRows)) {
                              asb.style(this.prntStyle.resolve(".rs"));
                           }

                           if (rownum) {
                              asb.styled(this.prntStyle.resolve(".rn"), this.addPadding(Integer.toString(row), columns.get(0) - columnSep.length() - 1));
                              asb.append(columnSep);
                              asb.append("\t");
                           }

                           row++;
                           List<Object> inner = this.objectToList(o);

                           for (int ixx = 0; ixx < inner.size(); ixx++) {
                              if (ixx > 0) {
                                 asb.append(columnSep);
                              }

                              AttributedString vx = this.highlightValue(options, null, inner.get(ixx));
                              if (this.isNumber(vx.toString())) {
                                 vx = this.addPadding(vx, this.cellWidth(firstColumn + ixx, columns, rownum, columnSep) - 1);
                              }

                              asb.append(vx);
                              asb.append("\t");
                           }

                           asb.columnSubSequence(0, width).println(this.terminal());
                        }
                     } else {
                        this.highlightList(options, collection, width);
                     }
                  } catch (Exception var35) {
                     Log.debug("Stack: ", var35);
                     this.highlightList(options, collection, width);
                  }
               }
            } else {
               this.highlightValue(options, null, this.objectToString(options, obj)).println(this.terminal());
            }
         } else if (this.canConvert(obj) && !options.containsKey("toString")) {
            this.highlightMap(options, this.objectToMap(options, obj), width);
         } else {
            this.highlightValue(options, null, this.objectToString(options, obj)).println(this.terminal());
         }
      }

      if (message != null) {
         AttributedStringBuilder asbx = new AttributedStringBuilder();
         asbx.styled(this.prntStyle.resolve(".em"), message);
         asbx.println(this.terminal());
      }

      if (runtimeException != null) {
         throw runtimeException;
      }
   }

   private boolean doRowHighlight(int row, Printer.TableRows tableRows) {
      if (tableRows == null) {
         return false;
      } else {
         switch (tableRows) {
            case EVEN:
               return row % 2 == 0;
            case ODD:
               return row % 2 == 1;
            case ALL:
               return true;
            default:
               return false;
         }
      }
   }

   private void highlightList(Map<String, Object> options, List<Object> collection, int width) {
      this.highlightList(options, collection, width, 0);
   }

   private void highlightList(Map<String, Object> options, List<Object> collection, int width, int depth) {
      int row = 0;
      int maxrows = (Integer)options.get("maxrows");
      int indent = (Integer)options.get("indention");
      List<Integer> tabs = new ArrayList<>();
      SyntaxHighlighter highlighter = depth == 0 ? (SyntaxHighlighter)options.get("style") : null;
      if (!(Boolean)options.getOrDefault("multiColumns", false)) {
         tabs.add(indent * depth);
         if (options.containsKey("rownum")) {
            tabs.add(indent * depth + this.digits(collection.size()) + 2);
         }

         options.remove("maxColumnWidth");

         for (Object o : collection) {
            AttributedStringBuilder asb = new AttributedStringBuilder().tabs(tabs);
            if (depth > 0) {
               asb.append("\t");
            }

            if (options.containsKey("rownum")) {
               asb.styled(this.prntStyle.resolve(".rn"), Integer.toString(row)).append(":");
               asb.append("\t");
               row++;
            }

            if (highlighter != null && o instanceof String) {
               asb.append(highlighter.highlight((String)o));
            } else {
               asb.append(this.highlightValue(options, null, o));
            }

            this.println(asb.columnSubSequence(0, width), maxrows);
         }
      } else {
         int maxWidth = 0;

         for (Object o : collection) {
            AttributedString as;
            if (highlighter != null && o instanceof String) {
               as = highlighter.highlight((String)o);
            } else {
               as = this.highlightValue(options, null, o);
            }

            if (as.length() > maxWidth) {
               maxWidth = as.length();
            }
         }

         int mcw = (Integer)options.getOrDefault("maxColumnWidth", Integer.MAX_VALUE);
         maxWidth = mcw < maxWidth ? mcw : maxWidth;
         tabs.add(maxWidth + 1);
         AttributedStringBuilder asbx = new AttributedStringBuilder().tabs(tabs);

         for (Object o : collection) {
            if (asbx.length() + maxWidth > width) {
               this.println(asbx.columnSubSequence(0, width), maxrows);
               asbx = new AttributedStringBuilder().tabs(tabs);
            }

            if (highlighter != null && o instanceof String) {
               asbx.append(highlighter.highlight((String)o));
            } else {
               asbx.append(this.highlightValue(options, null, o));
            }

            asbx.append("\t");
         }

         this.println(asbx.columnSubSequence(0, width), maxrows);
      }
   }

   private boolean collectionObject(Object obj) {
      return obj instanceof Iterator || obj instanceof Iterable || obj instanceof Object[];
   }

   private boolean simpleObject(Object obj) {
      return obj instanceof Number || obj instanceof String || obj instanceof Date || obj instanceof File || obj instanceof Boolean || obj instanceof Enum;
   }

   private boolean canConvert(Object obj) {
      return this.engine != null && obj != null && !(obj instanceof Class) && !(obj instanceof Map) && !this.simpleObject(obj) && !this.collectionObject(obj);
   }

   private int digits(int number) {
      if (number < 100) {
         return number < 10 ? 1 : 2;
      } else if (number < 1000) {
         return 3;
      } else {
         return number < 10000 ? 4 : 5;
      }
   }

   private int cellWidth(int pos, List<Integer> columns, boolean rownum, String columnSep) {
      return pos == 0 ? columns.get(0) : columns.get(pos) - columns.get(pos - 1) - (rownum && pos == 1 ? 0 : columnSep.length());
   }

   private void toTabStops(List<Integer> columns, int rows, boolean rownum, String columnSep) {
      if (rownum) {
         columns.add(0, this.digits(rows) + 2 + columnSep.length());
      }

      for (int i = 1; i < columns.size(); i++) {
         columns.set(i, columns.get(i - 1) + columns.get(i) + (i <= 1 && rownum ? 0 : columnSep.length()));
      }
   }

   private void highlightMap(Map<String, Object> options, Map<String, Object> map, int width) {
      if (!map.isEmpty()) {
         this.highlightMap(options, map, width, 0);
      } else {
         this.highlightValue(options, null, this.objectToString(options, map)).println(this.terminal());
      }
   }

   private void highlightMap(Map<String, Object> options, Map<String, Object> map, int width, int depth) {
      int maxrows = (Integer)options.get("maxrows");
      int max = map.keySet().stream().map(String::length).max(Integer::compareTo).get();
      if (max > (Integer)options.getOrDefault("maxColumnWidth", Integer.MAX_VALUE)) {
         max = (Integer)options.get("maxColumnWidth");
      }

      Map<String, Object> mapOptions = new HashMap<>(options);
      mapOptions.remove("maxColumnWidth");
      int indent = (Integer)options.get("indention");
      int maxDepth = (Integer)options.get("maxDepth");

      for (Entry<String, Object> entry : map.entrySet()) {
         if (depth != 0 || !options.containsKey("columns") || ((List)options.get("columns")).contains(entry.getKey())) {
            AttributedStringBuilder asb = new AttributedStringBuilder().tabs(Arrays.asList(0, depth * indent, depth * indent + max + 1));
            if (depth != 0) {
               asb.append("\t");
            }

            asb.styled(this.prntStyle.resolve(".mk"), this.truncateValue(max, entry.getKey()));
            Object elem = entry.getValue();
            boolean convert = this.canConvert(elem);
            boolean highlightValue = true;
            if (depth < maxDepth && !options.containsKey("toString")) {
               if (!(elem instanceof Map) && !convert) {
                  if (this.collectionObject(elem)) {
                     List<Object> collection = this.objectToList(elem);
                     if (!collection.isEmpty()) {
                        this.println(asb.columnSubSequence(0, width), maxrows);
                        Map<String, Object> listOptions = new HashMap<>(options);
                        listOptions.put("toString", true);
                        this.highlightList(listOptions, collection, width, depth + 1);
                        highlightValue = false;
                     }
                  }
               } else {
                  Map<String, Object> childMap = convert ? this.objectToMap(options, elem) : this.keysToString((Map<Object, Object>)elem);
                  if (!childMap.isEmpty()) {
                     this.println(asb.columnSubSequence(0, width), maxrows);
                     this.highlightMap(options, childMap, width, depth + 1);
                     highlightValue = false;
                  }
               }
            }

            if (highlightValue) {
               AttributedString val = this.highlightMapValue(mapOptions, entry.getKey(), map);
               asb.append("\t");
               if (map.size() == 1) {
                  if (val.contains('\n')) {
                     for (String v : val.toString().split("\\r?\\n")) {
                        asb.append(this.highlightValue(options, entry.getKey(), v));
                        this.println(asb.columnSubSequence(0, width), maxrows);
                        asb = new AttributedStringBuilder().tabs(Arrays.asList(0, max + 1));
                     }
                  } else {
                     asb.append(val);
                     this.println(asb.columnSubSequence(0, width), maxrows);
                  }
               } else {
                  if (val.contains('\n')) {
                     val = new AttributedString(Arrays.asList(val.toString().split("\\r?\\n")).toString());
                     asb.append(this.highlightValue(options, entry.getKey(), val.toString()));
                  } else {
                     asb.append(val);
                  }

                  this.println(asb.columnSubSequence(0, width), maxrows);
               }
            }
         }
      }
   }

   private static class BadOptionValueException extends RuntimeException {
      public BadOptionValueException(String message) {
         super(message);
      }
   }

   private static class TruncatedOutputException extends RuntimeException {
      public TruncatedOutputException(String message) {
         super(message);
      }
   }
}
