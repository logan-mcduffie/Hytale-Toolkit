package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.util.RadixConverter;
import org.bouncycastle.util.Arrays;

public final class FPEParameters implements CipherParameters {
   private final KeyParameter key;
   private final RadixConverter radixConverter;
   private final byte[] tweak;
   private final boolean useInverse;

   public FPEParameters(KeyParameter var1, int var2, byte[] var3) {
      this(var1, var2, var3, false);
   }

   public FPEParameters(KeyParameter var1, int var2, byte[] var3, boolean var4) {
      this(var1, new RadixConverter(var2), var3, var4);
   }

   public FPEParameters(KeyParameter var1, RadixConverter var2, byte[] var3, boolean var4) {
      this.key = var1;
      this.radixConverter = var2;
      this.tweak = Arrays.clone(var3);
      this.useInverse = var4;
   }

   public KeyParameter getKey() {
      return this.key;
   }

   public int getRadix() {
      return this.radixConverter.getRadix();
   }

   public RadixConverter getRadixConverter() {
      return this.radixConverter;
   }

   public byte[] getTweak() {
      return Arrays.clone(this.tweak);
   }

   public boolean isUsingInverseFunction() {
      return this.useInverse;
   }
}
