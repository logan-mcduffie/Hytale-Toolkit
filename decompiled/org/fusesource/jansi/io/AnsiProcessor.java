package org.fusesource.jansi.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class AnsiProcessor {
   protected final OutputStream os;
   protected static final int ERASE_SCREEN_TO_END = 0;
   protected static final int ERASE_SCREEN_TO_BEGINING = 1;
   protected static final int ERASE_SCREEN = 2;
   protected static final int ERASE_LINE_TO_END = 0;
   protected static final int ERASE_LINE_TO_BEGINING = 1;
   protected static final int ERASE_LINE = 2;
   protected static final int ATTRIBUTE_INTENSITY_BOLD = 1;
   protected static final int ATTRIBUTE_INTENSITY_FAINT = 2;
   protected static final int ATTRIBUTE_ITALIC = 3;
   protected static final int ATTRIBUTE_UNDERLINE = 4;
   protected static final int ATTRIBUTE_BLINK_SLOW = 5;
   protected static final int ATTRIBUTE_BLINK_FAST = 6;
   protected static final int ATTRIBUTE_NEGATIVE_ON = 7;
   protected static final int ATTRIBUTE_CONCEAL_ON = 8;
   protected static final int ATTRIBUTE_UNDERLINE_DOUBLE = 21;
   protected static final int ATTRIBUTE_INTENSITY_NORMAL = 22;
   protected static final int ATTRIBUTE_UNDERLINE_OFF = 24;
   protected static final int ATTRIBUTE_BLINK_OFF = 25;
   protected static final int ATTRIBUTE_NEGATIVE_OFF = 27;
   protected static final int ATTRIBUTE_CONCEAL_OFF = 28;
   protected static final int BLACK = 0;
   protected static final int RED = 1;
   protected static final int GREEN = 2;
   protected static final int YELLOW = 3;
   protected static final int BLUE = 4;
   protected static final int MAGENTA = 5;
   protected static final int CYAN = 6;
   protected static final int WHITE = 7;

   public AnsiProcessor(OutputStream os) {
      this.os = os;
   }

   protected int getNextOptionInt(Iterator<Object> optionsIterator) throws IOException {
      while (optionsIterator.hasNext()) {
         Object arg = optionsIterator.next();
         if (arg != null) {
            return (Integer)arg;
         }
      }

      throw new IllegalArgumentException();
   }

   protected boolean processEscapeCommand(ArrayList<Object> options, int command) throws IOException {
      try {
         switch (command) {
            case 65:
               this.processCursorUp(this.optionInt(options, 0, 1));
               return true;
            case 66:
               this.processCursorDown(this.optionInt(options, 0, 1));
               return true;
            case 67:
               this.processCursorRight(this.optionInt(options, 0, 1));
               return true;
            case 68:
               this.processCursorLeft(this.optionInt(options, 0, 1));
               return true;
            case 69:
               this.processCursorDownLine(this.optionInt(options, 0, 1));
               return true;
            case 70:
               this.processCursorUpLine(this.optionInt(options, 0, 1));
               return true;
            case 71:
               this.processCursorToColumn(this.optionInt(options, 0));
               return true;
            case 72:
            case 102:
               this.processCursorTo(this.optionInt(options, 0, 1), this.optionInt(options, 1, 1));
               return true;
            case 73:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 116:
            default:
               if (97 <= command && command <= 122) {
                  this.processUnknownExtension(options, command);
                  return true;
               } else {
                  if (65 <= command && command <= 90) {
                     this.processUnknownExtension(options, command);
                     return true;
                  }

                  return false;
               }
            case 74:
               this.processEraseScreen(this.optionInt(options, 0, 0));
               return true;
            case 75:
               this.processEraseLine(this.optionInt(options, 0, 0));
               return true;
            case 76:
               this.processInsertLine(this.optionInt(options, 0, 1));
               return true;
            case 77:
               this.processDeleteLine(this.optionInt(options, 0, 1));
               return true;
            case 83:
               this.processScrollUp(this.optionInt(options, 0, 1));
               return true;
            case 84:
               this.processScrollDown(this.optionInt(options, 0, 1));
               return true;
            case 109:
               for (Object next : options) {
                  if (next != null && next.getClass() != Integer.class) {
                     throw new IllegalArgumentException();
                  }
               }

               int count = 0;
               Iterator<Object> optionsIterator = options.iterator();

               while (optionsIterator.hasNext()) {
                  Object nextx = optionsIterator.next();
                  if (nextx != null) {
                     count++;
                     int value = (Integer)nextx;
                     if (30 <= value && value <= 37) {
                        this.processSetForegroundColor(value - 30);
                     } else if (40 <= value && value <= 47) {
                        this.processSetBackgroundColor(value - 40);
                     } else if (90 <= value && value <= 97) {
                        this.processSetForegroundColor(value - 90, true);
                     } else if (100 <= value && value <= 107) {
                        this.processSetBackgroundColor(value - 100, true);
                     } else if (value != 38 && value != 48) {
                        switch (value) {
                           case 0:
                              this.processAttributeReset();
                              break;
                           case 39:
                              this.processDefaultTextColor();
                              break;
                           case 49:
                              this.processDefaultBackgroundColor();
                              break;
                           default:
                              this.processSetAttribute(value);
                        }
                     } else if (optionsIterator.hasNext()) {
                        int arg2or5 = this.getNextOptionInt(optionsIterator);
                        if (arg2or5 == 2) {
                           int r = this.getNextOptionInt(optionsIterator);
                           int g = this.getNextOptionInt(optionsIterator);
                           int b = this.getNextOptionInt(optionsIterator);
                           if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
                              throw new IllegalArgumentException();
                           }

                           if (value == 38) {
                              this.processSetForegroundColorExt(r, g, b);
                           } else {
                              this.processSetBackgroundColorExt(r, g, b);
                           }
                        } else {
                           if (arg2or5 != 5) {
                              throw new IllegalArgumentException();
                           }

                           int paletteIndex = this.getNextOptionInt(optionsIterator);
                           if (paletteIndex < 0 || paletteIndex > 255) {
                              throw new IllegalArgumentException();
                           }

                           if (value == 38) {
                              this.processSetForegroundColorExt(paletteIndex);
                           } else {
                              this.processSetBackgroundColorExt(paletteIndex);
                           }
                        }
                     }
                  }
               }

               if (count == 0) {
                  this.processAttributeReset();
               }

               return true;
            case 115:
               this.processSaveCursorPosition();
               return true;
            case 117:
               this.processRestoreCursorPosition();
               return true;
         }
      } catch (IllegalArgumentException var11) {
         return false;
      }
   }

   protected boolean processOperatingSystemCommand(ArrayList<Object> options) {
      int command = this.optionInt(options, 0);
      String label = (String)options.get(1);

      try {
         switch (command) {
            case 0:
               this.processChangeIconNameAndWindowTitle(label);
               return true;
            case 1:
               this.processChangeIconName(label);
               return true;
            case 2:
               this.processChangeWindowTitle(label);
               return true;
            default:
               this.processUnknownOperatingSystemCommand(command, label);
               return true;
         }
      } catch (IllegalArgumentException var5) {
         return false;
      }
   }

   protected boolean processCharsetSelect(ArrayList<Object> options) {
      int set = this.optionInt(options, 0);
      char seq = (Character)options.get(1);
      this.processCharsetSelect(set, seq);
      return true;
   }

   private int optionInt(ArrayList<Object> options, int index) {
      if (options.size() <= index) {
         throw new IllegalArgumentException();
      } else {
         Object value = options.get(index);
         if (value == null) {
            throw new IllegalArgumentException();
         } else if (!value.getClass().equals(Integer.class)) {
            throw new IllegalArgumentException();
         } else {
            return (Integer)value;
         }
      }
   }

   private int optionInt(ArrayList<Object> options, int index, int defaultValue) {
      if (options.size() > index) {
         Object value = options.get(index);
         return value == null ? defaultValue : (Integer)value;
      } else {
         return defaultValue;
      }
   }

   protected void processRestoreCursorPosition() throws IOException {
   }

   protected void processSaveCursorPosition() throws IOException {
   }

   protected void processInsertLine(int optionInt) throws IOException {
   }

   protected void processDeleteLine(int optionInt) throws IOException {
   }

   protected void processScrollDown(int optionInt) throws IOException {
   }

   protected void processScrollUp(int optionInt) throws IOException {
   }

   protected void processEraseScreen(int eraseOption) throws IOException {
   }

   protected void processEraseLine(int eraseOption) throws IOException {
   }

   protected void processSetAttribute(int attribute) throws IOException {
   }

   protected void processSetForegroundColor(int color) throws IOException {
      this.processSetForegroundColor(color, false);
   }

   protected void processSetForegroundColor(int color, boolean bright) throws IOException {
   }

   protected void processSetForegroundColorExt(int paletteIndex) throws IOException {
   }

   protected void processSetForegroundColorExt(int r, int g, int b) throws IOException {
   }

   protected void processSetBackgroundColor(int color) throws IOException {
      this.processSetBackgroundColor(color, false);
   }

   protected void processSetBackgroundColor(int color, boolean bright) throws IOException {
   }

   protected void processSetBackgroundColorExt(int paletteIndex) throws IOException {
   }

   protected void processSetBackgroundColorExt(int r, int g, int b) throws IOException {
   }

   protected void processDefaultTextColor() throws IOException {
   }

   protected void processDefaultBackgroundColor() throws IOException {
   }

   protected void processAttributeReset() throws IOException {
   }

   protected void processCursorTo(int row, int col) throws IOException {
   }

   protected void processCursorToColumn(int x) throws IOException {
   }

   protected void processCursorUpLine(int count) throws IOException {
   }

   protected void processCursorDownLine(int count) throws IOException {
      for (int i = 0; i < count; i++) {
         this.os.write(10);
      }
   }

   protected void processCursorLeft(int count) throws IOException {
   }

   protected void processCursorRight(int count) throws IOException {
      for (int i = 0; i < count; i++) {
         this.os.write(32);
      }
   }

   protected void processCursorDown(int count) throws IOException {
   }

   protected void processCursorUp(int count) throws IOException {
   }

   protected void processUnknownExtension(ArrayList<Object> options, int command) {
   }

   protected void processChangeIconNameAndWindowTitle(String label) {
      this.processChangeIconName(label);
      this.processChangeWindowTitle(label);
   }

   protected void processChangeIconName(String label) {
   }

   protected void processChangeWindowTitle(String label) {
   }

   protected void processUnknownOperatingSystemCommand(int command, String param) {
   }

   protected void processCharsetSelect(int set, char seq) {
   }
}
