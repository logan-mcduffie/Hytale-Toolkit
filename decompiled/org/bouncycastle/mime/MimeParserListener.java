package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;

public interface MimeParserListener {
   MimeContext createContext(MimeParserContext var1, Headers var2);

   void object(MimeParserContext var1, Headers var2, InputStream var3) throws IOException;
}
