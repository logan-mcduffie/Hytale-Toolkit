package io.netty.handler.codec.quic;

import io.netty.util.internal.PlatformDependent;
import org.jetbrains.annotations.Nullable;

final class BoringSSLSessionTicketCallback {
   private volatile byte[][] sessionKeys;

   byte @Nullable [] findSessionTicket(byte @Nullable [] keyname) {
      byte[][] keys = this.sessionKeys;
      if (keys != null && keys.length != 0) {
         if (keyname == null) {
            return keys[0];
         } else {
            for (int i = 0; i < keys.length; i++) {
               byte[] key = keys[i];
               if (PlatformDependent.equals(keyname, 0, key, 1, keyname.length)) {
                  return key;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   void setSessionTicketKeys(SslSessionTicketKey @Nullable [] keys) {
      if (keys != null && keys.length != 0) {
         byte[][] sessionKeys = new byte[keys.length][];

         for (int i = 0; i < keys.length; i++) {
            SslSessionTicketKey key = keys[i];
            byte[] binaryKey = new byte[49];
            binaryKey[0] = (byte)(i == 0 ? 1 : 0);
            int dstCurPos = 1;
            System.arraycopy(key.name, 0, binaryKey, dstCurPos, 16);
            dstCurPos += 16;
            System.arraycopy(key.hmacKey, 0, binaryKey, dstCurPos, 16);
            dstCurPos += 16;
            System.arraycopy(key.aesKey, 0, binaryKey, dstCurPos, 16);
            sessionKeys[i] = binaryKey;
         }

         this.sessionKeys = sessionKeys;
      } else {
         this.sessionKeys = null;
      }
   }
}
