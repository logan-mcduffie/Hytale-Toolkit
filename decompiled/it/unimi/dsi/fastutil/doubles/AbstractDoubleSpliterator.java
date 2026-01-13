package it.unimi.dsi.fastutil.doubles;

public abstract class AbstractDoubleSpliterator implements DoubleSpliterator {
   protected AbstractDoubleSpliterator() {
   }

   @Override
   public final boolean tryAdvance(DoubleConsumer action) {
      return this.tryAdvance(action);
   }

   @Override
   public final void forEachRemaining(DoubleConsumer action) {
      this.forEachRemaining(action);
   }
}
