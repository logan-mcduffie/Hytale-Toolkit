package org.bouncycastle.pqc.crypto.lms;

import java.io.IOException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.crypto.MessageSigner;

public class HSSSigner implements MessageSigner {
   private HSSPrivateKeyParameters privKey;
   private HSSPublicKeyParameters pubKey;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var1) {
         this.privKey = (HSSPrivateKeyParameters)var2;
      } else {
         this.pubKey = (HSSPublicKeyParameters)var2;
      }
   }

   @Override
   public byte[] generateSignature(byte[] var1) {
      try {
         return HSS.generateSignature(this.privKey, var1).getEncoded();
      } catch (IOException var3) {
         throw new IllegalStateException("unable to encode signature: " + var3.getMessage());
      }
   }

   @Override
   public boolean verifySignature(byte[] var1, byte[] var2) {
      try {
         return HSS.verifySignature(this.pubKey, HSSSignature.getInstance(var2, this.pubKey.getL()), var1);
      } catch (IOException var4) {
         throw new IllegalStateException("unable to decode signature: " + var4.getMessage());
      }
   }
}
