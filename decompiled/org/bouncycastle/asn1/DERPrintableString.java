package org.bouncycastle.asn1;

public class DERPrintableString extends ASN1PrintableString {
   public DERPrintableString(String var1) {
      this(var1, false);
   }

   public DERPrintableString(String var1, boolean var2) {
      super(var1, var2);
   }

   DERPrintableString(byte[] var1, boolean var2) {
      super(var1, var2);
   }
}
