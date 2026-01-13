package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAlgorithmProtection;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RawContentVerifier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.TeeOutputStream;

public class SignerInformation {
   private final SignerId sid;
   private final CMSProcessable content;
   private final byte[] signature;
   private final ASN1ObjectIdentifier contentType;
   private final boolean isCounterSignature;
   private AttributeTable signedAttributeValues;
   private AttributeTable unsignedAttributeValues;
   private byte[] resultDigest;
   protected final SignerInfo info;
   protected final AlgorithmIdentifier digestAlgorithm;
   protected final AlgorithmIdentifier encryptionAlgorithm;
   protected final ASN1Set signedAttributeSet;
   protected final ASN1Set unsignedAttributeSet;

   SignerInformation(SignerInfo var1, ASN1ObjectIdentifier var2, CMSProcessable var3, byte[] var4) {
      this.info = var1;
      this.contentType = var2;
      this.isCounterSignature = var2 == null;
      SignerIdentifier var5 = var1.getSID();
      if (var5.isTagged()) {
         ASN1OctetString var6 = ASN1OctetString.getInstance(var5.getId());
         this.sid = new SignerId(var6.getOctets());
      } else {
         IssuerAndSerialNumber var7 = IssuerAndSerialNumber.getInstance(var5.getId());
         this.sid = new SignerId(var7.getName(), var7.getSerialNumber().getValue());
      }

      this.digestAlgorithm = var1.getDigestAlgorithm();
      this.signedAttributeSet = var1.getAuthenticatedAttributes();
      this.unsignedAttributeSet = var1.getUnauthenticatedAttributes();
      this.encryptionAlgorithm = var1.getDigestEncryptionAlgorithm();
      this.signature = var1.getEncryptedDigest().getOctets();
      this.content = var3;
      this.resultDigest = var4;
   }

   protected SignerInformation(SignerInformation var1) {
      this(var1, var1.info);
   }

   protected SignerInformation(SignerInformation var1, SignerInfo var2) {
      this.info = var2;
      this.contentType = var1.contentType;
      this.isCounterSignature = var1.isCounterSignature();
      this.sid = var1.getSID();
      this.digestAlgorithm = var2.getDigestAlgorithm();
      this.signedAttributeSet = var2.getAuthenticatedAttributes();
      this.unsignedAttributeSet = var2.getUnauthenticatedAttributes();
      this.encryptionAlgorithm = var2.getDigestEncryptionAlgorithm();
      this.signature = var2.getEncryptedDigest().getOctets();
      this.content = var1.content;
      this.resultDigest = var1.resultDigest;
      this.signedAttributeValues = this.getSignedAttributes();
      this.unsignedAttributeValues = this.getUnsignedAttributes();
   }

   public boolean isCounterSignature() {
      return this.isCounterSignature;
   }

   public ASN1ObjectIdentifier getContentType() {
      return this.contentType;
   }

   public SignerId getSID() {
      return this.sid;
   }

   public int getVersion() {
      return this.info.getVersion().intValueExact();
   }

   public AlgorithmIdentifier getDigestAlgorithmID() {
      return this.digestAlgorithm;
   }

   public String getDigestAlgOID() {
      return this.digestAlgorithm.getAlgorithm().getId();
   }

   public byte[] getDigestAlgParams() {
      try {
         return CMSUtils.encodeObj(this.digestAlgorithm.getParameters());
      } catch (Exception var2) {
         throw new RuntimeException("exception getting digest parameters " + var2);
      }
   }

   public byte[] getContentDigest() {
      if (this.resultDigest == null) {
         throw new IllegalStateException("method can only be called after verify.");
      } else {
         return Arrays.clone(this.resultDigest);
      }
   }

   public String getEncryptionAlgOID() {
      return this.encryptionAlgorithm.getAlgorithm().getId();
   }

   public byte[] getEncryptionAlgParams() {
      try {
         return CMSUtils.encodeObj(this.encryptionAlgorithm.getParameters());
      } catch (Exception var2) {
         throw new RuntimeException("exception getting encryption parameters " + var2);
      }
   }

   public AttributeTable getSignedAttributes() {
      if (this.signedAttributeSet != null && this.signedAttributeValues == null) {
         this.signedAttributeValues = new AttributeTable(this.signedAttributeSet);
      }

      return this.signedAttributeValues;
   }

