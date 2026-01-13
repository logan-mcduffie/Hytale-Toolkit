package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.Map.Entry;

public class DefaultLastHttpContent extends DefaultHttpContent implements LastHttpContent {
   private final HttpHeaders trailingHeaders;

   public DefaultLastHttpContent() {
      this(Unpooled.buffer(0));
   }

   public DefaultLastHttpContent(ByteBuf content) {
      this(content, DefaultHttpHeadersFactory.trailersFactory());
   }

   @Deprecated
   public DefaultLastHttpContent(ByteBuf content, boolean validateHeaders) {
      this(content, DefaultHttpHeadersFactory.trailersFactory().withValidation(validateHeaders));
   }

   public DefaultLastHttpContent(ByteBuf content, HttpHeadersFactory trailersFactory) {
      super(content);
      this.trailingHeaders = trailersFactory.newHeaders();
   }

   public DefaultLastHttpContent(ByteBuf content, HttpHeaders trailingHeaders) {
      super(content);
      this.trailingHeaders = ObjectUtil.checkNotNull(trailingHeaders, "trailingHeaders");
   }

   @Override
   public LastHttpContent copy() {
      return this.replace(this.content().copy());
   }

   @Override
   public LastHttpContent duplicate() {
      return this.replace(this.content().duplicate());
   }

   @Override
   public LastHttpContent retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   @Override
   public LastHttpContent replace(ByteBuf content) {
      return new DefaultLastHttpContent(content, this.trailingHeaders().copy());
   }

   @Override
   public LastHttpContent retain(int increment) {
      super.retain(increment);
      return this;
   }

   @Override
   public LastHttpContent retain() {
      super.retain();
      return this;
   }

   @Override
   public LastHttpContent touch() {
      super.touch();
      return this;
   }

   @Override
   public LastHttpContent touch(Object hint) {
      super.touch(hint);
      return this;
   }

   @Override
   public HttpHeaders trailingHeaders() {
      return this.trailingHeaders;
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder(super.toString());
      buf.append(StringUtil.NEWLINE);
      this.appendHeaders(buf);
      buf.setLength(buf.length() - StringUtil.NEWLINE.length());
      return buf.toString();
   }

   private void appendHeaders(StringBuilder buf) {
      for (Entry<String, String> e : this.trailingHeaders()) {
         buf.append(e.getKey());
         buf.append(": ");
         buf.append(e.getValue());
         buf.append(StringUtil.NEWLINE);
      }
   }
}
