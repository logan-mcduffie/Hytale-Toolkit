package org.jline.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jline.terminal.Terminal;

public class Display {
   protected final Terminal terminal;
   protected final boolean fullScreen;
   protected List<AttributedString> oldLines = Collections.emptyList();
   protected int cursorPos;
   protected int columns;
   protected int columns1;
   protected int rows;
   protected boolean reset;
   protected boolean delayLineWrap;
   protected final Map<InfoCmp.Capability, Integer> cost = new HashMap<>();
   protected final boolean canScroll;
   protected final boolean wrapAtEol;
   protected final boolean delayedWrapAtEol;
   protected final boolean cursorDownIsNewLine;

   public Display(Terminal terminal, boolean fullscreen) {
      this.terminal = terminal;
      this.fullScreen = fullscreen;
      this.canScroll = this.can(InfoCmp.Capability.insert_line, InfoCmp.Capability.parm_insert_line)
         && this.can(InfoCmp.Capability.delete_line, InfoCmp.Capability.parm_delete_line);
      this.wrapAtEol = terminal.getBooleanCapability(InfoCmp.Capability.auto_right_margin);
      this.delayedWrapAtEol = this.wrapAtEol && terminal.getBooleanCapability(InfoCmp.Capability.eat_newline_glitch);
      this.cursorDownIsNewLine = "\n".equals(Curses.tputs(terminal.getStringCapability(InfoCmp.Capability.cursor_down)));
   }

   public boolean delayLineWrap() {
      return this.delayLineWrap;
   }

   public void setDelayLineWrap(boolean v) {
      this.delayLineWrap = v;
   }

   public void resize(int rows, int columns) {
      if (rows == 0 || columns == 0) {
         columns = 2147483646;
         rows = 1;
      }

      if (this.rows != rows || this.columns != columns) {
         this.rows = rows;
         this.columns = columns;
         this.columns1 = columns + 1;
         this.oldLines = AttributedString.join(AttributedString.EMPTY, this.oldLines).columnSplitLength(columns, true, this.delayLineWrap());
      }
   }

   public void reset() {
      this.oldLines = Collections.emptyList();
   }

   public void clear() {
      if (this.fullScreen) {
         this.reset = true;
      }
   }

   public void updateAnsi(List<String> newLines, int targetCursorPos) {
      this.update(newLines.stream().map(AttributedString::fromAnsi).collect(Collectors.toList()), targetCursorPos);
   }

   public void update(List<AttributedString> newLines, int targetCursorPos) {
      this.update(newLines, targetCursorPos, true);
   }

