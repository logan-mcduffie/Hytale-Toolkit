package org.bouncycastle.asn1;

import java.util.Date;

public class DERUTCTime extends ASN1UTCTime {
   DERUTCTime(byte[] var1) {
      super(var1);
   }

   public DERUTCTime(Date var1) {
      super(var1);
   }

   public DERUTCTime(String var1) {
      super(var1);
   }
}
