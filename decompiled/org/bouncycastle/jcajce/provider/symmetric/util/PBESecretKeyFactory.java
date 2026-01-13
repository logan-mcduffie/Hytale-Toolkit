package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;

public class PBESecretKeyFactory extends BaseSecretKeyFactory implements PBE {
   private boolean forCipher;
   private int scheme;
   private int digest;
   private int keySize;
   private int ivSize;

   public PBESecretKeyFactory(String var1, ASN1ObjectIdentifier var2, boolean var3, int var4, int var5, int var6, int var7) {
      super(var1, var2);
      this.forCipher = var3;
      this.scheme = var4;
      this.digest = var5;
      this.keySize = var6;
      this.ivSize = var7;
   }

   @Override
   protected SecretKey engineGenerateSecret(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof PBEKeySpec) {
         PBEKeySpec var2 = (PBEKeySpec)var1;
         if (var2.getSalt() == null) {
            return new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, var2, null);
         } else {
            CipherParameters var3;
            if (this.forCipher) {
               var3 = PBE.Util.makePBEParameters(var2, this.scheme, this.digest, this.keySize, this.ivSize);
            } else {
               var3 = PBE.Util.makePBEMacParameters(var2, this.scheme, this.digest, this.keySize);
            }

            return new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, var2, var3);
         }
      } else {
         throw new InvalidKeySpecException("Invalid KeySpec");
      }
   }
}
