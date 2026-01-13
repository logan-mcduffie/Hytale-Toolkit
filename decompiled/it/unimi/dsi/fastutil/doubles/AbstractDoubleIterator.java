package it.unimi.dsi.fastutil.doubles;

public abstract class AbstractDoubleIterator implements DoubleIterator {
   protected AbstractDoubleIterator() {
   }

   @Override
   public final void forEachRemaining(DoubleConsumer action) {
      this.forEachRemaining(action);
   }
}
