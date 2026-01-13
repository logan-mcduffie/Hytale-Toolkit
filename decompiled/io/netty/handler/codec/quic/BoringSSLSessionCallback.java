package io.netty.handler.codec.quic;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.Nullable;

final class BoringSSLSessionCallback {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(BoringSSLSessionCallback.class);
   private final QuicClientSessionCache sessionCache;
   private final QuicheQuicSslEngineMap engineMap;

   BoringSSLSessionCallback(QuicheQuicSslEngineMap engineMap, @Nullable QuicClientSessionCache sessionCache) {
      this.engineMap = engineMap;
      this.sessionCache = sessionCache;
   }

   void newSession(long ssl, long creationTime, long timeout, byte[] session, boolean isSingleUse, byte @Nullable [] peerParams) {
      if (this.sessionCache != null) {
         QuicheQuicSslEngine engine = this.engineMap.get(ssl);
         if (engine == null) {
            logger.warn("engine is null ssl: {}", ssl);
         } else {
            if (peerParams == null) {
               peerParams = EmptyArrays.EMPTY_BYTES;
            }

            if (logger.isDebugEnabled()) {
               logger.debug("ssl: {}, session: {}, peerParams: {}", ssl, Arrays.toString(session), Arrays.toString(peerParams));
            }

            byte[] quicSession = toQuicheQuicSession(session, peerParams);
            if (quicSession != null) {
               logger.debug("save session host={}, port={}", engine.getSession().getPeerHost(), engine.getSession().getPeerPort());
               this.sessionCache
                  .saveSession(
                     engine.getSession().getPeerHost(),
                     engine.getSession().getPeerPort(),
                     TimeUnit.SECONDS.toMillis(creationTime),
                     TimeUnit.SECONDS.toMillis(timeout),
                     quicSession,
                     isSingleUse
                  );
            }
         }
      }
   }

   private static byte @Nullable [] toQuicheQuicSession(byte @Nullable [] sslSession, byte @Nullable [] peerParams) {
      if (sslSession != null && peerParams != null) {
         try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte[] var4;
            try {
               DataOutputStream dos = new DataOutputStream(bos);

               try {
                  dos.writeLong(sslSession.length);
                  dos.write(sslSession);
                  dos.writeLong(peerParams.length);
                  dos.write(peerParams);
                  var4 = bos.toByteArray();
               } catch (Throwable var8) {
                  try {
                     dos.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }

                  throw var8;
               }

               dos.close();
            } catch (Throwable var9) {
               try {
                  bos.close();
               } catch (Throwable var6) {
                  var9.addSuppressed(var6);
               }

               throw var9;
            }

            bos.close();
            return var4;
         } catch (IOException var10) {
            return null;
         }
      } else {
         return null;
      }
   }
}
