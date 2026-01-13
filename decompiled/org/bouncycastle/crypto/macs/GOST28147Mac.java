package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.util.Pack;

public class GOST28147Mac implements Mac {
   private final CryptoServicePurpose purpose;
   private static final int BLOCK_SIZE = 8;
   private static final int MAC_SIZE = 4;
   private int bufOff;
   private byte[] buf;
   private byte[] mac;
   private boolean firstStep = true;
   private int[] workingKey = null;
   private byte[] macIV = null;
   private byte[] S = new byte[]{
      9,
      6,
      3,
      2,
      8,
      11,
      1,
      7,
      10,
      4,
      14,
      15,
      12,
      0,
      13,
      5,
      3,
      7,
      14,
      9,
      8,
      10,
      15,
      0,
      5,
      2,
      6,
      12,
      11,
      4,
      13,
      1,
      14,
      4,
      6,
      2,
      11,
      3,
      13,
      8,
      12,
      15,
      5,
      10,
      0,
      7,
      1,
      9,
      14,
      7,
      10,
      12,
      13,
      1,
      3,
      9,
      0,
      2,
      11,
      4,
      15,
      8,
      5,
      6,
      11,
      5,
      1,
      9,
      8,
      13,
      15,
      0,
      14,
      4,
      2,
      3,
      12,
      7,
      10,
      6,
      3,
      10,
      13,
      12,
      1,
      2,
      0,
      11,
      7,
      5,
      9,
      4,
      8,
      15,
      14,
      6,
      1,
      13,
      2,
      9,
      7,
      10,
      6,
      0,
      8,
      12,
      4,
      5,
      15,
      3,
      11,
      14,
      11,
      10,
      15,
      5,
      0,
      12,
      14,
      8,
      6,
      2,
      3,
      9,
      1,
      7,
      13,
      4
   };

   public GOST28147Mac() {
      this(CryptoServicePurpose.AUTHENTICATION);
   }

   public GOST28147Mac(CryptoServicePurpose var1) {
      this.purpose = var1;
      this.mac = new byte[8];
      this.buf = new byte[8];
      this.bufOff = 0;
   }

   private int[] generateWorkingKey(byte[] var1) {
      if (var1.length != 32) {
         throw new IllegalArgumentException("Key length invalid. Key needs to be 32 byte - 256 bit!!!");
      } else {
         int[] var2 = new int[8];

         for (int var3 = 0; var3 != 8; var3++) {
            var2[var3] = Pack.littleEndianToInt(var1, var3 * 4);
         }

         return var2;
      }
   }

