package org.bouncycastle.operator.jcajce;

import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jcajce.io.OutputStreamFactory;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RawContentVerifier;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.util.io.TeeOutputStream;

public class JcaContentVerifierProviderBuilder {
   private OperatorHelper helper = new OperatorHelper(new DefaultJcaJceHelper());

   public JcaContentVerifierProviderBuilder setProvider(Provider var1) {
      this.helper = new OperatorHelper(new ProviderJcaJceHelper(var1));
      return this;
   }

   public JcaContentVerifierProviderBuilder setProvider(String var1) {
      this.helper = new OperatorHelper(new NamedJcaJceHelper(var1));
      return this;
   }

   public ContentVerifierProvider build(X509CertificateHolder var1) throws OperatorCreationException, CertificateException {
      return this.build(this.helper.convertCertificate(var1));
   }

   public ContentVerifierProvider build(final X509Certificate var1) throws OperatorCreationException {
      final JcaX509CertificateHolder var2;
      try {
         var2 = new JcaX509CertificateHolder(var1);
      } catch (CertificateEncodingException var4) {
         throw new OperatorCreationException("cannot process certificate: " + var4.getMessage(), var4);
      }

      return new ContentVerifierProvider() {
         @Override
         public boolean hasAssociatedCertificate() {
            return true;
         }

         @Override
         public X509CertificateHolder getAssociatedCertificate() {
            return var2;
         }

         @Override
         public ContentVerifier get(AlgorithmIdentifier var1x) throws OperatorCreationException {
            if (var1x.getAlgorithm().equals(MiscObjectIdentifiers.id_alg_composite)) {
               return JcaContentVerifierProviderBuilder.this.createCompositeVerifier(var1x, var1.getPublicKey());
            } else {
               Signature var2x;
               try {
                  var2x = JcaContentVerifierProviderBuilder.this.helper.createSignature(var1x);
                  var2x.initVerify(var1.getPublicKey());
               } catch (GeneralSecurityException var4) {
                  throw new OperatorCreationException("exception on setup: " + var4, var4);
               }

               Signature var3 = JcaContentVerifierProviderBuilder.this.createRawSig(var1x, var1.getPublicKey());
               return (ContentVerifier)(var3 != null
                  ? new JcaContentVerifierProviderBuilder.RawSigVerifier(var1x, var2x, var3)
                  : new JcaContentVerifierProviderBuilder.SigVerifier(var1x, var2x));
            }
         }
      };
   }

