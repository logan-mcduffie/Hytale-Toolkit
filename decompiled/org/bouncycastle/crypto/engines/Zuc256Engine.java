package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Memoable;

public final class Zuc256Engine extends Zuc256CoreEngine {
   public Zuc256Engine() {
   }

   public Zuc256Engine(int var1) {
      super(var1);
   }

   private Zuc256Engine(Zuc256Engine var1) {
      super(var1);
   }

   @Override
   public Memoable copy() {
      return new Zuc256Engine(this);
   }
}
