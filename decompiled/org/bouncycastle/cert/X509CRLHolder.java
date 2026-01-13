package org.bouncycastle.cert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AltSignatureAlgorithm;
import org.bouncycastle.asn1.x509.AltSignatureValue;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Encodable;

public class X509CRLHolder implements Encodable, Serializable {
   private static final long serialVersionUID = 20170722001L;
   private transient CertificateList x509CRL;
   private transient boolean isIndirect;
   private transient Extensions extensions;
   private transient GeneralNames issuerName;

   private static CertificateList parseStream(InputStream var0) throws IOException {
      try {
         ASN1Primitive var1 = new ASN1InputStream(var0, true).readObject();
         if (var1 == null) {
            throw new IOException("no content found");
         } else {
            return CertificateList.getInstance(var1);
         }
      } catch (ClassCastException var2) {
         throw new CertIOException("malformed data: " + var2.getMessage(), var2);
      } catch (IllegalArgumentException var3) {
         throw new CertIOException("malformed data: " + var3.getMessage(), var3);
      }
   }

   private static boolean isIndirectCRL(Extensions var0) {
      if (var0 == null) {
         return false;
      } else {
         Extension var1 = var0.getExtension(Extension.issuingDistributionPoint);
         return var1 != null && IssuingDistributionPoint.getInstance(var1.getParsedValue()).isIndirectCRL();
      }
   }

   public X509CRLHolder(byte[] var1) throws IOException {
      this(parseStream(new ByteArrayInputStream(var1)));
   }

   public X509CRLHolder(InputStream var1) throws IOException {
      this(parseStream(var1));
   }

   public X509CRLHolder(CertificateList var1) {
      this.init(var1);
   }

   private void init(CertificateList var1) {
      this.x509CRL = var1;
      this.extensions = var1.getTBSCertList().getExtensions();
      this.isIndirect = isIndirectCRL(this.extensions);
      this.issuerName = new GeneralNames(new GeneralName(var1.getIssuer()));
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return this.x509CRL.getEncoded();
   }

   public X500Name getIssuer() {
      return X500Name.getInstance(this.x509CRL.getIssuer());
   }

   public Date getThisUpdate() {
      return this.x509CRL.getThisUpdate().getDate();
   }

   public Date getNextUpdate() {
      Time var1 = this.x509CRL.getNextUpdate();
      return var1 != null ? var1.getDate() : null;
   }

   public X509CRLEntryHolder getRevokedCertificate(BigInteger var1) {
      GeneralNames var2 = this.issuerName;
      Enumeration var3 = this.x509CRL.getRevokedCertificateEnumeration();

      while (var3.hasMoreElements()) {
         TBSCertList.CRLEntry var4 = (TBSCertList.CRLEntry)var3.nextElement();
         if (var4.getUserCertificate().hasValue(var1)) {
            return new X509CRLEntryHolder(var4, this.isIndirect, var2);
         }

         if (this.isIndirect && var4.hasExtensions()) {
            Extension var5 = var4.getExtensions().getExtension(Extension.certificateIssuer);
            if (var5 != null) {
               var2 = GeneralNames.getInstance(var5.getParsedValue());
            }
         }
      }

      return null;
   }

   public Collection getRevokedCertificates() {
      TBSCertList.CRLEntry[] var1 = this.x509CRL.getRevokedCertificates();
      ArrayList var2 = new ArrayList(var1.length);
      GeneralNames var3 = this.issuerName;
      Enumeration var4 = this.x509CRL.getRevokedCertificateEnumeration();

      while (var4.hasMoreElements()) {
         TBSCertList.CRLEntry var5 = (TBSCertList.CRLEntry)var4.nextElement();
         X509CRLEntryHolder var6 = new X509CRLEntryHolder(var5, this.isIndirect, var3);
         var2.add(var6);
         var3 = var6.getCertificateIssuer();
      }

      return var2;
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

   public CertificateList toASN1Structure() {
      return this.x509CRL;
   }

   public boolean isSignatureValid(ContentVerifierProvider var1) throws CertException {
      TBSCertList var2 = this.x509CRL.getTBSCertList();
      if (!CertUtils.isAlgIdEqual(var2.getSignature(), this.x509CRL.getSignatureAlgorithm())) {
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

         return var3.verify(this.x509CRL.getSignature().getOctets());
      }
   }

   public boolean isAlternativeSignatureValid(ContentVerifierProvider var1) throws CertException {
      TBSCertList var2 = this.x509CRL.getTBSCertList();
      AltSignatureAlgorithm var3 = AltSignatureAlgorithm.fromExtensions(var2.getExtensions());
      AltSignatureValue var4 = AltSignatureValue.fromExtensions(var2.getExtensions());

      ContentVerifier var5;
      try {
         var5 = var1.get(AlgorithmIdentifier.getInstance(var3.toASN1Primitive()));
         OutputStream var6 = var5.getOutputStream();
         ASN1Sequence var7 = ASN1Sequence.getInstance(var2.toASN1Primitive());
         ASN1EncodableVector var8 = new ASN1EncodableVector();
         int var9 = 1;
         if (var7.getObjectAt(0) instanceof ASN1Integer) {
            var8.add(var7.getObjectAt(0));
            var9++;
         }

         for (int var10 = var9; var10 != var7.size() - 1; var10++) {
            var8.add(var7.getObjectAt(var10));
         }

         var8.add(CertUtils.trimExtensions(0, var2.getExtensions()));
         new DERSequence(var8).encodeTo(var6, "DER");
         var6.close();
      } catch (Exception var11) {
         throw new CertException("unable to process signature: " + var11.getMessage(), var11);
      }

      return var5.verify(var4.getSignature().getOctets());
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof X509CRLHolder)) {
         return false;
      } else {
         X509CRLHolder var2 = (X509CRLHolder)var1;
         return this.x509CRL.equals(var2.x509CRL);
      }
   }

   @Override
   public int hashCode() {
      return this.x509CRL.hashCode();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.init(CertificateList.getInstance(var1.readObject()));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
