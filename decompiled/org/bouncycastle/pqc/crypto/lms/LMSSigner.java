package org.bouncycastle.pqc.crypto.lms;

import java.io.IOException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.crypto.MessageSigner;

public class LMSSigner implements MessageSigner {
   private LMSPrivateKeyParameters privKey;
   private LMSPublicKeyParameters pubKey;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var1) {
         if (var2 instanceof HSSPrivateKeyParameters) {
            HSSPrivateKeyParameters var3 = (HSSPrivateKeyParameters)var2;
            if (var3.getL() != 1) {
               throw new IllegalArgumentException("only a single level HSS key can be used with LMS");
            }

            this.privKey = var3.getRootKey();
         } else {
            this.privKey = (LMSPrivateKeyParameters)var2;
         }
      } else if (var2 instanceof HSSPublicKeyParameters) {
         HSSPublicKeyParameters var4 = (HSSPublicKeyParameters)var2;
         if (var4.getL() != 1) {
            throw new IllegalArgumentException("only a single level HSS key can be used with LMS");
         }

         this.pubKey = var4.getLMSPublicKey();
      } else {
         this.pubKey = (LMSPublicKeyParameters)var2;
      }
   }

   @Override
   public byte[] generateSignature(byte[] var1) {
      try {
         return LMS.generateSign(this.privKey, var1).getEncoded();
      } catch (IOException var3) {
         throw new IllegalStateException("unable to encode signature: " + var3.getMessage());
      }
   }

   @Override
   public boolean verifySignature(byte[] var1, byte[] var2) {
      try {
         return LMS.verifySignature(this.pubKey, LMSSignature.getInstance(var2), var1);
      } catch (IOException var4) {
         throw new IllegalStateException("unable to decode signature: " + var4.getMessage());
      }
   }
}
