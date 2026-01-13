package org.jline.jansi.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.jline.jansi.AnsiColors;

public class ColorsAnsiProcessor extends AnsiProcessor {
   private final AnsiColors colors;

   public ColorsAnsiProcessor(OutputStream os, AnsiColors colors) {
      super(os);
      this.colors = colors;
   }

   @Override
   protected boolean processEscapeCommand(ArrayList<Object> options, int command) throws IOException {
      if (command == 109 && (this.colors == AnsiColors.Colors256 || this.colors == AnsiColors.Colors16)) {
         boolean has38or48 = false;

         for (Object next : options) {
            if (next != null && next.getClass() != Integer.class) {
               throw new IllegalArgumentException();
            }

            Integer value = (Integer)next;
            has38or48 |= value == 38 || value == 48;
         }

         if (!has38or48) {
            return false;
         } else {
            StringBuilder sb = new StringBuilder(32);
            sb.append('\u001b').append('[');
            boolean first = true;
            Iterator<Object> optionsIterator = options.iterator();

            while (optionsIterator.hasNext()) {
               Object next = optionsIterator.next();
               if (next != null) {
                  int value = (Integer)next;
                  if (value != 38 && value != 48) {
                     if (!first) {
                        sb.append(';');
                     }

                     first = false;
                     sb.append(value);
                  } else {
                     int arg2or5 = this.getNextOptionInt(optionsIterator);
                     if (arg2or5 == 2) {
                        int r = this.getNextOptionInt(optionsIterator);
                        int g = this.getNextOptionInt(optionsIterator);
                        int b = this.getNextOptionInt(optionsIterator);
                        if (this.colors == AnsiColors.Colors256) {
                           int col = Colors.roundRgbColor(r, g, b, 256);
                           if (!first) {
                              sb.append(';');
                           }

                           first = false;
                           sb.append(value);
                           sb.append(';');
                           sb.append(5);
                           sb.append(';');
                           sb.append(col);
                        } else {
                           int col = Colors.roundRgbColor(r, g, b, 16);
                           if (!first) {
                              sb.append(';');
                           }

                           first = false;
                           sb.append(value == 38 ? (col >= 8 ? 90 + col - 8 : 30 + col) : (col >= 8 ? 100 + col - 8 : 40 + col));
                        }
                     } else {
                        if (arg2or5 != 5) {
                           throw new IllegalArgumentException();
                        }

                        int paletteIndex = this.getNextOptionInt(optionsIterator);
                        if (this.colors == AnsiColors.Colors256) {
                           if (!first) {
                              sb.append(';');
                           }

                           first = false;
                           sb.append(value);
                           sb.append(';');
                           sb.append(5);
                           sb.append(';');
                           sb.append(paletteIndex);
                        } else {
                           int col = Colors.roundColor(paletteIndex, 16);
                           if (!first) {
                              sb.append(';');
                           }

                           first = false;
                           sb.append(value == 38 ? (col >= 8 ? 90 + col - 8 : 30 + col) : (col >= 8 ? 100 + col - 8 : 40 + col));
                        }
                     }
                  }
               }
            }

            sb.append('m');
            this.os.write(sb.toString().getBytes());
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   protected boolean processOperatingSystemCommand(ArrayList<Object> options) {
      return false;
   }

   @Override
   protected boolean processCharsetSelect(ArrayList<Object> options) {
      return false;
   }
}
