package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

public class EncryptedData extends ASN1Object {
   private final SequenceOfRecipientInfo recipients;
   private final SymmetricCiphertext ciphertext;

   public EncryptedData(SequenceOfRecipientInfo var1, SymmetricCiphertext var2) {
      this.recipients = var1;
      this.ciphertext = var2;
   }

   private EncryptedData(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.recipients = SequenceOfRecipientInfo.getInstance(var1.getObjectAt(0));
         this.ciphertext = SymmetricCiphertext.getInstance(var1.getObjectAt(1));
      }
   }

   public static EncryptedData getInstance(Object var0) {
      if (var0 instanceof EncryptedData) {
         return (EncryptedData)var0;
      } else {
         return var0 != null ? new EncryptedData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.recipients, this.ciphertext);
   }

   public SequenceOfRecipientInfo getRecipients() {
      return this.recipients;
   }

   public SymmetricCiphertext getCiphertext() {
      return this.ciphertext;
   }

   public static EncryptedData.Builder builder() {
      return new EncryptedData.Builder();
   }

   public static class Builder {
      private SequenceOfRecipientInfo recipients;
      private SymmetricCiphertext ciphertext;

      public EncryptedData.Builder setRecipients(SequenceOfRecipientInfo var1) {
         this.recipients = var1;
         return this;
      }

      public EncryptedData.Builder setCiphertext(SymmetricCiphertext var1) {
         this.ciphertext = var1;
         return this;
      }

      public EncryptedData createEncryptedData() {
         return new EncryptedData(this.recipients, this.ciphertext);
      }
   }
}
