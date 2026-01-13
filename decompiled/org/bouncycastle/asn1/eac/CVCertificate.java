package org.bouncycastle.asn1.eac;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class CVCertificate extends ASN1Object {
   private CertificateBody certificateBody;
   private byte[] signature;
   private int valid;
   private static int bodyValid = 1;
   private static int signValid = 2;

   private void setPrivateData(ASN1TaggedObject var1) throws IOException {
      this.valid = 0;
      if (var1.hasTag(64, 33)) {
         ASN1Sequence var2 = ASN1Sequence.getInstance(var1.getBaseUniversal(false, 16));
         Enumeration var3 = var2.getObjects();

         while (var3.hasMoreElements()) {
            Object var4 = var3.nextElement();
            if (!(var4 instanceof ASN1TaggedObject)) {
               throw new IOException("Invalid Object, not an Iso7816CertificateStructure");
            }

            ASN1TaggedObject var5 = ASN1TaggedObject.getInstance(var4, 64);
            switch (var5.getTagNo()) {
               case 55:
                  this.signature = ASN1OctetString.getInstance(var5.getBaseUniversal(false, 4)).getOctets();
                  this.valid = this.valid | signValid;
                  break;
               case 78:
                  this.certificateBody = CertificateBody.getInstance(var5);
                  this.valid = this.valid | bodyValid;
                  break;
               default:
                  throw new IOException("Invalid tag, not an Iso7816CertificateStructure :" + var5.getTagNo());
            }
         }

         if (this.valid != (signValid | bodyValid)) {
            throw new IOException("invalid CARDHOLDER_CERTIFICATE :" + var1.getTagNo());
         }
      } else {
         throw new IOException("not a CARDHOLDER_CERTIFICATE :" + var1.getTagNo());
      }
   }

   public CVCertificate(ASN1InputStream var1) throws IOException {
      this.initFrom(var1);
   }

   private void initFrom(ASN1InputStream var1) throws IOException {
      ASN1Primitive var2;
      while ((var2 = var1.readObject()) != null) {
         if (!(var2 instanceof ASN1TaggedObject)) {
            throw new IOException("Invalid Input Stream for creating an Iso7816CertificateStructure");
         }

         this.setPrivateData((ASN1TaggedObject)var2);
      }
   }

   private CVCertificate(ASN1TaggedObject var1) throws IOException {
      this.setPrivateData(var1);
   }

   public CVCertificate(CertificateBody var1, byte[] var2) throws IOException {
      this.certificateBody = var1;
      this.signature = Arrays.clone(var2);
      this.valid = this.valid | bodyValid;
      this.valid = this.valid | signValid;
   }

   public static CVCertificate getInstance(Object var0) {
      if (var0 instanceof CVCertificate) {
         return (CVCertificate)var0;
      } else if (var0 != null) {
         try {
            return new CVCertificate(ASN1TaggedObject.getInstance(var0, 64));
         } catch (IOException var2) {
            throw new ASN1ParsingException("unable to parse data: " + var2.getMessage(), var2);
         }
      } else {
         return null;
      }
   }

   public byte[] getSignature() {
      return Arrays.clone(this.signature);
   }

   public CertificateBody getBody() {
      return this.certificateBody;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      DERSequence var1 = new DERSequence(this.certificateBody, EACTagged.create(55, this.signature));
      return EACTagged.create(33, var1);
   }

   public ASN1ObjectIdentifier getHolderAuthorization() throws IOException {
      CertificateHolderAuthorization var1 = this.certificateBody.getCertificateHolderAuthorization();
      return var1.getOid();
   }

   public PackedDate getEffectiveDate() throws IOException {
      return this.certificateBody.getCertificateEffectiveDate();
   }

   public int getCertificateType() {
      return this.certificateBody.getCertificateType();
   }

   public PackedDate getExpirationDate() throws IOException {
      return this.certificateBody.getCertificateExpirationDate();
   }

   public int getRole() throws IOException {
      CertificateHolderAuthorization var1 = this.certificateBody.getCertificateHolderAuthorization();
      return var1.getAccessRights();
   }

   public CertificationAuthorityReference getAuthorityReference() throws IOException {
      return this.certificateBody.getCertificationAuthorityReference();
   }

   public CertificateHolderReference getHolderReference() throws IOException {
      return this.certificateBody.getCertificateHolderReference();
   }

   public int getHolderAuthorizationRole() throws IOException {
      int var1 = this.certificateBody.getCertificateHolderAuthorization().getAccessRights();
      return var1 & 192;
   }

   public Flags getHolderAuthorizationRights() throws IOException {
      return new Flags(this.certificateBody.getCertificateHolderAuthorization().getAccessRights() & 31);
   }
}