   public AttributeTable getUnsignedAttributes() {
      if (this.unsignedAttributeSet != null && this.unsignedAttributeValues == null) {
         this.unsignedAttributeValues = new AttributeTable(this.unsignedAttributeSet);
      }

      return this.unsignedAttributeValues;
   }

   public byte[] getSignature() {
      return Arrays.clone(this.signature);
   }

   public SignerInformationStore getCounterSignatures() {
      AttributeTable var1 = this.getUnsignedAttributes();
      if (var1 == null) {
         return new SignerInformationStore(new ArrayList<>(0));
      } else {
         ArrayList var2 = new ArrayList();
         ASN1EncodableVector var3 = var1.getAll(CMSAttributes.counterSignature);

         for (int var4 = 0; var4 < var3.size(); var4++) {
            Attribute var5 = (Attribute)var3.get(var4);
            ASN1Set var6 = var5.getAttrValues();
            if (var6.size() < 1) {
            }

            Enumeration var7 = var6.getObjects();

            while (var7.hasMoreElements()) {
               SignerInfo var8 = SignerInfo.getInstance(var7.nextElement());
               var2.add(new SignerInformation(var8, null, new CMSProcessableByteArray(this.getSignature()), null));
            }
         }

         return new SignerInformationStore(var2);
      }
   }

   public byte[] getEncodedSignedAttributes() throws IOException {
      return this.signedAttributeSet != null ? this.signedAttributeSet.getEncoded("DER") : null;
   }

   private boolean doVerify(SignerInformationVerifier var1) throws CMSException {
      String var2 = CMSSignedHelper.INSTANCE.getEncryptionAlgName(this.getEncryptionAlgOID());
      AlgorithmIdentifier var3 = this.signedAttributeSet != null
         ? this.info.getDigestAlgorithm()
         : translateBrokenRSAPkcs7(this.encryptionAlgorithm, this.info.getDigestAlgorithm());

      ContentVerifier var4;
      try {
         var4 = var1.getContentVerifier(this.encryptionAlgorithm, var3);
      } catch (OperatorCreationException var12) {
         throw new CMSException("can't create content verifier: " + var12.getMessage(), var12);
      }

      try {
         OutputStream var5 = var4.getOutputStream();
         if (this.resultDigest == null) {
            DigestCalculator var6 = var1.getDigestCalculator(var3);
            if (this.content != null) {
               OutputStream var7 = var6.getOutputStream();
               if (this.signedAttributeSet == null) {
                  if (var4 instanceof RawContentVerifier) {
                     this.content.write(var7);
                  } else {
                     TeeOutputStream var8 = new TeeOutputStream(var7, var5);
                     this.content.write(var8);
                     var8.close();
                  }
               } else {
                  this.content.write(var7);
                  var5.write(this.getEncodedSignedAttributes());
               }

               var7.close();
            } else {
               if (this.signedAttributeSet == null) {
                  throw new CMSException("data not encapsulated in signature - use detached constructor.");
               }

               var5.write(this.getEncodedSignedAttributes());
            }

            this.resultDigest = var6.getDigest();
         } else if (this.signedAttributeSet == null) {
            if (this.content != null) {
               this.content.write(var5);
            }
         } else {
            var5.write(this.getEncodedSignedAttributes());
         }

         var5.close();
      } catch (IOException var10) {
         throw new CMSException("can't process mime object to create signature.", var10);
      } catch (OperatorCreationException var11) {
         throw new CMSException("can't create digest calculator: " + var11.getMessage(), var11);
      }

      this.verifyContentTypeAttributeValue();
      AttributeTable var13 = this.getSignedAttributes();
      this.verifyAlgorithmIdentifierProtectionAttribute(var13);
      this.verifyMessageDigestAttribute();
      this.verifyCounterSignatureAttribute(var13);

      try {
         if (this.signedAttributeSet == null && this.resultDigest != null && var4 instanceof RawContentVerifier) {
            RawContentVerifier var14 = (RawContentVerifier)var4;
            if (var2.equals("RSA")) {
               DigestInfo var15 = new DigestInfo(new AlgorithmIdentifier(var3.getAlgorithm(), DERNull.INSTANCE), this.resultDigest);
               return var14.verify(var15.getEncoded("DER"), this.getSignature());
            } else {
               return var14.verify(this.resultDigest, this.getSignature());
            }
         } else {
            return var4.verify(this.getSignature());
         }
      } catch (IOException var9) {
         throw new CMSException("can't process mime object to create signature.", var9);
      }
   }

