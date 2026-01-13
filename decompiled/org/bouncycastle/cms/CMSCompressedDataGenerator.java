package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.CompressedData;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.OutputCompressor;

public class CMSCompressedDataGenerator {
   public static final String ZLIB = CMSObjectIdentifiers.zlibCompress.getId();

   public CMSCompressedData generate(CMSTypedData var1, OutputCompressor var2) throws CMSException {
      AlgorithmIdentifier var3;
      BEROctetString var4;
      try {
         ByteArrayOutputStream var5 = new ByteArrayOutputStream();
         OutputStream var6 = var2.getOutputStream(var5);
         var1.write(var6);
         var6.close();
         var3 = var2.getAlgorithmIdentifier();
         var4 = new BEROctetString(var5.toByteArray());
      } catch (IOException var7) {
         throw new CMSException("exception encoding data.", var7);
      }

      ContentInfo var8 = new ContentInfo(var1.getContentType(), var4);
      ContentInfo var9 = new ContentInfo(CMSObjectIdentifiers.compressedData, new CompressedData(var3, var8));
      return new CMSCompressedData(var9);
   }
}
