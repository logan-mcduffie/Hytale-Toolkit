package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.MaxBytesExceededException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.SkippingStreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

public class Salsa20Engine implements SkippingStreamCipher {
   public static final int DEFAULT_ROUNDS = 20;
   private static final int STATE_SIZE = 16;
   private static final int[] TAU_SIGMA = Pack.littleEndianToInt(Strings.toByteArray("expand 16-byte kexpand 32-byte k"), 0, 8);
   /** @deprecated */
   protected static final byte[] sigma = Strings.toByteArray("expand 32-byte k");
   /** @deprecated */
   protected static final byte[] tau = Strings.toByteArray("expand 16-byte k");
   protected int rounds;
   private int index = 0;
   protected int[] engineState = new int[16];
   protected int[] x = new int[16];
   private byte[] keyStream = new byte[64];
   private boolean initialised = false;
   private int cW0;
   private int cW1;
   private int cW2;

   protected void packTauOrSigma(int var1, int[] var2, int var3) {
      int var4 = (var1 - 16) / 4;
      var2[var3] = TAU_SIGMA[var4];
      var2[var3 + 1] = TAU_SIGMA[var4 + 1];
      var2[var3 + 2] = TAU_SIGMA[var4 + 2];
      var2[var3 + 3] = TAU_SIGMA[var4 + 3];
   }

   public Salsa20Engine() {
      this(20);
   }

   public Salsa20Engine(int var1) {
      if (var1 > 0 && (var1 & 1) == 0) {
         this.rounds = var1;
      } else {
         throw new IllegalArgumentException("'rounds' must be a positive, even number");
      }
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (!(var2 instanceof ParametersWithIV)) {
         throw new IllegalArgumentException(this.getAlgorithmName() + " Init parameters must include an IV");
      } else {
         ParametersWithIV var3 = (ParametersWithIV)var2;
         byte[] var4 = var3.getIV();
         if (var4 != null && var4.length == this.getNonceSize()) {
            CipherParameters var5 = var3.getParameters();
            if (var5 == null) {
               if (!this.initialised) {
                  throw new IllegalStateException(this.getAlgorithmName() + " KeyParameter can not be null for first initialisation");
               }

               this.setKey(null, var4);
            } else {
               if (!(var5 instanceof KeyParameter)) {
                  throw new IllegalArgumentException(this.getAlgorithmName() + " Init parameters must contain a KeyParameter (or null for re-init)");
               }

               byte[] var6 = ((KeyParameter)var5).getKey();
               this.setKey(var6, var4);
               CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), var6.length * 8, var2, Utils.getPurpose(var1)));
            }

