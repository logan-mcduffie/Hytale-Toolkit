package org.bouncycastle.pqc.crypto.bike;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;
import org.bouncycastle.util.Arrays;

public class BIKEKEMGenerator implements EncapsulatedSecretGenerator {
   private final SecureRandom sr;

   public BIKEKEMGenerator(SecureRandom var1) {
      this.sr = var1;
   }

   @Override
   public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1) {
      BIKEPublicKeyParameters var2 = (BIKEPublicKeyParameters)var1;
      BIKEEngine var3 = var2.getParameters().getEngine();
      byte[] var4 = new byte[var2.getParameters().getLByte()];
      byte[] var5 = new byte[var2.getParameters().getRByte()];
      byte[] var6 = new byte[var2.getParameters().getLByte()];
      byte[] var7 = var2.publicKey;
      var3.encaps(var5, var6, var4, var7, this.sr);
      byte[] var8 = Arrays.concatenate(var5, var6);
      return new SecretWithEncapsulationImpl(Arrays.copyOfRange(var4, 0, var2.getParameters().getSessionKeySize() / 8), var8);
   }
}
