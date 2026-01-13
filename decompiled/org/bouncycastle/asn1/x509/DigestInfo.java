package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class DigestInfo extends ASN1Object {
   private byte[] digest;
   private AlgorithmIdentifier algId;

   public static DigestInfo getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static DigestInfo getInstance(Object var0) {
      if (var0 instanceof DigestInfo) {
         return (DigestInfo)var0;
      } else {
         return var0 != null ? new DigestInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public DigestInfo(AlgorithmIdentifier var1, byte[] var2) {
      this.digest = Arrays.clone(var2);
      this.algId = var1;
   }

   public DigestInfo(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.algId = AlgorithmIdentifier.getInstance(var2.nextElement());
      this.digest = ASN1OctetString.getInstance(var2.nextElement()).getOctets();
   }

   public AlgorithmIdentifier getAlgorithmId() {
      return this.algId;
   }

   public byte[] getDigest() {
      return Arrays.clone(this.digest);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.algId, new DEROctetString(this.digest));
   }
}
