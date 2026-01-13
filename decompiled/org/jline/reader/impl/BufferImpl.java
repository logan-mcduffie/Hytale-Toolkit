package org.jline.reader.impl;

import java.util.Arrays;
import java.util.Objects;
import org.jline.reader.Buffer;

public class BufferImpl implements Buffer {
   private int cursor = 0;
   private int cursorCol = -1;
   private int[] buffer;
   private int g0;
   private int g1;

   public BufferImpl() {
      this(64);
   }

   public BufferImpl(int size) {
      this.buffer = new int[size];
      this.g0 = 0;
      this.g1 = this.buffer.length;
   }

   private BufferImpl(BufferImpl buffer) {
      this.cursor = buffer.cursor;
      this.cursorCol = buffer.cursorCol;
      this.buffer = (int[])buffer.buffer.clone();
      this.g0 = buffer.g0;
      this.g1 = buffer.g1;
   }

   public BufferImpl copy() {
      return new BufferImpl(this);
   }

   @Override
   public int cursor() {
      return this.cursor;
   }

   @Override
   public int length() {
      return this.buffer.length - (this.g1 - this.g0);
   }

   @Override
   public boolean currChar(int ch) {
      if (this.cursor == this.length()) {
         return false;
      } else {
         this.buffer[this.adjust(this.cursor)] = ch;
         return true;
      }
   }

   @Override
   public int currChar() {
      return this.cursor == this.length() ? 0 : this.atChar(this.cursor);
   }

   @Override
   public int prevChar() {
      return this.cursor <= 0 ? 0 : this.atChar(this.cursor - 1);
   }

   @Override
   public int nextChar() {
      return this.cursor >= this.length() - 1 ? 0 : this.atChar(this.cursor + 1);
   }

   @Override
   public int atChar(int i) {
      return i >= 0 && i < this.length() ? this.buffer[this.adjust(i)] : 0;
   }

   private int adjust(int i) {
      return i >= this.g0 ? i + this.g1 - this.g0 : i;
   }

   @Override
   public void write(int c) {
      this.write(new int[]{c});
   }

   @Override
   public void write(int c, boolean overTyping) {
      if (overTyping) {
         this.delete(1);
      }

      this.write(new int[]{c});
   }

   @Override
   public void write(CharSequence str) {
      Objects.requireNonNull(str);
      this.write(str.codePoints().toArray());
   }

   @Override
   public void write(CharSequence str, boolean overTyping) {
      Objects.requireNonNull(str);
      int[] ucps = str.codePoints().toArray();
      if (overTyping) {
         this.delete(ucps.length);
      }

      this.write(ucps);
   }

   private void write(int[] ucps) {
      this.moveGapToCursor();
      int len = this.length() + ucps.length;
      int sz = this.buffer.length;
      if (sz < len) {
         while (sz < len) {
            sz *= 2;
         }

         int[] nb = new int[sz];
         System.arraycopy(this.buffer, 0, nb, 0, this.g0);
         System.arraycopy(this.buffer, this.g1, nb, this.g1 + sz - this.buffer.length, this.buffer.length - this.g1);
         this.g1 = this.g1 + (sz - this.buffer.length);
         this.buffer = nb;
      }

      System.arraycopy(ucps, 0, this.buffer, this.cursor, ucps.length);
      this.g0 += ucps.length;
      this.cursor += ucps.length;
      this.cursorCol = -1;
   }

   @Override
   public boolean clear() {
      if (this.length() == 0) {
         return false;
      } else {
         this.g0 = 0;
         this.g1 = this.buffer.length;
         this.cursor = 0;
         this.cursorCol = -1;
         return true;
      }
   }

   @Override
   public String substring(int start) {
      return this.substring(start, this.length());
   }

   @Override
   public String substring(int start, int end) {
      if (start >= end || start < 0 || end > this.length()) {
         return "";
      } else if (end <= this.g0) {
         return new String(this.buffer, start, end - start);
      } else if (start > this.g0) {
         return new String(this.buffer, this.g1 - this.g0 + start, end - start);
      } else {
         int[] b = (int[])this.buffer.clone();
         System.arraycopy(b, this.g1, b, this.g0, b.length - this.g1);
         return new String(b, start, end - start);
      }
   }

