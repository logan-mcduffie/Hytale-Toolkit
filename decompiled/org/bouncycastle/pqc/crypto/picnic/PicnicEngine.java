package org.bouncycastle.pqc.crypto.picnic;

import java.security.SecureRandom;
import java.util.logging.Logger;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.math.raw.Bits;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

class PicnicEngine {
   private static final Logger LOG = Logger.getLogger(PicnicEngine.class.getName());
   protected static final int saltSizeBytes = 32;
   private static final int MAX_DIGEST_SIZE = 64;
   private static final int WORD_SIZE_BITS = 32;
   private static final int LOWMC_MAX_STATE_SIZE = 64;
   protected static final int LOWMC_MAX_WORDS = 16;
   protected static final int LOWMC_MAX_KEY_BITS = 256;
   protected static final int LOWMC_MAX_AND_GATES = 1144;
   private static final int MAX_AUX_BYTES = 176;
   private static final int PICNIC_MAX_LOWMC_BLOCK_SIZE = 32;
   private static final int TRANSFORM_FS = 0;
   private static final int TRANSFORM_UR = 1;
   private static final int TRANSFORM_INVALID = 255;
   private final int CRYPTO_SECRETKEYBYTES;
   private final int CRYPTO_PUBLICKEYBYTES;
   private final int CRYPTO_BYTES;
   protected final int numRounds;
   protected final int numSboxes;
   protected final int stateSizeBits;
   protected final int stateSizeBytes;
   protected final int stateSizeWords;
   protected final int andSizeBytes;
   protected final int UnruhGWithoutInputBytes;
   protected final int UnruhGWithInputBytes;
   protected final int numMPCRounds;
   protected final int numOpenedRounds;
   protected final int numMPCParties;
   protected final int seedSizeBytes;
   protected final int digestSizeBytes;
   protected final int pqSecurityLevel;
   protected final Xof digest;
   private final int transform;
   private final int parameters;
   private int signatureLength;
   protected final LowmcConstants lowmcConstants;

   public int getSecretKeySize() {
      return this.CRYPTO_SECRETKEYBYTES;
   }

   public int getPublicKeySize() {
      return this.CRYPTO_PUBLICKEYBYTES;
   }

   public int getSignatureSize(int var1) {
      return this.CRYPTO_BYTES + var1;
   }

   public int getTrueSignatureSize() {
      return this.signatureLength;
   }

   PicnicEngine(int var1, LowmcConstants var2) {
      this.lowmcConstants = var2;
      this.parameters = var1;
      switch (this.parameters) {
         case 1:
         case 2:
            this.pqSecurityLevel = 64;
            this.stateSizeBits = 128;
            this.numMPCRounds = 219;
            this.numMPCParties = 3;
            this.numSboxes = 10;
            this.numRounds = 20;
            this.digestSizeBytes = 32;
            this.numOpenedRounds = 0;
            break;
         case 3:
         case 4:
            this.pqSecurityLevel = 96;
            this.stateSizeBits = 192;
            this.numMPCRounds = 329;
            this.numMPCParties = 3;
            this.numSboxes = 10;
            this.numRounds = 30;
            this.digestSizeBytes = 48;
            this.numOpenedRounds = 0;
            break;
         case 5:
         case 6:
            this.pqSecurityLevel = 128;
            this.stateSizeBits = 256;
            this.numMPCRounds = 438;
            this.numMPCParties = 3;
            this.numSboxes = 10;
            this.numRounds = 38;
            this.digestSizeBytes = 64;
            this.numOpenedRounds = 0;
            break;
         case 7:
            this.pqSecurityLevel = 64;
            this.stateSizeBits = 129;
            this.numMPCRounds = 250;
            this.numOpenedRounds = 36;
            this.numMPCParties = 16;
            this.numSboxes = 43;
            this.numRounds = 4;
            this.digestSizeBytes = 32;
            break;
         case 8:
            this.pqSecurityLevel = 96;
            this.stateSizeBits = 192;
            this.numMPCRounds = 419;
            this.numOpenedRounds = 52;
            this.numMPCParties = 16;
            this.numSboxes = 64;
            this.numRounds = 4;
            this.digestSizeBytes = 48;
            break;
         case 9:
            this.pqSecurityLevel = 128;
            this.stateSizeBits = 255;
            this.numMPCRounds = 601;
            this.numOpenedRounds = 68;
            this.numMPCParties = 16;
            this.numSboxes = 85;
            this.numRounds = 4;
            this.digestSizeBytes = 64;
            break;
         case 10:
            this.pqSecurityLevel = 64;
            this.stateSizeBits = 129;
            this.numMPCRounds = 219;
            this.numMPCParties = 3;
            this.numSboxes = 43;
            this.numRounds = 4;
            this.digestSizeBytes = 32;
            this.numOpenedRounds = 0;
            break;
         case 11:
            this.pqSecurityLevel = 96;
            this.stateSizeBits = 192;
            this.numMPCRounds = 329;
            this.numMPCParties = 3;
            this.numSboxes = 64;
            this.numRounds = 4;
            this.digestSizeBytes = 48;
            this.numOpenedRounds = 0;
            break;
         case 12:
            this.pqSecurityLevel = 128;
            this.stateSizeBits = 255;
            this.numMPCRounds = 438;
            this.numMPCParties = 3;
            this.numSboxes = 85;
            this.numRounds = 4;
            this.digestSizeBytes = 64;
            this.numOpenedRounds = 0;
            break;
         default:
            throw new IllegalArgumentException("unknown parameter set " + this.parameters);
      }

      switch (this.parameters) {
         case 1:
            this.CRYPTO_SECRETKEYBYTES = 49;
            this.CRYPTO_PUBLICKEYBYTES = 33;
            this.CRYPTO_BYTES = 34036;
            break;
         case 2:
            this.CRYPTO_SECRETKEYBYTES = 49;
            this.CRYPTO_PUBLICKEYBYTES = 33;
            this.CRYPTO_BYTES = 53965;
            break;
         case 3:
            this.CRYPTO_SECRETKEYBYTES = 73;
            this.CRYPTO_PUBLICKEYBYTES = 49;
            this.CRYPTO_BYTES = 76784;
            break;
         case 4:
            this.CRYPTO_SECRETKEYBYTES = 73;
            this.CRYPTO_PUBLICKEYBYTES = 49;
            this.CRYPTO_BYTES = 121857;
            break;
         case 5:
            this.CRYPTO_SECRETKEYBYTES = 97;
            this.CRYPTO_PUBLICKEYBYTES = 65;
            this.CRYPTO_BYTES = 132876;
            break;
         case 6:
            this.CRYPTO_SECRETKEYBYTES = 97;
            this.CRYPTO_PUBLICKEYBYTES = 65;
            this.CRYPTO_BYTES = 209526;
            break;
         case 7:
            this.CRYPTO_SECRETKEYBYTES = 52;
            this.CRYPTO_PUBLICKEYBYTES = 35;
            this.CRYPTO_BYTES = 14612;
            break;
         case 8:
            this.CRYPTO_SECRETKEYBYTES = 73;
            this.CRYPTO_PUBLICKEYBYTES = 49;
            this.CRYPTO_BYTES = 35028;
            break;
         case 9:
            this.CRYPTO_SECRETKEYBYTES = 97;
            this.CRYPTO_PUBLICKEYBYTES = 65;
            this.CRYPTO_BYTES = 61028;
            break;
         case 10:
            this.CRYPTO_SECRETKEYBYTES = 52;
            this.CRYPTO_PUBLICKEYBYTES = 35;
            this.CRYPTO_BYTES = 32061;
            break;
         case 11:
            this.CRYPTO_SECRETKEYBYTES = 73;
            this.CRYPTO_PUBLICKEYBYTES = 49;
            this.CRYPTO_BYTES = 71179;
            break;
         case 12:
            this.CRYPTO_SECRETKEYBYTES = 97;
            this.CRYPTO_PUBLICKEYBYTES = 65;
            this.CRYPTO_BYTES = 126286;
            break;
         default:
            this.CRYPTO_SECRETKEYBYTES = -1;
            this.CRYPTO_PUBLICKEYBYTES = -1;
            this.CRYPTO_BYTES = -1;
      }

      this.andSizeBytes = Utils.numBytes(this.numSboxes * 3 * this.numRounds);
      this.stateSizeBytes = Utils.numBytes(this.stateSizeBits);
      this.seedSizeBytes = Utils.numBytes(2 * this.pqSecurityLevel);
      this.stateSizeWords = (this.stateSizeBits + 32 - 1) / 32;
      switch (this.parameters) {
         case 1:
         case 3:
         case 5:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
            this.transform = 0;
            break;
         case 2:
         case 4:
         case 6:
            this.transform = 1;
            break;
         default:
            this.transform = 255;
      }

      if (this.transform == 1) {
         this.UnruhGWithoutInputBytes = this.seedSizeBytes + this.andSizeBytes;
         this.UnruhGWithInputBytes = this.UnruhGWithoutInputBytes + this.stateSizeBytes;
      } else {
         this.UnruhGWithoutInputBytes = 0;
         this.UnruhGWithInputBytes = 0;
      }

      if (this.stateSizeBits != 128 && this.stateSizeBits != 129) {
         this.digest = new SHAKEDigest(256);
      } else {
         this.digest = new SHAKEDigest(128);
      }
   }

   public boolean crypto_sign_open(byte[] var1, byte[] var2, byte[] var3) {
      int var4 = Pack.littleEndianToInt(var2, 0);
      byte[] var5 = Arrays.copyOfRange(var2, 4, 4 + var1.length);
      int var6 = this.picnic_verify(var3, var5, var2, var4);
      if (var6 == -1) {
         return false;
      } else {
         System.arraycopy(var2, 4, var1, 0, var1.length);
         return true;
      }
   }

   private int picnic_verify(byte[] var1, byte[] var2, byte[] var3, int var4) {
      int[] var5 = new int[this.stateSizeWords];
      int[] var6 = new int[this.stateSizeWords];
      this.picnic_read_public_key(var5, var6, var1);
      if (is_picnic3(this.parameters)) {
         Signature2 var9 = new Signature2(this);
         int var10 = this.deserializeSignature2(var9, var3, var4, var2.length + 4);
         if (var10 != 0) {
            LOG.fine("Error couldn't deserialize signature (2)!");
            return -1;
         } else {
            return this.verify_picnic3(var9, var5, var6, var2);
         }
      } else {
         Signature var7 = new Signature(this);
         int var8 = this.deserializeSignature(var7, var3, var4, var2.length + 4);
         if (var8 != 0) {
            LOG.fine("Error couldn't deserialize signature!");
            return -1;
         } else {
            return this.verify(var7, var5, var6, var2);
         }
      }
   }

