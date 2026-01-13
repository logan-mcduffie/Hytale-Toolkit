package org.bouncycastle.its;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.its.operator.ECDSAEncoder;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateBase;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateType;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashAlgorithm;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.util.Arrays;

public class ITSExplicitCertificateBuilder extends ITSCertificateBuilder {
   private final ITSContentSigner signer;

   public ITSExplicitCertificateBuilder(ITSContentSigner var1, ToBeSignedCertificate.Builder var2) {
      super(var2);
      this.signer = var1;
   }

   public ITSCertificate build(CertificateId var1, ITSPublicVerificationKey var2) {
      return this.build(var1, var2, null);
   }

   public ITSCertificate build(CertificateId var1, ITSPublicVerificationKey var2, ITSPublicEncryptionKey var3) {
      ToBeSignedCertificate.Builder var4 = new ToBeSignedCertificate.Builder(this.tbsCertificateBuilder);
      var4.setId(var1);
      if (var3 != null) {
         var4.setEncryptionKey(var3.toASN1Structure());
      }

      var4.setVerifyKeyIndicator(VerificationKeyIndicator.verificationKey(var2.toASN1Structure()));
      ToBeSignedCertificate var5 = var4.createToBeSignedCertificate();
      ToBeSignedCertificate var6 = null;
      VerificationKeyIndicator var7;
      if (this.signer.isForSelfSigning()) {
         var7 = var5.getVerifyKeyIndicator();
      } else {
         var6 = this.signer.getAssociatedCertificate().toASN1Structure().getToBeSigned();
         var7 = var6.getVerifyKeyIndicator();
      }

      OutputStream var8 = this.signer.getOutputStream();

      try {
         var8.write(OEREncoder.toByteArray(var5, IEEE1609dot2.ToBeSignedCertificate.build()));
         var8.close();
      } catch (IOException var15) {
         throw new IllegalArgumentException("cannot produce certificate signature");
      }

      Object var9 = null;
      switch (var7.getChoice()) {
         case 0:
            var9 = ECDSAEncoder.toITS(SECObjectIdentifiers.secp256r1, this.signer.getSignature());
            break;
         case 1:
            var9 = ECDSAEncoder.toITS(TeleTrusTObjectIdentifiers.brainpoolP256r1, this.signer.getSignature());
            break;
         case 2:
            var9 = ECDSAEncoder.toITS(TeleTrusTObjectIdentifiers.brainpoolP384r1, this.signer.getSignature());
            break;
         default:
            throw new IllegalStateException("unknown key type");
      }

      CertificateBase.Builder var10 = new CertificateBase.Builder();
      ASN1ObjectIdentifier var11 = this.signer.getDigestAlgorithm().getAlgorithm();
      IssuerIdentifier var12;
      if (this.signer.isForSelfSigning()) {
         if (var11.equals(NISTObjectIdentifiers.id_sha256)) {
            var12 = IssuerIdentifier.self(HashAlgorithm.sha256);
         } else {
            if (!var11.equals(NISTObjectIdentifiers.id_sha384)) {
               throw new IllegalStateException("unknown digest");
            }

            var12 = IssuerIdentifier.self(HashAlgorithm.sha384);
         }
      } else {
         byte[] var13 = this.signer.getAssociatedCertificateDigest();
         HashedId8 var14 = new HashedId8(Arrays.copyOfRange(var13, var13.length - 8, var13.length));
         if (var11.equals(NISTObjectIdentifiers.id_sha256)) {
            var12 = IssuerIdentifier.sha256AndDigest(var14);
         } else {
            if (!var11.equals(NISTObjectIdentifiers.id_sha384)) {
               throw new IllegalStateException("unknown digest");
            }

            var12 = IssuerIdentifier.sha384AndDigest(var14);
         }
      }

      var10.setVersion(this.version);
      var10.setType(CertificateType.explicit);
      var10.setIssuer(var12);
      var10.setToBeSigned(var5);
      var10.setSignature((Signature)var9);
      return new ITSCertificate(var10.createCertificateBase());
   }
}
