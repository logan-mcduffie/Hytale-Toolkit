package org.bouncycastle.cert.cmp;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.POPODecKeyRespContent;

public class POPODecryptionKeyResponseContent {
   private final POPODecKeyRespContent respContent;

   POPODecryptionKeyResponseContent(POPODecKeyRespContent var1) {
      this.respContent = var1;
   }

   public byte[][] getResponses() {
      ASN1Integer[] var1 = this.respContent.toASN1IntegerArray();
      byte[][] var2 = new byte[var1.length][];

      for (int var3 = 0; var3 != var1.length; var3++) {
         var2[var3] = var1[var3].getValue().toByteArray();
      }

      return var2;
   }

   public static POPODecryptionKeyResponseContent fromPKIBody(PKIBody var0) {
      if (var0.getType() != 6) {
         throw new IllegalArgumentException("content of PKIBody wrong type: " + var0.getType());
      } else {
         return new POPODecryptionKeyResponseContent(POPODecKeyRespContent.getInstance(var0.getContent()));
      }
   }

   public POPODecKeyRespContent toASN1Structure() {
      return this.respContent;
   }
}
