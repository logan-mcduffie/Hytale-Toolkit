package org.bouncycastle.oer.its.etsi102941.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataEncrypted;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSignedExternalPayload;

public class EcSignature extends ASN1Object implements ASN1Choice {
   public static final int encryptedEcSignature = 0;
   public static final int ecSignature = 1;
   private final int choice;
   private final ASN1Encodable _ecSignature;

   public EcSignature(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this._ecSignature = var2;
   }

   private EcSignature(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
            this._ecSignature = EtsiTs103097DataEncrypted.getInstance(var1.getExplicitBaseObject());
            return;
         case 1:
            this._ecSignature = EtsiTs103097DataSignedExternalPayload.getInstance(var1.getExplicitBaseObject());
            return;
         default:
            throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static EcSignature getInstance(Object var0) {
      if (var0 instanceof EcSignature) {
         return (EcSignature)var0;
      } else {
         return var0 != null ? new EcSignature(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getEcSignature() {
      return this._ecSignature;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this._ecSignature);
   }
}
