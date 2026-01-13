package org.bson;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.bson.assertions.Assertions;
import org.bson.io.ByteBufferBsonInput;

public class RawBsonArray extends BsonArray implements Serializable {
   private static final long serialVersionUID = 2L;
   private static final String IMMUTABLE_MSG = "RawBsonArray instances are immutable";
   private final transient RawBsonArray.RawBsonArrayList delegate;

   public RawBsonArray(byte[] bytes) {
      this(Assertions.notNull("bytes", bytes), 0, bytes.length);
   }

   public RawBsonArray(byte[] bytes, int offset, int length) {
      this(new RawBsonArray.RawBsonArrayList(bytes, offset, length));
   }

   private RawBsonArray(RawBsonArray.RawBsonArrayList values) {
      super(values, false);
      this.delegate = values;
   }

   ByteBuf getByteBuffer() {
      return this.delegate.getByteBuffer();
   }

   @Override
   public boolean add(BsonValue bsonValue) {
      throw new UnsupportedOperationException("RawBsonArray instances are immutable");
   }

   @Override
   public boolean remove(Object o) {
      throw new UnsupportedOperationException("RawBsonArray instances are immutable");
   }

   @Override
   public boolean addAll(Collection<? extends BsonValue> c) {
      throw new UnsupportedOperationException("RawBsonArray instances are immutable");
   }

   @Override
   public boolean addAll(int index, Collection<? extends BsonValue> c) {
      throw new UnsupportedOperationException("RawBsonArray instances are immutable");
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      throw new UnsupportedOperationException("RawBsonArray instances are immutable");
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      throw new UnsupportedOperationException("RawBsonArray instances are immutable");
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException("RawBsonArray instances are immutable");
   }

   @Override
   public BsonValue set(int index, BsonValue element) {
      throw new UnsupportedOperationException("RawBsonArray instances are immutable");
   }

   @Override
   public void add(int index, BsonValue element) {
      throw new UnsupportedOperationException("RawBsonArray instances are immutable");
   }

   @Override
   public BsonValue remove(int index) {
      throw new UnsupportedOperationException("RawBsonArray instances are immutable");
   }

   @Override
   public BsonArray clone() {
      return new RawBsonArray((byte[])this.delegate.bytes.clone(), this.delegate.offset, this.delegate.length);
   }

