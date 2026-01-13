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

public class ECPrivateKey extends ASN1Object {
   private ASN1Sequence seq;

   private ECPrivateKey(ASN1Sequence var1) {
      this.seq = var1;
   }

   public static ECPrivateKey getInstance(Object var0) {
      if (var0 instanceof ECPrivateKey) {
         return (ECPrivateKey)var0;
      } else {
         return var0 != null ? new ECPrivateKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   /** @deprecated */
   public ECPrivateKey(BigInteger var1) {
      this(var1.bitLength(), var1);
   }

   public ECPrivateKey(int var1, BigInteger var2) {
      byte[] var3 = BigIntegers.asUnsignedByteArray((var1 + 7) / 8, var2);
      this.seq = new DERSequence(new ASN1Integer(1L), new DEROctetString(var3));
   }

   /** @deprecated */
   public ECPrivateKey(BigInteger var1, ASN1Encodable var2) {
      this(var1, null, var2);
   }

   /** @deprecated */
   public ECPrivateKey(BigInteger var1, ASN1BitString var2, ASN1Encodable var3) {
      this(var1.bitLength(), var1, var2, var3);
   }

   public ECPrivateKey(int var1, BigInteger var2, ASN1Encodable var3) {
      this(var1, var2, null, var3);
   }

   public ECPrivateKey(int var1, BigInteger var2, ASN1BitString var3, ASN1Encodable var4) {
      byte[] var5 = BigIntegers.asUnsignedByteArray((var1 + 7) / 8, var2);
      ASN1EncodableVector var6 = new ASN1EncodableVector(4);
      var6.add(new ASN1Integer(1L));
      var6.add(new DEROctetString(var5));
      if (var4 != null) {
         var6.add(new DERTaggedObject(true, 0, var4));
      }

      if (var3 != null) {
         var6.add(new DERTaggedObject(true, 1, var3));
      }

      this.seq = new DERSequence(var6);
   }

   public BigInteger getKey() {
      ASN1OctetString var1 = (ASN1OctetString)this.seq.getObjectAt(1);
      return new BigInteger(1, var1.getOctets());
   }

   public ASN1BitString getPublicKey() {
      return (ASN1BitString)this.getObjectInTag(1, 3);
   }

   /** @deprecated */
   public ASN1Primitive getParameters() {
      return this.getParametersObject().toASN1Primitive();
   }

   public ASN1Object getParametersObject() {
      return this.getObjectInTag(0, -1);
   }

   private ASN1Object getObjectInTag(int var1, int var2) {
      Enumeration var3 = this.seq.getObjects();

      while (var3.hasMoreElements()) {
         ASN1Encodable var4 = (ASN1Encodable)var3.nextElement();
         if (var4 instanceof ASN1TaggedObject) {
            ASN1TaggedObject var5 = (ASN1TaggedObject)var4;
            if (var5.hasContextTag(var1)) {
               return var2 < 0 ? var5.getExplicitBaseObject().toASN1Primitive() : var5.getBaseUniversal(true, var2);
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
