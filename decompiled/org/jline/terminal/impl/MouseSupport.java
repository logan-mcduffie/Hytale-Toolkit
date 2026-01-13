package org.jline.terminal.impl;

import java.io.EOFException;
import java.io.IOError;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.IntSupplier;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.jline.utils.InputStreamReader;

public class MouseSupport {
   public static boolean hasMouseSupport(Terminal terminal) {
      return terminal.getStringCapability(InfoCmp.Capability.key_mouse) != null;
   }

   public static boolean trackMouse(Terminal terminal, Terminal.MouseTracking tracking) {
      if (hasMouseSupport(terminal)) {
         switch (tracking) {
            case Off:
               terminal.writer().write("\u001b[?1000l\u001b[?1002l\u001b[?1003l\u001b[?1005l\u001b[?1006l\u001b[?1015l\u001b[?1016l");
               break;
            case Normal:
               terminal.writer().write("\u001b[?1005h\u001b[?1006h\u001b[?1000h");
               break;
            case Button:
               terminal.writer().write("\u001b[?1005h\u001b[?1006h\u001b[?1002h");
               break;
            case Any:
               terminal.writer().write("\u001b[?1005h\u001b[?1006h\u001b[?1003h");
         }

         terminal.flush();
         return true;
      } else {
         return false;
      }
   }

   public static MouseEvent readMouse(Terminal terminal, MouseEvent last) {
      return readMouse(() -> readExt(terminal), last, null);
   }

   public static MouseEvent readMouse(Terminal terminal, MouseEvent last, String prefix) {
      return readMouse(() -> readExt(terminal), last, prefix);
   }

   public static MouseEvent readMouse(IntSupplier reader, MouseEvent last) {
      return readMouse(reader, last, null);
   }

   public static MouseEvent readMouse(IntSupplier reader, MouseEvent last, String prefix) {
      if (prefix != null && !prefix.isEmpty()) {
         if (prefix.equals("\u001b[<")) {
            IntSupplier prefixReader = createReaderFromString("<");
            return readMouse(chainReaders(prefixReader, reader), last, null);
         }

         if (prefix.equals("\u001b[M")) {
            IntSupplier prefixReader = createReaderFromString("M");
            return readMouse(chainReaders(prefixReader, reader), last, null);
         }
      }

      int c = reader.getAsInt();
      if (c == 60) {
         return readMouseSGR(reader, last);
      } else if (c >= 48 && c <= 57) {
         return readMouseURXVT(c, reader, last);
      } else if (c == 77) {
         int cb = reader.getAsInt();
         int cx = reader.getAsInt();
         int cy = reader.getAsInt();
         return (cx & 128) == 0 && (cy & 128) == 0 ? readMouseX10(cb - 32, cx - 32 - 1, cy - 32 - 1, last) : readMouseUTF8(cb, cx, cy, reader, last);
      } else {
         return readMouseX10(c - 32, reader, last);
      }
   }

   private static MouseEvent readMouseX10(int cb, IntSupplier reader, MouseEvent last) {
      int cx = reader.getAsInt() - 32 - 1;
      int cy = reader.getAsInt() - 32 - 1;
      return parseMouseEvent(cb, cx, cy, false, last);
   }

   private static MouseEvent readMouseX10(int cb, int cx, int cy, MouseEvent last) {
      return parseMouseEvent(cb, cx, cy, false, last);
   }

   private static MouseEvent readMouseUTF8(int cb, int cx, int cy, IntSupplier reader, MouseEvent last) {
      int x = decodeUtf8Coordinate(cx, reader);
      int y = decodeUtf8Coordinate(cy, reader);
      x--;
      y--;
      return parseMouseEvent(cb - 32, x, y, false, last);
   }

   private static int decodeUtf8Coordinate(int firstByte, IntSupplier reader) {
      if ((firstByte & 128) == 0) {
         return firstByte - 32;
      } else if ((firstByte & 224) == 192) {
         int secondByte = reader.getAsInt();
         int value = (firstByte & 31) << 6 | secondByte & 63;
         return value - 32;
      } else if ((firstByte & 240) == 224) {
         int secondByte = reader.getAsInt();
         int thirdByte = reader.getAsInt();
         int value = (firstByte & 15) << 12 | (secondByte & 63) << 6 | thirdByte & 63;
         return value - 32;
      } else {
         return firstByte - 32;
      }
   }

   private static MouseEvent readMouseSGR(IntSupplier reader, MouseEvent last) {
      StringBuilder sb = new StringBuilder();
      int[] params = new int[3];
      int paramIndex = 0;
      boolean isPixels = false;
      boolean isRelease = false;

      int c;
      while ((c = reader.getAsInt()) != -1) {
         if (c == 77 || c == 109) {
            isRelease = c == 109;
            break;
         }

         if (c == 59) {
            if (paramIndex < params.length) {
               try {
                  params[paramIndex++] = Integer.parseInt(sb.toString());
               } catch (NumberFormatException var12) {
                  params[paramIndex++] = 0;
               }

               sb.setLength(0);
            }
         } else if (c >= 48 && c <= 57) {
            sb.append((char)c);
         }
      }

      if (sb.length() > 0 && paramIndex < params.length) {
         try {
            params[paramIndex] = Integer.parseInt(sb.toString());
         } catch (NumberFormatException var11) {
            params[paramIndex] = 0;
         }
      }

      int cb = params[0];
      int cx = params[1] - 1;
      int cy = params[2] - 1;
      return parseMouseEvent(cb, cx, cy, isRelease, last);
   }

