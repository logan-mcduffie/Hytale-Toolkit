package org.bouncycastle.est;

public class CSRRequestResponse {
   private final CSRAttributesResponse attributesResponse;
   private final Source source;

   public CSRRequestResponse(CSRAttributesResponse var1, Source var2) {
      this.attributesResponse = var1;
      this.source = var2;
   }

   public boolean hasAttributesResponse() {
      return this.attributesResponse != null;
   }

   public CSRAttributesResponse getAttributesResponse() {
      if (this.attributesResponse == null) {
         throw new IllegalStateException("Response has no CSRAttributesResponse.");
      } else {
         return this.attributesResponse;
      }
   }

   public Object getSession() {
      return this.source.getSession();
   }

   public Source getSource() {
      return this.source;
   }
}
