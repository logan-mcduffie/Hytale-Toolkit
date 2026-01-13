package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;

public interface FileUpload extends HttpData {
   String getFilename();

   void setFilename(String var1);

   void setContentType(String var1);

   String getContentType();

   void setContentTransferEncoding(String var1);

   String getContentTransferEncoding();

   FileUpload copy();

   FileUpload duplicate();

   FileUpload retainedDuplicate();

   FileUpload replace(ByteBuf var1);

   FileUpload retain();

   FileUpload retain(int var1);

   FileUpload touch();

   FileUpload touch(Object var1);
}
