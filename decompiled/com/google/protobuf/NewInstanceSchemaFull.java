package com.google.protobuf;

final class NewInstanceSchemaFull implements NewInstanceSchema {
   @Override
   public Object newInstance(Object defaultInstance) {
      return ((Message)defaultInstance).toBuilder().buildPartial();
   }
}
