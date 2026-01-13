package org.jline.reader;

public interface CompletingParsedLine extends ParsedLine {
   CharSequence escape(CharSequence var1, boolean var2);

   int rawWordCursor();

   int rawWordLength();
}
