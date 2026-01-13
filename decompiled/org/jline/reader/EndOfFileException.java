package org.jline.reader;

public class EndOfFileException extends RuntimeException {
   private static final long serialVersionUID = 528485360925144689L;
   private String partialLine;

   public EndOfFileException() {
   }

   public EndOfFileException(String message) {
      super(message);
   }

   public EndOfFileException(String message, Throwable cause) {
      super(message, cause);
   }

   public EndOfFileException(Throwable cause) {
      super(cause);
   }

   public EndOfFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   public EndOfFileException partialLine(String partialLine) {
      this.partialLine = partialLine;
      return this;
   }

   public String getPartialLine() {
      return this.partialLine;
   }
}
