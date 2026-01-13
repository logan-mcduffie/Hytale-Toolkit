package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class SecretKeyData extends ASN1Object {
   private final ASN1ObjectIdentifier keyAlgorithm;
   private final ASN1OctetString keyBytes;

   public SecretKeyData(ASN1ObjectIdentifier var1, byte[] var2) {
      this.keyAlgorithm = var1;
      this.keyBytes = new DEROctetString(Arrays.clone(var2));
   }

   private SecretKeyData(ASN1Sequence var1) {
      this.keyAlgorithm = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
      this.keyBytes = ASN1OctetString.getInstance(var1.getObjectAt(1));
   }

   public static SecretKeyData getInstance(Object var0) {
      if (var0 instanceof SecretKeyData) {
         return (SecretKeyData)var0;
      } else {
         return var0 != null ? new SecretKeyData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public byte[] getKeyBytes() {
      return Arrays.clone(this.keyBytes.getOctets());
   }

   public ASN1ObjectIdentifier getKeyAlgorithm() {
      return this.keyAlgorithm;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.keyAlgorithm, this.keyBytes);
   }
}
