package org.bouncycastle.asn1.x509;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x500.style.BCStyle;

public class X509DefaultEntryConverter extends X509NameEntryConverter {
   @Override
   public ASN1Primitive getConvertedValue(ASN1ObjectIdentifier var1, String var2) {
      if (var2.length() != 0 && var2.charAt(0) == '#') {
         try {
            return this.convertHexEncoded(var2, 1);
         } catch (IOException var4) {
            throw new RuntimeException("can't recode value for oid " + var1.getId());
         }
      } else {
         if (var2.length() != 0 && var2.charAt(0) == '\\') {
            var2 = var2.substring(1);
         }

         if (var1.equals(BCStyle.EmailAddress) || var1.equals(BCStyle.DC)) {
            return new DERIA5String(var2);
         } else if (var1.equals(BCStyle.DATE_OF_BIRTH)) {
            return new DERGeneralizedTime(var2);
         } else {
            return (ASN1Primitive)(!var1.equals(BCStyle.C)
                  && !var1.equals(BCStyle.SERIALNUMBER)
                  && !var1.equals(BCStyle.DN_QUALIFIER)
                  && !var1.equals(BCStyle.TELEPHONE_NUMBER)
               ? new DERUTF8String(var2)
               : new DERPrintableString(var2));
         }
      }
   }
}