   private int verify(Signature var1, int[] var2, int[] var3, byte[] var4) {
      byte[][][] var5 = new byte[this.numMPCRounds][this.numMPCParties][this.digestSizeBytes];
      byte[][][] var6 = new byte[this.numMPCRounds][3][this.UnruhGWithInputBytes];
      int[][][] var7 = new int[this.numMPCRounds][3][this.stateSizeBytes];
      Signature.Proof[] var8 = var1.proofs;
      byte[] var9 = var1.challengeBits;
      byte var10 = 0;
      Object var11 = null;
      byte[] var12 = new byte[Math.max(6 * this.stateSizeBytes, this.stateSizeBytes + this.andSizeBytes)];
      Tape var13 = new Tape(this);
      View[] var14 = new View[this.numMPCRounds];
      View[] var15 = new View[this.numMPCRounds];

      for (int var16 = 0; var16 < this.numMPCRounds; var16++) {
         var14[var16] = new View(this);
         var15[var16] = new View(this);
         if (!this.verifyProof(var8[var16], var14[var16], var15[var16], this.getChallenge(var9, var16), var1.salt, var16, var12, var3, var13)) {
            LOG.fine("Invalid signature. Did not verify");
            return -1;
         }

         int var17 = this.getChallenge(var9, var16);
         this.Commit(var8[var16].seed1, 0, var14[var16], var5[var16][var17]);
         this.Commit(var8[var16].seed2, 0, var15[var16], var5[var16][(var17 + 1) % 3]);
         System.arraycopy(var8[var16].view3Commitment, 0, var5[var16][(var17 + 2) % 3], 0, this.digestSizeBytes);
         if (this.transform == 1) {
            this.G(var17, var8[var16].seed1, 0, var14[var16], var6[var16][var17]);
            this.G((var17 + 1) % 3, var8[var16].seed2, 0, var15[var16], var6[var16][(var17 + 1) % 3]);
            int var18 = var17 == 0 ? this.UnruhGWithInputBytes : this.UnruhGWithoutInputBytes;
            System.arraycopy(var8[var16].view3UnruhG, 0, var6[var16][(var17 + 2) % 3], 0, var18);
         }

         var7[var16][var17] = var14[var16].outputShare;
         var7[var16][(var17 + 1) % 3] = var15[var16].outputShare;
         int[] var20 = new int[this.stateSizeWords];
         this.xor_three(var20, var14[var16].outputShare, var15[var16].outputShare, var2);
         var7[var16][(var17 + 2) % 3] = var20;
      }

      var11 = new byte[Utils.numBytes(2 * this.numMPCRounds)];
      this.H3(var2, var3, var7, var5, (byte[])var11, var1.salt, var4, var6);
      if (!subarrayEquals(var9, (byte[])var11, Utils.numBytes(2 * this.numMPCRounds))) {
         LOG.fine("Invalid signature. Did not verify");
         var10 = -1;
      }

      return var10;
   }

   boolean verifyProof(Signature.Proof var1, View var2, View var3, int var4, byte[] var5, int var6, byte[] var7, int[] var8, Tape var9) {
      System.arraycopy(var1.communicatedBits, 0, var3.communicatedBits, 0, this.andSizeBytes);
      var9.pos = 0;
      boolean var10 = false;
      switch (var4) {
         case 0:
            var10 = this.createRandomTape(var1.seed1, 0, var5, var6, 0, var7, this.stateSizeBytes + this.andSizeBytes);
            Pack.littleEndianToInt(var7, 0, var2.inputShare);
            System.arraycopy(var7, this.stateSizeBytes, var9.tapes[0], 0, this.andSizeBytes);
            var10 = var10 && this.createRandomTape(var1.seed2, 0, var5, var6, 1, var7, this.stateSizeBytes + this.andSizeBytes);
            if (var10) {
               Pack.littleEndianToInt(var7, 0, var3.inputShare);
               System.arraycopy(var7, this.stateSizeBytes, var9.tapes[1], 0, this.andSizeBytes);
            }
            break;
         case 1:
            var10 = this.createRandomTape(var1.seed1, 0, var5, var6, 1, var7, this.stateSizeBytes + this.andSizeBytes);
            Pack.littleEndianToInt(var7, 0, var2.inputShare);
            System.arraycopy(var7, this.stateSizeBytes, var9.tapes[0], 0, this.andSizeBytes);
            var10 = var10 && this.createRandomTape(var1.seed2, 0, var5, var6, 2, var9.tapes[1], this.andSizeBytes);
            if (var10) {
               System.arraycopy(var1.inputShare, 0, var3.inputShare, 0, this.stateSizeWords);
            }
            break;
         case 2:
            var10 = this.createRandomTape(var1.seed1, 0, var5, var6, 2, var9.tapes[0], this.andSizeBytes);
            System.arraycopy(var1.inputShare, 0, var2.inputShare, 0, this.stateSizeWords);
            var10 = var10 && this.createRandomTape(var1.seed2, 0, var5, var6, 0, var7, this.stateSizeBytes + this.andSizeBytes);
            if (var10) {
               Pack.littleEndianToInt(var7, 0, var3.inputShare);
               System.arraycopy(var7, this.stateSizeBytes, var9.tapes[1], 0, this.andSizeBytes);
            }
            break;
         default:
            LOG.fine("Invalid Challenge!");
      }

      if (!var10) {
         LOG.fine("Failed to generate random tapes, signature verification will fail (but signature may actually be valid)");
         return false;
      } else {
         Utils.zeroTrailingBits(var2.inputShare, this.stateSizeBits);
         Utils.zeroTrailingBits(var3.inputShare, this.stateSizeBits);
         int[] var11 = Pack.littleEndianToInt(var7, 0, var7.length / 4);
         this.mpc_LowMC_verify(var2, var3, var9, var11, var8, var4);
         return true;
      }
   }

   void mpc_LowMC_verify(View var1, View var2, Tape var3, int[] var4, int[] var5, int var6) {
      Arrays.fill(var4, 0, var4.length, 0);
      this.mpc_xor_constant_verify(var4, var5, 0, this.stateSizeWords, var6);
      KMatricesWithPointer var7 = this.lowmcConstants.KMatrix(this, 0);
      this.matrix_mul_offset(var4, 0, var1.inputShare, 0, var7.getData(), var7.getMatrixPointer());
      this.matrix_mul_offset(var4, this.stateSizeWords, var2.inputShare, 0, var7.getData(), var7.getMatrixPointer());
      this.mpc_xor(var4, var4, 2);

      for (int var8 = 1; var8 <= this.numRounds; var8++) {
         var7 = this.lowmcConstants.KMatrix(this, var8);
         this.matrix_mul_offset(var4, 0, var1.inputShare, 0, var7.getData(), var7.getMatrixPointer());
         this.matrix_mul_offset(var4, this.stateSizeWords, var2.inputShare, 0, var7.getData(), var7.getMatrixPointer());
         this.mpc_substitution_verify(var4, var3, var1, var2);
         var7 = this.lowmcConstants.LMatrix(this, var8 - 1);
         this.mpc_matrix_mul(var4, 2 * this.stateSizeWords, var4, 2 * this.stateSizeWords, var7.getData(), var7.getMatrixPointer(), 2);
         var7 = this.lowmcConstants.RConstant(this, var8 - 1);
         this.mpc_xor_constant_verify(var4, var7.getData(), var7.getMatrixPointer(), this.stateSizeWords, var6);
         this.mpc_xor(var4, var4, 2);
      }

      System.arraycopy(var4, 2 * this.stateSizeWords, var1.outputShare, 0, this.stateSizeWords);
      System.arraycopy(var4, 3 * this.stateSizeWords, var2.outputShare, 0, this.stateSizeWords);
   }

   void mpc_substitution_verify(int[] var1, Tape var2, View var3, View var4) {
      int[] var5 = new int[2];
      int[] var6 = new int[2];
      int[] var7 = new int[2];
      int[] var8 = new int[2];
      int[] var9 = new int[2];
      int[] var10 = new int[2];

      for (byte var12 = 0; var12 < this.numSboxes * 3; var12 += 3) {
         for (int var13 = 0; var13 < 2; var13++) {
            int var11 = (2 + var13) * this.stateSizeWords * 32;
            var5[var13] = Utils.getBitFromWordArray(var1, var11 + var12 + 2);
            var6[var13] = Utils.getBitFromWordArray(var1, var11 + var12 + 1);
            var7[var13] = Utils.getBitFromWordArray(var1, var11 + var12);
         }

         this.mpc_AND_verify(var5, var6, var8, var2, var3, var4);
         this.mpc_AND_verify(var6, var7, var9, var2, var3, var4);
         this.mpc_AND_verify(var7, var5, var10, var2, var3, var4);

         for (int var15 = 0; var15 < 2; var15++) {
            int var14 = (2 + var15) * this.stateSizeWords * 32;
            Utils.setBitInWordArray(var1, var14 + var12 + 2, var5[var15] ^ var9[var15]);
            Utils.setBitInWordArray(var1, var14 + var12 + 1, var5[var15] ^ var6[var15] ^ var10[var15]);
            Utils.setBitInWordArray(var1, var14 + var12, var5[var15] ^ var6[var15] ^ var7[var15] ^ var8[var15]);
         }
      }
   }

   void mpc_AND_verify(int[] var1, int[] var2, int[] var3, Tape var4, View var5, View var6) {
      byte var7 = Utils.getBit(var4.tapes[0], var4.pos);
      byte var8 = Utils.getBit(var4.tapes[1], var4.pos);
      int var9 = var1[0];
      int var10 = var1[1];
      int var11 = var2[0];
      int var12 = var2[1];
      var3[0] = var9 & var12 ^ var10 & var11 ^ var9 & var11 ^ var7 ^ var8;
      Utils.setBit(var5.communicatedBits, var4.pos, (byte)var3[0]);
      var3[1] = Utils.getBit(var6.communicatedBits, var4.pos);
      var4.pos++;
   }

   private void mpc_xor_constant_verify(int[] var1, int[] var2, int var3, int var4, int var5) {
      int var6 = 0;
      if (var5 == 0) {
         var6 = 2 * this.stateSizeWords;
      } else {
         if (var5 != 2) {
            return;
         }

         var6 = 3 * this.stateSizeWords;
      }

      for (int var7 = 0; var7 < var4; var7++) {
         var1[var7 + var6] = var1[var7 + var6] ^ var2[var7 + var3];
      }
   }

