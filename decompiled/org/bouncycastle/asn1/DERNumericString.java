package org.bouncycastle.asn1;

public class DERNumericString extends ASN1NumericString {
   public DERNumericString(String var1) {
      this(var1, false);
   }

   public DERNumericString(String var1, boolean var2) {
      super(var1, var2);
   }

   DERNumericString(byte[] var1, boolean var2) {
      super(var1, var2);
   }
}
