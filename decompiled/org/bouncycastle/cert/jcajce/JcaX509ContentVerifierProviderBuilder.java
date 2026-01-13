package org.bouncycastle.cert.jcajce;

import java.security.Provider;
import java.security.cert.CertificateException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ContentVerifierProviderBuilder;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;

public class JcaX509ContentVerifierProviderBuilder implements X509ContentVerifierProviderBuilder {
   private JcaContentVerifierProviderBuilder builder = new JcaContentVerifierProviderBuilder();

   public JcaX509ContentVerifierProviderBuilder setProvider(Provider var1) {
      this.builder.setProvider(var1);
      return this;
   }

   public JcaX509ContentVerifierProviderBuilder setProvider(String var1) {
      this.builder.setProvider(var1);
      return this;
   }

   @Override
   public ContentVerifierProvider build(SubjectPublicKeyInfo var1) throws OperatorCreationException {
      return this.builder.build(var1);
   }

   @Override
   public ContentVerifierProvider build(X509CertificateHolder var1) throws OperatorCreationException {
      try {
         return this.builder.build(var1);
      } catch (CertificateException var3) {
         throw new OperatorCreationException("Unable to process certificate: " + var3.getMessage(), var3);
      }
   }
}
