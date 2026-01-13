package com.google.crypto.tink.subtle;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.security.GeneralSecurityException;
import java.util.Arrays;

class StreamingAeadSeekableDecryptingChannel implements SeekableByteChannel {
   private static final int PLAINTEXT_SEGMENT_EXTRA_SIZE = 16;
   private final SeekableByteChannel ciphertextChannel;
   private final ByteBuffer ciphertextSegment;
   private final ByteBuffer plaintextSegment;
   private final ByteBuffer header;
   private final long ciphertextChannelSize;
   private final int numberOfSegments;
   private final int lastCiphertextSegmentSize;
   private final byte[] aad;
   private final StreamSegmentDecrypter decrypter;
   private long plaintextPosition;
   private long plaintextSize;
   private boolean headerRead;
   private boolean isCurrentSegmentDecrypted;
   private int currentSegmentNr;
   private boolean isopen;
   private final int plaintextSegmentSize;
   private final int ciphertextSegmentSize;
   private final int ciphertextOffset;
   private final int firstSegmentOffset;

   public StreamingAeadSeekableDecryptingChannel(NonceBasedStreamingAead streamAead, SeekableByteChannel ciphertext, byte[] associatedData) throws IOException, GeneralSecurityException {
      this.decrypter = streamAead.newStreamSegmentDecrypter();
      this.ciphertextChannel = ciphertext;
      this.header = ByteBuffer.allocate(streamAead.getHeaderLength());
      this.ciphertextSegmentSize = streamAead.getCiphertextSegmentSize();
      this.ciphertextSegment = ByteBuffer.allocate(this.ciphertextSegmentSize);
      this.plaintextSegmentSize = streamAead.getPlaintextSegmentSize();
      this.plaintextSegment = ByteBuffer.allocate(this.plaintextSegmentSize + 16);
      this.plaintextPosition = 0L;
      this.headerRead = false;
      this.currentSegmentNr = -1;
      this.isCurrentSegmentDecrypted = false;
      this.ciphertextChannelSize = this.ciphertextChannel.size();
      this.aad = Arrays.copyOf(associatedData, associatedData.length);
      this.isopen = this.ciphertextChannel.isOpen();
      int fullSegments = (int)(this.ciphertextChannelSize / this.ciphertextSegmentSize);
      int remainder = (int)(this.ciphertextChannelSize % this.ciphertextSegmentSize);
      int ciphertextOverhead = streamAead.getCiphertextOverhead();
      if (remainder > 0) {
         this.numberOfSegments = fullSegments + 1;
         if (remainder < ciphertextOverhead) {
            throw new IOException("Invalid ciphertext size");
         }

         this.lastCiphertextSegmentSize = remainder;
      } else {
         this.numberOfSegments = fullSegments;
         this.lastCiphertextSegmentSize = this.ciphertextSegmentSize;
      }

      this.ciphertextOffset = streamAead.getCiphertextOffset();
      this.firstSegmentOffset = this.ciphertextOffset - streamAead.getHeaderLength();
      if (this.firstSegmentOffset < 0) {
         throw new IOException("Invalid ciphertext offset or header length");
      } else {
         long overhead = (long)this.numberOfSegments * ciphertextOverhead + this.ciphertextOffset;
         if (overhead > this.ciphertextChannelSize) {
            throw new IOException("Ciphertext is too short");
         } else {
            this.plaintextSize = this.ciphertextChannelSize - overhead;
         }
      }
   }

