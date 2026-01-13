package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class DigestingStateAwareMessageSigner extends DigestingMessageSigner {
   private final StateAwareMessageSigner signer;

   public DigestingStateAwareMessageSigner(StateAwareMessageSigner var1, Digest var2) {
      super(var1, var2);
      this.signer = var1;
   }

   public AsymmetricKeyParameter getUpdatedPrivateKey() {
      return this.signer.getUpdatedPrivateKey();
   }
}
