package org.bouncycastle.tsp;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.LocaleUtil;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificate;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.Accuracy;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.tsp.TimeStampReq;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSAttributeTableGenerationException;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;

public class TimeStampTokenGenerator {
   public static final int R_SECONDS = 0;
   public static final int R_TENTHS_OF_SECONDS = 1;
   public static final int R_HUNDREDTHS_OF_SECONDS = 2;
   /** @deprecated */
   public static final int R_MICROSECONDS = 2;
   public static final int R_MILLISECONDS = 3;
   private int resolution = 0;
   private Locale locale = null;
   private int accuracySeconds = -1;
   private int accuracyMillis = -1;
   private int accuracyMicros = -1;
   boolean ordering = false;
   GeneralName tsa = null;
   private ASN1ObjectIdentifier tsaPolicyOID;
   private List certs = new ArrayList();
   private List crls = new ArrayList();
   private List attrCerts = new ArrayList();
   private Map otherRevoc = new HashMap();
   private SignerInfoGenerator signerInfoGen;

   public TimeStampTokenGenerator(SignerInfoGenerator var1, DigestCalculator var2, ASN1ObjectIdentifier var3) throws IllegalArgumentException, TSPException {
      this(var1, var2, var3, false);
   }

   public TimeStampTokenGenerator(final SignerInfoGenerator var1, DigestCalculator var2, ASN1ObjectIdentifier var3, boolean var4) throws IllegalArgumentException, TSPException {
      this.signerInfoGen = var1;
      this.tsaPolicyOID = var3;
      if (!var1.hasAssociatedCertificate()) {
         throw new IllegalArgumentException("SignerInfoGenerator must have an associated certificate");
      } else {
         X509CertificateHolder var5 = var1.getAssociatedCertificate();
         TSPUtil.validateCertificate(var5);
         AlgorithmIdentifier var6 = var2.getAlgorithmIdentifier();
         ASN1ObjectIdentifier var7 = var6.getAlgorithm();

         try {
            OutputStream var8 = var2.getOutputStream();
            var8.write(var5.getEncoded());
            var8.close();
            DEROctetString var9 = new DEROctetString(var2.getDigest());
            IssuerSerial var10 = null;
            if (var4) {
               GeneralNames var11 = new GeneralNames(new GeneralName(var5.getIssuer()));
               ASN1Integer var12 = var5.toASN1Structure().getSerialNumber();
               var10 = new IssuerSerial(var11, var12);
            }

            if (OIWObjectIdentifiers.idSHA1.equals(var7)) {
               final ESSCertID var15 = new ESSCertID(var9, var10);
               this.signerInfoGen = new SignerInfoGenerator(
                  var1,
                  new CMSAttributeTableGenerator() {
                     @Override
                     public AttributeTable getAttributes(Map var1x) throws CMSAttributeTableGenerationException {
                        AttributeTable var2x = var1.getSignedAttributeTableGenerator().getAttributes(var1x);
                        return var2x.get(PKCSObjectIdentifiers.id_aa_signingCertificate) == null
                           ? var2x.add(PKCSObjectIdentifiers.id_aa_signingCertificate, new SigningCertificate(var15))
                           : var2x;
                     }
                  },
                  var1.getUnsignedAttributeTableGenerator()
               );
            } else {
               var6 = new AlgorithmIdentifier(var7);
               final ESSCertIDv2 var16 = new ESSCertIDv2(var6, var9, var10);
               this.signerInfoGen = new SignerInfoGenerator(
                  var1,
                  new CMSAttributeTableGenerator() {
                     @Override
                     public AttributeTable getAttributes(Map var1x) throws CMSAttributeTableGenerationException {
                        AttributeTable var2 = var1.getSignedAttributeTableGenerator().getAttributes(var1x);
                        return var2.get(PKCSObjectIdentifiers.id_aa_signingCertificateV2) == null
                           ? var2.add(PKCSObjectIdentifiers.id_aa_signingCertificateV2, new SigningCertificateV2(var16))
                           : var2;
                     }
                  },
                  var1.getUnsignedAttributeTableGenerator()
               );
            }
         } catch (IOException var13) {
            throw new TSPException("Exception processing certificate.", var13);
         }
      }
   }

   public void addCertificates(Store var1) {
      this.certs.addAll(var1.getMatches(null));
   }

   public void addCRLs(Store var1) {
      this.crls.addAll(var1.getMatches(null));
   }

   public void addAttributeCertificates(Store var1) {
      this.attrCerts.addAll(var1.getMatches(null));
   }

   public void addOtherRevocationInfo(ASN1ObjectIdentifier var1, Store var2) {
      this.otherRevoc.put(var1, var2.getMatches(null));
   }

   public void setResolution(int var1) {
      this.resolution = var1;
   }

   public void setLocale(Locale var1) {
      this.locale = var1;
   }

   public void setAccuracySeconds(int var1) {
      this.accuracySeconds = var1;
   }

   public void setAccuracyMillis(int var1) {
      this.accuracyMillis = var1;
   }

   public void setAccuracyMicros(int var1) {
      this.accuracyMicros = var1;
   }

