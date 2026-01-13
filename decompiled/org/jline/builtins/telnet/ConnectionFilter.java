package org.jline.builtins.telnet;

import java.net.InetAddress;

public interface ConnectionFilter {
   boolean isAllowed(InetAddress var1);
}
