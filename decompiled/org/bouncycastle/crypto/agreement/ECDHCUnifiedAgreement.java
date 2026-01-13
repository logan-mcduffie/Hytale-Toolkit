package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.ECDHUPrivateParameters;
import org.bouncycastle.crypto.params.ECDHUPublicParameters;
import org.bouncycastle.util.BigIntegers;

public class ECDHCUnifiedAgreement {
   private ECDHUPrivateParameters privParams;

   public void init(CipherParameters var1) {
      this.privParams = (ECDHUPrivateParameters)var1;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECCDHU", this.privParams.getStaticPrivateKey()));
   }

   public int getFieldSize() {
      return this.privParams.getStaticPrivateKey().getParameters().getCurve().getFieldElementEncodingLength();
   }

   public byte[] calculateAgreement(CipherParameters var1) {
      ECDHUPublicParameters var2 = (ECDHUPublicParameters)var1;
      ECDHCBasicAgreement var3 = new ECDHCBasicAgreement();
      ECDHCBasicAgreement var4 = new ECDHCBasicAgreement();
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
