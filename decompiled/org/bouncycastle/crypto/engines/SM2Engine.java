package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SM2Engine {
   private final Digest digest;
   private final SM2Engine.Mode mode;
   private boolean forEncryption;
   private ECKeyParameters ecKey;
   private ECDomainParameters ecParams;
   private int curveLength;
   private SecureRandom random;

   public SM2Engine() {
      this(new SM3Digest());
   }

   public SM2Engine(SM2Engine.Mode var1) {
      this(new SM3Digest(), var1);
   }

   public SM2Engine(Digest var1) {
      this(var1, SM2Engine.Mode.C1C2C3);
   }

   public SM2Engine(Digest var1, SM2Engine.Mode var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("mode cannot be NULL");
      } else {
         this.digest = var1;
         this.mode = var2;
      }
   }

   public void init(boolean var1, CipherParameters var2) {
      this.forEncryption = var1;
      if (var1) {
         ParametersWithRandom var3 = (ParametersWithRandom)var2;
         this.ecKey = (ECKeyParameters)var3.getParameters();
         this.ecParams = this.ecKey.getParameters();
         ECPoint var4 = ((ECPublicKeyParameters)this.ecKey).getQ().multiply(this.ecParams.getH());
         if (var4.isInfinity()) {
            throw new IllegalArgumentException("invalid key: [h]Q at infinity");
         }

         this.random = var3.getRandom();
      } else {
         this.ecKey = (ECKeyParameters)var2;
         this.ecParams = this.ecKey.getParameters();
      }

      this.curveLength = this.ecParams.getCurve().getFieldElementEncodingLength();
      CryptoServicesRegistrar.checkConstraints(
         new DefaultServiceProperties("SM2", ConstraintUtils.bitsOfSecurityFor(this.ecParams.getCurve()), this.ecKey, Utils.getPurpose(var1))
      );
   }

   public byte[] processBlock(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      if (var2 + var3 > var1.length || var3 == 0) {
         throw new DataLengthException("input buffer too short");
      } else {
         return this.forEncryption ? this.encrypt(var1, var2, var3) : this.decrypt(var1, var2, var3);
      }
   }

   public int getOutputSize(int var1) {
      return 1 + 2 * this.curveLength + var1 + this.digest.getDigestSize();
   }

   protected ECMultiplier createBasePointMultiplier() {
      return new FixedPointCombMultiplier();
   }

   private byte[] encrypt(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      byte[] var4 = new byte[var3];
      System.arraycopy(var1, var2, var4, 0, var4.length);
      ECMultiplier var5 = this.createBasePointMultiplier();

      byte[] var6;
      ECPoint var7;
      do {
         BigInteger var8 = this.nextK();
         ECPoint var9 = var5.multiply(this.ecParams.getG(), var8).normalize();
         var6 = var9.getEncoded(false);
         var7 = ((ECPublicKeyParameters)this.ecKey).getQ().multiply(var8).normalize();
         this.kdf(this.digest, var7, var4);
      } while (this.notEncrypted(var4, var1, var2));

      byte[] var10 = new byte[this.digest.getDigestSize()];
      this.addFieldElement(this.digest, var7.getAffineXCoord());
      this.digest.update(var1, var2, var3);
      this.addFieldElement(this.digest, var7.getAffineYCoord());
      this.digest.doFinal(var10, 0);
      switch (this.mode) {
         case C1C3C2:
            return Arrays.concatenate(var6, var10, var4);
         default:
            return Arrays.concatenate(var6, var4, var10);
      }
   }

   private byte[] decrypt(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      byte[] var4 = new byte[this.curveLength * 2 + 1];
      System.arraycopy(var1, var2, var4, 0, var4.length);
      ECPoint var5 = this.ecParams.getCurve().decodePoint(var4);
      ECPoint var6 = var5.multiply(this.ecParams.getH());
      if (var6.isInfinity()) {
         throw new InvalidCipherTextException("[h]C1 at infinity");
      } else {
         var5 = var5.multiply(((ECPrivateKeyParameters)this.ecKey).getD()).normalize();
         int var7 = this.digest.getDigestSize();
         byte[] var8 = new byte[var3 - var4.length - var7];
         if (this.mode == SM2Engine.Mode.C1C3C2) {
            System.arraycopy(var1, var2 + var4.length + var7, var8, 0, var8.length);
         } else {
            System.arraycopy(var1, var2 + var4.length, var8, 0, var8.length);
         }

         this.kdf(this.digest, var5, var8);
         byte[] var9 = new byte[this.digest.getDigestSize()];
         this.addFieldElement(this.digest, var5.getAffineXCoord());
         this.digest.update(var8, 0, var8.length);
         this.addFieldElement(this.digest, var5.getAffineYCoord());
         this.digest.doFinal(var9, 0);
         int var10 = 0;
         if (this.mode == SM2Engine.Mode.C1C3C2) {
            for (int var11 = 0; var11 != var9.length; var11++) {
               var10 |= var9[var11] ^ var1[var2 + var4.length + var11];
            }
         } else {
            for (int var13 = 0; var13 != var9.length; var13++) {
               var10 |= var9[var13] ^ var1[var2 + var4.length + var8.length + var13];
            }
         }

         Arrays.fill(var4, (byte)0);
         Arrays.fill(var9, (byte)0);
         if (var10 != 0) {
            Arrays.fill(var8, (byte)0);
            throw new InvalidCipherTextException("invalid cipher text");
         } else {
            return var8;
         }
      }
   }

   private boolean notEncrypted(byte[] var1, byte[] var2, int var3) {
      for (int var4 = 0; var4 != var1.length; var4++) {
         if (var1[var4] != var2[var3 + var4]) {
            return false;
         }
      }

      return true;
   }

   private void kdf(Digest var1, ECPoint var2, byte[] var3) {
      int var4 = var1.getDigestSize();
      byte[] var5 = new byte[Math.max(4, var4)];
      int var6 = 0;
      Memoable var7 = null;
      Memoable var8 = null;
      if (var1 instanceof Memoable) {
         this.addFieldElement(var1, var2.getAffineXCoord());
         this.addFieldElement(var1, var2.getAffineYCoord());
         var7 = (Memoable)var1;
         var8 = var7.copy();
      }

      int var9 = 0;

      while (var6 < var3.length) {
         if (var7 != null) {
            var7.reset(var8);
         } else {
            this.addFieldElement(var1, var2.getAffineXCoord());
            this.addFieldElement(var1, var2.getAffineYCoord());
         }

         Pack.intToBigEndian(++var9, var5, 0);
         var1.update(var5, 0, 4);
         var1.doFinal(var5, 0);
         int var10 = Math.min(var4, var3.length - var6);
         Bytes.xorTo(var10, var5, 0, var3, var6);
         var6 += var10;
      }
   }

   private BigInteger nextK() {
      int var1 = this.ecParams.getN().bitLength();

      BigInteger var2;
      do {
         var2 = BigIntegers.createRandomBigInteger(var1, this.random);
      } while (var2.equals(BigIntegers.ZERO) || var2.compareTo(this.ecParams.getN()) >= 0);

      return var2;
   }

   private void addFieldElement(Digest var1, ECFieldElement var2) {
      byte[] var3 = BigIntegers.asUnsignedByteArray(this.curveLength, var2.toBigInteger());
      var1.update(var3, 0, var3.length);
   }

   public static enum Mode {
      C1C2C3,
      C1C3C2;
   }
}
