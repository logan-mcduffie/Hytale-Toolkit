package io.netty.resolver;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.io.Closeable;
import java.util.List;

public interface NameResolver<T> extends Closeable {
   Future<T> resolve(String var1);

   Future<T> resolve(String var1, Promise<T> var2);

   Future<List<T>> resolveAll(String var1);

   Future<List<T>> resolveAll(String var1, Promise<List<T>> var2);

   @Override
   void close();
}
