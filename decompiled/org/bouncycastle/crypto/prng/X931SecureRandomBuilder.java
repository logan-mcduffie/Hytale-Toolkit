package org.bouncycastle.crypto.prng;

import java.security.SecureRandom;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class X931SecureRandomBuilder {
   private SecureRandom random;
   private EntropySourceProvider entropySourceProvider;
   private byte[] dateTimeVector;

   public X931SecureRandomBuilder() {
      this(CryptoServicesRegistrar.getSecureRandom(), false);
   }

   public X931SecureRandomBuilder(SecureRandom var1, boolean var2) {
      this.random = var1;
      this.entropySourceProvider = new BasicEntropySourceProvider(this.random, var2);
   }

   public X931SecureRandomBuilder(EntropySourceProvider var1) {
      this.random = null;
      this.entropySourceProvider = var1;
   }

   public X931SecureRandomBuilder setDateTimeVector(byte[] var1) {
      this.dateTimeVector = Arrays.clone(var1);
      return this;
   }

   public X931SecureRandom build(BlockCipher var1, KeyParameter var2, boolean var3) {
      if (this.dateTimeVector == null) {
         this.dateTimeVector = new byte[var1.getBlockSize()];
         Pack.longToBigEndian(System.currentTimeMillis(), this.dateTimeVector, 0);
      }

      var1.init(true, var2);
      return new X931SecureRandom(this.random, new X931RNG(var1, this.dateTimeVector, this.entropySourceProvider.get(var1.getBlockSize() * 8)), var3);
   }
}
