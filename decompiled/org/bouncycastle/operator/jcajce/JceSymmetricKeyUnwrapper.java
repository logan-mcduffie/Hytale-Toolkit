package org.bouncycastle.operator.jcajce;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;

public class JceSymmetricKeyUnwrapper extends SymmetricKeyUnwrapper {
   private OperatorHelper helper = new OperatorHelper(new DefaultJcaJceHelper());
   private SecretKey secretKey;

   public JceSymmetricKeyUnwrapper(AlgorithmIdentifier var1, SecretKey var2) {
      super(var1);
      this.secretKey = var2;
   }

   public JceSymmetricKeyUnwrapper setProvider(Provider var1) {
      this.helper = new OperatorHelper(new ProviderJcaJceHelper(var1));
      return this;
   }

   public JceSymmetricKeyUnwrapper setProvider(String var1) {
      this.helper = new OperatorHelper(new NamedJcaJceHelper(var1));
      return this;
   }

   @Override
   public GenericKey generateUnwrappedKey(AlgorithmIdentifier var1, byte[] var2) throws OperatorException {
      try {
         Cipher var3 = this.helper.createSymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm());
         var3.init(4, this.secretKey);
         return new JceGenericKey(var1, var3.unwrap(var2, this.helper.getKeyAlgorithmName(var1.getAlgorithm()), 3));
      } catch (InvalidKeyException var4) {
         throw new OperatorException("key invalid in message.", var4);
      } catch (NoSuchAlgorithmException var5) {
         throw new OperatorException("can't find algorithm.", var5);
      }
   }
}