   private int deserializeSignature(Signature var1, byte[] var2, int var3, int var4) {
      Signature.Proof[] var5 = var1.proofs;
      byte[] var6 = var1.challengeBits;
      int var7 = Utils.numBytes(2 * this.numMPCRounds);
      if (var3 < var7) {
         return -1;
      } else {
         int var8 = this.countNonZeroChallenges(var2, var4);
         if (var8 < 0) {
            return -1;
         } else {
            int var9 = var8 * this.stateSizeBytes;
            int var10 = var7 + 32 + this.numMPCRounds * (2 * this.seedSizeBytes + this.andSizeBytes + this.digestSizeBytes) + var9;
            if (this.transform == 1) {
               var10 += this.UnruhGWithInputBytes * (this.numMPCRounds - var8);
               var10 += this.UnruhGWithoutInputBytes * var8;
            }

            if (var3 != var10) {
               LOG.fine("sigBytesLen = " + var3 + ", expected bytesRequired = " + var10);
               return -1;
            } else {
               System.arraycopy(var2, var4, var6, 0, var7);
               var4 += var7;
               System.arraycopy(var2, var4, var1.salt, 0, 32);
               var4 += 32;

               for (int var11 = 0; var11 < this.numMPCRounds; var11++) {
                  int var12 = this.getChallenge(var6, var11);
                  System.arraycopy(var2, var4, var5[var11].view3Commitment, 0, this.digestSizeBytes);
                  var4 += this.digestSizeBytes;
                  if (this.transform == 1) {
                     int var13 = var12 == 0 ? this.UnruhGWithInputBytes : this.UnruhGWithoutInputBytes;
                     System.arraycopy(var2, var4, var5[var11].view3UnruhG, 0, var13);
                     var4 += var13;
                  }

                  System.arraycopy(var2, var4, var5[var11].communicatedBits, 0, this.andSizeBytes);
                  var4 += this.andSizeBytes;
                  System.arraycopy(var2, var4, var5[var11].seed1, 0, this.seedSizeBytes);
                  var4 += this.seedSizeBytes;
                  System.arraycopy(var2, var4, var5[var11].seed2, 0, this.seedSizeBytes);
                  var4 += this.seedSizeBytes;
                  if (var12 == 1 || var12 == 2) {
                     Pack.littleEndianToInt(var2, var4, var5[var11].inputShare, 0, this.stateSizeBytes / 4);
                     if (this.stateSizeBits == 129) {
                        var5[var11].inputShare[this.stateSizeWords - 1] = var2[var4 + this.stateSizeBytes - 1] & 255;
                     }

                     var4 += this.stateSizeBytes;
                     if (!this.arePaddingBitsZero(var5[var11].inputShare, this.stateSizeBits)) {
                        return -1;
                     }
                  }
               }

               return 0;
            }
         }
      }
   }

   private int countNonZeroChallenges(byte[] var1, int var2) {
      int var3 = 0;
      int var4 = 0;

      byte var5;
      for (var5 = 0; var5 + 16 <= this.numMPCRounds; var5 += 16) {
         int var6 = Pack.littleEndianToInt(var1, var2 + (var5 >>> 2));
         var4 |= var6 & var6 >>> 1;
         var3 += Integers.bitCount((var6 ^ var6 >>> 1) & 1431655765);
      }

      int var9 = (this.numMPCRounds - var5) * 2;
      if (var9 > 0) {
         int var7 = (var9 + 7) / 8;
         int var8 = Pack.littleEndianToInt_Low(var1, var2 + (var5 >>> 2), var7);
         var8 &= Utils.getTrailingBitsMask(var9);
         var4 |= var8 & var8 >>> 1;
         var3 += Integers.bitCount((var8 ^ var8 >>> 1) & 1431655765);
      }

      return (var4 & 1431655765) == 0 ? var3 : -1;
   }

   private void picnic_read_public_key(int[] var1, int[] var2, byte[] var3) {
      byte var4 = 1;
      int var5 = 1 + this.stateSizeBytes;
      int var6 = this.stateSizeBytes / 4;
      Pack.littleEndianToInt(var3, var4, var1, 0, var6);
      Pack.littleEndianToInt(var3, var5, var2, 0, var6);
      if (var6 < this.stateSizeWords) {
         int var7 = var6 * 4;
         int var8 = this.stateSizeBytes - var7;
         var1[var6] = Pack.littleEndianToInt_Low(var3, var4 + var7, var8);
         var2[var6] = Pack.littleEndianToInt_Low(var3, var5 + var7, var8);
      }
   }

   private int verify_picnic3(Signature2 var1, int[] var2, int[] var3, byte[] var4) {
      byte[][][] var5 = new byte[this.numMPCRounds][this.numMPCParties][this.digestSizeBytes];
      byte[][] var6 = new byte[this.numMPCRounds][this.digestSizeBytes];
      byte[][] var7 = new byte[this.numMPCRounds][this.digestSizeBytes];
      Msg[] var8 = new Msg[this.numMPCRounds];
      Tree var9 = new Tree(this, this.numMPCRounds, this.digestSizeBytes);
      byte[] var10 = new byte[64];
      Tree[] var11 = new Tree[this.numMPCRounds];
      Tape[] var12 = new Tape[this.numMPCRounds];
      Tree var13 = new Tree(this, this.numMPCRounds, this.seedSizeBytes);
      int var14 = var13.reconstructSeeds(var1.challengeC, this.numOpenedRounds, var1.iSeedInfo, var1.iSeedInfoLen, var1.salt, 0);
      if (var14 != 0) {
         return -1;
      } else {
         for (int var15 = 0; var15 < this.numMPCRounds; var15++) {
            if (!this.contains(var1.challengeC, this.numOpenedRounds, var15)) {
               var11[var15] = new Tree(this, this.numMPCParties, this.seedSizeBytes);
               var11[var15].generateSeeds(var13.getLeaf(var15), var1.salt, var15);
            } else {
               var11[var15] = new Tree(this, this.numMPCParties, this.seedSizeBytes);
               int var16 = indexOf(var1.challengeC, this.numOpenedRounds, var15);
               int[] var17 = new int[]{var1.challengeP[var16]};
               var14 = var11[var15].reconstructSeeds(var17, 1, var1.proofs[var15].seedInfo, var1.proofs[var15].seedInfoLen, var1.salt, var15);
               if (var14 != 0) {
                  LOG.fine("Failed to reconstruct seeds for round " + var15);
                  return -1;
               }
            }
         }

         int var26 = this.numMPCParties - 1;
         byte[] var27 = new byte[176];

         for (int var28 = 0; var28 < this.numMPCRounds; var28++) {
            var12[var28] = new Tape(this);
            this.createRandomTapes(var12[var28], var11[var28].getLeaves(), var11[var28].getLeavesOffset(), var1.salt, var28);
            if (!this.contains(var1.challengeC, this.numOpenedRounds, var28)) {
               var12[var28].computeAuxTape(null);

               for (int var31 = 0; var31 < var26; var31++) {
                  this.commit(var5[var28][var31], var11[var28].getLeaf(var31), null, var1.salt, var28, var31);
               }

               this.getAuxBits(var27, var12[var28]);
               this.commit(var5[var28][var26], var11[var28].getLeaf(var26), var27, var1.salt, var28, var26);
            } else {
               int var18 = var1.challengeP[indexOf(var1.challengeC, this.numOpenedRounds, var28)];

               for (int var19 = 0; var19 < var26; var19++) {
                  if (var19 != var18) {
                     this.commit(var5[var28][var19], var11[var28].getLeaf(var19), null, var1.salt, var28, var19);
                  }
               }

               if (var26 != var18) {
                  this.commit(var5[var28][var26], var11[var28].getLeaf(var26), var1.proofs[var28].aux, var1.salt, var28, var26);
               }

               System.arraycopy(var1.proofs[var28].C, 0, var5[var28][var18], 0, this.digestSizeBytes);
            }
         }

         for (int var29 = 0; var29 < this.numMPCRounds; var29++) {
            this.commit_h(var6[var29], var5[var29]);
         }

         int[] var30 = new int[this.stateSizeBits];

         for (int var32 = 0; var32 < this.numMPCRounds; var32++) {
            var8[var32] = new Msg(this);
            if (this.contains(var1.challengeC, this.numOpenedRounds, var32)) {
               int var34 = var1.challengeP[indexOf(var1.challengeC, this.numOpenedRounds, var32)];
               if (var34 != var26) {
                  var12[var32].setAuxBits(var1.proofs[var32].aux);
               }

               System.arraycopy(var1.proofs[var32].msgs, 0, var8[var32].msgs[var34], 0, this.andSizeBytes);
               Arrays.fill(var12[var32].tapes[var34], (byte)0);
               var8[var32].unopened = var34;
               byte[] var20 = new byte[this.stateSizeWords * 4];
               System.arraycopy(var1.proofs[var32].input, 0, var20, 0, var1.proofs[var32].input.length);
               int[] var21 = new int[this.stateSizeWords];
               Pack.littleEndianToInt(var20, 0, var21, 0, this.stateSizeWords);
               int var22 = this.simulateOnline(var21, var12[var32], var30, var8[var32], var3, var2);
               if (var22 != 0) {
                  LOG.fine("MPC simulation failed for round " + var32 + ", signature invalid");
                  return -1;
               }

               this.commit_v(var7[var32], var1.proofs[var32].input, var8[var32]);
            } else {
               var7[var32] = null;
            }
         }

         int var33 = this.numMPCRounds - this.numOpenedRounds;
         int[] var35 = this.getMissingLeavesList(var1.challengeC);
         var14 = var9.addMerkleNodes(var35, var33, var1.cvInfo, var1.cvInfoLen);
         if (var14 != 0) {
            return -1;
         } else {
            var14 = var9.verifyMerkleTree(var7, var1.salt);
            if (var14 != 0) {
               return -1;
            } else {
               this.HCP(var10, null, null, var6, var9.nodes[0], var1.salt, var2, var3, var4);
               if (!subarrayEquals(var1.challengeHash, var10, this.digestSizeBytes)) {
                  LOG.fine("Challenge does not match, signature invalid");
                  return -1;
               } else {
                  return var14;
               }
            }
         }
      }
   }

