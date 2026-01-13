package org.jline.terminal.impl.ffm;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.impl.AbstractPty;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;

class FfmNativePty extends AbstractPty {
   private final int master;
   private final int slave;
   private final int slaveOut;
   private final String name;
   private final FileDescriptor masterFD;
   private final FileDescriptor slaveFD;
   private final FileDescriptor slaveOutFD;

   public FfmNativePty(TerminalProvider provider, SystemStream systemStream, int master, int slave, String name) {
      this(provider, systemStream, master, newDescriptor(master), slave, newDescriptor(slave), slave, newDescriptor(slave), name);
   }

   public FfmNativePty(
      TerminalProvider provider,
      SystemStream systemStream,
      int master,
      FileDescriptor masterFD,
      int slave,
      FileDescriptor slaveFD,
      int slaveOut,
      FileDescriptor slaveOutFD,
      String name
   ) {
      super(provider, systemStream);
      this.master = master;
      this.slave = slave;
      this.slaveOut = slaveOut;
      this.name = name;
      this.masterFD = masterFD;
      this.slaveFD = slaveFD;
      this.slaveOutFD = slaveOutFD;
   }

   @Override
   public void close() throws IOException {
      if (this.master > 0) {
         this.getMasterInput().close();
      }

      if (this.slave > 0) {
         this.getSlaveInput().close();
      }
   }

   public int getMaster() {
      return this.master;
   }

   public int getSlave() {
      return this.slave;
   }

   public int getSlaveOut() {
      return this.slaveOut;
   }

   public String getName() {
      return this.name;
   }

   public FileDescriptor getMasterFD() {
      return this.masterFD;
   }

   public FileDescriptor getSlaveFD() {
      return this.slaveFD;
   }

   public FileDescriptor getSlaveOutFD() {
      return this.slaveOutFD;
   }

   @Override
   public InputStream getMasterInput() {
      return new FileInputStream(this.getMasterFD());
   }

   @Override
   public OutputStream getMasterOutput() {
      return new FileOutputStream(this.getMasterFD());
   }

   @Override
   protected InputStream doGetSlaveInput() {
      return new FileInputStream(this.getSlaveFD());
   }

   @Override
   public OutputStream getSlaveOutput() {
      return new FileOutputStream(this.getSlaveOutFD());
   }

   @Override
   public Attributes getAttr() throws IOException {
      return CLibrary.getAttributes(this.slave);
   }

   @Override
   protected void doSetAttr(Attributes attr) throws IOException {
      CLibrary.setAttributes(this.slave, attr);
   }

   @Override
   public Size getSize() throws IOException {
      return CLibrary.getTerminalSize(this.slave);
   }

   @Override
   public void setSize(Size size) throws IOException {
      CLibrary.setTerminalSize(this.slave, size);
   }

   @Override
   public String toString() {
      return "FfmNativePty[" + this.getName() + "]";
   }

   public static boolean isPosixSystemStream(SystemStream stream) {
      switch (stream) {
         case Input:
            return CLibrary.isTty(0);
         case Output:
            return CLibrary.isTty(1);
         case Error:
            return CLibrary.isTty(2);
         default:
            throw new IllegalArgumentException();
      }
   }

   public static String posixSystemStreamName(SystemStream stream) {
      switch (stream) {
         case Input:
            return CLibrary.ttyName(0);
         case Output:
            return CLibrary.ttyName(1);
         case Error:
            return CLibrary.ttyName(2);
         default:
            throw new IllegalArgumentException();
      }
   }

   public static int systemStreamWidth(SystemStream systemStream) {
      int fd = systemStream == SystemStream.Output ? 1 : 2;
      return CLibrary.getTerminalSize(fd).getColumns();
   }
}
