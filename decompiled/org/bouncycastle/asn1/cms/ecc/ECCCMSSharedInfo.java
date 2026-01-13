package org.bouncycastle.asn1.cms.ecc;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class ECCCMSSharedInfo extends ASN1Object {
   private final AlgorithmIdentifier keyInfo;
   private final byte[] entityUInfo;
   private final byte[] suppPubInfo;

   public ECCCMSSharedInfo(AlgorithmIdentifier var1, byte[] var2, byte[] var3) {
      this.keyInfo = var1;
      this.entityUInfo = Arrays.clone(var2);
      this.suppPubInfo = Arrays.clone(var3);
   }

   public ECCCMSSharedInfo(AlgorithmIdentifier var1, byte[] var2) {
      this.keyInfo = var1;
      this.entityUInfo = null;
      this.suppPubInfo = Arrays.clone(var2);
   }

   private ECCCMSSharedInfo(ASN1Sequence var1) {
      this.keyInfo = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
      if (var1.size() == 2) {
         this.entityUInfo = null;
         this.suppPubInfo = ASN1OctetString.getInstance((ASN1TaggedObject)var1.getObjectAt(1), true).getOctets();
      } else {
         this.entityUInfo = ASN1OctetString.getInstance((ASN1TaggedObject)var1.getObjectAt(1), true).getOctets();
         this.suppPubInfo = ASN1OctetString.getInstance((ASN1TaggedObject)var1.getObjectAt(2), true).getOctets();
      }
   }

   public static ECCCMSSharedInfo getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static ECCCMSSharedInfo getInstance(Object var0) {
      if (var0 instanceof ECCCMSSharedInfo) {
         return (ECCCMSSharedInfo)var0;
      } else {
         return var0 != null ? new ECCCMSSharedInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.keyInfo);
      if (this.entityUInfo != null) {
         var1.add(new DERTaggedObject(true, 0, new DEROctetString(this.entityUInfo)));
      }

      var1.add(new DERTaggedObject(true, 2, new DEROctetString(this.suppPubInfo)));
      return new DERSequence(var1);
   }
}
