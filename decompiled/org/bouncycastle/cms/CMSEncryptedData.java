package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EncryptedData;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;

public class CMSEncryptedData {
   private ContentInfo contentInfo;
   private EncryptedData encryptedData;

   public CMSEncryptedData(ContentInfo var1) {
      this.contentInfo = var1;
      this.encryptedData = EncryptedData.getInstance(var1.getContent());
   }

   public byte[] getContent(InputDecryptorProvider var1) throws CMSException {
      try {
         return CMSUtils.streamToByteArray(this.getContentStream(var1).getContentStream());
      } catch (IOException var3) {
         throw new CMSException("unable to parse internal stream: " + var3.getMessage(), var3);
      }
   }

   public CMSTypedStream getContentStream(InputDecryptorProvider var1) throws CMSException {
      try {
         EncryptedContentInfo var2 = this.encryptedData.getEncryptedContentInfo();
         InputDecryptor var3 = var1.get(var2.getContentEncryptionAlgorithm());
         ByteArrayInputStream var4 = new ByteArrayInputStream(var2.getEncryptedContent().getOctets());
         return new CMSTypedStream(var2.getContentType(), var3.getInputStream(var4));
      } catch (Exception var5) {
         throw new CMSException("unable to create stream: " + var5.getMessage(), var5);
      }
   }

   public ContentInfo toASN1Structure() {
      return this.contentInfo;
   }
}
