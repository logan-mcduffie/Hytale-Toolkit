package org.jline.reader;

public class SyntaxError extends RuntimeException {
   private static final long serialVersionUID = 1L;
   private final int line;
   private final int column;

   public SyntaxError(int line, int column, String message) {
      super(message);
      this.line = line;
      this.column = column;
   }

   public int column() {
      return this.column;
   }

   public int line() {
      return this.line;
   }
}
