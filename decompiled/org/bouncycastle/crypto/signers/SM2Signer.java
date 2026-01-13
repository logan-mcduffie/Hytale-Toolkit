package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;

public class SM2Signer implements Signer, ECConstants {
   private final DSAKCalculator kCalculator = new RandomDSAKCalculator();
   private final Digest digest;
   private final DSAEncoding encoding;
   private int state = 0;
   private ECDomainParameters ecParams;
   private ECPoint pubPoint;
   private ECKeyParameters ecKey;
   private byte[] z;

   public SM2Signer() {
      this(StandardDSAEncoding.INSTANCE, new SM3Digest());
   }

   public SM2Signer(Digest var1) {
      this(StandardDSAEncoding.INSTANCE, var1);
   }

   public SM2Signer(DSAEncoding var1) {
      this.encoding = var1;
      this.digest = new SM3Digest();
   }

   public SM2Signer(DSAEncoding var1, Digest var2) {
      this.encoding = var1;
      this.digest = var2;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      CipherParameters var3;
      byte[] var4;
      if (var2 instanceof ParametersWithID) {
         var3 = ((ParametersWithID)var2).getParameters();
         var4 = ((ParametersWithID)var2).getID();
         if (var4.length >= 8192) {
            throw new IllegalArgumentException("SM2 user ID must be less than 2^16 bits long");
         }
      } else {
         var3 = var2;
         var4 = Hex.decodeStrict("31323334353637383132333435363738");
      }

      if (var1) {
         SecureRandom var5 = null;
         if (var3 instanceof ParametersWithRandom) {
            ParametersWithRandom var6 = (ParametersWithRandom)var3;
            var3 = var6.getParameters();
            var5 = var6.getRandom();
         }

         ECPrivateKeyParameters var10 = (ECPrivateKeyParameters)var3;
         this.ecKey = var10;
         this.ecParams = var10.getParameters();
         BigInteger var7 = var10.getD();
         BigInteger var8 = this.ecParams.getN();
         if (var7.compareTo(ONE) < 0 || var7.compareTo(var8.subtract(ONE)) >= 0) {
            throw new IllegalArgumentException("SM2 private key out of range");
         }

         this.kCalculator.init(var8, CryptoServicesRegistrar.getSecureRandom(var5));
         this.pubPoint = this.createBasePointMultiplier().multiply(this.ecParams.getG(), var7).normalize();
      } else {
         ECPublicKeyParameters var9 = (ECPublicKeyParameters)var3;
         this.ecKey = var9;
         this.ecParams = var9.getParameters();
         this.pubPoint = var9.getQ();
      }

      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECNR", this.ecKey, var1));
      this.digest.reset();
      this.z = this.getZ(var4);
      this.state = 1;
   }

   @Override
   public void update(byte var1) {
      this.checkData();
      this.digest.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.checkData();
      this.digest.update(var1, var2, var3);
   }

   @Override
   public boolean verifySignature(byte[] var1) {
      this.checkData();

      try {
         BigInteger[] var2 = this.encoding.decode(this.ecParams.getN(), var1);
         return this.verifySignature(var2[0], var2[1]);
      } catch (Exception var7) {
      } finally {
         this.reset();
      }

      return false;
   }

   @Override
   public void reset() {
      switch (this.state) {
         case 1:
            return;
         case 2:
            this.digest.reset();
            this.state = 1;
            return;
         default:
            throw new IllegalStateException("SM2Signer needs to be initialized");
      }
   }

   @Override
   public byte[] generateSignature() throws CryptoException {
      this.checkData();
      byte[] var1 = this.digestDoFinal();
      BigInteger var2 = this.ecParams.getN();
      BigInteger var3 = this.calculateE(var2, var1);
      BigInteger var4 = ((ECPrivateKeyParameters)this.ecKey).getD();
      ECMultiplier var7 = this.createBasePointMultiplier();

      while (true) {
         BigInteger var8 = this.kCalculator.nextK();
         ECPoint var9 = var7.multiply(this.ecParams.getG(), var8).normalize();
         BigInteger var5 = var3.add(var9.getAffineXCoord().toBigInteger()).mod(var2);
         if (!var5.equals(ZERO) && !var5.add(var8).equals(var2)) {
            BigInteger var17 = BigIntegers.modOddInverse(var2, var4.add(ONE));
            BigInteger var6 = var8.subtract(var5.multiply(var4)).mod(var2);
            var6 = var17.multiply(var6).mod(var2);
            if (!var6.equals(ZERO)) {
               try {
                  var16 = this.encoding.encode(this.ecParams.getN(), var5, var6);
               } catch (Exception var13) {
                  throw new CryptoException("unable to encode signature: " + var13.getMessage(), var13);
               } finally {
                  this.reset();
               }

               return var16;
            }
         }
      }
   }

   private boolean verifySignature(BigInteger var1, BigInteger var2) {
      BigInteger var3 = this.ecParams.getN();
      if (var1.compareTo(ONE) < 0 || var1.compareTo(var3) >= 0) {
         return false;
      } else if (var2.compareTo(ONE) >= 0 && var2.compareTo(var3) < 0) {
         byte[] var4 = this.digestDoFinal();
         BigInteger var5 = this.calculateE(var3, var4);
         BigInteger var6 = var1.add(var2).mod(var3);
         if (var6.equals(ZERO)) {
            return false;
         } else {
            ECPoint var7 = ((ECPublicKeyParameters)this.ecKey).getQ();
            ECPoint var8 = ECAlgorithms.sumOfTwoMultiplies(this.ecParams.getG(), var2, var7, var6).normalize();
            if (var8.isInfinity()) {
               return false;
            } else {
               BigInteger var9 = var5.add(var8.getAffineXCoord().toBigInteger()).mod(var3);
               return var9.equals(var1);
            }
         }
      } else {
         return false;
      }
   }

   private void checkData() {
      switch (this.state) {
         case 1:
            this.digest.update(this.z, 0, this.z.length);
            this.state = 2;
            return;
         case 2:
            return;
         default:
            throw new IllegalStateException("SM2Signer needs to be initialized");
      }
   }

   private byte[] digestDoFinal() {
      byte[] var1 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var1, 0);
      return var1;
   }

   private byte[] getZ(byte[] var1) {
      this.addUserID(this.digest, var1);
      this.addFieldElement(this.digest, this.ecParams.getCurve().getA());
      this.addFieldElement(this.digest, this.ecParams.getCurve().getB());
      this.addFieldElement(this.digest, this.ecParams.getG().getAffineXCoord());
      this.addFieldElement(this.digest, this.ecParams.getG().getAffineYCoord());
      this.addFieldElement(this.digest, this.pubPoint.getAffineXCoord());
      this.addFieldElement(this.digest, this.pubPoint.getAffineYCoord());
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

   protected ECMultiplier createBasePointMultiplier() {
      return new FixedPointCombMultiplier();
   }

   protected BigInteger calculateE(BigInteger var1, byte[] var2) {
      return new BigInteger(1, var2);
   }

   private static final class State {
      static final int UNINITIALIZED = 0;
      static final int INIT = 1;
      static final int DATA = 2;
   }
}
