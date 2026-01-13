package org.jline.reader.impl.completer;

import java.util.List;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

public final class NullCompleter implements Completer {
   public static final NullCompleter INSTANCE = new NullCompleter();

   @Override
   public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
   }
}