   @Override
   public boolean equals(Object o) {
      return super.equals(o);
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   private Object writeReplace() {
      return new RawBsonArray.SerializationProxy(this.delegate.bytes, this.delegate.offset, this.delegate.length);
   }

   private void readObject(ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   static class RawBsonArrayList extends AbstractList<BsonValue> {
      private static final int MIN_BSON_ARRAY_SIZE = 5;
      private Integer cachedSize;
      private final byte[] bytes;
      private final int offset;
      private final int length;

      RawBsonArrayList(byte[] bytes, int offset, int length) {
         Assertions.notNull("bytes", bytes);
         Assertions.isTrueArgument("offset >= 0", offset >= 0);
         Assertions.isTrueArgument("offset < bytes.length", offset < bytes.length);
         Assertions.isTrueArgument("length <= bytes.length - offset", length <= bytes.length - offset);
         Assertions.isTrueArgument("length >= 5", length >= 5);
         this.bytes = bytes;
         this.offset = offset;
         this.length = length;
      }

      public BsonValue get(int index) {
         if (index < 0) {
            throw new IndexOutOfBoundsException();
         } else {
            int curIndex = 0;
            BsonBinaryReader bsonReader = this.createReader();

            try {
               bsonReader.readStartDocument();

               while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                  bsonReader.skipName();
                  if (curIndex == index) {
                     return RawBsonValueHelper.decode(this.bytes, bsonReader);
                  }

                  bsonReader.skipValue();
                  curIndex++;
               }

               bsonReader.readEndDocument();
               throw new IndexOutOfBoundsException();
            } finally {
               bsonReader.close();
            }
         }
      }

      @Override
      public int size() {
         if (this.cachedSize != null) {
            return this.cachedSize;
         } else {
            int size = 0;
            BsonBinaryReader bsonReader = this.createReader();

            try {
               bsonReader.readStartDocument();

               while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                  size++;
                  bsonReader.readName();
                  bsonReader.skipValue();
               }

               bsonReader.readEndDocument();
            } finally {
               bsonReader.close();
            }

            this.cachedSize = size;
            return this.cachedSize;
         }
      }

      @Override
      public Iterator<BsonValue> iterator() {
         return new RawBsonArray.RawBsonArrayList.Itr();
      }

      @Override
      public ListIterator<BsonValue> listIterator() {
         return new RawBsonArray.RawBsonArrayList.ListItr(0);
      }

      @Override
      public ListIterator<BsonValue> listIterator(int index) {
         return new RawBsonArray.RawBsonArrayList.ListItr(index);
      }

      private BsonBinaryReader createReader() {
         return new BsonBinaryReader(new ByteBufferBsonInput(this.getByteBuffer()));
      }

      ByteBuf getByteBuffer() {
         ByteBuffer buffer = ByteBuffer.wrap(this.bytes, this.offset, this.length);
         buffer.order(ByteOrder.LITTLE_ENDIAN);
         return new ByteBufNIO(buffer);
      }

      private class Itr implements Iterator<BsonValue> {
         private int cursor = 0;
         private BsonBinaryReader bsonReader;
         private int currentPosition = 0;

         Itr() {
            this(0);
         }

         Itr(int cursorPosition) {
            this.setIterator(cursorPosition);
         }

         @Override
         public boolean hasNext() {
            boolean hasNext = this.cursor != RawBsonArrayList.this.size();
            if (!hasNext) {
               this.bsonReader.close();
            }

            return hasNext;
         }

         public BsonValue next() {
            while (this.cursor > this.currentPosition && this.bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
               this.bsonReader.skipName();
               this.bsonReader.skipValue();
               this.currentPosition++;
            }

            if (this.bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
               this.bsonReader.skipName();
               this.cursor++;
               this.currentPosition = this.cursor;
               return RawBsonValueHelper.decode(RawBsonArrayList.this.bytes, this.bsonReader);
            } else {
               this.bsonReader.close();
               throw new NoSuchElementException();
            }
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("RawBsonArray instances are immutable");
         }

         public int getCursor() {
            return this.cursor;
         }

         public void setCursor(int cursor) {
            this.cursor = cursor;
         }

         void setIterator(int cursorPosition) {
            this.cursor = cursorPosition;
            this.currentPosition = 0;
            if (this.bsonReader != null) {
               this.bsonReader.close();
            }

            this.bsonReader = RawBsonArrayList.this.createReader();
            this.bsonReader.readStartDocument();
         }
      }

      private class ListItr extends RawBsonArray.RawBsonArrayList.Itr implements ListIterator<BsonValue> {
         ListItr(int index) {
            super(index);
         }

         @Override
         public boolean hasPrevious() {
            return this.getCursor() != 0;
         }

         public BsonValue previous() {
            try {
               BsonValue previous = RawBsonArrayList.this.get(this.previousIndex());
               this.setIterator(this.previousIndex());
               return previous;
            } catch (IndexOutOfBoundsException var2) {
               throw new NoSuchElementException();
            }
         }

         @Override
         public int nextIndex() {
            return this.getCursor();
         }

         @Override
         public int previousIndex() {
            return this.getCursor() - 1;
         }

         public void set(BsonValue bsonValue) {
            throw new UnsupportedOperationException("RawBsonArray instances are immutable");
         }

         public void add(BsonValue bsonValue) {
            throw new UnsupportedOperationException("RawBsonArray instances are immutable");
         }
      }
   }

   private static class SerializationProxy implements Serializable {
      private static final long serialVersionUID = 1L;
      private final byte[] bytes;

      SerializationProxy(byte[] bytes, int offset, int length) {
         if (bytes.length == length) {
            this.bytes = bytes;
         } else {
            this.bytes = new byte[length];
            System.arraycopy(bytes, offset, this.bytes, 0, length);
         }
      }

      private Object readResolve() {
         return new RawBsonArray(this.bytes);
      }
   }
}
