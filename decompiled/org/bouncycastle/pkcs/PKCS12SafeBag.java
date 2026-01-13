package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CRLBag;
import org.bouncycastle.asn1.pkcs.CertBag;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;

public class PKCS12SafeBag {
   public static final ASN1ObjectIdentifier friendlyNameAttribute = PKCSObjectIdentifiers.pkcs_9_at_friendlyName;
   public static final ASN1ObjectIdentifier localKeyIdAttribute = PKCSObjectIdentifiers.pkcs_9_at_localKeyId;
   private SafeBag safeBag;

   public PKCS12SafeBag(SafeBag var1) {
      this.safeBag = var1;
   }

   public SafeBag toASN1Structure() {
      return this.safeBag;
   }

   public ASN1ObjectIdentifier getType() {
      return this.safeBag.getBagId();
   }

   public Attribute[] getAttributes() {
      ASN1Set var1 = this.safeBag.getBagAttributes();
      if (var1 == null) {
         return null;
      } else {
         Attribute[] var2 = new Attribute[var1.size()];

         for (int var3 = 0; var3 != var1.size(); var3++) {
            var2[var3] = Attribute.getInstance(var1.getObjectAt(var3));
         }

         return var2;
      }
   }

   public Object getBagValue() {
      if (this.getType().equals(PKCSObjectIdentifiers.pkcs8ShroudedKeyBag)) {
         return new PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo.getInstance(this.safeBag.getBagValue()));
      } else if (this.getType().equals(PKCSObjectIdentifiers.certBag)) {
         CertBag var2 = CertBag.getInstance(this.safeBag.getBagValue());
         return new X509CertificateHolder(Certificate.getInstance(ASN1OctetString.getInstance(var2.getCertValue()).getOctets()));
      } else if (this.getType().equals(PKCSObjectIdentifiers.keyBag)) {
         return PrivateKeyInfo.getInstance(this.safeBag.getBagValue());
      } else if (this.getType().equals(PKCSObjectIdentifiers.crlBag)) {
         CRLBag var1 = CRLBag.getInstance(this.safeBag.getBagValue());
         return new X509CRLHolder(CertificateList.getInstance(ASN1OctetString.getInstance(var1.getCrlValue()).getOctets()));
      } else {
         return this.safeBag.getBagValue();
      }
   }
}
