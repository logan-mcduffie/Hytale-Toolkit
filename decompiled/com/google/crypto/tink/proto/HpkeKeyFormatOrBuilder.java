package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface HpkeKeyFormatOrBuilder extends MessageOrBuilder {
   boolean hasParams();

   HpkeParams getParams();

   HpkeParamsOrBuilder getParamsOrBuilder();
}
