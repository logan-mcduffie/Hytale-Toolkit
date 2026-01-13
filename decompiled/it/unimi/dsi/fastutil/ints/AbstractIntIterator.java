package it.unimi.dsi.fastutil.ints;

public abstract class AbstractIntIterator implements IntIterator {
   protected AbstractIntIterator() {
   }

   @Override
   public final void forEachRemaining(IntConsumer action) {
      this.forEachRemaining(action);
   }
}
