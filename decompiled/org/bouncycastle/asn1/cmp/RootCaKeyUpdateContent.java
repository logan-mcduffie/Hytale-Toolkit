package org.bouncycastle.asn1.cmp;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class RootCaKeyUpdateContent extends ASN1Object {
   private final CMPCertificate newWithNew;
   private final CMPCertificate newWithOld;
   private final CMPCertificate oldWithNew;

   public RootCaKeyUpdateContent(CMPCertificate var1, CMPCertificate var2, CMPCertificate var3) {
      if (var1 == null) {
         throw new NullPointerException("'newWithNew' cannot be null");
      } else {
         this.newWithNew = var1;
         this.newWithOld = var2;
         this.oldWithNew = var3;
      }
   }

   private RootCaKeyUpdateContent(ASN1Sequence var1) {
      if (var1.size() >= 1 && var1.size() <= 3) {
         CMPCertificate var3 = null;
         CMPCertificate var4 = null;
         Iterator var5 = var1.iterator();
         CMPCertificate var2 = CMPCertificate.getInstance(var5.next());

         while (var5.hasNext()) {
            ASN1TaggedObject var6 = ASN1TaggedObject.getInstance(var5.next());
            if (var6.hasContextTag(0)) {
               var3 = CMPCertificate.getInstance(var6, true);
            } else if (var6.hasContextTag(1)) {
               var4 = CMPCertificate.getInstance(var6, true);
            }
         }

         this.newWithNew = var2;
         this.newWithOld = var3;
         this.oldWithNew = var4;
      } else {
         throw new IllegalArgumentException("expected sequence of 1 to 3 elements only");
      }
   }

   public static RootCaKeyUpdateContent getInstance(Object var0) {
      if (var0 instanceof RootCaKeyUpdateContent) {
         return (RootCaKeyUpdateContent)var0;
      } else {
         return var0 != null ? new RootCaKeyUpdateContent(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CMPCertificate getNewWithNew() {
      return this.newWithNew;
   }

   public CMPCertificate getNewWithOld() {
      return this.newWithOld;
   }

   public CMPCertificate getOldWithNew() {
      return this.oldWithNew;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.newWithNew);
      if (this.newWithOld != null) {
         var1.add(new DERTaggedObject(true, 0, this.newWithOld));
      }

      if (this.oldWithNew != null) {
         var1.add(new DERTaggedObject(true, 1, this.oldWithNew));
      }

      return new DERSequence(var1);
   }
}
