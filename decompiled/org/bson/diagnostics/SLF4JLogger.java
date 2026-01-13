package org.bson.diagnostics;

import org.slf4j.LoggerFactory;

class SLF4JLogger implements Logger {
   private final org.slf4j.Logger delegate;

   SLF4JLogger(String name) {
      this.delegate = LoggerFactory.getLogger(name);
   }

   @Override
   public String getName() {
      return this.delegate.getName();
   }

   @Override
   public boolean isTraceEnabled() {
      return this.delegate.isTraceEnabled();
   }

   @Override
   public void trace(String msg) {
      this.delegate.trace(msg);
   }

   @Override
   public void trace(String msg, Throwable t) {
      this.delegate.trace(msg, t);
   }

   @Override
   public boolean isDebugEnabled() {
      return this.delegate.isDebugEnabled();
   }

   @Override
   public void debug(String msg) {
      this.delegate.debug(msg);
   }

   @Override
   public void debug(String msg, Throwable t) {
      this.delegate.debug(msg, t);
   }

   @Override
   public boolean isInfoEnabled() {
      return this.delegate.isInfoEnabled();
   }

   @Override
   public void info(String msg) {
      this.delegate.info(msg);
   }

   @Override
   public void info(String msg, Throwable t) {
      this.delegate.info(msg, t);
   }

   @Override
   public boolean isWarnEnabled() {
      return this.delegate.isWarnEnabled();
   }

   @Override
   public void warn(String msg) {
      this.delegate.warn(msg);
   }

   @Override
   public void warn(String msg, Throwable t) {
      this.delegate.warn(msg, t);
   }

   @Override
   public boolean isErrorEnabled() {
      return this.delegate.isErrorEnabled();
   }

   @Override
   public void error(String msg) {
      this.delegate.error(msg);
   }

   @Override
   public void error(String msg, Throwable t) {
      this.delegate.error(msg, t);
   }
}
