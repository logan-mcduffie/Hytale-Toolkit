package com.google.protobuf;

@CheckReturnValue
interface MessageInfoFactory {
   boolean isSupported(Class<?> clazz);

   MessageInfo messageInfoFor(Class<?> clazz);
}
