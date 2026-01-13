package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

public class DESedeEngine extends DESBase implements BlockCipher {
   protected static final int BLOCK_SIZE = 8;
   private int[] workingKey1 = null;
   private int[] workingKey2 = null;
   private int[] workingKey3 = null;
   private boolean forEncryption;

   public DESedeEngine() {
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.bitsOfSecurity()));
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (!(var2 instanceof KeyParameter)) {
         throw new IllegalArgumentException("invalid parameter passed to DESede init - " + var2.getClass().getName());
      } else {
         byte[] var3 = ((KeyParameter)var2).getKey();
         if (var3.length != 24 && var3.length != 16) {
            throw new IllegalArgumentException("key size must be 16 or 24 bytes.");
         } else {
            this.forEncryption = var1;
            byte[] var4 = new byte[8];
            System.arraycopy(var3, 0, var4, 0, var4.length);
            this.workingKey1 = this.generateWorkingKey(var1, var4);
            byte[] var5 = new byte[8];
            System.arraycopy(var3, 8, var5, 0, var5.length);
            this.workingKey2 = this.generateWorkingKey(!var1, var5);
            if (var3.length == 24) {
               byte[] var6 = new byte[8];
               System.arraycopy(var3, 16, var6, 0, var6.length);
               this.workingKey3 = this.generateWorkingKey(var1, var6);
            } else {
               this.workingKey3 = this.workingKey1;
            }

            CryptoServicesRegistrar.checkConstraints(
               new DefaultServiceProperties(this.getAlgorithmName(), this.bitsOfSecurity(), var2, Utils.getPurpose(this.forEncryption))
            );
         }
      }
   }

   @Override
   public String getAlgorithmName() {
      return "DESede";
   }

   @Override
   public int getBlockSize() {
      return 8;
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) {
      if (this.workingKey1 == null) {
         throw new IllegalStateException("DESede engine not initialised");
      } else if (var2 + 8 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + 8 > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         byte[] var5 = new byte[8];
         if (this.forEncryption) {
            this.desFunc(this.workingKey1, var1, var2, var5, 0);
            this.desFunc(this.workingKey2, var5, 0, var5, 0);
            this.desFunc(this.workingKey3, var5, 0, var3, var4);
         } else {
            this.desFunc(this.workingKey3, var1, var2, var5, 0);
            this.desFunc(this.workingKey2, var5, 0, var5, 0);
            this.desFunc(this.workingKey1, var5, 0, var3, var4);
         }

         return 8;
      }
   }

   @Override
   public void reset() {
   }

   private int bitsOfSecurity() {
      return this.workingKey1 != null && this.workingKey1 == this.workingKey3 ? 80 : 112;
   }
}
