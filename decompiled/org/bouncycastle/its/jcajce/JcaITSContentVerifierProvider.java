package org.bouncycastle.its.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSPublicVerificationKey;
import org.bouncycastle.its.operator.ITSContentVerifierProvider;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Arrays;

public class JcaITSContentVerifierProvider implements ITSContentVerifierProvider {
   private final ITSCertificate issuer;
   private final byte[] parentData;
   private final JcaJceHelper helper;
   private AlgorithmIdentifier digestAlgo;
   private ECPublicKey pubParams;
   private int sigChoice;

   private JcaITSContentVerifierProvider(ITSCertificate var1, JcaJceHelper var2) {
      this.issuer = var1;
      this.helper = var2;

      try {
         this.parentData = var1.getEncoded();
      } catch (IOException var6) {
         throw new IllegalStateException("unable to extract parent data: " + var6.getMessage());
      }

      ToBeSignedCertificate var3 = var1.toASN1Structure().getToBeSigned();
      VerificationKeyIndicator var4 = var3.getVerifyKeyIndicator();
      if (var4.getVerificationKeyIndicator() instanceof PublicVerificationKey) {
         PublicVerificationKey var5 = PublicVerificationKey.getInstance(var4.getVerificationKeyIndicator());
         this.initForPvi(var5, var2);
      } else {
         throw new IllegalArgumentException("not public verification key");
      }
   }

   private JcaITSContentVerifierProvider(ITSPublicVerificationKey var1, JcaJceHelper var2) {
      this.issuer = null;
      this.parentData = null;
      this.helper = var2;
      this.initForPvi(var1.toASN1Structure(), var2);
   }

   private void initForPvi(PublicVerificationKey var1, JcaJceHelper var2) {
      this.sigChoice = var1.getChoice();
      switch (var1.getChoice()) {
         case 0:
            this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
            break;
         case 1:
            this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
            break;
         case 2:
            this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384);
            break;
         default:
            throw new IllegalArgumentException("unknown key type");
      }

      this.pubParams = (ECPublicKey)new JcaITSPublicVerificationKey(var1, var2).getKey();
   }

   @Override
   public boolean hasAssociatedCertificate() {
      return this.issuer != null;
   }

   @Override
   public ITSCertificate getAssociatedCertificate() {
      return this.issuer;
   }

   @Override
   public ContentVerifier get(int var1) throws OperatorCreationException {
      if (this.sigChoice != var1) {
         throw new OperatorCreationException("wrong verifier for algorithm: " + var1);
      } else {
         DigestCalculatorProvider var2;
         try {
            JcaDigestCalculatorProviderBuilder var3 = new JcaDigestCalculatorProviderBuilder().setHelper(this.helper);
            var2 = var3.build();
         } catch (Exception var8) {
            throw new IllegalStateException(var8.getMessage(), var8);
         }

         final DigestCalculator var10 = var2.get(this.digestAlgo);

         try {
            final OutputStream var4 = var10.getOutputStream();
            if (this.parentData != null) {
               var4.write(this.parentData, 0, this.parentData.length);
            }

            final byte[] var5 = var10.getDigest();
            final byte[] var6;
            if (this.issuer != null && this.issuer.getIssuer().isSelf()) {
               byte[] var7 = OEREncoder.toByteArray(this.issuer.toASN1Structure().getToBeSigned(), IEEE1609dot2.ToBeSignedCertificate.build());
               var4.write(var7, 0, var7.length);
               var6 = var10.getDigest();
            } else {
               var6 = null;
            }

            final Signature var11;
            switch (this.sigChoice) {
               case 0:
               case 1:
                  var11 = this.helper.createSignature("SHA256withECDSA");
                  break;
               case 2:
                  var11 = this.helper.createSignature("SHA384withECDSA");
                  break;
               default:
                  throw new IllegalArgumentException("choice " + this.sigChoice + " not supported");
            }

            return new ContentVerifier() {
               @Override
               public AlgorithmIdentifier getAlgorithmIdentifier() {
                  return null;
               }

               @Override
               public OutputStream getOutputStream() {
                  return var4;
               }

               @Override
               public boolean verify(byte[] var1) {
                  byte[] var2x = var10.getDigest();

                  try {
                     var11.initVerify(JcaITSContentVerifierProvider.this.pubParams);
                     var11.update(var2x);
                     if (var6 != null && Arrays.areEqual(var2x, var6)) {
                        byte[] var3 = var10.getDigest();
                        var11.update(var3);
                     } else {
                        var11.update(var5);
                     }

                     return var11.verify(var1);
                  } catch (Exception var4x) {
                     throw new RuntimeException(var4x.getMessage(), var4x);
                  }
               }
            };
         } catch (Exception var9) {
            throw new IllegalStateException(var9.getMessage(), var9);
         }
      }
   }

   public static class Builder {
      private JcaJceHelper helper = new DefaultJcaJceHelper();

      public JcaITSContentVerifierProvider.Builder setProvider(Provider var1) {
         this.helper = new ProviderJcaJceHelper(var1);
         return this;
      }

      public JcaITSContentVerifierProvider.Builder setProvider(String var1) {
         this.helper = new NamedJcaJceHelper(var1);
         return this;
      }

      public JcaITSContentVerifierProvider build(ITSCertificate var1) {
         return new JcaITSContentVerifierProvider(var1, this.helper);
      }

      public JcaITSContentVerifierProvider build(ITSPublicVerificationKey var1) {
         return new JcaITSContentVerifierProvider(var1, this.helper);
      }
   }
}
