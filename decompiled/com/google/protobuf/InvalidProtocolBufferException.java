package com.google.protobuf;

import java.io.IOException;

public class InvalidProtocolBufferException extends IOException {
   private static final long serialVersionUID = -1616151763072450476L;
   private MessageLite unfinishedMessage = null;
   private boolean wasThrownFromInputStream;

   public InvalidProtocolBufferException(String description) {
      super(description);
   }

   public InvalidProtocolBufferException(Exception e) {
      super(e.getMessage(), e);
   }

   public InvalidProtocolBufferException(String description, Exception e) {
      super(description, e);
   }

   public InvalidProtocolBufferException(IOException e) {
      super(e.getMessage(), e);
   }

   public InvalidProtocolBufferException(String description, IOException e) {
      super(description, e);
   }

   public InvalidProtocolBufferException setUnfinishedMessage(MessageLite unfinishedMessage) {
      this.unfinishedMessage = unfinishedMessage;
      return this;
   }

   public MessageLite getUnfinishedMessage() {
      return this.unfinishedMessage;
   }

   void setThrownFromInputStream() {
      this.wasThrownFromInputStream = true;
   }

   boolean getThrownFromInputStream() {
      return this.wasThrownFromInputStream;
   }

   public IOException unwrapIOException() {
      return (IOException)(this.getCause() instanceof IOException ? (IOException)this.getCause() : this);
   }

   static InvalidProtocolBufferException truncatedMessage() {
      return new InvalidProtocolBufferException(
         "While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either that the input has been truncated or that an embedded message misreported its own length."
      );
   }

   static InvalidProtocolBufferException negativeSize() {
      return new InvalidProtocolBufferException("CodedInputStream encountered an embedded string or message which claimed to have negative size.");
   }

   static InvalidProtocolBufferException malformedVarint() {
      return new InvalidProtocolBufferException("CodedInputStream encountered a malformed varint.");
   }

   static InvalidProtocolBufferException invalidTag() {
      return new InvalidProtocolBufferException("Protocol message contained an invalid tag (zero).");
   }

   static InvalidProtocolBufferException invalidEndTag() {
      return new InvalidProtocolBufferException("Protocol message end-group tag did not match expected tag.");
   }

   static InvalidProtocolBufferException.InvalidWireTypeException invalidWireType() {
      return new InvalidProtocolBufferException.InvalidWireTypeException("Protocol message tag had invalid wire type.");
   }

   static InvalidProtocolBufferException recursionLimitExceeded() {
      return new InvalidProtocolBufferException(
         "Protocol message had too many levels of nesting.  May be malicious.  Use setRecursionLimit() to increase the recursion depth limit."
      );
   }

   static InvalidProtocolBufferException sizeLimitExceeded() {
      return new InvalidProtocolBufferException(
         "Protocol message was too large.  May be malicious.  Use CodedInputStream.setSizeLimit() to increase the size limit. If reading multiple messages, consider resetting the counter between each message using CodedInputStream.resetSizeCounter()."
      );
   }

   static InvalidProtocolBufferException parseFailure() {
      return new InvalidProtocolBufferException("Failed to parse the message.");
   }

   static InvalidProtocolBufferException invalidUtf8() {
      return new InvalidProtocolBufferException("Protocol message had invalid UTF-8.");
   }

   public static class InvalidWireTypeException extends InvalidProtocolBufferException {
      private static final long serialVersionUID = 3283890091615336259L;

      public InvalidWireTypeException(String description) {
         super(description);
      }
   }
}
