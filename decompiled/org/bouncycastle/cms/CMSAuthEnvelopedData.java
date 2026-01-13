package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.AuthEnvelopedData;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;

public class CMSAuthEnvelopedData implements Encodable {
   RecipientInformationStore recipientInfoStore;
   ContentInfo contentInfo;
   private OriginatorInformation originatorInfo;
   private AlgorithmIdentifier authEncAlg;
   private ASN1Set authAttrs;
   private byte[] mac;
   private ASN1Set unauthAttrs;

   public CMSAuthEnvelopedData(byte[] var1) throws CMSException {
      this(CMSUtils.readContentInfo(var1));
   }

   public CMSAuthEnvelopedData(InputStream var1) throws CMSException {
      this(CMSUtils.readContentInfo(var1));
   }

   public CMSAuthEnvelopedData(ContentInfo var1) throws CMSException {
      this.contentInfo = var1;
      AuthEnvelopedData var2 = AuthEnvelopedData.getInstance(var1.getContent());
      if (var2.getOriginatorInfo() != null) {
         this.originatorInfo = new OriginatorInformation(var2.getOriginatorInfo());
      }

      ASN1Set var3 = var2.getRecipientInfos();
      final EncryptedContentInfo var4 = var2.getAuthEncryptedContentInfo();
      this.authEncAlg = var4.getContentEncryptionAlgorithm();
      this.mac = var2.getMac().getOctets();
      CMSSecureReadableWithAAD var5 = new CMSSecureReadableWithAAD() {
         private OutputStream aadStream;

         @Override
         public ASN1Set getAuthAttrSet() {
            return CMSAuthEnvelopedData.this.authAttrs;
         }

         @Override
         public void setAuthAttrSet(ASN1Set var1) {
         }

         @Override
         public boolean hasAdditionalData() {
            return this.aadStream != null && CMSAuthEnvelopedData.this.authAttrs != null;
         }

         @Override
         public ASN1ObjectIdentifier getContentType() {
            return var4.getContentType();
         }

         @Override
         public InputStream getInputStream() throws IOException {
            if (this.aadStream != null && CMSAuthEnvelopedData.this.authAttrs != null) {
               this.aadStream.write(CMSAuthEnvelopedData.this.authAttrs.getEncoded("DER"));
            }

            return new InputStreamWithMAC(new ByteArrayInputStream(var4.getEncryptedContent().getOctets()), CMSAuthEnvelopedData.this.mac);
         }

         @Override
         public void setAADStream(OutputStream var1) {
            this.aadStream = var1;
         }

         @Override
         public OutputStream getAADStream() {
            return this.aadStream;
         }

         @Override
         public byte[] getMAC() {
            return Arrays.clone(CMSAuthEnvelopedData.this.mac);
         }
      };
      this.authAttrs = var2.getAuthAttrs();
      this.unauthAttrs = var2.getUnauthAttrs();
      this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(var3, this.authEncAlg, var5);
   }

   public String getEncryptionAlgOID() {
      return this.authEncAlg.getAlgorithm().getId();
   }

   public OriginatorInformation getOriginatorInfo() {
      return this.originatorInfo;
   }

   public RecipientInformationStore getRecipientInfos() {
      return this.recipientInfoStore;
   }

   public AttributeTable getAuthAttrs() {
      return this.authAttrs == null ? null : new AttributeTable(this.authAttrs);
   }

   public AttributeTable getUnauthAttrs() {
      return this.unauthAttrs == null ? null : new AttributeTable(this.unauthAttrs);
   }

   public byte[] getMac() {
      return Arrays.clone(this.mac);
   }

   public ContentInfo toASN1Structure() {
      return this.contentInfo;
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return this.contentInfo.getEncoded();
   }
}
