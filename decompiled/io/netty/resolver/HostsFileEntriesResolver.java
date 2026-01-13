package io.netty.resolver;

import java.net.InetAddress;

public interface HostsFileEntriesResolver {
   HostsFileEntriesResolver DEFAULT = new DefaultHostsFileEntriesResolver();

   InetAddress address(String var1, ResolvedAddressTypes var2);
}
