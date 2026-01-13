package com.google.crypto.tink.subtle;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

class StreamingAeadEncryptingStream extends FilterOutputStream {
   private StreamSegmentEncrypter encrypter;
   private int plaintextSegmentSize;
   ByteBuffer ptBuffer;
   ByteBuffer ctBuffer;
   boolean open;

   private static Buffer toBuffer(ByteBuffer b) {
      return b;
   }

   public StreamingAeadEncryptingStream(NonceBasedStreamingAead streamAead, OutputStream ciphertextChannel, byte[] associatedData) throws GeneralSecurityException, IOException {
      super(ciphertextChannel);
      this.encrypter = streamAead.newStreamSegmentEncrypter(associatedData);
      this.plaintextSegmentSize = streamAead.getPlaintextSegmentSize();
      this.ptBuffer = ByteBuffer.allocate(this.plaintextSegmentSize);
      this.ctBuffer = ByteBuffer.allocate(streamAead.getCiphertextSegmentSize());
      toBuffer(this.ptBuffer).limit(this.plaintextSegmentSize - streamAead.getCiphertextOffset());
      ByteBuffer header = this.encrypter.getHeader();
      byte[] headerBytes = new byte[header.remaining()];
      header.get(headerBytes);
      this.out.write(headerBytes);
      this.open = true;
   }

   @Override
   public void write(int b) throws IOException {
      this.write(new byte[]{(byte)b});
   }

   @Override
   public void write(byte[] b) throws IOException {
      this.write(b, 0, b.length);
   }

   @Override
   public synchronized void write(byte[] pt, int offset, int length) throws IOException {
      if (!this.open) {
         throw new IOException("Trying to write to closed stream");
      } else {
         int startPosition = offset;
         int remaining = length;

         while (remaining > this.ptBuffer.remaining()) {
            int sliceSize = this.ptBuffer.remaining();
            ByteBuffer slice = ByteBuffer.wrap(pt, startPosition, sliceSize);
            startPosition += sliceSize;
            remaining -= sliceSize;

            try {
               toBuffer(this.ptBuffer).flip();
               toBuffer(this.ctBuffer).clear();
               this.encrypter.encryptSegment(this.ptBuffer, slice, false, this.ctBuffer);
            } catch (GeneralSecurityException var9) {
               throw new IOException(var9);
            }

            toBuffer(this.ctBuffer).flip();
            this.out.write(this.ctBuffer.array(), toBuffer(this.ctBuffer).position(), this.ctBuffer.remaining());
            toBuffer(this.ptBuffer).clear();
            toBuffer(this.ptBuffer).limit(this.plaintextSegmentSize);
         }

         this.ptBuffer.put(pt, startPosition, remaining);
      }
   }

   @Override
   public synchronized void close() throws IOException {
      if (this.open) {
         try {
            toBuffer(this.ptBuffer).flip();
            toBuffer(this.ctBuffer).clear();
            this.encrypter.encryptSegment(this.ptBuffer, true, this.ctBuffer);
         } catch (GeneralSecurityException var2) {
            throw new IOException("ptBuffer.remaining():" + this.ptBuffer.remaining() + " ctBuffer.remaining():" + this.ctBuffer.remaining(), var2);
         }

         toBuffer(this.ctBuffer).flip();
         this.out.write(this.ctBuffer.array(), this.ctBuffer.position(), this.ctBuffer.remaining());
         this.open = false;
         super.close();
      }
   }
}
