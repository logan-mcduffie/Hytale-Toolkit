package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.SslContext;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public abstract class QuicSslContext extends SslContext {
   public abstract QuicSslEngine newEngine(ByteBufAllocator var1);

   public abstract QuicSslEngine newEngine(ByteBufAllocator var1, String var2, int var3);

   public abstract QuicSslSessionContext sessionContext();

   static X509Certificate[] toX509Certificates0(InputStream stream) throws CertificateException {
      return SslContext.toX509Certificates(stream);
   }
}
