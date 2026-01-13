package org.bouncycastle.jcajce.spec;

import java.security.spec.EncodedKeySpec;

public class RawEncodedKeySpec extends EncodedKeySpec {
   public RawEncodedKeySpec(byte[] var1) {
      super(var1);
   }

   @Override
   public String getFormat() {
      return "RAW";
   }
}
