package org.bouncycastle.oer.its.etsi102941.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;

public class PublicKeys extends ASN1Object {
   private final PublicVerificationKey verificationKey;
   private final PublicEncryptionKey encryptionKey;

   public PublicKeys(PublicVerificationKey var1, PublicEncryptionKey var2) {
      this.verificationKey = var1;
      this.encryptionKey = var2;
   }

   public static PublicKeys getInstance(Object var0) {
      if (var0 instanceof PublicKeys) {
         return (PublicKeys)var0;
      } else {
         return var0 != null ? new PublicKeys(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private PublicKeys(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.verificationKey = PublicVerificationKey.getInstance(var1.getObjectAt(0));
         this.encryptionKey = OEROptional.getValue(PublicEncryptionKey.class, var1.getObjectAt(1));
      }
   }

   public PublicVerificationKey getVerificationKey() {
      return this.verificationKey;
   }

   public PublicEncryptionKey getEncryptionKey() {
      return this.encryptionKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.verificationKey, OEROptional.getInstance(this.encryptionKey)});
   }
}
