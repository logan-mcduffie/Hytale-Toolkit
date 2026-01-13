package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DLSequence;

public class EncryptionInfo extends ASN1Object {
   private ASN1ObjectIdentifier encryptionInfoType;
   private ASN1Encodable encryptionInfoValue;

   /** @deprecated */
   public static EncryptionInfo getInstance(ASN1Object var0) {
      return getInstance((Object)var0);
   }

   public static EncryptionInfo getInstance(Object var0) {
      if (var0 instanceof EncryptionInfo) {
         return (EncryptionInfo)var0;
      } else {
         return var0 != null ? new EncryptionInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static EncryptionInfo getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   private EncryptionInfo(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("wrong sequence size in constructor: " + var1.size());
      } else {
         this.encryptionInfoType = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
         this.encryptionInfoValue = var1.getObjectAt(1);
      }
   }

   public EncryptionInfo(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.encryptionInfoType = var1;
      this.encryptionInfoValue = var2;
   }

   public ASN1ObjectIdentifier getEncryptionInfoType() {
      return this.encryptionInfoType;
   }

   public ASN1Encodable getEncryptionInfoValue() {
      return this.encryptionInfoValue;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DLSequence(this.encryptionInfoType, this.encryptionInfoValue);
   }
}
