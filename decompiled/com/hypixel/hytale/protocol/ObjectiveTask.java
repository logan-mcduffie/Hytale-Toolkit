package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ObjectiveTask {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 9;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 9;
   public static final int MAX_SIZE = 16384014;
   @Nullable
   public String taskDescriptionKey;
   public int currentCompletion;
   public int completionNeeded;

   public ObjectiveTask() {
   }

   public ObjectiveTask(@Nullable String taskDescriptionKey, int currentCompletion, int completionNeeded) {
      this.taskDescriptionKey = taskDescriptionKey;
      this.currentCompletion = currentCompletion;
      this.completionNeeded = completionNeeded;
   }

   public ObjectiveTask(@Nonnull ObjectiveTask other) {
      this.taskDescriptionKey = other.taskDescriptionKey;
      this.currentCompletion = other.currentCompletion;
      this.completionNeeded = other.completionNeeded;
   }

   @Nonnull
   public static ObjectiveTask deserialize(@Nonnull ByteBuf buf, int offset) {
      ObjectiveTask obj = new ObjectiveTask();
      byte nullBits = buf.getByte(offset);
      obj.currentCompletion = buf.getIntLE(offset + 1);
      obj.completionNeeded = buf.getIntLE(offset + 5);
      int pos = offset + 9;
      if ((nullBits & 1) != 0) {
         int taskDescriptionKeyLen = VarInt.peek(buf, pos);
         if (taskDescriptionKeyLen < 0) {
            throw ProtocolException.negativeLength("TaskDescriptionKey", taskDescriptionKeyLen);
         }

         if (taskDescriptionKeyLen > 4096000) {
            throw ProtocolException.stringTooLong("TaskDescriptionKey", taskDescriptionKeyLen, 4096000);
         }

         int taskDescriptionKeyVarLen = VarInt.length(buf, pos);
         obj.taskDescriptionKey = PacketIO.readVarString(buf, pos, PacketIO.UTF8);
         pos += taskDescriptionKeyVarLen + taskDescriptionKeyLen;
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

   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.taskDescriptionKey != null) {
         nullBits = (byte)(nullBits | 1);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.currentCompletion);
      buf.writeIntLE(this.completionNeeded);
      if (this.taskDescriptionKey != null) {
         PacketIO.writeVarString(buf, this.taskDescriptionKey, 4096000);
      }
   }

   public int computeSize() {
      int size = 9;
      if (this.taskDescriptionKey != null) {
         size += PacketIO.stringSize(this.taskDescriptionKey);
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
            int taskDescriptionKeyLen = VarInt.peek(buffer, pos);
            if (taskDescriptionKeyLen < 0) {
               return ValidationResult.error("Invalid string length for TaskDescriptionKey");
            }

            if (taskDescriptionKeyLen > 4096000) {
               return ValidationResult.error("TaskDescriptionKey exceeds max length 4096000");
            }

            pos += VarInt.length(buffer, pos);
            pos += taskDescriptionKeyLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading TaskDescriptionKey");
            }
         }

         return ValidationResult.OK;
      }
   }

   public ObjectiveTask clone() {
      ObjectiveTask copy = new ObjectiveTask();
      copy.taskDescriptionKey = this.taskDescriptionKey;
      copy.currentCompletion = this.currentCompletion;
      copy.completionNeeded = this.completionNeeded;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof ObjectiveTask other)
            ? false
            : Objects.equals(this.taskDescriptionKey, other.taskDescriptionKey)
               && this.currentCompletion == other.currentCompletion
               && this.completionNeeded == other.completionNeeded;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.taskDescriptionKey, this.currentCompletion, this.completionNeeded);
   }
}
