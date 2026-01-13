package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;

public class Ieee1609Dot2Content extends ASN1Object implements ASN1Choice {
   public static final int unsecuredData = 0;
   public static final int signedData = 1;
   public static final int encryptedData = 2;
   public static final int signedCertificateRequest = 3;
   private final int choice;
   private final ASN1Encodable ieee1609Dot2Content;

   public Ieee1609Dot2Content(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.ieee1609Dot2Content = var2;
   }

   public static Ieee1609Dot2Content unsecuredData(Opaque var0) {
      return new Ieee1609Dot2Content(0, var0);
   }

   public static Ieee1609Dot2Content unsecuredData(byte[] var0) {
      return new Ieee1609Dot2Content(0, new DEROctetString(Arrays.clone(var0)));
   }

   public static Ieee1609Dot2Content signedData(SignedData var0) {
      return new Ieee1609Dot2Content(1, var0);
   }

   public static Ieee1609Dot2Content encryptedData(EncryptedData var0) {
      return new Ieee1609Dot2Content(2, var0);
   }

   public static Ieee1609Dot2Content signedCertificateRequest(Opaque var0) {
      return new Ieee1609Dot2Content(3, var0);
   }

   public static Ieee1609Dot2Content signedCertificateRequest(byte[] var0) {
      return new Ieee1609Dot2Content(3, new DEROctetString(Arrays.clone(var0)));
   }

   private Ieee1609Dot2Content(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
         case 3:
            this.ieee1609Dot2Content = Opaque.getInstance(var1.getExplicitBaseObject());
            return;
         case 1:
            this.ieee1609Dot2Content = SignedData.getInstance(var1.getExplicitBaseObject());
            return;
         case 2:
            this.ieee1609Dot2Content = EncryptedData.getInstance(var1.getExplicitBaseObject());
            return;
         default:
            throw new IllegalArgumentException("invalid choice value " + var1.getTagNo());
      }
   }

   public static Ieee1609Dot2Content getInstance(Object var0) {
      if (var0 instanceof Ieee1609Dot2Content) {
         return (Ieee1609Dot2Content)var0;
      } else {
         return var0 != null ? new Ieee1609Dot2Content(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.ieee1609Dot2Content);
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getIeee1609Dot2Content() {
      return this.ieee1609Dot2Content;
   }
}
