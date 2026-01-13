package org.bouncycastle.operator.bc;

import java.security.SecureRandom;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;

public class BcSymmetricKeyUnwrapper extends SymmetricKeyUnwrapper {
   private SecureRandom random;
   private Wrapper wrapper;
   private KeyParameter wrappingKey;

   public BcSymmetricKeyUnwrapper(AlgorithmIdentifier var1, Wrapper var2, KeyParameter var3) {
      super(var1);
      this.wrapper = var2;
      this.wrappingKey = var3;
   }

   public BcSymmetricKeyUnwrapper setSecureRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   @Override
   public GenericKey generateUnwrappedKey(AlgorithmIdentifier var1, byte[] var2) throws OperatorException {
      this.wrapper.init(false, this.wrappingKey);

      try {
         return new GenericKey(var1, this.wrapper.unwrap(var2, 0, var2.length));
      } catch (InvalidCipherTextException var4) {
         throw new OperatorException("unable to unwrap key: " + var4.getMessage(), var4);
      }
   }
}
