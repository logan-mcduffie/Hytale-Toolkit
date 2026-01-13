package org.bouncycastle.cms.jcajce;

import java.security.PrivateKey;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceSymmetricKeyUnwrapper;

class NamedJcaJceExtHelper extends NamedJcaJceHelper implements JcaJceExtHelper {
   public NamedJcaJceExtHelper(String var1) {
      super(var1);
   }

   @Override
   public JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier var1, PrivateKey var2) {
      var2 = CMSUtils.cleanPrivateKey(var2);
      return new JceAsymmetricKeyUnwrapper(var1, var2).setProvider(this.providerName);
   }

   @Override
   public JceKTSKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier var1, PrivateKey var2, byte[] var3, byte[] var4) {
      var2 = CMSUtils.cleanPrivateKey(var2);
      return new JceKTSKeyUnwrapper(var1, var2, var3, var4).setProvider(this.providerName);
   }

   @Override
   public SymmetricKeyUnwrapper createSymmetricUnwrapper(AlgorithmIdentifier var1, SecretKey var2) {
      return new JceSymmetricKeyUnwrapper(var1, var2).setProvider(this.providerName);
   }

   @Override
   public AsymmetricKeyUnwrapper createKEMUnwrapper(AlgorithmIdentifier var1, PrivateKey var2) {
      return new JceCMSKEMKeyUnwrapper(var1, var2).setProvider(this.providerName);
   }
}
