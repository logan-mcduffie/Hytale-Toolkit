package com.google.crypto.tink.subtle;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;

class StreamingAeadDecryptingStream extends FilterInputStream {
   private static final int PLAINTEXT_SEGMENT_EXTRA_SIZE = 16;
   private final ByteBuffer ciphertextSegment;
   private final ByteBuffer plaintextSegment;
   private final int headerLength;
   private boolean headerRead;
   private boolean endOfCiphertext;
   private boolean endOfPlaintext;
   private boolean decryptionErrorOccured;
   private final byte[] aad;
   private int segmentNr;
   private final StreamSegmentDecrypter decrypter;
   private final int ciphertextSegmentSize;
   private final int firstCiphertextSegmentSize;

   private static Buffer toBuffer(ByteBuffer b) {
      return b;
   }

   public StreamingAeadDecryptingStream(NonceBasedStreamingAead streamAead, InputStream ciphertextStream, byte[] associatedData) throws GeneralSecurityException, IOException {
      super(ciphertextStream);
      this.decrypter = streamAead.newStreamSegmentDecrypter();
      this.headerLength = streamAead.getHeaderLength();
      this.aad = Arrays.copyOf(associatedData, associatedData.length);
      this.ciphertextSegmentSize = streamAead.getCiphertextSegmentSize();
      this.ciphertextSegment = ByteBuffer.allocate(this.ciphertextSegmentSize + 1);
      toBuffer(this.ciphertextSegment).limit(0);
      this.firstCiphertextSegmentSize = this.ciphertextSegmentSize - streamAead.getCiphertextOffset();
      this.plaintextSegment = ByteBuffer.allocate(streamAead.getPlaintextSegmentSize() + 16);
      toBuffer(this.plaintextSegment).limit(0);
      this.headerRead = false;
      this.endOfCiphertext = false;
      this.endOfPlaintext = false;
      this.segmentNr = 0;
      this.decryptionErrorOccured = false;
   }

   private void readHeader() throws IOException {
      if (this.headerRead) {
         this.setDecryptionErrorOccured();
         throw new IOException("Decryption failed.");
      } else {
         ByteBuffer header = ByteBuffer.allocate(this.headerLength);

         while (header.remaining() > 0) {
            int read = this.in.read(header.array(), toBuffer(header).position(), header.remaining());
            if (read < 0) {
               this.setDecryptionErrorOccured();
               throw new IOException("Ciphertext is too short");
            }

            if (read == 0) {
               throw new IOException("Could not read bytes from the ciphertext stream");
            }

            toBuffer(header).position(toBuffer(header).position() + read);
         }

         toBuffer(header).flip();

         try {
            this.decrypter.init(header, this.aad);
         } catch (GeneralSecurityException var3) {
            throw new IOException(var3);
         }

         this.headerRead = true;
      }
   }

   private void setDecryptionErrorOccured() {
      this.decryptionErrorOccured = true;
      toBuffer(this.plaintextSegment).limit(0);
   }

   private void loadSegment() throws IOException {
      while (!this.endOfCiphertext && this.ciphertextSegment.remaining() > 0) {
         int read = this.in.read(this.ciphertextSegment.array(), this.ciphertextSegment.position(), this.ciphertextSegment.remaining());
         if (read > 0) {
            toBuffer(this.ciphertextSegment).position(toBuffer(this.ciphertextSegment).position() + read);
         } else {
            if (read != -1) {
               if (read == 0) {
                  throw new IOException("Could not read bytes from the ciphertext stream");
               }

               throw new IOException("Unexpected return value from in.read");
            }

            this.endOfCiphertext = true;
         }
      }

      byte lastByte = 0;
      if (!this.endOfCiphertext) {
         lastByte = this.ciphertextSegment.get(toBuffer(this.ciphertextSegment).position() - 1);
         toBuffer(this.ciphertextSegment).position(toBuffer(this.ciphertextSegment).position() - 1);
      }

      toBuffer(this.ciphertextSegment).flip();
      toBuffer(this.plaintextSegment).clear();

      try {
         this.decrypter.decryptSegment(this.ciphertextSegment, this.segmentNr, this.endOfCiphertext, this.plaintextSegment);
      } catch (GeneralSecurityException var3) {
         this.setDecryptionErrorOccured();
         throw new IOException(var3.getMessage() + "\n" + this.toString() + "\nsegmentNr:" + this.segmentNr + " endOfCiphertext:" + this.endOfCiphertext, var3);
      }

      this.segmentNr++;
      toBuffer(this.plaintextSegment).flip();
      toBuffer(this.ciphertextSegment).clear();
      if (!this.endOfCiphertext) {
         toBuffer(this.ciphertextSegment).clear();
         toBuffer(this.ciphertextSegment).limit(this.ciphertextSegmentSize + 1);
         this.ciphertextSegment.put(lastByte);
      }
   }