   public void setOrdering(boolean var1) {
      this.ordering = var1;
   }

   public void setTSA(GeneralName var1) {
      this.tsa = var1;
   }

   public TimeStampToken generate(TimeStampRequest var1, BigInteger var2, Date var3) throws TSPException {
      return this.generate(var1, var2, var3, null);
   }

   public TimeStampToken generate(TimeStampRequest var1, BigInteger var2, Date var3, Extensions var4) throws TSPException {
      TimeStampReq var5 = var1.toASN1Structure();
      MessageImprint var6 = var5.getMessageImprint();
      Accuracy var7 = null;
      if (this.accuracySeconds > 0 || this.accuracyMillis > 0 || this.accuracyMicros > 0) {
         ASN1Integer var8 = null;
         if (this.accuracySeconds > 0) {
            var8 = new ASN1Integer(this.accuracySeconds);
         }

         ASN1Integer var9 = null;
         if (this.accuracyMillis > 0) {
            var9 = new ASN1Integer(this.accuracyMillis);
         }

         ASN1Integer var10 = null;
         if (this.accuracyMicros > 0) {
            var10 = new ASN1Integer(this.accuracyMicros);
         }

         var7 = new Accuracy(var8, var9, var10);
      }

      ASN1Boolean var19 = null;
      if (this.ordering) {
         var19 = ASN1Boolean.getInstance(this.ordering);
      }

      ASN1Integer var20 = var5.getNonce();
      ASN1ObjectIdentifier var21 = var5.getReqPolicy();
      if (var21 == null) {
         var21 = this.tsaPolicyOID;
      }

      Extensions var11 = var1.getExtensions();
      if (var4 != null) {
         ExtensionsGenerator var12 = new ExtensionsGenerator();
         if (var11 != null) {
            Enumeration var13 = var11.oids();

            while (var13.hasMoreElements()) {
               var12.addExtension(var11.getExtension(ASN1ObjectIdentifier.getInstance(var13.nextElement())));
            }
         }

         Enumeration var23 = var4.oids();

         while (var23.hasMoreElements()) {
            var12.addExtension(var4.getExtension(ASN1ObjectIdentifier.getInstance(var23.nextElement())));
         }

         var11 = var12.generate();
      }

      ASN1GeneralizedTime var22;
      if (this.resolution == 0) {
         var22 = this.locale == null ? new ASN1GeneralizedTime(var3) : new ASN1GeneralizedTime(var3, this.locale);
      } else {
         var22 = this.createGeneralizedTime(var3);
      }

      TSTInfo var24 = new TSTInfo(var21, var6, new ASN1Integer(var2), var22, var7, var19, var20, this.tsa, var11);

      try {
         CMSSignedDataGenerator var14 = new CMSSignedDataGenerator();
         if (var1.getCertReq()) {
            var14.addCertificates(new CollectionStore(this.certs));
            var14.addAttributeCertificates(new CollectionStore(this.attrCerts));
         }

         var14.addCRLs(new CollectionStore(this.crls));
         if (!this.otherRevoc.isEmpty()) {
            for (ASN1ObjectIdentifier var16 : this.otherRevoc.keySet()) {
               var14.addOtherRevocationInfo(var16, new CollectionStore((Collection)this.otherRevoc.get(var16)));
            }
         }

         var14.addSignerInfoGenerator(this.signerInfoGen);
         byte[] var25 = var24.getEncoded("DER");
         CMSSignedData var26 = var14.generate(new CMSProcessableByteArray(PKCSObjectIdentifiers.id_ct_TSTInfo, var25), true);
         return new TimeStampToken(var26);
      } catch (CMSException var17) {
         throw new TSPException("Error generating time-stamp token", var17);
      } catch (IOException var18) {
         throw new TSPException("Exception encoding info", var18);
      }
   }

   private ASN1GeneralizedTime createGeneralizedTime(Date var1) throws TSPException {
      String var2 = "yyyyMMddHHmmss.SSS";
      SimpleDateFormat var3 = this.locale == null ? new SimpleDateFormat(var2, LocaleUtil.EN_Locale) : new SimpleDateFormat(var2, this.locale);
      var3.setTimeZone(new SimpleTimeZone(0, "Z"));
      StringBuilder var4 = new StringBuilder(var3.format(var1));
      int var5 = var4.indexOf(".");
      if (var5 < 0) {
         var4.append("Z");
         return new ASN1GeneralizedTime(var4.toString());
      } else {
         switch (this.resolution) {
            case 1:
               if (var4.length() > var5 + 2) {
                  var4.delete(var5 + 2, var4.length());
               }
               break;
            case 2:
               if (var4.length() > var5 + 3) {
                  var4.delete(var5 + 3, var4.length());
               }
            case 3:
               break;
            default:
               throw new TSPException("unknown time-stamp resolution: " + this.resolution);
         }

         while (var4.charAt(var4.length() - 1) == '0') {
            var4.deleteCharAt(var4.length() - 1);
         }

         if (var4.length() - 1 == var5) {
            var4.deleteCharAt(var4.length() - 1);
         }

         var4.append("Z");
         return new ASN1GeneralizedTime(var4.toString());
      }
   }
}
