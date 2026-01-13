package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.params.SkeinParameters;
import org.bouncycastle.util.Memoable;

public class SkeinDigest implements ExtendedDigest, Memoable {
   public static final int SKEIN_256 = 256;
   public static final int SKEIN_512 = 512;
   public static final int SKEIN_1024 = 1024;
   private final CryptoServicePurpose purpose;
   private SkeinEngine engine;

   public SkeinDigest(int var1, int var2) {
      this(var1, var2, CryptoServicePurpose.ANY);
   }

   public SkeinDigest(int var1, int var2, CryptoServicePurpose var3) {
      this.engine = new SkeinEngine(var1, var2);
      this.purpose = var3;
      this.init(null);
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.getDigestSize() * 4, var3));
   }

   public SkeinDigest(SkeinDigest var1) {
      this.engine = new SkeinEngine(var1.engine);
      this.purpose = var1.purpose;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, var1.getDigestSize() * 4, this.purpose));
   }

   @Override
   public void reset(Memoable var1) {
      SkeinDigest var2 = (SkeinDigest)var1;
      this.engine.reset(var2.engine);
   }

   @Override
   public Memoable copy() {
      return new SkeinDigest(this);
   }

   @Override
   public String getAlgorithmName() {
      return "Skein-" + this.engine.getBlockSize() * 8 + "-" + this.engine.getOutputSize() * 8;
   }

   @Override
   public int getDigestSize() {
      return this.engine.getOutputSize();
   }

   @Override
   public int getByteLength() {
      return this.engine.getBlockSize();
   }

   public void init(SkeinParameters var1) {
      this.engine.init(var1);
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
