package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.modes.KGCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class KGMac implements Mac {
   private final KGCMBlockCipher cipher;
   private final int macSizeBits;

   public KGMac(KGCMBlockCipher var1) {
      this.cipher = var1;
      this.macSizeBits = var1.getUnderlyingCipher().getBlockSize() * 8;
   }

   public KGMac(KGCMBlockCipher var1, int var2) {
      this.cipher = var1;
      this.macSizeBits = var2;
   }

   @Override
   public void init(CipherParameters var1) throws IllegalArgumentException {
      if (var1 instanceof ParametersWithIV) {
         ParametersWithIV var2 = (ParametersWithIV)var1;
         byte[] var3 = var2.getIV();
         KeyParameter var4 = (KeyParameter)var2.getParameters();
         this.cipher.init(true, new AEADParameters(var4, this.macSizeBits, var3));
      } else {
         throw new IllegalArgumentException("KGMAC requires ParametersWithIV");
      }
   }

   @Override
   public String getAlgorithmName() {
      return this.cipher.getUnderlyingCipher().getAlgorithmName() + "-KGMAC";
   }

   @Override
   public int getMacSize() {
      return this.macSizeBits / 8;
   }

   @Override
   public void update(byte var1) throws IllegalStateException {
      this.cipher.processAADByte(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) throws DataLengthException, IllegalStateException {
      this.cipher.processAADBytes(var1, var2, var3);
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException {
      try {
         return this.cipher.doFinal(var1, var2);
      } catch (InvalidCipherTextException var4) {
         throw new IllegalStateException(var4.toString());
      }
   }

   @Override
   public void reset() {
      this.cipher.reset();
   }
}
