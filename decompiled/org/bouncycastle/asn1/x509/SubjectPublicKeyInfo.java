package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;

public class SubjectPublicKeyInfo extends ASN1Object {
   private AlgorithmIdentifier algId;
   private ASN1BitString keyData;

   public static SubjectPublicKeyInfo getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static SubjectPublicKeyInfo getInstance(Object var0) {
      if (var0 instanceof SubjectPublicKeyInfo) {
         return (SubjectPublicKeyInfo)var0;
      } else {
         return var0 != null ? new SubjectPublicKeyInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public SubjectPublicKeyInfo(AlgorithmIdentifier var1, ASN1BitString var2) {
      this.keyData = var2;
      this.algId = var1;
   }

   public SubjectPublicKeyInfo(AlgorithmIdentifier var1, ASN1Encodable var2) throws IOException {
      this.keyData = new DERBitString(var2);
      this.algId = var1;
   }

   public SubjectPublicKeyInfo(AlgorithmIdentifier var1, byte[] var2) {
      this.keyData = new DERBitString(var2);
      this.algId = var1;
   }

   @Deprecated
   public SubjectPublicKeyInfo(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         Enumeration var2 = var1.getObjects();
         this.algId = AlgorithmIdentifier.getInstance(var2.nextElement());
         this.keyData = ASN1BitString.getInstance(var2.nextElement());
      }
   }

   public AlgorithmIdentifier getAlgorithm() {
      return this.algId;
   }

   @Deprecated
   public AlgorithmIdentifier getAlgorithmId() {
      return this.algId;
   }

   public ASN1Primitive parsePublicKey() throws IOException {
      return ASN1Primitive.fromByteArray(this.keyData.getOctets());
   }

   @Deprecated
   public ASN1Primitive getPublicKey() throws IOException {
      return ASN1Primitive.fromByteArray(this.keyData.getOctets());
   }

   public ASN1BitString getPublicKeyData() {
      return this.keyData;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.algId, this.keyData);
   }
}
