package org.bouncycastle.cert;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.TBSCertList;

public class X509CRLEntryHolder {
   private TBSCertList.CRLEntry entry;
   private GeneralNames ca;

   X509CRLEntryHolder(TBSCertList.CRLEntry var1, boolean var2, GeneralNames var3) {
      this.entry = var1;
      this.ca = var3;
      if (var2 && var1.hasExtensions()) {
         Extension var4 = var1.getExtensions().getExtension(Extension.certificateIssuer);
         if (var4 != null) {
            this.ca = GeneralNames.getInstance(var4.getParsedValue());
         }
      }
   }

   public BigInteger getSerialNumber() {
      return this.entry.getUserCertificate().getValue();
   }

   public Date getRevocationDate() {
      return this.entry.getRevocationDate().getDate();
   }

   public boolean hasExtensions() {
      return this.entry.hasExtensions();
   }

   public GeneralNames getCertificateIssuer() {
      return this.ca;
   }

   public Extension getExtension(ASN1ObjectIdentifier var1) {
      Extensions var2 = this.entry.getExtensions();
      return var2 != null ? var2.getExtension(var1) : null;
   }

   public Extensions getExtensions() {
      return this.entry.getExtensions();
   }

   public List getExtensionOIDs() {
      return CertUtils.getExtensionOIDs(this.entry.getExtensions());
   }

   public Set getCriticalExtensionOIDs() {
      return CertUtils.getCriticalExtensionOIDs(this.entry.getExtensions());
   }

   public Set getNonCriticalExtensionOIDs() {
      return CertUtils.getNonCriticalExtensionOIDs(this.entry.getExtensions());
   }
}
