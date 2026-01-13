package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ocsp.CertID;
import org.bouncycastle.asn1.ocsp.CertStatus;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.smime.SMIMECapabilities;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;

public class CertEtcToken extends ASN1Object implements ASN1Choice {
   public static final int TAG_CERTIFICATE = 0;
   public static final int TAG_ESSCERTID = 1;
   public static final int TAG_PKISTATUS = 2;
   public static final int TAG_ASSERTION = 3;
   public static final int TAG_CRL = 4;
   public static final int TAG_OCSPCERTSTATUS = 5;
   public static final int TAG_OCSPCERTID = 6;
   public static final int TAG_OCSPRESPONSE = 7;
   public static final int TAG_CAPABILITIES = 8;
   private static final boolean[] explicit = new boolean[]{false, true, false, true, false, true, false, false, true};
   private int tagNo;
   private ASN1Encodable value;
   private Extension extension;

   public CertEtcToken(int var1, ASN1Encodable var2) {
      this.tagNo = var1;
      this.value = var2;
   }

   public CertEtcToken(Extension var1) {
      this.tagNo = -1;
      this.extension = var1;
   }

   private CertEtcToken(ASN1TaggedObject var1) {
      this.tagNo = var1.getTagNo();
      switch (this.tagNo) {
         case 0:
            this.value = Certificate.getInstance(var1, false);
            break;
         case 1:
            this.value = ESSCertID.getInstance(var1.getExplicitBaseObject());
            break;
         case 2:
            this.value = PKIStatusInfo.getInstance(var1, false);
            break;
         case 3:
            this.value = ContentInfo.getInstance(var1.getExplicitBaseObject());
            break;
         case 4:
            this.value = CertificateList.getInstance(var1, false);
            break;
         case 5:
            this.value = CertStatus.getInstance(var1.getExplicitBaseObject());
            break;
         case 6:
            this.value = CertID.getInstance(var1, false);
            break;
         case 7:
            this.value = OCSPResponse.getInstance(var1, false);
            break;
         case 8:
            this.value = SMIMECapabilities.getInstance(var1.getExplicitBaseObject());
            break;
         default:
            throw new IllegalArgumentException("Unknown tag: " + this.tagNo);
      }
   }

   public static CertEtcToken getInstance(Object var0) {
      if (var0 instanceof CertEtcToken) {
         return (CertEtcToken)var0;
      } else if (var0 instanceof ASN1TaggedObject) {
         return new CertEtcToken(ASN1TaggedObject.getInstance(var0, 128));
      } else {
         return var0 != null ? new CertEtcToken(Extension.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return (ASN1Primitive)(this.extension == null ? new DERTaggedObject(explicit[this.tagNo], this.tagNo, this.value) : this.extension.toASN1Primitive());
   }

   public int getTagNo() {
      return this.tagNo;
   }

   public ASN1Encodable getValue() {
      return this.value;
   }

   public Extension getExtension() {
      return this.extension;
   }

   @Override
   public String toString() {
      return "CertEtcToken {\n" + this.value + "}\n";
   }

   public static CertEtcToken[] arrayFromSequence(ASN1Sequence var0) {
      CertEtcToken[] var1 = new CertEtcToken[var0.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = getInstance(var0.getObjectAt(var2));
      }

      return var1;
   }
}
