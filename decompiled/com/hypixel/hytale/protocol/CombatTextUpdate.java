package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CombatTextUpdate {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 5;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 5;
   public static final int MAX_SIZE = 16384010;
   public float hitAngleDeg;
   @Nullable
   public String text;

   public CombatTextUpdate() {
   }

   public CombatTextUpdate(float hitAngleDeg, @Nullable String text) {
      this.hitAngleDeg = hitAngleDeg;
      this.text = text;
   }

   public CombatTextUpdate(@Nonnull CombatTextUpdate other) {
      this.hitAngleDeg = other.hitAngleDeg;
      this.text = other.text;
   }

   @Nonnull
   public static CombatTextUpdate deserialize(@Nonnull ByteBuf buf, int offset) {
      CombatTextUpdate obj = new CombatTextUpdate();
      byte nullBits = buf.getByte(offset);
      obj.hitAngleDeg = buf.getFloatLE(offset + 1);
      int pos = offset + 5;
      if ((nullBits & 1) != 0) {
         int textLen = VarInt.peek(buf, pos);
         if (textLen < 0) {
            throw ProtocolException.negativeLength("Text", textLen);
         }

         if (textLen > 4096000) {
            throw ProtocolException.stringTooLong("Text", textLen, 4096000);
         }

         int textVarLen = VarInt.length(buf, pos);
         obj.text = PacketIO.readVarString(buf, pos, PacketIO.UTF8);
         pos += textVarLen + textLen;
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int pos = offset + 5;
      if ((nullBits & 1) != 0) {
         int sl = VarInt.peek(buf, pos);
         pos += VarInt.length(buf, pos) + sl;
      }

      return pos - offset;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.text != null) {
         nullBits = (byte)(nullBits | 1);
      }

      buf.writeByte(nullBits);
      buf.writeFloatLE(this.hitAngleDeg);
      if (this.text != null) {
         PacketIO.writeVarString(buf, this.text, 4096000);
      }
   }

   public int computeSize() {
      int size = 5;
      if (this.text != null) {
         size += PacketIO.stringSize(this.text);
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 5) {
         return ValidationResult.error("Buffer too small: expected at least 5 bytes");
      } else {
         byte nullBits = buffer.getByte(offset);
         int pos = offset + 5;
         if ((nullBits & 1) != 0) {
            int textLen = VarInt.peek(buffer, pos);
            if (textLen < 0) {
               return ValidationResult.error("Invalid string length for Text");
            }

            if (textLen > 4096000) {
               return ValidationResult.error("Text exceeds max length 4096000");
            }

            pos += VarInt.length(buffer, pos);
            pos += textLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading Text");
            }
         }

         return ValidationResult.OK;
      }
   }

   public CombatTextUpdate clone() {
      CombatTextUpdate copy = new CombatTextUpdate();
      copy.hitAngleDeg = this.hitAngleDeg;
      copy.text = this.text;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof CombatTextUpdate other) ? false : this.hitAngleDeg == other.hitAngleDeg && Objects.equals(this.text, other.text);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.hitAngleDeg, this.text);
   }
}
