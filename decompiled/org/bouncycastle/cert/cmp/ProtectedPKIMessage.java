package org.bouncycastle.cert.cmp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.PBEMacCalculatorProvider;
import org.bouncycastle.util.Arrays;

public class ProtectedPKIMessage {
   private PKIMessage pkiMessage;

   public ProtectedPKIMessage(GeneralPKIMessage var1) {
      if (!var1.hasProtection()) {
         throw new IllegalArgumentException("PKIMessage not protected");
      } else {
         this.pkiMessage = var1.toASN1Structure();
      }
   }

   ProtectedPKIMessage(PKIMessage var1) {
      if (var1.getHeader().getProtectionAlg() == null) {
         throw new IllegalArgumentException("PKIMessage not protected");
      } else {
         this.pkiMessage = var1;
      }
   }

   public PKIHeader getHeader() {
      return this.pkiMessage.getHeader();
   }

   public PKIBody getBody() {
      return this.pkiMessage.getBody();
   }

   public PKIMessage toASN1Structure() {
      return this.pkiMessage;
   }

   public boolean hasPasswordBasedMacProtection() {
      return CMPObjectIdentifiers.passwordBasedMac.equals(this.getProtectionAlgorithm().getAlgorithm());
   }

   public AlgorithmIdentifier getProtectionAlgorithm() {
      return this.pkiMessage.getHeader().getProtectionAlg();
   }

   public X509CertificateHolder[] getCertificates() {
      CMPCertificate[] var1 = this.pkiMessage.getExtraCerts();
      if (var1 == null) {
         return new X509CertificateHolder[0];
      } else {
         X509CertificateHolder[] var2 = new X509CertificateHolder[var1.length];

         for (int var3 = 0; var3 != var1.length; var3++) {
            var2[var3] = new X509CertificateHolder(var1[var3].getX509v3PKCert());
         }

         return var2;
      }
   }

   public boolean verify(ContentVerifierProvider var1) throws CMPException {
      try {
         ContentVerifier var2 = var1.get(this.getProtectionAlgorithm());
         return this.verifySignature(this.pkiMessage.getProtection().getOctets(), var2);
      } catch (Exception var3) {
         throw new CMPException("unable to verify signature: " + var3.getMessage(), var3);
      }
   }

   public boolean verify(PBEMacCalculatorProvider var1, char[] var2) throws CMPException {
      try {
         MacCalculator var3 = var1.get(this.getProtectionAlgorithm(), var2);
         CMPUtil.derEncodeToStream(this.createProtected(), var3.getOutputStream());
         return Arrays.constantTimeAreEqual(var3.getMac(), this.pkiMessage.getProtection().getOctets());
      } catch (Exception var4) {
         throw new CMPException("unable to verify MAC: " + var4.getMessage(), var4);
      }
   }

   private boolean verifySignature(byte[] var1, ContentVerifier var2) {
      CMPUtil.derEncodeToStream(this.createProtected(), var2.getOutputStream());
      return var2.verify(var1);
   }

   private DERSequence createProtected() {
      return new DERSequence(this.pkiMessage.getHeader(), this.pkiMessage.getBody());
   }
}
