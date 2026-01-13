package org.bouncycastle.cms.jcajce;

import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.KeyTransRecipientInfoGenerator;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyWrapper;

public class JceKeyTransRecipientInfoGenerator extends KeyTransRecipientInfoGenerator {
   public JceKeyTransRecipientInfoGenerator(X509Certificate var1) throws CertificateEncodingException {
      super(new IssuerAndSerialNumber(new JcaX509CertificateHolder(var1).toASN1Structure()), new JceAsymmetricKeyWrapper(var1));
   }

   public JceKeyTransRecipientInfoGenerator(X509Certificate var1, AsymmetricKeyWrapper var2) throws CertificateEncodingException {
      super(new IssuerAndSerialNumber(new JcaX509CertificateHolder(var1).toASN1Structure()), var2);
   }

   public JceKeyTransRecipientInfoGenerator(byte[] var1, PublicKey var2) {
      super(var1, new JceAsymmetricKeyWrapper(var2));
   }

   public JceKeyTransRecipientInfoGenerator(byte[] var1, AsymmetricKeyWrapper var2) {
      super(var1, var2);
   }

   public JceKeyTransRecipientInfoGenerator(X509Certificate var1, AlgorithmIdentifier var2) throws CertificateEncodingException {
      super(new IssuerAndSerialNumber(new JcaX509CertificateHolder(var1).toASN1Structure()), new JceAsymmetricKeyWrapper(var2, var1.getPublicKey()));
   }

   public JceKeyTransRecipientInfoGenerator(byte[] var1, AlgorithmIdentifier var2, PublicKey var3) {
      super(var1, new JceAsymmetricKeyWrapper(var2, var3));
   }

   public JceKeyTransRecipientInfoGenerator setProvider(String var1) {
      ((JceAsymmetricKeyWrapper)this.wrapper).setProvider(var1);
      return this;
   }

   public JceKeyTransRecipientInfoGenerator setProvider(Provider var1) {
      ((JceAsymmetricKeyWrapper)this.wrapper).setProvider(var1);
      return this;
   }

   public JceKeyTransRecipientInfoGenerator setAlgorithmMapping(ASN1ObjectIdentifier var1, String var2) {
      ((JceAsymmetricKeyWrapper)this.wrapper).setAlgorithmMapping(var1, var2);
      return this;
   }
}
