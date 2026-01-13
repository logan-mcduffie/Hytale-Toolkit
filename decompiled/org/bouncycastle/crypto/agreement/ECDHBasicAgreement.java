package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECPoint;

public class ECDHBasicAgreement implements BasicAgreement {
   private ECPrivateKeyParameters key;

   @Override
   public void init(CipherParameters var1) {
      this.key = (ECPrivateKeyParameters)var1;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECDH", this.key));
   }

   @Override
   public int getFieldSize() {
      return this.key.getParameters().getCurve().getFieldElementEncodingLength();
   }

   @Override
   public BigInteger calculateAgreement(CipherParameters var1) {
      ECPublicKeyParameters var2 = (ECPublicKeyParameters)var1;
      ECDomainParameters var3 = this.key.getParameters();
      if (!var3.equals(var2.getParameters())) {
         throw new IllegalStateException("ECDH public key has wrong domain parameters");
      } else {
         BigInteger var4 = this.key.getD();
         ECPoint var5 = ECAlgorithms.cleanPoint(var3.getCurve(), var2.getQ());
         if (var5.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid public key for ECDH");
         } else {
            BigInteger var6 = var3.getH();
            if (!var6.equals(ECConstants.ONE)) {
               var4 = var3.getHInv().multiply(var4).mod(var3.getN());
               var5 = ECAlgorithms.referenceMultiply(var5, var6);
            }

            ECPoint var7 = var5.multiply(var4).normalize();
            if (var7.isInfinity()) {
               throw new IllegalStateException("Infinity is not a valid agreement value for ECDH");
            } else {
               return var7.getAffineXCoord().toBigInteger();
            }
         }
      }
   }
}
