package io.netty.channel;

import io.netty.util.ReferenceCounted;
import java.net.SocketAddress;

public interface AddressedEnvelope<M, A extends SocketAddress> extends ReferenceCounted {
   M content();

   A sender();

   A recipient();

   AddressedEnvelope<M, A> retain();

   AddressedEnvelope<M, A> retain(int var1);

   AddressedEnvelope<M, A> touch();

   AddressedEnvelope<M, A> touch(Object var1);
}