   private static MouseEvent readMouseURXVT(int firstDigit, IntSupplier reader, MouseEvent last) {
      StringBuilder sb = new StringBuilder().append((char)firstDigit);
      int[] params = new int[3];
      int paramIndex = 0;

      int c;
      while ((c = reader.getAsInt()) != -1 && c != 77) {
         if (c == 59) {
            if (paramIndex < params.length) {
               try {
                  params[paramIndex++] = Integer.parseInt(sb.toString());
               } catch (NumberFormatException var11) {
                  params[paramIndex++] = 0;
               }

               sb.setLength(0);
            }
         } else if (c >= 48 && c <= 57) {
            sb.append((char)c);
         }
      }

      if (sb.length() > 0 && paramIndex < params.length) {
         try {
            params[paramIndex] = Integer.parseInt(sb.toString());
         } catch (NumberFormatException var10) {
            params[paramIndex] = 0;
         }
      }

      int cb = params[0];
      int cx = params[1] - 1;
      int cy = params[2] - 1;
      return parseMouseEvent(cb, cx, cy, false, last);
   }

   private static MouseEvent parseMouseEvent(int cb, int cx, int cy, boolean isRelease, MouseEvent last) {
      EnumSet<MouseEvent.Modifier> modifiers = EnumSet.noneOf(MouseEvent.Modifier.class);
      if ((cb & 4) == 4) {
         modifiers.add(MouseEvent.Modifier.Shift);
      }

      if ((cb & 8) == 8) {
         modifiers.add(MouseEvent.Modifier.Alt);
      }

      if ((cb & 16) == 16) {
         modifiers.add(MouseEvent.Modifier.Control);
      }

      MouseEvent.Type type;
      MouseEvent.Button button;
      if ((cb & 64) == 64) {
         type = MouseEvent.Type.Wheel;
         button = (cb & 1) == 1 ? MouseEvent.Button.WheelDown : MouseEvent.Button.WheelUp;
      } else if (isRelease) {
         button = getButtonForCode(cb & 3);
         type = MouseEvent.Type.Released;
      } else {
         int b = cb & 3;
         switch (b) {
            case 0:
               button = MouseEvent.Button.Button1;
               if (last.getButton() != button || last.getType() != MouseEvent.Type.Pressed && last.getType() != MouseEvent.Type.Dragged) {
                  type = MouseEvent.Type.Pressed;
               } else {
                  type = MouseEvent.Type.Dragged;
               }
               break;
            case 1:
               button = MouseEvent.Button.Button2;
               if (last.getButton() != button || last.getType() != MouseEvent.Type.Pressed && last.getType() != MouseEvent.Type.Dragged) {
                  type = MouseEvent.Type.Pressed;
               } else {
                  type = MouseEvent.Type.Dragged;
               }
               break;
            case 2:
               button = MouseEvent.Button.Button3;
               if (last.getButton() != button || last.getType() != MouseEvent.Type.Pressed && last.getType() != MouseEvent.Type.Dragged) {
                  type = MouseEvent.Type.Pressed;
               } else {
                  type = MouseEvent.Type.Dragged;
               }
               break;
            default:
               if (last.getType() != MouseEvent.Type.Pressed && last.getType() != MouseEvent.Type.Dragged) {
                  button = MouseEvent.Button.NoButton;
                  type = MouseEvent.Type.Moved;
               } else {
                  button = last.getButton();
                  type = MouseEvent.Type.Released;
               }
         }
      }

      return new MouseEvent(type, button, modifiers, cx, cy);
   }

   private static MouseEvent.Button getButtonForCode(int code) {
      switch (code) {
         case 0:
            return MouseEvent.Button.Button1;
         case 1:
            return MouseEvent.Button.Button2;
         case 2:
            return MouseEvent.Button.Button3;
         default:
            return MouseEvent.Button.NoButton;
      }
   }

   public static String[] keys() {
      return new String[]{"\u001b[<", "\u001b[M"};
   }

   public static String[] keys(Terminal terminal) {
      String keyMouse = terminal.getStringCapability(InfoCmp.Capability.key_mouse);
      if (keyMouse != null) {
         return Arrays.asList(keys()).contains(keyMouse) ? keys() : new String[]{keyMouse, "\u001b[<", "\u001b[M"};
      } else {
         return keys();
      }
   }

   private static int readExt(Terminal terminal) {
      try {
         int c;
         if (terminal.encoding() != StandardCharsets.UTF_8) {
            c = new InputStreamReader(terminal.input(), StandardCharsets.UTF_8).read();
         } else {
            c = terminal.reader().read();
         }

         if (c < 0) {
            throw new EOFException();
         } else {
            return c;
         }
      } catch (IOException var2) {
         throw new IOError(var2);
      }
   }

   private static IntSupplier createReaderFromString(String s) {
      int[] chars = s.chars().toArray();
      int[] index = new int[]{0};
      return () -> index[0] < chars.length ? chars[index[0]++] : -1;
   }

   private static IntSupplier chainReaders(final IntSupplier first, final IntSupplier second) {
      return new IntSupplier() {
         private boolean firstExhausted = false;

         @Override
         public int getAsInt() {
            if (!this.firstExhausted) {
               int c = first.getAsInt();
               if (c != -1) {
                  return c;
               }

               this.firstExhausted = true;
            }

            return second.getAsInt();
         }
      };
   }
}
