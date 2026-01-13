package io.sentry.cache.tape;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class FileObjectQueue<T> extends ObjectQueue<T> {
   private final QueueFile queueFile;
   private final FileObjectQueue.DirectByteArrayOutputStream bytes = new FileObjectQueue.DirectByteArrayOutputStream();
   final ObjectQueue.Converter<T> converter;

   FileObjectQueue(QueueFile queueFile, ObjectQueue.Converter<T> converter) {
      this.queueFile = queueFile;
      this.converter = converter;
   }

   @NotNull
   @Override
   public QueueFile file() {
      return this.queueFile;
   }

   @Override
   public int size() {
      return this.queueFile.size();
   }

   @Override
   public boolean isEmpty() {
      return this.queueFile.isEmpty();
   }

   @Override
   public void add(T entry) throws IOException {
      this.bytes.reset();
      this.converter.toStream(entry, this.bytes);
      this.queueFile.add(this.bytes.getArray(), 0, this.bytes.size());
   }

   @Nullable
   @Override
   public T peek() throws IOException {
      byte[] bytes = this.queueFile.peek();
      return bytes == null ? null : this.converter.from(bytes);
   }

   @Override
   public void remove() throws IOException {
      this.queueFile.remove();
   }

   @Override
   public void remove(int n) throws IOException {
      this.queueFile.remove(n);
   }

   @Override
   public void clear() throws IOException {
      this.queueFile.clear();
   }

   @Override
   public void close() throws IOException {
      this.queueFile.close();
   }

   @Override
   public Iterator<T> iterator() {
      return new FileObjectQueue.QueueFileIterator(this.queueFile.iterator());
   }

   @Override
   public String toString() {
      return "FileObjectQueue{queueFile=" + this.queueFile + '}';
   }

   private static final class DirectByteArrayOutputStream extends ByteArrayOutputStream {
      DirectByteArrayOutputStream() {
      }

      byte[] getArray() {
         return this.buf;
      }
   }

   private final class QueueFileIterator implements Iterator<T> {
      final Iterator<byte[]> iterator;

      QueueFileIterator(Iterator<byte[]> iterator) {
         this.iterator = iterator;
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      @Nullable
      @Override
      public T next() {
         byte[] data = this.iterator.next();

         try {
            return FileObjectQueue.this.converter.from(data);
         } catch (IOException var3) {
            throw (Error)QueueFile.getSneakyThrowable(var3);
         }
      }

      @Override
      public void remove() {
         this.iterator.remove();
      }
   }
}
