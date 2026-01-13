package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.util.Arrays;

public class RainbowPrivateKeyParameters extends RainbowKeyParameters {
   final byte[] sk_seed;
   final short[][] s1;
   final short[][] t1;
   final short[][] t3;
   final short[][] t4;
   final short[][][] l1_F1;
   final short[][][] l1_F2;
   final short[][][] l2_F1;
   final short[][][] l2_F2;
   final short[][][] l2_F3;
   final short[][][] l2_F5;
   final short[][][] l2_F6;
   private final byte[] pk_seed;
   private byte[] pk_encoded;

   RainbowPrivateKeyParameters(
      RainbowParameters var1,
      byte[] var2,
      short[][] var3,
      short[][] var4,
      short[][] var5,
      short[][] var6,
      short[][][] var7,
      short[][][] var8,
      short[][][] var9,
      short[][][] var10,
      short[][][] var11,
      short[][][] var12,
      short[][][] var13,
      byte[] var14
   ) {
      super(true, var1);
      this.pk_seed = null;
      this.pk_encoded = var14;
      this.sk_seed = (byte[])var2.clone();
      this.s1 = RainbowUtil.cloneArray(var3);
      this.t1 = RainbowUtil.cloneArray(var4);
      this.t3 = RainbowUtil.cloneArray(var5);
      this.t4 = RainbowUtil.cloneArray(var6);
      this.l1_F1 = RainbowUtil.cloneArray(var7);
      this.l1_F2 = RainbowUtil.cloneArray(var8);
      this.l2_F1 = RainbowUtil.cloneArray(var9);
      this.l2_F2 = RainbowUtil.cloneArray(var10);
      this.l2_F3 = RainbowUtil.cloneArray(var11);
      this.l2_F5 = RainbowUtil.cloneArray(var12);
      this.l2_F6 = RainbowUtil.cloneArray(var13);
   }

   RainbowPrivateKeyParameters(RainbowParameters var1, byte[] var2, byte[] var3, byte[] var4) {
      super(true, var1);
      RainbowPrivateKeyParameters var5 = new RainbowKeyComputation(var1, var2, var3).generatePrivateKey();
      this.pk_seed = var2;
      this.pk_encoded = var4;
      this.sk_seed = var3;
      this.s1 = var5.s1;
      this.t1 = var5.t1;
      this.t3 = var5.t3;
      this.t4 = var5.t4;
      this.l1_F1 = var5.l1_F1;
      this.l1_F2 = var5.l1_F2;
      this.l2_F1 = var5.l2_F1;
      this.l2_F2 = var5.l2_F2;
      this.l2_F3 = var5.l2_F3;
      this.l2_F5 = var5.l2_F5;
      this.l2_F6 = var5.l2_F6;
   }

   public RainbowPrivateKeyParameters(RainbowParameters var1, byte[] var2) {
      super(true, var1);
      if (var1.getVersion() == Version.COMPRESSED) {
         this.pk_seed = Arrays.copyOfRange(var2, 0, var1.getLen_pkseed());
         this.sk_seed = Arrays.copyOfRange(var2, var1.getLen_pkseed(), var1.getLen_pkseed() + var1.getLen_skseed());
         RainbowPrivateKeyParameters var3 = new RainbowKeyComputation(var1, this.pk_seed, this.sk_seed).generatePrivateKey();
         this.pk_encoded = var3.pk_encoded;
         this.s1 = var3.s1;
         this.t1 = var3.t1;
         this.t3 = var3.t3;
         this.t4 = var3.t4;
         this.l1_F1 = var3.l1_F1;
         this.l1_F2 = var3.l1_F2;
         this.l2_F1 = var3.l2_F1;
         this.l2_F2 = var3.l2_F2;
         this.l2_F3 = var3.l2_F3;
         this.l2_F5 = var3.l2_F5;
         this.l2_F6 = var3.l2_F6;
      } else {
         int var7 = var1.getV1();
         int var4 = var1.getO1();
         int var5 = var1.getO2();
         this.s1 = new short[var4][var5];
         this.t1 = new short[var7][var4];
         this.t4 = new short[var7][var5];
         this.t3 = new short[var4][var5];
         this.l1_F1 = new short[var4][var7][var7];
         this.l1_F2 = new short[var4][var7][var4];
         this.l2_F1 = new short[var5][var7][var7];
         this.l2_F2 = new short[var5][var7][var4];
         this.l2_F3 = new short[var5][var7][var5];
         this.l2_F5 = new short[var5][var4][var4];
         this.l2_F6 = new short[var5][var4][var5];
         int var6 = 0;
         this.pk_seed = null;
         this.sk_seed = Arrays.copyOfRange(var2, var6, var1.getLen_skseed());
         var6 += this.sk_seed.length;
         var6 += RainbowUtil.loadEncoded(this.s1, var2, var6);
         var6 += RainbowUtil.loadEncoded(this.t1, var2, var6);
         var6 += RainbowUtil.loadEncoded(this.t4, var2, var6);
         var6 += RainbowUtil.loadEncoded(this.t3, var2, var6);
         var6 += RainbowUtil.loadEncoded(this.l1_F1, var2, var6, true);
         var6 += RainbowUtil.loadEncoded(this.l1_F2, var2, var6, false);
         var6 += RainbowUtil.loadEncoded(this.l2_F1, var2, var6, true);
         var6 += RainbowUtil.loadEncoded(this.l2_F2, var2, var6, false);
         var6 += RainbowUtil.loadEncoded(this.l2_F3, var2, var6, false);
         var6 += RainbowUtil.loadEncoded(this.l2_F5, var2, var6, true);
         var6 += RainbowUtil.loadEncoded(this.l2_F6, var2, var6, false);
         this.pk_encoded = Arrays.copyOfRange(var2, var6, var2.length);
      }
   }

