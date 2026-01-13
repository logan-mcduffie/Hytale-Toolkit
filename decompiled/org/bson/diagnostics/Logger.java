package org.bson.diagnostics;

public interface Logger {
   String getName();

   default boolean isTraceEnabled() {
      return false;
   }

   default void trace(String msg) {
   }

   default void trace(String msg, Throwable t) {
   }

   default boolean isDebugEnabled() {
      return false;
   }

   default void debug(String msg) {
   }

   default void debug(String msg, Throwable t) {
   }

   default boolean isInfoEnabled() {
      return false;
   }

   default void info(String msg) {
   }

   default void info(String msg, Throwable t) {
   }

   default boolean isWarnEnabled() {
      return false;
   }

   default void warn(String msg) {
   }

   default void warn(String msg, Throwable t) {
   }

   default boolean isErrorEnabled() {
      return false;
   }

   default void error(String msg) {
   }

   default void error(String msg, Throwable t) {
   }
}
