package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public interface HttpData extends InterfaceHttpData, ByteBufHolder {
   long getMaxSize();

   void setMaxSize(long var1);

   void checkSize(long var1) throws IOException;

   void setContent(ByteBuf var1) throws IOException;

   void addContent(ByteBuf var1, boolean var2) throws IOException;

   void setContent(File var1) throws IOException;

   void setContent(InputStream var1) throws IOException;

   boolean isCompleted();

   long length();

   long definedLength();

   void delete();

   byte[] get() throws IOException;

   ByteBuf getByteBuf() throws IOException;

   ByteBuf getChunk(int var1) throws IOException;

   String getString() throws IOException;

   String getString(Charset var1) throws IOException;

   void setCharset(Charset var1);

   Charset getCharset();

   boolean renameTo(File var1) throws IOException;

   boolean isInMemory();

   File getFile() throws IOException;

   HttpData copy();

   HttpData duplicate();

   HttpData retainedDuplicate();

   HttpData replace(ByteBuf var1);

   HttpData retain();

   HttpData retain(int var1);

   HttpData touch();

   HttpData touch(Object var1);
}
