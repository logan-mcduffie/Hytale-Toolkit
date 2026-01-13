package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.util.AbstractReferenceCounted;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

abstract class AbstractMixedHttpData<D extends HttpData> extends AbstractReferenceCounted implements HttpData {
   final String baseDir;
   final boolean deleteOnExit;
   D wrapped;
   private final long limitSize;

   AbstractMixedHttpData(long limitSize, String baseDir, boolean deleteOnExit, D initial) {
      this.limitSize = limitSize;
      this.wrapped = initial;
      this.baseDir = baseDir;
      this.deleteOnExit = deleteOnExit;
   }

   abstract D makeDiskData();

   @Override
   public long getMaxSize() {
      return this.wrapped.getMaxSize();
   }

   @Override
   public void setMaxSize(long maxSize) {
      this.wrapped.setMaxSize(maxSize);
   }

   @Override
   public ByteBuf content() {
      return this.wrapped.content();
   }

   @Override
   public void checkSize(long newSize) throws IOException {
      this.wrapped.checkSize(newSize);
   }

   @Override
   public long definedLength() {
      return this.wrapped.definedLength();
   }

   @Override
   public Charset getCharset() {
      return this.wrapped.getCharset();
   }

   @Override
   public String getName() {
      return this.wrapped.getName();
   }

   @Override
   public void addContent(ByteBuf buffer, boolean last) throws IOException {
      if (this.wrapped instanceof AbstractMemoryHttpData) {
         try {
            this.checkSize(this.wrapped.length() + buffer.readableBytes());
            if (this.wrapped.length() + buffer.readableBytes() > this.limitSize) {
               D diskData = this.makeDiskData();
               ByteBuf data = ((AbstractMemoryHttpData)this.wrapped).getByteBuf();
               if (data != null && data.isReadable()) {
                  diskData.addContent(data.retain(), false);
               }

               this.wrapped.release();
               this.wrapped = diskData;
            }
         } catch (IOException var5) {
            buffer.release();
            throw var5;
         }
      }

      this.wrapped.addContent(buffer, last);
   }

   @Override
   protected void deallocate() {
      this.delete();
   }

   @Override
   public void delete() {
      this.wrapped.delete();
   }

   @Override
   public byte[] get() throws IOException {
      return this.wrapped.get();
   }

   @Override
   public ByteBuf getByteBuf() throws IOException {
      return this.wrapped.getByteBuf();
   }

   @Override
   public String getString() throws IOException {
      return this.wrapped.getString();
   }

   @Override
   public String getString(Charset encoding) throws IOException {
      return this.wrapped.getString(encoding);
   }

   @Override
   public boolean isInMemory() {
      return this.wrapped.isInMemory();
   }

   @Override
   public long length() {
      return this.wrapped.length();
   }

   @Override
   public boolean renameTo(File dest) throws IOException {
      return this.wrapped.renameTo(dest);
   }

   @Override
   public void setCharset(Charset charset) {
      this.wrapped.setCharset(charset);
   }

   @Override
   public void setContent(ByteBuf buffer) throws IOException {
      try {
         this.checkSize(buffer.readableBytes());
      } catch (IOException var3) {
         buffer.release();
         throw var3;
      }

      if (buffer.readableBytes() > this.limitSize && this.wrapped instanceof AbstractMemoryHttpData) {
         this.wrapped.release();
         this.wrapped = this.makeDiskData();
      }

      this.wrapped.setContent(buffer);
   }

   @Override
   public void setContent(File file) throws IOException {
      this.checkSize(file.length());
      if (file.length() > this.limitSize && this.wrapped instanceof AbstractMemoryHttpData) {
         this.wrapped.release();
         this.wrapped = this.makeDiskData();
      }

      this.wrapped.setContent(file);
   }

   @Override
   public void setContent(InputStream inputStream) throws IOException {
      if (this.wrapped instanceof AbstractMemoryHttpData) {
         this.wrapped.release();
         this.wrapped = this.makeDiskData();
      }

      this.wrapped.setContent(inputStream);
   }

   @Override
   public boolean isCompleted() {
      return this.wrapped.isCompleted();
   }

   @Override
   public InterfaceHttpData.HttpDataType getHttpDataType() {
      return this.wrapped.getHttpDataType();
   }

   @Override
   public int hashCode() {
      return this.wrapped.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      return this.wrapped.equals(obj);
   }

   public int compareTo(InterfaceHttpData o) {
      return this.wrapped.compareTo(o);
   }

   @Override
   public String toString() {
      return "Mixed: " + this.wrapped;
   }

   @Override
   public ByteBuf getChunk(int length) throws IOException {
      return this.wrapped.getChunk(length);
   }

   @Override
   public File getFile() throws IOException {
      return this.wrapped.getFile();
   }

   @Override
   public D copy() {
      return (D)this.wrapped.copy();
   }

   @Override
   public D duplicate() {
      return (D)this.wrapped.duplicate();
   }

   @Override
   public D retainedDuplicate() {
      return (D)this.wrapped.retainedDuplicate();
   }

   @Override
   public D replace(ByteBuf content) {
      return (D)this.wrapped.replace(content);
   }

   @Override
   public D touch() {
      this.wrapped.touch();
      return (D)this;
   }

   @Override
   public D touch(Object hint) {
      this.wrapped.touch(hint);
      return (D)this;
   }

   @Override
   public D retain() {
      return (D)super.retain();
   }

   @Override
   public D retain(int increment) {
      return (D)super.retain(increment);
   }
}
