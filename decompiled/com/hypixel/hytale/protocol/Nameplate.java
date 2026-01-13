package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Nameplate {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 1;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 1;
   public static final int MAX_SIZE = 16384006;
   @Nullable
   public String text;

   public Nameplate() {
   }

   public Nameplate(@Nullable String text) {
      this.text = text;
   }

   public Nameplate(@Nonnull Nameplate other) {
      this.text = other.text;
   }

   @Nonnull
   public static Nameplate deserialize(@Nonnull ByteBuf buf, int offset) {
      Nameplate obj = new Nameplate();
      byte nullBits = buf.getByte(offset);
      int pos = offset + 1;
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
      int pos = offset + 1;
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
      if (this.text != null) {
         PacketIO.writeVarString(buf, this.text, 4096000);
      }
   }

   public int computeSize() {
      int size = 1;
      if (this.text != null) {
         size += PacketIO.stringSize(this.text);
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 1) {
         return ValidationResult.error("Buffer too small: expected at least 1 bytes");
      } else {
         byte nullBits = buffer.getByte(offset);
         int pos = offset + 1;
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

   public Nameplate clone() {
      Nameplate copy = new Nameplate();
      copy.text = this.text;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof Nameplate other ? Objects.equals(this.text, other.text) : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.text);
   }
}