   private void verifyContentTypeAttributeValue() throws CMSException {
      ASN1Primitive var1 = this.getSingleValuedSignedAttribute(CMSAttributes.contentType, "content-type");
      if (var1 == null) {
         if (!this.isCounterSignature && this.signedAttributeSet != null) {
            throw new CMSException("The content-type attribute type MUST be present whenever signed attributes are present in signed-data");
         }
      } else {
         if (this.isCounterSignature) {
            throw new CMSException("[For counter signatures,] the signedAttributes field MUST NOT contain a content-type attribute");
         }

         if (!(var1 instanceof ASN1ObjectIdentifier)) {
            throw new CMSException("content-type attribute value not of ASN.1 type 'OBJECT IDENTIFIER'");
         }

         ASN1ObjectIdentifier var2 = (ASN1ObjectIdentifier)var1;
         if (!var2.equals(this.contentType)) {
            throw new CMSException("content-type attribute value does not match eContentType");
         }
      }
   }

   private void verifyMessageDigestAttribute() throws CMSException {
      ASN1Primitive var1 = this.getSingleValuedSignedAttribute(CMSAttributes.messageDigest, "message-digest");
      if (var1 == null) {
         if (this.signedAttributeSet != null) {
            throw new CMSException("the message-digest signed attribute type MUST be present when there are any signed attributes present");
         }
      } else {
         if (!(var1 instanceof ASN1OctetString)) {
            throw new CMSException("message-digest attribute value not of ASN.1 type 'OCTET STRING'");
         }

         ASN1OctetString var2 = (ASN1OctetString)var1;
         if (!Arrays.constantTimeAreEqual(this.resultDigest, var2.getOctets())) {
            throw new CMSSignerDigestMismatchException("message-digest attribute value does not match calculated value");
         }
      }
   }

   private void verifyAlgorithmIdentifierProtectionAttribute(AttributeTable var1) throws CMSException {
      AttributeTable var2 = this.getUnsignedAttributes();
      if (var2 != null && var2.getAll(CMSAttributes.cmsAlgorithmProtect).size() > 0) {
         throw new CMSException("A cmsAlgorithmProtect attribute MUST be a signed attribute");
      } else {
         if (var1 != null) {
            ASN1EncodableVector var3 = var1.getAll(CMSAttributes.cmsAlgorithmProtect);
            if (var3.size() > 1) {
               throw new CMSException("Only one instance of a cmsAlgorithmProtect attribute can be present");
            }

            if (var3.size() > 0) {
               Attribute var4 = Attribute.getInstance(var3.get(0));
               if (var4.getAttrValues().size() != 1) {
                  throw new CMSException("A cmsAlgorithmProtect attribute MUST contain exactly one value");
               }

               CMSAlgorithmProtection var5 = CMSAlgorithmProtection.getInstance(var4.getAttributeValues()[0]);
               if (!CMSUtils.isEquivalent(var5.getDigestAlgorithm(), this.info.getDigestAlgorithm())) {
                  throw new CMSException("CMS Algorithm Identifier Protection check failed for digestAlgorithm");
               }

               if (!CMSUtils.isEquivalent(var5.getSignatureAlgorithm(), this.info.getDigestEncryptionAlgorithm())) {
                  throw new CMSException("CMS Algorithm Identifier Protection check failed for signatureAlgorithm");
               }
            }
         }
      }
   }

   private void verifyCounterSignatureAttribute(AttributeTable var1) throws CMSException {
      if (var1 != null && var1.getAll(CMSAttributes.counterSignature).size() > 0) {
         throw new CMSException("A countersignature attribute MUST NOT be a signed attribute");
      } else {
         AttributeTable var2 = this.getUnsignedAttributes();
         if (var2 != null) {
            ASN1EncodableVector var3 = var2.getAll(CMSAttributes.counterSignature);

            for (int var4 = 0; var4 < var3.size(); var4++) {
               Attribute var5 = Attribute.getInstance(var3.get(var4));
               if (var5.getAttrValues().size() < 1) {
                  throw new CMSException("A countersignature attribute MUST contain at least one AttributeValue");
               }
            }
         }
      }
   }