   private int deserializeSignature2(Signature2 var1, byte[] var2, int var3, int var4) {
      int var5 = this.digestSizeBytes + 32;
      if (var2.length < var5) {
         return -1;
      } else {
         System.arraycopy(var2, var4, var1.challengeHash, 0, this.digestSizeBytes);
         var4 += this.digestSizeBytes;
         System.arraycopy(var2, var4, var1.salt, 0, 32);
         var4 += 32;
         this.expandChallengeHash(var1.challengeHash, var1.challengeC, var1.challengeP);
         Tree var6 = new Tree(this, this.numMPCRounds, this.seedSizeBytes);
         var1.iSeedInfoLen = var6.revealSeedsSize(var1.challengeC, this.numOpenedRounds);
         var5 += var1.iSeedInfoLen;
         int var7 = this.numMPCRounds - this.numOpenedRounds;
         int[] var8 = this.getMissingLeavesList(var1.challengeC);
         var6 = new Tree(this, this.numMPCRounds, this.digestSizeBytes);
         var1.cvInfoLen = var6.openMerkleTreeSize(var8, var7);
         var5 += var1.cvInfoLen;
         int[] var9 = new int[1];
         var6 = new Tree(this, this.numMPCParties, this.seedSizeBytes);
         int var10 = var6.revealSeedsSize(var9, 1);

         for (int var11 = 0; var11 < this.numMPCRounds; var11++) {
            if (this.contains(var1.challengeC, this.numOpenedRounds, var11)) {
               int var12 = var1.challengeP[indexOf(var1.challengeC, this.numOpenedRounds, var11)];
               if (var12 != this.numMPCParties - 1) {
                  var5 += this.andSizeBytes;
               }

               var5 += var10;
               var5 += this.stateSizeBytes;
               var5 += this.andSizeBytes;
               var5 += this.digestSizeBytes;
            }
         }

         if (var3 != var5) {
            LOG.fine("sigLen = " + var3 + ", expected bytesRequired = " + var5);
            return -1;
         } else {
            var1.iSeedInfo = new byte[var1.iSeedInfoLen];
            System.arraycopy(var2, var4, var1.iSeedInfo, 0, var1.iSeedInfoLen);
            var4 += var1.iSeedInfoLen;
            var1.cvInfo = new byte[var1.cvInfoLen];
            System.arraycopy(var2, var4, var1.cvInfo, 0, var1.cvInfoLen);
            var4 += var1.cvInfoLen;

            for (int var29 = 0; var29 < this.numMPCRounds; var29++) {
               if (this.contains(var1.challengeC, this.numOpenedRounds, var29)) {
                  var1.proofs[var29] = new Signature2.Proof2(this);
                  var1.proofs[var29].seedInfoLen = var10;
                  var1.proofs[var29].seedInfo = new byte[var1.proofs[var29].seedInfoLen];
                  System.arraycopy(var2, var4, var1.proofs[var29].seedInfo, 0, var1.proofs[var29].seedInfoLen);
                  var4 += var1.proofs[var29].seedInfoLen;
                  int var30 = var1.challengeP[indexOf(var1.challengeC, this.numOpenedRounds, var29)];
                  if (var30 != this.numMPCParties - 1) {
                     System.arraycopy(var2, var4, var1.proofs[var29].aux, 0, this.andSizeBytes);
                     var4 += this.andSizeBytes;
                     if (!this.arePaddingBitsZero(var1.proofs[var29].aux, 3 * this.numRounds * this.numSboxes)) {
                        LOG.fine("failed while deserializing aux bits");
                        return -1;
                     }
                  }

                  System.arraycopy(var2, var4, var1.proofs[var29].input, 0, this.stateSizeBytes);
                  var4 += this.stateSizeBytes;
                  int var13 = this.andSizeBytes;
                  System.arraycopy(var2, var4, var1.proofs[var29].msgs, 0, var13);
                  var4 += var13;
                  int var14 = 3 * this.numRounds * this.numSboxes;
                  if (!this.arePaddingBitsZero(var1.proofs[var29].msgs, var14)) {
                     LOG.fine("failed while deserializing msgs bits");
                     return -1;
                  }

                  System.arraycopy(var2, var4, var1.proofs[var29].C, 0, this.digestSizeBytes);
                  var4 += this.digestSizeBytes;
               }
            }

            return 0;
         }
      }
   }

   private boolean arePaddingBitsZero(byte[] var1, int var2) {
      int var3 = Utils.numBytes(var2);

      for (int var4 = var2; var4 < var3 * 8; var4++) {
         byte var5 = Utils.getBit(var1, var4);
         if (var5 != 0) {
            return false;
         }
      }

      return true;
   }

   private boolean arePaddingBitsZero(int[] var1, int var2) {
      int var3 = var2 & 31;
      if (var3 == 0) {
         return true;
      } else {
         int var4 = Utils.getTrailingBitsMask(var2);
         return (var1[var2 >>> 5] & ~var4) == 0;
      }
   }

   public void crypto_sign(byte[] var1, byte[] var2, byte[] var3) {
      boolean var4 = this.picnic_sign(var3, var2, var1);
      if (var4) {
         System.arraycopy(var2, 0, var1, 4, var2.length);
      }
   }

   private boolean picnic_sign(byte[] var1, byte[] var2, byte[] var3) {
      int[] var4 = new int[this.stateSizeWords];
      int[] var5 = new int[this.stateSizeWords];
      int[] var6 = new int[this.stateSizeWords];
      byte var7 = 1;
      int var8 = 1 + this.stateSizeBytes;
      int var9 = 1 + 2 * this.stateSizeBytes;
      int var10 = this.stateSizeBytes / 4;
      Pack.littleEndianToInt(var1, var7, var4, 0, var10);
      Pack.littleEndianToInt(var1, var8, var5, 0, var10);
      Pack.littleEndianToInt(var1, var9, var6, 0, var10);
      if (var10 < this.stateSizeWords) {
         int var11 = var10 * 4;
         int var12 = this.stateSizeBytes - var11;
         var4[var10] = Pack.littleEndianToInt_Low(var1, var7 + var11, var12);
         var5[var10] = Pack.littleEndianToInt_Low(var1, var8 + var11, var12);
         var6[var10] = Pack.littleEndianToInt_Low(var1, var9 + var11, var12);
      }

      if (!is_picnic3(this.parameters)) {
         Signature var15 = new Signature(this);
         int var17 = this.sign_picnic1(var4, var5, var6, var2, var15);
         if (var17 != 0) {
            LOG.fine("Failed to create signature");
            return false;
         } else {
            int var18 = this.serializeSignature(var15, var3, var2.length + 4);
            if (var18 < 0) {
               LOG.fine("Failed to serialize signature");
               return false;
            } else {
               this.signatureLength = var18;
               Pack.intToLittleEndian(var18, var3, 0);
               return true;
            }
         }
      } else {
         Signature2 var14 = new Signature2(this);
         boolean var16 = this.sign_picnic3(var4, var5, var6, var2, var14);
         if (!var16) {
            LOG.fine("Failed to create signature");
            return false;
         } else {
            int var13 = this.serializeSignature2(var14, var3, var2.length + 4);
            if (var13 < 0) {
               LOG.fine("Failed to serialize signature");
               return false;
            } else {
               this.signatureLength = var13;
               Pack.intToLittleEndian(var13, var3, 0);
               return true;
            }
         }
      }
   }

   int serializeSignature(Signature var1, byte[] var2, int var3) {
      Signature.Proof[] var4 = var1.proofs;
      byte[] var5 = var1.challengeBits;
      int var6 = Utils.numBytes(2 * this.numMPCRounds)
         + 32
         + this.numMPCRounds * (2 * this.seedSizeBytes + this.stateSizeBytes + this.andSizeBytes + this.digestSizeBytes);
      if (this.transform == 1) {
         var6 += this.UnruhGWithoutInputBytes * this.numMPCRounds;
      }

      if (this.CRYPTO_BYTES < var6) {
         return -1;
      } else {
         System.arraycopy(var5, 0, var2, var3, Utils.numBytes(2 * this.numMPCRounds));
         int var7 = var3 + Utils.numBytes(2 * this.numMPCRounds);
         System.arraycopy(var1.salt, 0, var2, var7, 32);
         var7 += 32;

         for (int var8 = 0; var8 < this.numMPCRounds; var8++) {
            int var9 = this.getChallenge(var5, var8);
            System.arraycopy(var4[var8].view3Commitment, 0, var2, var7, this.digestSizeBytes);
            var7 += this.digestSizeBytes;
            if (this.transform == 1) {
               int var10 = var9 == 0 ? this.UnruhGWithInputBytes : this.UnruhGWithoutInputBytes;
               System.arraycopy(var4[var8].view3UnruhG, 0, var2, var7, var10);
               var7 += var10;
            }

            System.arraycopy(var4[var8].communicatedBits, 0, var2, var7, this.andSizeBytes);
            var7 += this.andSizeBytes;
            System.arraycopy(var4[var8].seed1, 0, var2, var7, this.seedSizeBytes);
            var7 += this.seedSizeBytes;
            System.arraycopy(var4[var8].seed2, 0, var2, var7, this.seedSizeBytes);
            var7 += this.seedSizeBytes;
            if (var9 == 1 || var9 == 2) {
               Pack.intToLittleEndian(var4[var8].inputShare, 0, this.stateSizeWords, var2, var7);
               var7 += this.stateSizeBytes;
            }
         }

         return var7 - var3;
      }
   }

   int getChallenge(byte[] var1, int var2) {
      return Utils.getCrumbAligned(var1, var2);
   }

   private int serializeSignature2(Signature2 var1, byte[] var2, int var3) {
      int var4 = this.digestSizeBytes + 32;
      var4 += var1.iSeedInfoLen;
      var4 += var1.cvInfoLen;

      for (int var5 = 0; var5 < this.numMPCRounds; var5++) {
         if (this.contains(var1.challengeC, this.numOpenedRounds, var5)) {
            int var6 = var1.challengeP[indexOf(var1.challengeC, this.numOpenedRounds, var5)];
            var4 += var1.proofs[var5].seedInfoLen;
            if (var6 != this.numMPCParties - 1) {
               var4 += this.andSizeBytes;
            }

            var4 += this.stateSizeBytes;
            var4 += this.andSizeBytes;
            var4 += this.digestSizeBytes;
         }
      }

      if (var2.length < var4) {
         return -1;
      } else {
         System.arraycopy(var1.challengeHash, 0, var2, var3, this.digestSizeBytes);
         int var13 = var3 + this.digestSizeBytes;
         System.arraycopy(var1.salt, 0, var2, var13, 32);
         var13 += 32;
         System.arraycopy(var1.iSeedInfo, 0, var2, var13, var1.iSeedInfoLen);
         var13 += var1.iSeedInfoLen;
         System.arraycopy(var1.cvInfo, 0, var2, var13, var1.cvInfoLen);
         var13 += var1.cvInfoLen;

         for (int var20 = 0; var20 < this.numMPCRounds; var20++) {
            if (this.contains(var1.challengeC, this.numOpenedRounds, var20)) {
               System.arraycopy(var1.proofs[var20].seedInfo, 0, var2, var13, var1.proofs[var20].seedInfoLen);
               var13 += var1.proofs[var20].seedInfoLen;
               int var7 = var1.challengeP[indexOf(var1.challengeC, this.numOpenedRounds, var20)];
               if (var7 != this.numMPCParties - 1) {
                  System.arraycopy(var1.proofs[var20].aux, 0, var2, var13, this.andSizeBytes);
                  var13 += this.andSizeBytes;
               }

               System.arraycopy(var1.proofs[var20].input, 0, var2, var13, this.stateSizeBytes);
               var13 += this.stateSizeBytes;
               System.arraycopy(var1.proofs[var20].msgs, 0, var2, var13, this.andSizeBytes);
               var13 += this.andSizeBytes;
               System.arraycopy(var1.proofs[var20].C, 0, var2, var13, this.digestSizeBytes);
               var13 += this.digestSizeBytes;
            }
         }

         return var13 - var3;
      }
   }

