package org.bouncycastle.pqc.crypto.picnic;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class PicnicSigner implements MessageSigner {
   private PicnicPrivateKeyParameters privKey;
   private PicnicPublicKeyParameters pubKey;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var1) {
         this.privKey = (PicnicPrivateKeyParameters)var2;
      } else {
         this.pubKey = (PicnicPublicKeyParameters)var2;
      }
   }

   @Override
   public byte[] generateSignature(byte[] var1) {
      PicnicEngine var2 = this.privKey.getParameters().getEngine();
      byte[] var3 = new byte[var2.getSignatureSize(var1.length)];
      var2.crypto_sign(var3, var1, this.privKey.getEncoded());
      byte[] var4 = new byte[var2.getTrueSignatureSize()];
      System.arraycopy(var3, var1.length + 4, var4, 0, var2.getTrueSignatureSize());
      return var4;
   }

   @Override
   public boolean verifySignature(byte[] var1, byte[] var2) {
      PicnicEngine var3 = this.pubKey.getParameters().getEngine();
      byte[] var4 = new byte[var1.length];
      byte[] var5 = Arrays.concatenate(Pack.intToLittleEndian(var2.length), var1, var2);
      boolean var6 = var3.crypto_sign_open(var4, var5, this.pubKey.getEncoded());
      return !Arrays.areEqual(var1, var4) ? false : var6;
   }
}
