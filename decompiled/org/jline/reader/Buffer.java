package org.jline.reader;

public interface Buffer {
   int cursor();

   int atChar(int var1);

   int length();

   int currChar();

   int prevChar();

   int nextChar();

   boolean cursor(int var1);

   int move(int var1);

   boolean up();

   boolean down();

   boolean moveXY(int var1, int var2);

   boolean clear();

   boolean currChar(int var1);

   void write(int var1);

   void write(int var1, boolean var2);

   void write(CharSequence var1);

   void write(CharSequence var1, boolean var2);

   boolean backspace();

   int backspace(int var1);

   boolean delete();

   int delete(int var1);

   String substring(int var1);

   String substring(int var1, int var2);

   String upToCursor();

   @Override
   String toString();

   Buffer copy();

   void copyFrom(Buffer var1);

   void zeroOut();
}
