package com.google.protobuf;

@CheckReturnValue
final class NewInstanceSchemaLite implements NewInstanceSchema {
   @Override
   public Object newInstance(Object defaultInstance) {
      return ((GeneratedMessageLite)defaultInstance).newMutableInstance();
   }
}
