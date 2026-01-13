package org.bouncycastle.asn1.cryptopro;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;

public class GOST3410PublicKeyAlgParameters extends ASN1Object {
   private ASN1ObjectIdentifier publicKeyParamSet;
   private ASN1ObjectIdentifier digestParamSet;
   private ASN1ObjectIdentifier encryptionParamSet;

   public static GOST3410PublicKeyAlgParameters getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static GOST3410PublicKeyAlgParameters getInstance(Object var0) {
      if (var0 instanceof GOST3410PublicKeyAlgParameters) {
         return (GOST3410PublicKeyAlgParameters)var0;
      } else {
         return var0 != null ? new GOST3410PublicKeyAlgParameters(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public GOST3410PublicKeyAlgParameters(ASN1ObjectIdentifier var1, ASN1ObjectIdentifier var2) {
      this.publicKeyParamSet = var1;
      this.digestParamSet = var2;
      this.encryptionParamSet = null;
   }

   public GOST3410PublicKeyAlgParameters(ASN1ObjectIdentifier var1, ASN1ObjectIdentifier var2, ASN1ObjectIdentifier var3) {
      this.publicKeyParamSet = var1;
      this.digestParamSet = var2;
      this.encryptionParamSet = var3;
   }

   private GOST3410PublicKeyAlgParameters(ASN1Sequence var1) {
      this.publicKeyParamSet = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
      if (this.publicKeyParamSet.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetA)) {
         if (var1.size() > 1) {
            this.digestParamSet = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(1));
         }
      } else if (!this.publicKeyParamSet.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetB)
         && !this.publicKeyParamSet.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetC)
         && !this.publicKeyParamSet.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetD)) {
         if (var1.size() > 1) {
            this.digestParamSet = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(1));
         }
      } else if (var1.size() > 1) {
         throw new IllegalArgumentException("digestParamSet expected to be absent");
      }

      if (var1.size() > 2) {
         this.encryptionParamSet = (ASN1ObjectIdentifier)var1.getObjectAt(2);
      }
   }

   public ASN1ObjectIdentifier getPublicKeyParamSet() {
      return this.publicKeyParamSet;
   }

   public ASN1ObjectIdentifier getDigestParamSet() {
      return this.digestParamSet;
   }

   public ASN1ObjectIdentifier getEncryptionParamSet() {
      return this.encryptionParamSet;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.publicKeyParamSet);
      if (this.digestParamSet != null) {
         var1.add(this.digestParamSet);
      }

      if (this.encryptionParamSet != null) {
         var1.add(this.encryptionParamSet);
      }

      return new DERSequence(var1);
   }
}
