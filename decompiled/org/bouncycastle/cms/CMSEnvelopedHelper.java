package org.bouncycastle.cms;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.KEKRecipientInfo;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.OtherRecipientInfo;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;

class CMSEnvelopedHelper {
   static RecipientInformationStore buildRecipientInformationStore(ASN1Set var0, AlgorithmIdentifier var1, CMSSecureReadable var2) {
      ArrayList var3 = new ArrayList();

      for (int var4 = 0; var4 != var0.size(); var4++) {
         RecipientInfo var5 = RecipientInfo.getInstance(var0.getObjectAt(var4));
         readRecipientInfo(var3, var5, var1, var2);
      }

      return new RecipientInformationStore(var3);
   }

   private static void readRecipientInfo(List var0, RecipientInfo var1, AlgorithmIdentifier var2, CMSSecureReadable var3) {
      ASN1Encodable var4 = var1.getInfo();
      if (var4 instanceof KeyTransRecipientInfo) {
         var0.add(new KeyTransRecipientInformation((KeyTransRecipientInfo)var4, var2, var3));
      } else if (var4 instanceof OtherRecipientInfo) {
         OtherRecipientInfo var5 = OtherRecipientInfo.getInstance(var4);
         if (CMSObjectIdentifiers.id_ori_kem.equals(var5.getType())) {
            var0.add(new KEMRecipientInformation(KEMRecipientInfo.getInstance(var5.getValue()), var2, var3));
         }
      } else if (var4 instanceof KEKRecipientInfo) {
         var0.add(new KEKRecipientInformation((KEKRecipientInfo)var4, var2, var3));
      } else if (var4 instanceof KeyAgreeRecipientInfo) {
         KeyAgreeRecipientInformation.readRecipientInfo(var0, (KeyAgreeRecipientInfo)var4, var2, var3);
      } else if (var4 instanceof PasswordRecipientInfo) {
         var0.add(new PasswordRecipientInformation((PasswordRecipientInfo)var4, var2, var3));
      }
   }

   static class CMSAuthEnveSecureReadable extends CMSEnvelopedHelper.CMSDefaultSecureReadable {
      private AlgorithmIdentifier algorithm;

      CMSAuthEnveSecureReadable(AlgorithmIdentifier var1, ASN1ObjectIdentifier var2, CMSReadable var3) {
         super(var2, var3);
         this.algorithm = var1;
      }

      @Override
      public InputStream getInputStream() throws IOException, CMSException {
         return this.readable.getInputStream();
      }

      @Override
      public boolean hasAdditionalData() {
         return false;
      }
   }

   abstract static class CMSDefaultSecureReadable implements CMSSecureReadable {
      protected final ASN1ObjectIdentifier contentType;
      protected CMSReadable readable;
      protected ASN1Set authAttrSet;

      CMSDefaultSecureReadable(ASN1ObjectIdentifier var1, CMSReadable var2) {
         this.contentType = var1;
         this.readable = var2;
      }

      @Override
      public ASN1ObjectIdentifier getContentType() {
         return this.contentType;
      }

      @Override
      public ASN1Set getAuthAttrSet() {
         return this.authAttrSet;
      }

      @Override
      public void setAuthAttrSet(ASN1Set var1) {
         this.authAttrSet = var1;
      }
   }

   static class CMSDigestAuthenticatedSecureReadable extends CMSEnvelopedHelper.CMSDefaultSecureReadable {
      private DigestCalculator digestCalculator;

      public CMSDigestAuthenticatedSecureReadable(DigestCalculator var1, ASN1ObjectIdentifier var2, CMSReadable var3) {
         super(var2, var3);
         this.digestCalculator = var1;
      }

      @Override
      public InputStream getInputStream() throws IOException, CMSException {
         return new FilterInputStream(this.readable.getInputStream()) {
            @Override
            public int read() throws IOException {
               int var1 = this.in.read();
               if (var1 >= 0) {
                  CMSDigestAuthenticatedSecureReadable.this.digestCalculator.getOutputStream().write(var1);
               }

               return var1;
            }

            @Override
            public int read(byte[] var1, int var2, int var3) throws IOException {
               int var4 = this.in.read(var1, var2, var3);
               if (var4 >= 0) {
                  CMSDigestAuthenticatedSecureReadable.this.digestCalculator.getOutputStream().write(var1, var2, var4);
               }

               return var4;
            }
         };
      }

      public byte[] getDigest() {
         return this.digestCalculator.getDigest();
      }

      @Override
      public boolean hasAdditionalData() {
         return true;
      }
   }
}
