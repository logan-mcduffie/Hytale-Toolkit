package com.github.luben.zstd;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ZstdInputStream extends FilterInputStream {
   private ZstdInputStreamNoFinalizer inner;

   public ZstdInputStream(InputStream var1) throws IOException {
      super(var1);
      this.inner = new ZstdInputStreamNoFinalizer(var1);
   }

   public ZstdInputStream(InputStream var1, BufferPool var2) throws IOException {
      super(var1);
      this.inner = new ZstdInputStreamNoFinalizer(var1, var2);
   }

   @Deprecated
   public void setFinalize(boolean var1) {
   }

   @Override
   protected void finalize() throws Throwable {
      this.close();
   }

   public static long recommendedDInSize() {
      return ZstdInputStreamNoFinalizer.recommendedDInSize();
   }

   public static long recommendedDOutSize() {
      return ZstdInputStreamNoFinalizer.recommendedDOutSize();
   }

   public ZstdInputStream setContinuous(boolean var1) {
      this.inner.setContinuous(var1);
      return this;
   }

   public boolean getContinuous() {
      return this.inner.getContinuous();
   }

   public ZstdInputStream setDict(byte[] var1) throws IOException {
      this.inner.setDict(var1);
      return this;
   }

   public ZstdInputStream setDict(ZstdDictDecompress var1) throws IOException {
      this.inner.setDict(var1);
      return this;
   }

   public ZstdInputStream setLongMax(int var1) throws IOException {
      this.inner.setLongMax(var1);
      return this;
   }

   public ZstdInputStream setRefMultipleDDicts(boolean var1) throws IOException {
      this.inner.setRefMultipleDDicts(var1);
      return this;
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      return this.inner.read(var1, var2, var3);
   }

   @Override
   public int read() throws IOException {
      return this.inner.read();
   }

   @Override
   public int available() throws IOException {
      return this.inner.available();
   }

   @Override
   public long skip(long var1) throws IOException {
      return this.inner.skip(var1);
   }

   @Override
   public boolean markSupported() {
      return this.inner.markSupported();
   }

   @Override
   public void close() throws IOException {
      this.inner.close();
   }
}
