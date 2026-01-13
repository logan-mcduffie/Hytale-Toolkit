package org.jline.terminal;

public class Cursor {
   private final int x;
   private final int y;

   public Cursor(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof Cursor)) {
         return false;
      } else {
         Cursor c = (Cursor)o;
         return this.x == c.x && this.y == c.y;
      }
   }

   @Override
   public int hashCode() {
      return this.x * 31 + this.y;
   }

   @Override
   public String toString() {
      return "Cursor[x=" + this.x + ", y=" + this.y + ']';
   }
}
