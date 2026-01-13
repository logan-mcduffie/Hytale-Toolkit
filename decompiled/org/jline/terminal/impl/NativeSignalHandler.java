package org.jline.terminal.impl;

import org.jline.terminal.Terminal;

public final class NativeSignalHandler implements Terminal.SignalHandler {
   public static final NativeSignalHandler SIG_DFL = new NativeSignalHandler();
   public static final NativeSignalHandler SIG_IGN = new NativeSignalHandler();

   private NativeSignalHandler() {
   }

   @Override
   public void handle(Terminal.Signal signal) {
      throw new UnsupportedOperationException();
   }
}