   public void update(List<AttributedString> newLines, int targetCursorPos, boolean flush) {
      if (this.reset) {
         this.terminal.puts(InfoCmp.Capability.clear_screen);
         this.oldLines.clear();
         this.cursorPos = 0;
         this.reset = false;
      }

      Integer cols = this.terminal.getNumericCapability(InfoCmp.Capability.max_colors);
      if (cols == null || cols < 8) {
         newLines = newLines.stream().map(s -> new AttributedString(s.toString())).collect(Collectors.toList());
      }

      if ((this.fullScreen || newLines.size() >= this.rows) && newLines.size() == this.oldLines.size() && this.canScroll) {
         int nbHeaders = 0;
         int nbFooters = 0;
         int l = newLines.size();

         while (nbHeaders < l && Objects.equals(newLines.get(nbHeaders), this.oldLines.get(nbHeaders))) {
            nbHeaders++;
         }

         while (
            nbFooters < l - nbHeaders - 1
               && Objects.equals(newLines.get(newLines.size() - nbFooters - 1), this.oldLines.get(this.oldLines.size() - nbFooters - 1))
         ) {
            nbFooters++;
         }

         List<AttributedString> o1 = newLines.subList(nbHeaders, newLines.size() - nbFooters);
         List<AttributedString> o2 = this.oldLines.subList(nbHeaders, this.oldLines.size() - nbFooters);
         int[] common = longestCommon(o1, o2);
         if (common != null) {
            int s1 = common[0];
            int s2 = common[1];
            int sl = common[2];
            if (sl > 1 && s1 < s2) {
               this.moveVisualCursorTo((nbHeaders + s1) * this.columns1);
               int nb = s2 - s1;
               this.deleteLines(nb);

               for (int i = 0; i < nb; i++) {
                  this.oldLines.remove(nbHeaders + s1);
               }

               if (nbFooters > 0) {
                  this.moveVisualCursorTo((nbHeaders + s1 + sl) * this.columns1);
                  this.insertLines(nb);

                  for (int i = 0; i < nb; i++) {
                     this.oldLines.add(nbHeaders + s1 + sl, new AttributedString(""));
                  }
               }
            } else if (sl > 1 && s1 > s2) {
               int nb = s1 - s2;
               if (nbFooters > 0) {
                  this.moveVisualCursorTo((nbHeaders + s2 + sl) * this.columns1);
                  this.deleteLines(nb);

                  for (int i = 0; i < nb; i++) {
                     this.oldLines.remove(nbHeaders + s2 + sl);
                  }
               }

               this.moveVisualCursorTo((nbHeaders + s2) * this.columns1);
               this.insertLines(nb);

               for (int i = 0; i < nb; i++) {
                  this.oldLines.add(nbHeaders + s2, new AttributedString(""));
               }
            }
         }
      }

      int lineIndex = 0;
      int currentPos = 0;
      int numLines = Math.min(this.rows, Math.max(this.oldLines.size(), newLines.size()));
      boolean wrapNeeded = false;

      while (lineIndex < numLines) {
         AttributedString oldLine = lineIndex < this.oldLines.size() ? this.oldLines.get(lineIndex) : AttributedString.EMPTY;
         AttributedString newLine = lineIndex < newLines.size() ? newLines.get(lineIndex) : AttributedString.EMPTY;
         currentPos = lineIndex * this.columns1;
         int curCol = currentPos;
         int oldLength = oldLine.length();
         int newLength = newLine.length();
         boolean oldNL = oldLength > 0 && oldLine.charAt(oldLength - 1) == '\n';
         boolean newNL = newLength > 0 && newLine.charAt(newLength - 1) == '\n';
         if (oldNL) {
            oldLine = oldLine.substring(0, --oldLength);
         }

         if (newNL) {
            newLine = newLine.substring(0, --newLength);
         }

         if (wrapNeeded && lineIndex == (this.cursorPos + 1) / this.columns1 && lineIndex < newLines.size()) {
            this.cursorPos++;
            if (newLength != 0 && !newLine.isHidden(0)) {
               AttributedString firstChar = newLine.substring(0, 1);
               this.rawPrint(firstChar);
               this.cursorPos = this.cursorPos + firstChar.columnLength();
               newLine = newLine.substring(1, newLength);
               newLength--;
               if (oldLength > 0) {
                  oldLine = oldLine.substring(1, oldLength);
                  oldLength--;
               }

               currentPos = this.cursorPos;
            } else {
               this.rawPrint(32);
               this.terminal.puts(InfoCmp.Capability.cursor_left);
            }
         }

         List<DiffHelper.Diff> diffs = DiffHelper.diff(oldLine, newLine);
         boolean ident = true;
         boolean cleared = false;

         for (int i = 0; i < diffs.size(); i++) {
            DiffHelper.Diff diff = diffs.get(i);
            int width = diff.text.columnLength();
            switch (diff.operation) {
               case EQUAL:
                  if (!ident) {
                     this.cursorPos = this.moveVisualCursorTo(currentPos);
                     this.rawPrint(diff.text);
                     this.cursorPos += width;
                     currentPos = this.cursorPos;
                  } else {
                     currentPos += width;
                  }
                  break;
               case INSERT:
                  if (i <= diffs.size() - 2 && diffs.get(i + 1).operation == DiffHelper.Operation.EQUAL) {
                     this.cursorPos = this.moveVisualCursorTo(currentPos);
                     if (this.insertChars(width)) {
                        this.rawPrint(diff.text);
                        this.cursorPos += width;
                        currentPos = this.cursorPos;
                        break;
                     }
                  } else if (i <= diffs.size() - 2
                     && diffs.get(i + 1).operation == DiffHelper.Operation.DELETE
                     && width == diffs.get(i + 1).text.columnLength()) {
                     this.moveVisualCursorTo(currentPos);
                     this.rawPrint(diff.text);
                     this.cursorPos += width;
                     currentPos = this.cursorPos;
                     i++;
                     break;
                  }

                  this.moveVisualCursorTo(currentPos);
                  this.rawPrint(diff.text);
                  this.cursorPos += width;
                  currentPos = this.cursorPos;
                  ident = false;
                  break;
               case DELETE:
                  if (!cleared && currentPos - curCol < this.columns) {
                     if (i <= diffs.size() - 2
                        && diffs.get(i + 1).operation == DiffHelper.Operation.EQUAL
                        && currentPos + diffs.get(i + 1).text.columnLength() < this.columns) {
                        this.moveVisualCursorTo(currentPos);
                        if (this.deleteChars(width)) {
                           continue;
                        }
                     }

                     int oldLen = oldLine.columnLength();
                     int newLen = newLine.columnLength();
                     int nb = Math.max(oldLen, newLen) - (currentPos - curCol);
                     this.moveVisualCursorTo(currentPos);
                     if (!this.terminal.puts(InfoCmp.Capability.clr_eol)) {
                        this.rawPrint(' ', nb);
                        this.cursorPos += nb;
                     }

                     cleared = true;
                     ident = false;
                  }
            }
         }

         lineIndex++;
         boolean newWrap = !newNL && lineIndex < newLines.size();
         if (targetCursorPos + 1 == lineIndex * this.columns1 && (newWrap || !this.delayLineWrap)) {
            targetCursorPos++;
         }

         boolean atRight = (this.cursorPos - curCol) % this.columns1 == this.columns;
         wrapNeeded = false;
         if (this.delayedWrapAtEol) {
            boolean oldWrap = !oldNL && lineIndex < this.oldLines.size();
            if (newWrap != oldWrap && (!oldWrap || !cleared)) {
               this.moveVisualCursorTo(lineIndex * this.columns1 - 1, newLines);
               if (newWrap) {
                  wrapNeeded = true;
               } else {
                  this.terminal.puts(InfoCmp.Capability.clr_eol);
               }
            }
         } else if (atRight) {
            if (this.wrapAtEol) {
               if (!this.fullScreen || this.fullScreen && lineIndex < numLines) {
                  this.rawPrint(32);
                  this.terminal.puts(InfoCmp.Capability.cursor_left);
                  this.cursorPos++;
               }
            } else {
               this.terminal.puts(InfoCmp.Capability.carriage_return);
               this.cursorPos = curCol;
            }

            currentPos = this.cursorPos;
         }
      }

      if (this.cursorPos != targetCursorPos) {
         this.moveVisualCursorTo(targetCursorPos < 0 ? currentPos : targetCursorPos, newLines);
      }

      this.oldLines = newLines;
      if (flush) {
         this.terminal.flush();
      }
   }

