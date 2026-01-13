package org.bouncycastle.asn1.cryptopro;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.util.Arrays;

public class GostR3410TransportParameters extends ASN1Object {
   private final ASN1ObjectIdentifier encryptionParamSet;
   private final SubjectPublicKeyInfo ephemeralPublicKey;
   private final byte[] ukm;

   public GostR3410TransportParameters(ASN1ObjectIdentifier var1, SubjectPublicKeyInfo var2, byte[] var3) {
      this.encryptionParamSet = var1;
      this.ephemeralPublicKey = var2;
      this.ukm = Arrays.clone(var3);
   }

   private GostR3410TransportParameters(ASN1Sequence var1) {
      if (var1.size() == 2) {
         this.encryptionParamSet = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
         this.ukm = ASN1OctetString.getInstance(var1.getObjectAt(1)).getOctets();
         this.ephemeralPublicKey = null;
      } else {
         if (var1.size() != 3) {
            throw new IllegalArgumentException("unknown sequence length: " + var1.size());
         }

         this.encryptionParamSet = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
         this.ephemeralPublicKey = SubjectPublicKeyInfo.getInstance(ASN1TaggedObject.getInstance(var1.getObjectAt(1)), false);
         this.ukm = ASN1OctetString.getInstance(var1.getObjectAt(2)).getOctets();
      }
   }

   public static GostR3410TransportParameters getInstance(Object var0) {
      if (var0 instanceof GostR3410TransportParameters) {
         return (GostR3410TransportParameters)var0;
      } else {
         return var0 != null ? new GostR3410TransportParameters(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static GostR3410TransportParameters getInstance(ASN1TaggedObject var0, boolean var1) {
      return new GostR3410TransportParameters(ASN1Sequence.getInstance(var0, var1));
   }

   public ASN1ObjectIdentifier getEncryptionParamSet() {
      return this.encryptionParamSet;
   }

   public SubjectPublicKeyInfo getEphemeralPublicKey() {
      return this.ephemeralPublicKey;
   }

   public byte[] getUkm() {
      return Arrays.clone(this.ukm);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.encryptionParamSet);
      if (this.ephemeralPublicKey != null) {
         var1.add(new DERTaggedObject(false, 0, this.ephemeralPublicKey));
      }

      var1.add(new DEROctetString(this.ukm));
      return new DERSequence(var1);
   }
}
