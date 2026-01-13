package com.github.luben.zstd;

import com.github.luben.zstd.util.Native;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Zstd {
   public static long compress(byte[] var0, byte[] var1, int var2, boolean var3) {
      ZstdCompressCtx var4 = new ZstdCompressCtx();

      long var5;
      try {
         var4.setLevel(var2);
         var4.setChecksum(var3);
         var5 = var4.compress(var0, var1);
      } finally {
         var4.close();
      }

      return var5;
   }

   public static long compress(byte[] var0, byte[] var1, int var2) {
      return compress(var0, var1, var2, false);
   }

   public static long compressByteArray(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, int var6, boolean var7) {
      ZstdCompressCtx var8 = new ZstdCompressCtx();

      long var9;
      try {
         var8.setLevel(var6);
         var8.setChecksum(var7);
         var9 = var8.compressByteArray(var0, var1, var2, var3, var4, var5);
      } finally {
         var8.close();
      }

      return var9;
   }

   public static long compressByteArray(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, int var6) {
      return compressByteArray(var0, var1, var2, var3, var4, var5, var6, false);
   }

   public static long compressDirectByteBuffer(ByteBuffer var0, int var1, int var2, ByteBuffer var3, int var4, int var5, int var6, boolean var7) {
      ZstdCompressCtx var8 = new ZstdCompressCtx();

      long var9;
      try {
         var8.setLevel(var6);
         var8.setChecksum(var7);
         var9 = var8.compressDirectByteBuffer(var0, var1, var2, var3, var4, var5);
      } finally {
         var8.close();
      }

      return var9;
   }

   public static long compressDirectByteBuffer(ByteBuffer var0, int var1, int var2, ByteBuffer var3, int var4, int var5, int var6) {
      return compressDirectByteBuffer(var0, var1, var2, var3, var4, var5, var6, false);
   }

   public static native long compressUnsafe(long var0, long var2, long var4, long var6, int var8, boolean var9);

   public static long compressUnsafe(long var0, long var2, long var4, long var6, int var8) {
      return compressUnsafe(var0, var2, var4, var6, var8, false);
   }

   public static long compressUsingDict(byte[] var0, int var1, byte[] var2, int var3, int var4, byte[] var5, int var6) {
      ZstdCompressCtx var7 = new ZstdCompressCtx();

      long var8;
      try {
         var7.setLevel(var6);
         var7.loadDict(var5);
         var8 = var7.compressByteArray(var0, var1, var0.length - var1, var2, var3, var4);
      } finally {
         var7.close();
      }

      return var8;
   }

   public static long compressUsingDict(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, int var5) {
      ZstdCompressCtx var6 = new ZstdCompressCtx();

      long var7;
      try {
         var6.setLevel(var5);
         var6.loadDict(var4);
         var7 = var6.compressByteArray(var0, var1, var0.length - var1, var2, var3, var2.length - var3);
      } finally {
         var6.close();
      }

      return var7;
   }

   public static long compressDirectByteBufferUsingDict(ByteBuffer var0, int var1, int var2, ByteBuffer var3, int var4, int var5, byte[] var6, int var7) {
      ZstdCompressCtx var8 = new ZstdCompressCtx();

      long var9;
      try {
         var8.setLevel(var7);
         var8.loadDict(var6);
         var9 = var8.compressDirectByteBuffer(var0, var1, var2, var3, var4, var5);
      } finally {
         var8.close();
      }

      return var9;
   }

   public static long compressFastDict(byte[] var0, int var1, byte[] var2, int var3, int var4, ZstdDictCompress var5) {
      ZstdCompressCtx var6 = new ZstdCompressCtx();

      long var7;
      try {
         var6.loadDict(var5);
         var6.setLevel(var5.level());
         var7 = var6.compressByteArray(var0, var1, var0.length - var1, var2, var3, var4);
      } finally {
         var6.close();
      }

      return var7;
   }

   public static long compressFastDict(byte[] var0, int var1, byte[] var2, int var3, ZstdDictCompress var4) {
      ZstdCompressCtx var5 = new ZstdCompressCtx();

      long var6;
      try {
         var5.loadDict(var4);
         var5.setLevel(var4.level());
         var6 = var5.compressByteArray(var0, var1, var0.length - var1, var2, var3, var2.length - var3);
      } finally {
         var5.close();
      }

      return var6;
   }

   public static long compress(byte[] var0, byte[] var1, ZstdDictCompress var2) {
      ZstdCompressCtx var3 = new ZstdCompressCtx();

      long var4;
      try {
         var3.loadDict(var2);
         var3.setLevel(var2.level());
         var4 = var3.compress(var0, var1);
      } finally {
         var3.close();
      }

      return var4;
   }

   public static long compressDirectByteBufferFastDict(ByteBuffer var0, int var1, int var2, ByteBuffer var3, int var4, int var5, ZstdDictCompress var6) {
      ZstdCompressCtx var7 = new ZstdCompressCtx();

      long var8;
      try {
         var7.loadDict(var6);
         var7.setLevel(var6.level());
         var8 = var7.compressDirectByteBuffer(var0, var1, var2, var3, var4, var5);
      } finally {
         var7.close();
      }

      return var8;
   }

   public static long decompress(byte[] var0, byte[] var1) {
      ZstdDecompressCtx var2 = new ZstdDecompressCtx();

      long var3;
      try {
         var3 = var2.decompress(var0, var1);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static int decompress(byte[] var0, ByteBuffer var1) {
      ZstdDecompressCtx var2 = new ZstdDecompressCtx();

      int var3;
      try {
         var3 = var2.decompress(var0, var1);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static long decompressByteArray(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5) {
      ZstdDecompressCtx var6 = new ZstdDecompressCtx();

      long var7;
      try {
         var7 = var6.decompressByteArray(var0, var1, var2, var3, var4, var5);
      } finally {
         var6.close();
      }

      return var7;
   }

   public static long decompressDirectByteBuffer(ByteBuffer var0, int var1, int var2, ByteBuffer var3, int var4, int var5) {
      ZstdDecompressCtx var6 = new ZstdDecompressCtx();

      long var7;
      try {
         var7 = var6.decompressDirectByteBuffer(var0, var1, var2, var3, var4, var5);
      } finally {
         var6.close();
      }

      return var7;
   }

   public static native long decompressUnsafe(long var0, long var2, long var4, long var6);

   public static long decompressUsingDict(byte[] var0, int var1, byte[] var2, int var3, int var4, byte[] var5) {
      ZstdDecompressCtx var6 = new ZstdDecompressCtx();

      long var7;
      try {
         var6.loadDict(var5);
         var7 = var6.decompressByteArray(var0, var1, var0.length - var1, var2, var3, var4);
      } finally {
         var6.close();
      }

      return var7;
   }

   public static long decompressDirectByteBufferUsingDict(ByteBuffer var0, int var1, int var2, ByteBuffer var3, int var4, int var5, byte[] var6) {
      ZstdDecompressCtx var7 = new ZstdDecompressCtx();

      long var8;
      try {
         var7.loadDict(var6);
         var8 = var7.decompressDirectByteBuffer(var0, var1, var2, var3, var4, var5);
      } finally {
         var7.close();
      }

      return var8;
   }

   public static long decompressFastDict(byte[] var0, int var1, byte[] var2, int var3, int var4, ZstdDictDecompress var5) {
      ZstdDecompressCtx var6 = new ZstdDecompressCtx();

      long var7;
      try {
         var6.loadDict(var5);
         var7 = var6.decompressByteArray(var0, var1, var0.length - var1, var2, var3, var4);
      } finally {
         var6.close();
      }

      return var7;
   }

   public static long decompressDirectByteBufferFastDict(ByteBuffer var0, int var1, int var2, ByteBuffer var3, int var4, int var5, ZstdDictDecompress var6) {
      ZstdDecompressCtx var7 = new ZstdDecompressCtx();

      long var8;
      try {
         var7.loadDict(var6);
         var8 = var7.decompressDirectByteBuffer(var0, var1, var2, var3, var4, var5);
      } finally {
         var7.close();
      }

      return var8;
   }

   public static native int loadDictDecompress(long var0, byte[] var2, int var3);

   public static native int loadFastDictDecompress(long var0, ZstdDictDecompress var2);

   public static native int loadDictCompress(long var0, byte[] var2, int var3);

   public static native int loadFastDictCompress(long var0, ZstdDictCompress var2);

   public static native void registerSequenceProducer(long var0, long var2, long var4);

   static native long getBuiltinSequenceProducer();

   static native void generateSequences(long var0, long var2, long var4, long var6, long var8);

   static native long getStubSequenceProducer();

   public static native int setCompressionChecksums(long var0, boolean var2);

   public static native int setCompressionMagicless(long var0, boolean var2);

   public static native int setCompressionLevel(long var0, int var2);

   public static native int setCompressionLong(long var0, int var2);

   public static native int setCompressionWorkers(long var0, int var2);

   public static native int setCompressionOverlapLog(long var0, int var2);

   public static native int setCompressionJobSize(long var0, int var2);

   public static native int setCompressionTargetLength(long var0, int var2);

   public static native int setCompressionMinMatch(long var0, int var2);

   public static native int setCompressionSearchLog(long var0, int var2);

   public static native int setCompressionChainLog(long var0, int var2);

   public static native int setCompressionHashLog(long var0, int var2);

   public static native int setCompressionWindowLog(long var0, int var2);

   public static native int setCompressionStrategy(long var0, int var2);

   public static native int setDecompressionLongMax(long var0, int var2);

   public static native int setDecompressionMagicless(long var0, boolean var2);

   public static native int setRefMultipleDDicts(long var0, boolean var2);

   public static native int setValidateSequences(long var0, int var2);

   public static native int setSequenceProducerFallback(long var0, boolean var2);

   public static native int setSearchForExternalRepcodes(long var0, int var2);

   public static native int setEnableLongDistanceMatching(long var0, int var2);

   public static long findFrameCompressedSize(byte[] var0, int var1, int var2) {
      if (var1 >= var0.length) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else if (var1 + var2 > var0.length) {
         throw new ArrayIndexOutOfBoundsException(var1 + var2);
      } else {
         long var3 = findFrameCompressedSize0(var0, var1, var2);
         if (isError(var3)) {
            throw new ZstdException(var3);
         } else {
            return var3;
         }
      }
   }

   private static native long findFrameCompressedSize0(byte[] var0, int var1, int var2);

   public static long findFrameCompressedSize(byte[] var0, int var1) {
      return findFrameCompressedSize(var0, var1, var0.length - var1);
   }

   public static long findFrameCompressedSize(byte[] var0) {
      return findFrameCompressedSize(var0, 0);
   }

   public static long findFrameCompressedSize(ByteBuffer var0) {
      return findDirectByteBufferFrameCompressedSize(var0, var0.position(), var0.limit() - var0.position());
   }

   public static native long findDirectByteBufferFrameCompressedSize(ByteBuffer var0, int var1, int var2);

   public static long getFrameContentSize(byte[] var0, int var1, int var2, boolean var3) {
      if (var1 >= var0.length) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else if (var1 + var2 > var0.length) {
         throw new ArrayIndexOutOfBoundsException(var1 + var2);
      } else {
         return getFrameContentSize0(var0, var1, var2, var3);
      }
   }

   private static native long getFrameContentSize0(byte[] var0, int var1, int var2, boolean var3);

   @Deprecated
   public static long decompressedSize(byte[] var0, int var1, int var2, boolean var3) {
      if (var1 >= var0.length) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else if (var1 + var2 > var0.length) {
         throw new ArrayIndexOutOfBoundsException(var1 + var2);
      } else {
         return decompressedSize0(var0, var1, var2, var3);
      }
   }

   private static native long decompressedSize0(byte[] var0, int var1, int var2, boolean var3);

   public static long getFrameContentSize(byte[] var0, int var1, int var2) {
      return getFrameContentSize(var0, var1, var2, false);
   }

   @Deprecated
   public static long decompressedSize(byte[] var0, int var1, int var2) {
      return decompressedSize(var0, var1, var2, false);
   }

   public static long getFrameContentSize(byte[] var0, int var1) {
      return getFrameContentSize(var0, var1, var0.length - var1);
   }

   @Deprecated
   public static long decompressedSize(byte[] var0, int var1) {
      return decompressedSize(var0, var1, var0.length - var1);
   }

   public static long getFrameContentSize(byte[] var0) {
      return getFrameContentSize(var0, 0);
   }

   @Deprecated
   public static long decompressedSize(byte[] var0) {
      return decompressedSize(var0, 0);
   }

   @Deprecated
   public static native long decompressedDirectByteBufferSize(ByteBuffer var0, int var1, int var2, boolean var3);

   public static native long getDirectByteBufferFrameContentSize(ByteBuffer var0, int var1, int var2, boolean var3);

   @Deprecated
   public static long decompressedDirectByteBufferSize(ByteBuffer var0, int var1, int var2) {
      return decompressedDirectByteBufferSize(var0, var1, var2, false);
   }

   public static long getDirectByteBufferFrameContentSize(ByteBuffer var0, int var1, int var2) {
      return getDirectByteBufferFrameContentSize(var0, var1, var2, false);
   }

   public static native long compressBound(long var0);

   public static native boolean isError(long var0);

   public static native String getErrorName(long var0);

   public static native long getErrorCode(long var0);

   public static native long errNoError();

   public static native long errGeneric();

   public static native long errPrefixUnknown();

   public static native long errVersionUnsupported();

   public static native long errFrameParameterUnsupported();

   public static native long errFrameParameterWindowTooLarge();

   public static native long errCorruptionDetected();

   public static native long errChecksumWrong();

   public static native long errDictionaryCorrupted();

   public static native long errDictionaryWrong();

   public static native long errDictionaryCreationFailed();

   public static native long errParameterUnsupported();

   public static native long errParameterOutOfBound();

   public static native long errTableLogTooLarge();

   public static native long errMaxSymbolValueTooLarge();

   public static native long errMaxSymbolValueTooSmall();

   public static native long errStageWrong();

   public static native long errInitMissing();

   public static native long errMemoryAllocation();

   public static native long errWorkSpaceTooSmall();

   public static native long errDstSizeTooSmall();

   public static native long errSrcSizeWrong();

   public static native long errDstBufferNull();

   public static long trainFromBuffer(byte[][] var0, byte[] var1, boolean var2) {
      return trainFromBuffer(var0, var1, var2, defaultCompressionLevel());
   }

   public static long trainFromBuffer(byte[][] var0, byte[] var1, boolean var2, int var3) {
      if (var0.length <= 10) {
         throw new ZstdException(errGeneric(), "nb of samples too low");
      } else {
         return trainFromBuffer0(var0, var1, var2, var3);
      }
   }

   private static native long trainFromBuffer0(byte[][] var0, byte[] var1, boolean var2, int var3);

   public static long trainFromBufferDirect(ByteBuffer var0, int[] var1, ByteBuffer var2, boolean var3) {
      return trainFromBufferDirect(var0, var1, var2, var3, defaultCompressionLevel());
   }

   public static long trainFromBufferDirect(ByteBuffer var0, int[] var1, ByteBuffer var2, boolean var3, int var4) {
      if (var1.length <= 10) {
         throw new ZstdException(errGeneric(), "nb of samples too low");
      } else {
         return trainFromBufferDirect0(var0, var1, var2, var3, var4);
      }
   }

   private static native long trainFromBufferDirect0(ByteBuffer var0, int[] var1, ByteBuffer var2, boolean var3, int var4);

   public static native long getDictIdFromFrame(byte[] var0);

   public static native long getDictIdFromFrameBuffer(ByteBuffer var0);

   public static native long getDictIdFromDict(byte[] var0);

   private static native long getDictIdFromDictDirect(ByteBuffer var0, int var1, int var2);

   public static long getDictIdFromDictDirect(ByteBuffer var0) {
      int var1 = var0.limit() - var0.position();
      if (!var0.isDirect()) {
         throw new IllegalArgumentException("dict must be a direct buffer");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("dict cannot be empty.");
      } else {
         return getDictIdFromDictDirect(var0, var0.position(), var1);
      }
   }

   public static long trainFromBuffer(byte[][] var0, byte[] var1) {
      return trainFromBuffer(var0, var1, false);
   }

   public static long trainFromBufferDirect(ByteBuffer var0, int[] var1, ByteBuffer var2) {
      return trainFromBufferDirect(var0, var1, var2, false);
   }

   public static native int magicNumber();

   public static native int windowLogMin();

   public static native int windowLogMax();

   public static native int chainLogMin();

   public static native int chainLogMax();

   public static native int hashLogMin();

   public static native int hashLogMax();

   public static native int searchLogMin();

   public static native int searchLogMax();

   public static native int searchLengthMin();

   public static native int searchLengthMax();

   public static native int blockSizeMax();

   public static native int defaultCompressionLevel();

   public static native int minCompressionLevel();

   public static native int maxCompressionLevel();

   public static byte[] compress(byte[] var0) {
      return compress(var0, defaultCompressionLevel());
   }

   public static byte[] compress(byte[] var0, int var1) {
      ZstdCompressCtx var2 = new ZstdCompressCtx();

      byte[] var3;
      try {
         var2.setLevel(var1);
         var3 = var2.compress(var0);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static int compress(ByteBuffer var0, ByteBuffer var1) {
      return compress(var0, var1, defaultCompressionLevel());
   }

   public static int compress(ByteBuffer var0, ByteBuffer var1, int var2, boolean var3) {
      ZstdCompressCtx var4 = new ZstdCompressCtx();

      int var5;
      try {
         var4.setLevel(var2);
         var4.setChecksum(var3);
         var5 = var4.compress(var0, var1);
      } finally {
         var4.close();
      }

      return var5;
   }

   public static int compress(ByteBuffer var0, ByteBuffer var1, int var2) {
      return compress(var0, var1, var2, false);
   }

   public static ByteBuffer compress(ByteBuffer var0, int var1) {
      ZstdCompressCtx var2 = new ZstdCompressCtx();

      ByteBuffer var3;
      try {
         var2.setLevel(var1);
         var3 = var2.compress(var0);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static byte[] compress(byte[] var0, ZstdDictCompress var1) {
      ZstdCompressCtx var2 = new ZstdCompressCtx();

      byte[] var3;
      try {
         var2.loadDict(var1);
         var2.setLevel(var1.level());
         var3 = var2.compress(var0);
      } finally {
         var2.close();
      }

      return var3;
   }

   @Deprecated
   public static long compressUsingDict(byte[] var0, byte[] var1, byte[] var2, int var3) {
      return compressUsingDict(var0, 0, var1, 0, var1.length, var2, var3);
   }

   public static byte[] compressUsingDict(byte[] var0, byte[] var1, int var2) {
      ZstdCompressCtx var3 = new ZstdCompressCtx();

      byte[] var4;
      try {
         var3.loadDict(var1);
         var3.setLevel(var2);
         var4 = var3.compress(var0);
      } finally {
         var3.close();
      }

      return var4;
   }

   public static long compress(byte[] var0, byte[] var1, byte[] var2, int var3) {
      return compressUsingDict(var0, 0, var1, 0, var1.length, var2, var3);
   }

   public static int compress(ByteBuffer var0, ByteBuffer var1, byte[] var2, int var3) {
      ZstdCompressCtx var4 = new ZstdCompressCtx();

      int var5;
      try {
         var4.loadDict(var2);
         var4.setLevel(var3);
         var5 = var4.compress(var0, var1);
      } finally {
         var4.close();
      }

      return var5;
   }

   public static ByteBuffer compress(ByteBuffer var0, byte[] var1, int var2) {
      ZstdCompressCtx var3 = new ZstdCompressCtx();

      ByteBuffer var4;
      try {
         var3.loadDict(var1);
         var3.setLevel(var2);
         var4 = var3.compress(var0);
      } finally {
         var3.close();
      }

      return var4;
   }

   public static int compress(ByteBuffer var0, ByteBuffer var1, ZstdDictCompress var2) {
      ZstdCompressCtx var3 = new ZstdCompressCtx();

      int var4;
      try {
         var3.loadDict(var2);
         var3.setLevel(var2.level());
         var4 = var3.compress(var0, var1);
      } finally {
         var3.close();
      }

      return var4;
   }

   public static ByteBuffer compress(ByteBuffer var0, ZstdDictCompress var1) {
      ZstdCompressCtx var2 = new ZstdCompressCtx();

      ByteBuffer var3;
      try {
         var2.loadDict(var1);
         var2.setLevel(var1.level());
         var3 = var2.compress(var0);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static byte[] decompress(byte[] var0, int var1) {
      ZstdDecompressCtx var2 = new ZstdDecompressCtx();

      byte[] var3;
      try {
         var3 = var2.decompress(var0, var1);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static byte[] decompress(byte[] var0) {
      ArrayList var1 = new ArrayList();
      int var2 = calculateContentSizeAndFrames(var0, var1);
      byte[] var3 = new byte[var2];
      int var4 = 0;
      int var5 = 0;

      for (int var6 = 0; var6 < var1.size(); var6++) {
         Zstd.FrameData var7 = (Zstd.FrameData)var1.get(var6);
         long var8 = decompressByteArray(var3, var5, (int)var7.contentSize, var0, var4, (int)var7.compressedSize);
         if (isError(var8)) {
            throw new ZstdException(var8, String.format("error %s while decompressing %d frame", getErrorName(var8), var6));
         }

         if (var8 != var7.contentSize) {
            throw new IllegalStateException("decompressed size mismatch");
         }

         var4 += (int)var7.compressedSize;
         var5 += (int)var7.contentSize;
      }

      return var3;
   }

   private static int calculateContentSizeAndFrames(byte[] var0, List<Zstd.FrameData> var1) {
      int var2 = 0;
      int var3 = 0;

      while (var3 < var0.length) {
         Zstd.FrameData var4 = new Zstd.FrameData(var0, var3);
         var1.add(var4);
         var3 += (int)var4.compressedSize;
         var2 += (int)var4.contentSize;
      }

      return var2;
   }

   public static byte[] decompressFrame(byte[] var0, int var1, int var2, int var3) {
      ZstdDecompressCtx var4 = new ZstdDecompressCtx();

      byte[] var5;
      try {
         var5 = var4.decompress(var0, var1, var2, var3);
      } finally {
         var4.close();
      }

      return var5;
   }

   public static byte[] decompressFrame(byte[] var0, int var1) {
      int var2 = (int)findFrameCompressedSize(var0, var1);
      long var3 = getFrameContentSize(var0, var1, var2);
      if (isError(var3)) {
         if (var3 == -1L) {
            throw new ZstdException(var3, "Content size is unknown");
         } else {
            throw new ZstdException(var3);
         }
      } else {
         return decompressFrame(var0, var1, var2, (int)var3);
      }
   }

   public static byte[] decompressFrame(byte[] var0) {
      return decompressFrame(var0, 0);
   }

   public static int decompress(ByteBuffer var0, ByteBuffer var1) {
      ZstdDecompressCtx var2 = new ZstdDecompressCtx();

      int var3;
      try {
         var3 = var2.decompress(var0, var1);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static int decompress(ByteBuffer var0, byte[] var1) {
      ZstdDecompressCtx var2 = new ZstdDecompressCtx();

      int var3;
      try {
         var3 = var2.decompress(var0, var1);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static ByteBuffer decompress(ByteBuffer var0, int var1) {
      ZstdDecompressCtx var2 = new ZstdDecompressCtx();

      ByteBuffer var3;
      try {
         var3 = var2.decompress(var0, var1);
      } finally {
         var2.close();
      }

      return var3;
   }

   public static byte[] decompress(byte[] var0, ZstdDictDecompress var1, int var2) {
      ZstdDecompressCtx var3 = new ZstdDecompressCtx();

      byte[] var4;
      try {
         var3.loadDict(var1);
         var4 = var3.decompress(var0, var2);
      } finally {
         var3.close();
      }

      return var4;
   }

   @Deprecated
   public static long decompressUsingDict(byte[] var0, byte[] var1, byte[] var2) {
      return decompressUsingDict(var0, 0, var1, 0, var1.length, var2);
   }

   public static long decompress(byte[] var0, byte[] var1, byte[] var2) {
      return decompressUsingDict(var0, 0, var1, 0, var1.length, var2);
   }

   public static byte[] decompress(byte[] var0, byte[] var1, int var2) {
      ZstdDecompressCtx var3 = new ZstdDecompressCtx();

      byte[] var4;
      try {
         var3.loadDict(var1);
         var4 = var3.decompress(var0, var2);
      } finally {
         var3.close();
      }

      return var4;
   }

   @Deprecated
   public static long decompressedSize(ByteBuffer var0) {
      return decompressedDirectByteBufferSize(var0, var0.position(), var0.limit() - var0.position());
   }

   public static long getFrameContentSize(ByteBuffer var0) {
      return getDirectByteBufferFrameContentSize(var0, var0.position(), var0.limit() - var0.position());
   }

   public static int decompress(ByteBuffer var0, ByteBuffer var1, byte[] var2) {
      ZstdDecompressCtx var3 = new ZstdDecompressCtx();

      int var4;
      try {
         var3.loadDict(var2);
         var4 = var3.decompress(var0, var1);
      } finally {
         var3.close();
      }

      return var4;
   }

   public static ByteBuffer decompress(ByteBuffer var0, byte[] var1, int var2) {
      ZstdDecompressCtx var3 = new ZstdDecompressCtx();

      ByteBuffer var4;
      try {
         var3.loadDict(var1);
         var4 = var3.decompress(var0, var2);
      } finally {
         var3.close();
      }

      return var4;
   }

   public static int decompress(ByteBuffer var0, ByteBuffer var1, ZstdDictDecompress var2) {
      ZstdDecompressCtx var3 = new ZstdDecompressCtx();

      int var4;
      try {
         var3.loadDict(var2);
         var4 = var3.decompress(var0, var1);
      } finally {
         var3.close();
      }

      return var4;
   }

   public static ByteBuffer decompress(ByteBuffer var0, ZstdDictDecompress var1, int var2) {
      ZstdDecompressCtx var3 = new ZstdDecompressCtx();

      ByteBuffer var4;
      try {
         var3.loadDict(var1);
         var4 = var3.decompress(var0, var2);
      } finally {
         var3.close();
      }

      return var4;
   }

   static ByteBuffer getArrayBackedBuffer(BufferPool var0, int var1) throws ZstdIOException {
      ByteBuffer var2 = var0.get(var1);
      if (var2 == null) {
         throw new ZstdIOException(errMemoryAllocation(), "Cannot get ByteBuffer of size " + var1 + " from the BufferPool");
      } else if (var2.hasArray() && var2.arrayOffset() == 0) {
         return var2;
      } else {
         var0.release(var2);
         throw new IllegalArgumentException("provided ByteBuffer lacks array or has non-zero arrayOffset");
      }
   }

   static {
      Native.load();
   }

   private static class FrameData {
      final long contentSize;
      final long compressedSize;

      FrameData(byte[] var1, int var2) {
         this.compressedSize = Zstd.findFrameCompressedSize(var1, var2);
         this.contentSize = Zstd.getFrameContentSize(var1, var2, (int)this.compressedSize);
         if (Zstd.isError(this.contentSize)) {
            if (this.contentSize == -1L) {
               throw new ZstdException(this.contentSize, "Content size is unknown");
            } else {
               throw new ZstdException(this.contentSize);
            }
         }
      }
   }

   public static enum ParamSwitch {
      AUTO(0),
      ENABLE(1),
      DISABLE(2);

      private int val;

      private ParamSwitch(int nullxx) {
         this.val = nullxx;
      }

      public int getValue() {
         return this.val;
      }
   }
}
