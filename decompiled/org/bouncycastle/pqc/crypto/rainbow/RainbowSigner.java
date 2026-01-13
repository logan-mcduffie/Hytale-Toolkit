package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.util.Arrays;

public class RainbowSigner implements MessageSigner {
   private static final int MAXITS = 65536;
   private SecureRandom random;
   int signableDocumentLength;
   private ComputeInField cf = new ComputeInField();
   private RainbowKeyParameters key;
   private Digest hashAlgo;
   private Version version;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var1) {
         RainbowKeyParameters var3;
         if (var2 instanceof ParametersWithRandom) {
            ParametersWithRandom var4 = (ParametersWithRandom)var2;
            this.random = var4.getRandom();
            var3 = (RainbowKeyParameters)var4.getParameters();
         } else {
            var3 = (RainbowKeyParameters)var2;
            SecureRandom var6 = CryptoServicesRegistrar.getSecureRandom();
            byte[] var5 = new byte[var3.getParameters().getLen_skseed()];
            var6.nextBytes(var5);
            this.random = new RainbowDRBG(var5, var3.getParameters().getHash_algo());
         }

         this.version = var3.getParameters().getVersion();
         this.key = var3;
      } else {
         this.key = (RainbowKeyParameters)var2;
         this.version = this.key.getParameters().getVersion();
      }

      this.signableDocumentLength = this.key.getDocLength();
      this.hashAlgo = this.key.getParameters().getHash_algo();
   }

   private byte[] genSignature(byte[] var1) {
      byte[] var2 = new byte[this.hashAlgo.getDigestSize()];
      this.hashAlgo.update(var1, 0, var1.length);
      this.hashAlgo.doFinal(var2, 0);
      int var3 = this.key.getParameters().getV1();
      int var4 = this.key.getParameters().getO1();
      int var5 = this.key.getParameters().getO2();
      int var6 = this.key.getParameters().getM();
      int var7 = this.key.getParameters().getN();
      RainbowPrivateKeyParameters var8 = (RainbowPrivateKeyParameters)this.key;
      byte[] var9 = RainbowUtil.hash(this.hashAlgo, var8.sk_seed, var2, new byte[this.hashAlgo.getDigestSize()]);
      this.random = new RainbowDRBG(var9, var8.getParameters().getHash_algo());
      short[] var10 = new short[var3];
      short[][] var11 = null;
      short[] var13 = new short[var4];
      short[] var14 = new short[var5];
      short[] var15 = new short[var5];
      short[][] var16 = new short[var5][var4];
      short[][] var17 = new short[var5][var5];
      byte[] var18 = new byte[var8.getParameters().getLen_salt()];
      short[] var21 = new short[var6];
      short[] var22 = new short[var4];
      short[] var23 = null;

      int var28;
      for (var28 = 0; var11 == null && var28 < 65536; var28++) {
         byte[] var25 = new byte[var3];
         this.random.nextBytes(var25);

         for (int var29 = 0; var29 < var3; var29++) {
            var10[var29] = (short)(var25[var29] & 255);
         }

         var11 = new short[var4][var4];

         for (int var51 = 0; var51 < var3; var51++) {
            for (int var30 = 0; var30 < var4; var30++) {
               for (int var31 = 0; var31 < var4; var31++) {
                  short var26 = GF2Field.multElem(var8.l1_F2[var30][var51][var31], var10[var51]);
                  var11[var30][var31] = GF2Field.addElem(var11[var30][var31], var26);
               }
            }
         }

         var11 = this.cf.inverse(var11);
      }

      for (int var52 = 0; var52 < var4; var52++) {
         var13[var52] = this.cf.multiplyMatrix_quad(var8.l1_F1[var52], var10);
      }

      for (int var53 = 0; var53 < var3; var53++) {
         for (int var55 = 0; var55 < var5; var55++) {
            var14[var55] = this.cf.multiplyMatrix_quad(var8.l2_F1[var55], var10);

            for (int var59 = 0; var59 < var4; var59++) {
               short var38 = GF2Field.multElem(var8.l2_F2[var55][var53][var59], var10[var53]);
               var16[var55][var59] = GF2Field.addElem(var16[var55][var59], var38);
            }

            for (int var60 = 0; var60 < var5; var60++) {
               short var39 = GF2Field.multElem(var8.l2_F3[var55][var53][var60], var10[var53]);
               var17[var55][var60] = GF2Field.addElem(var17[var55][var60], var39);
            }
         }
      }

      for (byte[] var54 = new byte[var6]; var23 == null && var28 < 65536; var28++) {
         short[][] var12 = new short[var5][var5];
         this.random.nextBytes(var18);
         byte[] var19 = RainbowUtil.hash(this.hashAlgo, var2, var18, var54);
         short[] var20 = this.makeMessageRepresentative(var19);
         short[] var27 = this.cf.multiplyMatrix(var8.s1, Arrays.copyOfRange(var20, var4, var6));
         var27 = this.cf.addVect(Arrays.copyOf(var20, var4), var27);
         System.arraycopy(var27, 0, var21, 0, var4);
         System.arraycopy(var20, var4, var21, var4, var5);
         var27 = this.cf.addVect(var13, Arrays.copyOf(var21, var4));
         var22 = this.cf.multiplyMatrix(var11, var27);
         var27 = this.cf.multiplyMatrix(var16, var22);

         for (int var56 = 0; var56 < var5; var56++) {
            var15[var56] = this.cf.multiplyMatrix_quad(var8.l2_F5[var56], var22);
         }

         var27 = this.cf.addVect(var27, var15);
         var27 = this.cf.addVect(var27, var14);
         var27 = this.cf.addVect(var27, Arrays.copyOfRange(var21, var4, var6));

         for (int var57 = 0; var57 < var4; var57++) {
            for (int var61 = 0; var61 < var5; var61++) {
               for (int var32 = 0; var32 < var5; var32++) {
                  short var40 = GF2Field.multElem(var8.l2_F6[var61][var57][var32], var22[var57]);
                  var12[var61][var32] = GF2Field.addElem(var12[var61][var32], var40);
               }
            }
         }

         var12 = this.cf.addMatrix(var12, var17);
         var23 = this.cf.solveEquation(var12, var27);
      }

      var23 = var23 == null ? new short[var5] : var23;
      short[] var47 = this.cf.multiplyMatrix(var8.t1, var22);
      short[] var24 = this.cf.addVect(var10, var47);
      var47 = this.cf.multiplyMatrix(var8.t4, var23);
      var24 = this.cf.addVect(var24, var47);
      var47 = this.cf.multiplyMatrix(var8.t3, var23);
      var47 = this.cf.addVect(var22, var47);
      var24 = Arrays.copyOf(var24, var7);
      System.arraycopy(var47, 0, var24, var3, var4);
      System.arraycopy(var23, 0, var24, var4 + var3, var5);
      if (var28 == 65536) {
         throw new IllegalStateException("unable to generate signature - LES not solvable");
      } else {
         byte[] var58 = RainbowUtil.convertArray(var24);
         return Arrays.concatenate(var58, var18);
      }
   }

   @Override
   public byte[] generateSignature(byte[] var1) {
      return this.genSignature(var1);
   }

   @Override
   public boolean verifySignature(byte[] var1, byte[] var2) {
      byte[] var3 = new byte[this.hashAlgo.getDigestSize()];
      this.hashAlgo.update(var1, 0, var1.length);
      this.hashAlgo.doFinal(var3, 0);
      int var4 = this.key.getParameters().getM();
      int var5 = this.key.getParameters().getN();
      RainbowPublicMap var6 = new RainbowPublicMap(this.key.getParameters());
      byte[] var7 = Arrays.copyOfRange(var2, var5, var2.length);
      byte[] var8 = RainbowUtil.hash(this.hashAlgo, var3, var7, new byte[var4]);
      short[] var9 = this.makeMessageRepresentative(var8);
      byte[] var10 = Arrays.copyOfRange(var2, 0, var5);
      short[] var11 = RainbowUtil.convertArray(var10);
      short[] var12;
      switch (this.version) {
         case CLASSIC:
            RainbowPublicKeyParameters var13 = (RainbowPublicKeyParameters)this.key;
            var12 = var6.publicMap(var13, var11);
            break;
         case CIRCUMZENITHAL:
         case COMPRESSED:
            RainbowPublicKeyParameters var14 = (RainbowPublicKeyParameters)this.key;
            var12 = var6.publicMap_cyclic(var14, var11);
            break;
         default:
            throw new IllegalArgumentException("No valid version. Please choose one of the following: classic, circumzenithal, compressed");
      }

      return RainbowUtil.equals(var9, var12);
   }

   private short[] makeMessageRepresentative(byte[] var1) {
      short[] var2 = new short[this.signableDocumentLength];
      int var3 = 0;
      int var4 = 0;

      while (var4 < var1.length) {
         var2[var4] = (short)(var1[var3] & 255);
         var3++;
         if (++var4 >= var2.length) {
            break;
         }
      }

      return var2;
   }
}
