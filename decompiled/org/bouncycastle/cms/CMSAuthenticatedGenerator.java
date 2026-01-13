package org.bouncycastle.cms;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class CMSAuthenticatedGenerator extends CMSEnvelopedGenerator {
   protected CMSAttributeTableGenerator authGen;
   protected CMSAttributeTableGenerator unauthGen;

   public void setAuthenticatedAttributeGenerator(CMSAttributeTableGenerator var1) {
      this.authGen = var1;
   }

   public void setUnauthenticatedAttributeGenerator(CMSAttributeTableGenerator var1) {
      this.unauthGen = var1;
   }

   protected Map getBaseParameters(ASN1ObjectIdentifier var1, AlgorithmIdentifier var2, AlgorithmIdentifier var3, byte[] var4) {
      HashMap var5 = new HashMap();
      var5.put("contentType", var1);
      var5.put("digestAlgID", var2);
      var5.put("digest", Arrays.clone(var4));
      var5.put("macAlgID", var3);
      return var5;
   }
}
