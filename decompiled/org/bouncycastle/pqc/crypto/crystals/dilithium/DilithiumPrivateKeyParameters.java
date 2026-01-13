package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.util.Arrays;

public class DilithiumPrivateKeyParameters extends DilithiumKeyParameters {
   final byte[] rho;
   final byte[] k;
   final byte[] tr;
   final byte[] s1;
   final byte[] s2;
   final byte[] t0;
   private final byte[] t1;

   public DilithiumPrivateKeyParameters(DilithiumParameters var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7, byte[] var8) {
      super(true, var1);
      this.rho = Arrays.clone(var2);
      this.k = Arrays.clone(var3);
      this.tr = Arrays.clone(var4);
      this.s1 = Arrays.clone(var5);
      this.s2 = Arrays.clone(var6);
      this.t0 = Arrays.clone(var7);
      this.t1 = Arrays.clone(var8);
   }

   public DilithiumPrivateKeyParameters(DilithiumParameters var1, byte[] var2, DilithiumPublicKeyParameters var3) {
      super(true, var1);
      DilithiumEngine var4 = var1.getEngine(null);
      int var5 = 0;
      this.rho = Arrays.copyOfRange(var2, 0, 32);
      var5 += 32;
      this.k = Arrays.copyOfRange(var2, var5, var5 + 32);
      var5 += 32;
      this.tr = Arrays.copyOfRange(var2, var5, var5 + 64);
      var5 += 64;
      int var6 = var4.getDilithiumL() * var4.getDilithiumPolyEtaPackedBytes();
      this.s1 = Arrays.copyOfRange(var2, var5, var5 + var6);
      var5 += var6;
      var6 = var4.getDilithiumK() * var4.getDilithiumPolyEtaPackedBytes();
      this.s2 = Arrays.copyOfRange(var2, var5, var5 + var6);
      var5 += var6;
      var6 = var4.getDilithiumK() * 416;
      this.t0 = Arrays.copyOfRange(var2, var5, var5 + var6);
      var5 += var6;
      if (var3 != null) {
         this.t1 = var3.getT1();
      } else {
         this.t1 = null;
      }
   }

   public byte[] getEncoded() {
      return Arrays.concatenate(new byte[][]{this.rho, this.k, this.tr, this.s1, this.s2, this.t0});
   }

   public byte[] getK() {
      return Arrays.clone(this.k);
   }

   @Deprecated
   public byte[] getPrivateKey() {
      return this.getEncoded();
   }

   public byte[] getPublicKey() {
      return DilithiumPublicKeyParameters.getEncoded(this.rho, this.t1);
   }

   public DilithiumPublicKeyParameters getPublicKeyParameters() {
      return new DilithiumPublicKeyParameters(this.getParameters(), this.rho, this.t1);
   }

   public byte[] getRho() {
      return Arrays.clone(this.rho);
   }

   public byte[] getS1() {
      return Arrays.clone(this.s1);
   }

   public byte[] getS2() {
      return Arrays.clone(this.s2);
   }

   public byte[] getT0() {
      return Arrays.clone(this.t0);
   }

   public byte[] getT1() {
      return Arrays.clone(this.t1);
   }

   public byte[] getTr() {
      return Arrays.clone(this.tr);
   }
}
