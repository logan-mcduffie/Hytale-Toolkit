package io.netty.handler.codec.quic;

final class QuicStreamIdGenerator {
   private long nextBidirectionalStreamId;
   private long nextUnidirectionalStreamId;

   QuicStreamIdGenerator(boolean server) {
      this.nextBidirectionalStreamId = server ? 1L : 0L;
      this.nextUnidirectionalStreamId = server ? 3L : 2L;
   }

   long nextStreamId(boolean bidirectional) {
      if (bidirectional) {
         long stream = this.nextBidirectionalStreamId;
         this.nextBidirectionalStreamId += 4L;
         return stream;
      } else {
         long stream = this.nextUnidirectionalStreamId;
         this.nextUnidirectionalStreamId += 4L;
         return stream;
      }
   }
}
