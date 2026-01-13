package org.bouncycastle.asn1.eac;

import java.io.UnsupportedEncodingException;

public class CertificateHolderReference {
   private static final String ReferenceEncoding = "ISO-8859-1";
   private String countryCode;
   private String holderMnemonic;
   private String sequenceNumber;

   public CertificateHolderReference(String var1, String var2, String var3) {
      this.countryCode = var1;
      this.holderMnemonic = var2;
      this.sequenceNumber = var3;
   }

   CertificateHolderReference(byte[] var1) {
      try {
         String var2 = new String(var1, "ISO-8859-1");
         this.countryCode = var2.substring(0, 2);
         this.holderMnemonic = var2.substring(2, var2.length() - 5);
         this.sequenceNumber = var2.substring(var2.length() - 5);
      } catch (UnsupportedEncodingException var3) {
         throw new IllegalStateException(var3.toString());
      }
   }

   public String getCountryCode() {
      return this.countryCode;
   }

   public String getHolderMnemonic() {
      return this.holderMnemonic;
   }

   public String getSequenceNumber() {
      return this.sequenceNumber;
   }

   public byte[] getEncoded() {
      String var1 = this.countryCode + this.holderMnemonic + this.sequenceNumber;

      try {
         return var1.getBytes("ISO-8859-1");
      } catch (UnsupportedEncodingException var3) {
         throw new IllegalStateException(var3.toString());
      }
   }
}
