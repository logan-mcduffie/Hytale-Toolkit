package org.bouncycastle.cert.cmp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertConfirmContent;
import org.bouncycastle.asn1.cmp.CertStatus;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;

public class CertificateConfirmationContentBuilder {
   private DigestAlgorithmIdentifierFinder digestAlgFinder;
   private List<CMPCertificate> acceptedCerts = new ArrayList<>();
   private List<AlgorithmIdentifier> acceptedSignatureAlgorithms = new ArrayList<>();
   private List<ASN1Integer> acceptedReqIds = new ArrayList<>();

   public CertificateConfirmationContentBuilder() {
      this(new DefaultDigestAlgorithmIdentifierFinder());
   }

   public CertificateConfirmationContentBuilder(DigestAlgorithmIdentifierFinder var1) {
      this.digestAlgFinder = var1;
   }

   public CertificateConfirmationContentBuilder addAcceptedCertificate(X509CertificateHolder var1, BigInteger var2) {
      return this.addAcceptedCertificate(var1, new ASN1Integer(var2));
   }

   public CertificateConfirmationContentBuilder addAcceptedCertificate(X509CertificateHolder var1, ASN1Integer var2) {
      return this.addAcceptedCertificate(new CMPCertificate(var1.toASN1Structure()), var1.getSignatureAlgorithm(), var2);
   }

   public CertificateConfirmationContentBuilder addAcceptedCertificate(CMPCertificate var1, AlgorithmIdentifier var2, ASN1Integer var3) {
      this.acceptedCerts.add(var1);
      this.acceptedSignatureAlgorithms.add(var2);
      this.acceptedReqIds.add(var3);
      return this;
   }

   public CertificateConfirmationContent build(DigestCalculatorProvider var1) throws CMPException {
      ASN1EncodableVector var2 = new ASN1EncodableVector(this.acceptedCerts.size());

      for (int var3 = 0; var3 != this.acceptedCerts.size(); var3++) {
         byte[] var4 = CMPUtil.calculateCertHash(this.acceptedCerts.get(var3), this.acceptedSignatureAlgorithms.get(var3), var1, this.digestAlgFinder);
         ASN1Integer var5 = this.acceptedReqIds.get(var3);
         var2.add(new CertStatus(var4, var5));
      }

      CertConfirmContent var6 = CertConfirmContent.getInstance(new DERSequence(var2));
      return new CertificateConfirmationContent(var6, this.digestAlgFinder);
   }
}
