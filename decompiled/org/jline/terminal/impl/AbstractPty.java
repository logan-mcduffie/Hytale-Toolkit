package org.jline.terminal.impl;

import java.io.FileDescriptor;
import java.io.FilterInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.lang.reflect.Field;
import org.jline.nativ.JLineLibrary;
import org.jline.nativ.JLineNativeLoader;
import org.jline.terminal.Attributes;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.spi.Pty;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.NonBlockingInputStream;

public abstract class AbstractPty implements Pty {
   protected final TerminalProvider provider;
   protected final SystemStream systemStream;
   private Attributes current;
   private boolean skipNextLf;
   private static AbstractPty.FileDescriptorCreator fileDescriptorCreator;

   public AbstractPty(TerminalProvider provider, SystemStream systemStream) {
      this.provider = provider;
      this.systemStream = systemStream;
   }

   @Override
   public void setAttr(Attributes attr) throws IOException {
      this.current = new Attributes(attr);
      this.doSetAttr(attr);
   }

   @Override
   public InputStream getSlaveInput() throws IOException {
      InputStream si = this.doGetSlaveInput();
      InputStream nsi = new FilterInputStream(si) {
         @Override
         public int read() throws IOException {
            while (true) {
               int c = super.read();
               if (AbstractPty.this.current.getInputFlag(Attributes.InputFlag.INORMEOL)) {
                  if (c == 13) {
                     AbstractPty.this.skipNextLf = true;
                     c = 10;
                  } else if (c == 10) {
                     if (AbstractPty.this.skipNextLf) {
                        AbstractPty.this.skipNextLf = false;
                        continue;
                     }
                  } else {
                     AbstractPty.this.skipNextLf = false;
                  }
               }

               return c;
            }
         }
      };
      return (InputStream)(Boolean.parseBoolean(System.getProperty("org.jline.terminal.pty.nonBlockingReads", "true"))
         ? new AbstractPty.PtyInputStream(nsi)
         : nsi);
   }

   protected abstract void doSetAttr(Attributes var1) throws IOException;

   protected abstract InputStream doGetSlaveInput() throws IOException;

   protected void checkInterrupted() throws InterruptedIOException {
      if (Thread.interrupted()) {
         throw new InterruptedIOException();
      }
   }

   @Override
   public TerminalProvider getProvider() {
      return this.provider;
   }

   @Override
   public SystemStream getSystemStream() {
      return this.systemStream;
   }

   protected static FileDescriptor newDescriptor(int fd) {
      if (fileDescriptorCreator == null) {
         String str = System.getProperty("org.jline.terminal.pty.fileDescriptorCreationMode", TerminalBuilder.PROP_FILE_DESCRIPTOR_CREATION_MODE_DEFAULT);
         String[] modes = str.split(",");
         IllegalStateException ise = new IllegalStateException("Unable to create FileDescriptor");

         for (String mode : modes) {
            try {
               switch (mode) {
                  case "native":
                     fileDescriptorCreator = new AbstractPty.NativeFileDescriptorCreator();
                     break;
                  case "reflection":
                     fileDescriptorCreator = new AbstractPty.ReflectionFileDescriptorCreator();
               }
            } catch (Throwable var10) {
               ise.addSuppressed(var10);
            }

            if (fileDescriptorCreator != null) {
               break;
            }
         }

         if (fileDescriptorCreator == null) {
            throw ise;
         }
      }

      return fileDescriptorCreator.newDescriptor(fd);
   }

   interface FileDescriptorCreator {
      FileDescriptor newDescriptor(int var1);
   }

   static class NativeFileDescriptorCreator implements AbstractPty.FileDescriptorCreator {
      NativeFileDescriptorCreator() {
         JLineNativeLoader.initialize();
      }

      @Override
      public FileDescriptor newDescriptor(int fd) {
         return JLineLibrary.newFileDescriptor(fd);
      }
   }

   class PtyInputStream extends NonBlockingInputStream {
      final InputStream in;
      int c = 0;

      PtyInputStream(InputStream in) {
         this.in = in;
      }

      @Override
      public int read(long timeout, boolean isPeek) throws IOException {
         AbstractPty.this.checkInterrupted();
         if (this.c != 0) {
            int r = this.c;
            if (!isPeek) {
               this.c = 0;
            }

            return r;
         } else {
            this.setNonBlocking();
            long start = System.currentTimeMillis();

            long cur;
            do {
               int r = this.in.read();
               if (r >= 0) {
                  if (isPeek) {
                     this.c = r;
                  }

                  return r;
               }

               AbstractPty.this.checkInterrupted();
               cur = System.currentTimeMillis();
            } while (timeout <= 0L || cur - start <= timeout);

            return -2;
         }
      }

      private void setNonBlocking() {
         if (AbstractPty.this.current == null
            || AbstractPty.this.current.getControlChar(Attributes.ControlChar.VMIN) != 0
            || AbstractPty.this.current.getControlChar(Attributes.ControlChar.VTIME) != 1) {
            try {
               Attributes attr = AbstractPty.this.getAttr();
               attr.setControlChar(Attributes.ControlChar.VMIN, 0);
               attr.setControlChar(Attributes.ControlChar.VTIME, 1);
               AbstractPty.this.setAttr(attr);
            } catch (IOException var2) {
               throw new IOError(var2);
            }
         }
      }
   }

   static class ReflectionFileDescriptorCreator implements AbstractPty.FileDescriptorCreator {
      private final Field fileDescriptorField;

      ReflectionFileDescriptorCreator() throws Exception {
         Field field = FileDescriptor.class.getDeclaredField("fd");
         field.setAccessible(true);
         this.fileDescriptorField = field;
      }

      @Override
      public FileDescriptor newDescriptor(int fd) {
         FileDescriptor descriptor = new FileDescriptor();

         try {
            this.fileDescriptorField.set(descriptor, fd);
            return descriptor;
         } catch (IllegalAccessException var4) {
            throw new IllegalStateException(var4);
         }
      }
   }
}
