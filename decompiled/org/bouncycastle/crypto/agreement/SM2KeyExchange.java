package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.SM2KeyExchangePrivateParameters;
import org.bouncycastle.crypto.params.SM2KeyExchangePublicParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SM2KeyExchange {
   private final Digest digest;
   private byte[] userID;
   private ECPrivateKeyParameters staticKey;
   private ECPoint staticPubPoint;
   private ECPoint ephemeralPubPoint;
   private ECDomainParameters ecParams;
   private int w;
   private ECPrivateKeyParameters ephemeralKey;
   private boolean initiator;

   public SM2KeyExchange() {
      this(new SM3Digest());
   }

   public SM2KeyExchange(Digest var1) {
      this.digest = var1;
   }

   public void init(CipherParameters var1) {
      SM2KeyExchangePrivateParameters var2;
      if (var1 instanceof ParametersWithID) {
         var2 = (SM2KeyExchangePrivateParameters)((ParametersWithID)var1).getParameters();
         this.userID = checkUserID(((ParametersWithID)var1).getID());
      } else {
         var2 = (SM2KeyExchangePrivateParameters)var1;
         this.userID = new byte[0];
      }

      this.initiator = var2.isInitiator();
      this.staticKey = var2.getStaticPrivateKey();
      this.ephemeralKey = var2.getEphemeralPrivateKey();
      this.ecParams = this.staticKey.getParameters();
      this.staticPubPoint = var2.getStaticPublicPoint();
      this.ephemeralPubPoint = var2.getEphemeralPublicPoint();
      this.w = this.ecParams.getCurve().getFieldSize() / 2 - 1;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("SM2KE", this.staticKey));
   }

   public byte[] calculateKey(int var1, CipherParameters var2) {
      SM2KeyExchangePublicParameters var3;
      byte[] var4;
      if (var2 instanceof ParametersWithID) {
         var3 = (SM2KeyExchangePublicParameters)((ParametersWithID)var2).getParameters();
         var4 = checkUserID(((ParametersWithID)var2).getID());
      } else {
         var3 = (SM2KeyExchangePublicParameters)var2;
         var4 = new byte[0];
      }

      byte[] var5 = this.getZ(this.digest, this.userID, this.staticPubPoint);
      byte[] var6 = this.getZ(this.digest, var4, var3.getStaticPublicKey().getQ());
      ECPoint var7 = this.calculateU(var3);
      byte[] var8;
      if (this.initiator) {
         var8 = this.kdf(var7, var5, var6, var1);
      } else {
         var8 = this.kdf(var7, var6, var5, var1);
      }

      return var8;
   }

   public byte[][] calculateKeyWithConfirmation(int var1, byte[] var2, CipherParameters var3) {
      SM2KeyExchangePublicParameters var4;
      byte[] var5;
      if (var3 instanceof ParametersWithID) {
         var4 = (SM2KeyExchangePublicParameters)((ParametersWithID)var3).getParameters();
         var5 = checkUserID(((ParametersWithID)var3).getID());
      } else {
         var4 = (SM2KeyExchangePublicParameters)var3;
         var5 = new byte[0];
      }

      if (this.initiator && var2 == null) {
         throw new IllegalArgumentException("if initiating, confirmationTag must be set");
      } else {
         byte[] var6 = this.getZ(this.digest, this.userID, this.staticPubPoint);
         byte[] var7 = this.getZ(this.digest, var5, var4.getStaticPublicKey().getQ());
         ECPoint var8 = this.calculateU(var4);
         if (this.initiator) {
            byte[] var12 = this.kdf(var8, var6, var7, var1);
            byte[] var13 = this.calculateInnerHash(this.digest, var8, var6, var7, this.ephemeralPubPoint, var4.getEphemeralPublicKey().getQ());
            byte[] var11 = this.S1(this.digest, var8, var13);
            if (!Arrays.constantTimeAreEqual(var11, var2)) {
               throw new IllegalStateException("confirmation tag mismatch");
            } else {
               return new byte[][]{var12, this.S2(this.digest, var8, var13)};
            }
         } else {
            byte[] var9 = this.kdf(var8, var7, var6, var1);
            byte[] var10 = this.calculateInnerHash(this.digest, var8, var7, var6, var4.getEphemeralPublicKey().getQ(), this.ephemeralPubPoint);
            return new byte[][]{var9, this.S1(this.digest, var8, var10), this.S2(this.digest, var8, var10)};
         }
      }
   }

   private ECPoint calculateU(SM2KeyExchangePublicParameters var1) {
      ECDomainParameters var2 = this.staticKey.getParameters();
      ECPoint var3 = ECAlgorithms.cleanPoint(var2.getCurve(), var1.getStaticPublicKey().getQ());
      ECPoint var4 = ECAlgorithms.cleanPoint(var2.getCurve(), var1.getEphemeralPublicKey().getQ());
      BigInteger var5 = this.reduce(this.ephemeralPubPoint.getAffineXCoord().toBigInteger());
      BigInteger var6 = this.reduce(var4.getAffineXCoord().toBigInteger());
      BigInteger var7 = this.staticKey.getD().add(var5.multiply(this.ephemeralKey.getD()));
      BigInteger var8 = this.ecParams.getH().multiply(var7).mod(this.ecParams.getN());
      BigInteger var9 = var8.multiply(var6).mod(this.ecParams.getN());
      return ECAlgorithms.sumOfTwoMultiplies(var3, var8, var4, var9).normalize();
   }

   private byte[] kdf(ECPoint var1, byte[] var2, byte[] var3, int var4) {
      int var5 = this.digest.getDigestSize();
      byte[] var6 = new byte[Math.max(4, var5)];
      byte[] var7 = new byte[(var4 + 7) / 8];
      int var8 = 0;
      Memoable var9 = null;
      Memoable var10 = null;
      if (this.digest instanceof Memoable) {
         this.addFieldElement(this.digest, var1.getAffineXCoord());
         this.addFieldElement(this.digest, var1.getAffineYCoord());
         this.digest.update(var2, 0, var2.length);
         this.digest.update(var3, 0, var3.length);
         var9 = (Memoable)this.digest;
         var10 = var9.copy();
      }

      int var11 = 0;

      while (var8 < var7.length) {
         if (var9 != null) {
            var9.reset(var10);
         } else {
            this.addFieldElement(this.digest, var1.getAffineXCoord());
            this.addFieldElement(this.digest, var1.getAffineYCoord());
            this.digest.update(var2, 0, var2.length);
            this.digest.update(var3, 0, var3.length);
         }

         Pack.intToBigEndian(++var11, var6, 0);
         this.digest.update(var6, 0, 4);
         this.digest.doFinal(var6, 0);
         int var12 = Math.min(var5, var7.length - var8);
         System.arraycopy(var6, 0, var7, var8, var12);
         var8 += var12;
      }

      return var7;
   }

   private BigInteger reduce(BigInteger var1) {
      return var1.and(BigInteger.valueOf(1L).shiftLeft(this.w).subtract(BigInteger.valueOf(1L))).setBit(this.w);
   }

   private byte[] S1(Digest var1, ECPoint var2, byte[] var3) {
      var1.update((byte)2);
      this.addFieldElement(var1, var2.getAffineYCoord());
      var1.update(var3, 0, var3.length);
      return this.digestDoFinal();
   }

   private byte[] calculateInnerHash(Digest var1, ECPoint var2, byte[] var3, byte[] var4, ECPoint var5, ECPoint var6) {
      this.addFieldElement(var1, var2.getAffineXCoord());
      var1.update(var3, 0, var3.length);
      var1.update(var4, 0, var4.length);
      this.addFieldElement(var1, var5.getAffineXCoord());
      this.addFieldElement(var1, var5.getAffineYCoord());
      this.addFieldElement(var1, var6.getAffineXCoord());
      this.addFieldElement(var1, var6.getAffineYCoord());
      return this.digestDoFinal();
   }

   private byte[] S2(Digest var1, ECPoint var2, byte[] var3) {
      var1.update((byte)3);
      this.addFieldElement(var1, var2.getAffineYCoord());
      var1.update(var3, 0, var3.length);
      return this.digestDoFinal();
   }

   private byte[] getZ(Digest var1, byte[] var2, ECPoint var3) {
      this.addUserID(var1, var2);
      this.addFieldElement(var1, this.ecParams.getCurve().getA());
      this.addFieldElement(var1, this.ecParams.getCurve().getB());
      this.addFieldElement(var1, this.ecParams.getG().getAffineXCoord());
      this.addFieldElement(var1, this.ecParams.getG().getAffineYCoord());
      this.addFieldElement(var1, var3.getAffineXCoord());
      this.addFieldElement(var1, var3.getAffineYCoord());
      return this.digestDoFinal();
   }

   private void addUserID(Digest var1, byte[] var2) {
      int var3 = var2.length * 8;
      var1.update((byte)(var3 >>> 8));
      var1.update((byte)var3);
      var1.update(var2, 0, var2.length);
   }

   private void addFieldElement(Digest var1, ECFieldElement var2) {
      byte[] var3 = var2.getEncoded();
      var1.update(var3, 0, var3.length);
   }

   private byte[] digestDoFinal() {
      byte[] var1 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var1, 0);
      return var1;
   }

   private static byte[] checkUserID(byte[] var0) {
      if (var0.length >= 8192) {
         throw new IllegalArgumentException("SM2 user ID must be less than 2^16 bits long");
      } else {
         return var0;
      }
   }
}
