package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class XMSSKeyParameters extends AsymmetricKeyParameter {
   public static final String SHA_256 = "SHA-256";
   public static final String SHA_512 = "SHA-512";
   public static final String SHAKE128 = "SHAKE128";
   public static final String SHAKE256 = "SHAKE256";
   private final String treeDigest;

   public XMSSKeyParameters(boolean var1, String var2) {
      super(var1);
      this.treeDigest = var2;
   }

   public String getTreeDigest() {
      return this.treeDigest;
   }
}
