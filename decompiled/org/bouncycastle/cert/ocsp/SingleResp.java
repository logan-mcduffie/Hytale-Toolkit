package org.bouncycastle.cert.ocsp;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ocsp.CertStatus;
import org.bouncycastle.asn1.ocsp.RevokedInfo;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;

public class SingleResp {
   private SingleResponse resp;
   private Extensions extensions;

   public SingleResp(SingleResponse var1) {
      this.resp = var1;
      this.extensions = var1.getSingleExtensions();
   }

   public CertificateID getCertID() {
      return new CertificateID(this.resp.getCertID());
   }

   public CertificateStatus getCertStatus() {
      CertStatus var1 = this.resp.getCertStatus();
      if (var1.getTagNo() == 0) {
         return null;
      } else {
         return (CertificateStatus)(var1.getTagNo() == 1 ? new RevokedStatus(RevokedInfo.getInstance(var1.getStatus())) : new UnknownStatus());
      }
   }

   public Date getThisUpdate() {
      return OCSPUtils.extractDate(this.resp.getThisUpdate());
   }

   public Date getNextUpdate() {
      return this.resp.getNextUpdate() == null ? null : OCSPUtils.extractDate(this.resp.getNextUpdate());
   }

   public boolean hasExtensions() {
      return this.extensions != null;
   }

   public Extension getExtension(ASN1ObjectIdentifier var1) {
      return this.extensions != null ? this.extensions.getExtension(var1) : null;
   }

   public List getExtensionOIDs() {
      return OCSPUtils.getExtensionOIDs(this.extensions);
   }

   public Set getCriticalExtensionOIDs() {
      return OCSPUtils.getCriticalExtensionOIDs(this.extensions);
   }

   public Set getNonCriticalExtensionOIDs() {
      return OCSPUtils.getNonCriticalExtensionOIDs(this.extensions);
   }
}
