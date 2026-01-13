package org.bouncycastle.pqc.jcajce.provider.util;

import java.security.InvalidKeyException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.ARIAEngine;
import org.bouncycastle.crypto.engines.CamelliaEngine;
import org.bouncycastle.crypto.engines.RFC3394WrapEngine;
import org.bouncycastle.crypto.engines.RFC5649WrapEngine;
import org.bouncycastle.crypto.engines.SEEDEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.util.Arrays;

public class WrapUtil {
   public static Wrapper getKeyWrapper(KTSParameterSpec var0, byte[] var1) throws InvalidKeyException {
      Wrapper var2 = getWrapper(var0.getKeyAlgorithmName());
      AlgorithmIdentifier var3 = var0.getKdfAlgorithm();
      if (var3 == null) {
         var2.init(true, new KeyParameter(Arrays.copyOfRange(var1, 0, (var0.getKeySize() + 7) / 8)));
      } else {
         var2.init(true, new KeyParameter(makeKeyBytes(var0, var1)));
      }

      return var2;
   }

   public static Wrapper getKeyUnwrapper(KTSParameterSpec var0, byte[] var1) throws InvalidKeyException {
      Wrapper var2 = getWrapper(var0.getKeyAlgorithmName());
      AlgorithmIdentifier var3 = var0.getKdfAlgorithm();
      if (var3 == null) {
         var2.init(false, new KeyParameter(var1, 0, (var0.getKeySize() + 7) / 8));
      } else {
         var2.init(false, new KeyParameter(makeKeyBytes(var0, var1)));
      }

      return var2;
   }

   public static Wrapper getWrapper(String var0) {
      Object var1;
      if (var0.equalsIgnoreCase("AESWRAP") || var0.equalsIgnoreCase("AES")) {
         var1 = new RFC3394WrapEngine(AESEngine.newInstance());
      } else if (var0.equalsIgnoreCase("ARIA")) {
         var1 = new RFC3394WrapEngine(new ARIAEngine());
      } else if (var0.equalsIgnoreCase("Camellia")) {
         var1 = new RFC3394WrapEngine(new CamelliaEngine());
      } else if (var0.equalsIgnoreCase("SEED")) {
         var1 = new RFC3394WrapEngine(new SEEDEngine());
      } else if (var0.equalsIgnoreCase("AES-KWP")) {
         var1 = new RFC5649WrapEngine(new AESEngine());
      } else if (var0.equalsIgnoreCase("Camellia-KWP")) {
         var1 = new RFC5649WrapEngine(new CamelliaEngine());
      } else {
         if (!var0.equalsIgnoreCase("ARIA-KWP")) {
            throw new UnsupportedOperationException("unknown key algorithm: " + var0);
         }

         var1 = new RFC5649WrapEngine(new ARIAEngine());
      }

      return (Wrapper)var1;
   }

   public static byte[] trimSecret(String var0, byte[] var1) {
      return var0.equals("SEED") ? Arrays.copyOfRange(var1, 0, 16) : var1;
   }

   private static byte[] makeKeyBytes(KTSParameterSpec var0, byte[] var1) throws InvalidKeyException {
      try {
         return KdfUtil.makeKeyBytes(var0.getKdfAlgorithm(), var1, var0.getOtherInfo(), var0.getKeySize());
      } catch (IllegalArgumentException var3) {
         throw new InvalidKeyException(var3.getMessage());
      }
   }
}