   @Override
   public int read() throws IOException {
      byte[] oneByte = new byte[1];
      int ret = this.read(oneByte, 0, 1);
      if (ret == 1) {
         return oneByte[0] & 0xFF;
      } else if (ret == -1) {
         return ret;
      } else {
         throw new IOException("Reading failed");
      }
   }

   @Override
   public int read(byte[] dst) throws IOException {
      return this.read(dst, 0, dst.length);
   }

   @Override
   public synchronized int read(byte[] dst, int offset, int length) throws IOException {
      if (this.decryptionErrorOccured) {
         throw new IOException("Decryption failed.");
      } else {
         if (!this.headerRead) {
            this.readHeader();
            toBuffer(this.ciphertextSegment).clear();
            toBuffer(this.ciphertextSegment).limit(this.firstCiphertextSegmentSize + 1);
         }

         if (this.endOfPlaintext) {
            return -1;
         } else {
            int bytesRead = 0;

            while (bytesRead < length) {
               if (this.plaintextSegment.remaining() == 0) {
                  if (this.endOfCiphertext) {
                     this.endOfPlaintext = true;
                     break;
                  }

                  this.loadSegment();
               }

               int sliceSize = Math.min(this.plaintextSegment.remaining(), length - bytesRead);
               this.plaintextSegment.get(dst, bytesRead + offset, sliceSize);
               bytesRead += sliceSize;
            }

            return bytesRead == 0 && this.endOfPlaintext ? -1 : bytesRead;
         }
      }
   }

   @Override
   public synchronized void close() throws IOException {
      super.close();
   }

   @Override
   public synchronized int available() {
      return this.plaintextSegment.remaining();
   }

   @Override
   public synchronized void mark(int readlimit) {
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   @Override
   public long skip(long n) throws IOException {
      long maxSkipBufferSize = this.ciphertextSegmentSize;
      long remaining = n;
      if (n <= 0L) {
         return 0L;
      } else {
         int size = (int)Math.min(maxSkipBufferSize, n);
         byte[] skipBuffer = new byte[size];

         while (remaining > 0L) {
            int bytesRead = this.read(skipBuffer, 0, (int)Math.min((long)size, remaining));
            if (bytesRead <= 0) {
               break;
            }

            remaining -= bytesRead;
         }

         return n - remaining;
      }
   }

   @Override
   public synchronized String toString() {
      StringBuilder res = new StringBuilder();
      res.append("StreamingAeadDecryptingStream")
         .append("\nsegmentNr:")
         .append(this.segmentNr)
         .append("\nciphertextSegmentSize:")
         .append(this.ciphertextSegmentSize)
         .append("\nheaderRead:")
         .append(this.headerRead)
         .append("\nendOfCiphertext:")
         .append(this.endOfCiphertext)
         .append("\nendOfPlaintext:")
         .append(this.endOfPlaintext)
         .append("\ndecryptionErrorOccured:")
         .append(this.decryptionErrorOccured)
         .append("\nciphertextSgement")
         .append(" position:")
         .append(this.ciphertextSegment.position())
         .append(" limit:")
         .append(this.ciphertextSegment.limit())
         .append("\nplaintextSegment")
         .append(" position:")
         .append(this.plaintextSegment.position())
         .append(" limit:")
         .append(this.plaintextSegment.limit());
      return res.toString();
   }
}