   protected boolean deleteLines(int nb) {
      return this.perform(InfoCmp.Capability.delete_line, InfoCmp.Capability.parm_delete_line, nb);
   }

   protected boolean insertLines(int nb) {
      return this.perform(InfoCmp.Capability.insert_line, InfoCmp.Capability.parm_insert_line, nb);
   }

   protected boolean insertChars(int nb) {
      return this.perform(InfoCmp.Capability.insert_character, InfoCmp.Capability.parm_ich, nb);
   }

   protected boolean deleteChars(int nb) {
      return this.perform(InfoCmp.Capability.delete_character, InfoCmp.Capability.parm_dch, nb);
   }

   protected boolean can(InfoCmp.Capability single, InfoCmp.Capability multi) {
      return this.terminal.getStringCapability(single) != null || this.terminal.getStringCapability(multi) != null;
   }

   protected boolean perform(InfoCmp.Capability single, InfoCmp.Capability multi, int nb) {
      boolean hasMulti = this.terminal.getStringCapability(multi) != null;
      boolean hasSingle = this.terminal.getStringCapability(single) != null;
      if (!hasMulti || hasSingle && this.cost(single) * nb <= this.cost(multi)) {
         if (!hasSingle) {
            return false;
         } else {
            for (int i = 0; i < nb; i++) {
               this.terminal.puts(single);
            }

            return true;
         }
      } else {
         this.terminal.puts(multi, nb);
         return true;
      }
   }

