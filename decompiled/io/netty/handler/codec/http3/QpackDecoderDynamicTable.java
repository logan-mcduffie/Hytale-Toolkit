package io.netty.handler.codec.http3;

import java.util.Arrays;

final class QpackDecoderDynamicTable {
   private static final QpackException GET_ENTRY_ILLEGAL_INDEX_VALUE = QpackException.newStatic(
      QpackDecoderDynamicTable.class, "getEntry(...)", "QPACK - illegal decoder dynamic table index value"
   );
   private static final QpackException HEADER_TOO_LARGE = QpackException.newStatic(
      QpackDecoderDynamicTable.class, "add(...)", "QPACK - header entry too large."
   );
   private QpackHeaderField[] fields;
   private int head;
   private int tail;
   private long size;
   private long capacity = -1L;
   private int insertCount;

   int length() {
      return this.head < this.tail ? this.fields.length - this.tail + this.head : this.head - this.tail;
   }

   long size() {
      return this.size;
   }

   int insertCount() {
      return this.insertCount;
   }

   QpackHeaderField getEntry(int index) throws QpackException {
      if (index >= 0 && this.fields != null && index < this.fields.length) {
         QpackHeaderField entry = this.fields[index];
         if (entry == null) {
            throw GET_ENTRY_ILLEGAL_INDEX_VALUE;
         } else {
            return entry;
         }
      } else {
         throw GET_ENTRY_ILLEGAL_INDEX_VALUE;
      }
   }

   QpackHeaderField getEntryRelativeEncodedField(int index) throws QpackException {
      return this.getEntry(this.moduloIndex(index));
   }

   QpackHeaderField getEntryRelativeEncoderInstructions(int index) throws QpackException {
      return this.getEntry(index > this.tail ? this.fields.length - index + this.tail : this.tail - index);
   }

   void add(QpackHeaderField header) throws QpackException {
      long headerSize = header.size();
      if (headerSize > this.capacity) {
         throw HEADER_TOO_LARGE;
      } else {
         while (this.capacity - this.size < headerSize) {
            this.remove();
         }

         this.insertCount++;
         this.fields[this.getAndIncrementHead()] = header;
         this.size += headerSize;
      }
   }

   private void remove() {
      QpackHeaderField removed = this.fields[this.tail];
      if (removed != null) {
         this.size = this.size - removed.size();
         this.fields[this.getAndIncrementTail()] = null;
      }
   }

   void clear() {
      if (this.fields != null) {
         Arrays.fill(this.fields, null);
      }

      this.head = 0;
      this.tail = 0;
      this.size = 0L;
   }

   void setCapacity(long capacity) throws QpackException {
      if (capacity < 0L || capacity > 4294967295L) {
         throw new IllegalArgumentException("capacity is invalid: " + capacity);
      } else if (this.capacity != capacity) {
         this.capacity = capacity;
         if (capacity == 0L) {
            this.clear();
         } else {
            while (this.size > capacity) {
               this.remove();
            }
         }

         int maxEntries = QpackUtil.toIntOrThrow(2L * Math.floorDiv(capacity, 32L));
         if (this.fields == null || this.fields.length != maxEntries) {
            QpackHeaderField[] tmp = new QpackHeaderField[maxEntries];
            int len = this.length();
            if (this.fields != null && this.tail != this.head) {
               if (this.head > this.tail) {
                  System.arraycopy(this.fields, this.tail, tmp, 0, this.head - this.tail);
               } else {
                  System.arraycopy(this.fields, 0, tmp, 0, this.head);
                  System.arraycopy(this.fields, this.tail, tmp, this.head, this.fields.length - this.tail);
               }
            }

            this.tail = 0;
            this.head = this.tail + len;
            this.fields = tmp;
         }
      }
   }

   private int getAndIncrementHead() {
      int val = this.head;
      this.head = this.safeIncrementIndex(val);
      return val;
   }

   private int getAndIncrementTail() {
      int val = this.tail;
      this.tail = this.safeIncrementIndex(val);
      return val;
   }

   private int safeIncrementIndex(int index) {
      return ++index % this.fields.length;
   }

   private int moduloIndex(int index) {
      return this.fields == null ? index : index % this.fields.length;
   }
}
