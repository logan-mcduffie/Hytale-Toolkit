package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Extension;

public class ExtensionReq extends ASN1Object {
   private final Extension[] extensions;

   public static ExtensionReq getInstance(Object var0) {
      if (var0 instanceof ExtensionReq) {
         return (ExtensionReq)var0;
      } else {
         return var0 != null ? new ExtensionReq(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static ExtensionReq getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public ExtensionReq(Extension var1) {
      this.extensions = new Extension[]{var1};
   }

   public ExtensionReq(Extension[] var1) {
      this.extensions = Utils.clone(var1);
   }

   private ExtensionReq(ASN1Sequence var1) {
      this.extensions = new Extension[var1.size()];

      for (int var2 = 0; var2 != var1.size(); var2++) {
         this.extensions[var2] = Extension.getInstance(var1.getObjectAt(var2));
      }
   }

   public Extension[] getExtensions() {
      return Utils.clone(this.extensions);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.extensions);
   }
}
