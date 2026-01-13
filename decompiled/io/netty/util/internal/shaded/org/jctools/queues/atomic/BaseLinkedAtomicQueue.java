package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueueUtil;
import java.util.Iterator;

abstract class BaseLinkedAtomicQueue<E> extends BaseLinkedAtomicQueuePad2<E> {
   @Override
   public final Iterator<E> iterator() {
      throw new UnsupportedOperationException();
   }

   @Override
   public String toString() {
      return this.getClass().getName();
   }

   protected final LinkedQueueAtomicNode<E> newNode() {
      return new LinkedQueueAtomicNode<>();
   }

   protected final LinkedQueueAtomicNode<E> newNode(E e) {
      return new LinkedQueueAtomicNode<>(e);
   }

   @Override
   public final int size() {
      LinkedQueueAtomicNode<E> chaserNode = this.lvConsumerNode();
      LinkedQueueAtomicNode<E> producerNode = this.lvProducerNode();

      int size;
      for (size = 0; chaserNode != producerNode && chaserNode != null && size < Integer.MAX_VALUE; size++) {
         LinkedQueueAtomicNode<E> next = chaserNode.lvNext();
         if (next == chaserNode) {
            return size;
         }

         chaserNode = next;
      }

      return size;
   }

   @Override
   public boolean isEmpty() {
      LinkedQueueAtomicNode<E> consumerNode = this.lvConsumerNode();
      LinkedQueueAtomicNode<E> producerNode = this.lvProducerNode();
      return consumerNode == producerNode;
   }

   protected E getSingleConsumerNodeValue(LinkedQueueAtomicNode<E> currConsumerNode, LinkedQueueAtomicNode<E> nextNode) {
      E nextValue = nextNode.getAndNullValue();
      currConsumerNode.soNext(currConsumerNode);
      this.spConsumerNode(nextNode);
      return nextValue;
   }

   @Override
   public E poll() {
      LinkedQueueAtomicNode<E> currConsumerNode = this.lpConsumerNode();
      LinkedQueueAtomicNode<E> nextNode = currConsumerNode.lvNext();
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
      LinkedQueueAtomicNode<E> currConsumerNode = this.lpConsumerNode();
      LinkedQueueAtomicNode<E> nextNode = currConsumerNode.lvNext();
      if (nextNode != null) {
         return nextNode.lpValue();
      } else if (currConsumerNode != this.lvProducerNode()) {
         nextNode = this.spinWaitForNextNode(currConsumerNode);
         return nextNode.lpValue();
      } else {
         return null;
      }
   }

   LinkedQueueAtomicNode<E> spinWaitForNextNode(LinkedQueueAtomicNode<E> currNode) {
      LinkedQueueAtomicNode<E> nextNode;
      while ((nextNode = currNode.lvNext()) == null) {
      }

      return nextNode;
   }

   @Override
   public E relaxedPoll() {
      LinkedQueueAtomicNode<E> currConsumerNode = this.lpConsumerNode();
      LinkedQueueAtomicNode<E> nextNode = currConsumerNode.lvNext();
      return nextNode != null ? this.getSingleConsumerNodeValue(currConsumerNode, nextNode) : null;
   }

   @Override
   public E relaxedPeek() {
      LinkedQueueAtomicNode<E> nextNode = this.lpConsumerNode().lvNext();
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
         LinkedQueueAtomicNode<E> chaserNode = this.lpConsumerNode();

         for (int i = 0; i < limit; i++) {
            LinkedQueueAtomicNode<E> nextNode = chaserNode.lvNext();
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
