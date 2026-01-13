package org.jline.reader;

import java.util.List;

public interface Completer {
   void complete(LineReader var1, ParsedLine var2, List<Candidate> var3);
}
