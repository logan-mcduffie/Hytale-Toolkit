package com.github.luben.zstd;

public enum EndDirective {
   CONTINUE(0),
   FLUSH(1),
   END(2);

   private final int value;

   private EndDirective(int nullxx) {
      this.value = nullxx;
   }

   int value() {
      return this.value;
   }
}
