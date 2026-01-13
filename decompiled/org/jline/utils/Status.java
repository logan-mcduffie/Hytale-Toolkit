package org.jline.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.AbstractTerminal;

public class Status {
   protected final Terminal terminal;
   protected final boolean supported;
   protected boolean suspended = false;
   protected AttributedString borderString;
   protected int border = 0;
   protected Display display;
   protected List<AttributedString> lines = Collections.emptyList();
   protected int scrollRegion;
   private final AttributedString ellipsis = new AttributedStringBuilder().append("…", AttributedStyle.INVERSE).toAttributedString();

   public static Status getStatus(Terminal terminal) {
      return getStatus(terminal, true);
   }

   public static Optional<Status> getExistingStatus(Terminal terminal) {
      return Optional.ofNullable(getStatus(terminal, false));
   }

   public static Status getStatus(Terminal terminal, boolean create) {
      return terminal instanceof AbstractTerminal ? ((AbstractTerminal)terminal).getStatus(create) : null;
   }

   public Status(Terminal terminal) {
      this.terminal = Objects.requireNonNull(terminal, "terminal can not be null");
      this.supported = terminal.getStringCapability(InfoCmp.Capability.change_scroll_region) != null
         && terminal.getStringCapability(InfoCmp.Capability.save_cursor) != null
         && terminal.getStringCapability(InfoCmp.Capability.restore_cursor) != null
         && terminal.getStringCapability(InfoCmp.Capability.cursor_address) != null
         && this.isValid(terminal.getSize());
      if (this.supported) {
         this.display = new Status.MovingCursorDisplay(terminal);
         this.resize();
         this.display.reset();
         this.scrollRegion = this.display.rows - 1;
      }
   }

   private boolean isValid(Size size) {
      return size.getRows() > 0 && size.getRows() < 1000 && size.getColumns() > 0 && size.getColumns() < 1000;
   }

   public void close() {
      if (this.supported) {
         this.terminal.puts(InfoCmp.Capability.save_cursor);
         this.terminal.puts(InfoCmp.Capability.change_scroll_region, 0, this.display.rows - 1);
         this.terminal.puts(InfoCmp.Capability.restore_cursor);
         this.terminal.flush();
      }
   }

   public void setBorder(boolean border) {
      this.border = border ? 1 : 0;
   }

   public void resize() {
      this.resize(this.terminal.getSize());
   }

   public void resize(Size size) {
      if (this.supported) {
         this.display.resize(size.getRows(), size.getColumns());
      }
   }

   public void reset() {
      if (this.supported) {
         this.display.reset();
         this.scrollRegion = this.display.rows;
         this.terminal.puts(InfoCmp.Capability.change_scroll_region, 0, this.scrollRegion);
      }
   }

   public void redraw() {
      if (!this.suspended) {
         this.update(this.lines);
      }
   }

   public void hide() {
      this.update(Collections.emptyList());
   }

   public void update(List<AttributedString> lines) {
      this.update(lines, true);
   }

