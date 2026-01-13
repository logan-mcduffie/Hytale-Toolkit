package com.google.common.flogger;

public enum StackSize {
   SMALL(10),
   MEDIUM(20),
   LARGE(50),
   FULL(-1),
   NONE(0);

   private final int maxDepth;

   private StackSize(int value) {
      this.maxDepth = value;
   }

   int getMaxDepth() {
      return this.maxDepth;
   }
}
