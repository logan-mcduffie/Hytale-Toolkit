package org.bouncycastle.cert;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V2TBSCertListGenerator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.util.Exceptions;

public class X509v2CRLBuilder {
   private V2TBSCertListGenerator tbsGen = new V2TBSCertListGenerator();
   private ExtensionsGenerator extGenerator;

   public X509v2CRLBuilder(X500Name var1, Date var2) {
      this.extGenerator = new ExtensionsGenerator();
      this.tbsGen.setIssuer(var1);
      this.tbsGen.setThisUpdate(new Time(var2));
   }

   public X509v2CRLBuilder(X500Name var1, Date var2, Locale var3) {
      this.extGenerator = new ExtensionsGenerator();
      this.tbsGen.setIssuer(var1);
      this.tbsGen.setThisUpdate(new Time(var2, var3));
   }

   public X509v2CRLBuilder(X500Name var1, Time var2) {
      this.extGenerator = new ExtensionsGenerator();
      this.tbsGen.setIssuer(var1);
      this.tbsGen.setThisUpdate(var2);
   }

   public X509v2CRLBuilder(X509CRLHolder var1) {
      this.tbsGen.setIssuer(var1.getIssuer());
      this.tbsGen.setThisUpdate(new Time(var1.getThisUpdate()));
      Date var2 = var1.getNextUpdate();
      if (var2 != null) {
         this.tbsGen.setNextUpdate(new Time(var2));
      }

      this.addCRL(var1);
      this.extGenerator = new ExtensionsGenerator();
      Extensions var3 = var1.getExtensions();
      if (var3 != null) {
         Enumeration var4 = var3.oids();

         while (var4.hasMoreElements()) {
            ASN1ObjectIdentifier var5 = (ASN1ObjectIdentifier)var4.nextElement();
            if (!Extension.altSignatureAlgorithm.equals(var5) && !Extension.altSignatureValue.equals(var5)) {
               this.extGenerator.addExtension(var3.getExtension(var5));
            }
         }
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

   public X509v2CRLBuilder setThisUpdate(Date var1) {
      return this.setThisUpdate(new Time(var1));
   }

   public X509v2CRLBuilder setThisUpdate(Date var1, Locale var2) {
      return this.setThisUpdate(new Time(var1, var2));
   }

   public X509v2CRLBuilder setThisUpdate(Time var1) {
      this.tbsGen.setThisUpdate(var1);
      return this;
   }

   public X509v2CRLBuilder setNextUpdate(Date var1) {
      return this.setNextUpdate(new Time(var1));
   }

   public X509v2CRLBuilder setNextUpdate(Date var1, Locale var2) {
      return this.setNextUpdate(new Time(var1, var2));
   }

   public X509v2CRLBuilder setNextUpdate(Time var1) {
      this.tbsGen.setNextUpdate(var1);
      return this;
   }

   public X509v2CRLBuilder addCRLEntry(BigInteger var1, Date var2, int var3) {
      this.tbsGen.addCRLEntry(new ASN1Integer(var1), new Time(var2), var3);
      return this;
   }

   public X509v2CRLBuilder addCRLEntry(BigInteger var1, Date var2, int var3, Date var4) {
      this.tbsGen.addCRLEntry(new ASN1Integer(var1), new Time(var2), var3, new ASN1GeneralizedTime(var4));
      return this;
   }

   public X509v2CRLBuilder addCRLEntry(BigInteger var1, Date var2, Extensions var3) {
      this.tbsGen.addCRLEntry(new ASN1Integer(var1), new Time(var2), var3);
      return this;
   }

   public X509v2CRLBuilder addCRL(X509CRLHolder var1) {
      TBSCertList var2 = var1.toASN1Structure().getTBSCertList();
      if (var2 != null) {
         Enumeration var3 = var2.getRevokedCertificateEnumeration();

         while (var3.hasMoreElements()) {
            this.tbsGen.addCRLEntry(ASN1Sequence.getInstance(((ASN1Encodable)var3.nextElement()).toASN1Primitive()));
         }
      }

      return this;
   }

   public X509v2CRLBuilder addExtension(ASN1ObjectIdentifier var1, boolean var2, ASN1Encodable var3) throws CertIOException {
      CertUtils.addExtension(this.extGenerator, var1, var2, var3);
      return this;
   }

   public X509v2CRLBuilder addExtension(ASN1ObjectIdentifier var1, boolean var2, byte[] var3) throws CertIOException {
      this.extGenerator.addExtension(var1, var2, var3);
      return this;
   }

   public X509v2CRLBuilder addExtension(Extension var1) throws CertIOException {
      this.extGenerator.addExtension(var1);
      return this;
   }

   public X509v2CRLBuilder replaceExtension(ASN1ObjectIdentifier var1, boolean var2, ASN1Encodable var3) throws CertIOException {
      try {
         this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(var1, var2, new DEROctetString(var3)));
         return this;
      } catch (IOException var5) {
         throw new CertIOException("cannot encode extension: " + var5.getMessage(), var5);
      }
   }

   public X509v2CRLBuilder replaceExtension(Extension var1) throws CertIOException {
      this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, var1);
      return this;
   }

