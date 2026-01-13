package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueueUtil;

public class SpscLinkedAtomicQueue<E> extends BaseLinkedAtomicQueue<E> {
   public SpscLinkedAtomicQueue() {
      LinkedQueueAtomicNode<E> node = this.newNode();
      this.spProducerNode(node);
      this.spConsumerNode(node);
      node.soNext(null);
   }

   @Override
   public boolean offer(E e) {
      if (null == e) {
         throw new NullPointerException();
      } else {
         LinkedQueueAtomicNode<E> nextNode = this.newNode(e);
         LinkedQueueAtomicNode<E> oldNode = this.lpProducerNode();
         this.soProducerNode(nextNode);
         oldNode.soNext(nextNode);
         return true;
      }
   }

   @Override
   public int fill(MessagePassingQueue.Supplier<E> s) {
      return MessagePassingQueueUtil.fillUnbounded(this, s);
   }

   @Override
   public int fill(MessagePassingQueue.Supplier<E> s, int limit) {
      if (null == s) {
         throw new IllegalArgumentException("supplier is null");
      } else if (limit < 0) {
         throw new IllegalArgumentException("limit is negative:" + limit);
      } else if (limit == 0) {
         return 0;
      } else {
         LinkedQueueAtomicNode<E> tail = this.newNode(s.get());
         LinkedQueueAtomicNode<E> head = tail;

         for (int i = 1; i < limit; i++) {
            LinkedQueueAtomicNode<E> temp = this.newNode(s.get());
            tail.spNext(temp);
            tail = temp;
         }

         LinkedQueueAtomicNode<E> oldPNode = this.lpProducerNode();
         this.soProducerNode(tail);
         oldPNode.soNext(head);
         return limit;
      }
   }

   @Override
   public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit) {
      MessagePassingQueueUtil.fill(this, s, wait, exit);
   }
}
