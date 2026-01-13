package org.bouncycastle.est.jcajce;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.bouncycastle.est.LimitedSource;
import org.bouncycastle.est.Source;
import org.bouncycastle.est.TLSUniqueProvider;

class LimitedSSLSocketSource implements Source<SSLSession>, TLSUniqueProvider, LimitedSource {
   protected final SSLSocket socket;
   private final ChannelBindingProvider bindingProvider;
   private final Long absoluteReadLimit;

   public LimitedSSLSocketSource(SSLSocket var1, ChannelBindingProvider var2, Long var3) {
      this.socket = var1;
      this.bindingProvider = var2;
      this.absoluteReadLimit = var3;
   }

   @Override
   public InputStream getInputStream() throws IOException {
      return this.socket.getInputStream();
   }

   @Override
   public OutputStream getOutputStream() throws IOException {
      return this.socket.getOutputStream();
   }

   public SSLSession getSession() {
      return this.socket.getSession();
   }

   @Override
   public byte[] getTLSUnique() {
      if (this.isTLSUniqueAvailable()) {
         return this.bindingProvider.getChannelBinding(this.socket, "tls-unique");
      } else {
         throw new IllegalStateException("No binding provider.");
      }
   }

   @Override
   public boolean isTLSUniqueAvailable() {
      return this.bindingProvider.canAccessChannelBinding(this.socket);
   }

   @Override
   public void close() throws IOException {
      this.socket.close();
   }

   @Override
   public Long getAbsoluteReadLimit() {
      return this.absoluteReadLimit;
   }
}
