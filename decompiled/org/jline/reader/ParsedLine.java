package org.jline.reader;

import java.util.List;

public interface ParsedLine {
   String word();

   int wordCursor();

   int wordIndex();

   List<String> words();

   String line();

   int cursor();
}
