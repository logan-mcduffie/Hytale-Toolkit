package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.EcSignature;

public class AuthorizationValidationRequest extends ASN1Object {
   private final SharedAtRequest sharedAtRequest;
   private final EcSignature ecSignature;

   public AuthorizationValidationRequest(SharedAtRequest var1, EcSignature var2) {
      this.sharedAtRequest = var1;
      this.ecSignature = var2;
   }

   private AuthorizationValidationRequest(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.sharedAtRequest = SharedAtRequest.getInstance(var1.getObjectAt(0));
         this.ecSignature = EcSignature.getInstance(var1.getObjectAt(1));
      }
   }

   public static AuthorizationValidationRequest getInstance(Object var0) {
      if (var0 instanceof AuthorizationValidationRequest) {
         return (AuthorizationValidationRequest)var0;
      } else {
         return var0 != null ? new AuthorizationValidationRequest(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public SharedAtRequest getSharedAtRequest() {
      return this.sharedAtRequest;
   }

   public EcSignature getEcSignature() {
      return this.ecSignature;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.sharedAtRequest, this.ecSignature});
   }

   public static AuthorizationValidationRequest.Builder builder() {
      return new AuthorizationValidationRequest.Builder();
   }

   public static class Builder {
      private SharedAtRequest sharedAtRequest;
      private EcSignature ecSignature;

      public AuthorizationValidationRequest.Builder setSharedAtRequest(SharedAtRequest var1) {
         this.sharedAtRequest = var1;
         return this;
      }

      public AuthorizationValidationRequest.Builder setEcSignature(EcSignature var1) {
         this.ecSignature = var1;
         return this;
      }

      public AuthorizationValidationRequest createAuthorizationValidationRequest() {
         return new AuthorizationValidationRequest(this.sharedAtRequest, this.ecSignature);
      }
   }
}
