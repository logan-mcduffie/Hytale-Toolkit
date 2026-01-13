package io.netty.channel.epoll;

import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

final class TcpMd5Util {
   static Collection<InetAddress> newTcpMd5Sigs(AbstractEpollChannel channel, Collection<InetAddress> current, Map<InetAddress, byte[]> newKeys) throws IOException {
      ObjectUtil.checkNotNull(channel, "channel");
      ObjectUtil.checkNotNull(current, "current");
      ObjectUtil.checkNotNull(newKeys, "newKeys");

      for (Entry<InetAddress, byte[]> e : newKeys.entrySet()) {
         byte[] key = e.getValue();
         ObjectUtil.checkNotNullWithIAE(e.getKey(), "e.getKey");
         ObjectUtil.checkNonEmpty(key, e.getKey().toString());
         if (key.length > Native.TCP_MD5SIG_MAXKEYLEN) {
            throw new IllegalArgumentException(
               "newKeys[" + e.getKey() + "] has a key with invalid length; should not exceed the maximum length (" + Native.TCP_MD5SIG_MAXKEYLEN + ')'
            );
         }
      }

      for (InetAddress addr : current) {
         if (!newKeys.containsKey(addr)) {
            channel.socket.setTcpMd5Sig(addr, null);
         }
      }

      if (newKeys.isEmpty()) {
         return Collections.emptySet();
      } else {
         Collection<InetAddress> addresses = new ArrayList<>(newKeys.size());

         for (Entry<InetAddress, byte[]> ex : newKeys.entrySet()) {
            channel.socket.setTcpMd5Sig(ex.getKey(), ex.getValue());
            addresses.add(ex.getKey());
         }

         return addresses;
      }
   }

   private TcpMd5Util() {
   }
}
