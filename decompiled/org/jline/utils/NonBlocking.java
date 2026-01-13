package org.jline.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

public class NonBlocking {
   public static NonBlockingPumpReader nonBlockingPumpReader() {
      return new NonBlockingPumpReader();
   }

   public static NonBlockingPumpReader nonBlockingPumpReader(int size) {
      return new NonBlockingPumpReader(size);
   }

   public static NonBlockingPumpInputStream nonBlockingPumpInputStream() {
      return new NonBlockingPumpInputStream();
   }

   public static NonBlockingPumpInputStream nonBlockingPumpInputStream(int size) {
      return new NonBlockingPumpInputStream(size);
   }

   public static NonBlockingInputStream nonBlockingStream(NonBlockingReader reader, Charset encoding) {
      return new NonBlocking.NonBlockingReaderInputStream(reader, encoding);
   }

   public static NonBlockingInputStream nonBlocking(String name, InputStream inputStream) {
      return (NonBlockingInputStream)(inputStream instanceof NonBlockingInputStream
         ? (NonBlockingInputStream)inputStream
         : new NonBlockingInputStreamImpl(name, inputStream));
   }

   public static NonBlockingReader nonBlocking(String name, Reader reader) {
      return (NonBlockingReader)(reader instanceof NonBlockingReader ? (NonBlockingReader)reader : new NonBlockingReaderImpl(name, reader));
   }

   public static NonBlockingReader nonBlocking(String name, InputStream inputStream, Charset encoding) {
      return new NonBlocking.NonBlockingInputStreamReader(nonBlocking(name, inputStream), encoding);
   }

   private static class NonBlockingInputStreamReader extends NonBlockingReader {
      private final NonBlockingInputStream input;
      private final CharsetDecoder decoder;
      private final ByteBuffer bytes;
      private final CharBuffer chars;

      public NonBlockingInputStreamReader(NonBlockingInputStream inputStream, Charset encoding) {
         this(
            inputStream,
            (encoding != null ? encoding : Charset.defaultCharset())
               .newDecoder()
               .onMalformedInput(CodingErrorAction.REPLACE)
               .onUnmappableCharacter(CodingErrorAction.REPLACE)
         );
      }

      public NonBlockingInputStreamReader(NonBlockingInputStream input, CharsetDecoder decoder) {
         this.input = input;
         this.decoder = decoder;
         this.bytes = ByteBuffer.allocate(2048);
         this.chars = CharBuffer.allocate(1024);
         ((Buffer)this.bytes).limit(0);
         ((Buffer)this.chars).limit(0);
      }

      @Override
      protected int read(long timeout, boolean isPeek) throws IOException {
         Timeout t = new Timeout(timeout);

         while (!this.chars.hasRemaining() && !t.elapsed()) {
            int b = this.input.read(t.timeout());
            if (b == -1) {
               return -1;
            }

            if (b >= 0) {
               if (!this.bytes.hasRemaining()) {
                  ((Buffer)this.bytes).position(0);
                  ((Buffer)this.bytes).limit(0);
               }

               int l = this.bytes.limit();
               this.bytes.array()[this.bytes.arrayOffset() + l] = (byte)b;
               ((Buffer)this.bytes).limit(l + 1);
               ((Buffer)this.chars).clear();
               this.decoder.decode(this.bytes, this.chars, false);
               ((Buffer)this.chars).flip();
            }
         }

         if (this.chars.hasRemaining()) {
            return isPeek ? this.chars.get(this.chars.position()) : this.chars.get();
         } else {
            return -2;
         }
      }

      @Override
      public int readBuffered(char[] b, int off, int len, long timeout) throws IOException {
         if (b == null) {
            throw new NullPointerException();
         } else if (off < 0 || len < 0 || off + len < b.length) {
            throw new IllegalArgumentException();
         } else if (len == 0) {
            return 0;
         } else if (this.chars.hasRemaining()) {
            int r = Math.min(len, this.chars.remaining());
            this.chars.get(b, off, r);
            return r;
         } else {
            Timeout t = new Timeout(timeout);

            while (!this.chars.hasRemaining() && !t.elapsed()) {
               if (!this.bytes.hasRemaining()) {
                  ((Buffer)this.bytes).position(0);
                  ((Buffer)this.bytes).limit(0);
               }

               int nb = this.input.readBuffered(this.bytes.array(), this.bytes.limit(), this.bytes.capacity() - this.bytes.limit(), t.timeout());
               if (nb < 0) {
                  return nb;
               }

               ((Buffer)this.bytes).limit(this.bytes.limit() + nb);
               ((Buffer)this.chars).clear();
               this.decoder.decode(this.bytes, this.chars, false);
               ((Buffer)this.chars).flip();
            }

            int nb = Math.min(len, this.chars.remaining());
            this.chars.get(b, off, nb);
            return nb;
         }
      }

      @Override
      public void shutdown() {
         this.input.shutdown();
      }

      @Override
      public void close() throws IOException {
         this.input.close();
      }
   }

   private static class NonBlockingReaderInputStream extends NonBlockingInputStream {
      private final NonBlockingReader reader;
      private final CharsetEncoder encoder;
      private final ByteBuffer bytes;
      private final CharBuffer chars;

      private NonBlockingReaderInputStream(NonBlockingReader reader, Charset charset) {
         this.reader = reader;
         this.encoder = charset.newEncoder().onUnmappableCharacter(CodingErrorAction.REPLACE).onMalformedInput(CodingErrorAction.REPLACE);
         this.bytes = ByteBuffer.allocate(4);
         this.chars = CharBuffer.allocate(2);
         ((Buffer)this.bytes).limit(0);
         ((Buffer)this.chars).limit(0);
      }

      @Override
      public int available() {
         return (int)(this.reader.available() * this.encoder.averageBytesPerChar()) + this.bytes.remaining();
      }

      @Override
      public void close() throws IOException {
         this.reader.close();
      }

      @Override
      public int read(long timeout, boolean isPeek) throws IOException {
         Timeout t = new Timeout(timeout);

         while (!this.bytes.hasRemaining() && !t.elapsed()) {
            int c = this.reader.read(t.timeout());
            if (c == -1) {
               return -1;
            }

            if (c >= 0) {
               if (!this.chars.hasRemaining()) {
                  ((Buffer)this.chars).position(0);
                  ((Buffer)this.chars).limit(0);
               }

               int l = this.chars.limit();
               this.chars.array()[this.chars.arrayOffset() + l] = (char)c;
               ((Buffer)this.chars).limit(l + 1);
               ((Buffer)this.bytes).clear();
               this.encoder.encode(this.chars, this.bytes, false);
               ((Buffer)this.bytes).flip();
            }
         }

         if (this.bytes.hasRemaining()) {
            return isPeek ? this.bytes.get(this.bytes.position()) : this.bytes.get();
         } else {
            return -2;
         }
      }
   }
}
