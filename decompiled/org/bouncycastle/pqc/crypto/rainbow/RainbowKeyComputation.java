package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;

class RainbowKeyComputation {
   private SecureRandom random;
   private Version version;
   private RainbowParameters rainbowParams;
   ComputeInField cf = new ComputeInField();
   private int v1;
   private int o1;
   private int o2;
   private byte[] sk_seed;
   private byte[] pk_seed;
   private short[][] s1;
   private short[][] t1;
   private short[][] t2;
   private short[][] t3;
   private short[][] t4;
   private short[][][] l1_F1;
   private short[][][] l1_F2;
   private short[][][] l2_F1;
   private short[][][] l2_F2;
   private short[][][] l2_F3;
   private short[][][] l2_F5;
   private short[][][] l2_F6;
   private short[][][] l1_Q1;
   private short[][][] l1_Q2;
   private short[][][] l1_Q3;
   private short[][][] l1_Q5;
   private short[][][] l1_Q6;
   private short[][][] l1_Q9;
   private short[][][] l2_Q1;
   private short[][][] l2_Q2;
   private short[][][] l2_Q3;
   private short[][][] l2_Q5;
   private short[][][] l2_Q6;
   private short[][][] l2_Q9;

   public RainbowKeyComputation(RainbowParameters var1, SecureRandom var2) {
      this.rainbowParams = var1;
      this.random = var2;
      this.version = this.rainbowParams.getVersion();
      this.v1 = this.rainbowParams.getV1();
      this.o1 = this.rainbowParams.getO1();
      this.o2 = this.rainbowParams.getO2();
   }

   public RainbowKeyComputation(RainbowParameters var1, byte[] var2, byte[] var3) {
      this.rainbowParams = var1;
      this.random = null;
      this.version = this.rainbowParams.getVersion();
      this.pk_seed = var2;
      this.sk_seed = var3;
      this.v1 = this.rainbowParams.getV1();
      this.o1 = this.rainbowParams.getO1();
      this.o2 = this.rainbowParams.getO2();
   }

   private void generate_S_and_T(SecureRandom var1) {
      this.s1 = RainbowUtil.generate_random_2d(var1, this.o1, this.o2);
      this.t1 = RainbowUtil.generate_random_2d(var1, this.v1, this.o1);
      this.t2 = RainbowUtil.generate_random_2d(var1, this.v1, this.o2);
      this.t3 = RainbowUtil.generate_random_2d(var1, this.o1, this.o2);
   }

   private void generate_B1_and_B2(SecureRandom var1) {
      this.l1_Q1 = RainbowUtil.generate_random(var1, this.o1, this.v1, this.v1, true);
      this.l1_Q2 = RainbowUtil.generate_random(var1, this.o1, this.v1, this.o1, false);
      this.l2_Q1 = RainbowUtil.generate_random(var1, this.o2, this.v1, this.v1, true);
      this.l2_Q2 = RainbowUtil.generate_random(var1, this.o2, this.v1, this.o1, false);
      this.l2_Q3 = RainbowUtil.generate_random(var1, this.o2, this.v1, this.o2, false);
      this.l2_Q5 = RainbowUtil.generate_random(var1, this.o2, this.o1, this.o1, true);
      this.l2_Q6 = RainbowUtil.generate_random(var1, this.o2, this.o1, this.o2, false);
   }

   private void calculate_t4() {
      short[][] var1 = this.cf.multiplyMatrix(this.t1, this.t3);
      this.t4 = this.cf.addMatrix(var1, this.t2);
   }

