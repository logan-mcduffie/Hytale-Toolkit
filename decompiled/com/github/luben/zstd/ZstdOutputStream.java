package com.github.luben.zstd;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ZstdOutputStream extends FilterOutputStream {
   private ZstdOutputStreamNoFinalizer inner;

   @Deprecated
   public ZstdOutputStream(OutputStream var1, int var2, boolean var3, boolean var4) throws IOException {
      super(var1);
      this.inner = new ZstdOutputStreamNoFinalizer(var1, var2);
      this.inner.setCloseFrameOnFlush(var3);
      this.inner.setChecksum(var4);
   }

   @Deprecated
   public ZstdOutputStream(OutputStream var1, int var2, boolean var3) throws IOException {
      super(var1);
      this.inner = new ZstdOutputStreamNoFinalizer(var1, var2);
      this.inner.setCloseFrameOnFlush(var3);
   }

   public ZstdOutputStream(OutputStream var1, int var2) throws IOException {
      this(var1, NoPool.INSTANCE);
      this.inner.setLevel(var2);
   }

   public ZstdOutputStream(OutputStream var1) throws IOException {
      this(var1, NoPool.INSTANCE);
   }

   public ZstdOutputStream(OutputStream var1, BufferPool var2, int var3) throws IOException {
      this(var1, var2);
      this.inner.setLevel(var3);
   }

   public ZstdOutputStream(OutputStream var1, BufferPool var2) throws IOException {
      super(var1);
      this.inner = new ZstdOutputStreamNoFinalizer(var1, var2);
   }

   @Deprecated
   public void setFinalize(boolean var1) {
   }

   @Override
   protected void finalize() throws Throwable {
      this.close();
   }

   public static long recommendedCOutSize() {
      return ZstdOutputStreamNoFinalizer.recommendedCOutSize();
   }

   public ZstdOutputStream setChecksum(boolean var1) throws IOException {
      this.inner.setChecksum(var1);
      return this;
   }

   public ZstdOutputStream setLevel(int var1) throws IOException {
      this.inner.setLevel(var1);
      return this;
   }

   public ZstdOutputStream setLong(int var1) throws IOException {
      this.inner.setLong(var1);
      return this;
   }

   public ZstdOutputStream setWorkers(int var1) throws IOException {
      this.inner.setWorkers(var1);
      return this;
   }

   public ZstdOutputStream setOverlapLog(int var1) throws IOException {
      this.inner.setOverlapLog(var1);
      return this;
   }

   public ZstdOutputStream setJobSize(int var1) throws IOException {
      this.inner.setJobSize(var1);
      return this;
   }

   public ZstdOutputStream setTargetLength(int var1) throws IOException {
      this.inner.setTargetLength(var1);
      return this;
   }

   public ZstdOutputStream setMinMatch(int var1) throws IOException {
      this.inner.setMinMatch(var1);
      return this;
   }

   public ZstdOutputStream setSearchLog(int var1) throws IOException {
      this.inner.setSearchLog(var1);
      return this;
   }

   public ZstdOutputStream setChainLog(int var1) throws IOException {
      this.inner.setChainLog(var1);
      return this;
   }

   public ZstdOutputStream setHashLog(int var1) throws IOException {
      this.inner.setHashLog(var1);
      return this;
   }

   public ZstdOutputStream setWindowLog(int var1) throws IOException {
      this.inner.setWindowLog(var1);
      return this;
   }

   public ZstdOutputStream setStrategy(int var1) throws IOException {
      this.inner.setStrategy(var1);
      return this;
   }

   public ZstdOutputStream setCloseFrameOnFlush(boolean var1) {
      this.inner.setCloseFrameOnFlush(var1);
      return this;
   }

   public ZstdOutputStream setDict(byte[] var1) throws IOException {
      this.inner.setDict(var1);
      return this;
   }

   public ZstdOutputStream setDict(ZstdDictCompress var1) throws IOException {
      this.inner.setDict(var1);
      return this;
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.inner.write(var1, var2, var3);
   }

   @Override
   public void write(int var1) throws IOException {
      this.inner.write(var1);
   }

   @Override
   public void flush() throws IOException {
      this.inner.flush();
   }

   @Override
   public void close() throws IOException {
      this.inner.close();
   }
}
