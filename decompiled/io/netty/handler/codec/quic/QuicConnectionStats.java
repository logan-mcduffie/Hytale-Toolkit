package io.netty.handler.codec.quic;

public interface QuicConnectionStats {
   long recv();

   long sent();

   long lost();

   long retrans();

   long sentBytes();

   long recvBytes();

   long lostBytes();

   long streamRetransBytes();

   long pathsCount();
}
