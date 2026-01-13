package com.hypixel.hytale.protocol.packets.setup;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldLoadProgress implements Packet {
   public static final int PACKET_ID = 21;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 9;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 9;
   public static final int MAX_SIZE = 16384014;
   @Nullable
   public String status;
   public int percentComplete;
   public int percentCompleteSubitem;

   @Override
   public int getId() {
      return 21;
   }

   public WorldLoadProgress() {
   }

   public WorldLoadProgress(@Nullable String status, int percentComplete, int percentCompleteSubitem) {
      this.status = status;
      this.percentComplete = percentComplete;
      this.percentCompleteSubitem = percentCompleteSubitem;
   }

   public WorldLoadProgress(@Nonnull WorldLoadProgress other) {
      this.status = other.status;
      this.percentComplete = other.percentComplete;
      this.percentCompleteSubitem = other.percentCompleteSubitem;
   }

   @Nonnull
   public static WorldLoadProgress deserialize(@Nonnull ByteBuf buf, int offset) {
      WorldLoadProgress obj = new WorldLoadProgress();
      byte nullBits = buf.getByte(offset);
      obj.percentComplete = buf.getIntLE(offset + 1);
      obj.percentCompleteSubitem = buf.getIntLE(offset + 5);
      int pos = offset + 9;
      if ((nullBits & 1) != 0) {
         int statusLen = VarInt.peek(buf, pos);
         if (statusLen < 0) {
            throw ProtocolException.negativeLength("Status", statusLen);
         }

         if (statusLen > 4096000) {
            throw ProtocolException.stringTooLong("Status", statusLen, 4096000);
         }

         int statusVarLen = VarInt.length(buf, pos);
         obj.status = PacketIO.readVarString(buf, pos, PacketIO.UTF8);
         pos += statusVarLen + statusLen;
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int pos = offset + 9;
      if ((nullBits & 1) != 0) {
         int sl = VarInt.peek(buf, pos);
         pos += VarInt.length(buf, pos) + sl;
      }

      return pos - offset;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.status != null) {
         nullBits = (byte)(nullBits | 1);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.percentComplete);
      buf.writeIntLE(this.percentCompleteSubitem);
      if (this.status != null) {
         PacketIO.writeVarString(buf, this.status, 4096000);
      }
   }

   @Override
   public int computeSize() {
      int size = 9;
      if (this.status != null) {
         size += PacketIO.stringSize(this.status);
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 9) {
         return ValidationResult.error("Buffer too small: expected at least 9 bytes");
      } else {
         byte nullBits = buffer.getByte(offset);
         int pos = offset + 9;
         if ((nullBits & 1) != 0) {
            int statusLen = VarInt.peek(buffer, pos);
            if (statusLen < 0) {
               return ValidationResult.error("Invalid string length for Status");
            }

            if (statusLen > 4096000) {
               return ValidationResult.error("Status exceeds max length 4096000");
            }

            pos += VarInt.length(buffer, pos);
            pos += statusLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading Status");
            }
         }

         return ValidationResult.OK;
      }
   }

   public WorldLoadProgress clone() {
      WorldLoadProgress copy = new WorldLoadProgress();
      copy.status = this.status;
      copy.percentComplete = this.percentComplete;
      copy.percentCompleteSubitem = this.percentCompleteSubitem;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof WorldLoadProgress other)
            ? false
            : Objects.equals(this.status, other.status)
               && this.percentComplete == other.percentComplete
               && this.percentCompleteSubitem == other.percentCompleteSubitem;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.status, this.percentComplete, this.percentCompleteSubitem);
   }
}
