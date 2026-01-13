package org.bouncycastle.pqc.crypto.hqc;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;
import org.bouncycastle.util.Arrays;

public class HQCKEMGenerator implements EncapsulatedSecretGenerator {
   private final SecureRandom sr;

   public HQCKEMGenerator(SecureRandom var1) {
      this.sr = var1;
   }

   @Override
   public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1) {
      HQCPublicKeyParameters var2 = (HQCPublicKeyParameters)var1;
      HQCEngine var3 = var2.getParameters().getEngine();
      byte[] var4 = new byte[var2.getParameters().getSHA512_BYTES()];
      byte[] var5 = new byte[var2.getParameters().getN_BYTES()];
      byte[] var6 = new byte[var2.getParameters().getN1N2_BYTES()];
      byte[] var7 = new byte[var2.getParameters().getSALT_SIZE_BYTES()];
      byte[] var8 = var2.getPublicKey();
      var3.encaps(var5, var6, var4, var8, var7, this.sr);
      byte[] var9 = Arrays.concatenate(var5, var6, var7);
      return new SecretWithEncapsulationImpl(Arrays.copyOfRange(var4, 0, 32), var9);
   }
}