   public void update(List<AttributedString> lines, boolean flush) {
      if (this.supported) {
         this.lines = new ArrayList<>(lines);
         if (!this.suspended) {
            List<AttributedString> var11 = new ArrayList<>(lines);
            int rows = this.display.rows;
            int columns = this.display.columns;
            if (this.border == 1 && !var11.isEmpty() && rows > 1) {
               var11.add(0, this.getBorderString(columns));
            }

            for (int i = 0; i < var11.size(); i++) {
               AttributedString str = (AttributedString)var11.get(i);
               if (str.columnLength() > columns) {
                  str = new AttributedStringBuilder(columns)
                     .append(((AttributedString)var11.get(i)).columnSubSequence(0, columns - this.ellipsis.columnLength()))
                     .append(this.ellipsis)
                     .toAttributedString();
               } else if (str.columnLength() < columns) {
                  str = new AttributedStringBuilder(columns).append(str).append(' ', columns - str.columnLength()).toAttributedString();
               }

               var11.set(i, str);
            }

            List<AttributedString> oldLines = this.display.oldLines;
            int newScrollRegion = this.display.rows - 1 - var11.size();
            if (newScrollRegion < this.scrollRegion) {
               this.terminal.puts(InfoCmp.Capability.save_cursor);
               this.terminal.puts(InfoCmp.Capability.cursor_address, this.scrollRegion, 0);

               for (int i = newScrollRegion; i < this.scrollRegion; i++) {
                  this.terminal.puts(InfoCmp.Capability.cursor_down);
               }

               this.terminal.puts(InfoCmp.Capability.change_scroll_region, 0, newScrollRegion);
               this.terminal.puts(InfoCmp.Capability.restore_cursor);

               for (int i = newScrollRegion; i < this.scrollRegion; i++) {
                  this.terminal.puts(InfoCmp.Capability.cursor_up);
               }

               this.scrollRegion = newScrollRegion;
            } else if (newScrollRegion > this.scrollRegion) {
               this.terminal.puts(InfoCmp.Capability.save_cursor);
               this.terminal.puts(InfoCmp.Capability.change_scroll_region, 0, newScrollRegion);
               this.terminal.puts(InfoCmp.Capability.restore_cursor);
               this.scrollRegion = newScrollRegion;
            }

            List<AttributedString> toDraw = new ArrayList<>(var11);
            int nbToDraw = toDraw.size();
            int nbOldLines = oldLines.size();
            if (nbOldLines > nbToDraw) {
               this.terminal.puts(InfoCmp.Capability.save_cursor);
               this.terminal.puts(InfoCmp.Capability.cursor_address, this.display.rows - nbOldLines, 0);

               for (int i = 0; i < nbOldLines - nbToDraw; i++) {
                  this.terminal.puts(InfoCmp.Capability.clr_eol);
                  if (i < nbOldLines - nbToDraw - 1) {
                     this.terminal.puts(InfoCmp.Capability.cursor_down);
                  }

                  oldLines.remove(0);
               }

               this.terminal.puts(InfoCmp.Capability.restore_cursor);
            }

            this.display.update(var11, -1, flush);
         }
      }
   }

   private AttributedString getBorderString(int columns) {
      if (this.borderString == null || this.borderString.length() != columns) {
         char borderChar = 9472;
         AttributedStringBuilder bb = new AttributedStringBuilder();

         for (int i = 0; i < columns; i++) {
            bb.append(borderChar);
         }

         this.borderString = bb.toAttributedString();
      }

      return this.borderString;
   }

   public void suspend() {
      if (!this.suspended) {
         this.suspended = true;
      }
   }

   public void restore() {
      if (this.suspended) {
         this.suspended = false;
         this.update(this.lines);
      }
   }

   public int size() {
      return this.size(this.lines);
   }

   private int size(List<?> lines) {
      int l = lines.size();
      return l > 0 ? l + this.border : 0;
   }

   @Override
   public String toString() {
      return "Status[supported=" + this.supported + ']';
   }

   static class MovingCursorDisplay extends Display {
      protected int firstLine;

      public MovingCursorDisplay(Terminal terminal) {
         super(terminal, false);
      }

      @Override
      public void update(List<AttributedString> newLines, int targetCursorPos, boolean flush) {
         this.cursorPos = -1;
         this.firstLine = this.rows - newLines.size();
         super.update(newLines, targetCursorPos, flush);
         if (this.cursorPos != -1) {
            this.terminal.puts(InfoCmp.Capability.restore_cursor);
            if (flush) {
               this.terminal.flush();
            }
         }
      }

      @Override
      protected void moveVisualCursorTo(int targetPos, List<AttributedString> newLines) {
         this.initCursor();
         super.moveVisualCursorTo(targetPos, newLines);
      }

      @Override
      protected int moveVisualCursorTo(int i1) {
         this.initCursor();
         return super.moveVisualCursorTo(i1);
      }

      void initCursor() {
         if (this.cursorPos == -1) {
            this.terminal.puts(InfoCmp.Capability.save_cursor);
            this.terminal.puts(InfoCmp.Capability.cursor_address, this.firstLine, 0);
            this.cursorPos = 0;
         }
      }
   }
}