   private void calculate_F_from_Q() {
      this.l1_F1 = RainbowUtil.cloneArray(this.l1_Q1);
      this.l1_F2 = new short[this.o1][][];

      for (int var1 = 0; var1 < this.o1; var1++) {
         this.l1_F2[var1] = this.cf.addMatrixTranspose(this.l1_Q1[var1]);
         this.l1_F2[var1] = this.cf.multiplyMatrix(this.l1_F2[var1], this.t1);
         this.l1_F2[var1] = this.cf.addMatrix(this.l1_F2[var1], this.l1_Q2[var1]);
      }

      this.l2_F2 = new short[this.o2][][];
      this.l2_F3 = new short[this.o2][][];
      this.l2_F5 = new short[this.o2][][];
      this.l2_F6 = new short[this.o2][][];
      this.l2_F1 = RainbowUtil.cloneArray(this.l2_Q1);

      for (int var5 = 0; var5 < this.o2; var5++) {
         short[][] var2 = this.cf.addMatrixTranspose(this.l2_Q1[var5]);
         this.l2_F2[var5] = this.cf.multiplyMatrix(var2, this.t1);
         this.l2_F2[var5] = this.cf.addMatrix(this.l2_F2[var5], this.l2_Q2[var5]);
         this.l2_F3[var5] = this.cf.multiplyMatrix(var2, this.t4);
         short[][] var3 = this.cf.multiplyMatrix(this.l2_Q2[var5], this.t3);
         this.l2_F3[var5] = this.cf.addMatrix(this.l2_F3[var5], var3);
         this.l2_F3[var5] = this.cf.addMatrix(this.l2_F3[var5], this.l2_Q3[var5]);
         var3 = this.cf.multiplyMatrix(this.l2_Q1[var5], this.t1);
         var3 = this.cf.addMatrix(var3, this.l2_Q2[var5]);
         short[][] var4 = this.cf.transpose(this.t1);
         this.l2_F5[var5] = this.cf.multiplyMatrix(var4, var3);
         this.l2_F5[var5] = this.cf.addMatrix(this.l2_F5[var5], this.l2_Q5[var5]);
         this.l2_F5[var5] = this.cf.to_UT(this.l2_F5[var5]);
         this.l2_F6[var5] = this.cf.multiplyMatrix(var4, this.l2_F3[var5]);
         var3 = this.cf.multiplyMatrix(this.cf.transpose(this.l2_Q2[var5]), this.t4);
         this.l2_F6[var5] = this.cf.addMatrix(this.l2_F6[var5], var3);
         var3 = this.cf.addMatrixTranspose(this.l2_Q5[var5]);
         var3 = this.cf.multiplyMatrix(var3, this.t3);
         this.l2_F6[var5] = this.cf.addMatrix(this.l2_F6[var5], var3);
         this.l2_F6[var5] = this.cf.addMatrix(this.l2_F6[var5], this.l2_Q6[var5]);
      }
   }

   private void calculate_Q_from_F() {
      short[][] var1 = this.cf.transpose(this.t1);
      short[][] var2 = this.cf.transpose(this.t2);
      this.l1_Q1 = RainbowUtil.cloneArray(this.l1_F1);
      this.l1_Q2 = new short[this.o1][][];

      for (int var3 = 0; var3 < this.o1; var3++) {
         this.l1_Q2[var3] = this.cf.addMatrixTranspose(this.l1_F1[var3]);
         this.l1_Q2[var3] = this.cf.multiplyMatrix(this.l1_Q2[var3], this.t1);
         this.l1_Q2[var3] = this.cf.addMatrix(this.l1_Q2[var3], this.l1_F2[var3]);
      }

      this.calculate_l1_Q3569(var1, var2);
      this.l2_Q2 = new short[this.o2][][];
      this.l2_Q3 = new short[this.o2][][];
      this.l2_Q5 = new short[this.o2][][];
      this.l2_Q6 = new short[this.o2][][];
      this.l2_Q1 = RainbowUtil.cloneArray(this.l2_F1);

      for (int var5 = 0; var5 < this.o2; var5++) {
         short[][] var6 = this.cf.addMatrixTranspose(this.l2_F1[var5]);
         this.l2_Q2[var5] = this.cf.multiplyMatrix(var6, this.t1);
         this.l2_Q2[var5] = this.cf.addMatrix(this.l2_Q2[var5], this.l2_F2[var5]);
         this.l2_Q3[var5] = this.cf.multiplyMatrix(var6, this.t2);
         short[][] var4 = this.cf.multiplyMatrix(this.l2_F2[var5], this.t3);
         this.l2_Q3[var5] = this.cf.addMatrix(this.l2_Q3[var5], var4);
         this.l2_Q3[var5] = this.cf.addMatrix(this.l2_Q3[var5], this.l2_F3[var5]);
         var4 = this.cf.multiplyMatrix(this.l2_F1[var5], this.t1);
         var4 = this.cf.addMatrix(var4, this.l2_F2[var5]);
         this.l2_Q5[var5] = this.cf.multiplyMatrix(var1, var4);
         this.l2_Q5[var5] = this.cf.addMatrix(this.l2_Q5[var5], this.l2_F5[var5]);
         this.l2_Q5[var5] = this.cf.to_UT(this.l2_Q5[var5]);
         this.l2_Q6[var5] = this.cf.multiplyMatrix(var1, this.l2_Q3[var5]);
         var4 = this.cf.multiplyMatrix(this.cf.transpose(this.l2_F2[var5]), this.t2);
         this.l2_Q6[var5] = this.cf.addMatrix(this.l2_Q6[var5], var4);
         var4 = this.cf.addMatrixTranspose(this.l2_F5[var5]);
         var4 = this.cf.multiplyMatrix(var4, this.t3);
         this.l2_Q6[var5] = this.cf.addMatrix(this.l2_Q6[var5], var4);
         this.l2_Q6[var5] = this.cf.addMatrix(this.l2_Q6[var5], this.l2_F6[var5]);
      }

      this.calculate_l2_Q9(var2);
   }

