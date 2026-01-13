package io.netty.handler.codec.http3;

public interface QpackDecoderStateSyncStrategy {
   void sectionAcknowledged(int var1);

   boolean entryAdded(int var1);

   static QpackDecoderStateSyncStrategy ackEachInsert() {
      return new QpackDecoderStateSyncStrategy() {
         private int lastCountAcknowledged;

         @Override
         public void sectionAcknowledged(int requiredInsertCount) {
            if (this.lastCountAcknowledged < requiredInsertCount) {
               this.lastCountAcknowledged = requiredInsertCount;
            }
         }

         @Override
         public boolean entryAdded(int insertCount) {
            if (this.lastCountAcknowledged < insertCount) {
               this.lastCountAcknowledged = insertCount;
               return true;
            } else {
               return false;
            }
         }
      };
   }
}
