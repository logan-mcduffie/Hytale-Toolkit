package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class SPHINCSKeyParameters extends AsymmetricKeyParameter {
   public static final String SHA512_256 = "SHA-512/256";
   public static final String SHA3_256 = "SHA3-256";
   private final String treeDigest;

   protected SPHINCSKeyParameters(boolean var1, String var2) {
      super(var1);
      this.treeDigest = var2;
   }

   public String getTreeDigest() {
      return this.treeDigest;
   }
}
