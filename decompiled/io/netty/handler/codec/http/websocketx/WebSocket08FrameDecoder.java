package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteOrder;
import java.util.List;

public class WebSocket08FrameDecoder extends ByteToMessageDecoder implements WebSocketFrameDecoder {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameDecoder.class);
   private static final byte OPCODE_CONT = 0;
   private static final byte OPCODE_TEXT = 1;
   private static final byte OPCODE_BINARY = 2;
   private static final byte OPCODE_CLOSE = 8;
   private static final byte OPCODE_PING = 9;
   private static final byte OPCODE_PONG = 10;
   private final WebSocketDecoderConfig config;
   private int fragmentedFramesCount;
   private boolean frameFinalFlag;
   private boolean frameMasked;
   private int frameRsv;
   private int frameOpcode;
   private long framePayloadLength;
   private int mask;
   private int framePayloadLen1;
   private boolean receivedClosingHandshake;
   private WebSocket08FrameDecoder.State state = WebSocket08FrameDecoder.State.READING_FIRST;

   public WebSocket08FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength) {
      this(expectMaskedFrames, allowExtensions, maxFramePayloadLength, false);
   }

   public WebSocket08FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch) {
      this(
         WebSocketDecoderConfig.newBuilder()
            .expectMaskedFrames(expectMaskedFrames)
            .allowExtensions(allowExtensions)
            .maxFramePayloadLength(maxFramePayloadLength)
            .allowMaskMismatch(allowMaskMismatch)
            .build()
      );
   }

   public WebSocket08FrameDecoder(WebSocketDecoderConfig decoderConfig) {
      this.config = ObjectUtil.checkNotNull(decoderConfig, "decoderConfig");
   }

   @Override
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      if (this.receivedClosingHandshake) {
         in.skipBytes(this.actualReadableBytes());
      } else {
         switch (this.state) {
            case READING_FIRST:
               if (!in.isReadable()) {
                  return;
               }

               this.framePayloadLength = 0L;
               byte bx = in.readByte();
               this.frameFinalFlag = (bx & 128) != 0;
               this.frameRsv = (bx & 112) >> 4;
               this.frameOpcode = bx & 15;
               if (logger.isTraceEnabled()) {
                  logger.trace("Decoding WebSocket Frame opCode={}", this.frameOpcode);
               }

               this.state = WebSocket08FrameDecoder.State.READING_SECOND;
            case READING_SECOND:
               if (!in.isReadable()) {
                  return;
               }

               byte b = in.readByte();
               this.frameMasked = (b & 128) != 0;
               this.framePayloadLen1 = b & 127;
               if (this.frameRsv != 0 && !this.config.allowExtensions()) {
                  this.protocolViolation(ctx, in, "RSV != 0 and no extension negotiated, RSV:" + this.frameRsv);
                  return;
               }

               if (!this.config.allowMaskMismatch() && this.config.expectMaskedFrames() != this.frameMasked) {
                  this.protocolViolation(ctx, in, "received a frame that is not masked as expected");
                  return;
               }

               if (this.frameOpcode > 7) {
                  if (!this.frameFinalFlag) {
                     this.protocolViolation(ctx, in, "fragmented control frame");
                     return;
                  }

                  if (this.framePayloadLen1 > 125) {
                     this.protocolViolation(ctx, in, "control frame with payload length > 125 octets");
                     return;
                  }

                  if (this.frameOpcode != 8 && this.frameOpcode != 9 && this.frameOpcode != 10) {
                     this.protocolViolation(ctx, in, "control frame using reserved opcode " + this.frameOpcode);
                     return;
                  }

                  if (this.frameOpcode == 8 && this.framePayloadLen1 == 1) {
                     this.protocolViolation(ctx, in, "received close control frame with payload len 1");
                     return;
                  }
               } else {
                  if (this.frameOpcode != 0 && this.frameOpcode != 1 && this.frameOpcode != 2) {
                     this.protocolViolation(ctx, in, "data frame using reserved opcode " + this.frameOpcode);
                     return;
                  }

                  if (this.fragmentedFramesCount == 0 && this.frameOpcode == 0) {
                     this.protocolViolation(ctx, in, "received continuation data frame outside fragmented message");
                     return;
                  }

                  if (this.fragmentedFramesCount != 0 && this.frameOpcode != 0) {
                     this.protocolViolation(ctx, in, "received non-continuation data frame while inside fragmented message");
                     return;
                  }
               }

               this.state = WebSocket08FrameDecoder.State.READING_SIZE;
            case READING_SIZE:
               if (this.framePayloadLen1 == 126) {
                  if (in.readableBytes() < 2) {
                     return;
                  }

                  this.framePayloadLength = in.readUnsignedShort();
                  if (this.framePayloadLength < 126L) {
                     this.protocolViolation(ctx, in, "invalid data frame length (not using minimal length encoding)");
                     return;
                  }
               } else if (this.framePayloadLen1 == 127) {
                  if (in.readableBytes() < 8) {
                     return;
                  }

                  this.framePayloadLength = in.readLong();
                  if (this.framePayloadLength < 0L) {
                     this.protocolViolation(ctx, in, "invalid data frame length (negative length)");
                     return;
                  }

                  if (this.framePayloadLength < 65536L) {
                     this.protocolViolation(ctx, in, "invalid data frame length (not using minimal length encoding)");
                     return;
                  }
               } else {
                  this.framePayloadLength = this.framePayloadLen1;
               }

               if (this.framePayloadLength > this.config.maxFramePayloadLength()) {
                  this.protocolViolation(
                     ctx, in, WebSocketCloseStatus.MESSAGE_TOO_BIG, "Max frame length of " + this.config.maxFramePayloadLength() + " has been exceeded."
                  );
                  return;
               }

               if (logger.isTraceEnabled()) {
                  logger.trace("Decoding WebSocket Frame length={}", this.framePayloadLength);
               }

               this.state = WebSocket08FrameDecoder.State.MASKING_KEY;
            case MASKING_KEY:
               if (this.frameMasked) {
                  if (in.readableBytes() < 4) {
                     return;
                  }

                  this.mask = in.readInt();
               }

               this.state = WebSocket08FrameDecoder.State.PAYLOAD;
            case PAYLOAD:
               break;
            case CORRUPT:
               if (in.isReadable()) {
                  in.readByte();
               }

               return;
            default:
               throw new Error("Shouldn't reach here (state: " + this.state + ")");
         }

         if (in.readableBytes() >= this.framePayloadLength) {
            ByteBuf payloadBuffer = Unpooled.EMPTY_BUFFER;

            try {
               if (this.framePayloadLength > 0L) {
                  payloadBuffer = ByteBufUtil.readBytes(ctx.alloc(), in, toFrameLength(this.framePayloadLength));
               }

               this.state = WebSocket08FrameDecoder.State.READING_FIRST;
               if (this.frameMasked & this.framePayloadLength > 0L) {
                  this.unmask(payloadBuffer);
               }

               if (this.frameOpcode == 9) {
                  out.add(new PingWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
                  payloadBuffer = null;
                  return;
               }

               if (this.frameOpcode == 10) {
                  out.add(new PongWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
                  payloadBuffer = null;
                  return;
               }

               if (this.frameOpcode == 8) {
                  this.receivedClosingHandshake = true;
                  this.checkCloseFrameBody(ctx, payloadBuffer);
                  out.add(new CloseWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
                  payloadBuffer = null;
                  return;
               }

               if (this.frameFinalFlag) {
                  this.fragmentedFramesCount = 0;
               } else {
                  this.fragmentedFramesCount++;
               }

               if (this.frameOpcode == 1) {
                  out.add(new TextWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
                  payloadBuffer = null;
                  return;
               }

               if (this.frameOpcode != 2) {
                  if (this.frameOpcode != 0) {
                     throw new UnsupportedOperationException("Cannot decode web socket frame with opcode: " + this.frameOpcode);
                  }

                  out.add(new ContinuationWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
                  payloadBuffer = null;
                  return;
               }

               out.add(new BinaryWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
               payloadBuffer = null;
            } finally {
               if (payloadBuffer != null) {
                  payloadBuffer.release();
               }
            }
         }
      }
   }

   private void unmask(ByteBuf frame) {
      int i = frame.readerIndex();
      int end = frame.writerIndex();
      ByteOrder order = frame.order();
      int intMask = this.mask;
      if (intMask != 0) {
         long longMask = intMask & 4294967295L;
         longMask |= longMask << 32;

         for (int lim = end - 7; i < lim; i += 8) {
            frame.setLong(i, frame.getLong(i) ^ longMask);
         }

         if (i < end - 3) {
            frame.setInt(i, frame.getInt(i) ^ (int)longMask);
            i += 4;
         }

         if (order == ByteOrder.LITTLE_ENDIAN) {
            intMask = Integer.reverseBytes(intMask);
         }

         for (int maskOffset = 0; i < end; i++) {
            frame.setByte(i, frame.getByte(i) ^ WebSocketUtil.byteAtIndex(intMask, maskOffset++ & 3));
         }
      }
   }

   private void protocolViolation(ChannelHandlerContext ctx, ByteBuf in, String reason) {
      this.protocolViolation(ctx, in, WebSocketCloseStatus.PROTOCOL_ERROR, reason);
   }

   private void protocolViolation(ChannelHandlerContext ctx, ByteBuf in, WebSocketCloseStatus status, String reason) {
      this.protocolViolation(ctx, in, new CorruptedWebSocketFrameException(status, reason));
   }

   private void protocolViolation(ChannelHandlerContext ctx, ByteBuf in, CorruptedWebSocketFrameException ex) {
      this.state = WebSocket08FrameDecoder.State.CORRUPT;
      int readableBytes = in.readableBytes();
      if (readableBytes > 0) {
         in.skipBytes(readableBytes);
      }

      if (ctx.channel().isActive() && this.config.closeOnProtocolViolation()) {
         Object closeMessage;
         if (this.receivedClosingHandshake) {
            closeMessage = Unpooled.EMPTY_BUFFER;
         } else {
            WebSocketCloseStatus closeStatus = ex.closeStatus();
            String reasonText = ex.getMessage();
            if (reasonText == null) {
               reasonText = closeStatus.reasonText();
            }

            closeMessage = new CloseWebSocketFrame(closeStatus, reasonText);
         }

         ctx.writeAndFlush(closeMessage).addListener(ChannelFutureListener.CLOSE);
      }

      throw ex;
   }

   private static int toFrameLength(long l) {
      if (l > 2147483647L) {
         throw new TooLongFrameException("frame length exceeds 2147483647: " + l);
      } else {
         return (int)l;
      }
   }

   protected void checkCloseFrameBody(ChannelHandlerContext ctx, ByteBuf buffer) {
      if (buffer != null && buffer.isReadable()) {
         if (buffer.readableBytes() < 2) {
            this.protocolViolation(ctx, buffer, WebSocketCloseStatus.INVALID_PAYLOAD_DATA, "Invalid close frame body");
         }

         int statusCode = buffer.getShort(buffer.readerIndex());
         if (!WebSocketCloseStatus.isValidStatusCode(statusCode)) {
            this.protocolViolation(ctx, buffer, "Invalid close frame getStatus code: " + statusCode);
         }

         if (buffer.readableBytes() > 2) {
            try {
               new Utf8Validator().check(buffer, buffer.readerIndex() + 2, buffer.readableBytes() - 2);
            } catch (CorruptedWebSocketFrameException var5) {
               this.protocolViolation(ctx, buffer, var5);
            }
         }
      }
   }

   static enum State {
      READING_FIRST,
      READING_SECOND,
      READING_SIZE,
      MASKING_KEY,
      PAYLOAD,
      CORRUPT;
   }
}
