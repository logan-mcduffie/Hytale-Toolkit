package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BMPString;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.ASN1VisibleString;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.DERVisibleString;

public class DisplayText extends ASN1Object implements ASN1Choice {
   public static final int CONTENT_TYPE_IA5STRING = 0;
   public static final int CONTENT_TYPE_BMPSTRING = 1;
   public static final int CONTENT_TYPE_UTF8STRING = 2;
   public static final int CONTENT_TYPE_VISIBLESTRING = 3;
   public static final int DISPLAY_TEXT_MAXIMUM_SIZE = 200;
   int contentType;
   ASN1String contents;

   public DisplayText(int var1, String var2) {
      if (var2.length() > 200) {
         var2 = var2.substring(0, 200);
      }

      this.contentType = var1;
      switch (var1) {
         case 0:
            this.contents = new DERIA5String(var2);
            break;
         case 1:
            this.contents = new DERBMPString(var2);
            break;
         case 2:
            this.contents = new DERUTF8String(var2);
            break;
         case 3:
            this.contents = new DERVisibleString(var2);
            break;
         default:
            this.contents = new DERUTF8String(var2);
      }
   }

   public DisplayText(String var1) {
      if (var1.length() > 200) {
         var1 = var1.substring(0, 200);
      }

      this.contentType = 2;
      this.contents = new DERUTF8String(var1);
   }

   private DisplayText(ASN1String var1) {
      this.contents = var1;
      if (var1 instanceof ASN1UTF8String) {
         this.contentType = 2;
      } else if (var1 instanceof ASN1BMPString) {
         this.contentType = 1;
      } else if (var1 instanceof ASN1IA5String) {
         this.contentType = 0;
      } else {
         if (!(var1 instanceof ASN1VisibleString)) {
            throw new IllegalArgumentException("unknown STRING type in DisplayText");
         }

         this.contentType = 3;
      }
   }

   public static DisplayText getInstance(Object var0) {
      if (var0 instanceof ASN1String) {
         return new DisplayText((ASN1String)var0);
      } else if (var0 != null && !(var0 instanceof DisplayText)) {
         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      } else {
         return (DisplayText)var0;
      }
   }

   public static DisplayText getInstance(ASN1TaggedObject var0, boolean var1) {
      if (!var1) {
         throw new IllegalArgumentException("choice item must be explicitly tagged");
      } else {
         return getInstance(var0.getExplicitBaseObject());
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return (ASN1Primitive)this.contents;
   }

   public String getString() {
      return this.contents.getString();
   }
}