   @Override
   public void init(CipherParameters var1) throws IllegalArgumentException {
      this.reset();
      this.buf = new byte[8];
      this.macIV = null;
      this.recursiveInit(var1);
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 178, var1, this.purpose));
   }

   private void recursiveInit(CipherParameters var1) throws IllegalArgumentException {
      if (var1 != null) {
         CipherParameters var2 = null;
         if (var1 instanceof ParametersWithSBox) {
            ParametersWithSBox var3 = (ParametersWithSBox)var1;
            System.arraycopy(var3.getSBox(), 0, this.S, 0, var3.getSBox().length);
            var2 = var3.getParameters();
         } else if (var1 instanceof KeyParameter) {
            this.workingKey = this.generateWorkingKey(((KeyParameter)var1).getKey());
         } else {
            if (!(var1 instanceof ParametersWithIV)) {
               throw new IllegalArgumentException("invalid parameter passed to GOST28147 init - " + var1.getClass().getName());
            }

            ParametersWithIV var4 = (ParametersWithIV)var1;
            System.arraycopy(var4.getIV(), 0, this.mac, 0, this.mac.length);
            this.macIV = var4.getIV();
            var2 = var4.getParameters();
         }

         this.recursiveInit(var2);
      }
   }

   @Override
   public String getAlgorithmName() {
      return "GOST28147Mac";
   }

   @Override
   public int getMacSize() {
      return 4;
   }

   private int gost28147_mainStep(int var1, int var2) {
      int var3 = var2 + var1;
      int var4 = this.S[0 + (var3 >> 0 & 15)] << 0;
      var4 += this.S[16 + (var3 >> 4 & 15)] << 4;
      var4 += this.S[32 + (var3 >> 8 & 15)] << 8;
      var4 += this.S[48 + (var3 >> 12 & 15)] << 12;
      var4 += this.S[64 + (var3 >> 16 & 15)] << 16;
      var4 += this.S[80 + (var3 >> 20 & 15)] << 20;
      var4 += this.S[96 + (var3 >> 24 & 15)] << 24;
      var4 += this.S[112 + (var3 >> 28 & 15)] << 28;
      return var4 << 11 | var4 >>> 21;
   }

   private void gost28147MacFunc(int[] var1, byte[] var2, int var3, byte[] var4, int var5) {
      int var6 = Pack.littleEndianToInt(var2, var3);
      int var7 = Pack.littleEndianToInt(var2, var3 + 4);

      for (int var9 = 0; var9 < 2; var9++) {
         for (int var10 = 0; var10 < 8; var10++) {
            int var8 = var6;
            var6 = var7 ^ this.gost28147_mainStep(var6, var1[var10]);
            var7 = var8;
         }
      }

      Pack.intToLittleEndian(var6, var4, var5);
      Pack.intToLittleEndian(var7, var4, var5 + 4);
   }

   @Override
   public void update(byte var1) throws IllegalStateException {
      if (this.bufOff == this.buf.length) {
         byte[] var2 = new byte[this.buf.length];
         if (this.firstStep) {
            this.firstStep = false;
            if (this.macIV != null) {
               CM5func(this.buf, 0, this.macIV, var2);
            } else {
               System.arraycopy(this.buf, 0, var2, 0, this.mac.length);
            }
         } else {
            CM5func(this.buf, 0, this.mac, var2);
         }

         this.gost28147MacFunc(this.workingKey, var2, 0, this.mac, 0);
         this.bufOff = 0;
      }

      this.buf[this.bufOff++] = var1;
   }

   @Override
   public void update(byte[] var1, int var2, int var3) throws DataLengthException, IllegalStateException {
      if (var3 < 0) {
         throw new IllegalArgumentException("Can't have a negative input length!");
      } else {
         int var4 = 8 - this.bufOff;
         if (var3 > var4) {
            System.arraycopy(var1, var2, this.buf, this.bufOff, var4);
            byte[] var5 = new byte[this.buf.length];
            if (this.firstStep) {
               this.firstStep = false;
               if (this.macIV != null) {
                  CM5func(this.buf, 0, this.macIV, var5);
               } else {
                  System.arraycopy(this.buf, 0, var5, 0, this.mac.length);
               }
            } else {
               CM5func(this.buf, 0, this.mac, var5);
            }

            this.gost28147MacFunc(this.workingKey, var5, 0, this.mac, 0);
            this.bufOff = 0;
            var3 -= var4;

            for (var2 += var4; var3 > 8; var2 += 8) {
               CM5func(var1, var2, this.mac, var5);
               this.gost28147MacFunc(this.workingKey, var5, 0, this.mac, 0);
               var3 -= 8;
            }
         }

         System.arraycopy(var1, var2, this.buf, this.bufOff, var3);
         this.bufOff += var3;
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException {
      while (this.bufOff < 8) {
         this.buf[this.bufOff] = 0;
         this.bufOff++;
      }

      byte[] var3 = new byte[this.buf.length];
      if (this.firstStep) {
         this.firstStep = false;
         System.arraycopy(this.buf, 0, var3, 0, this.mac.length);
      } else {
         CM5func(this.buf, 0, this.mac, var3);
      }

      this.gost28147MacFunc(this.workingKey, var3, 0, this.mac, 0);
      System.arraycopy(this.mac, this.mac.length / 2 - 4, var1, var2, 4);
      this.reset();
      return 4;
   }

   @Override
   public void reset() {
      for (int var1 = 0; var1 < this.buf.length; var1++) {
         this.buf[var1] = 0;
      }

      this.bufOff = 0;
      this.firstStep = true;
   }

   private static void CM5func(byte[] var0, int var1, byte[] var2, byte[] var3) {
      for (int var4 = 0; var4 < 8; var4++) {
         var3[var4] = (byte)(var0[var1 + var4] ^ var2[var4]);
      }
   }
}
