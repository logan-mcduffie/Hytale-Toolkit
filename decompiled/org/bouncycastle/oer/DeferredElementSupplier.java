package org.bouncycastle.oer;

public class DeferredElementSupplier implements ElementSupplier {
   private final OERDefinition.Builder src;
   private Element buildProduct;

   public DeferredElementSupplier(OERDefinition.Builder var1) {
      this.src = var1;
   }

   @Override
   public Element build() {
      synchronized (this) {
         if (this.buildProduct == null) {
            this.buildProduct = this.src.build();
         }

         return this.buildProduct;
      }
   }
}
