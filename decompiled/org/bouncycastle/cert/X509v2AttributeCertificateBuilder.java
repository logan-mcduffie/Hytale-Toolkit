package org.bouncycastle.cert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.AttCertIssuer;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.V2AttributeCertificateInfoGenerator;
import org.bouncycastle.operator.ContentSigner;

public class X509v2AttributeCertificateBuilder {
   private V2AttributeCertificateInfoGenerator acInfoGen = new V2AttributeCertificateInfoGenerator();
   private ExtensionsGenerator extGenerator;

   public X509v2AttributeCertificateBuilder(AttributeCertificateHolder var1, AttributeCertificateIssuer var2, BigInteger var3, Date var4, Date var5) {
      this.extGenerator = new ExtensionsGenerator();
      this.acInfoGen.setHolder(var1.holder);
      this.acInfoGen.setIssuer(AttCertIssuer.getInstance(var2.form));
      this.acInfoGen.setSerialNumber(new ASN1Integer(var3));
      this.acInfoGen.setStartDate(new ASN1GeneralizedTime(var4));
      this.acInfoGen.setEndDate(new ASN1GeneralizedTime(var5));
   }

   public X509v2AttributeCertificateBuilder(
      AttributeCertificateHolder var1, AttributeCertificateIssuer var2, BigInteger var3, Date var4, Date var5, Locale var6
   ) {
      this.extGenerator = new ExtensionsGenerator();
      this.acInfoGen.setHolder(var1.holder);
      this.acInfoGen.setIssuer(AttCertIssuer.getInstance(var2.form));
      this.acInfoGen.setSerialNumber(new ASN1Integer(var3));
      this.acInfoGen.setStartDate(new ASN1GeneralizedTime(var4, var6));
      this.acInfoGen.setEndDate(new ASN1GeneralizedTime(var5, var6));
   }

   public X509v2AttributeCertificateBuilder(X509AttributeCertificateHolder var1) {
      this.acInfoGen.setSerialNumber(new ASN1Integer(var1.getSerialNumber()));
      this.acInfoGen.setIssuer(AttCertIssuer.getInstance(var1.getIssuer().form));
      this.acInfoGen.setStartDate(new ASN1GeneralizedTime(var1.getNotBefore()));
      this.acInfoGen.setEndDate(new ASN1GeneralizedTime(var1.getNotAfter()));
      this.acInfoGen.setHolder(var1.getHolder().holder);
      boolean[] var2 = var1.getIssuerUniqueID();
      if (var2 != null) {
         this.acInfoGen.setIssuerUniqueID(CertUtils.booleanToBitString(var2));
      }

      Attribute[] var3 = var1.getAttributes();

      for (int var4 = 0; var4 != var3.length; var4++) {
         this.acInfoGen.addAttribute(var3[var4]);
      }

      this.extGenerator = new ExtensionsGenerator();
      Extensions var6 = var1.getExtensions();
      Enumeration var5 = var6.oids();

      while (var5.hasMoreElements()) {
         this.extGenerator.addExtension(var6.getExtension((ASN1ObjectIdentifier)var5.nextElement()));
      }
   }

   public boolean hasExtension(ASN1ObjectIdentifier var1) {
      return this.doGetExtension(var1) != null;
   }

   public Extension getExtension(ASN1ObjectIdentifier var1) {
      return this.doGetExtension(var1);
   }

   private Extension doGetExtension(ASN1ObjectIdentifier var1) {
      if (this.extGenerator.isEmpty()) {
         return null;
      } else {
         Extensions var2 = this.extGenerator.generate();
         return var2.getExtension(var1);
      }
   }

   public X509v2AttributeCertificateBuilder addAttribute(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.acInfoGen.addAttribute(new Attribute(var1, new DERSet(var2)));
      return this;
   }

   public X509v2AttributeCertificateBuilder addAttribute(ASN1ObjectIdentifier var1, ASN1Encodable[] var2) {
      this.acInfoGen.addAttribute(new Attribute(var1, new DERSet(var2)));
      return this;
   }

   public void setIssuerUniqueId(boolean[] var1) {
      this.acInfoGen.setIssuerUniqueID(CertUtils.booleanToBitString(var1));
   }

   public X509v2AttributeCertificateBuilder addExtension(ASN1ObjectIdentifier var1, boolean var2, ASN1Encodable var3) throws CertIOException {
      CertUtils.addExtension(this.extGenerator, var1, var2, var3);
      return this;
   }

   public X509v2AttributeCertificateBuilder addExtension(ASN1ObjectIdentifier var1, boolean var2, byte[] var3) throws CertIOException {
      this.extGenerator.addExtension(var1, var2, var3);
      return this;
   }

   public X509v2AttributeCertificateBuilder addExtension(Extension var1) throws CertIOException {
      this.extGenerator.addExtension(var1);
      return this;
   }

   public X509v2AttributeCertificateBuilder replaceExtension(ASN1ObjectIdentifier var1, boolean var2, ASN1Encodable var3) throws CertIOException {
      try {
         this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(var1, var2, new DEROctetString(var3)));
         return this;
      } catch (IOException var5) {
         throw new CertIOException("cannot encode extension: " + var5.getMessage(), var5);
      }
   }

   public X509v2AttributeCertificateBuilder replaceExtension(Extension var1) throws CertIOException {
      this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, var1);
      return this;
   }

   public X509v2AttributeCertificateBuilder replaceExtension(ASN1ObjectIdentifier var1, boolean var2, byte[] var3) throws CertIOException {
      this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(var1, var2, var3));
      return this;
   }

   public X509v2AttributeCertificateBuilder removeExtension(ASN1ObjectIdentifier var1) {
      this.extGenerator = CertUtils.doRemoveExtension(this.extGenerator, var1);
      return this;
   }

   public X509AttributeCertificateHolder build(ContentSigner var1) {
      this.acInfoGen.setSignature(var1.getAlgorithmIdentifier());
      if (!this.extGenerator.isEmpty()) {
         this.acInfoGen.setExtensions(this.extGenerator.generate());
      }

      return CertUtils.generateFullAttrCert(var1, this.acInfoGen.generateAttributeCertificateInfo());
   }
}
