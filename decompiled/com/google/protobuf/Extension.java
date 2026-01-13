package com.google.protobuf;

public abstract class Extension<ContainingType extends MessageLite, Type> extends ExtensionLite<ContainingType, Type> {
   public abstract Message getMessageDefaultInstance();

   public abstract Descriptors.FieldDescriptor getDescriptor();

   @Override
   final boolean isLite() {
      return false;
   }

   protected abstract Extension.ExtensionType getExtensionType();

   public Extension.MessageType getMessageType() {
      return Extension.MessageType.PROTO2;
   }

   protected abstract Object fromReflectionType(Object value);

   protected abstract Object singularFromReflectionType(Object value);

   protected abstract Object toReflectionType(Object value);

   protected abstract Object singularToReflectionType(Object value);

   protected static enum ExtensionType {
      IMMUTABLE,
      MUTABLE,
      PROTO1;
   }

   public static enum MessageType {
      PROTO1,
      PROTO2;
   }
}
