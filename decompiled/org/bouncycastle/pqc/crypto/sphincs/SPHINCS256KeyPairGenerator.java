package org.bouncycastle.pqc.crypto.sphincs;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class SPHINCS256KeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private SecureRandom random;
   private Digest treeDigest;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.random = var1.getRandom();
      this.treeDigest = ((SPHINCS256KeyGenerationParameters)var1).getTreeDigest();
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      Tree.leafaddr var1 = new Tree.leafaddr();
      byte[] var2 = new byte[1088];
      this.random.nextBytes(var2);
      byte[] var3 = new byte[1056];
      System.arraycopy(var2, 32, var3, 0, 1024);
      var1.level = 11;
      var1.subtree = 0L;
      var1.subleaf = 0L;
      HashFunctions var4 = new HashFunctions(this.treeDigest);
      Tree.treehash(var4, var3, 1024, 5, var2, var1, var3, 0);
      return new AsymmetricCipherKeyPair(
         (AsymmetricKeyParameter)(new SPHINCSPublicKeyParameters(var3, this.treeDigest.getAlgorithmName())),
         (AsymmetricKeyParameter)(new SPHINCSPrivateKeyParameters(var2, this.treeDigest.getAlgorithmName()))
      );
   }
}
