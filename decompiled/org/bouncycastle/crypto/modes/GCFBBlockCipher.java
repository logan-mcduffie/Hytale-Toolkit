package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSBox;

public class GCFBBlockCipher extends StreamBlockCipher {
   private static final byte[] C = new byte[]{
      105, 0, 114, 34, 100, -55, 4, 35, -115, 58, -37, -106, 70, -23, 42, -60, 24, -2, -84, -108, 0, -19, 7, 18, -64, -122, -36, -62, -17, 76, -87, 43
   };
   private final CFBBlockCipher cfbEngine;
   private ParametersWithIV initParams;
   private KeyParameter key;
   private long counter = 0L;
   private boolean forEncryption;

   public GCFBBlockCipher(BlockCipher var1) {
      super(var1);
      this.cfbEngine = new CFBBlockCipher(var1, var1.getBlockSize() * 8);
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      this.counter = 0L;
      this.cfbEngine.init(var1, var2);
      byte[] var3 = null;
      this.forEncryption = var1;
      if (var2 instanceof ParametersWithIV) {
         ParametersWithIV var4 = (ParametersWithIV)var2;
         var2 = var4.getParameters();
         var3 = var4.getIV();
      }

      if (var2 instanceof ParametersWithRandom) {
         var2 = ((ParametersWithRandom)var2).getParameters();
      }

      if (var2 instanceof ParametersWithSBox) {
         var2 = ((ParametersWithSBox)var2).getParameters();
      }

      this.key = (KeyParameter)var2;
      if (this.key == null && this.initParams != null) {
         this.key = (KeyParameter)this.initParams.getParameters();
      }

      if (var3 == null && this.initParams != null) {
         var3 = this.initParams.getIV();
      } else {
         var3 = this.cfbEngine.getCurrentIV();
      }

      this.initParams = new ParametersWithIV(this.key, var3);
   }

   @Override
   public String getAlgorithmName() {
      String var1 = this.cfbEngine.getAlgorithmName();
      return var1.substring(0, var1.indexOf(47)) + "/G" + var1.substring(var1.indexOf(47) + 1);
   }

   @Override
   public int getBlockSize() {
      return this.cfbEngine.getBlockSize();
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      this.processBytes(var1, var2, this.cfbEngine.getBlockSize(), var3, var4);
      return this.cfbEngine.getBlockSize();
   }

   @Override
   protected byte calculateByte(byte var1) {
      if (this.counter > 0L && (this.counter & 1023L) == 0L) {
         BlockCipher var2 = this.cfbEngine.getUnderlyingCipher();
         var2.init(false, this.key);
         byte[] var3 = new byte[32];
         int var4 = var2.getBlockSize();

         for (int var5 = 0; var5 < var3.length; var5 += var4) {
            var2.processBlock(C, var5, var3, var5);
         }

         this.key = new KeyParameter(var3);
         var2.init(true, this.key);
         byte[] var6 = this.cfbEngine.getCurrentIV();
         var2.processBlock(var6, 0, var6, 0);
         this.cfbEngine.init(this.forEncryption, new ParametersWithIV(this.key, var6));
      }

      this.counter++;
      return this.cfbEngine.calculateByte(var1);
   }

   @Override
   public void reset() {
      this.counter = 0L;
      if (this.initParams != null) {
         this.key = (KeyParameter)this.initParams.getParameters();
         this.cfbEngine.init(this.forEncryption, this.initParams);
      } else {
         this.cfbEngine.reset();
      }
   }
}
