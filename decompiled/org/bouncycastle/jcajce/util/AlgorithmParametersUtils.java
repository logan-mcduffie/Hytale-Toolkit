package org.bouncycastle.jcajce.util;

import java.io.IOException;
import java.security.AlgorithmParameters;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;

public class AlgorithmParametersUtils {
   private AlgorithmParametersUtils() {
   }

   public static ASN1Encodable extractParameters(AlgorithmParameters var0) throws IOException {
      ASN1Primitive var1;
      try {
         var1 = ASN1Primitive.fromByteArray(var0.getEncoded("ASN.1"));
      } catch (Exception var3) {
         var1 = ASN1Primitive.fromByteArray(var0.getEncoded());
      }

      return var1;
   }

   public static void loadParameters(AlgorithmParameters var0, ASN1Encodable var1) throws IOException {
      try {
         var0.init(var1.toASN1Primitive().getEncoded(), "ASN.1");
      } catch (Exception var3) {
         var0.init(var1.toASN1Primitive().getEncoded());
      }
   }
}
