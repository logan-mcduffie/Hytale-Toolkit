package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.ExtendedContentSigner;
import org.bouncycastle.operator.OperatorCreationException;

public class SignerInfoGeneratorBuilder {
   private final DigestAlgorithmIdentifierFinder digAlgFinder = new DefaultDigestAlgorithmIdentifierFinder();
   private DigestCalculatorProvider digestProvider;
   private boolean directSignature;
   private CMSAttributeTableGenerator signedGen;
   private CMSAttributeTableGenerator unsignedGen;
   private CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder;
   private AlgorithmIdentifier contentDigest;

   public SignerInfoGeneratorBuilder(DigestCalculatorProvider var1) {
      this(var1, new DefaultCMSSignatureEncryptionAlgorithmFinder());
   }

   public SignerInfoGeneratorBuilder(DigestCalculatorProvider var1, CMSSignatureEncryptionAlgorithmFinder var2) {
      this.digestProvider = var1;
      this.sigEncAlgFinder = var2;
   }

   public SignerInfoGeneratorBuilder setDirectSignature(boolean var1) {
      this.directSignature = var1;
      return this;
   }

   public SignerInfoGeneratorBuilder setContentDigest(AlgorithmIdentifier var1) {
      this.contentDigest = var1;
      return this;
   }

   public SignerInfoGeneratorBuilder setSignedAttributeGenerator(CMSAttributeTableGenerator var1) {
      this.signedGen = var1;
      return this;
   }

   public SignerInfoGeneratorBuilder setUnsignedAttributeGenerator(CMSAttributeTableGenerator var1) {
      this.unsignedGen = var1;
      return this;
   }

   public SignerInfoGenerator build(ContentSigner var1, X509CertificateHolder var2) throws OperatorCreationException {
      SignerIdentifier var3 = new SignerIdentifier(new IssuerAndSerialNumber(var2.toASN1Structure()));
      SignerInfoGenerator var4 = this.createGenerator(var1, var3);
      var4.setAssociatedCertificate(var2);
      return var4;
   }

   public SignerInfoGenerator build(ContentSigner var1, byte[] var2) throws OperatorCreationException {
      SignerIdentifier var3 = new SignerIdentifier((ASN1OctetString)(new DEROctetString(var2)));
      return this.createGenerator(var1, var3);
   }

   private SignerInfoGenerator createGenerator(ContentSigner var1, SignerIdentifier var2) throws OperatorCreationException {
      DigestCalculator var3;
      if (this.contentDigest != null) {
         var3 = this.digestProvider.get(this.contentDigest);
      } else {
         AlgorithmIdentifier var4 = null;
         if (var1 instanceof ExtendedContentSigner) {
            var4 = ((ExtendedContentSigner)var1).getDigestAlgorithmIdentifier();
         }

         if (var4 == null) {
            var4 = this.digAlgFinder.find(var1.getAlgorithmIdentifier());
         }

         if (var4 == null) {
            throw new OperatorCreationException("no digest algorithm specified for signature algorithm");
         }

         var3 = this.digestProvider.get(var4);
      }

      if (this.directSignature) {
         return new SignerInfoGenerator(var2, var1, var3.getAlgorithmIdentifier(), this.sigEncAlgFinder);
      } else if (this.signedGen == null && this.unsignedGen == null) {
         return new SignerInfoGenerator(var2, var1, var3, this.sigEncAlgFinder, new DefaultSignedAttributeTableGenerator(), null);
      } else {
         if (this.signedGen == null) {
            this.signedGen = new DefaultSignedAttributeTableGenerator();
         }

         return new SignerInfoGenerator(var2, var1, var3, this.sigEncAlgFinder, this.signedGen, this.unsignedGen);
      }
   }
}
