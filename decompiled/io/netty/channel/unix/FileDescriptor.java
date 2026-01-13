package io.netty.channel.unix;

import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class FileDescriptor {
   private static final AtomicIntegerFieldUpdater<FileDescriptor> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(FileDescriptor.class, "state");
   private static final int STATE_CLOSED_MASK = 1;
   private static final int STATE_INPUT_SHUTDOWN_MASK = 2;
   private static final int STATE_OUTPUT_SHUTDOWN_MASK = 4;
   private static final int STATE_ALL_MASK = 7;
   volatile int state;
   final int fd;

   public FileDescriptor(int fd) {
      ObjectUtil.checkPositiveOrZero(fd, "fd");
      this.fd = fd;
   }

   public final int intValue() {
      return this.fd;
   }

   protected boolean markClosed() {
      int state;
      do {
         state = this.state;
         if (isClosed(state)) {
            return false;
         }
      } while (!this.casState(state, state | 7));

      return true;
   }

   public void close() throws IOException {
      if (this.markClosed()) {
         int res = close(this.fd);
         if (res < 0) {
            throw Errors.newIOException("close", res);
         }
      }
   }

   public boolean isOpen() {
      return !isClosed(this.state);
   }

   public final int write(ByteBuffer buf, int pos, int limit) throws IOException {
      int res = write(this.fd, buf, pos, limit);
      return res >= 0 ? res : Errors.ioResult("write", res);
   }

   public final int writeAddress(long address, int pos, int limit) throws IOException {
      int res = writeAddress(this.fd, address, pos, limit);
      return res >= 0 ? res : Errors.ioResult("writeAddress", res);
   }

   public final long writev(ByteBuffer[] buffers, int offset, int length, long maxBytesToWrite) throws IOException {
      long res = writev(this.fd, buffers, offset, Math.min(Limits.IOV_MAX, length), maxBytesToWrite);
      return res >= 0L ? res : Errors.ioResult("writev", (int)res);
   }

   public final long writevAddresses(long memoryAddress, int length) throws IOException {
      long res = writevAddresses(this.fd, memoryAddress, length);
      return res >= 0L ? res : Errors.ioResult("writevAddresses", (int)res);
   }

   public final int read(ByteBuffer buf, int pos, int limit) throws IOException {
      int res = read(this.fd, buf, pos, limit);
      if (res > 0) {
         return res;
      } else {
         return res == 0 ? -1 : Errors.ioResult("read", res);
      }
   }

   public final int readAddress(long address, int pos, int limit) throws IOException {
      int res = readAddress(this.fd, address, pos, limit);
      if (res > 0) {
         return res;
      } else {
         return res == 0 ? -1 : Errors.ioResult("readAddress", res);
      }
   }

   @Override
   public String toString() {
      return "FileDescriptor{fd=" + this.fd + '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return !(o instanceof FileDescriptor) ? false : this.fd == ((FileDescriptor)o).fd;
      }
   }

   @Override
   public int hashCode() {
      return this.fd;
   }

   public static FileDescriptor from(String path) throws IOException {
      int res = open(ObjectUtil.checkNotNull(path, "path"));
      if (res < 0) {
         throw Errors.newIOException("open", res);
      } else {
         return new FileDescriptor(res);
      }
   }

   public static FileDescriptor from(File file) throws IOException {
      return from(ObjectUtil.checkNotNull(file, "file").getPath());
   }

   public static FileDescriptor[] pipe() throws IOException {
      long res = newPipe();
      if (res < 0L) {
         throw Errors.newIOException("newPipe", (int)res);
      } else {
         return new FileDescriptor[]{new FileDescriptor((int)(res >>> 32)), new FileDescriptor((int)res)};
      }
   }

   final boolean casState(int expected, int update) {
      return stateUpdater.compareAndSet(this, expected, update);
   }

   static boolean isClosed(int state) {
      return (state & 1) != 0;
   }

   static boolean isInputShutdown(int state) {
      return (state & 2) != 0;
   }

   static boolean isOutputShutdown(int state) {
      return (state & 4) != 0;
   }

   static int inputShutdown(int state) {
      return state | 2;
   }

   static int outputShutdown(int state) {
      return state | 4;
   }

   private static native int open(String var0);

   private static native int close(int var0);

   private static native int write(int var0, ByteBuffer var1, int var2, int var3);

   private static native int writeAddress(int var0, long var1, int var3, int var4);

   private static native long writev(int var0, ByteBuffer[] var1, int var2, int var3, long var4);

   private static native long writevAddresses(int var0, long var1, int var3);

   private static native int read(int var0, ByteBuffer var1, int var2, int var3);

   private static native int readAddress(int var0, long var1, int var3, int var4);

   private static native long newPipe();
}
