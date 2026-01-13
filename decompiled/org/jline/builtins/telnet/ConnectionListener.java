package org.jline.builtins.telnet;

public interface ConnectionListener {
   default void connectionIdle(ConnectionEvent ce) {
   }

   default void connectionTimedOut(ConnectionEvent ce) {
   }

   default void connectionLogoutRequest(ConnectionEvent ce) {
   }

   default void connectionSentBreak(ConnectionEvent ce) {
   }

   default void connectionTerminalGeometryChanged(ConnectionEvent ce) {
   }
}
