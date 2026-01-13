package org.jline.reader;

public interface Expander {
   String expandHistory(History var1, String var2);

   String expandVar(String var1);
}
