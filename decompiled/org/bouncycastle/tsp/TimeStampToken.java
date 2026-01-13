package org.bouncycastle.tsp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificate;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Store;

public class TimeStampToken {
   CMSSignedData tsToken;
   SignerInformation tsaSignerInfo;
   TimeStampTokenInfo tstInfo;
   ESSCertIDv2 certID;

   public TimeStampToken(ContentInfo var1) throws TSPException, IOException {
      this(getSignedData(var1));
   }

   private static CMSSignedData getSignedData(ContentInfo var0) throws TSPException {
      try {
         return new CMSSignedData(var0);
      } catch (CMSException var2) {
         throw new TSPException("TSP parsing error: " + var2.getMessage(), var2.getCause());
      }
   }

   public TimeStampToken(CMSSignedData var1) throws TSPException, IOException {
      this.tsToken = var1;
      if (!this.tsToken.getSignedContentTypeOID().equals(PKCSObjectIdentifiers.id_ct_TSTInfo.getId())) {
         throw new TSPValidationException("ContentInfo object not for a time stamp.");
      } else {
         Collection var2 = this.tsToken.getSignerInfos().getSigners();
         if (var2.size() != 1) {
            throw new IllegalArgumentException("Time-stamp token signed by " + var2.size() + " signers, but it must contain just the TSA signature.");
         } else {
            this.tsaSignerInfo = (SignerInformation)var2.iterator().next();

            try {
               CMSTypedData var3 = this.tsToken.getSignedContent();
               ByteArrayOutputStream var4 = new ByteArrayOutputStream();
               var3.write(var4);
               this.tstInfo = new TimeStampTokenInfo(TSTInfo.getInstance(var4.toByteArray()));
               Attribute var5 = this.tsaSignerInfo.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificate);
               if (var5 != null) {
                  SigningCertificate var6 = SigningCertificate.getInstance(var5.getAttrValues().getObjectAt(0));
                  this.certID = ESSCertIDv2.from(ESSCertID.getInstance(var6.getCerts()[0]));
               } else {
                  var5 = this.tsaSignerInfo.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificateV2);
                  if (var5 == null) {
                     throw new TSPValidationException("no signing certificate attribute found, time stamp invalid.");
                  }

                  SigningCertificateV2 var9 = SigningCertificateV2.getInstance(var5.getAttrValues().getObjectAt(0));
                  this.certID = ESSCertIDv2.getInstance(var9.getCerts()[0]);
               }
            } catch (CMSException var7) {
               throw new TSPException(var7.getMessage(), var7.getUnderlyingException());
            }
         }
      }
   }

   public TimeStampTokenInfo getTimeStampInfo() {
      return this.tstInfo;
   }

   public SignerId getSID() {
      return this.tsaSignerInfo.getSID();
   }

   public AttributeTable getSignedAttributes() {
      return this.tsaSignerInfo.getSignedAttributes();
   }

   public AttributeTable getUnsignedAttributes() {
      return this.tsaSignerInfo.getUnsignedAttributes();
   }

   public Store<X509CertificateHolder> getCertificates() {
      return this.tsToken.getCertificates();
   }

   public Store<X509CRLHolder> getCRLs() {
      return this.tsToken.getCRLs();
   }

   public Store<X509AttributeCertificateHolder> getAttributeCertificates() {
      return this.tsToken.getAttributeCertificates();
   }

   public void validate(SignerInformationVerifier var1) throws TSPException, TSPValidationException {
      if (!var1.hasAssociatedCertificate()) {
         throw new IllegalArgumentException("verifier provider needs an associated certificate");
      } else {
         try {
            X509CertificateHolder var2 = var1.getAssociatedCertificate();
            DigestCalculator var3 = var1.getDigestCalculator(this.certID.getHashAlgorithm());
            OutputStream var4 = var3.getOutputStream();
            var4.write(var2.getEncoded());
            var4.close();
            if (!Arrays.constantTimeAreEqual(this.certID.getCertHashObject().getOctets(), var3.getDigest())) {
               throw new TSPValidationException("certificate hash does not match certID hash.");
            } else {
               IssuerSerial var5 = this.certID.getIssuerSerial();
               if (var5 != null) {
                  Certificate var6 = var2.toASN1Structure();
                  if (!var5.getSerial().equals(var6.getSerialNumber())) {
                     throw new TSPValidationException("certificate serial number does not match certID for signature.");
                  }

                  GeneralName[] var7 = var5.getIssuer().getNames();
                  boolean var8 = false;

                  for (int var9 = 0; var9 != var7.length; var9++) {
                     if (var7[var9].getTagNo() == 4 && X500Name.getInstance(var7[var9].getName()).equals(var6.getIssuer())) {
                        var8 = true;
                        break;
                     }
                  }

                  if (!var8) {
                     throw new TSPValidationException("certificate name does not match certID for signature. ");
                  }
               }

               TSPUtil.validateCertificate(var2);
               if (!var2.isValidOn(this.tstInfo.getGenTime())) {
                  throw new TSPValidationException("certificate not valid when time stamp created.");
               } else if (!this.tsaSignerInfo.verify(var1)) {
                  throw new TSPValidationException("signature not created by certificate.");
               }
            }
         } catch (CMSException var10) {
            if (var10.getUnderlyingException() != null) {
               throw new TSPException(var10.getMessage(), var10.getUnderlyingException());
            } else {
               throw new TSPException("CMS exception: " + var10, var10);
            }
         } catch (IOException var11) {
            throw new TSPException("problem processing certificate: " + var11, var11);
         } catch (OperatorCreationException var12) {
            throw new TSPException("unable to create digest: " + var12.getMessage(), var12);
         }
      }
   }

   public boolean isSignatureValid(SignerInformationVerifier var1) throws TSPException {
      try {
         return this.tsaSignerInfo.verify(var1);
      } catch (CMSException var3) {
         if (var3.getUnderlyingException() != null) {
            throw new TSPException(var3.getMessage(), var3.getUnderlyingException());
         } else {
            throw new TSPException("CMS exception: " + var3, var3);
         }
      }
   }

   public CMSSignedData toCMSSignedData() {
      return this.tsToken;
   }

   public byte[] getEncoded() throws IOException {
      return this.tsToken.getEncoded("DL");
   }

   public byte[] getEncoded(String var1) throws IOException {
      return this.tsToken.getEncoded(var1);
   }
}
