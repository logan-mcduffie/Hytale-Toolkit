package org.bouncycastle.cms.jcajce;

import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.KEMRecipientInfoGenerator;

public class JceKEMRecipientInfoGenerator extends KEMRecipientInfoGenerator {
   public JceKEMRecipientInfoGenerator(X509Certificate var1, ASN1ObjectIdentifier var2) throws CertificateEncodingException {
      super(new IssuerAndSerialNumber(new JcaX509CertificateHolder(var1).toASN1Structure()), new JceCMSKEMKeyWrapper(var1.getPublicKey(), var2));
   }

   public JceKEMRecipientInfoGenerator(byte[] var1, PublicKey var2, ASN1ObjectIdentifier var3) {
      super(var1, new JceCMSKEMKeyWrapper(var2, var3));
   }

   public JceKEMRecipientInfoGenerator setProvider(String var1) {
      ((JceCMSKEMKeyWrapper)this.wrapper).setProvider(var1);
      return this;
   }

   public JceKEMRecipientInfoGenerator setProvider(Provider var1) {
      ((JceCMSKEMKeyWrapper)this.wrapper).setProvider(var1);
      return this;
   }

   public JceKEMRecipientInfoGenerator setSecureRandom(SecureRandom var1) {
      ((JceCMSKEMKeyWrapper)this.wrapper).setSecureRandom(var1);
      return this;
   }

   public JceKEMRecipientInfoGenerator setKDF(AlgorithmIdentifier var1) {
      ((JceCMSKEMKeyWrapper)this.wrapper).setKDF(var1);
      return this;
   }

   public JceKEMRecipientInfoGenerator setAlgorithmMapping(ASN1ObjectIdentifier var1, String var2) {
      ((JceCMSKEMKeyWrapper)this.wrapper).setAlgorithmMapping(var1, var2);
      return this;
   }
}
