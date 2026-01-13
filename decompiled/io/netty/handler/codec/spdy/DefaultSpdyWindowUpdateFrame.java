package io.netty.handler.codec.spdy;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultSpdyWindowUpdateFrame implements SpdyWindowUpdateFrame {
   private int streamId;
   private int deltaWindowSize;

   public DefaultSpdyWindowUpdateFrame(int streamId, int deltaWindowSize) {
      this.setStreamId(streamId);
      this.setDeltaWindowSize(deltaWindowSize);
   }

   @Override
   public int streamId() {
      return this.streamId;
   }

   @Override
   public SpdyWindowUpdateFrame setStreamId(int streamId) {
      ObjectUtil.checkPositiveOrZero(streamId, "streamId");
      this.streamId = streamId;
      return this;
   }

   @Override
   public int deltaWindowSize() {
      return this.deltaWindowSize;
   }

   @Override
   public SpdyWindowUpdateFrame setDeltaWindowSize(int deltaWindowSize) {
      ObjectUtil.checkPositive(deltaWindowSize, "deltaWindowSize");
      this.deltaWindowSize = deltaWindowSize;
      return this;
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this)
         + StringUtil.NEWLINE
         + "--> Stream-ID = "
         + this.streamId()
         + StringUtil.NEWLINE
         + "--> Delta-Window-Size = "
         + this.deltaWindowSize();
   }
}
