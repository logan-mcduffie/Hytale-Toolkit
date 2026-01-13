package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;

public class ContentIdentifier extends ASN1Object {
   ASN1OctetString value;

   public static ContentIdentifier getInstance(Object var0) {
      if (var0 instanceof ContentIdentifier) {
         return (ContentIdentifier)var0;
      } else {
         return var0 != null ? new ContentIdentifier(ASN1OctetString.getInstance(var0)) : null;
      }
   }

   private ContentIdentifier(ASN1OctetString var1) {
      this.value = var1;
   }

   public ContentIdentifier(byte[] var1) {
      this(new DEROctetString(var1));
   }

   public ASN1OctetString getValue() {
      return this.value;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.value;
   }
}
