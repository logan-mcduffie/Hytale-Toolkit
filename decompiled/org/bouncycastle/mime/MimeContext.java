package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;

public interface MimeContext {
   InputStream applyContext(Headers var1, InputStream var2) throws IOException;
}
