package org.bouncycastle.cert.path.validations;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ContentVerifierProviderBuilder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Memoable;

public class ParentCertIssuedValidation implements CertPathValidation {
   private X509ContentVerifierProviderBuilder contentVerifierProvider;
   private X500Name workingIssuerName;
   private SubjectPublicKeyInfo workingPublicKey;
   private AlgorithmIdentifier workingAlgId;

   public ParentCertIssuedValidation(X509ContentVerifierProviderBuilder var1) {
      this.contentVerifierProvider = var1;
   }

   @Override
   public void validate(CertPathValidationContext var1, X509CertificateHolder var2) throws CertPathValidationException {
      if (this.workingIssuerName != null && !this.workingIssuerName.equals(var2.getIssuer())) {
         throw new CertPathValidationException("Certificate issue does not match parent");
      } else {
         if (this.workingPublicKey != null) {
            try {
               SubjectPublicKeyInfo var3;
               if (this.workingPublicKey.getAlgorithm().equals(this.workingAlgId)) {
                  var3 = this.workingPublicKey;
               } else {
                  var3 = new SubjectPublicKeyInfo(this.workingAlgId, this.workingPublicKey.parsePublicKey());
               }

               if (!var2.isSignatureValid(this.contentVerifierProvider.build(var3))) {
                  throw new CertPathValidationException("Certificate signature not for public key in parent");
               }
            } catch (OperatorCreationException var4) {
               throw new CertPathValidationException("Unable to create verifier: " + var4.getMessage(), var4);
            } catch (CertException var5) {
               throw new CertPathValidationException("Unable to validate signature: " + var5.getMessage(), var5);
            } catch (IOException var6) {
               throw new CertPathValidationException("Unable to build public key: " + var6.getMessage(), var6);
            }
         }

         this.workingIssuerName = var2.getSubject();
         this.workingPublicKey = var2.getSubjectPublicKeyInfo();
         if (this.workingAlgId != null) {
            if (this.workingPublicKey.getAlgorithm().getAlgorithm().equals(this.workingAlgId.getAlgorithm())) {
               if (!this.isNull(this.workingPublicKey.getAlgorithm().getParameters())) {
                  this.workingAlgId = this.workingPublicKey.getAlgorithm();
               }
            } else {
               this.workingAlgId = this.workingPublicKey.getAlgorithm();
            }
         } else {
            this.workingAlgId = this.workingPublicKey.getAlgorithm();
         }
      }
   }

   private boolean isNull(ASN1Encodable var1) {
      return var1 == null || var1 instanceof ASN1Null;
   }

   @Override
   public Memoable copy() {
      ParentCertIssuedValidation var1 = new ParentCertIssuedValidation(this.contentVerifierProvider);
      var1.workingAlgId = this.workingAlgId;
      var1.workingIssuerName = this.workingIssuerName;
      var1.workingPublicKey = this.workingPublicKey;
      return var1;
   }

   @Override
   public void reset(Memoable var1) {
      ParentCertIssuedValidation var2 = (ParentCertIssuedValidation)var1;
      this.contentVerifierProvider = var2.contentVerifierProvider;
      this.workingAlgId = var2.workingAlgId;
      this.workingIssuerName = var2.workingIssuerName;
      this.workingPublicKey = var2.workingPublicKey;
   }
}
