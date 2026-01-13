package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.StreamingAead;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.security.GeneralSecurityException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import javax.annotation.concurrent.GuardedBy;

final class SeekableByteChannelDecrypter implements SeekableByteChannel {
   @GuardedBy("this")
   SeekableByteChannel attemptingChannel = null;
   @GuardedBy("this")
   SeekableByteChannel matchingChannel = null;
   @GuardedBy("this")
   SeekableByteChannel ciphertextChannel;
   @GuardedBy("this")
   long cachedPosition;
   @GuardedBy("this")
   long startingPosition;
   Deque<StreamingAead> remainingPrimitives = new ArrayDeque<>();
   byte[] associatedData;

   public SeekableByteChannelDecrypter(List<StreamingAead> allPrimitives, SeekableByteChannel ciphertextChannel, final byte[] associatedData) throws IOException {
      for (StreamingAead primitive : allPrimitives) {
         this.remainingPrimitives.add(primitive);
      }

      this.ciphertextChannel = ciphertextChannel;
      this.cachedPosition = -1L;
      this.startingPosition = ciphertextChannel.position();
      this.associatedData = (byte[])associatedData.clone();
   }

   @GuardedBy("this")
   private synchronized SeekableByteChannel nextAttemptingChannel() throws IOException {
      while (!this.remainingPrimitives.isEmpty()) {
         this.ciphertextChannel.position(this.startingPosition);
         StreamingAead streamingAead = this.remainingPrimitives.removeFirst();

         try {
            SeekableByteChannel decChannel = streamingAead.newSeekableDecryptingChannel(this.ciphertextChannel, this.associatedData);
            if (this.cachedPosition >= 0L) {
               decChannel.position(this.cachedPosition);
            }

            return decChannel;
         } catch (GeneralSecurityException var3) {
         }
      }

      throw new IOException("No matching key found for the ciphertext in the stream.");
   }

   @GuardedBy("this")
   @Override
   public synchronized int read(ByteBuffer dst) throws IOException {
      if (dst.remaining() == 0) {
         return 0;
      } else if (this.matchingChannel != null) {
         return this.matchingChannel.read(dst);
      } else {
         if (this.attemptingChannel == null) {
            this.attemptingChannel = this.nextAttemptingChannel();
         }

         while (true) {
            try {
               int retValue = this.attemptingChannel.read(dst);
               if (retValue == 0) {
                  return 0;
               }

               this.matchingChannel = this.attemptingChannel;
               this.attemptingChannel = null;
               return retValue;
            } catch (IOException var3) {
               this.attemptingChannel = this.nextAttemptingChannel();
            }
         }
      }
   }

   @CanIgnoreReturnValue
   @GuardedBy("this")
   @Override
   public synchronized SeekableByteChannel position(long newPosition) throws IOException {
      if (this.matchingChannel != null) {
         this.matchingChannel.position(newPosition);
      } else {
         if (newPosition < 0L) {
            throw new IllegalArgumentException("Position must be non-negative");
         }

         this.cachedPosition = newPosition;
         if (this.attemptingChannel != null) {
            this.attemptingChannel.position(this.cachedPosition);
         }
      }

      return this;
   }

   @GuardedBy("this")
   @Override
   public synchronized long position() throws IOException {
      return this.matchingChannel != null ? this.matchingChannel.position() : this.cachedPosition;
   }

   @GuardedBy("this")
   @Override
   public synchronized long size() throws IOException {
      if (this.matchingChannel != null) {
         return this.matchingChannel.size();
      } else {
         throw new IOException("Cannot determine size before first read()-call.");
      }
   }

   @Override
   public SeekableByteChannel truncate(long size) throws IOException {
      throw new NonWritableChannelException();
   }

   @Override
   public int write(ByteBuffer src) throws IOException {
      throw new NonWritableChannelException();
   }

   @GuardedBy("this")
   @Override
   public synchronized void close() throws IOException {
      this.ciphertextChannel.close();
   }

   @GuardedBy("this")
   @Override
   public synchronized boolean isOpen() {
      return this.ciphertextChannel.isOpen();
   }
}
