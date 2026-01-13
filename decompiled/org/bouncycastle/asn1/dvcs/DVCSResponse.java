package org.bouncycastle.asn1.dvcs;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class DVCSResponse extends ASN1Object implements ASN1Choice {
   private DVCSCertInfo dvCertInfo;
   private DVCSErrorNotice dvErrorNote;

   public DVCSResponse(DVCSCertInfo var1) {
      this.dvCertInfo = var1;
   }

   public DVCSResponse(DVCSErrorNotice var1) {
      this.dvErrorNote = var1;
   }

   public static DVCSResponse getInstance(Object var0) {
      if (var0 == null || var0 instanceof DVCSResponse) {
         return (DVCSResponse)var0;
      } else if (var0 instanceof byte[]) {
         try {
            return getInstance(ASN1Primitive.fromByteArray((byte[])var0));
         } catch (IOException var3) {
            throw new IllegalArgumentException("failed to construct sequence from byte[]: " + var3.getMessage());
         }
      } else if (var0 instanceof ASN1Sequence) {
         DVCSCertInfo var4 = DVCSCertInfo.getInstance(var0);
         return new DVCSResponse(var4);
      } else if (var0 instanceof ASN1TaggedObject) {
         ASN1TaggedObject var1 = ASN1TaggedObject.getInstance(var0);
         DVCSErrorNotice var2 = DVCSErrorNotice.getInstance(var1, false);
         return new DVCSResponse(var2);
      } else {
         throw new IllegalArgumentException("Couldn't convert from object to DVCSResponse: " + var0.getClass().getName());
      }
   }

   public static DVCSResponse getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public DVCSCertInfo getCertInfo() {
      return this.dvCertInfo;
   }

   public DVCSErrorNotice getErrorNotice() {
      return this.dvErrorNote;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return (ASN1Primitive)(this.dvCertInfo != null ? this.dvCertInfo.toASN1Primitive() : new DERTaggedObject(false, 0, this.dvErrorNote));
   }

   @Override
   public String toString() {
      return this.dvCertInfo != null
         ? "DVCSResponse {\ndvCertInfo: " + this.dvCertInfo.toString() + "}\n"
         : "DVCSResponse {\ndvErrorNote: " + this.dvErrorNote.toString() + "}\n";
   }
}
