package org.bouncycastle.cms;

import java.io.OutputStream;

interface CMSSecureReadableWithAAD extends CMSSecureReadable {
   void setAADStream(OutputStream var1);

   OutputStream getAADStream();

   byte[] getMAC();
}
