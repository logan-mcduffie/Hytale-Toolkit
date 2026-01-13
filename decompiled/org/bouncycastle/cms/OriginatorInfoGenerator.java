package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Store;

public class OriginatorInfoGenerator {
   private final List origCerts;
   private final List origCRLs;

   public OriginatorInfoGenerator(X509CertificateHolder var1) {
      this.origCerts = new ArrayList(1);
      this.origCRLs = null;
      this.origCerts.add(var1.toASN1Structure());
   }

   public OriginatorInfoGenerator(Store var1) throws CMSException {
      this(var1, null);
   }

   public OriginatorInfoGenerator(Store var1, Store var2) throws CMSException {
      if (var1 != null) {
         this.origCerts = CMSUtils.getCertificatesFromStore(var1);
      } else {
         this.origCerts = null;
      }

      if (var2 != null) {
         this.origCRLs = CMSUtils.getCRLsFromStore(var2);
      } else {
         this.origCRLs = null;
      }
   }

   public OriginatorInformation generate() {
      ASN1Set var1 = this.origCerts == null ? null : CMSUtils.createDerSetFromList(this.origCerts);
      ASN1Set var2 = this.origCRLs == null ? null : CMSUtils.createDerSetFromList(this.origCRLs);
      return new OriginatorInformation(new OriginatorInfo(var1, var2));
   }
}
