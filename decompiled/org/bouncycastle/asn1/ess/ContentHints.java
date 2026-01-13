package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;

public class ContentHints extends ASN1Object {
   private ASN1UTF8String contentDescription;
   private ASN1ObjectIdentifier contentType;

   public static ContentHints getInstance(Object var0) {
      if (var0 instanceof ContentHints) {
         return (ContentHints)var0;
      } else {
         return var0 != null ? new ContentHints(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private ContentHints(ASN1Sequence var1) {
      ASN1Encodable var2 = var1.getObjectAt(0);
      if (var2.toASN1Primitive() instanceof ASN1UTF8String) {
         this.contentDescription = ASN1UTF8String.getInstance(var2);
         this.contentType = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(1));
      } else {
         this.contentType = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
      }
   }

   public ContentHints(ASN1ObjectIdentifier var1) {
      this.contentType = var1;
      this.contentDescription = null;
   }

   public ContentHints(ASN1ObjectIdentifier var1, ASN1UTF8String var2) {
      this.contentType = var1;
      this.contentDescription = var2;
   }

   public ASN1ObjectIdentifier getContentType() {
      return this.contentType;
   }

   /** @deprecated */
   public DERUTF8String getContentDescription() {
      return null != this.contentDescription && !(this.contentDescription instanceof DERUTF8String)
         ? new DERUTF8String(this.contentDescription.getString())
         : (DERUTF8String)this.contentDescription;
   }

   public ASN1UTF8String getContentDescriptionUTF8() {
      return this.contentDescription;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.contentDescription != null) {
         var1.add(this.contentDescription);
      }

      var1.add(this.contentType);
      return new DERSequence(var1);
   }
}
