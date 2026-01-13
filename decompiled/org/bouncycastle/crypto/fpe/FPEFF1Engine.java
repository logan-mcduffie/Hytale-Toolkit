package org.bouncycastle.crypto.fpe;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.FPEParameters;
import org.bouncycastle.util.Properties;

public class FPEFF1Engine extends FPEEngine {
   public FPEFF1Engine() {
      this(AESEngine.newInstance());
   }

   public FPEFF1Engine(BlockCipher var1) {
      super(var1);
      if (var1.getBlockSize() != 16) {
         throw new IllegalArgumentException("base cipher needs to be 128 bits");
      } else if (Properties.isOverrideSet("org.bouncycastle.fpe.disable") || Properties.isOverrideSet("org.bouncycastle.fpe.disable_ff1")) {
         throw new UnsupportedOperationException("FF1 encryption disabled");
      }
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.forEncryption = var1;
      this.fpeParameters = (FPEParameters)var2;
      this.baseCipher.init(!this.fpeParameters.isUsingInverseFunction(), this.fpeParameters.getKey());
   }

   @Override
   public String getAlgorithmName() {
      return "FF1";
   }

   @Override
   protected int encryptBlock(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      byte[] var6;
      if (this.fpeParameters.getRadix() > 256) {
         var6 = toByteArray(
            SP80038G.encryptFF1w(this.baseCipher, this.fpeParameters.getRadixConverter(), this.fpeParameters.getTweak(), toShortArray(var1), var2, var3 / 2)
         );
      } else {
         var6 = SP80038G.encryptFF1(this.baseCipher, this.fpeParameters.getRadixConverter(), this.fpeParameters.getTweak(), var1, var2, var3);
      }

      System.arraycopy(var6, 0, var4, var5, var3);
      return var3;
   }

   @Override
   protected int decryptBlock(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      byte[] var6;
      if (this.fpeParameters.getRadix() > 256) {
         var6 = toByteArray(
            SP80038G.decryptFF1w(this.baseCipher, this.fpeParameters.getRadixConverter(), this.fpeParameters.getTweak(), toShortArray(var1), var2, var3 / 2)
         );
      } else {
         var6 = SP80038G.decryptFF1(this.baseCipher, this.fpeParameters.getRadixConverter(), this.fpeParameters.getTweak(), var1, var2, var3);
      }

      System.arraycopy(var6, 0, var4, var5, var3);
      return var3;
   }
}
