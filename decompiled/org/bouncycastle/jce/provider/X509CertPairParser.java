package org.bouncycastle.jce.provider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateParsingException;
import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.x509.X509CertificatePair;
import org.bouncycastle.x509.X509StreamParserSpi;
import org.bouncycastle.x509.util.StreamParsingException;

public class X509CertPairParser extends X509StreamParserSpi {
   private InputStream currentStream = null;

   private X509CertificatePair readDERCrossCertificatePair(InputStream var1) throws IOException, CertificateParsingException {
      ASN1InputStream var2 = new ASN1InputStream(var1);
      ASN1Sequence var3 = (ASN1Sequence)var2.readObject();
      CertificatePair var4 = CertificatePair.getInstance(var3);
      return new X509CertificatePair(var4);
   }

   @Override
   public void engineInit(InputStream var1) {
      this.currentStream = var1;
      if (!this.currentStream.markSupported()) {
         this.currentStream = new BufferedInputStream(this.currentStream);
      }
   }

   @Override
   public Object engineRead() throws StreamParsingException {
      try {
         this.currentStream.mark(10);
         int var1 = this.currentStream.read();
         if (var1 == -1) {
            return null;
         } else {
            this.currentStream.reset();
            return this.readDERCrossCertificatePair(this.currentStream);
         }
      } catch (Exception var2) {
         throw new StreamParsingException(var2.toString(), var2);
      }
   }

   @Override
   public Collection engineReadAll() throws StreamParsingException {
      ArrayList var2 = new ArrayList();

      X509CertificatePair var1;
      while ((var1 = (X509CertificatePair)this.engineRead()) != null) {
         var2.add(var1);
      }

      return var2;
   }
}
