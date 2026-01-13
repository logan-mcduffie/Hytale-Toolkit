package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.DigestedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;

public class CMSDigestedData implements Encodable {
   private ContentInfo contentInfo;
   private DigestedData digestedData;

   public CMSDigestedData(byte[] var1) throws CMSException {
      this(CMSUtils.readContentInfo(var1));
   }

   public CMSDigestedData(InputStream var1) throws CMSException {
      this(CMSUtils.readContentInfo(var1));
   }

   public CMSDigestedData(ContentInfo var1) throws CMSException {
      this.contentInfo = var1;

      try {
         this.digestedData = DigestedData.getInstance(var1.getContent());
      } catch (ClassCastException var3) {
         throw new CMSException("Malformed content.", var3);
      } catch (IllegalArgumentException var4) {
         throw new CMSException("Malformed content.", var4);
      }
   }

   public ASN1ObjectIdentifier getContentType() {
      return this.contentInfo.getContentType();
   }

   public AlgorithmIdentifier getDigestAlgorithm() {
      return this.digestedData.getDigestAlgorithm();
   }

   public CMSProcessable getDigestedContent() throws CMSException {
      ContentInfo var1 = this.digestedData.getEncapContentInfo();

      try {
         return new CMSProcessableByteArray(var1.getContentType(), ((ASN1OctetString)var1.getContent()).getOctets());
      } catch (Exception var3) {
         throw new CMSException("exception reading digested stream.", var3);
      }
   }

   public ContentInfo toASN1Structure() {
      return this.contentInfo;
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return this.contentInfo.getEncoded();
   }

   public boolean verify(DigestCalculatorProvider var1) throws CMSException {
      try {
         ContentInfo var2 = this.digestedData.getEncapContentInfo();
         DigestCalculator var3 = var1.get(this.digestedData.getDigestAlgorithm());
         OutputStream var4 = var3.getOutputStream();
         var4.write(((ASN1OctetString)var2.getContent()).getOctets());
         return Arrays.areEqual(this.digestedData.getDigest(), var3.getDigest());
      } catch (OperatorCreationException var5) {
         throw new CMSException("unable to create digest calculator: " + var5.getMessage(), var5);
      } catch (IOException var6) {
         throw new CMSException("unable process content: " + var6.getMessage(), var6);
      }
   }
}
