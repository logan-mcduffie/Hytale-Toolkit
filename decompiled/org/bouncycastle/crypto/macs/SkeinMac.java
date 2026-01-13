package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SkeinEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.SkeinParameters;

public class SkeinMac implements Mac {
   public static final int SKEIN_256 = 256;
   public static final int SKEIN_512 = 512;
   public static final int SKEIN_1024 = 1024;
   private SkeinEngine engine;

   public SkeinMac(int var1, int var2) {
      this.engine = new SkeinEngine(var1, var2);
   }

   public SkeinMac(SkeinMac var1) {
      this.engine = new SkeinEngine(var1.engine);
   }

   @Override
   public String getAlgorithmName() {
      return "Skein-MAC-" + this.engine.getBlockSize() * 8 + "-" + this.engine.getOutputSize() * 8;
   }

   @Override
   public void init(CipherParameters var1) throws IllegalArgumentException {
      SkeinParameters var2;
      if (var1 instanceof SkeinParameters) {
         var2 = (SkeinParameters)var1;
      } else {
         if (!(var1 instanceof KeyParameter)) {
            throw new IllegalArgumentException("Invalid parameter passed to Skein MAC init - " + var1.getClass().getName());
         }

         var2 = new SkeinParameters.Builder().setKey(((KeyParameter)var1).getKey()).build();
      }

      if (var2.getKey() == null) {
         throw new IllegalArgumentException("Skein MAC requires a key parameter.");
      } else {
         this.engine.init(var2);
      }
   }

   @Override
   public int getMacSize() {
      return this.engine.getOutputSize();
   }

   @Override
   public void reset() {
      this.engine.reset();
   }

   @Override
   public void update(byte var1) {
      this.engine.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.engine.update(var1, var2, var3);
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      return this.engine.doFinal(var1, var2);
   }
}
