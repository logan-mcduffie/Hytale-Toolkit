package io.netty.util.concurrent;

import java.util.concurrent.Executor;

public interface ThreadAwareExecutor extends Executor {
   boolean isExecutorThread(Thread var1);
}