   public X509v2CRLBuilder replaceExtension(ASN1ObjectIdentifier var1, boolean var2, byte[] var3) throws CertIOException {
      this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(var1, var2, var3));
      return this;
   }

   public X509v2CRLBuilder removeExtension(ASN1ObjectIdentifier var1) {
      this.extGenerator = CertUtils.doRemoveExtension(this.extGenerator, var1);
      return this;
   }

   public X509CRLHolder build(ContentSigner var1) {
      this.tbsGen.setSignature(var1.getAlgorithmIdentifier());
      if (!this.extGenerator.isEmpty()) {
         this.tbsGen.setExtensions(this.extGenerator.generate());
      }

      return generateFullCRL(var1, this.tbsGen.generateTBSCertList());
   }

   public X509CRLHolder build(ContentSigner var1, boolean var2, ContentSigner var3) {
      this.tbsGen.setSignature(null);

      try {
         this.extGenerator.addExtension(Extension.altSignatureAlgorithm, var2, var3.getAlgorithmIdentifier());
      } catch (IOException var6) {
         throw Exceptions.illegalStateException("cannot add altSignatureAlgorithm extension", var6);
      }

      this.tbsGen.setExtensions(this.extGenerator.generate());

      try {
         this.extGenerator.addExtension(Extension.altSignatureValue, var2, new DERBitString(generateSig(var3, this.tbsGen.generatePreTBSCertList())));
         this.tbsGen.setSignature(var1.getAlgorithmIdentifier());
         this.tbsGen.setExtensions(this.extGenerator.generate());
         TBSCertList var4 = this.tbsGen.generateTBSCertList();
         return new X509CRLHolder(generateCRLStructure(var4, var1.getAlgorithmIdentifier(), generateSig(var1, var4)));
      } catch (IOException var5) {
         throw Exceptions.illegalArgumentException("cannot produce certificate signature", var5);
      }
   }

   private static X509CRLHolder generateFullCRL(ContentSigner var0, TBSCertList var1) {
      try {
         return new X509CRLHolder(generateCRLStructure(var1, var0.getAlgorithmIdentifier(), generateSig(var0, var1)));
      } catch (IOException var3) {
         throw Exceptions.illegalStateException("cannot produce certificate signature", var3);
      }
   }

   private static CertificateList generateCRLStructure(TBSCertList var0, AlgorithmIdentifier var1, byte[] var2) {
      ASN1EncodableVector var3 = new ASN1EncodableVector();
      var3.add(var0);
      var3.add(var1);
      var3.add(new DERBitString(var2));
      return CertificateList.getInstance(new DERSequence(var3));
   }

   private static byte[] generateSig(ContentSigner var0, ASN1Object var1) throws IOException {
      OutputStream var2 = var0.getOutputStream();
      var1.encodeTo(var2, "DER");
      var2.close();
      return var0.getSignature();
   }
}
