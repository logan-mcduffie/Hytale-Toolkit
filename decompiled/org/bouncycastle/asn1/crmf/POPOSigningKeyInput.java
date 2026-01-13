package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class POPOSigningKeyInput extends ASN1Object {
   private GeneralName sender;
   private PKMACValue publicKeyMAC;
   private SubjectPublicKeyInfo publicKey;

   private POPOSigningKeyInput(ASN1Sequence var1) {
      ASN1Encodable var2 = var1.getObjectAt(0);
      if (var2 instanceof ASN1TaggedObject) {
         ASN1TaggedObject var3 = (ASN1TaggedObject)var2;
         this.sender = GeneralName.getInstance(ASN1Util.getExplicitContextBaseObject(var3, 0));
      } else {
         this.publicKeyMAC = PKMACValue.getInstance(var2);
      }

      this.publicKey = SubjectPublicKeyInfo.getInstance(var1.getObjectAt(1));
   }

   public static POPOSigningKeyInput getInstance(Object var0) {
      if (var0 instanceof POPOSigningKeyInput) {
         return (POPOSigningKeyInput)var0;
      } else {
         return var0 != null ? new POPOSigningKeyInput(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public POPOSigningKeyInput(GeneralName var1, SubjectPublicKeyInfo var2) {
      this.sender = var1;
      this.publicKey = var2;
   }

   public POPOSigningKeyInput(PKMACValue var1, SubjectPublicKeyInfo var2) {
      this.publicKeyMAC = var1;
      this.publicKey = var2;
   }

   public GeneralName getSender() {
      return this.sender;
   }

   public PKMACValue getPublicKeyMAC() {
      return this.publicKeyMAC;
   }

   public SubjectPublicKeyInfo getPublicKey() {
      return this.publicKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.sender != null) {
         var1.add(new DERTaggedObject(false, 0, this.sender));
      } else {
         var1.add(this.publicKeyMAC);
      }

      var1.add(this.publicKey);
      return new DERSequence(var1);
   }
}
