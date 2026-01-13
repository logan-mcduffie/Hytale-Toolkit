package com.google.protobuf;

public abstract class ExtensionLite<ContainingType extends MessageLite, Type> {
   public abstract int getNumber();

   public abstract WireFormat.FieldType getLiteType();

   public abstract boolean isRepeated();

   public abstract Type getDefaultValue();

   public abstract MessageLite getMessageDefaultInstance();

   boolean isLite() {
      return true;
   }
}
