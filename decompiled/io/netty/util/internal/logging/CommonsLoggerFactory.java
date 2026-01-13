package io.netty.util.internal.logging;

import org.apache.commons.logging.LogFactory;

@Deprecated
public class CommonsLoggerFactory extends InternalLoggerFactory {
   public static final InternalLoggerFactory INSTANCE = new CommonsLoggerFactory();

   @Override
   public InternalLogger newInstance(String name) {
      return new CommonsLogger(LogFactory.getLog(name), name);
   }
}
