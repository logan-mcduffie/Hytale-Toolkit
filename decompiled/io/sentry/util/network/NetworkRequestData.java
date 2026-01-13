package io.sentry.util.network;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NetworkRequestData {
   @Nullable
   private final String method;
   @Nullable
   private Integer statusCode;
   @Nullable
   private Long requestBodySize;
   @Nullable
   private Long responseBodySize;
   @Nullable
   private ReplayNetworkRequestOrResponse request;
   @Nullable
   private ReplayNetworkRequestOrResponse response;

   public NetworkRequestData(@Nullable String method) {
      this.method = method;
   }

   @Nullable
   public String getMethod() {
      return this.method;
   }

   @Nullable
   public Integer getStatusCode() {
      return this.statusCode;
   }

   @Nullable
   public Long getRequestBodySize() {
      return this.requestBodySize;
   }

   @Nullable
   public Long getResponseBodySize() {
      return this.responseBodySize;
   }

   @Nullable
   public ReplayNetworkRequestOrResponse getRequest() {
      return this.request;
   }

   @Nullable
   public ReplayNetworkRequestOrResponse getResponse() {
      return this.response;
   }

   public void setRequestDetails(@NotNull ReplayNetworkRequestOrResponse requestData) {
      this.request = requestData;
      this.requestBodySize = requestData.getSize();
   }

   public void setResponseDetails(int statusCode, @NotNull ReplayNetworkRequestOrResponse responseData) {
      this.statusCode = statusCode;
      this.response = responseData;
      this.responseBodySize = responseData.getSize();
   }

   @Override
   public String toString() {
      return "NetworkRequestData{method='"
         + this.method
         + '\''
         + ", statusCode="
         + this.statusCode
         + ", requestBodySize="
         + this.requestBodySize
         + ", responseBodySize="
         + this.responseBodySize
         + ", request="
         + this.request
         + ", response="
         + this.response
         + '}';
   }
}
