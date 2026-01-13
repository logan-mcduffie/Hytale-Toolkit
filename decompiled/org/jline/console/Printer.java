package org.jline.console;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Printer {
   String ALL = "all";
   String COLUMNS = "columns";
   String EXCLUDE = "exclude";
   String INCLUDE = "include";
   String INDENTION = "indention";
   String MAX_COLUMN_WIDTH = "maxColumnWidth";
   String MAX_DEPTH = "maxDepth";
   String MAXROWS = "maxrows";
   String ONE_ROW_TABLE = "oneRowTable";
   String ROWNUM = "rownum";
   String SHORT_NAMES = "shortNames";
   String SKIP_DEFAULT_OPTIONS = "skipDefaultOptions";
   String STRUCT_ON_TABLE = "structsOnTable";
   String STYLE = "style";
   String TO_STRING = "toString";
   String VALUE_STYLE = "valueStyle";
   String WIDTH = "width";
   String BORDER = "border";
   String ROW_HIGHLIGHT = "rowHighlight";
   String COLUMNS_IN = "columnsIn";
   String COLUMNS_OUT = "columnsOut";
   String HIGHLIGHT_VALUE = "highlightValue";
   String MAP_SIMILARITY = "mapSimilarity";
   String OBJECT_TO_MAP = "objectToMap";
   String OBJECT_TO_STRING = "objectToString";
   String VALUE_STYLE_ALL = "valueStyleAll";
   String MULTI_COLUMNS = "multiColumns";
   List<String> BOOLEAN_KEYS = Arrays.asList(
      "all", "oneRowTable", "rownum", "shortNames", "skipDefaultOptions", "structsOnTable", "toString", "valueStyleAll", "multiColumns"
   );

   default void println(Object object) {
      this.println(new HashMap<>(), object);
   }

   void println(Map<String, Object> var1, Object var2);

   default Exception prntCommand(CommandInput input) {
      return null;
   }

   boolean refresh();

   public static enum TableRows {
      EVEN,
      ODD,
      ALL;
   }
}