   byte[] getSk_seed() {
      return Arrays.clone(this.sk_seed);
   }

   short[][] getS1() {
      return RainbowUtil.cloneArray(this.s1);
   }

   short[][] getT1() {
      return RainbowUtil.cloneArray(this.t1);
   }

   short[][] getT4() {
      return RainbowUtil.cloneArray(this.t4);
   }

   short[][] getT3() {
      return RainbowUtil.cloneArray(this.t3);
   }

   short[][][] getL1_F1() {
      return RainbowUtil.cloneArray(this.l1_F1);
   }

   short[][][] getL1_F2() {
      return RainbowUtil.cloneArray(this.l1_F2);
   }

   short[][][] getL2_F1() {
      return RainbowUtil.cloneArray(this.l2_F1);
   }

   short[][][] getL2_F2() {
      return RainbowUtil.cloneArray(this.l2_F2);
   }

   short[][][] getL2_F3() {
      return RainbowUtil.cloneArray(this.l2_F3);
   }

   short[][][] getL2_F5() {
      return RainbowUtil.cloneArray(this.l2_F5);
   }

   short[][][] getL2_F6() {
      return RainbowUtil.cloneArray(this.l2_F6);
   }

   public byte[] getPrivateKey() {
      if (this.getParameters().getVersion() == Version.COMPRESSED) {
         return Arrays.concatenate(this.pk_seed, this.sk_seed);
      } else {
         byte[] var1 = this.sk_seed;
         var1 = Arrays.concatenate(var1, RainbowUtil.getEncoded(this.s1));
         var1 = Arrays.concatenate(var1, RainbowUtil.getEncoded(this.t1));
         var1 = Arrays.concatenate(var1, RainbowUtil.getEncoded(this.t4));
         var1 = Arrays.concatenate(var1, RainbowUtil.getEncoded(this.t3));
         var1 = Arrays.concatenate(var1, RainbowUtil.getEncoded(this.l1_F1, true));
         var1 = Arrays.concatenate(var1, RainbowUtil.getEncoded(this.l1_F2, false));
         var1 = Arrays.concatenate(var1, RainbowUtil.getEncoded(this.l2_F1, true));
         var1 = Arrays.concatenate(var1, RainbowUtil.getEncoded(this.l2_F2, false));
         var1 = Arrays.concatenate(var1, RainbowUtil.getEncoded(this.l2_F3, false));
         var1 = Arrays.concatenate(var1, RainbowUtil.getEncoded(this.l2_F5, true));
         return Arrays.concatenate(var1, RainbowUtil.getEncoded(this.l2_F6, false));
      }
   }

   public byte[] getEncoded() {
      return this.getParameters().getVersion() == Version.COMPRESSED
         ? Arrays.concatenate(this.pk_seed, this.sk_seed)
         : Arrays.concatenate(this.getPrivateKey(), this.pk_encoded);
   }

   public byte[] getPublicKey() {
      return this.pk_encoded;
   }
}
