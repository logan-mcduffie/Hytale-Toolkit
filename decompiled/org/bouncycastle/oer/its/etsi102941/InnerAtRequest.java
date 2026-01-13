package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.EcSignature;
import org.bouncycastle.oer.its.etsi102941.basetypes.PublicKeys;
import org.bouncycastle.util.Arrays;

public class InnerAtRequest extends ASN1Object {
   private final PublicKeys publicKeys;
   private final ASN1OctetString hmacKey;
   private final SharedAtRequest sharedAtRequest;
   private final EcSignature ecSignature;

   public InnerAtRequest(PublicKeys var1, ASN1OctetString var2, SharedAtRequest var3, EcSignature var4) {
      this.publicKeys = var1;
      this.hmacKey = var2;
      this.sharedAtRequest = var3;
      this.ecSignature = var4;
   }

   private InnerAtRequest(ASN1Sequence var1) {
      if (var1.size() != 4) {
         throw new IllegalArgumentException("expected sequence size of 4");
      } else {
         this.publicKeys = PublicKeys.getInstance(var1.getObjectAt(0));
         this.hmacKey = ASN1OctetString.getInstance(var1.getObjectAt(1));
         this.sharedAtRequest = SharedAtRequest.getInstance(var1.getObjectAt(2));
         this.ecSignature = EcSignature.getInstance(var1.getObjectAt(3));
      }
   }

   public static InnerAtRequest getInstance(Object var0) {
      if (var0 instanceof InnerAtRequest) {
         return (InnerAtRequest)var0;
      } else {
         return var0 != null ? new InnerAtRequest(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public PublicKeys getPublicKeys() {
      return this.publicKeys;
   }

   public ASN1OctetString getHmacKey() {
      return this.hmacKey;
   }

   public SharedAtRequest getSharedAtRequest() {
      return this.sharedAtRequest;
   }

   public EcSignature getEcSignature() {
      return this.ecSignature;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.publicKeys, this.hmacKey, this.sharedAtRequest, this.ecSignature});
   }

   public static InnerAtRequest.Builder builder() {
      return new InnerAtRequest.Builder();
   }

   public static class Builder {
      private PublicKeys publicKeys;
      private ASN1OctetString hmacKey;
      private SharedAtRequest sharedAtRequest;
      private EcSignature ecSignature;

      public InnerAtRequest.Builder setPublicKeys(PublicKeys var1) {
         this.publicKeys = var1;
         return this;
      }

      public InnerAtRequest.Builder setHmacKey(ASN1OctetString var1) {
         this.hmacKey = var1;
         return this;
      }

      public InnerAtRequest.Builder setHmacKey(byte[] var1) {
         this.hmacKey = new DEROctetString(Arrays.clone(var1));
         return this;
      }

      public InnerAtRequest.Builder setSharedAtRequest(SharedAtRequest var1) {
         this.sharedAtRequest = var1;
         return this;
      }

      public InnerAtRequest.Builder setEcSignature(EcSignature var1) {
         this.ecSignature = var1;
         return this;
      }

      public InnerAtRequest createInnerAtRequest() {
         return new InnerAtRequest(this.publicKeys, this.hmacKey, this.sharedAtRequest, this.ecSignature);
      }
   }
}
