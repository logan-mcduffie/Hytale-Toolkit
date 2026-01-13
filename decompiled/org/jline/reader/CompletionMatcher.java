package org.jline.reader;

import java.util.List;
import java.util.Map;

public interface CompletionMatcher {
   void compile(Map<LineReader.Option, Boolean> var1, boolean var2, CompletingParsedLine var3, boolean var4, int var5, String var6);

   List<Candidate> matches(List<Candidate> var1);

   Candidate exactMatch();

   String getCommonPrefix();
}
