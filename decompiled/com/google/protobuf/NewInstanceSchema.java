package com.google.protobuf;

@CheckReturnValue
interface NewInstanceSchema {
   Object newInstance(Object defaultInstance);
}
