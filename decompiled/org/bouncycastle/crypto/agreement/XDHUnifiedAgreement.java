package org.bouncycastle.crypto.agreement;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.crypto.params.XDHUPrivateParameters;
import org.bouncycastle.crypto.params.XDHUPublicParameters;

public class XDHUnifiedAgreement implements RawAgreement {
   private final RawAgreement xAgreement;
   private XDHUPrivateParameters privParams;

   public XDHUnifiedAgreement(RawAgreement var1) {
      this.xAgreement = var1;
   }

   @Override
   public void init(CipherParameters var1) {
      this.privParams = (XDHUPrivateParameters)var1;
      this.xAgreement.init(this.privParams.getStaticPrivateKey());
   }

   @Override
   public int getAgreementSize() {
      return this.xAgreement.getAgreementSize() * 2;
   }

   @Override
   public void calculateAgreement(CipherParameters var1, byte[] var2, int var3) {
      XDHUPublicParameters var4 = (XDHUPublicParameters)var1;
      this.xAgreement.init(this.privParams.getEphemeralPrivateKey());
      this.xAgreement.calculateAgreement(var4.getEphemeralPublicKey(), var2, var3);
      this.xAgreement.init(this.privParams.getStaticPrivateKey());
      this.xAgreement.calculateAgreement(var4.getStaticPublicKey(), var2, var3 + this.xAgreement.getAgreementSize());
   }
}
