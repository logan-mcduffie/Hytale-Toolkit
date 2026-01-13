package org.jline.utils;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

public class AnsiWriter extends FilterWriter {
   private static final char[] RESET_CODE = "\u001b[0m".toCharArray();
   private static final int MAX_ESCAPE_SEQUENCE_LENGTH = 100;
   private final char[] buffer = new char[100];
   private int pos = 0;
   private int startOfValue;
   private final ArrayList<Object> options = new ArrayList<>();
   private static final int LOOKING_FOR_FIRST_ESC_CHAR = 0;
   private static final int LOOKING_FOR_SECOND_ESC_CHAR = 1;
   private static final int LOOKING_FOR_NEXT_ARG = 2;
   private static final int LOOKING_FOR_STR_ARG_END = 3;
   private static final int LOOKING_FOR_INT_ARG_END = 4;
   private static final int LOOKING_FOR_OSC_COMMAND = 5;
   private static final int LOOKING_FOR_OSC_COMMAND_END = 6;
   private static final int LOOKING_FOR_OSC_PARAM = 7;
   private static final int LOOKING_FOR_ST = 8;
   private static final int LOOKING_FOR_CHARSET = 9;
   int state = 0;
   private static final int FIRST_ESC_CHAR = 27;
   private static final int SECOND_ESC_CHAR = 91;
   private static final int SECOND_OSC_CHAR = 93;
   private static final int BEL = 7;
   private static final int SECOND_ST_CHAR = 92;
   private static final int SECOND_CHARSET0_CHAR = 40;
   private static final int SECOND_CHARSET1_CHAR = 41;
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
   @Deprecated
   protected static final int ATTRIBUTE_NEGATIVE_Off = 27;
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

   public AnsiWriter(Writer out) {
      super(out);
   }

   @Override
   public synchronized void write(int data) throws IOException {
      switch (this.state) {
         case 0:
            if (data == 27) {
               this.buffer[this.pos++] = (char)data;
               this.state = 1;
            } else {
               this.out.write(data);
            }
            break;
         case 1:
            this.buffer[this.pos++] = (char)data;
            if (data == 91) {
               this.state = 2;
            } else if (data == 93) {
               this.state = 5;
            } else if (data == 40) {
               this.options.add(48);
               this.state = 9;
            } else if (data == 41) {
               this.options.add(49);
               this.state = 9;
            } else {
               this.reset(false);
            }
            break;
         case 2:
            this.buffer[this.pos++] = (char)data;
            if (34 == data) {
               this.startOfValue = this.pos - 1;
               this.state = 3;
            } else if (48 <= data && data <= 57) {
               this.startOfValue = this.pos - 1;
               this.state = 4;
            } else if (59 == data) {
               this.options.add(null);
            } else if (63 == data) {
               this.options.add('?');
            } else if (61 == data) {
               this.options.add('=');
            } else {
               boolean skip = true;

               try {
                  skip = this.processEscapeCommand(this.options, data);
               } finally {
                  this.reset(skip);
               }
            }
            break;
         case 3:
            this.buffer[this.pos++] = (char)data;
            if (34 != data) {
               String value = new String(this.buffer, this.startOfValue, this.pos - 1 - this.startOfValue);
               this.options.add(value);
               if (data == 59) {
                  this.state = 2;
               } else {
                  this.reset(this.processEscapeCommand(this.options, data));
               }
            }
            break;
         case 4:
            this.buffer[this.pos++] = (char)data;
            if (48 > data || data > 57) {
               String strValue = new String(this.buffer, this.startOfValue, this.pos - 1 - this.startOfValue);
               Integer value = Integer.valueOf(strValue);
               this.options.add(value);
               if (data == 59) {
                  this.state = 2;
               } else {
                  boolean skip = true;

                  try {
                     skip = this.processEscapeCommand(this.options, data);
                  } finally {
                     this.reset(skip);
                  }
               }
            }
            break;
         case 5:
            this.buffer[this.pos++] = (char)data;
            if (48 <= data && data <= 57) {
               this.startOfValue = this.pos - 1;
               this.state = 6;
            } else {
               this.reset(false);
            }
            break;
         case 6:
            this.buffer[this.pos++] = (char)data;
            if (59 == data) {
               String strValue = new String(this.buffer, this.startOfValue, this.pos - 1 - this.startOfValue);
               Integer value = Integer.valueOf(strValue);
               this.options.add(value);
               this.startOfValue = this.pos;
               this.state = 7;
            } else if (48 > data || data > 57) {
               this.reset(false);
            }
            break;
         case 7:
            this.buffer[this.pos++] = (char)data;
            if (7 == data) {
               String value = new String(this.buffer, this.startOfValue, this.pos - 1 - this.startOfValue);
               this.options.add(value);
               boolean skip = true;

               try {
                  skip = this.processOperatingSystemCommand(this.options);
               } finally {
                  this.reset(skip);
               }
            } else if (27 == data) {
               this.state = 8;
            }
            break;
         case 8:
            this.buffer[this.pos++] = (char)data;
            if (92 == data) {
               String value = new String(this.buffer, this.startOfValue, this.pos - 2 - this.startOfValue);
               this.options.add(value);
               boolean skip = true;

               try {
                  skip = this.processOperatingSystemCommand(this.options);
               } finally {
                  this.reset(skip);
               }
            } else {
               this.state = 7;
            }
            break;
         case 9:
            this.options.add((char)data);
            this.reset(this.processCharsetSelect(this.options));
      }

      if (this.pos >= this.buffer.length) {
         this.reset(false);
      }
   }

