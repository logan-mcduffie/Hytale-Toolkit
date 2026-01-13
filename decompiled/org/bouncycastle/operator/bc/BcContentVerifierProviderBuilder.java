package org.bouncycastle.operator.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;

public abstract class BcContentVerifierProviderBuilder {
   protected BcDigestProvider digestProvider = BcDefaultDigestProvider.INSTANCE;

   public ContentVerifierProvider build(final X509CertificateHolder var1) throws OperatorCreationException {
      return new ContentVerifierProvider() {
         @Override
         public boolean hasAssociatedCertificate() {
            return true;
         }

         @Override
         public X509CertificateHolder getAssociatedCertificate() {
            return var1;
         }

         @Override
         public ContentVerifier get(AlgorithmIdentifier var1x) throws OperatorCreationException {
            try {
               AsymmetricKeyParameter var2 = BcContentVerifierProviderBuilder.this.extractKeyParameters(var1.getSubjectPublicKeyInfo());
               BcSignerOutputStream var3 = BcContentVerifierProviderBuilder.this.createSignatureStream(var1x, var2);
               return new BcContentVerifierProviderBuilder.SigVerifier(var1x, var3);
            } catch (IOException var4) {
               throw new OperatorCreationException("exception on setup: " + var4, var4);
            }
         }
      };
   }

   public ContentVerifierProvider build(final AsymmetricKeyParameter var1) throws OperatorCreationException {
      return new ContentVerifierProvider() {
         @Override
         public boolean hasAssociatedCertificate() {
            return false;
         }

         @Override
         public X509CertificateHolder getAssociatedCertificate() {
            return null;
         }

         @Override
         public ContentVerifier get(AlgorithmIdentifier var1x) throws OperatorCreationException {
            BcSignerOutputStream var2 = BcContentVerifierProviderBuilder.this.createSignatureStream(var1x, var1);
            return new BcContentVerifierProviderBuilder.SigVerifier(var1x, var2);
         }
      };
   }

   private BcSignerOutputStream createSignatureStream(AlgorithmIdentifier var1, AsymmetricKeyParameter var2) throws OperatorCreationException {
      Signer var3 = this.createSigner(var1);
      var3.init(false, var2);
      return new BcSignerOutputStream(var3);
   }

   protected abstract AsymmetricKeyParameter extractKeyParameters(SubjectPublicKeyInfo var1) throws IOException;

   protected abstract Signer createSigner(AlgorithmIdentifier var1) throws OperatorCreationException;

   private static class SigVerifier implements ContentVerifier {
      private BcSignerOutputStream stream;
      private AlgorithmIdentifier algorithm;

      SigVerifier(AlgorithmIdentifier var1, BcSignerOutputStream var2) {
         this.algorithm = var1;
         this.stream = var2;
      }

      @Override
      public AlgorithmIdentifier getAlgorithmIdentifier() {
         return this.algorithm;
      }

      @Override
      public OutputStream getOutputStream() {
         if (this.stream == null) {
            throw new IllegalStateException("verifier not initialised");
         } else {
            return this.stream;
         }
      }

      @Override
      public boolean verify(byte[] var1) {
         return this.stream.verify(var1);
      }
   }
}
