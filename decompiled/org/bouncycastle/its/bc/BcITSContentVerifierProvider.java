package org.bouncycastle.its.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.operator.ITSContentVerifierProvider;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.util.Arrays;

public class BcITSContentVerifierProvider implements ITSContentVerifierProvider {
   private final ITSCertificate issuer;
   private final byte[] parentData;
   private final AlgorithmIdentifier digestAlgo;
   private final ECPublicKeyParameters pubParams;
   private final int sigChoice;

   public BcITSContentVerifierProvider(ITSCertificate var1) throws IOException {
      this.issuer = var1;
      this.parentData = var1.getEncoded();
      ToBeSignedCertificate var2 = var1.toASN1Structure().getToBeSigned();
      VerificationKeyIndicator var3 = var2.getVerifyKeyIndicator();
      if (var3.getVerificationKeyIndicator() instanceof PublicVerificationKey) {
         PublicVerificationKey var4 = PublicVerificationKey.getInstance(var3.getVerificationKeyIndicator());
         this.sigChoice = var4.getChoice();
         switch (var4.getChoice()) {
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
               throw new IllegalStateException("unknown key type");
         }

         this.pubParams = (ECPublicKeyParameters)new BcITSPublicVerificationKey(var4).getKey();
      } else {
         throw new IllegalStateException("not public verification key");
      }
   }

   @Override
   public ITSCertificate getAssociatedCertificate() {
      return this.issuer;
   }

   @Override
   public boolean hasAssociatedCertificate() {
      return this.issuer != null;
   }

   @Override
   public ContentVerifier get(int var1) throws OperatorCreationException {
      if (this.sigChoice != var1) {
         throw new OperatorCreationException("wrong verifier for algorithm: " + var1);
      } else {
         final ExtendedDigest var2 = BcDefaultDigestProvider.INSTANCE.get(this.digestAlgo);
         final byte[] var3 = new byte[var2.getDigestSize()];
         var2.update(this.parentData, 0, this.parentData.length);
         var2.doFinal(var3, 0);
         final byte[] var4 = this.issuer.getIssuer().isSelf() ? new byte[var2.getDigestSize()] : null;
         if (var4 != null) {
            byte[] var5 = OEREncoder.toByteArray(this.issuer.toASN1Structure().getToBeSigned(), IEEE1609dot2.ToBeSignedCertificate.build());
            var2.update(var5, 0, var5.length);
            var2.doFinal(var4, 0);
         }

         final OutputStream var6 = new OutputStream() {
            @Override
            public void write(int var1) throws IOException {
               var2.update((byte)var1);
            }

            @Override
            public void write(byte[] var1) throws IOException {
               var2.update(var1, 0, var1.length);
            }

            @Override
            public void write(byte[] var1, int var2x, int var3x) throws IOException {
               var2.update(var1, var2x, var3x);
            }
         };
         return new ContentVerifier() {
            final DSADigestSigner signer = new DSADigestSigner(
               new ECDSASigner(), BcDefaultDigestProvider.INSTANCE.get(BcITSContentVerifierProvider.this.digestAlgo)
            );

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
               return null;
            }

            @Override
            public OutputStream getOutputStream() {
               return var6;
            }

            @Override
            public boolean verify(byte[] var1) {
               byte[] var2x = new byte[var2.getDigestSize()];
               var2.doFinal(var2x, 0);
               this.signer.init(false, BcITSContentVerifierProvider.this.pubParams);
               this.signer.update(var2x, 0, var2x.length);
               if (var4 != null && Arrays.areEqual(var2x, var4)) {
                  byte[] var3x = new byte[var2.getDigestSize()];
                  var2.doFinal(var3x, 0);
                  this.signer.update(var3x, 0, var3x.length);
               } else {
                  this.signer.update(var3, 0, var3.length);
               }

               return this.signer.verifySignature(var1);
            }
         };
      }
   }
}
