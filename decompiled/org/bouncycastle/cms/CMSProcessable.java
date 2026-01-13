package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;

public interface CMSProcessable {
   void write(OutputStream var1) throws IOException, CMSException;

   Object getContent();
}
