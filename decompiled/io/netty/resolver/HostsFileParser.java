package io.netty.resolver;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class HostsFileParser {
   public static HostsFileEntries parseSilently() {
      return hostsFileEntries(HostsFileEntriesProvider.parser().parseSilently());
   }

   public static HostsFileEntries parseSilently(Charset... charsets) {
      return hostsFileEntries(HostsFileEntriesProvider.parser().parseSilently(charsets));
   }

   public static HostsFileEntries parse() throws IOException {
      return hostsFileEntries(HostsFileEntriesProvider.parser().parse());
   }

   public static HostsFileEntries parse(File file) throws IOException {
      return hostsFileEntries(HostsFileEntriesProvider.parser().parse(file));
   }

   public static HostsFileEntries parse(File file, Charset... charsets) throws IOException {
      return hostsFileEntries(HostsFileEntriesProvider.parser().parse(file, charsets));
   }

   public static HostsFileEntries parse(Reader reader) throws IOException {
      return hostsFileEntries(HostsFileEntriesProvider.parser().parse(reader));
   }

   private HostsFileParser() {
   }

   private static HostsFileEntries hostsFileEntries(HostsFileEntriesProvider provider) {
      return provider == HostsFileEntriesProvider.EMPTY
         ? HostsFileEntries.EMPTY
         : new HostsFileEntries(
            (Map<String, Inet4Address>)toMapWithSingleValue(provider.ipv4Entries()), (Map<String, Inet6Address>)toMapWithSingleValue(provider.ipv6Entries())
         );
   }

   private static Map<String, ?> toMapWithSingleValue(Map<String, List<InetAddress>> fromMapWithListValue) {
      Map<String, InetAddress> result = new HashMap<>(fromMapWithListValue.size());

      for (Entry<String, List<InetAddress>> entry : fromMapWithListValue.entrySet()) {
         List<InetAddress> value = entry.getValue();
         if (!value.isEmpty()) {
            result.put(entry.getKey(), value.get(0));
         }
      }

      return result;
   }
}