   private int sign_picnic1(int[] var1, int[] var2, int[] var3, byte[] var4, Signature var5) {
      View[][] var7 = new View[this.numMPCRounds][3];
      byte[][][] var8 = new byte[this.numMPCRounds][this.numMPCParties][this.digestSizeBytes];
      byte[][][] var9 = new byte[this.numMPCRounds][3][this.UnruhGWithInputBytes];
      byte[] var10 = this.computeSeeds(var1, var2, var3, var4);
      int var11 = this.numMPCParties * this.seedSizeBytes;
      System.arraycopy(var10, var11 * this.numMPCRounds, var5.salt, 0, 32);
      Tape var12 = new Tape(this);
      byte[] var13 = new byte[Math.max(9 * this.stateSizeBytes, this.stateSizeBytes + this.andSizeBytes)];

      for (int var14 = 0; var14 < this.numMPCRounds; var14++) {
         var7[var14][0] = new View(this);
         var7[var14][1] = new View(this);
         var7[var14][2] = new View(this);

         for (int var15 = 0; var15 < 2; var15++) {
            boolean var6 = this.createRandomTape(
               var10, var11 * var14 + var15 * this.seedSizeBytes, var5.salt, var14, var15, var13, this.stateSizeBytes + this.andSizeBytes
            );
            if (!var6) {
               LOG.fine("createRandomTape failed");
               return -1;
            }

            int[] var16 = var7[var14][var15].inputShare;
            Pack.littleEndianToInt(var13, 0, var16);
            Utils.zeroTrailingBits(var16, this.stateSizeBits);
            System.arraycopy(var13, this.stateSizeBytes, var12.tapes[var15], 0, this.andSizeBytes);
         }

         boolean var17 = this.createRandomTape(var10, var11 * var14 + 2 * this.seedSizeBytes, var5.salt, var14, 2, var12.tapes[2], this.andSizeBytes);
         if (!var17) {
            LOG.fine("createRandomTape failed");
            return -1;
         }

         this.xor_three(var7[var14][2].inputShare, var1, var7[var14][0].inputShare, var7[var14][1].inputShare);
         var12.pos = 0;
         int[] var19 = Pack.littleEndianToInt(var13, 0, var13.length / 4);
         this.mpc_LowMC(var12, var7[var14], var3, var19);
         Pack.intToLittleEndian(var19, var13, 0);
         int[] var21 = new int[16];
         this.xor_three(var21, var7[var14][0].outputShare, var7[var14][1].outputShare, var7[var14][2].outputShare);
         if (!subarrayEquals(var21, var2, this.stateSizeWords)) {
            LOG.fine("Simulation failed; output does not match public key (round = " + var14 + ")");
            return -1;
         }

         this.Commit(var10, var11 * var14 + 0 * this.seedSizeBytes, var7[var14][0], var8[var14][0]);
         this.Commit(var10, var11 * var14 + 1 * this.seedSizeBytes, var7[var14][1], var8[var14][1]);
         this.Commit(var10, var11 * var14 + 2 * this.seedSizeBytes, var7[var14][2], var8[var14][2]);
         if (this.transform == 1) {
            this.G(0, var10, var11 * var14 + 0 * this.seedSizeBytes, var7[var14][0], var9[var14][0]);
            this.G(1, var10, var11 * var14 + 1 * this.seedSizeBytes, var7[var14][1], var9[var14][1]);
            this.G(2, var10, var11 * var14 + 2 * this.seedSizeBytes, var7[var14][2], var9[var14][2]);
         }
      }

      this.H3(var2, var3, var7, var8, var5.challengeBits, var5.salt, var4, var9);

      for (int var18 = 0; var18 < this.numMPCRounds; var18++) {
         Signature.Proof var20 = var5.proofs[var18];
         this.prove(
            var20, this.getChallenge(var5.challengeBits, var18), var10, var11 * var18, var7[var18], var8[var18], this.transform != 1 ? null : var9[var18]
         );
      }

      return 0;
   }

   void prove(Signature.Proof var1, int var2, byte[] var3, int var4, View[] var5, byte[][] var6, byte[][] var7) {
      if (var2 == 0) {
         System.arraycopy(var3, var4 + 0 * this.seedSizeBytes, var1.seed1, 0, this.seedSizeBytes);
         System.arraycopy(var3, var4 + 1 * this.seedSizeBytes, var1.seed2, 0, this.seedSizeBytes);
      } else if (var2 == 1) {
         System.arraycopy(var3, var4 + 1 * this.seedSizeBytes, var1.seed1, 0, this.seedSizeBytes);
         System.arraycopy(var3, var4 + 2 * this.seedSizeBytes, var1.seed2, 0, this.seedSizeBytes);
      } else {
         if (var2 != 2) {
            LOG.fine("Invalid challenge");
            throw new IllegalArgumentException("challenge");
         }

         System.arraycopy(var3, var4 + 2 * this.seedSizeBytes, var1.seed1, 0, this.seedSizeBytes);
         System.arraycopy(var3, var4 + 0 * this.seedSizeBytes, var1.seed2, 0, this.seedSizeBytes);
      }

      if (var2 == 1 || var2 == 2) {
         System.arraycopy(var5[2].inputShare, 0, var1.inputShare, 0, this.stateSizeWords);
      }

      System.arraycopy(var5[(var2 + 1) % 3].communicatedBits, 0, var1.communicatedBits, 0, this.andSizeBytes);
      System.arraycopy(var6[(var2 + 2) % 3], 0, var1.view3Commitment, 0, this.digestSizeBytes);
      if (this.transform == 1) {
         int var8 = var2 == 0 ? this.UnruhGWithInputBytes : this.UnruhGWithoutInputBytes;
         System.arraycopy(var7[(var2 + 2) % 3], 0, var1.view3UnruhG, 0, var8);
      }
   }

   private void H3(int[] var1, int[] var2, View[][] var3, byte[][][] var4, byte[] var5, byte[] var6, byte[] var7, byte[][][] var8) {
      this.digest.update((byte)1);
      byte[] var9 = new byte[this.stateSizeWords * 4];

      for (int var10 = 0; var10 < this.numMPCRounds; var10++) {
         for (int var11 = 0; var11 < 3; var11++) {
            Pack.intToLittleEndian(var3[var10][var11].outputShare, var9, 0);
            this.digest.update(var9, 0, this.stateSizeBytes);
         }
      }

      this.implH3(var1, var2, var4, var5, var6, var7, var8);
   }

   private void H3(int[] var1, int[] var2, int[][][] var3, byte[][][] var4, byte[] var5, byte[] var6, byte[] var7, byte[][][] var8) {
      this.digest.update((byte)1);
      byte[] var9 = new byte[this.stateSizeWords * 4];

      for (int var10 = 0; var10 < this.numMPCRounds; var10++) {
         for (int var11 = 0; var11 < 3; var11++) {
            Pack.intToLittleEndian(var3[var10][var11], var9, 0);
            this.digest.update(var9, 0, this.stateSizeBytes);
         }
      }

      this.implH3(var1, var2, var4, var5, var6, var7, var8);
   }

   private void implH3(int[] var1, int[] var2, byte[][][] var3, byte[] var4, byte[] var5, byte[] var6, byte[][][] var7) {
      byte[] var8 = new byte[this.digestSizeBytes];
      var4[Utils.numBytes(this.numMPCRounds * 2) - 1] = 0;

      for (int var9 = 0; var9 < this.numMPCRounds; var9++) {
         for (int var10 = 0; var10 < 3; var10++) {
            this.digest.update(var3[var9][var10], 0, this.digestSizeBytes);
         }
      }

      if (this.transform == 1) {
         for (int var15 = 0; var15 < this.numMPCRounds; var15++) {
            for (int var17 = 0; var17 < 3; var17++) {
               int var11 = var17 == 2 ? this.UnruhGWithInputBytes : this.UnruhGWithoutInputBytes;
               this.digest.update(var7[var15][var17], 0, var11);
            }
         }
      }

      this.digest.update(Pack.intToLittleEndian(var1), 0, this.stateSizeBytes);
      this.digest.update(Pack.intToLittleEndian(var2), 0, this.stateSizeBytes);
      this.digest.update(var5, 0, 32);
      this.digest.update(var6, 0, var6.length);
      this.digest.doFinal(var8, 0, this.digestSizeBytes);
      int var16 = 0;
      boolean var18 = true;

      while (var18) {
         for (int var19 = 0; var19 < this.digestSizeBytes; var19++) {
            byte var12 = var8[var19];

            for (byte var13 = 0; var13 < 8; var13 += 2) {
               int var14 = var12 >>> 6 - var13 & 3;
               if (var14 < 3) {
                  this.setChallenge(var4, var16, var14);
                  if (++var16 == this.numMPCRounds) {
                     var18 = false;
                     break;
                  }
               }
            }

            if (!var18) {
               break;
            }
         }

         if (!var18) {
            break;
         }

         this.digest.update((byte)1);
         this.digest.update(var8, 0, this.digestSizeBytes);
         this.digest.doFinal(var8, 0, this.digestSizeBytes);
      }
   }

   private void setChallenge(byte[] var1, int var2, int var3) {
      Utils.setBit(var1, 2 * var2, (byte)(var3 & 1));
      Utils.setBit(var1, 2 * var2 + 1, (byte)(var3 >>> 1 & 1));
   }

   private void G(int var1, byte[] var2, int var3, View var4, byte[] var5) {
      int var6 = this.seedSizeBytes + this.andSizeBytes;
      this.digest.update((byte)5);
      this.digest.update(var2, var3, this.seedSizeBytes);
      this.digest.doFinal(var5, 0, this.digestSizeBytes);
      this.digest.update(var5, 0, this.digestSizeBytes);
      if (var1 == 2) {
         this.digest.update(Pack.intToLittleEndian(var4.inputShare), 0, this.stateSizeBytes);
         var6 += this.stateSizeBytes;
      }

      this.digest.update(var4.communicatedBits, 0, this.andSizeBytes);
      this.digest.update(Pack.intToLittleEndian(var6), 0, 2);
      this.digest.doFinal(var5, 0, var6);
   }

