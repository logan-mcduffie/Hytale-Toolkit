package org.bouncycastle.cms.jcajce;

import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.cms.KEKIdentifier;
import org.bouncycastle.cms.KEKRecipientInfoGenerator;
import org.bouncycastle.operator.jcajce.JceSymmetricKeyWrapper;

public class JceKEKRecipientInfoGenerator extends KEKRecipientInfoGenerator {
   public JceKEKRecipientInfoGenerator(KEKIdentifier var1, SecretKey var2) {
      super(var1, new JceSymmetricKeyWrapper(var2));
   }

   public JceKEKRecipientInfoGenerator(byte[] var1, SecretKey var2) {
      this(new KEKIdentifier(var1, null, null), var2);
   }

   public JceKEKRecipientInfoGenerator setProvider(Provider var1) {
      ((JceSymmetricKeyWrapper)this.wrapper).setProvider(var1);
      return this;
   }

   public JceKEKRecipientInfoGenerator setProvider(String var1) {
      ((JceSymmetricKeyWrapper)this.wrapper).setProvider(var1);
      return this;
   }

   public JceKEKRecipientInfoGenerator setSecureRandom(SecureRandom var1) {
      ((JceSymmetricKeyWrapper)this.wrapper).setSecureRandom(var1);
      return this;
   }
}
