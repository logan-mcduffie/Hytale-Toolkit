package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;

public class PKIFreeText extends ASN1Object {
   ASN1Sequence strings;

   private PKIFreeText(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();

      while (var2.hasMoreElements()) {
         if (!(var2.nextElement() instanceof ASN1UTF8String)) {
            throw new IllegalArgumentException("attempt to insert non UTF8 STRING into PKIFreeText");
         }
      }

      this.strings = var1;
   }

   public PKIFreeText(ASN1UTF8String var1) {
      this.strings = new DERSequence(var1);
   }

   public PKIFreeText(String var1) {
      this(new DERUTF8String(var1));
   }

   public PKIFreeText(ASN1UTF8String[] var1) {
      this.strings = new DERSequence(var1);
   }

   public PKIFreeText(String[] var1) {
      ASN1EncodableVector var2 = new ASN1EncodableVector(var1.length);

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2.add(new DERUTF8String(var1[var3]));
      }

      this.strings = new DERSequence(var2);
   }

   public static PKIFreeText getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static PKIFreeText getInstance(Object var0) {
      if (var0 instanceof PKIFreeText) {
         return (PKIFreeText)var0;
      } else {
         return var0 != null ? new PKIFreeText(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public int size() {
      return this.strings.size();
   }

   /** @deprecated */
   public DERUTF8String getStringAt(int var1) {
      ASN1UTF8String var2 = this.getStringAtUTF8(var1);
      return null != var2 && !(var2 instanceof DERUTF8String) ? new DERUTF8String(var2.getString()) : (DERUTF8String)var2;
   }

   public ASN1UTF8String getStringAtUTF8(int var1) {
      return (ASN1UTF8String)this.strings.getObjectAt(var1);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.strings;
   }
}