   @Override
   public synchronized String toString() {
      StringBuilder res = new StringBuilder();

      String ctChannel;
      try {
         ctChannel = "position:" + this.ciphertextChannel.position();
      } catch (IOException var4) {
         ctChannel = "position: n/a";
      }

      res.append("StreamingAeadSeekableDecryptingChannel")
         .append("\nciphertextChannel")
         .append(ctChannel)
         .append("\nciphertextChannelSize:")
         .append(this.ciphertextChannelSize)
         .append("\nplaintextSize:")
         .append(this.plaintextSize)
         .append("\nciphertextSegmentSize:")
         .append(this.ciphertextSegmentSize)
         .append("\nnumberOfSegments:")
         .append(this.numberOfSegments)
         .append("\nheaderRead:")
         .append(this.headerRead)
         .append("\nplaintextPosition:")
         .append(this.plaintextPosition)
         .append("\nHeader")
         .append(" position:")
         .append(this.header.position())
         .append(" limit:")
         .append(this.header.limit())
         .append("\ncurrentSegmentNr:")
         .append(this.currentSegmentNr)
         .append("\nciphertextSgement")
         .append(" position:")
         .append(this.ciphertextSegment.position())
         .append(" limit:")
         .append(this.ciphertextSegment.limit())
         .append("\nisCurrentSegmentDecrypted:")
         .append(this.isCurrentSegmentDecrypted)
         .append("\nplaintextSegment")
         .append(" position:")
         .append(this.plaintextSegment.position())
         .append(" limit:")
         .append(this.plaintextSegment.limit());
      return res.toString();
   }

   @Override
   public synchronized long position() {
      return this.plaintextPosition;
   }

   @CanIgnoreReturnValue
   @Override
   public synchronized SeekableByteChannel position(long newPosition) {
      this.plaintextPosition = newPosition;
      return this;
   }

   private boolean tryReadHeader() throws IOException {
      this.ciphertextChannel.position(this.header.position() + this.firstSegmentOffset);
      int headerSize = this.header.remaining();
      int numBytesRead = this.ciphertextChannel.read(this.header);
      if (numBytesRead == -1) {
         throw new IOException("Unexpected end-of-stream: ciphertextChannel.read(header) returned -1");
      } else if (numBytesRead != headerSize - this.header.remaining()) {
         throw new IOException(
            "Unexpected return value from ciphertextChannel.read(header). headerSize = "
               + headerSize
               + ", header.remaining() = "
               + this.header.remaining()
               + ", but read returned "
               + numBytesRead
         );
      } else if (this.header.remaining() > 0) {
         return false;
      } else {
         this.header.flip();

         try {
            this.decrypter.init(this.header, this.aad);
            this.headerRead = true;
            return true;
         } catch (GeneralSecurityException var4) {
            throw new IOException(var4);
         }
      }
   }

   private int getSegmentNr(long plaintextPosition) {
      return (int)((plaintextPosition + this.ciphertextOffset) / this.plaintextSegmentSize);
   }

   private boolean tryLoadSegment(int segmentNr) throws IOException {
      if (segmentNr >= 0 && segmentNr < this.numberOfSegments) {
         boolean isLast = segmentNr == this.numberOfSegments - 1;
         if (segmentNr == this.currentSegmentNr) {
            if (this.isCurrentSegmentDecrypted) {
               return true;
            }
         } else {
            long ciphertextPosition = (long)segmentNr * this.ciphertextSegmentSize;
            int segmentSize = this.ciphertextSegmentSize;
            if (isLast) {
               segmentSize = this.lastCiphertextSegmentSize;
            }

            if (segmentNr == 0) {
               segmentSize -= this.ciphertextOffset;
               ciphertextPosition = this.ciphertextOffset;
            }

            this.ciphertextChannel.position(ciphertextPosition);
            this.ciphertextSegment.clear();
            this.ciphertextSegment.limit(segmentSize);
            this.currentSegmentNr = segmentNr;
            this.isCurrentSegmentDecrypted = false;
         }

         if (this.ciphertextSegment.remaining() > 0) {
            int remainingDataBeforeRead = this.ciphertextSegment.remaining();
            int numBytesRead = this.ciphertextChannel.read(this.ciphertextSegment);
            if (numBytesRead == -1) {
               throw new IOException("Unexpected end-of-stream: ciphertextChannel.read(ciphertextSegment) returned -1");
            }

            if (numBytesRead != remainingDataBeforeRead - this.ciphertextSegment.remaining()) {
               throw new IOException(
                  "Unexpected return value from ciphertextChannel.read(ciphertextSegment). remainingDataBeforeRead = "
                     + remainingDataBeforeRead
                     + ", ciphertextSegment.remaining() = "
                     + this.ciphertextSegment.remaining()
                     + ", but read returned "
                     + numBytesRead
               );
            }
         }

         if (this.ciphertextSegment.remaining() > 0) {
            return false;
         } else {
            this.ciphertextSegment.flip();
            this.plaintextSegment.clear();

            try {
               this.decrypter.decryptSegment(this.ciphertextSegment, segmentNr, isLast, this.plaintextSegment);
            } catch (GeneralSecurityException var6) {
               this.currentSegmentNr = -1;
               throw new IOException("Failed to decrypt", var6);
            }

            this.plaintextSegment.flip();
            this.isCurrentSegmentDecrypted = true;
            return true;
         }
      } else {
         throw new IOException("Invalid position");
      }
   }

