package org.jline.keymap;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import org.jline.reader.EndOfFileException;
import org.jline.utils.ClosedException;
import org.jline.utils.NonBlockingReader;

public class BindingReader {
   protected final NonBlockingReader reader;
   protected final StringBuilder opBuffer = new StringBuilder();
   protected final Deque<Integer> pushBackChar = new ArrayDeque<>();
   protected String lastBinding;

   public BindingReader(NonBlockingReader reader) {
      this.reader = reader;
   }

   public <T> T readBinding(KeyMap<T> keys) {
      return this.readBinding(keys, null, true);
   }

   public <T> T readBinding(KeyMap<T> keys, KeyMap<T> local) {
      return this.readBinding(keys, local, true);
   }

   public <T> T readBinding(KeyMap<T> keys, KeyMap<T> local, boolean block) {
      this.lastBinding = null;
      T o = null;
      int[] remaining = new int[1];
      boolean hasRead = false;

      while (true) {
         if (local != null) {
            o = local.getBound(this.opBuffer, remaining);
         }

         if (o == null && (local == null || remaining[0] >= 0)) {
            o = keys.getBound(this.opBuffer, remaining);
         }

         if (o != null) {
            if (remaining[0] >= 0) {
               this.runMacro(this.opBuffer.substring(this.opBuffer.length() - remaining[0]));
               this.opBuffer.setLength(this.opBuffer.length() - remaining[0]);
            } else {
               long ambiguousTimeout = keys.getAmbiguousTimeout();
               if (ambiguousTimeout > 0L && this.peekCharacter(ambiguousTimeout) != -2) {
                  o = null;
               }
            }

            if (o != null) {
               this.lastBinding = this.opBuffer.toString();
               this.opBuffer.setLength(0);
               return o;
            }
         } else if (remaining[0] > 0) {
            int cp = this.opBuffer.codePointAt(0);
            String rem = this.opBuffer.substring(Character.charCount(cp));
            this.lastBinding = this.opBuffer.substring(0, Character.charCount(cp));
            o = cp >= 128 ? keys.getUnicode() : keys.getNomatch();
            this.opBuffer.setLength(0);
            this.opBuffer.append(rem);
            if (o != null) {
               return o;
            }
         }

         if (!block && hasRead) {
            return null;
         }

         int c = this.readCharacter();
         if (c == -1) {
            return null;
         }

         this.opBuffer.appendCodePoint(c);
         hasRead = true;
      }
   }

   public String readStringUntil(String sequence) {
      StringBuilder sb = new StringBuilder();
      if (!this.pushBackChar.isEmpty()) {
         this.pushBackChar.forEach(sb::appendCodePoint);
      }

      try {
         char[] buf = new char[64];

         while (true) {
            int idx = sb.indexOf(sequence, Math.max(0, sb.length() - buf.length - sequence.length()));
            if (idx >= 0) {
               String rem = sb.substring(idx + sequence.length());
               this.runMacro(rem);
               return sb.substring(0, idx);
            }

            int l = this.reader.readBuffered(buf);
            if (l < 0) {
               throw new ClosedException();
            }

            sb.append(buf, 0, l);
         }
      } catch (ClosedException var6) {
         throw new EndOfFileException(var6);
      } catch (IOException var7) {
         throw new IOError(var7);
      }
   }

   public int readCharacter() {
      if (!this.pushBackChar.isEmpty()) {
         return this.pushBackChar.pop();
      } else {
         try {
            int c = -2;
            int s = 0;

            while (c == -2) {
               c = this.reader.read(100L);
               if (c >= 0 && Character.isHighSurrogate((char)c)) {
                  s = c;
                  c = -2;
               }
            }

            return s != 0 ? Character.toCodePoint((char)s, (char)c) : c;
         } catch (ClosedException var3) {
            throw new EndOfFileException(var3);
         } catch (IOException var4) {
            throw new IOError(var4);
         }
      }
   }

   public int readCharacterBuffered() {
      try {
         if (this.pushBackChar.isEmpty()) {
            char[] buf = new char[32];
            int l = this.reader.readBuffered(buf);
            if (l <= 0) {
               return -1;
            }

            int s = 0;
            int i = 0;

            while (i < l) {
               int c = buf[i++];
               if (Character.isHighSurrogate((char)c)) {
                  s = c;
                  if (i >= l) {
                     break;
                  }

                  int var9 = buf[i++];
                  this.pushBackChar.addLast(Character.toCodePoint((char)c, (char)var9));
               } else {
                  s = 0;
                  this.pushBackChar.addLast(c);
               }
            }

            if (s != 0) {
               i = this.reader.read();
               if (i < 0) {
                  return -1;
               }

               this.pushBackChar.addLast(Character.toCodePoint((char)s, (char)i));
            }
         }

         return this.pushBackChar.pop();
      } catch (ClosedException var6) {
         throw new EndOfFileException(var6);
      } catch (IOException var7) {
         throw new IOError(var7);
      }
   }

   public int peekCharacter(long timeout) {
      if (!this.pushBackChar.isEmpty()) {
         return this.pushBackChar.peek();
      } else {
         try {
            return this.reader.peek(timeout);
         } catch (IOException var4) {
            throw new IOError(var4);
         }
      }
   }

   public void runMacro(String macro) {
      macro.codePoints().forEachOrdered(this.pushBackChar::addLast);
   }

   public String getCurrentBuffer() {
      return this.opBuffer.toString();
   }

   public String getLastBinding() {
      return this.lastBinding;
   }
}
