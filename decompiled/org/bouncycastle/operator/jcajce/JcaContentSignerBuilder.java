package org.bouncycastle.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.CompositePrivateKey;
import org.bouncycastle.jcajce.io.OutputStreamFactory;
import org.bouncycastle.jcajce.spec.CompositeAlgorithmSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.ExtendedContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.TeeOutputStream;

public class JcaContentSignerBuilder {
   private static final Set isAlgIdFromPrivate = new HashSet();
   private static final DefaultSignatureAlgorithmIdentifierFinder SIGNATURE_ALGORITHM_IDENTIFIER_FINDER = new DefaultSignatureAlgorithmIdentifierFinder();
   private final String signatureAlgorithm;
   private final AlgorithmIdentifier signatureDigestAlgorithm;
   private OperatorHelper helper = new OperatorHelper(new DefaultJcaJceHelper());
   private SecureRandom random;
   private AlgorithmIdentifier sigAlgId;
   private AlgorithmParameterSpec sigAlgSpec;

   public JcaContentSignerBuilder(String var1) {
      this(var1, (AlgorithmIdentifier)null);
   }

   private static AlgorithmIdentifier getSigDigAlgId(PublicKey var0) {
      byte[] var1 = var0.getEncoded();
      SubjectPublicKeyInfo var2 = SubjectPublicKeyInfo.getInstance(var1);
      if (var2.getAlgorithm().getAlgorithm().equals(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig)) {
         byte[] var3 = var2.getPublicKeyData().getOctets();
         int var4 = Pack.bigEndianToInt(var3, 4);
         LMSigParameters var5 = LMSigParameters.getParametersForType(var4);
         return new AlgorithmIdentifier(var5.getDigestOID());
      } else {
         return null;
      }
   }

   public JcaContentSignerBuilder(String var1, PublicKey var2) {
      this(var1, getSigDigAlgId(var2));
   }

   public JcaContentSignerBuilder(String var1, AlgorithmIdentifier var2) {
      this.signatureAlgorithm = var1;
      this.signatureDigestAlgorithm = var2;
   }

   public JcaContentSignerBuilder(String var1, AlgorithmParameterSpec var2) {
      this(var1, var2, null);
   }

