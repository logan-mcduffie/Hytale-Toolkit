package org.bouncycastle.openssl;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Arrays;

public class X509TrustedCertificateBlock {
   private final X509CertificateHolder certificateHolder;
   private final CertificateTrustBlock trustBlock;

   public X509TrustedCertificateBlock(X509CertificateHolder var1, CertificateTrustBlock var2) {
      this.certificateHolder = var1;
      this.trustBlock = var2;
   }

   public X509TrustedCertificateBlock(byte[] var1) throws IOException {
      ASN1InputStream var2 = new ASN1InputStream(var1);
      this.certificateHolder = new X509CertificateHolder(var2.readObject().getEncoded());
      ASN1Primitive var3 = var2.readObject();
      if (var3 != null) {
         this.trustBlock = new CertificateTrustBlock(var3.getEncoded());
      } else {
         this.trustBlock = null;
      }
   }

   public byte[] getEncoded() throws IOException {
      return Arrays.concatenate(this.certificateHolder.getEncoded(), this.trustBlock.toASN1Sequence().getEncoded());
   }

   public X509CertificateHolder getCertificateHolder() {
      return this.certificateHolder;
   }

   public CertificateTrustBlock getTrustBlock() {
      return this.trustBlock;
   }
}
