package io.netty.channel.epoll;

import io.netty.channel.unix.IovArray;

final class NativeArrays {
   private IovArray iovArray;
   private NativeDatagramPacketArray datagramPacketArray;

   IovArray cleanIovArray() {
      if (this.iovArray == null) {
         this.iovArray = new IovArray();
      } else {
         this.iovArray.clear();
      }

      return this.iovArray;
   }

   NativeDatagramPacketArray cleanDatagramPacketArray() {
      if (this.datagramPacketArray == null) {
         this.datagramPacketArray = new NativeDatagramPacketArray();
      } else {
         this.datagramPacketArray.clear();
      }

      return this.datagramPacketArray;
   }

   void free() {
      if (this.iovArray != null) {
         this.iovArray.release();
         this.iovArray = null;
      }

      if (this.datagramPacketArray != null) {
         this.datagramPacketArray.release();
         this.datagramPacketArray = null;
      }
   }
}
