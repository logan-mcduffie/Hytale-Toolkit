package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.StreamingAead;
import com.google.crypto.tink.subtle.RewindableReadableByteChannel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.security.GeneralSecurityException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import javax.annotation.concurrent.GuardedBy;

final class ReadableByteChannelDecrypter implements ReadableByteChannel {
   @GuardedBy("this")
   ReadableByteChannel attemptingChannel = null;
   @GuardedBy("this")
   ReadableByteChannel matchingChannel = null;
   @GuardedBy("this")
   RewindableReadableByteChannel ciphertextChannel;
   Deque<StreamingAead> remainingPrimitives = new ArrayDeque<>();
   byte[] associatedData;

   public ReadableByteChannelDecrypter(List<StreamingAead> allPrimitives, ReadableByteChannel ciphertextChannel, final byte[] associatedData) {
      for (StreamingAead primitive : allPrimitives) {
         this.remainingPrimitives.add(primitive);
      }

      this.ciphertextChannel = new RewindableReadableByteChannel(ciphertextChannel);
      this.associatedData = (byte[])associatedData.clone();
   }

   @GuardedBy("this")
   private synchronized ReadableByteChannel nextAttemptingChannel() throws IOException {
      while (!this.remainingPrimitives.isEmpty()) {
         StreamingAead streamingAead = this.remainingPrimitives.removeFirst();

         try {
            return streamingAead.newDecryptingChannel(this.ciphertextChannel, this.associatedData);
         } catch (GeneralSecurityException var3) {
            this.ciphertextChannel.rewind();
         }
      }

      throw new IOException("No matching key found for the ciphertext in the stream.");
   }

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
               this.ciphertextChannel.disableRewinding();
               return retValue;
            } catch (IOException var3) {
               this.ciphertextChannel.rewind();
               this.attemptingChannel = this.nextAttemptingChannel();
            }
         }
      }
   }

   @Override
   public synchronized void close() throws IOException {
      this.ciphertextChannel.close();
   }

   @Override
   public synchronized boolean isOpen() {
      return this.ciphertextChannel.isOpen();
   }
}
