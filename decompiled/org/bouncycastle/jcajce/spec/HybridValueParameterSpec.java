package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.security.auth.Destroyable;
import org.bouncycastle.util.Arrays;

public class HybridValueParameterSpec implements AlgorithmParameterSpec, Destroyable {
   private final AtomicBoolean hasBeenDestroyed = new AtomicBoolean(false);
   private final boolean doPrepend;
   private volatile byte[] t;
   private volatile AlgorithmParameterSpec baseSpec;

   public HybridValueParameterSpec(byte[] var1, AlgorithmParameterSpec var2) {
      this(var1, false, var2);
   }

   public HybridValueParameterSpec(byte[] var1, boolean var2, AlgorithmParameterSpec var3) {
      this.t = var1;
      this.baseSpec = var3;
      this.doPrepend = var2;
   }

   public boolean isPrependedT() {
      return this.doPrepend;
   }

   public byte[] getT() {
      byte[] var1 = this.t;
      this.checkDestroyed();
      return var1;
   }

   public AlgorithmParameterSpec getBaseParameterSpec() {
      AlgorithmParameterSpec var1 = this.baseSpec;
      this.checkDestroyed();
      return var1;
   }

   @Override
   public boolean isDestroyed() {
      return this.hasBeenDestroyed.get();
   }

   @Override
   public void destroy() {
      if (!this.hasBeenDestroyed.getAndSet(true)) {
         Arrays.clear(this.t);
         this.t = null;
         this.baseSpec = null;
      }
   }

   private void checkDestroyed() {
      if (this.isDestroyed()) {
         throw new IllegalStateException("spec has been destroyed");
      }
   }
}