   public boolean verify(SignerInformationVerifier var1) throws CMSException {
      Time var2 = this.getSigningTime();
      if (var1.hasAssociatedCertificate() && var2 != null) {
         X509CertificateHolder var3 = var1.getAssociatedCertificate();
         if (!var3.isValidOn(var2.getDate())) {
            throw new CMSVerifierCertificateNotValidException("verifier not valid at signingTime");
         }
      }

      return this.doVerify(var1);
   }

   public SignerInfo toASN1Structure() {
      return this.info;
   }

   private ASN1Primitive getSingleValuedSignedAttribute(ASN1ObjectIdentifier var1, String var2) throws CMSException {
      AttributeTable var3 = this.getUnsignedAttributes();
      if (var3 != null && var3.getAll(var1).size() > 0) {
         throw new CMSException("The " + var2 + " attribute MUST NOT be an unsigned attribute");
      } else {
         AttributeTable var4 = this.getSignedAttributes();
         if (var4 == null) {
            return null;
         } else {
            ASN1EncodableVector var5 = var4.getAll(var1);
            switch (var5.size()) {
               case 0:
                  return null;
               case 1:
                  Attribute var6 = (Attribute)var5.get(0);
                  ASN1Set var7 = var6.getAttrValues();
                  if (var7.size() != 1) {
                     throw new CMSException("A " + var2 + " attribute MUST have a single attribute value");
                  }

                  return var7.getObjectAt(0).toASN1Primitive();
               default:
                  throw new CMSException("The SignedAttributes in a signerInfo MUST NOT include multiple instances of the " + var2 + " attribute");
            }
         }
      }
   }

   private Time getSigningTime() throws CMSException {
      ASN1Primitive var1 = this.getSingleValuedSignedAttribute(CMSAttributes.signingTime, "signing-time");
      if (var1 == null) {
         return null;
      } else {
         try {
            return Time.getInstance(var1);
         } catch (IllegalArgumentException var3) {
            throw new CMSException("signing-time attribute value not a valid 'Time' structure");
         }
      }
   }

   public static SignerInformation replaceUnsignedAttributes(SignerInformation var0, AttributeTable var1) {
      SignerInfo var2 = var0.info;
      DERSet var3 = null;
      if (var1 != null) {
         var3 = new DERSet(var1.toASN1EncodableVector());
      }

      return new SignerInformation(
         new SignerInfo(
            var2.getSID(), var2.getDigestAlgorithm(), var2.getAuthenticatedAttributes(), var2.getDigestEncryptionAlgorithm(), var2.getEncryptedDigest(), var3
         ),
         var0.contentType,
         var0.content,
         null
      );
   }

   public static SignerInformation addCounterSigners(SignerInformation var0, SignerInformationStore var1) {
      SignerInfo var2 = var0.info;
      AttributeTable var3 = var0.getUnsignedAttributes();
      ASN1EncodableVector var4;
      if (var3 != null) {
         var4 = var3.toASN1EncodableVector();
      } else {
         var4 = new ASN1EncodableVector();
      }

      ASN1EncodableVector var5 = new ASN1EncodableVector();
      Iterator var6 = var1.getSigners().iterator();

      while (var6.hasNext()) {
         var5.add(((SignerInformation)var6.next()).toASN1Structure());
      }

      var4.add(new Attribute(CMSAttributes.counterSignature, new DERSet(var5)));
      return new SignerInformation(
         new SignerInfo(
            var2.getSID(),
            var2.getDigestAlgorithm(),
            var2.getAuthenticatedAttributes(),
            var2.getDigestEncryptionAlgorithm(),
            var2.getEncryptedDigest(),
            new DERSet(var4)
         ),
         var0.contentType,
         var0.content,
         null
      );
   }

   private static AlgorithmIdentifier translateBrokenRSAPkcs7(AlgorithmIdentifier var0, AlgorithmIdentifier var1) {
      return !PKCSObjectIdentifiers.rsaEncryption.equals(var0.getAlgorithm())
            || !OIWObjectIdentifiers.sha1WithRSA.equals(var1.getAlgorithm()) && !PKCSObjectIdentifiers.sha1WithRSAEncryption.equals(var1.getAlgorithm())
         ? var1
         : new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
   }
}
