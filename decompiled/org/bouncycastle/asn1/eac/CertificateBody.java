package org.bouncycastle.asn1.eac;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class CertificateBody extends ASN1Object {
   ASN1InputStream seq;
   private ASN1TaggedObject certificateProfileIdentifier;
   private ASN1TaggedObject certificationAuthorityReference;
   private PublicKeyDataObject publicKey;
   private ASN1TaggedObject certificateHolderReference;
   private CertificateHolderAuthorization certificateHolderAuthorization;
   private ASN1TaggedObject certificateEffectiveDate;
   private ASN1TaggedObject certificateExpirationDate;
   private int certificateType = 0;
   private static final int CPI = 1;
   private static final int CAR = 2;
   private static final int PK = 4;
   private static final int CHR = 8;
   private static final int CHA = 16;
   private static final int CEfD = 32;
   private static final int CExD = 64;
   /** @deprecated */
   public static final int profileType = 127;
   private static final int profileType_m = 127;
   private static final int profileType_r = 0;
   /** @deprecated */
   public static final int requestType = 13;
   private static final int requestType_m = 13;
   private static final int requestType_r = 2;

   private void setIso7816CertificateBody(ASN1TaggedObject var1) throws IOException {
      if (var1.hasTag(64, 78)) {
         ASN1Sequence var2 = ASN1Sequence.getInstance(var1.getBaseUniversal(false, 16));
         int var3 = 0;

         for (int var4 = var2.size(); var3 < var4; var3++) {
            ASN1TaggedObject var5 = ASN1TaggedObject.getInstance(var2.getObjectAt(var3), 64);
            switch (var5.getTagNo()) {
               case 2:
                  this.setCertificationAuthorityReference(var5);
                  break;
               case 32:
                  this.setCertificateHolderReference(var5);
                  break;
               case 36:
                  this.setCertificateExpirationDate(var5);
                  break;
               case 37:
                  this.setCertificateEffectiveDate(var5);
                  break;
               case 41:
                  this.setCertificateProfileIdentifier(var5);
                  break;
               case 73:
                  this.setPublicKey(PublicKeyDataObject.getInstance(var5.getBaseUniversal(false, 16)));
                  break;
               case 76:
                  this.setCertificateHolderAuthorization(new CertificateHolderAuthorization(var5));
                  break;
               default:
                  this.certificateType = 0;
                  throw new IOException("Not a valid iso7816 ASN1TaggedObject tag " + var5.getTagNo());
            }
         }
      } else {
         throw new IOException("Bad tag : not an iso7816 CERTIFICATE_CONTENT_TEMPLATE");
      }
   }

   public CertificateBody(
      ASN1TaggedObject var1,
      CertificationAuthorityReference var2,
      PublicKeyDataObject var3,
      CertificateHolderReference var4,
      CertificateHolderAuthorization var5,
      PackedDate var6,
      PackedDate var7
   ) {
      this.setCertificateProfileIdentifier(var1);
      this.setCertificationAuthorityReference(EACTagged.create(2, var2.getEncoded()));
      this.setPublicKey(var3);
      this.setCertificateHolderReference(EACTagged.create(32, var4.getEncoded()));
      this.setCertificateHolderAuthorization(var5);
      this.setCertificateEffectiveDate(EACTagged.create(37, var6.getEncoding()));
      this.setCertificateExpirationDate(EACTagged.create(36, var7.getEncoding()));
   }

   private CertificateBody(ASN1TaggedObject var1) throws IOException {
      this.setIso7816CertificateBody(var1);
   }

   private ASN1Primitive profileToASN1Object() throws IOException {
      ASN1EncodableVector var1 = new ASN1EncodableVector(7);
      var1.add(this.certificateProfileIdentifier);
      var1.add(this.certificationAuthorityReference);
      var1.add(EACTagged.create(73, this.publicKey));
      var1.add(this.certificateHolderReference);
      var1.add(this.certificateHolderAuthorization);
      var1.add(this.certificateEffectiveDate);
      var1.add(this.certificateExpirationDate);
      return EACTagged.create(78, new DERSequence(var1));
   }

   private void setCertificateProfileIdentifier(ASN1TaggedObject var1) throws IllegalArgumentException {
      if (var1.hasTag(64, 41)) {
         this.certificateProfileIdentifier = var1;
         this.certificateType |= 1;
      } else {
         throw new IllegalArgumentException("Not an Iso7816Tags.INTERCHANGE_PROFILE tag :" + var1.getTagNo());
      }
   }

   private void setCertificateHolderReference(ASN1TaggedObject var1) throws IllegalArgumentException {
      if (var1.hasTag(64, 32)) {
         this.certificateHolderReference = var1;
         this.certificateType |= 8;
      } else {
         throw new IllegalArgumentException("Not an Iso7816Tags.CARDHOLDER_NAME tag");
      }
   }

   private void setCertificationAuthorityReference(ASN1TaggedObject var1) throws IllegalArgumentException {
      if (var1.hasTag(64, 2)) {
         this.certificationAuthorityReference = var1;
         this.certificateType |= 2;
      } else {
         throw new IllegalArgumentException("Not an Iso7816Tags.ISSUER_IDENTIFICATION_NUMBER tag");
      }
   }

   private void setPublicKey(PublicKeyDataObject var1) {
      this.publicKey = PublicKeyDataObject.getInstance(var1);
      this.certificateType |= 4;
   }

   private ASN1Primitive requestToASN1Object() throws IOException {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.certificateProfileIdentifier);
      if (this.certificationAuthorityReference != null) {
         var1.add(this.certificationAuthorityReference);
      }

      var1.add(EACTagged.create(73, this.publicKey));
      var1.add(this.certificateHolderReference);
      return EACTagged.create(78, new DERSequence(var1));
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      try {
         if ((this.certificateType & -1) == 127) {
            return this.profileToASN1Object();
         }

         if ((this.certificateType & -3) == 13) {
            return this.requestToASN1Object();
         }
      } catch (IOException var2) {
      }

      return null;
   }

   public int getCertificateType() {
      return this.certificateType;
   }

   public static CertificateBody getInstance(Object var0) throws IOException {
      if (var0 instanceof CertificateBody) {
         return (CertificateBody)var0;
      } else {
         return var0 != null ? new CertificateBody(ASN1TaggedObject.getInstance(var0, 64)) : null;
      }
   }

   public PackedDate getCertificateEffectiveDate() {
      return (this.certificateType & 32) == 32
         ? new PackedDate(ASN1OctetString.getInstance(this.certificateEffectiveDate.getBaseUniversal(false, 4)).getOctets())
         : null;
   }

   private void setCertificateEffectiveDate(ASN1TaggedObject var1) throws IllegalArgumentException {
      if (var1.hasTag(64, 37)) {
         this.certificateEffectiveDate = var1;
         this.certificateType |= 32;
      } else {
         throw new IllegalArgumentException("Not an Iso7816Tags.APPLICATION_EFFECTIVE_DATE tag :" + var1.getTagNo());
      }
   }

   public PackedDate getCertificateExpirationDate() throws IOException {
      if ((this.certificateType & 64) == 64) {
         return new PackedDate(ASN1OctetString.getInstance(this.certificateExpirationDate.getBaseUniversal(false, 4)).getOctets());
      } else {
         throw new IOException("certificate Expiration Date not set");
      }
   }

   private void setCertificateExpirationDate(ASN1TaggedObject var1) throws IllegalArgumentException {
      if (var1.hasTag(64, 36)) {
         this.certificateExpirationDate = var1;
         this.certificateType |= 64;
      } else {
         throw new IllegalArgumentException("Not an Iso7816Tags.APPLICATION_EXPIRATION_DATE tag");
      }
   }

   public CertificateHolderAuthorization getCertificateHolderAuthorization() throws IOException {
      if ((this.certificateType & 16) == 16) {
         return this.certificateHolderAuthorization;
      } else {
         throw new IOException("Certificate Holder Authorisation not set");
      }
   }

   private void setCertificateHolderAuthorization(CertificateHolderAuthorization var1) {
      this.certificateHolderAuthorization = var1;
      this.certificateType |= 16;
   }

   public CertificateHolderReference getCertificateHolderReference() {
      return new CertificateHolderReference(ASN1OctetString.getInstance(this.certificateHolderReference.getBaseUniversal(false, 4)).getOctets());
   }

   public ASN1TaggedObject getCertificateProfileIdentifier() {
      return this.certificateProfileIdentifier;
   }

   public CertificationAuthorityReference getCertificationAuthorityReference() throws IOException {
      if ((this.certificateType & 2) == 2) {
         return new CertificationAuthorityReference(ASN1OctetString.getInstance(this.certificationAuthorityReference.getBaseUniversal(false, 4)).getOctets());
      } else {
         throw new IOException("Certification authority reference not set");
      }
   }

   public PublicKeyDataObject getPublicKey() {
      return this.publicKey;
   }
}
