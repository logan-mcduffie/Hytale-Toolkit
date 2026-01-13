package org.bouncycastle.cert.cmp;

import org.bouncycastle.asn1.cmp.CertConfirmContent;
import org.bouncycastle.asn1.cmp.CertStatus;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class CertificateConfirmationContent {
   private DigestAlgorithmIdentifierFinder digestAlgFinder;
   private CertConfirmContent content;

   public CertificateConfirmationContent(CertConfirmContent var1) {
      this(var1, new DefaultDigestAlgorithmIdentifierFinder());
   }

   public CertificateConfirmationContent(CertConfirmContent var1, DigestAlgorithmIdentifierFinder var2) {
      this.digestAlgFinder = var2;
      this.content = var1;
   }

   public static CertificateConfirmationContent fromPKIBody(PKIBody var0) {
      return fromPKIBody(var0, new DefaultDigestAlgorithmIdentifierFinder());
   }

   public static CertificateConfirmationContent fromPKIBody(PKIBody var0, DigestAlgorithmIdentifierFinder var1) {
      if (!isCertificateConfirmationContent(var0.getType())) {
         throw new IllegalArgumentException("content of PKIBody wrong type: " + var0.getType());
      } else {
         return new CertificateConfirmationContent(CertConfirmContent.getInstance(var0.getContent()), var1);
      }
   }

   public static boolean isCertificateConfirmationContent(int var0) {
      switch (var0) {
         case 24:
            return true;
         default:
            return false;
      }
   }

   public CertConfirmContent toASN1Structure() {
      return this.content;
   }

   public CertificateStatus[] getStatusMessages() {
      CertStatus[] var1 = this.content.toCertStatusArray();
      CertificateStatus[] var2 = new CertificateStatus[var1.length];

      for (int var3 = 0; var3 != var2.length; var3++) {
         var2[var3] = new CertificateStatus(this.digestAlgFinder, var1[var3]);
      }

      return var2;
   }
}