   private boolean reachedEnd() {
      return this.plaintextPosition == this.plaintextSize
         && this.isCurrentSegmentDecrypted
         && this.currentSegmentNr == this.numberOfSegments - 1
         && this.plaintextSegment.remaining() == 0;
   }

   public synchronized int read(ByteBuffer dst, long start) throws IOException {
      long oldPosition = this.position();

      int var6;
      try {
         this.position(start);
         var6 = this.read(dst);
      } finally {
         this.position(oldPosition);
      }

      return var6;
   }

   @Override
   public synchronized int read(ByteBuffer dst) throws IOException {
      if (!this.isopen) {
         throw new ClosedChannelException();
      } else if (!this.headerRead && !this.tryReadHeader()) {
         return 0;
      } else {
         int startPos = dst.position();

         while (dst.remaining() > 0 && this.plaintextPosition < this.plaintextSize) {
            int segmentNr = this.getSegmentNr(this.plaintextPosition);
            int segmentOffset;
            if (segmentNr == 0) {
               segmentOffset = (int)this.plaintextPosition;
            } else {
               segmentOffset = (int)((this.plaintextPosition + this.ciphertextOffset) % this.plaintextSegmentSize);
            }

            if (!this.tryLoadSegment(segmentNr)) {
               break;
            }

            this.plaintextSegment.position(segmentOffset);
            if (this.plaintextSegment.remaining() <= dst.remaining()) {
               this.plaintextPosition = this.plaintextPosition + this.plaintextSegment.remaining();
               dst.put(this.plaintextSegment);
            } else {
               int sliceSize = dst.remaining();
               ByteBuffer slice = this.plaintextSegment.duplicate();
               slice.limit(slice.position() + sliceSize);
               dst.put(slice);
               this.plaintextPosition += sliceSize;
               this.plaintextSegment.position(this.plaintextSegment.position() + sliceSize);
            }
         }

         int read = dst.position() - startPos;
         return read == 0 && this.reachedEnd() ? -1 : read;
      }
   }

   @Override
   public long size() {
      return this.plaintextSize;
   }

   public synchronized long verifiedSize() throws IOException {
      if (this.tryLoadSegment(this.numberOfSegments - 1)) {
         return this.plaintextSize;
      } else {
         throw new IOException("could not verify the size");
      }
   }

   @Override
   public SeekableByteChannel truncate(long size) throws NonWritableChannelException {
      throw new NonWritableChannelException();
   }

   @Override
   public int write(ByteBuffer src) throws NonWritableChannelException {
      throw new NonWritableChannelException();
   }

   @Override
   public synchronized void close() throws IOException {
      this.ciphertextChannel.close();
      this.isopen = false;
   }

   @Override
   public synchronized boolean isOpen() {
      return this.isopen;
   }
}
