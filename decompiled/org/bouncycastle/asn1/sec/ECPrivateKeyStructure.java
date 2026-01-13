package org.bouncycastle.asn1.sec;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.BigIntegers;

/** @deprecated */
public class ECPrivateKeyStructure extends ASN1Object {
   private ASN1Sequence seq;

   public ECPrivateKeyStructure(ASN1Sequence var1) {
      this.seq = var1;
   }

   public ECPrivateKeyStructure(BigInteger var1) {
      byte[] var2 = BigIntegers.asUnsignedByteArray(var1);
      this.seq = new DERSequence(new ASN1Integer(1L), new DEROctetString(var2));
   }

   public ECPrivateKeyStructure(BigInteger var1, ASN1Encodable var2) {
      this(var1, null, var2);
   }

   public ECPrivateKeyStructure(BigInteger var1, ASN1BitString var2, ASN1Encodable var3) {
      byte[] var4 = BigIntegers.asUnsignedByteArray(var1);
      ASN1EncodableVector var5 = new ASN1EncodableVector(4);
      var5.add(new ASN1Integer(1L));
      var5.add(new DEROctetString(var4));
      if (var3 != null) {
         var5.add(new DERTaggedObject(true, 0, var3));
      }

      if (var2 != null) {
         var5.add(new DERTaggedObject(true, 1, var2));
      }

      this.seq = new DERSequence(var5);
   }

   public BigInteger getKey() {
      ASN1OctetString var1 = (ASN1OctetString)this.seq.getObjectAt(1);
      return new BigInteger(1, var1.getOctets());
   }

   public ASN1BitString getPublicKey() {
      return (ASN1BitString)this.getObjectInTag(1);
   }

   public ASN1Primitive getParameters() {
      return this.getObjectInTag(0);
   }

   private ASN1Primitive getObjectInTag(int var1) {
      Enumeration var2 = this.seq.getObjects();

      while (var2.hasMoreElements()) {
         ASN1Encodable var3 = (ASN1Encodable)var2.nextElement();
         if (var3 instanceof ASN1TaggedObject) {
            ASN1TaggedObject var4 = (ASN1TaggedObject)var3;
            if (var4.getTagNo() == var1) {
               return var4.getExplicitBaseObject().toASN1Primitive();
            }
         }
      }

      return null;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.seq;
   }
}
