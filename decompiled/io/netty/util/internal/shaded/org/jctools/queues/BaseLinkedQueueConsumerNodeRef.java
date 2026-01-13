package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;

abstract class BaseLinkedQueueConsumerNodeRef<E> extends BaseLinkedQueuePad1<E> {
   private static final long C_NODE_OFFSET = UnsafeAccess.fieldOffset(BaseLinkedQueueConsumerNodeRef.class, "consumerNode");
   private LinkedQueueNode<E> consumerNode;

   final void spConsumerNode(LinkedQueueNode<E> newValue) {
      this.consumerNode = newValue;
   }

   final LinkedQueueNode<E> lvConsumerNode() {
      return (LinkedQueueNode<E>)UnsafeAccess.UNSAFE.getObjectVolatile(this, C_NODE_OFFSET);
   }

   final LinkedQueueNode<E> lpConsumerNode() {
      return this.consumerNode;
   }
}
