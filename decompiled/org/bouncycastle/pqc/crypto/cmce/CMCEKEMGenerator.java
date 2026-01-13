package org.bouncycastle.pqc.crypto.cmce;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;

public class CMCEKEMGenerator implements EncapsulatedSecretGenerator {
   private final SecureRandom sr;

   public CMCEKEMGenerator(SecureRandom var1) {
      this.sr = var1;
   }

   @Override
   public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1) {
      CMCEPublicKeyParameters var2 = (CMCEPublicKeyParameters)var1;
      CMCEEngine var3 = var2.getParameters().getEngine();
      return this.generateEncapsulated(var1, var3.getDefaultSessionKeySize());
   }

   public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1, int var2) {
      CMCEPublicKeyParameters var3 = (CMCEPublicKeyParameters)var1;
      CMCEEngine var4 = var3.getParameters().getEngine();
      byte[] var5 = new byte[var4.getCipherTextSize()];
      byte[] var6 = new byte[var2 / 8];
      var4.kem_enc(var5, var6, var3.getPublicKey(), this.sr);
      return new SecretWithEncapsulationImpl(var6, var5);
   }
}
