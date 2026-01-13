package io.netty.handler.codec.quic;

import javax.net.ssl.SSLSessionContext;
import org.jetbrains.annotations.Nullable;

public interface QuicSslSessionContext extends SSLSessionContext {
   void setTicketKeys(SslSessionTicketKey @Nullable ... var1);
}
