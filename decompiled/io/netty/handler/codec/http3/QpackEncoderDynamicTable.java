package io.netty.handler.codec.http3;

import io.netty.util.AsciiString;
import io.netty.util.internal.MathUtil;
import org.jetbrains.annotations.Nullable;

final class QpackEncoderDynamicTable {
   private static final QpackException INVALID_KNOW_RECEIVED_COUNT_INCREMENT = QpackException.newStatic(
      QpackDecoder.class, "incrementKnownReceivedCount(...)", "QPACK - invalid known received count increment."
   );
   private static final QpackException INVALID_REQUIRED_INSERT_COUNT_INCREMENT = QpackException.newStatic(
      QpackDecoder.class, "acknowledgeInsertCount(...)", "QPACK - invalid required insert count acknowledgment."
   );
   private static final QpackException INVALID_TABLE_CAPACITY = QpackException.newStatic(
      QpackDecoder.class, "validateCapacity(...)", "QPACK - dynamic table capacity is invalid."
   );
   private static final QpackException CAPACITY_ALREADY_SET = QpackException.newStatic(
      QpackDecoder.class, "maxTableCapacity(...)", "QPACK - dynamic table capacity is already set."
   );
   public static final int NOT_FOUND = Integer.MIN_VALUE;
   private final QpackEncoderDynamicTable.HeaderEntry[] fields;
   private final int expectedFreeCapacityPercentage;
   private final byte hashMask;
   private long size;
   private long maxTableCapacity = -1L;
   private final QpackEncoderDynamicTable.HeaderEntry head;
   private QpackEncoderDynamicTable.HeaderEntry drain;
   private QpackEncoderDynamicTable.HeaderEntry knownReceived;
   private QpackEncoderDynamicTable.HeaderEntry tail;

   QpackEncoderDynamicTable() {
      this(16, 10);
   }

   QpackEncoderDynamicTable(int arraySizeHint, int expectedFreeCapacityPercentage) {
      this.fields = new QpackEncoderDynamicTable.HeaderEntry[MathUtil.findNextPositivePowerOfTwo(Math.max(2, Math.min(arraySizeHint, 128)))];
      this.hashMask = (byte)(this.fields.length - 1);
      this.head = new QpackEncoderDynamicTable.HeaderEntry(-1, AsciiString.EMPTY_STRING, AsciiString.EMPTY_STRING, -1, null);
      this.expectedFreeCapacityPercentage = expectedFreeCapacityPercentage;
      this.resetIndicesToHead();
   }

   int add(CharSequence name, CharSequence value, long headerSize) {
      if (this.maxTableCapacity - this.size < headerSize) {
         return -1;
      } else if (this.tail.index == Integer.MAX_VALUE) {
         this.evictUnreferencedEntries();
         return -1;
      } else {
         int h = AsciiString.hashCode(name);
         int i = this.index(h);
         QpackEncoderDynamicTable.HeaderEntry old = this.fields[i];
         QpackEncoderDynamicTable.HeaderEntry e = new QpackEncoderDynamicTable.HeaderEntry(h, name, value, this.tail.index + 1, old);
         this.fields[i] = e;
         e.addNextTo(this.tail);
         this.tail = e;
         this.size += headerSize;
         this.ensureFreeCapacity();
         return e.index;
      }
   }

   void acknowledgeInsertCountOnAck(int entryIndex) throws QpackException {
      this.acknowledgeInsertCount(entryIndex, true);
   }

   void acknowledgeInsertCountOnCancellation(int entryIndex) throws QpackException {
      this.acknowledgeInsertCount(entryIndex, false);
   }

   private void acknowledgeInsertCount(int entryIndex, boolean updateKnownReceived) throws QpackException {
      if (entryIndex < 0) {
         throw INVALID_REQUIRED_INSERT_COUNT_INCREMENT;
      } else {
         for (QpackEncoderDynamicTable.HeaderEntry e = this.head.next; e != null; e = e.next) {
            if (e.index == entryIndex) {
               assert e.refCount > 0;

               e.refCount--;
               if (updateKnownReceived && e.index > this.knownReceived.index) {
                  this.knownReceived = e;
               }

               this.evictUnreferencedEntries();
               return;
            }
         }

         throw INVALID_REQUIRED_INSERT_COUNT_INCREMENT;
      }
   }

   void incrementKnownReceivedCount(int knownReceivedCountIncr) throws QpackException {
      if (knownReceivedCountIncr <= 0) {
         throw INVALID_KNOW_RECEIVED_COUNT_INCREMENT;
      } else {
         while (this.knownReceived.next != null && knownReceivedCountIncr > 0) {
            this.knownReceived = this.knownReceived.next;
            knownReceivedCountIncr--;
         }

         if (knownReceivedCountIncr == 0) {
            this.evictUnreferencedEntries();
         } else {
            throw INVALID_KNOW_RECEIVED_COUNT_INCREMENT;
         }
      }
   }

   int insertCount() {
      return this.tail.index + 1;
   }

   int encodedRequiredInsertCount(int reqInsertCount) {
      return reqInsertCount == 0 ? 0 : reqInsertCount % Math.toIntExact(2L * QpackUtil.maxEntries(this.maxTableCapacity)) + 1;
   }

   int encodedKnownReceivedCount() {
      return this.encodedRequiredInsertCount(this.knownReceived.index + 1);
   }

