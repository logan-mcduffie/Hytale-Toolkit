package org.bouncycastle.jce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class IESParameterSpec implements AlgorithmParameterSpec {
   private byte[] derivation;
   private byte[] encoding;
   private int macKeySize;
   private int cipherKeySize;
   private byte[] nonce;
   private boolean usePointCompression;

   public IESParameterSpec(byte[] var1, byte[] var2, int var3) {
      this(var1, var2, var3, -1, null, false);
   }

   public IESParameterSpec(byte[] var1, byte[] var2, int var3, int var4, byte[] var5) {
      this(var1, var2, var3, var4, var5, false);
   }

   public IESParameterSpec(byte[] var1, byte[] var2, int var3, int var4, byte[] var5, boolean var6) {
      if (var1 != null) {
         this.derivation = new byte[var1.length];
         System.arraycopy(var1, 0, this.derivation, 0, var1.length);
      } else {
         this.derivation = null;
      }

      if (var2 != null) {
         this.encoding = new byte[var2.length];
         System.arraycopy(var2, 0, this.encoding, 0, var2.length);
      } else {
         this.encoding = null;
      }

      this.macKeySize = var3;
      this.cipherKeySize = var4;
      this.nonce = Arrays.clone(var5);
      this.usePointCompression = var6;
   }

   public byte[] getDerivationV() {
      return Arrays.clone(this.derivation);
   }

   public byte[] getEncodingV() {
      return Arrays.clone(this.encoding);
   }

   public int getMacKeySize() {
      return this.macKeySize;
   }

   public int getCipherKeySize() {
      return this.cipherKeySize;
   }

   public byte[] getNonce() {
      return Arrays.clone(this.nonce);
   }

   public void setPointCompression(boolean var1) {
      this.usePointCompression = var1;
   }

   public boolean getPointCompression() {
      return this.usePointCompression;
   }
}
