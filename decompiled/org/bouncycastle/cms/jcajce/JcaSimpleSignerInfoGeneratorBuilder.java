package org.bouncycastle.cms.jcajce;

import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class JcaSimpleSignerInfoGeneratorBuilder {
   private JcaSimpleSignerInfoGeneratorBuilder.Helper helper = new JcaSimpleSignerInfoGeneratorBuilder.Helper();
   private boolean hasNoSignedAttributes;
   private CMSAttributeTableGenerator signedGen;
   private CMSAttributeTableGenerator unsignedGen;
   private AlgorithmIdentifier contentDigest;

   public JcaSimpleSignerInfoGeneratorBuilder() throws OperatorCreationException {
   }

   public JcaSimpleSignerInfoGeneratorBuilder setProvider(String var1) throws OperatorCreationException {
      this.helper = new JcaSimpleSignerInfoGeneratorBuilder.NamedHelper(var1);
      return this;
   }

   public JcaSimpleSignerInfoGeneratorBuilder setProvider(Provider var1) throws OperatorCreationException {
      this.helper = new JcaSimpleSignerInfoGeneratorBuilder.ProviderHelper(var1);
      return this;
   }

   public JcaSimpleSignerInfoGeneratorBuilder setDirectSignature(boolean var1) {
      this.hasNoSignedAttributes = var1;
      return this;
   }

   public JcaSimpleSignerInfoGeneratorBuilder setContentDigest(AlgorithmIdentifier var1) {
      this.contentDigest = var1;
      return this;
   }

   public JcaSimpleSignerInfoGeneratorBuilder setSignedAttributeGenerator(CMSAttributeTableGenerator var1) {
      this.signedGen = var1;
      return this;
   }

   public JcaSimpleSignerInfoGeneratorBuilder setSignedAttributeGenerator(AttributeTable var1) {
      this.signedGen = new DefaultSignedAttributeTableGenerator(var1);
      return this;
   }

   public JcaSimpleSignerInfoGeneratorBuilder setUnsignedAttributeGenerator(CMSAttributeTableGenerator var1) {
      this.unsignedGen = var1;
      return this;
   }

   public SignerInfoGenerator build(String var1, PrivateKey var2, X509CertificateHolder var3) throws OperatorCreationException {
      var2 = CMSUtils.cleanPrivateKey(var2);
      ContentSigner var4 = this.helper.createContentSigner(var1, var2);
      return this.configureAndBuild().build(var4, var3);
   }

   public SignerInfoGenerator build(String var1, PrivateKey var2, X509Certificate var3) throws OperatorCreationException, CertificateEncodingException {
      var2 = CMSUtils.cleanPrivateKey(var2);
      ContentSigner var4 = this.helper.createContentSigner(var1, var2);
      return this.configureAndBuild().build(var4, new JcaX509CertificateHolder(var3));
   }

   public SignerInfoGenerator build(String var1, PrivateKey var2, byte[] var3) throws OperatorCreationException {
      var2 = CMSUtils.cleanPrivateKey(var2);
      ContentSigner var4 = this.helper.createContentSigner(var1, var2);
      return this.configureAndBuild().build(var4, var3);
   }

   private SignerInfoGeneratorBuilder configureAndBuild() throws OperatorCreationException {
      SignerInfoGeneratorBuilder var1 = new SignerInfoGeneratorBuilder(this.helper.createDigestCalculatorProvider());
      var1.setDirectSignature(this.hasNoSignedAttributes);
      var1.setContentDigest(this.contentDigest);
      var1.setSignedAttributeGenerator(this.signedGen);
      var1.setUnsignedAttributeGenerator(this.unsignedGen);
      return var1;
   }

   private static class Helper {
      private Helper() {
      }

      ContentSigner createContentSigner(String var1, PrivateKey var2) throws OperatorCreationException {
         var2 = CMSUtils.cleanPrivateKey(var2);
         return new JcaContentSignerBuilder(var1).build(var2);
      }

      DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
         return new JcaDigestCalculatorProviderBuilder().build();
      }
   }

   private static class NamedHelper extends JcaSimpleSignerInfoGeneratorBuilder.Helper {
      private final String providerName;

      public NamedHelper(String var1) {
         this.providerName = var1;
      }

      @Override
      ContentSigner createContentSigner(String var1, PrivateKey var2) throws OperatorCreationException {
         var2 = CMSUtils.cleanPrivateKey(var2);
         return new JcaContentSignerBuilder(var1).setProvider(this.providerName).build(var2);
      }

      @Override
      DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
         return new JcaDigestCalculatorProviderBuilder().setProvider(this.providerName).build();
      }
   }

   private static class ProviderHelper extends JcaSimpleSignerInfoGeneratorBuilder.Helper {
      private final Provider provider;

      public ProviderHelper(Provider var1) {
         this.provider = var1;
      }

      @Override
      ContentSigner createContentSigner(String var1, PrivateKey var2) throws OperatorCreationException {
         var2 = CMSUtils.cleanPrivateKey(var2);
         return new JcaContentSignerBuilder(var1).setProvider(this.provider).build(var2);
      }

      @Override
      DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
         return new JcaDigestCalculatorProviderBuilder().setProvider(this.provider).build();
      }
   }
}
