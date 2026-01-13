package org.bouncycastle.jce.provider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.x509.X509StreamParserSpi;
import org.bouncycastle.x509.util.StreamParsingException;

public class X509CRLParser extends X509StreamParserSpi {
   private static final PEMUtil PEM_PARSER = new PEMUtil("CRL");
   private ASN1Set sData = null;
   private int sDataObjectCount = 0;
   private InputStream currentStream = null;

   private CRL readDERCRL(InputStream var1) throws IOException, CRLException {
      ASN1InputStream var2 = new ASN1InputStream(var1);
      ASN1Sequence var3 = (ASN1Sequence)var2.readObject();
      if (var3.size() > 1 && var3.getObjectAt(0) instanceof ASN1ObjectIdentifier && var3.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData)) {
         this.sData = new SignedData(ASN1Sequence.getInstance((ASN1TaggedObject)var3.getObjectAt(1), true)).getCRLs();
         return this.getCRL();
      } else {
         return new X509CRLObject(CertificateList.getInstance(var3));
      }
   }

   private CRL getCRL() throws CRLException {
      return this.sData != null && this.sDataObjectCount < this.sData.size()
         ? new X509CRLObject(CertificateList.getInstance(this.sData.getObjectAt(this.sDataObjectCount++)))
         : null;
   }

   private CRL readPEMCRL(InputStream var1) throws IOException, CRLException {
      ASN1Sequence var2 = PEM_PARSER.readPEMObject(var1);
      return var2 != null ? new X509CRLObject(CertificateList.getInstance(var2)) : null;
   }

   @Override
   public void engineInit(InputStream var1) {
      this.currentStream = var1;
      this.sData = null;
      this.sDataObjectCount = 0;
      if (!this.currentStream.markSupported()) {
         this.currentStream = new BufferedInputStream(this.currentStream);
      }
   }

   @Override
   public Object engineRead() throws StreamParsingException {
      try {
         if (this.sData != null) {
            if (this.sDataObjectCount != this.sData.size()) {
               return this.getCRL();
            } else {
               this.sData = null;
               this.sDataObjectCount = 0;
               return null;
            }
         } else {
            this.currentStream.mark(10);
            int var1 = this.currentStream.read();
            if (var1 == -1) {
               return null;
            } else if (var1 != 48) {
               this.currentStream.reset();
               return this.readPEMCRL(this.currentStream);
            } else {
               this.currentStream.reset();
               return this.readDERCRL(this.currentStream);
            }
         }
      } catch (Exception var2) {
         throw new StreamParsingException(var2.toString(), var2);
      }
   }

   @Override
   public Collection engineReadAll() throws StreamParsingException {
      ArrayList var2 = new ArrayList();

      CRL var1;
      while ((var1 = (CRL)this.engineRead()) != null) {
         var2.add(var1);
      }

      return var2;
   }
}
