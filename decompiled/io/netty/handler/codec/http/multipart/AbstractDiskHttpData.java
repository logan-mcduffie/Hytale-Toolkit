package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public abstract class AbstractDiskHttpData extends AbstractHttpData {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractDiskHttpData.class);
   private File file;
   private boolean isRenamed;
   private FileChannel fileChannel;

   protected AbstractDiskHttpData(String name, Charset charset, long size) {
      super(name, charset, size);
   }

   protected abstract String getDiskFilename();

   protected abstract String getPrefix();

   protected abstract String getBaseDirectory();

   protected abstract String getPostfix();

   protected abstract boolean deleteOnExit();

   private File tempFile() throws IOException {
      String diskFilename = this.getDiskFilename();
      String newpostfix;
      if (diskFilename != null) {
         newpostfix = '_' + Integer.toString(diskFilename.hashCode());
      } else {
         newpostfix = this.getPostfix();
      }

      File tmpFile;
      if (this.getBaseDirectory() == null) {
         tmpFile = PlatformDependent.createTempFile(this.getPrefix(), newpostfix, null);
      } else {
         tmpFile = PlatformDependent.createTempFile(this.getPrefix(), newpostfix, new File(this.getBaseDirectory()));
      }

      if (this.deleteOnExit()) {
         DeleteFileOnExitHook.add(tmpFile.getPath());
      }

      return tmpFile;
   }

   @Override
   public void setContent(ByteBuf buffer) throws IOException {
      ObjectUtil.checkNotNull(buffer, "buffer");

      try {
         this.size = buffer.readableBytes();
         this.checkSize(this.size);
         if (this.definedSize > 0L && this.definedSize < this.size) {
            throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
         }

         if (this.file == null) {
            this.file = this.tempFile();
         }

         if (buffer.readableBytes() != 0) {
            RandomAccessFile accessFile = new RandomAccessFile(this.file, "rw");

            try {
               accessFile.setLength(0L);
               FileChannel localfileChannel = accessFile.getChannel();
               ByteBuffer byteBuffer = buffer.nioBuffer();
               int written = 0;

               while (written < this.size) {
                  written += localfileChannel.write(byteBuffer);
               }

               buffer.readerIndex(buffer.readerIndex() + written);
               localfileChannel.force(false);
            } finally {
               accessFile.close();
            }

            this.setCompleted();
            return;
         }

         if (this.file.createNewFile()) {
            return;
         }

         if (this.file.length() != 0L) {
            if (this.file.delete() && this.file.createNewFile()) {
               return;
            }

            throw new IOException("file exists already: " + this.file);
         }
      } finally {
         buffer.release();
      }
   }

   @Override
   public void addContent(ByteBuf buffer, boolean last) throws IOException {
      if (buffer != null) {
         try {
            int localsize = buffer.readableBytes();
            this.checkSize(this.size + localsize);
            if (this.definedSize > 0L && this.definedSize < this.size + localsize) {
               throw new IOException("Out of size: " + (this.size + localsize) + " > " + this.definedSize);
            }

            if (this.file == null) {
               this.file = this.tempFile();
            }

            if (this.fileChannel == null) {
               RandomAccessFile accessFile = new RandomAccessFile(this.file, "rw");
               this.fileChannel = accessFile.getChannel();
            }

            int remaining = localsize;
            long position = this.fileChannel.position();
            int index = buffer.readerIndex();

            while (remaining > 0) {
               int written = buffer.getBytes(index, this.fileChannel, position, remaining);
               if (written < 0) {
                  break;
               }

               remaining -= written;
               position += written;
               index += written;
            }

            this.fileChannel.position(position);
            buffer.readerIndex(index);
            this.size += localsize - remaining;
         } finally {
            buffer.release();
         }
      }

      if (last) {
         if (this.file == null) {
            this.file = this.tempFile();
         }

         if (this.fileChannel == null) {
            RandomAccessFile accessFile = new RandomAccessFile(this.file, "rw");
            this.fileChannel = accessFile.getChannel();
         }

         try {
            this.fileChannel.force(false);
         } finally {
            this.fileChannel.close();
         }

         this.fileChannel = null;
         this.setCompleted();
      } else {
         ObjectUtil.checkNotNull(buffer, "buffer");
      }
   }

   @Override
   public void setContent(File file) throws IOException {
      long size = file.length();
      this.checkSize(size);
      this.size = size;
      if (this.file != null) {
         this.delete();
      }

      this.file = file;
      this.isRenamed = true;
      this.setCompleted();
   }

   @Override
   public void setContent(InputStream inputStream) throws IOException {
      ObjectUtil.checkNotNull(inputStream, "inputStream");
      if (this.file != null) {
         this.delete();
      }

      this.file = this.tempFile();
      RandomAccessFile accessFile = new RandomAccessFile(this.file, "rw");
      long written = 0L;

      try {
         accessFile.setLength(0L);
         FileChannel localfileChannel = accessFile.getChannel();
         byte[] bytes = new byte[16384];
         ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

         for (int read = inputStream.read(bytes); read > 0; read = inputStream.read(bytes)) {
            ((Buffer)byteBuffer).position(read).flip();
            written += localfileChannel.write(byteBuffer);
            this.checkSize(written);
            ((Buffer)byteBuffer).clear();
         }

         localfileChannel.force(false);
      } finally {
         accessFile.close();
      }

      this.size = written;
      if (this.definedSize > 0L && this.definedSize < this.size) {
         if (!this.file.delete()) {
            logger.warn("Failed to delete: {}", this.file);
         }

         this.file = null;
         throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
      } else {
         this.isRenamed = true;
         this.setCompleted();
      }
   }

   @Override
   public void delete() {
      if (this.fileChannel != null) {
         try {
            this.fileChannel.force(false);
         } catch (IOException var10) {
            logger.warn("Failed to force.", (Throwable)var10);
         } finally {
            try {
               this.fileChannel.close();
            } catch (IOException var9) {
               logger.warn("Failed to close a file.", (Throwable)var9);
            }
         }

         this.fileChannel = null;
      }

      if (!this.isRenamed) {
         String filePath = null;
         if (this.file != null && this.file.exists()) {
            filePath = this.file.getPath();
            if (!this.file.delete()) {
               filePath = null;
               logger.warn("Failed to delete: {}", this.file);
            }
         }

         if (this.deleteOnExit() && filePath != null) {
            DeleteFileOnExitHook.remove(filePath);
         }

         this.file = null;
      }
   }

   @Override
   public byte[] get() throws IOException {
      return this.file == null ? EmptyArrays.EMPTY_BYTES : readFrom(this.file);
   }

   @Override
   public ByteBuf getByteBuf() throws IOException {
      if (this.file == null) {
         return Unpooled.EMPTY_BUFFER;
      } else {
         byte[] array = readFrom(this.file);
         return Unpooled.wrappedBuffer(array);
      }
   }

   @Override
   public ByteBuf getChunk(int length) throws IOException {
      if (this.file != null && length != 0) {
         if (this.fileChannel == null) {
            RandomAccessFile accessFile = new RandomAccessFile(this.file, "r");
            this.fileChannel = accessFile.getChannel();
         }

         int read = 0;
         ByteBuffer byteBuffer = ByteBuffer.allocate(length);

         try {
            while (read < length) {
               int readnow = this.fileChannel.read(byteBuffer);
               if (readnow == -1) {
                  this.fileChannel.close();
                  this.fileChannel = null;
                  break;
               }

               read += readnow;
            }
         } catch (IOException var5) {
            this.fileChannel.close();
            this.fileChannel = null;
            throw var5;
         }

         if (read == 0) {
            return Unpooled.EMPTY_BUFFER;
         } else {
            ((Buffer)byteBuffer).flip();
            ByteBuf buffer = Unpooled.wrappedBuffer(byteBuffer);
            buffer.readerIndex(0);
            buffer.writerIndex(read);
            return buffer;
         }
      } else {
         return Unpooled.EMPTY_BUFFER;
      }
   }

   @Override
   public String getString() throws IOException {
      return this.getString(HttpConstants.DEFAULT_CHARSET);
   }

   @Override
   public String getString(Charset encoding) throws IOException {
      if (this.file == null) {
         return "";
      } else if (encoding == null) {
         byte[] array = readFrom(this.file);
         return new String(array, HttpConstants.DEFAULT_CHARSET);
      } else {
         byte[] array = readFrom(this.file);
         return new String(array, encoding);
      }
   }

   @Override
   public boolean isInMemory() {
      return false;
   }

   @Override
   public boolean renameTo(File dest) throws IOException {
      ObjectUtil.checkNotNull(dest, "dest");
      if (this.file == null) {
         throw new IOException("No file defined so cannot be renamed");
      } else if (!this.file.renameTo(dest)) {
         IOException exception = null;
         RandomAccessFile inputAccessFile = null;
         RandomAccessFile outputAccessFile = null;
         long chunkSize = 8196L;
         long position = 0L;

         try {
            inputAccessFile = new RandomAccessFile(this.file, "r");
            outputAccessFile = new RandomAccessFile(dest, "rw");
            FileChannel in = inputAccessFile.getChannel();

            for (FileChannel out = outputAccessFile.getChannel(); position < this.size; position += in.transferTo(position, chunkSize, out)) {
               if (chunkSize < this.size - position) {
                  chunkSize = this.size - position;
               }
            }
         } catch (IOException var23) {
            exception = var23;
         } finally {
            if (inputAccessFile != null) {
               try {
                  inputAccessFile.close();
               } catch (IOException var22) {
                  if (exception == null) {
                     exception = var22;
                  } else {
                     logger.warn("Multiple exceptions detected, the following will be suppressed {}", (Throwable)var22);
                  }
               }
            }

            if (outputAccessFile != null) {
               try {
                  outputAccessFile.close();
               } catch (IOException var21) {
                  if (exception != null) {
                     logger.warn("Multiple exceptions detected, the following will be suppressed {}", (Throwable)var21);
                  }
               }
            }
         }

         if (exception != null) {
            throw exception;
         } else if (position == this.size) {
            if (!this.file.delete()) {
               logger.warn("Failed to delete: {}", this.file);
            }

            this.file = dest;
            this.isRenamed = true;
            return true;
         } else {
            if (!dest.delete()) {
               logger.warn("Failed to delete: {}", dest);
            }

            return false;
         }
      } else {
         this.file = dest;
         this.isRenamed = true;
         return true;
      }
   }

   private static byte[] readFrom(File src) throws IOException {
      long srcsize = src.length();
      if (srcsize > 2147483647L) {
         throw new IllegalArgumentException("File too big to be loaded in memory");
      } else {
         RandomAccessFile accessFile = new RandomAccessFile(src, "r");
         byte[] array = new byte[(int)srcsize];

         try {
            FileChannel fileChannel = accessFile.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.wrap(array);
            int read = 0;

            while (read < srcsize) {
               read += fileChannel.read(byteBuffer);
            }
         } finally {
            accessFile.close();
         }

         return array;
      }
   }

   @Override
   public File getFile() throws IOException {
      return this.file;
   }

   @Override
   public HttpData touch() {
      return this;
   }

   @Override
   public HttpData touch(Object hint) {
      return this;
   }
}
