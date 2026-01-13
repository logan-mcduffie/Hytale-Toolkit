package org.jline.reader;

import java.io.IOException;
import java.util.List;

public interface Editor {
   void open(List<String> var1) throws IOException;

   void run() throws IOException;

   void setRestricted(boolean var1);
}
