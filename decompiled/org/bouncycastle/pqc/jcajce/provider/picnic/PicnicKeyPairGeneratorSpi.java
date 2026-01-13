package org.bouncycastle.pqc.jcajce.provider.picnic;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.pqc.crypto.picnic.PicnicKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicKeyPairGenerator;
import org.bouncycastle.pqc.crypto.picnic.PicnicParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.PicnicParameterSpec;
import org.bouncycastle.util.Strings;

public class PicnicKeyPairGeneratorSpi extends KeyPairGenerator {
   private static Map parameters = new HashMap();
   PicnicKeyGenerationParameters param;
   PicnicKeyPairGenerator engine = new PicnicKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public PicnicKeyPairGeneratorSpi() {
      super("Picnic");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (var3 != null) {
         this.param = new PicnicKeyGenerationParameters(var2, (PicnicParameters)parameters.get(var3));
         this.engine.init(this.param);
         this.initialised = true;
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof PicnicParameterSpec) {
         PicnicParameterSpec var1 = (PicnicParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toLowerCase(SpecUtil.getNameFrom(var0));
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.param = new PicnicKeyGenerationParameters(this.random, PicnicParameters.picnicl3ur);
         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      PicnicPublicKeyParameters var2 = (PicnicPublicKeyParameters)var1.getPublic();
      PicnicPrivateKeyParameters var3 = (PicnicPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCPicnicPublicKey(var2), new BCPicnicPrivateKey(var3));
   }

   static {
      parameters.put(PicnicParameterSpec.picnicl1fs.getName(), PicnicParameters.picnicl1fs);
      parameters.put(PicnicParameterSpec.picnicl1ur.getName(), PicnicParameters.picnicl1ur);
      parameters.put(PicnicParameterSpec.picnicl3fs.getName(), PicnicParameters.picnicl3fs);
      parameters.put(PicnicParameterSpec.picnicl3ur.getName(), PicnicParameters.picnicl3ur);
      parameters.put(PicnicParameterSpec.picnicl5fs.getName(), PicnicParameters.picnicl5fs);
      parameters.put(PicnicParameterSpec.picnicl5ur.getName(), PicnicParameters.picnicl5ur);
      parameters.put(PicnicParameterSpec.picnic3l1.getName(), PicnicParameters.picnic3l1);
      parameters.put(PicnicParameterSpec.picnic3l3.getName(), PicnicParameters.picnic3l3);
      parameters.put(PicnicParameterSpec.picnic3l5.getName(), PicnicParameters.picnic3l5);
      parameters.put(PicnicParameterSpec.picnicl1full.getName(), PicnicParameters.picnicl1full);
      parameters.put(PicnicParameterSpec.picnicl3full.getName(), PicnicParameters.picnicl3full);
      parameters.put(PicnicParameterSpec.picnicl5full.getName(), PicnicParameters.picnicl5full);
   }
}
