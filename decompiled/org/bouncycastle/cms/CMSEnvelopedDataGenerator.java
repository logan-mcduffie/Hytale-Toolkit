package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.operator.OutputAEADEncryptor;
import org.bouncycastle.operator.OutputEncryptor;

public class CMSEnvelopedDataGenerator extends CMSEnvelopedGenerator {
   private CMSEnvelopedData doGenerate(CMSTypedData var1, OutputEncryptor var2) throws CMSException {
      ASN1EncodableVector var3 = CMSUtils.getRecipentInfos(var2.getKey(), this.recipientInfoGenerators);
      ByteArrayOutputStream var4 = new ByteArrayOutputStream();

      try {
         OutputStream var5 = var2.getOutputStream(var4);
         var1.write(var5);
         var5.close();
         if (var2 instanceof OutputAEADEncryptor) {
            byte[] var6 = ((OutputAEADEncryptor)var2).getMAC();
            var4.write(var6, 0, var6.length);
         }
      } catch (IOException var9) {
         throw new CMSException("");
      }

      byte[] var10 = var4.toByteArray();
      EncryptedContentInfo var11 = CMSUtils.getEncryptedContentInfo(var1, var2, var10);
      ASN1Set var7 = CMSUtils.getAttrBERSet(this.unprotectedAttributeGenerator);
      ContentInfo var8 = new ContentInfo(CMSObjectIdentifiers.envelopedData, new EnvelopedData(this.originatorInfo, new DERSet(var3), var11, var7));
      return new CMSEnvelopedData(var8);
   }

   public CMSEnvelopedData generate(CMSTypedData var1, OutputEncryptor var2) throws CMSException {
      return this.doGenerate(var1, var2);
   }
}
