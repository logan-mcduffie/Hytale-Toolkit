package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;

public class FalconSigner implements MessageSigner {
   private byte[] encodedkey;
   private FalconNIST nist;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var1) {
         if (var2 instanceof ParametersWithRandom) {
            FalconPrivateKeyParameters var3 = (FalconPrivateKeyParameters)((ParametersWithRandom)var2).getParameters();
            this.encodedkey = var3.getEncoded();
            this.nist = new FalconNIST(var3.getParameters().getLogN(), var3.getParameters().getNonceLength(), ((ParametersWithRandom)var2).getRandom());
         } else {
            FalconPrivateKeyParameters var4 = (FalconPrivateKeyParameters)var2;
            this.encodedkey = ((FalconPrivateKeyParameters)var2).getEncoded();
            this.nist = new FalconNIST(var4.getParameters().getLogN(), var4.getParameters().getNonceLength(), CryptoServicesRegistrar.getSecureRandom());
         }
      } else {
         FalconPublicKeyParameters var5 = (FalconPublicKeyParameters)var2;
         this.encodedkey = var5.getH();
         this.nist = new FalconNIST(var5.getParameters().getLogN(), var5.getParameters().getNonceLength(), CryptoServicesRegistrar.getSecureRandom());
      }
   }

   @Override
   public byte[] generateSignature(byte[] var1) {
      byte[] var2 = new byte[this.nist.CRYPTO_BYTES];
      return this.nist.crypto_sign(var2, var1, var1.length, this.encodedkey);
   }

   @Override
   public boolean verifySignature(byte[] var1, byte[] var2) {
      if (var2[0] != (byte)(48 + this.nist.LOGN)) {
         return false;
      } else {
         byte[] var3 = new byte[this.nist.NONCELEN];
         byte[] var4 = new byte[var2.length - this.nist.NONCELEN - 1];
         System.arraycopy(var2, 1, var3, 0, this.nist.NONCELEN);
         System.arraycopy(var2, this.nist.NONCELEN + 1, var4, 0, var2.length - this.nist.NONCELEN - 1);
         return this.nist.crypto_sign_open(var4, var3, var1, this.encodedkey) == 0;
      }
   }
}
