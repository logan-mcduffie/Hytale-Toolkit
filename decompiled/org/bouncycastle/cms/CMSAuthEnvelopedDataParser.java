package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.AuthEnvelopedDataParser;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.EncryptedContentInfoParser;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class CMSAuthEnvelopedDataParser extends CMSContentInfoParser {
   private final RecipientInformationStore recipientInfoStore;
   private final AuthEnvelopedDataParser authEvnData;
   private final CMSAuthEnvelopedDataParser.LocalMacProvider localMacProvider;
   private final AlgorithmIdentifier encAlg;
   private AttributeTable authAttrs;
   private ASN1Set authAttrSet;
   private AttributeTable unauthAttrs;
   private boolean authAttrNotRead = true;
   private boolean unauthAttrNotRead = true;
   private OriginatorInformation originatorInfo;

   public CMSAuthEnvelopedDataParser(byte[] var1) throws CMSException, IOException {
      this(new ByteArrayInputStream(var1));
   }

   public CMSAuthEnvelopedDataParser(InputStream var1) throws CMSException, IOException {
      super(var1);
      this.authEvnData = new AuthEnvelopedDataParser((ASN1SequenceParser)this._contentInfo.getContent(16));
      OriginatorInfo var2 = this.authEvnData.getOriginatorInfo();
      if (var2 != null) {
         this.originatorInfo = new OriginatorInformation(var2);
      }

      ASN1Set var3 = ASN1Set.getInstance(this.authEvnData.getRecipientInfos().toASN1Primitive());
      final EncryptedContentInfoParser var4 = this.authEvnData.getAuthEncryptedContentInfo();
      this.encAlg = var4.getContentEncryptionAlgorithm();
      this.localMacProvider = new CMSAuthEnvelopedDataParser.LocalMacProvider(this.authEvnData, this);
      final CMSProcessableInputStream var5 = new CMSProcessableInputStream(
         new InputStreamWithMAC(((ASN1OctetStringParser)var4.getEncryptedContent(4)).getOctetStream(), this.localMacProvider)
      );
      CMSSecureReadableWithAAD var6 = new CMSSecureReadableWithAAD() {
         private OutputStream aadStream;

         @Override
         public ASN1ObjectIdentifier getContentType() {
            return var4.getContentType();
         }

         @Override
         public InputStream getInputStream() throws IOException, CMSException {
            return var5.getInputStream();
         }

         @Override
         public ASN1Set getAuthAttrSet() {
            return CMSAuthEnvelopedDataParser.this.authAttrSet;
         }

         @Override
         public void setAuthAttrSet(ASN1Set var1) {
         }

         @Override
         public boolean hasAdditionalData() {
            return true;
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
            return Arrays.clone(CMSAuthEnvelopedDataParser.this.localMacProvider.getMAC());
         }
      };
      this.localMacProvider.setSecureReadable(var6);
      this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(var3, this.encAlg, var6);
   }

   public OriginatorInformation getOriginatorInfo() {
      return this.originatorInfo;
   }

   public AlgorithmIdentifier getEncryptionAlgOID() {
      return this.encAlg;
   }

   public String getEncAlgOID() {
      return this.encAlg.getAlgorithm().toString();
   }

   public byte[] getEncAlgParams() {
      try {
         return CMSUtils.encodeObj(this.encAlg.getParameters());
      } catch (Exception var2) {
         throw new RuntimeException("exception getting encryption parameters " + var2);
      }
   }

   public RecipientInformationStore getRecipientInfos() {
      return this.recipientInfoStore;
   }

   public byte[] getMac() throws IOException {
      return Arrays.clone(this.localMacProvider.getMAC());
   }

   private ASN1Set getAuthAttrSet() throws IOException {
      if (this.authAttrs == null && this.authAttrNotRead) {
         ASN1SetParser var1 = this.authEvnData.getAuthAttrs();
         if (var1 != null) {
            this.authAttrSet = (ASN1Set)var1.toASN1Primitive();
         }

         this.authAttrNotRead = false;
      }

      return this.authAttrSet;
   }

   public AttributeTable getAuthAttrs() throws IOException {
      if (this.authAttrs == null && this.authAttrNotRead) {
         ASN1Set var1 = this.getAuthAttrSet();
         if (var1 != null) {
            this.authAttrs = new AttributeTable(var1);
         }
      }

      return this.authAttrs;
   }

   public AttributeTable getUnauthAttrs() throws IOException {
      if (this.unauthAttrs == null && this.unauthAttrNotRead) {
         this.unauthAttrNotRead = false;
         this.unauthAttrs = CMSUtils.getAttributesTable(this.authEvnData.getUnauthAttrs());
      }

      return this.unauthAttrs;
   }

   public byte[] getContentDigest() {
      return this.authAttrs != null
         ? ASN1OctetString.getInstance(this.authAttrs.get(CMSAttributes.messageDigest).getAttrValues().getObjectAt(0)).getOctets()
         : null;
   }

   static class LocalMacProvider implements MACProvider {
      private byte[] mac;
      private final AuthEnvelopedDataParser authEnvData;
      private final CMSAuthEnvelopedDataParser parser;
      private CMSSecureReadableWithAAD readable;

      LocalMacProvider(AuthEnvelopedDataParser var1, CMSAuthEnvelopedDataParser var2) {
         this.authEnvData = var1;
         this.parser = var2;
      }

      @Override
      public void init() throws IOException {
         this.parser.authAttrs = this.parser.getAuthAttrs();
         if (this.parser.authAttrs != null) {
            this.readable.setAuthAttrSet(this.parser.authAttrSet);
            this.readable.getAADStream().write(this.parser.authAttrs.toASN1Structure().getEncoded("DER"));
         }

         this.mac = this.authEnvData.getMac().getOctets();
      }

      void setSecureReadable(CMSSecureReadableWithAAD var1) {
         this.readable = var1;
      }

      @Override
      public byte[] getMAC() {
         return this.mac;
      }
   }
}
