package org.jline.terminal;

public class Size {
   private int rows;
   private int cols;

   public Size() {
   }

   public Size(int columns, int rows) {
      this();
      this.setColumns(columns);
      this.setRows(rows);
   }

   public int getColumns() {
      return this.cols;
   }

   public void setColumns(int columns) {
      this.cols = (short)columns;
   }

   public int getRows() {
      return this.rows;
   }

   public void setRows(int rows) {
      this.rows = (short)rows;
   }

   public int cursorPos(int row, int col) {
      return row * (this.cols + 1) + col;
   }

   public void copy(Size size) {
      this.setColumns(size.getColumns());
      this.setRows(size.getRows());
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof Size)) {
         return false;
      } else {
         Size size = (Size)o;
         return this.rows == size.rows && this.cols == size.cols;
      }
   }

   @Override
   public int hashCode() {
      return this.rows * 31 + this.cols;
   }

   @Override
   public String toString() {
      return "Size[cols=" + this.cols + ", rows=" + this.rows + ']';
   }
}
