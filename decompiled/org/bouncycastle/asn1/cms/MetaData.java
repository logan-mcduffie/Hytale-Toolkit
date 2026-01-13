package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;

public class MetaData extends ASN1Object {
   private ASN1Boolean hashProtected;
   private ASN1UTF8String fileName;
   private ASN1IA5String mediaType;
   private Attributes otherMetaData;

   public MetaData(ASN1Boolean var1, ASN1UTF8String var2, ASN1IA5String var3, Attributes var4) {
      this.hashProtected = var1;
      this.fileName = var2;
      this.mediaType = var3;
      this.otherMetaData = var4;
   }

   private MetaData(ASN1Sequence var1) {
      this.hashProtected = ASN1Boolean.getInstance(var1.getObjectAt(0));
      int var2 = 1;
      if (var2 < var1.size() && var1.getObjectAt(var2) instanceof ASN1UTF8String) {
         this.fileName = ASN1UTF8String.getInstance(var1.getObjectAt(var2++));
      }

      if (var2 < var1.size() && var1.getObjectAt(var2) instanceof ASN1IA5String) {
         this.mediaType = ASN1IA5String.getInstance(var1.getObjectAt(var2++));
      }

      if (var2 < var1.size()) {
         this.otherMetaData = Attributes.getInstance(var1.getObjectAt(var2++));
      }
   }

   public static MetaData getInstance(Object var0) {
      if (var0 instanceof MetaData) {
         return (MetaData)var0;
      } else {
         return var0 != null ? new MetaData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.hashProtected);
      if (this.fileName != null) {
         var1.add(this.fileName);
      }

      if (this.mediaType != null) {
         var1.add(this.mediaType);
      }

      if (this.otherMetaData != null) {
         var1.add(this.otherMetaData);
      }

      return new DERSequence(var1);
   }

   public boolean isHashProtected() {
      return this.hashProtected.isTrue();
   }

   /** @deprecated */
   public DERUTF8String getFileName() {
      return null != this.fileName && !(this.fileName instanceof DERUTF8String) ? new DERUTF8String(this.fileName.getString()) : (DERUTF8String)this.fileName;
   }

   public ASN1UTF8String getFileNameUTF8() {
      return this.fileName;
   }

   /** @deprecated */
   public DERIA5String getMediaType() {
      return null != this.mediaType && !(this.mediaType instanceof DERIA5String)
         ? new DERIA5String(this.mediaType.getString(), false)
         : (DERIA5String)this.mediaType;
   }

   public ASN1IA5String getMediaTypeIA5() {
      return this.mediaType;
   }

   public Attributes getOtherMetaData() {
      return this.otherMetaData;
   }
}