   private void mpc_LowMC(Tape var1, View[] var2, int[] var3, int[] var4) {
      Arrays.fill(var4, 0, var4.length, 0);
      this.mpc_xor_constant(var4, 3 * this.stateSizeWords, var3, 0, this.stateSizeWords);
      KMatricesWithPointer var5 = this.lowmcConstants.KMatrix(this, 0);

      for (int var6 = 0; var6 < 3; var6++) {
         this.matrix_mul_offset(var4, var6 * this.stateSizeWords, var2[var6].inputShare, 0, var5.getData(), var5.getMatrixPointer());
      }

      this.mpc_xor(var4, var4, 3);

      for (int var11 = 1; var11 <= this.numRounds; var11++) {
         var5 = this.lowmcConstants.KMatrix(this, var11);

         for (int var7 = 0; var7 < 3; var7++) {
            this.matrix_mul_offset(var4, var7 * this.stateSizeWords, var2[var7].inputShare, 0, var5.getData(), var5.getMatrixPointer());
         }

         this.mpc_substitution(var4, var1, var2);
         var5 = this.lowmcConstants.LMatrix(this, var11 - 1);
         this.mpc_matrix_mul(var4, 3 * this.stateSizeWords, var4, 3 * this.stateSizeWords, var5.getData(), var5.getMatrixPointer(), 3);
         var5 = this.lowmcConstants.RConstant(this, var11 - 1);
         this.mpc_xor_constant(var4, 3 * this.stateSizeWords, var5.getData(), var5.getMatrixPointer(), this.stateSizeWords);
         this.mpc_xor(var4, var4, 3);
      }

      for (int var12 = 0; var12 < 3; var12++) {
         System.arraycopy(var4, (3 + var12) * this.stateSizeWords, var2[var12].outputShare, 0, this.stateSizeWords);
      }
   }

   private void Commit(byte[] var1, int var2, View var3, byte[] var4) {
      this.digest.update((byte)4);
      this.digest.update(var1, var2, this.seedSizeBytes);
      this.digest.doFinal(var4, 0, this.digestSizeBytes);
      this.digest.update((byte)0);
      this.digest.update(var4, 0, this.digestSizeBytes);
      this.digest.update(Pack.intToLittleEndian(var3.inputShare), 0, this.stateSizeBytes);
      this.digest.update(var3.communicatedBits, 0, this.andSizeBytes);
      this.digest.update(Pack.intToLittleEndian(var3.outputShare), 0, this.stateSizeBytes);
      this.digest.doFinal(var4, 0, this.digestSizeBytes);
   }

   private void mpc_substitution(int[] var1, Tape var2, View[] var3) {
      int[] var4 = new int[3];
      int[] var5 = new int[3];
      int[] var6 = new int[3];
      int[] var7 = new int[3];
      int[] var8 = new int[3];
      int[] var9 = new int[3];

      for (byte var11 = 0; var11 < this.numSboxes * 3; var11 += 3) {
         for (int var12 = 0; var12 < 3; var12++) {
            int var10 = (3 + var12) * this.stateSizeWords * 32;
            var4[var12] = Utils.getBitFromWordArray(var1, var10 + var11 + 2);
            var5[var12] = Utils.getBitFromWordArray(var1, var10 + var11 + 1);
            var6[var12] = Utils.getBitFromWordArray(var1, var10 + var11);
         }

         this.mpc_AND(var4, var5, var7, var2, var3);
         this.mpc_AND(var5, var6, var8, var2, var3);
         this.mpc_AND(var6, var4, var9, var2, var3);

         for (int var14 = 0; var14 < 3; var14++) {
            int var13 = (3 + var14) * this.stateSizeWords * 32;
            Utils.setBitInWordArray(var1, var13 + var11 + 2, var4[var14] ^ var8[var14]);
            Utils.setBitInWordArray(var1, var13 + var11 + 1, var4[var14] ^ var5[var14] ^ var9[var14]);
            Utils.setBitInWordArray(var1, var13 + var11, var4[var14] ^ var5[var14] ^ var6[var14] ^ var7[var14]);
         }
      }
   }

   private void mpc_AND(int[] var1, int[] var2, int[] var3, Tape var4, View[] var5) {
      byte var6 = Utils.getBit(var4.tapes[0], var4.pos);
      byte var7 = Utils.getBit(var4.tapes[1], var4.pos);
      byte var8 = Utils.getBit(var4.tapes[2], var4.pos);
      var3[0] = var1[0] & var2[1] ^ var1[1] & var2[0] ^ var1[0] & var2[0] ^ var6 ^ var7;
      var3[1] = var1[1] & var2[2] ^ var1[2] & var2[1] ^ var1[1] & var2[1] ^ var7 ^ var8;
      var3[2] = var1[2] & var2[0] ^ var1[0] & var2[2] ^ var1[2] & var2[2] ^ var8 ^ var6;
      Utils.setBit(var5[0].communicatedBits, var4.pos, (byte)var3[0]);
      Utils.setBit(var5[1].communicatedBits, var4.pos, (byte)var3[1]);
      Utils.setBit(var5[2].communicatedBits, var4.pos, (byte)var3[2]);
      var4.pos++;
   }

   private void mpc_xor(int[] var1, int[] var2, int var3) {
      int var4 = 0;

      for (int var5 = this.stateSizeWords * var3; var4 < var5; var4++) {
         var1[var3 * this.stateSizeWords + var4] = var1[var3 * this.stateSizeWords + var4] ^ var2[var4];
      }
   }

   private void mpc_matrix_mul(int[] var1, int var2, int[] var3, int var4, int[] var5, int var6, int var7) {
      for (int var8 = 0; var8 < var7; var8++) {
         this.matrix_mul_offset(var1, var2 + var8 * this.stateSizeWords, var3, var4 + var8 * this.stateSizeWords, var5, var6);
      }
   }

   private void mpc_xor_constant(int[] var1, int var2, int[] var3, int var4, int var5) {
      for (int var6 = 0; var6 < var5; var6++) {
         var1[var6 + var2] = var1[var6 + var2] ^ var3[var6 + var4];
      }
   }

   private boolean createRandomTape(byte[] var1, int var2, byte[] var3, int var4, int var5, byte[] var6, int var7) {
      if (var7 < this.digestSizeBytes) {
         return false;
      } else {
         this.digest.update((byte)2);
         this.digest.update(var1, var2, this.seedSizeBytes);
         this.digest.doFinal(var6, 0, this.digestSizeBytes);
         this.digest.update(var6, 0, this.digestSizeBytes);
         this.digest.update(var3, 0, 32);
         this.digest.update(Pack.intToLittleEndian(var4), 0, 2);
         this.digest.update(Pack.intToLittleEndian(var5), 0, 2);
         this.digest.update(Pack.intToLittleEndian(var7), 0, 2);
         this.digest.doFinal(var6, 0, var7);
         return true;
      }
   }

   private byte[] computeSeeds(int[] var1, int[] var2, int[] var3, byte[] var4) {
      byte[] var5 = new byte[this.seedSizeBytes * this.numMPCParties * this.numMPCRounds + 32];
      byte[] var6 = new byte[32];
      this.updateDigest(var1, var6);
      this.digest.update(var4, 0, var4.length);
      this.updateDigest(var2, var6);
      this.updateDigest(var3, var6);
      this.digest.update(Pack.intToLittleEndian(this.stateSizeBits), 0, 2);
      this.digest.doFinal(var5, 0, this.seedSizeBytes * this.numMPCParties * this.numMPCRounds + 32);
      return var5;
   }

   private boolean sign_picnic3(int[] var1, int[] var2, int[] var3, byte[] var4, Signature2 var5) {
      byte[] var6 = new byte[32 + this.seedSizeBytes];
      this.computeSaltAndRootSeed(var6, var1, var2, var3, var4);
      byte[] var7 = Arrays.copyOfRange(var6, 32, var6.length);
      var5.salt = Arrays.copyOfRange(var6, 0, 32);
      Tree var8 = new Tree(this, this.numMPCRounds, this.seedSizeBytes);
      var8.generateSeeds(var7, var5.salt, 0);
      byte[][] var9 = var8.getLeaves();
      int var10 = var8.getLeavesOffset();
      Tape[] var11 = new Tape[this.numMPCRounds];
      Tree[] var12 = new Tree[this.numMPCRounds];

      for (int var13 = 0; var13 < this.numMPCRounds; var13++) {
         var11[var13] = new Tape(this);
         var12[var13] = new Tree(this, this.numMPCParties, this.seedSizeBytes);
         var12[var13].generateSeeds(var9[var13 + var10], var5.salt, var13);
         this.createRandomTapes(var11[var13], var12[var13].getLeaves(), var12[var13].getLeavesOffset(), var5.salt, var13);
      }

      byte[][] var28 = new byte[this.numMPCRounds][this.stateSizeWords * 4];
      byte[] var14 = new byte[176];

      for (int var15 = 0; var15 < this.numMPCRounds; var15++) {
         var11[var15].computeAuxTape(var28[var15]);
      }

      byte[][][] var29 = new byte[this.numMPCRounds][this.numMPCParties][this.digestSizeBytes];

      for (int var16 = 0; var16 < this.numMPCRounds; var16++) {
         for (int var17 = 0; var17 < this.numMPCParties - 1; var17++) {
            this.commit(var29[var16][var17], var12[var16].getLeaf(var17), null, var5.salt, var16, var17);
         }

         int var31 = this.numMPCParties - 1;
         this.getAuxBits(var14, var11[var16]);
         this.commit(var29[var16][var31], var12[var16].getLeaf(var31), var14, var5.salt, var16, var31);
      }

      Msg[] var30 = new Msg[this.numMPCRounds];
      int[] var32 = new int[this.stateSizeBits];

      for (int var18 = 0; var18 < this.numMPCRounds; var18++) {
         var30[var18] = new Msg(this);
         int[] var19 = Pack.littleEndianToInt(var28[var18], 0, this.stateSizeWords);
         this.xor_array(var19, var19, var1, 0);
         int var20 = this.simulateOnline(var19, var11[var18], var32, var30[var18], var3, var2);
         if (var20 != 0) {
            LOG.fine("MPC simulation failed, aborting signature");
            return false;
         }

         Pack.intToLittleEndian(var19, var28[var18], 0);
      }

      byte[][] var33 = new byte[this.numMPCRounds][this.digestSizeBytes];
      byte[][] var34 = new byte[this.numMPCRounds][this.digestSizeBytes];

      for (int var35 = 0; var35 < this.numMPCRounds; var35++) {
         this.commit_h(var33[var35], var29[var35]);
         this.commit_v(var34[var35], var28[var35], var30[var35]);
      }

      Tree var36 = new Tree(this, this.numMPCRounds, this.digestSizeBytes);
      var36.buildMerkleTree(var34, var5.salt);
      var5.challengeC = new int[this.numOpenedRounds];
      var5.challengeP = new int[this.numOpenedRounds];
      var5.challengeHash = new byte[this.digestSizeBytes];
      this.HCP(var5.challengeHash, var5.challengeC, var5.challengeP, var33, var36.nodes[0], var5.salt, var2, var3, var4);
      int var21 = this.numMPCRounds - this.numOpenedRounds;
      int[] var22 = this.getMissingLeavesList(var5.challengeC);
      int[] var23 = new int[1];
      var5.cvInfo = var36.openMerkleTree(var22, var21, var23);
      var5.cvInfoLen = var23[0];
      var5.iSeedInfo = new byte[this.numMPCRounds * this.seedSizeBytes];
      var5.iSeedInfoLen = var8.revealSeeds(var5.challengeC, this.numOpenedRounds, var5.iSeedInfo, this.numMPCRounds * this.seedSizeBytes);
      var5.proofs = new Signature2.Proof2[this.numMPCRounds];

      for (int var24 = 0; var24 < this.numMPCRounds; var24++) {
         if (this.contains(var5.challengeC, this.numOpenedRounds, var24)) {
            var5.proofs[var24] = new Signature2.Proof2(this);
            int var25 = indexOf(var5.challengeC, this.numOpenedRounds, var24);
            int[] var26 = new int[]{var5.challengeP[var25]};
            var5.proofs[var24].seedInfo = new byte[this.numMPCParties * this.seedSizeBytes];
            var5.proofs[var24].seedInfoLen = var12[var24].revealSeeds(var26, 1, var5.proofs[var24].seedInfo, this.numMPCParties * this.seedSizeBytes);
            int var27 = this.numMPCParties - 1;
            if (var5.challengeP[var25] != var27) {
               this.getAuxBits(var5.proofs[var24].aux, var11[var24]);
            }

            System.arraycopy(var28[var24], 0, var5.proofs[var24].input, 0, this.stateSizeBytes);
            System.arraycopy(var30[var24].msgs[var5.challengeP[var25]], 0, var5.proofs[var24].msgs, 0, this.andSizeBytes);
            System.arraycopy(var29[var24][var5.challengeP[var25]], 0, var5.proofs[var24].C, 0, this.digestSizeBytes);
         }
      }

      return true;
   }