   public JcaContentSignerBuilder(String var1, AlgorithmParameterSpec var2, AlgorithmIdentifier var3) {
      this.signatureAlgorithm = var1;
      this.signatureDigestAlgorithm = var3;
      if (var2 instanceof PSSParameterSpec) {
         PSSParameterSpec var4 = (PSSParameterSpec)var2;
         this.sigAlgSpec = var4;
         this.sigAlgId = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS, createPSSParams(var4));
      } else {
         if (!(var2 instanceof CompositeAlgorithmSpec)) {
            throw new IllegalArgumentException("unknown sigParamSpec: " + (var2 == null ? "null" : var2.getClass().getName()));
         }

         CompositeAlgorithmSpec var5 = (CompositeAlgorithmSpec)var2;
         this.sigAlgSpec = var5;
         this.sigAlgId = new AlgorithmIdentifier(MiscObjectIdentifiers.id_alg_composite, createCompParams(var5));
      }
   }

   public JcaContentSignerBuilder setProvider(Provider var1) {
      this.helper = new OperatorHelper(new ProviderJcaJceHelper(var1));
      return this;
   }

   public JcaContentSignerBuilder setProvider(String var1) {
      this.helper = new OperatorHelper(new NamedJcaJceHelper(var1));
      return this;
   }

   public JcaContentSignerBuilder setSecureRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   public ContentSigner build(PrivateKey var1) throws OperatorCreationException {
      if (var1 instanceof CompositePrivateKey
         && ((CompositePrivateKey)var1).getAlgorithmIdentifier().getAlgorithm().equals(MiscObjectIdentifiers.id_composite_key)) {
         return this.buildComposite((CompositePrivateKey)var1);
      } else {
         try {
            if (this.sigAlgSpec == null) {
               this.sigAlgId = this.getSigAlgId(var1);
            }

            final AlgorithmIdentifier var2 = this.sigAlgId;
            final Signature var3 = this.helper.createSignature(this.sigAlgId);
            if (this.random != null) {
               var3.initSign(var1, this.random);
            } else {
               var3.initSign(var1);
            }

            final ContentSigner var4 = new ContentSigner() {
               private OutputStream stream = OutputStreamFactory.createStream(var3);

               @Override
               public AlgorithmIdentifier getAlgorithmIdentifier() {
                  return var2;
               }

               @Override
               public OutputStream getOutputStream() {
                  return this.stream;
               }

               @Override
               public byte[] getSignature() {
                  try {
                     return var3.sign();
                  } catch (SignatureException var2x) {
                     throw new RuntimeOperatorException("exception obtaining signature: " + var2x.getMessage(), var2x);
                  }
               }
            };
            return (ContentSigner)(this.signatureDigestAlgorithm != null ? new ExtendedContentSigner() {
               private final AlgorithmIdentifier digestAlgorithm = JcaContentSignerBuilder.this.signatureDigestAlgorithm;
               private final ContentSigner signer = var4;

               @Override
               public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
                  return this.digestAlgorithm;
               }

               @Override
               public AlgorithmIdentifier getAlgorithmIdentifier() {
                  return this.signer.getAlgorithmIdentifier();
               }

               @Override
               public OutputStream getOutputStream() {
                  return this.signer.getOutputStream();
               }

               @Override
               public byte[] getSignature() {
                  return this.signer.getSignature();
               }
            } : var4);
         } catch (GeneralSecurityException var5) {
            throw new OperatorCreationException("cannot create signer: " + var5.getMessage(), var5);
         }
      }
   }

   private AlgorithmIdentifier getSigAlgId(PrivateKey var1) {
      if (isAlgIdFromPrivate.contains(Strings.toUpperCase(this.signatureAlgorithm))) {
         AlgorithmIdentifier var2 = SIGNATURE_ALGORITHM_IDENTIFIER_FINDER.find(var1.getAlgorithm());
         return var2 == null ? PrivateKeyInfo.getInstance(var1.getEncoded()).getPrivateKeyAlgorithm() : var2;
      } else {
         return SIGNATURE_ALGORITHM_IDENTIFIER_FINDER.find(this.signatureAlgorithm);
      }
   }

   private ContentSigner buildComposite(CompositePrivateKey var1) throws OperatorCreationException {
      try {
         List var2 = var1.getPrivateKeys();
         ASN1Sequence var3 = ASN1Sequence.getInstance(this.sigAlgId.getParameters());
         final Signature[] var4 = new Signature[var3.size()];

         for (int var5 = 0; var5 != var3.size(); var5++) {
            var4[var5] = this.helper.createSignature(AlgorithmIdentifier.getInstance(var3.getObjectAt(var5)));
            if (this.random != null) {
               var4[var5].initSign((PrivateKey)var2.get(var5), this.random);
            } else {
               var4[var5].initSign((PrivateKey)var2.get(var5));
            }
         }

         final Object var8 = OutputStreamFactory.createStream(var4[0]);

         for (int var6 = 1; var6 != var4.length; var6++) {
            var8 = new TeeOutputStream((OutputStream)var8, OutputStreamFactory.createStream(var4[var6]));
         }

         return new ContentSigner() {
            OutputStream stream = (OutputStream)var8;

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
               return JcaContentSignerBuilder.this.sigAlgId;
            }

            @Override
            public OutputStream getOutputStream() {
               return this.stream;
            }

            @Override
            public byte[] getSignature() {
               try {
                  ASN1EncodableVector var1x = new ASN1EncodableVector();

                  for (int var2x = 0; var2x != var4.length; var2x++) {
                     var1x.add(new DERBitString(var4[var2x].sign()));
                  }

                  return new DERSequence(var1x).getEncoded("DER");
               } catch (IOException var3x) {
                  throw new RuntimeOperatorException("exception encoding signature: " + var3x.getMessage(), var3x);
               } catch (SignatureException var4x) {
                  throw new RuntimeOperatorException("exception obtaining signature: " + var4x.getMessage(), var4x);
               }
            }
         };
      } catch (GeneralSecurityException var7) {
         throw new OperatorCreationException("cannot create signer: " + var7.getMessage(), var7);
      }
   }

   private static RSASSAPSSparams createPSSParams(PSSParameterSpec var0) {
      DefaultDigestAlgorithmIdentifierFinder var1 = new DefaultDigestAlgorithmIdentifierFinder();
      AlgorithmIdentifier var2 = var1.find(var0.getDigestAlgorithm());
      if (var2.getParameters() == null) {
         var2 = new AlgorithmIdentifier(var2.getAlgorithm(), DERNull.INSTANCE);
      }

      AlgorithmIdentifier var3 = var1.find(((MGF1ParameterSpec)var0.getMGFParameters()).getDigestAlgorithm());
      if (var3.getParameters() == null) {
         var3 = new AlgorithmIdentifier(var3.getAlgorithm(), DERNull.INSTANCE);
      }

      return new RSASSAPSSparams(
         var2, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, var3), new ASN1Integer(var0.getSaltLength()), new ASN1Integer(var0.getTrailerField())
      );
   }

   private static ASN1Sequence createCompParams(CompositeAlgorithmSpec var0) {
      DefaultSignatureAlgorithmIdentifierFinder var1 = new DefaultSignatureAlgorithmIdentifierFinder();
      ASN1EncodableVector var2 = new ASN1EncodableVector();
      List var3 = var0.getAlgorithmNames();
      List var4 = var0.getParameterSpecs();

      for (int var5 = 0; var5 != var3.size(); var5++) {
         AlgorithmParameterSpec var6 = (AlgorithmParameterSpec)var4.get(var5);
         if (var6 == null) {
            var2.add(var1.find((String)var3.get(var5)));
         } else {
            if (!(var6 instanceof PSSParameterSpec)) {
               throw new IllegalArgumentException("unrecognized parameterSpec");
            }

            var2.add(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS, createPSSParams((PSSParameterSpec)var6)));
         }
      }

      return new DERSequence(var2);
   }

   static {
      isAlgIdFromPrivate.add("COMPOSITE");
      isAlgIdFromPrivate.add("DILITHIUM");
      isAlgIdFromPrivate.add("SPHINCS+");
      isAlgIdFromPrivate.add("SPHINCSPlus");
      isAlgIdFromPrivate.add("ML-DSA");
      isAlgIdFromPrivate.add("SLH-DSA");
      isAlgIdFromPrivate.add("HASH-ML-DSA");
      isAlgIdFromPrivate.add("HASH-SLH-DSA");
   }
}
