package io.netty.util.internal.shaded.org.jctools.queues;

public interface MessagePassingQueue<T> {
   int UNBOUNDED_CAPACITY = -1;

   boolean offer(T var1);

   T poll();

   T peek();

   int size();

   void clear();

   boolean isEmpty();

   int capacity();

   boolean relaxedOffer(T var1);

   T relaxedPoll();

   T relaxedPeek();

   int drain(MessagePassingQueue.Consumer<T> var1, int var2);

   int fill(MessagePassingQueue.Supplier<T> var1, int var2);

   int drain(MessagePassingQueue.Consumer<T> var1);

   int fill(MessagePassingQueue.Supplier<T> var1);

   void drain(MessagePassingQueue.Consumer<T> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3);

   void fill(MessagePassingQueue.Supplier<T> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3);

   public interface Consumer<T> {
      void accept(T var1);
   }

   public interface ExitCondition {
      boolean keepRunning();
   }

   public interface Supplier<T> {
      T get();
   }

   public interface WaitStrategy {
      int idle(int var1);
   }
}
