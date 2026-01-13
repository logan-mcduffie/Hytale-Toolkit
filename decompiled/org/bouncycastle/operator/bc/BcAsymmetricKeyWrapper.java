package org.bouncycastle.operator.bc;

import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;

public abstract class BcAsymmetricKeyWrapper extends AsymmetricKeyWrapper {
   private AsymmetricKeyParameter publicKey;
   private SecureRandom random;

   public BcAsymmetricKeyWrapper(AlgorithmIdentifier var1, AsymmetricKeyParameter var2) {
      super(var1);
      this.publicKey = var2;
   }

   public BcAsymmetricKeyWrapper setSecureRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   @Override
   public byte[] generateWrappedKey(GenericKey var1) throws OperatorException {
      AsymmetricBlockCipher var2 = this.createAsymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm());
      Object var3 = this.publicKey;
      if (this.random != null) {
         var3 = new ParametersWithRandom((CipherParameters)var3, this.random);
      }

      try {
         byte[] var4 = OperatorUtils.getKeyBytes(var1);
         var2.init(true, (CipherParameters)var3);
         return var2.processBlock(var4, 0, var4.length);
      } catch (InvalidCipherTextException var5) {
         throw new OperatorException("unable to encrypt contents key", var5);
      }
   }

   protected abstract AsymmetricBlockCipher createAsymmetricWrapper(ASN1ObjectIdentifier var1);
}
