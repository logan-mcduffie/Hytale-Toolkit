package org.bouncycastle.asn1.cms.ecc;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;

public class MQVuserKeyingMaterial extends ASN1Object {
   private OriginatorPublicKey ephemeralPublicKey;
   private ASN1OctetString addedukm;

   public MQVuserKeyingMaterial(OriginatorPublicKey var1, ASN1OctetString var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("Ephemeral public key cannot be null");
      } else {
         this.ephemeralPublicKey = var1;
         this.addedukm = var2;
      }
   }

   private MQVuserKeyingMaterial(ASN1Sequence var1) {
      if (var1.size() != 1 && var1.size() != 2) {
         throw new IllegalArgumentException("Sequence has incorrect number of elements");
      } else {
         this.ephemeralPublicKey = OriginatorPublicKey.getInstance(var1.getObjectAt(0));
         if (var1.size() > 1) {
            this.addedukm = ASN1OctetString.getInstance((ASN1TaggedObject)var1.getObjectAt(1), true);
         }
      }
   }

   public static MQVuserKeyingMaterial getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static MQVuserKeyingMaterial getInstance(Object var0) {
      if (var0 instanceof MQVuserKeyingMaterial) {
         return (MQVuserKeyingMaterial)var0;
      } else {
         return var0 != null ? new MQVuserKeyingMaterial(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public OriginatorPublicKey getEphemeralPublicKey() {
      return this.ephemeralPublicKey;
   }

   public ASN1OctetString getAddedukm() {
      return this.addedukm;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.ephemeralPublicKey);
      if (this.addedukm != null) {
         var1.add(new DERTaggedObject(true, 0, this.addedukm));
      }

      return new DERSequence(var1);
   }
}
