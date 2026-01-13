package org.jline.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class WriterOutputStream extends OutputStream {
   private final Writer out;
   private final CharsetDecoder decoder;
   private final ByteBuffer decoderIn = ByteBuffer.allocate(256);
   private final CharBuffer decoderOut = CharBuffer.allocate(128);

   public WriterOutputStream(Writer out, Charset charset) {
      this(out, charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE));
   }

   public WriterOutputStream(Writer out, CharsetDecoder decoder) {
      this.out = out;
      this.decoder = decoder;
   }

   @Override
   public void write(int b) throws IOException {
      this.write(new byte[]{(byte)b}, 0, 1);
   }

   @Override
   public void write(byte[] b) throws IOException {
      this.write(b, 0, b.length);
   }

   @Override
   public void write(byte[] b, int off, int len) throws IOException {
      while (len > 0) {
         int c = Math.min(len, this.decoderIn.remaining());
         this.decoderIn.put(b, off, c);
         this.processInput(false);
         len -= c;
         off += c;
      }

      this.flush();
   }

   @Override
   public void flush() throws IOException {
      this.flushOutput();
      this.out.flush();
   }

   @Override
   public void close() throws IOException {
      this.processInput(true);
      this.flush();
      this.out.close();
   }

   private void processInput(boolean endOfInput) throws IOException {
      ((Buffer)this.decoderIn).flip();

      while (true) {
         CoderResult coderResult = this.decoder.decode(this.decoderIn, this.decoderOut, endOfInput);
         if (!coderResult.isOverflow()) {
            if (coderResult.isUnderflow()) {
               this.decoderIn.compact();
               return;
            } else {
               throw new IOException("Unexpected coder result");
            }
         }

         this.flushOutput();
      }
   }

   private void flushOutput() throws IOException {
      if (this.decoderOut.position() > 0) {
         this.out.write(this.decoderOut.array(), 0, this.decoderOut.position());
         ((Buffer)this.decoderOut).rewind();
      }
   }
}
