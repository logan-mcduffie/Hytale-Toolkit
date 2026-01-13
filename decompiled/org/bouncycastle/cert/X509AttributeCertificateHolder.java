package org.bouncycastle.cert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AttCertValidityPeriod;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.AttributeCertificateInfo;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Encodable;

public class X509AttributeCertificateHolder implements Encodable, Serializable {
   private static final long serialVersionUID = 20170722001L;
   private static Attribute[] EMPTY_ARRAY = new Attribute[0];
   private transient AttributeCertificate attrCert;
   private transient Extensions extensions;

   private static AttributeCertificate parseBytes(byte[] var0) throws IOException {
      try {
         return AttributeCertificate.getInstance(CertUtils.parseNonEmptyASN1(var0));
      } catch (ClassCastException var2) {
         throw new CertIOException("malformed data: " + var2.getMessage(), var2);
      } catch (IllegalArgumentException var3) {
         throw new CertIOException("malformed data: " + var3.getMessage(), var3);
      }
   }

   public X509AttributeCertificateHolder(byte[] var1) throws IOException {
      this(parseBytes(var1));
   }

   public X509AttributeCertificateHolder(AttributeCertificate var1) {
      this.init(var1);
   }

   private void init(AttributeCertificate var1) {
      this.attrCert = var1;
      this.extensions = var1.getAcinfo().getExtensions();
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return this.attrCert.getEncoded();
   }

   public int getVersion() {
      return this.attrCert.getAcinfo().getVersion().intValueExact() + 1;
   }

   public BigInteger getSerialNumber() {
      return this.attrCert.getAcinfo().getSerialNumber().getValue();
   }

   public AttributeCertificateHolder getHolder() {
      return new AttributeCertificateHolder((ASN1Sequence)this.attrCert.getAcinfo().getHolder().toASN1Primitive());
   }

   public AttributeCertificateIssuer getIssuer() {
      return new AttributeCertificateIssuer(this.attrCert.getAcinfo().getIssuer());
   }

   public Date getNotBefore() {
      return CertUtils.recoverDate(this.attrCert.getAcinfo().getAttrCertValidityPeriod().getNotBeforeTime());
   }

   public Date getNotAfter() {
      return CertUtils.recoverDate(this.attrCert.getAcinfo().getAttrCertValidityPeriod().getNotAfterTime());
   }

   public Attribute[] getAttributes() {
      ASN1Sequence var1 = this.attrCert.getAcinfo().getAttributes();
      Attribute[] var2 = new Attribute[var1.size()];

      for (int var3 = 0; var3 != var1.size(); var3++) {
         var2[var3] = Attribute.getInstance(var1.getObjectAt(var3));
      }

      return var2;
   }

   public Attribute[] getAttributes(ASN1ObjectIdentifier var1) {
      ASN1Sequence var2 = this.attrCert.getAcinfo().getAttributes();
      ArrayList var3 = new ArrayList();

      for (int var4 = 0; var4 != var2.size(); var4++) {
         Attribute var5 = Attribute.getInstance(var2.getObjectAt(var4));
         if (var5.getAttrType().equals(var1)) {
            var3.add(var5);
         }
      }

      return var3.size() == 0 ? EMPTY_ARRAY : var3.toArray(new Attribute[var3.size()]);
   }

   public boolean hasExtensions() {
      return this.extensions != null;
   }

   public Extension getExtension(ASN1ObjectIdentifier var1) {
      return this.extensions != null ? this.extensions.getExtension(var1) : null;
   }

   public Extensions getExtensions() {
      return this.extensions;
   }

   public List getExtensionOIDs() {
      return CertUtils.getExtensionOIDs(this.extensions);
   }

   public Set getCriticalExtensionOIDs() {
      return CertUtils.getCriticalExtensionOIDs(this.extensions);
   }

   public Set getNonCriticalExtensionOIDs() {
      return CertUtils.getNonCriticalExtensionOIDs(this.extensions);
   }

   public boolean[] getIssuerUniqueID() {
      return CertUtils.bitStringToBoolean(this.attrCert.getAcinfo().getIssuerUniqueID());
   }

   public AlgorithmIdentifier getSignatureAlgorithm() {
      return this.attrCert.getSignatureAlgorithm();
   }

   public byte[] getSignature() {
      return this.attrCert.getSignatureValue().getOctets();
   }

   public AttributeCertificate toASN1Structure() {
      return this.attrCert;
   }

   public boolean isValidOn(Date var1) {
      AttCertValidityPeriod var2 = this.attrCert.getAcinfo().getAttrCertValidityPeriod();
      return !var1.before(CertUtils.recoverDate(var2.getNotBeforeTime())) && !var1.after(CertUtils.recoverDate(var2.getNotAfterTime()));
   }

   public boolean isSignatureValid(ContentVerifierProvider var1) throws CertException {
      AttributeCertificateInfo var2 = this.attrCert.getAcinfo();
      if (!CertUtils.isAlgIdEqual(var2.getSignature(), this.attrCert.getSignatureAlgorithm())) {
         throw new CertException("signature invalid - algorithm identifier mismatch");
      } else {
         ContentVerifier var3;
         try {
            var3 = var1.get(var2.getSignature());
            OutputStream var4 = var3.getOutputStream();
            var2.encodeTo(var4, "DER");
            var4.close();
         } catch (Exception var5) {
            throw new CertException("unable to process signature: " + var5.getMessage(), var5);
         }

         return var3.verify(this.getSignature());
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof X509AttributeCertificateHolder)) {
         return false;
      } else {
         X509AttributeCertificateHolder var2 = (X509AttributeCertificateHolder)var1;
         return this.attrCert.equals(var2.attrCert);
      }
   }

   @Override
   public int hashCode() {
      return this.attrCert.hashCode();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.init(AttributeCertificate.getInstance(var1.readObject()));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