   static int indexOf(int[] var0, int var1, int var2) {
      for (int var3 = 0; var3 < var1; var3++) {
         if (var0[var3] == var2) {
            return var3;
         }
      }

      return -1;
   }

   private int[] getMissingLeavesList(int[] var1) {
      int var2 = this.numMPCRounds - this.numOpenedRounds;
      int[] var3 = new int[var2];
      int var4 = 0;

      for (int var5 = 0; var5 < this.numMPCRounds; var5++) {
         if (!this.contains(var1, this.numOpenedRounds, var5)) {
            var3[var4] = var5;
            var4++;
         }
      }

      return var3;
   }

   private void HCP(byte[] var1, int[] var2, int[] var3, byte[][] var4, byte[] var5, byte[] var6, int[] var7, int[] var8, byte[] var9) {
      for (int var10 = 0; var10 < this.numMPCRounds; var10++) {
         this.digest.update(var4[var10], 0, this.digestSizeBytes);
      }

      byte[] var11 = new byte[32];
      this.digest.update(var5, 0, this.digestSizeBytes);
      this.digest.update(var6, 0, 32);
      this.updateDigest(var7, var11);
      this.updateDigest(var8, var11);
      this.digest.update(var9, 0, var9.length);
      this.digest.doFinal(var1, 0, this.digestSizeBytes);
      if (var2 != null && var3 != null) {
         this.expandChallengeHash(var1, var2, var3);
      }
   }

   static int bitsToChunks(int var0, byte[] var1, int var2, int[] var3) {
      if (var0 > var2 * 8) {
         return 0;
      } else {
         int var4 = var2 * 8 / var0;

         for (int var5 = 0; var5 < var4; var5++) {
            var3[var5] = 0;

            for (int var6 = 0; var6 < var0; var6++) {
               var3[var5] += Utils.getBit(var1, var5 * var0 + var6) << var6;
            }
         }

         return var4;
      }
   }

   static int appendUnique(int[] var0, int var1, int var2) {
      if (var2 == 0) {
         var0[var2] = var1;
         return var2 + 1;
      } else {
         for (int var3 = 0; var3 < var2; var3++) {
            if (var0[var3] == var1) {
               return var2;
            }
         }

         var0[var2] = var1;
         return var2 + 1;
      }
   }

   private void expandChallengeHash(byte[] var1, int[] var2, int[] var3) {
      int var4 = Utils.ceil_log2(this.numMPCRounds);
      int var5 = Utils.ceil_log2(this.numMPCParties);
      int[] var6 = new int[this.digestSizeBytes * 8 / Math.min(var4, var5)];
      byte[] var7 = new byte[64];
      System.arraycopy(var1, 0, var7, 0, this.digestSizeBytes);
      int var8 = 0;

      while (var8 < this.numOpenedRounds) {
         int var9 = bitsToChunks(var4, var7, this.digestSizeBytes, var6);

         for (int var10 = 0; var10 < var9; var10++) {
            if (var6[var10] < this.numMPCRounds) {
               var8 = appendUnique(var2, var6[var10], var8);
            }

            if (var8 == this.numOpenedRounds) {
               break;
            }
         }

         this.digest.update((byte)1);
         this.digest.update(var7, 0, this.digestSizeBytes);
         this.digest.doFinal(var7, 0, this.digestSizeBytes);
      }

      int var12 = 0;

      while (var12 < this.numOpenedRounds) {
         int var13 = bitsToChunks(var5, var7, this.digestSizeBytes, var6);

         for (int var11 = 0; var11 < var13; var11++) {
            if (var6[var11] < this.numMPCParties) {
               var3[var12] = var6[var11];
               var12++;
            }

            if (var12 == this.numOpenedRounds) {
               break;
            }
         }

         this.digest.update((byte)1);
         this.digest.update(var7, 0, this.digestSizeBytes);
         this.digest.doFinal(var7, 0, this.digestSizeBytes);
      }
   }

   private void commit_h(byte[] var1, byte[][] var2) {
      for (int var3 = 0; var3 < this.numMPCParties; var3++) {
         this.digest.update(var2[var3], 0, this.digestSizeBytes);
      }

      this.digest.doFinal(var1, 0, this.digestSizeBytes);
   }

   private void commit_v(byte[] var1, byte[] var2, Msg var3) {
      this.digest.update(var2, 0, this.stateSizeBytes);

      for (int var4 = 0; var4 < this.numMPCParties; var4++) {
         int var5 = Utils.numBytes(var3.pos);
         this.digest.update(var3.msgs[var4], 0, var5);
      }

      this.digest.doFinal(var1, 0, this.digestSizeBytes);
   }

   private int simulateOnline(int[] var1, Tape var2, int[] var3, Msg var4, int[] var5, int[] var6) {
      byte var7 = 0;
      int[] var8 = new int[16];
      int[] var9 = new int[16];
      KMatricesWithPointer var10 = this.lowmcConstants.KMatrix(this, 0);
      this.matrix_mul(var8, var1, var10.getData(), var10.getMatrixPointer());
      this.xor_array(var9, var8, var5, 0);

      for (int var11 = 1; var11 <= this.numRounds; var11++) {
         this.tapesToWords(var3, var2);
         this.mpc_sbox(var9, var3, var2, var4);
         var10 = this.lowmcConstants.LMatrix(this, var11 - 1);
         this.matrix_mul(var9, var9, var10.getData(), var10.getMatrixPointer());
         var10 = this.lowmcConstants.RConstant(this, var11 - 1);
         this.xor_array(var9, var9, var10.getData(), var10.getMatrixPointer());
         var10 = this.lowmcConstants.KMatrix(this, var11);
         this.matrix_mul(var8, var1, var10.getData(), var10.getMatrixPointer());
         this.xor_array(var9, var8, var9, 0);
      }

      if (!subarrayEquals(var9, var6, this.stateSizeWords)) {
         var7 = -1;
      }

      return var7;
   }

   private void createRandomTapes(Tape var1, byte[][] var2, int var3, byte[] var4, int var5) {
      int var6 = 2 * this.andSizeBytes;

      for (int var7 = 0; var7 < this.numMPCParties; var7++) {
         this.digest.update(var2[var7 + var3], 0, this.seedSizeBytes);
         this.digest.update(var4, 0, 32);
         this.digest.update(Pack.intToLittleEndian(var5), 0, 2);
         this.digest.update(Pack.intToLittleEndian(var7), 0, 2);
         this.digest.doFinal(var1.tapes[var7], 0, var6);
      }
   }

