package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class DiskFileUpload extends AbstractDiskHttpData implements FileUpload {
   public static String baseDirectory;
   public static boolean deleteOnExitTemporaryFile = true;
   public static final String prefix = "FUp_";
   public static final String postfix = ".tmp";
   private final String baseDir;
   private final boolean deleteOnExit;
   private String filename;
   private String contentType;
   private String contentTransferEncoding;

   public DiskFileUpload(
      String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size, String baseDir, boolean deleteOnExit
   ) {
      super(name, charset, size);
      this.setFilename(filename);
      this.setContentType(contentType);
      this.setContentTransferEncoding(contentTransferEncoding);
      this.baseDir = baseDir == null ? baseDirectory : baseDir;
      this.deleteOnExit = deleteOnExit;
   }

   public DiskFileUpload(String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size) {
      this(name, filename, contentType, contentTransferEncoding, charset, size, baseDirectory, deleteOnExitTemporaryFile);
   }

   @Override
   public InterfaceHttpData.HttpDataType getHttpDataType() {
      return InterfaceHttpData.HttpDataType.FileUpload;
   }

   @Override
   public String getFilename() {
      return this.filename;
   }

   @Override
   public void setFilename(String filename) {
      this.filename = ObjectUtil.checkNotNull(filename, "filename");
   }

   @Override
   public int hashCode() {
      return FileUploadUtil.hashCode(this);
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof FileUpload && FileUploadUtil.equals(this, (FileUpload)o);
   }

   public int compareTo(InterfaceHttpData o) {
      if (!(o instanceof FileUpload)) {
         throw new ClassCastException("Cannot compare " + this.getHttpDataType() + " with " + o.getHttpDataType());
      } else {
         return this.compareTo((FileUpload)o);
      }
   }

   public int compareTo(FileUpload o) {
      return FileUploadUtil.compareTo(this, o);
   }

   @Override
   public void setContentType(String contentType) {
      this.contentType = ObjectUtil.checkNotNull(contentType, "contentType");
   }

   @Override
   public String getContentType() {
      return this.contentType;
   }

   @Override
   public String getContentTransferEncoding() {
      return this.contentTransferEncoding;
   }

   @Override
   public void setContentTransferEncoding(String contentTransferEncoding) {
      this.contentTransferEncoding = contentTransferEncoding;
   }

   @Override
   public String toString() {
      File file = null;

      try {
         file = this.getFile();
      } catch (IOException var3) {
      }

      return HttpHeaderNames.CONTENT_DISPOSITION
         + ": "
         + HttpHeaderValues.FORM_DATA
         + "; "
         + HttpHeaderValues.NAME
         + "=\""
         + this.getName()
         + "\"; "
         + HttpHeaderValues.FILENAME
         + "=\""
         + this.filename
         + "\"\r\n"
         + HttpHeaderNames.CONTENT_TYPE
         + ": "
         + this.contentType
         + (this.getCharset() != null ? "; " + HttpHeaderValues.CHARSET + '=' + this.getCharset().name() + "\r\n" : "\r\n")
         + HttpHeaderNames.CONTENT_LENGTH
         + ": "
         + this.length()
         + "\r\nCompleted: "
         + this.isCompleted()
         + "\r\nIsInMemory: "
         + this.isInMemory()
         + "\r\nRealFile: "
         + (file != null ? file.getAbsolutePath() : "null")
         + " DeleteAfter: "
         + this.deleteOnExit;
   }

   @Override
   protected boolean deleteOnExit() {
      return this.deleteOnExit;
   }

   @Override
   protected String getBaseDirectory() {
      return this.baseDir;
   }

   @Override
   protected String getDiskFilename() {
      return "upload";
   }

   @Override
   protected String getPostfix() {
      return ".tmp";
   }

   @Override
   protected String getPrefix() {
      return "FUp_";
   }

   @Override
   public FileUpload copy() {
      ByteBuf content = this.content();
      return this.replace(content != null ? content.copy() : null);
   }

   @Override
   public FileUpload duplicate() {
      ByteBuf content = this.content();
      return this.replace(content != null ? content.duplicate() : null);
   }

   @Override
   public FileUpload retainedDuplicate() {
      ByteBuf content = this.content();
      if (content != null) {
         content = content.retainedDuplicate();
         boolean success = false;

         FileUpload var4;
         try {
            FileUpload duplicate = this.replace(content);
            success = true;
            var4 = duplicate;
         } finally {
            if (!success) {
               content.release();
            }
         }

         return var4;
      } else {
         return this.replace(null);
      }
   }

   @Override
   public FileUpload replace(ByteBuf content) {
      DiskFileUpload upload = new DiskFileUpload(
         this.getName(),
         this.getFilename(),
         this.getContentType(),
         this.getContentTransferEncoding(),
         this.getCharset(),
         this.size,
         this.baseDir,
         this.deleteOnExit
      );
      if (content != null) {
         try {
            upload.setContent(content);
         } catch (IOException var4) {
            throw new ChannelException(var4);
         }
      }

      upload.setCompleted(this.isCompleted());
      return upload;
   }

   @Override
   public FileUpload retain(int increment) {
      super.retain(increment);
      return this;
   }

   @Override
   public FileUpload retain() {
      super.retain();
      return this;
   }

   @Override
   public FileUpload touch() {
      super.touch();
      return this;
   }

   @Override
   public FileUpload touch(Object hint) {
      super.touch(hint);
      return this;
   }
}
