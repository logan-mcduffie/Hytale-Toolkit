package org.jline.utils;

import java.io.IOException;

public class ClosedException extends IOException {
   private static final long serialVersionUID = 3085420657077696L;

   public ClosedException() {
   }

   public ClosedException(String message) {
      super(message);
   }

   public ClosedException(String message, Throwable cause) {
      super(message, cause);
   }

   public ClosedException(Throwable cause) {
      super(cause);
   }
}
