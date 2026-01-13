package io.sentry.cache.tape;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class QueueFile implements Closeable, Iterable<byte[]> {
   private static final int VERSIONED_HEADER = -2147483647;
   static final int INITIAL_LENGTH = 4096;
   private static final byte[] ZEROES = new byte[4096];
   RandomAccessFile raf;
   final File file;
   final int headerLength = 32;
   long fileLength;
   int elementCount;
   QueueFile.Element first;
   private QueueFile.Element last;
   private final byte[] buffer = new byte[32];
   int modCount = 0;
   private final boolean zero;
   private final int maxElements;
   boolean closed;

   static RandomAccessFile initializeFromFile(File file) throws IOException {
      if (!file.exists()) {
         File tempFile = new File(file.getPath() + ".tmp");
         RandomAccessFile raf = open(tempFile);

         try {
            raf.setLength(4096L);
            raf.seek(0L);
            raf.writeInt(-2147483647);
            raf.writeLong(4096L);
         } finally {
            raf.close();
         }

         if (!tempFile.renameTo(file)) {
            throw new IOException("Rename failed!");
         }
      }

      return open(file);
   }

   private static RandomAccessFile open(File file) throws FileNotFoundException {
      return new RandomAccessFile(file, "rwd");
   }

   QueueFile(File file, RandomAccessFile raf, boolean zero, int maxElements) throws IOException {
      this.file = file;
      this.raf = raf;
      this.zero = zero;
      this.maxElements = maxElements;
      this.readInitialData();
   }

   private void readInitialData() throws IOException {
      this.raf.seek(0L);
      this.raf.readFully(this.buffer);
      this.fileLength = readLong(this.buffer, 4);
      this.elementCount = readInt(this.buffer, 12);
      long firstOffset = readLong(this.buffer, 16);
      long lastOffset = readLong(this.buffer, 24);
      if (this.fileLength > this.raf.length()) {
         throw new IOException("File is truncated. Expected length: " + this.fileLength + ", Actual length: " + this.raf.length());
      } else if (this.fileLength <= 32L) {
         throw new IOException("File is corrupt; length stored in header (" + this.fileLength + ") is invalid.");
      } else {
         this.first = this.readElement(firstOffset);
         this.last = this.readElement(lastOffset);
      }
   }

   private void resetFile() throws IOException {
      this.raf.close();
      this.file.delete();
      this.raf = initializeFromFile(this.file);
      this.readInitialData();
   }

   private static void writeInt(byte[] buffer, int offset, int value) {
      buffer[offset] = (byte)(value >> 24);
      buffer[offset + 1] = (byte)(value >> 16);
      buffer[offset + 2] = (byte)(value >> 8);
      buffer[offset + 3] = (byte)value;
   }

   private static int readInt(byte[] buffer, int offset) {
      return ((buffer[offset] & 0xFF) << 24) + ((buffer[offset + 1] & 0xFF) << 16) + ((buffer[offset + 2] & 0xFF) << 8) + (buffer[offset + 3] & 0xFF);
   }

   private static void writeLong(byte[] buffer, int offset, long value) {
      buffer[offset] = (byte)(value >> 56);
      buffer[offset + 1] = (byte)(value >> 48);
      buffer[offset + 2] = (byte)(value >> 40);
      buffer[offset + 3] = (byte)(value >> 32);
      buffer[offset + 4] = (byte)(value >> 24);
      buffer[offset + 5] = (byte)(value >> 16);
      buffer[offset + 6] = (byte)(value >> 8);
      buffer[offset + 7] = (byte)value;
   }

   private static long readLong(byte[] buffer, int offset) {
      return ((buffer[offset] & 255L) << 56)
         + ((buffer[offset + 1] & 255L) << 48)
         + ((buffer[offset + 2] & 255L) << 40)
         + ((buffer[offset + 3] & 255L) << 32)
         + ((buffer[offset + 4] & 255L) << 24)
         + ((buffer[offset + 5] & 255L) << 16)
         + ((buffer[offset + 6] & 255L) << 8)
         + (buffer[offset + 7] & 255L);
   }

   private void writeHeader(long fileLength, int elementCount, long firstPosition, long lastPosition) throws IOException {
      this.raf.seek(0L);
      writeInt(this.buffer, 0, -2147483647);
      writeLong(this.buffer, 4, fileLength);
      writeInt(this.buffer, 12, elementCount);
      writeLong(this.buffer, 16, firstPosition);
      writeLong(this.buffer, 24, lastPosition);
      this.raf.write(this.buffer, 0, 32);
   }

   QueueFile.Element readElement(long position) throws IOException {
      if (position == 0L) {
         return QueueFile.Element.NULL;
      } else {
         boolean success = this.ringRead(position, this.buffer, 0, 4);
         if (!success) {
            return QueueFile.Element.NULL;
         } else {
            int length = readInt(this.buffer, 0);
            return new QueueFile.Element(position, length);
         }
      }
   }

   long wrapPosition(long position) {
      return position < this.fileLength ? position : 32L + position - this.fileLength;
   }

   private void ringWrite(long position, byte[] buffer, int offset, int count) throws IOException {
      position = this.wrapPosition(position);
      if (position + count <= this.fileLength) {
         this.raf.seek(position);
         this.raf.write(buffer, offset, count);
      } else {
         int beforeEof = (int)(this.fileLength - position);
         this.raf.seek(position);
         this.raf.write(buffer, offset, beforeEof);
         this.raf.seek(32L);
         this.raf.write(buffer, offset + beforeEof, count - beforeEof);
      }
   }

   private void ringErase(long position, long length) throws IOException {
      while (length > 0L) {
         int chunk = (int)Math.min(length, (long)ZEROES.length);
         this.ringWrite(position, ZEROES, 0, chunk);
         length -= chunk;
         position += chunk;
      }
   }

   boolean ringRead(long position, byte[] buffer, int offset, int count) throws IOException {
      try {
         position = this.wrapPosition(position);
         if (position + count <= this.fileLength) {
            this.raf.seek(position);
            this.raf.readFully(buffer, offset, count);
         } else {
            int beforeEof = (int)(this.fileLength - position);
            this.raf.seek(position);
            this.raf.readFully(buffer, offset, beforeEof);
            this.raf.seek(32L);
            this.raf.readFully(buffer, offset + beforeEof, count - beforeEof);
         }

         return true;
      } catch (EOFException var7) {
         this.resetFile();
      } catch (IOException var8) {
         throw var8;
      } catch (Throwable var9) {
         this.resetFile();
      }

      return false;
   }

   public void add(byte[] data) throws IOException {
      this.add(data, 0, data.length);
   }

   public void add(byte[] data, int offset, int count) throws IOException {
      if (data == null) {
         throw new NullPointerException("data == null");
      } else if ((offset | count) < 0 || count > data.length - offset) {
         throw new IndexOutOfBoundsException();
      } else if (this.closed) {
         throw new IllegalStateException("closed");
      } else {
         if (this.isAtFullCapacity()) {
            this.remove();
         }

         this.expandIfNecessary(count);
         boolean wasEmpty = this.isEmpty();
         long position = wasEmpty ? 32L : this.wrapPosition(this.last.position + 4L + this.last.length);
         QueueFile.Element newLast = new QueueFile.Element(position, count);
         writeInt(this.buffer, 0, count);
         this.ringWrite(newLast.position, this.buffer, 0, 4);
         this.ringWrite(newLast.position + 4L, data, offset, count);
         long firstPosition = wasEmpty ? newLast.position : this.first.position;
         this.writeHeader(this.fileLength, this.elementCount + 1, firstPosition, newLast.position);
         this.last = newLast;
         this.elementCount++;
         this.modCount++;
         if (wasEmpty) {
            this.first = this.last;
         }
      }
   }

   private long usedBytes() {
      if (this.elementCount == 0) {
         return 32L;
      } else {
         return this.last.position >= this.first.position
            ? this.last.position - this.first.position + 4L + this.last.length + 32L
            : this.last.position + 4L + this.last.length + this.fileLength - this.first.position;
      }
   }

   private long remainingBytes() {
      return this.fileLength - this.usedBytes();
   }

   public boolean isEmpty() {
      return this.elementCount == 0;
   }

   private void expandIfNecessary(long dataLength) throws IOException {
      long elementLength = 4L + dataLength;
      long remainingBytes = this.remainingBytes();
      if (remainingBytes < elementLength) {
         long previousLength = this.fileLength;

         long newLength;
         do {
            remainingBytes += previousLength;
            newLength = previousLength << 1;
            previousLength = newLength;
         } while (remainingBytes < elementLength);

         this.setLength(newLength);
         long endOfLastElement = this.wrapPosition(this.last.position + 4L + this.last.length);
         long count = 0L;
         if (endOfLastElement <= this.first.position) {
            FileChannel channel = this.raf.getChannel();
            channel.position(this.fileLength);
            count = endOfLastElement - 32L;
            if (channel.transferTo(32L, count, channel) != count) {
               throw new AssertionError("Copied insufficient number of bytes!");
            }
         }

         if (this.last.position < this.first.position) {
            long newLastPosition = this.fileLength + this.last.position - 32L;
            this.writeHeader(newLength, this.elementCount, this.first.position, newLastPosition);
            this.last = new QueueFile.Element(newLastPosition, this.last.length);
         } else {
            this.writeHeader(newLength, this.elementCount, this.first.position, this.last.position);
         }

         this.fileLength = newLength;
         if (this.zero) {
            this.ringErase(32L, count);
         }
      }
   }

   private void setLength(long newLength) throws IOException {
      this.raf.setLength(newLength);
      this.raf.getChannel().force(true);
   }

   @Nullable
   public byte[] peek() throws IOException {
      if (this.closed) {
         throw new IllegalStateException("closed");
      } else if (this.isEmpty()) {
         return null;
      } else {
         int length = this.first.length;
         byte[] data = new byte[length];
         boolean success = this.ringRead(this.first.position + 4L, data, 0, length);
         return success ? data : null;
      }
   }

   @Override
   public Iterator<byte[]> iterator() {
      return new QueueFile.ElementIterator();
   }

   public int size() {
      return this.elementCount;
   }

   public void remove() throws IOException {
      this.remove(1);
   }

   public void remove(int n) throws IOException {
      if (n < 0) {
         throw new IllegalArgumentException("Cannot remove negative (" + n + ") number of elements.");
      } else if (n != 0) {
         if (n == this.elementCount) {
            this.clear();
         } else if (this.isEmpty()) {
            throw new NoSuchElementException();
         } else if (n > this.elementCount) {
            throw new IllegalArgumentException("Cannot remove more elements (" + n + ") than present in queue (" + this.elementCount + ").");
         } else {
            long eraseStartPosition = this.first.position;
            long eraseTotalLength = 0L;
            long newFirstPosition = this.first.position;
            int newFirstLength = this.first.length;

            for (int i = 0; i < n; i++) {
               eraseTotalLength += 4 + newFirstLength;
               newFirstPosition = this.wrapPosition(newFirstPosition + 4L + newFirstLength);
               boolean success = this.ringRead(newFirstPosition, this.buffer, 0, 4);
               if (!success) {
                  return;
               }

               newFirstLength = readInt(this.buffer, 0);
            }

            this.writeHeader(this.fileLength, this.elementCount - n, newFirstPosition, this.last.position);
            this.elementCount -= n;
            this.modCount++;
            this.first = new QueueFile.Element(newFirstPosition, newFirstLength);
            if (this.zero) {
               this.ringErase(eraseStartPosition, eraseTotalLength);
            }
         }
      }
   }

   public void clear() throws IOException {
      if (this.closed) {
         throw new IllegalStateException("closed");
      } else {
         this.writeHeader(4096L, 0, 0L, 0L);
         if (this.zero) {
            this.raf.seek(32L);
            this.raf.write(ZEROES, 0, 4064);
         }

         this.elementCount = 0;
         this.first = QueueFile.Element.NULL;
         this.last = QueueFile.Element.NULL;
         if (this.fileLength > 4096L) {
            this.setLength(4096L);
         }

         this.fileLength = 4096L;
         this.modCount++;
      }
   }

   public boolean isAtFullCapacity() {
      return this.maxElements == -1 ? false : this.size() == this.maxElements;
   }

   public File file() {
      return this.file;
   }

   @Override
   public void close() throws IOException {
      this.closed = true;
      this.raf.close();
   }

   @Override
   public String toString() {
      return "QueueFile{file="
         + this.file
         + ", zero="
         + this.zero
         + ", length="
         + this.fileLength
         + ", size="
         + this.elementCount
         + ", first="
         + this.first
         + ", last="
         + this.last
         + '}';
   }

   static <T extends Throwable> T getSneakyThrowable(Throwable t) throws T {
      throw t;
   }

   public static final class Builder {
      final File file;
      boolean zero = true;
      int size = -1;

      public Builder(File file) {
         if (file == null) {
            throw new NullPointerException("file == null");
         } else {
            this.file = file;
         }
      }

      public QueueFile.Builder zero(boolean zero) {
         this.zero = zero;
         return this;
      }

      public QueueFile.Builder size(int size) {
         this.size = size;
         return this;
      }

      public QueueFile build() throws IOException {
         RandomAccessFile raf = QueueFile.initializeFromFile(this.file);
         QueueFile qf = null;

         QueueFile var3;
         try {
            qf = new QueueFile(this.file, raf, this.zero, this.size);
            var3 = qf;
         } finally {
            if (qf == null) {
               raf.close();
            }
         }

         return var3;
      }
   }

   static final class Element {
      static final QueueFile.Element NULL = new QueueFile.Element(0L, 0);
      static final int HEADER_LENGTH = 4;
      final long position;
      final int length;

      Element(long position, int length) {
         this.position = position;
         this.length = length;
      }

      @Override
      public String toString() {
         return this.getClass().getSimpleName() + "[position=" + this.position + ", length=" + this.length + "]";
      }
   }

   private final class ElementIterator implements Iterator<byte[]> {
      int nextElementIndex = 0;
      private long nextElementPosition = QueueFile.this.first.position;
      int expectedModCount = QueueFile.this.modCount;

      ElementIterator() {
      }

      private void checkForComodification() {
         if (QueueFile.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }

      @Override
      public boolean hasNext() {
         if (QueueFile.this.closed) {
            throw new IllegalStateException("closed");
         } else {
            this.checkForComodification();
            return this.nextElementIndex != QueueFile.this.elementCount;
         }
      }

      public byte[] next() {
         if (QueueFile.this.closed) {
            throw new IllegalStateException("closed");
         } else {
            this.checkForComodification();
            if (QueueFile.this.isEmpty()) {
               throw new NoSuchElementException();
            } else if (this.nextElementIndex >= QueueFile.this.elementCount) {
               throw new NoSuchElementException();
            } else {
               try {
                  QueueFile.Element current = QueueFile.this.readElement(this.nextElementPosition);
                  byte[] buffer = new byte[current.length];
                  this.nextElementPosition = QueueFile.this.wrapPosition(current.position + 4L);
                  boolean success = QueueFile.this.ringRead(this.nextElementPosition, buffer, 0, current.length);
                  if (!success) {
                     this.nextElementIndex = QueueFile.this.elementCount;
                     return QueueFile.ZEROES;
                  } else {
                     this.nextElementPosition = QueueFile.this.wrapPosition(current.position + 4L + current.length);
                     this.nextElementIndex++;
                     return buffer;
                  }
               } catch (IOException var5) {
                  throw (Error)QueueFile.getSneakyThrowable(var5);
               } catch (OutOfMemoryError var6) {
                  try {
                     QueueFile.this.resetFile();
                     this.nextElementIndex = QueueFile.this.elementCount;
                  } catch (IOException var4) {
                     throw (Error)QueueFile.getSneakyThrowable(var4);
                  }

                  return QueueFile.ZEROES;
               }
            }
         }
      }

      @Override
      public void remove() {
         this.checkForComodification();
         if (QueueFile.this.isEmpty()) {
            throw new NoSuchElementException();
         } else if (this.nextElementIndex != 1) {
            throw new UnsupportedOperationException("Removal is only permitted from the head.");
         } else {
            try {
               QueueFile.this.remove();
            } catch (IOException var2) {
               throw (Error)QueueFile.getSneakyThrowable(var2);
            }

            this.expectedModCount = QueueFile.this.modCount;
            this.nextElementIndex--;
         }
      }
   }
}
