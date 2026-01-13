package io.sentry.cache.tape;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public abstract class ObjectQueue<T> implements Iterable<T>, Closeable {
   public static <T> ObjectQueue<T> create(QueueFile qf, ObjectQueue.Converter<T> converter) {
      return new FileObjectQueue<>(qf, converter);
   }

   public static <T> ObjectQueue<T> createEmpty() {
      return new EmptyObjectQueue<>();
   }

   @Nullable
   public abstract QueueFile file();

   public abstract int size();

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public abstract void add(T var1) throws IOException;

   @Nullable
   public abstract T peek() throws IOException;

   public List<T> peek(int max) throws IOException {
      int end = Math.min(max, this.size());
      List<T> subList = new ArrayList<>(end);
      Iterator<T> iterator = this.iterator();

      for (int i = 0; i < end; i++) {
         subList.add(iterator.next());
      }

      return Collections.unmodifiableList(subList);
   }

   public List<T> asList() throws IOException {
      return this.peek(this.size());
   }

   public void remove() throws IOException {
      this.remove(1);
   }

   public abstract void remove(int var1) throws IOException;

   public void clear() throws IOException {
      this.remove(this.size());
   }

   public interface Converter<T> {
      @Nullable
      T from(byte[] var1) throws IOException;

      void toStream(T var1, OutputStream var2) throws IOException;
   }
}
