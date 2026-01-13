package org.bouncycastle.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

class DerUtil {
   static ASN1OctetString getOctetString(byte[] var0) {
      return var0 == null ? new DEROctetString(new byte[0]) : new DEROctetString(Arrays.clone(var0));
   }

   static byte[] toByteArray(ASN1Primitive var0) {
      try {
         return var0.getEncoded();
      } catch (final IOException var2) {
         throw new IllegalStateException("Cannot get encoding: " + var2.getMessage()) {
            @Override
            public Throwable getCause() {
               return var2;
            }
         };
      }
   }
}
