package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.collection.LongObjectHashMap;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.Map.Entry;

final class QpackEncoder {
   private static final QpackException INVALID_SECTION_ACKNOWLEDGMENT = QpackException.newStatic(
      QpackDecoder.class, "sectionAcknowledgment(...)", "QPACK - section acknowledgment received for unknown stream."
   );
   private static final int DYNAMIC_TABLE_ENCODE_NOT_DONE = -1;
   private static final int DYNAMIC_TABLE_ENCODE_NOT_POSSIBLE = -2;
   private final QpackHuffmanEncoder huffmanEncoder = new QpackHuffmanEncoder();
   private final QpackEncoderDynamicTable dynamicTable;
   private int maxBlockedStreams;
   private int blockedStreams;
   private LongObjectHashMap<Queue<QpackEncoder.Indices>> streamSectionTrackers;

   QpackEncoder() {
      this(new QpackEncoderDynamicTable());
   }

   QpackEncoder(QpackEncoderDynamicTable dynamicTable) {
      this.dynamicTable = dynamicTable;
   }

   void encodeHeaders(QpackAttributes qpackAttributes, ByteBuf out, ByteBufAllocator allocator, long streamId, Http3Headers headers) {
      int base = this.dynamicTable.insertCount();
      ByteBuf tmp = allocator.buffer();

      try {
         int maxDynamicTblIdx = -1;
         int requiredInsertCount = 0;
         QpackEncoder.Indices dynamicTableIndices = null;

         for (Entry<CharSequence, CharSequence> header : headers) {
            CharSequence name = header.getKey();
            CharSequence value = header.getValue();
            int dynamicTblIdx = this.encodeHeader(qpackAttributes, tmp, base, name, value);
            if (dynamicTblIdx >= 0) {
               int req = this.dynamicTable.addReferenceToEntry(name, value, dynamicTblIdx);
               if (dynamicTblIdx > maxDynamicTblIdx) {
                  maxDynamicTblIdx = dynamicTblIdx;
                  requiredInsertCount = req;
               }

               if (dynamicTableIndices == null) {
                  dynamicTableIndices = new QpackEncoder.Indices();
               }

               dynamicTableIndices.add(dynamicTblIdx);
            }
         }

         if (dynamicTableIndices != null) {
            assert this.streamSectionTrackers != null;

            this.streamSectionTrackers.computeIfAbsent(streamId, __ -> new ArrayDeque<>()).add(dynamicTableIndices);
         }

         QpackUtil.encodePrefixedInteger(out, (byte)0, 8, this.dynamicTable.encodedRequiredInsertCount(requiredInsertCount));
         if (base >= requiredInsertCount) {
            QpackUtil.encodePrefixedInteger(out, (byte)0, 7, base - requiredInsertCount);
         } else {
            QpackUtil.encodePrefixedInteger(out, (byte)-128, 7, requiredInsertCount - base - 1);
         }

         out.writeBytes(tmp);
      } finally {
         tmp.release();
      }
   }

   void configureDynamicTable(QpackAttributes attributes, long maxTableCapacity, int blockedStreams) throws QpackException {
      if (maxTableCapacity > 0L) {
         assert attributes.encoderStreamAvailable();

         QuicStreamChannel encoderStream = attributes.encoderStream();
         this.dynamicTable.maxTableCapacity(maxTableCapacity);
         ByteBuf tableCapacity = encoderStream.alloc().buffer(8);
         QpackUtil.encodePrefixedInteger(tableCapacity, (byte)32, 5, maxTableCapacity);
         Http3CodecUtils.closeOnFailure(encoderStream.writeAndFlush(tableCapacity));
         this.streamSectionTrackers = new LongObjectHashMap<>();
         this.maxBlockedStreams = blockedStreams;
      }
   }

   void sectionAcknowledgment(long streamId) throws QpackException {
      assert this.streamSectionTrackers != null;

      Queue<QpackEncoder.Indices> tracker = this.streamSectionTrackers.get(streamId);
      if (tracker == null) {
         throw INVALID_SECTION_ACKNOWLEDGMENT;
      } else {
         QpackEncoder.Indices dynamicTableIndices = tracker.poll();
         if (tracker.isEmpty()) {
            this.streamSectionTrackers.remove(streamId);
         }

         if (dynamicTableIndices == null) {
            throw INVALID_SECTION_ACKNOWLEDGMENT;
         } else {
            dynamicTableIndices.forEach(this.dynamicTable::acknowledgeInsertCountOnAck);
         }
      }
   }

