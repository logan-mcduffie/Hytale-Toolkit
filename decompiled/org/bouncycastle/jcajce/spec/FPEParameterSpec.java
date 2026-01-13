package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.util.RadixConverter;
import org.bouncycastle.util.Arrays;

public class FPEParameterSpec implements AlgorithmParameterSpec {
   private final RadixConverter radixConverter;
   private final byte[] tweak;
   private final boolean useInverse;

   public FPEParameterSpec(int var1, byte[] var2) {
      this(var1, var2, false);
   }

   public FPEParameterSpec(int var1, byte[] var2, boolean var3) {
      this(new RadixConverter(var1), var2, var3);
   }

   public FPEParameterSpec(RadixConverter var1, byte[] var2, boolean var3) {
      this.radixConverter = var1;
      this.tweak = Arrays.clone(var2);
      this.useInverse = var3;
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