   private int cost(InfoCmp.Capability cap) {
      return this.cost.computeIfAbsent(cap, this::computeCost);
   }

   private int computeCost(InfoCmp.Capability cap) {
      String s = Curses.tputs(this.terminal.getStringCapability(cap), 0);
      return s != null ? s.length() : Integer.MAX_VALUE;
   }

   private static int[] longestCommon(List<AttributedString> l1, List<AttributedString> l2) {
      int start1 = 0;
      int start2 = 0;
      int max = 0;

      for (int i = 0; i < l1.size(); i++) {
         for (int j = 0; j < l2.size(); j++) {
            int x = 0;

            while (Objects.equals(l1.get(i + x), l2.get(j + x))) {
               x++;
               if (i + x >= l1.size() || j + x >= l2.size()) {
                  break;
               }
            }

            if (x > max) {
               max = x;
               start1 = i;
               start2 = j;
            }
         }
      }

      return max != 0 ? new int[]{start1, start2, max} : null;
   }

   protected void moveVisualCursorTo(int targetPos, List<AttributedString> newLines) {
      if (this.cursorPos != targetPos) {
         boolean atRight = targetPos % this.columns1 == this.columns;
         this.moveVisualCursorTo(targetPos - (atRight ? 1 : 0));
         if (atRight) {
            int row = targetPos / this.columns1;
            AttributedString lastChar = row >= newLines.size() ? AttributedString.EMPTY : newLines.get(row).columnSubSequence(this.columns - 1, this.columns);
            if (lastChar.length() == 0) {
               this.rawPrint(32);
            } else {
               this.rawPrint(lastChar);
            }

            this.cursorPos++;
         }
      }
   }

   protected int moveVisualCursorTo(int i1) {
      int i0 = this.cursorPos;
      if (i0 == i1) {
         return i1;
      } else {
         int width = this.columns1;
         int l0 = i0 / width;
         int c0 = i0 % width;
         int l1 = i1 / width;
         int c1 = i1 % width;
         if (c0 == this.columns) {
            this.terminal.puts(InfoCmp.Capability.carriage_return);
            c0 = 0;
         }

         if (l0 > l1) {
            this.perform(InfoCmp.Capability.cursor_up, InfoCmp.Capability.parm_up_cursor, l0 - l1);
         } else if (l0 < l1) {
            if (this.fullScreen) {
               if (!this.terminal.puts(InfoCmp.Capability.parm_down_cursor, l1 - l0)) {
                  for (int i = l0; i < l1; i++) {
                     this.terminal.puts(InfoCmp.Capability.cursor_down);
                  }

                  if (this.cursorDownIsNewLine) {
                     c0 = 0;
                  }
               }
            } else {
               this.terminal.puts(InfoCmp.Capability.carriage_return);
               this.rawPrint('\n', l1 - l0);
               c0 = 0;
            }
         }

         if (c0 != 0 && c1 == 0) {
            this.terminal.puts(InfoCmp.Capability.carriage_return);
         } else if (c0 < c1) {
            this.perform(InfoCmp.Capability.cursor_right, InfoCmp.Capability.parm_right_cursor, c1 - c0);
         } else if (c0 > c1) {
            this.perform(InfoCmp.Capability.cursor_left, InfoCmp.Capability.parm_left_cursor, c0 - c1);
         }

         this.cursorPos = i1;
         return i1;
      }
   }

   void rawPrint(char c, int num) {
      for (int i = 0; i < num; i++) {
         this.rawPrint(c);
      }
   }

   void rawPrint(int c) {
      this.terminal.writer().write(c);
   }

   void rawPrint(AttributedString str) {
      str.print(this.terminal);
   }

   public int wcwidth(String str) {
      return str != null ? AttributedString.fromAnsi(str).columnLength() : 0;
   }
}
