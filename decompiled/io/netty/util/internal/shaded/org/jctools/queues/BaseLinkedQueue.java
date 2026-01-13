package io.netty.util.internal.shaded.org.jctools.queues;

import java.util.Iterator;

abstract class BaseLinkedQueue<E> extends BaseLinkedQueuePad2<E> {
   @Override
   public final Iterator<E> iterator() {
      throw new UnsupportedOperationException();
   }

   @Override
   public String toString() {
      return this.getClass().getName();
   }

   protected final LinkedQueueNode<E> newNode() {
      return new LinkedQueueNode<>();
   }

   protected final LinkedQueueNode<E> newNode(E e) {
      return new LinkedQueueNode<>(e);
   }

   @Override
   public final int size() {
      LinkedQueueNode<E> chaserNode = this.lvConsumerNode();
      LinkedQueueNode<E> producerNode = this.lvProducerNode();

      int size;
      for (size = 0; chaserNode != producerNode && chaserNode != null && size < Integer.MAX_VALUE; size++) {
         LinkedQueueNode<E> next = chaserNode.lvNext();
         if (next == chaserNode) {
            return size;
         }

         chaserNode = next;
      }

      return size;
   }

   @Override
   public boolean isEmpty() {
      LinkedQueueNode<E> consumerNode = this.lvConsumerNode();
      LinkedQueueNode<E> producerNode = this.lvProducerNode();
      return consumerNode == producerNode;
   }

   protected E getSingleConsumerNodeValue(LinkedQueueNode<E> currConsumerNode, LinkedQueueNode<E> nextNode) {
      E nextValue = nextNode.getAndNullValue();
      currConsumerNode.soNext(currConsumerNode);
      this.spConsumerNode(nextNode);
      return nextValue;
   }

   @Override
   public E poll() {
      LinkedQueueNode<E> currConsumerNode = this.lpConsumerNode();
      LinkedQueueNode<E> nextNode = currConsumerNode.lvNext();
      if (nextNode != null) {
         return this.getSingleConsumerNodeValue(currConsumerNode, nextNode);
      } else if (currConsumerNode != this.lvProducerNode()) {
         nextNode = this.spinWaitForNextNode(currConsumerNode);
         return this.getSingleConsumerNodeValue(currConsumerNode, nextNode);
      } else {
         return null;
      }
   }

   @Override
   public E peek() {
      LinkedQueueNode<E> currConsumerNode = this.lpConsumerNode();
      LinkedQueueNode<E> nextNode = currConsumerNode.lvNext();
      if (nextNode != null) {
         return nextNode.lpValue();
      } else if (currConsumerNode != this.lvProducerNode()) {
         nextNode = this.spinWaitForNextNode(currConsumerNode);
         return nextNode.lpValue();
      } else {
         return null;
      }
   }

   LinkedQueueNode<E> spinWaitForNextNode(LinkedQueueNode<E> currNode) {
      LinkedQueueNode<E> nextNode;
      while ((nextNode = currNode.lvNext()) == null) {
      }

      return nextNode;
   }

   @Override
   public E relaxedPoll() {
      LinkedQueueNode<E> currConsumerNode = this.lpConsumerNode();
      LinkedQueueNode<E> nextNode = currConsumerNode.lvNext();
      return nextNode != null ? this.getSingleConsumerNodeValue(currConsumerNode, nextNode) : null;
   }

   @Override
   public E relaxedPeek() {
      LinkedQueueNode<E> nextNode = this.lpConsumerNode().lvNext();
      return nextNode != null ? nextNode.lpValue() : null;
   }

   @Override
   public boolean relaxedOffer(E e) {
      return this.offer(e);
   }

   @Override
   public int drain(MessagePassingQueue.Consumer<E> c, int limit) {
      if (null == c) {
         throw new IllegalArgumentException("c is null");
      } else if (limit < 0) {
         throw new IllegalArgumentException("limit is negative: " + limit);
      } else if (limit == 0) {
         return 0;
      } else {
         LinkedQueueNode<E> chaserNode = this.lpConsumerNode();

         for (int i = 0; i < limit; i++) {
            LinkedQueueNode<E> nextNode = chaserNode.lvNext();
            if (nextNode == null) {
               return i;
            }

            E nextValue = this.getSingleConsumerNodeValue(chaserNode, nextNode);
            chaserNode = nextNode;
            c.accept(nextValue);
         }

         return limit;
      }
   }

   @Override
   public int drain(MessagePassingQueue.Consumer<E> c) {
      return MessagePassingQueueUtil.drain(this, c);
   }

   @Override
   public void drain(MessagePassingQueue.Consumer<E> c, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit) {
      MessagePassingQueueUtil.drain(this, c, wait, exit);
   }

   @Override
   public int capacity() {
      return -1;
   }
}
