package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class Bzip2Decoder extends ByteToMessageDecoder {
   private Bzip2Decoder.State currentState = Bzip2Decoder.State.INIT;
   private final Bzip2BitReader reader = new Bzip2BitReader();
   private Bzip2BlockDecompressor blockDecompressor;
   private Bzip2HuffmanStageDecoder huffmanStageDecoder;
   private int blockSize;
   private int blockCRC;
   private int streamCRC;

   @Override
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      if (in.isReadable()) {
         Bzip2BitReader reader = this.reader;
         reader.setByteBuf(in);

         label403:
         while (true) {
            switch (this.currentState) {
               case INIT:
                  if (in.readableBytes() < 4) {
                     return;
                  }

                  int magicNumber = in.readUnsignedMedium();
                  if (magicNumber != 4348520) {
                     throw new DecompressionException("Unexpected stream identifier contents. Mismatched bzip2 protocol version?");
                  }

                  int blockSize = in.readByte() - 48;
                  if (blockSize < 1 || blockSize > 9) {
                     throw new DecompressionException("block size is invalid");
                  }

                  this.blockSize = blockSize * 100000;
                  this.streamCRC = 0;
                  this.currentState = Bzip2Decoder.State.INIT_BLOCK;
               case INIT_BLOCK:
                  if (!reader.hasReadableBytes(10)) {
                     return;
                  }

                  int magic1 = reader.readBits(24);
                  int magic2 = reader.readBits(24);
                  if (magic1 == 1536581 && magic2 == 3690640) {
                     int storedCombinedCRC = reader.readInt();
                     if (storedCombinedCRC != this.streamCRC) {
                        throw new DecompressionException("stream CRC error");
                     }

                     this.currentState = Bzip2Decoder.State.EOF;
                     break;
                  } else {
                     if (magic1 != 3227993 || magic2 != 2511705) {
                        throw new DecompressionException("bad block header");
                     }

                     this.blockCRC = reader.readInt();
                     this.currentState = Bzip2Decoder.State.INIT_BLOCK_PARAMS;
                  }
               case INIT_BLOCK_PARAMS:
                  if (!reader.hasReadableBits(25)) {
                     return;
                  }

                  boolean blockRandomised = reader.readBoolean();
                  int bwtStartPointer = reader.readBits(24);
                  this.blockDecompressor = new Bzip2BlockDecompressor(this.blockSize, this.blockCRC, blockRandomised, bwtStartPointer, reader);
                  this.currentState = Bzip2Decoder.State.RECEIVE_HUFFMAN_USED_MAP;
               case RECEIVE_HUFFMAN_USED_MAP:
                  if (!reader.hasReadableBits(16)) {
                     return;
                  }

                  this.blockDecompressor.huffmanInUse16 = reader.readBits(16);
                  this.currentState = Bzip2Decoder.State.RECEIVE_HUFFMAN_USED_BITMAPS;
               case RECEIVE_HUFFMAN_USED_BITMAPS:
                  Bzip2BlockDecompressor blockDecompressor = this.blockDecompressor;
                  int inUse16 = blockDecompressor.huffmanInUse16;
                  int bitNumber = Integer.bitCount(inUse16);
                  byte[] huffmanSymbolMap = blockDecompressor.huffmanSymbolMap;
                  if (!reader.hasReadableBits(bitNumber * 16 + 3)) {
                     return;
                  }

                  int huffmanSymbolCount = 0;
                  if (bitNumber > 0) {
                     for (int i = 0; i < 16; i++) {
                        if ((inUse16 & 32768 >>> i) != 0) {
                           int j = 0;

                           for (int k = i << 4; j < 16; k++) {
                              if (reader.readBoolean()) {
                                 huffmanSymbolMap[huffmanSymbolCount++] = (byte)k;
                              }

                              j++;
                           }
                        }
                     }
                  }

                  blockDecompressor.huffmanEndOfBlockSymbol = huffmanSymbolCount + 1;
                  int totalTables = reader.readBits(3);
                  if (totalTables < 2 || totalTables > 6) {
                     throw new DecompressionException("incorrect huffman groups number");
                  }

                  int alphaSize = huffmanSymbolCount + 2;
                  if (alphaSize > 258) {
                     throw new DecompressionException("incorrect alphabet size");
                  }

                  this.huffmanStageDecoder = new Bzip2HuffmanStageDecoder(reader, totalTables, alphaSize);
                  this.currentState = Bzip2Decoder.State.RECEIVE_SELECTORS_NUMBER;
               case RECEIVE_SELECTORS_NUMBER:
                  if (!reader.hasReadableBits(15)) {
                     return;
                  }

                  int totalSelectors = reader.readBits(15);
                  if (totalSelectors < 1 || totalSelectors > 18002) {
                     throw new DecompressionException("incorrect selectors number");
                  }

                  this.huffmanStageDecoder.selectors = new byte[totalSelectors];
                  this.currentState = Bzip2Decoder.State.RECEIVE_SELECTORS;
               case RECEIVE_SELECTORS:
                  Bzip2HuffmanStageDecoder huffmanStageDecoder = this.huffmanStageDecoder;
                  byte[] selectors = huffmanStageDecoder.selectors;
                  int totalSelectors = selectors.length;
                  Bzip2MoveToFrontTable tableMtf = huffmanStageDecoder.tableMTF;

                  for (int currSelector = huffmanStageDecoder.currentSelector; currSelector < totalSelectors; currSelector++) {
                     if (!reader.hasReadableBits(6)) {
                        huffmanStageDecoder.currentSelector = currSelector;
                        return;
                     }

                     int index = 0;

                     while (reader.readBoolean()) {
                        index++;
                     }

                     selectors[currSelector] = tableMtf.indexToFront(index);
                  }

                  this.currentState = Bzip2Decoder.State.RECEIVE_HUFFMAN_LENGTH;
               case RECEIVE_HUFFMAN_LENGTH:
                  Bzip2HuffmanStageDecoder huffmanStageDecoder = this.huffmanStageDecoder;
                  int totalTables = huffmanStageDecoder.totalTables;
                  byte[][] codeLength = huffmanStageDecoder.tableCodeLengths;
                  int alphaSize = huffmanStageDecoder.alphabetSize;
                  int currLength = huffmanStageDecoder.currentLength;
                  int currAlpha = 0;
                  boolean modifyLength = huffmanStageDecoder.modifyLength;
                  boolean saveStateAndReturn = false;

                  int currGroup;
                  label336:
                  for (currGroup = huffmanStageDecoder.currentGroup; currGroup < totalTables; currGroup++) {
                     if (!reader.hasReadableBits(5)) {
                        saveStateAndReturn = true;
                        break;
                     }

                     if (currLength < 0) {
                        currLength = reader.readBits(5);
                     }

                     for (currAlpha = huffmanStageDecoder.currentAlpha; currAlpha < alphaSize; currAlpha++) {
                        if (!reader.isReadable()) {
                           saveStateAndReturn = true;
                           break label336;
                        }

                        while (modifyLength || reader.readBoolean()) {
                           if (!reader.isReadable()) {
                              modifyLength = true;
                              saveStateAndReturn = true;
                              break label336;
                           }

                           currLength += reader.readBoolean() ? -1 : 1;
                           modifyLength = false;
                           if (!reader.isReadable()) {
                              saveStateAndReturn = true;
                              break label336;
                           }
                        }

                        codeLength[currGroup][currAlpha] = (byte)currLength;
                     }

                     currLength = -1;
                     currAlpha = huffmanStageDecoder.currentAlpha = 0;
                     modifyLength = false;
                  }

                  if (saveStateAndReturn) {
                     huffmanStageDecoder.currentGroup = currGroup;
                     huffmanStageDecoder.currentLength = currLength;
                     huffmanStageDecoder.currentAlpha = currAlpha;
                     huffmanStageDecoder.modifyLength = modifyLength;
                     return;
                  }

                  huffmanStageDecoder.createHuffmanDecodingTables();
                  this.currentState = Bzip2Decoder.State.DECODE_HUFFMAN_DATA;
               case DECODE_HUFFMAN_DATA:
                  break label403;
               case EOF:
                  in.skipBytes(in.readableBytes());
                  return;
               default:
                  throw new IllegalStateException();
            }
         }

         Bzip2BlockDecompressor blockDecompressorx = this.blockDecompressor;
         int oldReaderIndex = in.readerIndex();
         boolean decoded = blockDecompressorx.decodeHuffmanData(this.huffmanStageDecoder);
         if (decoded) {
            if (in.readerIndex() == oldReaderIndex && in.isReadable()) {
               reader.refill();
            }

            int blockLength = blockDecompressorx.blockLength();
            ByteBuf uncompressed = ctx.alloc().buffer(blockLength);

            try {
               int uncByte;
               while ((uncByte = blockDecompressorx.read()) >= 0) {
                  uncompressed.writeByte(uncByte);
               }

               this.currentState = Bzip2Decoder.State.INIT_BLOCK;
               int currentBlockCRC = blockDecompressorx.checkCRC();
               this.streamCRC = (this.streamCRC << 1 | this.streamCRC >>> 31) ^ currentBlockCRC;
               out.add(uncompressed);
               uncompressed = null;
            } finally {
               if (uncompressed != null) {
                  uncompressed.release();
               }
            }
         }
      }
   }

   public boolean isClosed() {
      return this.currentState == Bzip2Decoder.State.EOF;
   }

   private static enum State {
      INIT,
      INIT_BLOCK,
      INIT_BLOCK_PARAMS,
      RECEIVE_HUFFMAN_USED_MAP,
      RECEIVE_HUFFMAN_USED_BITMAPS,
      RECEIVE_SELECTORS_NUMBER,
      RECEIVE_SELECTORS,
      RECEIVE_HUFFMAN_LENGTH,
      DECODE_HUFFMAN_DATA,
      EOF;
   }
}
