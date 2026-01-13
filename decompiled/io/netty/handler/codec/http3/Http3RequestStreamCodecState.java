package io.netty.handler.codec.http3;

interface Http3RequestStreamCodecState {
   Http3RequestStreamCodecState NO_STATE = new Http3RequestStreamCodecState() {
      @Override
      public boolean started() {
         return false;
      }

      @Override
      public boolean receivedFinalHeaders() {
         return false;
      }

      @Override
      public boolean terminated() {
         return false;
      }
   };

   boolean started();

   boolean receivedFinalHeaders();

   boolean terminated();
}
