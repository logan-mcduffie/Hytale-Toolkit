package org.bouncycastle.asn1;

public class DERUniversalString extends ASN1UniversalString {
   public DERUniversalString(byte[] var1) {
      this(var1, true);
   }

   DERUniversalString(byte[] var1, boolean var2) {
      super(var1, var2);
   }
}
