package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.DHMQVPrivateParameters;
import org.bouncycastle.crypto.params.DHMQVPublicParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;

public class MQVBasicAgreement implements BasicAgreement {
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   DHMQVPrivateParameters privParams;

   @Override
   public void init(CipherParameters var1) {
      this.privParams = (DHMQVPrivateParameters)var1;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("MQV", this.privParams.getStaticPrivateKey()));
   }

   @Override
   public int getFieldSize() {
      return (this.privParams.getStaticPrivateKey().getParameters().getP().bitLength() + 7) / 8;
   }

   @Override
   public BigInteger calculateAgreement(CipherParameters var1) {
      DHMQVPublicParameters var2 = (DHMQVPublicParameters)var1;
      DHPrivateKeyParameters var3 = this.privParams.getStaticPrivateKey();
      if (!this.privParams.getStaticPrivateKey().getParameters().equals(var2.getStaticPublicKey().getParameters())) {
         throw new IllegalStateException("MQV public key components have wrong domain parameters");
      } else if (this.privParams.getStaticPrivateKey().getParameters().getQ() == null) {
         throw new IllegalStateException("MQV key domain parameters do not have Q set");
      } else {
         BigInteger var4 = this.calculateDHMQVAgreement(
            var3.getParameters(),
            var3,
            var2.getStaticPublicKey(),
            this.privParams.getEphemeralPrivateKey(),
            this.privParams.getEphemeralPublicKey(),
            var2.getEphemeralPublicKey()
         );
         if (var4.equals(ONE)) {
            throw new IllegalStateException("1 is not a valid agreement value for MQV");
         } else {
            return var4;
         }
      }
   }

   private BigInteger calculateDHMQVAgreement(
      DHParameters var1,
      DHPrivateKeyParameters var2,
      DHPublicKeyParameters var3,
      DHPrivateKeyParameters var4,
      DHPublicKeyParameters var5,
      DHPublicKeyParameters var6
   ) {
      BigInteger var7 = var1.getQ();
      int var8 = (var7.bitLength() + 1) / 2;
      BigInteger var9 = BigInteger.valueOf(2L).pow(var8);
      BigInteger var10 = var5.getY().mod(var9).add(var9);
      BigInteger var11 = var4.getX().add(var10.multiply(var2.getX())).mod(var7);
      BigInteger var12 = var6.getY().mod(var9).add(var9);
      return var6.getY().multiply(var3.getY().modPow(var12, var1.getP())).modPow(var11, var1.getP());
   }
}
