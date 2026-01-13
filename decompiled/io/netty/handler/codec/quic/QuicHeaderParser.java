package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;

public final class QuicHeaderParser implements AutoCloseable {
   private static final int AES_128_GCM_TAG_LENGTH = 16;
   private final int localConnectionIdLength;
   private boolean closed;

   public QuicHeaderParser(int localConnectionIdLength) {
      this.localConnectionIdLength = ObjectUtil.checkPositiveOrZero(localConnectionIdLength, "localConnectionIdLength");
   }

   @Override
   public void close() {
      if (!this.closed) {
         this.closed = true;
      }
   }

   public void parse(InetSocketAddress sender, InetSocketAddress recipient, ByteBuf packet, QuicHeaderParser.QuicHeaderProcessor callback) throws Exception {
      if (this.closed) {
         throw new IllegalStateException(QuicHeaderParser.class.getSimpleName() + " is already closed");
      } else {
         int offset = 0;
         int readable = packet.readableBytes();
         checkReadable(offset, readable, 1);
         byte first = packet.getByte(offset);
         offset++;
         QuicPacketType type;
         long version;
         ByteBuf dcid;
         ByteBuf scid;
         ByteBuf token;
         if (hasShortHeader(first)) {
            version = 0L;
            type = QuicPacketType.SHORT;
            scid = Unpooled.EMPTY_BUFFER;
            token = Unpooled.EMPTY_BUFFER;
            dcid = sliceCid(packet, offset, this.localConnectionIdLength);
         } else {
            checkReadable(offset, readable, 4);
            version = packet.getUnsignedInt(offset);
            offset += 4;
            type = typeOfLongHeader(first, version);
            int dcidLen = packet.getUnsignedByte(offset);
            checkCidLength(dcidLen);
            dcid = sliceCid(packet, ++offset, dcidLen);
            offset += dcidLen;
            int scidLen = packet.getUnsignedByte(offset);
            checkCidLength(scidLen);
            scid = sliceCid(packet, ++offset, scidLen);
            offset += scidLen;
            token = sliceToken(type, packet, offset, readable);
         }

         callback.process(sender, recipient, packet, type, version, scid, dcid, token);
      }
   }

   private static void checkCidLength(int length) throws QuicException {
      if (length > 20) {
         throw new QuicException("connection id to large: " + length + " > " + 20, QuicTransportError.PROTOCOL_VIOLATION);
      }
   }

   private static ByteBuf sliceToken(QuicPacketType type, ByteBuf packet, int offset, int readable) throws QuicException {
      switch (type) {
         case INITIAL:
            checkReadable(offset, readable, 1);
            int numBytes = numBytesForVariableLengthInteger(packet.getByte(offset));
            int len = (int)getVariableLengthInteger(packet, offset, numBytes);
            offset += numBytes;
            checkReadable(offset, readable, len);
            return packet.slice(offset, len);
         case RETRY:
            checkReadable(offset, readable, 16);
            int tokenLen = readable - offset - 16;
            return packet.slice(offset, tokenLen);
         default:
            return Unpooled.EMPTY_BUFFER;
      }
   }

   private static QuicException newProtocolViolationException(String message) {
      return new QuicException(message, QuicTransportError.PROTOCOL_VIOLATION);
   }

   static ByteBuf sliceCid(ByteBuf buffer, int offset, int len) throws QuicException {
      checkReadable(offset, buffer.readableBytes(), len);
      return buffer.slice(offset, len);
   }

   private static void checkReadable(int offset, int readable, int needed) throws QuicException {
      int r = readable - offset;
      if (r < needed) {
         throw newProtocolViolationException("Not enough bytes to read, " + r + " < " + needed);
      }
   }

   private static long getVariableLengthInteger(ByteBuf in, int offset, int len) throws QuicException {
      checkReadable(offset, in.readableBytes(), len);
      switch (len) {
         case 1:
            return in.getUnsignedByte(offset);
         case 2:
            return in.getUnsignedShort(offset) & 16383;
         case 3:
         case 5:
         case 6:
         case 7:
         default:
            throw newProtocolViolationException("Unsupported length:" + len);
         case 4:
            return in.getUnsignedInt(offset) & 1073741823L;
         case 8:
            return in.getLong(offset) & 4611686018427387903L;
      }
   }

   private static int numBytesForVariableLengthInteger(byte b) {
      byte val = (byte)(b >> 6);
      if ((val & 1) != 0) {
         return (val & 2) != 0 ? 8 : 2;
      } else {
         return (val & 2) != 0 ? 4 : 1;
      }
   }

   static boolean hasShortHeader(byte b) {
      return (b & 128) == 0;
   }

   private static QuicPacketType typeOfLongHeader(byte first, long version) throws QuicException {
      if (version == 0L) {
         return QuicPacketType.VERSION_NEGOTIATION;
      } else {
         int packetType = (first & 48) >> 4;
         switch (packetType) {
            case 0:
               return QuicPacketType.INITIAL;
            case 1:
               return QuicPacketType.ZERO_RTT;
            case 2:
               return QuicPacketType.HANDSHAKE;
            case 3:
               return QuicPacketType.RETRY;
            default:
               throw newProtocolViolationException("Unknown packet type: " + packetType);
         }
      }
   }

   public interface QuicHeaderProcessor {
      void process(InetSocketAddress var1, InetSocketAddress var2, ByteBuf var3, QuicPacketType var4, long var5, ByteBuf var7, ByteBuf var8, ByteBuf var9) throws Exception;
   }
}
