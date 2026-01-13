package io.netty.resolver;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.io.Closeable;
import java.net.SocketAddress;
import java.util.List;

public interface AddressResolver<T extends SocketAddress> extends Closeable {
   boolean isSupported(SocketAddress var1);

   boolean isResolved(SocketAddress var1);

   Future<T> resolve(SocketAddress var1);

   Future<T> resolve(SocketAddress var1, Promise<T> var2);

   Future<List<T>> resolveAll(SocketAddress var1);

   Future<List<T>> resolveAll(SocketAddress var1, Promise<List<T>> var2);

   @Override
   void close();
}
