package io.netty.util.concurrent;

import java.lang.Thread.State;

public interface ThreadProperties {
   State state();

   int priority();

   boolean isInterrupted();

   boolean isDaemon();

   String name();

   long id();

   StackTraceElement[] stackTrace();

   boolean isAlive();
}
