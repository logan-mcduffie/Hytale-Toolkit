package org.bouncycastle.tsp.cms;

import java.net.URI;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.cms.MetaData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;

public class CMSTimeStampedGenerator {
   protected MetaData metaData;
   protected URI dataUri;

   public void setDataUri(URI var1) {
      this.dataUri = var1;
   }

   public void setMetaData(boolean var1, String var2, String var3) {
      this.setMetaData(var1, var2, var3, null);
   }

   public void setMetaData(boolean var1, String var2, String var3, Attributes var4) {
      DERUTF8String var5 = null;
      if (var2 != null) {
         var5 = new DERUTF8String(var2);
      }

      DERIA5String var6 = null;
      if (var3 != null) {
         var6 = new DERIA5String(var3);
      }

      this.setMetaData(var1, var5, var6, var4);
   }

   private void setMetaData(boolean var1, ASN1UTF8String var2, ASN1IA5String var3, Attributes var4) {
      this.metaData = new MetaData(ASN1Boolean.getInstance(var1), var2, var3, var4);
   }

   public void initialiseMessageImprintDigestCalculator(DigestCalculator var1) throws CMSException {
      MetaDataUtil var2 = new MetaDataUtil(this.metaData);
      var2.initialiseMessageImprintDigestCalculator(var1);
   }
}
