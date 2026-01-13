package org.bson;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class LazyBSONDecoder implements BSONDecoder {
   private static final int BYTES_IN_INTEGER = 4;

   @Override
   public BSONObject readObject(byte[] bytes) {
      BSONCallback bsonCallback = new LazyBSONCallback();
      this.decode(bytes, bsonCallback);
      return (BSONObject)bsonCallback.get();
   }

   @Override
   public BSONObject readObject(InputStream in) throws IOException {
      BSONCallback bsonCallback = new LazyBSONCallback();
      this.decode(in, bsonCallback);
      return (BSONObject)bsonCallback.get();
   }

   @Override
   public int decode(byte[] bytes, BSONCallback callback) {
      try {
         return this.decode(new ByteArrayInputStream(bytes), callback);
      } catch (IOException var4) {
         throw new BSONException("Invalid bytes received", var4);
      }
   }

   @Override
   public int decode(InputStream in, BSONCallback callback) throws IOException {
      byte[] documentSizeBuffer = new byte[4];
      int documentSize = Bits.readInt(in, documentSizeBuffer);
      byte[] documentBytes = Arrays.copyOf(documentSizeBuffer, documentSize);
      Bits.readFully(in, documentBytes, 4, documentSize - 4);
      callback.gotBinary(null, (byte)0, documentBytes);
      return documentSize;
   }
}
