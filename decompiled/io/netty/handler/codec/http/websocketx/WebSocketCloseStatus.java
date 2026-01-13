package io.netty.handler.codec.http.websocketx;

import io.netty.util.internal.ObjectUtil;

public final class WebSocketCloseStatus implements Comparable<WebSocketCloseStatus> {
   public static final WebSocketCloseStatus NORMAL_CLOSURE = new WebSocketCloseStatus(1000, "Bye");
   public static final WebSocketCloseStatus ENDPOINT_UNAVAILABLE = new WebSocketCloseStatus(1001, "Endpoint unavailable");
   public static final WebSocketCloseStatus PROTOCOL_ERROR = new WebSocketCloseStatus(1002, "Protocol error");
   public static final WebSocketCloseStatus INVALID_MESSAGE_TYPE = new WebSocketCloseStatus(1003, "Invalid message type");
   public static final WebSocketCloseStatus INVALID_PAYLOAD_DATA = new WebSocketCloseStatus(1007, "Invalid payload data");
   public static final WebSocketCloseStatus POLICY_VIOLATION = new WebSocketCloseStatus(1008, "Policy violation");
   public static final WebSocketCloseStatus MESSAGE_TOO_BIG = new WebSocketCloseStatus(1009, "Message too big");
   public static final WebSocketCloseStatus MANDATORY_EXTENSION = new WebSocketCloseStatus(1010, "Mandatory extension");
   public static final WebSocketCloseStatus INTERNAL_SERVER_ERROR = new WebSocketCloseStatus(1011, "Internal server error");
   public static final WebSocketCloseStatus SERVICE_RESTART = new WebSocketCloseStatus(1012, "Service Restart");
   public static final WebSocketCloseStatus TRY_AGAIN_LATER = new WebSocketCloseStatus(1013, "Try Again Later");
   public static final WebSocketCloseStatus BAD_GATEWAY = new WebSocketCloseStatus(1014, "Bad Gateway");
   public static final WebSocketCloseStatus EMPTY = new WebSocketCloseStatus(1005, "Empty", false);
   public static final WebSocketCloseStatus ABNORMAL_CLOSURE = new WebSocketCloseStatus(1006, "Abnormal closure", false);
   public static final WebSocketCloseStatus TLS_HANDSHAKE_FAILED = new WebSocketCloseStatus(1015, "TLS handshake failed", false);
   private final int statusCode;
   private final String reasonText;
   private String text;

   public WebSocketCloseStatus(int statusCode, String reasonText) {
      this(statusCode, reasonText, true);
   }

   public WebSocketCloseStatus(int statusCode, String reasonText, boolean validate) {
      if (validate && !isValidStatusCode(statusCode)) {
         throw new IllegalArgumentException("WebSocket close status code does NOT comply with RFC-6455: " + statusCode);
      } else {
         this.statusCode = statusCode;
         this.reasonText = ObjectUtil.checkNotNull(reasonText, "reasonText");
      }
   }

   public int code() {
      return this.statusCode;
   }

   public String reasonText() {
      return this.reasonText;
   }

   public int compareTo(WebSocketCloseStatus o) {
      return this.code() - o.code();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (null != o && this.getClass() == o.getClass()) {
         WebSocketCloseStatus that = (WebSocketCloseStatus)o;
         return this.statusCode == that.statusCode;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.statusCode;
   }

   @Override
   public String toString() {
      String text = this.text;
      if (text == null) {
         this.text = text = this.code() + " " + this.reasonText();
      }

      return text;
   }

   public static boolean isValidStatusCode(int code) {
      return code < 0 || 1000 <= code && code <= 1003 || 1007 <= code && code <= 1014 || 3000 <= code;
   }

   public static WebSocketCloseStatus valueOf(int code) {
      switch (code) {
         case 1000:
            return NORMAL_CLOSURE;
         case 1001:
            return ENDPOINT_UNAVAILABLE;
         case 1002:
            return PROTOCOL_ERROR;
         case 1003:
            return INVALID_MESSAGE_TYPE;
         case 1004:
         default:
            return new WebSocketCloseStatus(code, "Close status #" + code);
         case 1005:
            return EMPTY;
         case 1006:
            return ABNORMAL_CLOSURE;
         case 1007:
            return INVALID_PAYLOAD_DATA;
         case 1008:
            return POLICY_VIOLATION;
         case 1009:
            return MESSAGE_TOO_BIG;
         case 1010:
            return MANDATORY_EXTENSION;
         case 1011:
            return INTERNAL_SERVER_ERROR;
         case 1012:
            return SERVICE_RESTART;
         case 1013:
            return TRY_AGAIN_LATER;
         case 1014:
            return BAD_GATEWAY;
         case 1015:
            return TLS_HANDSHAKE_FAILED;
      }
   }
}
