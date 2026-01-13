package org.bouncycastle.asn1.isismtt.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.DirectoryString;

public class AdditionalInformationSyntax extends ASN1Object {
   private DirectoryString information;

   public static AdditionalInformationSyntax getInstance(Object var0) {
      if (var0 instanceof AdditionalInformationSyntax) {
         return (AdditionalInformationSyntax)var0;
      } else {
         return var0 != null ? new AdditionalInformationSyntax(DirectoryString.getInstance(var0)) : null;
      }
   }

   private AdditionalInformationSyntax(DirectoryString var1) {
      this.information = var1;
   }

   public AdditionalInformationSyntax(String var1) {
      this(new DirectoryString(var1));
   }

   public DirectoryString getInformation() {
      return this.information;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.information.toASN1Primitive();
   }
}
