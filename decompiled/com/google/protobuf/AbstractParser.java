package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public abstract class AbstractParser<MessageType extends MessageLite> implements Parser<MessageType> {
   private static final ExtensionRegistryLite EMPTY_REGISTRY = ExtensionRegistryLite.getEmptyRegistry();

   private UninitializedMessageException newUninitializedMessageException(MessageType message) {
      return message instanceof AbstractMessageLite
         ? ((AbstractMessageLite)message).newUninitializedMessageException()
         : new UninitializedMessageException(message);
   }

   private MessageType checkMessageInitialized(MessageType message) throws InvalidProtocolBufferException {
      if (message != null && !message.isInitialized()) {
         throw this.newUninitializedMessageException(message).asInvalidProtocolBufferException().setUnfinishedMessage(message);
      } else {
         return message;
      }
   }

   public MessageType parsePartialFrom(CodedInputStream input) throws InvalidProtocolBufferException {
      return this.parsePartialFrom(input, EMPTY_REGISTRY);
   }

   public MessageType parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return this.checkMessageInitialized(this.parsePartialFrom(input, extensionRegistry));
   }

   public MessageType parseFrom(CodedInputStream input) throws InvalidProtocolBufferException {
      return this.parseFrom(input, EMPTY_REGISTRY);
   }

   public MessageType parsePartialFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      try {
         CodedInputStream input = data.newCodedInput();
         MessageType message = this.parsePartialFrom(input, extensionRegistry);

         try {
            input.checkLastTagWas(0);
         } catch (InvalidProtocolBufferException var6) {
            throw var6.setUnfinishedMessage(message);
         }

         return message;
      } catch (InvalidProtocolBufferException var7) {
         throw var7;
      }
   }

   public MessageType parsePartialFrom(ByteString data) throws InvalidProtocolBufferException {
      return this.parsePartialFrom(data, EMPTY_REGISTRY);
   }

   public MessageType parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return this.checkMessageInitialized(this.parsePartialFrom(data, extensionRegistry));
   }

   public MessageType parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return this.parseFrom(data, EMPTY_REGISTRY);
   }

   public MessageType parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      MessageType message;
      try {
         CodedInputStream input = CodedInputStream.newInstance(data);
         message = this.parsePartialFrom(input, extensionRegistry);

         try {
            input.checkLastTagWas(0);
         } catch (InvalidProtocolBufferException var6) {
            throw var6.setUnfinishedMessage(message);
         }
      } catch (InvalidProtocolBufferException var7) {
         throw var7;
      }

      return this.checkMessageInitialized(message);
   }

   public MessageType parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return this.parseFrom(data, EMPTY_REGISTRY);
   }

   public MessageType parsePartialFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      try {
         CodedInputStream input = CodedInputStream.newInstance(data, off, len);
         MessageType message = this.parsePartialFrom(input, extensionRegistry);

         try {
            input.checkLastTagWas(0);
         } catch (InvalidProtocolBufferException var8) {
            throw var8.setUnfinishedMessage(message);
         }

         return message;
      } catch (InvalidProtocolBufferException var9) {
         throw var9;
      }
   }

   public MessageType parsePartialFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
      return this.parsePartialFrom(data, off, len, EMPTY_REGISTRY);
   }

   public MessageType parsePartialFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return this.parsePartialFrom(data, 0, data.length, extensionRegistry);
   }

   public MessageType parsePartialFrom(byte[] data) throws InvalidProtocolBufferException {
      return this.parsePartialFrom(data, 0, data.length, EMPTY_REGISTRY);
   }

   public MessageType parseFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return this.checkMessageInitialized(this.parsePartialFrom(data, off, len, extensionRegistry));
   }

   public MessageType parseFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
      return this.parseFrom(data, off, len, EMPTY_REGISTRY);
   }

   public MessageType parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return this.parseFrom(data, 0, data.length, extensionRegistry);
   }

   public MessageType parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return this.parseFrom(data, EMPTY_REGISTRY);
   }

   public MessageType parsePartialFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      CodedInputStream codedInput = CodedInputStream.newInstance(input);
      MessageType message = this.parsePartialFrom(codedInput, extensionRegistry);

      try {
         codedInput.checkLastTagWas(0);
         return message;
      } catch (InvalidProtocolBufferException var6) {
         throw var6.setUnfinishedMessage(message);
      }
   }

   public MessageType parsePartialFrom(InputStream input) throws InvalidProtocolBufferException {
      return this.parsePartialFrom(input, EMPTY_REGISTRY);
   }

   public MessageType parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return this.checkMessageInitialized(this.parsePartialFrom(input, extensionRegistry));
   }

   public MessageType parseFrom(InputStream input) throws InvalidProtocolBufferException {
      return this.parseFrom(input, EMPTY_REGISTRY);
   }

   public MessageType parsePartialDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      int size;
      try {
         int firstByte = input.read();
         if (firstByte == -1) {
            return null;
         }

         size = CodedInputStream.readRawVarint32(firstByte, input);
      } catch (IOException var5) {
         throw new InvalidProtocolBufferException(var5);
      }

      InputStream limitedInput = new AbstractMessageLite.Builder.LimitedInputStream(input, size);
      return this.parsePartialFrom(limitedInput, extensionRegistry);
   }

   public MessageType parsePartialDelimitedFrom(InputStream input) throws InvalidProtocolBufferException {
      return this.parsePartialDelimitedFrom(input, EMPTY_REGISTRY);
   }

   public MessageType parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return this.checkMessageInitialized(this.parsePartialDelimitedFrom(input, extensionRegistry));
   }

   public MessageType parseDelimitedFrom(InputStream input) throws InvalidProtocolBufferException {
      return this.parseDelimitedFrom(input, EMPTY_REGISTRY);
   }
}
