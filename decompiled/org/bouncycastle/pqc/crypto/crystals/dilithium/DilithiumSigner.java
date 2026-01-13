package org.bouncycastle.pqc.crypto.crystals.dilithium;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;

public class DilithiumSigner implements MessageSigner {
   private DilithiumPrivateKeyParameters privKey;
   private DilithiumPublicKeyParameters pubKey;
   private SecureRandom random;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var1) {
         if (var2 instanceof ParametersWithRandom) {
            this.privKey = (DilithiumPrivateKeyParameters)((ParametersWithRandom)var2).getParameters();
            this.random = ((ParametersWithRandom)var2).getRandom();
         } else {
            this.privKey = (DilithiumPrivateKeyParameters)var2;
            this.random = null;
         }
      } else {
         this.pubKey = (DilithiumPublicKeyParameters)var2;
      }
   }

   @Override
   public byte[] generateSignature(byte[] var1) {
      DilithiumEngine var2 = this.privKey.getParameters().getEngine(this.random);
      return var2.sign(var1, var1.length, this.privKey.rho, this.privKey.k, this.privKey.tr, this.privKey.t0, this.privKey.s1, this.privKey.s2);
   }

   public byte[] internalGenerateSignature(byte[] var1, byte[] var2) {
      DilithiumEngine var3 = this.privKey.getParameters().getEngine(this.random);
      return var3.signSignatureInternal(
         var1, var1.length, this.privKey.rho, this.privKey.k, this.privKey.tr, this.privKey.t0, this.privKey.s1, this.privKey.s2, var2
      );
   }

   @Override
   public boolean verifySignature(byte[] var1, byte[] var2) {
      DilithiumEngine var3 = this.pubKey.getParameters().getEngine(this.random);
      return var3.signOpen(var1, var2, var2.length, this.pubKey.rho, this.pubKey.t1);
   }
}
