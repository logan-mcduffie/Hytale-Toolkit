package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DLSequence;

public class AuthenticatedSafe extends ASN1Object {
   private ContentInfo[] info;
   private boolean isBer = true;

   private AuthenticatedSafe(ASN1Sequence var1) {
      this.info = new ContentInfo[var1.size()];

      for (int var2 = 0; var2 != this.info.length; var2++) {
         this.info[var2] = ContentInfo.getInstance(var1.getObjectAt(var2));
      }

      this.isBer = var1 instanceof BERSequence;
   }

   public static AuthenticatedSafe getInstance(Object var0) {
      if (var0 instanceof AuthenticatedSafe) {
         return (AuthenticatedSafe)var0;
      } else {
         return var0 != null ? new AuthenticatedSafe(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AuthenticatedSafe(ContentInfo[] var1) {
      this.info = this.copy(var1);
   }

   public ContentInfo[] getContentInfo() {
      return this.copy(this.info);
   }

   private ContentInfo[] copy(ContentInfo[] var1) {
      ContentInfo[] var2 = new ContentInfo[var1.length];
      System.arraycopy(var1, 0, var2, 0, var2.length);
      return var2;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return (ASN1Primitive)(this.isBer ? new BERSequence(this.info) : new DLSequence(this.info));
   }
}
