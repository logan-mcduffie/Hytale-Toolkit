package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.nio.charset.Charset;

public abstract class AbstractHttpData extends AbstractReferenceCounted implements HttpData {
   private final String name;
   protected long definedSize;
   protected long size;
   private Charset charset = HttpConstants.DEFAULT_CHARSET;
   private boolean completed;
   private long maxSize = -1L;

   protected AbstractHttpData(String name, Charset charset, long size) {
      ObjectUtil.checkNotNull(name, "name");
      this.name = ObjectUtil.checkNonEmpty(cleanName(name), "name");
      if (charset != null) {
         this.setCharset(charset);
      }

      this.definedSize = size;
   }

   private static String cleanName(String name) {
      int len = name.length();
      StringBuilder sb = null;
      int start = 0;
      int end = len;

      while (start < end && Character.isWhitespace(name.charAt(start))) {
         start++;
      }

      while (end > start && Character.isWhitespace(name.charAt(end - 1))) {
         end--;
      }

      for (int i = start; i < end; i++) {
         char c = name.charAt(i);
         if (c == '\n') {
            if (sb == null) {
               sb = new StringBuilder(len);
               sb.append(name, start, i);
            }
         } else if (c != '\r' && c != '\t') {
            if (sb != null) {
               sb.append(c);
            }
         } else {
            if (sb == null) {
               sb = new StringBuilder(len);
               sb.append(name, start, i);
            }

            sb.append(' ');
         }
      }

      return sb == null ? name.substring(start, end) : sb.toString();
   }

   @Override
   public long getMaxSize() {
      return this.maxSize;
   }

   @Override
   public void setMaxSize(long maxSize) {
      this.maxSize = maxSize;
   }

   @Override
   public void checkSize(long newSize) throws IOException {
      if (this.maxSize >= 0L && newSize > this.maxSize) {
         throw new IOException("Size exceed allowed maximum capacity");
      }
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public boolean isCompleted() {
      return this.completed;
   }

   protected void setCompleted() {
      this.setCompleted(true);
   }

   protected void setCompleted(boolean completed) {
      this.completed = completed;
   }

   @Override
   public Charset getCharset() {
      return this.charset;
   }

   @Override
   public void setCharset(Charset charset) {
      this.charset = ObjectUtil.checkNotNull(charset, "charset");
   }

   @Override
   public long length() {
      return this.size;
   }

   @Override
   public long definedLength() {
      return this.definedSize;
   }

   @Override
   public ByteBuf content() {
      try {
         return this.getByteBuf();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   @Override
   protected void deallocate() {
      this.delete();
   }

   @Override
   public HttpData retain() {
      super.retain();
      return this;
   }

   @Override
   public HttpData retain(int increment) {
      super.retain(increment);
      return this;
   }

   @Override
   public abstract HttpData touch();

   @Override
   public abstract HttpData touch(Object var1);
}
