package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;

public interface LastHttpContent extends HttpContent {
   LastHttpContent EMPTY_LAST_CONTENT = new LastHttpContent() {
      @Override
      public ByteBuf content() {
         return Unpooled.EMPTY_BUFFER;
      }

      @Override
      public LastHttpContent copy() {
         return EMPTY_LAST_CONTENT;
      }

      @Override
      public LastHttpContent duplicate() {
         return this;
      }

      @Override
      public LastHttpContent replace(ByteBuf content) {
         return new DefaultLastHttpContent(content);
      }

      @Override
      public LastHttpContent retainedDuplicate() {
         return this;
      }

      @Override
      public HttpHeaders trailingHeaders() {
         return EmptyHttpHeaders.INSTANCE;
      }

      @Override
      public DecoderResult decoderResult() {
         return DecoderResult.SUCCESS;
      }

      @Deprecated
      @Override
      public DecoderResult getDecoderResult() {
         return this.decoderResult();
      }

      @Override
      public void setDecoderResult(DecoderResult result) {
         throw new UnsupportedOperationException("read only");
      }

      @Override
      public int refCnt() {
         return 1;
      }

      @Override
      public LastHttpContent retain() {
         return this;
      }

      @Override
      public LastHttpContent retain(int increment) {
         return this;
      }

      @Override
      public LastHttpContent touch() {
         return this;
      }

      @Override
      public LastHttpContent touch(Object hint) {
         return this;
      }

      @Override
      public boolean release() {
         return false;
      }

      @Override
      public boolean release(int decrement) {
         return false;
      }

      @Override
      public String toString() {
         return "EmptyLastHttpContent";
      }
   };

   HttpHeaders trailingHeaders();

   LastHttpContent copy();

   LastHttpContent duplicate();

   LastHttpContent retainedDuplicate();

   LastHttpContent replace(ByteBuf var1);

   LastHttpContent retain(int var1);

   LastHttpContent retain();

   LastHttpContent touch();

   LastHttpContent touch(Object var1);
}
