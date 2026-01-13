package io.netty.util;

public interface BooleanSupplier {
   BooleanSupplier FALSE_SUPPLIER = new BooleanSupplier() {
      @Override
      public boolean get() {
         return false;
      }
   };
   BooleanSupplier TRUE_SUPPLIER = new BooleanSupplier() {
      @Override
      public boolean get() {
         return true;
      }
   };

   boolean get() throws Exception;
}
