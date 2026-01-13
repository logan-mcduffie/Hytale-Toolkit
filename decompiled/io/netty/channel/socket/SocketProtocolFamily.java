package io.netty.channel.socket;

import java.net.ProtocolFamily;
import java.net.StandardProtocolFamily;

public enum SocketProtocolFamily implements ProtocolFamily {
   INET,
   INET6,
   UNIX;

   public ProtocolFamily toJdkFamily() {
      switch (this) {
         case INET:
            return StandardProtocolFamily.INET;
         case INET6:
            return StandardProtocolFamily.INET6;
         case UNIX:
            return StandardProtocolFamily.valueOf("UNIX");
         default:
            throw new UnsupportedOperationException("ProtocolFamily cant be converted to something that is known by the JDKi: " + this);
      }
   }

   public static SocketProtocolFamily of(ProtocolFamily family) {
      if (family instanceof StandardProtocolFamily) {
         switch ((StandardProtocolFamily)family) {
            case INET:
               return INET;
            case INET6:
               return INET6;
            default:
               if (UNIX.name().equals(family.name())) {
                  return UNIX;
               }
         }
      } else if (family instanceof SocketProtocolFamily) {
         return (SocketProtocolFamily)family;
      }

      throw new UnsupportedOperationException("ProtocolFamily is not supported: " + family);
   }
}
