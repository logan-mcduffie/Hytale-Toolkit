package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EncryptedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.OutputEncryptor;

public class CMSEncryptedDataGenerator extends CMSEncryptedGenerator {
   private CMSEncryptedData doGenerate(CMSTypedData var1, OutputEncryptor var2) throws CMSException {
      ByteArrayOutputStream var5 = new ByteArrayOutputStream();

      try {
         OutputStream var6 = var2.getOutputStream(var5);
         var1.write(var6);
         var6.close();
      } catch (IOException var10) {
         throw new CMSException("");
      }

      byte[] var11 = var5.toByteArray();
      AlgorithmIdentifier var3 = var2.getAlgorithmIdentifier();
      new BEROctetString(var11);
      EncryptedContentInfo var7 = CMSUtils.getEncryptedContentInfo(var1.getContentType(), var3, var11);
      ASN1Set var8 = CMSUtils.getAttrBERSet(this.unprotectedAttributeGenerator);
      ContentInfo var9 = new ContentInfo(CMSObjectIdentifiers.encryptedData, new EncryptedData(var7, var8));
      return new CMSEncryptedData(var9);
   }

   public CMSEncryptedData generate(CMSTypedData var1, OutputEncryptor var2) throws CMSException {
      return this.doGenerate(var1, var2);
   }
}
