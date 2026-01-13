package org.bouncycastle.cert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AltSignatureAlgorithm;
import org.bouncycastle.asn1.x509.AltSignatureValue;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Encodable;

public class X509CertificateHolder implements Encodable, Serializable {
   private static final long serialVersionUID = 20170722001L;
   private transient Certificate x509Certificate;
   private transient Extensions extensions;

   private static Certificate parseBytes(byte[] var0) throws IOException {
      try {
         return Certificate.getInstance(CertUtils.parseNonEmptyASN1(var0));
      } catch (ClassCastException var2) {
         throw new CertIOException("malformed data: " + var2.getMessage(), var2);
      } catch (IllegalArgumentException var3) {
         throw new CertIOException("malformed data: " + var3.getMessage(), var3);
      }
   }

   public X509CertificateHolder(byte[] var1) throws IOException {
      this(parseBytes(var1));
   }

   public X509CertificateHolder(Certificate var1) {
      this.init(var1);
   }

   private void init(Certificate var1) {
      this.x509Certificate = var1;
      this.extensions = var1.getTBSCertificate().getExtensions();
   }

   public int getVersionNumber() {
      return this.x509Certificate.getVersionNumber();
   }

   /** @deprecated */
   public int getVersion() {
      return this.x509Certificate.getVersionNumber();
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

   public BigInteger getSerialNumber() {
      return this.x509Certificate.getSerialNumber().getValue();
   }

   public X500Name getIssuer() {
      return X500Name.getInstance(this.x509Certificate.getIssuer());
   }

   public X500Name getSubject() {
      return X500Name.getInstance(this.x509Certificate.getSubject());
   }

   public Date getNotBefore() {
      return this.x509Certificate.getStartDate().getDate();
   }

   public Date getNotAfter() {
      return this.x509Certificate.getEndDate().getDate();
   }

   public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
      return this.x509Certificate.getSubjectPublicKeyInfo();
   }

   public TBSCertificate getTBSCertificate() {
      return this.x509Certificate.getTBSCertificate();
   }

   public Certificate toASN1Structure() {
      return this.x509Certificate;
   }

   public AlgorithmIdentifier getSignatureAlgorithm() {
      return this.x509Certificate.getSignatureAlgorithm();
   }

   public byte[] getSignature() {
      return this.x509Certificate.getSignature().getOctets();
   }

   public boolean isValidOn(Date var1) {
      return !var1.before(this.x509Certificate.getStartDate().getDate()) && !var1.after(this.x509Certificate.getEndDate().getDate());
   }

   public boolean isSignatureValid(ContentVerifierProvider var1) throws CertException {
      TBSCertificate var2 = this.x509Certificate.getTBSCertificate();
      if (!CertUtils.isAlgIdEqual(var2.getSignature(), this.x509Certificate.getSignatureAlgorithm())) {
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

   public boolean isAlternativeSignatureValid(ContentVerifierProvider var1) throws CertException {
      TBSCertificate var2 = this.x509Certificate.getTBSCertificate();
      AltSignatureAlgorithm var3 = AltSignatureAlgorithm.fromExtensions(var2.getExtensions());
      AltSignatureValue var4 = AltSignatureValue.fromExtensions(var2.getExtensions());

      ContentVerifier var5;
      try {
         var5 = var1.get(AlgorithmIdentifier.getInstance(var3.toASN1Primitive()));
         OutputStream var6 = var5.getOutputStream();
         ASN1Sequence var7 = ASN1Sequence.getInstance(var2.toASN1Primitive());
         ASN1EncodableVector var8 = new ASN1EncodableVector();

         for (int var9 = 0; var9 != var7.size() - 1; var9++) {
            if (var9 != 2) {
               var8.add(var7.getObjectAt(var9));
            }
         }

         var8.add(CertUtils.trimExtensions(3, var2.getExtensions()));
         new DERSequence(var8).encodeTo(var6, "DER");
         var6.close();
      } catch (Exception var10) {
         throw new CertException("unable to process signature: " + var10.getMessage(), var10);
      }

      return var5.verify(var4.getSignature().getOctets());
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof X509CertificateHolder)) {
         return false;
      } else {
         X509CertificateHolder var2 = (X509CertificateHolder)var1;
         return this.x509Certificate.equals(var2.x509Certificate);
      }
   }

   @Override
   public int hashCode() {
      return this.x509Certificate.hashCode();
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return this.x509Certificate.getEncoded();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.init(Certificate.getInstance(var1.readObject()));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
