package org.bouncycastle.cms.jcajce;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSSignatureEncryptionAlgorithmFinder;
import org.bouncycastle.cms.DefaultCMSSignatureEncryptionAlgorithmFinder;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class JcaSignerInfoGeneratorBuilder {
   private SignerInfoGeneratorBuilder builder;

   public JcaSignerInfoGeneratorBuilder(DigestCalculatorProvider var1) {
      this(var1, new DefaultCMSSignatureEncryptionAlgorithmFinder());
   }

   public JcaSignerInfoGeneratorBuilder(DigestCalculatorProvider var1, CMSSignatureEncryptionAlgorithmFinder var2) {
      this.builder = new SignerInfoGeneratorBuilder(var1, var2);
   }

   public JcaSignerInfoGeneratorBuilder setDirectSignature(boolean var1) {
      this.builder.setDirectSignature(var1);
      return this;
   }

   public JcaSignerInfoGeneratorBuilder setContentDigest(AlgorithmIdentifier var1) {
      this.builder.setContentDigest(var1);
      return this;
   }

   public JcaSignerInfoGeneratorBuilder setSignedAttributeGenerator(CMSAttributeTableGenerator var1) {
      this.builder.setSignedAttributeGenerator(var1);
      return this;
   }

   public JcaSignerInfoGeneratorBuilder setUnsignedAttributeGenerator(CMSAttributeTableGenerator var1) {
      this.builder.setUnsignedAttributeGenerator(var1);
      return this;
   }

   public SignerInfoGenerator build(ContentSigner var1, X509CertificateHolder var2) throws OperatorCreationException {
      return this.builder.build(var1, var2);
   }

   public SignerInfoGenerator build(ContentSigner var1, byte[] var2) throws OperatorCreationException {
      return this.builder.build(var1, var2);
   }

   public SignerInfoGenerator build(ContentSigner var1, X509Certificate var2) throws OperatorCreationException, CertificateEncodingException {
      return this.build(var1, new JcaX509CertificateHolder(var2));
   }
}
