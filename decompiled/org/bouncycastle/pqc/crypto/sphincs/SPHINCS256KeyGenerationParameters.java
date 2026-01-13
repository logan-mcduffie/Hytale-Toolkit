package org.bouncycastle.pqc.crypto.sphincs;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class SPHINCS256KeyGenerationParameters extends KeyGenerationParameters {
   private final Digest treeDigest;

   public SPHINCS256KeyGenerationParameters(SecureRandom var1, Digest var2) {
      super(var1, 8448);
      this.treeDigest = var2;
   }

   public Digest getTreeDigest() {
      return this.treeDigest;
   }
}
