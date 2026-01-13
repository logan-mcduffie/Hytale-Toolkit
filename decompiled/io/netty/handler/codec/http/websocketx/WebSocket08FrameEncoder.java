package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteOrder;
import java.util.List;

public class WebSocket08FrameEncoder extends MessageToMessageEncoder<WebSocketFrame> implements WebSocketFrameEncoder {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameEncoder.class);
   private static final byte OPCODE_CONT = 0;
   private static final byte OPCODE_TEXT = 1;
   private static final byte OPCODE_BINARY = 2;
   private static final byte OPCODE_CLOSE = 8;
   private static final byte OPCODE_PING = 9;
   private static final byte OPCODE_PONG = 10;
   private static final int GATHERING_WRITE_THRESHOLD = 1024;
   private final WebSocketFrameMaskGenerator maskGenerator;

   public WebSocket08FrameEncoder(boolean maskPayload) {
      this(maskPayload ? RandomWebSocketFrameMaskGenerator.INSTANCE : null);
   }

   public WebSocket08FrameEncoder(WebSocketFrameMaskGenerator maskGenerator) {
      super(WebSocketFrame.class);
      this.maskGenerator = maskGenerator;
   }

   protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
      ByteBuf data = msg.content();
      byte opcode = getOpCode(msg);
      int length = data.readableBytes();
      if (logger.isTraceEnabled()) {
         logger.trace("Encoding WebSocket Frame opCode={} length={}", opcode, length);
      }

      int b0 = 0;
      if (msg.isFinalFragment()) {
         b0 |= 128;
      }

      b0 |= (msg.rsv() & 7) << 4;
      b0 |= opcode & 127;
      if (opcode == 9 && length > 125) {
         throw new TooLongFrameException("invalid payload for PING (payload length must be <= 125, was " + length);
      } else {
         boolean release = true;
         ByteBuf buf = null;

         try {
            int maskLength = this.maskGenerator != null ? 4 : 0;
            if (length <= 125) {
               int size = 2 + maskLength + length;
               buf = ctx.alloc().buffer(size);
               buf.writeByte(b0);
               byte b = (byte)(this.maskGenerator != null ? 128 | length : length);
               buf.writeByte(b);
            } else if (length > 65535) {
               int size = 10 + maskLength;
               if (this.maskGenerator != null) {
                  size += length;
               }

               buf = ctx.alloc().buffer(size);
               buf.writeByte(b0);
               buf.writeByte(this.maskGenerator != null ? 255 : 127);
               buf.writeLong(length);
            } else {
               int size = 4 + maskLength;
               if (this.maskGenerator != null || length <= 1024) {
                  size += length;
               }

               buf = ctx.alloc().buffer(size);
               buf.writeByte(b0);
               buf.writeByte(this.maskGenerator != null ? 254 : 126);
               buf.writeByte(length >>> 8 & 0xFF);
               buf.writeByte(length & 0xFF);
            }

            if (this.maskGenerator != null) {
               int mask = this.maskGenerator.nextMask();
               buf.writeInt(mask);
               if (mask == 0) {
                  addBuffers(buf, data, out);
               } else {
                  if (length > 0) {
                     ByteOrder srcOrder = data.order();
                     ByteOrder dstOrder = buf.order();
                     int i = data.readerIndex();
                     int end = data.writerIndex();
                     if (srcOrder == dstOrder) {
                        long longMask = mask & 4294967295L;
                        longMask |= longMask << 32;
                        if (srcOrder == ByteOrder.LITTLE_ENDIAN) {
                           longMask = Long.reverseBytes(longMask);
                        }

                        for (int lim = end - 7; i < lim; i += 8) {
                           buf.writeLong(data.getLong(i) ^ longMask);
                        }

                        if (i < end - 3) {
                           buf.writeInt(data.getInt(i) ^ (int)longMask);
                           i += 4;
                        }
                     }

                     for (int maskOffset = 0; i < end; i++) {
                        byte byteData = data.getByte(i);
                        buf.writeByte(byteData ^ WebSocketUtil.byteAtIndex(mask, maskOffset++ & 3));
                     }
                  }

                  out.add(buf);
               }
            } else {
               addBuffers(buf, data, out);
            }

            release = false;
         } finally {
            if (release && buf != null) {
               buf.release();
            }
         }
      }
   }

   private static byte getOpCode(WebSocketFrame msg) {
      if (msg instanceof TextWebSocketFrame) {
         return 1;
      } else if (msg instanceof BinaryWebSocketFrame) {
         return 2;
      } else if (msg instanceof PingWebSocketFrame) {
         return 9;
      } else if (msg instanceof PongWebSocketFrame) {
         return 10;
      } else if (msg instanceof CloseWebSocketFrame) {
         return 8;
      } else if (msg instanceof ContinuationWebSocketFrame) {
         return 0;
      } else {
         throw new UnsupportedOperationException("Cannot encode frame of type: " + msg.getClass().getName());
      }
   }

   private static void addBuffers(ByteBuf buf, ByteBuf data, List<Object> out) {
      int readableBytes = data.readableBytes();
      if (buf.writableBytes() >= readableBytes) {
         buf.writeBytes(data);
         out.add(buf);
      } else {
         out.add(buf);
         if (readableBytes > 0) {
            out.add(data.retain());
         }
      }
   }
}
