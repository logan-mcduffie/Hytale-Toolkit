package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;

final class HeapByteBufUtil {
   static byte getByte(byte[] memory, int index) {
      return memory[index];
   }

   static short getShort(byte[] memory, int index) {
      return PlatformDependent.hasVarHandle() ? VarHandleByteBufferAccess.getShortBE(memory, index) : getShort0(memory, index);
   }

   private static short getShort0(byte[] memory, int index) {
      return (short)(memory[index] << 8 | memory[index + 1] & 255);
   }

   static short getShortLE(byte[] memory, int index) {
      return PlatformDependent.hasVarHandle() ? VarHandleByteBufferAccess.getShortLE(memory, index) : (short)(memory[index] & 255 | memory[index + 1] << 8);
   }

   static int getUnsignedMedium(byte[] memory, int index) {
      return (memory[index] & 0xFF) << 16 | (memory[index + 1] & 0xFF) << 8 | memory[index + 2] & 0xFF;
   }

   static int getUnsignedMediumLE(byte[] memory, int index) {
      return memory[index] & 0xFF | (memory[index + 1] & 0xFF) << 8 | (memory[index + 2] & 0xFF) << 16;
   }

   static int getInt(byte[] memory, int index) {
      return PlatformDependent.hasVarHandle() ? VarHandleByteBufferAccess.getIntBE(memory, index) : getInt0(memory, index);
   }

   private static int getInt0(byte[] memory, int index) {
      return (memory[index] & 0xFF) << 24 | (memory[index + 1] & 0xFF) << 16 | (memory[index + 2] & 0xFF) << 8 | memory[index + 3] & 0xFF;
   }

   static int getIntLE(byte[] memory, int index) {
      return PlatformDependent.hasVarHandle() ? VarHandleByteBufferAccess.getIntLE(memory, index) : getIntLE0(memory, index);
   }

   private static int getIntLE0(byte[] memory, int index) {
      return memory[index] & 0xFF | (memory[index + 1] & 0xFF) << 8 | (memory[index + 2] & 0xFF) << 16 | (memory[index + 3] & 0xFF) << 24;
   }

   static long getLong(byte[] memory, int index) {
      return PlatformDependent.hasVarHandle() ? VarHandleByteBufferAccess.getLongBE(memory, index) : getLong0(memory, index);
   }

   private static long getLong0(byte[] memory, int index) {
      return (memory[index] & 255L) << 56
         | (memory[index + 1] & 255L) << 48
         | (memory[index + 2] & 255L) << 40
         | (memory[index + 3] & 255L) << 32
         | (memory[index + 4] & 255L) << 24
         | (memory[index + 5] & 255L) << 16
         | (memory[index + 6] & 255L) << 8
         | memory[index + 7] & 255L;
   }

   static long getLongLE(byte[] memory, int index) {
      return PlatformDependent.hasVarHandle() ? VarHandleByteBufferAccess.getLongLE(memory, index) : getLongLE0(memory, index);
   }

   private static long getLongLE0(byte[] memory, int index) {
      return memory[index] & 255L
         | (memory[index + 1] & 255L) << 8
         | (memory[index + 2] & 255L) << 16
         | (memory[index + 3] & 255L) << 24
         | (memory[index + 4] & 255L) << 32
         | (memory[index + 5] & 255L) << 40
         | (memory[index + 6] & 255L) << 48
         | (memory[index + 7] & 255L) << 56;
   }

   static void setByte(byte[] memory, int index, int value) {
      memory[index] = (byte)(value & 0xFF);
   }

   static void setShort(byte[] memory, int index, int value) {
      if (PlatformDependent.hasVarHandle()) {
         VarHandleByteBufferAccess.setShortBE(memory, index, value);
      } else {
         memory[index] = (byte)(value >>> 8);
         memory[index + 1] = (byte)value;
      }
   }

   static void setShortLE(byte[] memory, int index, int value) {
      if (PlatformDependent.hasVarHandle()) {
         VarHandleByteBufferAccess.setShortLE(memory, index, value);
      } else {
         memory[index] = (byte)value;
         memory[index + 1] = (byte)(value >>> 8);
      }
   }

   static void setMedium(byte[] memory, int index, int value) {
      memory[index] = (byte)(value >>> 16);
      memory[index + 1] = (byte)(value >>> 8);
      memory[index + 2] = (byte)value;
   }

   static void setMediumLE(byte[] memory, int index, int value) {
      memory[index] = (byte)value;
      memory[index + 1] = (byte)(value >>> 8);
      memory[index + 2] = (byte)(value >>> 16);
   }

   static void setInt(byte[] memory, int index, int value) {
      if (PlatformDependent.hasVarHandle()) {
         VarHandleByteBufferAccess.setIntBE(memory, index, value);
      } else {
         setInt0(memory, index, value);
      }
   }

   private static void setInt0(byte[] memory, int index, int value) {
      memory[index] = (byte)(value >>> 24);
      memory[index + 1] = (byte)(value >>> 16);
      memory[index + 2] = (byte)(value >>> 8);
      memory[index + 3] = (byte)value;
   }

   static void setIntLE(byte[] memory, int index, int value) {
      if (PlatformDependent.hasVarHandle()) {
         VarHandleByteBufferAccess.setIntLE(memory, index, value);
      } else {
         setIntLE0(memory, index, value);
      }
   }

   private static void setIntLE0(byte[] memory, int index, int value) {
      memory[index] = (byte)value;
      memory[index + 1] = (byte)(value >>> 8);
      memory[index + 2] = (byte)(value >>> 16);
      memory[index + 3] = (byte)(value >>> 24);
   }

   static void setLong(byte[] memory, int index, long value) {
      if (PlatformDependent.hasVarHandle()) {
         VarHandleByteBufferAccess.setLongBE(memory, index, value);
      } else {
         setLong0(memory, index, value);
      }
   }

   private static void setLong0(byte[] memory, int index, long value) {
      memory[index] = (byte)(value >>> 56);
      memory[index + 1] = (byte)(value >>> 48);
      memory[index + 2] = (byte)(value >>> 40);
      memory[index + 3] = (byte)(value >>> 32);
      memory[index + 4] = (byte)(value >>> 24);
      memory[index + 5] = (byte)(value >>> 16);
      memory[index + 6] = (byte)(value >>> 8);
      memory[index + 7] = (byte)value;
   }

   static void setLongLE(byte[] memory, int index, long value) {
      if (PlatformDependent.hasVarHandle()) {
         VarHandleByteBufferAccess.setLongLE(memory, index, value);
      } else {
         setLongLE0(memory, index, value);
      }
   }

   private static void setLongLE0(byte[] memory, int index, long value) {
      memory[index] = (byte)value;
      memory[index + 1] = (byte)(value >>> 8);
      memory[index + 2] = (byte)(value >>> 16);
      memory[index + 3] = (byte)(value >>> 24);
      memory[index + 4] = (byte)(value >>> 32);
      memory[index + 5] = (byte)(value >>> 40);
      memory[index + 6] = (byte)(value >>> 48);
      memory[index + 7] = (byte)(value >>> 56);
   }

   private HeapByteBufUtil() {
   }
}
