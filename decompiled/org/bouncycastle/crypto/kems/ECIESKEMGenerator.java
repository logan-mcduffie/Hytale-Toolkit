package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class ECIESKEMGenerator implements EncapsulatedSecretGenerator {
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private DerivationFunction kdf;
   private SecureRandom rnd;
   private final int keySize;
   private boolean CofactorMode;
   private boolean OldCofactorMode;
   private boolean SingleHashMode;

   public ECIESKEMGenerator(int var1, DerivationFunction var2, SecureRandom var3) {
      this.keySize = var1;
      this.kdf = var2;
      this.rnd = var3;
      this.CofactorMode = false;
      this.OldCofactorMode = false;
      this.SingleHashMode = false;
   }

   public ECIESKEMGenerator(int var1, DerivationFunction var2, SecureRandom var3, boolean var4, boolean var5, boolean var6) {
      this.kdf = var2;
      this.rnd = var3;
      this.keySize = var1;
      this.CofactorMode = var4;
      if (var4) {
         this.OldCofactorMode = false;
      } else {
         this.OldCofactorMode = var5;
      }

      this.SingleHashMode = var6;
   }

   private ECMultiplier createBasePointMultiplier() {
      return new FixedPointCombMultiplier();
   }

   @Override
   public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1) {
      if (!(var1 instanceof ECPublicKeyParameters)) {
         throw new IllegalArgumentException("EC public key required");
      } else {
         ECPublicKeyParameters var2 = (ECPublicKeyParameters)var1;
         CryptoServicesRegistrar.checkConstraints(
            new DefaultServiceProperties("ECIESKem", ConstraintUtils.bitsOfSecurityFor(var2.getParameters().getCurve()), var1, CryptoServicePurpose.ENCRYPTION)
         );
         ECDomainParameters var3 = var2.getParameters();
         ECCurve var4 = var3.getCurve();
         BigInteger var5 = var3.getN();
         BigInteger var6 = var3.getH();
         BigInteger var7 = BigIntegers.createRandomInRange(ONE, var5, this.rnd);
         BigInteger var8 = this.OldCofactorMode ? var7.multiply(var6).mod(var5) : var7;
         ECMultiplier var9 = this.createBasePointMultiplier();
         ECPoint[] var10 = new ECPoint[]{var9.multiply(var3.getG(), var7), var2.getQ().multiply(var8)};
         var4.normalizeAll(var10);
         ECPoint var11 = var10[0];
         ECPoint var12 = var10[1];
         byte[] var13 = var11.getEncoded(false);
         byte[] var14 = new byte[var13.length];
         System.arraycopy(var13, 0, var14, 0, var13.length);
         byte[] var15 = var12.getAffineXCoord().getEncoded();
         return new SecretWithEncapsulationImpl(deriveKey(this.SingleHashMode, this.kdf, this.keySize, var13, var15), var14);
      }
   }

   static byte[] deriveKey(boolean var0, DerivationFunction var1, int var2, byte[] var3, byte[] var4) {
      byte[] var5 = var4;
      if (!var0) {
         var5 = Arrays.concatenate(var3, var4);
         Arrays.fill(var4, (byte)0);
      }

      byte[] var7;
      try {
         var1.init(new KDFParameters(var5, null));
         byte[] var6 = new byte[var2];
         var1.generateBytes(var6, 0, var6.length);
         var7 = var6;
      } finally {
         Arrays.fill(var5, (byte)0);
      }

      return var7;
   }
}
