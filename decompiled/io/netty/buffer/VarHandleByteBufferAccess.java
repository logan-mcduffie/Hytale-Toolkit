package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

final class VarHandleByteBufferAccess {
   private VarHandleByteBufferAccess() {
   }

   static short getShortBE(ByteBuffer buffer, int index) {
      return (short)PlatformDependent.shortBeByteBufferView().get((ByteBuffer)buffer, (int)index);
   }

   static void setShortBE(ByteBuffer buffer, int index, int value) {
      PlatformDependent.shortBeByteBufferView().set((ByteBuffer)buffer, (int)index, (short)((short)value));
   }

   static short getShortLE(ByteBuffer buffer, int index) {
      return (short)PlatformDependent.shortLeByteBufferView().get((ByteBuffer)buffer, (int)index);
   }

   static void setShortLE(ByteBuffer buffer, int index, int value) {
      PlatformDependent.shortLeByteBufferView().set((ByteBuffer)buffer, (int)index, (short)((short)value));
   }

   static int getIntBE(ByteBuffer buffer, int index) {
      return (int)PlatformDependent.intBeByteBufferView().get((ByteBuffer)buffer, (int)index);
   }

   static void setIntBE(ByteBuffer buffer, int index, int value) {
      PlatformDependent.intBeByteBufferView().set((ByteBuffer)buffer, (int)index, (int)value);
   }

   static int getIntLE(ByteBuffer buffer, int index) {
      return (int)PlatformDependent.intLeByteBufferView().get((ByteBuffer)buffer, (int)index);
   }

   static void setIntLE(ByteBuffer buffer, int index, int value) {
      PlatformDependent.intLeByteBufferView().set((ByteBuffer)buffer, (int)index, (int)value);
   }

   static long getLongBE(ByteBuffer buffer, int index) {
      return (long)PlatformDependent.longBeByteBufferView().get((ByteBuffer)buffer, (int)index);
   }

   static void setLongBE(ByteBuffer buffer, int index, long value) {
      PlatformDependent.longBeByteBufferView().set((ByteBuffer)buffer, (int)index, (long)value);
   }

   static long getLongLE(ByteBuffer buffer, int index) {
      return (long)PlatformDependent.longLeByteBufferView().get((ByteBuffer)buffer, (int)index);
   }

   static void setLongLE(ByteBuffer buffer, int index, long value) {
      PlatformDependent.longLeByteBufferView().set((ByteBuffer)buffer, (int)index, (long)value);
   }

   static short getShortBE(byte[] memory, int index) {
      return (short)PlatformDependent.shortBeArrayView().get((byte[])memory, (int)index);
   }

   static void setShortBE(byte[] memory, int index, int value) {
      PlatformDependent.shortBeArrayView().set((byte[])memory, (int)index, (short)((short)value));
   }

   static short getShortLE(byte[] memory, int index) {
      return (short)PlatformDependent.shortLeArrayView().get((byte[])memory, (int)index);
   }

   static void setShortLE(byte[] memory, int index, int value) {
      PlatformDependent.shortLeArrayView().set((byte[])memory, (int)index, (short)((short)value));
   }

   static int getIntBE(byte[] memory, int index) {
      return (int)PlatformDependent.intBeArrayView().get((byte[])memory, (int)index);
   }

   static void setIntBE(byte[] memory, int index, int value) {
      PlatformDependent.intBeArrayView().set((byte[])memory, (int)index, (int)value);
   }

   static int getIntLE(byte[] memory, int index) {
      return (int)PlatformDependent.intLeArrayView().get((byte[])memory, (int)index);
   }

   static void setIntLE(byte[] memory, int index, int value) {
      PlatformDependent.intLeArrayView().set((byte[])memory, (int)index, (int)value);
   }

   static long getLongBE(byte[] memory, int index) {
      return (long)PlatformDependent.longBeArrayView().get((byte[])memory, (int)index);
   }

   static void setLongBE(byte[] memory, int index, long value) {
      PlatformDependent.longBeArrayView().set((byte[])memory, (int)index, (long)value);
   }

   static long getLongLE(byte[] memory, int index) {
      return (long)PlatformDependent.longLeArrayView().get((byte[])memory, (int)index);
   }

   static void setLongLE(byte[] memory, int index, long value) {
      PlatformDependent.longLeArrayView().set((byte[])memory, (int)index, (long)value);
   }
}
