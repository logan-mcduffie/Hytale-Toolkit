package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.DHUPrivateParameters;
import org.bouncycastle.crypto.params.DHUPublicParameters;
import org.bouncycastle.util.BigIntegers;

public class DHUnifiedAgreement {
   private DHUPrivateParameters privParams;

   public void init(CipherParameters var1) {
      this.privParams = (DHUPrivateParameters)var1;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("DHU", this.privParams.getStaticPrivateKey()));
   }

   public int getFieldSize() {
      return (this.privParams.getStaticPrivateKey().getParameters().getP().bitLength() + 7) / 8;
   }

   public byte[] calculateAgreement(CipherParameters var1) {
      DHUPublicParameters var2 = (DHUPublicParameters)var1;
      DHBasicAgreement var3 = new DHBasicAgreement();
      DHBasicAgreement var4 = new DHBasicAgreement();
      var3.init(this.privParams.getStaticPrivateKey());
      BigInteger var5 = var3.calculateAgreement(var2.getStaticPublicKey());
      var4.init(this.privParams.getEphemeralPrivateKey());
      BigInteger var6 = var4.calculateAgreement(var2.getEphemeralPublicKey());
      int var7 = this.getFieldSize();
      byte[] var8 = new byte[var7 * 2];
      BigIntegers.asUnsignedByteArray(var6, var8, 0, var7);
      BigIntegers.asUnsignedByteArray(var5, var8, var7, var7);
      return var8;
   }
}
