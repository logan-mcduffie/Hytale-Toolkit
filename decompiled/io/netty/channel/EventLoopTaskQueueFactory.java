package io.netty.channel;

import java.util.Queue;

@Deprecated
public interface EventLoopTaskQueueFactory {
   Queue<Runnable> newTaskQueue(int var1);
}
