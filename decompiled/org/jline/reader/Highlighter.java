package org.jline.reader;

import java.util.regex.Pattern;
import org.jline.utils.AttributedString;

public interface Highlighter {
   AttributedString highlight(LineReader var1, String var2);

   default void refresh(LineReader reader) {
   }

   default void setErrorPattern(Pattern errorPattern) {
   }

   default void setErrorIndex(int errorIndex) {
   }
}
