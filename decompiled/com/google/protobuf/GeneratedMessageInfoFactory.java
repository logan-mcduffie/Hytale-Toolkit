package com.google.protobuf;

class GeneratedMessageInfoFactory implements MessageInfoFactory {
   private static final GeneratedMessageInfoFactory instance = new GeneratedMessageInfoFactory();

   private GeneratedMessageInfoFactory() {
   }

   public static GeneratedMessageInfoFactory getInstance() {
      return instance;
   }

   @Override
   public boolean isSupported(Class<?> messageType) {
      return GeneratedMessageLite.class.isAssignableFrom(messageType);
   }

   @Override
   public MessageInfo messageInfoFor(Class<?> messageType) {
      if (!GeneratedMessageLite.class.isAssignableFrom(messageType)) {
         throw new IllegalArgumentException("Unsupported message type: " + messageType.getName());
      } else {
         try {
            return (MessageInfo)GeneratedMessageLite.getDefaultInstance(messageType.asSubclass(GeneratedMessageLite.class)).buildMessageInfo();
         } catch (Exception var3) {
            throw new RuntimeException("Unable to get message info for " + messageType.getName(), var3);
         }
      }
   }
}
