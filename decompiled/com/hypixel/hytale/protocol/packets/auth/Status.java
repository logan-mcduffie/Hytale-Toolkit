package com.hypixel.hytale.protocol.packets.auth;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Status implements Packet {
   public static final int PACKET_ID = 10;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 9;
   public static final int VARIABLE_FIELD_COUNT = 2;
   public static final int VARIABLE_BLOCK_START = 17;
   public static final int MAX_SIZE = 2587;
   @Nullable
   public String name;
   @Nullable
   public String motd;
   public int playerCount;
   public int maxPlayers;

   @Override
   public int getId() {
      return 10;
   }

   public Status() {
   }

   public Status(@Nullable String name, @Nullable String motd, int playerCount, int maxPlayers) {
      this.name = name;
      this.motd = motd;
      this.playerCount = playerCount;
      this.maxPlayers = maxPlayers;
   }

   public Status(@Nonnull Status other) {
      this.name = other.name;
      this.motd = other.motd;
      this.playerCount = other.playerCount;
      this.maxPlayers = other.maxPlayers;
   }

   @Nonnull
   public static Status deserialize(@Nonnull ByteBuf buf, int offset) {
      Status obj = new Status();
      byte nullBits = buf.getByte(offset);
      obj.playerCount = buf.getIntLE(offset + 1);
      obj.maxPlayers = buf.getIntLE(offset + 5);
      if ((nullBits & 1) != 0) {
         int varPos0 = offset + 17 + buf.getIntLE(offset + 9);
         int nameLen = VarInt.peek(buf, varPos0);
         if (nameLen < 0) {
            throw ProtocolException.negativeLength("Name", nameLen);
         }

         if (nameLen > 128) {
            throw ProtocolException.stringTooLong("Name", nameLen, 128);
         }

         obj.name = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
      }

      if ((nullBits & 2) != 0) {
         int varPos1 = offset + 17 + buf.getIntLE(offset + 13);
         int motdLen = VarInt.peek(buf, varPos1);
         if (motdLen < 0) {
            throw ProtocolException.negativeLength("Motd", motdLen);
         }

         if (motdLen > 512) {
            throw ProtocolException.stringTooLong("Motd", motdLen, 512);
         }

         obj.motd = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 17;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 9);
         int pos0 = offset + 17 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.length(buf, pos0) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 13);
         int pos1 = offset + 17 + fieldOffset1;
         int sl = VarInt.peek(buf, pos1);
         pos1 += VarInt.length(buf, pos1) + sl;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      return maxEnd;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.name != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.motd != null) {
         nullBits = (byte)(nullBits | 2);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.playerCount);
      buf.writeIntLE(this.maxPlayers);
      int nameOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int motdOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.name != null) {
         buf.setIntLE(nameOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.name, 128);
      } else {
         buf.setIntLE(nameOffsetSlot, -1);
      }

      if (this.motd != null) {
         buf.setIntLE(motdOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.motd, 512);
      } else {
         buf.setIntLE(motdOffsetSlot, -1);
      }
   }

   @Override
   public int computeSize() {
      int size = 17;
      if (this.name != null) {
         size += PacketIO.stringSize(this.name);
      }

      if (this.motd != null) {
         size += PacketIO.stringSize(this.motd);
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 17) {
         return ValidationResult.error("Buffer too small: expected at least 17 bytes");
      } else {
         byte nullBits = buffer.getByte(offset);
         if ((nullBits & 1) != 0) {
            int nameOffset = buffer.getIntLE(offset + 9);
            if (nameOffset < 0) {
               return ValidationResult.error("Invalid offset for Name");
            }

            int pos = offset + 17 + nameOffset;
            if (pos >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for Name");
            }

            int nameLen = VarInt.peek(buffer, pos);
            if (nameLen < 0) {
               return ValidationResult.error("Invalid string length for Name");
            }

            if (nameLen > 128) {
               return ValidationResult.error("Name exceeds max length 128");
            }

            pos += VarInt.length(buffer, pos);
            pos += nameLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading Name");
            }
         }

         if ((nullBits & 2) != 0) {
            int motdOffset = buffer.getIntLE(offset + 13);
            if (motdOffset < 0) {
               return ValidationResult.error("Invalid offset for Motd");
            }

            int posx = offset + 17 + motdOffset;
            if (posx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for Motd");
            }

            int motdLen = VarInt.peek(buffer, posx);
            if (motdLen < 0) {
               return ValidationResult.error("Invalid string length for Motd");
            }

            if (motdLen > 512) {
               return ValidationResult.error("Motd exceeds max length 512");
            }

            posx += VarInt.length(buffer, posx);
            posx += motdLen;
            if (posx > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading Motd");
            }
         }

         return ValidationResult.OK;
      }
   }

   public Status clone() {
      Status copy = new Status();
      copy.name = this.name;
      copy.motd = this.motd;
      copy.playerCount = this.playerCount;
      copy.maxPlayers = this.maxPlayers;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof Status other)
            ? false
            : Objects.equals(this.name, other.name)
               && Objects.equals(this.motd, other.motd)
               && this.playerCount == other.playerCount
               && this.maxPlayers == other.maxPlayers;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.name, this.motd, this.playerCount, this.maxPlayers);
   }
}