   public ContentVerifierProvider build(final PublicKey var1) throws OperatorCreationException {
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
            if (var1x.getAlgorithm().equals(MiscObjectIdentifiers.id_alg_composite)) {
               return JcaContentVerifierProviderBuilder.this.createCompositeVerifier(var1x, var1);
            } else if (var1 instanceof CompositePublicKey
               && ((CompositePublicKey)var1).getAlgorithmIdentifier().getAlgorithm().equals(MiscObjectIdentifiers.id_composite_key)) {
               List var7 = ((CompositePublicKey)var1).getPublicKeys();

               for (int var8 = 0; var8 != var7.size(); var8++) {
                  try {
                     Signature var4 = JcaContentVerifierProviderBuilder.this.createSignature(var1x, (PublicKey)var7.get(var8));
                     Signature var5 = JcaContentVerifierProviderBuilder.this.createRawSig(var1x, (PublicKey)var7.get(var8));
                     if (var5 != null) {
                        return new JcaContentVerifierProviderBuilder.RawSigVerifier(var1x, var4, var5);
                     }

                     return new JcaContentVerifierProviderBuilder.SigVerifier(var1x, var4);
                  } catch (OperatorCreationException var6) {
                  }
               }

               throw new OperatorCreationException("no matching algorithm found for key");
            } else {
               Signature var2 = JcaContentVerifierProviderBuilder.this.createSignature(var1x, var1);
               Signature var3 = JcaContentVerifierProviderBuilder.this.createRawSig(var1x, var1);
               return (ContentVerifier)(var3 != null
                  ? new JcaContentVerifierProviderBuilder.RawSigVerifier(var1x, var2, var3)
                  : new JcaContentVerifierProviderBuilder.SigVerifier(var1x, var2));
            }
         }
      };
   }

   public ContentVerifierProvider build(SubjectPublicKeyInfo var1) throws OperatorCreationException {
      return this.build(this.helper.convertPublicKey(var1));
   }

   private ContentVerifier createCompositeVerifier(AlgorithmIdentifier var1, PublicKey var2) throws OperatorCreationException {
      if (var2 instanceof CompositePublicKey) {
         List var9 = ((CompositePublicKey)var2).getPublicKeys();
         ASN1Sequence var10 = ASN1Sequence.getInstance(var1.getParameters());
         Signature[] var11 = new Signature[var10.size()];

         for (int var12 = 0; var12 != var10.size(); var12++) {
            AlgorithmIdentifier var7 = AlgorithmIdentifier.getInstance(var10.getObjectAt(var12));
            if (var9.get(var12) != null) {
               var11[var12] = this.createSignature(var7, (PublicKey)var9.get(var12));
            } else {
               var11[var12] = null;
            }
         }

         return new JcaContentVerifierProviderBuilder.CompositeVerifier(var11);
      } else {
         ASN1Sequence var3 = ASN1Sequence.getInstance(var1.getParameters());
         Signature[] var4 = new Signature[var3.size()];

         for (int var5 = 0; var5 != var3.size(); var5++) {
            AlgorithmIdentifier var6 = AlgorithmIdentifier.getInstance(var3.getObjectAt(var5));

            try {
               var4[var5] = this.createSignature(var6, var2);
            } catch (Exception var8) {
               var4[var5] = null;
            }
         }

         return new JcaContentVerifierProviderBuilder.CompositeVerifier(var4);
      }
   }

   private Signature createSignature(AlgorithmIdentifier var1, PublicKey var2) throws OperatorCreationException {
      try {
         Signature var3 = this.helper.createSignature(var1);
         var3.initVerify(var2);
         return var3;
      } catch (GeneralSecurityException var4) {
         throw new OperatorCreationException("exception on setup: " + var4, var4);
      }
   }

   private Signature createRawSig(AlgorithmIdentifier var1, PublicKey var2) {
      Signature var3;
      try {
         var3 = this.helper.createRawSignature(var1);
         if (var3 != null) {
            var3.initVerify(var2);
         }
      } catch (Exception var5) {
         var3 = null;
      }

      return var3;
   }

   private static class CompositeVerifier implements ContentVerifier {
      private Signature[] sigs;
      private OutputStream stream;

      public CompositeVerifier(Signature[] var1) throws OperatorCreationException {
         this.sigs = var1;
         int var2 = 0;

         while (var2 < var1.length && var1[var2] == null) {
            var2++;
         }

         if (var2 == var1.length) {
            throw new OperatorCreationException("no matching signature found in composite");
         } else {
            this.stream = OutputStreamFactory.createStream(var1[var2]);

            for (int var3 = var2 + 1; var3 != var1.length; var3++) {
               if (var1[var3] != null) {
                  this.stream = new TeeOutputStream(this.stream, OutputStreamFactory.createStream(var1[var3]));
               }
            }
         }
      }

      @Override
      public AlgorithmIdentifier getAlgorithmIdentifier() {
         return new AlgorithmIdentifier(MiscObjectIdentifiers.id_alg_composite);
      }

      @Override
      public OutputStream getOutputStream() {
         return this.stream;
      }

      @Override
      public boolean verify(byte[] var1) {
         try {
            ASN1Sequence var2 = ASN1Sequence.getInstance(var1);
            boolean var3 = false;

            for (int var4 = 0; var4 != var2.size(); var4++) {
               if (this.sigs[var4] != null && !this.sigs[var4].verify(ASN1BitString.getInstance(var2.getObjectAt(var4)).getOctets())) {
                  var3 = true;
               }
            }

            return !var3;
         } catch (SignatureException var5) {
            throw new RuntimeOperatorException("exception obtaining signature: " + var5.getMessage(), var5);
         }
      }
   }

   private static class RawSigVerifier extends JcaContentVerifierProviderBuilder.SigVerifier implements RawContentVerifier {
      private Signature rawSignature;

      RawSigVerifier(AlgorithmIdentifier var1, Signature var2, Signature var3) {
         super(var1, var2);
         this.rawSignature = var3;
      }

      @Override
      public boolean verify(byte[] var1) {
         boolean var2;
         try {
            var2 = super.verify(var1);
         } finally {
            try {
               this.rawSignature.verify(var1);
            } catch (Exception var9) {
            }
         }

         return var2;
      }

      @Override
      public boolean verify(byte[] var1, byte[] var2) {
         boolean var3;
         try {
            this.rawSignature.update(var1);
            var3 = this.rawSignature.verify(var2);
         } catch (SignatureException var12) {
            throw new RuntimeOperatorException("exception obtaining raw signature: " + var12.getMessage(), var12);
         } finally {
            try {
               this.rawSignature.verify(var2);
            } catch (Exception var11) {
            }
         }

         return var3;
      }
   }

   private static class SigVerifier implements ContentVerifier {
      private final AlgorithmIdentifier algorithm;
      private final Signature signature;
      protected final OutputStream stream;

      SigVerifier(AlgorithmIdentifier var1, Signature var2) {
         this.algorithm = var1;
         this.signature = var2;
         this.stream = OutputStreamFactory.createStream(var2);
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
         try {
            return this.signature.verify(var1);
         } catch (SignatureException var3) {
            throw new RuntimeOperatorException("exception obtaining signature: " + var3.getMessage(), var3);
         }
      }
   }
}