   private void reset(boolean skipBuffer) throws IOException {
      if (!skipBuffer) {
         this.out.write(this.buffer, 0, this.pos);
      }

      this.pos = 0;
      this.startOfValue = 0;
      this.options.clear();
      this.state = 0;
   }

   private int getNextOptionInt(Iterator<Object> optionsIterator) throws IOException {
      while (optionsIterator.hasNext()) {
         Object arg = optionsIterator.next();
         if (arg != null) {
            return (Integer)arg;
         }
      }

      throw new IllegalArgumentException();
   }

   private boolean processEscapeCommand(ArrayList<Object> options, int command) throws IOException {
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
               if (97 <= command && 122 <= command) {
                  this.processUnknownExtension(options, command);
                  return true;
               } else {
                  if (65 <= command && 90 <= command) {
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
                     } else if (value == 38 || value == 48) {
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
                     } else {
                        switch (value) {
                           case 0:
                              this.processAttributeRest();
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
                     }
                  }
               }

               if (count == 0) {
                  this.processAttributeRest();
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

   private boolean processOperatingSystemCommand(ArrayList<Object> options) throws IOException {
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
      this.processSetForegroundColorExt(bright ? color + 8 : color);
   }

   protected void processSetForegroundColorExt(int paletteIndex) throws IOException {
   }

   protected void processSetForegroundColorExt(int r, int g, int b) throws IOException {
      this.processSetForegroundColorExt(Colors.roundRgbColor(r, g, b, 16));
   }

   protected void processSetBackgroundColor(int color) throws IOException {
      this.processSetBackgroundColor(color, false);
   }

   protected void processSetBackgroundColor(int color, boolean bright) throws IOException {
      this.processSetBackgroundColorExt(bright ? color + 8 : color);
   }

   protected void processSetBackgroundColorExt(int paletteIndex) throws IOException {
   }

   protected void processSetBackgroundColorExt(int r, int g, int b) throws IOException {
      this.processSetBackgroundColorExt(Colors.roundRgbColor(r, g, b, 16));
   }

   protected void processDefaultTextColor() throws IOException {
   }

   protected void processDefaultBackgroundColor() throws IOException {
   }

   protected void processAttributeRest() throws IOException {
   }

   protected void processCursorTo(int row, int col) throws IOException {
   }

   protected void processCursorToColumn(int x) throws IOException {
   }

   protected void processCursorUpLine(int count) throws IOException {
   }

   protected void processCursorDownLine(int count) throws IOException {
      for (int i = 0; i < count; i++) {
         this.out.write(10);
      }
   }

   protected void processCursorLeft(int count) throws IOException {
   }

   protected void processCursorRight(int count) throws IOException {
      for (int i = 0; i < count; i++) {
         this.out.write(32);
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

   protected void processChangeIconName(String name) {
   }

   protected void processChangeWindowTitle(String title) {
   }

   protected void processUnknownOperatingSystemCommand(int command, String param) {
   }

   private boolean processCharsetSelect(ArrayList<Object> options) throws IOException {
      int set = this.optionInt(options, 0);
      char seq = (Character)options.get(1);
      this.processCharsetSelect(set, seq);
      return true;
   }

   protected void processCharsetSelect(int set, char seq) {
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

   @Override
   public void write(char[] cbuf, int off, int len) throws IOException {
      for (int i = 0; i < len; i++) {
         this.write(cbuf[off + i]);
      }
   }

   @Override
   public void write(String str, int off, int len) throws IOException {
      for (int i = 0; i < len; i++) {
         this.write(str.charAt(off + i));
      }
   }

   @Override
   public void close() throws IOException {
      this.write(RESET_CODE);
      this.flush();
      super.close();
   }
}
