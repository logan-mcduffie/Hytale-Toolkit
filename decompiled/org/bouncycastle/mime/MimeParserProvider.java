package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;

public interface MimeParserProvider {
   MimeParser createParser(InputStream var1) throws IOException;

   MimeParser createParser(Headers var1, InputStream var2) throws IOException;
}