   @Override
   public String upToCursor() {
      return this.substring(0, this.cursor);
   }

   @Override
   public boolean cursor(int position) {
      return position == this.cursor ? true : this.move(position - this.cursor) != 0;
   }

   @Override
   public int move(int num) {
      int where = num;
      if (this.cursor == 0 && num <= 0) {
         return 0;
      } else if (this.cursor == this.length() && num >= 0) {
         return 0;
      } else {
         if (this.cursor + num < 0) {
            where = -this.cursor;
         } else if (this.cursor + num > this.length()) {
            where = this.length() - this.cursor;
         }

         this.cursor += where;
         this.cursorCol = -1;
         return where;
      }
   }

   @Override
   public boolean up() {
      int col = this.getCursorCol();
      int pnl = this.cursor - 1;

      while (pnl >= 0 && this.atChar(pnl) != 10) {
         pnl--;
      }

      if (pnl < 0) {
         return false;
      } else {
         int ppnl = pnl - 1;

         while (ppnl >= 0 && this.atChar(ppnl) != 10) {
            ppnl--;
         }

         this.cursor = Math.min(ppnl + col + 1, pnl);
         return true;
      }
   }

   @Override
   public boolean down() {
      int col = this.getCursorCol();
      int nnl = this.cursor;

      while (nnl < this.length() && this.atChar(nnl) != 10) {
         nnl++;
      }

      if (nnl >= this.length()) {
         return false;
      } else {
         int nnnl = nnl + 1;

         while (nnnl < this.length() && this.atChar(nnnl) != 10) {
            nnnl++;
         }

         this.cursor = Math.min(nnl + col + 1, nnnl);
         return true;
      }
   }

   @Override
   public boolean moveXY(int dx, int dy) {
      int col = 0;

      while (this.prevChar() != 10 && this.move(-1) == -1) {
         col++;
      }

      this.cursorCol = 0;

      while (dy < 0) {
         this.up();
         dy++;
      }

      while (dy > 0) {
         this.down();
         dy--;
      }

      col = Math.max(col + dx, 0);
      int i = 0;

      while (i < col && this.move(1) == 1 && this.currChar() != 10) {
         i++;
      }

      this.cursorCol = col;
      return true;
   }

   private int getCursorCol() {
      if (this.cursorCol < 0) {
         this.cursorCol = 0;
         int pnl = this.cursor - 1;

         while (pnl >= 0 && this.atChar(pnl) != 10) {
            pnl--;
         }

         this.cursorCol = this.cursor - pnl - 1;
      }

      return this.cursorCol;
   }

   @Override
   public int backspace(int num) {
      int count = Math.max(Math.min(this.cursor, num), 0);
      this.moveGapToCursor();
      this.cursor -= count;
      this.g0 -= count;
      this.cursorCol = -1;
      return count;
   }

   @Override
   public boolean backspace() {
      return this.backspace(1) == 1;
   }

   @Override
   public int delete(int num) {
      int count = Math.max(Math.min(this.length() - this.cursor, num), 0);
      this.moveGapToCursor();
      this.g1 += count;
      this.cursorCol = -1;
      return count;
   }

   @Override
   public boolean delete() {
      return this.delete(1) == 1;
   }

   @Override
   public String toString() {
      return this.substring(0, this.length());
   }

   @Override
   public void copyFrom(Buffer buf) {
      if (!(buf instanceof BufferImpl)) {
         throw new IllegalStateException();
      } else {
         BufferImpl that = (BufferImpl)buf;
         this.g0 = that.g0;
         this.g1 = that.g1;
         this.buffer = (int[])that.buffer.clone();
         this.cursor = that.cursor;
         this.cursorCol = that.cursorCol;
      }
   }

   private void moveGapToCursor() {
      if (this.cursor < this.g0) {
         int l = this.g0 - this.cursor;
         System.arraycopy(this.buffer, this.cursor, this.buffer, this.g1 - l, l);
         this.g0 -= l;
         this.g1 -= l;
      } else if (this.cursor > this.g0) {
         int l = this.cursor - this.g0;
         System.arraycopy(this.buffer, this.g1, this.buffer, this.g0, l);
         this.g0 += l;
         this.g1 += l;
      }
   }

   @Override
   public void zeroOut() {
      Arrays.fill(this.buffer, 0);
   }
}