   private static boolean subarrayEquals(byte[] var0, byte[] var1, int var2) {
      if (var0.length >= var2 && var1.length >= var2) {
         for (int var3 = 0; var3 < var2; var3++) {
            if (var0[var3] != var1[var3]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static boolean subarrayEquals(int[] var0, int[] var1, int var2) {
      if (var0.length >= var2 && var1.length >= var2) {
         for (int var3 = 0; var3 < var2; var3++) {
            if (var0[var3] != var1[var3]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   static int extend(int var0) {
      return ~(var0 - 1);
   }

   private void wordToMsgs(int var1, Msg var2) {
      for (int var3 = 0; var3 < this.numMPCParties; var3++) {
         int var4 = Utils.getBit(var1, var3);
         Utils.setBit(var2.msgs[var3], var2.pos, (byte)var4);
      }

      var2.pos++;
   }

   private int mpc_AND(int var1, int var2, int var3, int var4, Tape var5, Msg var6) {
      int var7 = var5.tapesToWord();
      int var8 = extend(var1) & var4 ^ extend(var2) & var3 ^ var7;
      if (var6.unopened >= 0) {
         byte var9 = Utils.getBit(var6.msgs[var6.unopened], var6.pos);
         var8 = Utils.setBit(var8, var6.unopened, var9);
      }

      this.wordToMsgs(var8, var6);
      return Utils.parity16(var8) ^ var1 & var2;
   }

   private void mpc_sbox(int[] var1, int[] var2, Tape var3, Msg var4) {
      for (byte var5 = 0; var5 < this.numSboxes * 3; var5 += 3) {
         int var6 = Utils.getBitFromWordArray(var1, var5 + 2);
         int var7 = var2[var5 + 2];
         int var8 = Utils.getBitFromWordArray(var1, var5 + 1);
         int var9 = var2[var5 + 1];
         int var10 = Utils.getBitFromWordArray(var1, var5);
         int var11 = var2[var5];
         int var12 = this.mpc_AND(var6, var8, var7, var9, var3, var4);
         int var13 = this.mpc_AND(var8, var10, var9, var11, var3, var4);
         int var14 = this.mpc_AND(var10, var6, var11, var7, var3, var4);
         int var15 = var6 ^ var13;
         int var16 = var6 ^ var8 ^ var14;
         int var17 = var6 ^ var8 ^ var10 ^ var12;
         Utils.setBitInWordArray(var1, var5 + 2, var15);
         Utils.setBitInWordArray(var1, var5 + 1, var16);
         Utils.setBitInWordArray(var1, var5, var17);
      }
   }

   protected void aux_mpc_sbox(int[] var1, int[] var2, Tape var3) {
      for (byte var4 = 0; var4 < this.numSboxes * 3; var4 += 3) {
         int var5 = Utils.getBitFromWordArray(var1, var4 + 2);
         int var6 = Utils.getBitFromWordArray(var1, var4 + 1);
         int var7 = Utils.getBitFromWordArray(var1, var4);
         int var8 = Utils.getBitFromWordArray(var2, var4 + 2);
         int var9 = Utils.getBitFromWordArray(var2, var4 + 1);
         int var10 = Utils.getBitFromWordArray(var2, var4);
         int var11 = var10 ^ var5 ^ var6 ^ var7;
         int var12 = var8 ^ var5;
         int var13 = var9 ^ var5 ^ var6;
         this.aux_mpc_AND(var5, var6, var11, var3);
         this.aux_mpc_AND(var6, var7, var12, var3);
         this.aux_mpc_AND(var7, var5, var13, var3);
      }
   }

   private void aux_mpc_AND(int var1, int var2, int var3, Tape var4) {
      int var5 = this.numMPCParties - 1;
      int var6 = var4.tapesToWord();
      var6 = Utils.parity16(var6) ^ Utils.getBit(var4.tapes[var5], var4.pos - 1);
      int var7 = var1 & var2 ^ var6 ^ var3;
      Utils.setBit(var4.tapes[var5], var4.pos - 1, (byte)(var7 & 0xFF));
   }

   private boolean contains(int[] var1, int var2, int var3) {
      for (int var4 = 0; var4 < var2; var4++) {
         if (var1[var4] == var3) {
            return true;
         }
      }

      return false;
   }

   private void tapesToWords(int[] var1, Tape var2) {
      for (int var3 = 0; var3 < this.stateSizeBits; var3++) {
         var1[var3] = var2.tapesToWord();
      }
   }

   private void getAuxBits(byte[] var1, Tape var2) {
      byte[] var3 = var2.tapes[this.numMPCParties - 1];
      int var4 = this.stateSizeBits;
      int var5 = 0;
      int var6 = 0;

      for (int var7 = 0; var7 < this.numRounds; var7++) {
         var6 += var4;

         for (int var8 = 0; var8 < var4; var8++) {
            Utils.setBit(var1, var5++, Utils.getBit(var3, var6++));
         }
      }
   }

   private void commit(byte[] var1, byte[] var2, byte[] var3, byte[] var4, int var5, int var6) {
      this.digest.update(var2, 0, this.seedSizeBytes);
      if (var3 != null) {
         this.digest.update(var3, 0, this.andSizeBytes);
      }

      this.digest.update(var4, 0, 32);
      this.digest.update(Pack.intToLittleEndian(var5), 0, 2);
      this.digest.update(Pack.intToLittleEndian(var6), 0, 2);
      this.digest.doFinal(var1, 0, this.digestSizeBytes);
   }

   private void computeSaltAndRootSeed(byte[] var1, int[] var2, int[] var3, int[] var4, byte[] var5) {
      byte[] var6 = new byte[32];
      this.updateDigest(var2, var6);
      this.digest.update(var5, 0, var5.length);
      this.updateDigest(var3, var6);
      this.updateDigest(var4, var6);
      Pack.shortToLittleEndian((short)this.stateSizeBits, var6, 0);
      this.digest.update(var6, 0, 2);
      this.digest.doFinal(var1, 0, var1.length);
   }

   private void updateDigest(int[] var1, byte[] var2) {
      Pack.intToLittleEndian(var1, var2, 0);
      this.digest.update(var2, 0, this.stateSizeBytes);
   }

   static boolean is_picnic3(int var0) {
      return var0 == 7 || var0 == 8 || var0 == 9;
   }

   public void crypto_sign_keypair(byte[] var1, byte[] var2, SecureRandom var3) {
      byte[] var4 = new byte[this.stateSizeWords * 4];
      byte[] var5 = new byte[this.stateSizeWords * 4];
      byte[] var6 = new byte[this.stateSizeWords * 4];
      this.picnic_keygen(var4, var5, var6, var3);
      this.picnic_write_public_key(var5, var4, var1);
      this.picnic_write_private_key(var6, var5, var4, var2);
   }

   private int picnic_write_private_key(byte[] var1, byte[] var2, byte[] var3, byte[] var4) {
      int var5 = 1 + 3 * this.stateSizeBytes;
      if (var4.length < var5) {
         LOG.fine("Failed writing private key!");
         return -1;
      } else {
         var4[0] = (byte)this.parameters;
         System.arraycopy(var1, 0, var4, 1, this.stateSizeBytes);
         System.arraycopy(var2, 0, var4, 1 + this.stateSizeBytes, this.stateSizeBytes);
         System.arraycopy(var3, 0, var4, 1 + 2 * this.stateSizeBytes, this.stateSizeBytes);
         return var5;
      }
   }

   private int picnic_write_public_key(byte[] var1, byte[] var2, byte[] var3) {
      int var4 = 1 + 2 * this.stateSizeBytes;
      if (var3.length < var4) {
         LOG.fine("Failed writing public key!");
         return -1;
      } else {
         var3[0] = (byte)this.parameters;
         System.arraycopy(var1, 0, var3, 1, this.stateSizeBytes);
         System.arraycopy(var2, 0, var3, 1 + this.stateSizeBytes, this.stateSizeBytes);
         return var4;
      }
   }

   private void picnic_keygen(byte[] var1, byte[] var2, byte[] var3, SecureRandom var4) {
      int[] var5 = new int[var3.length / 4];
      int[] var6 = new int[var1.length / 4];
      int[] var7 = new int[var2.length / 4];
      var4.nextBytes(var3);
      Pack.littleEndianToInt(var3, 0, var5);
      Utils.zeroTrailingBits(var5, this.stateSizeBits);
      var4.nextBytes(var1);
      Pack.littleEndianToInt(var1, 0, var6);
      Utils.zeroTrailingBits(var6, this.stateSizeBits);
      this.LowMCEnc(var6, var7, var5);
      Pack.intToLittleEndian(var5, var3, 0);
      Pack.intToLittleEndian(var6, var1, 0);
      Pack.intToLittleEndian(var7, var2, 0);
   }

   private void LowMCEnc(int[] var1, int[] var2, int[] var3) {
      int[] var4 = new int[16];
      if (var1 != var2) {
         System.arraycopy(var1, 0, var2, 0, this.stateSizeWords);
      }

      KMatricesWithPointer var5 = this.lowmcConstants.KMatrix(this, 0);
      this.matrix_mul(var4, var3, var5.getData(), var5.getMatrixPointer());
      this.xor_array(var2, var2, var4, 0);

      for (int var6 = 1; var6 <= this.numRounds; var6++) {
         var5 = this.lowmcConstants.KMatrix(this, var6);
         this.matrix_mul(var4, var3, var5.getData(), var5.getMatrixPointer());
         this.substitution(var2);
         var5 = this.lowmcConstants.LMatrix(this, var6 - 1);
         this.matrix_mul(var2, var2, var5.getData(), var5.getMatrixPointer());
         var5 = this.lowmcConstants.RConstant(this, var6 - 1);
         this.xor_array(var2, var2, var5.getData(), var5.getMatrixPointer());
         this.xor_array(var2, var2, var4, 0);
      }
   }

   private void substitution(int[] var1) {
      for (byte var2 = 0; var2 < this.numSboxes * 3; var2 += 3) {
         int var3 = Utils.getBitFromWordArray(var1, var2 + 2);
         int var4 = Utils.getBitFromWordArray(var1, var2 + 1);
         int var5 = Utils.getBitFromWordArray(var1, var2);
         Utils.setBitInWordArray(var1, var2 + 2, var3 ^ var4 & var5);
         Utils.setBitInWordArray(var1, var2 + 1, var3 ^ var4 ^ var3 & var5);
         Utils.setBitInWordArray(var1, var2, var3 ^ var4 ^ var5 ^ var3 & var4);
      }
   }

   private void xor_three(int[] var1, int[] var2, int[] var3, int[] var4) {
      for (int var5 = 0; var5 < this.stateSizeWords; var5++) {
         var1[var5] = var2[var5] ^ var3[var5] ^ var4[var5];
      }
   }

   protected void xor_array(int[] var1, int[] var2, int[] var3, int var4) {
      for (int var5 = 0; var5 < this.stateSizeWords; var5++) {
         var1[var5] = var2[var5] ^ var3[var5 + var4];
      }
   }

   protected void matrix_mul(int[] var1, int[] var2, int[] var3, int var4) {
      this.matrix_mul_offset(var1, 0, var2, 0, var3, var4);
   }

   protected void matrix_mul_offset(int[] var1, int var2, int[] var3, int var4, int[] var5, int var6) {
      int[] var8 = new int[16];
      var8[this.stateSizeWords - 1] = 0;
      int var9 = this.stateSizeBits / 32;
      int var10 = this.stateSizeWords * 32 - this.stateSizeBits;
      int var11 = -1 >>> var10;
      var11 = Bits.bitPermuteStepSimple(var11, 1431655765, 1);
      var11 = Bits.bitPermuteStepSimple(var11, 858993459, 2);
      var11 = Bits.bitPermuteStepSimple(var11, 252645135, 4);

      for (int var12 = 0; var12 < this.stateSizeBits; var12++) {
         int var7 = 0;

         for (int var13 = 0; var13 < var9; var13++) {
            int var14 = var12 * this.stateSizeWords + var13;
            var7 ^= var3[var4 + var13] & var5[var6 + var14];
         }

         if (var10 > 0) {
            int var18 = var12 * this.stateSizeWords + var9;
            var7 ^= var3[var4 + var9] & var5[var6 + var18] & var11;
         }

         Utils.setBit(var8, var12, Utils.parity32(var7));
      }

      System.arraycopy(var8, 0, var1, var2, this.stateSizeWords);
   }
}
