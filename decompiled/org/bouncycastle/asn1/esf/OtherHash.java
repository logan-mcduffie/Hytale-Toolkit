package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class OtherHash extends ASN1Object implements ASN1Choice {
   private ASN1OctetString sha1Hash;
   private OtherHashAlgAndValue otherHash;

   public static OtherHash getInstance(Object var0) {
      if (var0 instanceof OtherHash) {
         return (OtherHash)var0;
      } else {
         return var0 instanceof ASN1OctetString ? new OtherHash((ASN1OctetString)var0) : new OtherHash(OtherHashAlgAndValue.getInstance(var0));
      }
   }

   private OtherHash(ASN1OctetString var1) {
      this.sha1Hash = var1;
   }

   public OtherHash(OtherHashAlgAndValue var1) {
      this.otherHash = var1;
   }

   public OtherHash(byte[] var1) {
      this.sha1Hash = new DEROctetString(var1);
   }

   public AlgorithmIdentifier getHashAlgorithm() {
      return null == this.otherHash ? new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1) : this.otherHash.getHashAlgorithm();
   }

   public byte[] getHashValue() {
      return null == this.otherHash ? this.sha1Hash.getOctets() : this.otherHash.getHashValue().getOctets();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return (ASN1Primitive)(null == this.otherHash ? this.sha1Hash : this.otherHash.toASN1Primitive());
   }
}
