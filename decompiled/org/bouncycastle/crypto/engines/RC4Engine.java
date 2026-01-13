package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

public class RC4Engine implements StreamCipher {
   private static final int STATE_LENGTH = 256;
   private byte[] engineState = null;
   private int x = 0;
   private int y = 0;
   private byte[] workingKey = null;
   private boolean forEncryption;

   public RC4Engine() {
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 20));
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var2 instanceof KeyParameter) {
         this.workingKey = ((KeyParameter)var2).getKey();
         this.forEncryption = var1;
         this.setKey(this.workingKey);
         CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 20, var2, Utils.getPurpose(var1)));
      } else {
         throw new IllegalArgumentException("invalid parameter passed to RC4 init - " + var2.getClass().getName());
      }
   }

   @Override
   public String getAlgorithmName() {
      return "RC4";
   }

   @Override
   public byte returnByte(byte var1) {
      this.x = this.x + 1 & 0xFF;
      this.y = this.engineState[this.x] + this.y & 0xFF;
      byte var2 = this.engineState[this.x];
      this.engineState[this.x] = this.engineState[this.y];
      this.engineState[this.y] = var2;
      return (byte)(var1 ^ this.engineState[this.engineState[this.x] + this.engineState[this.y] & 0xFF]);
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      if (var2 + var3 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var5 + var3 > var4.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         for (int var6 = 0; var6 < var3; var6++) {
            this.x = this.x + 1 & 0xFF;
            this.y = this.engineState[this.x] + this.y & 0xFF;
            byte var7 = this.engineState[this.x];
            this.engineState[this.x] = this.engineState[this.y];
            this.engineState[this.y] = var7;
            var4[var6 + var5] = (byte)(var1[var6 + var2] ^ this.engineState[this.engineState[this.x] + this.engineState[this.y] & 0xFF]);
         }

         return var3;
      }
   }

   @Override
   public void reset() {
      this.setKey(this.workingKey);
   }

   private void setKey(byte[] var1) {
      this.workingKey = var1;
      this.x = 0;
      this.y = 0;
      if (this.engineState == null) {
         this.engineState = new byte[256];
      }

      for (int var2 = 0; var2 < 256; var2++) {
         this.engineState[var2] = (byte)var2;
      }

      int var6 = 0;
      int var3 = 0;

      for (int var4 = 0; var4 < 256; var4++) {
         var3 = (var1[var6] & 255) + this.engineState[var4] + var3 & 0xFF;
         byte var5 = this.engineState[var4];
         this.engineState[var4] = this.engineState[var3];
         this.engineState[var3] = var5;
         var6 = (var6 + 1) % var1.length;
      }
   }
}
