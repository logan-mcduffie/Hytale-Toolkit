package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.AuthenticatedDataParser;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.ContentInfoParser;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;

public class CMSAuthenticatedDataParser extends CMSContentInfoParser {
   RecipientInformationStore recipientInfoStore;
   AuthenticatedDataParser authData;
   private AlgorithmIdentifier macAlg;
   private byte[] mac;
   private AttributeTable authAttrs;
   private ASN1Set authAttrSet;
   private AttributeTable unauthAttrs;
   private boolean authAttrNotRead = true;
   private boolean unauthAttrNotRead;
   private OriginatorInformation originatorInfo;
   private CMSSecureReadable secureReadable;

   public CMSAuthenticatedDataParser(byte[] var1) throws CMSException, IOException {
      this(new ByteArrayInputStream(var1));
   }

   public CMSAuthenticatedDataParser(byte[] var1, DigestCalculatorProvider var2) throws CMSException, IOException {
      this(new ByteArrayInputStream(var1), var2);
   }

   public CMSAuthenticatedDataParser(InputStream var1) throws CMSException, IOException {
      this(var1, null);
   }

   public CMSAuthenticatedDataParser(InputStream var1, DigestCalculatorProvider var2) throws CMSException, IOException {
      super(var1);
      this.authData = new AuthenticatedDataParser((ASN1SequenceParser)this._contentInfo.getContent(16));
      OriginatorInfo var3 = this.authData.getOriginatorInfo();
      if (var3 != null) {
         this.originatorInfo = new OriginatorInformation(var3);
      }

      ASN1Set var4 = ASN1Set.getInstance(this.authData.getRecipientInfos().toASN1Primitive());
      this.macAlg = this.authData.getMacAlgorithm();
      AlgorithmIdentifier var5 = this.authData.getDigestAlgorithm();
      if (var5 != null) {
         if (var2 == null) {
            throw new CMSException("a digest calculator provider is required if authenticated attributes are present");
         }

         ContentInfoParser var6 = this.authData.getEncapsulatedContentInfo();
         CMSProcessableInputStream var7 = new CMSProcessableInputStream(((ASN1OctetStringParser)var6.getContent(4)).getOctetStream());

         try {
            this.secureReadable = new CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable(var2.get(var5), var6.getContentType(), var7);
            this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(var4, this.macAlg, this.secureReadable);
         } catch (OperatorCreationException var9) {
            throw new CMSException("unable to create digest calculator: " + var9.getMessage(), var9);
         }
      } else {
         ContentInfoParser var10 = this.authData.getEncapsulatedContentInfo();
         CMSProcessableInputStream var11 = new CMSProcessableInputStream(((ASN1OctetStringParser)var10.getContent(4)).getOctetStream());
         this.secureReadable = new CMSEnvelopedHelper.CMSAuthEnveSecureReadable(this.macAlg, var10.getContentType(), var11);
         this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(var4, this.macAlg, this.secureReadable);
      }
   }

   public OriginatorInformation getOriginatorInfo() {
      return this.originatorInfo;
   }

   public AlgorithmIdentifier getMacAlgorithm() {
      return this.macAlg;
   }

   public String getMacAlgOID() {
      return this.macAlg.getAlgorithm().toString();
   }

   public byte[] getMacAlgParams() {
      try {
         return CMSUtils.encodeObj(this.macAlg.getParameters());
      } catch (Exception var2) {
         throw new RuntimeException("exception getting encryption parameters " + var2);
      }
   }

   public RecipientInformationStore getRecipientInfos() {
      return this.recipientInfoStore;
   }

   public byte[] getMac() throws IOException {
      if (this.mac == null) {
         this.getAuthAttrs();
         this.mac = this.authData.getMac().getOctets();
      }

      return Arrays.clone(this.mac);
   }

   private ASN1Set getAuthAttrSet() throws IOException {
      if (this.authAttrs == null && this.authAttrNotRead) {
         ASN1SetParser var1 = this.authData.getAuthAttrs();
         if (var1 != null) {
            this.authAttrSet = (ASN1Set)var1.toASN1Primitive();
         }

         this.authAttrNotRead = false;
         this.secureReadable.setAuthAttrSet(this.authAttrSet);
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
         this.unauthAttrs = CMSUtils.getAttributesTable(this.authData.getUnauthAttrs());
      }

      return this.unauthAttrs;
   }

   public byte[] getContentDigest() {
      return this.authAttrs != null
         ? ASN1OctetString.getInstance(this.authAttrs.get(CMSAttributes.messageDigest).getAttrValues().getObjectAt(0)).getOctets()
         : null;
   }
}
