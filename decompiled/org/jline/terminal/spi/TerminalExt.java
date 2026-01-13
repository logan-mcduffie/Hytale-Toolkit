package org.jline.terminal.spi;

import org.jline.terminal.Terminal;

public interface TerminalExt extends Terminal {
   TerminalProvider getProvider();

   SystemStream getSystemStream();
}
