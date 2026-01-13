package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.EmptyArrays;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Objects;

public final class QuicConnectionAddress extends SocketAddress {
   static final QuicConnectionAddress NULL_LEN = new QuicConnectionAddress(EmptyArrays.EMPTY_BYTES);
   public static final QuicConnectionAddress EPHEMERAL = new QuicConnectionAddress(null, false);
   private final String toStr;
   private final ByteBuffer connId;

   public QuicConnectionAddress(byte[] connId) {
      this(ByteBuffer.wrap((byte[])connId.clone()), true);
   }

   public QuicConnectionAddress(ByteBuffer connId) {
      this(connId.duplicate(), true);
   }

   private QuicConnectionAddress(ByteBuffer connId, boolean validate) {
      Quic.ensureAvailability();
      if (validate && connId.remaining() > Quiche.QUICHE_MAX_CONN_ID_LEN) {
         throw new IllegalArgumentException("Connection ID can only be of max length " + Quiche.QUICHE_MAX_CONN_ID_LEN);
      } else {
         if (connId == null) {
            this.connId = null;
            this.toStr = "QuicConnectionAddress{EPHEMERAL}";
         } else {
            this.connId = connId.asReadOnlyBuffer().duplicate();
            ByteBuf buffer = Unpooled.wrappedBuffer(connId);

            try {
               this.toStr = "QuicConnectionAddress{connId=" + ByteBufUtil.hexDump(buffer) + '}';
            } finally {
               buffer.release();
            }
         }
      }
   }

   @Override
   public String toString() {
      return this.toStr;
   }

   @Override
   public int hashCode() {
      return this == EPHEMERAL ? System.identityHashCode(EPHEMERAL) : Objects.hash(this.connId);
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof QuicConnectionAddress)) {
         return false;
      } else {
         QuicConnectionAddress address = (QuicConnectionAddress)obj;
         return obj == this ? true : this.connId.equals(address.connId);
      }
   }

   ByteBuffer id() {
      return this.connId == null ? ByteBuffer.allocate(0) : this.connId.duplicate();
   }

   public static QuicConnectionAddress random(int length) {
      return new QuicConnectionAddress(QuicConnectionIdGenerator.randomGenerator().newId(length));
   }

   public static QuicConnectionAddress random() {
      return random(Quiche.QUICHE_MAX_CONN_ID_LEN);
   }
}