   void maxTableCapacity(long capacity) throws QpackException {
      validateCapacity(capacity);
      if (this.maxTableCapacity >= 0L) {
         throw CAPACITY_ALREADY_SET;
      } else {
         this.maxTableCapacity = capacity;
      }
   }

   int relativeIndexForEncoderInstructions(int entryIndex) {
      assert entryIndex >= 0;

      assert entryIndex <= this.tail.index;

      return this.tail.index - entryIndex;
   }

   int getEntryIndex(@Nullable CharSequence name, @Nullable CharSequence value) {
      if (this.tail != this.head && name != null && value != null) {
         int h = AsciiString.hashCode(name);
         int i = this.index(h);
         QpackEncoderDynamicTable.HeaderEntry firstNameMatch = null;
         QpackEncoderDynamicTable.HeaderEntry entry = null;

         for (QpackEncoderDynamicTable.HeaderEntry e = this.fields[i]; e != null; e = e.nextSibling) {
            if (e.hash == h && QpackUtil.equalsVariableTime(value, e.value)) {
               if (QpackUtil.equalsVariableTime(name, e.name)) {
                  entry = e;
                  break;
               }
            } else if (firstNameMatch == null && QpackUtil.equalsVariableTime(name, e.name)) {
               firstNameMatch = e;
            }
         }

         if (entry != null) {
            return entry.index;
         }

         if (firstNameMatch != null) {
            return -firstNameMatch.index - 1;
         }
      }

      return Integer.MIN_VALUE;
   }

   int addReferenceToEntry(@Nullable CharSequence name, @Nullable CharSequence value, int idx) {
      if (this.tail != this.head && name != null && value != null) {
         int h = AsciiString.hashCode(name);
         int i = this.index(h);

         for (QpackEncoderDynamicTable.HeaderEntry e = this.fields[i]; e != null; e = e.nextSibling) {
            if (e.hash == h && idx == e.index) {
               e.refCount++;
               return e.index + 1;
            }
         }
      }

      throw new IllegalArgumentException("Index " + idx + " not found");
   }

   boolean requiresDuplication(int idx, long size) {
      assert this.head != this.tail;

      return this.size + size <= this.maxTableCapacity && this.head != this.drain ? idx >= this.head.next.index && idx <= this.drain.index : false;
   }

   private void evictUnreferencedEntries() {
      if (this.head != this.knownReceived && this.head != this.drain) {
         while (this.head.next != null && this.head.next != this.knownReceived.next && this.head.next != this.drain.next) {
            if (!this.removeIfUnreferenced()) {
               return;
            }
         }
      }
   }

   private boolean removeIfUnreferenced() {
      QpackEncoderDynamicTable.HeaderEntry toRemove = this.head.next;
      if (toRemove.refCount != 0) {
         return false;
      } else {
         this.size = this.size - toRemove.size();
         int i = this.index(toRemove.hash);
         QpackEncoderDynamicTable.HeaderEntry e = this.fields[i];

         QpackEncoderDynamicTable.HeaderEntry prev;
         for (prev = null; e != null && e != toRemove; e = e.nextSibling) {
            prev = e;
         }

         if (e == toRemove) {
            if (prev == null) {
               this.fields[i] = e.nextSibling;
            } else {
               prev.nextSibling = e.nextSibling;
            }
         }

         toRemove.remove(this.head);
         if (toRemove == this.tail) {
            this.resetIndicesToHead();
         }

         if (toRemove == this.drain) {
            this.drain = this.head;
         }

         if (toRemove == this.knownReceived) {
            this.knownReceived = this.head;
         }

         return true;
      }
   }

   private void resetIndicesToHead() {
      this.tail = this.head;
      this.drain = this.head;
      this.knownReceived = this.head;
   }

   private void ensureFreeCapacity() {
      long maxDesiredSize = Math.max(32L, (100 - this.expectedFreeCapacityPercentage) * this.maxTableCapacity / 100L);
      long cSize = this.size;

      QpackEncoderDynamicTable.HeaderEntry nDrain;
      for (nDrain = this.head; nDrain.next != null && cSize > maxDesiredSize; nDrain = nDrain.next) {
         cSize -= nDrain.next.size();
      }

      if (cSize != this.size) {
         this.drain = nDrain;
         this.evictUnreferencedEntries();
      }
   }

   private int index(int h) {
      return h & this.hashMask;
   }

   private static void validateCapacity(long capacity) throws QpackException {
      if (capacity < 0L || capacity > 4294967295L) {
         throw INVALID_TABLE_CAPACITY;
      }
   }

   private static final class HeaderEntry extends QpackHeaderField {
      QpackEncoderDynamicTable.HeaderEntry next;
      QpackEncoderDynamicTable.HeaderEntry nextSibling;
      int refCount;
      final int hash;
      final int index;

      HeaderEntry(int hash, CharSequence name, CharSequence value, int index, @Nullable QpackEncoderDynamicTable.HeaderEntry nextSibling) {
         super(name, value);
         this.index = index;
         this.hash = hash;
         this.nextSibling = nextSibling;
      }

      void remove(QpackEncoderDynamicTable.HeaderEntry prev) {
         assert prev != this;

         prev.next = this.next;
         this.next = null;
         this.nextSibling = null;
      }

      void addNextTo(QpackEncoderDynamicTable.HeaderEntry prev) {
         assert prev != this;

         this.next = prev.next;
         prev.next = this;
      }
   }
}