   private void calculate_Q_from_F_cyclic() {
      short[][] var1 = this.cf.transpose(this.t1);
      short[][] var2 = this.cf.transpose(this.t2);
      this.calculate_l1_Q3569(var1, var2);
      this.calculate_l2_Q9(var2);
   }

   private void calculate_l1_Q3569(short[][] var1, short[][] var2) {
      this.l1_Q3 = new short[this.o1][][];
      this.l1_Q5 = new short[this.o1][][];
      this.l1_Q6 = new short[this.o1][][];
      this.l1_Q9 = new short[this.o1][][];

      for (int var5 = 0; var5 < this.o1; var5++) {
         short[][] var3 = this.cf.multiplyMatrix(this.l1_F2[var5], this.t3);
         this.l1_Q3[var5] = this.cf.addMatrixTranspose(this.l1_F1[var5]);
         this.l1_Q3[var5] = this.cf.multiplyMatrix(this.l1_Q3[var5], this.t2);
         this.l1_Q3[var5] = this.cf.addMatrix(this.l1_Q3[var5], var3);
         this.l1_Q5[var5] = this.cf.multiplyMatrix(this.l1_F1[var5], this.t1);
         this.l1_Q5[var5] = this.cf.addMatrix(this.l1_Q5[var5], this.l1_F2[var5]);
         this.l1_Q5[var5] = this.cf.multiplyMatrix(var1, this.l1_Q5[var5]);
         this.l1_Q5[var5] = this.cf.to_UT(this.l1_Q5[var5]);
         short[][] var4 = this.cf.multiplyMatrix(this.cf.transpose(this.l1_F2[var5]), this.t2);
         this.l1_Q6[var5] = this.cf.multiplyMatrix(var1, this.l1_Q3[var5]);
         this.l1_Q6[var5] = this.cf.addMatrix(this.l1_Q6[var5], var4);
         var4 = this.cf.multiplyMatrix(this.l1_F1[var5], this.t2);
         this.l1_Q9[var5] = this.cf.addMatrix(var4, var3);
         this.l1_Q9[var5] = this.cf.multiplyMatrix(var2, this.l1_Q9[var5]);
         this.l1_Q9[var5] = this.cf.to_UT(this.l1_Q9[var5]);
      }
   }

   private void calculate_l2_Q9(short[][] var1) {
      this.l2_Q9 = new short[this.o2][][];

      for (int var3 = 0; var3 < this.o2; var3++) {
         this.l2_Q9[var3] = this.cf.multiplyMatrix(this.l2_F1[var3], this.t2);
         short[][] var2 = this.cf.multiplyMatrix(this.l2_F2[var3], this.t3);
         this.l2_Q9[var3] = this.cf.addMatrix(this.l2_Q9[var3], var2);
         this.l2_Q9[var3] = this.cf.addMatrix(this.l2_Q9[var3], this.l2_F3[var3]);
         this.l2_Q9[var3] = this.cf.multiplyMatrix(var1, this.l2_Q9[var3]);
         var2 = this.cf.multiplyMatrix(this.l2_F5[var3], this.t3);
         var2 = this.cf.addMatrix(var2, this.l2_F6[var3]);
         var2 = this.cf.multiplyMatrix(this.cf.transpose(this.t3), var2);
         this.l2_Q9[var3] = this.cf.addMatrix(this.l2_Q9[var3], var2);
         this.l2_Q9[var3] = this.cf.to_UT(this.l2_Q9[var3]);
      }
   }