   void streamCancellation(long streamId) throws QpackException {
      if (this.streamSectionTrackers != null) {
         Queue<QpackEncoder.Indices> tracker = this.streamSectionTrackers.remove(streamId);
         if (tracker != null) {
            while (true) {
               QpackEncoder.Indices dynamicTableIndices = tracker.poll();
               if (dynamicTableIndices == null) {
                  break;
               }

               dynamicTableIndices.forEach(this.dynamicTable::acknowledgeInsertCountOnCancellation);
            }
         }
      }
   }

   void insertCountIncrement(int increment) throws QpackException {
      this.dynamicTable.incrementKnownReceivedCount(increment);
   }

   private int encodeHeader(QpackAttributes qpackAttributes, ByteBuf out, int base, CharSequence name, CharSequence value) {
      int index = QpackStaticTable.findFieldIndex(name, value);
      if (index == -1) {
         if (qpackAttributes.dynamicTableDisabled()) {
            this.encodeLiteral(out, name, value);
            return -2;
         } else {
            return this.encodeWithDynamicTable(qpackAttributes, out, base, name, value);
         }
      } else {
         if ((index & 1024) == 1024) {
            int dynamicTblIdx = this.tryEncodeWithDynamicTable(qpackAttributes, out, base, name, value);
            if (dynamicTblIdx >= 0) {
               return dynamicTblIdx;
            }

            int nameIdx = index ^ 1024;
            dynamicTblIdx = this.tryAddToDynamicTable(qpackAttributes, true, nameIdx, name, value);
            if (dynamicTblIdx >= 0) {
               if (dynamicTblIdx >= base) {
                  this.encodePostBaseIndexed(out, base, dynamicTblIdx);
               } else {
                  this.encodeIndexedDynamicTable(out, base, dynamicTblIdx);
               }

               return dynamicTblIdx;
            }

            this.encodeLiteralWithNameRefStaticTable(out, nameIdx, value);
         } else {
            this.encodeIndexedStaticTable(out, index);
         }

         return qpackAttributes.dynamicTableDisabled() ? -2 : -1;
      }
   }

   private int encodeWithDynamicTable(QpackAttributes qpackAttributes, ByteBuf out, int base, CharSequence name, CharSequence value) {
      int idx = this.tryEncodeWithDynamicTable(qpackAttributes, out, base, name, value);
      if (idx >= 0) {
         return idx;
      } else {
         if (idx == -1) {
            idx = this.tryAddToDynamicTable(qpackAttributes, false, -1, name, value);
            if (idx >= 0) {
               if (idx >= base) {
                  this.encodePostBaseIndexed(out, base, idx);
               } else {
                  this.encodeIndexedDynamicTable(out, base, idx);
               }

               return idx;
            }
         }

         this.encodeLiteral(out, name, value);
         return idx;
      }
   }

   private int tryEncodeWithDynamicTable(QpackAttributes qpackAttributes, ByteBuf out, int base, CharSequence name, CharSequence value) {
      if (qpackAttributes.dynamicTableDisabled()) {
         return -2;
      } else {
         assert qpackAttributes.encoderStreamAvailable();

         QuicStreamChannel encoderStream = qpackAttributes.encoderStream();
         int idx = this.dynamicTable.getEntryIndex(name, value);
         if (idx == Integer.MIN_VALUE) {
            return -1;
         } else {
            if (idx >= 0) {
               if (this.dynamicTable.requiresDuplication(idx, QpackHeaderField.sizeOf(name, value))) {
                  idx = this.dynamicTable.add(name, value, QpackHeaderField.sizeOf(name, value));

                  assert idx >= 0;

                  ByteBuf duplicate = encoderStream.alloc().buffer(8);
                  QpackUtil.encodePrefixedInteger(duplicate, (byte)0, 5, this.dynamicTable.relativeIndexForEncoderInstructions(idx));
                  Http3CodecUtils.closeOnFailure(encoderStream.writeAndFlush(duplicate));
                  if (this.mayNotBlockStream()) {
                     return -2;
                  }
               }

               if (idx >= base) {
                  this.encodePostBaseIndexed(out, base, idx);
               } else {
                  this.encodeIndexedDynamicTable(out, base, idx);
               }
            } else {
               idx = -(idx + 1);
               int addIdx = this.tryAddToDynamicTable(qpackAttributes, false, this.dynamicTable.relativeIndexForEncoderInstructions(idx), name, value);
               if (addIdx < 0) {
                  return -2;
               }

               idx = addIdx;
               if (addIdx >= base) {
                  this.encodeLiteralWithPostBaseNameRef(out, base, addIdx, value);
               } else {
                  this.encodeLiteralWithNameRefDynamicTable(out, base, addIdx, value);
               }
            }

            return idx;
         }
      }
   }

