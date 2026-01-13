package org.bouncycastle.its;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateBase;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateType;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;

public class ITSImplicitCertificateBuilder extends ITSCertificateBuilder {
   private final IssuerIdentifier issuerIdentifier;

   public ITSImplicitCertificateBuilder(ITSCertificate var1, DigestCalculatorProvider var2, ToBeSignedCertificate.Builder var3) {
      super(var1, var3);
      AlgorithmIdentifier var4 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
      ASN1ObjectIdentifier var5 = var4.getAlgorithm();

      DigestCalculator var6;
      try {
         var6 = var2.get(var4);
      } catch (OperatorCreationException var10) {
         throw new IllegalStateException(var10.getMessage(), var10);
      }

      try {
         OutputStream var7 = var6.getOutputStream();
         var7.write(var1.getEncoded());
         var7.close();
      } catch (IOException var9) {
         throw new IllegalStateException(var9.getMessage(), var9);
      }

      byte[] var11 = var6.getDigest();
      HashedId8 var8 = new HashedId8(Arrays.copyOfRange(var11, var11.length - 8, var11.length));
      if (var5.equals(NISTObjectIdentifiers.id_sha256)) {
         this.issuerIdentifier = IssuerIdentifier.sha256AndDigest(var8);
      } else {
         if (!var5.equals(NISTObjectIdentifiers.id_sha384)) {
            throw new IllegalStateException("unknown digest");
         }

         this.issuerIdentifier = IssuerIdentifier.sha384AndDigest(var8);
      }
   }

   public ITSCertificate build(CertificateId var1, BigInteger var2, BigInteger var3) {
      return this.build(var1, var2, var3, null);
   }

   public ITSCertificate build(CertificateId var1, BigInteger var2, BigInteger var3, PublicEncryptionKey var4) {
      EccP256CurvePoint var5 = EccP256CurvePoint.uncompressedP256(var2, var3);
      ToBeSignedCertificate.Builder var6 = new ToBeSignedCertificate.Builder(this.tbsCertificateBuilder);
      var6.setId(var1);
      if (var4 != null) {
         var6.setEncryptionKey(var4);
      }

      var6.setVerifyKeyIndicator(VerificationKeyIndicator.reconstructionValue(var5));
      CertificateBase.Builder var7 = new CertificateBase.Builder();
      var7.setVersion(this.version);
      var7.setType(CertificateType.implicit);
      var7.setIssuer(this.issuerIdentifier);
      var7.setToBeSigned(var6.createToBeSignedCertificate());
      return new ITSCertificate(var7.createCertificateBase());
   }
}
