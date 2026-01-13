package org.bouncycastle.crypto.engines;

public class ARIAWrapEngine extends RFC3394WrapEngine {
   public ARIAWrapEngine() {
      super(new ARIAEngine());
   }

   public ARIAWrapEngine(boolean var1) {
      super(new ARIAEngine(), var1);
   }
}
