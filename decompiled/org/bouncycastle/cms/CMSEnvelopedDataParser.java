package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.EncryptedContentInfoParser;
import org.bouncycastle.asn1.cms.EnvelopedDataParser;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CMSEnvelopedDataParser extends CMSContentInfoParser {
   RecipientInformationStore recipientInfoStore;
   EnvelopedDataParser envelopedData;
   private AlgorithmIdentifier encAlg;
   private AttributeTable unprotectedAttributes;
   private boolean attrNotRead = true;
   private OriginatorInformation originatorInfo;

   public CMSEnvelopedDataParser(byte[] var1) throws CMSException, IOException {
      this(new ByteArrayInputStream(var1));
   }

   public CMSEnvelopedDataParser(InputStream var1) throws CMSException, IOException {
      super(var1);
      this.envelopedData = new EnvelopedDataParser((ASN1SequenceParser)this._contentInfo.getContent(16));
      OriginatorInfo var2 = this.envelopedData.getOriginatorInfo();
      if (var2 != null) {
         this.originatorInfo = new OriginatorInformation(var2);
      }

      ASN1Set var3 = ASN1Set.getInstance(this.envelopedData.getRecipientInfos().toASN1Primitive());
      EncryptedContentInfoParser var4 = this.envelopedData.getEncryptedContentInfo();
      this.encAlg = var4.getContentEncryptionAlgorithm();
      CMSProcessableInputStream var5 = new CMSProcessableInputStream(((ASN1OctetStringParser)var4.getEncryptedContent(4)).getOctetStream());
      CMSEnvelopedHelper.CMSAuthEnveSecureReadable var6 = new CMSEnvelopedHelper.CMSAuthEnveSecureReadable(this.encAlg, var4.getContentType(), var5);
      this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(var3, this.encAlg, var6);
   }

   public String getEncryptionAlgOID() {
      return this.encAlg.getAlgorithm().toString();
   }

   public byte[] getEncryptionAlgParams() {
      try {
         return CMSUtils.encodeObj(this.encAlg.getParameters());
      } catch (Exception var2) {
         throw new RuntimeException("exception getting encryption parameters " + var2);
      }
   }

   public AlgorithmIdentifier getContentEncryptionAlgorithm() {
      return this.encAlg;
   }

   public OriginatorInformation getOriginatorInfo() {
      return this.originatorInfo;
   }

   public RecipientInformationStore getRecipientInfos() {
      return this.recipientInfoStore;
   }

   public AttributeTable getUnprotectedAttributes() throws IOException {
      if (this.unprotectedAttributes == null && this.attrNotRead) {
         this.attrNotRead = false;
         this.unprotectedAttributes = CMSUtils.getAttributesTable(this.envelopedData.getUnprotectedAttrs());
      }

      return this.unprotectedAttributes;
   }
}
