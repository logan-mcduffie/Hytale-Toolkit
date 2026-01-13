package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class X25519KeyGenerationParameters extends KeyGenerationParameters {
   public X25519KeyGenerationParameters(SecureRandom var1) {
      super(var1, 255);
   }
}
