package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.util.Arrays;

public class AesCcmCiphertext extends ASN1Object {
   private final ASN1OctetString nonce;
   private final Opaque ccmCiphertext;

   public AesCcmCiphertext(ASN1OctetString var1, Opaque var2) {
      this.nonce = var1;
      this.ccmCiphertext = var2;
   }

   private AesCcmCiphertext(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         Iterator var2 = var1.iterator();
         this.nonce = ASN1OctetString.getInstance(var2.next());
         this.ccmCiphertext = Opaque.getInstance(var2.next());
      }
   }

   public static AesCcmCiphertext getInstance(Object var0) {
      if (var0 instanceof AesCcmCiphertext) {
         return (AesCcmCiphertext)var0;
      } else {
         return var0 != null ? new AesCcmCiphertext(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1OctetString getNonce() {
      return this.nonce;
   }

   public Opaque getCcmCiphertext() {
      return this.ccmCiphertext;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.nonce, this.ccmCiphertext);
   }

   public static AesCcmCiphertext.Builder builder() {
      return new AesCcmCiphertext.Builder();
   }

   public static class Builder {
      private ASN1OctetString nonce;
      private Opaque opaque;

      public AesCcmCiphertext.Builder setNonce(ASN1OctetString var1) {
         this.nonce = var1;
         return this;
      }

      public AesCcmCiphertext.Builder setNonce(byte[] var1) {
         return this.setNonce(new DEROctetString(Arrays.clone(var1)));
      }

      public AesCcmCiphertext.Builder setCcmCiphertext(Opaque var1) {
         this.opaque = var1;
         return this;
      }

      public AesCcmCiphertext.Builder setCcmCiphertext(byte[] var1) {
         return this.setCcmCiphertext(new Opaque(var1));
      }

      public AesCcmCiphertext createAesCcmCiphertext() {
         return new AesCcmCiphertext(this.nonce, this.opaque);
      }
   }
}
