package org.bouncycastle.cms.bc;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.SignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentVerifierProviderBuilder;

public class BcRSASignerInfoVerifierBuilder {
   private BcRSAContentVerifierProviderBuilder contentVerifierProviderBuilder;
   private DigestCalculatorProvider digestCalculatorProvider;
   private CMSSignatureAlgorithmNameGenerator sigAlgNameGen;
   private SignatureAlgorithmIdentifierFinder sigAlgIdFinder;

   public BcRSASignerInfoVerifierBuilder(
      CMSSignatureAlgorithmNameGenerator var1, SignatureAlgorithmIdentifierFinder var2, DigestAlgorithmIdentifierFinder var3, DigestCalculatorProvider var4
   ) {
      this.sigAlgNameGen = var1;
      this.sigAlgIdFinder = var2;
      this.contentVerifierProviderBuilder = new BcRSAContentVerifierProviderBuilder(var3);
      this.digestCalculatorProvider = var4;
   }

   public SignerInformationVerifier build(X509CertificateHolder var1) throws OperatorCreationException {
      return new SignerInformationVerifier(
         this.sigAlgNameGen, this.sigAlgIdFinder, this.contentVerifierProviderBuilder.build(var1), this.digestCalculatorProvider
      );
   }

   public SignerInformationVerifier build(AsymmetricKeyParameter var1) throws OperatorCreationException {
      return new SignerInformationVerifier(
         this.sigAlgNameGen, this.sigAlgIdFinder, this.contentVerifierProviderBuilder.build(var1), this.digestCalculatorProvider
      );
   }
}
