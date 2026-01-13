package org.bouncycastle.oer;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.ASN1Encodable;

public class OEREncoder {
   public static byte[] toByteArray(ASN1Encodable var0, Element var1) {
      try {
         ByteArrayOutputStream var2 = new ByteArrayOutputStream();
         new OEROutputStream(var2).write(var0, var1);
         var2.flush();
         var2.close();
         return var2.toByteArray();
      } catch (Exception var3) {
         throw new IllegalStateException(var3.getMessage(), var3);
      }
   }
}
