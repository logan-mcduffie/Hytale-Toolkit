package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;

public class ConstantMimeContext implements MimeContext, MimeMultipartContext {
   public static final ConstantMimeContext Instance = new ConstantMimeContext();

   @Override
   public InputStream applyContext(Headers var1, InputStream var2) throws IOException {
      return var2;
   }

   @Override
   public MimeContext createContext(int var1) throws IOException {
      return this;
   }
}
