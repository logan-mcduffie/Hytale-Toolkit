package org.bson.codecs.configuration;

public class CodecConfigurationException extends RuntimeException {
   private static final long serialVersionUID = -5656763889202800056L;

   public CodecConfigurationException(String msg) {
      super(msg);
   }

   public CodecConfigurationException(String message, Throwable cause) {
      super(message, cause);
   }
}
