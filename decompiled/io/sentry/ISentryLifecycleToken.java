package io.sentry;

public interface ISentryLifecycleToken extends AutoCloseable {
   @Override
   void close();
}
