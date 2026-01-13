package org.bouncycastle.cert.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertStatus;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.util.Arrays;

public class CertificateStatus {
   private DigestAlgorithmIdentifierFinder digestAlgFinder;
   private CertStatus certStatus;

   CertificateStatus(DigestAlgorithmIdentifierFinder var1, CertStatus var2) {
      this.digestAlgFinder = var1;
      this.certStatus = var2;
   }

   public PKIStatusInfo getStatusInfo() {
      return this.certStatus.getStatusInfo();
   }

   public BigInteger getCertRequestID() {
      return this.certStatus.getCertReqId().getValue();
   }

   public boolean isVerified(X509CertificateHolder var1, DigestCalculatorProvider var2) throws CMPException {
      return this.isVerified(new CMPCertificate(var1.toASN1Structure()), var1.getSignatureAlgorithm(), var2);
   }

   public boolean isVerified(CMPCertificate var1, AlgorithmIdentifier var2, DigestCalculatorProvider var3) throws CMPException {
      byte[] var4 = CMPUtil.calculateCertHash(var1, var2, var3, this.digestAlgFinder);
      return Arrays.constantTimeAreEqual(this.certStatus.getCertHash().getOctets(), var4);
   }
}
