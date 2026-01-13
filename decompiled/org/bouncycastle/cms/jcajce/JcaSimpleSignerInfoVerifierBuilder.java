package org.bouncycastle.cms.jcajce;

import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class JcaSimpleSignerInfoVerifierBuilder {
   private JcaSimpleSignerInfoVerifierBuilder.Helper helper = new JcaSimpleSignerInfoVerifierBuilder.Helper();
   private DigestCalculatorProvider digestCalculatorProvider = null;

   public JcaSimpleSignerInfoVerifierBuilder setProvider(Provider var1) {
      this.helper = new JcaSimpleSignerInfoVerifierBuilder.ProviderHelper(var1);
      return this;
   }

   public JcaSimpleSignerInfoVerifierBuilder setProvider(String var1) {
      this.helper = new JcaSimpleSignerInfoVerifierBuilder.NamedHelper(var1);
      return this;
   }

   public JcaSimpleSignerInfoVerifierBuilder setDigestCalculatorProvider(DigestCalculatorProvider var1) {
      this.digestCalculatorProvider = var1;
      return this;
   }

   public SignerInformationVerifier build(X509CertificateHolder var1) throws OperatorCreationException, CertificateException {
      return new SignerInformationVerifier(
         new DefaultCMSSignatureAlgorithmNameGenerator(),
         new DefaultSignatureAlgorithmIdentifierFinder(),
         this.helper.createContentVerifierProvider(var1),
         this.getDigestCalculatorProvider()
      );
   }

   public SignerInformationVerifier build(X509Certificate var1) throws OperatorCreationException {
      return new SignerInformationVerifier(
         new DefaultCMSSignatureAlgorithmNameGenerator(),
         new DefaultSignatureAlgorithmIdentifierFinder(),
         this.helper.createContentVerifierProvider(var1),
         this.getDigestCalculatorProvider()
      );
   }

   public SignerInformationVerifier build(PublicKey var1) throws OperatorCreationException {
      return new SignerInformationVerifier(
         new DefaultCMSSignatureAlgorithmNameGenerator(),
         new DefaultSignatureAlgorithmIdentifierFinder(),
         this.helper.createContentVerifierProvider(var1),
         this.getDigestCalculatorProvider()
      );
   }

   private DigestCalculatorProvider getDigestCalculatorProvider() throws OperatorCreationException {
      return this.digestCalculatorProvider != null ? this.digestCalculatorProvider : this.helper.createDigestCalculatorProvider();
   }

   private static class Helper {
      private Helper() {
      }

      ContentVerifierProvider createContentVerifierProvider(PublicKey var1) throws OperatorCreationException {
         return new JcaContentVerifierProviderBuilder().build(var1);
      }

      ContentVerifierProvider createContentVerifierProvider(X509Certificate var1) throws OperatorCreationException {
         return new JcaContentVerifierProviderBuilder().build(var1);
      }

      ContentVerifierProvider createContentVerifierProvider(X509CertificateHolder var1) throws OperatorCreationException, CertificateException {
         return new JcaContentVerifierProviderBuilder().build(var1);
      }

      DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
         return new JcaDigestCalculatorProviderBuilder().build();
      }
   }

   private static class NamedHelper extends JcaSimpleSignerInfoVerifierBuilder.Helper {
      private final String providerName;

      public NamedHelper(String var1) {
         this.providerName = var1;
      }

      @Override
      ContentVerifierProvider createContentVerifierProvider(PublicKey var1) throws OperatorCreationException {
         return new JcaContentVerifierProviderBuilder().setProvider(this.providerName).build(var1);
      }

      @Override
      ContentVerifierProvider createContentVerifierProvider(X509Certificate var1) throws OperatorCreationException {
         return new JcaContentVerifierProviderBuilder().setProvider(this.providerName).build(var1);
      }

      @Override
      DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
         return new JcaDigestCalculatorProviderBuilder().setProvider(this.providerName).build();
      }

      @Override
      ContentVerifierProvider createContentVerifierProvider(X509CertificateHolder var1) throws OperatorCreationException, CertificateException {
         return new JcaContentVerifierProviderBuilder().setProvider(this.providerName).build(var1);
      }
   }

   private static class ProviderHelper extends JcaSimpleSignerInfoVerifierBuilder.Helper {
      private final Provider provider;

      public ProviderHelper(Provider var1) {
         this.provider = var1;
      }

      @Override
      ContentVerifierProvider createContentVerifierProvider(PublicKey var1) throws OperatorCreationException {
         return new JcaContentVerifierProviderBuilder().setProvider(this.provider).build(var1);
      }

      @Override
      ContentVerifierProvider createContentVerifierProvider(X509Certificate var1) throws OperatorCreationException {
         return new JcaContentVerifierProviderBuilder().setProvider(this.provider).build(var1);
      }

      @Override
      DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
         return new JcaDigestCalculatorProviderBuilder().setProvider(this.provider).build();
      }

      @Override
      ContentVerifierProvider createContentVerifierProvider(X509CertificateHolder var1) throws OperatorCreationException, CertificateException {
         return new JcaContentVerifierProviderBuilder().setProvider(this.provider).build(var1);
      }
   }
}
