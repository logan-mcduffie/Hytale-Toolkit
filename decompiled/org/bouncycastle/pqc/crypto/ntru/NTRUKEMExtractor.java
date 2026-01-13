package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;
import org.bouncycastle.util.Arrays;

public class NTRUKEMExtractor implements EncapsulatedSecretExtractor {
   private final NTRUPrivateKeyParameters ntruPrivateKey;

   public NTRUKEMExtractor(NTRUPrivateKeyParameters var1) {
      if (var1 == null) {
         throw new NullPointerException("'ntruPrivateKey' cannot be null");
      } else {
         this.ntruPrivateKey = var1;
      }
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      NTRUParameterSet var2 = this.ntruPrivateKey.getParameters().getParameterSet();
      if (var1 == null) {
         throw new NullPointerException("'encapsulation' cannot be null");
      } else if (var1.length != var2.ntruCiphertextBytes()) {
         throw new IllegalArgumentException("encapsulation");
      } else {
         byte[] var3 = this.ntruPrivateKey.privateKey;
         NTRUOWCPA var4 = new NTRUOWCPA(var2);
         OWCPADecryptResult var5 = var4.decrypt(var1, var3);
         byte[] var6 = var5.rm;
         int var7 = var5.fail;
         SHA3Digest var8 = new SHA3Digest(256);
         byte[] var9 = new byte[var8.getDigestSize()];
         var8.update(var6, 0, var6.length);
         var8.doFinal(var9, 0);
         var8.update(var3, var2.owcpaSecretKeyBytes(), var2.prfKeyBytes());
         var8.update(var1, 0, var1.length);
         var8.doFinal(var6, 0);
         this.cmov(var9, var6, (byte)var7);
         byte[] var10 = Arrays.copyOfRange(var9, 0, var2.sharedKeyBytes());
         Arrays.clear(var9);
         return var10;
      }
   }

   private void cmov(byte[] var1, byte[] var2, byte var3) {
      var3 = (byte)(~var3 + 1);

      for (int var4 = 0; var4 < var1.length; var4++) {
         var1[var4] = (byte)(var1[var4] ^ var3 & (var2[var4] ^ var1[var4]));
      }
   }

   @Override
   public int getEncapsulationLength() {
      return this.ntruPrivateKey.getParameters().getParameterSet().ntruCiphertextBytes();
   }
}
