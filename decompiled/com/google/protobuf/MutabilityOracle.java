package com.google.protobuf;

interface MutabilityOracle {
   MutabilityOracle IMMUTABLE = new MutabilityOracle() {
      @Override
      public void ensureMutable() {
         throw new UnsupportedOperationException();
      }
   };

   void ensureMutable();
}
