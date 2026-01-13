package org.bouncycastle.pqc.crypto.frodo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;

public class FrodoKEMGenerator implements EncapsulatedSecretGenerator {
   private final SecureRandom sr;

   public FrodoKEMGenerator(SecureRandom var1) {
      this.sr = var1;
   }

   @Override
   public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1) {
      FrodoPublicKeyParameters var2 = (FrodoPublicKeyParameters)var1;
      FrodoEngine var3 = var2.getParameters().getEngine();
      byte[] var4 = new byte[var3.getCipherTextSize()];
      byte[] var5 = new byte[var3.getSessionKeySize()];
      var3.kem_enc(var4, var5, var2.getPublicKey(), this.sr);
      return new SecretWithEncapsulationImpl(var5, var4);
   }
}
