package com.google.protobuf;

import java.util.Arrays;

public final class TextFormatParseLocation {
   public static final TextFormatParseLocation EMPTY = new TextFormatParseLocation(-1, -1);
   private final int line;
   private final int column;

   static TextFormatParseLocation create(int line, int column) {
      if (line == -1 && column == -1) {
         return EMPTY;
      } else if (line >= 0 && column >= 0) {
         return new TextFormatParseLocation(line, column);
      } else {
         throw new IllegalArgumentException(String.format("line and column values must be >= 0: line %d, column: %d", line, column));
      }
   }

   private TextFormatParseLocation(int line, int column) {
      this.line = line;
      this.column = column;
   }

   public int getLine() {
      return this.line;
   }

   public int getColumn() {
      return this.column;
   }

   @Override
   public String toString() {
      return String.format("ParseLocation{line=%d, column=%d}", this.line, this.column);
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TextFormatParseLocation)) {
         return false;
      } else {
         TextFormatParseLocation that = (TextFormatParseLocation)o;
         return this.line == that.getLine() && this.column == that.getColumn();
      }
   }

   @Override
   public int hashCode() {
      int[] values = new int[]{this.line, this.column};
      return Arrays.hashCode(values);
   }
}