            this.reset();
            this.initialised = true;
         } else {
            throw new IllegalArgumentException(this.getAlgorithmName() + " requires exactly " + this.getNonceSize() + " bytes of IV");
         }
      }
   }

   protected int getNonceSize() {
      return 8;
   }

   @Override
   public String getAlgorithmName() {
      String var1 = "Salsa20";
      if (this.rounds != 20) {
         var1 = var1 + "/" + this.rounds;
      }

      return var1;
   }

   @Override
   public byte returnByte(byte var1) {
      if (this.limitExceeded()) {
         throw new MaxBytesExceededException("2^70 byte limit per IV; Change IV");
      } else {
         byte var2 = (byte)(this.keyStream[this.index] ^ var1);
         this.index = this.index + 1 & 63;
         if (this.index == 0) {
            this.advanceCounter();
            this.generateKeyStream(this.keyStream);
         }

         return var2;
      }
   }

   protected void advanceCounter(long var1) {
      int var3 = (int)(var1 >>> 32);
      int var4 = (int)var1;
      if (var3 > 0) {
         this.engineState[9] = this.engineState[9] + var3;
      }

      int var5 = this.engineState[8];
      this.engineState[8] = this.engineState[8] + var4;
      if (var5 != 0 && this.engineState[8] < var5) {
         this.engineState[9]++;
      }
   }

   protected void advanceCounter() {
      if (++this.engineState[8] == 0) {
         this.engineState[9]++;
      }
   }

   protected void retreatCounter(long var1) {
      int var3 = (int)(var1 >>> 32);
      int var4 = (int)var1;
      if (var3 != 0) {
         if ((this.engineState[9] & 4294967295L) < (var3 & 4294967295L)) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
         }

         this.engineState[9] = this.engineState[9] - var3;
      }

      if ((this.engineState[8] & 4294967295L) >= (var4 & 4294967295L)) {
         this.engineState[8] = this.engineState[8] - var4;
      } else {
         if (this.engineState[9] == 0) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
         }

         this.engineState[9]--;
         this.engineState[8] = this.engineState[8] - var4;
      }
   }

   protected void retreatCounter() {
      if (this.engineState[8] == 0 && this.engineState[9] == 0) {
         throw new IllegalStateException("attempt to reduce counter past zero.");
      } else {
         if (--this.engineState[8] == -1) {
            this.engineState[9]--;
         }
      }
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      if (!this.initialised) {
         throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
      } else if (var2 + var3 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var5 + var3 > var4.length) {
         throw new OutputLengthException("output buffer too short");
      } else if (this.limitExceeded(var3)) {
         throw new MaxBytesExceededException("2^70 byte limit per IV would be exceeded; Change IV");
      } else {
         for (int var6 = 0; var6 < var3; var6++) {
            var4[var6 + var5] = (byte)(this.keyStream[this.index] ^ var1[var6 + var2]);
            this.index = this.index + 1 & 63;
            if (this.index == 0) {
               this.advanceCounter();
               this.generateKeyStream(this.keyStream);
            }
         }

         return var3;
      }
   }

   @Override
   public long skip(long var1) {
      if (var1 >= 0L) {
         long var3 = var1;
         if (var1 >= 64L) {
            long var5 = var1 / 64L;
            this.advanceCounter(var5);
            var3 = var1 - var5 * 64L;
         }

         int var8 = this.index;
         this.index = this.index + (int)var3 & 63;
         if (this.index < var8) {
            this.advanceCounter();
         }
      } else {
         long var7 = -var1;
         if (var7 >= 64L) {
            long var9 = var7 / 64L;
            this.retreatCounter(var9);
            var7 -= var9 * 64L;
         }

         for (long var10 = 0L; var10 < var7; var10++) {
            if (this.index == 0) {
               this.retreatCounter();
            }

            this.index = this.index - 1 & 63;
         }
      }

      this.generateKeyStream(this.keyStream);
      return var1;
   }

   @Override
   public long seekTo(long var1) {
      this.reset();
      return this.skip(var1);
   }

   @Override
   public long getPosition() {
      return this.getCounter() * 64L + this.index;
   }

   @Override
   public void reset() {
      this.index = 0;
      this.resetLimitCounter();
      this.resetCounter();
      this.generateKeyStream(this.keyStream);
   }

   protected long getCounter() {
      return (long)this.engineState[9] << 32 | this.engineState[8] & 4294967295L;
   }

   protected void resetCounter() {
      this.engineState[8] = this.engineState[9] = 0;
   }

   protected void setKey(byte[] var1, byte[] var2) {
      if (var1 != null) {
         if (var1.length != 16 && var1.length != 32) {
            throw new IllegalArgumentException(this.getAlgorithmName() + " requires 128 bit or 256 bit key");
         }

         int var3 = (var1.length - 16) / 4;
         this.engineState[0] = TAU_SIGMA[var3];
         this.engineState[5] = TAU_SIGMA[var3 + 1];
         this.engineState[10] = TAU_SIGMA[var3 + 2];
         this.engineState[15] = TAU_SIGMA[var3 + 3];
         Pack.littleEndianToInt(var1, 0, this.engineState, 1, 4);
         Pack.littleEndianToInt(var1, var1.length - 16, this.engineState, 11, 4);
      }

      Pack.littleEndianToInt(var2, 0, this.engineState, 6, 2);
   }

   protected void generateKeyStream(byte[] var1) {
      salsaCore(this.rounds, this.engineState, this.x);
      Pack.intToLittleEndian(this.x, var1, 0);
   }

   public static void salsaCore(int var0, int[] var1, int[] var2) {
      if (var1.length != 16) {
         throw new IllegalArgumentException();
      } else if (var2.length != 16) {
         throw new IllegalArgumentException();
      } else if (var0 % 2 != 0) {
         throw new IllegalArgumentException("Number of rounds must be even");
      } else {
         int var3 = var1[0];
         int var4 = var1[1];
         int var5 = var1[2];
         int var6 = var1[3];
         int var7 = var1[4];
         int var8 = var1[5];
         int var9 = var1[6];
         int var10 = var1[7];
         int var11 = var1[8];
         int var12 = var1[9];
         int var13 = var1[10];
         int var14 = var1[11];
         int var15 = var1[12];
         int var16 = var1[13];
         int var17 = var1[14];
         int var18 = var1[15];

         for (int var19 = var0; var19 > 0; var19 -= 2) {
            int var24 = var7 ^ Integers.rotateLeft(var3 + var15, 7);
            int var28 = var11 ^ Integers.rotateLeft(var24 + var3, 9);
            int var32 = var15 ^ Integers.rotateLeft(var28 + var24, 13);
            var3 ^= Integers.rotateLeft(var32 + var28, 18);
            int var29 = var12 ^ Integers.rotateLeft(var8 + var4, 7);
            int var33 = var16 ^ Integers.rotateLeft(var29 + var8, 9);
            int var21 = var4 ^ Integers.rotateLeft(var33 + var29, 13);
            var8 ^= Integers.rotateLeft(var21 + var33, 18);
            int var34 = var17 ^ Integers.rotateLeft(var13 + var9, 7);
            int var22 = var5 ^ Integers.rotateLeft(var34 + var13, 9);
            int var26 = var9 ^ Integers.rotateLeft(var22 + var34, 13);
            var13 ^= Integers.rotateLeft(var26 + var22, 18);
            int var23 = var6 ^ Integers.rotateLeft(var18 + var14, 7);
            int var27 = var10 ^ Integers.rotateLeft(var23 + var18, 9);
            int var31 = var14 ^ Integers.rotateLeft(var27 + var23, 13);
            var18 ^= Integers.rotateLeft(var31 + var27, 18);
            var4 = var21 ^ Integers.rotateLeft(var3 + var23, 7);
            var5 = var22 ^ Integers.rotateLeft(var4 + var3, 9);
            var6 = var23 ^ Integers.rotateLeft(var5 + var4, 13);
            var3 ^= Integers.rotateLeft(var6 + var5, 18);
            var9 = var26 ^ Integers.rotateLeft(var8 + var24, 7);
            var10 = var27 ^ Integers.rotateLeft(var9 + var8, 9);
            var7 = var24 ^ Integers.rotateLeft(var10 + var9, 13);
            var8 ^= Integers.rotateLeft(var7 + var10, 18);
            var14 = var31 ^ Integers.rotateLeft(var13 + var29, 7);
            var11 = var28 ^ Integers.rotateLeft(var14 + var13, 9);
            var12 = var29 ^ Integers.rotateLeft(var11 + var14, 13);
            var13 ^= Integers.rotateLeft(var12 + var11, 18);
            var15 = var32 ^ Integers.rotateLeft(var18 + var34, 7);
            var16 = var33 ^ Integers.rotateLeft(var15 + var18, 9);
            var17 = var34 ^ Integers.rotateLeft(var16 + var15, 13);
            var18 ^= Integers.rotateLeft(var17 + var16, 18);
         }

         var2[0] = var3 + var1[0];
         var2[1] = var4 + var1[1];
         var2[2] = var5 + var1[2];
         var2[3] = var6 + var1[3];
         var2[4] = var7 + var1[4];
         var2[5] = var8 + var1[5];
         var2[6] = var9 + var1[6];
         var2[7] = var10 + var1[7];
         var2[8] = var11 + var1[8];
         var2[9] = var12 + var1[9];
         var2[10] = var13 + var1[10];
         var2[11] = var14 + var1[11];
         var2[12] = var15 + var1[12];
         var2[13] = var16 + var1[13];
         var2[14] = var17 + var1[14];
         var2[15] = var18 + var1[15];
      }
   }

   private void resetLimitCounter() {
      this.cW0 = 0;
      this.cW1 = 0;
      this.cW2 = 0;
   }

   private boolean limitExceeded() {
      return ++this.cW0 == 0 && ++this.cW1 == 0 ? (++this.cW2 & 32) != 0 : false;
   }

   private boolean limitExceeded(int var1) {
      this.cW0 += var1;
      return this.cW0 < var1 && this.cW0 >= 0 && ++this.cW1 == 0 ? (++this.cW2 & 32) != 0 : false;
   }
}
