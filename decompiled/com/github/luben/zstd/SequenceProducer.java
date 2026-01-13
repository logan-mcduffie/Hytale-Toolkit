package com.github.luben.zstd;

public interface SequenceProducer {
   long getFunctionPointer();

   long createState();

   void freeState(long var1);
}
