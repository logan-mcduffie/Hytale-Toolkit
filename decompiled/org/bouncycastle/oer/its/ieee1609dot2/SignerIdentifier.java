package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class SignerIdentifier extends ASN1Object implements ASN1Choice {
   public static final int digest = 0;
   public static final int certificate = 1;
   public static final int self = 2;
   private final int choice;
   private final ASN1Encodable signerIdentifier;

   public SignerIdentifier(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.signerIdentifier = var2;
   }

   private SignerIdentifier(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
            this.signerIdentifier = HashedId8.getInstance(var1.getExplicitBaseObject());
            break;
         case 1:
            this.signerIdentifier = SequenceOfCertificate.getInstance(var1.getExplicitBaseObject());
            break;
         case 2:
            this.signerIdentifier = DERNull.getInstance(var1.getExplicitBaseObject());
            break;
         default:
            throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static SignerIdentifier getInstance(Object var0) {
      if (var0 instanceof SignerIdentifier) {
         return (SignerIdentifier)var0;
      } else {
         return var0 != null ? new SignerIdentifier(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public int getChoice() {
      return this.choice;
   }

   public static SignerIdentifier digest(HashedId8 var0) {
      return new SignerIdentifier(0, var0);
   }

   public static SignerIdentifier certificate(SequenceOfCertificate var0) {
      return new SignerIdentifier(1, var0);
   }

   public static SignerIdentifier self() {
      return new SignerIdentifier(2, DERNull.INSTANCE);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.signerIdentifier);
   }

   public ASN1Encodable getSignerIdentifier() {
      return this.signerIdentifier;
   }
}
