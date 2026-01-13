package org.bouncycastle.asn1.x500;

import org.bouncycastle.asn1.ASN1BMPString;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1T61String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.ASN1UniversalString;
import org.bouncycastle.asn1.DERUTF8String;

public class DirectoryString extends ASN1Object implements ASN1Choice, ASN1String {
   private ASN1String string;

   public static DirectoryString getInstance(Object var0) {
      if (var0 == null || var0 instanceof DirectoryString) {
         return (DirectoryString)var0;
      } else if (var0 instanceof ASN1T61String) {
         return new DirectoryString((ASN1T61String)var0);
      } else if (var0 instanceof ASN1PrintableString) {
         return new DirectoryString((ASN1PrintableString)var0);
      } else if (var0 instanceof ASN1UniversalString) {
         return new DirectoryString((ASN1UniversalString)var0);
      } else if (var0 instanceof ASN1UTF8String) {
         return new DirectoryString((ASN1UTF8String)var0);
      } else if (var0 instanceof ASN1BMPString) {
         return new DirectoryString((ASN1BMPString)var0);
      } else {
         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      }
   }

   public static DirectoryString getInstance(ASN1TaggedObject var0, boolean var1) {
      if (!var1) {
         throw new IllegalArgumentException("choice item must be explicitly tagged");
      } else {
         return getInstance(var0.getExplicitBaseObject());
      }
   }

   private DirectoryString(ASN1T61String var1) {
      this.string = var1;
   }

   private DirectoryString(ASN1PrintableString var1) {
      this.string = var1;
   }

   private DirectoryString(ASN1UniversalString var1) {
      this.string = var1;
   }

   private DirectoryString(ASN1UTF8String var1) {
      this.string = var1;
   }

   private DirectoryString(ASN1BMPString var1) {
      this.string = var1;
   }

   public DirectoryString(String var1) {
      this.string = new DERUTF8String(var1);
   }

   @Override
   public String getString() {
      return this.string.getString();
   }

   @Override
   public String toString() {
      return this.string.getString();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ((ASN1Encodable)this.string).toASN1Primitive();
   }
}