   private int tryAddToDynamicTable(QpackAttributes qpackAttributes, boolean staticTableNameRef, int nameIdx, CharSequence name, CharSequence value) {
      if (qpackAttributes.dynamicTableDisabled()) {
         return -2;
      } else {
         assert qpackAttributes.encoderStreamAvailable();

         QuicStreamChannel encoderStream = qpackAttributes.encoderStream();
         int idx = this.dynamicTable.add(name, value, QpackHeaderField.sizeOf(name, value));
         if (idx >= 0) {
            ByteBuf insert = null;

            try {
               if (nameIdx >= 0) {
                  insert = encoderStream.alloc().buffer(value.length() + 16);
                  QpackUtil.encodePrefixedInteger(insert, (byte)(staticTableNameRef ? 192 : 128), 6, nameIdx);
               } else {
                  insert = encoderStream.alloc().buffer(name.length() + value.length() + 16);
                  this.encodeLengthPrefixedHuffmanEncodedLiteral(insert, (byte)96, 5, name);
               }

               this.encodeStringLiteral(insert, value);
            } catch (Exception var10) {
               ReferenceCountUtil.release(insert);
               return -1;
            }

            Http3CodecUtils.closeOnFailure(encoderStream.writeAndFlush(insert));
            if (this.mayNotBlockStream()) {
               return -1;
            }

            this.blockedStreams++;
         }

         return idx;
      }
   }

   private void encodeIndexedStaticTable(ByteBuf out, int index) {
      QpackUtil.encodePrefixedInteger(out, (byte)-64, 6, index);
   }

   private void encodeIndexedDynamicTable(ByteBuf out, int base, int index) {
      QpackUtil.encodePrefixedInteger(out, (byte)-128, 6, base - index - 1);
   }

   private void encodePostBaseIndexed(ByteBuf out, int base, int index) {
      QpackUtil.encodePrefixedInteger(out, (byte)16, 4, index - base);
   }

   private void encodeLiteralWithNameRefStaticTable(ByteBuf out, int nameIndex, CharSequence value) {
      QpackUtil.encodePrefixedInteger(out, (byte)80, 4, nameIndex);
      this.encodeStringLiteral(out, value);
   }

   private void encodeLiteralWithNameRefDynamicTable(ByteBuf out, int base, int nameIndex, CharSequence value) {
      QpackUtil.encodePrefixedInteger(out, (byte)80, 4, base - nameIndex - 1);
      this.encodeStringLiteral(out, value);
   }

   private void encodeLiteralWithPostBaseNameRef(ByteBuf out, int base, int nameIndex, CharSequence value) {
      QpackUtil.encodePrefixedInteger(out, (byte)0, 4, nameIndex - base);
      this.encodeStringLiteral(out, value);
   }

   private void encodeLiteral(ByteBuf out, CharSequence name, CharSequence value) {
      this.encodeLengthPrefixedHuffmanEncodedLiteral(out, (byte)40, 3, name);
      this.encodeStringLiteral(out, value);
   }

   private void encodeStringLiteral(ByteBuf out, CharSequence value) {
      this.encodeLengthPrefixedHuffmanEncodedLiteral(out, (byte)-128, 7, value);
   }

   private void encodeLengthPrefixedHuffmanEncodedLiteral(ByteBuf out, byte mask, int prefix, CharSequence value) {
      int huffmanLength = this.huffmanEncoder.getEncodedLength(value);
      QpackUtil.encodePrefixedInteger(out, mask, prefix, huffmanLength);
      this.huffmanEncoder.encode(out, value);
   }

   private boolean mayNotBlockStream() {
      return this.blockedStreams >= this.maxBlockedStreams - 1;
   }

   private static final class Indices {
      private int idx;
      private int[] array = new int[4];

      private Indices() {
      }

      void add(int index) {
         if (this.idx == this.array.length) {
            this.array = Arrays.copyOf(this.array, this.array.length << 1);
         }

         this.array[this.idx++] = index;
      }

      void forEach(QpackEncoder.Indices.IndexConsumer consumer) throws QpackException {
         for (int i = 0; i < this.idx; i++) {
            consumer.accept(this.array[i]);
         }
      }

      @FunctionalInterface
      interface IndexConsumer {
         void accept(int var1) throws QpackException;
      }
   }
}
