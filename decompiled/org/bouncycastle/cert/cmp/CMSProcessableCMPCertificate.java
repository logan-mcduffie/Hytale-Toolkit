package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;

public class CMSProcessableCMPCertificate implements CMSTypedData {
   private final CMPCertificate cmpCert;

   public CMSProcessableCMPCertificate(X509CertificateHolder var1) {
      this(new CMPCertificate(var1.toASN1Structure()));
   }

   public CMSProcessableCMPCertificate(CMPCertificate var1) {
      this.cmpCert = var1;
   }

   @Override
   public void write(OutputStream var1) throws IOException, CMSException {
      var1.write(this.cmpCert.getEncoded());
   }

   @Override
   public Object getContent() {
      return this.cmpCert;
   }

   @Override
   public ASN1ObjectIdentifier getContentType() {
      return PKCSObjectIdentifiers.data;
   }
}
