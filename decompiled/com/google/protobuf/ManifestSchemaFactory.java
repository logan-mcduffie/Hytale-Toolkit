package com.google.protobuf;

@CheckReturnValue
final class ManifestSchemaFactory implements SchemaFactory {
   private final MessageInfoFactory messageInfoFactory;
   private static final MessageInfoFactory EMPTY_FACTORY = new MessageInfoFactory() {
      @Override
      public boolean isSupported(Class<?> clazz) {
         return false;
      }

      @Override
      public MessageInfo messageInfoFor(Class<?> clazz) {
         throw new IllegalStateException("This should never be called.");
      }
   };

   public ManifestSchemaFactory() {
      this(getDefaultMessageInfoFactory());
   }

   private ManifestSchemaFactory(MessageInfoFactory messageInfoFactory) {
      this.messageInfoFactory = Internal.checkNotNull(messageInfoFactory, "messageInfoFactory");
   }

   @Override
   public <T> Schema<T> createSchema(Class<T> messageType) {
      SchemaUtil.requireGeneratedMessage(messageType);
      MessageInfo messageInfo = this.messageInfoFactory.messageInfoFor(messageType);
      if (messageInfo.isMessageSetWireFormat()) {
         return useLiteRuntime(messageType)
            ? MessageSetSchema.newSchema(SchemaUtil.unknownFieldSetLiteSchema(), ExtensionSchemas.lite(), messageInfo.getDefaultInstance())
            : MessageSetSchema.newSchema(SchemaUtil.unknownFieldSetFullSchema(), ExtensionSchemas.full(), messageInfo.getDefaultInstance());
      } else {
         return newSchema(messageType, messageInfo);
      }
   }

   private static <T> Schema<T> newSchema(Class<T> messageType, MessageInfo messageInfo) {
      return useLiteRuntime(messageType)
         ? MessageSchema.newSchema(
            messageType,
            messageInfo,
            NewInstanceSchemas.lite(),
            ListFieldSchemas.lite(),
            SchemaUtil.unknownFieldSetLiteSchema(),
            allowExtensions(messageInfo) ? ExtensionSchemas.lite() : null,
            MapFieldSchemas.lite()
         )
         : MessageSchema.newSchema(
            messageType,
            messageInfo,
            NewInstanceSchemas.full(),
            ListFieldSchemas.full(),
            SchemaUtil.unknownFieldSetFullSchema(),
            allowExtensions(messageInfo) ? ExtensionSchemas.full() : null,
            MapFieldSchemas.full()
         );
   }

   private static boolean allowExtensions(MessageInfo messageInfo) {
      switch (messageInfo.getSyntax()) {
         case PROTO3:
            return false;
         default:
            return true;
      }
   }

   private static MessageInfoFactory getDefaultMessageInfoFactory() {
      return new ManifestSchemaFactory.CompositeMessageInfoFactory(GeneratedMessageInfoFactory.getInstance(), getDescriptorMessageInfoFactory());
   }

   private static MessageInfoFactory getDescriptorMessageInfoFactory() {
      if (Android.assumeLiteRuntime) {
         return EMPTY_FACTORY;
      } else {
         try {
            Class<?> clazz = Class.forName("com.google.protobuf.DescriptorMessageInfoFactory");
            return (MessageInfoFactory)clazz.getDeclaredMethod("getInstance").invoke(null);
         } catch (Exception var1) {
            return EMPTY_FACTORY;
         }
      }
   }

   private static boolean useLiteRuntime(Class<?> messageType) {
      return Android.assumeLiteRuntime || GeneratedMessageLite.class.isAssignableFrom(messageType);
   }

   private static class CompositeMessageInfoFactory implements MessageInfoFactory {
      private MessageInfoFactory[] factories;

      CompositeMessageInfoFactory(MessageInfoFactory... factories) {
         this.factories = factories;
      }

      @Override
      public boolean isSupported(Class<?> clazz) {
         for (MessageInfoFactory factory : this.factories) {
            if (factory.isSupported(clazz)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public MessageInfo messageInfoFor(Class<?> clazz) {
         for (MessageInfoFactory factory : this.factories) {
            if (factory.isSupported(clazz)) {
               return factory.messageInfoFor(clazz);
            }
         }

         throw new UnsupportedOperationException("No factory is available for message type: " + clazz.getName());
      }
   }
}
