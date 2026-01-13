package org.bouncycastle.asn1.cryptopro;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class GostR3410KeyTransport extends ASN1Object {
   private final Gost2814789EncryptedKey sessionEncryptedKey;
   private final GostR3410TransportParameters transportParameters;

   private GostR3410KeyTransport(ASN1Sequence var1) {
      this.sessionEncryptedKey = Gost2814789EncryptedKey.getInstance(var1.getObjectAt(0));
      this.transportParameters = GostR3410TransportParameters.getInstance(ASN1TaggedObject.getInstance(var1.getObjectAt(1)), false);
   }

   public GostR3410KeyTransport(Gost2814789EncryptedKey var1, GostR3410TransportParameters var2) {
      this.sessionEncryptedKey = var1;
      this.transportParameters = var2;
   }

   public static GostR3410KeyTransport getInstance(Object var0) {
      if (var0 instanceof GostR3410KeyTransport) {
         return (GostR3410KeyTransport)var0;
      } else {
         return var0 != null ? new GostR3410KeyTransport(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public Gost2814789EncryptedKey getSessionEncryptedKey() {
      return this.sessionEncryptedKey;
   }

   public GostR3410TransportParameters getTransportParameters() {
      return this.transportParameters;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.sessionEncryptedKey);
      if (this.transportParameters != null) {
         var1.add(new DERTaggedObject(false, 0, this.transportParameters));
      }

      return new DERSequence(var1);
   }
}
