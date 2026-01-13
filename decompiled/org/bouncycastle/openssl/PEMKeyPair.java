package org.bouncycastle.openssl;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class PEMKeyPair {
   private final SubjectPublicKeyInfo publicKeyInfo;
   private final PrivateKeyInfo privateKeyInfo;

   public PEMKeyPair(SubjectPublicKeyInfo var1, PrivateKeyInfo var2) {
      this.publicKeyInfo = var1;
      this.privateKeyInfo = var2;
   }

   public PrivateKeyInfo getPrivateKeyInfo() {
      return this.privateKeyInfo;
   }

   public SubjectPublicKeyInfo getPublicKeyInfo() {
      return this.publicKeyInfo;
   }
}
