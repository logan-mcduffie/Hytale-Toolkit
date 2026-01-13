package org.bson.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.bson.BsonSerializationException;
import org.bson.ByteBuf;
import org.bson.types.ObjectId;

public abstract class OutputBuffer extends OutputStream implements BsonOutput {
   @Override
   public void write(byte[] b) {
      this.write(b, 0, b.length);
   }

   @Override
   public void close() {
   }

   @Override
   public void write(byte[] bytes, int offset, int length) {
      this.writeBytes(bytes, offset, length);
   }

   @Override
   public void writeBytes(byte[] bytes) {
      this.writeBytes(bytes, 0, bytes.length);
   }

   @Override
   public void writeInt32(int value) {
      this.write(value >> 0);
      this.write(value >> 8);
      this.write(value >> 16);
      this.write(value >> 24);
   }

   @Override
   public void writeInt32(int position, int value) {
      this.write(position, value >> 0);
      this.write(position + 1, value >> 8);
      this.write(position + 2, value >> 16);
      this.write(position + 3, value >> 24);
   }

   @Override
   public void writeInt64(long value) {
      this.write((byte)(255L & value >> 0));
      this.write((byte)(255L & value >> 8));
      this.write((byte)(255L & value >> 16));
      this.write((byte)(255L & value >> 24));
      this.write((byte)(255L & value >> 32));
      this.write((byte)(255L & value >> 40));
      this.write((byte)(255L & value >> 48));
      this.write((byte)(255L & value >> 56));
   }

   @Override
   public void writeDouble(double x) {
      this.writeLong(Double.doubleToRawLongBits(x));
   }

   @Override
   public void writeString(String str) {
      this.writeInt(0);
      int strLen = this.writeCharacters(str, false);
      this.writeInt32(this.getPosition() - strLen - 4, strLen);
   }

   @Override
   public void writeCString(String value) {
      this.writeCharacters(value, true);
   }

   @Override
   public void writeObjectId(ObjectId value) {
      this.write(value.toByteArray());
   }

   public int size() {
      return this.getSize();
   }

   public abstract int pipe(OutputStream var1) throws IOException;

   public abstract List<ByteBuf> getByteBuffers();

   @Override
   public abstract void truncateToPosition(int var1);

   public byte[] toByteArray() {
      try {
         ByteArrayOutputStream bout = new ByteArrayOutputStream(this.size());
         this.pipe(bout);
         return bout.toByteArray();
      } catch (IOException var2) {
         throw new RuntimeException("should be impossible", var2);
      }
   }

   @Override
   public void write(int value) {
      this.writeByte(value);
   }

   public void writeInt(int value) {
      this.writeInt32(value);
   }

   @Override
   public String toString() {
      return this.getClass().getName() + " size: " + this.size() + " pos: " + this.getPosition();
   }

   protected abstract void write(int var1, int var2);

   public void writeLong(long value) {
      this.writeInt64(value);
   }

   private int writeCharacters(String str, boolean checkForNullCharacters) {
      int len = str.length();
      int total = 0;
      int i = 0;

      while (i < len) {
         int c = Character.codePointAt(str, i);
         if (checkForNullCharacters && c == 0) {
            throw new BsonSerializationException(String.format("BSON cstring '%s' is not valid because it contains a null character at index %d", str, i));
         }

         if (c < 128) {
            this.write((byte)c);
            total++;
         } else if (c < 2048) {
            this.write((byte)(192 + (c >> 6)));
            this.write((byte)(128 + (c & 63)));
            total += 2;
         } else if (c < 65536) {
            this.write((byte)(224 + (c >> 12)));
            this.write((byte)(128 + (c >> 6 & 63)));
            this.write((byte)(128 + (c & 63)));
            total += 3;
         } else {
            this.write((byte)(240 + (c >> 18)));
            this.write((byte)(128 + (c >> 12 & 63)));
            this.write((byte)(128 + (c >> 6 & 63)));
            this.write((byte)(128 + (c & 63)));
            total += 4;
         }

         i += Character.charCount(c);
      }

      this.write(0);
      return total + 1;
   }
}
