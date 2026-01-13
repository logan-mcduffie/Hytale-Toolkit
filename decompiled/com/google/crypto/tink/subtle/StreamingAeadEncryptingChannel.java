package com.google.crypto.tink.subtle;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;
import java.security.GeneralSecurityException;

class StreamingAeadEncryptingChannel implements WritableByteChannel {
   private WritableByteChannel ciphertextChannel;
   private StreamSegmentEncrypter encrypter;
   ByteBuffer ptBuffer;
   ByteBuffer ctBuffer;
   private int plaintextSegmentSize;
   boolean open = true;

   @CanIgnoreReturnValue
   private int writeWithCheck(WritableByteChannel dst, ByteBuffer src) throws IOException {
      int r = src.remaining();
      int n = dst.write(src);
      if (n >= 0 && n <= r) {
         if (src.remaining() != r - n) {
            throw new IOException("Unexpected state after of src after writing to dst:  src.remaining() = " + src.remaining() + " != r - n = " + r + " - " + n);
         } else {
            return n;
         }
      } else {
         throw new IOException("Invalid return value from dst.write: n = " + n + ", r = " + r);
      }
   }

   public StreamingAeadEncryptingChannel(NonceBasedStreamingAead streamAead, WritableByteChannel ciphertextChannel, byte[] associatedData) throws GeneralSecurityException, IOException {
      this.ciphertextChannel = ciphertextChannel;
      this.encrypter = streamAead.newStreamSegmentEncrypter(associatedData);
      this.plaintextSegmentSize = streamAead.getPlaintextSegmentSize();
      this.ptBuffer = ByteBuffer.allocate(this.plaintextSegmentSize);
      this.ptBuffer.limit(this.plaintextSegmentSize - streamAead.getCiphertextOffset());
      this.ctBuffer = ByteBuffer.allocate(streamAead.getCiphertextSegmentSize());
      this.ctBuffer.put(this.encrypter.getHeader());
      this.ctBuffer.flip();
      this.writeWithCheck(ciphertextChannel, this.ctBuffer);
   }

   @Override
   public synchronized int write(ByteBuffer pt) throws IOException {
      if (!this.open) {
         throw new ClosedChannelException();
      } else {
         if (this.ctBuffer.remaining() > 0) {
            this.writeWithCheck(this.ciphertextChannel, this.ctBuffer);
         }

         int startPosition = pt.position();

         while (pt.remaining() > this.ptBuffer.remaining()) {
            if (this.ctBuffer.remaining() > 0) {
               return pt.position() - startPosition;
            }

            int sliceSize = this.ptBuffer.remaining();
            ByteBuffer slice = pt.slice();
            slice.limit(sliceSize);
            pt.position(pt.position() + sliceSize);

            try {
               this.ptBuffer.flip();
               this.ctBuffer.clear();
               if (slice.remaining() != 0) {
                  this.encrypter.encryptSegment(this.ptBuffer, slice, false, this.ctBuffer);
               } else {
                  this.encrypter.encryptSegment(this.ptBuffer, false, this.ctBuffer);
               }
            } catch (GeneralSecurityException var6) {
               throw new IOException(var6);
            }

            this.ctBuffer.flip();
            this.writeWithCheck(this.ciphertextChannel, this.ctBuffer);
            this.ptBuffer.clear();
            this.ptBuffer.limit(this.plaintextSegmentSize);
         }

         this.ptBuffer.put(pt);
         return pt.position() - startPosition;
      }
   }

   @Override
   public synchronized void close() throws IOException {
      if (this.open) {
         while (this.ctBuffer.remaining() > 0) {
            int n = this.writeWithCheck(this.ciphertextChannel, this.ctBuffer);
            if (n <= 0) {
               throw new IOException("Failed to write ciphertext before closing");
            }
         }

         try {
            this.ctBuffer.clear();
            this.ptBuffer.flip();
            this.encrypter.encryptSegment(this.ptBuffer, true, this.ctBuffer);
         } catch (GeneralSecurityException var2) {
            throw new IOException(var2);
         }

         this.ctBuffer.flip();

         while (this.ctBuffer.remaining() > 0) {
            int n = this.writeWithCheck(this.ciphertextChannel, this.ctBuffer);
            if (n <= 0) {
               throw new IOException("Failed to write ciphertext before closing");
            }
         }

         this.ciphertextChannel.close();
         this.open = false;
      }
   }

   @Override
   public boolean isOpen() {
      return this.open;
   }
}