   private void genKeyMaterial() {
      this.sk_seed = new byte[this.rainbowParams.getLen_skseed()];
      this.random.nextBytes(this.sk_seed);
      RainbowDRBG var1 = new RainbowDRBG(this.sk_seed, this.rainbowParams.getHash_algo());
      this.generate_S_and_T(var1);
      this.l1_F1 = RainbowUtil.generate_random(var1, this.o1, this.v1, this.v1, true);
      this.l1_F2 = RainbowUtil.generate_random(var1, this.o1, this.v1, this.o1, false);
      this.l2_F1 = RainbowUtil.generate_random(var1, this.o2, this.v1, this.v1, true);
      this.l2_F2 = RainbowUtil.generate_random(var1, this.o2, this.v1, this.o1, false);
      this.l2_F3 = RainbowUtil.generate_random(var1, this.o2, this.v1, this.o2, false);
      this.l2_F5 = RainbowUtil.generate_random(var1, this.o2, this.o1, this.o1, true);
      this.l2_F6 = RainbowUtil.generate_random(var1, this.o2, this.o1, this.o2, false);
      this.calculate_Q_from_F();
      this.calculate_t4();
      this.l1_Q1 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q1, this.l1_Q1);
      this.l1_Q2 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q2, this.l1_Q2);
      this.l1_Q3 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q3, this.l1_Q3);
      this.l1_Q5 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q5, this.l1_Q5);
      this.l1_Q6 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q6, this.l1_Q6);
      this.l1_Q9 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q9, this.l1_Q9);
   }

   private void genPrivateKeyMaterial_cyclic() {
      RainbowDRBG var1 = new RainbowDRBG(this.sk_seed, this.rainbowParams.getHash_algo());
      RainbowDRBG var2 = new RainbowDRBG(this.pk_seed, this.rainbowParams.getHash_algo());
      this.generate_S_and_T(var1);
      this.calculate_t4();
      this.generate_B1_and_B2(var2);
      this.l1_Q1 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q1, this.l1_Q1);
      this.l1_Q2 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q2, this.l1_Q2);
      this.calculate_F_from_Q();
   }

   private void genKeyMaterial_cyclic() {
      this.sk_seed = new byte[this.rainbowParams.getLen_skseed()];
      this.random.nextBytes(this.sk_seed);
      this.pk_seed = new byte[this.rainbowParams.getLen_pkseed()];
      this.random.nextBytes(this.pk_seed);
      this.genPrivateKeyMaterial_cyclic();
      this.calculate_Q_from_F_cyclic();
      this.l1_Q3 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q3, this.l1_Q3);
      this.l1_Q5 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q5, this.l1_Q5);
      this.l1_Q6 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q6, this.l1_Q6);
      this.l1_Q9 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q9, this.l1_Q9);
   }

   public AsymmetricCipherKeyPair genKeyPairClassical() {
      this.genKeyMaterial();
      RainbowPublicKeyParameters var1 = new RainbowPublicKeyParameters(
         this.rainbowParams,
         this.l1_Q1,
         this.l1_Q2,
         this.l1_Q3,
         this.l1_Q5,
         this.l1_Q6,
         this.l1_Q9,
         this.l2_Q1,
         this.l2_Q2,
         this.l2_Q3,
         this.l2_Q5,
         this.l2_Q6,
         this.l2_Q9
      );
      RainbowPrivateKeyParameters var2 = new RainbowPrivateKeyParameters(
         this.rainbowParams,
         this.sk_seed,
         this.s1,
         this.t1,
         this.t3,
         this.t4,
         this.l1_F1,
         this.l1_F2,
         this.l2_F1,
         this.l2_F2,
         this.l2_F3,
         this.l2_F5,
         this.l2_F6,
         var1.getEncoded()
      );
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var1, (AsymmetricKeyParameter)var2);
   }

   public AsymmetricCipherKeyPair genKeyPairCircumzenithal() {
      this.genKeyMaterial_cyclic();
      RainbowPublicKeyParameters var1 = new RainbowPublicKeyParameters(
         this.rainbowParams, this.pk_seed, this.l1_Q3, this.l1_Q5, this.l1_Q6, this.l1_Q9, this.l2_Q9
      );
      RainbowPrivateKeyParameters var2 = new RainbowPrivateKeyParameters(
         this.rainbowParams,
         this.sk_seed,
         this.s1,
         this.t1,
         this.t3,
         this.t4,
         this.l1_F1,
         this.l1_F2,
         this.l2_F1,
         this.l2_F2,
         this.l2_F3,
         this.l2_F5,
         this.l2_F6,
         var1.getEncoded()
      );
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var1, (AsymmetricKeyParameter)var2);
   }

   public AsymmetricCipherKeyPair genKeyPairCompressed() {
      this.genKeyMaterial_cyclic();
      RainbowPublicKeyParameters var1 = new RainbowPublicKeyParameters(
         this.rainbowParams, this.pk_seed, this.l1_Q3, this.l1_Q5, this.l1_Q6, this.l1_Q9, this.l2_Q9
      );
      RainbowPrivateKeyParameters var2 = new RainbowPrivateKeyParameters(this.rainbowParams, this.pk_seed, this.sk_seed, var1.getEncoded());
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var1, (AsymmetricKeyParameter)var2);
   }

   RainbowPrivateKeyParameters generatePrivateKey() {
      this.sk_seed = Arrays.clone(this.sk_seed);
      this.pk_seed = Arrays.clone(this.pk_seed);
      this.genPrivateKeyMaterial_cyclic();
      return new RainbowPrivateKeyParameters(
         this.rainbowParams,
         this.sk_seed,
         this.s1,
         this.t1,
         this.t3,
         this.t4,
         this.l1_F1,
         this.l1_F2,
         this.l2_F1,
         this.l2_F2,
         this.l2_F3,
         this.l2_F5,
         this.l2_F6,
         null
      );
   }
}
