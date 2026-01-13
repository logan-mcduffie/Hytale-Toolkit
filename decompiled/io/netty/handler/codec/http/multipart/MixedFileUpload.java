package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import java.nio.charset.Charset;

public class MixedFileUpload extends AbstractMixedHttpData<FileUpload> implements FileUpload {
   public MixedFileUpload(String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size, long limitSize) {
      this(
         name, filename, contentType, contentTransferEncoding, charset, size, limitSize, DiskFileUpload.baseDirectory, DiskFileUpload.deleteOnExitTemporaryFile
      );
   }

   public MixedFileUpload(
      String name,
      String filename,
      String contentType,
      String contentTransferEncoding,
      Charset charset,
      long size,
      long limitSize,
      String baseDir,
      boolean deleteOnExit
   ) {
      super(
         limitSize,
         baseDir,
         deleteOnExit,
         (FileUpload)(size > limitSize
            ? new DiskFileUpload(name, filename, contentType, contentTransferEncoding, charset, size, baseDir, deleteOnExit)
            : new MemoryFileUpload(name, filename, contentType, contentTransferEncoding, charset, size))
      );
   }

   @Override
   public String getContentTransferEncoding() {
      return this.wrapped.getContentTransferEncoding();
   }

   @Override
   public String getFilename() {
      return this.wrapped.getFilename();
   }

   @Override
   public void setContentTransferEncoding(String contentTransferEncoding) {
      this.wrapped.setContentTransferEncoding(contentTransferEncoding);
   }

   @Override
   public void setFilename(String filename) {
      this.wrapped.setFilename(filename);
   }

   @Override
   public void setContentType(String contentType) {
      this.wrapped.setContentType(contentType);
   }

   @Override
   public String getContentType() {
      return this.wrapped.getContentType();
   }

   FileUpload makeDiskData() {
      DiskFileUpload diskFileUpload = new DiskFileUpload(
         this.getName(),
         this.getFilename(),
         this.getContentType(),
         this.getContentTransferEncoding(),
         this.getCharset(),
         this.definedLength(),
         this.baseDir,
         this.deleteOnExit
      );
      diskFileUpload.setMaxSize(this.getMaxSize());
      return diskFileUpload;
   }

   @Override
   public FileUpload copy() {
      return (FileUpload)super.copy();
   }

   @Override
   public FileUpload duplicate() {
      return (FileUpload)super.duplicate();
   }

   @Override
   public FileUpload retainedDuplicate() {
      return (FileUpload)super.retainedDuplicate();
   }

   @Override
   public FileUpload replace(ByteBuf content) {
      return (FileUpload)super.replace(content);
   }

   @Override
   public FileUpload touch() {
      return (FileUpload)super.touch();
   }

   @Override
   public FileUpload touch(Object hint) {
      return (FileUpload)super.touch(hint);
   }

   @Override
   public FileUpload retain() {
      return (FileUpload)super.retain();
   }

   @Override
   public FileUpload retain(int increment) {
      return (FileUpload)super.retain(increment);
   }
}
