package org.bouncycastle.tsp.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.cms.MetaData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;

class MetaDataUtil {
   private final MetaData metaData;

   MetaDataUtil(MetaData var1) {
      this.metaData = var1;
   }

   void initialiseMessageImprintDigestCalculator(DigestCalculator var1) throws CMSException {
      if (this.metaData != null && this.metaData.isHashProtected()) {
         try {
            var1.getOutputStream().write(this.metaData.getEncoded("DER"));
         } catch (IOException var3) {
            throw new CMSException("unable to initialise calculator from metaData: " + var3.getMessage(), var3);
         }
      }
   }

   String getFileName() {
      return this.metaData != null ? this.convertString(this.metaData.getFileNameUTF8()) : null;
   }

   String getMediaType() {
      return this.metaData != null ? this.convertString(this.metaData.getMediaTypeIA5()) : null;
   }

   Attributes getOtherMetaData() {
      return this.metaData != null ? this.metaData.getOtherMetaData() : null;
   }

   private String convertString(ASN1String var1) {
      return var1 != null ? var1.toString() : null;
   }
}
