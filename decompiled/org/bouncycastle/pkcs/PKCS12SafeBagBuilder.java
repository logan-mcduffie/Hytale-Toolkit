package org.bouncycastle.pkcs;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CertBag;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.OutputEncryptor;

public class PKCS12SafeBagBuilder {
   private ASN1ObjectIdentifier bagType;
   private ASN1Encodable bagValue;
   private ASN1EncodableVector bagAttrs = new ASN1EncodableVector();

   public PKCS12SafeBagBuilder(PrivateKeyInfo var1, OutputEncryptor var2) {
      this.bagType = PKCSObjectIdentifiers.pkcs8ShroudedKeyBag;
      this.bagValue = new PKCS8EncryptedPrivateKeyInfoBuilder(var1).build(var2).toASN1Structure();
   }

   public PKCS12SafeBagBuilder(PrivateKeyInfo var1) {
      this.bagType = PKCSObjectIdentifiers.keyBag;
      this.bagValue = var1;
   }

   public PKCS12SafeBagBuilder(X509CertificateHolder var1) throws IOException {
      this(var1.toASN1Structure());
   }

   public PKCS12SafeBagBuilder(X509CRLHolder var1) throws IOException {
      this(var1.toASN1Structure());
   }

   public PKCS12SafeBagBuilder(Certificate var1) throws IOException {
      this.bagType = PKCSObjectIdentifiers.certBag;
      this.bagValue = new CertBag(PKCSObjectIdentifiers.x509Certificate, new DEROctetString(var1.getEncoded()));
   }

   public PKCS12SafeBagBuilder(CertificateList var1) throws IOException {
      this.bagType = PKCSObjectIdentifiers.crlBag;
      this.bagValue = new CertBag(PKCSObjectIdentifiers.x509Crl, new DEROctetString(var1.getEncoded()));
   }

   public PKCS12SafeBagBuilder addBagAttribute(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.bagAttrs.add(new Attribute(var1, new DERSet(var2)));
      return this;
   }

   public PKCS12SafeBag build() {
      return new PKCS12SafeBag(new SafeBag(this.bagType, this.bagValue, new DERSet(this.bagAttrs)));
   }
}
