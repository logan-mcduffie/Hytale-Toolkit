package org.bson;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

class Bits {
   static void readFully(InputStream inputStream, byte[] buffer) throws IOException {
      readFully(inputStream, buffer, 0, buffer.length);
   }

   static void readFully(InputStream inputStream, byte[] buffer, int offset, int length) throws IOException {
      if (buffer.length < length + offset) {
         throw new IllegalArgumentException("Buffer is too small");
      } else {
         int arrayOffset = offset;
         int bytesToRead = length;

         while (bytesToRead > 0) {
            int bytesRead = inputStream.read(buffer, arrayOffset, bytesToRead);
            if (bytesRead < 0) {
               throw new EOFException();
            }

            bytesToRead -= bytesRead;
            arrayOffset += bytesRead;
         }
      }
   }

   static int readInt(InputStream inputStream, byte[] buffer) throws IOException {
      readFully(inputStream, buffer, 0, 4);
      return readInt(buffer);
   }

   static int readInt(byte[] buffer) {
      return readInt(buffer, 0);
   }

   static int readInt(byte[] buffer, int offset) {
      int x = 0;
      x |= (255 & buffer[offset + 0]) << 0;
      x |= (255 & buffer[offset + 1]) << 8;
      x |= (255 & buffer[offset + 2]) << 16;
      return x | (0xFF & buffer[offset + 3]) << 24;
   }

   static long readLong(InputStream inputStream) throws IOException {
      return readLong(inputStream, new byte[8]);
   }

   static long readLong(InputStream inputStream, byte[] buffer) throws IOException {
      readFully(inputStream, buffer, 0, 8);
      return readLong(buffer);
   }

   static long readLong(byte[] buffer) {
      return readLong(buffer, 0);
   }

   static long readLong(byte[] buffer, int offset) {
      long x = 0L;
      x |= (255L & buffer[offset + 0]) << 0;
      x |= (255L & buffer[offset + 1]) << 8;
      x |= (255L & buffer[offset + 2]) << 16;
      x |= (255L & buffer[offset + 3]) << 24;
      x |= (255L & buffer[offset + 4]) << 32;
      x |= (255L & buffer[offset + 5]) << 40;
      x |= (255L & buffer[offset + 6]) << 48;
      return x | (255L & buffer[offset + 7]) << 56;
   }
}
