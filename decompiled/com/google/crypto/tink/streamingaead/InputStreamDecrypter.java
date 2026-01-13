package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.StreamingAead;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import javax.annotation.concurrent.GuardedBy;

final class InputStreamDecrypter extends InputStream {
   @GuardedBy("this")
   boolean attemptedMatching = false;
   @GuardedBy("this")
   InputStream matchingStream = null;
   @GuardedBy("this")
   InputStream ciphertextStream;
   List<StreamingAead> primitives;
   byte[] associatedData;

   public InputStreamDecrypter(List<StreamingAead> primitives, InputStream ciphertextStream, final byte[] associatedData) {
      this.primitives = primitives;
      if (ciphertextStream.markSupported()) {
         this.ciphertextStream = ciphertextStream;
      } else {
         this.ciphertextStream = new BufferedInputStream(ciphertextStream);
      }

      this.ciphertextStream.mark(Integer.MAX_VALUE);
      this.associatedData = (byte[])associatedData.clone();
   }

   @GuardedBy("this")
   private void rewind() throws IOException {
      this.ciphertextStream.reset();
   }

   @GuardedBy("this")
   private void disableRewinding() throws IOException {
      this.ciphertextStream.mark(0);
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   @GuardedBy("this")
   @Override
   public synchronized int available() throws IOException {
      return this.matchingStream == null ? 0 : this.matchingStream.available();
   }

   @GuardedBy("this")
   @Override
   public synchronized int read() throws IOException {
      byte[] oneByte = new byte[1];
      return this.read(oneByte) == 1 ? oneByte[0] & 0xFF : -1;
   }

   @GuardedBy("this")
   @Override
   public synchronized int read(byte[] b) throws IOException {
      return this.read(b, 0, b.length);
   }

   @GuardedBy("this")
   @Override
   public synchronized int read(byte[] b, int offset, int len) throws IOException {
      if (len == 0) {
         return 0;
      } else if (this.matchingStream != null) {
         return this.matchingStream.read(b, offset, len);
      } else if (this.attemptedMatching) {
         throw new IOException("No matching key found for the ciphertext in the stream.");
      } else {
         this.attemptedMatching = true;

         for (StreamingAead streamingAead : this.primitives) {
            try {
               InputStream attemptedStream = streamingAead.newDecryptingStream(this.ciphertextStream, this.associatedData);
               int retValue = attemptedStream.read(b, offset, len);
               if (retValue == 0) {
                  throw new IOException("Could not read bytes from the ciphertext stream");
               }

               this.matchingStream = attemptedStream;
               this.disableRewinding();
               return retValue;
            } catch (IOException var8) {
               this.rewind();
            } catch (GeneralSecurityException var9) {
               this.rewind();
            }
         }

         throw new IOException("No matching key found for the ciphertext in the stream.");
      }
   }

   @GuardedBy("this")
   @Override
   public synchronized void close() throws IOException {
      this.ciphertextStream.close();
   }
}
