package org.jline.terminal.impl;

import java.io.IOError;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;
import org.jline.terminal.Attributes;
import org.jline.terminal.Cursor;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.spi.Pty;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.NonBlockingReader;

public abstract class AbstractPosixTerminal extends AbstractTerminal {
   protected final Pty pty;
   protected final Attributes originalAttributes;

   public AbstractPosixTerminal(String name, String type, Pty pty) throws IOException {
      this(name, type, pty, null, Terminal.SignalHandler.SIG_DFL);
   }

   public AbstractPosixTerminal(String name, String type, Pty pty, Charset encoding, Terminal.SignalHandler signalHandler) throws IOException {
      this(name, type, pty, encoding, encoding, encoding, signalHandler);
   }

   public AbstractPosixTerminal(
      String name, String type, Pty pty, Charset encoding, Charset inputEncoding, Charset outputEncoding, Terminal.SignalHandler signalHandler
   ) throws IOException {
      super(name, type, encoding, inputEncoding, outputEncoding, signalHandler);
      Objects.requireNonNull(pty);
      this.pty = pty;
      this.originalAttributes = this.pty.getAttr();
   }

   public Pty getPty() {
      return this.pty;
   }

   @Override
   public Attributes getAttributes() {
      try {
         return this.pty.getAttr();
      } catch (IOException var2) {
         throw new IOError(var2);
      }
   }

   @Override
   public void setAttributes(Attributes attr) {
      try {
         this.pty.setAttr(attr);
      } catch (IOException var3) {
         throw new IOError(var3);
      }
   }

   @Override
   public Size getSize() {
      try {
         return this.pty.getSize();
      } catch (IOException var2) {
         throw new IOError(var2);
      }
   }

   @Override
   public void setSize(Size size) {
      try {
         this.pty.setSize(size);
      } catch (IOException var3) {
         throw new IOError(var3);
      }
   }

   @Override
   protected void doClose() throws IOException {
      super.doClose();
      this.pty.setAttr(this.originalAttributes);
      this.pty.close();
   }

   @Override
   public Cursor getCursorPosition(IntConsumer discarded) {
      return CursorSupport.getCursorPosition(this, discarded);
   }

   @Override
   public TerminalProvider getProvider() {
      return this.getPty().getProvider();
   }

   @Override
   public SystemStream getSystemStream() {
      return this.getPty().getSystemStream();
   }

   @Override
   public String toString() {
      return this.getKind()
         + "[name='"
         + this.name
         + '\''
         + ", pty='"
         + this.pty
         + '\''
         + ", type='"
         + this.type
         + '\''
         + ", size='"
         + this.getSize()
         + '\''
         + ']';
   }

   @Override
   public int getDefaultForegroundColor() {
      try {
         this.writer().write("\u001b]10;?\u001b\\");
         this.writer().flush();
         return this.parseColorResponse(this.reader(), 10);
      } catch (IOException var2) {
         return -1;
      }
   }

   @Override
   public int getDefaultBackgroundColor() {
      try {
         this.writer().write("\u001b]11;?\u001b\\");
         this.writer().flush();
         return this.parseColorResponse(this.reader(), 11);
      } catch (IOException var2) {
         return -1;
      }
   }

   private int parseColorResponse(NonBlockingReader reader, int colorType) throws IOException {
      if (reader.peek(50L) < 0) {
         return -1;
      } else if (reader.read(10L) == 27 && reader.read(10L) == 93) {
         int tens = reader.read(10L);
         int ones = reader.read(10L);
         if (tens == 49 && (ones == 48 || ones == 49)) {
            int type = ones - 48 + 10;
            if (type != colorType) {
               return -1;
            } else if (reader.read(10L) != 59) {
               return -1;
            } else if (reader.read(10L) == 114 && reader.read(10L) == 103 && reader.read(10L) == 98 && reader.read(10L) == 58) {
               StringBuilder sb = new StringBuilder(16);
               List<String> rgb = new ArrayList<>();

               while (true) {
                  int c = reader.read(10L);
                  if (c == 7) {
                     rgb.add(sb.toString());
                     break;
                  }

                  if (c == 27) {
                     int next = reader.read(10L);
                     if (next != 92) {
                        return -1;
                     }

                     rgb.add(sb.toString());
                     break;
                  }

                  if ((c < 48 || c > 57) && (c < 65 || c > 90) && (c < 97 || c > 122)) {
                     if (c == 47) {
                        rgb.add(sb.toString());
                        sb.setLength(0);
                     }
                  } else {
                     sb.append((char)c);
                  }
               }

               if (rgb.size() != 3) {
                  return -1;
               } else {
                  double r = Integer.parseInt(rgb.get(0), 16) / ((1 << 4 * rgb.get(0).length()) - 1.0);
                  double g = Integer.parseInt(rgb.get(1), 16) / ((1 << 4 * rgb.get(1).length()) - 1.0);
                  double b = Integer.parseInt(rgb.get(2), 16) / ((1 << 4 * rgb.get(2).length()) - 1.0);
                  return (int)((Math.round(r * 255.0) << 16) + (Math.round(g * 255.0) << 8) + Math.round(b * 255.0));
               }
            } else {
               return -1;
            }
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }
}
